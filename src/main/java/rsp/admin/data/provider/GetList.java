package rsp.admin.data.provider;

import rsp.admin.data.entity.KeyedEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetList<K, T> {
    CompletableFuture<List<KeyedEntity<K, T>>> getList(GetListQuery<K> query);
}
