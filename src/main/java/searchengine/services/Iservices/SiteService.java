package searchengine.services.Iservices;

import java.util.Collection;

public interface SiteService<T> {
    T getById(int id);
    Collection<T> getAll();
    void add(T site);
    void update(T site);
    void delete(Integer id);
    void deleteAll();
    T findSiteByUrl(String url);
//    Integer findIdByUrl(String url);
}
