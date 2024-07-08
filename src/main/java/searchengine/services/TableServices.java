package searchengine.services;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class TableServices {
    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;
    private final IndexServiceImpl indexService;
    private final LemmaServiceImpl lemmaService;

    @Autowired
    public TableServices(SiteServiceImpl siteService, PageServiceImpl pageService, IndexServiceImpl indexService, LemmaServiceImpl lemmaService) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.indexService = indexService;
        this.lemmaService = lemmaService;
    }

    public void clearTables() {
        siteService.truncate();
        pageService.truncate();
        indexService.truncate();
        lemmaService.truncate();
    }
}
