package fi.thl.thldtkk.api.metadata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
public class ControllerExceptionHandling {
    @ExceptionHandler({ HttpStatusCodeException.class })
    public ResponseEntity<String> handleApiExceptions(HttpStatusCodeException exception) {
        return new ResponseEntity<>(exception.getResponseBodyAsString(), exception.getStatusCode());
    }
}
