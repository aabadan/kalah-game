package com.game.kalah.repository;

import com.game.kalah.exception.GameNotFoundException;
import com.game.kalah.model.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameRepository {
    private final Map<String, Game> repository = new HashMap<>();

    public Game find(final String id) {
        final Game game = this.repository.get(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }

    public Game save(final Game game) {
        this.repository.put(game.getId(), game);
        return this.find(game.getId());
    }
}
