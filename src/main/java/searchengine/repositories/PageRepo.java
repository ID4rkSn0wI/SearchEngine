package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.indexing.PageDto;
import searchengine.model.Page;

public interface PageRepo extends JpaRepository<Page, Integer> {
    @Query(value = "SELECT id FROM Page WHERE Page.path=:path and Page.site_id=:siteId LIMIT 1", nativeQuery = true)
    Integer findIdByPathAndSiteId(String path, int siteId);

    @Query(value = "SELECT count(*) FROM Page WHERE Page.site_id=:siteId", nativeQuery = true)
    Integer countBySiteId(int siteId);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE Page", nativeQuery = true)
    void truncate();
}
