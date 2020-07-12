package com.cargohub.dto;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouteDto {
    private List<String> hubs;

    public RouteEntity toEntity() {
        RouteEntity route = new RouteEntity();
        List<HubEntity> hubsList = new ArrayList<>();
        for (String hub : hubs) {
            HubEntity hubEntity = new HubEntity();
            hubEntity.setName(hub);
            hubsList.add(hubEntity);
        }
        route.setHubs(hubsList);
        return route;
    }
}
