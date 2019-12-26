package com.game.kalah.controller;

import com.game.kalah.model.Game;
import com.game.kalah.response.GameResource;
import com.game.kalah.response.GameResourceConverter;
import com.game.kalah.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final GameResourceConverter gameResourceConverter;

    @PostMapping
    public ResponseEntity<GameResource> createNewGame() {
        final Game game = gameService.createGame();
        final GameResource gameResource = gameResourceConverter.convert(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(gameResource);
    }

    @PutMapping("/{gameId}/move/{pitId}")
    public ResponseEntity<GameResource> makeMove(@PathVariable final String gameId,
                                                 @PathVariable final int pitId) {
        final Game game = gameService.makeMove(gameId, pitId);
        final GameResource gameResource = gameResourceConverter.convert(game);
        return ResponseEntity.status(HttpStatus.OK).body(gameResource);
    }
}
