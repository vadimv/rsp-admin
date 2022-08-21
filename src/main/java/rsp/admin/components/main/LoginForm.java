package rsp.admin.components.main;

import rsp.Component;
import rsp.admin.components.text.TextInput;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.Optional;

import static rsp.html.HtmlDsl.*;

public class LoginForm implements Component<LoginForm.State> {

    @Override
    public DocumentPartDefinition render(UseState<State> us) {
        return div(attr("class", "login"),
                   form(on("submit",
                            c -> {
                                final String userName = c.eventObject().value("username")
                                                                       .map(v -> v.toString())
                                                                       .flatMap(s -> s.isBlank() ? Optional.empty() : Optional.of(s.trim()))
                                                                       .orElse("");
                                final String password = c.eventObject().value("password")
                                                                       .map(v -> v.toString())
                                                                       .flatMap(s -> s.isBlank() ? Optional.empty() : Optional.of(s.trim()))
                                                                       .orElse("");
                                us.accept(new State(userName,
                                                    userName.isEmpty() ? Optional.of("Required") : Optional.empty(),
                                                    password,
                                                    userName.isEmpty() ? Optional.of("Required") : Optional.empty(),
                                                    Optional.empty(),
                                                    Optional.of(c.sessionId().deviceId)));
                                }),
                new TextInput("username", TextInput.Type.TEXT, "Username","").render(us.get().userNameValidationError),
                new TextInput("password", TextInput.Type.PASSWORD, "Password", "").render(us.get().passwordVaildationError),
                button(attr("type", "submit"), text("Login")),
                of(us.get().loginValidationError.stream().map(lve -> span(lve)))));
    }

    public static class State {
        public final String userName;
        public final Optional<String> userNameValidationError;
        public final String password;
        public final Optional<String> passwordVaildationError;
        public final Optional<String> loginValidationError;
        public final Optional<String> deviceId;


        public State(String userName,
                     Optional<String> userNameValidationError,
                     String password,
                     Optional<String> passwordVaildationError,
                     Optional<String> loginValidationError,
                     Optional<String> deviceId) {
            this.userName = userName;
            this.userNameValidationError = userNameValidationError;
            this.password = password;
            this.passwordVaildationError = passwordVaildationError;
            this.loginValidationError = loginValidationError;
            this.deviceId = deviceId;
        }

        public State() {
            this("", Optional.empty(), "", Optional.empty(), Optional.empty(), Optional.empty());
        }
    }
}
