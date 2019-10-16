import pandas as pd
import numpy as np
import re
from sklearn.utils import shuffle

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)


input1 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Combine/Benign.csv'
input2 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Combine/Scareware.csv'
#input3 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Combine/SMSmalware.csv'
training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Output/CIC_train2.csv'
testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Output/CIC_test2.csv'

DF1 = pd.read_csv(input1,encoding = "utf-8",dtype=str,header=None)
DF2 = pd.read_csv(input2,encoding = "utf-8",dtype=str,header=None)
#DF3 = pd.read_csv(input3,encoding = "utf-8",dtype=str,header=None)

DF1=DF1.drop(index=[0])
DF2=DF2.drop(index=[0])
#DF3=DF3.drop(index=[0])

row=DF1.shape[0]
get=round(row * 0.5)

DF1=DF1[0:get]

DataDF=pd.concat([DF1, DF2],axis=0, ignore_index=True)
#DataDF=pd.concat([DataDF, DF3],axis=0, ignore_index=True)

#DataDF=DataDF.drop([0,6,55,61,78],axis=1)
DataDF=DataDF.drop([0,6,55,61],axis=1)

#DataDF = DataDF.dropna()

print(DataDF.shape)
print(DataDF.head(3))

DataDF[1]=DataDF[1].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )
DataDF[3]=DataDF[3].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )

DataDF = DataDF.dropna()
#DataDF[3]=DataDF[3].map(lambda x: float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) )


# no need to change: 2, 4, 5, 7, 8, 9， 35-40， 49-54， 56, 62-77, 79-82

#print(DataDF.groupby([78],as_index=False)[78].agg({'cnt':'count'}))

print(DataDF.shape)
print(DataDF.head(3))
DF=shuffle(DataDF)

row=DF.shape[0]

trainrow=round(row * 0.7)

train=DF[0:trainrow]
test=DF[trainrow:row]

train.to_csv(training,sep=',',header=False,index=False)
test.to_csv(testing,sep=',',header=False,index=False)

print(train.shape)
print(test.shape)

print("finish")
