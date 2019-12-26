package com.game.kalah.service;

import com.game.kalah.exception.IllegalMoveException;
import com.game.kalah.model.Game;
import com.game.kalah.model.Pit;
import com.game.kalah.model.Player;
import com.game.kalah.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceUnitTest {

    private GameRepository gameRepository;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameRepository = new GameRepository();
        gameService = new GameService(gameRepository);
    }

    @Test
    void shouldCreateGame() {
        // when
        final Game expectedGame = gameService.createGame();

        // then
        assertNotNull(expectedGame.getId());
        assertEquals(expectedGame.getPlayerOnTurn(), Player.PLAYER_1);
        assertNull(expectedGame.getWinnerPlayer());
    }

    @Test
    void shouldFillPits() {
        // when
        final Game game = gameService.createGame();

        // then
        assertEquals(14, game.getPits().size());
        assertEquals(72, game.getPits().stream().mapToInt(Pit::getNumberOfStones).sum());
        assertEquals(Player.PLAYER_1, getPit(game, 7).getOwnerPlayer());
        assertEquals(Player.PLAYER_2, getPit(game, 14).getOwnerPlayer());
        assertTrue(getPit(game, 7).isKalaha());
        assertTrue(getPit(game, 14).isKalaha());
        assertTrue(game.getPits().stream().filter(pit ->
                !pit.isKalaha()).allMatch(pit -> pit.getNumberOfStones() == 6));
        assertTrue(game.getPits().stream().filter(Pit::isKalaha).allMatch(pit -> pit.getNumberOfStones() == 0));
    }

    @Test
    void shouldCollectAndSowStones() {
        // given
        final Game createdGame = gameService.createGame();

        // when
        final Game game = gameService.makeMove(createdGame.getId(), 5);

        // then
        assertEquals(0, getPit(game, 5).getNumberOfStones());
        assertEquals(7, getPit(game, 6).getNumberOfStones());
        assertEquals(1, getPit(game, 7).getNumberOfStones());
        assertEquals(7, getPit(game, 8).getNumberOfStones());
        assertEquals(7, getPit(game, 9).getNumberOfStones());
        assertEquals(7, getPit(game, 10).getNumberOfStones());
        assertEquals(7, getPit(game, 11).getNumberOfStones());
        assertEquals(6, getPit(game, 12).getNumberOfStones());
    }

    @Test
    void shouldUpdateTurnForPlayer1() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.setPlayerOnTurn(Player.PLAYER_2);

        // when
        final Game game = gameService.makeMove(createdGame.getId(), 11);

        // then
        assertEquals(Player.PLAYER_1, game.getPlayerOnTurn());
    }

    @Test
    void shouldUpdateTurnForPlayer2() {
        // given
        final Game createdGame = gameService.createGame();

        // when
        final Game game = gameService.makeMove(createdGame.getId(), 6);

        // then
        assertEquals(Player.PLAYER_2, game.getPlayerOnTurn());
    }

    @Test
    void shouldGetOpponentsStonesIfSowingEndsOnEmptyPit() {
        // given
        final Game game = gameService.createGame();
        getPit(game, 6).setNumberOfStones(8);
        getPit(game, 1).setNumberOfStones(0);
        getPit(game, 13).setNumberOfStones(11);
        getPit(game, 7).setNumberOfStones(3);

        // when
        gameService.makeMove(game.getId(), 6);

        // then
        assertEquals(0, getPit(game, 6).getNumberOfStones());
        assertEquals(0, getPit(game, 1).getNumberOfStones());
        assertEquals(0, getPit(game, 13).getNumberOfStones());
        assertEquals(17, getPit(game, 7).getNumberOfStones());
    }

    @Test
    void shouldThrowExceptionIfInvalidPitIdReceived() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.setWinnerPlayer(Player.PLAYER_1);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 22);
        });

        // then
        assertEquals("Invalid pit id!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfGameIsOver() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.setWinnerPlayer(Player.PLAYER_1);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 6);
        });

        // then
        assertEquals("Game is already over!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPlayer1sTurn() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.setPlayerOnTurn(Player.PLAYER_1);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 10);
        });

        // then
        assertEquals("Player 1's turn!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfPlayer2sTurn() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.setPlayerOnTurn(Player.PLAYER_2);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 3);
        });

        // then
        assertEquals("Player 2's turn!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfEmptyPitSelectedForMove() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.getPits().stream().filter(pit -> pit.getId() == 3).findFirst().get().setNumberOfStones(0);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 3);
        });

        // then
        assertEquals("Sowing can not start from empty pit!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfKalahaSelectedForMove() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.getPits().stream().filter(pit -> pit.getId() == 7).findFirst().get().setNumberOfStones(1);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 7);
        });

        // then
        assertEquals("Sowing can not start from kalaha pit!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldThrowExceptionIfKalahaPitSelectedForMove() {
        // given
        final Game createdGame = gameService.createGame();
        createdGame.getPits().stream().filter(pit -> pit.getId() == 3).findFirst().get().setNumberOfStones(0);

        // when
        IllegalMoveException illegalMoveException = assertThrows(IllegalMoveException.class, () -> {
            gameService.makeMove(createdGame.getId(), 3);
        });

        //then
        assertEquals("Sowing can not start from empty pit!!!", illegalMoveException.getMessage());
    }

    @Test
    void shouldDecideWinOfPlayer1() {
        // given
        final Game game = gameService.createGame();
        game.getPits().stream().filter(pit -> !pit.isKalaha()).forEach(pit -> pit.setNumberOfStones(0));
        getPit(game, Player.PLAYER_1.getKalahPitId()).setNumberOfStones(48);
        getPit(game, Player.PLAYER_2.getKalahPitId()).setNumberOfStones(23);
        getPit(game, 6).setNumberOfStones(1);

        // when
        final Game endGame = gameService.makeMove(game.getId(), 6);

        // then
        assertEquals(Player.PLAYER_1, endGame.getWinnerPlayer());
    }

    @Test
    void shouldDecideWinOfPlayer2() {
        // given
        final Game game = gameService.createGame();
        game.getPits().stream().filter(pit -> !pit.isKalaha()).forEach(pit -> pit.setNumberOfStones(0));
        getPit(game, Player.PLAYER_1.getKalahPitId()).setNumberOfStones(23);
        getPit(game, Player.PLAYER_2.getKalahPitId()).setNumberOfStones(48);
        getPit(game, 6).setNumberOfStones(1);

        // when
        final Game endGame = gameService.makeMove(game.getId(), 6);

        // then
        assertEquals(Player.PLAYER_2, endGame.getWinnerPlayer());
    }

    @Test
    void shouldResetPitsAndMoveToKalaha() {
        // given
        final Game game = gameService.createGame();
        game.getPits().stream().filter(pit -> !pit.isKalaha()).forEach(pit -> pit.setNumberOfStones(0));
        getPit(game, Player.PLAYER_1.getKalahPitId()).setNumberOfStones(23);
        getPit(game, Player.PLAYER_2.getKalahPitId()).setNumberOfStones(34);
        getPit(game, 6).setNumberOfStones(1);
        getPit(game, 8).setNumberOfStones(6);
        getPit(game, 10).setNumberOfStones(8);

        // when
        final Game endGame = gameService.makeMove(game.getId(), 6);

        // then
        assertEquals(0, getPit(game, 6).getNumberOfStones());
        assertEquals(0, getPit(game, 8).getNumberOfStones());
        assertEquals(0, getPit(game, 10).getNumberOfStones());
        assertEquals(24, getPit(game, Player.PLAYER_1.getKalahPitId()).getNumberOfStones());
        assertEquals(48, getPit(game, Player.PLAYER_2.getKalahPitId()).getNumberOfStones());
    }

    private Pit getPit(final Game game, final int pitId) {
        return game.getPits().stream().filter(pit -> pit.getId() == pitId).findFirst().get();
    }

}
