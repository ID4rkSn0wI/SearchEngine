package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;

import java.util.List;
import java.util.Set;

public interface IndexRepo extends JpaRepository<Index, Integer> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM search_index where search_index.page_id=:pageId", nativeQuery = true)
    void deleteAllByPageId(int pageId);

    @Query(value = "SELECT lemma_id FROM search_index WHERE search_index.page_id=:pageId", nativeQuery = true)
    List<Integer> getLemmaIdsByPageId(Integer pageId);

    @Query(value = "SELECT page_id FROM search_index WHERE search_index.lemma_id=:lemmaId", nativeQuery = true)
    Set<Integer> findPageIdsByLemmaId(int lemmaId);

    @Query(value = "SELECT lemma_rank FROM search_index WHERE lemma_id=:lemmaId and page_id=:pageId", nativeQuery = true)
    Float findRankByLemmaIdAndPageId(Integer lemmaId, Integer pageId);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE search_index", nativeQuery = true)
    void truncate();
}
