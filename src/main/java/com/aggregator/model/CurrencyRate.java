package com.aggregator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class CurrencyRate {

    @JsonProperty("code")
    private String currencyRateCode;

    @JsonProperty("buy")
    private Double currencyRateBuyPrice;

    @JsonProperty("sell")
    private Double currencyRateSellPrice;

    public CurrencyRate() {
    }

    public CurrencyRate(final String code,
                        final Double buy,
                        final Double sell) {
        this.currencyRateCode = code;
        this.currencyRateBuyPrice = buy;
        this.currencyRateSellPrice = sell;
    }

    public Double getCurrencyRateBuyPrice() {
        return currencyRateBuyPrice;
    }

    public void setCurrencyRateBuyPrice(final Double buy) {
        currencyRateBuyPrice = buy;
    }

    public Double getCurrencyRateSellPrice() {
        return currencyRateSellPrice;
    }

    public void setCurrencyRateSellPrice(final Double sell) {
        currencyRateSellPrice = sell;
    }

    public String getCurrencyRateCode() {
        return currencyRateCode;
    }

    public void setCurrencyRateCode(final String code) {
        currencyRateCode = code;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(currencyRateCode, that.currencyRateCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyRateCode);
    }

    @Override
    public String toString() {
        return "CurrencyRate{"
                + "currencyRateCode='" + currencyRateCode + '\''
                + ", currencyRateBuyPrice=" + currencyRateBuyPrice
                + ", currencyRateSellPrice=" + currencyRateSellPrice
                + '}';
    }
}
