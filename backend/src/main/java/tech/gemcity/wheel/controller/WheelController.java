package tech.gemcity.wheel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.gemcity.wheel.dto.WheelSegmentDto;
import tech.gemcity.wheel.service.WheelService;

@RestController
@RequestMapping("/api/wheel")
@RequiredArgsConstructor
public class WheelController {

    private final WheelService wheelService;

    @GetMapping("/spin")
    public ResponseEntity<WheelSegmentDto> spin() {
        return ResponseEntity.ok(wheelService.spin());
    }
}
