package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Genre update(String id, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        Update update = new Update();
        update.set("name", name);

        mongoTemplate.updateFirst(query, update, Genre.class);

        Update updateBooks = new Update();
        updateBooks.set("genre.name", name);
        mongoTemplate.updateMulti(new Query(Criteria.where("genre.id").is(id)), updateBooks, Book.class);

        return mongoTemplate.findOne(query, Genre.class);
    }
}
