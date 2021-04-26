package com.kaogu.Algorithm;

public class DoubleLinkedNode {

    private Dot dot;
    private DoubleLinkedNode prev;
    private DoubleLinkedNode next;

    public Dot getDot() {
        return dot;
    }

    public void setDot(Dot dot) {
        this.dot = dot;
    }

    public DoubleLinkedNode getPrev() {
        return prev;
    }

    public void setPrev(DoubleLinkedNode prev) {
        this.prev = prev;
    }

    public DoubleLinkedNode getNext() {
        return next;
    }

    public void setNext(DoubleLinkedNode next) {
        this.next = next;
    }

    public DoubleLinkedNode() {

    }

    public DoubleLinkedNode(Dot dot) {
        this.dot = dot;
    }

    public DoubleLinkedNode(Dot dot, DoubleLinkedNode prev, DoubleLinkedNode next) {
        this.dot = dot;
        this.prev = prev;
        this.next = next;
    }
}
