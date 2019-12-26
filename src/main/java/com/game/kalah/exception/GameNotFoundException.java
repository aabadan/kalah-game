package com.game.kalah.exception;


public class GameNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6188925110977079719L;

    public GameNotFoundException(final String id) {
        super("Game with id: " + id + " not found");
    }
}
