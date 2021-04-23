package com.kaogu.Algorithm;

import java.util.*;

public class PLYModel {

    private List<Dot> dotList;    //记录点
    private List<Face> faceList;  //记录小三角形
    private List<List<Integer>> FaceGroupList; //记录FaceGroup，每一组FaceGroup中记录的是face的序号
    private int[][] graph;  //记录小三角形和哪三个小三角形相邻，graph = new int[dotlist,size][3],记录的是三个小三角形的序号

    public int[][] getGraph() {
        return graph;
    }

    public void setGraph(int[][] graph) {
        this.graph = graph;
    }

    public List<List<Integer>> getFaceGroupList() {
        return FaceGroupList;
    }

    public void setFaceGroupList(List<List<Integer>> faceGroupList) {
        FaceGroupList = faceGroupList;
    }

    public List<Dot> getDotList() {
        return dotList;
    }

    public void setDotList(List<Dot> dotList) {
        this.dotList = dotList;
    }

    public List<Face> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<Face> faceList) {
        this.faceList = faceList;
    }


    //生成图
    public void makeGraph() {
        this.graph = new int[faceList.size()][3];
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
                    graph[face_index][size[face_index]] = i;
                    size[face_index]++;
                    graph[i][size[i]] = face_index;
                    size[i]++;
                }
            }
        }
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

    //根据法向量夹角阈值分组，threshold为夹角阈值，核心想法是bfs
    public List<List<Integer>> ClassifyFaceGroup(double threshold) {
        this.makeGraph();
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
                            Vertex VertexA = mainAlgorithm.getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                            Vertex VertexB = mainAlgorithm.getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
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

    //将小的组合并到大的组，group_cnt是最后要分几组，核心想法是dijkstra
    public List<List<Integer>> UnionSmallGroup(int group_cnt) {
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
                int[] Face_index = graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        Vertex VertexA = mainAlgorithm.getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                        Vertex VertexB = mainAlgorithm.getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                        double intermediate = VertexA.DotProduct(VertexB) / VertexA.getRank() / VertexB.getRank();
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
                int[] Face_index = graph[temp];
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
                int[] Face_index = graph[temp];
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
                if (distance[i][j] < min) {
                    min = distance[i][j];
                    index = j;
                }
            }
            if (min < 5) {
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
                int[] Face_index = graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        Vertex VertexA = mainAlgorithm.getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                        Vertex VertexB = mainAlgorithm.getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                        double intermediate = VertexA.DotProduct(VertexB) / VertexA.getRank() / VertexB.getRank();
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

    public List<List<Integer>> UnionSmallGroup4(int group_cnt) {
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
                int[] Face_index = graph[temp];
                for (int j : Face_index) {
                    if (!temp_visited[j]) {
                        Face FaceA = faceList.get(j);
                        Face FaceB = faceList.get(temp);
                        Vertex VertexA = mainAlgorithm.getNormalVertex(dotList.get(FaceA.getDot_indices().get(0)), dotList.get(FaceA.getDot_indices().get(1)), dotList.get(FaceA.getDot_indices().get(2)));
                        Vertex VertexB = mainAlgorithm.getNormalVertex(dotList.get(FaceB.getDot_indices().get(0)), dotList.get(FaceB.getDot_indices().get(1)), dotList.get(FaceB.getDot_indices().get(2)));
                        double intermediate = VertexA.DotProduct(VertexB) / VertexA.getRank() / VertexB.getRank();
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
                if (step_min < 4) {
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

    public PLYModel() {
        this.dotList = new ArrayList<>();
        this.faceList = new ArrayList<>();
        this.graph = new int[dotList.size()][dotList.size()];
        this.FaceGroupList = new ArrayList<>();
    }
}
