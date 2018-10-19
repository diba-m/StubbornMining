import random
class pool:
    def __init__(self, private_chain, honest_chain, stubborn_blocks, honest_blocks, stubborn_orphans, honest_orphans, unpublished_blocks):
        self.private_chain = private_chain #length of selfish miners private chain
        self.honest_chain = honest_chain  #length of the public chain
        self.stubborn_blocks = stubborn_blocks #number of published blocks mined by selfish miners
        self.honest_blocks = honest_blocks #number of blocks mined by honest miners
        self.stubborn_orphans = stubborn_orphans
        self.honest_orphans = honest_orphans
        self.unpublished_blocks = unpublished_blocks

def SimulateLeadStubborn(alpha, gamma, iteration):
    P = pool(0, 0, 0, 0, 0, 0, 0)
    state = 0

    for i in range(1,iteration):
        r = random.random()
        if state == 0:
            if r<alpha: #stubborn miner mined a block
                state = 1
                P.private_chain += 1
                P.unpublished_blocks += 1
            else:
                P.honest_blocks += 1

        elif state == 1:
            if r<alpha:
                state = 2
                P.private_chain += 1
                P.unpublished_blocks += 1
            else:
                state = -1
                P.honest_blocks += 1
                P.honest_chain += 1
                P.unpublished_blocks = 0
                P.stubborn_blocks += 1 #???
        elif state == 2:
            if r<alpha:
                state = 3
                P.private_chain += 1
                P.unpublished_blocks += 1
            else:
                state = -2
                P.unpublished_blocks -= 1
                P.honest_chain += 1
                P.stubborn_blocks += 1 #???
                P.honest_blocks += 1 #???
        elif state > 2:
            if r<alpha:
                state += 1
                P.private_chain += 1
                P.unpublished_blocks += 1
            else:
                state = -state
                P.unpublished_blocks -= 1
                P.honest_chain += 1
                P.stubborn_blocks += 1 #???
                P.honest_blocks += 1 #???
        elif state == -1: #state zero prime
                if r<alpha:
                    state = -2
                    P.unpublished_blocks += 1
                    P. private_chain += 1
                elif r<=(1-alpha)*(1-gamma):
                    state = 0
                    P.unpublished_blocks = 0
                    P.private_chain = 0
                    P.honest_chain = 0
                    P.honest_blocks += 1
                    P.stubborn_orphans += 1
                else:
                    state = 0
                    P.honest_blocks += 1
                    P.honest_orphans += 1
                    P.private_chain = 0
                    P.honest_chain = 0
                    P.unpublished_blocks = 0
        elif state == -2: #state one prime
                if r<alpha:
                        state -= 1
                        P.unpublished_blocks += 1
                        P.private_chain += 1
                elif r<=(1-alpha)*(1-gamma):
                        state = -1
                        P.honest_chain += 1
                        P.honest_blocks += 1 #???
                        P.stubborn_blocks += 1 #???
                        P.unpublished_blocks -= 1
                else:
                        state = -1
                        P.unpublished_blocks = 0
                        P.private_chain = 1
                        P.public_chain = 1
                        P.honest_blocks += 1 #???
                        P.stubborn_blocks += 1 #???
                        P.honest_orphans += 1
        elif state<-2:
                if r<alpha:
                        state -= 1
                        P.private_chain += 1
                        P.unpublished_blocks += 1
                elif r<=(1-alpha)*(1-gamma):
                        state += 1
                        P.stubborn_blocks += 1 #???
                        P.honest_blocks += 1 #???
                        P.unpublished_blocks -= 1
                        P.honest_chain += 1
                else:
                        state += 1
                        P.unpublished_blocks -= 1
                        P.honest_orphans += 1
                        P.honest_chain = 1
                        P.stubborn_blocks += 1 #???
                        P.honest_blocks += 1 #???
    return P

def main():
    alpha = 0.33
    gamma = 0.3
    iteration = 10000
    P = SimulateLeadStubborn(alpha, gamma, iteration)

    print("\n Iterationations | %d \n alpha | %f (Selfish miner hash power) \n gamma | %f (Proportion of honest miners which mine on the selfish pool)" % (iteration, alpha, gamma))
    print("\n Theoretical Performance | %f \n Simulated Performance | %f" % ((alpha*(1-alpha)**2*(4*alpha+gamma*(1-2*alpha))-alpha**3)/(1-alpha*(1+(2-alpha)*alpha)), P.stubborn_blocks / float(P.stubborn_blocks + P.honest_blocks)))
    print("\n Selfish Blocks Mined | %d \n Honest Blocks Mined | %d \n (Selfish Blocks Mined)/(Total Blocks Mined) | %f" % (P.stubborn_blocks, P.honest_blocks, P.stubborn_blocks / float(P.stubborn_blocks + P.honest_blocks)))
    print("\n Selfish Orphan Blocks | %d \n Honest Orphan Blocks |  %d" % (P.stubborn_orphans, P.honest_orphans))
    print(" Difficulty | %d percent" % (float(P.stubborn_blocks + P.honest_blocks)/iteration*100))

    print(P.stubborn_blocks)
    print(P.honest_blocks)
    print(P.private_chain)
    print(P.unpublished_blocks)
    print(P.honest_orphans)
    print(P.stubborn_orphans)



if __name__ == "__main__":
    main()
