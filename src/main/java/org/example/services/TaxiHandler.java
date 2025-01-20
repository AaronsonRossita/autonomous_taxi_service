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
            taxis.add(new Taxi(i));
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
        System.out.println("=== Start of Taxi Update Cycle ===");
        for (Taxi taxi : taxis) {
            System.out.println("Processing Taxi " + taxi.getId() + " - Current State: " + taxi);

            if (taxi.isBusy() && taxi.getCurrentOrder() != null) {
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
                    //maxDistance -= moveY;
                    System.out.println("Taxi " + taxi.getId() + " moved " + moveY + " meters along Y-axis to: " + current);
                }

                System.out.println("Step: Check if taxi reached target location");
                if (current.equals(order.getOrderInitialCoordinates()) && taxi.isHeadingToPickup()) {
                    System.out.println("Taxi " + taxi.getId() + " reached the pickup location.");
                    taxi.setHeadingToPickup(false);
                    System.out.println("Taxi " + taxi.getId() + " is now heading to destination: " + order.getOrderDestinationCoordinates());
                } else if (current.equals(order.getOrderDestinationCoordinates()) && !taxi.isHeadingToPickup()) {
                    System.out.println("Taxi " + taxi.getId() + " reached the destination.");
                    taxi.setBusy(false);
                    taxi.setCurrentOrder(null);
                    System.out.println("Taxi " + taxi.getId() + " is now idle.");

                    if (!orderQueue.isEmpty()) {
                        Order nextOrder = orderQueue.poll();
                        assignOrderToTaxi(taxi, nextOrder);
                        System.out.println("Taxi " + taxi.getId() + " assigned to new order: " + nextOrder);
                    } else {
                        System.out.println("Taxi " + taxi.getId() + " has no pending orders.");
                    }
                } else {
                    System.out.println("Taxi " + taxi.getId() + " is still en route to target location.");
                }
            } else {
                if (taxi.isBusy()) {
                    System.out.println("Taxi " + taxi.getId() + " has no current order but is marked as busy.");
                } else {
                    System.out.println("Taxi " + taxi.getId() + " is idle and waiting for an order.");
                }
            }
            System.out.println("=== End of processing for Taxi " + taxi.getId() + " ===");
        }

        System.out.println("=== End of Taxi Update Cycle ===");
        System.out.println("Processing Unassigned Orders...");
        processOrders();
        System.out.println("=== End of Unassigned Orders Processing ===");
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
        System.out.println("Taxis Status:");
        for (int i = 0; i < taxis.size(); i++) {
            System.out.println("Taxi " + i + ": " + taxis.get(i));
        }
        System.out.println("=====================");
    }
}
