package tech.gemcity.wheel.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.gemcity.wheel.dto.WheelSegmentDto;
import tech.gemcity.wheel.model.WheelSegmentType;
import tech.gemcity.wheel.service.WheelService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WheelController.class)
@DisplayName("WheelController")
class WheelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WheelService wheelService;

    @Test
    @DisplayName("GET /api/wheel/spin returns 200 with a segment")
    void spin_returns200WithSegment() throws Exception {
        WheelSegmentDto dto = WheelSegmentDto.builder()
                .type(WheelSegmentType.POINTS)
                .points(500)
                .label("500")
                .color("#4a1a6b")
                .build();

        when(wheelService.spin()).thenReturn(dto);

        mockMvc.perform(get("/api/wheel/spin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("POINTS"))
                .andExpect(jsonPath("$.points").value(500))
                .andExpect(jsonPath("$.label").value("500"))
                .andExpect(jsonPath("$.color").value("#4a1a6b"));
    }

    @Test
    @DisplayName("GET /api/wheel/spin can return BANKRUPT segment")
    void spin_canReturnBankrupt() throws Exception {
        WheelSegmentDto dto = WheelSegmentDto.builder()
                .type(WheelSegmentType.BANKRUPT)
                .points(0)
                .label("BANKRUPT")
                .color("#6b1a1a")
                .build();

        when(wheelService.spin()).thenReturn(dto);

        mockMvc.perform(get("/api/wheel/spin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("BANKRUPT"))
                .andExpect(jsonPath("$.points").value(0));
    }

    @Test
    @DisplayName("GET /api/wheel/spin can return FREE_VOWEL segment")
    void spin_canReturnFreeVowel() throws Exception {
        WheelSegmentDto dto = WheelSegmentDto.builder()
                .type(WheelSegmentType.FREE_VOWEL)
                .points(0)
                .label("FREE VOWEL")
                .color("#6b3a1a")
                .build();

        when(wheelService.spin()).thenReturn(dto);

        mockMvc.perform(get("/api/wheel/spin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FREE_VOWEL"));
    }
}
