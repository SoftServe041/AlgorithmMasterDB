package com.cargohub.service.impl;

import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.OrderException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.OrderRepository;
import com.cargohub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Random;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CargoRepository cargoRepository;
    private final HubRepository hubRepository;
    private final DimensionsRepository dimensionsRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public OrderServiceImpl(OrderRepository repository,
                            CargoRepository cargoRepository,
                            HubRepository hubRepository,
                            DimensionsRepository dimensionsRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.hubRepository = hubRepository;
        this.dimensionsRepository = dimensionsRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public OrderEntity findById(Integer id) {
        OrderEntity result;
        result = repository.findById(id).orElseThrow(() -> new OrderException("Order not found"));
        return result;
    }

    @Override
    public Page<OrderEntity> findAllByUserId(Integer userid, Pageable pageable) {

        Page<OrderEntity> orderPage = repository.findAllByUserId(userid, pageable);

        if (orderPage == null) {
            throw new OrderException("No record found");
        }
        return orderPage;
    }

    @Override
    public OrderEntity update(OrderEntity orderEntity) {
        if (orderEntity.getId() == null) {
            throw new OrderException("Illegal state for Order");
        }
        if (existsById(orderEntity.getId())) {
            return repository.save(orderEntity);
        }
        throw new OrderException("Order not found");
    }

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        if (orderEntity.getId() != null) {
            throw new OrderException("Illegal state for Order");
        }
        String arrName = orderEntity.getArrivalHub().getName();
        String depName = orderEntity.getDepartureHub().getName();
        Optional<Hub> optional = hubRepository.findByName(arrName);
        optional.ifPresent(orderEntity::setArrivalHub);
        optional = hubRepository.findByName(depName);
        optional.ifPresent(orderEntity::setDepartureHub);

        for (CargoEntity cargo : orderEntity.getCargoEntities()
        ) {
            dimensionsRepository.save(cargo.getDimensions());
            cargoRepository.save(cargo);
        }
        OrderEntity returnEntity = repository.save(orderEntity);
        List<CargoEntity> cargoList = orderEntity.getCargoEntities();
        cargoList.forEach(x -> x.setOrderEntity(orderEntity));
        cargoList.forEach(cargoRepository::save);
        return returnEntity;
    }


    @Override
    public Page<OrderEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new OrderException("Cargo not found");
    }

    @Override
    public void simulate() {
        Random random = new Random();
        List<OrderEntity> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new OrderEntity());
        }
        setHubs(list.get(0), "Kharkiv", "Poltava");
        setHubs(list.get(1), "Poltava", "Kharkiv");
        setHubs(list.get(2), "Myrgorod", "Kharkiv");
        setHubs(list.get(3), "Kharkiv", "Myrgorod");
        setHubs(list.get(4), "Kyiv", "Lviv");
        setHubs(list.get(5), "Lviv", "Kyiv");
        setHubs(list.get(6), "Kyiv", "Odesa");
        setHubs(list.get(7), "Odesa", "Kyiv");
        setHubs(list.get(8), "Odesa", "Sumy");
        list.forEach(x -> x.setUserId(1));
        list.forEach(x -> x.setPrice(1000.0));
        list.forEach(x -> {
            try {
                x.setEstimatedDeliveryDate(new SimpleDateFormat("yyyyMMdd").parse("20200820"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        int counter = 0;
        for(OrderEntity order : list){
            order.setTrackingId("ch" + random.nextInt(100) + counter++ + order.getArrivalHub().getName().hashCode()+ random.nextInt(1000));
        }
        for (int i = 0; i < 3; i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.PROCESSING);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.08.20 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 3; i < 6; i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.07.10 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 6; i < list.size(); i++) {
            list.get(i).setDeliveryStatus(DeliveryStatus.DELIVERED);
            try {
                list.get(i).setCreated(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse("2020.06.15 21:45:33"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        list.forEach(this::setCargo);
        list.forEach(this::save);
    }

    private void setCargo(OrderEntity orderEntity) {
        List<CargoEntity> cargoList = new ArrayList<>();
        Random random = new Random();
        int randCargosNumber = 5 + random.nextInt(6);
        int randWeight = 100;
        for (int i = 0; i < randCargosNumber; i++) {
            CargoEntity cargo = new CargoEntity();
            cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
            cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
            cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
            cargo.setWeight((double) random.nextInt(randWeight+100));
            cargo.setDimensions(getRandDimensions());
            cargoList.add(cargo);
        }
        orderEntity.setCargoEntities(cargoList);
    }

    private Dimensions getRandDimensions() {
        Random random = new Random();
        int max = 10;
        int rand = random.nextInt(max);
        switch (rand) {
            case 0: {
              return returnSetDimensions(0.3, 0.6,0.6);
            }
            case 1: {
                return returnSetDimensions(0.6, 0.6,0.6);
            }
            case 2: {
                return returnSetDimensions(0.9, 0.9,0.9);
            }
            case 3: {
                return returnSetDimensions(0.9, 1.2,1.5);
            }
            case 4: {
                return returnSetDimensions(1.5, 1.5,1.8);
            }
            case 5: {
                return returnSetDimensions(1.8, 1.8,1.8);
            }
            case 6: {
                return returnSetDimensions(2.1, 1.8,2.4);
            }
            case 7: {
                return returnSetDimensions(2.1, 2.1,2.4);
            }
            case 8: {
                return returnSetDimensions(2.4, 2.1,2.4);
            }
            case 9: {
                return returnSetDimensions(2.4, 2.4,2.4);
            }
        }
         return null;
    }
    private Dimensions returnSetDimensions(double height, double width, double length){
        Dimensions dimensions = new Dimensions();
        dimensions.setHeight(height);
        dimensions.setWidth(width);
        dimensions.setLength(length);
        return dimensions;
    }

    private void setHubs(OrderEntity order, String dep, String arr) {
        Hub depHub = new Hub();
        Hub arrHub = new Hub();
        depHub.setName(dep);
        arrHub.setName(arr);
        order.setArrivalHub(arrHub);
        order.setDepartureHub(depHub);
    }
}
