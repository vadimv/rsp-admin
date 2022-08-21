package rsp.admin.crud.components.details;

import rsp.Component;
import rsp.admin.crud.components.text.TextInput;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;
import rsp.util.data.Tuple2;
import rsp.util.json.JsonDataType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static rsp.html.HtmlDsl.*;

public final class Form implements Component<Form.State> {

    private final Consumer<Function<String, Optional<String>>> submittedData;
    private final List<TextInput> fieldsComponents;

    public Form(Consumer<Function<String, Optional<String>>> submittedData, TextInput... fieldsComponents) {
        this.submittedData = submittedData;
        this.fieldsComponents = Arrays.asList(fieldsComponents);
    }


    @Override
    public DocumentPartDefinition render(UseState<Form.State> useState) {
        return
            div(form(on("submit", c -> {
                        // Validate all fieldComponents, if any is invalid update state with validation messages, if all valid accept the values
                        final Map<String, String> topValidationErrors =
                                fieldsComponents.stream().map(component ->
                                    new Tuple2<>(component.fieldName,
                                                 c.eventObject().value(component.fieldName).stream().flatMap(value ->
                                                                       component.validations.stream().flatMap(validation ->
                                                                              validation.apply(value.toString()).stream())).collect(Collectors.toList())))
                                        .filter(t -> t._2.size() > 0)
                                        .collect(Collectors.toMap(t -> t._1, t -> t._2.get(0)));

                            if (topValidationErrors.size() > 0) {
                                useState.accept(new Form.State(topValidationErrors));
                            } else {
                                submittedData.accept(name -> c.eventObject().value(name).map(v -> {
                                    assert v instanceof JsonDataType.String;
                                    final var o = (JsonDataType.String) v;
                                    return o.value();
                                }));
                            }
                        }),
                        of(fieldsComponents.stream().map(component ->
                                div(component.render(Optional.ofNullable(useState.get().validationErrors.get(component.fieldName)))))),
                        button(attr("type", "submit"), text("Ok")),
                        button(attr("type", "button"),
                                on("click", ctx -> useState.accept(new State(Collections.emptyMap()))),
                                text("Cancel"))));
    }


    public static class State {
        public final Map<String, String> validationErrors;

        public State(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
        }


    /*    public State() {
            validationErrors = Collections.EMPTY_MAP;
        }*/

    }
}
