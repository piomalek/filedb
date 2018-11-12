package ch.pmalek.filedb.web.handler;

import ch.pmalek.filedb.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DbControllerHandler {

    //TODO - imlpement handler for each scenario (table does not exist, id is invalid, unexpected error ...)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleCustomException(Exception e) {
        log.error("HANDLED: Runtime Exception thrown: {}", e);
        return Result.builder().statusCode("ERR").message(e.getMessage()).build();
    }
}
