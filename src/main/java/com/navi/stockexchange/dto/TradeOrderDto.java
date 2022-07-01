package com.navi.stockexchange.dto;

public class TradeOrderDto {
    private String buyOrderId;
    private String sellOrderId;
    private Double price;
    private Long quantity;

    public TradeOrderDto() {
    }

    public TradeOrderDto(String buyOrderId, String sellOrderId, Double price, Long quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String buyOrderId;
        private String sellOrderId;
        private Double price;
        private Long quantity;

        public Builder setBuyOrderId(String buyOrderId) {
            this.buyOrderId = buyOrderId;
            return this;
        }

        public Builder setSellOrderId(String sellOrderId) {
            this.sellOrderId = sellOrderId;
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

        public TradeOrderDto build() {
            return new TradeOrderDto(buyOrderId, sellOrderId, price, quantity);
        }
    }

    @Override
    public String toString() {
        return "TradeOrderDto{" +
                "buyOrderId='" + buyOrderId + '\'' +
                ", sellOrderId='" + sellOrderId + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
