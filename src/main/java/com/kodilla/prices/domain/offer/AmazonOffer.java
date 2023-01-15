package com.kodilla.prices.domain.offer;

import javax.money.MonetaryAmount;

public record AmazonOffer(String id, String asin, String title, MonetaryAmount currentPrice, MonetaryAmount targetPrice) {
}
