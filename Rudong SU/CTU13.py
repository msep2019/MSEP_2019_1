import pandas as pd
import numpy as np

data = pd.read_csv('CTU13/1.binetflow')
print(data.isnull().sum().sort_values(ascending=False))
print(data.info())

# drop columns????
# data.drop(['StartTime'], inplace=True, axis=1)
# data.drop(['State'],inplace=True, axis=1)
# data.drop(['Dir'],inplace=True, axis=1)

# replace string to int
data['Proto'].replace('udp', '1', inplace = True)
data['Proto'].replace('tcp', '0', inplace = True)
data['Proto'].replace('icmp', '2', inplace = True)
data['Proto'].replace('rtp', '3', inplace = True)
data['Proto'].replace('rtcp', '4', inplace = True)
data['Proto'].replace('igmp', '5', inplace = True)
data['Proto'].replace('arp', '6', inplace = True)
data['Proto'].replace('ipv6-icmp', '7', inplace = True)
data['Proto'].replace('ipx/spx', '8', inplace = True)
data['Proto'].replace('udt', '8', inplace = True)
data['Proto'].replace('ipv6', '8', inplace = True)
data['Proto'].replace('pim', '8', inplace = True)
data['Proto'].replace('esp', '8', inplace = True)
data['Proto'].replace('rarp', '9', inplace = True)
data['Proto'].replace('unas', '10', inplace = True)
data['Proto'].replace('gre', '11', inplace = True)

data['Dir'].replace('  <->', '1', inplace = True)
data['Dir'].replace('   ->', '2', inplace = True)
data['Dir'].replace('  <?>', '3', inplace = True)
data['Dir'].replace('  <-', '4', inplace = True)
data['Dir'].replace('   ?>', '5', inplace = True)
data['Dir'].replace('  who', '6', inplace = True)
data['Dir'].replace('  <?', '7', inplace = True)

# remove . in SrcAddr
data['SrcAddr'] = data['SrcAddr'].str.replace('.', '')

# remove . in DstAddr
data['DstAddr'] = data['DstAddr'].str.replace('.', '')

# fill na
data.Sport= data.Sport.fillna('0')
data.Dport= data.Dport.fillna('0')
data.dTos= data.dTos.fillna('0')
data.sTos= data.sTos.fillna('0')
data.SrcAddr= data.SrcAddr.fillna('0')


# FORCE change to float64　
data["SrcAddr"] = data["SrcAddr"].apply(pd.to_numeric, errors='coerce')
data["DstAddr"] = data["DstAddr"].apply(pd.to_numeric, errors='coerce')
data["Dport"] = data["Dport"].apply(pd.to_numeric, errors='coerce')

# change data type to int
data[["Proto"]] = data[["Proto"]].astype(int)
data[["Sport"]] = data[["Proto"]].astype(int)
data[["sTos"]] = data[["sTos"]].astype(int)
data[["dTos"]] = data[["dTos"]].astype(int)
data[["Dir"]] = data[["Dir"]].astype(int)

# fill na
data.Sport= data.Sport.fillna('0')
data.Dport= data.Dport.fillna('0')
data.dTos= data.dTos.fillna('0')
data.sTos= data.sTos.fillna('0')
data.SrcAddr= data.SrcAddr.fillna('0')
data.DstAddr= data.DstAddr.fillna('0')

# to check values on column
print(dict(data['Proto'].value_counts()))

# double check

print('####################################################')
print(data.isnull().sum().sort_values(ascending=False))
print(data.dtypes)
print('####################################################')



data.to_csv(r'new_pre_ctu1.csv')

