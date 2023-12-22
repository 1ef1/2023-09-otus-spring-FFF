package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        String sql = "select b.id book_id, b.title, b.author_id, a.full_name  from books b , authors a " +
                "where b.author_id =a.id  and b.id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        Book book = namedParameterJdbcOperations.query(sql, parameters, new BookResultSetExtractor());
        return Optional.ofNullable(book);
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
        Map<Long, List<Genre>> bookIdToGenresMap = new HashMap<>();

        for (BookGenreRelation relation : relations) {
            long bookId = relation.bookId();
            long genreId = relation.genreId();
            Genre genre = genreRepository.findAllByIds(Set.of(genreId)).stream().findFirst().orElse(null);
            if (genre != null) {
                bookIdToGenresMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(genre);
            }
        }

        for (Book book : booksWithoutGenres) {
            long bookId = book.getId();
            List<Genre> bookGenres = bookIdToGenresMap.getOrDefault(bookId, new ArrayList<>());
            book.setGenres(bookGenres);
        }

    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        if (keyHolder.getKey() != null) {
            book.setId(keyHolder.getKeyAs(Long.class));
        }
        long newIdBook = batchInsertGenresRelationsFor(book);
        book.setId(newIdBook);
        return book;
    }

    private Book update(Book book) {
        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД // Вопрос если меняется жанр ??

//        boolean noGenreUpdate = false;

        Optional<Book> book1 = this.findById(book.getId());
        if (book1.isPresent()) {
            if (!book1.get().getGenres().equals(book.getGenres())) {
//                noGenreUpdate = true;
                removeGenresRelationsFor(book);
                insertGenres(book, book.getId());
            }
        } else {
            insertGenres(book, book.getId());
        }
        int[] updateCounts = updateBook(book);

        boolean noRowsAffected = Arrays.stream(updateCounts).allMatch(count -> count == 0);

        if (noRowsAffected) { // || noGenreUpdate) {
            throw new EntityNotFoundException("no updated rows");
        }
        return book;
    }

    private int[] updateBook(Book book) {
        String updateAutorInBook = "update books set title=?, author_id=? where id=?";

        int[] rez = jdbc.batchUpdate(updateAutorInBook,
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, book.getTitle());
                        ps.setLong(2, book.getAuthor().getId());
                        ps.setLong(3, book.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return 1;
                    }
                });


        return rez;
    }

    private long batchInsertGenresRelationsFor(Book book) {
        long authorId = insertAutorAndGetId(book);
        long bookId = insertBookAndGetId(book, authorId);
        insertGenres(book, bookId);
        return bookId;
    }

    private void insertGenres(Book book, long bookId) {
        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            List<Genre> genreList = new ArrayList<>(book.getGenres());
            List<Genre> copiedgenreList = new ArrayList<>(genreList);
            genreList.removeIf(genreRepository.findAll()::contains);
            if (genreList.isEmpty()) {
                insetBooksGenres(bookId, copiedgenreList);
                return;
            }
            List<Object[]> genresParams = new ArrayList<>();
            for (Genre genre : genreList) {
                Object[] params = new Object[]{genre.getName()};
                genresParams.add(params);
            }
            jdbc.batchUpdate("insert into genres (name) values (?)", genresParams);
            insetBooksGenres(bookId, copiedgenreList);
        }
    }

    private void insetBooksGenres(long bookId, List<Genre> genreList) {
        List<Object[]> booksGenresParams = new ArrayList<>();
        for (long idGanre : genreList.stream().map(Genre::getId).toList()) {
            Object[] params = new Object[]{bookId, idGanre};
            booksGenresParams.add(params);
        }
        jdbc.batchUpdate("insert into books_genres (book_id, genre_id) values (?, ?)", booksGenresParams);
    }

    private long insertBookAndGetId(Book book, long authorId) {
        KeyHolder keyHolderBook = new GeneratedKeyHolder();
        int rezUpdate = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into books (title, author_id) values (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setLong(2, authorId);
            return ps;
        }, keyHolderBook);
        return keyHolderBook.getKey().longValue();
    }

    private long insertAutorAndGetId(Book book) {
        KeyHolder keyHolderAuthor = new GeneratedKeyHolder();
        return jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into authors (full_name) values (?)");
            ps.setString(1, book.getAuthor().getFullName());
            return ps;
        }, keyHolderAuthor);
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

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                long bookId = rs.getLong("id");
                String title = rs.getString("title");
                long authorId = rs.getLong("author_id");
                String authorName = rs.getString("full_name");
                Author author = new Author(authorId, authorName);
                var genresIdSet = genreRepository.findAllByBookId(bookId);
                var genres = genreRepository.findAllByIds(genresIdSet);
                Book book = new Book(bookId, title, author, genres);
                return book;
            } else {
                return null;
            }
        }

        private Author getAuthorById(long authorId) {
            String sql = "select id, full_name from authors where id = :authorid";

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("authorid", authorId);

            return namedParameterJdbcOperations.queryForObject(sql, parameters, (rs, rowNum) -> {
                long id = rs.getLong("id");
                String fullName = rs.getString("full_name");

                return new Author(id, fullName);
            });
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
