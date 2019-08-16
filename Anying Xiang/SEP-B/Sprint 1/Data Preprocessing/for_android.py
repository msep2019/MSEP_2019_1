import pandas as pd

df = pd.read_csv('TotalFeatures-ISCXFlowMeter.csv')

header = list(df.columns.values)
print header
# df.isnull().sum()

print len(header)

new_header = ['min_fpktl', 'max_fpktl', 'sflow_fbytes', 'sflow_bbytes', 'bVarianceDataBytes', 'flow_fin', 'max_flowiat', 'Init_Win_bytes_forward', 'min_seg_size_forward', 'calss']

dfFeatureSelection = df[new_header]
# print dfFeatureSelection

dfFeatureSelection.isnull().sum()

dfDropNaN = dfFeatureSelection.dropna()
dfDropNaN.isnull().sum()

dfDropNaN.to_csv('pre-TotalFeatures-ISCXFlowMeter.csv', header = None, index = False)

import numpy as np

new_header_feature = ['min_fpktl', 'max_fpktl', 'sflow_fbytes', 'sflow_bbytes', 'bVarianceDataBytes', 'flow_fin', 'max_flowiat', 'Init_Win_bytes_forward', 'min_seg_size_forward']
cnt = 0
for col in new_header_feature:
    for row in dfDropNaN[col]:
        try:
            int(row)
        except ValueError:
            dfDropNaN.loc[cnt, col] = np.nan
            print row
            pass
        cnt+=1