package searchengine.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SearchResponse {
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int count;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SearchData> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
}
