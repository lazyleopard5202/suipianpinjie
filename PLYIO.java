package com.kaogu.Algorithm;

import org.smurn.jply.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PLYIO {

    public static PLYModel readPLY(String ply_path) throws IOException {
        PLYModel plyModel = new PLYModel();
        PlyReader ply = new PlyReaderFile(ply_path);
        ElementReader readerTypeA = ply.nextElementReader();
        List<Dot> dotList = new ArrayList<>();
        Element elementA = readerTypeA.readElement();
        while (elementA!=null) {
            Dot dot = new Dot((float)(elementA.getDouble("x")), (float)(elementA.getDouble("y")), (float)(elementA.getDouble("z")));
            dotList.add(dot);
            elementA = readerTypeA.readElement();
        }
        plyModel.setDotList(dotList);
        readerTypeA.close();
        ElementReader readerTypeB = ply.nextElementReader();
        List<Face> faceList = new ArrayList<>();
        Element elementB = readerTypeB.readElement();
        while (elementB!=null) {
            Face face = new Face();
            List<Integer> dot_indices = new ArrayList<>();
            int[] array = elementB.getIntList("vertex_indices");
            for (int i = 0; i < array.length; i++) {
                dot_indices.add(array[i]);
            }
            face.setDot_indices(dot_indices);
            faceList.add(face);
            elementB = readerTypeB.readElement();
        }
        readerTypeB.close();
        plyModel.setFaceList(faceList);
        plyModel.init();
        System.out.println("文件读取成功");
        return plyModel;
    }

    public static PLYModel readColorPLY(String ply_path) throws IOException {
        PLYModel plyModel = new PLYModel();
        PlyReader ply = new PlyReaderFile(ply_path);
        ElementReader readerTypeA = ply.nextElementReader();
        List<Dot> dotList = new ArrayList<>();
        Element elementA = readerTypeA.readElement();
        while (elementA!=null) {
            Dot dot = new Dot((float)(elementA.getDouble("x")), (float)(elementA.getDouble("y")), (float)(elementA.getDouble("z")));
            dotList.add(dot);
            elementA = readerTypeA.readElement();
        }
        plyModel.setDotList(dotList);
        readerTypeA.close();
        ElementReader readerTypeB = ply.nextElementReader();
        List<Face> faceList = new ArrayList<>();
        Element elementB = readerTypeB.readElement();
        while (elementB!=null) {
            Face face = new Face();
            List<Integer> dot_indices = new ArrayList<>();
            int[] array = elementB.getIntList("vertex_indices");
            int red = elementB.getInt("red");
            int green = elementB.getInt("green");
            int blue = elementB.getInt("blue");
            for (int i = 0; i < array.length; i++) {
                dot_indices.add(array[i]);
            }
            face.setDot_indices(dot_indices);
            face.setColor(new Color(red, green, blue));
            faceList.add(face);
            elementB = readerTypeB.readElement();
        }
        plyModel.setFaceList(faceList);
        readerTypeB.close();
        System.out.println("文件读取成功");
        return plyModel;
    }

    public static void writePLY(String ply_path, int threshold) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_modified" + String.valueOf(threshold) + ".ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n" +
                    "end_header\n");
            for (Dot dot: dotList) {
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + dot.getColor().getRed() + " " + dot.getColor().getGreen() + " " + dot.getColor().getBlue() + " " + "\n");
            }
            for (Face face: faceList) {
                List<Integer> face_indices = face.getDot_indices();
                out.write(face_indices.size() + " ");
                for (int face_index: face_indices) {
                    out.write(face_index + " ");
                }
                out.write("\n");
            }
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeColorPLY(String ply_path, double threshold, int group_cnt) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.setThreshold(threshold);
        plyModel.setGroup_cnt(group_cnt);
        plyModel.ClassifyFaceGroup();
        String target_path = "target.ply";
        if (group_cnt == 0) {
            target_path = ply_path.substring(0, ply_path.length()-4) + "_t" + threshold + ".ply";
        }else {
            plyModel.UnionSmallGroup4();
            target_path = ply_path.substring(0, ply_path.length()-4) + "_t" + threshold + "_c" + group_cnt + ".ply";
        }
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = plyModel.getFaceGroupList();
        for (int i = 0; i < FaceGroupList.size(); i++) {
            List<Integer> FaceGroup = FaceGroupList.get(i);
            Color color = new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
            for (int face_index : FaceGroup) {
                faceList.get(face_index).setColor(color);
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("end_header\n");
            for (int i = 0; i < dotList.size(); i++) {
                Dot dot = dotList.get(i);
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " \n");
            }
            for (int i = 0; i < faceList.size(); i++) {
                Face face = faceList.get(i);
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                out.write(dot_indices.get(0) + " " + dot_indices.get(1) + " " + dot_indices.get(2)+ " ");
                out.write(face.getColor().toColor() + "\n");
            }
            out.close();
            System.out.println("文件创建成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeGaussianPLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_gaussian.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color Red = new Color(255, 0, 0);
            Color Green = new Color(0, 255, 0);
            Color Blue = new Color(0, 0, 255);
            for (Dot dot : dotList) {
                double K = dot.getK();
                if (K > 0) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Blue.toColor() + " \n");
                }else if (K < 0) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Red.toColor() + " \n");
                }else {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Green.toColor() + " \n");
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                out.write(dot_indices.get(0) + " " + dot_indices.get(1) + " " + dot_indices.get(2)+ " \n");
            }
            out.close();
            System.out.println("GaussianPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMeanPLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_mean.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color Red = new Color(255, 0, 0);
            Color Green = new Color(0, 255, 0);
            Color Blue = new Color(0, 0, 255);
            for (Dot dot : dotList) {
                double H = dot.getH();
                if (H < 0.1) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Green.toColor() + " \n");
                }else if (H < 0.2) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Blue.toColor() + " \n");
                }else {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Red.toColor() + " \n");
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                out.write(dot_indices.get(0) + " " + dot_indices.get(1) + " " + dot_indices.get(2)+ " \n");
            }
            out.close();
            System.out.println("GaussianPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeK1PLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_k1.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color Red = new Color(255, 0, 0);
            Color Green = new Color(0, 255, 0);
            Color Blue = new Color(0, 0, 255);
            for (Dot dot : dotList) {
                double k1 = dot.getK1();
                if (k1 > 0.5) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Blue.toColor() + " \n");
                }else if (k1 < 0.5) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Red.toColor() + " \n");
                }else {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Green.toColor() + " \n");
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                out.write(dot_indices.get(0) + " " + dot_indices.get(1) + " " + dot_indices.get(2)+ " \n");
            }
            out.close();
            System.out.println("K1PLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeK2PLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_k2.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color Red = new Color(255, 0, 0);
            Color Green = new Color(0, 255, 0);
            Color Blue = new Color(0, 0, 255);
            for (Dot dot : dotList) {
                double k2 = dot.getK2();
                if (k2 > 0) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Blue.toColor() + " \n");
                }else if (k2 < 0) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Red.toColor() + " \n");
                }else {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Green.toColor() + " \n");
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                out.write(dot_indices.get(0) + " " + dot_indices.get(1) + " " + dot_indices.get(2)+ " \n");
            }
            out.close();
            System.out.println("K2PLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeData(String ply_path, double threshold, int group_cnt) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.makeFaceGraph();
        plyModel.setThreshold(threshold);
        plyModel.setGroup_cnt(group_cnt);
        plyModel.ClassifyFaceGroup();
        plyModel.UnionSmallGroup4();
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = plyModel.getFaceGroupList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_data.txt";
        List<Integer> FaceGroup = FaceGroupList.get(FaceGroupList.size()-1);
        HashSet<Integer> hashSet = new HashSet<>();
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        for (int i : FaceGroup) {
            Face face = faceList.get(i);
            List<Integer> dot_indices = face.getDot_indices();
            for (int d : dot_indices) {
                if (!hashSet.contains(d)) {
                    hashSet.add(d);
                    priorityQueue.add(d);
                }
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            while (!priorityQueue.isEmpty()) {
                Dot dot = dotList.get(priorityQueue.poll());
                out.write(dot.getX() + "\t" + dot.getY() + "\t" + dot.getZ() + "\n");
            }
            out.close();
            System.out.println("data写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSectionPLY(String ply_path, double threshold, int group_cnt) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.setThreshold(threshold);
        plyModel.setGroup_cnt(group_cnt);
        plyModel.ClassifyFaceGroup();
        plyModel.UnionSmallGroup2(group_cnt);
        plyModel.removeNoise();
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = plyModel.getFaceGroupList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_section.ply";
        List<Integer> FaceGroup = FaceGroupList.get(FaceGroupList.size()-1);
        HashSet<Integer> hashSet = new HashSet<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int cnt = 0;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        for (int i : FaceGroup) {
            Face face = faceList.get(i);
            List<Integer> dot_indices = face.getDot_indices();
            for (int d : dot_indices) {
                if (!hashSet.contains(d)) {
                    hashSet.add(d);
                    priorityQueue.add(d);
                }
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + priorityQueue.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element face " + FaceGroup.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll();
                Dot dot = dotList.get(temp);
                hashMap.put(temp, cnt);
                cnt++;
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + "\n");
            }
            for (int i : FaceGroup) {
                List<Integer> dot_indices = faceList.get(i).getDot_indices();
                out.write(dot_indices.size() + " ");
                for (int j : dot_indices) {
                    out.write(hashMap.get(j) + " ");
                }
                out.write("\n");
            }
            out.close();
            System.out.println("Section_PLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DeNoisedSectionPLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.removeNoise();
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_denoised.ply";
        HashSet<Integer> hashSet = new HashSet<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int cnt = 0;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        for (Face face : faceList) {
            List<Integer> dot_indices = face.getDot_indices();
            for (int d : dot_indices) {
                if (!hashSet.contains(d)) {
                    hashSet.add(d);
                    priorityQueue.add(d);
                }
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + priorityQueue.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll();
                Dot dot = dotList.get(temp);
                hashMap.put(temp, cnt);
                cnt++;
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + "\n");
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                for (int j : dot_indices) {
                    out.write(hashMap.get(j) + " ");
                }
                out.write("\n");
            }
            out.close();
            System.out.println("Section_PLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBorderPLY(String ply_path, double threshold, int group_cnt) throws Exception {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.setThreshold(threshold);
        plyModel.setGroup_cnt(group_cnt);
        plyModel.ClassifyFaceGroup();
        plyModel.UnionSmallGroup4();
        plyModel.removeNoise();
        List<Dot> dotList = plyModel.getDotList();
        HashMap<Dot, Integer> DotMap = plyModel.getDotMap();
        List<DoubleLinkedList> borders = plyModel.getSectionBorderLine();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_border.ply";
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < borders.size(); i++) {
            DoubleLinkedList doubleLinkedList = borders.get(i);
            DoubleLinkedNode first = doubleLinkedList.getHead().getNext();
            DoubleLinkedNode temp = first;
            while (temp.getNext() != first) {
                priorityQueue.add(DotMap.get(temp.getDot()));
                temp = temp.getNext();
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + priorityQueue.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element edge " + priorityQueue.size() + "\n");
            out.write("property int vertex1\n" +
                    "property int vertex2\n");
            out.write("end_header\n");
            int cnt = 0;
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll();
                hashMap.put(temp, cnt);
                cnt++;
                Dot dot = dotList.get(temp);
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " \n");
            }
            for (int i = 0; i < borders.size(); i++) {
                DoubleLinkedList doubleLinkedList = borders.get(i);
                DoubleLinkedNode start = doubleLinkedList.getHead().getNext();
                DoubleLinkedNode temp = start;
                while (temp.getNext() != start) {

                    out.write(hashMap.get(DotMap.get(temp.getDot())) + " " + hashMap.get(DotMap.get(temp.getNext().getDot())) + "\n");
                    temp = temp.getNext();
                }
            }
            out.close();
            System.out.println("Section_Border写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBorderedPLY(String ply_path, double threshold, int group_cnt) throws Exception {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.setThreshold(threshold);
        plyModel.setGroup_cnt(group_cnt);
        plyModel.ClassifyFaceGroup();
        plyModel.UnionSmallGroup4();
        plyModel.removeNoise();
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        HashMap<Dot, Integer> DotMap = plyModel.getDotMap();
        List<DoubleLinkedList> borders = plyModel.getSectionBorderLine();
        int border_length = 0;
        for (int i = 0; i < borders.size(); i++) {
            border_length += borders.get(i).size();
        }
        System.out.println(border_length);
        String target_path =ply_path.substring(0, ply_path.length()-4) + "_partial_bordered.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("element edge " + border_length + "\n");
            out.write("property int vertex1\n" +
                    "property int vertex2\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("end_header\n");
            for (Dot dot : dotList) {
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " \n");
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                for (int i = 0; i < dot_indices.size(); i++) {
                    out.write(dot_indices.get(i) + " ");
                }
                out.write("\n");
            }
            Color Red = new Color(255, 0, 0);
            for (DoubleLinkedList doubleLinkedList : borders) {
                DoubleLinkedNode temp = doubleLinkedList.getHead();
                for (int i = 0; i < doubleLinkedList.size(); i++) {
                    temp = temp.getNext();
                    out.write(DotMap.get(temp.getDot()) + " " + DotMap.get(temp.getNext().getDot()) + " " + Red.toColor() + "\n");
                }
            }
            out.close();
            System.out.println("BorderedPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LCSMatchPLY(String ply_path1, String ply_path2) throws Exception {
        PLYModel plyModel1 = readPLY(ply_path1);
        PLYModel plyModel2 = readPLY(ply_path2);
        plyModel1.setThreshold(5);
        plyModel2.setThreshold(3);
        plyModel1.setGroup_cnt(4);
        plyModel2.setGroup_cnt(5);
        plyModel1.ClassifyFaceGroup();
        plyModel2.ClassifyFaceGroup();
        plyModel1.UnionSmallGroup4();
        plyModel2.UnionSmallGroup4();
        plyModel1.removeNoise();
        plyModel2.removeNoise();
        List<DoubleLinkedList> list1 = plyModel1.getSectionBorderLine();
        List<DoubleLinkedList> list2 = plyModel2.getSectionBorderLine();
        DoubleLinkedList doubleLinkedList1 = list1.get(0);
        DoubleLinkedList doubleLinkedList2 = list1.get(1);
        DoubleLinkedList doubleLinkedList3 = doubleLinkedList1.reverse();
        DoubleLinkedList doubleLinkedList4 = doubleLinkedList2.reverse();
        DoubleLinkedList doubleLinkedList5 = list2.get(0);
        DoubleLinkedList doubleLinkedList6 = list2.get(1);
        DoubleLinkedList doubleLinkedList7 = doubleLinkedList5.reverse();
        DoubleLinkedList doubleLinkedList8 = doubleLinkedList6.reverse();
        int[][] ret = new int[16][];
        ret[0] = doubleLinkedList1.LCS(doubleLinkedList5);
        ret[1] = doubleLinkedList2.LCS(doubleLinkedList6);
        ret[2] = doubleLinkedList1.LCS(doubleLinkedList6);
        ret[3] = doubleLinkedList2.LCS(doubleLinkedList5);
        ret[4] = doubleLinkedList1.LCS(doubleLinkedList7);
        ret[5] = doubleLinkedList2.LCS(doubleLinkedList8);
        ret[6] = doubleLinkedList1.LCS(doubleLinkedList8);
        ret[7] = doubleLinkedList2.LCS(doubleLinkedList7);
        ret[8] = doubleLinkedList3.LCS(doubleLinkedList5);
        ret[9] = doubleLinkedList4.LCS(doubleLinkedList6);
        ret[10] = doubleLinkedList3.LCS(doubleLinkedList6);
        ret[11] = doubleLinkedList4.LCS(doubleLinkedList5);
        ret[12] = doubleLinkedList3.LCS(doubleLinkedList7);
        ret[13] = doubleLinkedList4.LCS(doubleLinkedList8);
        ret[14] = doubleLinkedList3.LCS(doubleLinkedList8);
        ret[15] = doubleLinkedList4.LCS(doubleLinkedList7);
        for (int i = 0; i < 16; i++) {
            System.out.println("ret" + i + ": x=" + ret[i][0] + ", y=" + ret[i][1] + ", max=" + ret[i][2]);
        }
        for (int i = 0; i < 8; i++) {
            String target_path1 = ply_path1.substring(0, ply_path1.length()-4) + "_match" + i + ".ply";
            String target_path2 = ply_path2.substring(0, ply_path2.length()-4) + "_match" + i + ".ply";
            Color[][] colors = new Color[2][];
            colors[0] = new Color[ret[2*i][2]];
            colors[1] = new Color[ret[2*i+1][2]];
            for (int j = 0; j < colors[0].length; j++) {
                colors[0][j] = new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
            }
            for (int j = 0; j < colors[1].length; j++) {
                colors[1][j] = new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
            }
            DoubleLinkedList[] linkedLists = new DoubleLinkedList[2];
            int[][] match = new int[2][];
            match[0] = ret[2*i];
            match[1] = ret[2*i+1];
            if (i < 4) {
                linkedLists[0] = doubleLinkedList1;
                linkedLists[1] = doubleLinkedList2;
            }else {
                linkedLists[0] = doubleLinkedList3;
                linkedLists[1] = doubleLinkedList4;
            }
            writeLCSMatchPLY(target_path1, plyModel1, linkedLists, match, colors, true);
            if (i % 4 == 0) {
                linkedLists[0] = doubleLinkedList5;
                linkedLists[1] = doubleLinkedList6;
            }else if (i % 4 == 1) {
                linkedLists[0] = doubleLinkedList6;
                linkedLists[1] = doubleLinkedList5;
            }else if (i % 4 == 2) {
                linkedLists[0] = doubleLinkedList7;
                linkedLists[1] = doubleLinkedList8;
            }else {
                linkedLists[0] = doubleLinkedList8;
                linkedLists[1] = doubleLinkedList7;
            }
            writeLCSMatchPLY(target_path2, plyModel2, linkedLists, match, colors, false);
        }
    }

    public static void writeLCSMatchPLY(String ply_path, PLYModel plyModel, DoubleLinkedList[] doubleLinkedLists, int[][] match, Color[][] colors, Boolean first) {
        Color white = new Color(255, 255, 255);
        for (int i = 0; i < 1; i++) {
            colors[0][i] = white;
            colors[1][i] = white;
        }
        DoubleLinkedList doubleLinkedList1 = doubleLinkedLists[0];
        DoubleLinkedList doubleLinkedList2 = doubleLinkedLists[1];
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        HashMap<Dot, Integer> DotMap = plyModel.getDotMap();
        HashSet<Integer> hashSet = new HashSet<>();
        DoubleLinkedNode temp1 = doubleLinkedList1.getHead().getNext();
        DoubleLinkedNode temp2 = doubleLinkedList2.getHead().getNext();
        if (first) {
            for (int i = 0; i < match[0][0]; i++) {
                temp1 = temp1.getNext();
            }
            for (int i = 0; i < match[1][0]; i++) {
                temp2 = temp2.getNext();
            }
        }else {
            for (int i = 0; i < match[0][1]; i++) {
                temp1 = temp1.getNext();
            }
            for (int i = 0; i < match[1][1]; i++) {
                temp2 = temp2.getNext();
            }
        }
        for (int i = 0; i < match[0][2]; i++) {
            hashSet.add(DotMap.get(temp1.getDot()));
            temp1 = temp1.getNext();
        }
        for (int i = 0; i < match[1][2]; i++) {
            hashSet.add(DotMap.get(temp2.getDot()));
            temp2 = temp2.getNext();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(ply_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color black = new Color(0,0,0);
            for (int i = 0; i < dotList.size(); i++) {
                Dot dot = dotList.get(i);
                if (!hashSet.contains(i)) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + black.toColor() + "\n");
                }else {
                    int index1 = doubleLinkedList1.getIndex(dot);
                    int index2 = doubleLinkedList2.getIndex(dot);
                    int c = 0;
                    if (index1 != -1) {
                        if (first) {
                            c = (index1 - match[0][0] + doubleLinkedList1.size()) % doubleLinkedList1.size();
                        }else {
                            c = (index1 - match[0][1] + doubleLinkedList1.size()) % doubleLinkedList1.size();
                        }
                        out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + colors[0][c].toColor() + "\n");
                    }else if (index2 != -1) {
                        if (first) {
                            c = (index2 - match[1][0] + doubleLinkedList2.size()) % doubleLinkedList2.size();
                        }else {
                            c = (index2 - match[1][1] + doubleLinkedList2.size()) % doubleLinkedList2.size();
                        }
                        doubleLinkedList1.getHead().getNext().getDot().setColor(new Color(255,255,255));
                        out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + colors[1][c].toColor() + "\n");
                    }else {
                        System.out.println("something wrong");
                    }
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                for (int i = 0; i < dot_indices.size(); i++) {
                    out.write(dot_indices.get(i) + " ");
                }
                out.write("\n");
            }
            out.close();
            System.out.println("MatchPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LCSSMatchPLY(String ply_path1, String ply_path2, double threshold1, int group_cnt1, double threshold2, int group_cnt2) throws Exception {
        PLYModel plyModel1 = readPLY(ply_path1);
        PLYModel plyModel2 = readPLY(ply_path2);
        plyModel1.setThreshold(threshold1);
        plyModel2.setThreshold(threshold2);
        plyModel1.setGroup_cnt(group_cnt1);
        plyModel2.setGroup_cnt(group_cnt2);
        plyModel1.ClassifyFaceGroup();
        plyModel2.ClassifyFaceGroup();
        plyModel1.UnionSmallGroup4();
        plyModel2.UnionSmallGroup4();
        plyModel1.removeNoise();
        plyModel2.removeNoise();
        List<DoubleLinkedList> list1 = plyModel1.getSectionBorderLine();
        List<DoubleLinkedList> list2 = plyModel2.getSectionBorderLine();
        DoubleLinkedList doubleLinkedList1 = list1.get(0);
        DoubleLinkedList doubleLinkedList2 = list1.get(1);
        DoubleLinkedList doubleLinkedList3 = doubleLinkedList1.reverse();
        DoubleLinkedList doubleLinkedList4 = doubleLinkedList2.reverse();
        DoubleLinkedList doubleLinkedList5 = list2.get(0);
        DoubleLinkedList doubleLinkedList6 = list2.get(1);
        DoubleLinkedList doubleLinkedList7 = doubleLinkedList5.reverse();
        DoubleLinkedList doubleLinkedList8 = doubleLinkedList6.reverse();
        int[][][] ret = new int[16][][];
        ret[0] = doubleLinkedList1.LCSS(doubleLinkedList5);
        ret[1] = doubleLinkedList2.LCSS(doubleLinkedList6);
        ret[2] = doubleLinkedList1.LCSS(doubleLinkedList6);
        ret[3] = doubleLinkedList2.LCSS(doubleLinkedList5);
        ret[4] = doubleLinkedList1.LCSS(doubleLinkedList7);
        ret[5] = doubleLinkedList2.LCSS(doubleLinkedList8);
        ret[6] = doubleLinkedList1.LCSS(doubleLinkedList8);
        ret[7] = doubleLinkedList2.LCSS(doubleLinkedList7);
        ret[8] = doubleLinkedList3.LCSS(doubleLinkedList5);
        ret[9] = doubleLinkedList4.LCSS(doubleLinkedList6);
        ret[10] = doubleLinkedList3.LCSS(doubleLinkedList6);
        ret[11] = doubleLinkedList4.LCSS(doubleLinkedList5);
        ret[12] = doubleLinkedList3.LCSS(doubleLinkedList7);
        ret[13] = doubleLinkedList4.LCSS(doubleLinkedList8);
        ret[14] = doubleLinkedList3.LCSS(doubleLinkedList8);
        ret[15] = doubleLinkedList4.LCSS(doubleLinkedList7);
        for (int i = 0; i < 8; i++) {
            String target_path1 = ply_path1.substring(0, ply_path1.length()-4) + "_match" + i + ".ply";
            String target_path2 = ply_path2.substring(0, ply_path2.length()-4) + "_match" + i + ".ply";
            Color[][] colors = new Color[2][];
            colors[0] = new Color[ret[2*i][0].length];
            colors[1] = new Color[ret[2*i+1][0].length];
            for (int j = 0; j < colors[0].length; j++) {
                colors[0][j] = new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
            }
            for (int j = 0; j < colors[1].length; j++) {
                colors[1][j] = new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
            }
            DoubleLinkedList[] linkedLists = new DoubleLinkedList[2];
            int[][][] match = new int[2][][];
            match[0] = ret[2*i];
            match[1] = ret[2*i+1];
            if (i < 4) {
                linkedLists[0] = doubleLinkedList1;
                linkedLists[1] = doubleLinkedList2;
            }else {
                linkedLists[0] = doubleLinkedList3;
                linkedLists[1] = doubleLinkedList4;
            }
            writeLCSSMatchPLY(target_path1, plyModel1, linkedLists, match, colors, true);
            if (i % 4 == 0) {
                linkedLists[0] = doubleLinkedList5;
                linkedLists[1] = doubleLinkedList6;
            }else if (i % 4 == 1) {
                linkedLists[0] = doubleLinkedList6;
                linkedLists[1] = doubleLinkedList5;
            }else if (i % 4 == 2) {
                linkedLists[0] = doubleLinkedList7;
                linkedLists[1] = doubleLinkedList8;
            }else {
                linkedLists[0] = doubleLinkedList8;
                linkedLists[1] = doubleLinkedList7;
            }
            writeLCSSMatchPLY(target_path2, plyModel2, linkedLists, match, colors, false);
        }
    }

    public static void writeLCSSMatchPLY(String ply_path, PLYModel plyModel, DoubleLinkedList[] doubleLinkedLists, int[][][] match, Color[][] colors, Boolean first) throws Exception {
        Color white = new Color(255, 255, 255);
        DoubleLinkedList doubleLinkedList1 = doubleLinkedLists[0];
        DoubleLinkedList doubleLinkedList2 = doubleLinkedLists[1];
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        HashMap<Dot, Integer> DotMap = plyModel.getDotMap();
        HashSet<Dot> hashSet = new HashSet<>();
        if (first) {
            for (int i = 0; i < match[0][0].length; i++) {
                hashSet.add(doubleLinkedList1.getNode(match[0][0][i]).getDot());
            }
            for (int i = 0; i < match[1][0].length; i++) {
                hashSet.add(doubleLinkedList2.getNode(match[1][0][i]).getDot());
            }
        }else {
            for (int i = 0; i < match[0][1].length; i++) {
                hashSet.add(doubleLinkedList1.getNode(match[0][1][i]).getDot());
            }
            for (int i = 0; i < match[1][1].length; i++) {
                hashSet.add(doubleLinkedList2.getNode(match[1][1][i]).getDot());
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(ply_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("element face " + faceList.size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("end_header\n");
            Color black = new Color(0,0,0);
            int cnt1 = 0;
            int cnt2 = 0;
            for (int i = 0; i < dotList.size(); i++) {
                Dot dot = dotList.get(i);
                if (hashSet.contains(dot)) {
                    int index1 = doubleLinkedList1.getIndex(dot);
                    int index2 = doubleLinkedList2.getIndex(dot);
                    if (index1 != -1) {
                        out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + colors[0][cnt1].toColor() + "\n");
                        cnt1++;
                    }else if (index2 != -1){
                        out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + colors[1][cnt2].toColor() + "\n");
                        cnt2++;
                    }else {
                        System.out.println("something wrong");
                    }
                }else {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + black.toColor() + "\n");
                }
            }
            for (Face face : faceList) {
                List<Integer> dot_indices = face.getDot_indices();
                out.write(dot_indices.size() + " ");
                for (int i = 0; i < dot_indices.size(); i++) {
                    out.write(dot_indices.get(i) + " ");
                }
                out.write("\n");
            }
            out.close();
            System.out.println("MatchPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String ply_path = "C:\\Users\\what1\\IdeaProjects\\kaogu-master\\src\\main\\resources\\static\\polygons\\sa1.ply";
        String ply_path1 = ply_path;
        String ply_path2 = "C:\\Users\\what1\\IdeaProjects\\kaogu-master\\src\\main\\resources\\static\\polygons\\newa2.ply";
        long start = new Date().getTime();
        LCSSMatchPLY(ply_path1,ply_path2,5,4,5.01,5);
        long end = new Date().getTime();
        System.out.println((end-start));
    }
}
