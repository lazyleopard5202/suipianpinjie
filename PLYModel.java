package com.kaogu.Algorithm;

import java.util.*;

public class PLYModel {

    private List<Dot> dotList;    //记录点
    private List<Face> faceList;  //记录小三角形
    private List<List<Integer>> FaceGroupList; //记录FaceGroup，每一组FaceGroup中记录的是face的序号
    private int[][] face_graph;  //记录小三角形和哪三个小三角形相邻，graph = new int[dotlist,size][3],记录的是三个小三角形的序号
    private Set<Integer>[] dot_face_graph;
    private HashMap<Dot, Integer> dotMap;
    private double threshold = 5;
    private int group_cnt = 4;

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getGroup_cnt() {
        return group_cnt;
    }

    public void setGroup_cnt(int group_cnt) {
        this.group_cnt = group_cnt;
    }

    public HashMap<Dot, Integer> getDotMap() {
        return dotMap;
    }

    public void setDotMap(HashMap<Dot, Integer> dotMap) {
        this.dotMap = dotMap;
    }

    public int[][] getFace_graph() {
        return makeFaceGraph();
    }

    public List<List<Integer>> getFaceGroupList() {
        return FaceGroupList;
    }

    public List<Dot> getDotList() {
        return dotList;
    }

    public List<Face> getFaceList() {
        return faceList;
    }

    public void setDotList(List<Dot> dotList) {
        this.dotList = dotList;
    }

    public void setFaceList(List<Face> faceList) {
        this.faceList = faceList;
    }

    public void makeDotMap() {
        for (int i = 0; i < dotList.size(); i++) {
            dotMap.put(dotList.get(i), i);
        }
    }

    public void calNVectors() {
        for (Face face : faceList) {
            List<Integer> dot_indices = face.getDot_indices();
            Dot dotA = dotList.get(dot_indices.get(0));
            Dot dotB = dotList.get(dot_indices.get(1));
            Dot dotC = dotList.get(dot_indices.get(2));
            NVector vectorA = new NVector(dotA.getX()-dotB.getX(), dotA.getY()-dotB.getY(), dotA.getZ()- dotB.getZ());
            NVector vectorB = new NVector(dotA.getX()-dotC.getX(), dotA.getY()-dotC.getY(), dotA.getZ()- dotC.getZ());
            double i = vectorA.getY()*vectorB.getZ() - vectorA.getZ()*vectorB.getY();
            double j = vectorA.getZ()*vectorB.getX() - vectorA.getX()*vectorB.getZ();
            double k = vectorA.getX()*vectorB.getY() - vectorA.getY()*vectorB.getX();
            NVector nVector = new NVector(i, j, k);
            face.setNVector(nVector);
        }
    }

