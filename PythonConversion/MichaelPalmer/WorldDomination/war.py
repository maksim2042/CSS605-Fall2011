import random as r
import unittest
from place import *
from ruler import *

class warprotocol(object):
    def attacksucceeded(self,attackintensity,defenseintensity):
        attacknum = attackintensity / (attackintensity + defenseintensity * 1.0)
        if (r.uniform(0,1.0)>= attacknum): return False
        return True
        
    def canattack(self,attacker,attackIntensity,attackedTerritoryID,numterritories,defender):
        if ( attackIntensity <= 0 or 
             attackIntensity > attacker.soldiers or
             attackedTerritoryID < 0 or 
             attackedTerritoryID > numterritories - 1 or 
             attacker == defender or
            ( attacker.isAbove(defender)==True or
              defender not in attacker.neighbors)) :
                return False          
        return True
    def adjustdefensiveintensity(self,intensity,defender):
        if ( intensity < 0 or intensity > defender.soldiers ):            
                return 0.0            
        return intensity
    def getSoldierModifier(self,succeeded):
        if (succeeded):
            return 4.0
        else:
            return 2.0
    def war(self,territories,rulers,period):
        
        for attacker in territories:
            attacker.ruler.attack()
            attackIntensity = attacker.ruler.attackingSoldiers
            attackedTerritoryID = attacker.ruler.attackedTerritoryKey
            defender = territories[attackedTerritoryID]
            if self.canattack(attacker,attackIntensity,attackedTerritoryID,len(territories),defender):
                    defender.ruler.defend(attacker, attackIntensity)
                    defenseIntensity = self.adjustdefensiveintensity(defender.ruler.defendingSoldiers,defender)
                    attackSucceeded = self.attacksucceeded(attackIntensity,defenseIntensity)
                    attacker.soldiers += - attackIntensity / self.getSoldierModifier(attackSucceeded)
                    defender.soldiers += - defenseIntensity/ self.getSoldierModifier(not attackSucceeded)
                    
                    attacker.ruler.battleOutcome(period, attacker.key, attackIntensity, defender.key, defenseIntensity, attackSucceeded)
                    defender.ruler.battleOutcome(period, attacker.key, attackIntensity, defender.key, defenseIntensity, not attackSucceeded)
                    
                    if ( attackSucceeded ):
                        if ( defender.superior != None):
                            defender.superior.subordinates.remove(defender)
                        if ( attacker.isAbove(defender) ):
                            attacker.superior.subordinates.remove(attacker)
                            attacker.superior = None
                        defender.superior = attacker
                        attacker.subordinates.append(defender)
                        attacker.updateNeighbors()
                        defender.updateNeighbors()
                        
                        
class testwar(unittest.TestCase):
    def runTest(self):
        runner = unittest.TextTestRunner(verbosity=2)
        suite = self.getsuite()
        result = runner.run(suite)
    def getsuite(self):
        tests = ['test_noWar','test_attackWins','test_attackLoses']
        suite = unittest.TestSuite()
        for t in tests:
            suite.addTest(testwar(t))
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
    def test_noWar(self):
        waus,eaus = self.getAustralias()

        waus.ruler.attackedTerritoryKey = -1
        eaus.ruler.attackedTerritoryKey = -1
        
        warplans = warprotocol()
        
        territories = [waus,eaus]
        rulers      = [waus.ruler,eaus.ruler]     
        
        warplans.war(territories,rulers,0)
        
        self.assertEqual(waus.soldiers,0)
        self.assertEqual(eaus.soldiers,0)
    def setupAussieWar(self,waus,eaus):    
        
        waus.ruler.attackedTerritoryKey =  1
        eaus.ruler.attackedTerritoryKey = -1
        
        waus.soldiers = 100
        eaus.soldiers = 100
        
        waus.ruler.attackingSoldiers = 8
        eaus.ruler.defendingSoldiers = 8
               
    def test_attackWins(self):
        waus,eaus = self.getAustralias()
        self.setupAussieWar(waus,eaus)
        # this seed should work based on the mersenne twister algorithm standard in python 2.7
        r.seed(1)
        
        warplans = warprotocol()    
                           
        territories = [waus,eaus]
        rulers      = [waus.ruler,eaus.ruler]     
        
        warplans.war(territories,rulers,0)
        
        self.assertEqual(waus.soldiers,98)
        self.assertEqual(eaus.soldiers,96) 
        
        self.assertEqual(eaus.superior,waus)
        self.assertTrue(eaus in waus.subordinates)   
        
    def test_attackLoses(self):
        waus,eaus = self.getAustralias()
        self.setupAussieWar(waus,eaus)
        # this seed should work based on the mersenne twister algorithm standard in python 2.7
        r.seed(2)
        
        warplans = warprotocol()    
                           
        territories = [waus,eaus]
        rulers      = [waus.ruler,eaus.ruler]     
        
        warplans.war(territories,rulers,0)
        
        self.assertEqual(waus.soldiers,96)
        self.assertEqual(eaus.soldiers,98) 
        
        self.assertNotEqual(eaus.superior,waus)
        self.assertFalse(eaus in waus.subordinates)                      