package com.cargohub.service;

import com.cargohub.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    boolean existsById(Integer id);

    Order findById(Integer id);

    Order update(Order order);

    Order save(Order order);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByUserId(Integer userId, Pageable pageable);

    void delete(Integer id);

}
