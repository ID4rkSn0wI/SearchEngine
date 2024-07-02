package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;

public interface SiteRepo extends JpaRepository<Site, Integer> {
    Site findByUrl(String url);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE Site", nativeQuery = true)
    void truncate();
}
