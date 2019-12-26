package com.game.kalah.controller;

import com.game.kalah.exception.IllegalMoveException;
import com.game.kalah.model.Game;
import com.game.kalah.model.Pit;
import com.game.kalah.model.Player;
import com.game.kalah.response.GameResource;
import com.game.kalah.response.GameResourceConverter;
import com.game.kalah.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerUnitTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameResourceConverter gameResourceConverter;

    @InjectMocks
    private GameController gameController;

    @Test
    void shouldCreateGame() {
        // given
        final Game game = Game.builder()
                .id("123")
                .pits(Collections.singletonList(Pit.builder().build()))
                .playerOnTurn(Player.PLAYER_2)
                .build();
        final GameResource givenGameResource = GameResource.builder().id("23").url("aUri").build();
        given(gameService.createGame()).willReturn(game);
        given(gameResourceConverter.convert(eq(game))).willReturn(givenGameResource);

        // when
        final ResponseEntity<GameResource> gameResource = gameController.createNewGame();

        // then
        assertEquals(gameResource.getStatusCode(), HttpStatus.CREATED);
        assertEquals(gameResource.getBody(), givenGameResource);
    }

    @Test
    void shouldMakeAMove() {
        // given
        final Game game = Game.builder()
                .id("123")
                .pits(Collections.singletonList(Pit.builder().build()))
                .playerOnTurn(Player.PLAYER_2)
                .build();
        final GameResource givenGameResource = GameResource.builder().id("23").url("aUri").build();
        given(gameService.makeMove(eq(game.getId()), eq(3))).willReturn(game);
        given(gameResourceConverter.convert(eq(game))).willReturn(givenGameResource);

        // when
        final ResponseEntity<GameResource> gameResource = gameController.makeMove(game.getId(), 3);

        // then
        assertEquals(gameResource.getStatusCode(), HttpStatus.OK);
        assertEquals(gameResource.getBody(), givenGameResource);
    }
}
