package tech.gemcity.wheel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.gemcity.wheel.dto.PuzzleDto;
import tech.gemcity.wheel.model.Puzzle;
import tech.gemcity.wheel.repository.PuzzleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PuzzleService {

    // Characters that are always visible on the board (never hidden)
    static final Set<Character> ALWAYS_VISIBLE = Set.of(
            '{', '}', '(', ')', '[', ']', ';', '.', ',', ':',
            '<', '>', '"', '\'', '+', '-', '*', '/', '!', '='
    );

    private final PuzzleRepository puzzleRepository;

    /**
     * Returns a random puzzle with all letters hidden.
     */
    public PuzzleDto getRandomPuzzle() {
        Puzzle puzzle = puzzleRepository.findRandom()
                .orElseThrow(() -> new NoSuchElementException("No puzzles found in database"));
        return toDto(puzzle, Set.of());
    }

    /**
     * Converts a Puzzle entity to a DTO, revealing only the letters
     * present in the revealedLetters set (plus punctuation/spaces).
     */
    public PuzzleDto toDto(Puzzle puzzle, Set<Character> revealedLetters) {
        String answer = puzzle.getAnswer().toUpperCase();
        List<String> pattern = new ArrayList<>();
        List<Boolean> solved = new ArrayList<>();
        int letterCount = 0;

        for (char c : answer.toCharArray()) {
            if (c == ' ') {
                pattern.add(" ");
                solved.add(false);
            } else if (ALWAYS_VISIBLE.contains(c)) {
                pattern.add(String.valueOf(c));
                solved.add(true);
            } else {
                // It's a letter
                letterCount++;
                boolean isRevealed = revealedLetters.contains(c);
                pattern.add(isRevealed ? String.valueOf(c) : "_");
                solved.add(isRevealed);
            }
        }

        return PuzzleDto.builder()
                .id(puzzle.getId())
                .category(puzzle.getCategory())
                .hint(puzzle.getHint())
                .totalLetters(letterCount)
                .visiblePattern(pattern)
                .solvedPositions(solved)
                .build();
    }

    /**
     * Fetches a puzzle by ID — throws if not found.
     */
    public Puzzle findById(Long id) {
        return puzzleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Puzzle not found: " + id));
    }

    /**
     * Counts how many times a letter appears in the answer (letters only).
     */
    public int countMatches(String answer, char letter) {
        char upper = Character.toUpperCase(letter);
        int count = 0;
        for (char c : answer.toUpperCase().toCharArray()) {
            if (c == upper) count++;
        }
        return count;
    }

    /**
     * Builds the updated visible pattern given a set of revealed letters.
     */
    public List<String> buildPattern(String answer, Set<Character> revealed) {
        List<String> pattern = new ArrayList<>();
        for (char c : answer.toUpperCase().toCharArray()) {
            if (c == ' ') {
                pattern.add(" ");
            } else if (ALWAYS_VISIBLE.contains(c)) {
                pattern.add(String.valueOf(c));
            } else {
                pattern.add(revealed.contains(c) ? String.valueOf(c) : "_");
            }
        }
        return pattern;
    }

    /**
     * Returns true if every non-space, non-punct character in the answer
     * has been revealed.
     */
    public boolean isSolved(String answer, Set<Character> revealed) {
        for (char c : answer.toUpperCase().toCharArray()) {
            if (c != ' ' && !ALWAYS_VISIBLE.contains(c) && !revealed.contains(c)) {
                return false;
            }
        }
        return true;
    }
}
