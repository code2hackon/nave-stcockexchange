package com.navi.stockexchange.enums;

public enum OrderType {
    BUY("BUY"),
    SELL("SELL");

    public final String value;

    OrderType(String value) {
        this.value = value;
    }
}
