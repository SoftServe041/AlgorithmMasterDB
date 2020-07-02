package com.cargohub.order_builder;

import com.cargohub.models.OrderModel;
import com.cargohub.models.RouteModel;
import com.cargohub.service.impl.RouteService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class FormUnpaidOrders {

    @Autowired
    RouteService routeService;

    @Setter
    Double pricePerKm = 1.4;

    @Setter
    Double averageSpeed = 60d;
    Double truckVolume = 80d;
    Double carryingCapacity = 22d;

    public Map<String, List<UnpaidOrder>> formUnpaidOrders(String departure, String arrival) {
        Map<String,List<UnpaidOrder>> unpaidOrdersMap = new HashMap<>();
        List<RouteModel> routes = routeService.getRoute(departure, arrival);
        List<UnpaidOrder> unpaidOrders = new ArrayList<>();
        routes.forEach((routeModel) -> {
            int price = (int) (routeModel.getDistance() * pricePerKm);
            double hours = routeModel.getDistance() / averageSpeed;
            int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
            LocalDate localDate = LocalDate.now().plusDays(days);
            UnpaidOrder unpaidOrder = new UnpaidOrder();
            unpaidOrder.setEstimatedDeliveryDate(localDate);
            unpaidOrder.setPrice(price);
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
        List<RouteModel> routes = routeService.getRoute(orderModel.getDepartureHub(), orderModel.getArrivalHub());
        List<UnpaidOrder> unpaidOrders = new ArrayList<>();
        routes.forEach((routeModel) -> {
            int price = (int) (routeModel.getDistance() * countPriceForRoute(orderModel));
            double hours = routeModel.getDistance() / averageSpeed;
            int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
            LocalDate localDate = LocalDate.now().plusDays(days);
            UnpaidOrder unpaidOrder = new UnpaidOrder();
            unpaidOrder.setEstimatedDeliveryDate(localDate);
            unpaidOrder.setPrice(price);
            unpaidOrders.add(unpaidOrder);
        });
        unpaidOrders.sort(new PriceComparator());
        unpaidOrdersMap.put("priceSorted", unpaidOrders);
        unpaidOrders.sort(new DateComparator());
        unpaidOrdersMap.put("dateSorted", unpaidOrders);
        return unpaidOrdersMap;
    }

    private int countPriceForRoute(OrderModel orderModel) {
        int volumeOfCargo = (int) Math.ceil(orderModel.getCargoHeight() * orderModel.getCargoLength() * orderModel.getCargoWidth());
        double cub = carryingCapacity / truckVolume; // ? weight in 1 m^3 according to truck properties

        double cargoWeight = orderModel.getCargoWeight() / 1000; // tonne

        double admittedWeightForCargo = cub * volumeOfCargo; // determine how much is applicable for cargoVolume
        while (admittedWeightForCargo < cargoWeight - 0.030) { // increase admittedWeight while it equals approximately to cargoWeight
            admittedWeightForCargo = cub * (++volumeOfCargo);
        }
        int price = (int) Math.ceil(admittedWeightForCargo * pricePerKm);
        return price;
    }

}