package rsp.admin.components.list;

import rsp.Component;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import static rsp.html.HtmlDsl.a;
import static rsp.html.HtmlDsl.on;

public final class EditButton implements Component<String> {

    public EditButton() {
    }

    @Override
    public DocumentPartDefinition render(UseState<String> useState) {
        return a("#", "Edit", on("click", ctx -> useState.accept(useState.get())));
    }

}
