package org.example.lab7.validator;

import java.util.Optional;

public interface Validator<T> {
    void validate(Optional<T> entity) throws ValidationException;

}
