package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.*;
import com.sun.javafx.scene.traversal.Hueristic2D;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            throw new HubException("Hub no found by name:" + hubName);
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
        if (result.size() == 0) {
            throw new OrderException("No orders found by departure hub: " + hub.getName());
        }
        return result;
    }

    public List<OrderEntity> getAllOrdersByRoute(RouteEntity route) {
        return orderRepository.findAllByRouteAndDeliveryStatus(route, DeliveryStatus.PROCESSING);
    }

    public List<TransporterEntity> loadAllTransportersInHub(HubEntity hub) {
        List<TransporterEntity> allTransporters = transporterRepository.findAllByCurrentHub(hub);
        List<OrderEntity> allOrders = getAllOrdersByHub(hub.getName());
        Map<String, List<OrderEntity>> ordersByArrivalHub = new HashMap<>();
        for (OrderEntity order : allOrders) {
            String hubName = order.getArrivalHub().getName();
            if (!ordersByArrivalHub.containsKey(hubName)) {
                ordersByArrivalHub.put(hubName, new ArrayList<>());
            }
            ordersByArrivalHub.get(hubName).add(order);
        }
        List<OrderEntity> maxVolumeList = findMaxVolumeListInMap(ordersByArrivalHub);

        return allTransporters;
    }

    private List<OrderEntity> findMaxVolumeListInMap(Map<String, List<OrderEntity>> ordersByArrivalHub) {
        List<OrderEntity> result = new ArrayList<>();
        double pastVolume = 0d;
        double currentVolume;
        for (List<OrderEntity> list : ordersByArrivalHub.values()) {
            currentVolume = computeOrderListVolume(list);
            if (currentVolume > pastVolume) {
                result = list;
            }
            pastVolume = currentVolume;
        }
        return result;
    }

    private double computeOrderListVolume(List<OrderEntity> list) {
        double volume = 0d;
        for (OrderEntity order : list) {
            for (CargoEntity cargo : order.getCargoEntities()) {
                DimensionsEntity dimensions = cargo.getDimensions();
                volume += dimensions.getHeight() * dimensions.getWidth() * dimensions.getLength();
            }
        }
        return volume;
    }

    private List<OrderEntity> findOrdersWithParticularRoute(List<OrderEntity> allOrders, RouteEntity route) {
        List<OrderEntity> result = new ArrayList<>();
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() < route.getHubs().size() && route.getHubs().containsAll(orderHubs)) {
                result.add(order);
            }
        }
        return result;
    }

}
