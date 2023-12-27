package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    public Comment insert(long id, long bookId, String commentText) {
        return save(id, bookId, commentText);
    }

    @Override
    public Comment update(long id, long bookId, String commentText) {
        return save(id, bookId, commentText);
    }

    public Comment save(long id, long bookId, String commentText) {
        var comment = new Comment(id, bookId, commentText);
        return commentRepository.save(comment);
    }

    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
