package com.leteatgo.domain.region.service;

import com.leteatgo.domain.region.dto.response.RegionInfo;
import com.leteatgo.domain.region.dto.response.RegionResponse;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.repository.RegionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionResponse getRegions() {
        List<Region> regions = regionRepository.findAll();
        return new RegionResponse(
                regions.stream()
                        .map(region -> new RegionInfo(region.getId(), region.getName()))
                        .toList()
        );
    }
}
