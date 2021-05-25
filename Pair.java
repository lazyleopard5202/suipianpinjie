package com.kaogu.Algorithm;

public class Pair {

    private Dot dot1;
    private Dot dot2;

    public Dot getDot1() {
        return dot1;
    }

    public void setDot1(Dot dot1) {
        this.dot1 = dot1;
    }

    public Dot getDot2() {
        return dot2;
    }

    public void setDot2(Dot dot2) {
        this.dot2 = dot2;
    }

    public Pair() {

    }

    public Pair(Dot dot1, Dot dot2) {
        this.dot1 = dot1;
        this.dot2 = dot2;
    }
}
