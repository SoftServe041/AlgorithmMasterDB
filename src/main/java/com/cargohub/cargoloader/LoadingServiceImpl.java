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

import java.util.*;
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
    private final CargoPositionRepository cargoPositionRepository;

    public LoadingServiceImpl(CargoRepository cargoRepository,
                              HubRepository hubRepository,
                              CarrierCompartmentRepository carrierCompartmentRepository,
                              TransporterRepository transporterRepository,
                              OrderRepository orderRepository,
                              TransportDetailsRepository transportDetailsRepository,
                              CargoLoader3D cargoLoader3D,
                              CargoPositionRepository cargoPositionRepository) {
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.transporterRepository = transporterRepository;
        this.orderRepository = orderRepository;
        this.transportDetailsRepository = transportDetailsRepository;
        this.cargoLoader3D = cargoLoader3D;
        this.cargoPositionRepository = cargoPositionRepository;
    }

    public List<OrderEntity> getAllOrdersByHub(String hubName) {
        HubEntity hub = hubRepository.findByName(hubName).orElseThrow(() -> {
            throw new HubException("Hub no found by name:" + hubName);
        });
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase()
                .withMatcher("departure_hub", exact()).withMatcher("delivery_status", exact());
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

    //TODO remove
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
        sortOrdersByRouteAndVolume(ordersByArrivalHub);

        for (TransporterEntity transporter : allTransporters) {
            double cellSize = transportDetailsRepository.findByType(transporter.getType()).orElseThrow(() -> {
                throw new TransportDetailsException("TransportDetails not found");
            }).getCellSize();
            loadCompartment(transporter.getCompartments().get(0), allOrders, ordersByArrivalHub, cellSize);
            //TODO removing orders from map
            for (Map.Entry<String, List<OrderEntity>> entry : ordersByArrivalHub.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (entry.getValue().get(i).getDeliveryStatus() == DeliveryStatus.ON_THE_WAY) {
                        allOrders.remove(entry.getValue().get(i));
                        entry.getValue().remove(i);
                        i--;
                    }
                }
            }
            if (allOrders.size() == 0) {
                break;
            }
        }
        return allTransporters;
    }

    private CarrierCompartmentEntity loadCompartment(CarrierCompartmentEntity compartment,
                                                     List<OrderEntity> allOrders,
                                                     Map<String, List<OrderEntity>> ordersByArrivalHub,
                                                     double cellSize) {
        List<OrderEntity> ordersForLoading = optimizeOrdersForLoading(ordersByArrivalHub, allOrders, compartment, cellSize);
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

    private List<CargoEntity> postLoadingCargoProcessing(List<Cargo> cargoList,
                                                         List<OrderEntity> ordersForLoading,
                                                         CarrierCompartmentEntity compartment) {
        Map<Integer, CargoEntity> dataTransferMap = new HashMap<>();
        List<CargoEntity> cargoEntities = new ArrayList<>();
        ordersForLoading.forEach(orderEntity -> orderEntity.getCargoEntities()
                .forEach(cargoEntity -> dataTransferMap.put(cargoEntity.getId(), cargoEntity)));
        saveCargosWithPositions(cargoList, compartment, dataTransferMap, cargoEntities);
        for (OrderEntity order : ordersForLoading) {
            order.setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
            orderRepository.save(order);
        }
        return cargoEntities;
    }

    private void saveCargosWithPositions(List<Cargo> cargoList,
                                         CarrierCompartmentEntity compartment,
                                         Map<Integer, CargoEntity> dataTransferMap,
                                         List<CargoEntity> cargoEntities) {
        for (Cargo cargo : cargoList) {
            CargoEntity cargoEntity = dataTransferMap.get(cargo.getId());
            CargoPositionEntity position = new CargoPositionEntity();
            position.setCargoEntity(cargoEntity);
            position.setHeightPos(cargo.getHeightPos());
            position.setWidthPos(cargo.getWidthPos());
            position.setLengthPos(cargo.getDepthPos());
            cargoPositionRepository.save(position);
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

    private List<OrderEntity> optimizeOrdersForLoading(Map<String, List<OrderEntity>> ordersByArrivalHub,
                                                       List<OrderEntity> allOrders,
                                                       CarrierCompartmentEntity compartment,
                                                       double cellSize) {
        RouteEntity route;
        List<OrderEntity> maxVolumeList = findMaxVolumeListInMap(ordersByArrivalHub);
        if (maxVolumeList.size() > 0) {
            route = maxVolumeList.get(0).getRoute();
        } else {
            throw new OrderException("Illegal state of Orders");
        }
        List<HubEntity> routeForTransporter = new ArrayList<>(route.getHubs());
        compartment.getTransporter().setRoute(routeForTransporter);
        double compartmentVolume = computeCompartmentVolume(compartment) * 0.9; // 90% of max volume
        double compartmentMaximumWeight = compartment.getMaximumWeight() * 1000d;
        List<OrderEntity> sameRouteOrders = findOrdersWithSameRoute(maxVolumeList, route);
        List<OrderEntity> particularRouteOrders = findOrdersWithParticularRoute(allOrders, route);
        List<OrderEntity> ordersForLoading = new ArrayList<>();
        double accuracy = computeCompartmentVolume(compartment) * 0.1; // 10% accuracy (10% of total)
        // To be sure algo will load compartment total volume of Orders will not exceed
        // 100% of compartment's volume
        List<OrderEntity> sameDirectionOrders = new ArrayList<>();
        sameDirectionOrders.addAll(sameRouteOrders);
        sameDirectionOrders.addAll(particularRouteOrders);
        for (OrderEntity order : sameDirectionOrders) {
            ordersForLoading.add(order);
            double ordersVolume = computeOrderListVolume(ordersForLoading);
            double ordersWeight = computeOrderListWeight(ordersForLoading);
            List<Cargo> cargosForLoading = mapOrdersToCargos(ordersForLoading);
            CargoHold cargoHold = initializeCargoHold(compartment, cellSize);
            if (cargoLoader3D.checkLoadingForOrder(cargosForLoading, cargoHold.getLoadingMatrix())) {
                if (Math.abs(compartmentVolume - ordersVolume) <= accuracy
                        && ordersWeight <= compartmentMaximumWeight) {
                    return ordersForLoading;
                } else {
                    if (ordersVolume > computeCompartmentVolume(compartment)
                            || ordersWeight > compartmentMaximumWeight) {
                        ordersForLoading.remove(order);
                    }
                }
            }
        }
        return ordersForLoading;
    }

    private CargoHold restoreCargoHold(CarrierCompartmentEntity compartment, double cellSize) {
        DimensionsEntity volume = compartment.getVolume();
        int depthInCells = (int) (volume.getLength() / cellSize);
        int heightInCells = (int) (volume.getHeight() / cellSize);
        int widthInCells = (int) (volume.getWidth() / cellSize);
        int[][][] loadingMatrix = new int[depthInCells][heightInCells][widthInCells];
        List<CargoEntity> cargoEntities = compartment.getCargoEntities();
        for (CargoEntity cargo : cargoEntities) {
            CargoPositionEntity cargoPosition = cargo.getCargoPosition();
            DimensionsEntity dimensions = cargo.getDimensions();
            int cargoDepthInCells = (int) (dimensions.getLength() / cellSize);
            int cargoHeightInCells = (int) (dimensions.getHeight() / cellSize);
            int cargoWidthInCells = (int) (dimensions.getWidth() / cellSize);
            for (int i = cargoPosition.getLengthPos(); i < cargoPosition.getLengthPos() + cargoDepthInCells; i++) {
                for (int j = cargoPosition.getHeightPos(); j < cargoPosition.getHeightPos() + cargoHeightInCells; j++) {
                    for (int k = cargoPosition.getWidthPos(); k < cargoPosition.getWidthPos() + cargoWidthInCells; k++) {
                        loadingMatrix[i][j][k] = 1;
                    }
                }
            }
        }
        return fillRestoringCargoHold(compartment, loadingMatrix);
    }

    private CargoHold fillRestoringCargoHold(CarrierCompartmentEntity compartment, int[][][] loadingMatrix) {
        CargoHold cargoHold = new CargoHold(compartment.getVolume().getWidth(),
                compartment.getVolume().getHeight(),
                compartment.getVolume().getLength(),
                compartment.getMaximumWeight().intValue() * 1000,
                loadingMatrix);
        List<CargoEntity> cargoEntities = compartment.getCargoEntities();
        for (CargoEntity cargoEntity : cargoEntities) {
            if(!cargoHold.getLoadedCargo().containsKey(cargoEntity.getFinalDestination())){
                cargoHold.getLoadedCargo().put(cargoEntity.getFinalDestination(), new ArrayList<>());
            }
            cargoHold.getLoadedCargo().get(cargoEntity.getFinalDestination()).add(Cargo.toCargo(cargoEntity));
        }
        return cargoHold;
    }

    //TODO remove this
    private boolean fillOrdersForLoadingList(CarrierCompartmentEntity compartment,
                                             List<OrderEntity> sameRouteOrders,
                                             List<OrderEntity> ordersForLoading) {
        double compartmentVolume = computeCompartmentVolume(compartment) * 0.8; // 80% of max volume
        double compartmentMaxWeight = compartment.getMaximumWeight() * 1000;
        double accuracy = compartmentVolume * 0.05; // 5% accuracy (5% of setup == 4% of total)
        double ordersVolume;
        double ordersWeight;
        for (OrderEntity order : sameRouteOrders) {
            ordersForLoading.add(order);
            ordersVolume = computeOrderListVolume(ordersForLoading);
            ordersWeight = computeOrderListWeight(ordersForLoading);
            if (Math.abs(compartmentVolume - ordersVolume) < accuracy && ordersWeight <= compartmentMaxWeight) {
                return true;
            } else {
                if (compartmentVolume < ordersVolume || ordersWeight <= compartmentMaxWeight) {
                    ordersForLoading.remove(order);
                }
            }
        }
        return false;
    }

    private CargoHold initializeCargoHold(CarrierCompartmentEntity compartment, double cellSize) {
        double width = compartment.getVolume().getWidth();
        double height = compartment.getVolume().getHeight();
        double length = compartment.getVolume().getLength();
        double maximumWeight = compartment.getMaximumWeight();
        // Got from previous version KHJ-71 branch commit
        // 85eaf2de266f7fa76205964800f07e098e68b88b
        int[][][] loadMatrix = new int[(int) (length / cellSize)][(int) (height / cellSize)][(int) (width / cellSize)];
        return new CargoHold(width, height, length, (int) maximumWeight, loadMatrix);
    }

    //TODO remove this
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
        boolean contains = true;
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() < route.getHubs().size()) {
                for (int i = 0; i < orderHubs.size(); i++) {
                    if (!orderHubs.get(i).getName().equals(route.getHubs().get(i).getName())) {
                        contains = false;
                    }
                }
                if (contains) {
                    result.add(order);
                }
            }
        }
        return result;
    }

    private List<OrderEntity> findOrdersWithSameRoute(List<OrderEntity> allOrders, RouteEntity route) {
        List<OrderEntity> result = new ArrayList<>();
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() == route.getHubs().size() && route.getHubs().containsAll(orderHubs)) {
                result.add(order);
            }
        }
        return result;
    }

    //TODO remove this
    private List<OrderEntity> findOrdersWithLargerRoute(List<OrderEntity> allOrders, RouteEntity route) {
        List<OrderEntity> result = new ArrayList<>();
        RouteEntity firstMatchingRoute = null;
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() > route.getHubs().size() && orderHubs.containsAll(order.getRoute().getHubs())) {
                if (firstMatchingRoute == null) {
                    firstMatchingRoute = order.getRoute();
                }
                if (order.getRoute().equals(firstMatchingRoute)) {
                    result.add(order);
                }
            }
        }
        return result;
    }

    // TODO: Check comparators
    // Create multiple comparator
    class OrderChainedComparator implements Comparator<OrderEntity> {

        private List<Comparator<OrderEntity>> listComparators;

        @SafeVarargs
        public OrderChainedComparator(Comparator<OrderEntity>... comparators) {
            this.listComparators = Arrays.asList(comparators);
        }

        @Override
        public int compare(OrderEntity order1, OrderEntity order2) {
            for (Comparator<OrderEntity> comparator : listComparators) {
                int result = comparator.compare(order1, order2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }

    // Create comparators
    class OrderRouteSizeComparator implements Comparator<OrderEntity> {
        @Override
        public int compare(OrderEntity order1, OrderEntity order2) {
            return order2.getRoute().getHubs().size() - order1.getRoute().getHubs().size();
        }
    }

    class OrderVolumeComparator implements Comparator<OrderEntity> {
        @Override
        public int compare(OrderEntity order1, OrderEntity order2) {
            return Double.compare(computeOrderVolume(order2), computeOrderVolume(order1));
        }
    }

    // TODO: Check this method
    // Sort cargo by route and volume from largest route and biggest volume to
    // shortest
    // route and smallest volume
    void sortOrdersByRouteAndVolume(Map<String, List<OrderEntity>> ordersByArrivalHub) {
        for (Map.Entry<String, List<OrderEntity>> entry : ordersByArrivalHub.entrySet()) {
            entry.getValue()
                    .sort(new OrderChainedComparator(new OrderRouteSizeComparator(), new OrderVolumeComparator()));
        }
    }
}
