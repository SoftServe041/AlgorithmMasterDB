package com.cargohub.service.impl;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.DimensionsEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.exceptions.TransporterException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.TransporterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransportServiceImplTest {

    @InjectMocks
    TransporterServiceImpl service;

    @Mock
    TransporterRepository repository;

    @Mock
    HubRepository hubRepository;

    @Mock
    CarrierCompartmentRepository carrierCompartmentRepository;

    @Mock
    DimensionsRepository dimensionsRepository;

    TransporterEntity subject;
    Page<TransporterEntity> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TransporterEntity();
        subject.setId(9992);
        CarrierCompartmentEntity carrierCompartment = new CarrierCompartmentEntity();
        carrierCompartment.setVolume(new DimensionsEntity());
        carrierCompartment.setCargoEntities(List.of(new CargoEntity()));
        List<CarrierCompartmentEntity> compartments = new ArrayList<>();
        compartments.add(carrierCompartment);
        subject.setCompartments(compartments);
        subject.setCurrentHub(new HubEntity());
        subject.setType(TransporterType.TRUCK);
        subject.setRoute(List.of(new HubEntity()));

        page = new PageImpl(List.of(subject));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void create() {
        subject.setId(null);
        when(repository.save(nullable(TransporterEntity.class))).thenReturn(subject);
        when(hubRepository.findByName(nullable(String.class))).thenReturn(Optional.of(subject.getCurrentHub()));
        when(carrierCompartmentRepository.save(nullable(CarrierCompartmentEntity.class))).thenReturn(subject.getCompartments().get(0));
        ArgumentCaptor<TransporterEntity> captor = ArgumentCaptor.forClass(TransporterEntity.class);
        TransporterEntity returned = service.save(subject);
        verify(repository).save(captor.capture());
        TransporterEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
    }

    @Test
    void createThrowsCargoPositionException() {
        assertThrows(TransporterException.class, () -> service.save(subject));
    }

    @Test
    void findById() {
        when(repository.findById(nullable(Integer.class))).thenReturn(Optional.of(subject));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        TransporterEntity returned = service.findById(subject.getId());
        verify(repository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(subject.getId()));
        assertThat(returned, is(subject));
    }

    @Test
    void findByIdThrowsCargoPositionException() {
        when(repository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(TransporterException.class, () -> service.findById(subject.getId()));
        subject.setId(null);
        assertThrows(TransporterException.class, () -> service.findById(subject.getId()));
    }

    @Test
    void update() {
        when(repository.save(nullable(TransporterEntity.class))).thenReturn(subject);
        when(repository.findById(nullable(Integer.class))).thenReturn(Optional.of(subject));
        when(repository.existsById(nullable(Integer.class))).thenReturn(true);
        when(hubRepository.findByName(nullable(String.class))).thenReturn(Optional.of(subject.getCurrentHub()));
        ArgumentCaptor<TransporterEntity> captor = ArgumentCaptor.forClass(TransporterEntity.class);
        TransporterEntity returned = service.update(subject);
        verify(repository).save(captor.capture());
        TransporterEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
    }

    @Test
    void updateThrowsCargoPositionException() {
        assertThrows(TransporterException.class, () -> service.update(subject));
        subject.setId(null);
        assertThrows(TransporterException.class, () -> service.update(subject));
    }


    @Test
    void delete() {
        when(repository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        service.delete(subject.getId());
        verify(repository).deleteById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(subject.getId()));
    }

    @Test
    void deleteThrowsCargoPositionException() {
        assertThrows(TransporterException.class, () -> service.delete(subject.getId()));
    }

    @Test
    void findAll() {
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<TransporterEntity> returned = service.findAll(pageable);
        verify(repository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(returned, is(page));
        assertThat(used, is(pageable));
    }
}