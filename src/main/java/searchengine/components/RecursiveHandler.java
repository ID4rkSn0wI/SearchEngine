package searchengine.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.PageDto;
import searchengine.services.HandlerService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

@Slf4j
@Component
public class RecursiveHandler extends RecursiveAction {
    private final PageDto pageDto;
    private final HandlerService handlerService;


    @Autowired
    public RecursiveHandler(PageDto pageDto, HandlerService handlerService) {
        this.pageDto = pageDto;
        this.handlerService = handlerService;
    }

    @Override
    protected void compute() {
        List<RecursiveAction> actions = new ArrayList<>();
        if (!ForkJoinTask.getPool().isShutdown()) {
            handlerService.handlePage(pageDto);
        }
        for (String path : pageDto.getSubPaths()) {
            PageDto pageDtoChild = new PageDto();
            pageDtoChild.setPath(path);
            pageDtoChild.setRoot(pageDto.getRoot());
            pageDtoChild.setSiteId(pageDto.getSiteId());
            pageDtoChild.setSubPaths(new HashSet<String>(0));

            if (!ForkJoinTask.getPool().isShutdown()) {
                RecursiveHandler action = new RecursiveHandler(pageDtoChild, handlerService);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                    log.info("Interrupted");
                    Thread.currentThread().interrupt();
                }

                action.fork();
                actions.add(action);
            }
        }
        actions.forEach(ForkJoinTask::join);
    }
}
