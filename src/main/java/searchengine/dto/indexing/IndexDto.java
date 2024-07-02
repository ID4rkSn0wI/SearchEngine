package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndexDto {
    private int id;
    private int pageId;
    private int lemmaId;
    private float rank;

    @Override
    public boolean equals(Object obj) {
        IndexDto indexDto = (IndexDto) obj;
        return this.pageId == indexDto.pageId && this.lemmaId == indexDto.lemmaId;
    }

    @Override
    public int hashCode() {
        return Integer.toString(this.pageId).hashCode() + Integer.toString(this.lemmaId).hashCode();
    }
}
