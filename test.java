package com.kaogu.Algorithm;

import java.io.IOException;
import java.util.*;

public class test {

    public static void main(String[] args) throws IOException {
        int[] a = new int[5];
        a[0] =1;
        int[] b = new int[5];
        b = a.clone();
        b[0] = 2;
        System.out.println(a[0]);
    }
}
