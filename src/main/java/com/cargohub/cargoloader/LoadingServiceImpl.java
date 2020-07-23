package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.entities.enums.TransporterStatus;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.OrderException;
import com.cargohub.exceptions.TransportDetailsException;
import com.cargohub.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Slf4j
@Service
@Transactional
public class LoadingServiceImpl {
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;
    private final TransporterRepository transporterRepository;
    private final OrderRepository orderRepository;
    private final TransportDetailsRepository transportDetailsRepository;
    private final CargoLoader3D cargoLoader3D;
    private final CargoPositionRepository cargoPositionRepository;
    private final RouteRepository routeRepository;
    private final static String DEMO = "<====================== D E M O ======================>\n";

    public LoadingServiceImpl(CargoRepository cargoRepository, HubRepository hubRepository,
                              CarrierCompartmentRepository carrierCompartmentRepository, TransporterRepository transporterRepository,
                              OrderRepository orderRepository, TransportDetailsRepository transportDetailsRepository,
                              CargoLoader3D cargoLoader3D, CargoPositionRepository cargoPositionRepository, RouteRepository routeRepository) {
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.transporterRepository = transporterRepository;
        this.orderRepository = orderRepository;
        this.transportDetailsRepository = transportDetailsRepository;
        this.cargoLoader3D = cargoLoader3D;
        this.cargoPositionRepository = cargoPositionRepository;
        this.routeRepository = routeRepository;
    }

