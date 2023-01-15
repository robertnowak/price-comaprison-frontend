package com.kodilla.prices.external.prices;

import com.kodilla.prices.domain.offer.AmazonOffer;
import org.apache.commons.lang3.RandomUtils;
import org.javamoney.moneta.FastMoney;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class OfferService {
    //todo connect via rest to backend
    private final List<AmazonOffer> offers = new ArrayList(IntStream.range(0, 3).mapToObj(i -> sampleOffer()).toList());

    public List<AmazonOffer> getOffers() {
        return offers;
    }

    public void addOffer(AmazonOffer amazonOffer) {
        offers.add(amazonOffer);
    }

    public AmazonOffer getOffer(String id) {
        return offers.get(0);
    }

    private AmazonOffer sampleOffer() {
        return new AmazonOffer("ABCD-" + RandomUtils.nextInt(), RandomUtils.nextInt() + "", FastMoney.of(RandomUtils.nextInt(0, 100), "PLN"));
    }

    public Collection<AmazonOffer> findByAsin(String asin) {
        return offers.stream().filter(it -> it.asin().contains(asin)).toList();
    }
}

