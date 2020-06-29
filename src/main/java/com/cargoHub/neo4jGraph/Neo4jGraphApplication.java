package com.cargoHub.neo4jGraph;

import com.cargoHub.neo4jGraph.repository.LocationRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories//(basePackageClasses = LocationRepository.class)
public class Neo4jGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(Neo4jGraphApplication.class, args);
	}
}
