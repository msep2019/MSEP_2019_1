from scapy.all import *
import pandas as pd
import time

pd.set_option('display.width',None)
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)

outputfileNameStr_training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/botnet/Output/output_train3.csv'
outputfileNameStr_testing = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/botnet/Output/output_testing1.csv'

now=time.time()
local_time = time.localtime(now)
Read_csv =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Read csv time:", Read_csv)

inputfileNameStr_training = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/botnet/Bkup_Original/ISCX_Botnet_Training.csv'
csv_DF = pd.read_csv(inputfileNameStr_training,encoding = "utf-8",dtype=str)
csv_DF = csv_DF.drop(['Time','Length','Info'],axis=1)
csv_DF = csv_DF.rename(columns={'No.':'seq_id'})

now=time.time()
local_time = time.localtime(now)
Read_pcap =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Read pcap time:", Read_pcap)

pcaps = rdpcap("/Users/joannekwok/Desktop/2019_Semester2/Project_B/Datasets/botnet/train.pcap")
#print(pcaps)
packet = pcaps[0]

first_time=packet.time
#print("show",packet.show())


#print(len(packet[Raw].load))
#print(len(packet[Padding].load))
#print(packet.getlayer('Ether').padding())

#print(packet.haslayer('ARP'))
#print(packet.haslayer('NBNS query request'))
#print(packet.haslayer('DNS'))

i=20001

now=time.time()
local_time = time.localtime(now)
load_pcap =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("load pcap time:", load_pcap)

#Create Dataframe
DataDF = pd.DataFrame(columns=['seq_id','packet_len','epoch_time','last_time','ether_dst','ether_src','ether_type','ip_version','ip_ihl','ip_tos','ip_len','ip_id','ip_flags','ip_frag','ip_ttl','ip_proto','ip_chksum','ip_src','ip_dst','tcp_sport','tcp_dport','tcp_seq','tcp_ack','tcp_dataofs','tcp_reserved','tcp_flags','tcp_window','tcp_chksum','tcp_urgptr','udp_sport','udp_dport','udp_len','udp_chksum','payload_len','padding_len','label'])

