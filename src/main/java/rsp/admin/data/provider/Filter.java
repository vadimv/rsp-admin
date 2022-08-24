package rsp.admin.data.provider;

public sealed interface Filter<K> {
    record ById<K>(K id) implements Filter {}

    record ByIds<K>(K[] ids) implements Filter {}

    record ByFieldString<K>(String filter, String fieldName) implements Filter {}
}




