package org.example.models;

import org.example.utils.Utils;

public class Taxi {

    private Coordinates taxiCoordinates;
    private boolean isBusy;
    private Order currentOrder;

    public Taxi() {
        this.taxiCoordinates = Utils.generateRandomCoordinates();
        this.isBusy = false;
        this.currentOrder = null;
    }

    public Coordinates getTaxiCoordinates() {
        return taxiCoordinates;
    }

    public void setTaxiCoordinates(Coordinates taxiCoordinates) {
        this.taxiCoordinates = taxiCoordinates;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    @Override
    public String toString() {
        return "Taxi { " + this.taxiCoordinates + (this.isBusy ? "not " : "") + " available\n";
    }
}
