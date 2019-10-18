from numpy import *
import pandas as pd
import matplotlib.pyplot as plt
from sklearn import linear_model

rules = '/Users/joannekwok/Desktop/2019_Semester2/Project_B/spinrt_5/rules/generating_rules.csv'

rulesample = pd.read_csv(rules,encoding = "utf-8",dtype=float,header=None)

print(rulesample.shape)

samplenum=rulesample.shape[0]
featurenum=rulesample.shape[1]-1

rulesample[2]=rulesample[2].apply(lambda x: 1/x)
rulesample[3]=rulesample[3].apply(lambda x: 1/x)
rulesample[4]=rulesample[4].apply(lambda x: 1/x)

matsample=mat(rulesample)

x=matsample[:,:featurenum]
y=matsample[:,featurenum:]

y = mat(y)

n_alphas = 200
alphas = logspace(-10, -2, n_alphas)
clf = linear_model.Ridge(fit_intercept=False)
coefs = []
for t in alphas:
    clf.set_params(alpha=t)
    clf.fit(x, y)
    coefs.append(clf.coef_)

print(coefs[len(coefs)-1])
