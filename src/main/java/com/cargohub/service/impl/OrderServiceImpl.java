package com.cargohub.service.impl;

import com.cargohub.cargoloader.OrderSimulation;
import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.RouteEntity;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.*;
import com.cargohub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final DimensionsRepository dimensionsRepository;
    private final RouteRepository routeRepository;
    private final OrderSimulation orderSimulation;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public OrderServiceImpl(OrderRepository repository,
                            CargoRepository cargoRepository,
                            HubRepository hubRepository,
                            DimensionsRepository dimensionsRepository,
                            RouteRepository routeRepository,
                            OrderSimulation orderSimulation
                            ) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.dimensionsRepository = dimensionsRepository;
        this.routeRepository = routeRepository;
        this.orderSimulation = orderSimulation;
    }
    @Override
    public void simulate(){
        RouteEntity route = new RouteEntity();
        List<HubEntity> hubList = new ArrayList<>();
        HubEntity hub = new HubEntity();
        hub.setName("Berlin");
        hubList.add(hub);
        hub = new HubEntity();
        hub.setName("Frankfurt");
        hubList.add(hub);
        hub = new HubEntity();
        hub.setName("Stuttgart");
        hubList.add(hub);
        route.setHubs(hubList);

        OrderEntity orderEntity = orderSimulation.getNewOrder(route, 30.0);
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

        getRealHubsForRoute(orderEntity);
        orderEntity.getRoute().getOrder().add(orderEntity);
        List<CargoEntity> cargoList = orderEntity.getCargoEntities();
        cargoList.forEach(x -> x.setOrderEntity(orderEntity));
        RouteEntity route = orderEntity.getRoute();

        List<RouteEntity> list = routeRepository.findByHubsIn(route.getHubs());
        route = TryToFindRouteInRepo(route, list);
        orderEntity.setRoute(route);
        return repository.save(orderEntity);
    }

    private RouteEntity TryToFindRouteInRepo(RouteEntity route, List<RouteEntity> list) {
        if(list.size()>0){
            List<HubEntity> hubs = route.getHubs();
            for(RouteEntity routeEnt : list){
                List<String> routeEntList = routeEnt.getHubs().stream().map(HubEntity::getName).collect(Collectors.toList());
                if(list.size() == hubs.size()){
                    List<String> hubsList = hubs.stream().map(HubEntity::getName).collect(Collectors.toList());
                    if(routeEntList.equals(hubsList)){
                        route = routeEnt;
                    }
                }
            }
        }
        return route;
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
}
