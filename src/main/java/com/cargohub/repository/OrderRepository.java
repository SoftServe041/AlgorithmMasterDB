package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Integer> {
    @Query("select o from OrderEntity o WHERE o.userId = :userId")
    Page<OrderEntity> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);

    List<OrderEntity> findAllByDepartureHubAndDeliveryStatus(HubEntity hub, DeliveryStatus status);

    List<OrderEntity> findAll(Example<OrderEntity> example);

}
