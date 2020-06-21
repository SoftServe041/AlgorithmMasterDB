import com.cargohub.entities.Cargo;
import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.TransporterType;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.exceptions.TransporterException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.TransporterRepository;
import com.cargohub.service.impl.TransporterServiceImpl;
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

    Transporter subject;
    Page<Transporter> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new Transporter();
        subject.setId(9992);
        CarrierCompartment carrierCompartment = new CarrierCompartment();
        carrierCompartment.setVolume(new Dimensions());
        carrierCompartment.setCargos(List.of(new Cargo()));
        subject.setCompartments(List.of(carrierCompartment));
        subject.setCurrentHub(new Hub());
        subject.setType(TransporterType.TRUCK);
        subject.setRoute(List.of(new Hub()));

        page = new PageImpl(List.of(subject));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void create() {
        subject.setId(null);
        when(repository.save(nullable(Transporter.class))).thenReturn(subject);
        when(carrierCompartmentRepository.save(nullable(CarrierCompartment.class))).thenReturn(subject.getCompartments().get(0));
        ArgumentCaptor<Transporter> captor = ArgumentCaptor.forClass(Transporter.class);
        Transporter returned = service.save(subject);
        verify(repository).save(captor.capture());
        Transporter used = captor.getValue();
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
        Transporter returned = service.findById(subject.getId());
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
        when(repository.save(nullable(Transporter.class))).thenReturn(subject);
        when(repository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Transporter> captor = ArgumentCaptor.forClass(Transporter.class);
        Transporter returned = service.update(subject);
        verify(repository).save(captor.capture());
        Transporter used = captor.getValue();
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
        Page<Transporter> returned = service.findAll(pageable);
        verify(repository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(returned, is(page));
        assertThat(used, is(pageable));
    }
}