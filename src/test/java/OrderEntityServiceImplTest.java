import com.cargohub.entities.OrderEntity;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.impl.OrderServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderEntityServiceImplTest {
    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;

    OrderEntity orderEntity;
    Page<OrderEntity> page;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        orderEntity = new OrderEntity();
        orderEntity.setId(1);
        orderEntity.setPrice(2.2);
        orderEntity.setTrackingId("123232323");
        orderEntity.setUserId(4);
        page = new PageImpl(List.of(orderEntity));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createOrder() {
        orderEntity.setId(null);

        when(orderRepository.save(nullable(OrderEntity.class))).thenReturn(orderEntity);
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        OrderEntity returned = orderService.save(orderEntity);
        verify(orderRepository).save(captor.capture());
        OrderEntity used = captor.getValue();
        assertThat(used, is(orderEntity));
        assertThat(returned, is(orderEntity));

    }

    @Test
    void findOrderById() {
        when(orderRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(orderEntity));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        OrderEntity returned = orderService.findById(orderEntity.getId());
        verify(orderRepository).findById(captor.capture());
        Integer used = captor.getValue();
        assertThat(used, is(orderEntity.getId()));
        assertThat(returned, is(orderEntity));
    }
    @Test
    void findByIdThrowsOrderException() {
        when(orderRepository.findById(nullable(Integer.class))).thenReturn(Optional.empty());
        assertThrows(OrderException.class, () -> orderService.findById(orderEntity.getId()));
        orderEntity.setId(null);
        assertThrows(OrderException.class, () -> orderService.findById(orderEntity.getId()));
    }
    @Test
    void findAllByUserId() {
        when(orderRepository.findAllByUserId(nullable(Integer.class), any(Pageable.class))).thenReturn(page);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Pageable> captor2 = ArgumentCaptor.forClass(Pageable.class);
        Page<OrderEntity> returned = orderService.findAllByUserId(orderEntity.getUserId(),pageable);
        verify(orderRepository).findAllByUserId(captor.capture(), captor2.capture());
        Integer used = captor.getValue();
        assertThat(used, is(orderEntity.getUserId()));
        assertThat(returned, is(page));
    }
    @Test
    void updateOrder() {
        when(orderRepository.save(nullable(OrderEntity.class))).thenReturn(orderEntity);
        when(orderRepository.existsById(nullable(Integer.class))).thenReturn(true);
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        OrderEntity returned = orderService.update(orderEntity);
        verify(orderRepository).save(captor.capture());
        OrderEntity used = captor.getValue();
        assertThat(used, is(orderEntity));
        assertThat(returned, is(orderEntity));
        assertEquals(orderEntity.getUserId(), used.getUserId());
    }








}
