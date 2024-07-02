package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import searchengine.model.Status;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class SiteDto {
    private int id;
    private String name;
    private String url;
    private Status status;
    private String lastError;
    private LocalDateTime statusTime;
}
