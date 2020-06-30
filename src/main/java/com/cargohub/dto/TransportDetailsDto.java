package com.cargohub.dto;

import com.cargohub.entities.transports.TransportDetails;
import com.cargohub.entities.transports.TransporterType;
import lombok.Data;

import javax.persistence.Enumerated;

@Data
public class TransportDetailsDto {
    private Integer id;
    @Enumerated
    private TransporterType type;
    private Double averageSpeed;
    private Double pricePerKm;

    public static TransportDetailsDto toDto(TransportDetails transportDetails) {
        TransportDetailsDto dto = new TransportDetailsDto();
        dto.setId(transportDetails.getId());
        dto.setAverageSpeed(transportDetails.getAverageSpeed());
        dto.setPricePerKm(transportDetails.getPricePerKm());
        dto.setType(transportDetails.getType());
        return dto;
    }

    public TransportDetails toTransportDetails() {
        TransportDetails details = new TransportDetails();
        details.setId(this.id);
        details.setAverageSpeed(this.averageSpeed);
        details.setPricePerKm(this.pricePerKm);
        details.setType(this.type);
        return details;
    }
}
