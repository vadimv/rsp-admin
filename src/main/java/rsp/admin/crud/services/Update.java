package rsp.admin.crud.services;

import rsp.admin.crud.entities.KeyedEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Update<K, T> {
    CompletableFuture<Optional<KeyedEntity<K, T>>> update(KeyedEntity<K, T> updatedKeyedEntity);
}
