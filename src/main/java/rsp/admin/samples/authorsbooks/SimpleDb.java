package rsp.admin.samples.authorsbooks;

import rsp.admin.data.provider.EntityService;
import rsp.admin.data.entity.KeyedEntity;
import rsp.admin.data.provider.GetListQuery;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SimpleDb {
    public AtomicLong authorsIdGenerator = new AtomicLong();
    public final Map<String, Author> authors = new HashMap<>();

    public AtomicLong booksIdGenerator =  new AtomicLong();
    public final Map<String, Book> books = new HashMap<>();

    public EntityService<String, Author> authorsService() {
        return new SimpleAuthorsEntityService();
    }

    public EntityService<String, Book> booksService() {
        return new SimpleBooksEntityService();
    }


    private class SimpleAuthorsEntityService implements EntityService<String, Author> {
        @Override
        public CompletableFuture<KeyedEntity<String, Author>> getOne(String key) {
            final Author a = authors.get(key);
            return CompletableFuture.completedFuture(new KeyedEntity<>(key, a));
        }

        @Override
        public CompletableFuture<List<KeyedEntity<String, Author>>> getList(GetListQuery<String> query) {
            return CompletableFuture.completedFuture(new ArrayList<>(authors.entrySet().stream().map(e ->
                                                     new KeyedEntity<>(e.getKey(), e.getValue())).collect(Collectors.toList())));
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Author>> create(Author entity) {
            final String key = Long.toString(authorsIdGenerator.incrementAndGet());
            var ke = new KeyedEntity<>(key, entity);
            for (var book : entity.books) {
                booksService().getOne(book.key).thenAccept(bke -> bke.update(bke.data.addAuthor(ke)));
            }

            authors.put(key, entity);
            return CompletableFuture.completedFuture(ke);
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Author>> delete(String key) {
            final Author a = authors.remove(key);
            if (a == null) {
                return CompletableFuture.failedFuture( new RuntimeException());
            } else {
                return CompletableFuture.completedFuture(new KeyedEntity<>(key,a));
            }
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Author>> update(KeyedEntity<String, Author> updatedKeyedEntity) {
            final Author a = authors.get(updatedKeyedEntity.key);
            if (a == null) {
                return CompletableFuture.failedFuture(new RuntimeException("Not found"));
            } else {
                authors.put(updatedKeyedEntity.key, updatedKeyedEntity.data);
                return CompletableFuture.completedFuture(updatedKeyedEntity);
            }
        }


    }

    public class SimpleBooksEntityService implements EntityService<String, Book> {
        @Override
        public CompletableFuture<KeyedEntity<String, Book>> getOne(String key) {
            final Book a = books.get(key);
            return CompletableFuture.completedFuture(new KeyedEntity<>(key, a));
        }

        @Override
        public CompletableFuture<List<KeyedEntity<String, Book>>> getList(GetListQuery<String> query) {
            return CompletableFuture.completedFuture(new ArrayList<>(books.entrySet().stream().map(e ->
                    new KeyedEntity<>(e.getKey(), e.getValue())).collect(Collectors.toList())));
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Book>> create(Book entity) {
            String key = Long.toString(booksIdGenerator.incrementAndGet());
            var ke = new KeyedEntity<>(key, entity);
            for (var author : entity.authors) {
                authorsService().getOne(author.key).thenAccept(bke -> authorsService().update(bke.update(bke.data.addBook(ke))));

            }

            books.put(key, entity);
            return CompletableFuture.completedFuture(ke);
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Book>> delete(String key) {
            final Book a = books.remove(key);
            if (a == null) {
                return CompletableFuture.failedFuture(new RuntimeException("Not found"));
            } else {
                return CompletableFuture.completedFuture(new KeyedEntity<>(key,a));
            }
        }

        @Override
        public CompletableFuture<KeyedEntity<String, Book>> update(KeyedEntity<String, Book> updatedKeyedEntity) {
            final Book a = books.get(updatedKeyedEntity.key);
            if (a == null) {
                return CompletableFuture.failedFuture(new RuntimeException("Not found"));
            } else {
                books.put(updatedKeyedEntity.key, updatedKeyedEntity.data);
                return CompletableFuture.completedFuture(updatedKeyedEntity);
            }
        }
    }
}
