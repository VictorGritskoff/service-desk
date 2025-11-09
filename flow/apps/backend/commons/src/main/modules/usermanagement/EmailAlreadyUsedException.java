package io.flow.modules.usermanagement;

import io.flow.exceptions.UserException;

public class EmailAlreadyUsedException extends UserException {

    public EmailAlreadyUsedException() {
        super("Email is already in use!");
    }
}
