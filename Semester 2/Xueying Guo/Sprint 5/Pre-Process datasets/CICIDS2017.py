import math

import pandas as pd

from sklearn.utils import shuffle

import os
import time
import numpy as np

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)


path = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICIDS2017/Full_Dataset.csv'
train_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICIDS2017/Output/CICIDS2017_train.csv'
test_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICIDS2017/Output/CICIDS2017_test.csv'
#files= os.listdir(path)

DataDF= pd.read_csv(path, encoding="utf-8", dtype=str,header=None)

print(DataDF.shape)
print(DataDF.head(3))

DataDF=DataDF.drop([0,6],axis=1)

print(DataDF.shape)
print(DataDF.head(3))

# no need to change: 2, 4, 5, 7-12, 13-22, 23-83

DataDF[1]=DataDF[1].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )
DataDF = DataDF.dropna()

DataDF[3]=DataDF[3].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )

DataDF = DataDF.dropna()

print(DataDF.shape)
print(DataDF.head(3))



DF=shuffle(DataDF)
row=DF.shape[0]

trainrow=round(row * 0.7)

print(row)

print(trainrow)

train=DF[0:trainrow]
test=DF[trainrow:row]

print(train.shape)

print(test.shape)


train.to_csv(train_output,sep=',',header=False,index=False)
test.to_csv(test_output,sep=',',header=False,index=False)
