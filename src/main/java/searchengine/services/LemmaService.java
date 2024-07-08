package searchengine.services;

import java.util.Collection;
import java.util.List;

public interface LemmaService<T> {
    Collection<T> getAll();
    void addAll(Collection<T> lemmas);
    void update(T lemma);
    void delete(Integer id);
    T findLemmaDtoByLemmaAndSiteId(String lemma, int siteId);
    Integer countBySiteId(int site_id);
    void deleteAllByIds(Collection<Integer> lemmaIds);
    List<T> findLemmasDtoByLemma(String lemma);
    void truncate();
}