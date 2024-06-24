package searchengine.services;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.components.RunIndexing;
import searchengine.dto.indexing.IndexDto;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.services.implservices.IndexServiceImpl;
import searchengine.services.implservices.LemmaServiceImpl;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.services.implservices.SiteServiceImpl;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Setter
@Getter
@Service
@Slf4j
public class HandlerService {
    private final IndexServiceImpl indexService;
    private ConnectionProvider connectionProvider;

    private static CopyOnWriteArraySet<String> uniquePaths = new CopyOnWriteArraySet<>();
    private static String pattern = "(.+(\\.(?i)(jpg|png|gif|bmp|pdf|jpeg|eps|xml|doc|xlx|xlsx))$)";

    private SiteServiceImpl siteService;
    private PageServiceImpl pageService;
    private LuceneMorphologyService luceneMorphologyService;
    private LemmaServiceImpl lemmaService;

    @Autowired
    public HandlerService(ConnectionProvider connectionProvider, SiteServiceImpl siteService, PageServiceImpl pageService, LuceneMorphologyService luceneMorphologyService, LemmaServiceImpl lemmaService, IndexServiceImpl indexService) {
        this.connectionProvider = connectionProvider;
        this.siteService = siteService;
        this.pageService = pageService;
        this.luceneMorphologyService = luceneMorphologyService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
    }

    public void handlePage(PageDto pageDto) {
        if (RunIndexing.isShutdown()) {return;}
        Document document = parsePage(pageDto);

        if (document == null) {
            return;
        }
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
            pageService.delete(pageDto.getId());
            List<Integer> lemma_ids = indexService.getLemmaIdsByPageId(pageDto.getId());
            indexService.deleteAllByPageId(pageDto.getId());
            lemmaService.deleteAllByIds(lemma_ids);
        }
        Document document = parsePage(pageDto);
        if (document == null) {
            return;
        }
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
        pageService.add(pageDto);
        return document;
    }

    private void parsePageLemmas(PageDto pageDto, Document document) {
        Map<String, Integer> bodyLemmas = luceneMorphologyService.countLemmas(document
                .getElementsByTag("body").text());

        Map<String, Float> totalWords = new HashMap<>();
        bodyLemmas.forEach((key1, value1) -> totalWords.put(key1, Float.valueOf(value1)));

        saveLemma(totalWords, pageDto);
    }

    public void saveLemma(Map<String, Float> lemmas, PageDto pageDto) {

        for (Map.Entry word : lemmas.entrySet()) {
            LemmaDto lemmaDto = lemmaService.findLemmaDtoByLemmaAndSiteId(word.getKey().toString(),
                    pageDto.getSiteId());
            if (lemmaDto.getLemma() == null) {
                lemmaDto = new LemmaDto();
                lemmaDto.setSiteId(pageDto.getSiteId());
                lemmaDto.setLemma(word.getKey().toString());
                lemmaDto.setFrequency(1);
            } else {
                lemmaDto.setFrequency(lemmaDto.getFrequency() + 1);
            }
            lemmaService.add(lemmaDto);

            IndexDto indexDto = new IndexDto();
            indexDto.setPageId(pageService.findIdByPathAndSiteId(pageDto.getPath(), pageDto.getSiteId()));
            indexDto.setLemmaId(lemmaService.getIdByLemmaAndSiteId(lemmaDto.getLemma(), lemmaDto.getSiteId()));
            indexDto.setRank((float) word.getValue());
            indexService.add(indexDto);
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

    public boolean checkUrl(String url, String rootUrl) {
        return url.startsWith(rootUrl) && !url.matches(pattern) && !url.contains("#");
    }

    public void clearUniqueLinks() {
        uniquePaths.clear();
    }
}
