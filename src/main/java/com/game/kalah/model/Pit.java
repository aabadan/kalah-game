package com.game.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Pit {
    private final int id;
    private final boolean isKalaha;
    private final Player ownerPlayer;
    private int numberOfStones;
}
