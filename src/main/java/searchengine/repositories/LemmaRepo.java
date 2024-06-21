package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;

import java.util.Collection;
import java.util.List;

public interface LemmaRepo extends JpaRepository<Lemma, Integer> {
    @Query("SELECT l FROM Lemma as l WHERE l.lemma=:lemma and l.site_id=:siteId")
    Lemma findLemmaDtoByLemmaAndSiteId(String lemma, int siteId);

    @Query(value = "SELECT COUNT(*) FROM Lemma WHERE Lemma.site_id=:siteId", nativeQuery = true)
    Integer countBySiteId(int siteId);

    @Query(value = "SELECT id FROM Lemma WHERE Lemma.lemma=:lemma and Lemma.site_id=:siteId", nativeQuery = true)
    Integer getIdByLemmaAndSiteId(String lemma, int siteId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Lemma WHERE Lemma.id IN :lemmaIds", nativeQuery = true)
    void deleteAllByIds(Collection<Integer> lemmaIds);

    @Query("SELECT l FROM Lemma as l WHERE l.lemma=:lemma")
    List<Lemma> findLemmasDtoByLemma(String lemma);
}
