package rsp.admin.crud.entities;

import rsp.admin.data.KeyedEntity;

import java.util.HashSet;
import java.util.Set;

public class Author {
    public final Name name;
    public final Set<KeyedEntity<String, Book>> books;

    public Author(Name name, Set<KeyedEntity<String, Book>> books) {
        this.name = name;
        this.books = books;
    }

    public Author(Name name) {
        this(name, Set.of());
    }

    public static rsp.admin.crud.entities.Author of(String name) {
        return new rsp.admin.crud.entities.Author(Name.of(name));
    }
    @Override
    public String toString() {
        return name.toString();
    }

    public rsp.admin.crud.entities.Author addBook(KeyedEntity<String, Book> book) {
        final var b = new HashSet<>(books);
        b.add(book);
        return new rsp.admin.crud.entities.Author(name, b);
    }

}
