import pandas as pd
import numpy as np

data = pd.read_csv('1_newwww.csv')

 print(data.info())

 df.drop(['StartTime'],inplace=True, axis=1)
 df['Proto'].replace('udp', '1', inplace = True)

df['Proto'].replace('tcp', '0', inplace = True)

df.to_csv(r'1-newww.csv')

data['Proto'].replace('icmp', '2', inplace = True)
data['Proto'].replace('rtp', '3', inplace = True)
data['Proto'].replace('rctp', '4', inplace = True)
data['Proto'].replace('igmp', '5', inplace = True)
data['Proto'].replace('arp', '6', inplace = True)
data['Proto'].replace('ipv6-icmp', '7', inplace = True)
data['Proto'].replace('ipx/spx', '8', inplace = True)
data['Proto'].replace('udt', '8', inplace = True)
data['Proto'].replace('ipv6', '8', inplace = True)
data['Proto'].replace('pim', '8', inplace = True)
data['Proto'].replace('esp', '8', inplace = True)


#remove . in SrcAddr
data['SrcAddr'] = data['SrcAddr'].str.replace('.', '')

#remove . in DstAddr
data['DstAddr'] = data['DstAddr'].str.replace('.', '')

drop State column
data.drop(['State'],inplace=True, axis=1)


print(dict(data['Proto'].value_counts()))

print(data['DstAddr'])

print(data)


data.to_csv(r'PRE_CTU13_1.csv')
