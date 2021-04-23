package com.kaogu.Algorithm;

import java.util.List;

public class Face {

    private List<Integer> dot_indices;
    private Color color;

    public List<Integer> getDot_indices() {
        return dot_indices;
    }

    public void setDot_indices(List<Integer> dot_indices) {
        this.dot_indices = dot_indices;
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
}
