package com.cargohub.order_builder;

import java.util.Comparator;

public class DateComparator implements Comparator<UnpaidOrder> {
    @Override
    public int compare(UnpaidOrder o1, UnpaidOrder o2) {
        return o1.getEstimatedDeliveryDate().compareTo(o2.getEstimatedDeliveryDate());
    }
}
