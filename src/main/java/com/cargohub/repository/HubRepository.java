package com.cargohub.repository;

import com.cargohub.entities.Hub;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface HubRepository extends PagingAndSortingRepository<Hub, Integer> {
    Optional<Hub> findByName(String name);
}
