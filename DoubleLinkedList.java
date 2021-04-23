package com.kaogu.Algorithm;

public class DoubleLinkedList {

    private Dot dot;
    private DoubleLinkedList prev;
    private DoubleLinkedList next;
    private static DoubleLinkedList head;
    private static DoubleLinkedList tail;

    public DoubleLinkedList() {
    }

    public DoubleLinkedList(Dot dot) {
        this.dot = dot;
    }

    public DoubleLinkedList(Dot dot, DoubleLinkedList prev, DoubleLinkedList next) {
        this.dot = dot;
        this.prev = prev;
        this.next = next;
    }

    public Dot getDot() {
        return dot;
    }

    public void setDot(Dot dot) {
        this.dot = dot;
    }

    public DoubleLinkedList getPrev() {
        return prev;
    }

    public void setPrev(DoubleLinkedList prev) {
        this.prev = prev;
    }

    public DoubleLinkedList getNext() {
        return next;
    }

    public void setNext(DoubleLinkedList next) {
        this.next = next;
    }

    public static DoubleLinkedList getHead() {
        return head;
    }

    public static void setHead(DoubleLinkedList head) {
        DoubleLinkedList.head = head;
    }

    public static DoubleLinkedList getTail() {
        return tail;
    }

    public static void setTail(DoubleLinkedList tail) {
        DoubleLinkedList.tail = tail;
    }

    public int length() {
        int cnt = 0;
        for (DoubleLinkedList i = head; i != null; i = i.next) {
            cnt++;
        }
        return cnt;
    }

    public void push_front(Dot dot) {
        DoubleLinkedList temp = new DoubleLinkedList(dot);
        if (head == null) {
            tail = temp;
        }else {
            head.prev = temp;
            temp.next = head;
        }
        head = temp;
    }

    public void push_back(Dot dot) {
        DoubleLinkedList temp = new DoubleLinkedList(dot);
        if (length() == 0) {
            head = temp;
        }else {
            tail.next = temp;
            temp.prev = tail;
        }
        tail = temp;
    }

    public void add(Dot dot, int index) throws Exception {
        if (index == 0) {
            push_front(dot);
        }else if (index == length()) {
            push_back(dot);
        }else if (index > length()) {
            throw new Exception("index " + index + " out of bound " + length());
        }else {
            int cnt = 0;
            for (DoubleLinkedList temp = head; temp != null; temp = temp.next) {
                if (cnt == index) {
                    DoubleLinkedList A = temp;
                    DoubleLinkedList B = temp.next;
                    DoubleLinkedList node = new DoubleLinkedList(dot);
                    A.next = node;
                    node.prev = A;
                    node.next = B;
                    B.prev = node;
                    break;
                }
                cnt++;
            }
        }
    }

    public void remove(int index) throws Exception {
        if (index == 0) {
            head = head.next;
            head.prev = null;
        }else if (index == length()-1) {
            tail = tail.prev;
            tail.next = null;
        }else if (index > length()) {
            throw new Exception("index " + index + " out of bound " + length());
        }else {
            int cnt = 0;
            for (DoubleLinkedList temp = head; temp != null; temp = temp.next) {
                if (cnt == index) {
                    temp.next.prev = temp.prev;
                    temp.prev.next = temp.next;
                    break;
                }
                cnt++;
            }
        }
    }

    public DoubleLinkedList get(int index) {
        int cnt = 0;
        for (DoubleLinkedList temp = head; temp != null; temp = temp.next) {
            if (cnt == index) {
                return temp;
            }
            cnt++;
        }
        return null;
    }

    public DoubleLinkedList get(Dot dot) {
        for (DoubleLinkedList temp = head; temp != null; temp = temp.next) {
            if (temp.getDot() == dot) {
                return temp;
            }
        }
        return null;
    }
}
