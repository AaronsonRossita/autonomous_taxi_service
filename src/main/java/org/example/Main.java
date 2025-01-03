package org.example;

import org.example.services.TaxiHandler;
import org.example.utils.Constants;

public class Main {


    public static void main(String[] args) {

        TaxiHandler taxiHandler = TaxiHandler.getInstance();
        taxiHandler.initializeTaxis(Constants.taxiAmount);

        taxiHandler.startSimulation();

    }
}