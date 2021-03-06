package rsp.admin.crud.services;

import rsp.admin.crud.entities.KeyedEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetList<K, T> {
    CompletableFuture<List<KeyedEntity<K, T>>> getList(int offset, int limit);
}
