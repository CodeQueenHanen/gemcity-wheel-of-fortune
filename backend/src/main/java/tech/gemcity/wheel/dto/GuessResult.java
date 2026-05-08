package tech.gemcity.wheel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuessResult {
    private String letter;
    private boolean correct;
    private int matchCount;
    private int pointsEarned;
    private List<String> updatedPattern;
    private List<Boolean> solvedPositions;
    private boolean puzzleSolved;
    private int newScore;
}
