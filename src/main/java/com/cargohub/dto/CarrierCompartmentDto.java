package com.cargohub.dto;

import com.cargohub.entities.transports.CarrierCompartmentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarrierCompartmentDto {
    private Integer id;
    private Double maximumWeight;
    private Double freeSpace;
    private DimensionsDto volume;

    public static CarrierCompartmentDto toDto(CarrierCompartmentEntity carrierCompartment) {
        CarrierCompartmentDto dto = new CarrierCompartmentDto();
        dto.setId(carrierCompartment.getId());
        dto.setFreeSpace(carrierCompartment.getFreeSpace());
        dto.setMaximumWeight(carrierCompartment.getMaximumWeight());
        dto.setVolume(DimensionsDto.toDto(carrierCompartment.getVolume()));
        return dto;
    }

    public CarrierCompartmentEntity toCarrierCompartment() {
        CarrierCompartmentEntity carrierCompartment = new CarrierCompartmentEntity();
        carrierCompartment.setId(this.id);
        carrierCompartment.setFreeSpace(this.freeSpace == null ? 100d : this.freeSpace);
        carrierCompartment.setMaximumWeight(this.maximumWeight);
        carrierCompartment.setVolume(this.volume.toDimensions());
        return carrierCompartment;
    }

}
