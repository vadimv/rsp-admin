package rsp.admin.crud.services;

import rsp.admin.data.KeyedEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GetOne<K, T> {
    CompletableFuture<Optional<KeyedEntity<K, T>>> getOne(K key);
}
