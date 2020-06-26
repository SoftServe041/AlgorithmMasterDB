package com.cargohub.service.impl;

import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterStatus;
import com.cargohub.exceptions.CarrierCompartmentException;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.TransporterException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.TransporterRepository;
import com.cargohub.service.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransporterServiceImpl implements TransporterService {

    private final TransporterRepository repository;
    private final HubRepository hubRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;
    private final DimensionsRepository dimensionsRepository;

    @Autowired
    public TransporterServiceImpl(TransporterRepository repository, HubRepository hubRepository,
                                  CarrierCompartmentRepository carrierCompartmentRepository,
                                  DimensionsRepository dimensionsRepository) {
        this.repository = repository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.dimensionsRepository = dimensionsRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Transporter findById(Integer id) {
        Transporter result;
        result = repository.findById(id).orElseThrow(() -> new TransporterException("Transporter not found"));
        return result;
    }

    @Override
    public Transporter update(Transporter newTransporter) {
        if (newTransporter.getId() == null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        if (existsById(newTransporter.getId())) {
            Transporter existingTransporter = repository.findById(newTransporter.getId()).get();
            Hub hub = hubRepository.findByName(newTransporter.getCurrentHub().getName()).orElseThrow(
                    () -> new HubException("Hub not found by name: " + newTransporter.getCurrentHub().getName()));
            existingTransporter.setCurrentHub(hub);
            List<CarrierCompartment> existingCompartments = existingTransporter.getCompartments();
            List<CarrierCompartment> newCompartments = newTransporter.getCompartments();
            List<CarrierCompartment> resolvedCompartments = resolveCompartments(existingCompartments, newCompartments);
            existingTransporter.setCompartments(resolvedCompartments);
            return repository.save(existingTransporter);
        }
        throw new TransporterException("Transporter not found");
    }

    private List<CarrierCompartment> resolveCompartments(List<CarrierCompartment> existingCompartments,
                                                         List<CarrierCompartment> newCompartments) {
        Transporter transporter = existingCompartments.get(0).getTransporter();
        List<CarrierCompartment> result = new ArrayList<>();
        for (CarrierCompartment newC : newCompartments) {
            if (newC.getId() != null) {
                CarrierCompartment compartment = null;
                compartment = getExistingCompartment(existingCompartments, newC, compartment);
                if (compartment == null) {
                    throw new CarrierCompartmentException("Compartment not Exists or belongs to another Transporter");
                }
                result.add(compartment);
            } else {
                createNewCompartment(transporter, result, newC);
            }
        }
        if (existingCompartments.size() > 0) {
            deleteUnusedCompartments(existingCompartments);
        }
        return result;
    }

    private void deleteUnusedCompartments(List<CarrierCompartment> existingCompartments) {
        do{
            CarrierCompartment deleting = existingCompartments.get(0);
            existingCompartments.remove(deleting);
            deleting.setTransporter(null);
            //By OrphanRemoval dimensions are also deleting
            carrierCompartmentRepository.deleteById(deleting.getId());
        }while (existingCompartments.size() != 0);
    }

    private void createNewCompartment(Transporter transporter, List<CarrierCompartment> result, CarrierCompartment newC) {
        newC.setTransporter(transporter);
        newC.setVolume(dimensionsRepository.save(newC.getVolume()));
        carrierCompartmentRepository.save(newC);
        result.add(newC);
    }

    private CarrierCompartment getExistingCompartment(List<CarrierCompartment> existingCompartments, CarrierCompartment newC, CarrierCompartment compartment) {
        for (int i = 0; i < existingCompartments.size(); i++) {
            if (newC.getId().equals(existingCompartments.get(i).getId())) {
                compartment = existingCompartments.remove(i);
                compartment.setFreeSpace(newC.getFreeSpace());
                compartment.setMaximumWeight(newC.getMaximumWeight());
                compartment.getVolume().setHeight(newC.getVolume().getHeight());
                compartment.getVolume().setWidth(newC.getVolume().getWidth());
                compartment.getVolume().setLength(newC.getVolume().getLength());
            }
        }
        return compartment;
    }

    @Override
    public Transporter save(Transporter transporter) {
        if (transporter.getId() != null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        for (CarrierCompartment compartment : transporter.getCompartments()) {
            Dimensions dimensions = dimensionsRepository.save(compartment.getVolume());
            compartment.setVolume(dimensions);
            compartment.setFreeSpace(100d);
            compartment = carrierCompartmentRepository.save(compartment);
        }
        Hub hub = hubRepository.findByName(transporter.getCurrentHub().getName()).orElseThrow(
                () -> new HubException("Hub not found by name: " + transporter.getCurrentHub().getName()));
        transporter.setCurrentHub(hub);
        transporter.setStatus(TransporterStatus.WAITING);
        Transporter result = repository.save(transporter);
        for (CarrierCompartment compartment : transporter.getCompartments()) {
            compartment.setTransporter(result);
            carrierCompartmentRepository.save(compartment);
        }
        return result;
    }

    @Override
    public Page<Transporter> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new TransporterException("Transporter not found");
    }
}
