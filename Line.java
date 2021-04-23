package com.kaogu.Algorithm;

import java.util.List;

public class Line {

    private int start;
    private int end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    public Line() {}

    public Line(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean equals(Line line) {
        if ((this.start == line.getStart() && this.end == line.getEnd()) | (this.start == line.getEnd() && this.end == line.getStart())) {
            return true;
        }else {
            return false;
        }
    }
}
