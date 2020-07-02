package com.cargohub.models;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class RouteModel {

    Double distance;
    List<String> routes;

}
