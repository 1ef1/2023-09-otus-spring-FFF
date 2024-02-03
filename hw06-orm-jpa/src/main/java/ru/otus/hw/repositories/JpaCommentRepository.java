package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final  EntityManager em;

    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }


    @Override
    public Optional<Comment> findById(long id) {
        Comment comment = em.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        String query = "SELECT c FROM Comment c WHERE c.book.id = :bookId";
        return em.createQuery(query, Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }
}
