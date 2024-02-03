package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {


    @PersistenceContext
    private final  EntityManager entityManager;

    public JpaGenreRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<Genre> query = entityManager.createQuery("SELECT g FROM Genre g WHERE g.id IN :ids", Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            entityManager.persist(genre);
            return genre;
        } else {
            return entityManager.merge(genre);
        }
    }
}
