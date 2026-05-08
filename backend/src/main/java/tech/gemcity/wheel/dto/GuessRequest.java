package tech.gemcity.wheel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuessRequest {

    @NotNull(message = "puzzleId is required")
    private Long puzzleId;

    @NotBlank(message = "letter is required")
    @Pattern(regexp = "[A-Za-z]", message = "Must be a single letter A-Z")
    private String letter;

    @PositiveOrZero(message = "pointsPerHit must be >= 0")
    private int pointsPerHit;

    private List<String> revealedLetters = new ArrayList<>();
}
