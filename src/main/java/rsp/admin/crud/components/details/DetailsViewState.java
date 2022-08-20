package rsp.admin.crud.components.details;

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

    public DetailsViewState<T> show() {
        return new DetailsViewState<>(this.currentValue, this.currentKey, this.validationErrors);
    }

    public DetailsViewState<T> withValue(T value) {
        return new DetailsViewState<>(Optional.of(value), this.currentKey, this.validationErrors);
    }

    public DetailsViewState<T> withValidationErrors(Map<String, String> validationErrors) {
        return new DetailsViewState<>(this.currentValue, this.currentKey, validationErrors);
    }
}