package com.cargohub.service.impl;

import com.cargohub.entities.DimensionsEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.entities.enums.TransporterStatus;
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
    public TransporterEntity findById(Integer id) {
        TransporterEntity result;
        result = repository.findById(id).orElseThrow(() -> new TransporterException("Transporter not found"));
        return result;
    }

    @Override
    public TransporterEntity update(TransporterEntity newTransporter) {
        if (newTransporter.getId() == null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        if (existsById(newTransporter.getId())) {
            TransporterEntity existingTransporter = repository.findById(newTransporter.getId()).get();
            HubEntity hub = hubRepository.findByName(newTransporter.getCurrentHub().getName()).orElseThrow(
                    () -> new HubException("Hub not found by name: " + newTransporter.getCurrentHub().getName()));
            existingTransporter.setCurrentHub(hub);
            List<CarrierCompartmentEntity> existingCompartments = existingTransporter.getCompartments();
            List<CarrierCompartmentEntity> newCompartments = newTransporter.getCompartments();
            List<CarrierCompartmentEntity> resolvedCompartments = resolveCompartments(existingCompartments, newCompartments);
            existingTransporter.setCompartments(resolvedCompartments);
            return repository.save(existingTransporter);
        }
        throw new TransporterException("Transporter not found");
    }

    private List<CarrierCompartmentEntity> resolveCompartments(List<CarrierCompartmentEntity> existingCompartments,
                                                               List<CarrierCompartmentEntity> newCompartments) {
        TransporterEntity transporter = existingCompartments.get(0).getTransporter();
        List<CarrierCompartmentEntity> result = new ArrayList<>();
        for (CarrierCompartmentEntity newC : newCompartments) {
            if (newC.getId() != null) {
                CarrierCompartmentEntity compartment = null;
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

    private void deleteUnusedCompartments(List<CarrierCompartmentEntity> existingCompartments) {
        do{
            CarrierCompartmentEntity deleting = existingCompartments.get(0);
            existingCompartments.remove(deleting);
            deleting.setTransporter(null);
            //By OrphanRemoval dimensions are also deleting
            carrierCompartmentRepository.deleteById(deleting.getId());
        }while (existingCompartments.size() != 0);
    }

    private void createNewCompartment(TransporterEntity transporter, List<CarrierCompartmentEntity> result, CarrierCompartmentEntity newC) {
        newC.setTransporter(transporter);
        newC.setVolume(dimensionsRepository.save(newC.getVolume()));
        carrierCompartmentRepository.save(newC);
        result.add(newC);
    }

    private CarrierCompartmentEntity getExistingCompartment(List<CarrierCompartmentEntity> existingCompartments, CarrierCompartmentEntity newC, CarrierCompartmentEntity compartment) {
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
    public TransporterEntity save(TransporterEntity transporter) {
        if (transporter.getId() != null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        for (CarrierCompartmentEntity compartment : transporter.getCompartments()) {
            DimensionsEntity dimensions = dimensionsRepository.save(compartment.getVolume());
            compartment.setVolume(dimensions);
            compartment.setFreeSpace(100d);
            compartment = carrierCompartmentRepository.save(compartment);
        }
        HubEntity hub = hubRepository.findByName(transporter.getCurrentHub().getName()).orElseThrow(
                () -> new HubException("Hub not found by name: " + transporter.getCurrentHub().getName()));
        transporter.setCurrentHub(hub);
        transporter.setStatus(TransporterStatus.WAITING);
        TransporterEntity result = repository.save(transporter);
        for (int i = 0; i < transporter.getCompartments().size(); i++) {
            CarrierCompartmentEntity compartment = transporter.getCompartments().get(i);
            compartment.setTransporter(result);
            carrierCompartmentRepository.save(compartment);
        }
        return result;
    }

    @Override
    public Page<TransporterEntity> findAll(Pageable pageable) {
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

    @Override
    public List<TransporterEntity> saveAll(List<TransporterEntity> transporters) {
        return (List<TransporterEntity>) repository.saveAll(transporters);
    }
}
