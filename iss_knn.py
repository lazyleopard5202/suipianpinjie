import numpy as np
from scipy.spatial import KDTree
import open3d as o3d


def get_iss_knn():
	knn = 20
	t1 = 0.05
	t2 = 0.05
	path = 'sa5_pointcloud.pcd'
	pcd = o3d.io.read_point_cloud(path)
	point = np.asarray(pcd.points)
	point_size = point.shape[0]
	ISS = np.array([0, 0, 0])
	mean_knn_source = np.zeros((point_size, knn, 3))
	mean_keypoint_source = np.zeros((point_size, 3))
	for i in range(point_size):
		tree = o3d.geometry.KDTreeFlann(pcd)
		[_, idx, dis] = tree.search_knn_vector_3d(point[i], knn + 1)
		mean_keypoint_source[i] = point[i]
		mean_knn_source[i] = point[idx[1:]]
		d_vector = mean_knn_source[i] - mean_keypoint_source[i]
		C_tem = np.sum(np.reshape((np.sum(d_vector[-1] ** 2) ** (1 / 2) - np.sum(d_vector ** 2, axis=1) ** (1 / 2)),
								  (knn, 1, 1)) * np.matmul(np.expand_dims(d_vector, axis=-1),
														   np.transpose(np.expand_dims(d_vector, axis=-1),
																		(0, 2, 1))), axis=0) / np.sum(
			(np.sum(d_vector[-1] ** 2) ** (1 / 2) - np.sum(d_vector[1:] ** 2, axis=1) ** (1 / 2)))
		eigvalue, eigvector = np.linalg.eig(C_tem)
		eigvalue = np.sort(eigvalue)
		if eigvalue[1] / eigvalue[2] <= t1 and eigvalue[0] / eigvalue[1] <= t2:
			ISS = np.vstack((ISS, point[i]))
	iss_point = ISS[1:]
	print(iss_point)

	return iss_point



