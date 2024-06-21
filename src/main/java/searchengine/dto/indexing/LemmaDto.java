package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class LemmaDto {
    private int id;
    private int siteId;
    private String lemma;
    private int frequency;
}
