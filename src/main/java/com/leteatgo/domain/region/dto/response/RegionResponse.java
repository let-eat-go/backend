package com.leteatgo.domain.region.dto.response;

import com.leteatgo.domain.region.entity.Region;
import java.util.List;

public record RegionResponse(
        List<RegionInfo> regions
) {

}
