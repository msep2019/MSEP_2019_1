import pandas as pd
import numpy as np
import re
from sklearn.utils import shuffle

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/NSL-KDD/KDDTrain+.txt'
testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/NSL-KDD/KDDTest+.txt'
train_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/NSL-KDD/Output/NSLKDDTrain.txt'
test_output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/NSL-KDD/Output/NSLKDDTest.txt'

DF1 = pd.read_csv(training,encoding = "utf-8",dtype=str,header=None,error_bad_lines=False)
DF2 = pd.read_csv(testing,encoding = "utf-8",dtype=str,header=None,error_bad_lines=False)

#print(DF1.shape)
#print(DF2.shape)

DataDF=pd.concat([DF1, DF2],axis=0, ignore_index=True)

DataDF=DataDF.drop([42],axis=1)

#print(DataDF.shape)
print(DataDF.head(3))

# start pre-processing

mapping1 = {label:idx for idx,label in enumerate(np.unique(DataDF[1]))}
#print(mapping1)

mapping2 = {label:idx for idx,label in enumerate(np.unique(DataDF[2]))}
#print(mapping2)

mapping3 = {label:idx for idx,label in enumerate(np.unique(DataDF[3]))}
#print(mapping3)

DataDF[1]=DataDF[1].map(mapping1)

DataDF[2]=DataDF[2].map(mapping2)

DataDF[3]=DataDF[3].map(mapping3)

DataDF[41]=DataDF[41].apply(lambda x: 0 if (x=="normal") else 1 )

#mapping41 = {label:idx for idx,label in enumerate(np.unique(DataDF[41]))}
#print(mapping41)

# end pre-processing

print(DataDF.head(3))

train=DataDF[:DF1.shape[0]]
test=DataDF[DF1.shape[0]:]

#print(train.shape)
#print(test.shape)

# output

train.to_csv(train_output,sep=',',header=False,index=False)
test.to_csv(test_output,sep=',',header=False,index=False)

print("finish")
