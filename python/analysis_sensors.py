import warnings
warnings.filterwarnings("ignore")
import pandas as pd
import numpy as np
from scipy.signal import argrelextrema
from scipy.signal import find_peaks_cwt
from scipy.ndimage.filters import gaussian_filter1d
import matplotlib.pyplot as plt
from zoom_factory import *

rw = 6
order = 2

df = pd.read_csv('data/sensor-2022_07_10_16_08_29.csv.gz')
da = df[df['Sensor']==10]
dg = df[df['Sensor']==4]
dl = df[df['Sensor']==100]
da['MA'] = gaussian_filter1d(da.Y.values, sigma=4)
da['MAX'] = da.iloc[argrelextrema(da.MA.values, comparator=np.greater, mode='wrap', order=order)[0]]['MA']
dg['MA'] = gaussian_filter1d(dg.Z.values, sigma=4)
#dg['MA'] = dg.Z.rolling(window=rw).mean()
dg['GRAD'] = np.gradient(dg.MA)
dl['MA'] = dl.X.rolling(window=int(rw/10)).mean()


fig, ax = plt.subplots()
# ax.plot(da.Time,da.X)
#ax.plot(da.Time,da.Y)
ax.plot(da.Time,da.MA)
ax.plot(dg.Time,dg.MA)
ax.plot(dl.Time,dl.X)
ax.scatter(da.Time,da.MAX)

scale = 1.5
f = zoom_factory(ax,base_scale = scale)
plt.show()

df = pd.merge(da,dg,on='Time',how='outer')
df = pd.merge_ordered(da,dg,on='Time',fill_method="ffill")
df = pd.merge_asof(da, dg, on="Time", direction="nearest",allow_exact_matches=True)

df = df[['Time','MA_x','MA_y','MAX','GRAD']]
df.rename(columns={df.columns[0]:"Time",df.columns[1]:"A",df.columns[2]:"G"},inplace = True)

df['LEFT'] = float('nan')
df['LEFT'] = np.where(((df.MAX > 0) & (df.GRAD < -0.0001)),df['MAX'],df['LEFT'])
#df['SIDE'] = np.where(((df.MAX > 0) & (df.GRAD > 0)),'Right',df['SIDE'])
#df[df['SIDE']!='nan']

fig, ax = plt.subplots()
# ax.plot(da.Time,da.X)
#ax.plot(da.Time,da.Y)
ax.plot(df.Time,df.A)
ax.plot(df.Time,df.G)
ax.scatter(df.Time,df.MAX)
ax.scatter(df.Time,df.LEFT)


scale = 1.5
f = zoom_factory(ax,base_scale = scale)
plt.grid()
plt.show()





df['GRAD'].fillna(method='ffill', inplace=True)
df['MA_x'].fillna(method='ffill', inplace=True)
df['MA_y'].fillna(method='ffill', inplace=True)

df = df[['Time','MA_x','MA_y','MAX','GRAD']]

df.dropna(subset=['MA_y'], how='all', inplace=True)
df.dropna(subset=['MA_y'], how='all', inplace=True)

df.rename(columns={df.columns[0]:"Time",df.columns[1]:"A",df.columns[2]:"G"},inplace = True)

fig, ax = plt.subplots()
# ax.plot(da.Time,da.X)
#ax.plot(da.Time,da.Y)
ax.plot(df.Time,df.A)
ax.plot(df.Time,df.G)
ax.scatter(df.Time,df.MAX)

scale = 1.5
f = zoom_factory(ax,base_scale = scale)
plt.show()




fig, ax = plt.subplots()
ax.plot(da.Time,da['X'], label='X')
ax.plot(da.Time,da['Y'], label='Y')
ax.plot(da.Time,da['Z'], label='Z')
ax.legend()

scale = 1.5
f = zoom_factory(ax,base_scale = scale)
plt.show()



dg['MA'] = dg.Z.rolling(window=rw).mean()
dg['GRAD'] = np.gradient(dg.MA)



dv = df[df['Sensor']==100]
dv['X'] = df['X']*3.6
dv['X'].plot()
plt.show()

