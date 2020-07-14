package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.OrderException;
import com.cargohub.exceptions.TransportDetailsException;
import com.cargohub.repository.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class LoadingServiceImpl {

    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;
    private final TransporterRepository transporterRepository;
    private final OrderRepository orderRepository;
    private final TransportDetailsRepository transportDetailsRepository;
    private final CargoLoader3D cargoLoader3D;

    public LoadingServiceImpl(CargoRepository cargoRepository,
                              HubRepository hubRepository,
                              CarrierCompartmentRepository carrierCompartmentRepository,
                              TransporterRepository transporterRepository,
                              OrderRepository orderRepository,
                              TransportDetailsRepository transportDetailsRepository, CargoLoader3D cargoLoader3D) {
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.transporterRepository = transporterRepository;
        this.orderRepository = orderRepository;
        this.transportDetailsRepository = transportDetailsRepository;
        this.cargoLoader3D = cargoLoader3D;
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

    public List<TransporterEntity> loadAllTransportersInHub(String hubName) {
        HubEntity hub = hubRepository.findByName(hubName).orElseThrow(() -> {
            throw new HubException("Hub not found");
        });
        List<TransporterEntity> allTransporters = transporterRepository.findAllByCurrentHub(hub);
        List<OrderEntity> allOrders = getAllOrdersByHub(hub.getName());
        Map<String, List<OrderEntity>> ordersByArrivalHub = new HashMap<>();
        for (OrderEntity order : allOrders) {
            String arrivalHubName = order.getArrivalHub().getName();
            if (!ordersByArrivalHub.containsKey(arrivalHubName)) {
                ordersByArrivalHub.put(arrivalHubName, new ArrayList<>());
            }
            ordersByArrivalHub.get(arrivalHubName).add(order);
        }
        //ToDo check on splitting & splitting functionality
        List<OrderEntity> maxVolumeList = findMaxVolumeListInMap(ordersByArrivalHub);
        double transporterVolume;
        for (TransporterEntity transporter : allTransporters) {
            transporterVolume = computeTransporterVolume(transporter);
            double cellSize = transportDetailsRepository.findByType(transporter.getType()).
                    orElseThrow(() -> {
                        throw new TransportDetailsException("TransportDetails not found");
                    }).getCellSize();
            if (transporter.getCompartments().size() == 1) {
                loadCompartment(transporter.getCompartments().get(0), maxVolumeList, allOrders, cellSize);
            }
        }
        return allTransporters;
    }

    private CarrierCompartmentEntity loadCompartment(CarrierCompartmentEntity compartment,
                                                     List<OrderEntity> maxVolumeList,
                                                     List<OrderEntity> allOrders,
                                                     double cellSize) {
        List<OrderEntity> ordersForLoading = optimizeOrdersForLoading(maxVolumeList, allOrders, compartment);
        List<Cargo> cargosForLoading = mapOrdersToCargos(ordersForLoading);
        CargoHold cargoHold = initializeCargoHold(compartment, cellSize);
        cargoLoader3D.loadCargo(cargosForLoading, ordersForLoading.get(0).getRoute(), cargoHold);
        List<Cargo> cargoList = cargoHold.getCargoList();
        List<CargoEntity> cargoEntities = postLoadingCargoProcessing(cargoList, ordersForLoading, compartment);
        compartment.setFreeSpace(0d);
        compartment.setCargoEntities(cargoEntities);
        carrierCompartmentRepository.save(compartment);
        return compartment;
    }

    private List<CargoEntity> postLoadingCargoProcessing(List<Cargo> cargoList, List<OrderEntity> ordersForLoading, CarrierCompartmentEntity compartment) {
        Map<Integer, CargoEntity> dataTransferMap = new HashMap<>();
        List<CargoEntity> cargoEntities = new ArrayList<>();
        ordersForLoading.forEach(
                orderEntity -> orderEntity.getCargoEntities()
                        .forEach(cargoEntity -> dataTransferMap.put(cargoEntity.getId(), cargoEntity))
        );
        saveCargosWithPositions(cargoList, compartment, dataTransferMap, cargoEntities);
        for (OrderEntity order : ordersForLoading) {
            order.setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
            orderRepository.save(order);
        }
        return cargoEntities;
    }

    private void saveCargosWithPositions(List<Cargo> cargoList, CarrierCompartmentEntity compartment, Map<Integer, CargoEntity> dataTransferMap, List<CargoEntity> cargoEntities) {
        for (Cargo cargo : cargoList) {
            CargoEntity cargoEntity = dataTransferMap.get(cargo.getId());
            CargoPositionEntity position = new CargoPositionEntity();
            position.setCargoEntity(cargoEntity);
            position.setHeightPos(cargo.getHeightPos());
            position.setWidthPos(cargo.getWidthPos());
            position.setLengthPos(cargo.getDepthPos());
            cargoEntity.setCargoPosition(position);
            cargoEntity.setCarrierCompartment(compartment);
            cargoEntity.setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
            cargoRepository.save(cargoEntity);
            cargoEntities.add(cargoEntity);
        }
    }

    private List<Cargo> mapOrdersToCargos(List<OrderEntity> ordersForLoading) {
        List<CargoEntity> cargoEntities = new ArrayList<>();
        ordersForLoading.forEach(orderEntity -> cargoEntities.addAll(orderEntity.getCargoEntities()));
        return cargoEntities.stream().map(Cargo::toCargo).collect(Collectors.toList());
    }

    private List<OrderEntity> optimizeOrdersForLoading(List<OrderEntity> maxVolumeList,
                                                       List<OrderEntity> allOrders,
                                                       CarrierCompartmentEntity compartment) {
        double compartmentVolume = computeCompartmentVolume(compartment) * 0.8; //80% of max volume
        double compartmentMaxWeight = compartment.getMaximumWeight() * 1000;
        double ordersVolume = computeOrderListVolume(maxVolumeList);
        double ordersWeight = computeOrderListWeight(maxVolumeList);
        RouteEntity route = maxVolumeList.get(0).getRoute();
        double accuracy = compartmentVolume * 0.05; //5% accuracy (5% of setup == 4% of total)
        //To be sure algo will load compartment total volume of Orders will not exceed 84% of compartment's volume
        if (!(Math.abs(compartmentVolume - ordersVolume) < accuracy && compartmentMaxWeight >= ordersWeight)) {
            List<OrderEntity> particularRouteOrders = findOrdersWithParticularRoute(allOrders, route);
            List<OrderEntity> largerRouteOrders = findOrdersWithLargerRoute(allOrders, route);
            double newOrdersVolume = ordersVolume;
            double newOrdersWeight = ordersWeight;
            for (OrderEntity particularOrder : particularRouteOrders) {
                newOrdersVolume += computeOrderVolume(particularOrder);
                newOrdersWeight += computeOrderWeight(particularOrder);
                maxVolumeList.add(particularOrder);
                if (Math.abs(compartmentVolume - newOrdersVolume) < accuracy && compartmentMaxWeight >= newOrdersWeight) {
                    return maxVolumeList;
                } else {
                    if (compartmentVolume < newOrdersVolume) {
                        newOrdersVolume -= computeOrderVolume(particularOrder);
                        newOrdersWeight -= computeOrderWeight(particularOrder);
                        maxVolumeList.remove(particularOrder);
                    }
                }
            }
            if(compartmentVolume < newOrdersVolume){
                for (OrderEntity particularOrder : largerRouteOrders) {
                    newOrdersVolume += computeOrderVolume(particularOrder);
                    newOrdersWeight += computeOrderWeight(particularOrder);
                    maxVolumeList.add(particularOrder);
                    if (Math.abs(compartmentVolume - newOrdersVolume) < accuracy && compartmentMaxWeight >= newOrdersWeight) {
                        return maxVolumeList;
                    } else {
                        if (compartmentVolume < newOrdersVolume) {
                            newOrdersVolume -= computeOrderVolume(particularOrder);
                            newOrdersWeight -= computeOrderWeight(particularOrder);
                            maxVolumeList.remove(particularOrder);
                        }
                    }
                }
            }
        }
        return maxVolumeList;
    }

    private CargoHold initializeCargoHold(CarrierCompartmentEntity compartment, double cellSize) {
        double width = compartment.getVolume().getWidth();
        double height = compartment.getVolume().getHeight();
        double length = compartment.getVolume().getLength();
        double maximumWeight = compartment.getMaximumWeight();
        //Got from previous version KHJ-71 branch commit 85eaf2de266f7fa76205964800f07e098e68b88b
        int[][][] loadMatrix = new int[(int) (length / cellSize)][(int) (height / cellSize)][(int) (width / cellSize)];
        return new CargoHold(width, height, length, (int) maximumWeight, loadMatrix);
    }

    private double computeTransporterVolume(TransporterEntity transporter) {
        DimensionsEntity dimensions;
        double result = 0d;
        for (CarrierCompartmentEntity compartment : transporter.getCompartments()) {
            dimensions = compartment.getVolume();
            result += dimensions.getHeight() * dimensions.getWidth() * dimensions.getLength();
        }
        return result;
    }

    private double computeCompartmentVolume(CarrierCompartmentEntity compartment) {
        DimensionsEntity dimensions = compartment.getVolume();
        return dimensions.getHeight() * dimensions.getWidth() * dimensions.getLength();
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
            volume += computeOrderVolume(order);
        }
        return volume;
    }

    private double computeOrderListWeight(List<OrderEntity> list) {
        double weight = 0d;
        for (OrderEntity order : list) {
            weight += computeOrderWeight(order);
        }
        return weight;
    }

    private double computeOrderVolume(OrderEntity order) {
        double volume = 0d;
        for (CargoEntity cargo : order.getCargoEntities()) {
            DimensionsEntity dimensions = cargo.getDimensions();
            volume += dimensions.getHeight() * dimensions.getWidth() * dimensions.getLength();
        }
        return volume;
    }

    private double computeOrderWeight(OrderEntity order) {
        double weight = 0d;
        for (CargoEntity cargo : order.getCargoEntities()) {
            weight += cargo.getWeight();
        }
        return weight;
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

    private List<OrderEntity> findOrdersWithLargerRoute(List<OrderEntity> allOrders, RouteEntity route) {
        List<OrderEntity> result = new ArrayList<>();
        RouteEntity firstMatchingRoute = null;
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() > route.getHubs().size() && orderHubs.containsAll(order.getRoute().getHubs())) {
                if(firstMatchingRoute == null){
                    firstMatchingRoute = order.getRoute();
                }
                if(order.getRoute().equals(firstMatchingRoute)){
                    result.add(order);
                }
            }
        }
        return result;
    }

}
