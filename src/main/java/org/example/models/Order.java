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
        int dx = (int) (Math.random() * (Constants.maxDestination * 2 + 1)) - Constants.maxDestination;

        int maxDy = Constants.maxDestination - Math.abs(dx);

        int dy = (int) (Math.random() * (maxDy * 2 + 1)) - maxDy;

        if (dx == 0 && dy == 0) {
            dy = 1;
        }

        int destinationX = this.orderInitialCoordinates.getXLocation() + dx;
        int destinationY = this.orderInitialCoordinates.getYLocation() + dy;

        int cityRadius = Constants.cityRadius;
        destinationX = Math.max(0, Math.min(cityRadius, destinationX));
        destinationY = Math.max(0, Math.min(cityRadius, destinationY));

        System.out.printf("Generated dx: %d, dy: %d, Total distance: %d, Destination: (%d, %d)%n",
                dx, dy, Math.abs(dx) + Math.abs(dy), destinationX, destinationY);

        return new Coordinates(destinationX, destinationY);
    }

    @Override
    public String toString() {
        return "Order { located : " + this.orderInitialCoordinates +
                " ,going to " + this.orderDestinationCoordinates + " }";
    }
}
