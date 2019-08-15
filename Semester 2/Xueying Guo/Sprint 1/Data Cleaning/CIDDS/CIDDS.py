import pandas as pd
import time

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

inputfileNameStr = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CIDDS001/traffic/OpenStack/CIDDS-001-internal-week1.csv'
#inputfileNameStr = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CIDDS001/test.csv'
outputfileNameStr_testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CIDDS001/Output/CIDDS-week1.csv'
DataDF = pd.read_csv(inputfileNameStr,encoding = "utf-8",dtype=str )

#print(DataDF.shape)
#print(DataDF)

now=time.time()
local_time = time.localtime(now)
finish_time =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Drop at:", finish_time)

DataDF = DataDF.drop(['Flows','Tos'],axis=1)

#DataDF['Date first seen'] = DataDF['Date first seen'].astype('float')
#print(DataDF.groupby(['Date first seen'],as_index=False)['Duration'].agg({'cnt':'count'}))

now=time.time()
local_time = time.localtime(now)
finish_time =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Start at:", finish_time)

DataDF['Duration'] = DataDF['Duration'].astype('float')
#print(DataDF.groupby(['Duration'],as_index=False)['Duration'].agg({'cnt':'count'}))

DataDF['Proto'] = DataDF['Proto'].map(str.strip)
#print(DataDF.groupby(['Proto'],as_index=False)['Proto'].agg({'cnt':'count'}))

DataDF['Src IP Addr'] = DataDF['Src IP Addr'].map(str.strip)
#print(DataDF.groupby(['Src IP Addr'],as_index=False)['Src IP Addr'].agg({'cnt':'count'}))

DataDF['Src Pt'] = DataDF['Src Pt'].map(str.strip)
#print(DataDF.groupby(['Src Pt'],as_index=False)['Src Pt'].agg({'cnt':'count'}))

DataDF['Dst IP Addr'] = DataDF['Dst IP Addr'].map(str.strip)
#print(DataDF.groupby(['Dst IP Addr'],as_index=False)['Dst IP Addr'].agg({'cnt':'count'}))

DataDF['Dst Pt'] = DataDF['Dst Pt'].map(str.strip)
#print(DataDF.groupby(['Dst Pt'],as_index=False)['Dst Pt'].agg({'cnt':'count'}))

DataDF['Packets'] = DataDF['Packets'].astype('int')
#print(DataDF.groupby(['Packets'],as_index=False)['Packets'].agg({'cnt':'count'}))

DataDF['Bytes'] = DataDF['Bytes'].map(str.strip)
#print(DataDF.groupby(['Bytes'],as_index=False)['Bytes'].agg({'cnt':'count'}))

DataDF['Flags'] = DataDF['Flags'].map(str.strip)
#print(DataDF.groupby(['Flags'],as_index=False)['Flags'].agg({'cnt':'count'}))

DataDF['class'] = DataDF['class'].map(str.strip)
#print(DataDF.groupby(['class'],as_index=False)['class'].agg({'cnt':'count'}))

DataDF['attackType'] = DataDF['attackType'].map(str.strip)
#print(DataDF.groupby(['attackType'],as_index=False)['attackType'].agg({'cnt':'count'}))

DataDF['attackID'] = DataDF['attackID'].map(str.strip)
#print(DataDF.groupby(['attackID'],as_index=False)['attackID'].agg({'cnt':'count'}))

DataDF['attackDescription'] = DataDF['attackDescription'].map(str.strip)
#print(DataDF.groupby(['attackDescription'],as_index=False)['attackDescription'].agg({'cnt':'count'}))

DataDF.to_csv(outputfileNameStr_testing,sep=',',header=True,index=False)
