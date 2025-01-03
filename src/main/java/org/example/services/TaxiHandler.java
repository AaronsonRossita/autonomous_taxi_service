package org.example.services;

import org.example.models.Coordinates;
import org.example.models.Taxi;
import org.example.models.Order;
import org.example.utils.Constants;

import java.util.*;

public class TaxiHandler {

    private static TaxiHandler instance;
    private final List<Taxi> taxis;
    private boolean serviceRunning;
    private final Queue<Order> orderQueue = new LinkedList<>();

    private TaxiHandler() {
        this.taxis = new ArrayList<>();
        this.serviceRunning = false;
    }

    public static TaxiHandler getInstance() {
        if (instance == null) {
            synchronized (TaxiHandler.class) {
                if (instance == null) {
                    instance = new TaxiHandler();
                }
            }
        }
        return instance;
    }

    public void initializeTaxis(int numTaxis) {
        for (int i = 0; i < numTaxis; i++) {
            taxis.add(new Taxi());
        }
        System.out.println("Initialized " + numTaxis + " taxis.");
    }

    public void addNewOrder() {
        Order newOrder = new Order();
        orderQueue.add(newOrder);
        System.out.println("New order added: " + newOrder);
    }

    public Taxi allocateTaxi(Coordinates startLocation) {
        Taxi nearestTaxi = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Taxi taxi : taxis) {
            if (!taxi.isBusy()) {
                int distance = Math.abs(startLocation.getXLocation() - taxi.getTaxiCoordinates().getXLocation()) +
                        Math.abs(startLocation.getYLocation() - taxi.getTaxiCoordinates().getYLocation());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestTaxi = taxi;
                }
            }
        }
        return nearestTaxi;
    }

    public void processOrders() {
        Iterator<Order> iterator = orderQueue.iterator();

        while (iterator.hasNext()) {
            Order order = iterator.next();
            Taxi nearestTaxi = allocateTaxi(order.getOrderInitialCoordinates());

            if (nearestTaxi != null) {
                assignOrderToTaxi(nearestTaxi, order);
                iterator.remove();
                System.out.println("Order assigned to taxi: " + nearestTaxi);
            }
        }
    }

    public void updateTaxiLocations(int elapsedTime) {
        for (Taxi taxi : taxis) {
            if (taxi.isBusy() && taxi.getCurrentOrder() != null) {
                Coordinates current = taxi.getTaxiCoordinates();
                Order order = taxi.getCurrentOrder();
                Coordinates target;

                if (!current.equals(order.getOrderInitialCoordinates())) {
                    target = order.getOrderInitialCoordinates();
                } else {
                    target = order.getOrderDestinationCoordinates();
                }

                int deltaX = target.getXLocation() - current.getXLocation();
                int deltaY = target.getYLocation() - current.getYLocation();

                int maxMovement = Constants.taxiSpeedMetersSeconds * elapsedTime;
                int moveX = Math.min(Math.abs(deltaX), maxMovement);
                int moveY = 0;

                if (moveX < maxMovement) {
                    moveY = Math.min(Math.abs(deltaY), maxMovement - moveX);
                }

                current.setXLocation(current.getXLocation() + (deltaX > 0 ? moveX : -moveX));
                current.setYLocation(current.getYLocation() + (deltaY > 0 ? moveY : -moveY));

                if (current.equals(target)) {
                    if (target.equals(order.getOrderDestinationCoordinates())) {
                        taxi.setBusy(false);
                        taxi.setCurrentOrder(null);
                        System.out.println("Taxi " + taxi + " completed an order.");
                    }
                }
            }
        }
    }

    public void assignOrderToTaxi(Taxi taxi, Order order) {
        taxi.setCurrentOrder(order);
        taxi.setBusy(true);
    }

    public int calculateBusyTime(Coordinates taxiLocation, Coordinates startLocation, Coordinates endLocation) {
        int distanceToPickup = Math.abs(startLocation.getXLocation() - taxiLocation.getXLocation()) +
                Math.abs(startLocation.getYLocation() - taxiLocation.getYLocation());
        int distanceToDestination = Math.abs(endLocation.getXLocation() - startLocation.getXLocation()) +
                Math.abs(endLocation.getYLocation() - startLocation.getYLocation());
        int totalDistance = distanceToPickup + distanceToDestination;

        return (int) Math.ceil(totalDistance / Constants.taxiSpeedMetersSeconds);
    }

    public void startSimulation() {
        serviceRunning = true;

        new Thread(() -> {
            while (serviceRunning) {
                try {
                    Thread.sleep(Constants.orderFrequency * 1000);

                    addNewOrder();

                    updateTaxiLocations(Constants.orderFrequency);

                    processOrders();

                    printStatus();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void printStatus() {
        System.out.println("=== System Status ===");
        System.out.println("Pending Orders: " + orderQueue.size());
        System.out.println("Taxis Status:");
        for (int i = 0; i < taxis.size(); i++) {
            System.out.println("Taxi " + i + ": " + taxis.get(i));
        }
        System.out.println("=====================");
    }
}
