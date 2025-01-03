package org.example.utils;

import org.example.models.Coordinates;

public class Utils {

    private Utils(){}

    public static Coordinates generateRandomCoordinates(){
        int x = (int) (Math.random() * (Constants.cityRadius));
        int y = (int) (Math.random() * (Constants.cityRadius));
        return new Coordinates(x,y);
    }

}
