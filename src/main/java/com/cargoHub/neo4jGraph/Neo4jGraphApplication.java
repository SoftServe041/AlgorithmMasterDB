package com.cargoHub.neo4jGraph;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.repository.LocationRepository;
import com.cargoHub.neo4jGraph.service.LocationService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
@EnableNeo4jRepositories(basePackageClasses = LocationRepository.class)
public class Neo4jGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(Neo4jGraphApplication.class, args);
	}

	/*@Bean
	CommandLineRunner demo(LocationRepository locationRepository) {
		return args -> {

			locationRepository.deleteAll();

			Location kyiv = new Location("Kyiv");
			Location kharkiv = new Location("Kharkiv");
			Location poltava = new Location("Poltava");
			Location mirgorod = new Location("Mirgorod");

			List<Location> connectedLocations = Arrays.asList(kyiv, kharkiv, poltava, mirgorod);

			locationRepository.save(kyiv);
			locationRepository.save(kharkiv);
			locationRepository.save(poltava);
			locationRepository.save(mirgorod);

			locationRepository.getAllLocations();

		};
	}*/
}