    //生成图
    public int[][] makeFaceGraph() {
        this.face_graph = new int[faceList.size()][3];
        for (int[] temp : face_graph) {
            Arrays.fill(temp, -1);
        }
        long dot_size = dotList.size();
        int[] size = new int[faceList.size()];
        HashMap<Long, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            List<Integer> dot_indices = face.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            int t = 0;
            if(a > b){
                t = a;
                a = b;
                b = t;
            }
            if(a > c){
                t = a;
                a = c;
                c = t;
            }
            if(b > c){
                t = b;
                b = c;
                c = t;
            }
            for (int j = 0; j < 3; j++) {
                long temp = -1;
                if (j == 0) {
                    temp = a * dot_size + b;
                }else if (j == 1) {
                    temp = a * dot_size + c;
                }else if (j == 2) {
                    temp = b * dot_size + c;
                }
                if (!hashMap.containsKey(temp)) {
                    hashMap.put(temp, i);
                }else {
                    int face_index = hashMap.get(temp);
                    face_graph[face_index][size[face_index]] = i;
                    size[face_index]++;
                    face_graph[i][size[i]] = face_index;
                    size[i]++;
                }
            }
        }
        return face_graph;
    }

    public void makeDotFaceGraph() {
        if (face_graph == null) {
            makeFaceGraph();
        }
        dot_face_graph = new Set[dotList.size()];
        for (int i = 0; i < dot_face_graph.length; i++) {
            dot_face_graph[i] = new HashSet<>();
        }
        for (int i = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            List<Integer> dot_indices = face.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            dot_face_graph[a].add(i);
            dot_face_graph[b].add(i);
            dot_face_graph[c].add(i);
        }
    }

    public void calAngles() {
        for (Face face : faceList){
            ArrayList<Double> angles = new ArrayList<>();
            List<Integer> dot_indices = face.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            Dot dotA = dotList.get(a);
            Dot dotB = dotList.get(b);
            Dot dotC = dotList.get(c);
            NVector vectorAB = new NVector(dotB.getX()-dotA.getX(), dotB.getY()- dotA.getY(), dotB.getZ()-dotA.getZ());
            NVector vectorAC = new NVector(dotC.getX()-dotA.getX(), dotC.getY()- dotA.getY(), dotC.getZ()-dotA.getZ());
            NVector vectorBA = new NVector(-vectorAB.getX(), -vectorAB.getY(), -vectorAB.getZ());
            NVector vectorBC = new NVector(dotC.getX()-dotB.getX(), dotC.getY()- dotB.getY(), dotC.getZ()-dotB.getZ());
            NVector vectorCA = new NVector(-vectorAC.getX(), -vectorAC.getY(), -vectorAC.getZ());
            NVector vectorCB = new NVector(-vectorBC.getX(), -vectorBC.getY(), -vectorBC.getZ());
            double intermediate = vectorAB.DotProduct(vectorAC) / vectorAB.getRank() / vectorAC.getRank();
            double angle = Math.acos(intermediate);
            angles.add(angle);
            intermediate = vectorBA.DotProduct(vectorBC) / vectorBA.getRank() / vectorBC.getRank();
            angle = Math.acos(intermediate);
            angles.add(angle);
            intermediate = vectorCA.DotProduct(vectorCB) / vectorCA.getRank() / vectorCB.getRank();
            angle = Math.acos(intermediate);
            angles.add(angle);
            face.setAngles(angles);
        }
    }

    public void calVAreas() {
        for (int i = 0; i < dot_face_graph.length; i++) {
            double area = 0;
            Iterator<Integer> iterator = dot_face_graph[i].iterator();
            while (iterator.hasNext()) {
                int f = iterator.next();
                Face face = faceList.get(f);
                List<Integer> dot_indices = face.getDot_indices();
                List<Double> angles = face.getAngles();
                int index = -1;
                for (int j = 0; j < dot_indices.size(); j++) {
                    if (dot_indices.get(j) == i) {
                        index = j;
                        break;
                    }
                }
                if (index < 0) {
                    System.out.println("calVArea wrong");
                }
                Dot dotA = dotList.get(dot_indices.get(0));
                Dot dotB = dotList.get(dot_indices.get(1));
                Dot dotC = dotList.get(dot_indices.get(2));
                double angleA = angles.get(0);
                double angleB = angles.get(1);
                double angleC = angles.get(2);
                double angle90 = Math.PI/2;
                double temp = 0;
                if (index == 0) {
                    NVector vectorAB = new NVector(dotB.getX()-dotA.getX(), dotB.getY()- dotA.getY(), dotB.getZ()-dotA.getZ());
                    NVector vectorAC = new NVector(dotC.getX()-dotA.getX(), dotC.getY()- dotA.getY(), dotC.getZ()-dotA.getZ());
                    temp = (Math.pow(vectorAB.getRank(), 2) / Math.tan(angleC) + Math.pow(vectorAC.getRank(), 2) / Math.tan(angleB)) / 8;
                }else if (index == 1) {
                    NVector vectorBA = new NVector(dotA.getX()-dotB.getX(), dotA.getY()- dotB.getY(), dotA.getZ()-dotB.getZ());
                    NVector vectorBC = new NVector(dotC.getX()-dotB.getX(), dotC.getY()- dotB.getY(), dotC.getZ()-dotB.getZ());
                    temp = (Math.pow(vectorBA.getRank(), 2) / Math.tan(angleC) + Math.pow(vectorBC.getRank(), 2) / Math.tan(angleC)) / 8;
                }else if (index == 2) {
                    NVector vectorCA = new NVector(dotA.getX()-dotC.getX(), dotA.getY()- dotC.getY(), dotA.getZ()-dotC.getZ());
                    NVector vectorCB = new NVector(dotC.getX()-dotB.getX(), dotC.getY()- dotB.getY(), dotC.getZ()-dotB.getZ());
                    temp = (Math.pow(vectorCA.getRank(), 2) / Math.tan(angleB) + Math.pow(vectorCB.getRank(), 2) / Math.tan(angleA)) / 8;
                }
                if (angleA <= angle90 && angleB <= angle90 && angleC <= angle90) {
                    area += temp;
                }else {
                    if (index == 0) {
                        if (angleA > angle90) {
                            area += temp/2;
                        }else {
                            area += temp/4;
                        }
                    }else if (index == 1) {
                        if (angleB > angle90) {
                            area += temp/2;
                        }else {
                            area += temp/4;
                        }
                    }else if (index == 2) {
                        if (angleC > angle90) {
                            area += temp/2;
                        }else {
                            area += temp/4;
                        }
                    }
                }
            }
            dotList.get(i).setVArea(area);
        }
    }

    public void makeGaussianCurvatures() {
        for (int i = 0; i < dotList.size(); i++) {
            Dot dot = dotList.get(i);
            double area = dot.getVArea();
            double angle = 0;
            Iterator<Integer> iterator = dot_face_graph[i].iterator();
            while (iterator.hasNext()) {
                int f = iterator.next();
                Face face = faceList.get(f);
                List<Integer> dot_indices = face.getDot_indices();
                int index = -1;
                for (int j = 0; j < dot_indices.size(); j++) {
                    if (dot_indices.get(j) == i) {
                        index = j;
                        break;
                    }
                }
                angle += face.getAngles().get(index);
            }
            double res = (2*Math.PI - angle) / area;
            dot.setK(res);
        }
    }

    public void makeMeanCurvatures() {
        for (int i = 0; i < dotList.size(); i++) {
            Dot dot = dotList.get(i);
            double area = dot.getVArea();
            NVector ans = new NVector(0,0,0);
            double res = 0;
            Iterator<Integer> iterator = dot_face_graph[i].iterator();
            HashSet<Integer> hashSet = new HashSet<>();
            while (iterator.hasNext()) {
                int f = iterator.next();
                Face face = faceList.get(f);
                List<Integer> dot_indices = face.getDot_indices();
                int indexA = -1;
                int indexB = -1;
                int indexC = -1;
                Boolean first = false;
                for (int j = 0; j < dot_indices.size(); j++) {
                    int dot_index = dot_indices.get(j);
                    if (dot_index == i) {
                        indexA = j;
                    }else {
                        if (!first) {
                            indexB = j;
                            first = true;
                        }else {
                            indexC = j;
                        }
                    }
                }
                int dotA_index = dot_indices.get(indexA);
                int dotB_index = dot_indices.get(indexB);
                int dotC_index = dot_indices.get(indexC);
                Dot dotA = dotList.get(dotA_index);
                Dot dotB = dotList.get(dotB_index);
                Dot dotC = dotList.get(dotC_index);
                NVector ret = new NVector(0, 0, 0);
                if (!hashSet.contains(dotB_index)) {
                    NVector vectorBA = new NVector(dotA.getX()-dotB.getX(), dotA.getY()- dotB.getY(), dotA.getZ()-dotB.getZ());
                    for (int j = 0; j < face_graph[f].length; j++) {
                        if (face_graph[f][j] == -1) {
                            continue;
                        }
                        Face neighbor = faceList.get(face_graph[f][j]);
                        List<Integer> neighbor_dot_indices = neighbor.getDot_indices();
                        int cnt = 0;
                        int indexD = 0;
                        int[] arr = new int[3];
                        for (int k = 0; k < 3; k++) {
                            int index = neighbor_dot_indices.get(k);
                            if (dotA_index == index | dotB_index == index | dotC_index == index) {
                                arr[k] += 1;
                                cnt++;
                            }else {
                                indexD = k;
                            }
                        }
                        if (cnt == 2) {
                            double angleC = face.getAngles().get(indexC);
                            double angleD = neighbor.getAngles().get(indexD);
                            double temp = (1 / Math.tan(angleC) + 1 / Math.tan(angleD));
                            ret = new NVector(temp*vectorBA.getX(), temp*vectorBA.getY(), temp*vectorBA.getZ());
                        }
                    }
                    hashSet.add(dotB_index);
                }else if (!hashSet.contains(dotC_index)) {
                    NVector vectorCA = new NVector(dotA.getX()-dotC.getX(), dotA.getY()- dotC.getY(), dotA.getZ()-dotC.getZ());
                    for (int j = 0; j < face_graph[f].length; j++) {
                        if (face_graph[f][j] == -1) {
                            continue;
                        }
                        Face neighbor = faceList.get(face_graph[f][j]);
                        List<Integer> neighbor_dot_indices = neighbor.getDot_indices();
                        int cnt = 0;
                        int indexD = 0;
                        int[] arr = new int[3];
                        for (int k = 0; k < 3; k++) {
                            int index = neighbor_dot_indices.get(k);
                            if (dotA_index == index | dotB_index == index | dotC_index == index) {
                                arr[k] += 1;
                                cnt++;
                            }else {
                                indexD = k;
                            }
                        }
                        if (cnt == 2) {
                            double angleB = face.getAngles().get(indexB);
                            double angleD = neighbor.getAngles().get(indexD);
                            double temp = (1 / Math.tan(angleB) + 1 / Math.tan(angleD));
                            ret = new NVector(temp*vectorCA.getX(), temp*vectorCA.getY(), temp*vectorCA.getZ());
                        }
                    }
                    hashSet.add(dotC_index);
                }
                ans.add(ret);
            }
            double H = ans.getRank() / 4 / area;
            dot.setH(H);
        }
    }

    public void makeK1K2() {
        for (int i = 0; i < dotList.size(); i++) {
            Dot dot = dotList.get(i);
            double H = dot.getH();
            double K = dot.getK();
//            System.out.println(i + ": H = " + H + ", K = " + K);
            double temp = Math.pow((Math.pow(H,2) - K), 0.5);
            double K1 = H + temp;
            double K2 = H - temp;
            dot.setK1(K1);
            dot.setK2(K2);
        }
    }

    public void init() {
        makeDotMap();
        makeFaceGraph();
        makeDotFaceGraph();
        calAngles();
        calNVectors();
        calVAreas();
        makeGaussianCurvatures();
        makeMeanCurvatures();
        makeK1K2();
    }

    public void test() {
        int dot_size = dotList.size();
        int[][] arr = new int[dot_size][dot_size];
        for (int i = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            List<Integer> dotList = face.getDot_indices();
            int a = dotList.get(0);
            int b = dotList.get(1);
            int c = dotList.get(2);
            arr[a][b] += 1;
            arr[b][a] += 1;
            arr[a][c] += 1;
            arr[c][a] += 1;
            arr[b][c] += 1;
            arr[c][b] += 1;
        }
        for (int i = 0; i < dot_size; i++) {
            for (int j = 0; j < dot_size; j++) {
                if (arr[i][j] != 0) {
                    System.out.println(i + " " + j + ": " + arr[i][j]);
                }
            }
        }
    }

    public void removeNoise() {
        if (FaceGroupList.size() == 0) {
            ClassifyFaceGroup();
            UnionSmallGroup4();
        }
        List<Integer> FaceGroup = FaceGroupList.get(FaceGroupList.size()-1);
        HashSet<Integer> Group = new HashSet<>();
        for (int i = 0; i < FaceGroup.size(); i++) {
            Group.add(FaceGroup.get(i));
        }
        HashSet<Integer> visited = new HashSet<>();
        int index = 0;
        while (index < FaceGroup.size()) {
            int root = FaceGroup.get(index);
            if (visited.contains(root)) {
                index++;
                continue;
            }
            ArrayList<Integer> list = new ArrayList<>();
            Queue<Integer> queue = new LinkedList<>();
            queue.add(root);
            visited.add(root);
            list.add(root);
            while (!queue.isEmpty()) {
                int temp = queue.poll();
                for (int i = 0; i < 3; i++) {
                    int neighbor = face_graph[temp][i];
                    if (neighbor == -1) {
                        continue;
                    }
                    if (!Group.contains(neighbor)) {
                        continue;
                    }
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        list.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
            if (list.size() > 0.5 * FaceGroup.size()) {
                ArrayList<Integer> rest = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (!FaceGroup.contains(list.get(i))) {
                        rest.add(list.get(i));
                    }
                }
                FaceGroupList.remove(FaceGroup);
                FaceGroupList.add(rest);
                FaceGroupList.add(list);
                break;
            }
        }
    }

    //根据法向量夹角阈值分组，threshold为夹角阈值，核心想法是bfs
    public List<List<Integer>> ClassifyFaceGroup() {
        this.makeFaceGraph();
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
                        int neighbor = face_graph[temp][j];
                        if (visited[neighbor] == false) {
                            Face FaceB = faceList.get(neighbor);
                            NVector VectorA = FaceA.getNVector();
                            NVector VectorB = FaceB.getNVector();
                            double intermediate = VectorA.DotProduct(VectorB) / VectorA.getRank() / VectorB.getRank();
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

    //将小的组合并到大的组，group_cnt是最后要分几组，核心想法是dijkstra
    public List<List<Integer>> UnionSmallGroup() {
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size()-o1.size();
            }
        });
        double[][] distance = new double[faceList.size()][group_cnt];
        for (double[] temp : distance) {
            Arrays.fill(temp, 1000000);
        }
        Boolean[] visited = new Boolean[faceList.size()];
        Arrays.fill(visited, false);
        for (int i = 0; i < group_cnt; i++) {
            List<Integer> GroupList = FaceGroupList.get(i);
            for (int j : GroupList) {
                visited[j] = true;
                distance[j][i] = 0;
            }
        }
        for (int i = 0; i < group_cnt; i++) {
            PriorityQueue<Dij> priorityQueue = new PriorityQueue<>(new Comparator<Dij>() {
                @Override
                public int compare(Dij o1, Dij o2) {
                    return (int) (o1.distance - o2.distance);
                }
            });
            List<Integer> GroupList = FaceGroupList.get(i);
            Boolean[] temp_visited = visited.clone();
            for (int j : GroupList) {
                temp_visited[j] = false;
            }
            int root = GroupList.get(0);
            priorityQueue.add(new Dij(root, distance[root][i]));
            temp_visited[root] = true;
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll().getIndex();
                int[] Face_index = face_graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        NVector VectorA = FaceA.getNVector();
                        NVector VectorB = FaceB.getNVector();
                        double intermediate = VectorA.DotProduct(VectorB) / VectorA.getRank() / VectorB.getRank();
                        double angle = Math.acos(intermediate) * 180 / Math.PI;
                        angle = Math.min(angle, 180-angle);
                        distance[j][i] = Math.min(distance[j][i], distance[temp][i] + angle);
                        priorityQueue.add(new Dij(j, distance[j][i]));
                        temp_visited[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < faceList.size(); i++) {
            if (visited[i] == true) {
                continue;
            }
            int index = -1;
            double min = 10000000;
            for (int j = 0; j < group_cnt; j++) {
                if (distance[i][j] < min) {
                    min = distance[i][j];
                    index = j;
                }
            }
            FaceGroupList.get(index).add(i);
        }
        for (int i = group_cnt; i < FaceGroupList.size(); i++) {
            FaceGroupList.get(i).clear();
        }
        int cnt = 0;
        Iterator<List<Integer>> iterator = FaceGroupList.iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next();
            cnt += list.size();
            if (list.size() == 0) {
                iterator.remove();
            }
        }
        System.out.println(cnt);
        return FaceGroupList;
    }

    //将小的组合并到大的组，group_cnt是最后要分成几组，根据FaceGroup的size由小到大吃剩下的小碎片 bfs
    public List<List<Integer>> UnionSmallGroup1(int group_cnt) {
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size()-o1.size();
            }
        });
        int[] visited = new int[faceList.size()];
        Arrays.fill(visited, 0);
        for (int i = 0; i < group_cnt; i++) {
            List<Integer> GroupList = FaceGroupList.get(i);
            for (int j : GroupList) {
                visited[j] = i+100;
            }
        }
        for (int i = group_cnt-1; i >= 0; i--) {
            Queue<Integer> queue = new LinkedList<>();
            List<Integer> GroupList = FaceGroupList.get(i);
            int[] temp_visited = visited.clone();
            int root = GroupList.get(0);
            queue.add(root);
            while (!queue.isEmpty()) {
                int temp = queue.poll();
                int[] Face_index = face_graph[temp];
                for (int j : Face_index) {
                    if (temp_visited[j] == 0) {
                        queue.add(j);
                        GroupList.add(j);
                        temp_visited[j] = -1;
                        visited[j] = -1;
                    }else if (temp_visited[j] == i+100) {
                        queue.add(j);
                        temp_visited[j] = -1;
                    }
                }
            }
        }
        int cnt = 0;
        int index = 0;
        Iterator<List<Integer>> iterator = FaceGroupList.iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next();
            if (index >= group_cnt) {
                iterator.remove();
            }else {
                cnt += list.size();
            }
            index++;
        }
        System.out.println(cnt);
        return FaceGroupList;
    }

    public List<List<Integer>> UnionSmallGroup2(int group_cnt) {
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size()-o1.size();
            }
        });
        double[][] distance = new double[faceList.size()][group_cnt];
        for (double[] temp : distance) {
            Arrays.fill(temp, 1000000);
        }
        Boolean[] visited = new Boolean[faceList.size()];
        Arrays.fill(visited, false);
        for (int i = 0; i < group_cnt; i++) {
            List<Integer> GroupList = FaceGroupList.get(i);
            for (int j : GroupList) {
                visited[j] = true;
                distance[j][i] = 0;
            }
        }
        for (int i = 0; i < group_cnt; i++) {
            PriorityQueue<Dij> priorityQueue = new PriorityQueue<>(new Comparator<Dij>() {
                @Override
                public int compare(Dij o1, Dij o2) {
                    return (int) (o1.distance - o2.distance);
                }
            });
            List<Integer> GroupList = FaceGroupList.get(i);
            Boolean[] temp_visited = visited.clone();
            for (int j : GroupList) {
                temp_visited[j] = false;
            }
            int root = GroupList.get(0);
            priorityQueue.add(new Dij(root, distance[root][i]));
            temp_visited[root] = true;
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll().getIndex();
                int[] Face_index = face_graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        distance[j][i] = Math.min(distance[j][i], distance[temp][i] + 1);
                        priorityQueue.add(new Dij(j, distance[j][i]));
                        temp_visited[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < faceList.size(); i++) {
            if (visited[i] == true) {
                continue;
            }
            int index = -1;
            double min = 10000000;
            for (int j = 0; j < group_cnt; j++) {
                System.out.print(distance[i][j] + " ");
                if (distance[i][j] < min) {
                    min = distance[i][j];
                    index = j;
                }
            }
            System.out.println();
            if (min < 3) {
                FaceGroupList.get(index).add(i);
            }else {
                if (distance[i][group_cnt-1] > 10000) {
                    FaceGroupList.get(index).add(i);
                }else {
                    FaceGroupList.get(group_cnt-1).add(i);
                }
            }
        }
        for (int i = group_cnt; i < FaceGroupList.size(); i++) {
            FaceGroupList.get(i).clear();
        }
        int cnt = 0;
        Iterator<List<Integer>> iterator = FaceGroupList.iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next();
            cnt += list.size();
            if (list.size() == 0) {
                iterator.remove();
            }
        }
        System.out.println(cnt);
        return FaceGroupList;
    }

    public List<List<Integer>> UnionSmallGroup3(int group_cnt) {
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size()-o1.size();
            }
        });
        double[][] distance = new double[faceList.size()][group_cnt];
        for (double[] temp : distance) {
            Arrays.fill(temp, 1000000);
        }
        Boolean[] visited = new Boolean[faceList.size()];
        Arrays.fill(visited, false);
        for (int i = 0; i < group_cnt; i++) {
            List<Integer> GroupList = FaceGroupList.get(i);
            for (int j : GroupList) {
                visited[j] = true;
                distance[j][i] = 0;
            }
        }
        for (int i = 0; i < group_cnt; i++) {
            PriorityQueue<Dij> priorityQueue = new PriorityQueue<>(new Comparator<Dij>() {
                @Override
                public int compare(Dij o1, Dij o2) {
                    return (int) (o1.distance - o2.distance);
                }
            });
            List<Integer> GroupList = FaceGroupList.get(i);
            Boolean[] temp_visited = visited.clone();
            for (int j : GroupList) {
                temp_visited[j] = false;
            }
            int root = GroupList.get(0);
            priorityQueue.add(new Dij(root, distance[root][i]));
            temp_visited[root] = true;
            while (!priorityQueue.isEmpty()) {
                int temp = priorityQueue.poll().getIndex();
                int[] Face_index = face_graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        NVector VectorA = FaceA.getNVector();
                        NVector VectorB = FaceB.getNVector();
                        double intermediate = VectorA.DotProduct(VectorB) / VectorA.getRank() / VectorB.getRank();
                        double angle = Math.acos(intermediate) * 180 / Math.PI;
                        angle = Math.min(angle, 180-angle);
                        distance[j][i] = Math.min(distance[j][i], distance[temp][i] + angle);
                        priorityQueue.add(new Dij(j, distance[j][i]));
                        temp_visited[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < faceList.size(); i++) {
            if (visited[i] == true) {
                continue;
            }
            int index = -1;
            double min = 10000000;
            for (int j = 0; j < group_cnt; j++) {
                if (distance[i][j] < min) {
                    min = distance[i][j];
                    index = j;
                }
            }
            if (min < 30) {
                FaceGroupList.get(index).add(i);
            }else {
                FaceGroupList.get(group_cnt-1).add(i);
            }
        }
        for (int i = group_cnt; i < FaceGroupList.size(); i++) {
            FaceGroupList.get(i).clear();
        }
        int cnt = 0;
        Iterator<List<Integer>> iterator = FaceGroupList.iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next();
            cnt += list.size();
            if (list.size() == 0) {
                iterator.remove();
            }
        }
        System.out.println(cnt);
        return FaceGroupList;
    }

    public List<List<Integer>> UnionSmallGroup4() {
        FaceGroupList.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o2.size()-o1.size();
            }
        });
        double[][] angle_distance = new double[faceList.size()][group_cnt];
        double[][] step_distance = new double[faceList.size()][group_cnt];
        for (double[] temp : angle_distance) {
            Arrays.fill(temp, 1000000);
        }
        for (double[] temp : step_distance) {
            Arrays.fill(temp, 1000000);
        }
        Boolean[] visited = new Boolean[faceList.size()];
        Arrays.fill(visited, false);
        for (int i = 0; i < group_cnt; i++) {
            List<Integer> GroupList = FaceGroupList.get(i);
            for (int j : GroupList) {
                visited[j] = true;
                angle_distance[j][i] = 0;
                step_distance[j][i] = 0;
            }
        }
        for (int i = 0; i < group_cnt; i++) {
            PriorityQueue<Dij> angle_priorityQueue = new PriorityQueue<>(new Comparator<Dij>() {
                @Override
                public int compare(Dij o1, Dij o2) {
                    return (int) (o1.distance - o2.distance);
                }
            });
            PriorityQueue<Dij> step_priorityQueue = new PriorityQueue<>(new Comparator<Dij>() {
                @Override
                public int compare(Dij o1, Dij o2) {
                    return (int) (o1.distance - o2.distance);
                }
            });
            List<Integer> GroupList = FaceGroupList.get(i);
            Boolean[] temp_visited = visited.clone();
            for (int j : GroupList) {
                temp_visited[j] = false;
            }
            int root = GroupList.get(0);
            angle_priorityQueue.add(new Dij(root, angle_distance[root][i]));
            temp_visited[root] = true;
            while (!angle_priorityQueue.isEmpty()) {
                int temp = angle_priorityQueue.poll().getIndex();
                int[] Face_index = face_graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        NVector VectorA = FaceA.getNVector();
                        NVector VectorB = FaceB.getNVector();
                        double intermediate = VectorA.DotProduct(VectorB) / VectorA.getRank() / VectorB.getRank();
                        double angle = Math.acos(intermediate) * 180 / Math.PI;
                        angle = Math.min(angle, 180-angle);
                        angle_distance[j][i] = Math.min(angle_distance[j][i], angle_distance[temp][i] + angle);
                        angle_priorityQueue.add(new Dij(j, angle_distance[j][i]));
                        step_distance[j][i] = Math.min(step_distance[j][i], step_distance[temp][i] + 1);
                        step_priorityQueue.add(new Dij(j, step_distance[j][i]));
                        temp_visited[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < faceList.size(); i++) {
            if (visited[i] == true) {
                continue;
            }
            int angle_index = -1;
            double angle_min = 10000000;
            int step_index = -1;
            double step_min = 10000000;
            int[] step_order = new int[group_cnt];
            for (int j = 0; j < group_cnt; j++) {
                if (angle_distance[i][j] < angle_min) {
                    angle_min = angle_distance[i][j];
                    angle_index = j;
                }
                if (step_distance[i][j] < step_min) {
                    step_min = step_distance[i][j];
                    step_index = j;
                }
                for (int k = 0; k < group_cnt; k++) {
                    if (step_distance[i][k] < step_distance[i][j]) {
                        step_order[j]++;
                    }
                }
            }
            if (step_index == group_cnt-1) {
                FaceGroupList.get(step_index).add(i);
            }else {
                if (step_min < 5) {
                    FaceGroupList.get(step_index).add(i);
                }else if (angle_min < 30) {
                    FaceGroupList.get(angle_index).add(i);
                }else {
                    if (step_distance[i][group_cnt-1] > 1000) {
                        FaceGroupList.get(step_index).add(i);
                    }else {
                        FaceGroupList.get(group_cnt-1).add(i);
                    }
                }
            }
        }
        for (int i = group_cnt; i < FaceGroupList.size(); i++) {
            FaceGroupList.get(i).clear();
        }
        int cnt = 0;
        Iterator<List<Integer>> iterator = FaceGroupList.iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next();
            cnt += list.size();
            if (list.size() == 0) {
                iterator.remove();
            }
        }
        System.out.println(cnt);
        return FaceGroupList;
    }

    public DoubleLinkedList[] getBorderLine() throws Exception {
        List<Integer>[] dot_graph = new ArrayList[dotList.size()];
        for (int i = 0; i < dot_graph.length; i++) {
            dot_graph[i] = new ArrayList<>();
        }
        for (int i = 0; i < face_graph.length; i++) {
            Face faceA = faceList.get(i);
            List<Integer> dot_indices = faceA.getDot_indices();
            int a = dot_indices.get(0);
            int b = dot_indices.get(1);
            int c = dot_indices.get(2);
            int cnt = 0;
            for (int j = 0; j < 3; j++) {
                if (face_graph[i][j] != -1) {
                    cnt++;
                }
            }
            if (cnt < 3) {
                Boolean[] line = new Boolean[3];
                Arrays.fill(line, true);
                for (int j = 0; j < 3; j++) {
                    if (face_graph[i][j] != -1) {
                        Face faceB = faceList.get(face_graph[i][j]);
                        List<Integer> dot_indicesB = faceB.getDot_indices();
                        if (dot_indicesB.contains(a) && dot_indicesB.contains(b)) {
                            line[0] = false;
                        }else if (dot_indicesB.contains(b) && dot_indicesB.contains(c)) {
                            line[1] = false;
                        }else if (dot_indicesB.contains(a) && dot_indicesB.contains(c)) {
                            line[2] = false;
                        }
                    }
                }
                if (line[0]) {
                    dot_graph[a].add(b);
                    dot_graph[b].add(a);
                }
                if (line[1]) {
                    dot_graph[b].add(c);
                    dot_graph[c].add(b);
                }
                if (line[2]) {
                    dot_graph[a].add(c);
                    dot_graph[c].add(a);
                }
            }
        }
        for (int i = 0; i < dot_graph.length; i++) {
            if (dot_graph[i].size() != 0 && dot_graph[i].size() != 2) {
                System.out.println(dot_graph[i].size());
            }
        }
        int root = -1;
        int[] visited = new int[dotList.size()];
        DoubleLinkedList[] doubleLinkedLists = new DoubleLinkedList[2];
        for (int cnt = 0; cnt < 2; cnt++) {
            doubleLinkedLists[cnt] = new DoubleLinkedList();
            while (doubleLinkedLists[cnt].size() < 50) {
                doubleLinkedLists[cnt] = new DoubleLinkedList();
                for (int i = root+1; i < dot_graph.length; i++) {
                    if(dot_graph[i].size() == 2 && visited[i] == 0) {
                        doubleLinkedLists[cnt].push_back(dotList.get(i));
                        visited[i]++;
                        root = i;
                        break;
                    }
                }
                int prev = root;
                while (true) {
                    if (dot_graph[prev].size() != 2) {
                        System.out.println("dot_graph[" + prev + "].size() = " + dot_graph[prev].size());
                    }else {
                        int a = dot_graph[prev].get(0);
                        int b = dot_graph[prev].get(1);
                        if (visited[a] == 0) {
                            doubleLinkedLists[cnt].push_back(dotList.get(a));
                            visited[a]++;
                            prev = a;
                        }else if (visited[b] == 0) {
                            doubleLinkedLists[cnt].push_back(dotList.get(b));
                            visited[b]++;
                            prev = b;
                        }else {
                            break;
                        }
                    }
                }
            }
        }
        return doubleLinkedLists;
    }

    public List<DoubleLinkedList> getSectionBorderLine() throws Exception {
        List<DoubleLinkedList> ret = new ArrayList<>();
        List<Integer> FaceGroup = FaceGroupList.get(FaceGroupList.size()-1);
        HashSet<Integer>[] dot_graph = new HashSet[dotList.size()];
        for (int i = 0; i < dot_graph.length; i++) {
            dot_graph[i] = new HashSet<>();
        }
        HashSet<Integer> Group = new HashSet<>();
        HashSet<Integer> visited = new HashSet<>();
        for (int i = 0; i < FaceGroup.size(); i++) {
            Group.add(FaceGroup.get(i));
        }
        int index = 0;
        while (index < FaceGroup.size()) {
            int root = FaceGroup.get(index);
            if (visited.contains(root)) {
                index++;
                continue;
            }
            Queue<Integer> queue = new LinkedList<>();
            queue.add(root);
            visited.add(root);
            while (!queue.isEmpty()) {
                int temp = queue.poll();
                for (int i = 0; i < face_graph[temp].length; i++) {
                    int neighbor = face_graph[temp][i];
                    if (visited.contains(neighbor)) {
                        continue;
                    }
                    if (Group.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }else {
                        Face faceA = faceList.get(temp);
                        Face faceB = faceList.get(neighbor);
                        List<Integer> sharing = faceA.sharingDot(faceB);
                        int a = sharing.get(0);
                        int b = sharing.get(1);
                        dot_graph[a].add(b);
                        dot_graph[b].add(a);
                    }
                }
            }
            HashSet<Integer> BorderDots = new HashSet<>();
            for (int i = 0; i < dot_graph.length; i++) {
                if (dot_graph[i].size() != 0 & dot_graph[i].size() != 2) {
                    System.out.println("dot_graph[" + i + "].size=" + dot_graph[i].size());
                }else {
                    if (dot_graph[i].size() == 2) {
                        BorderDots.add(i);
                    }
                }
            }
            Iterator<Integer> iterator = BorderDots.iterator();
            HashSet<Integer> visited_dots = new HashSet<>();
            queue.clear();
            while (iterator.hasNext()) {
                int dot_index = iterator.next();
                if (visited_dots.contains(dot_index)) {
                    continue;
                }else {
                    DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
                    doubleLinkedList.push_back(dotList.get(dot_index));
                    queue.add(dot_index);
                    visited_dots.add(dot_index);
                    while (!queue.isEmpty()) {
                        int temp = queue.poll();
                        Iterator iterator1 = dot_graph[temp].iterator();
                        int a = (int) iterator1.next();
                        int b = (int) iterator1.next();
                        if (!visited_dots.contains(a)) {
                            doubleLinkedList.push_back(dotList.get(a));
                            queue.add(a);
                            visited_dots.add(a);
                        }else if (!visited_dots.contains(b)) {
                            doubleLinkedList.push_back(dotList.get(b));
                            queue.add(b);
                            visited_dots.add(b);
                        }
                    }
                    ret.add(doubleLinkedList);
                }
            }
        }
        ret.sort(new Comparator<DoubleLinkedList>() {
            @Override
            public int compare(DoubleLinkedList o1, DoubleLinkedList o2) {
                return o2.size() - o1.size();
            }
        });
        return ret.subList(0,2);
    }

    public int[][] match(PLYModel plyModel2) throws Exception {
        List<DoubleLinkedList> list1 = this.getSectionBorderLine();
        List<DoubleLinkedList> list2 = plyModel2.getSectionBorderLine();
        DoubleLinkedList doubleLinkedList1 = list1.get(0);
        DoubleLinkedList doubleLinkedList2 = list2.get(1);
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
        return ret;
    }

    public PLYModel() {
        this.dotList = new ArrayList<>();
        this.faceList = new ArrayList<>();
        this.face_graph = new int[dotList.size()][dotList.size()];
        this.FaceGroupList = new ArrayList<>();
        this.dotMap = new HashMap<>();
    }
}
