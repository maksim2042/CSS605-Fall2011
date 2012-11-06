from trade import tradeprotocol
from war import warprotocol
import random as r

#partial - not ready for prime time

class worlddomination(object):
    def __init__(self,mapObject,rulerlist,steps=100,seed=None):
        self.rulers      = []
        self.territories = []
        self.tradeplans  = tradeprotocol()
        self.warplans    = warprotocol()
        self.steps       = steps
        self.period      = 0
        self.mapObject   = mapObject
        self.rulerlist   = rulerlist
        r.seed(seed)
    def setup(self):
        rulerclasses = self.readRulers()
        territories  = self.mapObject.readTerritories()
        self.divideterritories(rulerclasses,territories)
        self.period  = 0
    def go(self):
        while self.period < self.steps:
            self.step()
    def step(self):
        lands = r.sample(self.territories,len(self.territories))
        
        for land in lands:
            land.ruler.chooseTax()
            self.payTax(land)
            land.ruler.setRetributionsAndBeneficiaries()
            self.redistribute(land)
            
        for land in lands:
            self.tradeplans.trade(self.territories,self.rulers,self.period)
            self.warplans.war(self.territories, self.rulers, self.period)
    def payTax(self,land):
        for sub in land.subordinates:
            self.payTax(sub)
            
        if land.superior != None:
            tax = land.superior.ruler.getTax()
            if (tax < 0 or tax>0.5) :tax = 0
            land.superior.food     += land.food     * tax
            land.superior.peasants += land.peasants * tax
            land.food     -= land.food     * tax
            land.peasants -= land.peasants * tax           