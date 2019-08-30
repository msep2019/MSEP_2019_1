import math

import pandas as pd
import os
import time
import numpy as np

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

path = "/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/BotIot/test/" #folder
output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/BotIot/merge/Iot_test.csv'
files= os.listdir(path)

input = []
csv_DF = []
final_DF = pd.DataFrame(columns=['f1', 'f2','f3','f4','f5','f6','f7','f8','f9','f10','f11','f12', 'f13','f14','f15','f16','f17','f18','f19','f20','f21','f22', 'f23','f24','f25','f26', 'Label'])

feature_num = 11
i = 0

#i=0
print(final_DF.shape)

path_str = ""

now=time.time()
local_time = time.localtime(now)
current =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Start time:", current)

print(final_DF.head(3))
for file in files:
    if ((not os.path.isdir(file)) and (file.__contains__(".csv"))):
        print("merging")
        print(file)
        path_str = path + file
        csv_DF1 = pd.read_csv(path_str, encoding="utf-8", dtype=str,header=None)
        csv_DF1 = csv_DF1.reset_index(drop=False)
        csv_DF1 = csv_DF1.rename(
            columns={0: 'f1', 1: 'f2', 2: 'f3', 3: 'f4', 4: 'f5',
                     5: 'f6', 6: 'f7', 7: 'f8', 8: 'f9',
                     9: 'f10', 10: 'f11', 11: 'f12',
                     12: 'f13', 13: 'f14', 14: 'f15',
                     15: 'f16', 16: 'f17', 17: 'f18',
                     18: 'f19', 19: 'f20', 20: 'f21',
                     21: 'f22', 22: 'f23', 23: 'f24', 24: 'f25',
                     25: 'f26', 26: 'Label'})
        csv_DF1 = csv_DF1.drop('index', axis=1)

        print(csv_DF1.head(3))
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
