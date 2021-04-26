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
        plyModel.setFaceList(faceList);
        readerTypeB.close();
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

    public static Vertex getNormalVertex(Dot A, Dot B, Dot C) {
        Vertex vertexA = new Vertex(B.getX() - A.getX(), B.getY()-A.getY(), B.getZ()-A.getZ());
        Vertex vertexB = new Vertex(C.getX() - A.getX(), C.getY()-A.getY(), C.getZ()-A.getZ());
        double i = vertexA.getY()*vertexB.getZ() - vertexA.getZ()*vertexB.getY();
        double j = vertexA.getZ()*vertexB.getX() - vertexA.getX()*vertexB.getZ();
        double k = vertexA.getX()*vertexB.getY() - vertexA.getY()*vertexB.getX();
        Vertex normal = new Vertex(i, j, k);
        return normal;
    }

    public static List<List<Integer>> ClassifyFaceGroup(PLYModel plyModel, double threshold) {
        plyModel.makeGraph();
        int[][] graph = plyModel.getGraph();
        List<Face> faceList = plyModel.getFaceList();
        List<Dot> dotList = plyModel.getDotList();
        List<List<Integer>> FaceGroupList = new ArrayList<>();
        Boolean[] visited = new Boolean[faceList.size()];
        Arrays.fill(visited, false);
        for (int i = 0; i < faceList.size(); i++) {
            if (visited[i] == true) {
                continue;
            }else {
                List<Integer> list = new ArrayList<>();
                Queue<Integer> queue = new LinkedList<>();
                queue.add(i);
                visited[i] = true;
                while (!queue.isEmpty()) {
                    int temp = queue.poll();
                    Face FaceA = faceList.get(temp);
                    list.add(temp);
                    for (int j = 0; j < 3; j++) {
                        int neighbor = graph[temp][j];
                        if (visited[neighbor] == false) {
                            Face FaceB = faceList.get(neighbor);
                            Vertex VertexA = getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                            Vertex VertexB = getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                            double intermediate = VertexA.DotProduct(VertexB) / VertexA.getRank() / VertexB.getRank();
                            double angle = Math.acos(intermediate) * 180 / Math.PI;
                            angle = Math.min(angle, 180-angle);
                            if (angle <= threshold) {
                                queue.add(neighbor);
                                visited[neighbor] = true;
                            }
                        }
                    }
                }
                FaceGroupList.add(list);
            }
        }
        return FaceGroupList;
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
        plyModel.makeGraph();
        plyModel.ClassifyFaceGroup(threshold);
        plyModel.UnionSmallGroup4(group_cnt);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = plyModel.getFaceGroupList();
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_t" + threshold + "_c" + group_cnt + ".ply";
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
        plyModel.makeGraph();
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
        plyModel.makeGraph();
        plyModel.ClassifyFaceGroup(threshold);
        plyModel.UnionSmallGroup2(group_cnt);
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

    public static void rewriteSectionPLY(String ply_path, double threshold, int group_cnt) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        plyModel.makeGraph();
        plyModel.ClassifyFaceGroup(threshold);
        plyModel.UnionSmallGroup(group_cnt);
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = plyModel.getFaceGroupList();
        for (int i = 0; i < group_cnt; i++) {
            String target_path = ply_path.substring(0, ply_path.length()-4) + i + ".ply";
            List<Integer> FaceGroup = FaceGroupList.get(i);
            HashSet<Integer> hashSet = new HashSet<>();
            HashMap<Integer, Integer> hashMap = new HashMap<>();
            int cnt = 0;
            PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 - o2;
                }
            });
            for (int j : FaceGroup) {
                Face face = faceList.get(j);
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
                for (int k : FaceGroup) {
                    List<Integer> dot_indices = faceList.get(k).getDot_indices();
                    out.write(dot_indices.size() + " ");
                    for (int j : dot_indices) {
                        out.write(hashMap.get(j) + " ");
                    }
                    out.write("\n");
                }
                out.close();
                System.out.println("SectionGroups写入成功");
            }catch (IOException e) {
                e.printStackTrace();
            }
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

    public static void main(String[] args) throws Exception {
        String ply_path = "C:\\Users\\what1\\IdeaProjects\\kaogu-master\\src\\main\\resources\\static\\polygons\\sa1_section.ply";
        long start = new Date().getTime();
        writeBorderPLY(ply_path);
        long end = new Date().getTime();
        System.out.println((end-start));
    }
}