for packet in pcaps:
    seq_id=str(i)
    packet_len=len(packet)
    epoch_time=packet.time
    last_time=epoch_time-first_time
    ether_dst = packet.dst
    ether_src = packet.src
    ether_type = packet.type
    #detect IP layer
    if packet.haslayer('IP') == True:
        ip_version = packet.version
        ip_ihl = packet.ihl
        ip_tos = packet.tos
        ip_len = packet.len
        ip_id = packet.id
        ip_flags = packet.flags
        ip_frag = packet.frag
        ip_ttl = packet.ttl
        ip_proto = packet.getlayer('IP').proto
        ip_chksum = packet.chksum
        ip_src = packet.getlayer('IP').src
        ip_dst = packet.getlayer('IP').dst
    else:
        ip_version = "none"
        ip_ihl = "none"
        ip_tos = "none"
        ip_len = "none"
        ip_id = "none"
        ip_flags = "none"
        ip_frag = "none"
        ip_ttl = "none"
        ip_proto = "none"
        ip_chksum = "none"
        ip_src = "none"
        ip_dst = "none"
    # detect TCP layer
    if packet.haslayer('TCP') == True:
        tcp_sport = packet.getlayer('TCP').sport
        tcp_dport = packet.getlayer('TCP').dport
        tcp_seq = packet.getlayer('TCP').seq
        tcp_ack = packet.getlayer('TCP').ack
        tcp_dataofs = packet.getlayer('TCP').dataofs
        tcp_reserved = packet.getlayer('TCP').reserved
        tcp_flags = packet.getlayer('TCP').flags
        tcp_window = packet.getlayer('TCP').window
        tcp_chksum = packet.getlayer('TCP').chksum
        tcp_urgptr = packet.getlayer('TCP').urgptr
        tcp_options = packet.getlayer('TCP').options
    else:
        tcp_sport = "none"
        tcp_dport = "none"
        tcp_seq = "none"
        tcp_ack = "none"
        tcp_dataofs = "none"
        tcp_reserved = "none"
        tcp_flags = "none"
        tcp_window = "none"
        tcp_chksum = "none"
        tcp_urgptr = "none"
        tcp_options = "none"
    # detect UDP layer
    if packet.haslayer('UDP') == True:
        udp_sport=packet.sport
        udp_dport=packet.dport
        udp_len=packet.getlayer('UDP').len
        udp_chksum = packet.getlayer('UDP').chksum
    else:
        udp_sport = "none"
        udp_dport="none"
        udp_len="none"
        udp_chksum = "none"

    if packet.haslayer('Raw') == True:
        payload_len=len(packet[Raw].load)
    else:
        payload_len = 0

    if packet.haslayer('Padding') == True:
        padding_len=len(packet[Padding].load)
    else:
        padding_len = 0

    if ((ip_src=='192.168.2.112' and ip_dst=='131.202.243.84') or (ip_src=='192.168.5.122' and ip_dst=='198.164.30.2') or (ip_src=='192.168.2.110' and ip_dst=='192.168.5.122') or (ip_src=='192.168.4.118' and ip_dst=='192.168.5.122') or (ip_src=='192.168.2.113' and ip_dst=='192.168.5.122') or (ip_src=='192.168.1.103' and ip_dst=='192.168.5.122') or (ip_src=='192.168.4.120' and ip_dst=='192.168.5.122') or (ip_src=='192.168.2.112' and ip_dst in ( '192.168.2.110', '192.168.4.120','192.168.1.103','192.168.2.113','192.168.4.118','192.168.2.109','192.168.2.105') ) or (ip_src=='192.168.1.105' and ip_dst=='192.168.5.122') ):
        label='1'
    elif ip_src in ('147.32.84.180','147.32.84.170','147.32.84.150','147.32.84.140','147.32.84.130','147.32.84.160','10.0.2.15','192.168.106.141','192.168.106.131','172.16.253.130', '172.16.253.131', '172.16.253.129', '172.16.253.240','74.78.117.238','158.65.110.24','192.168.3.35', '192.168.3.25', '192.168.3.65', '172.29.0.116','172.29.0.109','172.16.253.132', '192.168.248.165','10.37.130.4'):
        label='1'
    else:
        label='0'

    a = pd.Series({'seq_id':seq_id,'packet_len':packet_len,'epoch_time':epoch_time,'last_time':last_time,'ether_dst':ether_dst,'ether_src':ether_src,'ether_type':ether_type,'ip_version':ip_version,'ip_ihl':ip_ihl,'ip_tos':ip_tos,'ip_len':ip_len,'ip_id':ip_id,'ip_flags':ip_flags,'ip_frag':ip_frag,'ip_ttl':ip_ttl,'ip_proto':ip_proto,'ip_chksum':ip_chksum,'ip_src':ip_src,'ip_dst':ip_dst,'tcp_sport':tcp_sport,'tcp_dport':tcp_dport,'tcp_seq':tcp_seq,'tcp_ack':tcp_ack,'tcp_dataofs':tcp_dataofs,'tcp_reserved':tcp_reserved,'tcp_flags':tcp_flags,'tcp_window':tcp_window,'tcp_chksum':tcp_chksum,'tcp_urgptr':tcp_urgptr,'udp_sport':udp_sport,'udp_dport':udp_dport,'udp_len':udp_len,'udp_chksum':udp_chksum,'payload_len':payload_len,'padding_len':padding_len,'label':label})

    DataDF = DataDF.append(a,ignore_index=True)

    i=i+1

now=time.time()
local_time = time.localtime(now)
merge =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("merge time:", merge)

final_DF = pd.merge(csv_DF,DataDF,on='seq_id')

final_DF['ip_flags'] = final_DF['ip_flags'].replace('',"-")

now=time.time()
local_time = time.localtime(now)
export_csv =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("export to csv time:", export_csv)

#print(final_DF)

final_DF.to_csv(outputfileNameStr_training,sep=',',header=False,index=False)

now=time.time()
local_time = time.localtime(now)
finish_time =  time.strftime('%Y-%m-%d %H:%M:%S',local_time)
print("Done at:", finish_time)
