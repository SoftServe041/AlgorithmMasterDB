package com.cargohub.dto;

import com.cargohub.entities.Hub;
import com.cargohub.entities.Route;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouteDto {
    private List<String> hubs;

    public Route toEntity() {
        Route route = new Route();
        List<Hub> hubsList = new ArrayList<>();
        for (String hub : hubs) {
            Hub hubEntity = new Hub();
            hubEntity.setName(hub);
            hubsList.add(hubEntity);
        }
        route.setRoute(hubsList);
        return route;
    }
}
