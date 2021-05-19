package com.kaogu.Algorithm;

import org.apache.tomcat.jni.Proc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DoubleLinkedList {

    private DoubleLinkedNode head;
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

    public int getIndex(Dot dot) throws Exception {
        DoubleLinkedNode temp = head;
        int index = 0;
        for (int i = 0; i < size; i++) {
            temp = temp.getNext();
            if (temp.getDot() == dot) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void push_front(Dot dot) throws Exception {
        DoubleLinkedNode temp = new DoubleLinkedNode(dot);
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

    public DoubleLinkedNode getLast() {
        return head.getNext().getPrev();
    }

    public DoubleLinkedList reverse() throws Exception {
        DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
        DoubleLinkedNode temp = head.getNext();
        do {
            doubleLinkedList.push_front(temp.getDot());
            temp = temp.getNext();
        }while (temp.getDot() != head.getNext().getDot());
        return doubleLinkedList;
    }

    public int[] LCS(DoubleLinkedList pattern) {
        int x = -1;
        int y = -1;
        int max = 0;
        int[] ret = new int[3];
        double standard = Math.pow(10, -1);
        int[][] record = new int[size][pattern.size()];
        DoubleLinkedNode node1 = head;
        for (int i = 0; i < size; i++) {
            node1 = node1.getNext();
            Dot dot1 = node1.getDot();
            Dot dot1left = node1.getPrev().getDot();
            Dot dot1right = node1.getNext().getDot();
            NVector left1 = new NVector(dot1left.getX()-dot1.getX(), dot1left.getY()-dot1.getY(), dot1left.getZ()-dot1.getZ());
            NVector right1 = new NVector(dot1right.getX()-dot1.getX(), dot1right.getY()-dot1.getY(), dot1right.getZ()-dot1.getZ());
            double leftLength1 = left1.getRank();
            double rightLength1 = right1.getRank();
            double angle1 = left1.DotProduct(right1) / leftLength1 / rightLength1;
            angle1 = Math.acos(angle1);
            DoubleLinkedNode node2 = pattern.getHead();
            for (int j = 0; j < pattern.size(); j++) {
                node2 = node2.getNext();
                Dot dot2 = node2.getDot();
                Dot dot2left = node2.getPrev().getDot();
                Dot dot2right = node2.getNext().getDot();
                NVector left2 = new NVector(dot2left.getX()-dot2.getX(), dot2left.getY()-dot2.getY(), dot2left.getZ()-dot2.getZ());
                NVector right2 = new NVector(dot2right.getX()-dot2.getX(), dot1right.getY()-dot2.getY(), dot2right.getZ()-dot2.getZ());
                double leftLength2 = left2.getRank();
                double rightLength2 = right2.getRank();
                double angle2 = left2.DotProduct(right2) / leftLength2 / rightLength2;
                angle2 = Math.acos(angle2);
                double k21 = node1.getDot().getK2();
                double k22 = node2.getDot().getK2();
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

    public int[][] LCSS(DoubleLinkedList pattern) {
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
            Dot dot1 = node1.getDot();
            Dot dot1left = node1.getPrev().getDot();
            Dot dot1right = node1.getNext().getDot();
            NVector left1 = new NVector(dot1left.getX()-dot1.getX(), dot1left.getY()-dot1.getY(), dot1left.getZ()-dot1.getZ());
            NVector right1 = new NVector(dot1right.getX()-dot1.getX(), dot1right.getY()-dot1.getY(), dot1right.getZ()-dot1.getZ());
            double leftLength1 = left1.getRank();
            double rightLength1 = right1.getRank();
            double angle1 = left1.DotProduct(right1) / leftLength1 / rightLength1;
            angle1 = Math.acos(angle1);
            DoubleLinkedNode node2 = pattern.getHead();
            for (int j = 0; j < pattern.size(); j++) {
                node2 = node2.getNext();
                Dot dot2 = node2.getDot();
                Dot dot2left = node2.getPrev().getDot();
                Dot dot2right = node2.getNext().getDot();
                NVector left2 = new NVector(dot2left.getX()-dot2.getX(), dot2left.getY()-dot2.getY(), dot2left.getZ()-dot2.getZ());
                NVector right2 = new NVector(dot2right.getX()-dot2.getX(), dot1right.getY()-dot2.getY(), dot2right.getZ()-dot2.getZ());
                double leftLength2 = left2.getRank();
                double rightLength2 = right2.getRank();
                double angle2 = left2.DotProduct(right2) / leftLength2 / rightLength2;
                angle2 = Math.acos(angle2);
                double k21 = node1.getDot().getK();
                double k22 = node2.getDot().getK();
                double min = (Math.abs(k21) < Math.abs(k22)) ? Math.abs(k22) : Math.abs(k21);
                double res = k21 + k22;
                if (Math.abs(res) < 0.1 * min) {
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
