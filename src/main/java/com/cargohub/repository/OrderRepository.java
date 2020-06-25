package com.cargohub.repository;

import com.cargohub.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {



    @Query("select o from Order o WHERE o.userId = :userId")
    Page<Order> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
}
