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
    private final Map<Order, Taxi> reservedOrders = new HashMap<>();

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
            taxis.add(new Taxi(i));
        }
        System.out.println("Initialized " + numTaxis + " taxis.");
    }

    public void addNewOrder() {
        Order newOrder = new Order();
        orderQueue.add(newOrder);
        System.out.println("New order added: " + newOrder);
        processOrders(); // Re-evaluate orders when a new one is added
    }

    public void processOrders() {
        System.out.println("Processing pending orders...");
        for (Order order : orderQueue) {
            if (!reservedOrders.containsKey(order)) {
                Taxi bestTaxi = findBestTaxiForOrder(order);
                if (bestTaxi != null) {
                    reservedOrders.put(order, bestTaxi);
                    System.out.println("Reserved order " + order + " for taxi " + bestTaxi.getId());
                }
            }
        }
    }

    private Taxi findBestTaxiForOrder(Order order) {
        Taxi bestTaxi = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Taxi taxi : taxis) {
            Coordinates taxiLocation = taxi.isBusy()
                    ? taxi.getCurrentOrder().getOrderDestinationCoordinates()
                    : taxi.getTaxiCoordinates();

            int distance = Math.abs(order.getOrderInitialCoordinates().getXLocation() - taxiLocation.getXLocation()) +
                    Math.abs(order.getOrderInitialCoordinates().getYLocation() - taxiLocation.getYLocation());

            if (distance < shortestDistance && (!taxi.isBusy() || !reservedOrders.containsValue(taxi))) {
                shortestDistance = distance;
                bestTaxi = taxi;
            }
        }

        return bestTaxi;
    }

    public void updateTaxiLocations(int elapsedTime) {
        System.out.println("=== Start of Taxi Update Cycle ===");
        for (Taxi taxi : taxis) {
            System.out.println("Processing Taxi " + taxi.getId() + " - Current State: " + taxi);

            if (taxi.isBusy() && taxi.getCurrentOrder() != null) {
                processTaxiMovement(taxi, elapsedTime);
            } else if (!taxi.isBusy()) {
                // Check if any reserved order matches this taxi
                for (Map.Entry<Order, Taxi> entry : reservedOrders.entrySet()) {
                    if (entry.getValue().equals(taxi)) {
                        Order reservedOrder = entry.getKey();
                        assignOrderToTaxi(taxi, reservedOrder);
                        reservedOrders.remove(reservedOrder);
                        orderQueue.remove(reservedOrder);
                        System.out.println("Taxi " + taxi.getId() + " assigned to reserved order: " + reservedOrder);
                        break;
                    }
                }
            } else {
                System.out.println("Taxi " + taxi.getId() + " is idle and waiting for an order.");
            }

            System.out.println("=== End of processing for Taxi " + taxi.getId() + " ===");
        }

        System.out.println("=== End of Taxi Update Cycle ===");
        System.out.println("Processing Unassigned Orders...");
        processOrders();
        System.out.println("=== End of Unassigned Orders Processing ===");
    }

    private void processTaxiMovement(Taxi taxi, int elapsedTime) {
        Coordinates current = taxi.getTaxiCoordinates();
        Order order = taxi.getCurrentOrder();

        Coordinates target = taxi.isHeadingToPickup()
                ? order.getOrderInitialCoordinates()
                : order.getOrderDestinationCoordinates();

        System.out.println("Step: Determine target location");
        System.out.println("Taxi " + taxi.getId() + " is heading to " +
                (taxi.isHeadingToPickup() ? "pickup" : "destination") + " at: " + target);

        int maxDistance = Constants.taxiSpeedMetersSeconds * elapsedTime;
        System.out.println("Step: Calculate maximum distance");
        System.out.println("Taxi " + taxi.getId() + " can move up to " + maxDistance + " meters this cycle.");

        if (current.getXLocation() != target.getXLocation() && maxDistance > 0) {
            int deltaX = target.getXLocation() - current.getXLocation();
            int moveX = Math.min(maxDistance, Math.abs(deltaX));
            current.setXLocation(current.getXLocation() + (deltaX > 0 ? moveX : -moveX));
            maxDistance -= moveX;
            System.out.println("Taxi " + taxi.getId() + " moved " + moveX + " meters along X-axis to: " + current);
        }

        if (current.getYLocation() != target.getYLocation() && maxDistance > 0) {
            int deltaY = target.getYLocation() - current.getYLocation();
            int moveY = Math.min(maxDistance, Math.abs(deltaY));
            current.setYLocation(current.getYLocation() + (deltaY > 0 ? moveY : -moveY));
            System.out.println("Taxi " + taxi.getId() + " moved " + moveY + " meters along Y-axis to: " + current);
        }

        if (current.equals(order.getOrderInitialCoordinates()) && taxi.isHeadingToPickup()) {
            System.out.println("Taxi " + taxi.getId() + " reached the pickup location.");
            taxi.setHeadingToPickup(false);
        } else if (current.equals(order.getOrderDestinationCoordinates()) && !taxi.isHeadingToPickup()) {
            System.out.println("Taxi " + taxi.getId() + " reached the destination.");
            taxi.setBusy(false);
            taxi.setCurrentOrder(null);
        }
    }

    public void assignOrderToTaxi(Taxi taxi, Order order) {
        taxi.setCurrentOrder(order);
        taxi.setBusy(true);
        taxi.setHeadingToPickup(true);
    }

    public void startSimulation() {
        serviceRunning = true;

        new Thread(() -> {
            while (serviceRunning) {
                try {
                    Thread.sleep(Constants.orderFrequency * 1000);

                    addNewOrder();

                    updateTaxiLocations(Constants.orderFrequency);

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
        System.out.println("Reserved Orders: " + reservedOrders.size());
        System.out.println("Taxis Status:");
        for (int i = 0; i < taxis.size(); i++) {
            System.out.println("Taxi " + i + ": " + taxis.get(i));
        }
        System.out.println("=====================");
    }
}
