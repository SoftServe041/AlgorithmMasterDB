package com.cargohub.dto;

import com.cargohub.entities.transports.TransportDetailsEntity;
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
    private Double cellSize;

    public static TransportDetailsDto toDto(TransportDetailsEntity transportDetails) {
        TransportDetailsDto dto = new TransportDetailsDto();
        dto.setId(transportDetails.getId());
        dto.setAverageSpeed(transportDetails.getAverageSpeed());
        dto.setPricePerKm(transportDetails.getPricePerKm());
        dto.setType(transportDetails.getType());
        dto.setCellSize(transportDetails.getCellSize());
        return dto;
    }

    public TransportDetailsEntity toTransportDetails() {
        TransportDetailsEntity details = new TransportDetailsEntity();
        details.setId(this.id);
        details.setAverageSpeed(this.averageSpeed);
        details.setPricePerKm(this.pricePerKm);
        details.setType(this.type);
        details.setCellSize(this.cellSize);
        return details;
    }
}
