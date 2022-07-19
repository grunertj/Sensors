import warnings
warnings.filterwarnings("ignore")
import pandas as pd
import numpy as np
from scipy.signal import argrelextrema
from scipy.signal import find_peaks_cwt
from scipy.ndimage.filters import gaussian_filter1d
import matplotlib.pyplot as plt
from zoom_factory import *
# https://stackoverflow.com/questions/25571260/scipy-signal-find-peaks-cwt-not-finding-the-peaks-accurately
rw = 100
order = 8

da = pd.read_csv('data/Accelerometer.csv')
da.rename(columns={da.columns[0]:"Time",da.columns[1]:"X",da.columns[2]:"Y",da.columns[3]:"Z"},inplace = True)
# da['MA'] = da.Y.rolling(window=rw).mean()
da['MA'] = gaussian_filter1d(da.Y.values, sigma=40)
da['MIN'] = da.iloc[argrelextrema(da.MA.values, comparator=np.less, mode='wrap', order=order)[0]]['MA']
# da['MIN'] = gaussian_filter1d(da.MA.values, sigma=5)
# da['MINN'] = da.iloc[argrelextrema(da.MA.values, comparator=np.less, mode='wrap', order=order)[0]]['MA']
# # da['MINP'] = da['MINN'].interpolate(method='polynomial', order=3)
# # da['MINP'] = da['MINN'].interpolate(method='linear', limit_direction='forward', axis=0)
# da['MIN'] = da.iloc[argrelextrema(da.MINP.values, comparator=np.less, mode='wrap', order=order)[0]]['MINP']

dg = pd.read_csv('data/Gyroscope.csv')
dg.rename(columns={dg.columns[0]:"Time",dg.columns[1]:"X",dg.columns[2]:"Y",dg.columns[3]:"Z"},inplace = True)
dg['MA'] = dg.Z.rolling(window=rw).mean()
dg['GRAD'] = np.gradient(dg.MA)

dl = pd.read_csv('data/Location.csv')
dl.rename(columns={dl.columns[0]:"Time",dl.columns[1]:"LAT",dl.columns[2]:"LONG",dl.columns[3]:"H",dl.columns[4]:"V",dl.columns[5]:"D",dl.columns[6]:"HA",dl.columns[6]:"VA"},inplace = True)
dl['MA'] = dl.V.rolling(window=int(rw/10)).mean()


fig, ax = plt.subplots()
# ax.plot(da.Time,da.X)
ax.plot(da.Time,da.MA)
ax.scatter(da.Time,da.MIN)
ax.plot(dg.Time,dg.MA)
ax.plot(dl.Time,dl.MA)

scale = 1.5
f = zoom_factory(ax,base_scale = scale)
plt.show()





