package com.cargohub.repository;

import com.cargohub.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Integer> {
    @Query("select o from OrderEntity o WHERE o.userId = :userId")
    Page<OrderEntity> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
}
