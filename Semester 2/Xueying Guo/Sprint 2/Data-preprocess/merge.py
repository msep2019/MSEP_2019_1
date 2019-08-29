#merge CIDD datasets

import math

import pandas as pd
import os
import time
import numpy as np

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

path = "/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CIDDS/pre-CIDDS/" #folder
output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CIDDS/CIDD_merge/merge_all.csv'
files= os.listdir(path)

input = []
csv_DF = []
final_DF = pd.DataFrame(columns=['f1', 'f2','f3','f4','f5','f6','f7','f8','f9','f10','f11', 'Label'])

feature_num = 11
i = 0

#i=0
print(final_DF.shape)

path_str = ""

now=time.time()
local_time = time.localtime(now)
current =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Start time:", current)


for file in files:
    if ((not os.path.isdir(file)) and (file.__contains__(".csv"))):
        print("merging")
        print(file)
        path_str = path + file
        csv_DF1 = pd.read_csv(path_str, encoding="utf-8", dtype=str)
#        for i in range(len(csv_DF1)):
#            if( (csv_DF1.iloc[i,feature_num]!='normal') and (csv_DF1.iloc[i,feature_num] != 'suspicious')  ):
#                csv_DF1.iloc[i,feature_num] = "abnormal"

        csv_DF2 = pd.concat([final_DF, csv_DF1],axis=0, ignore_index=True)
        final_DF = csv_DF2

print("finish merging")
final_DF = final_DF.dropna()

print("load into csv file.")
final_DF.to_csv(output,sep=',',header=False,index=False)

now=time.time()
local_time = time.localtime(now)
current =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Finish time:", current)
