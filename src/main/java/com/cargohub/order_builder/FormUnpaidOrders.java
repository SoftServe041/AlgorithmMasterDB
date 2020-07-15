package com.cargohub.order_builder;

import com.cargohub.models.CargoSizeModel;
import com.cargohub.models.OrderModel;
import com.cargohub.models.RouteModel;
import com.cargohub.service.impl.RouteNeo4jServiceImpl;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class FormUnpaidOrders {

    @Autowired
    RouteNeo4jServiceImpl routeNeo4jServiceImpl;

    @Setter
    Double pricePerKm = 1.4;

    @Setter
    Double averageSpeed = 60d;
    Double truckVolume = 80d;
    Double carryingCapacity = 22d;

    public Map<String, List<UnpaidOrder>> formUnpaidOrders(String departure, String arrival) {
        Map<String,List<UnpaidOrder>> unpaidOrdersMap = new HashMap<>();
        List<RouteModel> routes = routeNeo4jServiceImpl.getRoute(departure, arrival);
        List<UnpaidOrder> unpaidOrders = new ArrayList<>();
        routes.forEach((routeModel) -> {
            int price = (int) (routeModel.getDistance() * pricePerKm);
            double hours = routeModel.getDistance() / averageSpeed;
            int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
            LocalDate localDate = LocalDate.now().plusDays(days);
            UnpaidOrder unpaidOrder = new UnpaidOrder();
            unpaidOrder.setEstimatedDeliveryDate(localDate);
            unpaidOrder.setPrice(price);
            unpaidOrder.setHubs(routeModel.getRoutes());
            unpaidOrders.add(unpaidOrder);
        });
        unpaidOrders.sort(new PriceComparator());
        unpaidOrdersMap.put("priceSorted", unpaidOrders);
        unpaidOrders.sort(new DateComparator());
        unpaidOrdersMap.put("dateSorted", unpaidOrders);
        return unpaidOrdersMap;
    }

    public Map<String, List<UnpaidOrder>> formUnpaidOrders(OrderModel orderModel) {
        Map<String,List<UnpaidOrder>> unpaidOrdersMap = new HashMap<>();
        List<RouteModel> routes = routeNeo4jServiceImpl.getRoute(orderModel.getDepartureHub(), orderModel.getArrivalHub());
        List<UnpaidOrder> unpaidOrders = new ArrayList<>();
        routes.forEach((routeModel) -> {
            int price = (int) (routeModel.getDistance() * countPriceForRoute(orderModel));
            double hours = routeModel.getDistance() / averageSpeed;
            int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
            LocalDate localDate = LocalDate.now().plusDays(days);
            UnpaidOrder unpaidOrder = new UnpaidOrder();
            unpaidOrder.setEstimatedDeliveryDate(localDate);
            unpaidOrder.setPrice(price);
            unpaidOrder.setHubs(routeModel.getRoutes());
            unpaidOrders.add(unpaidOrder);
        });
        unpaidOrders.sort(new PriceComparator());
        unpaidOrdersMap.put("priceSorted", unpaidOrders);
        unpaidOrders.sort(new DateComparator());
        unpaidOrdersMap.put("dateSorted", unpaidOrders);
        return unpaidOrdersMap;
    }

    private int countPriceForRoute(OrderModel orderModel) {
        int price = 0;
        for (CargoSizeModel cargoSizeModel : orderModel.getSizeList()) {
            price += getPrice(cargoSizeModel);
        }
        return price;
    }

    private int getPrice(CargoSizeModel cargoSizeModel) {
        double volumeOfCargo = cargoSizeModel.getCargoHeight() * cargoSizeModel.getCargoLength() * cargoSizeModel.getCargoWidth();
        double cub = carryingCapacity / truckVolume; // ? weight in 1 m^3 according to truck properties

        double cargoWeight = cargoSizeModel.getCargoWeight() / 1000; // tonne

        double admittedWeightForCargo = cub * volumeOfCargo; // determine how much is applicable for cargoVolume
        while (admittedWeightForCargo < cargoWeight - 0.030) { // increase admittedWeight while it equals approximately to cargoWeight
            volumeOfCargo += 0.1;
            admittedWeightForCargo = cub * volumeOfCargo;
        }
        return (int) Math.ceil(admittedWeightForCargo * pricePerKm);
    }

}
