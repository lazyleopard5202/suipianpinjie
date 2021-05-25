import matplotlib.pyplot as plt
import pandas as pd
from sklearn.cluster import SpectralClustering
from sklearn.preprocessing import StandardScaler
from mpl_toolkits.mplot3d import Axes3D
from suipianpinjie.iss_o3d import get_iss_o3d

data = pd.DataFrame(get_iss_o3d())

data1 = data.iloc[:,0:3]


transfer = StandardScaler()
data_new = transfer.fit_transform(data1)


Spectral = SpectralClustering(n_clusters=2)
y_pred = Spectral.fit_predict(data_new)

fig = plt.figure()
ax = Axes3D(fig)
for i in range(2):
    ax.scatter3D(data_new[y_pred == i,0],data_new[y_pred == i,1],data_new[y_pred == i,2],marker = ".")
ax.view_init(elev = 60,azim = 30)
ax.set_zlabel('Z')
ax.set_ylabel('Y')
ax.set_xlabel('X')
plt.show()
