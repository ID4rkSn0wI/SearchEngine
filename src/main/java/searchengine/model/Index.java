package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "search_index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT NOT NULL", nullable = false, name = "page_id")
    private int pageId;

    @Column(columnDefinition = "INT NOT NULL", nullable = false, name = "lemma_id")
    private int lemmaId;

    @Column(columnDefinition = "FLOAT NOT NULL", nullable = false, name = "lemma_rank")
    private float rank;
}
