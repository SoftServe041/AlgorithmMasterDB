package com.cargohub.cargoloader;

import com.cargohub.entities.CargoPositionEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;
import com.cargohub.exceptions.CargoPositionException;
import com.cargohub.repository.CargoPositionRepository;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CargoLoader3DTest {

	@InjectMocks
	CargoLoader3D cargoloader3d;

	CargoHold cargohold;
	List<Cargo> listCargo;
	RouteEntity route;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		listCargo = new LinkedList<Cargo>();

//		Cargo box1 = new Cargo(1, 1.2, 1.2, 1.2, 1, 1, "Kyiv");
//		Cargo box2 = new Cargo(2, 0.6, 0.6, 1.2, 2, 2, "Kyiv");
//		Cargo box3 = new Cargo(3, 1.2, 1.2, 1.2, 2, 3, "Lviv");
//		Cargo box4 = new Cargo(4, 0.6, 0.6, 1.2, 5, 4, "Lviv");
//		Cargo box5 = new Cargo(5, 1.2, 1.2, 1.2, 2, 5, "Lviv");
//		Cargo box6 = new Cargo(6, 0.6, 1.2, 1.2, 2, 6, "Kyiv");
//		Cargo box7 = new Cargo(7, 0.3, 0.3, 0.6, 2, 7, "Lviv");
//		Cargo box8 = new Cargo(8, 0.6, 0.6, 1.2, 2, 8, "Kyiv");
//		Cargo box9 = new Cargo(9, 1.2, 1.2, 1.2, 2, 9, "Kyiv");
//		Cargo box11 = new Cargo(11, 1.2, 1.2, 1.2, 1, 1, "Kyiv");
//		Cargo box12 = new Cargo(12, 0.6, 0.6, 1.2, 2, 2, "Kyiv");
//		Cargo box13 = new Cargo(13, 1.2, 1.2, 1.2, 2, 3, "Lviv");
//		Cargo box14 = new Cargo(14, 0.6, 0.6, 1.2, 5, 4, "Lviv");
//		Cargo box15 = new Cargo(15, 1.2, 1.2, 1.2, 2, 5, "Lviv");
//		Cargo box16 = new Cargo(16, 0.6, 1.2, 1.2, 2, 6, "Kyiv");
//		Cargo box17 = new Cargo(17, 0.3, 0.3, 0.6, 2, 7, "Lviv");
//		Cargo box18 = new Cargo(18, 0.6, 0.6, 1.2, 2, 8, "Kyiv");
//		Cargo box19 = new Cargo(19, 2.4, 2.4, 2.4, 2, 9, "Kyiv");
//
//		listCargo.add(box1);
//		listCargo.add(box2);
//		listCargo.add(box3);
//		listCargo.add(box4);
//		listCargo.add(box5);
//		listCargo.add(box6);
//		listCargo.add(box7);
//		listCargo.add(box8);
//		listCargo.add(box9);
//		listCargo.add(box11);
//		listCargo.add(box12);
//		listCargo.add(box13);
//		listCargo.add(box14);
//		listCargo.add(box15);
//		listCargo.add(box16);
//		listCargo.add(box17);
//		listCargo.add(box18);
//		listCargo.add(box19);

		HubEntity hub1 = new HubEntity();
		hub1.setName("Kharkiv");
		HubEntity hub2 = new HubEntity();
		hub2.setName("Kyiv");
		HubEntity hub3 = new HubEntity();
		hub3.setName("Lviv");

		route = new RouteEntity();

		route.setHubs(Arrays.asList(hub1, hub2, hub3));

		cargohold = new CargoHold(2.4, 2.4, 12d, 22000, new int[40][8][8]);

		cargoloader3d = new CargoLoader3D();

	}

	@Test
	void loaderTest() {
		cargoloader3d.loadCargo(listCargo, route, cargohold);
		System.out.println(cargohold.getLoadedCargo());
	}
}