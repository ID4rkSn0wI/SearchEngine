package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;

import java.util.Collection;
import java.util.List;

public interface LemmaRepo extends JpaRepository<Lemma, Integer> {
    @Query(value = "SELECT * FROM Lemma WHERE Lemma.lemma=:lemma and Lemma.site_id=:siteId", nativeQuery = true)
    Lemma findLemmaByLemmaAndSiteId(String lemma, int siteId);

    @Query(value = "SELECT COUNT(*) FROM Lemma WHERE Lemma.site_id=:siteId", nativeQuery = true)
    Integer countBySiteId(int siteId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Lemma WHERE Lemma.id IN :lemmaIds", nativeQuery = true)
    void deleteAllByIds(Collection<Integer> lemmaIds);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Lemma SET Lemma.frequency = Lemma.frequency + 1 WHERE Lemma.lemma=:lemma and Lemma.site_id=:siteId", nativeQuery = true)
    void incrementFrequency(String lemma, int siteId);

    @Query("SELECT l FROM Lemma as l WHERE l.lemma=:lemma")
    List<Lemma> findLemmasDtoByLemma(String lemma);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE Lemma", nativeQuery = true)
    void truncate();
}
