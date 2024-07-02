package searchengine.utils;

import lombok.extern.slf4j.Slf4j;
import searchengine.components.RunIndexing;
import searchengine.dto.indexing.PageDto;
import searchengine.services.SiteHandlerService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

@Slf4j
public class RecursiveHandler extends RecursiveAction {
    private final SiteHandlerService siteHandlerService;
    private final PageDto pageDto;

    public RecursiveHandler(PageDto pageDto, SiteHandlerService siteHandlerService) {
        this.siteHandlerService = siteHandlerService;
        this.pageDto = pageDto;
    }

    @Override
    protected void compute() {
        List<RecursiveAction> actions = new ArrayList<>();
        if (RunIndexing.isShutdown()) {return;}
        siteHandlerService.handlePage(pageDto);
        if (RunIndexing.isShutdown()) {return;}
        for (String path : pageDto.getSubPaths()) {
            PageDto pageDtoChild = new PageDto();
            pageDtoChild.setPath(path);
            pageDtoChild.setRoot(pageDto.getRoot());
            pageDtoChild.setSiteId(pageDto.getSiteId());
            pageDtoChild.setSubPaths(new HashSet<String>(0));

            RecursiveHandler action = new RecursiveHandler(pageDtoChild, siteHandlerService);

            action.fork();
            actions.add(action);
        }
        actions.forEach(ForkJoinTask::join);
    }
}
