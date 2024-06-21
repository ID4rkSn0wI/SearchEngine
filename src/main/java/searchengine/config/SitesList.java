package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesList {
    private List<Site> sites;
}
