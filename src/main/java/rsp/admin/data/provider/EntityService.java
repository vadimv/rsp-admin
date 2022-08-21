package rsp.admin.data.provider;

public interface EntityService<K, T> extends GetOne<K, T>, GetList<K, T>, Create<K, T>, Delete<K, T>, Update<K, T> {
}
