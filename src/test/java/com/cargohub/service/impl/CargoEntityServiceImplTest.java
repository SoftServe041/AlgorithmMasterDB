package com.cargohub.service.impl;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.CargoPositionEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.CargoException;
import com.cargohub.repository.CargoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CargoEntityServiceImplTest {

    @InjectMocks
    CargoServiceImpl cargoService;

    @Mock
    CargoRepository cargoRepository;

    CargoEntity subject;
    Page<CargoEntity> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CargoEntity();
        subject.setId(9992);
        subject.setCargoPosition(new CargoPositionEntity());
        subject.setDeliveryStatus(DeliveryStatus.PROCESSING);
        subject.setFinalDestination("Rome");
        subject.setWeight(1000d);

        page = new PageImpl(List.of(subject));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void create() {
        subject.setId(null);
        when(cargoRepository.save(nullable(CargoEntity.class))).thenReturn(subject);
        ArgumentCaptor<CargoEntity> captor = ArgumentCaptor.forClass(CargoEntity.class);
        CargoEntity returned = cargoService.save(subject);
        verify(cargoRepository).save(captor.capture());
        CargoEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
    }

    @Test
    void createThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.save(subject));
    }

    @Test
    void findById() {
        when(cargoRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(subject));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        CargoEntity returned = cargoService.findById(subject.getId());
        verify(cargoRepository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(subject.getId()));
        assertThat(returned, is(subject));
    }

    @Test
    void findByIdThrowsCargoException() {
        when(cargoRepository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(CargoException.class, () -> cargoService.findById(subject.getId()));
        subject.setId(null);
        assertThrows(CargoException.class, () -> cargoService.findById(subject.getId()));
    }

    @Test
    void update() {
        when(cargoRepository.save(nullable(CargoEntity.class))).thenReturn(subject);
        when(cargoRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<CargoEntity> captor = ArgumentCaptor.forClass(CargoEntity.class);
        CargoEntity returned = cargoService.update(subject);
        verify(cargoRepository).save(captor.capture());
        CargoEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
        assertEquals(subject.getFinalDestination(), used.getFinalDestination());
    }

    @Test
    void updateCargoThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.update(subject));
        subject.setId(null);
        assertThrows(CargoException.class, () -> cargoService.update(subject));
    }


    @Test
    void delete() {
        when(cargoRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        cargoService.delete(subject.getId());
        verify(cargoRepository).deleteById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(subject.getId()));
    }

    @Test
    void deleteThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.delete(subject.getId()));
    }

    @Test
    void findAll() {
        when(cargoRepository.findAll(any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<CargoEntity> returned = cargoService.findAll(pageable);
        verify(cargoRepository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(returned, is(page));
        assertThat(used, is(pageable));
    }

}