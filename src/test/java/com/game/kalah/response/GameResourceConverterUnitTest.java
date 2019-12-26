package com.game.kalah.response;

import com.game.kalah.model.Game;
import com.game.kalah.model.Pit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameResourceConverterUnitTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private GameResourceConverter gameResourceConverter;

    @Test
    void shouldConvertGame() {
        // given
        final Game game = Game.builder().id("44").pits(Arrays.asList(Pit.builder().id(1).numberOfStones(5).build(),
                Pit.builder().id(2).numberOfStones(1).build())).build();
        final String gameUrl = "http://localhost:1111/game/44";
        given(environment.getProperty(eq("server.port"), any(), any())).willReturn(1111);

        // when
        final GameResource gameResource = gameResourceConverter.convert(game);

        // then
        assertEquals(gameResource.getId(), game.getId());
        assertEquals(gameResource.getUrl(), gameUrl);
        assertEquals(gameResource.getBoard().toString(), "{1=5, 2=1}");
        assertNull(gameResource.getMessage());
    }
}