package searchengine.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Getter
public class Config {
    private final SitesList sitesList;
    private final List<Site> sites;
    private final HashMap<String, Integer> limits;

    @Autowired
    public Config(SitesList sitesList) {
        this.sitesList = sitesList;
        this.sites = sitesList.getSites();
        this.limits = (HashMap<String, Integer>) sites.stream().collect(Collectors.toMap(Site::getUrl, Site::getLimit));
    }
}
