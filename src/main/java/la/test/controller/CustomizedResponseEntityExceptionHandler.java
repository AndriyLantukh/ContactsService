package la.test.controller;

import la.test.exception.ContactsServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger EXCEPTION_LOGGER = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

    @ExceptionHandler(ContactsServiceException.class)
    public final ResponseEntity<String> handleContactsServiceException(ContactsServiceException ex, WebRequest request) {
        EXCEPTION_LOGGER.error(ex.getMessage(), ex);

        HttpStatus httpStatus = ex.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR : ex.getHttpStatus();

        return new ResponseEntity<>(ex.getMessage(), httpStatus);
    }
}
