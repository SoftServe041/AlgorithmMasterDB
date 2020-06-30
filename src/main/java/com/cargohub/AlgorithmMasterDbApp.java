package com.cargohub;

import com.cargohub.neo4jGraph.repository.LocationRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories(basePackageClasses = LocationRepository.class)
public class AlgorithmMasterDbApp {

    public static void main(String[] args) {
        SpringApplication.run(AlgorithmMasterDbApp.class, args);
    }
}
