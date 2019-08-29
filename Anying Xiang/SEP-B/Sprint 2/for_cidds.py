import pandas as pd
import numpy as np

df = pd.read_csv('week2.csv')

header = list(df.columns.values)
print header

# label or class
new_header = ['Duration', 'Proto', 'Src IP Addr', 'Src Pt', 'Dst IP Addr', 'Dst Pt', 'Packets', 'Bytes', 'Flows', 'Flags', 'Tos', 'label']
dfFeatureSelection = df[new_header]
dfDropNaN = dfFeatureSelection.dropna()

from sklearn.preprocessing import LabelEncoder
category1 = LabelEncoder()
category2 = LabelEncoder()
# category3 = LabelEncoder()
dfDropNaN['Proto'] = category1.fit_transform(dfDropNaN['Proto'].values)
dfDropNaN['Flags'] = category2.fit_transform(dfDropNaN['Flags'].values)
# dfDropNaN['class'] = category3.fit_transform(dfDropNaN['class'].values)

# print list(category3.classes_)


dfDropNaN["Src IP Addr"].replace('OPENSTACK NET', '0.0.0.1', inplace = True)
dfDropNaN["Src IP Addr"].replace('DNS', '0.0.0.2', inplace = True)
dfDropNaN["Src IP Addr"].replace('EXT SERVER', '0.0.0.3', inplace = True)
dfDropNaN["Src IP Addr"].replace('ATTACKER1', '0.0.0.4', inplace = True)
dfDropNaN["Src IP Addr"].replace('ATTACKER2', '0.0.0.4', inplace = True)
dfDropNaN["Src IP Addr"].replace('ATTACKER3', '0.0.0.4', inplace = True)

source_ip = dfDropNaN["Src IP Addr"].str.split(".", expand = True)

dfDropNaN["source_ip_1"] = source_ip[0]
dfDropNaN["source_ip_2"] = source_ip[1]
dfDropNaN["source_ip_3"] = source_ip[2]
dfDropNaN["source_ip_4"] = source_ip[3]

dfDropNaN.drop(columns =["Src IP Addr"], inplace = True)

dfDropNaN["source_ip_1"] = pd.to_numeric(dfDropNaN["source_ip_1"], errors='coerce')
dfDropNaN["source_ip_2"] = pd.to_numeric(dfDropNaN["source_ip_2"], errors='coerce')
dfDropNaN["source_ip_3"] = pd.to_numeric(dfDropNaN["source_ip_3"], errors='coerce')
dfDropNaN["source_ip_4"] = pd.to_numeric(dfDropNaN["source_ip_4"], errors='coerce')

# dfDropNaN.dropna(inplace = True)
dfDropNaN["source_ip_1"].fillna(0, inplace = True)
dfDropNaN["source_ip_2"].fillna(0, inplace = True)
dfDropNaN["source_ip_3"].fillna(0, inplace = True)
dfDropNaN["source_ip_4"].fillna(0, inplace = True)
dfDropNaN.eval('SrcIPAddr = 16777216 * source_ip_1 + 65536 * source_ip_2 + 256 * source_ip_3 + source_ip_4', inplace = True)



dfDropNaN["Dst IP Addr"].replace('OPENSTACK NET', '0.0.0.1', inplace = True)
dfDropNaN["Dst IP Addr"].replace('DNS', '0.0.0.2', inplace = True)
dfDropNaN["Dst IP Addr"].replace('EXT SERVER', '0.0.0.3', inplace = True)
dfDropNaN["Dst IP Addr"].replace('ATTACKER1', '0.0.0.4', inplace = True)
dfDropNaN["Dst IP Addr"].replace('ATTACKER2', '0.0.0.4', inplace = True)
dfDropNaN["Dst IP Addr"].replace('ATTACKER3', '0.0.0.4', inplace = True)


destination_ip = dfDropNaN["Dst IP Addr"].str.split(".", expand = True)

dfDropNaN["destination_ip_1"] = destination_ip[0]
dfDropNaN["destination_ip_2"] = destination_ip[1]
dfDropNaN["destination_ip_3"] = destination_ip[2]
dfDropNaN["destination_ip_4"] = destination_ip[3]

dfDropNaN.drop(columns =["Dst IP Addr"], inplace = True)

dfDropNaN["destination_ip_1"] = pd.to_numeric(dfDropNaN["destination_ip_1"], errors='coerce')
dfDropNaN["destination_ip_2"] = pd.to_numeric(dfDropNaN["destination_ip_2"], errors='coerce')
dfDropNaN["destination_ip_3"] = pd.to_numeric(dfDropNaN["destination_ip_3"], errors='coerce')
dfDropNaN["destination_ip_4"] = pd.to_numeric(dfDropNaN["destination_ip_4"], errors='coerce')

# dfDropNaN.dropna(inplace = True)
dfDropNaN["destination_ip_1"].fillna(0, inplace = True)
dfDropNaN["destination_ip_2"].fillna(0, inplace = True)
dfDropNaN["destination_ip_3"].fillna(0, inplace = True)
dfDropNaN["destination_ip_4"].fillna(0, inplace = True)
dfDropNaN.eval('DstIPAddr = 16777216 * destination_ip_1 + 65536 * destination_ip_2 + 256 * destination_ip_3 + destination_ip_4', inplace = True)


bytes = dfDropNaN["Bytes"].str.split(' ', expand = True)
dfDropNaN["Bytes_2"] = bytes[1]

dfDropNaN["Bytes"].replace(' M', '', inplace = True)

dfDropNaN[dfDropNaN == ''] = np.nan
dfDropNaN["Bytes_2"].fillna(1, inplace = True)
dfDropNaN["Bytes_2"].replace('M', 1024, inplace = True)
dfDropNaN["Bytes"] = pd.to_numeric(dfDropNaN["Bytes"], errors='coerce')
dfDropNaN["Bytes_2"] = pd.to_numeric(dfDropNaN["Bytes_2"], errors='coerce')


dfDropNaN.eval('Bytes_1 = Bytes * Bytes_2', inplace = True)
dfDropNaN.drop(columns =["Bytes"], inplace = True)
dfDropNaN.drop(columns =["Bytes_2"], inplace = True)

new_header2 = ['Duration', 'Proto', 'SrcIPAddr', 'Src Pt', 'DstIPAddr', 'Dst Pt', 'Packets', 'Bytes_1', 'Flows', 'Flags', 'Tos', 'label']
dfDropNaN = dfDropNaN[new_header2]

dfDropNaN.dropna(inplace = True)

new_header2 = ['Duration', 'Proto', 'SrcIPAddr', 'Src Pt', 'DstIPAddr', 'Dst Pt', 'Packets', 'Bytes_1', 'Flows', 'Flags', 'Tos']
for col in new_header2:
    dfDropNaN[col] = pd.to_numeric(dfDropNaN[col], errors = 'coerce')
    dfDropNaN[col][dfDropNaN[col] < 0] = 0
#     print col


dfDropNaN.to_csv('pre-week2', header = None, index = False)

