package io.flow.modules.usermanagement.controller.errors;

import io.flow.exceptions.ResourceConstraintException;
import io.flow.exceptions.ResourceNotFoundException;
import io.flow.exceptions.UserException;
import io.flow.modules.usermanagement.EmailAlreadyUsedException;
import io.flow.modules.usermanagement.InvalidLoginException;
import io.flow.modules.usermanagement.InvalidPasswordException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

/** Mapping between exception and response status code */
public class ExceptionStatusMapper {

    private static final Map<Class<? extends Throwable>, HttpStatus> exceptionToStatusMap =
            new HashMap<>();

    static {
        // Populate the map with exception-to-status mappings
        exceptionToStatusMap.put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(InvalidLoginException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(InvalidPasswordException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(EmailAlreadyUsedException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(UserException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(BadCredentialsException.class, HttpStatus.UNAUTHORIZED);
        exceptionToStatusMap.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
        exceptionToStatusMap.put(AuthenticationException.class, HttpStatus.UNAUTHORIZED);
        exceptionToStatusMap.put(ResponseStatusException.class, HttpStatus.NOT_FOUND);
        exceptionToStatusMap.put(ResourceNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionToStatusMap.put(IllegalStateException.class, HttpStatus.CONFLICT);
        exceptionToStatusMap.put(ResourceConstraintException.class, HttpStatus.CONFLICT);
    }

    public static HttpStatus getHttpStatus(Throwable ex) {
        return exceptionToStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