    public void loadAllTransportersInHub(String hubName) {
        HubEntity hub = hubRepository.findByName(hubName).orElseThrow(() -> {
            throw new HubException("Hub not found");
        });
        List<TransporterEntity> allTransporters = transporterRepository.
                findAllByCurrentHubAndStatus(hub, TransporterStatus.WAITING);
        List<OrderEntity> allOrders = getAllOrdersByHub(hub.getName());
        Map<String, List<OrderEntity>> ordersByArrivalHub = formOrdersByArrivalHubMap(allOrders);
        for (TransporterEntity transporter : allTransporters) {
            double cellSize = transportDetailsRepository.findByType(transporter.getType()).orElseThrow(() -> {
                throw new TransportDetailsException("TransportDetails not found");
            }).getCellSize();
            //All next methods should be synchronized

            loadCompartment(transporter.getCompartments().get(0), allOrders, ordersByArrivalHub, cellSize);
            //Initialize thread for each transporter,
            //set for transporter as current hub the next hub in the route
            //sleep thread for time = estimated delivery time - current time;
            //unload + load new orders,
            // next loop step
            Thread thread = new Thread(() -> {
                int countHub = 0;
                while (countHub < transporter.getRoute().size() - 1) {
                    StringBuilder routelog = new StringBuilder("Route = [ ");
                    for (HubEntity h : transporter.getRoute()) {
                        routelog.append(h.getName()).append(", ");
                    }
                    routelog.append("]");
                    log.info(routelog.toString());
                    log.info(DEMO + " Transporter " + transporter.getId() + " left "
                            + transporter.getCurrentHub().getName() + " on the way to "
                            + transporter.getRoute().get(countHub + 1).getName());
                    transporter.setCurrentHub(transporter.getRoute().get(countHub + 1));
                    List<CargoEntity> cargoEntities = transporter.getCompartments().get(0).getCargoEntities();
                    Date deliveryDate = getNextArrivalDate(cargoEntities, transporter.getCurrentHub());
                    long milliseconds = deliveryDate.getTime() - new Date().getTime();
                    if (milliseconds > 0) {
                        try {
                            Thread.sleep(milliseconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info(DEMO + " Transporter " + transporter.getId()
                            + " is starting unload in " + transporter.getCurrentHub().getName());
                    countHub++;
                    if (countHub < transporter.getRoute().size() - 1) {
                        unloadAndFillUpCompartment(transporter.getCompartments().get(0), cellSize);
                        log.info(DEMO + " Transporter " + transporter.getId()
                                + " is unloaded and loaded in " + transporter.getCurrentHub().getName());
                    } else {
                        log.info(DEMO + " Transporter " + transporter.getId()
                                + " is unloaded and reached final destination " + transporter.getCurrentHub().getName());
                        saveTransporterReachedFinalDestination(transporter);
                    }
                }
            });
            thread.start();
            allOrders = getAllOrdersByHub(hub.getName());
            ordersByArrivalHub = formOrdersByArrivalHubMap(allOrders);
            if (allOrders.size() == 0) {
                break;
            }
        }
    }

    private synchronized void saveTransporterReachedFinalDestination(TransporterEntity transporter) {
        transporter.setStatus(TransporterStatus.WAITING);
        CarrierCompartmentEntity compartment = transporter.getCompartments().get(0);
        compartment.setFreeSpace(100d);
        List<CargoEntity> cargoEntities = compartment.getCargoEntities();
        compartment.setCargoEntities(null);
        List<OrderEntity> orders = new ArrayList<>();
        for (CargoEntity cargo : cargoEntities) {
            if(!orders.contains(cargo.getOrderEntity())){
                cargo.getOrderEntity().setDeliveryStatus(DeliveryStatus.DELIVERED);
                orders.add(cargo.getOrderEntity());
            }
            cargo.setDeliveryStatus(DeliveryStatus.DELIVERED);
            cargo.setCarrierCompartment(null);
        }
        transporterRepository.save(transporter);
        carrierCompartmentRepository.save(compartment);
        orderRepository.saveAll(orders);
        cargoRepository.saveAll(cargoEntities);
    }

    private Date getNextArrivalDate(List<CargoEntity> cargoEntities, HubEntity nextHub) {
        String nextHubName = nextHub.getName();
        OrderEntity nextHubOrder = null;
        for (CargoEntity cargo : cargoEntities) {
            if (cargo.getOrderEntity().getArrivalHub().getName().equals(nextHubName)) {
                nextHubOrder = cargo.getOrderEntity();
                break;
            }
        }
        Date result = new Date();
        if (nextHubOrder != null) {
            result = nextHubOrder.getEstimatedDeliveryDate();
        }
        return result;
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

    private Map<String, List<OrderEntity>> formOrdersByArrivalHubMap(List<OrderEntity> allOrders) {
        Map<String, List<OrderEntity>> ordersByArrivalHub = new HashMap<>();
        for (OrderEntity order : allOrders) {
            String arrivalHubName = order.getArrivalHub().getName();
            if (!ordersByArrivalHub.containsKey(arrivalHubName)) {
                ordersByArrivalHub.put(arrivalHubName, new ArrayList<>());
            }
            ordersByArrivalHub.get(arrivalHubName).add(order);
        }
        sortOrdersByRouteAndVolume(ordersByArrivalHub);
        return ordersByArrivalHub;
    }

    private synchronized void loadCompartment(CarrierCompartmentEntity compartment,
                                              List<OrderEntity> allOrders,
                                              Map<String, List<OrderEntity>> ordersByArrivalHub,
                                              double cellSize) {
        List<OrderEntity> ordersForLoading = optimizeOrdersForFirstLoading(ordersByArrivalHub, allOrders, compartment,
                cellSize);
        log.info(" Transporter " + compartment.getTransporter().getId() + " loaded " + computeOrderListVolume(ordersForLoading));
        List<Cargo> cargosForLoading = mapOrdersToCargos(ordersForLoading);
        CargoHold cargoHold = restoreCargoHoldState(compartment, cellSize);
        cargoLoader3D.loadCargo(cargosForLoading, ordersForLoading.get(0).getRoute(), cargoHold);
        List<Cargo> cargoList = cargoHold.getCargoList();
        List<CargoEntity> cargoEntities = postLoadingCargoProcessing(cargoList, ordersForLoading, compartment);
        compartment.setFreeSpace(0d);
        compartment.setCargoEntities(cargoEntities);
        compartment.getTransporter().setStatus(TransporterStatus.ON_THE_WAY);
        carrierCompartmentRepository.save(compartment);
    }

    private synchronized void unloadAndFillUpCompartment(CarrierCompartmentEntity compartment, double cellSize) {
        String currentHubName = compartment.getTransporter().getCurrentHub().getName();
        List<CargoEntity> allCarrierCargos = compartment.getCargoEntities();
        List<CargoEntity> arrivedCargos = allCarrierCargos.stream().
                filter(cargoEntity -> cargoEntity.getFinalDestination().equals(currentHubName)).
                collect(Collectors.toList());
        compartment.getCargoEntities().removeAll(arrivedCargos);
        List<OrderEntity> arrivedOrders = new ArrayList<>();
        arrivedCargos.forEach(cargo -> {
            if (!arrivedOrders.contains(cargo.getOrderEntity())) {
                arrivedOrders.add(cargo.getOrderEntity());
            }
        });
        if (arrivedOrders.size() > 0) {
            freeArrivedOrders(arrivedOrders);
            fillUpUnloadedCompartment(compartment, cellSize);
        }
    }

    private void fillUpUnloadedCompartment(CarrierCompartmentEntity compartment, double cellSize) {
        String currentHubName = compartment.getTransporter().getCurrentHub().getName();
        List<OrderEntity> allOrdersByHub = getAllOrdersByHub(currentHubName);
        if (allOrdersByHub.size() > 0) {
            List<OrderEntity> compartmentOrders = getCompartmentOrders(compartment);
            List<HubEntity> transporterRoute = compartment.getTransporter().getRoute();
            RouteEntity restOfTheRoute = new RouteEntity();
            for (int i = 0; i < compartment.getTransporter().getRoute().size(); i++) {
                if (transporterRoute.get(i).getName().equals(currentHubName)) {
                    restOfTheRoute.setHubs(transporterRoute.subList(i, transporterRoute.size()));
                }
            }
            List<OrderEntity> ordersForTransitLoading =
                    optimizeOrdersForTransitLoading(allOrdersByHub, compartment, compartmentOrders,
                            restOfTheRoute, cellSize);
            if (ordersForTransitLoading.size() > 0) {
                loadAndSaveTransitCargos(compartment, cellSize, restOfTheRoute, ordersForTransitLoading);
            }
        }
    }

    private void loadAndSaveTransitCargos(CarrierCompartmentEntity compartment,
                                          double cellSize,
                                          RouteEntity restOfTheRoute,
                                          List<OrderEntity> ordersForTransitLoading) {
        CargoHold cargoHold = restoreCargoHoldState(compartment, cellSize);
        List<Cargo> cargosForLoading = mapOrdersToCargos(ordersForTransitLoading);
        cargoLoader3D.loadCargo(cargosForLoading, restOfTheRoute, cargoHold);
        List<Integer> loadedCargosIds = cargosForLoading.stream().map(Cargo::getId).collect(Collectors.toList());
        //getCargoList returns both boxes which were loaded earlier and boxes loaded in this method,
        //so we need to process only those ones which we have loaded yet
        List<Cargo> loadedCargoList = cargoHold.getCargoList().stream().
                filter(cargo -> loadedCargosIds.contains(cargo.getId())).
                collect(Collectors.toList());
        List<CargoEntity> cargoEntities =
                postLoadingCargoProcessing(loadedCargoList, ordersForTransitLoading, compartment);
        compartment.setFreeSpace(0d);
        compartment.getCargoEntities().addAll(cargoEntities);
        compartment.getTransporter().setStatus(TransporterStatus.ON_THE_WAY);
        carrierCompartmentRepository.save(compartment);
    }

    private List<OrderEntity> getCompartmentOrders(CarrierCompartmentEntity compartment) {
        List<CargoEntity> compartmentCargos = compartment.getCargoEntities();
        List<OrderEntity> compartmentOrders = new ArrayList<>();
        compartmentCargos.forEach(cargoEntity -> {
            OrderEntity orderEntity = cargoEntity.getOrderEntity();
            if (!compartmentOrders.contains(orderEntity)) {
                compartmentOrders.add(orderEntity);
            }
        });
        return compartmentOrders;
    }

    private List<OrderEntity> optimizeOrdersForTransitLoading(List<OrderEntity> allOrders,
                                                              CarrierCompartmentEntity compartment,
                                                              List<OrderEntity> compartmentOrders,
                                                              RouteEntity route,
                                                              double cellSize) {

        double freeVolume = computeCompartmentVolume(compartment) - computeOrderListVolume(compartmentOrders);
        double freeWeight = compartment.getMaximumWeight() * 1000d - computeOrderListWeight(compartmentOrders);
        List<OrderEntity> sameRouteOrders = findOrdersWithSameRoute(allOrders, route);
        List<OrderEntity> particularRouteOrders = findOrdersWithParticularRoute(allOrders, route);
        List<OrderEntity> ordersForLoading = new ArrayList<>();
        double accuracy = freeVolume * 0.1;
        List<OrderEntity> sameDirectionOrders = new ArrayList<>(sameRouteOrders);
        sameDirectionOrders.addAll(particularRouteOrders);
        for (OrderEntity order : sameDirectionOrders) {
            ordersForLoading.add(order);
            double ordersVolume = computeOrderListVolume(ordersForLoading);
            double ordersWeight = computeOrderListWeight(ordersForLoading);
            List<Cargo> cargosForLoading = mapOrdersToCargos(ordersForLoading);
            CargoHold cargoHold = restoreCargoHoldState(compartment, cellSize);
            if (cargoLoader3D.checkLoadingForOrder(cargosForLoading, cargoHold.getLoadingMatrix())) {
                if (Math.abs(freeVolume * 0.9 - ordersVolume) <= accuracy && ordersWeight <= freeWeight) {
                    return ordersForLoading;
                }
            } else {
                if (ordersVolume > freeVolume || ordersWeight > freeWeight) {
                    ordersForLoading.remove(order);
                }
            }
        }
        return ordersForLoading;
    }

    private List<OrderEntity> optimizeOrdersForFirstLoading(Map<String, List<OrderEntity>> ordersByArrivalHub,
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
        System.out.println("maxVolumeList.get(0).getArrivalHub() = " + maxVolumeList.get(0).getArrivalHub());
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
            CargoHold cargoHold = restoreCargoHoldState(compartment, cellSize);
            if (cargoLoader3D.checkLoadingForOrder(cargosForLoading, cargoHold.getLoadingMatrix())) {
                if (Math.abs(compartmentVolume - ordersVolume) <= accuracy
                        && ordersWeight <= compartmentMaximumWeight) {
                    return ordersForLoading;
                }
            } else {
                if (ordersVolume > computeCompartmentVolume(compartment)
                        || ordersWeight > compartmentMaximumWeight) {
                    ordersForLoading.remove(order);
                }
            }
        }
        return ordersForLoading;
    }

    private void freeArrivedOrders(List<OrderEntity> arrivedOrders) {
        arrivedOrders.forEach(arrivedOrder -> {
            arrivedOrder.setDeliveryStatus(DeliveryStatus.DELIVERED);
            arrivedOrder.getCargoEntities().forEach(cargoEntity -> {
                cargoEntity.setDeliveryStatus(DeliveryStatus.DELIVERED);
                cargoEntity.setCarrierCompartment(null);
                cargoEntity.setCargoPosition(null);
            });
            orderRepository.save(arrivedOrder);
        });
    }

    private synchronized List<CargoEntity> postLoadingCargoProcessing(List<Cargo> cargoList,
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

    public void saveCargosWithPositions(List<Cargo> cargoList, CarrierCompartmentEntity compartment,
                                        Map<Integer, CargoEntity> dataTransferMap, List<CargoEntity> cargoEntities) {
        for (Cargo cargo : cargoList) {
            CargoEntity cargoEntity = dataTransferMap.get(cargo.getId());
            CargoPositionEntity position = new CargoPositionEntity();
            position.setCargoEntity(cargoEntity);
            position.setHeightPos(cargo.getHeightPos());
            position.setWidthPos(cargo.getWidthPos());
            position.setLengthPos(cargo.getDepthPos());
            position = cargoPositionRepository.save(position);
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

    private CargoHold restoreCargoHoldState(CarrierCompartmentEntity compartment, double cellSize) {
        DimensionsEntity volume = compartment.getVolume();
        int heightInCells = (int) (volume.getHeight() / cellSize);
        int depthInCells = (int) (volume.getLength() / cellSize);
        int widthInCells = (int) (volume.getWidth() / cellSize);
        int[][][] loadingMatrix = new int[depthInCells][heightInCells][widthInCells];
        List<CargoEntity> cargoEntities = compartment.getCargoEntities();
        if (cargoEntities != null && cargoEntities.size() != 0) {
            for (CargoEntity cargo : cargoEntities) {
                CargoPositionEntity cargoPosition = cargo.getCargoPosition();
                DimensionsEntity dimensions = cargo.getDimensions();
                int cargoDepthInCells = (int) (dimensions.getLength() / cellSize);
                int cargoHeightInCells = (int) (dimensions.getHeight() / cellSize);
                int cargoWidthInCells = (int) (dimensions.getWidth() / cellSize);
                for (int i = cargoPosition.getLengthPos(); i < cargoPosition.getLengthPos() + cargoDepthInCells; i++) {
                    for (int j = cargoPosition.getHeightPos(); j < cargoPosition.getHeightPos()
                            + cargoHeightInCells; j++) {
                        for (int k = cargoPosition.getWidthPos(); k < cargoPosition.getWidthPos()
                                + cargoWidthInCells; k++) {
                            loadingMatrix[i][j][k] = 1;
                        }
                    }
                }
            }
        }
        return fillRestoringCargoHold(compartment, loadingMatrix);
    }

    private CargoHold fillRestoringCargoHold(CarrierCompartmentEntity compartment, int[][][] loadingMatrix) {
        CargoHold cargoHold = new CargoHold(compartment.getVolume().getWidth(), compartment.getVolume().getHeight(),
                compartment.getVolume().getLength(), compartment.getMaximumWeight().intValue() * 1000, loadingMatrix);
        List<CargoEntity> cargoEntities = compartment.getCargoEntities();
        if (cargoEntities != null && cargoEntities.size() != 0) {
            for (CargoEntity cargoEntity : cargoEntities) {
                if (!cargoHold.getLoadedCargo().containsKey(cargoEntity.getFinalDestination())) {
                    cargoHold.getLoadedCargo().put(cargoEntity.getFinalDestination(), new ArrayList<>());
                }
                cargoHold.getLoadedCargo().get(cargoEntity.getFinalDestination()).add(Cargo.toCargo(cargoEntity));
            }
        }
        return cargoHold;
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
                pastVolume = currentVolume;
            }
        }
        return result;
    }

    public double computeOrderListVolume(List<OrderEntity> list) {
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
                        break;
                    }
                }
                if (contains) {
                    result.add(order);
                }
            }
            contains = true;
        }
        return result;
    }

    public List<OrderEntity> findOrdersWithSameRoute(List<OrderEntity> allOrders, RouteEntity route) {
        List<OrderEntity> result = new ArrayList<>();
        for (OrderEntity order : allOrders) {
            List<HubEntity> orderHubs = order.getRoute().getHubs();
            if (orderHubs.size() == route.getHubs().size() && route.getHubs().containsAll(orderHubs)) {
                result.add(order);
            }
        }
        return result;
    }

    public void clearDatabaseAfterSimulation() {
        cargoRepository.deleteAll();
        transporterRepository.deleteAll();
        routeRepository.deleteAll();
        orderRepository.deleteAll();
        cargoPositionRepository.deleteAll();
    }

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
