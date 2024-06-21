package searchengine.dto.indexing;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@Component
public class PageDto {
    private Integer id;
    private String path;
    private int siteId;
    private int code;
    private String content;
    private String root;

    private HashSet<String> subPaths;
}
