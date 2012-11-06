import unittest


def correctforbadinput(checkinput,resource):
    if resource == 0: return 0
    if checkinput > resource: return resource
    return checkinput


class territory(object):
    def __init__(self,key=None,name=None,peasantGrowth=None,foodGrowth=None,alpha=None):
        self.key = key
        self.name = name
        self.peasantGrowth = peasantGrowth
        self.foodGrowth    = foodGrowth
        self.alpha         = alpha
        self.peasants      = 0
        self.food          = 0
        self.soldiers      = 0

        self.physicalNeighbors = []
        self.neighbors         = []
        self.subordinates      = []
        self.superior          = None
        self.ruler             = None
        self.rulerType         = None


    def getBeneficiaries(self):
        return list(self.ruler.beneficiaries)

    def getSubordinates(self):
        return list(self.subordinates)

    def getTaxRate(self):
        return self.ruler.getTax()

    def __str__(self):
        return repr(self.key)

    def getNeighbors(self):
        return list(self.neighbors)

    def produceSoldiers(self,food,peasants):
        food     = correctforbadinput(food,self.food)
        peasants = correctforbadinput(peasants,self.peasants)

        self.soldiers = self.soldiers + (self.food**self.alpha  * self.peasants **(1 - self.alpha) )
        self.peasants -= peasants;
        self.food     -= food;

    def grow(self):
        self.food     += self.foodGrowth
        self.peasants += self.peasantGrowth

    def feedSoldiers(self):
        if self.soldiers <= self.food:
            self.food -= self.soldiers
        else:
            self.soldiers = self.food
            self.food     = 0
            
    def getHead(self):
        if self.superior != None:
            return self.superior
        else:
            return self
    
    def countDiffSubordinates(self,rulertype):
        numofsub = 0
        for sub in self.subordinates:
            if sub.rulertype != rulertype: numofsub +=1
            numofsub += sub.countDiffSubordinates(rulertype)
        return numofsub
        
    def countSameSuperiors(self,rulertype):
        samesup = 0
        if self.superior!=None:
            if self.superior.rulertype == rulertype:
                samesup += 1   
            samesup += self.superior.countSameSuperiors(rulertype)
        return samesup 
    
    def isAbove(self,territory):
        isAbove = False
        if self.superior!=None:
            if (self.superior == territory):
                isAbove = True
            else:
                isAbove = self.superior.isAbove(territory) 
        return isAbove
        
    def updateNeighbors(self):
        self.neighbors = list(self.physicalNeighbors)
        for sub in self.subordinates:
            for subneighbor in sub.neighbors:
                if ((subneighbor not in self.neighbors) and (subneighbor != self)):
                    self.neighbors.add(subneighbor)  

    def isInHierarchy(self,territory):
        isHere = False
        for sub in self.subordinates:
            if (isHere == False):
                isHere = True
                break
            else:
                isHere = sub.isInHierarchy(territory)
        return isHere


class testterritory(unittest.TestCase):
    def runTest(self):
        runner = unittest.TextTestRunner(verbosity=2)
        suite = self.getsuite()
        result = runner.run(suite)
    def getsuite(self):
        tests = ['test_basiccreate','test_growth','test_makesoldiers','test_headfunction']
        suite = unittest.TestSuite()
        for t in tests:
            suite.addTest(testterritory(t))
        return suite
    def getAustralias(self):
        ter0 = territory(0,'Western Australia',5,6,.1) 
        ter1 = territory(1,'Eastern Australia',3,9,.2)
        ter0.physicalNeighbors.append(ter1)
        ter1.physicalNeighbors.append(ter0)
        ter0.updateNeighbors()
        ter1.updateNeighbors()
        return (ter0,ter1)
    def test_basiccreate(self):
        ter = territory(0,'Western Australia',5,6,.1) 
        self.assertEqual(ter.key,0,'Key Equality Failed.')
        self.assertEqual(ter.name,'Western Australia','Name Equality Failed')
        self.assertEqual(ter.peasantGrowth,5,'Peasant Growth Equality Failed')
        self.assertEqual(ter.foodGrowth,6,'Food Growth Equality Failed')
        self.assertEqual(ter.peasants,0,'Peasants init failed')
        self.assertEqual(ter.food,0,'Food init failed')
        self.assertEqual(ter.soldiers,0,'Soldier init failed')
        self.assertEqual(str(ter),'0','String convert failed')
        self.assertEqual(ter,ter.getHead(),'Head Comparison Failed')
    def test_growth(self):
        westaustralia,eastaustralia = self.getAustralias()
        westaustralia.grow()
        self.assertEqual(westaustralia.peasants,5,'Peasant Growth Failed')
        self.assertEquals(westaustralia.food,6,'Food Growth Failed')
    def test_makesoldiers(self):
        westaustralia,eastaustralia = self.getAustralias()
        westaustralia.grow()
        westaustralia.grow()
        westaustralia.produceSoldiers(5,5)
        self.assertEqual(round(westaustralia.soldiers,3),10.184,'Produce Soldiers Failed')
    def test_headfunction(self):
        westaustralia,eastaustralia = self.getAustralias()
        eastaustralia.superior = westaustralia
        self.assertEqual(westaustralia,westaustralia.getHead())
        self.assertEqual(westaustralia,eastaustralia.getHead())
