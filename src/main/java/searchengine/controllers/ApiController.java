package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import searchengine.components.RunIndexing;
import searchengine.config.Config;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.answers.DefaultAnswer;
import searchengine.dto.indexing.SiteDto;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Status;
import searchengine.services.*;
import searchengine.services.implservices.LemmaServiceImpl;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.services.implservices.SiteServiceImpl;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {
    private final StatisticsServiceImpl statisticsService;
    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;
    private final ConnectionProvider connectionProvider;
    private final LemmaServiceImpl lemmaServiceImpl;
    private final SearchService searchService;
    private RunIndexing runIndexing;
    private final HandlerService handlerService;
    private final Config config;
    private final ClearDataService clearDataService;
    private final List<Site> sites;

    @Autowired
    public ApiController(StatisticsServiceImpl statisticsService, SiteServiceImpl siteService, PageServiceImpl pageService, ConnectionProvider connectionProvider, SitesList sitesList, RunIndexing runIndexing, HandlerService handlerService, ClearDataService clearDataService, LemmaServiceImpl lemmaServiceImpl, SearchService searchService, Config config) {
        this.statisticsService = statisticsService;
        this.siteService = siteService;
        this.pageService = pageService;
        this.connectionProvider = connectionProvider;
        this.config = config;
        this.runIndexing = runIndexing;
        this.handlerService = handlerService;
        this.clearDataService = clearDataService;
        this.lemmaServiceImpl = lemmaServiceImpl;
        this.searchService = searchService;
        this.sites = config.getSites();
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics(Model model) {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @ModelAttribute("siteList")
    public List<Site> siteList() {
        return sites;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing(Model model) {
        RunIndexing.setShutdown(false);
        DefaultAnswer defaultAnswer = new DefaultAnswer();
        if (!RunIndexing.isRunning()) {
            RunIndexing.setRunning(true);
            Thread thread = new StartIndexingThread();
            thread.start();
            defaultAnswer.setResult(true);
            return new ResponseEntity<>(defaultAnswer, HttpStatus.OK);
        }
        defaultAnswer.setResult(false);
        defaultAnswer.setError("Индексация уже запущена");
        return new ResponseEntity<>(defaultAnswer, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing(Model model) {
        DefaultAnswer defaultAnswer = new DefaultAnswer();
        if (RunIndexing.isRunning()) {
            RunIndexing.setShutdown(true);
            Thread thread = new Thread(() -> {
                runIndexing.stopIndexing();
                runIndexing = new RunIndexing(connectionProvider, siteService, handlerService, pageService);
                RunIndexing.setRunning(false);
            });
            thread.start();
            defaultAnswer.setResult(true);
            return new ResponseEntity<>(defaultAnswer, HttpStatus.OK);
        }
        defaultAnswer.setResult(false);
        defaultAnswer.setError("Индексация не запущена\"");
        return new ResponseEntity<>(defaultAnswer, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(URL url) {
        RunIndexing.setShutdown(false);
        DefaultAnswer defaultAnswer = new DefaultAnswer();

        String root = url.getProtocol() + "://" + url.getHost();
        String path = url.getPath();
        if (!RunIndexing.isRunning()) {
            RunIndexing.setRunning(true);
            if (siteService.getAll().stream().map(SiteDto::getUrl).anyMatch(root::equals) && handlerService.checkUrl(root + path, root)) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runIndexing.runIndexingPage(root, path);
                    }
                });
                thread.start();
                defaultAnswer.setResult(true);
                RunIndexing.setRunning(false);
                return new ResponseEntity<>(defaultAnswer, HttpStatus.OK);
            }
            RunIndexing.setRunning(false);
        }
        defaultAnswer.setResult(false);
        defaultAnswer.setError("Данная страница находится за пределами сайтов,\n" +
                               "указанных в конфигурационном файле");
        return new ResponseEntity<>(defaultAnswer, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/search")
    public SearchResponse search(@RequestParam("query") String query, @RequestParam(value = "site", required = false) String site,
                                    @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        DefaultAnswer defaultAnswer = new DefaultAnswer();
        //        if (!query.isBlank()) {
//
//            return searchResponse;
//        }

//        defaultAnswer.setResult(false);
//        defaultAnswer.setError("Задан пустой поисковый запрос\"");
//        return new ResponseEntity<>(defaultAnswer, HttpStatus.BAD_REQUEST);
        return searchService.search(query, site, offset, limit);
    }

    private class StartIndexingThread extends Thread {
        @Override
        public void run() {
            clearDataService.clearTables();
            log.info("Starting indexing");
            Set<Thread> threads = new HashSet<>(0);
            for (Site site : sites) {
                String siteUrl = site.getUrl();
                String siteTitle = site.getName();
                Runnable task = () -> {
                    runIndexing.runIndexing(siteUrl, siteTitle);
                };
                Thread thread1 = new Thread(task, siteUrl);
                thread1.start();
                threads.add(thread1);
            }
            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException ignored) {
                    log.error("Thread interrupted");
                }
            });
            log.info("Threads: {} finished", threads.size());
            RunIndexing.setRunning(false);
        }
    }
}
