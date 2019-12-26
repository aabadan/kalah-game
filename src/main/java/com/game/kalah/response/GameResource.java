package com.game.kalah.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class GameResource {
    private String id;
    private String url;
    private Map<Integer, String> board;
    private String message;
}
