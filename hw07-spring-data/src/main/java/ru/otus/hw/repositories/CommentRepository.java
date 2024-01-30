package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(Long id);

    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId")
    List<Comment> findAllByBookId(Long bookId);

    void deleteById(Long id);
}
