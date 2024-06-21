package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.Site;

public interface SiteRepo extends JpaRepository<Site, Integer> {
    Site findByUrl(String url);
//    @Query(value = "select id from Site where Site.url = ?1" )
//    Integer findIdByUrl(String url);
}
