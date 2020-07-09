package com.cargohub.cargoloader;

import com.cargohub.repository.*;
import org.springframework.stereotype.Service;

@Service
public class LoadingServiceImpl {

    private CargoRepository cargoRepository;
    private HubRepository hubRepository;
    private CarrierCompartmentRepository carrierCompartmentRepository;
    private TransporterRepository transporterRepository;
    private OrderRepository orderRepository;

    public LoadingServiceImpl(CargoRepository cargoRepository,
                              HubRepository hubRepository,
                              CarrierCompartmentRepository carrierCompartmentRepository,
                              TransporterRepository transporterRepository,
                              OrderRepository orderRepository) {
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.transporterRepository = transporterRepository;
        this.orderRepository = orderRepository;
    }



}
