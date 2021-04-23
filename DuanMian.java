package com.kaogu.Algorithm;

import org.smurn.jply.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DuanMian {

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

    public static Vertex getNormalVertex(Dot A, Dot B, Dot C) {
        Vertex vertexA = new Vertex(B.getX() - A.getX(), B.getY()-A.getY(), B.getZ()-A.getZ());
        Vertex vertexB = new Vertex(C.getX() - A.getX(), C.getY()-A.getY(), C.getZ()-A.getZ());
        //a×b=（）i+（）j+（）k，为了帮助记忆，利用三阶行列式，
        double i = vertexA.getY()*vertexB.getZ() - vertexA.getZ()*vertexB.getY();
        double j = vertexA.getZ()*vertexB.getX() - vertexA.getX()*vertexB.getZ();
        double k = vertexA.getX()*vertexB.getY() - vertexA.getY()*vertexB.getX();
        Vertex normal = new Vertex(i, j, k);
        return normal;
    }

    public static List<List<Integer>> findFaceGroup(PLYModel plyModel, double threshold) {
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = new ArrayList<>();
        List<List<Integer>> GroupIndicesList = new ArrayList<>();
        HashMap<Integer, Integer> hashMap1 = new HashMap<>();
        HashMap<Integer, Integer> hashMap2 = new HashMap<>();
        int index = 0;
        label1: for (int i = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            List<Integer> dot_indices = face.getDot_indices();
            List<Integer> GroupIndices = new ArrayList<>();
            label2: for (int j = 0; j < FaceGroupList.size(); j++) {
                List<Integer> FaceGroup = FaceGroupList.get(j);
                for (int face_index: FaceGroup) {
                    int cnt = 0;
                    for (int dot_index: dot_indices) {
                        if (faceList.get(face_index).getDot_indices().contains(dot_index)) {
                            cnt++;
                        }
                    }
                    if (cnt >= 2) {
                        Face FaceA = face;
                        Face FaceB = faceList.get(face_index);
                        Vertex VertexA = getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                        Vertex VertexB = getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                        double temp = dotProduct(VertexA, VertexB) / VertexA.getRank() / VertexB.getRank();
                        double angle = Math.acos(temp) * 180 / Math.PI;
                        angle = Math.min(angle, 180-angle);
                        if (angle <= threshold) {
                            if (GroupIndices.size() == 0) {
                                FaceGroup.add(i);
                            }
                            GroupIndices.add(j);
                            if (GroupIndices.size() <= 2) {
                                continue label2;
                            }else {
                                GroupIndicesList.add(GroupIndices);
                                continue label1;
                            }
                        }
                    }
                }
            }
            if (GroupIndices.size() == 0) {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                FaceGroupList.add(list);
            }else if (GroupIndices.size() > 1) {
                GroupIndicesList.add(GroupIndices);
            }
        }
        List<List<Integer>> intermediate_list = new ArrayList<>();
        for (List<Integer> GroupIndices : GroupIndicesList) {
            List<Integer> list = new ArrayList<>();
            for (int group_index : GroupIndices) {
                if (!hashMap1.containsKey(group_index)) {
                    hashMap1.put(group_index, index);
                    hashMap2.put(index, group_index);
                    index++;
                }
                list.add(hashMap1.get(group_index));
            }
            intermediate_list.add(list);
        }
        GroupIndicesList = intermediate_list;
        List<Integer>[] graph = new List[index];
        for (int i = 0; i < index; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int i = 0; i < GroupIndicesList.size(); i++) {
            List<Integer> GroupIndices = GroupIndicesList.get(i);
            for (int j : GroupIndices) {
                for (int k : GroupIndices) {
                    if ((j != k) && (!graph[j].contains(k))) {
                        graph[j].add(k);
                    }
                }
            }
        }
        Boolean[] visited = new Boolean[index];
        Arrays.fill(visited, false);
        List<List<Integer>> UnionList = new ArrayList<>();
        int root = 0;
        while (root < index) {
            if (visited[root] == true) {
                root++;
                continue;
            }
            Queue<Integer> queue = new LinkedList<>();
            ArrayList<Integer> temp_list = new ArrayList<>();
            temp_list.add(root);
            queue.add(root);
            visited[root] = true;
            root++;
            while (!queue.isEmpty()) {
                int temp = queue.poll();
                for (int i = 0; i < graph[temp].size(); i++) {
                    int visiting = graph[temp].get(i);
                    if (visited[visiting] == false) {
                        queue.add(visiting);
                        temp_list.add(visiting);
                        visited[visiting] = true;
                    }
                }
            }
            UnionList.add(temp_list);
        }
        int[][] temp_arr = new int[UnionList.size()][];
        for (int i = 0; i < UnionList.size(); i++) {
            List<Integer> Group = UnionList.get(i);
            temp_arr[i] = new int[Group.size()];
            for (int j = 0; j < temp_arr[i].length; j++) {
                temp_arr[i][j] = hashMap2.get(Group.get(j));
            }
            Arrays.sort(temp_arr[i]);
            for (int j = 1; j < temp_arr[i].length; j++) {
                FaceGroupList.get(temp_arr[i][0]).addAll(FaceGroupList.get(temp_arr[i][j]));
            }
        }
        List<Integer> group_indices = new ArrayList<>();
        for (int i = 0; i < temp_arr.length; i++) {
            for (int j = 1; j < temp_arr[i].length; j++) {
                group_indices.add(temp_arr[i][j]);
            }
        }
        List<List<Integer>> ResultList = new ArrayList<>();
        for (int i = 0; i < FaceGroupList.size(); i++) {
            if (!group_indices.contains(i)) {
                ResultList.add(FaceGroupList.get(i));
            }
        }
        System.out.println(ResultList.size());
        return ResultList;
    }

    public static void UnionSmallGroup(PLYModel plyModel) {
        HashMap<Integer, Integer> hashMap1 = new HashMap<>();
        HashMap<Integer, Integer> hashMap2 = new HashMap<>();
        int[][][] arr = new int[256][256][256];
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < faceList.size(); i++) {
            Face temp_face =  faceList.get(i);
            Color color = temp_face.getColor();
            int temp = color.getRed()*255*255 + color.getGreen()*255 + color.getBlue();
            if (!hashMap1.containsKey(temp)) {
                hashMap1.put(temp, cnt);
                hashMap2.put(cnt, temp);
                cnt++;
                FaceGroupList.add(new ArrayList<>());
            }
            FaceGroupList.get(hashMap1.get(temp)).add(i);
        }
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size() - o1.size();
            }
        });
        for (int i = 0; i < FaceGroupList.size(); i++) {
            System.out.println(FaceGroupList.get(i).size() + ": " + (FaceGroupList.get(i).size() * 100.0 / faceList.size()) + "%");
        }
    }

    public static void findBorderLine(PLYModel plyModel) {
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        int[][] arr = new int[dotList.size()][dotList.size()];
        for (Face face : faceList) {
            List<Integer> dot_indices = face.getDot_indices();
            int i = dot_indices.get(0);
            int j = dot_indices.get(1);
            int k = dot_indices.get(2);
            arr[i][j] += 1;
            arr[j][i] += 1;
            arr[i][k] += 1;
            arr[k][i] += 1;
            arr[j][k] += 1;
            arr[k][j] += 1;
        }
        for (int i = 0; i < dotList.size(); i++) {
            for (int j = 0; j < dotList.size(); j++) {
                if (arr[i][j] > 2) {
                    System.out.println(i + ", " + j + ": " + arr[i][j]);
                }
            }
        }
    }

    public static List<List<Line>> findBorder(PLYModel plyModel, int threshold) {
        List<Dot> dotList = plyModel.getDotList();
        List<Face> faceList = plyModel.getFaceList();
        List<List<Integer>> FaceGroupList = new ArrayList<>();
        List<List<Line>> LineGroupList = new ArrayList<>();
        HashMap<Line, Face> hashMap = new HashMap<>();
        label: for (int f = 0; f < faceList.size(); f++) {
            Face face = faceList.get(f);
            List<Integer> dot_indices = face.getDot_indices();
            int i = dot_indices.get(0);
            int j = dot_indices.get(1);
            int k = dot_indices.get(2);
            Line ij = new Line(i, j);
            Line ki = new Line(k, i);
            Line jk = new Line(j, k);
            boolean match = false;
            for (int l = 0; l < LineGroupList.size(); l++) {
                for (int m = 0; m < LineGroupList.get(l).size(); m++) {
                    Line temp_line = LineGroupList.get(l).get(m);
                    Face FaceA = face;
                    Boolean A = temp_line.equals(ij);
                    Boolean B = temp_line.equals(ki);
                    Boolean C = temp_line.equals(jk);
                    if (A | B | C) {
                        hashMap.put(ij, face);
                        hashMap.put(ki, face);
                        hashMap.put(jk, face);
                        Face FaceB = hashMap.get(temp_line);
                        Vertex VertexA = getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                        Vertex VertexB = getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                        double temp = dotProduct(VertexA, VertexB) / VertexA.getRank() / VertexB.getRank();
                        double angle = Math.acos(temp) * 180 / Math.PI;
                        angle = Math.min(angle, 180-angle);
                        if (angle <= threshold) {
                            LineGroupList.get(l).remove(temp_line);
                            FaceGroupList.get(l).add(f);
                            if (A) {
                                LineGroupList.get(l).add(ki);
                                LineGroupList.get(l).add(jk);
                            }
                            if (B) {
                                LineGroupList.get(l).add(ij);
                                LineGroupList.get(l).add(jk);
                            }
                            if (C) {
                                LineGroupList.get(l).add(ij);
                                LineGroupList.get(l).add(ki);
                            }
                            continue label;
                        }
                    }
                }
            }
            if (!match) {
                List<Line> lineList = new ArrayList<>();
                lineList.add(ij);
                lineList.add(jk);
                lineList.add(ki);
                LineGroupList.add(lineList);
                List<Integer> FaceGroup = new ArrayList<>();
                FaceGroup.add(f);
                FaceGroupList.add(FaceGroup);
                hashMap.put(ij, face);
                hashMap.put(ki, face);
                hashMap.put(jk, face);
            }

        }
        for (int i = 0; i < FaceGroupList.size(); i++) {
            System.out.println(i);
            for (int j = 0; j < FaceGroupList.get(i).size(); j++) {
                System.out.println(FaceGroupList.get(i).get(j));
            }
            for (int j = 0; j < LineGroupList.get(i).size(); j++) {
                System.out.println(LineGroupList.get(i).get(j));
            }
        }
        return LineGroupList;
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

    public static void writeBorderPLY(String ply_path) throws IOException {
        PLYModel plyModel = readPLY(ply_path);
        List<Dot> dotList = plyModel.getDotList();
        List<List<Line>> LineGroupList = findBorder(plyModel, 90);
        int border_length = 0;
        for (int i = 0; i < LineGroupList.size(); i++) {
            border_length += LineGroupList.get(i).size();
        }
        String target_path = ply_path.substring(0, ply_path.length()-4) + "_border.ply";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + dotList.size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element edge " + border_length + "\n");
            out.write("property int vertex1\n" +
                    "property int vertex2\n");
            out.write("end_header\n");
            for (int i = 0; i < dotList.size(); i++) {
                Dot dot = dotList.get(i);
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + " \n");
            }
            for (int i = 0; i < LineGroupList.size(); i++) {
                for (int j = 0; j < LineGroupList.get(i).size(); j++) {
                    Line line = LineGroupList.get(i).get(j);
                    out.write(line.getStart() + " " +line.getEnd() + " \n");
                }
            }
            out.close();
            System.out.println("文件创建成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double dotProduct(Vertex A, Vertex B) {
        double res = A.getX()*B.getX() + A.getY()*B.getY() + A.getZ()*B.getZ();
        return res;
    }

    public static void main(String[] args) throws IOException {
        String ply_path = "C:\\Users\\what1\\IdeaProjects\\kaogu-master\\src\\main\\resources\\static\\polygons\\a14.ply";
        PLYModel plyModel = readPLY(ply_path);
    }
}
