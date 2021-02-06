package rsp.admin.crud.entities;

import rsp.admin.crud.entities.Author;
import rsp.admin.crud.entities.KeyedEntity;

import java.util.HashSet;
import java.util.Set;

public class Book {
    public final String title;
    public final String description;
    public final Set<KeyedEntity<String, Author>> authors;

    public Book(String title, String description, Set<KeyedEntity<String, Author>>  authors) {
        this.title = title;
        this.description = description;
        this.authors = authors;
    }

    public rsp.admin.crud.entities.Book addAuthor(KeyedEntity<String, Author> author) {
        final var a = new HashSet<>(authors);
        a.add(author);
        return new rsp.admin.crud.entities.Book(title, description, a);
    }
}
