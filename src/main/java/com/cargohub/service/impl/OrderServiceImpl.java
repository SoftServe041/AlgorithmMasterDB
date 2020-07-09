package com.cargohub.service.impl;

import com.cargohub.entities.Cargo;
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
        Hub arrivalHub;
        Hub departureHub;
        Optional<Hub> optional = hubRepository.findByName(arrName);
        optional.ifPresent(orderEntity::setArrivalHub);
        optional = hubRepository.findByName(depName);
        if(optional.isPresent()){
            orderEntity.setDepartureHub(optional.get());
        }

        //orderEntity.setArrivalHub(hubRepository.findByName(orderEntity.getArrivalHub().getName()).get());
        orderEntity.setTrackingId(generateTrackingId(orderEntity.getDepartureHub().getName(), orderEntity.getArrivalHub().getName(), orderEntity.getUserId()));
        for (CargoEntity cargo : orderEntity.getCargoEntities()
        ) {
            dimensionsRepository.save(cargo.getDimensions());
            cargoRepository.save(cargo);
        }
        //hubRepository.save(orderEntity.getArrivalHub());
        //hubRepository.save(orderEntity.getDepartureHub());
        return repository.save(orderEntity);
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
        List<OrderEntity> list = new ArrayList<>();
        for(int i = 0; i < 9; i++){
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
        setHubs(list.get(9), "Sumy", "Pavlograd");
        list.forEach(x -> x.setUserId(1));
        list.forEach(x -> x.setPrice(1000.0));
        list.forEach(x -> {
            try {
                x.setEstimatedDeliveryDate(new SimpleDateFormat("yyyyMMdd").parse("20200820"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        for(int i = 0; i < 3; i++){
            list.get(i).setDeliveryStatus(DeliveryStatus.PROCESSING);
        }
        for(int i = 3; i < 6; i++){
            list.get(i).setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
        }
        for(int i = 6; i < list.size(); i++){
            list.get(i).setDeliveryStatus(DeliveryStatus.DELIVERED);
        }



    }
    private static void setCargo(OrderEntity orderEntity, double weight, Integer width, Integer length, Integer height){
        Cargo cargo = new Cargo();
        cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
        cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
        cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
        cargo.setWeight(weight);
        Dimensions dimensions = new Dimensions();
        dimensions.setHeight(height);
        dimensions.setLength(length);
        dimensions.setWidth(width);
        cargo.setDimensions(dimensions);
        orderEntity.setCargo(cargo);
    }

    private static void setHubs(OrderEntity order, String dep, String arr){
        Hub depHub = new Hub();
        Hub arrHub = new Hub();
        depHub.setName(dep);
        arrHub.setName(arr);
        order.setArrivalHub(arrHub);
        order.setDepartureHub(depHub);
    }


    private String generateTrackingId(String firstCity, String secondCity, long id) {
        final Random random = new Random();

        byte[] byteCity1 = firstCity.getBytes();
        byte[] byteCity2 = secondCity.getBytes();

        StringBuffer returnStr = new StringBuffer();
        for (byte a : byteCity1) {
            returnStr.append(a);
        }
        for (byte a : byteCity2) {
            returnStr.append(a);
        }
        returnStr.append(id);
        returnStr.append(random.nextInt());
        returnStr.append(System.currentTimeMillis() % 100);

        return returnStr.toString();
    }
}
