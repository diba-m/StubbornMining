import random

class pool:
    def __init__(self, selfish_chain, honest_chain, selfish_blocks, honest_blocks, selfish_orphans, honest_orphans):
        self.selfish_chain = selfish_chain #length of selfish miners' private chain
        self.honest_chain = honest_chain  #length of the public chain
        self.selfish_blocks = selfish_blocks #number of published blocks mined by selfish miners
        self.honest_blocks = honest_blocks #number of blocks mined by honest miners
        self.selfish_orphans = selfish_orphans
        self.honest_orphans = honest_orphans


def Simulate(alpha, gamma, iteration):
    P = pool(0, 0, 0, 0, 0, 0)
    
    for i in range(1,iteration):
        if(random.random()<alpha): #selfish miners mined a block
            if P.selfish_chain>0 and P.selfish_chain == P.honest_chain: #delta = 0
                P.selfish_chain +=1 #delta = 1
                P.selfish_blocks += P.selfish_chain
                P.honest_orphans += P.honest_chain
                P.honest_chain = 0
                P.selfish_chain = 0
            else:
                P.selfish_chain +=1 #selfish miner doesn't publish??
        else: #honest miners mined a block
            if P.selfish_chain == 0:
                P.honest_chain += 1
                P.honest_blocks += P.honest_chain
                P.selfish_orphans += P.selfish_chain #seems useless
                P.honest_chain = 0
                P.selfish_chain = 0 #seems useless
            elif P.selfish_chain == P.honest_chain and random.random()<gamma:
                P.selfish_blocks += P.selfish_chain
                P.honest_orphans += P.honest_chain
                P.honest_chain = 0
                P.selfish_chain = 0
                P.honest_blocks += 1
            else:
                P.honest_chain += 1
                if P.selfish_chain == P.honest_chain+1:
                    P.selfish_blocks += P.selfish_chain
                    P.honest_orphans += P.honest_chain
                    P.honest_chain = 0
                    P.selfish_chain = 0
                if P.honest_chain>P.selfish_chain:
                    P.honest_blocks += P.honest_chain
                    P.selfish_orphans += P.selfish_chain
                    P.honest_chain = 0
                    P.selfish_chain = 0
    return P

def main():
    alpha = 0.33
    gamma = 0.3
    iteration = 10000
    P = Simulate(alpha, gamma, iteration)
    
    print("\n Iterationations | %d \n α | %f (Selfish miner hash power) \n γ | %f (Proportion of honest miners which mine on the selfish pool)" % (iteration, alpha, gamma))
    print("\n Theoretical Performance | %f \n Simulated Performance | %f" % ((alpha*(1-alpha)**2*(4*alpha+gamma*(1-2*alpha))-alpha**3)/(1-alpha*(1+(2-alpha)*alpha)), P.selfish_blocks / float(P.selfish_blocks + P.honest_blocks)))
    print("\n Selfish Blocks Mined | %d \n Honest Blocks Mined | %d \n (Selfish Blocks Mined)/(Total Blocks Mined) | %f" % (P.selfish_blocks, P.honest_blocks, P.selfish_blocks / float(P.selfish_blocks + P.honest_blocks)))
    print("\n Selfish Orphan Blocks | %d \n Honest Orphan Blocks |  %d" % (P.selfish_orphans, P.honest_orphans))
    print(" Difficulty | %d percent" % (float(P.selfish_blocks + P.honest_blocks)/iteration*100))
    
    print("profitability | %f " %(        ((1-alpha)*1/10)-((1-gamma)*(1-alpha)*(alpha**2)*(1-2*alpha)*1)/(((1+alpha*(1-alpha))*(1-2*alpha)+(alpha*(1-alpha)))*10)       )   )
    
if __name__ == "__main__":
    main()
