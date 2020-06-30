package com.cargohub.order_builder;

import com.cargoHub.neo4jGraph.model.RouteModel;
import com.cargoHub.neo4jGraph.service.RouteService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

public class FormUnpaidOrders {
    @Autowired
    RouteService routeService;
    @Setter
    Double pricePerKm;
    @Setter
    Double averageSpeed;

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
}
