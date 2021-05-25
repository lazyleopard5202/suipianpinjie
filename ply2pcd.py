import numpy as np
import open3d as o3d
# ply转pcd
pcd = o3d.io.read_point_cloud("sa5_color5.ply")
o3d.io.write_point_cloud("sa5_pointcloud.pcd", pcd)
points = np.asarray(pcd.points)
print('The original numbers of point: '+str(points.shape[0]))


