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
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TransporterDto {
    private Integer id;
    private String hubName;
    private List<CarrierCompartmentDto> compartments;
    @Enumerated
    private TransporterType type;

    public static TransporterDto toDto(Transporter transporter) {
        TransporterDto dto = new TransporterDto();
        dto.setId(transporter.getId());
        dto.setCompartments(transporter
                .getCompartments()
                .stream()
                .map(CarrierCompartmentDto::toDto)
                .collect(Collectors.toList()));
        dto.setHubName(transporter.getCurrentHub().getName());
        dto.setType(transporter.getType());
        return dto;
    }

    public Transporter toTransporter() {
        Transporter transporter = new Transporter();
        transporter.setId(id);
        List<CarrierCompartment> carrierCompartments = new ArrayList<>();
        for (CarrierCompartmentDto dto : compartments) {
            carrierCompartments.add(dto.toCarrierCompartment());
        }
        transporter.setCompartments(carrierCompartments);
        transporter.setType(type);
        Hub hub = new Hub();
        hub.setName(hubName);
        transporter.setCurrentHub(hub);
        return transporter;
    }
}
