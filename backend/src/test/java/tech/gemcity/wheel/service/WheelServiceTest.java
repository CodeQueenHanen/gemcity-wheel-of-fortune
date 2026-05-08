package tech.gemcity.wheel.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import tech.gemcity.wheel.dto.WheelSegmentDto;
import tech.gemcity.wheel.model.WheelSegmentType;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WheelService")
class WheelServiceTest {

    @Test
    @DisplayName("spin() returns a non-null segment")
    void spinReturnsSegment() {
        WheelService service = new WheelService();
        WheelSegmentDto result = service.spin();

        assertThat(result).isNotNull();
        assertThat(result.getType()).isNotNull();
        assertThat(result.getLabel()).isNotBlank();
        assertThat(result.getColor()).isNotBlank();
    }

    @Test
    @DisplayName("spin() returns BANKRUPT when random index hits a BANKRUPT slot")
    void spinReturnsBankruptDeterministically() {
        // BANKRUPT is at index 5 in the segment list
        Random seededRandom = mock(Random.class);
        when(seededRandom.nextInt(anyInt())).thenReturn(5);

        WheelService service = new WheelService(seededRandom);
        WheelSegmentDto result = service.spin();

        assertThat(result.getType()).isEqualTo(WheelSegmentType.BANKRUPT);
        assertThat(result.getPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("spin() returns FREE_VOWEL when random index hits FREE_VOWEL slot")
    void spinReturnsFreeVowelDeterministically() {
        // FREE_VOWEL is at index 7
        Random seededRandom = mock(Random.class);
        when(seededRandom.nextInt(anyInt())).thenReturn(7);

        WheelService service = new WheelService(seededRandom);
        WheelSegmentDto result = service.spin();

        assertThat(result.getType()).isEqualTo(WheelSegmentType.FREE_VOWEL);
        assertThat(result.getPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("spin() returns 1000 pts when random index hits the 1000 slot")
    void spinReturnsMaxPoints() {
        // 1000 is at index 11
        Random seededRandom = mock(Random.class);
        when(seededRandom.nextInt(anyInt())).thenReturn(11);

        WheelService service = new WheelService(seededRandom);
        WheelSegmentDto result = service.spin();

        assertThat(result.getType()).isEqualTo(WheelSegmentType.POINTS);
        assertThat(result.getPoints()).isEqualTo(1000);
    }

    @RepeatedTest(20)
    @DisplayName("spin() always returns a valid segment from the known list")
    void spinAlwaysReturnsValidSegment() {
        WheelService service = new WheelService();
        WheelSegmentDto result = service.spin();

        assertThat(result.getType()).isIn(
                WheelSegmentType.POINTS,
                WheelSegmentType.BANKRUPT,
                WheelSegmentType.FREE_VOWEL
        );
        assertThat(result.getPoints()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("getAllSegments() returns 12 segments")
    void getAllSegmentsReturnsCorrectCount() {
        WheelService service = new WheelService();
        assertThat(service.getAllSegments()).hasSize(12);
    }

    @Test
    @DisplayName("BANKRUPT segments always have 0 points")
    void bankruptSegmentsHaveZeroPoints() {
        WheelService service = new WheelService();
        service.getAllSegments().stream()
                .filter(s -> s.getType() == WheelSegmentType.BANKRUPT)
                .forEach(s -> assertThat(s.getPoints()).isEqualTo(0));
    }
}
