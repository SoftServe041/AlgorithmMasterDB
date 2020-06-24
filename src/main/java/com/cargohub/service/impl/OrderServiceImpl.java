package com.cargohub.service.impl;

import com.cargohub.dto.jar.ResponseOrderDto;
import com.cargohub.entities.Order;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository repository, CargoRepository cargoRepository, HubRepository hubRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Order findById(Integer id) {
        Order result;
        result = repository.findById(id).orElseThrow(() -> new OrderException("Order not found"));
        return result;
    }

    @Override
    public Page<Order> findAllByUserId(Integer id, Pageable pageable) {

        Page<Order> orderPage= repository.findAllByUserId(id, pageable);
        if(orderPage.isEmpty()){
            throw new OrderException("No record found");
        }
        return orderPage;
    }

    @Override
    public Order update(Order order) {
        if (order.getId() == null) {
            throw new OrderException("Illegal state for Order");
        }
        if (existsById(order.getId())) {
            return repository.save(order);
        }
        throw new OrderException("Order not found");
    }

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
            throw new OrderException("Illegal state for Order");
        }
        cargoRepository.save(order.getCargo());
        hubRepository.save(order.getArrivalHub());
        hubRepository.save(order.getDepartureHub());
        return repository.save(order);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
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
