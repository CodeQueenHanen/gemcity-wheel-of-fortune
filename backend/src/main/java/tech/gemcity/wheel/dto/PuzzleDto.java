package tech.gemcity.wheel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.gemcity.wheel.model.PuzzleCategory;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleDto {
    private Long id;
    private PuzzleCategory category;
    private String hint;
    private int totalLetters;
    private List<String> visiblePattern;
    private List<Boolean> solvedPositions;
}
