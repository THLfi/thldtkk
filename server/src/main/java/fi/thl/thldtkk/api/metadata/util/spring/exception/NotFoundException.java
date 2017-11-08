package fi.thl.thldtkk.api.metadata.util.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  public NotFoundException() {
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Class<?> clazz, UUID id) {
    super(clazz.getSimpleName() + " '" + id + "'");
  }

}
