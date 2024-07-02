package searchengine.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SearchData {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private Float relevance;

    public static int compareByRelevance(SearchData searchData, SearchData secondSearchData){
        return Float.compare(secondSearchData.getRelevance(), searchData.getRelevance());
    }
}
