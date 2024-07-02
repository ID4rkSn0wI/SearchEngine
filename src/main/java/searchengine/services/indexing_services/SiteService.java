package searchengine.services.indexing_services;

import java.util.Collection;

public interface SiteService<T> {
    T getById(int id);
    Collection<T> getAll();
    void add(T site);
    void update(T site);
    void delete(Integer id);
    T findSiteByUrl(String url);
    void truncate();
}
