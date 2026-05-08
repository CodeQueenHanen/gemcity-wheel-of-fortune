package tech.gemcity.wheel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.gemcity.wheel.dto.GuessResult;
import tech.gemcity.wheel.model.Puzzle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stateless game logic — the frontend tracks which letters have been
 * guessed so far and passes pointsPerHit from the wheel spin.
 * Each request is fully self-contained.
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final PuzzleService puzzleService;

    /**
     * Processes a single letter guess against the puzzle answer.
     *
     * @param puzzleId     the puzzle being played
     * @param letter       the letter the player guessed (single char, A-Z)
     * @param pointsPerHit points awarded per matching letter from the wheel spin
     * @param guessedSoFar letters already revealed this game (so pattern is accurate)
     */
    public GuessResult processGuess(Long puzzleId, char letter, int pointsPerHit,
                                    Set<Character> guessedSoFar) {
        Puzzle puzzle = puzzleService.findById(puzzleId);
        char upper = Character.toUpperCase(letter);

        int matchCount = puzzleService.countMatches(puzzle.getAnswer(), upper);
        boolean correct = matchCount > 0;
        int pointsEarned = correct ? matchCount * pointsPerHit : 0;

        Set<Character> nowRevealed = new HashSet<>(guessedSoFar);
        if (correct) nowRevealed.add(upper);

        List<String> updatedPattern = puzzleService.buildPattern(puzzle.getAnswer(), nowRevealed);
        List<Boolean> solvedPositions = buildSolvedPositions(puzzle.getAnswer(), nowRevealed);
        boolean puzzleSolved = puzzleService.isSolved(puzzle.getAnswer(), nowRevealed);

        return GuessResult.builder()
                .letter(String.valueOf(upper))
                .correct(correct)
                .matchCount(matchCount)
                .pointsEarned(pointsEarned)
                .updatedPattern(updatedPattern)
                .solvedPositions(solvedPositions)
                .puzzleSolved(puzzleSolved)
                .newScore(0) // frontend owns cumulative score for MVP
                .build();
    }

    /**
     * Processes a full solve attempt.
     */
    public GuessResult processSolve(Long puzzleId, String attempt) {
        Puzzle puzzle = puzzleService.findById(puzzleId);
        boolean correct = puzzle.getAnswer().equalsIgnoreCase(attempt.trim());

        Set<Character> allRevealed = new HashSet<>();
        if (correct) {
            // Reveal all letters
            for (char c : puzzle.getAnswer().toUpperCase().toCharArray()) {
                allRevealed.add(c);
            }
        }

        List<String> updatedPattern = puzzleService.buildPattern(puzzle.getAnswer(), allRevealed);
        List<Boolean> solvedPositions = buildSolvedPositions(puzzle.getAnswer(), allRevealed);

        return GuessResult.builder()
                .letter("")
                .correct(correct)
                .matchCount(0)
                .pointsEarned(correct ? 500 : 0)
                .updatedPattern(updatedPattern)
                .solvedPositions(solvedPositions)
                .puzzleSolved(correct)
                .newScore(0)
                .build();
    }

    private List<Boolean> buildSolvedPositions(String answer, Set<Character> revealed) {
        List<Boolean> positions = new ArrayList<>();
        for (char c : answer.toUpperCase().toCharArray()) {
            if (c == ' ') {
                positions.add(false);
            } else if (PuzzleService.ALWAYS_VISIBLE.contains(c)) {
                positions.add(true);
            } else {
                positions.add(revealed.contains(c));
            }
        }
        return positions;
    }
}
