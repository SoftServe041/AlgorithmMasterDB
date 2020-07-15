package com.cargohub.service;

import com.cargohub.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    boolean existsById(Integer id);

    OrderEntity findById(Integer id);

    OrderEntity update(OrderEntity orderEntity);

    OrderEntity save(OrderEntity orderEntity);

    Page<OrderEntity> findAll(Pageable pageable);

    Page<OrderEntity> findAllByUserId(Integer userId, Pageable pageable);

    void delete(Integer id);

    void simulate();
}
