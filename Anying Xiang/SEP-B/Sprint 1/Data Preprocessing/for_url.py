import pandas as pd

df = pd.read_csv('All.csv')

header = list(df.columns.values)
print header
# df.isnull().sum()

new_header = ['Entropy_Domain', 'argPathRatio', 'ArgUrlRatio', 'argDomanRatio', 'pathurlRatio', 'CharacterContinuityRate', 'NumberRate_FileName', 'domainUrlRatio', 'NumberRate_URL', 'pathDomainRatio', 'NumberRate_AfterPath', 'avgpathtokenlen', 'URL_Type_obf_Type']
dfFeatureSelection = df[new_header]
# print dfFeatureSelection

dfFeatureSelection.isnull().sum()

dfDropNaN = dfFeatureSelection.dropna()
dfDropNaN.isnull().sum()

dfDropNaN.to_csv('pre-All.csv', header = None, index = False)

import numpy as np

new_header_feature = ['Entropy_Domain', 'argPathRatio', 'ArgUrlRatio', 'argDomanRatio', 'pathurlRatio', 'CharacterContinuityRate', 'NumberRate_FileName', 'domainUrlRatio', 'NumberRate_URL', 'pathDomainRatio', 'NumberRate_AfterPath', 'avgpathtokenlen']

cnt = 0
for col in new_header_feature:
#     print col
    for row in dfDropNaN[col]:
        try:
            float(row)
        except ValueError:
            dfDropNaN.loc[cnt, col] = np.nan
            print 'string'
            pass
        cnt+=1