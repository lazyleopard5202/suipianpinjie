package com.kaogu.Algorithm;

public class Dij {

    int index;
    double distance;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Dij(int index, double distance) {
        this.index = index;
        this.distance = distance;
    }
}
