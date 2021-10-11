package rsp.admin.crud.components;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class DetailsViewState<T> {
    public final Optional<T> currentValue;
    public final Optional<String> currentKey;
    public final Map<String, String> validationErrors;
    public DetailsViewState(Optional<T> value,
                            Optional<String> key,
                            Map<String, String> validationErrors) {
        this.currentValue = value;
        this.currentKey = key;
        this.validationErrors = validationErrors;
    }
    public DetailsViewState(Optional<T> value, Optional<String> key) {
        this(value, key, Collections.emptyMap());
    }

    public DetailsViewState() {
        this(Optional.empty(), Optional.empty(), Collections.emptyMap());
    }

    public rsp.admin.crud.components.DetailsViewState<T> show() {
        return new rsp.admin.crud.components.DetailsViewState<>(this.currentValue, this.currentKey, this.validationErrors);
    }

    public rsp.admin.crud.components.DetailsViewState<T> withValue(T value) {
        return new rsp.admin.crud.components.DetailsViewState<T>(Optional.of(value), this.currentKey, this.validationErrors);
    }

    public rsp.admin.crud.components.DetailsViewState<T> withValidationErrors(Map<String, String> validationErrors) {
        return new rsp.admin.crud.components.DetailsViewState<T>(this.currentValue, this.currentKey, validationErrors);
    }
}