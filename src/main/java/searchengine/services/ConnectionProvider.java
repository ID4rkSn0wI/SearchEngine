package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.PageDto;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.utils.RandomUserAgent;

import java.io.IOException;

@Slf4j
@Service
public class ConnectionProvider {
    private final PageServiceImpl pageService;
    @Value("${config.request-properties.referrer}")
    private String referrer;
    @Value("${config.time-between-requests}")
    private int timeBetweenRequests;

    @Autowired
    public ConnectionProvider(PageServiceImpl pageService) {
        this.pageService = pageService;
    }

    public synchronized String getDoc(String path) {
        Document document = null;
        String exception = null;
        try {
            Thread.sleep(timeBetweenRequests);
            document = Jsoup.connect(path)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .maxBodySize(0)
                    .timeout(5000)
                    .get();

        } catch (HttpStatusException ex) {
            exception = ex.getMessage();
            return exception;
        } catch (IOException e) {
            exception = e.getMessage();
            return exception;
        } catch (InterruptedException e) {
            exception = e.getMessage();
            throw new RuntimeException(e);
        }
        return exception;
    }

    public synchronized Document getDoc(PageDto pageDto) {
        Document document = null;
        try {
            Thread.sleep(1000);
            document = Jsoup.connect(pageDto.getRoot() + pageDto.getPath())
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .maxBodySize(0)
                    .timeout(5000)
                    .get();

        } catch (HttpStatusException ex) {
            pageService.addWithDefineErrorCode(pageDto, ex.getStatusCode());
            return document;
        } catch (IOException e) {
            pageService.addWithDefineErrorCode(pageDto, 404);
            return document;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return document;
    }
}
