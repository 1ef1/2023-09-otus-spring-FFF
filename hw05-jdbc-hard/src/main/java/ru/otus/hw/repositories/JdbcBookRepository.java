package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        String sql = "SELECT b.id, b.title, a.id AS author_id, a.full_name, g.id AS genre_id, g.name " +
                "FROM books b " +
                "JOIN authors a ON a.id = b.author_id " +
                "JOIN books_genres bg ON bg.book_id = b.id " +
                "JOIN genres g ON g.id = bg.genre_id " +
                "WHERE b.id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return namedParameterJdbcOperations.query(sql, parameters, new BookResultSetExtractor());
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "delete from books where books.id = :bookId";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("bookId", id);
        namedParameterJdbcOperations.update(sql, parameters);
    }

    private List<Book> getAllBooksWithoutGenres() {

        String sql = "select b.id, b.title, a.id as authorid, a.full_name from books b " +
                "inner join authors a on b.author_id = a.id";
        return jdbc.query(sql, new JdbcBookRepository.BookRowMapper());
    }

    @SuppressWarnings("checkstyle:LineLength")
    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("select book_id, genre_id from books_genres",
                new JdbcBookRepository.BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            List<Genre> bookGenres = getBookGenres(genres, relations, book.getId());
            book.setGenres(bookGenres);
        }
    }

    private List<Genre> getBookGenres(List<Genre> genres, List<BookGenreRelation> relations, long bookId) {
        List<Long> genreIds = relations.stream()
                .filter(relation -> relation.bookId() == bookId)
                .map(BookGenreRelation::genreId)
                .toList();

        return genres.stream()
                .filter(genre -> genreIds.contains(genre.getId()))
                .collect(Collectors.toList());
    }

    private Book insert(Book book) {
        long bookId = insertBookAndGetId(book);
        book.setId(bookId);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        if (updateBook(book) == 0) {
            throw new EntityNotFoundException("no updated rows");
        } else {
            removeGenresRelationsFor(book);
            batchInsertGenresRelationsFor(book);
        }
        return book;
    }

    private int updateBook(Book book) {
        String updateSql = "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId())
                .addValue("id", book.getId());

        return namedParameterJdbcOperations.update(updateSql, namedParameters);
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String insertBooksGenres = "insert into books_genres (book_id, genre_id) values  (?, ?)";
        List<Genre> genreList = book.getGenres();

        jdbc.batchUpdate(insertBooksGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setLong(1, book.getId());
                preparedStatement.setLong(2, genreList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    private void insertGenres(Book book, long bookId) {
        List<Genre> genreList = new ArrayList<>(book.getGenres());
        List<Genre> copiedgenreList = new ArrayList<>(genreList);
        genreList.removeIf(genreRepository.findAll()::contains);
        if (genreList.isEmpty()) {
            insetBooksGenres(bookId, copiedgenreList);
        } else {
            throw new EntityNotFoundException("genre doesn't exist");
        }
    }

    private void insetBooksGenres(long bookId, List<Genre> genreList) {
        String insertSql = "insert into books_genres (book_id, genre_id) values (:bookId, :genreId)";
        for (long idGanre : genreList.stream().map(Genre::getId).toList()) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("bookId", bookId);
            parameters.addValue("genreId", idGanre);
            namedParameterJdbcOperations.update(insertSql, parameters);
        }
    }

    private long insertBookAndGetId(Book book) {

        String insertSql = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", book.getTitle());
        parameters.addValue("authorId", book.getAuthor().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(insertSql, parameters, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "delete from books_genres b " +
                "where b.book_id = :bookId";

        Map<String, Object> namedParameters = Map.of("bookId", book.getId());

        namedParameterJdbcOperations.update(sql, namedParameters);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            Author author = new Author();
            author.setId(rs.getLong("authorId"));
            author.setFullName(rs.getString("full_name"));

            book.setAuthor(author);
            book.setGenres(null);

            return book;
        }
    }

    public class BookResultSetExtractor implements ResultSetExtractor<Optional<Book>> {

        @Override
        public Optional<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> books = new HashMap<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                if (!books.containsKey(id)) {
                    Book book = new Book();
                    book.setId(id);
                    book.setTitle(rs.getString("title"));
                    Author author = new Author();
                    author.setId(rs.getLong("author_id"));
                    author.setFullName(rs.getString("full_name"));
                    book.setAuthor(author);
                    book.setGenres(new ArrayList<>());
                    books.put(id, book);
                }

                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("name"));
                books.get(id).getGenres().add(genre);
            }

            return Optional.ofNullable(books.values().stream().findFirst().orElse(null));
        }
    }


    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            BookGenreRelation bookGenreRelation = new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
            return bookGenreRelation;
        }
    }
}
