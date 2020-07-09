package com.cargohub.controllers;

import com.cargohub.dto.CarrierCompartmentDto;
import com.cargohub.dto.DimensionsDto;
import com.cargohub.dto.TransportDetailsDto;
import com.cargohub.dto.TransporterDto;
import com.cargohub.entities.transports.TransportDetails;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterType;
import com.cargohub.service.TransportDetailsService;
import com.cargohub.service.TransporterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransportControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransporterService service;

    @Autowired
    TransportDetailsService detailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransporterDto transporterDto;

    private TransportDetailsDto transportDetailsDto;

    @BeforeEach
    private void init() {
        List<CarrierCompartmentDto> compartments = new ArrayList<>();
        CarrierCompartmentDto compartment = new CarrierCompartmentDto();
        DimensionsDto dimensionsDto = new DimensionsDto();
        dimensionsDto.setWidth(Double.MAX_VALUE / 2);
        dimensionsDto.setHeight(Double.MAX_VALUE / 2);
        dimensionsDto.setLength(Double.MAX_VALUE / 2);
        compartment.setVolume(dimensionsDto);
        compartment.setFreeSpace(Double.MAX_VALUE / 2);
        compartment.setMaximumWeight(Double.MAX_VALUE / 2);
        compartments.add(compartment);

        transporterDto = new TransporterDto();
        transporterDto.setCompartments(compartments);
        transporterDto.setHubName("Kharkiv");
        transporterDto.setType(TransporterType.TRUCK);

        transportDetailsDto = new TransportDetailsDto();
        transportDetailsDto.setType(TransporterType.TRUCK);
        transportDetailsDto.setPricePerKm(999d);
        transportDetailsDto.setAverageSpeed(9379992d);
        transportDetailsDto.setCellSize(.999);
    }


    @Test
    @Order(1)
    public void createTransporterShouldSaveToDb() throws Exception {
        this.mockMvc.perform(post("/admin/transport")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transporterDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void updateTransporterShouldUpdateRowInDb() throws Exception {
        CarrierCompartmentDto compartment = new CarrierCompartmentDto();
        compartment.setMaximumWeight(Double.MAX_VALUE / 2);
        compartment.setFreeSpace(101d);
        DimensionsDto dimensions = new DimensionsDto();
        dimensions.setWidth(Double.MIN_VALUE / 2);
        dimensions.setHeight(Double.MIN_VALUE / 2);
        dimensions.setLength(Double.MIN_VALUE / 2);
        compartment.setVolume(dimensions);
        Integer id = getTransporterTestingValueId();
        transporterDto.setId(id);
        transporterDto.setCompartments(List.of(compartment));
        this.mockMvc.perform(put("/admin/transport")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transporterDto)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void getTransporterShouldLoadFromDb() throws Exception {
        Integer id = getTransporterTestingValueId();
        this.mockMvc.perform(get("/admin/transport/" + id)
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @Order(4)
    public void getAllTransporterShouldLoadAllFromDb() throws Exception {
        this.mockMvc.perform(get("/admin/transport")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @Order(5)
    public void deleteTransporterShouldDeleteFromDb() throws Exception {
        Integer id = getTransporterTestingValueId();
        this.mockMvc.perform(delete("/admin/transport/" + id)
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void createTransporterDetailsShouldSaveToDb() throws Exception {
        this.mockMvc.perform(post("/admin/transport/details")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transportDetailsDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(7)
    public void updateTransporterDetailsShouldUpdateRowInDb() throws Exception {
        Integer id = getTransporterDetailsTestingValueId();
        transportDetailsDto.setId(id);
        transportDetailsDto.setAverageSpeed(.9379992);
        this.mockMvc.perform(put("/admin/transport/details")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transportDetailsDto)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void getTransporterDetailsShouldLoadFromDb() throws Exception {
        Integer id = getTransporterDetailsTestingValueId();
        this.mockMvc.perform(get("/admin/transport/details/" + id)
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @Order(9)
    public void getAllTransporterDetailsShouldLoadAllFromDb() throws Exception {
        this.mockMvc.perform(get("/admin/transport/details")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @Order(10)
    public void deleteTransporterDetailsShouldDeleteFromDb() throws Exception {
        Integer id = getTransporterDetailsTestingValueId();
        this.mockMvc.perform(delete("/admin/transport/details/" + id)
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }


    @Test
    public void getAllTransporterTypesShouldLoadAllFromDb() throws Exception {
        this.mockMvc.perform(get("/admin/transport/types")
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBh" +
                        "ZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6MTU5MjU0OT" +
                        "U2NywiZXhwIjo1MTkyNTQ5NTY3fQ.BeENEITc0RQWLcjRbvorXdQ1GFrqZF5vXaSIOf8auME")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    private Integer getTransporterTestingValueId() {
        Page<Transporter> all = service.findAll(PageRequest.of(0, 1));
        long lastElement = all.getTotalElements();
        Page<Transporter> last = service.findAll(PageRequest.of((int) lastElement - 1, 1));
        return last.getContent().get(0).getId();
    }

    private Integer getTransporterDetailsTestingValueId() {
        Page<TransportDetails> all = detailsService.findAll(PageRequest.of(0, 1));
        long lastElement = all.getTotalElements();
        Page<TransportDetails> last = detailsService.findAll(PageRequest.of((int) lastElement - 1, 1));
        return last.getContent().get(0).getId();
    }
}
