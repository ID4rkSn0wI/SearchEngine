package searchengine.services.indexing_services;

import java.util.Collection;

public interface PageService<T> {
    T getById(int id);
    Collection<T> getAll();
    void save(T page);
    Integer saveAndReturnId(T page);
    void update(T page);
    void delete(Integer id);
    void addWithDefineErrorCode(T page, int code);
    Integer findIdByPathAndSiteId(String path, int siteId);
    Integer countBySiteId(int siteId);
    void truncate();
}
