import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MyPLYModel extends PLYModel {
    private Map<Integer, ArrayList<Integer>> faceCluster;           //类号： face的index
    private Map<Integer, Double> faceClusterArea;                   //类号：类总面积
    private Map<Integer, Set<Integer>> faceClusterNeighbor;     //类号：类的临类
    private Map<Integer, Vertex> faceUnitVertex;

    public void hierarchicalClusteringInit() {
        faceCluster = new HashMap<>();
        faceClusterArea = new HashMap<>();
        faceUnitVertex = new HashMap<>();
        for (int i = 0; i < super.getFaceList().size(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(i);
            faceCluster.put(i, temp);
            faceClusterArea.put(i, calculateArea(i));
            faceUnitVertex.put(i, getUnitVertexByFace(i));
        }
        generateNeighbor();
    }

    private Vertex getUnitVertexByFace(int index) {
        ArrayList<Dot> arrayList = getDotOfFace(index);
        Dot A = arrayList.get(0);
        Dot B = arrayList.get(1);
        Dot C = arrayList.get(2);
        Vertex vertexA = new Vertex(B.getX() - A.getX(), B.getY() - A.getY(), B.getZ() - A.getZ());
        Vertex vertexB = new Vertex(C.getX() - A.getX(), C.getY() - A.getY(), C.getZ() - A.getZ());
        double i = vertexA.getY() * vertexB.getZ() - vertexA.getZ() * vertexB.getY();
        double j = vertexA.getZ() * vertexB.getX() - vertexA.getX() * vertexB.getZ();
        double k = vertexA.getX() * vertexB.getY() - vertexA.getY() * vertexB.getX();
        Vertex normal = new Vertex(i, j, k);
        normal.toUnit();
        return normal;
    }

    public void generateNeighbor() {
        faceClusterNeighbor = new HashMap<>();
        for (int i = 0; i < super.getFaceList().size(); i++) {
            faceClusterNeighbor.put(i, new HashSet<>());
        }
        long dot_size = super.getDotList().size();
        HashMap<Long, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < super.getFaceList().size(); i++) {
            Face face = super.getFaceList().get(i);
            List<Integer> dot_indices = face.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            int t = 0;
            if (a > b) {
                t = a;
                a = b;
                b = t;
            }
            if (a > c) {
                t = a;
                a = c;
                c = t;
            }
            if (b > c) {
                t = b;
                b = c;
                c = t;
            }
            for (int j = 0; j < 3; j++) {
                long temp = -1;
                if (j == 0) {
                    temp = a * dot_size + b;
                } else if (j == 1) {
                    temp = a * dot_size + c;
                } else {
                    temp = b * dot_size + c;
                }
                if (!hashMap.containsKey(temp)) {
                    hashMap.put(temp, i);
                } else {
                    int face_index = hashMap.get(temp);
                    this.faceClusterNeighbor.get(face_index).add(i);
                    this.faceClusterNeighbor.get(i).add(face_index);
                }
            }
        }
    }

    /*
     *
     *
     @param index of face i
     * @return three dot
     * @author Drum
     * @date 2021/4/15 15:11
     */
    private ArrayList<Dot> getDotOfFace(int i) {
        ArrayList<Dot> arrayList = new ArrayList<>();
        for (int dotIndex :
                super.getFaceList().get(i).getDot_indices()) {
            arrayList.add(super.getDotList().get(dotIndex));
        }
        return arrayList;
    }

    public Map<Integer, ArrayList<Integer>> getFaceCluster() {
        return faceCluster;
    }

    public void setFaceCluster(Map<Integer, ArrayList<Integer>> faceCluster) {
        this.faceCluster = faceCluster;
    }

    public Map<Integer, Double> getFaceClusterArea() {
        return faceClusterArea;
    }

    public void setFaceClusterArea(Map<Integer, Double> faceClusterArea) {
        this.faceClusterArea = faceClusterArea;
    }

    public Map<Integer, Set<Integer>> getFaceClusterNeighbor() {
        return faceClusterNeighbor;
    }

    public void setFaceClusterNeighbor(Map<Integer, Set<Integer>> faceClusterNeighbor) {
        this.faceClusterNeighbor = faceClusterNeighbor;
    }

    public Map<Integer, Vertex> getFaceUnitVertex() {
        return faceUnitVertex;
    }

    public void setFaceUnitVertex(Map<Integer, Vertex> faceUnitVertex) {
        this.faceUnitVertex = faceUnitVertex;
    }

    private double calculateEdge(Dot dot1, Dot dot2) {
        return Math.sqrt(Math.pow(dot1.getX() - dot2.getX(), 2) + Math.pow(dot1.getY() - dot2.getY(), 2) + Math.pow(dot1.getZ() - dot2.getZ(), 2));
    }

    private Double calculateArea(int i) {
        ArrayList<Dot> dots = getDotOfFace(i);
        double a = calculateEdge(dots.get(0), dots.get(1));
        double b = calculateEdge(dots.get(0), dots.get(2));
        double c = calculateEdge(dots.get(1), dots.get(2));
        double p = (a + b + c) / 2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    public int getClassifySize() {
        return this.faceCluster.size();
    }

    public int[] findDistanceBetweenClsuterMIN() {
        double maxCos = -1;
        int[] pairs = new int[2];
        for (Integer i : this.faceCluster.keySet()) {
            for (int j : this.faceClusterNeighbor.get(i)) {
//                System.out.println(i+" "+j+"\n");
                double cos = Math.abs(calculateCos(i, j));
                if (cos > maxCos) {
                    maxCos = cos;
                    pairs[0] = i;
                    pairs[1] = j;
                }
            }
        }
        return pairs;
    }

    public double calculateCos(int i, int j) {
        Vertex a = this.faceUnitVertex.get(i);
        Vertex b = this.faceUnitVertex.get(j);
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public double calculateCos(Vertex i, Vertex j) {
        return i.x * j.x + i.y * j.y + i.z * j.z;
    }


    public void combineCluster(int[] twoCluster) throws Exception {
        if (twoCluster.length != 2) {
            throw new Exception("Must combine only two cluster!");
        }
        int cluster1 = twoCluster[0];
        int cluster2 = twoCluster[1];
        if (this.faceCluster.get(cluster1).size() < this.faceCluster.get(cluster2).size()) {
            int temp = cluster1;
            cluster1 = cluster2;
            cluster2 = temp;
        }

        //combine neighbor
        for (Integer neighbor : faceClusterNeighbor.get(cluster2)) {
            faceClusterNeighbor.get(neighbor).remove(cluster2);
            if (neighbor != cluster1) {
                faceClusterNeighbor.get(neighbor).add(cluster1);
            }
        }
        faceClusterNeighbor.get(cluster1).addAll(faceClusterNeighbor.get(cluster2));
        faceClusterNeighbor.get(cluster1).remove(cluster1);
        faceClusterNeighbor.remove(cluster2);

        Vertex vertex1 = faceUnitVertex.get(cluster1);
        Vertex vertex2 = faceUnitVertex.get(cluster2);

        double area1 = faceClusterArea.get(cluster1);
        double area2 = faceClusterArea.get(cluster2);

        //combine Vertex
        if (calculateCos(cluster1, cluster2) < 0) {
            vertex1.reverse();
        }
        vertex1.x = (vertex1.x * area1 + vertex2.x * area2) / (area1 + area2);
        vertex1.y = (vertex1.y * area1 + vertex2.y * area2) / (area1 + area2);
        vertex1.z = (vertex1.z * area1 + vertex2.z * area2) / (area1 + area2);
        vertex1.toUnit();
        faceUnitVertex.put(cluster1, vertex1);
        faceUnitVertex.remove(cluster2);

        //combine Area
        faceClusterArea.put(cluster1, area1 + area2);
        faceClusterArea.remove(cluster2);

        //combineCluster
        faceCluster.get(cluster1).addAll(faceCluster.get(cluster2));
        faceCluster.remove(cluster2);
    }


    public void write(String ply_path) {
        Random random = new Random();
        String target_path = ply_path.substring(0, ply_path.length() - 4) + "_modified_" + getClassifySize() + ".ply";
        Color[] colors = new Color[this.getClassifySize()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(target_path));
            out.write("ply\n" +
                    "format ascii 1.0\n" +
                    "element vertex " + this.getDotList().size() + "\n");
            out.write("property float x\n" +
                    "property float y\n" +
                    "property float z\n");
            out.write("element face " + this.getFaceList().size() + "\n");
            out.write("property list uchar int vertex_indices\n");
            out.write("property uchar red\n" +
                    "property uchar green\n" +
                    "property uchar blue\n");
            out.write("end_header\n");
            for (Dot dot : getDotList()) {
                out.write(dot.getX() + " " + dot.getY() + " " + dot.getZ() + "\n");
            }
            int index = 0;
            for (ArrayList<Integer> faces : this.faceCluster.values()) {
                for (int faceIndex :
                        faces) {
                    List<Integer> dot_indices = this.getFaceList().get(faceIndex).getDot_indices();
                    out.write(dot_indices.size() + " ");
                    for (int dot_index : dot_indices) {
                        out.write(dot_index + " ");
                    }
                    out.write(colors[index].getRed() + " ");
                    out.write(colors[index].getGreen() + " ");
                    out.write(colors[index].getBlue() + " ");
                    out.write("\n");
                }
                index++;
            }
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readFromExistColor(String ply_path) throws IOException {
        Map<Color, Integer> colors = new HashMap<Color, Integer>();
        int maxColorType = 0;

        this.faceCluster = new HashMap<Integer, ArrayList<Integer>>();
        this.faceClusterNeighbor = new HashMap<>();
        this.faceClusterArea = new HashMap<>();
        this.faceUnitVertex = new HashMap<>();
        ArrayList<Integer> reverseCluster = new ArrayList<Integer>();

        PlyReader ply = new PlyReaderFile(ply_path);
        ElementReader readerTypeA = ply.nextElementReader();
        List<Dot> dotList = new ArrayList<>();
        Element elementA = readerTypeA.readElement();
        while (elementA != null) {
            Dot dot = new Dot((float) (elementA.getDouble("x")), (float) (elementA.getDouble("y")), (float) (elementA.getDouble("z")));
            dotList.add(dot);
            elementA = readerTypeA.readElement();
        }
        this.setDotList(dotList);
        readerTypeA.close();
        ElementReader readerTypeB = ply.nextElementReader();
        List<Face> faceList = new ArrayList<>();
        Element elementB = readerTypeB.readElement();
        while (elementB != null) {
            Face face = new Face();
            List<Integer> dot_indices = new ArrayList<>();
            int[] array = elementB.getIntList("vertex_indices");
            int red = elementB.getInt("red");
            int green = elementB.getInt("green");
            int blue = elementB.getInt("blue");
            for (int j : array) {
                dot_indices.add(j);
            }
            face.setDot_indices(dot_indices);
            Color color = new Color(red, green, blue);
            if (!colors.containsKey(color)) {
                colors.put(color, maxColorType);
                this.faceCluster.put(maxColorType, new ArrayList<>());
                maxColorType++;
            }
            reverseCluster.add(colors.get(color));
            this.faceCluster.get(colors.get(color)).add(faceList.size());
//            face.setColor(new Color(red, green, blue));
            faceList.add(face);
            elementB = readerTypeB.readElement();
        }
        this.setFaceList(faceList);
        readerTypeB.close();

        this.faceClusterNeighbor = generateNeighbor2(reverseCluster);
        for (int index : this.faceCluster.keySet()) {
            ArrayList<Integer> faces = this.faceCluster.get(index);
            this.faceClusterArea.put(index, 0d);

            for (int faceIndex : faces) {
                double area1 = calculateArea(faceIndex);
                if (!this.faceUnitVertex.containsKey(index)) {
                    this.faceUnitVertex.put(index, getUnitVertexByFace(faceIndex));
                } else {
                    double area2 = this.faceClusterArea.get(index);
                    Vertex vertex2 = this.faceUnitVertex.get(index);
                    Vertex vertex1 = getUnitVertexByFace(faceIndex);

                    if (calculateCos(vertex1, vertex2) < 0) {
                        vertex1.reverse();
                    }

                    vertex1.x = (vertex1.x * area1 + vertex2.x * area2) / (area1 + area2);
                    vertex1.y = (vertex1.y * area1 + vertex2.y * area2) / (area1 + area2);
                    vertex1.z = (vertex1.z * area1 + vertex2.z * area2) / (area1 + area2);
                    vertex1.toUnit();
                    this.faceUnitVertex.put(index, vertex1);
                }
                this.faceClusterArea.put(index, this.faceClusterArea.get(index) + area1);
            }
        }
        for (int index :
                this.faceCluster.keySet()) {
            this.faceClusterNeighbor.get(index).remove(index);
        }


        System.out.println("文件读取成功");
    }

    public HashMap<Integer, Set<Integer>> generateNeighbor2(ArrayList<Integer> reverseCluster) {
        HashMap<Integer, Set<Integer>> neighbors = new HashMap<>();
        for (int i = 0; i < this.faceCluster.size(); i++) {
            neighbors.put(i, new HashSet<>());
        }
        long dot_size = super.getDotList().size();
        HashMap<Long, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < super.getFaceList().size(); i++) {
            Face face = super.getFaceList().get(i);
            List<Integer> dot_indices = face.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            int t = 0;
            if (a > b) {
                t = a;
                a = b;
                b = t;
            }
            if (a > c) {
                t = a;
                a = c;
                c = t;
            }
            if (b > c) {
                t = b;
                b = c;
                c = t;
            }
            for (int j = 0; j < 3; j++) {
                long temp = -1;
                if (j == 0) {
                    temp = a * dot_size + b;
                } else if (j == 1) {
                    temp = a * dot_size + c;
                } else {
                    temp = b * dot_size + c;
                }
                if (!hashMap.containsKey(temp)) {
                    hashMap.put(temp, i);
                } else {
                    int face_index = hashMap.get(temp);
                    neighbors.get(reverseCluster.get(face_index)).add(reverseCluster.get(i));
                    neighbors.get(reverseCluster.get(i)).add(reverseCluster.get(face_index));
                }
            }
        }
        return neighbors;
    }

    public void readPLY(String ply_path) throws IOException {
        PlyReader ply = new PlyReaderFile(ply_path);
        ElementReader readerTypeA = ply.nextElementReader();
        List<Dot> dotList = new ArrayList<>();
        Element elementA = readerTypeA.readElement();
        while (elementA!=null) {
            Dot dot = new Dot((float)(elementA.getDouble("x")), (float)(elementA.getDouble("y")), (float)(elementA.getDouble("z")));
            dotList.add(dot);
            elementA = readerTypeA.readElement();
        }
        this.setDotList(dotList);
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
        this.setFaceList(faceList);
        readerTypeB.close();
        System.out.println("文件读取成功");
    }

    public void hierarchicalClustering(int k) throws Exception {
        while(this.getClassifySize()>k){
            int[] twoCluster = this.findDistanceBetweenClsuterMIN();
            if(Math.abs(this.calculateCos(twoCluster[0],twoCluster[1]))<Math.abs(Math.cos(Math.PI*7.5/18)))
                break;
            this.combineCluster(twoCluster);
        }
    }

}
