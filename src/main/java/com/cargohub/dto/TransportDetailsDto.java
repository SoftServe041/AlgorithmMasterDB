package com.cargohub.dto;

import com.cargohub.entities.transports.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
public class TransportDetailsDto {
    @Enumerated
    private TransportType type;
    private Double averageSpeed;
    private Double prisePerKm;
}
