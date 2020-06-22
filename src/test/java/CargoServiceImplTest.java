import com.cargohub.dto.jar.DeliveryStatus;
import com.cargohub.dto.jar.PaymentStatus;
import com.cargohub.entities.Cargo;
import com.cargohub.entities.CargoPosition;
import com.cargohub.exceptions.CargoException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.service.impl.CargoServiceImpl;
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

class CargoServiceImplTest {

    @InjectMocks
    CargoServiceImpl cargoService;

    @Mock
    CargoRepository cargoRepository;

    Cargo cargo;
    Page<Cargo> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        cargo = new Cargo();
        cargo.setId(9992);
        cargo.setCargoPosition(new CargoPosition());
        cargo.setDeliveryStatus(DeliveryStatus.PROCESSING);
        cargo.setFinalDestination("Rome");
        cargo.setPaymentStatus(PaymentStatus.PAID);
        cargo.setWeight(1000d);

        page = new PageImpl(List.of(cargo));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createCargo() {
        cargo.setId(null);

        when(cargoRepository.save(nullable(Cargo.class))).thenReturn(cargo);
        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);
        Cargo returned = cargoService.save(cargo);
        verify(cargoRepository).save(captor.capture());
        Cargo used = captor.getValue();
        assertThat(used, is(cargo));
        assertThat(returned, is(cargo));
    }

    @Test
    void createCargoThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.save(cargo));
    }

    @Test
    void findCargoById() {
        when(cargoRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(cargo));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Cargo returned = cargoService.findById(cargo.getId());
        verify(cargoRepository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(cargo.getId()));
        assertThat(returned, is(cargo));
    }

    @Test
    void findByIdThrowsCargoException() {
        when(cargoRepository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(CargoException.class, () -> cargoService.findById(cargo.getId()));
        cargo.setId(null);
        assertThrows(CargoException.class, () -> cargoService.findById(cargo.getId()));
    }

    @Test
    void updateCargo() {
        when(cargoRepository.save(nullable(Cargo.class))).thenReturn(cargo);
        when(cargoRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);
        Cargo returned = cargoService.update(cargo);
        verify(cargoRepository).save(captor.capture());
        Cargo used = captor.getValue();
        assertThat(used, is(cargo));
        assertThat(returned, is(cargo));
        assertEquals(cargo.getFinalDestination(), used.getFinalDestination());
    }

    @Test
    void updateCargoThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.update(cargo));
        cargo.setId(null);
        assertThrows(CargoException.class, () -> cargoService.update(cargo));
    }


    @Test
    void deleteCargo() {
        when(cargoRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        cargoService.delete(cargo.getId());
        verify(cargoRepository).deleteById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(cargo.getId()));
    }

    @Test
    void deleteCargoThrowsCargoException() {
        assertThrows(CargoException.class, () -> cargoService.delete(cargo.getId()));
    }

    @Test
    void findAllCargos() {
        when(cargoRepository.findAll(any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<Cargo> returned = cargoService.findAll(pageable);
        verify(cargoRepository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(returned, is(page));
        assertThat(used, is(pageable));
    }

}