package rsp.admin.data.provider;

import java.util.Objects;
import java.util.Optional;

public final class GetListQuery<K> {

    public final Optional<Pagination> pagination;
    public final Optional<Sort> sort;
    public final Optional<Filter<K>> filter;
    public GetListQuery(Optional<Pagination> pagination,
                        Optional<Sort> sort,
                        Optional<Filter<K>> filter) {

        this.pagination = Objects.requireNonNull(pagination);
        this.sort = Objects.requireNonNull(sort);
        this.filter = Objects.requireNonNull(filter);
    }

    private GetListQuery() {
        this.pagination = Optional.empty();
        this.sort = Optional.empty();
        this.filter = Optional.empty();
    }

    public static <K> GetListQuery<K> empty(Class<K> keyClass) {
        return new GetListQuery<>();
    }

    public GetListQuery<K> withPagination(Pagination pagination) {
        return new GetListQuery<K>(Optional.of(Objects.requireNonNull(pagination)), this.sort, this.filter);
    }

    public GetListQuery<K> withSort(Sort sort) {
        return new GetListQuery<>(this.pagination, Optional.of(Objects.requireNonNull(sort)), this.filter);
    }

    public GetListQuery<K> withFilter(Filter<K> filter) {
        return new GetListQuery<>(this.pagination, this.sort, Optional.of(Objects.requireNonNull(filter)));
    }
}
