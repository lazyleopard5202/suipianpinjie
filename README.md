# usage

1. 

``` java
String ply_path = "src/sa"+5+"_color5.ply";
MyPLYModel plyModel = new MyPLYModel();
plyModel.readPLY(ply_path);
plyModel.hierarchicalClusteringInit();
plyModel.hierarchicalClustering(6);
```

2. 

```java
String ply_path = "src/sa5_color5.ply";
MyPLYModel plyModel = new MyPLYModel();
plyModel.readFromExistColor(ply_path);
plyModel.hierarchicalClustering2(plyModel, 6);
```

