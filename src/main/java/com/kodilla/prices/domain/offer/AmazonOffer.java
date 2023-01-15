package com.kodilla.prices.domain.offer;

import javax.money.MonetaryAmount;

public record AmazonOffer(String asin, String userID, MonetaryAmount targetPrice) {
}
