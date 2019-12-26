package com.game.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String gameNotFoundHandler(final GameNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalMoveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalMoveHandler(final IllegalMoveException e) {
        return e.getMessage();
    }
}
