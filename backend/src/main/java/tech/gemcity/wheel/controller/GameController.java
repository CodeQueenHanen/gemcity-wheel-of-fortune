package tech.gemcity.wheel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.gemcity.wheel.dto.GuessRequest;
import tech.gemcity.wheel.dto.GuessResult;
import tech.gemcity.wheel.dto.SolveRequest;
import tech.gemcity.wheel.service.GameService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/guess")
    public ResponseEntity<GuessResult> guess(@Valid @RequestBody GuessRequest request) {
        char letter = request.getLetter().toUpperCase().charAt(0);
        List<String> revealedLetters = request.getRevealedLetters();
        Set<Character> guessedSoFar = new HashSet<>();
        if (revealedLetters != null) {
            for (String value : revealedLetters) {
                if (value != null && !value.isBlank()) {
                    guessedSoFar.add(Character.toUpperCase(value.charAt(0)));
                }
            }
        }

        GuessResult result = gameService.processGuess(
                request.getPuzzleId(),
                letter,
                request.getPointsPerHit(),
                guessedSoFar
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/solve")
    public ResponseEntity<GuessResult> solve(@Valid @RequestBody SolveRequest request) {
        GuessResult result = gameService.processSolve(
                request.getPuzzleId(),
                request.getAttempt()
        );
        return ResponseEntity.ok(result);
    }
}
