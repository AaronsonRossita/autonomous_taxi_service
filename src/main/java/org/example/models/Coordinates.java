package org.example.models;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinates other = (Coordinates) obj;
        return xLocation == other.xLocation && yLocation == other.yLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xLocation, yLocation);
    }

    @Override
    public String toString() {
        return "xLocation = " + xLocation + ",yLocation = " + yLocation;
    }
}
