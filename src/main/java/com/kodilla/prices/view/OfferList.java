package com.kodilla.prices.view;

import com.kodilla.prices.domain.offer.AmazonOffer;
import com.kodilla.prices.external.prices.OfferService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main view contains a button and a click listener.
 */
@Route("/")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class OfferList extends VerticalLayout {

    private final OfferService offerService;
    private TextField asinFilter = new TextField();

    private final Grid<AmazonOffer> offerGrid = new Grid<>(AmazonOffer.class);

    private final Button addButton = new Button("Add new");

    private final Button refreshAllButton = new Button("Refresh all");

    public OfferList(@Autowired OfferService offerService) {
        this.offerService = offerService;
        asinFilter.setPlaceholder("Filter by asin code");
        asinFilter.setClearButtonVisible(true);
        asinFilter.setValueChangeMode(ValueChangeMode.EAGER);
        asinFilter.addValueChangeListener(e -> applyAsinFilter());

        offerGrid.addColumn(AmazonOffer::asin).setHeader("Asin").setSortable(true);
        offerGrid.addColumn(AmazonOffer::title).setHeader("Title").setSortable(true);
        offerGrid.addColumn(AmazonOffer::currentPrice).setHeader("Current price");
        offerGrid.addColumn(AmazonOffer::targetPrice).setHeader("Alert price").setSortable(true);

        offerGrid.addComponentColumn(offer -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(event -> {
                editButton.getUI()
                        .flatMap(ui -> ui.navigate(AddOffer.class))
                        .ifPresent(editor -> editor.setAmazonOfferDto(new AmazonOfferDto(offer)));
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        offerGrid.addComponentColumn(offer -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(event -> {
                offerService.delete(offer.id());
                refresh();
            });
            return deleteButton;
        }).setWidth("150px").setFlexGrow(0);


        offerGrid.addComponentColumn(offer -> {
            Button refreshButton = new Button("Refresh");
            refreshButton.addClickListener(event -> {
                offerService.refresh(offer.asin());
                refresh();
            });
            return refreshButton;
        }).setWidth("150px").setFlexGrow(0);

        Component bottomButtons = bottomButtons(offerService);
        refresh();

        add(asinFilter, offerGrid, bottomButtons);

        setSizeFull();
        refresh();
    }

    private Component bottomButtons(OfferService offerService) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        addButton.addClickListener(event ->
                addButton.getUI()
                        .flatMap(ui -> ui.navigate(AddOffer.class)));

        refreshAllButton.addClickListener(event -> offerService.refreshAll());
        buttonLayout.add(addButton, refreshAllButton);
        return buttonLayout;
    }

    private void applyAsinFilter() {
        offerGrid.setItems(offerService.findByAsin(asinFilter.getValue()));
    }

    public void refresh() {
        offerGrid.setItems(offerService.getOffers());
    }

}
