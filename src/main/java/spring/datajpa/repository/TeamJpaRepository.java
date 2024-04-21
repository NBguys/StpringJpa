package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import spring.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext
    EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(em.find(Team.class, id));
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

    public void delete(Team team) {
        em.remove(team);
    }

}
