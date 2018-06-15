package com.aggregator.model;

import java.util.Objects;

public class CurrencyRate {


    private String code;
    private Double buy;
    private Double sell;


    public CurrencyRate() {
    }

    public CurrencyRate(String code, Double buy, Double sell) {
        this.code = code;
        this.buy = buy;
        this.sell = sell;
    }


    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "code='" + code + '\'' +
                ", buy=" + buy +
                ", sell=" + sell +
                '}';
    }
}
