package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Integer> {
    List<RouteEntity> findByHubsIn(Collection<HubEntity> hubs);
    
}
