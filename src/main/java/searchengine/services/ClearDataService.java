package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Slf4j
@Service
public class ClearDataService {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ClearDataService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void clearTables() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = entityManager.unwrap(Session.class);
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            for (String table: List.of("site", "page", "search_index", "lemma")) {
                String sql = "TRUNCATE TABLE " + table + ";";
                session.createSQLQuery(sql).executeUpdate();
            }
            tx.commit();

        } catch (HibernateException hex) {
            if (tx != null) {
                tx.rollback();
            } else {
                log.error("Clear tables failed", hex);
            }
        } finally {
            session.close();
        }
    }
}
