package com.cargohub.service.impl;

import com.cargohub.entities.transports.TransportDetailsEntity;
import com.cargohub.exceptions.TransportDetailsException;
import com.cargohub.repository.TransportDetailsRepository;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransportDetailsServiceImplTest {

    @InjectMocks
    TransportDetailsServiceImpl service;

    @Mock
    TransportDetailsRepository repository;

    TransportDetailsEntity subject;
    Page<TransportDetailsEntity> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new TransportDetailsEntity();
        subject.setId(9992);

        page = new PageImpl(List.of(subject));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void create() {
        subject.setId(null);
        when(repository.save(nullable(TransportDetailsEntity.class))).thenReturn(subject);
        ArgumentCaptor<TransportDetailsEntity> captor = ArgumentCaptor.forClass(TransportDetailsEntity.class);
        TransportDetailsEntity returned = service.save(subject);
        verify(repository).save(captor.capture());
        TransportDetailsEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
    }

    @Test
    void createThrowsCargoPositionException() {
        assertThrows(TransportDetailsException.class, () -> service.save(subject));
    }

    @Test
    void findById() {
        when(repository.findById(nullable(Integer.class))).thenReturn(Optional.of(subject));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        TransportDetailsEntity returned = service.findById(subject.getId());
        verify(repository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(subject.getId()));
        assertThat(returned, is(subject));
    }

    @Test
    void findByIdThrowsCargoPositionException() {
        when(repository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(TransportDetailsException.class, () -> service.findById(subject.getId()));
        subject.setId(null);
        assertThrows(TransportDetailsException.class, () -> service.findById(subject.getId()));
    }

    @Test
    void update() {
        when(repository.save(nullable(TransportDetailsEntity.class))).thenReturn(subject);
        when(repository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<TransportDetailsEntity> captor = ArgumentCaptor.forClass(TransportDetailsEntity.class);
        TransportDetailsEntity returned = service.update(subject);
        verify(repository).save(captor.capture());
        TransportDetailsEntity used = captor.getValue();
        assertThat(used, is(subject));
        assertThat(returned, is(subject));
    }

    @Test
    void updateThrowsCargoPositionException() {
        assertThrows(TransportDetailsException.class, () -> service.update(subject));
        subject.setId(null);
        assertThrows(TransportDetailsException.class, () -> service.update(subject));
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
        assertThrows(TransportDetailsException.class, () -> service.delete(subject.getId()));
    }

    @Test
    void findAll() {
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<TransportDetailsEntity> returned = service.findAll(pageable);
        verify(repository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(returned, is(page));
        assertThat(used, is(pageable));
    }

}