#this is for combine csv files within a folder, since it is not feasible to change file name manually

import columns as columns
import pandas as pd
import os

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

path = "/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/SMSmalware/Fakeinst/" #folder
output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/SMSmalware/Output/Fakeinst.csv'
files= os.listdir(path)

input = []
csv_DF = []
final_DF = pd.DataFrame(columns=['Flow ID', 'Source IP', 'Source Port', 'Destination IP', 'Destination Port', 'Protocol', 'Timestamp', 'Flow Duration', 'Total Fwd Packets', 'Total Backward Packets','Total Length of Fwd Packets', 'Total Length of Bwd Packets', 'Fwd Packet Length Max', 'Fwd Packet Length Min', 'Fwd Packet Length Mean', 'Fwd Packet Length Std','Bwd Packet Length Max', 'Bwd Packet Length Min', 'Bwd Packet Length Mean', 'Bwd Packet Length Std','Flow Bytes/s', 'Flow Packets/s', 'Flow IAT Mean', 'Flow IAT Std', 'Flow IAT Max', 'Flow IAT Min','Fwd IAT Total', 'Fwd IAT Mean', 'Fwd IAT Std', 'Fwd IAT Max', 'Fwd IAT Min','Bwd IAT Total', 'Bwd IAT Mean', 'Bwd IAT Std', 'Bwd IAT Max', 'Bwd IAT Min','Fwd PSH Flags', 'Bwd PSH Flags', 'Fwd URG Flags', 'Bwd URG Flags', 'Fwd Header Length', 'Bwd Header Length','Fwd Packets/s', 'Bwd Packets/s', 'Min Packet Length', 'Max Packet Length', 'Packet Length Mean', 'Packet Length Std', 'Packet Length Variance','FIN Flag Count', 'SYN Flag Count', 'RST Flag Count', 'PSH Flag Count', 'ACK Flag Count', 'URG Flag Count', 'CWE Flag Count', 'ECE Flag Count', 'Down/Up Ratio', 'Average Packet Size', 'Avg Fwd Segment Size', 'Avg Bwd Segment Size', 'Fwd Avg Bytes/Bulk', 'Fwd Avg Packets/Bulk', 'Fwd Avg Bulk Rate', 'Bwd Avg Bytes/Bulk', 'Bwd Avg Packets/Bulk','Bwd Avg Bulk Rate','Subflow Fwd Packets', 'Subflow Fwd Bytes', 'Subflow Bwd Packets', 'Subflow Bwd Bytes','Init_Win_bytes_forward', 'Init_Win_bytes_backward', 'act_data_pkt_fwd', 'min_seg_size_forward','Active Mean', 'Active Std', 'Active Max', 'Active Min','Idle Mean', 'Idle Std', 'Idle Max', 'Idle Min', 'Label'])

#i=0
#print(final_DF.shape)

#print(final_DF)
#csv_DF1 = csv_DF1[columns].T.drop_duplicates().T
#csv_DF1 = csv_DF1.T.drop_duplicates().T

#print(csv_DF1.shape)
#print(csv_DF1.head(3))

path_str = ""


for file in files:
    if not os.path.isdir(file):
        path_str = path + file
        #input.append(path_str)
        csv_DF1 = pd.read_csv(path_str, encoding="utf-8", dtype=str)

        csv_DF1 = csv_DF1.T             #revert dataframe
        csv_DF1 = csv_DF1.reset_index(drop=True)    #reset the index
        csv_DF1 = csv_DF1.drop(index=[61])          #drop the duplicate column
        csv_DF1 = csv_DF1.T                         #revert back to the original dataframe
        #rename column name of dataframe
        csv_DF1 = csv_DF1.rename(columns={0:'Flow ID', 1:'Source IP', 2:'Source Port', 3:'Destination IP', 4:'Destination Port', 5:'Protocol', 6:'Timestamp', 7:'Flow Duration', 8:'Total Fwd Packets', 9:'Total Backward Packets',10:'Total Length of Fwd Packets', 11:'Total Length of Bwd Packets', 12:'Fwd Packet Length Max', 13:'Fwd Packet Length Min', 14:'Fwd Packet Length Mean', 15:'Fwd Packet Length Std',16:'Bwd Packet Length Max', 17:'Bwd Packet Length Min', 18:'Bwd Packet Length Mean', 19:'Bwd Packet Length Std',20:'Flow Bytes/s', 21:'Flow Packets/s', 22:'Flow IAT Mean', 23:'Flow IAT Std', 24:'Flow IAT Max', 25:'Flow IAT Min',26:'Fwd IAT Total', 27:'Fwd IAT Mean', 28:'Fwd IAT Std', 29:'Fwd IAT Max', 30:'Fwd IAT Min', 31:'Bwd IAT Total', 32:'Bwd IAT Mean', 33:'Bwd IAT Std', 34:'Bwd IAT Max', 35:'Bwd IAT Min',36:'Fwd PSH Flags', 37:'Bwd PSH Flags', 38:'Fwd URG Flags', 39:'Bwd URG Flags', 40:'Fwd Header Length', 41:'Bwd Header Length',42:'Fwd Packets/s', 43:'Bwd Packets/s', 44:'Min Packet Length', 45:'Max Packet Length', 46:'Packet Length Mean', 47:'Packet Length Std', 48:'Packet Length Variance', 49:'FIN Flag Count', 50:'SYN Flag Count', 51:'RST Flag Count', 52:'PSH Flag Count', 53:'ACK Flag Count', 54:'URG Flag Count', 55:'CWE Flag Count', 56:'ECE Flag Count', 57:'Down/Up Ratio', 58:'Average Packet Size', 59:'Avg Fwd Segment Size', 60:'Avg Bwd Segment Size', 62:'Fwd Avg Bytes/Bulk', 63:'Fwd Avg Packets/Bulk', 64:'Fwd Avg Bulk Rate', 65:'Bwd Avg Bytes/Bulk', 66:'Bwd Avg Packets/Bulk', 67:'Bwd Avg Bulk Rate',68:'Subflow Fwd Packets', 69:'Subflow Fwd Bytes', 70:'Subflow Bwd Packets', 71:'Subflow Bwd Bytes',72:'Init_Win_bytes_forward', 73:'Init_Win_bytes_backward', 74:'act_data_pkt_fwd', 75:'min_seg_size_forward',76:'Active Mean', 77:'Active Std', 78:'Active Max', 79:'Active Min',80:'Idle Mean', 81:'Idle Std', 82:'Idle Max', 83:'Idle Min', 84:'Label'})
        csv_DF2 = pd.concat([final_DF, csv_DF1],axis=0, ignore_index=True)
        final_DF = csv_DF2


final_DF.to_csv(output,sep=',',header=True,index=False)
