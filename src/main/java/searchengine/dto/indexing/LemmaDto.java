package searchengine.dto.indexing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class LemmaDto {
    private int id;
    private int siteId;
    private String lemma;
    private int frequency;

    @Override
    public boolean equals(Object obj) {
        LemmaDto indexDto = (LemmaDto) obj;
        return this.siteId == indexDto.siteId && Objects.equals(this.lemma, indexDto.lemma);
    }

    @Override
    public int hashCode() {
        return this.lemma.hashCode() + Integer.toString(this.siteId).hashCode();
    }
}
