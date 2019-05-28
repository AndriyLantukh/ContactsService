package la.test.exception;

import org.springframework.http.HttpStatus;

public class ContactsServiceException extends RuntimeException {

    private HttpStatus httpStatus;

    public ContactsServiceException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ContactsServiceException(String exceptionMessage, HttpStatus httpStatus) {
        super(exceptionMessage);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
