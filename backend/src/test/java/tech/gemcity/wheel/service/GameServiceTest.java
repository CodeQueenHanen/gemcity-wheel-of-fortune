package tech.gemcity.wheel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.gemcity.wheel.dto.GuessResult;
import tech.gemcity.wheel.model.Puzzle;
import tech.gemcity.wheel.model.PuzzleCategory;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService")
class GameServiceTest {

    @Mock
    private PuzzleService puzzleService;

    @InjectMocks
    private GameService gameService;

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
        puzzle = Puzzle.builder()
                .id(1L)
                .category(PuzzleCategory.CONCEPT)
                .answer("BINARY SEARCH")
                .hint("An efficient lookup algorithm")
                .build();
    }

    // ── processGuess ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("processGuess()")
    class ProcessGuess {

        @BeforeEach
        void setUp() {
            when(puzzleService.findById(1L)).thenReturn(puzzle);
            when(puzzleService.countMatches(eq("BINARY SEARCH"), anyChar()))
                    .thenAnswer(inv -> {
                        char letter = inv.getArgument(1);
                        String answer = "BINARY SEARCH";
                        int count = 0;
                        for (char c : answer.toCharArray()) if (c == letter) count++;
                        return count;
                    });
            when(puzzleService.buildPattern(anyString(), anySet()))
                    .thenReturn(List.of("_"));
            when(puzzleService.isSolved(anyString(), anySet()))
                    .thenReturn(false);
        }

        @Test
        @DisplayName("correct guess: returns correct=true with matchCount and pointsEarned")
        void correctGuessReturnsMatchInfo() {
            GuessResult result = gameService.processGuess(1L, 'B', 500, Set.of());

            assertThat(result.isCorrect()).isTrue();
            assertThat(result.getMatchCount()).isEqualTo(1);
            assertThat(result.getPointsEarned()).isEqualTo(500);
            assertThat(result.getLetter()).isEqualTo("B");
        }

        @Test
        @DisplayName("incorrect guess: returns correct=false with zero points")
        void incorrectGuessReturnsZeroPoints() {
            GuessResult result = gameService.processGuess(1L, 'Z', 500, Set.of());

            assertThat(result.isCorrect()).isFalse();
            assertThat(result.getMatchCount()).isEqualTo(0);
            assertThat(result.getPointsEarned()).isEqualTo(0);
        }

        @Test
        @DisplayName("multi-match letter multiplies points correctly")
        void multiMatchMultipliesPoints() {
            // 'R' appears twice in BINARY SEARCH
            GuessResult result = gameService.processGuess(1L, 'R', 250, Set.of());

            assertThat(result.getMatchCount()).isEqualTo(2);
            assertThat(result.getPointsEarned()).isEqualTo(500); // 2 × 250
        }

        @Test
        @DisplayName("letter is uppercased before processing")
        void normalizesLetterToUppercase() {
            GuessResult result = gameService.processGuess(1L, 'b', 500, Set.of());

            assertThat(result.getLetter()).isEqualTo("B");
        }

        @Test
        @DisplayName("FREE_VOWEL guess with pointsPerHit=0 earns no points even if correct")
        void freeVowelEarnsNoPoints() {
            GuessResult result = gameService.processGuess(1L, 'A', 0, Set.of());

            assertThat(result.isCorrect()).isTrue();
            assertThat(result.getPointsEarned()).isEqualTo(0);
        }

        @Test
        @DisplayName("marks puzzle as solved when isSolved returns true")
        void marksPuzzleSolvedCorrectly() {
            when(puzzleService.isSolved(anyString(), anySet())).thenReturn(true);

            GuessResult result = gameService.processGuess(1L, 'H', 500, Set.of());

            assertThat(result.isPuzzleSolved()).isTrue();
        }
    }

    // ── processSolve ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("processSolve()")
    class ProcessSolve {

        @BeforeEach
        void setUp() {
            when(puzzleService.findById(1L)).thenReturn(puzzle);
            when(puzzleService.buildPattern(anyString(), anySet()))
                    .thenReturn(List.of("B","I","N","A","R","Y"));
        }

        @Test
        @DisplayName("correct solve attempt returns puzzleSolved=true")
        void correctSolveReturnsSolved() {
            GuessResult result = gameService.processSolve(1L, "BINARY SEARCH");

            assertThat(result.isPuzzleSolved()).isTrue();
            assertThat(result.isCorrect()).isTrue();
            assertThat(result.getPointsEarned()).isEqualTo(500);
        }

        @Test
        @DisplayName("wrong solve attempt returns puzzleSolved=false and zero points")
        void wrongSolveReturnsNotSolved() {
            GuessResult result = gameService.processSolve(1L, "BUBBLE SORT");

            assertThat(result.isPuzzleSolved()).isFalse();
            assertThat(result.isCorrect()).isFalse();
            assertThat(result.getPointsEarned()).isEqualTo(0);
        }

        @Test
        @DisplayName("solve is case-insensitive")
        void solveIsCaseInsensitive() {
            GuessResult result = gameService.processSolve(1L, "binary search");

            assertThat(result.isCorrect()).isTrue();
        }

        @Test
        @DisplayName("solve trims whitespace from attempt")
        void solveTrimssWhitespace() {
            GuessResult result = gameService.processSolve(1L, "  BINARY SEARCH  ");

            assertThat(result.isCorrect()).isTrue();
        }
    }
}
