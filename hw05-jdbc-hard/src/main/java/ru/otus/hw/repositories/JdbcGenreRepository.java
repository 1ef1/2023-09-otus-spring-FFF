package ru.otus.hw.repositories;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class JdbcGenreRepository implements GenreRepository {
    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcGenreRepository(JdbcOperations jdbc, NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = jdbc;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query("select id, name from genres", new GnreRowMapper());
    }

    public Set<Long> findAllByBookId(Long bookId) {
        String sql = "select genre_id  from books_genres where book_id = :bookId";
        Map<String, Object> namedParameters = Collections.singletonMap("bookId", bookId);

        return  new HashSet<>(namedParameterJdbcOperations.query(sql, namedParameters,
                (rs, rowNum) -> rs.getLong("genre_id")));
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        String sql = "select id, name from genres where id in (:ids)";

        Map<String, Object> namedParameters = Collections.singletonMap("ids", ids);

        return namedParameterJdbcOperations.query(sql, namedParameters,new GnreRowMapper());
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
