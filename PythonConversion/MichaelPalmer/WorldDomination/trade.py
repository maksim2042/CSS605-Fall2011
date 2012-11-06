import unittest
from place import *
from ruler import *

FOOD     = 1
PEASANTS = 2
SOLDIERS = 3

PARTNERID     = 0
GOODWANTED    = 1
WANTEDAMOUNT  = 2
OFFEREDGOOD   = 3
OFFEREDAMOUNT = 4



FOODNAME     = "food"
PEASANTSNAME = "peasants"
SOLDIERSNAME = "soldiers"

TRADELOOKUP = { (FOOD,PEASANTS):(FOODNAME,PEASANTSNAME),
                (FOOD,SOLDIERS):(FOODNAME,SOLDIERSNAME),
                (PEASANTS,FOOD):(PEASANTSNAME,FOODNAME),
                (PEASANTS,SOLDIERS):(PEASANTSNAME,SOLDIERSNAME),
                (SOLDIERS,FOOD):(SOLDIERSNAME,FOODNAME),
                (SOLDIERS,PEASANTS):(SOLDIERSNAME,PEASANTSNAME)}

class tradeprotocol(object):
    def cantrade(self,tradeId,territories,trader):
        if tradeId >= 0 and tradeId < len(territories):
            if trader != territories[tradeId]:
                if ((territories[tradeId].rulerType == trader.rulerType) or (trader in territories[tradeId].neighbors)): 
                    return True
        return False
    def legaltrade(self,trader,partner,tradedata):
        if ((tradedata[WANTEDAMOUNT]>=0) and (tradedata[OFFEREDAMOUNT]>=0)):
            if tradedata[GOODWANTED] != tradedata[OFFEREDGOOD]:
                lookup = (tradedata[GOODWANTED],tradedata[OFFEREDGOOD])
                if (TRADELOOKUP.has_key(lookup)):
                    tradermethod,partnermethod = TRADELOOKUP[lookup]
                    if ((tradedata[OFFEREDAMOUNT] <= trader.__getattribute__(tradermethod)) and (tradedata[WANTEDAMOUNT]  <= partner.__getattribute__(partnermethod))):
                        return True
        return False
    def trade(self,territories,rulers,period):
        for trader in territories:
            trader.ruler.trade()
            tradedata = list(trader.ruler.tradedata)
            if self.cantrade(tradedata[PARTNERID],territories,trader):
                partner = territories[tradedata[PARTNERID]] 
                
                if self.legaltrade(trader,partner,tradedata):
                    partner.ruler.allowTrade(partner, tradedata[WANTEDAMOUNT], tradedata[GOODWANTED], tradedata[OFFEREDAMOUNT], tradedata[OFFEREDGOOD])
                    if(partner.ruler.acceptTrade):
                        lookup = (tradedata[GOODWANTED],tradedata[OFFEREDGOOD])
                        tradermethod,partnermethod = TRADELOOKUP[lookup]
                        trader.__setattr__(tradermethod,trader.__getattribute__(tradermethod)   - tradedata[OFFEREDAMOUNT])
                        trader.__setattr__(partnermethod,trader.__getattribute__(partnermethod) + tradedata[WANTEDAMOUNT])
                        partner.__setattr__(tradermethod,partner.__getattribute__(tradermethod)   + tradedata[OFFEREDAMOUNT])
                        partner.__setattr__(partnermethod,partner.__getattribute__(partnermethod) - tradedata[WANTEDAMOUNT])
                        trader.ruler.tradeOutcome(period, trader.key,  tradedata, True)
                        partner.ruler.tradeOutcome(period, trader.key, tradedata, True)
                    else:
                        trader.ruler.tradeOutcome(period, trader.key,  tradedata, False)
                        partner.ruler.tradeOutcome(period, trader.key, tradedata, False) 
                        
class testtrade(unittest.TestCase):
    def runTest(self):
        runner = unittest.TextTestRunner(verbosity=2)
        suite = self.getsuite()
        result = runner.run(suite)
    def getsuite(self):
        tests = ['test_noTrade','test_allTrades']
        suite = unittest.TestSuite()
        for t in tests:
            suite.addTest(testtrade(t))
        return suite                        
    def getAustralias(self):
        ter0 = territory(0,'Western Australia',5,6,.1) 
        ter1 = territory(1,'Eastern Australia',3,9,.2)
        ter0.physicalNeighbors.append(ter1)
        ter1.physicalNeighbors.append(ter0)
        ter0.updateNeighbors()
        ter1.updateNeighbors()
        
        agent0 = agent(0,0,'Sean Connery')
        agent1 = agent(1,1,'Roger Moore')
        
        ter0.ruler = agent0
        ter0.rulerType = agent0.rulertype 
        
        ter1.ruler = agent1
        ter1.rulerType = agent1.rulertype
        
        return (ter0,ter1)    
    def test_noTrade(self):
        waus,eaus = self.getAustralias()
        waus.ruler.tradedata.append(-1)
        eaus.ruler.tradedata.append(-1)
        
        tprotocol = tradeprotocol()
        
        territories = [waus,eaus]
        rulers      = [waus.ruler,eaus.ruler]
        tprotocol.trade(territories,rulers,0)
        self.assertEqual(waus.soldiers,0)
        self.assertEqual(eaus.soldiers,0)
        self.assertEqual(waus.food,0)
        self.assertEqual(eaus.food,0)
        self.assertEqual(waus.peasants,0)
        self.assertEqual(eaus.peasants,0)  
    def setupAussieTrading(self,waus,eaus,lookupkey,lookupvalue):
        waus.ruler.tradedata = [] 
        waus.ruler.tradedata.append(1)
        waus.ruler.tradedata.append(lookupkey[0])
        waus.ruler.tradedata.append(10)
        waus.ruler.tradedata.append(lookupkey[1])        
        waus.ruler.tradedata.append(10)
        
        eaus.ruler.acceptTrade = True
        eaus.ruler.tradedata = []
        eaus.ruler.tradedata.append(-1)
        
        waus.__setattr__(lookupvalue[0],100)
        waus.__setattr__(lookupvalue[1],0)
        eaus.__setattr__(lookupvalue[0],0)
        eaus.__setattr__(lookupvalue[1],100)
        
    def test_allTrades(self):
        waus,eaus = self.getAustralias() 
        tprotocol = tradeprotocol()       
        territories = [waus,eaus]
        rulers      = [waus.ruler,eaus.ruler]
        period      = 0        
        for item in TRADELOOKUP:
            self.setupAussieTrading(waus,eaus,item,TRADELOOKUP[item])

            tprotocol.trade(territories,rulers,period)
            period +=1

            self.assertEqual(waus.__getattribute__(TRADELOOKUP[item][0]),90) 
            self.assertEqual(waus.__getattribute__(TRADELOOKUP[item][1]),10)
            self.assertEqual(eaus.__getattribute__(TRADELOOKUP[item][0]),10)
            self.assertEqual(eaus.__getattribute__(TRADELOOKUP[item][1]),90)                                         