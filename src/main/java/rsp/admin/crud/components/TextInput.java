package rsp.admin.crud.components;

import rsp.Component;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static rsp.html.HtmlDsl.*;

public class TextInput implements Component<Optional<String>> {
    public enum Type {
        TEXT, PASSWORD
    }

    public final String fieldName;
    private final Type type;
    private final String labelText;
    private final String initialValue;
    public final List<Function<String, Optional<String>>> validations;

    @SafeVarargs
    public TextInput(String fieldName,
                     rsp.admin.crud.components.TextInput.Type type,
                     String labelText,
                     String initialValue,
                     Function<String, Optional<String>>... validations) {
        this.fieldName = fieldName;
        this.type = type;
        this.labelText = labelText;
        this.initialValue = initialValue;
        this.validations = Arrays.asList(validations);
    }

    @Override
    public DocumentPartDefinition render(UseState<Optional<String>> useState) {
        return div(label(attr("for", fieldName), text(labelText)),
                   input(attr("type", Type.PASSWORD == type ? "password" : "text"),
                         attr("name", fieldName),
                         prop("value", initialValue)),
                   of(useState.get().stream().map(validationErrorMessage -> span(style("color", "red"),
                                                                                 text(validationErrorMessage)))));
    }

}
