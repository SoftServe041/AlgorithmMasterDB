/*
package com.cargoHub.neo4jGraph.service;


import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class Config {

    @Bean
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration.Builder()
                .uri(databaseUrl)
                .credentials(userName, password)
                .build();
        return new SessionFactory(configuration, "com.cargoHub.neo4jGraph");
    }

    @Value("${spring.data.neo4j.uri}")
    private String databaseUrl;
    @Value("${spring.data.neo4j.username}")
    private String userName;
    @Value("${spring.data.neo4j.password}")
    private String password;
}
*/
