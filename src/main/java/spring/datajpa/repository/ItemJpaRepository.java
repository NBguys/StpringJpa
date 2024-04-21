package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import spring.datajpa.entity.Item;

@Repository
public class ItemJpaRepository {

    @PersistenceContext
    EntityManager em;

    public Item save(Item item) {
        em.persist(item);
        return item;
    }

}
