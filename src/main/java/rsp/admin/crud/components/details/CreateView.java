package rsp.admin.crud.components.details;

import rsp.Component;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import static rsp.html.HtmlDsl.div;
import static rsp.html.HtmlDsl.span;
import static rsp.state.UseState.writeOnly;

public final class CreateView<T> implements Component<DetailsViewState<T>> {

    private final Function<Consumer<T>, Form> formFunction;

    public CreateView(Function<Consumer<T>, Form> formFunction) {
        this.formFunction = formFunction;
    }

    @Override
    public DocumentPartDefinition render(UseState<DetailsViewState<T>> us) {
        return div(span("Create"),
                   formFunction.apply(writeOnly(v -> us.accept(us.get().withValue(v).withValidationErrors(Collections.emptyMap()))))
                                                   .render(new Form.State(us.get().validationErrors),
                                                           v -> us.accept(us.get().withValidationErrors(v.validationErrors))));
    }
}
