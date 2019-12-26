package com.game.kalah.exception;

public class IllegalMoveException extends RuntimeException {

    private static final long serialVersionUID = 36706300044610524L;

    public IllegalMoveException(final String message) {
        super(message);
    }
}
