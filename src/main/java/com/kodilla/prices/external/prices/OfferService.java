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

    //todo connect via rest to backend
    private final Map<String, AmazonOffer> offers = new HashMap<>(IntStream.range(0, 3).mapToObj(i -> sampleOffer()).collect(Collectors.toMap(AmazonOffer::id, Function.identity())));

    public List<AmazonOffer> getOffersFromActualDb() {
        return offersClient.getOffers();
    }

    public List<AmazonOffer> getOffers() {
        return offers.values().stream().map(offer ->
                new AmazonOffer(offer.id(),
                        offer.asin(),
                        "a title"  + stringId(), //it's from db, not kept in frontend so I mock it on read
                        money("EUR"),
                        offer.targetPrice()
                        )).sorted(Comparator.comparing(AmazonOffer::asin)).toList();
    }


    public void addOrUpdateOffer(AmazonOffer amazonOffer) {
        if (amazonOffer.id() == null) {
            storeInDatabase(amazonOffer);
        } else {
            updateInDatabase(amazonOffer);
        }
    }

    private void updateInDatabase(AmazonOffer amazonOffer) {
        offers.put(amazonOffer.id(), amazonOffer);
    }

    private void storeInDatabase(AmazonOffer amazonOffer) {
        AmazonOffer newOffer = new AmazonOffer(stringId(), amazonOffer.asin(), amazonOffer.title(), money("USD"), amazonOffer.targetPrice());
        offers.put(newOffer.id(), newOffer);
    }

    private AmazonOffer sampleOffer() {
        return new AmazonOffer(stringId(),
                "ASIN-" + RandomUtils.nextInt(),
                "a title " + stringId(),
                money("EUR"),
                money("PLN"));
    }

    private static FastMoney money(String currency) {
        return FastMoney.of(RandomUtils.nextInt(0, 100), currency);
    }

    private static String stringId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    public Collection<AmazonOffer> findByAsin(String asin) {
        return offers.values().stream().filter(amazonOffer -> amazonOffer.asin().contains(asin)).toList();
    }

    public void delete(String offerId) {
        offers.remove(offerId);
    }
}

