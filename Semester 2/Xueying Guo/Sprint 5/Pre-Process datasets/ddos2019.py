import pandas as pd
import numpy as np
import re
from sklearn.utils import shuffle

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

input1 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/DDOS2019/03-11/LDAP.csv'
training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/DDOS2019/Output/DDOS2019_train.csv'
testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/DDOS2019/Output/DDOS2019_test.csv'

DataDF = pd.read_csv(input1,encoding = "utf-8",dtype=str,header=None,error_bad_lines=False)

print(DataDF.shape)
print(DataDF.head(3))

DataDF=DataDF.drop([0,1,7],axis=1)

DataDF[2]=DataDF[2].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )
DataDF = DataDF.dropna()

DataDF[4]=DataDF[4].apply(lambda x: (float(x.split('.')[0]) * 256 * 256 * 256 + float(x.split('.')[1]) * 256 * 256 + float(x.split('.')[2]) * 256 + float(x.split('.')[3]) ) if x.count('.')==3 else np.nan )
DataDF = DataDF.dropna()

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
