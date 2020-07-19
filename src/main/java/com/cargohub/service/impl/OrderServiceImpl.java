package com.cargohub.service.impl;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.RouteEntity;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.OrderRepository;
import com.cargohub.repository.RouteRepository;
import com.cargohub.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final HubRepository hubRepository;
    private final RouteRepository routeRepository;

    public OrderServiceImpl(OrderRepository repository,
                            HubRepository hubRepository,
                            RouteRepository routeRepository
    ) {
        this.repository = repository;
        this.hubRepository = hubRepository;
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

        getRealHubsForRoute(orderEntity);
        orderEntity.getRoute().getOrder().add(orderEntity);
        List<CargoEntity> cargoList = orderEntity.getCargoEntities();
        cargoList.forEach(x -> x.setOrderEntity(orderEntity));
        RouteEntity route = orderEntity.getRoute();
        Optional<RouteEntity> optional = routeRepository.findByHubsIn(route.getHubs());
        if (optional.isPresent()) {
            route = optional.get();
        }
        orderEntity.setRoute(route);
        return repository.save(orderEntity);

    }

    private void getRealHubsForRoute(OrderEntity orderEntity) {
        RouteEntity route = orderEntity.getRoute();
        route.setOrder(new ArrayList<>());
        List<HubEntity> list = new ArrayList<>();
        for (HubEntity hub : route.getHubs()) {
            Optional<HubEntity> optional = hubRepository.findByName(hub.getName());
            if (optional.isPresent()) {
                list.add(optional.get());
            } else {
                throw new IllegalArgumentException("No such hub");
            }

        }
        route.setHubs(list);
    }

    private HubEntity getRealHubFromName(HubEntity hub) {
        Optional<HubEntity> optional = hubRepository.findByName(hub.getName());
        if (optional.isPresent())
            return optional.get();
        else {
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
