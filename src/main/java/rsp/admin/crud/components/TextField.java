package rsp.admin.crud.components;

import rsp.Component;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.Objects;

import static rsp.html.HtmlDsl.text;

public class TextField<T> implements Component<String> {
    private final T data;

    public TextField(T data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public DocumentPartDefinition render(UseState<String> useState) {
        return text(data);
    }


}
