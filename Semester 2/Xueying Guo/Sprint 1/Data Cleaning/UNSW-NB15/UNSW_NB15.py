import pandas as pd

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

inputfileNameStr_training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW-NB15/UNSW_NB15_training-set.csv'
inputfileNameStr_testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW-NB15/UNSW_NB15_testing-set.csv'
outputfileNameStr_training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW-NB15/Output/UNSW_NB15_training.csv'
outputfileNameStr_testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/UNSW-NB15/Output/UNSW_NB15_testing.csv'
DataDF = pd.read_csv(inputfileNameStr_testing,encoding = "utf-8",dtype=str )

#id
#DataDF = DataDF.drop(['id'],axis=1)

#dur
DataDF['dur'] = DataDF['dur'].astype('float')
#print(DataDF.groupby(['dur'],as_index=False)['dur'].agg({'cnt':'count'}))

#proto
#print(DataDF.groupby(['proto'],as_index=False)['proto'].agg({'cnt':'count'}))

#service
DataDF['service'] = DataDF['service'].replace('-','none')
#print(DataDF.groupby(['service'],as_index=False)['service'].agg({'cnt':'count'}))

#state
DataDF = DataDF[DataDF.state.str.contains('-') | DataDF.state.str.contains('ACC') | DataDF.state.str.contains('CLO') | DataDF.state.str.contains('CON') | DataDF.state.str.contains('ECO') | DataDF.state.str.contains('ECR') | DataDF.state.str.contains('FIN') | DataDF.state.str.contains('INT') | DataDF.state.str.contains('MAS') | DataDF.state.str.contains('PAR') | DataDF.state.str.contains('REQ') | DataDF.state.str.contains('RST') | DataDF.state.str.contains('TST') | DataDF.state.str.contains('TXD') | DataDF.state.str.contains('URH') | DataDF.state.str.contains('URN') ]
DataDF['state'] = DataDF['state'].replace('-','NONE')
#print(DataDF.groupby(['state'],as_index=False)['state'].agg({'cnt':'count'}))

#spkts
DataDF['spkts'] = DataDF['spkts'].astype('int')
#print(DataDF.groupby(['spkts'],as_index=False)['spkts'].agg({'cnt':'count'}))

#dpkts
DataDF['dpkts'] = DataDF['dpkts'].astype('int')
#print(DataDF.groupby(['dpkts'],as_index=False)['dpkts'].agg({'cnt':'count'}))

#sbytes
DataDF['sbytes'] = DataDF['sbytes'].astype('int')
#print(DataDF.groupby(['sbytes'],as_index=False)['sbytes'].agg({'cnt':'count'}))

#dbytes
DataDF['dbytes'] = DataDF['dbytes'].astype('int')
#print(DataDF.groupby(['dbytes'],as_index=False)['dbytes'].agg({'cnt':'count'}))

#rate
DataDF['rate'] = DataDF['rate'].astype('float')
#print(DataDF.groupby(['rate'],as_index=False)['rate'].agg({'cnt':'count'}))

#sttl
DataDF['sttl'] = DataDF['sttl'].astype('int')
#print(DataDF.groupby(['sttl'],as_index=False)['sttl'].agg({'cnt':'count'}))

#dttl
DataDF['dttl'] = DataDF['dttl'].astype('int')
#print(DataDF.groupby(['dttl'],as_index=False)['dttl'].agg({'cnt':'count'}))

#sload
DataDF['sload'] = DataDF['sload'].astype('float')
#print(DataDF.groupby(['sload'],as_index=False)['sload'].agg({'cnt':'count'}))

#dload
DataDF['dload'] = DataDF['dload'].astype('float')
#print(DataDF.groupby(['dload'],as_index=False)['dload'].agg({'cnt':'count'}))

#sloss
DataDF['sloss'] = DataDF['sloss'].astype('int')
#print(DataDF.groupby(['sloss'],as_index=False)['sloss'].agg({'cnt':'count'}))

#dloss
DataDF['dloss'] = DataDF['dloss'].astype('int')
#print(DataDF.groupby(['dloss'],as_index=False)['dloss'].agg({'cnt':'count'}))

#sinpkt
DataDF['sinpkt'] = DataDF['sinpkt'].astype('float')
#print(DataDF.groupby(['sinpkt'],as_index=False)['sinpkt'].agg({'cnt':'count'}))

#dinpkt
DataDF['dinpkt'] = DataDF['dinpkt'].astype('float')
#print(DataDF.groupby(['dinpkt'],as_index=False)['dinpkt'].agg({'cnt':'count'}))

#sjit
DataDF['sjit'] = DataDF['sjit'].astype('float')
#print(DataDF.groupby(['sjit'],as_index=False)['sjit'].agg({'cnt':'count'}))

#djit
DataDF['djit'] = DataDF['djit'].astype('float')
#print(DataDF.groupby(['djit'],as_index=False)['djit'].agg({'cnt':'count'}))

#swin
DataDF['swin'] = DataDF['swin'].astype('int')
#print(DataDF.groupby(['swin'],as_index=False)['swin'].agg({'cnt':'count'}))

#stcpb
DataDF['stcpb'] = DataDF['stcpb'].astype('int')
#print(DataDF.groupby(['stcpb'],as_index=False)['stcpb'].agg({'cnt':'count'}))

