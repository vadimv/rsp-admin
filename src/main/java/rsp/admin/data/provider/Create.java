package rsp.admin.data.provider;

import rsp.admin.data.entity.KeyedEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Create<K, T> {
    CompletableFuture<KeyedEntity<K, T>> create(T entity);
}
