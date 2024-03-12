package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "books")
@Data
@ToString(exclude = {"author", "genre"})
@EqualsAndHashCode(exclude = {"author", "genre"})
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private String id;

    @Field("title")
    private String title;

    private Author author;

    private Genre genre;
}

