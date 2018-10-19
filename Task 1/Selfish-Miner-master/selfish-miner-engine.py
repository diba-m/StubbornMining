# -*- coding: utf-8 -*-
"""
Created on Thu May 12 09:25:36 2016

@author: pmotylinski
Python implementation of the selfish miner strategy. 
Algorithm constructed in accordance with the strategy as 
outlined in the original paper of Eyal and Sirer.
"""
import random
from math import *
import numpy as np
import matplotlib.pyplot as plt 
import scipy

###

alp = 1.0/3.0
gam = 0.5

Niter = 100000
HMcount = 0
SMcount = 0

histolist = []

### Loop over scenarios ###
for scen in range(0,Niter):
    HMchain = 0    
    SMchain = 0    
    notPublish = True
    histolist.append('0')
## Stay in loop while not publishing chain
    while(notPublish):
        r0 = random.random()      
        #print "r0 = " + str(r0)
        if r0 > alp: #HM mine a block
            HMchain += 1
            if SMchain == 0: #no SM block, HM get revenue +1, reset (0 --> 0)
                HMcount += 1
                notPublish = False
            elif SMchain == 1:  # state 0'
                histolist.append('0p')
                r1 = random.random()
                if r1 > alp and r1 < ((1-alp)*gam + alp): #SM and HM win one each, reset (0' --> 0)
                    HMcount += 1
                    SMcount += 1
                elif r1 >= ((1-alp)*gam + alp): #HM win, reset (0' --> 0)
                    HMcount += 2
                else:   #SM win, reset (0' --> 0)
                    SMcount += 2
                notPublish = False
            elif SMchain == 2: #SM lead was 2, HM mine 1, SM publish, reset (2 --> 0)
                SMcount += SMchain
                notPublish = False
            else: #SMchain > 2
                if (SMchain - HMchain) > 1: #HM mine block, SM still two or more blocks ahead (k --> k-1)
                    histolist.append(str(SMchain - HMchain))    
                    SMcount += 1
                else: #HM mine block, SM lead was two now one, SM get remaining revenue, reset (1 --> 0)
                    SMcount += 2
                    notPublish = False 
        else : #SM mine a block
            SMchain += 1
            histolist.append(str(SMchain - HMchain))
####################################################            
            


### Test ###
print ("Total:   " + str(HMcount + SMcount))
print ("HM gain: " + str(HMcount))
print ("SM rel.: " + str(float(SMcount)/float(HMcount + SMcount)))
#print sorted(histolist)
total = len(histolist)
s0p   = histolist.count('0p')
s0    = histolist.count('0')    
s1    = histolist.count('1')
s2    = histolist.count('2')
s3    = histolist.count('3')
s4    = histolist.count('4')
print ("---")
print ("Simulated vals")
print ("---")
print ("p0   = " + str(float(s0)/float(total)))
print ("p0p  = " + str(float(s0p)/float(total)))
print ("p1   = " + str(float(s1)/float(total)))
print ("p2   = " + str(float(s2)/float(total)))
print ("p3   = " + str(float(s3)/float(total)))
print ("p4   = " + str(float(s4)/float(total)))
print ("---")
print ("ES vals")
print ("---")
p0 = (alp - 2*alp**2)/(alp*(2*alp**3 - 4*alp**2 + 1))
p0p= p0 * (alp*(1-alp))
p1 = p0*alp
p2 = p1*(alp/(1-alp))
p3 = p2*(alp/(1-alp))
p4 = p3*(alp/(1-alp))
print ("p0   = " + str(p0))
print ("p0p  = " + str(p0p))
print ("p1   = " + str(p1))
print ("p2   = " + str(p2))
print ("p3   = " + str(p3))
print ("p4   = " + str(p4))