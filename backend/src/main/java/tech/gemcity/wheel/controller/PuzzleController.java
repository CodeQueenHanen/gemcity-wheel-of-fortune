package tech.gemcity.wheel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.gemcity.wheel.dto.PuzzleDto;
import tech.gemcity.wheel.service.PuzzleService;

@RestController
@RequestMapping("/api/puzzle")
@RequiredArgsConstructor
public class PuzzleController {

    private final PuzzleService puzzleService;

    @GetMapping("/random")
    public ResponseEntity<PuzzleDto> getRandomPuzzle() {
        return ResponseEntity.ok(puzzleService.getRandomPuzzle());
    }
}
