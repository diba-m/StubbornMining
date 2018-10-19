# -*- coding: utf-8 -*-
"""
Created on Thu May 12 09:25:36 2016

@author: Patrick Motylinski
Simplistic Python simulation of the selfish miner strategy. 
Algorithm constructed in accordance with the strategy as 
outlined in the original paper by Eyal and Sirer (arXiv:1311.0243v5)
"""
import random
import matplotlib.pyplot as plt 

###

def main(alp, gam, Niter):
### Init ###

    HMcount = 0 # Revenue count for honest miners
    SMcount = 0 # Revenue count for selfish miners

    histolist = []   # Keep track of states
    SMchainlen = []  # Keep track of SM chain length at the time of publishing

### Loop over attemps ###
    for attempt in range(0,Niter):
        HMchain = 0    
        SMchain = 0    
        notPublish = True # variable to keep track of when the system returns to state 0
        histolist.append('0') # keeping track of frequency of system being in a particular state
## Stay in loop while not publishing chain
        while(notPublish):
            r0 = random.random()      
            if r0 > alp: #HM mine a block
                HMchain += 1
                if SMchain == 0: #no SM block, HM get revenue +1, reset (0 --> 0)
                    HMcount += 1
                    notPublish = False
                elif SMchain == 1:  # state 0'
                    histolist.append('0p')
                    r1 = random.random()
                    if r1 > alp and r1 < ((1-alp)*gam + alp): #SM and HM win +1 each, reset (0' --> 0)
                        HMcount += 1
                        SMcount += 1
                    elif r1 >= ((1-alp)*gam + alp): #HM win +2, reset (0' --> 0)
                        HMcount += 2
                    else:   #SM win +2, reset (0' --> 0)
                        SMcount += 2
                    notPublish = False
                elif SMchain == 2: #SM lead was 2, HM mine 1, SM win +2, reset (2 --> 0)
                    SMcount += SMchain
                    notPublish = False
                else: #SMchain > 2
                    if (SMchain - HMchain) > 1: #HM mine block, SM still two or more blocks ahead (k --> k-1), SM add +1 for now
                        histolist.append(str(SMchain - HMchain))    
                        SMcount += 1
                    else: #HM mine block, SM lead was two now one, SM get remaining revenue +2, reset (1 --> 0)
                        SMcount += 2
                        notPublish = False 
            else : #SM mine a block. revenue still to be determined            
                SMchain += 1
                histolist.append(str(SMchain - HMchain))
        
            if not notPublish: #Create histogram of SM chain lengths and their frequencies
                SMchainlen.append(SMchain)
            #else:
            #    return (histolist, HMcount, SMcount, SMchainlen)
    return (histolist, HMcount, SMcount, SMchainlen)
#########################################################      
### state 0': [0:1] divided as
### [(0 : alp) ; (alp : (1-alp)*gam) ; ((1-alp)*gam : 1)]
#########################################################


if __name__ == "__main__":
    alp = 1.0/3.0 # hashing power
    gam = 0.5     # gamma
    Niter = 10000  # Number of interations 
    (histolist, HMcount, SMcount, SMchainlen) = main(alp,gam,Niter)
### Test ###
    print "Total:   " + str(HMcount + SMcount)
    print "SM gain: " + str(SMcount)
    print "HM gain: " + str(HMcount)
    print "SM rel.: " + str(float(SMcount)/float(HMcount + SMcount))
    #print sorted(histolist)
    fig1 = plt.figure() 
    ax1 = fig1.add_subplot(1, 1, 1)
    n, bins, patches = ax1.hist(SMchainlen,bins=[0,1,2,3,4,5])
    ax1.set_xlabel('SM chain length')
    ax1.set_ylabel('Frequency')
    
    fig2 = plt.figure()
    ax2 = fig2.add_subplot(1, 1, 1)
    n, bins, patches = ax2.hist(SMchainlen,bins=[5,6,7,8,9,10])
    ax2.set_xlabel('SM chain length')
    ax2.set_ylabel('Frequency')
    
    fig3 = plt.figure()
    ax3 = fig3.add_subplot(1, 1, 1)
    n, bins, patches = ax3.hist(SMchainlen,bins=[10,11,12,13,14,15])
    ax3.set_xlabel('SM chain length')
    ax3.set_ylabel('Frequency')
    
    fig4 = plt.figure()
    ax4 = fig4.add_subplot(1, 1, 1)
    n, bins, patches = ax4.hist(SMchainlen,bins=[15,16,17,18,19,20])
    ax4.set_xlabel('SM chain length')
    ax4.set_ylabel('Frequency')
    
    fig5 = plt.figure()
    ax5 = fig5.add_subplot(1, 1, 1)
    n, bins, patches = ax5.hist(SMchainlen,bins=[20,21,22,23,24,25])
    ax5.set_xlabel('SM chain length')
    ax5.set_ylabel('Frequency')
    
    #fig1.hist(SMchainlen,bins=[0,1,2,3,4] )
    #plt.hist(SMchainlen,bins=[5,6,7,8,9,10])
    #plt.hist(SMchainlen,bins=[11,12,13,14,15,16,17,18,19,20])
    total = len(histolist)
    s0p   = histolist.count('0p')
    s0    = histolist.count('0')    
    s1    = histolist.count('1')
    s2    = histolist.count('2')
    s3    = histolist.count('3')
    s4    = histolist.count('4')
    s5    = histolist.count('5')
    print "---"
    print "Simulated vals"
    print "---"
    print "p0   = " + str(float(s0)/float(total))
    print "p0p  = " + str(float(s0p)/float(total))
    print "p1   = " + str(float(s1)/float(total))
    print "p2   = " + str(float(s2)/float(total))
    print "p3   = " + str(float(s3)/float(total))
    print "p4   = " + str(float(s4)/float(total))
    print "p5   = " + str(float(s5)/float(total))
    print "---"
    print "ES vals"
    print "---"
    p0 = (alp - 2*alp**2)/(alp*(2*alp**3 - 4*alp**2 + 1))
    p0p= p0 * (alp*(1-alp))
    p1 = p0*alp
    p2 = p1*(alp/(1-alp))
    p3 = p2*(alp/(1-alp))
    p4 = p3*(alp/(1-alp))
    p5 = p4*(alp/(1-alp))
    print "p0   = " + str(p0)
    print "p0p  = " + str(p0p)
    print "p1   = " + str(p1)
    print "p2   = " + str(p2)
    print "p3   = " + str(p3)
    print "p4   = " + str(p4)
    print "p5   = " + str(p5)

