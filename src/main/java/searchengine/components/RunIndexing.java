package searchengine.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.PageDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.Status;
import searchengine.services.*;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.services.implservices.SiteServiceImpl;
import searchengine.utils.ForkJoinUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RunIndexing {
    private final ConnectionProvider connectionProvider;
    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;
    private ForkJoinPool forkJoinPool;
    private final HandlerService handlerService;
    private volatile boolean isRunning = false;

    @Autowired
    public RunIndexing(ConnectionProvider connectionProvider, SiteServiceImpl siteService, HandlerService handlerService, PageServiceImpl pageService) {
        this.connectionProvider = connectionProvider;
        this.siteService = siteService;
        this.handlerService = handlerService;
        forkJoinPool = ForkJoinUtil.forkJoinPool;
        this.pageService = pageService;
    }

    public void runIndexing(String path, String name) {
        String siteAnswer = connectionProvider.getDoc(path);

        SiteDto siteDto = new SiteDto();
        siteDto.setName(name);
        siteDto.setUrl(path);
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setStatus(Status.INDEXING);
        if (siteAnswer != null) {
            siteDto.setLastError(siteAnswer);
            siteDto.setStatus(Status.FAILED);
            siteService.add(siteDto);
            return;
        }
        siteService.add(siteDto);

        PageDto pageDto = new PageDto();
        log.info("Path: {}", path);
        pageDto.setRoot(path);
        pageDto.setPath("/");
        pageDto.setSiteId(siteService.findSiteByUrl(path).getId());
        pageDto.setSubPaths(new HashSet<String>(0));

        long startTime = System.currentTimeMillis();

        RecursiveHandler recursiveHandler = new RecursiveHandler(pageDto, handlerService);
        forkJoinPool.invoke(recursiveHandler);

        siteDto.setId(siteService.findSiteByUrl(path).getId());
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setStatus(Status.INDEXED);

        if (!forkJoinPool.isShutdown()) {
            long finishTime = System.currentTimeMillis() - startTime;
            log.info("Indexed {} for {} ms", name, finishTime);
        } else {
            siteDto.setStatus(Status.FAILED);
            siteDto.setLastError("Индексирование остановлена пользователем");
            log.info("Indexing failed for: {}", path);
        }

        siteService.update(siteDto);
    }

    public void stopIndexing() {
        try {
            forkJoinPool.shutdown();
            forkJoinPool.awaitTermination(40, TimeUnit.SECONDS);
        }
        catch (InterruptedException ignored) {
            log.error("Indexing interrupted");
        }
        finally {
            forkJoinPool.shutdownNow();
            ForkJoinUtil.refreshForkJoinPool();
            handlerService.clearUniqueLinks();
        }
    }

    public void runIndexingPage(String root, String path) {
        int site_id = siteService.findSiteByUrl(root).getId();
        PageDto pageDto = new PageDto();
        pageDto.setPath(path);
        pageDto.setSiteId(site_id);
        pageDto.setRoot(root);
        Integer id = pageService.findIdByPathAndSiteId(path, site_id);
        if (id != null) {
            log.info("Found id: {}", id);
            pageDto.setId(id);
        }

        handlerService.handleSinglePage(pageDto);
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean getRunning() {
        return isRunning;
    }
}
