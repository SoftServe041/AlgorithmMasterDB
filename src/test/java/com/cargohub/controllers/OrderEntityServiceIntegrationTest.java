package com.cargohub.controllers;

import com.cargohub.dto.jar.RequestOrderDto;
import com.cargohub.entities.OrderEntity;
import com.cargohub.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderEntityServiceIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderService service;

    @Autowired
    private ObjectMapper objectMapper;

    RequestOrderDto requestOrderDto;
    Page<OrderEntity> page;
    Pageable pageable;

    @BeforeEach
    void setUp() throws ParseException {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        requestOrderDto = new RequestOrderDto();
        requestOrderDto.setArrivalHub("A");
        requestOrderDto.setDepartureHub("B");
        requestOrderDto.setCargoHeight(15);
        requestOrderDto.setCargoLength(15);
        requestOrderDto.setCargoWidth(15);
        requestOrderDto.setCargoWeight(20.0);
        Date date = new SimpleDateFormat("yyyyMMdd").parse("20100520");
        requestOrderDto.setEstimatedDeliveryDate(date);
        requestOrderDto.setPrice(400.0);
    }

    @Test
    @Order(1)
    void createOrderShouldSaveToDb() throws Exception {
        mvc.perform(post("/13").header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestOrderDto)))
                .andExpect(status().isCreated());
    }


    @Test
    @Order(2)
    public void getAllOrdersByUserIdShouldLoadFromDb() throws Exception {
        Integer id = getOrderTestingValueId();

        mvc.perform(get("/" + id + "/profile").header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void deleteOrderShouldDeleteFromDb() throws Exception {
        Integer id = getOrderTestingValueId();
        mvc.perform(delete("/" + id + "/deleteOrder").header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME").contentType("application/json"))
                .andExpect(status().isOk());
    }

    private Integer getOrderTestingValueId() {
        Page<OrderEntity> all = service.findAll(PageRequest.of(0, 1));
        long lastElement = all.getTotalElements();
        Page<OrderEntity> last = service.findAll(PageRequest.of((int) lastElement - 1, 1));
        return last.getContent().get(0).getId();
    }
}
