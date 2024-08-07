package searchengine.services.indexing_services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IndexService<T> {
    Collection<T> getAll();
    void add(T index);
    void update(T index);
    void delete(Integer id);
    void deleteAllByPageId(int pageId);
    List<Integer> getLemmaIdsByPageId(Integer pageId);
    Set<Integer> findPageIdsByLemmaId(int lemmaId);
    Float findRankByLemmaIdAndPageId(Integer lemmaId, Integer pageId);
    void truncate();
}
