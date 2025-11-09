package io.flow.modules.usermanagement;

import io.flow.exceptions.UserException;

public class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
        this("Incorrect password");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
