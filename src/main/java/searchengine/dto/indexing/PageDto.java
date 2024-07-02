package searchengine.dto.indexing;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
public class PageDto {
    private Integer id;
    private String path;
    private int siteId;
    private int code;
    private String content;
    private String root;

    private HashSet<String> subPaths;
}
