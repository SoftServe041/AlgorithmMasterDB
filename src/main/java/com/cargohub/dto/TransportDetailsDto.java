package com.cargohub.dto;

import com.cargohub.entities.transports.TransporterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
public class TransportDetailsDto {
    @Enumerated
    private TransporterType type;
    private Double averageSpeed;
    private Double prisePerKm;
}
