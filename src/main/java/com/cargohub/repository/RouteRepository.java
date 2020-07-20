package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Integer> {
    Optional<RouteEntity> findByHubsIn(List<HubEntity> hubs);

}
