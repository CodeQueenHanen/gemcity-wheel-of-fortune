package tech.gemcity.wheel.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tech.gemcity.wheel.dto.GuessRequest;
import tech.gemcity.wheel.dto.SolveRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration tests — boots the entire Spring context with H2 + seed data.
 * These tests verify the real service → repository → DB stack, not just mocks.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integration: full stack with H2")
class GameIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ── /api/puzzle/random ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/puzzle/random returns a seeded puzzle with hidden letters")
    void getRandomPuzzle_returnsSeededPuzzle() throws Exception {
        mockMvc.perform(get("/api/puzzle/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.category").isString())
                .andExpect(jsonPath("$.hint").isString())
                .andExpect(jsonPath("$.totalLetters").isNumber())
                .andExpect(jsonPath("$.visiblePattern").isArray())
                .andExpect(jsonPath("$.solvedPositions").isArray());
    }

    // ── /api/wheel/spin ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/wheel/spin returns a valid segment")
    void spin_returnsValidSegment() throws Exception {
        mockMvc.perform(get("/api/wheel/spin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.points").isNumber())
                .andExpect(jsonPath("$.label").isString())
                .andExpect(jsonPath("$.color").isString());
    }

    // ── /api/game/guess ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/game/guess with real puzzle ID returns valid guess result")
    void guess_withRealPuzzle_returnsResult() throws Exception {
        // First, get a real puzzle ID from the database
        MvcResult puzzleResult = mockMvc.perform(get("/api/puzzle/random"))
                .andExpect(status().isOk())
                .andReturn();

        Long puzzleId = objectMapper.readTree(
                puzzleResult.getResponse().getContentAsString()
        ).get("id").asLong();

        // Now guess a letter
        GuessRequest request = new GuessRequest(puzzleId, "A", 500);

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.letter").value("A"))
                .andExpect(jsonPath("$.correct").isBoolean())
                .andExpect(jsonPath("$.matchCount").isNumber())
                .andExpect(jsonPath("$.pointsEarned").isNumber())
                .andExpect(jsonPath("$.updatedPattern").isArray())
                .andExpect(jsonPath("$.solvedPositions").isArray())
                .andExpect(jsonPath("$.puzzleSolved").isBoolean());
    }

    @Test
    @DisplayName("POST /api/game/guess with non-existent puzzle ID returns 404")
    void guess_withInvalidPuzzleId_returns404() throws Exception {
        GuessRequest request = new GuessRequest(99999L, "A", 500);

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── /api/game/solve ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/game/solve with wrong answer returns correct=false")
    void solve_withWrongAnswer_returnsNotSolved() throws Exception {
        MvcResult puzzleResult = mockMvc.perform(get("/api/puzzle/random"))
                .andExpect(status().isOk())
                .andReturn();

        Long puzzleId = objectMapper.readTree(
                puzzleResult.getResponse().getContentAsString()
        ).get("id").asLong();

        SolveRequest request = new SolveRequest(puzzleId, "DEFINITELY WRONG ANSWER XYZ");

        mockMvc.perform(post("/api/game/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false))
                .andExpect(jsonPath("$.puzzleSolved").value(false))
                .andExpect(jsonPath("$.pointsEarned").value(0));
    }

    @Test
    @DisplayName("POST /api/game/solve with correct answer returns puzzleSolved=true")
    void solve_withCorrectAnswer_returnsSolved() throws Exception {
        // Puzzle ID 1 is seeded as "BINARY SEARCH" from data.sql
        SolveRequest request = new SolveRequest(1L, "BINARY SEARCH");

        mockMvc.perform(post("/api/game/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.puzzleSolved").value(true))
                .andExpect(jsonPath("$.pointsEarned").value(500));
    }
}
