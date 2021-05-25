package com.kaogu.Algorithm;

import org.apache.tomcat.jni.Proc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DoubleLinkedList<T> {

    private DoubleLinkedNode<T> head;
    private int size;

    public DoubleLinkedNode getHead() {
        return head;
    }

    public void setSize(int size) {
        this.size = size;
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

    public int getIndex(T value) throws Exception {
        DoubleLinkedNode temp = head;
        int index = 0;
        for (int i = 0; i < size; i++) {
            temp = temp.getNext();
            if (temp.getVal() == value) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void push_front(T val) throws Exception {
        DoubleLinkedNode temp = new DoubleLinkedNode(val);
        if (size == 0) {
            temp.setPrev(temp);
            temp.setNext(temp);
            head.setNext(temp);
        }else {
            DoubleLinkedNode next = head.getNext();
            temp.setPrev(next.getPrev());
            temp.setNext(next);
            next.getPrev().setNext(temp);
            next.setPrev(temp);
            head.setNext(temp);
        }
        size++;
    }

    public void push_back(T val) throws Exception {
        DoubleLinkedNode temp = new DoubleLinkedNode(val);
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

    public void add(T val, int index) throws Exception {
        DoubleLinkedNode node = getNode(index);
        DoubleLinkedNode temp = new DoubleLinkedNode(val, node.getPrev(), node);
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

    public void remove(DoubleLinkedNode node) throws Exception {
        DoubleLinkedNode temp = head;
        for (int i = 0; i < size; i++) {
            temp = temp.getNext();
            if (temp == node) {
                DoubleLinkedNode prev = temp.getPrev();
                DoubleLinkedNode next = temp.getNext();
                prev.setNext(next);
                next.setPrev(prev);
                size--;
                return;
            }
        }
        return;
    }

    public DoubleLinkedNode get(int index) throws Exception {
        DoubleLinkedNode node = getNode(index);
        return node;
    }

    public DoubleLinkedNode getLast() {
        return head.getNext().getPrev();
    }

    public DoubleLinkedList reverse() throws Exception {
        DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
        DoubleLinkedNode temp = head.getNext();
        do {
            doubleLinkedList.push_front(temp.getVal());
            temp = temp.getNext();
        }while (temp.getVal() != head.getNext().getVal());
        return doubleLinkedList;
    }

    public <Dot extends com.kaogu.Algorithm.Dot> DoubleLinkedList<Pair> group(DoubleLinkedList<Dot> mate) throws Exception {
        DoubleLinkedList<Pair> result = new DoubleLinkedList<>();
        Comparator<Dij> comparator = new Comparator<Dij>() {
            @Override
            public int compare(Dij o1, Dij o2) {
                return (o1.distance < o2.distance) ? -1 : 1;
            }
        };
        PriorityQueue<Dij> priorityQueue = new PriorityQueue<>(comparator);
        for (int i = 0; i < size; i++) {
            Dot dotA = (Dot) getNode(i).getVal();
            for (int j = 0; j < mate.size; j++) {
                Dot dotB = (Dot) mate.getNode(j).getVal();
                priorityQueue.add(new Dij(j, dotA.getDistance(dotB)));
            }
            Dij dij = priorityQueue.peek();
            result.push_back(new Pair((Dot)getNode(i).getVal(), (Dot)mate.getNode(dij.getIndex()).getVal()));
            priorityQueue.clear();
        }
        return result;
    }

    public<Dot extends com.kaogu.Algorithm.Dot> int[] LCS(DoubleLinkedList pattern) {
        int x = -1;
        int y = -1;
        int max = 0;
        int[] ret = new int[3];
        double standard = Math.pow(10, -1);
        int[][] record = new int[this.size()][pattern.size()];
        DoubleLinkedNode node1 = head;
        for (int i = 0; i < size; i++) {
            node1 = node1.getNext();
            Dot dot1 = (Dot)node1.getVal();
            Dot dot1left = (Dot)node1.getPrev().getVal();
            Dot dot1right = (Dot)node1.getNext().getVal();
            NVector left1 = new NVector(dot1left.getX()-dot1.getX(), dot1left.getY()-dot1.getY(), dot1left.getZ()-dot1.getZ());
            NVector right1 = new NVector(dot1right.getX()-dot1.getX(), dot1right.getY()-dot1.getY(), dot1right.getZ()-dot1.getZ());
            double leftLength1 = left1.getRank();
            double rightLength1 = right1.getRank();
            double angle1 = left1.DotProduct(right1) / leftLength1 / rightLength1;
            angle1 = Math.acos(angle1);
            DoubleLinkedNode node2 = pattern.getHead();
            for (int j = 0; j < pattern.size(); j++) {
                node2 = node2.getNext();
                Dot dot2 = (Dot)node2.getVal();
                Dot dot2left = (Dot)node2.getPrev().getVal();
                Dot dot2right = (Dot)node2.getNext().getVal();
                NVector left2 = new NVector(dot2left.getX()-dot2.getX(), dot2left.getY()-dot2.getY(), dot2left.getZ()-dot2.getZ());
                NVector right2 = new NVector(dot2right.getX()-dot2.getX(), dot1right.getY()-dot2.getY(), dot2right.getZ()-dot2.getZ());
                double leftLength2 = left2.getRank();
                double rightLength2 = right2.getRank();
                double angle2 = left2.DotProduct(right2) / leftLength2 / rightLength2;
                angle2 = Math.acos(angle2);
                double k21 = dot1.getK2();
                double k22 = dot2.getK2();
                double min = (Math.abs(k21) < Math.abs(k22)) ? Math.abs(k22) : Math.abs(k21);
                double res = k21 + k22;
                if (Math.abs(res) < 0.05 * min) {
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
        try {
            String match = "match.txt";
            BufferedWriter out = new BufferedWriter(new FileWriter(match));
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < pattern.size; j++) {
                    out.write(record[i][j] + " ");
                }
                out.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public<Pair extends com.kaogu.Algorithm.Pair> int[][] LCSS(DoubleLinkedList<Pair> pattern) {
        int x = -1;
        int y = -1;
        int max = 0;
        int gap = 5;
        Line[][] record = new Line[size][pattern.size()];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < pattern.size(); j++) {
                record[i][j] = new Line(0, 0);
            }
        }
        DoubleLinkedNode node1 = head;
        for (int i = 0; i < size; i++) {
            node1 = node1.getNext();
            Pair pair1 = (Pair)node1.getVal();
            Dot dot11 = pair1.getDot1();
            Dot dot12 = pair1.getDot2();
//            NVector left1 = new NVector(dot1left.getX()-dot1.getX(), dot1left.getY()-dot1.getY(), dot1left.getZ()-dot1.getZ());
//            NVector right1 = new NVector(dot1right.getX()-dot1.getX(), dot1right.getY()-dot1.getY(), dot1right.getZ()-dot1.getZ());
//            double leftLength1 = left1.getRank();
//            double rightLength1 = right1.getRank();
//            double angle1 = left1.DotProduct(right1) / leftLength1 / rightLength1;
//            angle1 = Math.acos(angle1);
            DoubleLinkedNode node2 = pattern.getHead();
            for (int j = 0; j < pattern.size(); j++) {
                node2 = node2.getNext();
                Pair pair2 = (Pair) node2.getVal();
                Dot dot21 = pair2.getDot1();
                Dot dot22 = pair2.getDot2();
//                NVector left2 = new NVector(dot2left.getX()-dot2.getX(), dot2left.getY()-dot2.getY(), dot2left.getZ()-dot2.getZ());
//                NVector right2 = new NVector(dot2right.getX()-dot2.getX(), dot1right.getY()-dot2.getY(), dot2right.getZ()-dot2.getZ());
//                double leftLength2 = left2.getRank();
//                double rightLength2 = right2.getRank();
//                double angle2 = left2.DotProduct(right2) / leftLength2 / rightLength2;
//                angle2 = Math.acos(angle2);
                double k11 = dot11.getK();
                double k12 = dot12.getK();
                double k21 = dot21.getK();
                double k22 = dot22.getK();
                double min1 = (Math.abs(k11) < Math.abs(k21)) ? Math.abs(k21) : Math.abs(k11);
                double min2 = (Math.abs(k12) < Math.abs(k22)) ? Math.abs(k22) : Math.abs(k12);
                double res1 = k11 + k21;
                double res2 = k12 + k22;
                if (Math.abs(res1) < 0.1 * min1 && Math.abs(res2) < 0.1 * min2) {
                    if (i == 0 || j == 0) {
                        record[i][j].setStart(1);
                    }else {
                        record[i][j].setStart(record[i-1][j-1].getStart()+1);
                        record[i][j].setEnd(0);
                    }
                    if (record[i][j].getStart() > max) {
                        max = record[i][j].getStart();
                        x = i;
                        y = j;
                    }
                }else {
                    if (i == 0 || j == 0) {
                        record[i][j].setStart(0);
                    }else {
                        int start1 = record[i-1][j].getStart();
                        int start2 = record[i][j-1].getStart();
                        int start3 = record[i-1][j-1].getStart();
                        int end1 = record[i-1][j].getEnd();
                        int end2 = record[i][j-1].getEnd();
                        int end3 = record[i-1][j-1].getEnd();
                        if (start3 >= start1 && start3 >= start2) {
                            if (end3 < gap) {
                                record[i][j].setStart(start3);
                                record[i][j].setEnd(end3+1);
                            }else {
                                if (start1 > start2) {
                                    if (end1 < gap) {
                                        record[i][j].setStart(start1);
                                        record[i][j].setEnd(end1+1);
                                    }else if (end2 < gap) {
                                        record[i][j].setStart(start2);
                                        record[i][j].setEnd(end2+1);
                                    }else {
                                        record[i][j].setStart(0);
                                        record[i][j].setEnd(0);
                                    }
                                }else {
                                    if (end2 < gap) {
                                        record[i][j].setStart(start2);
                                        record[i][j].setEnd(end2+1);
                                    }else if (end1 < gap) {
                                        record[i][j].setStart(start1);
                                        record[i][j].setEnd(end1+1);
                                    }else {
                                        record[i][j].setStart(0);
                                        record[i][j].setEnd(0);
                                    }
                                }
                            }
                        }else {
                            if (start1 > start2) {
                                if (end1 < gap) {
                                    record[i][j].setStart(start1);
                                    record[i][j].setEnd(end1+1);
                                }else if (end2 < gap) {
                                    record[i][j].setStart(start2);
                                    record[i][j].setEnd(end2+1);
                                }else {
                                    record[i][j].setStart(0);
                                    record[i][j].setEnd(0);
                                }
                            }else {
                                if (end2 < gap) {
                                    record[i][j].setStart(start2);
                                    record[i][j].setEnd(end2+1);
                                }else if (end1 < gap) {
                                    record[i][j].setStart(start1);
                                    record[i][j].setEnd(end1+1);
                                }else {
                                    record[i][j].setStart(0);
                                    record[i][j].setEnd(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        int[][] result = new int[2][max];
        result[0][max-1] = x;
        result[1][max-1] = y;
        while (max > 1) {
            int left = record[x-1][y].getStart();
            int above = record[x][y-1].getStart();
            int top_left = record[x-1][y-1].getStart();
            if (top_left == max) {
                x--;
                y--;
            }else {
                if (above == max) {
                    y--;
                }else if (left == max) {
                    x--;
                }else {
                    x--;
                    y--;
                    max--;
                    result[0][max-1] = x;
                    result[1][max-1] = y;
                }
            }
        }
        return result;
    }
}
