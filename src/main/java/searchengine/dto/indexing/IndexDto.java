package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class IndexDto {
    private int id;
    private int pageId;
    private Integer lemmaId;
    private float rank;
    private String lemma;
    private Integer siteId;

    @Override
    public boolean equals(Object obj) {
        IndexDto indexDto = (IndexDto) obj;
        if (indexDto.lemmaId != null) {
            return this.pageId == indexDto.pageId && Objects.equals(this.lemmaId, indexDto.lemmaId);
        }
        return this.pageId == indexDto.pageId && Objects.equals(this.lemma, indexDto.lemma);
    }

    @Override
    public int hashCode() {
        return Integer.toString(this.pageId).hashCode() + Integer.toString(this.lemmaId).hashCode();
    }
}
