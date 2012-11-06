import unittest 


class agent(object):
    def __init__(self,key,mytype,myname = ''):
        self.key        = key
        self.rulertype     = mytype
        self.empireName = myname
        self.tax=0.0
        self.attackedTerritoryKey= 0
        self.attackingSoldiers=0.0
        self.defendingSoldiers=0.0
        self.tradedata = []
        self.acceptTrade = False
        self.myTerritory = None
        self.beneficiaries = []
        self.retributions  = []

    def trade(self):
        pass
    
    def allowTrade(self,offerer, demand, typeDemand, offer, typeOffer):
        pass
    
    def chooseTax(self):
        pass
    
    def setRetributionsAndBeneficiaries(self):
        pass
    
    def attack(self):
        pass
    
    def defend(self,attaker,soldiersAttacking):
        pass
    
    def battleOutcome(self,period,attackerID,soldiersAttack,deffenderID,soldiersDefend,youWon):
        pass
    
    def tradeOutcome(self,period,proposerID,tradeProposal,tradeCompleted):
        pass