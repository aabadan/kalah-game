package com.game.kalah.service;

import com.game.kalah.exception.IllegalMoveException;
import com.game.kalah.model.Game;
import com.game.kalah.model.Pit;
import com.game.kalah.model.Player;
import com.game.kalah.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameService {

    private final static int NUMBER_OF_PITS = 6;
    public final static int TOTAL_NUMBER_OF_PITS = 2 * (NUMBER_OF_PITS + 1);
    private final static int INITIAL_STONES_COUNT_IN_PIT = 6;

    private final GameRepository gameRepository;

    public Game createGame() {
        final Game game = Game.builder().id(UUID.randomUUID().toString()).pits(createAndFillPits()).playerOnTurn(Player.PLAYER_1).build();
        return gameRepository.save(game);
    }

    private List<Pit> createAndFillPits() {
        List<Pit> pits = new ArrayList<>();
        Arrays.stream(Player.values()).forEach(player -> distributeStones(player, pits));
        return pits;
    }

    private void distributeStones(final Player player, final List<Pit> pits) {
        int startIndex = pits.size() + 1;
        IntStream.range(startIndex, startIndex + NUMBER_OF_PITS)
                .forEach(idx ->
                        pits.add(Pit.builder().id(idx).ownerPlayer(player).numberOfStones(INITIAL_STONES_COUNT_IN_PIT).isKalaha(false).build())
                );
        pits.add(Pit.builder().id(startIndex + NUMBER_OF_PITS).ownerPlayer(player).numberOfStones(0).isKalaha(true).build());
    }

    public Game makeMove(final String gameId, final int pitId) {
        Game game = gameRepository.find(gameId);
        validateStoneMove(game, pitId);
        collectAndSowStones(game, pitId);
        checkGameStatus(game);
        return gameRepository.save(game);
    }

    private void validateStoneMove(final Game game, final int pitId) {
        if (pitId < 0 || pitId > TOTAL_NUMBER_OF_PITS) {
            throw new IllegalMoveException("Invalid pit id!!!");
        }
        final Pit selectedPit = getPit(game, pitId);
        if (game.getWinnerPlayer() != null) {
            throw new IllegalMoveException("Game is already over!!!");
        }
        if (selectedPit.getOwnerPlayer() == Player.PLAYER_2 && game.getPlayerOnTurn() == Player.PLAYER_1) {
            throw new IllegalMoveException("Player 1's turn!!!");
        }
        if (selectedPit.getOwnerPlayer() == Player.PLAYER_1 && game.getPlayerOnTurn() == Player.PLAYER_2) {
            throw new IllegalMoveException("Player 2's turn!!!");
        }
        if (selectedPit.getNumberOfStones() == 0 && !selectedPit.isKalaha()) {
            throw new IllegalMoveException("Sowing can not start from empty pit!!!");
        }
        if (selectedPit.isKalaha()) {
            throw new IllegalMoveException("Sowing can not start from kalaha pit!!!");
        }
    }

    private void collectAndSowStones(final Game game, final int pitId) {
        int numberOfStonesTaken = collectStones(game, pitId);
        int nextPositionIndex = pitId;
        Pit nextPit = null;
        while (numberOfStonesTaken > 0) {
            nextPit = game.getPits().get(nextPositionIndex);
            if (pitIsNotOpponentsKalaha(game, nextPit.getId())) {
                nextPit.setNumberOfStones(nextPit.getNumberOfStones() + 1);
                numberOfStonesTaken--;
            }
            nextPositionIndex = (nextPositionIndex + 1) % TOTAL_NUMBER_OF_PITS;
        }
        checkIfSowingEndsOnEmptyPit(game, nextPit.getId());
        updateTurn(game, nextPit.getId());
    }

    private void updateTurn(final Game game, final int pitId) {
        if (!checkIfSowingEndsOnOwnKalaha(game, pitId)) {
            if (game.getPlayerOnTurn() == Player.PLAYER_1) {
                game.setPlayerOnTurn(Player.PLAYER_2);
            } else {
                game.setPlayerOnTurn(Player.PLAYER_1);
            }
        }
    }

    private void checkGameStatus(final Game game) {
        final int player1StoneCount = game.getPits().stream().filter(pit -> pit.getOwnerPlayer() == Player.PLAYER_1).filter(pit -> !pit.isKalaha()).mapToInt(Pit::getNumberOfStones).sum();
        final int player2StoneCount = game.getPits().stream().filter(pit -> pit.getOwnerPlayer() == Player.PLAYER_2).filter(pit -> !pit.isKalaha()).mapToInt(Pit::getNumberOfStones).sum();
        if (player1StoneCount == 0 || player2StoneCount == 0) {
            Pit player1Kalaha = getPit(game, Player.PLAYER_1.getKalahPitId());
            Pit player2Kalaha = getPit(game, Player.PLAYER_2.getKalahPitId());
            player1Kalaha.setNumberOfStones(player1Kalaha.getNumberOfStones() + player1StoneCount);
            player2Kalaha.setNumberOfStones(player2Kalaha.getNumberOfStones() + player2StoneCount);
            resetPits(game);
            decideWinner(game);
        }
    }

    private void decideWinner(final Game game) {
        final int player1KalahaStoneCount = getPit(game, Player.PLAYER_1.getKalahPitId()).getNumberOfStones();
        final int player2KalahaStoneCount = getPit(game, Player.PLAYER_2.getKalahPitId()).getNumberOfStones();
        if (player1KalahaStoneCount > player2KalahaStoneCount) {
            game.setWinnerPlayer(Player.PLAYER_1);
        } else {
            game.setWinnerPlayer(Player.PLAYER_2);
        }
    }

    private void resetPits(final Game game) {
        game.getPits().stream().filter(pit -> !pit.isKalaha()).forEach(pit -> pit.setNumberOfStones(0));
    }

    private boolean pitIsNotOpponentsKalaha(final Game game, final int pitId) {
        Player opponent = game.getPlayerOnTurn() == Player.PLAYER_1 ? Player.PLAYER_2 : Player.PLAYER_1;
        return opponent.getKalahPitId() != pitId;
    }

    private int collectStones(final Game game, final int pitId) {
        Pit selectedPit = getPit(game, pitId);
        int numberOfStonesTaken = selectedPit.getNumberOfStones();
        selectedPit.setNumberOfStones(0);
        return numberOfStonesTaken;
    }

    private boolean checkIfSowingEndsOnOwnKalaha(final Game game, final int pitId) {
        final Pit endingPit = getPit(game, pitId);
        return endingPit.isKalaha() && endingPit.getOwnerPlayer() == game.getPlayerOnTurn();
    }

    private void checkIfSowingEndsOnEmptyPit(final Game game, final int pitId) {
        Pit endingPit = getPit(game, pitId);
        if (!endingPit.isKalaha() && endingPit.getNumberOfStones() == 1 && endingPit.getOwnerPlayer() == game.getPlayerOnTurn()) {
            Pit oppositePit = getPit(game, TOTAL_NUMBER_OF_PITS - endingPit.getId());
            Pit playerKalaha = getPit(game, game.getPlayerOnTurn().getKalahPitId());
            playerKalaha.setNumberOfStones(playerKalaha.getNumberOfStones() + endingPit.getNumberOfStones() + oppositePit.getNumberOfStones());
            endingPit.setNumberOfStones(0);
            oppositePit.setNumberOfStones(0);
        }
    }

    private Pit getPit(final Game game, final int pitId) {
        return game.getPits().stream().filter(pit -> pit.getId() == pitId).findFirst().get();
    }
}
