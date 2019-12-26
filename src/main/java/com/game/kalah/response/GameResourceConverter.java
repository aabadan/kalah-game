package com.game.kalah.response;

import com.game.kalah.model.Game;
import com.game.kalah.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class GameResourceConverter {

    private final Environment environment;

    public GameResource convert(final Game game) {
        return GameResource.builder().id(String.valueOf(game.getId())).url(getUrl(game.getId()))
                .board(resolveStatus(game)).message(resolveMessage(game)).build();
    }

    private String getUrl(final String gameId) {
        int port = environment.getProperty("server.port", Integer.class, 8080);
        return String.format("http://%s:%s/game/%s", InetAddress.getLoopbackAddress().getHostName(), port, gameId);
    }

    private Map<Integer, String> resolveStatus(final Game game) {
        final Map<Integer, String> statusMap = new HashMap<>();
        game.getPits().forEach(pit -> statusMap.put(pit.getId(), String.valueOf(pit.getNumberOfStones())));
        return new TreeMap<>(statusMap);
    }

    private String resolveMessage(final Game game) {
        if (game.getWinnerPlayer() == Player.PLAYER_1) {
            return "Player 1 WON !!!";
        } else if (game.getWinnerPlayer() == Player.PLAYER_2) {
            return "Player 2 WON !!!";
        } else {
            return null;
        }
    }
}
