package uk.co.huntersix.spring.rest.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PersonAlreadyExistException extends RuntimeException {

    public PersonAlreadyExistException(String message) {
        super(message);
    }

    public PersonAlreadyExistException() {
        super();
    }
}
