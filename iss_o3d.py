import open3d as o3d
import numpy as np
import time

def keypoints_to_spheres(keypoints):
        spheres = o3d.geometry.TriangleMesh()
        for keypoint in keypoints.points:
         sphere = o3d.geometry.TriangleMesh.create_sphere(radius=1)
         sphere.translate(keypoint)
         spheres += sphere
         spheres.paint_uniform_color([1.0, 0.75, 0.0])
        return spheres


def get_iss_o3d():
    pcd = o3d.io.read_point_cloud("sa5_pointcloud.pcd")
    tic = time.time()
    keypoints = o3d.geometry.keypoint.compute_iss_keypoints(pcd,
                                                            salient_radius=1,
                                                            non_max_radius=1,
                                                            gamma_21=0.8,
                                                            gamma_32=0.5)
    toc = 1000 * (time.time() - tic)
    print("ISS Computation took {:.0f} [ms]".format(toc))
    print("Extract", keypoints)
    print(len(keypoints.points))

    pcd.paint_uniform_color([0.0, 1.0, 0.0])
    o3d.visualization.draw_geometries([keypoints_to_spheres(keypoints),pcd], width=800, height=800)

    return keypoints.points



