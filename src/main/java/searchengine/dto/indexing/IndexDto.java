package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class IndexDto {
    private int id;
    private int pageId;
    private int lemmaId;
    private float rank;
}
