package com.kodilla.prices.view;

import com.kodilla.prices.domain.offer.SupportedCurrencies;
import com.kodilla.prices.external.prices.OfferService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import javax.money.CurrencyUnit;

@Route("addOffer")
public class AddOffer extends FormLayout {

    private final transient OfferService offerService;

    private transient AmazonOfferDto amazonOfferDto = new AmazonOfferDto();

    @PropertyId("asin")
    private final TextField asin = new TextField("Asin");

    @PropertyId("targetPrice")
    private final NumberField targetPriceField = new NumberField("Alert price");

    @PropertyId("targetCurrency")
    private final Select<CurrencyUnit> currencySelectField = new Select<>();

    private final Button save = new Button("Save");

    private final Binder<AmazonOfferDto> binder = new Binder<>(AmazonOfferDto.class);


    public AddOffer(OfferService offerService) {
        this.offerService = offerService;

        HorizontalLayout fields = new HorizontalLayout(asin, targetPriceField, currencySelectField);

        HorizontalLayout buttons = new HorizontalLayout(save);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> save());

        currencySelectField.setLabel("Currency");
        currencySelectField.setItems(SupportedCurrencies.CURRENCIES);

        binder.bindInstanceFields(this);

        add(fields, buttons);
    }

    private void save() {
        try {
            binder.writeBean(amazonOfferDto);
            offerService.addOrUpdateOffer(amazonOfferDto.toDomain());
            save.getUI().flatMap(ui -> ui.navigate(OfferList.class));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

    }

    public void setAmazonOfferDto(AmazonOfferDto amazonOfferDto) {
        this.amazonOfferDto = amazonOfferDto;
        binder.readBean(amazonOfferDto);
    }


}
