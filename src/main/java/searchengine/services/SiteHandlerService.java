package searchengine.services;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import searchengine.components.RunIndexing;
import searchengine.dto.indexing.IndexDto;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Setter
@Getter
@Service
@Slf4j
public class SiteHandlerService {
    private ConnectionProvider connectionProvider;
    private static CopyOnWriteArraySet<String> uniquePaths = new CopyOnWriteArraySet<>();
    private static Hashtable<String, LemmaDto> lemmasToSave = new Hashtable<>();
    private static CopyOnWriteArraySet<IndexDto> indexesToSave = new CopyOnWriteArraySet<>();
    private static String pattern = "(.+(\\.(?i)(jpg|png|gif|bmp|pdf|jpeg|eps|xml|doc|xlx|xlsx|html))$)";
    private LuceneMorphologyService luceneMorphologyService;
    private TableServices tableServices;

    @Autowired
    public SiteHandlerService(ConnectionProvider connectionProvider, LuceneMorphologyService luceneMorphologyService, TableServices tableServices) {
        this.connectionProvider = connectionProvider;
        this.luceneMorphologyService = luceneMorphologyService;
        this.tableServices = tableServices;
    }

    public void handlePage(PageDto pageDto) {
        if (RunIndexing.isShutdown()) {return;}
        Document document = parsePage(pageDto);

        if (document == null) {
            return;
        }
        if (RunIndexing.isShutdown()) {return;}
        HashSet<String> subLinks = new HashSet<>();
        Set<String> allLinks = getAllSubLinks(document, pageDto.getRoot());
        for (String link : allLinks) {
            String fullPath = pageDto.getRoot() + link;
            if (!uniquePaths.contains(fullPath)) {
                uniquePaths.add(fullPath);
                subLinks.add(link);
            }
        }
        pageDto.setSubPaths(subLinks);

        if (RunIndexing.isShutdown()) {return;}
        parsePageLemmas(pageDto, document);
    }

    public void handleSinglePage(PageDto pageDto) {
        if (pageDto.getId() != null) {
            tableServices.getPageService().delete(pageDto.getId());
            List<Integer> lemma_ids = tableServices.getIndexService().getLemmaIdsByPageId(pageDto.getId());
            tableServices.getIndexService().deleteAllByPageId(pageDto.getId());
            tableServices.getLemmaService().deleteAllByIds(lemma_ids);
        }
        Document document = parsePage(pageDto);
        if (document == null) {return;}
        parsePageLemmas(pageDto, document);
    }

    private Document parsePage(PageDto pageDto) {
        Document document = connectionProvider.getDoc(pageDto);
        if (document == null) {
            log.info("No document found for {}", pageDto.getPath());
            return null;
        }
        pageDto.setCode(200);
        pageDto.setContent(document.html());
        pageDto.setId(tableServices.getPageService().saveAndReturnId(pageDto));
        return document;
    }

    private void parsePageLemmas(PageDto pageDto, Document document) {
        Map<String, Integer> bodyLemmas = luceneMorphologyService.countLemmas(document
                .getElementsByTag("body").text());

        Map<String, Float> totalWords = new HashMap<>();
        bodyLemmas.forEach((key1, value1) -> totalWords.put(key1, Float.valueOf(value1)));

        if (RunIndexing.isShutdown()) {return;}
        saveLemma(totalWords, pageDto);
    }

    public void saveLemma(Map<String, Float> lemmas, PageDto pageDto) {
        for (Map.Entry word : lemmas.entrySet()) {
            try {
//                LemmaDto lemmaDto = lemmaService.findLemmaDtoByLemmaAndSiteId(word.getKey().toString(),
//                        pageDto.getSiteId());
                LemmaDto lemmaDto = lemmasToSave.get(word.getKey().toString());
                if (lemmaDto == null) {
                    lemmaDto = new LemmaDto();
                    lemmaDto.setSiteId(pageDto.getSiteId());
                    lemmaDto.setLemma(word.getKey().toString());
                    lemmaDto.setFrequency(1);
                    lemmaDto.setId(lemmasToSave.size() + 1);
                    lemmasToSave.put(lemmaDto.getLemma(), lemmaDto);
                } else {
                    lemmaDto.setFrequency(lemmaDto.getFrequency() + 1);
                    lemmasToSave.put(lemmaDto.getLemma(), lemmaDto);
                }

//                lemmaDto.setId(lemmaService.addAndReturnId(lemmaDto));
                IndexDto indexDto = new IndexDto();
                indexDto.setPageId(pageDto.getId());
                indexDto.setLemmaId(lemmaDto.getId());
                indexDto.setRank((float) word.getValue());
                indexesToSave.add(indexDto);
//                tableServices.getIndexService().add(indexDto);
            } catch (IncorrectResultSizeDataAccessException e) {
                log.info(word.getKey().toString());
            }

        }
    }

    public HashSet<String> getAllSubLinks(Document document, String rootUrl) {
        Elements elements = document.select("a[href]");
        return new HashSet<>(elements.stream()
                .map(e -> e.attr("abs:href"))
                .filter(e -> checkUrl(e, rootUrl))
                .map(e -> e.replaceAll(rootUrl, ""))
                .toList());
    }

    public static boolean checkUrl(String url, String rootUrl) {
        return url.startsWith(rootUrl) && !url.matches(pattern) && !url.contains("#");
    }

    public void saveAllLemmasAndIndexes() {
        tableServices.getLemmaService().saveAll(lemmasToSave.values());
        tableServices.getIndexService().saveAll(indexesToSave);
    }
}
