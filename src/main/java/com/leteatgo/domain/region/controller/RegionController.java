package com.leteatgo.domain.region.controller;

import com.leteatgo.domain.region.dto.response.RegionResponse;
import com.leteatgo.domain.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<RegionResponse> getRegions() {
        return ResponseEntity.ok(regionService.getRegions());
    }
}
