package com.kaogu.Algorithm;

public class NVector {

    public double x;
    public double y;
    public double z;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public NVector add(NVector vector) {
        x += vector.getX();
        y += vector.getY();
        z += vector.getZ();
        return this;
    }

    public NVector() {}

    public NVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getRank(){
        double res = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
        return res;
    }

    public double DotProduct(NVector nVector) {
        double product = x * nVector.getX() + y * nVector.getY() + z * nVector.getZ();
        return product;
    }
}
