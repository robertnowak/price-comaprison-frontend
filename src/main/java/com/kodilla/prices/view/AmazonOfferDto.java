package com.kodilla.prices.view;

import com.kodilla.prices.domain.offer.AmazonOffer;
import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;

public class AmazonOfferDto {


    private String asin;
    private String userId;
    private Double targetPrice;
    private CurrencyUnit targetCurrency;

    public AmazonOfferDto(String asin, String userId, Double targetPrice, CurrencyUnit targetCurrency) {
        this.asin = asin;
        this.userId = userId;
        this.targetPrice = targetPrice;
        this.targetCurrency = targetCurrency;
    }

    public AmazonOfferDto() {
    }

    public AmazonOfferDto(AmazonOffer amazonOffer) {
        this(amazonOffer.asin(), amazonOffer.userID(), amazonOffer.targetPrice().getNumber().doubleValue(), amazonOffer.targetPrice().getCurrency());
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public CurrencyUnit getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyUnit targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public AmazonOffer toDomain() {
        return new AmazonOffer(
                asin,
                userId,
                FastMoney.of(targetPrice, targetCurrency.getCurrencyCode())
        );
    }

}
