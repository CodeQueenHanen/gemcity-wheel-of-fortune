package tech.gemcity.wheel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolveRequest {

    @NotNull(message = "puzzleId is required")
    private Long puzzleId;

    @NotBlank(message = "attempt is required")
    private String attempt;
}
