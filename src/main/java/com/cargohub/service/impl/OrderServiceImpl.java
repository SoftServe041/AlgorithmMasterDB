package com.cargohub.service.impl;

import com.cargohub.cargoloader.pathfinder.entities.Route;
import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.*;
import com.cargohub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Random;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final DimensionsRepository dimensionsRepository;
    private final RouteRepository routeRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public OrderServiceImpl(OrderRepository repository,
                            CargoRepository cargoRepository,
                            HubRepository hubRepository,
                            DimensionsRepository dimensionsRepository,
                            RouteRepository routeRepository
                            ) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.dimensionsRepository = dimensionsRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public OrderEntity findById(Integer id) {
        OrderEntity result;
        result = repository.findById(id).orElseThrow(() -> new OrderException("Order not found"));
        return result;
    }

    @Override
    public Page<OrderEntity> findAllByUserId(Integer userid, Pageable pageable) {

        Page<OrderEntity> orderPage = repository.findAllByUserId(userid, pageable);

        if (orderPage == null) {
            throw new OrderException("No record found");
        }
        return orderPage;
    }

    @Override
    public OrderEntity update(OrderEntity orderEntity) {
        if (orderEntity.getId() == null) {
            throw new OrderException("Illegal state for Order");
        }
        if (existsById(orderEntity.getId())) {
            return repository.save(orderEntity);
        }
        throw new OrderException("Order not found");
    }

    @Override
    public OrderEntity save(OrderEntity orderEntity) {

        if (orderEntity.getId() != null) {
            throw new OrderException("Illegal state for Order");
        }
        orderEntity.setArrivalHub(getRealHubFromName(orderEntity.getArrivalHub()));
        orderEntity.setDepartureHub(getRealHubFromName(orderEntity.getDepartureHub()));

        for (CargoEntity cargo : orderEntity.getCargoEntities()
        ) {
            dimensionsRepository.save(cargo.getDimensions());
            cargoRepository.save(cargo);
        }

        getRealHubsForRoute(orderEntity);
        RouteEntity route = orderEntity.getRoute();
        Optional<RouteEntity> optional = routeRepository.findByHubsIn(route.getHubs());
        if(optional.isPresent()){
            route = optional.get();
        }
        RouteEntity savedRoute = routeRepository.save(route);

        orderEntity.setRoute(savedRoute);
        OrderEntity returnEntity = repository.save(orderEntity);
        List<CargoEntity> cargoList = orderEntity.getCargoEntities();
        cargoList.forEach(x -> x.setOrderEntity(orderEntity));
        cargoList.forEach(cargoRepository::save);

        savedRoute.getOrder().add(orderEntity);
        routeRepository.save(savedRoute);

        return returnEntity;
    }

    private void getRealHubsForRoute(OrderEntity orderEntity) {
        RouteEntity route = orderEntity.getRoute();
        route.setOrder(new ArrayList<>());
        List<HubEntity> list = new ArrayList<>();
        for(HubEntity hub : route.getHubs()){
            Optional<HubEntity> optional = hubRepository.findByName(hub.getName());
            if(optional.isPresent()) {
                list.add(optional.get());
            }
            else{
                throw new IllegalArgumentException("No such hub");
            }

        }
        route.setHubs(list);
    }

    private HubEntity getRealHubFromName(HubEntity hub){
        Optional<HubEntity> optional = hubRepository.findByName(hub.getName());
        if(optional.isPresent())
        return optional.get();
        else{
            throw new IllegalArgumentException("No such hub");
        }
    }


    @Override
    public Page<OrderEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new OrderException("Cargo not found");
    }

    @Override
    public void simulate() {
        Random random = new Random();
        List<OrderEntity> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new OrderEntity());
        }

        RouteEntity route;
        List<HubEntity> hubList;
        setHubs(list.get(0), "Kharkiv", "Poltava");
        setHubsForRoute(list.get(0), "Myrgorod");

        setHubs(list.get(1), "Poltava", "Kharkiv");
        setHubsForRoute(list.get(1), "Myrgorod");

        setHubs(list.get(2), "Myrgorod", "Kharkiv");
        setHubsForRoute(list.get(2), "Poltava");

        setHubs(list.get(3), "Kharkiv", "Myrgorod");
        setHubsForRoute(list.get(3), "Poltava");

        setHubs(list.get(4), "Kyiv", "Lviv");
        setHubsForRoute(list.get(4), "Kharkiv");

        setHubs(list.get(5), "Lviv", "Kyiv");
        setHubsForRoute(list.get(5), "Kharkiv");

        setHubs(list.get(6), "Kyiv", "Odesa");
        setHubsForRoute(list.get(6), "Pavlograd");

        setHubs(list.get(7), "Odesa", "Kyiv");
        setHubsForRoute(list.get(7), "Pavlograd");

        setHubs(list.get(8), "Odesa", "Sumy");
        setHubsForRoute(list.get(8), "Kyiv");

        list.forEach(x -> x.setUserId(1));
        list.forEach(x -> x.setPrice(1000.0));
        list.forEach(x -> {
            try {
                x.setEstimatedDeliveryDate(new SimpleDateFormat("yyyyMMdd").parse("20200820"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        int counter = 0;
        for(OrderEntity order : list){
            order.setTrackingId("ch" + random.nextInt(100) + counter++ + order.getArrivalHub().getName().hashCode()+ random.nextInt(1000));
        }
        for (int i = 0; i < 3; i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.PROCESSING);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.08.20 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 3; i < 6; i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.07.10 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 6; i < list.size(); i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.DELIVERED);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.06.15 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        list.forEach(this::setCargo);
        list.forEach(this::save);
    }
    private void setHubsForRoute(OrderEntity order,String middleHub){
        RouteEntity route = new RouteEntity();
        List<HubEntity> hubList = new ArrayList<>();
        hubList.add(order.getDepartureHub());
        HubEntity hubEntity = new HubEntity();
        hubEntity.setName(middleHub);
        hubList.add(hubEntity);
        hubList.add(order.getArrivalHub());
        route.setHubs(hubList);
        order.setRoute(route);
    }

    private void setCargo(OrderEntity orderEntity) {
        List<CargoEntity> cargoList = new ArrayList<>();
        Random random = new Random();
        int randCargosNumber = 5 + random.nextInt(6);
        int randWeight = 100;
        for (int i = 0; i < randCargosNumber; i++) {
            CargoEntity cargo = new CargoEntity();
            cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
            cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
            cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
            cargo.setWeight((double) random.nextInt(randWeight+100));
            cargo.setDimensions(getRandDimensions());
            cargoList.add(cargo);
        }
        orderEntity.setCargoEntities(cargoList);
    }

    private DimensionsEntity getRandDimensions() {
        Random random = new Random();
        int max = 10;
        int rand = random.nextInt(max);
        switch (rand) {
            case 0: {
              return returnSetDimensions(0.3, 0.6,0.6);
            }
            case 1: {
                return returnSetDimensions(0.6, 0.6,0.6);
            }
            case 2: {
                return returnSetDimensions(0.9, 0.9,0.9);
            }
            case 3: {
                return returnSetDimensions(0.9, 1.2,1.5);
            }
            case 4: {
                return returnSetDimensions(1.5, 1.5,1.8);
            }
            case 5: {
                return returnSetDimensions(1.8, 1.8,1.8);
            }
            case 6: {
                return returnSetDimensions(2.1, 1.8,2.4);
            }
            case 7: {
                return returnSetDimensions(2.1, 2.1,2.4);
            }
            case 8: {
                return returnSetDimensions(2.4, 2.1,2.4);
            }
            case 9: {
                return returnSetDimensions(2.4, 2.4,2.4);
            }
        }
         return null;
    }
    private DimensionsEntity returnSetDimensions(double height, double width, double length){
        DimensionsEntity dimensions = new DimensionsEntity();
        dimensions.setHeight(height);
        dimensions.setWidth(width);
        dimensions.setLength(length);
        return dimensions;
    }

    private void setHubs(OrderEntity order, String dep, String arr) {
        HubEntity depHub = new HubEntity();
        HubEntity arrHub = new HubEntity();
        depHub.setName(dep);
        arrHub.setName(arr);
        order.setArrivalHub(arrHub);
        order.setDepartureHub(depHub);
    }
}
