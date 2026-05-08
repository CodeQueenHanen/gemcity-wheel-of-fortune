package tech.gemcity.wheel.service;

import org.springframework.stereotype.Service;
import tech.gemcity.wheel.dto.WheelSegmentDto;
import tech.gemcity.wheel.model.WheelSegmentType;

import java.util.List;
import java.util.Random;

@Service
public class WheelService {

    private static final List<WheelSegmentDto> SEGMENTS = List.of(
            segment(WheelSegmentType.POINTS,     100,  "100",        "#1a6b3a"),
            segment(WheelSegmentType.POINTS,     250,  "250",        "#1a4a6b"),
            segment(WheelSegmentType.POINTS,     500,  "500",        "#4a1a6b"),
            segment(WheelSegmentType.POINTS,     750,  "750",        "#6b4a1a"),
            segment(WheelSegmentType.POINTS,     100,  "100",        "#1a6b3a"),
            segment(WheelSegmentType.BANKRUPT,   0,    "BANKRUPT",   "#6b1a1a"),
            segment(WheelSegmentType.POINTS,     250,  "250",        "#1a4a6b"),
            segment(WheelSegmentType.FREE_VOWEL, 0,    "FREE VOWEL", "#6b3a1a"),
            segment(WheelSegmentType.POINTS,     500,  "500",        "#4a1a6b"),
            segment(WheelSegmentType.POINTS,     750,  "750",        "#6b4a1a"),
            segment(WheelSegmentType.BANKRUPT,   0,    "BANKRUPT",   "#6b1a1a"),
            segment(WheelSegmentType.POINTS,     1000, "1000",       "#006b4a")
    );

    private final Random random;

    public WheelService() {
        this.random = new Random();
    }

    // Constructor for test injection of seeded Random
    WheelService(Random random) {
        this.random = random;
    }

    public WheelSegmentDto spin() {
        int index = random.nextInt(SEGMENTS.size());
        return SEGMENTS.get(index);
    }

    public List<WheelSegmentDto> getAllSegments() {
        return SEGMENTS;
    }

    private static WheelSegmentDto segment(WheelSegmentType type, int points, String label, String color) {
        return WheelSegmentDto.builder()
                .type(type)
                .points(points)
                .label(label)
                .color(color)
                .build();
    }
}
