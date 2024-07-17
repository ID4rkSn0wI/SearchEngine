package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lemma_id", nullable = false, foreignKey=@ForeignKey(name = "FK_index_lemma"))
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Lemma lemma;

    @Column(columnDefinition = "FLOAT NOT NULL", nullable = false, name = "lemma_rank")
    private float rank;
}
