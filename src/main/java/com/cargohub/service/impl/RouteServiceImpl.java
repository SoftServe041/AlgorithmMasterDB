package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;
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
    public RouteEntity findById(Integer id) {
        RouteEntity result;
        result = repository.findById(id).orElseThrow(() -> new RouteException("Route not found"));
        return result;
    }

    @Override
    public RouteEntity update(RouteEntity route) {
        if (route.getId() == null) {
            throw new RouteException("Illegal state for Route");
        }
        if (existsById(route.getId())) {
            return repository.save(route);
        }
        throw new RouteException("Route not found");
    }

    @Override
    public RouteEntity save(RouteEntity route) {
        if (route.getId() != null) {
            throw new RouteException("Illegal state for Route");
        }
        List<HubEntity> realHubs = new ArrayList<>();
        for (HubEntity routeHub : route.getHubs()) {
            HubEntity hub = hubRepository.findByName(routeHub.getName()).
                    orElseThrow(() -> {
                        throw new HubException("Hub not found");
                    });
            realHubs.add(hub);
        }
        route.setHubs(realHubs);
        return repository.save(route);
    }

    @Override
    public Page<RouteEntity> findAll(Pageable pageable) {
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
