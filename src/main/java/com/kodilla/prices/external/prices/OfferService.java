package com.kodilla.prices.external.prices;

import com.kodilla.prices.domain.offer.AmazonOffer;
import org.apache.commons.lang3.RandomUtils;
import org.javamoney.moneta.FastMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OfferService {

    private final OffersClient offersClient;

    public OfferService(@Autowired OffersClient offersClient) {
        this.offersClient = offersClient;
    }

    public List<AmazonOffer> getOffers() {
        return offersClient.getOffers();
    }

    public void addOrUpdateOffer(AmazonOffer amazonOffer) {
        if (amazonOffer.id() == null) {
            storeInDatabase(amazonOffer);
        } else {
            updateInDatabase(amazonOffer);
        }
    }

    private void updateInDatabase(AmazonOffer amazonOffer) {
        offersClient.addOffer(AmazonOfferDto.fromDomain(amazonOffer));
    }

    private void storeInDatabase(AmazonOffer amazonOffer) {
        offersClient.addOffer(AmazonOfferDto.fromDomain(amazonOffer));
    }

    public Collection<AmazonOffer> findByAsin(String asin) {
        return getOffers().stream().filter(amazonOffer -> amazonOffer.asin().contains(asin)).toList();
    }

    public void delete(String offerId) {
        offersClient.delete(offerId);
    }
}

