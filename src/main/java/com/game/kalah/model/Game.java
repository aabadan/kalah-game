package com.game.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Game {
    private final String id;
    private final List<Pit> pits;
    private Player winnerPlayer;
    private Player playerOnTurn;
}
