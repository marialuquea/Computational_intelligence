# -*- coding: utf-8 -*-
"""
Created on Mon Apr 06 10:25:57 2020

@author: Maria
"""
###############################################
#              TXT files to CSV               #
###############################################

import pandas as pd
import scipy.stats as stats

lines = []
with open('txt_files/tournament.txt', 'r') as file:
    lines = file.readlines()

line = []
for x in lines:
    one = x.split(' ')
    toAdd = [one[1], float(one[3])]
    line.append(toAdd)
    
one = []
two = []
three = []
four = []
five = []
six = []
seven = []
eight = []
for x in line:
    if x[0] == '5':
        one.append(x[1])
    if x[0] == '7':
        two.append(x[1])
    if '10' in x[0]:
        three.append(x[1])
    if '15' in x[0]:
        four.append(x[1])
    if '20' in x[0]:
        five.append(x[1])
#    if '150' in x[0]:
#        six.append(x[1])
#    if '0.5' in x[0]:
#        seven.append(x[1])
#    if '6' in x[0]:
#        eight.append(x[1])
     
pd.DataFrame({'5':one, 
              '7':two, 
              '10':three, 
              '15':four,
              '20':five, 
#              '150':six, 
#              '0.5':seven,
#              '6.0':eight
              }).to_csv('tournament.csv', index=False)



###############################################
#             Shapiro Wilk Test               #
###############################################

def shapiroWilkTest(dataset):
   
   train = pd.read_csv(dataset)
   
   df = train[['activation']]
   
   W, P = stats.shapiro(df)
   
   print("W = ", W)
   print("p = ",P)

shapiroWilkTest('activation.csv')



###############################################
#                  T Test                     #
###############################################

def t_test(one, two):
   
   train = pd.read_csv(one)
   test = pd.read_csv(two)
   
   a = train[['activation_train']]
   b = test[['activation_test']]
   
   t, p = stats.ttest_ind(a,b)
   
   print("t = ",float(t))
   print("p = ",float(p))
   
t_test('activation_train.csv', 'activation_test.csv')



