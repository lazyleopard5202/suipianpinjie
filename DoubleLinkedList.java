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

    public int[] LCS(DoubleLinkedList pattern) {
        int x = -1;
        int y = -1;
        int max = 0;
        int[] ret = new int[3];
        double standard = Math.pow(10, -5);
        int[][] record = new int[size][pattern.size()];
        DoubleLinkedNode node1 = head;
        for (int i = 0; i < size; i++) {
            node1 = node1.getNext();
            DoubleLinkedNode node2 = pattern.getHead();
            for (int j = 0; j < pattern.size(); j++) {
                node2 = node2.getNext();
                double res = node1.getDot().getK() + node2.getDot().getK();
                if (res > -standard || res < standard) {
                    if (i == 0 || j == 0) {
                        record[i][j] = 1;
                    }else {
                        record[i][j] = record[i-1][j-1] + 1;
                    }
                    if (record[i][j] > max) {
                        max = record[i][j];
                        x = i;
                        y = j;
                    }
                }else {
                    record[i][j] = 0;
                }
            }
        }
        x = x + 1 - max;
        y = y + 1 - max;
        ret[0] = x;
        ret[1] = y;
        ret[2] = max;
        return ret;
    }

}
