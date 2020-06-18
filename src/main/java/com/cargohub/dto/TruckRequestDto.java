package com.cargohub.dto;

import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.TransportType;
import com.cargohub.entities.transports.Truck;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TruckRequestDto {
    private String hubName;
    private List<CarrierCompartmentRequestDto> compartments;
    @Enumerated
    private TransportType type;

    public Truck toTruck() {
        Truck truck = new Truck();
        List<CarrierCompartment> carrierCompartments = new ArrayList<>();
        for (CarrierCompartmentRequestDto dto : compartments) {
            carrierCompartments.add(dto.toCarrierCompartment());
        }
        truck.setCompartments(carrierCompartments);
        truck.setType(type);
        Hub hub = new Hub();
        hub.setName(hubName);
        truck.setCurrentHub(hub);
        return truck;
    }
}
