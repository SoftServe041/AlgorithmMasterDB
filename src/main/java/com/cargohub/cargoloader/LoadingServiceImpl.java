package com.cargohub.cargoloader;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class LoadingServiceImpl {

    private CargoRepository cargoRepository;
    private HubRepository hubRepository;
    private CarrierCompartmentRepository carrierCompartmentRepository;
    private TransporterRepository transporterRepository;
    private OrderRepository orderRepository;
    private TransportDetailsRepository transportDetailsRepository;

    public LoadingServiceImpl(CargoRepository cargoRepository,
                              HubRepository hubRepository,
                              CarrierCompartmentRepository carrierCompartmentRepository,
                              TransporterRepository transporterRepository,
                              OrderRepository orderRepository,
                              TransportDetailsRepository transportDetailsRepository) {
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.transporterRepository = transporterRepository;
        this.orderRepository = orderRepository;
        this.transportDetailsRepository = transportDetailsRepository;
    }

    public List<OrderEntity> getAllOrdersByHub(String hubName) {
        HubEntity hub = hubRepository.findByName(hubName).orElseThrow(() -> {
            throw new HubException("Hub not found");
        });
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withMatcher("departure_hub", exact())
                .withMatcher("delivery_status", exact());
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDepartureHub(hub);
        orderEntity.setDeliveryStatus(DeliveryStatus.PROCESSING);
        Example<OrderEntity> example = Example.of(orderEntity, matcher);
        List<OrderEntity> result = orderRepository.findAll(example);
        if(result.size() == 0){
            throw new OrderException("No orders found by departure hub: " + hubName);
        }
        return result;
    }
}
