import pandas as pd

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

input1 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-0e06bbfdeb4a7c4bcf7f0a6fcfc6fa38.pcap_ISCX.csv'
input2 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-06dddec91d1efd55363aaab40b3ff2aa.pcap_ISCX.csv'
input3 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-17c290bc50f76292ebb10586babb8ac0.pcap_ISCX.csv'
input4 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-1021f51f46879b802d3d40803dcd2fc5.pcap_ISCX.csv'
input5 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-028733be662b4c4b8b7f2676c2987411.pcap_ISCX.csv'

input6 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_23_2017-sc-AvForAndroid-MixedAVs-070194b27034d592f62e98887489e00b.pcap_ISCX.csv'

input7 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_26_2017-sc-AvForAndroid-MixedAVs-1d2bd3908ab93ac56ba69019791e1a63.pcap_ISCX.csv'
input8 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_26_2017-sc-AvForAndroid-MixedAVs-18f0a4edf574a68346244b0e02cf8b88.pcap_ISCX.csv'
input9 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_26_2017-sc-AvForAndroid-MixedAVs-309fecc4cb7e80023ef83fd9ba75cfbb.pcap_ISCX.csv'

input10 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AVforAndroid/06_26_2017-sc-AvForAndroid-MixedAVs-3455aff554ef42c1fc41e4bcf6acebb7.pcap_ISCX.csv'
'''
input11 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_26_2017-sc-AndroidDefender-Fortinet-199cf86600c9e7a518ac9f1795b307c7.pcap_ISCX.csv'
input12 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_26_2017-sc-AndroidDefender-Fortinet-08026e2b63ec51cb36bc6cff00c28909.pcap_ISCX.csv'
input13 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_26_2017-sc-AndroidDefender-Fortinet-11517a3faa093728a24a3b7f044bf9c2.pcap_ISCX.csv'
input14 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_26_2017-sc-AndroidDefender-Fortinet-11521e42f4ed27605dfab6aab6d7f06e.pcap_ISCX.csv'
input15 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_26_2017-sc-AndroidDefender-Fortinet-14292932679d6930f521a21de4e8bffd.pcap_ISCX.csv'

input16 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_27_2017-sc-AndroidDefender-Fortinet-1f0f79475e428c84aa26e51fef472f3b.pcap_ISCX.csv'
input17 = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/AndroidDefender/06_27_2017-sc-AndroidDefender-Fortinet-2299b0b039c1bc23ed0dc9abe0227435.pcap_ISCX.csv'

'''


output = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/CICAndMal2017/Scareware/Output/AVforAndroid.csv'


csv_DF1 = pd.read_csv(input1,encoding = "utf-8",dtype=str)
csv_DF2 = pd.read_csv(input2,encoding = "utf-8",dtype=str)
csv_DF3 = pd.read_csv(input3,encoding = "utf-8",dtype=str)
csv_DF4 = pd.read_csv(input4,encoding = "utf-8",dtype=str)
csv_DF5 = pd.read_csv(input5,encoding = "utf-8",dtype=str)
csv_DF6 = pd.read_csv(input6,encoding = "utf-8",dtype=str)
csv_DF7 = pd.read_csv(input7,encoding = "utf-8",dtype=str)
csv_DF8 = pd.read_csv(input8,encoding = "utf-8",dtype=str)
csv_DF9 = pd.read_csv(input9,encoding = "utf-8",dtype=str)

csv_DF10 = pd.read_csv(input10,encoding = "utf-8",dtype=str)
'''
csv_DF11 = pd.read_csv(input11,encoding = "utf-8",dtype=str)
csv_DF12 = pd.read_csv(input12,encoding = "utf-8",dtype=str)
csv_DF13 = pd.read_csv(input13,encoding = "utf-8",dtype=str)
csv_DF14 = pd.read_csv(input14,encoding = "utf-8",dtype=str)
csv_DF15 = pd.read_csv(input15,encoding = "utf-8",dtype=str)
csv_DF16 = pd.read_csv(input16,encoding = "utf-8",dtype=str)
csv_DF17 = pd.read_csv(input17,encoding = "utf-8",dtype=str)

'''
#print(csv_DF1.head())
#print(csv_DF1.head(100))

final_DF=pd.concat([csv_DF1,csv_DF2,csv_DF3,csv_DF4,csv_DF5,csv_DF6,csv_DF7,csv_DF8,csv_DF9,csv_DF10],axis=0,ignore_index=True)

final_DF.to_csv(output,sep=',',header=True,index=False)
