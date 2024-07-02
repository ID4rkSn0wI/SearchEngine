package searchengine.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.Config;
import searchengine.config.Site;
import searchengine.dto.indexing.PageDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.Status;
import searchengine.services.*;
import searchengine.utils.ForkJoinUtil;
import searchengine.utils.RecursiveHandler;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.*;

@Slf4j
@Component
public class RunIndexing {
    private final ConnectionProvider connectionProvider;
    private final TableServices tableServices;
    private ForkJoinPool forkJoinPool;
    @Getter
    private volatile static boolean running = false;
    @Getter
    private volatile static boolean shutdown = false;
    private final Config config;
    private ThreadPoolExecutor executor;
    private final LuceneMorphologyService luceneMorphologyService;

    @Autowired
    public RunIndexing(ConnectionProvider connectionProvider, TableServices tableServices, Config config, LuceneMorphologyService luceneMorphologyService) {
        this.connectionProvider = connectionProvider;
        this.tableServices = tableServices;
        this.config = config;
        this.luceneMorphologyService = luceneMorphologyService;
        this.refreshThreads();
    }

    public void startIndexing() {
        tableServices.clearTables();
        for (Site site : config.getSites()) {
            String siteUrl = site.getUrl();
            String siteTitle = site.getName();
            executor.execute(() -> this.runIndexing(siteUrl, siteTitle));
        }
    }

    public void startIndexingPage(String root, String path) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> runIndexingPage(root, path));
    }

    private void runIndexing(String path, String name) {
        String siteAnswer = connectionProvider.getDoc(path);

        SiteDto siteDto = new SiteDto();
        siteDto.setName(name);
        siteDto.setUrl(path);
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setStatus(Status.INDEXING);
        if (siteAnswer != null) {
            siteDto.setLastError(siteAnswer);
            siteDto.setStatus(Status.FAILED);
            tableServices.getSiteService().save(siteDto);
            return;
        }
        tableServices.getSiteService().save(siteDto);

        PageDto pageDto = new PageDto();
        log.info("Path: {}", path);
        pageDto.setRoot(path);
        pageDto.setPath("/");
        pageDto.setSiteId(tableServices.getSiteService().findSiteByUrl(path).getId());
        pageDto.setSubPaths(new HashSet<String>(0));

        long startTime = System.currentTimeMillis();
        SiteHandlerService siteHandlerService = new SiteHandlerService(connectionProvider, luceneMorphologyService, tableServices);
        RecursiveHandler recursiveHandler = new RecursiveHandler(pageDto, siteHandlerService);
        forkJoinPool.invoke(recursiveHandler);

        siteHandlerService.saveAllLemmasAndIndexes();
        siteDto.setId(tableServices.getSiteService().findSiteByUrl(path).getId());
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setStatus(Status.INDEXED);

        if (!RunIndexing.isShutdown()) {
            long finishTime = System.currentTimeMillis() - startTime;
            log.info("Indexed {} for {} ms", name, finishTime);
        } else {
            siteDto.setStatus(Status.FAILED);
            siteDto.setLastError("Индексирование остановлена пользователем");
            log.info("Indexing failed for: {}", path);
        }

        tableServices.getSiteService().update(siteDto);

        synchronized (RunIndexing.this) {
            if (executor.getActiveCount() == 1) {
                RunIndexing.setRunning(false);
            }
        }
    }

    public void stopIndexing() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(this::stopIndexingNow);
    }

    private void stopIndexingNow() {
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            forkJoinPool.shutdownNow();
        } catch (InterruptedException e) {
            executor.shutdownNow();
        } finally {
            RunIndexing.setRunning(false);
            RunIndexing.setShutdown(false);
        }
    }

    public void runIndexingPage(String root, String path) {
        int site_id = tableServices.getSiteService().findSiteByUrl(root).getId();
        PageDto pageDto = new PageDto();
        pageDto.setPath(path);
        pageDto.setSiteId(site_id);
        pageDto.setRoot(root);
        Integer id = tableServices.getPageService().findIdByPathAndSiteId(path, site_id);
        if (id != null) {
            log.info("Found id: {}", id);
            pageDto.setId(id);
        }
        new SiteHandlerService(connectionProvider, luceneMorphologyService, tableServices).handleSinglePage(pageDto);
        RunIndexing.setRunning(false);
    }

    public static void setRunning(boolean running) {
        RunIndexing.running = running;
    }

    public static void setShutdown(boolean shutdown) {
        RunIndexing.shutdown = shutdown;
    }

    public void refreshThreads() {
        forkJoinPool = ForkJoinUtil.forkJoinPool;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getSites().size());
    }
}
