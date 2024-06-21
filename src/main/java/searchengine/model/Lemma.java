package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Index;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "lemma", indexes = @Index(columnList = "lemma", name = "lemma_index"))
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "site_id", columnDefinition = "INT NOT NULL", nullable = false)
    private int siteId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL", nullable = false)
    private String lemma;

    @Column(columnDefinition = "INT NOT NULL", nullable = false)
    private int frequency;
}
