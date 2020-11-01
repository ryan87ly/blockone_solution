package ry.an.controller;

import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class InputValidations {
    public static void require(boolean condition, Supplier<String> errorMessageGenerator) {
        if (!condition) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessageGenerator.get());
        }
    }

    public static void requireNotNullOrEmpty(String value, Supplier<String> errorMessageGenerator) {
        require(!Strings.isNullOrEmpty(value), errorMessageGenerator);
    }

    public static <T> void requireNotNullOrEmpty(Collection<T> value, Supplier<String> errorMessageGenerator) {
        require(Objects.nonNull(value) && !value.isEmpty(), errorMessageGenerator);
    }

    public static <T> T requirePresent(Optional<T> opt, Supplier<String> errorMessageGenerator) {
        require(opt.isPresent(), errorMessageGenerator);
        return opt.get();
    }
}
