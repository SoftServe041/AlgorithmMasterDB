package com.cargohub.repository;

import com.cargohub.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {

    Page<Order> findAllByUserId(Integer userId, Pageable pageable);
}
