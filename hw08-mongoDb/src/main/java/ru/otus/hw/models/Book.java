package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "books")
@Data
@ToString(exclude = {"author", "genres"})
@EqualsAndHashCode(exclude = {"author", "genres"})
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private String id;

    @Field("title")
    private String title;

    private Author author;

    private List<Genre> genres;
}

