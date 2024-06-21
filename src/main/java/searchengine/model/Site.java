package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "DATETIME NOT NULL", nullable = false)
    private LocalDateTime statusTime;

    @Column(columnDefinition = "TEXT", name = "last_error")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL", nullable = false)
    private String url;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL", nullable = false)
    private String name;
}
