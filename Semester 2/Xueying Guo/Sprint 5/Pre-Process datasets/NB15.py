import pandas as pd
import numpy as np
import re
from sklearn.utils import shuffle

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

input1 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW_NB15/UNSW-NB15_1.csv'
input2 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW_NB15/UNSW-NB15_2.csv'
training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW_NB15/Output/NB15_train.csv'
testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW_NB15/Output/NB15_test.csv'

DF1 = pd.read_csv(input1,encoding = "utf-8",dtype=str,header=None)
DF2 = pd.read_csv(input2,encoding = "utf-8",dtype=str,header=None)

DataDF=pd.concat([DF1, DF2],axis=0, ignore_index=True)

print(DataDF.shape)
print(DataDF.head(3))

DataDF=DataDF.drop([47],axis=1)

DataDF = DataDF.dropna()

#print(DataDF.groupby(['dbytes'],as_index=False)['dbytes'].agg({'cnt':'count'}))

mapping4 = {label:idx for idx,label in enumerate(np.unique(DataDF[4]))}
#print(mapping4)

mapping5 = {label:idx for idx,label in enumerate(np.unique(DataDF[5]))}
#print(mapping5)

mapping13 = {label:idx for idx,label in enumerate(np.unique(DataDF[13]))}
#print(mapping13)


DataDF[0]=DataDF[0].map(lambda x: float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) )

DataDF[1]=DataDF[1].apply(lambda x: x if re.search("^\d+$", str(x)) else np.nan)

DataDF[2]=DataDF[2].map(lambda x: float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) )

DataDF[3]=DataDF[3].apply(lambda x: x if re.search("^\d+$", str(x)) else np.nan)

DataDF[4]=DataDF[4].map(mapping4)

DataDF[5]=DataDF[5].map(mapping5)

DataDF[6]=DataDF[6].astype(float)

DataDF[7]=DataDF[7].astype(float)

DataDF[8]=DataDF[8].astype(float)

DataDF[9]=DataDF[9].astype(float)

DataDF[10]=DataDF[10].astype(float)

DataDF[11]=DataDF[11].astype(float)

DataDF[12]=DataDF[12].astype(float)

DataDF[13]=DataDF[13].map(mapping13)

DataDF[14]=DataDF[14].astype(float)

DataDF[16]=DataDF[16].astype(float)

DataDF[17]=DataDF[17].astype(float)

DataDF[18]=DataDF[18].astype(float)

DataDF[19]=DataDF[19].astype(float)

DataDF[20]=DataDF[20].astype(float)

DataDF[21]=DataDF[21].astype(float)

DataDF[22]=DataDF[22].astype(float)

DataDF[23]=DataDF[23].astype(float)

DataDF[24]=DataDF[24].astype(float)

DataDF[25]=DataDF[25].astype(float)

DataDF[26]=DataDF[26].astype(float)

DataDF[27]=DataDF[27].astype(float)

DataDF[28]=DataDF[28].astype(float)

DataDF[29]=DataDF[29].astype(float)

DataDF[30]=DataDF[30].astype(float)

DataDF[31]=DataDF[31].astype(float)

DataDF[32]=DataDF[32].astype(float)

DataDF[33]=DataDF[33].astype(float)

DataDF[34]=DataDF[34].astype(float)

DataDF[35]=DataDF[35].astype(float)

DataDF[36]=DataDF[36].astype(float)

DataDF[37]=DataDF[37].astype(float)

DataDF[38]=DataDF[38].astype(float)

DataDF[39]=DataDF[39].astype(float)

DataDF[40]=DataDF[40].astype(float)

DataDF[41]=DataDF[41].astype(float)

DataDF[42]=DataDF[42].astype(float)

DataDF[43]=DataDF[43].astype(float)

DataDF[44]=DataDF[44].astype(float)

DataDF[45]=DataDF[45].astype(float)

DataDF[46]=DataDF[46].astype(float)

DataDF = DataDF.dropna()


print(DataDF.head(3))
print(DataDF.shape)

DF=shuffle(DataDF)
row=DF.shape[0]

trainrow=round(row * 0.7)

train=DF[0:trainrow]
test=DF[trainrow:row]

train.to_csv(training,sep=',',header=False,index=False)
test.to_csv(testing,sep=',',header=False,index=False)

print("finish")
