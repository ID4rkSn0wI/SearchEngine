package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import searchengine.components.RunIndexing;
import searchengine.config.Config;
import searchengine.dto.answers.DefaultAnswer;
import searchengine.dto.indexing.SiteDto;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.*;
import searchengine.services.implservices.StatisticsServiceImpl;

import java.net.URL;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {
    private final StatisticsServiceImpl statisticsService;
    private final ConnectionProvider connectionProvider;
    private final SearchService searchService;
    private RunIndexing runIndexing;
    private final SiteHandlerService siteHandlerService;
    private final Config config;
    private final TableServices tableServices;

    @Autowired
    public ApiController(StatisticsServiceImpl statisticsService, ConnectionProvider connectionProvider, RunIndexing runIndexing, SiteHandlerService siteHandlerService, SearchService searchService, Config config, TableServices tableServices) {
        this.statisticsService = statisticsService;
        this.connectionProvider = connectionProvider;
        this.config = config;
        this.runIndexing = runIndexing;
        this.siteHandlerService = siteHandlerService;
        this.searchService = searchService;
        this.tableServices = tableServices;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics(Model model) {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing(Model model) {
        RunIndexing.setShutdown(false);
        DefaultAnswer defaultAnswer = new DefaultAnswer();
        if (!RunIndexing.isRunning()) {
            RunIndexing.setRunning(true);
            runIndexing.startIndexing();
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
            runIndexing.stopIndexing();
            runIndexing = new RunIndexing(connectionProvider, tableServices, siteHandlerService, config);
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
        defaultAnswer.setResult(false);
        String root = url.getProtocol() + "://" + url.getHost();
        String path = url.getPath();
        if (!RunIndexing.isRunning()) {
            if (siteHandlerService.checkUrl(root + path, root)) {
                if (tableServices.getSiteService().getAll().stream().map(SiteDto::getUrl).anyMatch(root::equals)) {
                    RunIndexing.setRunning(true);
                    runIndexing.startIndexingPage(root, path);
                    defaultAnswer.setResult(true);
                    return new ResponseEntity<>(defaultAnswer, HttpStatus.OK);
                }
                defaultAnswer.setError("Данная страница находится за пределами сайтов,\n" +
                        "указанных в конфигурационном файле");
            }
            defaultAnswer.setError("Неверно указана ссылка на страницу");
        } else {
            defaultAnswer.setError("Индексация уже запущена");
        }
        return new ResponseEntity<>(defaultAnswer, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query, @RequestParam(value = "site", required = false) String site,
                                    @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return searchService.search(query, site, offset, limit);
    }
}
