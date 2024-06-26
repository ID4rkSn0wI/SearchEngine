package searchengine.dto.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import searchengine.model.Status;

@Data
public class DetailedStatisticsItem {
    private String url;
    private String name;
    private Status status;
    private long statusTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    private int pages;
    private int lemmas;
}
