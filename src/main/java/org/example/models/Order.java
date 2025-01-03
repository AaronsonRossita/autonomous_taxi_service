package org.example.models;

import org.example.utils.Constants;
import org.example.utils.Utils;

public class Order {

    private final Coordinates orderInitialCoordinates;
    private final Coordinates orderDestinationCoordinates;

    public Order() {
        this.orderInitialCoordinates = generateOriginalCoordinates();
        this.orderDestinationCoordinates = generateDestination();
    }

    public Coordinates getOrderInitialCoordinates() {
        return orderInitialCoordinates;
    }

    public Coordinates getOrderDestinationCoordinates() {
        return orderDestinationCoordinates;
    }

    private Coordinates generateOriginalCoordinates() {
        return Utils.generateRandomCoordinates();
    }

    private Coordinates generateDestination() {
        int dx, dy;

        do {
            dx = (int) ((Math.random() * (Constants.maxDestination * 2 + 1)) - Constants.maxDestination);
            dy = (int) ((Math.random() * (Constants.maxDestination + 1 - Math.abs(dx) + 1)) - (Constants.maxDestination + 1 - Math.abs(dx)) / 2);
        } while (Math.abs(dx) + Math.abs(dy) > Constants.maxDestination);

        int destinationX = this.orderInitialCoordinates.getXLocation() + dx;
        int destinationY = this.orderInitialCoordinates.getYLocation() + dy;

        return new Coordinates(destinationX, destinationY);
    }

    @Override
    public String toString() {
        return "Order { located : " + this.orderInitialCoordinates +
                " ,going to " + this.orderDestinationCoordinates + " }";
    }
}
