package org.example.models;

import org.example.utils.Utils;

public class Taxi {

    private final int id;
    private Coordinates taxiCoordinates;
    private boolean isBusy;
    private boolean isHeadingToPickup;
    private Order currentOrder;

    public Taxi(int id) {
        this.id = id;
        this.taxiCoordinates = Utils.generateRandomCoordinates();
        this.isBusy = false;
        this.isHeadingToPickup = true;
        this.currentOrder = null;
    }

    public Coordinates getTaxiCoordinates() {
        return taxiCoordinates;
    }

    public void setTaxiCoordinates(Coordinates taxiCoordinates) {
        this.taxiCoordinates = taxiCoordinates;
    }

    public int getId() {
        return id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean isHeadingToPickup() {
        return isHeadingToPickup;
    }

    public void setHeadingToPickup(boolean headingToPickup) {
        isHeadingToPickup = headingToPickup;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    @Override
    public String toString() {
        String targetLocation;
        if (currentOrder == null) {
            targetLocation = "No active order";
        } else if (isHeadingToPickup) {
            targetLocation = "Pickup location: " + currentOrder.getOrderInitialCoordinates();
        } else {
            targetLocation = "Destination: " + currentOrder.getOrderDestinationCoordinates();
        }

        return "Taxi number " + id + " { " + this.taxiCoordinates + (this.isBusy ? " (busy)" : " (available)") +
                "\nCurrently " + targetLocation + " }";
    }
}
