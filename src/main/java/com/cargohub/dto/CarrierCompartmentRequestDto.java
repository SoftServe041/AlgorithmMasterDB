package com.cargohub.dto;

import com.cargohub.entities.transports.CarrierCompartment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarrierCompartmentRequestDto {
    private Double maximumWeight;
    private DimensionsRequestDto volume;

    public CarrierCompartment toCarrierCompartment() {
        CarrierCompartment carrierCompartment = new CarrierCompartment();
        carrierCompartment.setMaximumWeight(this.maximumWeight);
        carrierCompartment.setVolume(this.volume.toDimensions());
        return carrierCompartment;
    }

}
