package com.cargohub.controllers;

import com.cargohub.dto.CarrierCompartmentDto;
import com.cargohub.dto.DimensionsDto;
import com.cargohub.dto.TransporterDto;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterType;
import com.cargohub.service.TransporterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.JavaMigration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransportControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransporterService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TransporterDto transporterDto;
    //private UserDto userDto;

    private Flyway flyway;

    @Value("${spring.flyway.url}")
    String dbUrl;

    @Value("${spring.flyway.user}")
    String user;

    @Value("${spring.flyway.password}")
    String password;

    @BeforeEach
    private void init() {
        flyway = Flyway
                .configure()
                .dataSource(dbUrl, user, password)
                .load();
        flyway.migrate();
        List<CarrierCompartmentDto> compartments = new ArrayList<>();
        CarrierCompartmentDto compartment = new CarrierCompartmentDto();
        DimensionsDto dimensionsDto = new DimensionsDto();
        dimensionsDto.setWidth(240);
        dimensionsDto.setHeight(240);
        dimensionsDto.setLength(1200);
        compartment.setVolume(dimensionsDto);
        compartment.setFreeSpace(100d);
        compartment.setMaximumWeight(22d);
        compartments.add(compartment);

        transporterDto = new TransporterDto();
        transporterDto.setCompartments(compartments);
        transporterDto.setHubName("Kharkiv");
        transporterDto.setType(TransporterType.TRUCK);
    }


    @Test
    public void createTransporterShouldSaveToDb() throws Exception {
        //doReturn(userDto).when(userService).createUser(nullable(UserDto.class));
        this.mockMvc.perform(post("/admin/transport")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transporterDto)))
                .andExpect(status().isCreated());

        Page<Transporter> transporters = service.findAll(PageRequest.of(0, 10));
        for (Transporter t : transporters.getContent()) {
            System.out.println("t.getId() = " + t.getId());
        }

    }
}
