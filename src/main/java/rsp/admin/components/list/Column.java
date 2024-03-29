package rsp.admin.components.list;

import rsp.Component;

import java.util.function.Function;

public final class Column<T> {
    public final String title;
    public final Function<T, Component<String>> fieldComponent;

    public Column(String title, Function<T, Component<String>> fieldComponent) {
        this.title = title;
        this.fieldComponent = fieldComponent;
    }

    public Column(Function<T, Component<String>> fieldComponent) {
        this("", fieldComponent);
    }
}
