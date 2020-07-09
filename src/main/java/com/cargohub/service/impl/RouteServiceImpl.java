package com.cargohub.service.impl;

import com.cargohub.entities.Hub;
import com.cargohub.entities.Route;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.RouteException;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.RouteRepository;
import com.cargohub.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository repository;
    private final HubRepository hubRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository repository, HubRepository hubRepository) {
        this.repository = repository;
        this.hubRepository = hubRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Route findById(Integer id) {
        Route result;
        result = repository.findById(id).orElseThrow(() -> new RouteException("Route not found"));
        return result;
    }

    @Override
    public Route update(Route route) {
        if (route.getId() == null) {
            throw new RouteException("Illegal state for Route");
        }
        if (existsById(route.getId())) {
            return repository.save(route);
        }
        throw new RouteException("Route not found");
    }

    @Override
    public Route save(Route route) {
        if (route.getId() != null) {
            throw new RouteException("Illegal state for Route");
        }
        List<Hub> realHubs = new ArrayList<>();
        for (Hub routeHub : route.getRoute()) {
            Hub hub = hubRepository.findByName(routeHub.getName()).
                    orElseThrow(() -> {
                        throw new HubException("Hub not found");
                    });
            realHubs.add(hub);
        }
        route.setRoute(realHubs);
        return repository.save(route);
    }

    @Override
    public Page<Route> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new RouteException("Route not found");
    }
}
