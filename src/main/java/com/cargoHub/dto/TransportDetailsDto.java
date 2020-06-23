package com.cargohub.dto;

import com.cargohub.entities.transports.TransportDetails;
import com.cargohub.entities.transports.TransporterType;
import lombok.Data;

import javax.persistence.Enumerated;

@Data
public class TransportDetailsDto {
    @Enumerated
    private TransporterType type;
    private Double averageSpeed;
    private Double prisePerKm;

    public static TransportDetailsDto toDto(TransportDetails transportDetails) {
        TransportDetailsDto dto = new TransportDetailsDto();
        dto.setAverageSpeed(transportDetails.getAverageSpeed());
        dto.setPrisePerKm(transportDetails.getPrisePerKm());
        dto.setType(transportDetails.getType());
        return dto;
    }

    public TransportDetails toTransportDetails() {
        TransportDetails details = new TransportDetails();
        details.setAverageSpeed(this.averageSpeed);
        details.setPrisePerKm(this.prisePerKm);
        details.setType(this.type);
        return details;
    }
}
