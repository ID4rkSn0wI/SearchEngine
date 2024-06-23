package searchengine.services.indexing_services;

import java.util.Collection;

public interface PageService<T> {
    T getById(int id);
    Collection<T> getAll();
    void add(T site);
    void update(T site);
    void delete(Integer id);
    void deleteAll();
    void addWithDefineErrorCode(T site, int code);
    Integer findIdByPathAndSiteId(String path, int siteId);
    Integer countBySiteId(int siteId);
}
