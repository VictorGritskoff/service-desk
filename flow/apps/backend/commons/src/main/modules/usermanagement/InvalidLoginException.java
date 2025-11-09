package io.flow.modules.usermanagement;

import io.flow.exceptions.UserException;

public class InvalidLoginException extends UserException {
    public InvalidLoginException() {
        super("Invalid login");
    }
}
