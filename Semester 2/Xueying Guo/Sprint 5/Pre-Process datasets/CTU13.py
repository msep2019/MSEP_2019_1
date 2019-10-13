import pandas as pd
import numpy as np
import re
from sklearn.utils import shuffle

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

input1 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CTU-13/new_pre_ctu1.csv'
input2 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CTU-13/new_pre_ctu2.csv'

training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CTU-13/Output/CTU13_train.csv'
testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CTU-13/Output/CTU13_test.csv'

DF1 = pd.read_csv(input1,encoding = "utf-8",dtype=str,header=None)
DF2 = pd.read_csv(input2,encoding = "utf-8",dtype=str,header=None)

DF1=DF1.drop(index=[0])
DF2=DF2.drop(index=[0])

DataDF=pd.concat([DF1, DF2],axis=0, ignore_index=True)

print(DataDF.shape)
print(DataDF.head(3))

DataDF=DataDF.drop([0,1],axis=1)

#print(DataDF.groupby([9],as_index=False)[9].agg({'cnt':'count'}))

mapping9 = {label:idx for idx,label in enumerate(np.unique(DataDF[9].astype(str)))}
#print(mapping9)

DataDF[2]=DataDF[2].astype(float)

DataDF[3]=DataDF[3].astype(float)

DataDF[4]=DataDF[4].astype(float)

DataDF[7]=DataDF[7].astype(float)

DataDF[8]=DataDF[8].astype(float)

DataDF[9]=DataDF[9].map(mapping9)

DataDF[15]=DataDF[15].apply(lambda x: 0 if 'Normal' in x else ( 1 if 'Botnet' in x else 2)  )

print(DataDF.groupby([15],as_index=False)[15].agg({'cnt':'count'}))


print(DataDF.shape)
print(DataDF.head(3))

DF=shuffle(DataDF)
row=DF.shape[0]

trainrow=round(row * 0.7)

train=DF[0:trainrow]
test=DF[trainrow:row]

train.to_csv(training,sep=',',header=False,index=False)
test.to_csv(testing,sep=',',header=False,index=False)

print("finish")
