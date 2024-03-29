package rsp.admin.components.text;

import java.util.Optional;
import java.util.function.Function;


public final class RequiredValidation implements Function<String, Optional<String>> {

    @Override
    public Optional<String> apply(String s) {

        return s != null && s.length() > 0 ? Optional.empty() : Optional.of("Required value");
    }
}
