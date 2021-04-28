package com.kaogu.Algorithm;

import org.smurn.jply.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class mainAlgorithm {

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
        plyModel.ClassifyFaceGroup(threshold);
        String target_path = "target.ply";
        if (group_cnt == 0) {
            target_path = ply_path.substring(0, ply_path.length()-4) + "_t" + threshold + ".ply";
        }else {
            plyModel.UnionSmallGroup4(group_cnt);
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

    public static void writeData(String ply_path, double threshold, int group_cnt) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.makeFaceGraph();
        plyModel.ClassifyFaceGroup(threshold);
        plyModel.UnionSmallGroup1(group_cnt);
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
        plyModel.ClassifyFaceGroup(threshold);
        plyModel.UnionSmallGroup4(group_cnt);
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

    public static void writeBorderPLY(String ply_path) throws Exception {
        PLYModel section_ply = readPLY(ply_path);
        DoubleLinkedList[] doubleLinkedLists = section_ply.getBorderLine();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_border.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + (doubleLinkedLists[0].size()+doubleLinkedLists[1].size()) + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element edge " + (doubleLinkedLists[0].size()+doubleLinkedLists[1].size()) + "\n");
            out.write("property int vertex1\n" +
                    "property int vertex2\n");
            out.write("end_header\n");
            for (DoubleLinkedList doubleLinkedList : doubleLinkedLists) {
                for (int i = 0; i < doubleLinkedList.size(); i++) {
                    Dot dot = doubleLinkedList.getNode(i).getDot();
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + "\n");
                }
            }
            DoubleLinkedList doubleLinkedList1 = doubleLinkedLists[0];
            DoubleLinkedList doubleLinkedList2 = doubleLinkedLists[1];
            for (int i = 0; i < doubleLinkedList1.size(); i++) {
                out.write(i + " " + ((i+1) % doubleLinkedList1.size()) + "\n");
            }
            for (int i = 0; i < doubleLinkedList2.size()-1; i++) {
                out.write((i+doubleLinkedList1.size()) + " " + (i+doubleLinkedList1.size()+1) + "\n");
            }
            out.write((doubleLinkedList1.size()+ doubleLinkedList2.size()-1) + " " + doubleLinkedList1.size() + "\n");
            out.close();
            System.out.println("Section_Border写入成功");
        }catch (IOException e) {
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
                if (H > 0.3) {
                    out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " " + Blue.toColor() + " \n");
                }else if (H < 0.3) {
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
            System.out.println("GaussianPLY写入成功");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String ply_path = "C:\\Users\\what1\\IdeaProjects\\kaogu-master\\src\\main\\resources\\static\\polygons\\sa2_section.ply";
        long start = new Date().getTime();
        DeNoisedSectionPLY(ply_path);
        long end = new Date().getTime();
        System.out.println((end-start));
    }
}
