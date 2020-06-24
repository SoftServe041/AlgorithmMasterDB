import com.cargohub.config.Config;
import com.cargohub.entities.Order;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
public class OrderServiceIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;


    //private final OrderService orderService;

    Order order;
    Page<Order> page;
    Pageable pageable;
   /* public OrderServiceIntegrationTest(OrderService orderService){
      this.orderService = orderService;
    }*/
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        order = new Order();
        order.setId(1);
        order.setPrice(2.2);
        order.setTrackingId("123232323");
        order.setUserId(4);
        page = new PageImpl(List.of(order));
        pageable = PageRequest.of(0, 10);
    }


    @Test
    void createOrder() throws Exception {
        order.setId(null);

        mvc.perform(post("/13")
                .param("trackingId","12332332")
                .param("price","123232")
                .param("estimatedDeliveryDate","2020.04.14")
                .param("departureHub","A")
                .param("arrivalHub","B")
                .param("cargo","20")
                .param("paymentStatus","PAID")
                .param("deliveryStatus","PROCESSING")
                .param("cargoWeight","20")
                .param("cargoWidth","12")
                .param("cargoHeight","13")
                .param("CargoLength","14"))
                .andExpect(status().is2xxSuccessful());


    }







}
