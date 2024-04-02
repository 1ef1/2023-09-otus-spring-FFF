package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Book> findByAuthorId(String id) {
        return bookRepository.findByAuthorId(id);
    }

    @Override
    @Transactional
    public Book insert(String title, String authorId, String genresId) {
        return save("0", title, authorId, genresId);
    }

    @Override
    @Transactional
    public Book update(String id, String title, String authorId, String genresId) {
        return save(id, title, authorId, genresId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

       private Book save(String id, String title, String authorId, String genresId) {
        if (genresId == null) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genre = genreRepository.findById(genresId);

        var book = new Book(id, title, author, genre.get());
        return bookRepository.save(book);
    }

    private BookDTO toDto(Book book) {
        String authorName = "";
        String genreName = "";
        if (book.getAuthor() != null) {
            authorName = book.getAuthor().getFullName();
        }
        if (book.getGenre() != null) {
            genreName = book.getGenre().getName();
        }

        return new BookDTO(book.getId(), book.getTitle(), authorName, genreName);
    }
}
