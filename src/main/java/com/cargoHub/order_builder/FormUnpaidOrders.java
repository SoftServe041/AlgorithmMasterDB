package com.cargohub.order_builder;

import com.cargohub.neo4jGraph.model.RouteModel;
import com.cargohub.service.impl.RouteService;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FormUnpaidOrders {
    final
    RouteService routeService;
    @Setter
    Double pricePerKm = 10d;
    @Setter
    Double averageSpeed = 10d;

    public FormUnpaidOrders(RouteService routeService) {
        this.routeService = routeService;
    }

    public Map<String, List<UnpaidOrder>> formUnpaidOrders(String departure, String arrival) {
        Map<String, List<UnpaidOrder>> unpaidOrdersMap = new HashMap<>();
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
