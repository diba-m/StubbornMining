import random

def Simulate(alpha,gamma,N):
    #This function simulate the selfish miners strategy.
    #It returns the proportion of blocks in the longest chain
    #which belongs to the selfish miners.
    state=0
    LongestChainLength=0
    NumberOfSelfishMineBlock=0

    #A round begins when the state=0 and finishes when we return to it
    for i in range(N):
        r=random.random()
        if state==0:
            #Initial State.
            #The selfish miners have 0 hidden block.
            if r<=alpha:
                #The selfish miners found a block.
                #They don't publish it. 
                state=1
            else:
                #The honest miners found a block.
                #The round is finished : the honest miners found 1 block
                # and the selfish miners found 0 block.
                LongestChainLength+=1
                state=0

        elif state==1:
            #There is one hidden block in the pocket of the selfish miners.
            if r<=alpha:
                #The selfish miners found a new block.
                #It remains hidden.
                #The selfish miners are now two blocks ahead.
                #The two blocks are hidden.
                state=2
                n=2
            else:
                state=-1

        elif state==-1:
            #It's the state 0' in the paper of Eyal and Gun Sirer
            #The honest miners found a block.
            #So the selfish miners publish their hidden block.
            #The blockchain is forked with one block in each fork.
            if r<=alpha:
                #the selfish miners found a block in their fork.
                #The round is finished : Selfish miners won 2 blocks and the honest miners 0.
                NumberOfSelfishMineBlock+=2
                LongestChainLength+=2
                state=0
            elif r<=alpha+(1-alpha)*gamma:
                #The honest miners found a block in the fork of the selfish miners.
                #The round is finished : Selfish miners won 1 blocks and the honest miners 1.
                NumberOfSelfishMineBlock+=1
                LongestChainLength+=2
                state=0
            else:
                #The honest miners found a block in their fork.
                #The round is finished : Selfish miners won 0 blocks and the honest miners 2.
                NumberOfSelfishMineBlock+=0
                LongestChainLength+=2
                state=0

        elif state==2:
            #The selfish miners have 2 hidden blocks in their pocket.
            if r<=alpha:
                #The selfish miners found a new hidden block
                n+=1
                state=3
            else:
                #The honest miners found a block.
                #The selfish miners are only one block ahead of the honest miners,
                #So they publish their chain which is of length n.
                #The round is finished : Selfish miners won n blocks and the honest miners 0.
                LongestChainLength+=n
                NumberOfSelfishMineBlock+=n
                state=0
        elif state>2:
            if r<=alpha:
                #The selfish miners found a new hidden block
                n+=1
                state+=1
            else:
                #The honest miners found a block
                #The selfish miners publish one of their hidden block
                # and are losing one point in the run.
                state-=1
    return float(NumberOfSelfishMineBlock)/LongestChainLength

def main():
    alpha=0.35
    gamma=0.5
    Nsimu=10**7
    print ("Theoretical probability :",(alpha*(1-alpha)**2*(4*alpha+gamma*(1-2*alpha))-alpha**3)/(1-alpha*(1+(2-alpha)*alpha)))
    print ("Simulated probability :",Simulate(alpha,gamma,Nsimu))
main()