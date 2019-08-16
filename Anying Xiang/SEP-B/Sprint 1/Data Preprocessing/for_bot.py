import pandas as pd

for num in range(2, 7):
    df = pd.read_csv('UNSW_2018_IoT_Botnet_Dataset_' + bytes(num) + '.csv', names=['c1', 'c2', 'c3', 'c4', 'c5', 'c6', 'c7', 'c8', 'c9', 'c10', 'c11', 'c12', 'c13', 'c14', 'c15', 'c16', 'c17', 'c18', 'c19', 'c20', 'c21', 'c22', 'c23', 'c24', 'c25', 'c26', 'c27', 'c28', 'c29', 'c30', 'c31', 'c32', 'c33', 'c34', 'c35'])
    new_header = ['c1', 'c2', 'c3', 'c4', 'c5', 'c6', 'c7', 'c8', 'c9', 'c10', 'c11', 'c12', 'c13', 'c14', 'c15', 'c16', 'c19', 'c20', 'c21', 'c26', 'c27', 'c28', 'c29', 'c30', 'c31', 'c32', 'c33', 'c34', 'c35']

    dfFeatureSelection = df[new_header]
    dfDropNaN = dfFeatureSelection.dropna()

    from sklearn.preprocessing import LabelEncoder
    category1 = LabelEncoder()
    category2 = LabelEncoder()
    category3 = LabelEncoder()
    dfDropNaN['c3'] = category1.fit_transform(dfDropNaN['c3'].values)
    dfDropNaN['c4'] = category2.fit_transform(dfDropNaN['c4'].values)
    dfDropNaN['c11'] = category3.fit_transform(dfDropNaN['c11'].values)
    
    dfDropNaN.to_csv('pre-UNSW_2018_IoT_Botnet_Dataset_' + bytes(num) + '.csv', header = None, index = False)
    
    print 'finish: ' + bytes(num)