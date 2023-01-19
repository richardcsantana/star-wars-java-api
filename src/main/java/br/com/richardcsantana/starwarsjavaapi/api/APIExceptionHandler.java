package br.com.richardcsantana.starwarsjavaapi.api;

import br.com.richardcsantana.starwarsjavaapi.api.errors.ErrorResponse;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiNotFoundException;
import br.com.richardcsantana.starwarsjavaapi.common.application.errors.ResourceNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class APIExceptionHandler {

    Logger logger = Logger.getLogger(APIExceptionHandler.class.getName());

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(value = {ResourceNotFoundException.class, SwapiNotFoundException.class})
    public Mono<ErrorResponse> handleResourceNotFound(Throwable e) {
        return Mono.just(new ErrorResponse(e.getMessage()));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleException(Exception e) {
        logger.log(java.util.logging.Level.SEVERE, e.getMessage(), e);
        return Mono.just(new ErrorResponse("Internal server error"));
    }
}
