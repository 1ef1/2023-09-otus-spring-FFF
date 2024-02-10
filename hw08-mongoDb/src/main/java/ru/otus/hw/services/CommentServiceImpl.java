package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(String commentText, String bookid) {
        return save("0", commentText, bookid);
    }

    @Override
    @Transactional
    public Comment update(String id, String commentText, String bookid) {
        return save(id, commentText, bookid);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }


    private Comment save(String id, String commentText, String bookid) {

        var book = bookRepository.findById(bookid)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookid)));


        Comment comment = new Comment(id, book, commentText);
        return commentRepository.save(comment);
    }

}