#dtcpb
DataDF['dtcpb'] = DataDF['dtcpb'].astype('int')
#print(DataDF.groupby(['dtcpb'],as_index=False)['dtcpb'].agg({'cnt':'count'}))

#dwin
DataDF['dwin'] = DataDF['dwin'].astype('int')
#print(DataDF.groupby(['dwin'],as_index=False)['dwin'].agg({'cnt':'count'}))

#tcprtt
DataDF['tcprtt'] = DataDF['tcprtt'].astype('float')
#print(DataDF.groupby(['tcprtt'],as_index=False)['tcprtt'].agg({'cnt':'count'}))

#synack
DataDF['synack'] = DataDF['synack'].astype('float')
#print(DataDF.groupby(['synack'],as_index=False)['synack'].agg({'cnt':'count'}))

#ackdat
DataDF['ackdat'] = DataDF['ackdat'].astype('float')
#print(DataDF.groupby(['ackdat'],as_index=False)['ackdat'].agg({'cnt':'count'}))

#smean
DataDF['smean'] = DataDF['smean'].astype('int')
#print(DataDF.groupby(['smean'],as_index=False)['smean'].agg({'cnt':'count'}))

#dmean
DataDF['dmean'] = DataDF['dmean'].astype('int')
#print(DataDF.groupby(['dmean'],as_index=False)['dmean'].agg({'cnt':'count'}))

#trans_depth
DataDF['trans_depth'] = DataDF['trans_depth'].astype('int')
#print(DataDF.groupby(['trans_depth'],as_index=False)['trans_depth'].agg({'cnt':'count'}))

#response_body_len
DataDF['response_body_len'] = DataDF['response_body_len'].astype('int')
#print(DataDF.groupby(['response_body_len'],as_index=False)['response_body_len'].agg({'cnt':'count'}))

#ct_srv_src
DataDF['ct_srv_src'] = DataDF['ct_srv_src'].astype('int')
#print(DataDF.groupby(['ct_srv_src'],as_index=False)['ct_srv_src'].agg({'cnt':'count'}))

#ct_state_ttl
DataDF['ct_state_ttl'] = DataDF['ct_state_ttl'].astype('int')
#print(DataDF.groupby(['ct_state_ttl'],as_index=False)['ct_state_ttl'].agg({'cnt':'count'}))

#ct_dst_ltm
DataDF['ct_dst_ltm'] = DataDF['ct_dst_ltm'].astype('int')
#print(DataDF.groupby(['ct_dst_ltm'],as_index=False)['ct_dst_ltm'].agg({'cnt':'count'}))

#ct_src_dport_ltm
DataDF['ct_src_dport_ltm'] = DataDF['ct_src_dport_ltm'].astype('int')
#print(DataDF.groupby(['ct_src_dport_ltm'],as_index=False)['ct_src_dport_ltm'].agg({'cnt':'count'}))

#ct_dst_sport_ltm
DataDF['ct_dst_sport_ltm'] = DataDF['ct_dst_sport_ltm'].astype('int')
#print(DataDF.groupby(['ct_dst_sport_ltm'],as_index=False)['ct_dst_sport_ltm'].agg({'cnt':'count'}))

#ct_dst_src_ltm
DataDF['ct_dst_src_ltm'] = DataDF['ct_dst_src_ltm'].astype('int')
#print(DataDF.groupby(['ct_dst_src_ltm'],as_index=False)['ct_dst_src_ltm'].agg({'cnt':'count'}))

#is_ftp_login
#print(DataDF[DataDF['is_ftp_login'].str.contains('2')])
DataDF = DataDF[DataDF.is_ftp_login.str.contains('0') | DataDF.is_ftp_login.str.contains('1') ]
#print(DataDF.groupby(['is_ftp_login'],as_index=False)['is_ftp_login'].agg({'cnt':'count'}))

#ct_ftp_cmd
DataDF['ct_dst_src_ltm'] = DataDF['ct_dst_src_ltm'].astype('int')
#print(DataDF.groupby(['ct_dst_src_ltm'],as_index=False)['ct_dst_src_ltm'].agg({'cnt':'count'}))

#ct_flw_http_mthd
DataDF['ct_flw_http_mthd'] = DataDF['ct_flw_http_mthd'].astype('int')
#print(DataDF.groupby(['ct_flw_http_mthd'],as_index=False)['ct_flw_http_mthd'].agg({'cnt':'count'}))

#ct_src_ltm
DataDF['ct_src_ltm'] = DataDF['ct_src_ltm'].astype('int')
#print(DataDF.groupby(['ct_src_ltm'],as_index=False)['ct_src_ltm'].agg({'cnt':'count'}))

#ct_srv_dst
DataDF['ct_srv_dst'] = DataDF['ct_srv_dst'].astype('int')
#print(DataDF.groupby(['ct_srv_dst'],as_index=False)['ct_srv_dst'].agg({'cnt':'count'}))

#is_sm_ips_ports
#print(DataDF.groupby(['is_sm_ips_ports'],as_index=False)['is_sm_ips_ports'].agg({'cnt':'count'}))

#attack_cat (considering remove this field)
#print(DataDF.groupby(['attack_cat'],as_index=False)['attack_cat'].agg({'cnt':'count'}))

#label
#print(DataDF.groupby(['label'],as_index=False)['label'].agg({'cnt':'count'}))

#output
DataDF.to_csv(outputfileNameStr_testing,sep=',',header=True,index=False)
