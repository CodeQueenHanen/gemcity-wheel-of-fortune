package tech.gemcity.wheel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.gemcity.wheel.dto.GuessRequest;
import tech.gemcity.wheel.dto.GuessResult;
import tech.gemcity.wheel.dto.SolveRequest;
import tech.gemcity.wheel.service.GameService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@DisplayName("GameController")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    private GuessResult correctGuessResult;
    private GuessResult incorrectGuessResult;
    private GuessResult solvedResult;

    @BeforeEach
    void setUp() {
        correctGuessResult = GuessResult.builder()
                .letter("B")
                .correct(true)
                .matchCount(1)
                .pointsEarned(500)
                .updatedPattern(List.of("B","_","_","_","_","_"," ","_","_","_","_","_","_"))
                .solvedPositions(List.of(true,false,false,false,false,false,false,false,false,false,false,false,false))
                .puzzleSolved(false)
                .newScore(0)
                .build();

        incorrectGuessResult = GuessResult.builder()
                .letter("Z")
                .correct(false)
                .matchCount(0)
                .pointsEarned(0)
                .updatedPattern(List.of("_","_","_","_","_","_"," ","_","_","_","_","_","_"))
                .solvedPositions(List.of(false,false,false,false,false,false,false,false,false,false,false,false,false))
                .puzzleSolved(false)
                .newScore(0)
                .build();

        solvedResult = GuessResult.builder()
                .letter("")
                .correct(true)
                .matchCount(0)
                .pointsEarned(500)
                .updatedPattern(List.of("B","I","N","A","R","Y"," ","S","E","A","R","C","H"))
                .solvedPositions(List.of(true,true,true,true,true,true,false,true,true,true,true,true,true))
                .puzzleSolved(true)
                .newScore(0)
                .build();
    }

    // ── POST /api/game/guess ───────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/game/guess")
    class GuessEndpoint {

        @Test
        @DisplayName("returns 200 with correct=true for a matching letter")
        void correctGuess_returns200() throws Exception {
            when(gameService.processGuess(anyLong(), anyChar(), anyInt(), anySet()))
                    .thenReturn(correctGuessResult);

            GuessRequest request = new GuessRequest(1L, "B", 500);

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct").value(true))
                    .andExpect(jsonPath("$.letter").value("B"))
                    .andExpect(jsonPath("$.matchCount").value(1))
                    .andExpect(jsonPath("$.pointsEarned").value(500))
                    .andExpect(jsonPath("$.puzzleSolved").value(false))
                    .andExpect(jsonPath("$.updatedPattern").isArray());
        }

        @Test
        @DisplayName("passes revealed letters through to game service")
        void revealedLetters_areForwarded() throws Exception {
            when(gameService.processGuess(eq(1L), eq('B'), eq(500), eq(Set.of('A', 'E'))))
                    .thenReturn(correctGuessResult);

            String requestJson = objectMapper.writeValueAsString(
                    Map.of(
                            "puzzleId", 1,
                            "letter", "B",
                            "pointsPerHit", 500,
                            "revealedLetters", List.of("A", "E")
                    )
            );

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk());

            verify(gameService).processGuess(eq(1L), eq('B'), eq(500), eq(Set.of('A', 'E')));
        }

        @Test
        @DisplayName("returns 200 with correct=false for a non-matching letter")
        void incorrectGuess_returns200() throws Exception {
            when(gameService.processGuess(anyLong(), anyChar(), anyInt(), anySet()))
                    .thenReturn(incorrectGuessResult);

            GuessRequest request = new GuessRequest(1L, "Z", 500);

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct").value(false))
                    .andExpect(jsonPath("$.pointsEarned").value(0));
        }

        @Test
        @DisplayName("returns 400 when letter field is missing")
        void missingLetter_returns400() throws Exception {
            String badJson = "{\"puzzleId\": 1, \"pointsPerHit\": 500}";

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("returns 400 when letter is a digit, not A-Z")
        void invalidLetter_returns400() throws Exception {
            GuessRequest request = new GuessRequest(1L, "5", 500);

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when puzzleId is missing")
        void missingPuzzleId_returns400() throws Exception {
            String badJson = "{\"letter\": \"B\", \"pointsPerHit\": 500}";

            mockMvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── POST /api/game/solve ───────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/game/solve")
    class SolveEndpoint {

        @Test
        @DisplayName("returns 200 with puzzleSolved=true for correct answer")
        void correctSolve_returns200() throws Exception {
            when(gameService.processSolve(anyLong(), anyString()))
                    .thenReturn(solvedResult);

            SolveRequest request = new SolveRequest(1L, "BINARY SEARCH");

            mockMvc.perform(post("/api/game/solve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct").value(true))
                    .andExpect(jsonPath("$.puzzleSolved").value(true))
                    .andExpect(jsonPath("$.pointsEarned").value(500));
        }

        @Test
        @DisplayName("returns 400 when attempt is blank")
        void blankAttempt_returns400() throws Exception {
            SolveRequest request = new SolveRequest(1L, "");

            mockMvc.perform(post("/api/game/solve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when puzzleId is missing")
        void missingPuzzleId_returns400() throws Exception {
            String badJson = "{\"attempt\": \"BINARY SEARCH\"}";

            mockMvc.perform(post("/api/game/solve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
