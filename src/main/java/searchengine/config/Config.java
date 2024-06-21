package searchengine.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Getter
public class Config {
    private final SitesList sitesList;
    private final List<Site> sites;

    @Autowired
    public Config(SitesList sitesList) {
        this.sitesList = sitesList;
        this.sites = sitesList.getSites();
    }
}
