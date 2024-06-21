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
@Table(indexes = @Index(columnList = "path", name = "path_index"), name="page")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT", nullable = false, name = "site_id")
    private int siteId;

    @Column(columnDefinition = "VARCHAR(280)", nullable = false)
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    private Site site;
}
