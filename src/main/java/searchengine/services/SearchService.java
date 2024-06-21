package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.services.implservices.IndexServiceImpl;
import searchengine.services.implservices.LemmaServiceImpl;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.services.implservices.SiteServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    private LuceneMorphologyService luceneMorphologyService;
    private LemmaServiceImpl lemmasService;
    private SiteServiceImpl siteService;
    private PageServiceImpl pageService;
    private IndexServiceImpl indexService;
    private ConnectionProvider connectionProvider;
    @Value("${config.amount-of-words}")
    private int amountOfWords;

    @Autowired
    public SearchService(IndexServiceImpl indexService, PageServiceImpl pageService, SiteServiceImpl siteService, LemmaServiceImpl lemmasService, LuceneMorphologyService luceneMorphologyService, ConnectionProvider connectionProvider) {
        this.indexService = indexService;
        this.pageService = pageService;
        this.siteService = siteService;
        this.lemmasService = lemmasService;
        this.luceneMorphologyService = luceneMorphologyService;
        this.connectionProvider = connectionProvider;
    }

    public SearchResponse search(String query, String siteUrl, int offset, int limit) {
        Integer siteId;
        SiteDto siteDto = null;
        if  (siteUrl != null) {
            siteDto = siteService.findSiteByUrl(siteUrl);
            if (siteDto == null) {
                throw new IllegalArgumentException("Invalid site url: " + siteUrl);
            }
            siteId = siteDto.getId();
        } else {
            siteId = null;
        }

        SearchResponse searchResponse = new SearchResponse();
        float maxFrequency = pageService.getAll().size() * 0.3f;
        Set<String> wordsInQuery = luceneMorphologyService.getLemmaSet(query);
        Set<LemmaDto> sortedSetLemmas = new HashSet<>(0);
        for (String word : wordsInQuery) {
            if (siteId != null) {
                LemmaDto lemmaDto = lemmasService.findLemmaDtoByLemmaAndSiteId(word, siteId);
                sortedSetLemmas.add(lemmaDto);
            } else {
                List<LemmaDto> lemmaDtoList = lemmasService.findLemmasDtoByLemma(word);
                sortedSetLemmas.addAll(lemmaDtoList);
            }
        }
        sortedSetLemmas = sortedSetLemmas.stream()
                .filter(l -> !l.getLemma().equals("null"))
                .filter(l -> l.getFrequency() < maxFrequency)
                .sorted(Comparator.comparing(LemmaDto::getFrequency))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Integer> filteredPageIds = getFilteredPageIds(sortedSetLemmas);
        if (filteredPageIds.isEmpty()) {
            searchResponse.setCount(0);
            searchResponse.setResult(false);
            searchResponse.setData(new ArrayList<SearchData>());
            return searchResponse;
        }

        Map<Integer, Float> pagesRelevance = getPagesRelevance(filteredPageIds, sortedSetLemmas);
        List<SearchData> searchDataList = getSearchData(pagesRelevance, siteDto, sortedSetLemmas, offset, limit)
                .stream()
                .sorted(SearchData::compareByRelevance).toList();
        searchResponse.setResult(true);
        searchResponse.setCount(pagesRelevance.size());
        searchResponse.setData(List.of(searchDataList.toArray(new SearchData[0])));
        return searchResponse;
    }

    private List<SearchData> getSearchData(Map<Integer, Float> relevanceAbsolutPages,
                                           SiteDto siteDto, Set<LemmaDto> sortedSetLemmas, int offset, int limit) {
        List<SearchData> searchDataList = new ArrayList<>();
        int count = -1;
        for (Map.Entry element : relevanceAbsolutPages.entrySet()) {
            count++;
            if (count < offset){
                continue;
            }
            if (count > offset + limit - 1){
                break;
            }
            PageDto pageDto = pageService.getById((int) element.getKey());
            if (pageDto.getPath() == null) {
                continue;
            }
            String content = pageDto.getContent();
            Document document = Jsoup.parse(content);
            String title = document.title();
            String body = document.body().text();

            String snippet = getSnippet(body, sortedSetLemmas);

            SiteDto siteResult = siteDto != null? siteDto : siteService.getById(pageDto.getSiteId());
            SearchData dataResponse = new SearchData();
            dataResponse.setSite(siteResult.getUrl());
            dataResponse.setSiteName(siteResult.getName());
            dataResponse.setTitle(title);
            dataResponse.setUri(pageDto.getPath());
            dataResponse.setSnippet(snippet);
            dataResponse.setRelevance((float) element.getValue());
            searchDataList.add(dataResponse);
        }
        return searchDataList;
    }

    public String getSnippet(String text, Set<LemmaDto> lemmaInQuery) {
        if (lemmaInQuery.isEmpty()){
            return null;
        }
        List<String> words = List.of(text.toLowerCase().replaceAll("[^а-яёa-z]", " ").trim().split("\\s+"));
        List<String> defaultWords = List.of(text.replaceAll("[^а-яёА-ЯA-Za-zЁ]", " ").trim().split("\\s+"));
        Boolean[] foundIndexes = new Boolean[words.size()];
        for (int i = 0; i < words.size(); i++) {
            foundIndexes[i] = false;
        }
        int[] startIndexes = new int[words.size()];
        int maxCount = 0;
        int start = 0;
        int lastIndex = -1;
        int sameStart = 0;

        for (int i = 0; i < words.size(); i++) {
            for (String wordLemma : luceneMorphologyService.getLemmaSet(words.get(i))) {
                for (String lemma : lemmaInQuery.stream().map(LemmaDto::getLemma).toList()) {
                    if (wordLemma.equals(lemma)) {
                        foundIndexes[i] = true;
                        text = text.replaceAll("<b>" + defaultWords.get(i) + "</b>", defaultWords.get(i));
                        text = text.replaceAll(defaultWords.get(i), "<b>" + defaultWords.get(i) + "</b>");
                    }
                }
            }
            int index = StringUtils.indexOf(text, defaultWords.get(i), lastIndex);
            if (index == -1) {
                index = StringUtils.indexOf(text, "<b>" + defaultWords.get(i) + "</b>", lastIndex);
            }
            lastIndex = index;
            startIndexes[i] = index;
        }
        for (int i = 0; i < words.size() - amountOfWords + 1; i++) {
            int count = Collections.frequency(List.of(Arrays.copyOfRange(foundIndexes, i, i + amountOfWords)), true);
            if (count > maxCount) {
                maxCount = count;
                start = i;
                sameStart = i;
            } else if (count == maxCount) {
                sameStart = i;
            } else {
                if ((sameStart - start) <= amountOfWords - 1 && sameStart > start) {
                    int division = (start + sameStart) / 2;
                    start = division;
                    sameStart = division;
                }
            }
        }
        if ((sameStart - start) <= amountOfWords - 1 && sameStart > start) {
            start = sameStart;
        }

        return text.substring(startIndexes[start], startIndexes[start + amountOfWords - 1] + words.get(start + amountOfWords - 1).length() + (foundIndexes[start + amountOfWords - 1] ? 7 : 0));
    }

    private Map<Integer, Float> getPagesRelevance(Set<Integer> filteredPageIds, Set<LemmaDto> sortedLemmas) {
        Map<Integer, Float> pagesRelevance = new HashMap<>();
        float maxRelevance = 0f;
        for (Integer pageId : filteredPageIds) {
            float pageRank = 0f;
            for (LemmaDto lemmaDto : sortedLemmas) {
                pageRank += indexService.findRankByLemmaIdAndPageId(lemmaDto.getId(), pageId);
            }
            pagesRelevance.put(pageId, pageRank);
            maxRelevance = Math.max(maxRelevance, pageRank);
        }
        final float finalMaxRelevance = maxRelevance;
        return pagesRelevance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((v1, v2) -> v2.compareTo(v1)))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue() / finalMaxRelevance, (v1, v2) -> v1, LinkedHashMap::new));
    }

    private Set<Integer> getFilteredPageIds(Set<LemmaDto> sortedLemmas) {
        Set<Integer> filteredPageIds = new HashSet<>();
        int count = 0;
        for (LemmaDto lemmaDto : sortedLemmas) {
            count++;
            Set<Integer> pagesForLemma = indexService.findPageIdsByLemmaId(lemmaDto.getId());
            if (count == 1) {
                filteredPageIds.addAll(pagesForLemma);
                continue;
            }
            filteredPageIds.retainAll(pagesForLemma);
        }

        return filteredPageIds;
    }
}
