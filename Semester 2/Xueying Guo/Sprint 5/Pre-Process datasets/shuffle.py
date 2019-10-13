import math

import pandas as pd

from sklearn.utils import shuffle

import os
import time
import numpy as np

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

path = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/BotIot/new/Iot_Botnet.csv'
train_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/BotIot/new/IOT_train.csv'
test_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/BotIot/new/IOT_test.csv'
#files= os.listdir(path)

DF= pd.read_csv(path, encoding="utf-8", dtype=str,header=None)

print(DF.shape)

DF=shuffle(DF)
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
