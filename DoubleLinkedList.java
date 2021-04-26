package com.kaogu.Algorithm;

public class DoubleLinkedList {

    private DoubleLinkedNode head;
    private int size;

    public DoubleLinkedNode getHead() {
        return head;
    }

    public DoubleLinkedList() {
        head = new DoubleLinkedNode();
        size = 0;
    }

    public int size() {
        return size;
    }

    public DoubleLinkedNode getNode(int index) throws Exception {
        if (index >= size) {
            throw new Exception("index " + index + " out of bound " + size);
        }
        DoubleLinkedNode temp = head;
        for (int i = 0; i <= index; i++) {
            temp = temp.getNext();
        }
        return temp;
    }

    public void push_front(Dot dot) {
        DoubleLinkedNode temp = new DoubleLinkedNode(dot);
        temp.setNext(head.getNext());
        temp.getNext().setPrev(temp);
        temp.setPrev(head);
        head.setNext(temp);
        size++;
    }

    public void push_back(Dot dot) throws Exception {
        DoubleLinkedNode temp = new DoubleLinkedNode(dot);
        if (size == 0) {
            temp.setPrev(temp);
            temp.setNext(temp);
            head.setNext(temp);
        }else {
            DoubleLinkedNode prev = getNode(size-1);
            temp.setNext(prev.getNext());
            temp.setPrev(prev);
            prev.getNext().setPrev(temp);
            prev.setNext(temp);
        }
        size++;
    }

    public void add(Dot dot, int index) throws Exception {
        DoubleLinkedNode node = getNode(index);
        DoubleLinkedNode temp = new DoubleLinkedNode(dot, node.getPrev(), node);
        node.getPrev().setNext(temp);
        node.setPrev(temp);
        size++;
    }

    public void remove(int index) throws Exception {
        DoubleLinkedNode node = getNode(index);
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
        size--;
    }

    public DoubleLinkedNode get(int index) throws Exception {
        DoubleLinkedNode node = getNode(index);
        return node;
    }
}
