package searchengine.services.indexing_services;

import java.util.Collection;
import java.util.List;

public interface LemmaService<T> {
    T getById(int id);
    Collection<T> getAll();
    void add(T site);
    void update(T site);
    void delete(Integer id);
    T findLemmaDtoByLemmaAndSiteId(String lemma, int siteId);
    Integer countBySiteId(int site_id);
    Integer getIdByLemmaAndSiteId(String lemma, int siteId);
    void deleteAllByIds(Collection<Integer> lemmaIds);
    List<T> findLemmasDtoByLemma(String lemma);
}
