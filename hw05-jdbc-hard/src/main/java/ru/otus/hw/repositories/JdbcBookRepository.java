package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
        String sql = "select id, title, author_id from books where id = :id";

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
        //...
        String sql = "delete from books where id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        jdbc.update(sql, parameters);
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
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        // Для каждой связи ищем соответствующий жанр и добавляем его в мапу
        Map<Long, List<Genre>> bookIdToGenresMap = new HashMap<>();

        // Построение мапы ID книги к списку жанров
        for (BookGenreRelation relation : relations) {
            long bookId = relation.bookId();
            long genreId = relation.genreId();
            Genre genre = genreRepository.findAllByIds(Set.of(genreId)).stream().findFirst().orElse(null);
            if (genre != null) {
                // Создаем список, если это первый жанр для книги
                bookIdToGenresMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(genre);
            }
        }

        // Добавление жанров к каждой книге
        for (Book book : booksWithoutGenres) {
            long bookId = book.getId();
            List<Genre> bookGenres = bookIdToGenresMap.getOrDefault(bookId, new ArrayList<>());
            book.setGenres(bookGenres);
        }

    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        //...
        //noinspection DataFlowIssue
        if (keyHolder.getKey() != null) {
            book.setId(keyHolder.getKeyAs(Long.class));
        }
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        //...

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
        // Вам потребуется определить SQL запросы для каждой из таблиц
        long authorId = insertAutorAndGetId(book);

        long bookId = insertBookAndGetId(book, authorId);

        insertGenres(book, bookId);
    }

    private void insertGenres(Book book, long bookId) {
        // Вставка жанров, если они есть
        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            List<Object[]> genresParams = new ArrayList<>();
            for (Genre genre : book.getGenres()) {
                Object[] params = new Object[]{genre.getName()};
                genresParams.add(params);
            }
//            int[] genresIds = jdbc.batchUpdate("insert into genres (name) values (?) on duplicate key update" +
//                    " id=last_insert_id(id), name=values(name)", genresParams);
            int[] genresIds = jdbc.batchUpdate("insert into genres (name) values (?)", genresParams);

            // Каждая пара книга-жанр становится записью в books_genres
            List<Object[]> booksGenresParams = new ArrayList<>();
            int i = 0;
            for (int genreId : genresIds) {
                Object[] params = new Object[]{bookId, genresIds[i++]};
                booksGenresParams.add(params);
            }
            jdbc.batchUpdate("insert into books_genres (book_id, genre_id) values (?, ?)", booksGenresParams);
        }
    }

    private long insertBookAndGetId(Book book, long authorId) {
        // Вставка записи книги
        KeyHolder keyHolderBook = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into books (title, author_id) values (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setLong(2, authorId);
            return ps;
        }, keyHolderBook);
        long bookId = keyHolderBook.getKey().longValue(); // Получаем сгенерированный идентификатор книги
        return bookId;
    }

    private long insertAutorAndGetId(Book book) {
        // Вставка записи об авторе, если это необходимо (запрос с предположением MySQL и поддержкой ON DUPLICATE KEY)
        KeyHolder keyHolderAuthor = new GeneratedKeyHolder();
        return  jdbc.update(connection -> {
//            PreparedStatement ps = connection.prepareStatement("insert into authors (full_name) values (?)" +
//                    " on duplicate key update id=last_insert_id(id), full_name=values(full_name)", new String[]{"id"});
            PreparedStatement ps = connection.prepareStatement("insert into authors (full_name) values (?)");
            ps.setString(1, book.getAuthor().getFullName());
            return ps;
        }, keyHolderAuthor);
//        long authorId = keyHolderAuthor.getKey().longValue(); // Получаем сгенерированный идентификатор автора
//        return authorId;
    }

    private void removeGenresRelationsFor(Book book) {
        //...
        String sql = "delete from books_genres b " +
                "where b.book_id = bookid";

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

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                long bookId = rs.getLong("id");
                String title = rs.getString("title");
                long authorId = rs.getLong("author_id");

                Author author = getAuthorById(authorId);
                Book book = new Book(bookId, title, author, Collections.emptyList());
                var relations = getAllGenreRelations();
                var genres = genreRepository.findAll();
                List<Book> bookList = List.of(book);
                mergeBooksInfo(List.of(book), genres, relations);
                return bookList.get(0);
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
