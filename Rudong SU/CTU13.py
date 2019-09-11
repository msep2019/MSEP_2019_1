import pandas as pd
import numpy as np

data = pd.read_csv('1_newwww.csv')


# check null values
data.isnull().sum().sort_values(ascending=False)

# print(data.info())

# drop time column
df.drop(['StartTime'],inplace=True, axis=1)

# replace string with int
df['Proto'].replace('udp', '1', inplace = True)
df['Proto'].replace('tcp', '0', inplace = True)
# ........


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

# change data type to int
data[["Proto"]] = data[["Proto"]].astype(int)

# FORCE change to float64　
data["SrcAddr"] = data["SrcAddr"].apply(pd.to_numeric, errors='coerce')

# remove . in SrcAddr
data['SrcAddr'] = data['SrcAddr'].str.replace('.', '')

# remove . in DstAddr
data['DstAddr'] = data['DstAddr'].str.replace('.', '')

# drop State column
data.drop(['State'],inplace=True, axis=1)

# fill na
data.Sport= data.Sport.fillna('0')

# to check values on column
print(dict(data['Proto'].value_counts()))

print(data['DstAddr'])

print(data)


data.to_csv(r'pre_CTU1_final.csv')
