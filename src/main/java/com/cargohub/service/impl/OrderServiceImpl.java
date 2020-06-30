package com.cargohub.service.impl;

import com.cargohub.entities.OrderEntity;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final DimensionsRepository dimensionsRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public OrderServiceImpl(OrderRepository repository,
                            CargoRepository cargoRepository,
                            HubRepository hubRepository,
                            DimensionsRepository dimensionsRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.dimensionsRepository = dimensionsRepository;
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
        orderEntity.setTrackingId(generateTrackingId(orderEntity.getDepartureHub().getName(), orderEntity.getArrivalHub().getName(), orderEntity.getUserId()));
        dimensionsRepository.save(orderEntity.getCargo().getDimensions());
        cargoRepository.save(orderEntity.getCargo());
        hubRepository.save(orderEntity.getArrivalHub());
        hubRepository.save(orderEntity.getDepartureHub());
        return repository.save(orderEntity);
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
    private String generateTrackingId(String firstCity, String secondCity, long id) {
        final Random random = new Random();

        byte[] byteCity1 = firstCity.getBytes();
        byte[] byteCity2 = secondCity.getBytes();

        StringBuffer returnStr = new StringBuffer();
        for (byte a : byteCity1) {
            returnStr.append(a);
        }
        for (byte a : byteCity2) {
            returnStr.append(a);
        }
        returnStr.append(id);
        returnStr.append(random.nextInt());
        returnStr.append(System.currentTimeMillis() % 100);

        return returnStr.toString();
    }
}
