package rsp.admin.crud.components;

import rsp.Component;
import rsp.dsl.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.Collections;
import java.util.function.Function;

import static rsp.dsl.Html.*;

public class Edit<T> implements Component<DetailsViewState<T>> {
    private final Function<UseState<T>, Form> formFunction;

    public Edit(Function<UseState<T>, Form> formFunction) {
        this.formFunction = formFunction;
    }

    @Override
    public DocumentPartDefinition render(UseState<DetailsViewState<T>> us) {
        return div(span("Edit"),
                   of(us.get().currentValue.map(currentValue -> formFunction.apply(UseState.readWrite(() -> currentValue,
                                                v -> us.accept(us.get().withValue(v).withValidationErrors(Collections.EMPTY_MAP))))
                                                       .render(new Form.State(us.get().validationErrors),
                                                               v -> us.accept(us.get().withValidationErrors(v.validationErrors)))).stream()));
    }
}
