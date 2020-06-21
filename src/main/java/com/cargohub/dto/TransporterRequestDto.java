package com.cargohub.dto;

import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TransporterRequestDto {
    private String hubName;
    private List<CarrierCompartmentRequestDto> compartments;
    @Enumerated
    private TransporterType type;

    public Transporter toTruck() {
        Transporter truck = new Transporter();
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
