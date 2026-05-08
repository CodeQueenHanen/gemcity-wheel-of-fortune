package tech.gemcity.wheel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.gemcity.wheel.dto.PuzzleDto;
import tech.gemcity.wheel.model.PuzzleCategory;
import tech.gemcity.wheel.service.PuzzleService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PuzzleController.class)
@DisplayName("PuzzleController")
class PuzzleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PuzzleService puzzleService;

    @Test
    @DisplayName("GET /api/puzzle/random returns 200 with puzzle JSON")
    void getRandomPuzzle_returns200() throws Exception {
        PuzzleDto dto = PuzzleDto.builder()
                .id(1L)
                .category(PuzzleCategory.CONCEPT)
                .hint("An efficient lookup algorithm")
                .totalLetters(12)
                .visiblePattern(List.of("_","_","_","_","_","_"," ","_","_","_","_","_","_"))
                .solvedPositions(List.of(false,false,false,false,false,false,false,false,false,false,false,false,false))
                .build();

        when(puzzleService.getRandomPuzzle()).thenReturn(dto);

        mockMvc.perform(get("/api/puzzle/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("CONCEPT"))
                .andExpect(jsonPath("$.hint").value("An efficient lookup algorithm"))
                .andExpect(jsonPath("$.totalLetters").value(12))
                .andExpect(jsonPath("$.visiblePattern").isArray())
                .andExpect(jsonPath("$.solvedPositions").isArray());
    }

    @Test
    @DisplayName("GET /api/puzzle/random returns 404 when no puzzles exist")
    void getRandomPuzzle_returns404WhenEmpty() throws Exception {
        when(puzzleService.getRandomPuzzle())
                .thenThrow(new NoSuchElementException("No puzzles found in database"));

        mockMvc.perform(get("/api/puzzle/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
