package com.kaogu.Algorithm;

public class DoubleLinkedNode<T> {

    private T val;
    private DoubleLinkedNode prev;
    private DoubleLinkedNode next;

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
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

    public DoubleLinkedNode(T val) {
        this.val = val;
    }

    public DoubleLinkedNode(T val, DoubleLinkedNode prev, DoubleLinkedNode next) {
        this.val = val;
        this.prev = prev;
        this.next = next;
    }
}
