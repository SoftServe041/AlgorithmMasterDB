package com.cargohub.order_builder;

import java.util.Comparator;

public class PriceComparator implements Comparator<UnpaidOrder> {
    @Override
    public int compare(UnpaidOrder o1, UnpaidOrder o2) {
        return o1.getPrice().compareTo(o2.getPrice());
    }
}
