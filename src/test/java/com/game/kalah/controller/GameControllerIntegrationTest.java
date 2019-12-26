package com.game.kalah.controller;

import com.game.kalah.model.Game;
import com.game.kalah.model.Player;
import com.game.kalah.response.GameResourceConverter;
import com.game.kalah.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameResourceConverter gameResourceConverter;

    @Test
    void shouldCreateGame() throws Exception {
        // when
        final ResultActions resultActions = mockMvc.perform(post("/game"));

        // then
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.id").isNotEmpty());
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.url").isNotEmpty());
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void shouldMakeAMove() throws Exception {
        // given
        final Game game = gameService.createGame();

        //when
        final ResultActions resultActions = mockMvc.perform(put("/game/" + game.getId() + "/move/" + 3));

        // then
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.1").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.2").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.3").value("0"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.4").value("7"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.5").value("7"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.6").value("7"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.7").value("1"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.8").value("7"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.9").value("7"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.10").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.11").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.12").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.13").value("6"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.board.14").value("0"));
        resultActions.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void shouldReturnBadRequestOnIllegalMove() throws Exception {
        // given
        final Game game = gameService.createGame();
        game.setPlayerOnTurn(Player.PLAYER_1);

        //when
        final ResultActions resultActions = mockMvc.perform(put("/game/" + game.getId() + "/move/" + 11));

        // then
        resultActions.andExpect(status().is4xxClientError())
                .andExpect(result -> result.getResponse().getContentAsString().equals("Player 1's turn!!!"));
    }

    @Test
    void shouldReturnNotFoundOnWrongGameId() throws Exception {
        // given
        final Game game = gameService.createGame();
        game.setPlayerOnTurn(Player.PLAYER_1);

        //when
        final ResultActions resultActions = mockMvc.perform(put("/game/" + 1 + "/move/" + 11));

        // then
        resultActions.andExpect(status().is4xxClientError())
                .andExpect(result -> result.getResponse().getContentAsString().equals("Game with id: 1 not found"));
    }
}
