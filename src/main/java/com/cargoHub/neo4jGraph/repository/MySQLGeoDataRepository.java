package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MySQLGeoDataRepository extends PagingAndSortingRepository<Location, Integer> {
}
