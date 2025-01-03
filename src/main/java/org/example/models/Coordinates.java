package org.example.models;

public class Coordinates {

    private int xLocation;
    private int yLocation;

    public Coordinates(int xLocation, int yLocation) {
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public int getXLocation() {
        return xLocation;
    }

    public void setXLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }

    public void setYLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    @Override
    public String toString() {
        return "xLocation = " + xLocation + ",yLocation = " + yLocation;
    }
}
