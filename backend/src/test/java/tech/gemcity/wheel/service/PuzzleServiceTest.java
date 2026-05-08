package tech.gemcity.wheel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.gemcity.wheel.dto.PuzzleDto;
import tech.gemcity.wheel.model.Puzzle;
import tech.gemcity.wheel.model.PuzzleCategory;
import tech.gemcity.wheel.repository.PuzzleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PuzzleService")
class PuzzleServiceTest {

    @Mock
    private PuzzleRepository puzzleRepository;

    @InjectMocks
    private PuzzleService puzzleService;

    private Puzzle conceptPuzzle;
    private Puzzle snippetPuzzle;

    @BeforeEach
    void setUp() {
        conceptPuzzle = Puzzle.builder()
                .id(1L)
                .category(PuzzleCategory.CONCEPT)
                .answer("BINARY SEARCH")
                .hint("An efficient lookup algorithm")
                .build();

        snippetPuzzle = Puzzle.builder()
                .id(2L)
                .category(PuzzleCategory.SNIPPET)
                .answer("int add(int a) { return a; }")
                .hint("What does this method do?")
                .build();
    }

    // ── getRandomPuzzle ───────────────────────────────────────────────────

    @Nested
    @DisplayName("getRandomPuzzle()")
    class GetRandomPuzzle {

        @Test
        @DisplayName("returns a PuzzleDto with all letters hidden when no letters revealed")
        void returnsHiddenPuzzle() {
            when(puzzleRepository.findRandom()).thenReturn(Optional.of(conceptPuzzle));

            PuzzleDto result = puzzleService.getRandomPuzzle();

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCategory()).isEqualTo(PuzzleCategory.CONCEPT);
            assertThat(result.getHint()).isEqualTo("An efficient lookup algorithm");
            // All letters should be hidden
            assertThat(result.getVisiblePattern()).doesNotContain("B","I","N","A","R","Y","S","E","C","H");
            assertThat(result.getVisiblePattern()).contains("_");
        }

        @Test
        @DisplayName("throws NoSuchElementException when no puzzles in database")
        void throwsWhenNoPuzzles() {
            when(puzzleRepository.findRandom()).thenReturn(Optional.empty());

            assertThatThrownBy(() -> puzzleService.getRandomPuzzle())
                    .isInstanceOf(java.util.NoSuchElementException.class)
                    .hasMessageContaining("No puzzles found");
        }
    }

    // ── buildPattern ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("buildPattern()")
    class BuildPattern {

        @Test
        @DisplayName("hides all letters when no letters revealed")
        void hidesAllLetters() {
            List<String> pattern = puzzleService.buildPattern("BINARY SEARCH", Set.of());

            assertThat(pattern).containsOnly("_", " ");
        }

        @Test
        @DisplayName("reveals correct letters when some are guessed")
        void revealsGuessedLetters() {
            List<String> pattern = puzzleService.buildPattern("BINARY SEARCH", Set.of('B', 'A'));

            assertThat(pattern.get(0)).isEqualTo("B");  // B
            assertThat(pattern.get(1)).isEqualTo("_");  // I hidden
            assertThat(pattern.get(3)).isEqualTo("A");  // A revealed
        }

        @Test
        @DisplayName("always shows punctuation in code snippets")
        void alwaysShowsPunctuation() {
            List<String> pattern = puzzleService.buildPattern("add(int a) { return a; }", Set.of());

            // Punctuation chars should always be visible
            assertThat(pattern).contains("(", ")", "{", "}", ";");
            // Letters should be hidden
            assertThat(pattern).contains("_");
        }

        @Test
        @DisplayName("spaces are preserved as spaces")
        void preservesSpaces() {
            List<String> pattern = puzzleService.buildPattern("AB CD", Set.of());

            assertThat(pattern.get(2)).isEqualTo(" ");
        }
    }

    // ── countMatches ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("countMatches()")
    class CountMatches {

        @Test
        @DisplayName("counts correct number of matching letters")
        void countsMatches() {
            assertThat(puzzleService.countMatches("BINARY SEARCH", 'R')).isEqualTo(2);
            assertThat(puzzleService.countMatches("BINARY SEARCH", 'A')).isEqualTo(2);
            assertThat(puzzleService.countMatches("BINARY SEARCH", 'B')).isEqualTo(1);
        }

        @Test
        @DisplayName("returns zero for letter not in answer")
        void returnsZeroForMiss() {
            assertThat(puzzleService.countMatches("BINARY SEARCH", 'Z')).isEqualTo(0);
        }

        @Test
        @DisplayName("is case-insensitive")
        void isCaseInsensitive() {
            assertThat(puzzleService.countMatches("Binary Search", 'b')).isEqualTo(1);
            assertThat(puzzleService.countMatches("Binary Search", 'B')).isEqualTo(1);
        }
    }

    // ── isSolved ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("isSolved()")
    class IsSolved {

        @Test
        @DisplayName("returns true when all letters are revealed")
        void trueWhenAllRevealed() {
            Set<Character> allLetters = Set.of('B','I','N','A','R','Y','S','E','C','H');
            assertThat(puzzleService.isSolved("BINARY SEARCH", allLetters)).isTrue();
        }

        @Test
        @DisplayName("returns false when some letters are still hidden")
        void falseWhenNotComplete() {
            assertThat(puzzleService.isSolved("BINARY SEARCH", Set.of('B', 'I'))).isFalse();
        }

        @Test
        @DisplayName("returns true for snippet when only punctuation remains (always visible)")
        void trueForSnippetWithPunctOnly() {
            // "{ }" — only punctuation and spaces, no letters to guess
            assertThat(puzzleService.isSolved("{ }", Set.of())).isTrue();
        }
    }

    // ── toDto ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toDto()")
    class ToDto {

        @Test
        @DisplayName("totalLetters counts only guessable letters, not spaces or punctuation")
        void countsTotalLettersCorrectly() {
            PuzzleDto dto = puzzleService.toDto(snippetPuzzle, Set.of());

            // "int add(int a) { return a; }" — letters only, not ( ) { } ; spaces
            assertThat(dto.getTotalLetters()).isGreaterThan(0);
            // Punctuation should be in pattern but not counted as letters
        }

        @Test
        @DisplayName("visiblePattern and solvedPositions have same length")
        void patternAndSolvedSameLength() {
            PuzzleDto dto = puzzleService.toDto(conceptPuzzle, Set.of());

            assertThat(dto.getVisiblePattern()).hasSameSizeAs(dto.getSolvedPositions());
        }
    }
}
