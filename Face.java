package com.kaogu.Algorithm;

import java.util.ArrayList;
import java.util.List;

public class Face {

    private List<Integer> dot_indices;
    private List<Double> angles;
    private NVector nVector;
    private Color color;

    public List<Integer> getDot_indices() {
        return dot_indices;
    }

    public void setDot_indices(List<Integer> dot_indices) {
        this.dot_indices = dot_indices;
    }

    public NVector getNVector() {
        return nVector;
    }

    public void setNVector(NVector nVector) {
        this.nVector = nVector;
    }

    public List<Double> getAngles() {
        return angles;
    }

    public void setAngles(List<Double> angles) {
        this.angles = angles;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Face() {}

    public Face(List<Integer> dot_indices) {
        this.dot_indices = dot_indices;
    }

    public List<Integer> sharingDot(Face face) {
        List<Integer> dots = face.getDot_indices();
        List<Integer> res = new ArrayList<>();
        for (int i : dot_indices) {
            if (dots.contains(i)) {
                res.add(i);
            }
        }
        return res;
    }
}
