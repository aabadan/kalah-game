package com.game.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.game.kalah.service.GameService.TOTAL_NUMBER_OF_PITS;

@Getter
@AllArgsConstructor
public enum Player {
    PLAYER_1(TOTAL_NUMBER_OF_PITS / 2),
    PLAYER_2(TOTAL_NUMBER_OF_PITS);

    private final int kalahPitId;
}
