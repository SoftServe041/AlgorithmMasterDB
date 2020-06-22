import com.cargohub.entities.Cargo;
import com.cargohub.entities.Order;
import com.cargohub.exceptions.CargoException;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.OrderService;
import com.cargohub.service.impl.CargoServiceImpl;
import com.cargohub.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

public class OrderServiceImplTest {
    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;

    Order order;
    Page<Order> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        order = new Order();
        order.setId(1);
        order.setPrice(2.2);
        order.setTrackingId("123232323");
        order.setUserId(4);
        page = new PageImpl(List.of(order));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createOrder() {
        order.setId(null);

        when(orderRepository.save(nullable(Order.class))).thenReturn(order);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        Order returned = orderService.save(order);
        verify(orderRepository).save(captor.capture());
        Order used = captor.getValue();
        assertThat(used, is(order));
        assertThat(returned, is(order));

    }

    @Test
    void findOrderById() {
        when(orderRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(order));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Order returned = orderService.findById(order.getId());
        verify(orderRepository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(order.getId()));
        assertThat(returned, is(order));
    }
    @Test
    void findByIdThrowsOrderException() {
        when(orderRepository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(OrderException.class, () -> orderService.findById(order.getId()));
        order.setId(null);
        assertThrows(OrderException.class, () -> orderService.findById(order.getId()));
    }
    @Test
    void findAllByUserId() {
        when(orderRepository.findAllByUserId(nullable(Integer.class), any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Pageable> captor2 = ArgumentCaptor.forClass(Pageable.class);
        Page<Order> returned = orderService.findAllByUserId(order.getUserId(),pageable);
        verify(orderRepository).findAllByUserId(captor.capture(), captor2.capture());
        Integer used = captor.getValue();
        assertThat(used, is(order.getUserId()));
        assertThat(returned, is(page));
    }
    @Test
    void updateOrder() {
        when(orderRepository.save(nullable(Order.class))).thenReturn(order);
        when(orderRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        Order returned = orderService.update(order);
        verify(orderRepository).save(captor.capture());
        Order used = captor.getValue();
        assertThat(used, is(order));
        assertThat(returned, is(order));
        assertEquals(order.getUserId(), used.getUserId());
    }








}
