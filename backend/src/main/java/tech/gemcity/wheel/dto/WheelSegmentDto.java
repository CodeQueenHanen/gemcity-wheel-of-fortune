package tech.gemcity.wheel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.gemcity.wheel.model.WheelSegmentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WheelSegmentDto {
    private WheelSegmentType type;
    private int points;
    private String label;
    private String color;
}
