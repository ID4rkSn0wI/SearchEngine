package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.SiteDto;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.services.implservices.LemmaServiceImpl;
import searchengine.services.implservices.PageServiceImpl;
import searchengine.services.implservices.SiteServiceImpl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();

    private SiteServiceImpl siteService;
    private PageServiceImpl pageService;
    private LemmaServiceImpl lemmaService;

    @Autowired
    public StatisticsServiceImpl(SiteServiceImpl siteService, PageServiceImpl pageService, LemmaServiceImpl lemmaService) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
    }

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(siteService.getAll().size());
        total.setPages(pageService.getAll().size());
        total.setLemmas(lemmaService.getAll().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        Collection<SiteDto> sites = siteService.getAll();
        for(SiteDto site : sites) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = random.nextInt(1_000);
            int lemmas = pages * random.nextInt(1_000);
            item.setPages(pageService.countBySiteId(site.getId()));
            item.setLemmas(lemmaService.countBySiteId(site.getId()));
            item.setStatus(site.getStatus());
            if (site.getLastError() != null) {
                item.setError(site.getLastError());
            }
            item.setStatusTime(site.getStatusTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
