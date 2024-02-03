package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(String commentText, long bookid) {
        return save(0, commentText, bookid);
    }

    @Override
    @Transactional
    public Comment update(long id, String commentText, long bookid) {
        return save(id, commentText, bookid);
    }


    private Comment save(long id, String commentText, long bookid) {

        var book = bookRepository.findById(bookid)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookid)));


        Comment comment = new Comment(id, book, commentText);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
