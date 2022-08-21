package rsp.admin.data.provider;

import rsp.admin.data.entity.KeyedEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GetOne<K, T> {
    CompletableFuture<Optional<KeyedEntity<K, T>>> getOne(K key);
}
