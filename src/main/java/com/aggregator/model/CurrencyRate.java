package com.aggregator.model;

import com.aggregator.utils.CurrencyRateDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.opencsv.bean.CsvBindByPosition;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.Objects;

@JsonDeserialize(using = CurrencyRateDeserializer.class)
public final class CurrencyRate {

    @JsonProperty("code")
    @CsvBindByPosition(position = 0)
    private CurrencyUnit currencyRateCode;

    @JsonProperty("buy")
    @CsvBindByPosition(position = 1)
    private MonetaryAmount currencyRateBuyPrice;

    @JsonProperty("sell")
    @CsvBindByPosition(position = 2)
    private MonetaryAmount currencyRateSellPrice;

    public CurrencyRate() {
    }

    public CurrencyRate(final String code,
                        final Double buy,
                        final Double sell) {
        currencyRateCode = Monetary.getCurrency(code);
        currencyRateBuyPrice = Money.of(buy, currencyRateCode);
        currencyRateSellPrice = Money.of(sell, currencyRateCode);
    }

    public MonetaryAmount getCurrencyRateBuyPrice() {
        return currencyRateBuyPrice;
    }

    public void setCurrencyRateBuyPrice(final Double buy) {
        currencyRateBuyPrice = Money.of(buy, currencyRateCode);
    }

    public MonetaryAmount getCurrencyRateSellPrice() {
        return currencyRateSellPrice;
    }

    public void setCurrencyRateSellPrice(final Double sell) {
        currencyRateSellPrice = Money.of(sell, currencyRateCode);
    }

    public String getCurrencyRateCode() {
        return currencyRateCode.getCurrencyCode();
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
