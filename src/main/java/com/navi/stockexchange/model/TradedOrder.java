package com.navi.stockexchange.model;

public class TradedOrder {
    private Order buyOrder;
    private Order sellOrder;
    private Double price;
    private Long quantity;

    public TradedOrder() {
    }

    public TradedOrder(Order buyOrder, Order sellOrder, Double price, Long quantity) {
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.price = price;
        this.quantity = quantity;
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(Order buyOrder) {
        this.buyOrder = buyOrder;
    }

    public Order getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(Order sellOrder) {
        this.sellOrder = sellOrder;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public static Order.Builder builder() {
        return new Order.Builder();
    }

    public static class Builder {
        private Order buyOrder;
        private Order sellOrder;
        private Double price;
        private Long quantity;

        public Builder setBuyOrder(Order buyOrder) {
            this.buyOrder = buyOrder;
            return this;
        }

        public Builder setSellOrder(Order sellOrder) {
            this.sellOrder = sellOrder;
            return this;
        }

        public Builder setPrice(Double price) {
            this.price = price;
            return this;
        }

        public Builder setQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public TradedOrder build() {
            return new TradedOrder(buyOrder, sellOrder, price, quantity);
        }
    }

    @Override
    public String toString() {
        return "TradedOrder{" +
                "buyOrder=" + buyOrder +
                ", sellOrder=" + sellOrder +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
