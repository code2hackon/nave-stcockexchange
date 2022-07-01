package com.navi.stockexchange.service.comparators;

import com.navi.stockexchange.model.Order;

import java.util.Comparator;

public class OrderComparatorByDescendingPriceAndAscendingTime implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        if(Double.compare(o1.getPrice(), o2.getPrice()) == 0)
            return Long.compare(o1.getTime(), o2.getTime());
        return o2.getPrice().compareTo(o1.getPrice());
    }
}
