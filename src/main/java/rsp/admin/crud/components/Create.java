package rsp.admin.crud.components;

import rsp.Component;
import rsp.dsl.DocumentPartDefinition;
import rsp.admin.crud.components.DetailsViewState;
import rsp.admin.crud.components.Form;
import rsp.state.UseState;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import static rsp.dsl.Html.div;
import static rsp.dsl.Html.span;
import static rsp.state.UseState.readWrite;

public class Create<T> implements Component<DetailsViewState<T>> {
    private final Function<Consumer<T>, Form> formFunction;

    public Create(Function<Consumer<T>, Form> formFunction) {
        this.formFunction = formFunction;
    }

    @Override
    public DocumentPartDefinition render(UseState<DetailsViewState<T>> us) {
        return div(span("Create"),
                   formFunction.apply(readWrite(() -> us.get().currentValue.get(),
                                            v -> us.accept(us.get().withValue(v).withValidationErrors(Collections.EMPTY_MAP))))
                                                   .render(readWrite(() -> new Form.State(us.get().validationErrors),
                                                                     v -> us.accept(us.get().withValidationErrors(v.validationErrors)))));
    }
}
