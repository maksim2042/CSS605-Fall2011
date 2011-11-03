package agents;
import java.util.Random;
import java.util.*;
import risk.*;
import sim.util.Bag;
import risk.Imperial;
import risk.HierarchiesGraph;
import risk.Territory;

/**
 *
 * @author Russell Thomas
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class Calvin extends Agent{
    private boolean DEBUG = false;
    static boolean firstTurn;
    private static Territory[] theLot;
    private long ticks;
    private int troopResource;
    private int tradeResource;
    private int strongResource;
    private int weakResource;

    //int lastAttacked;    //this is really the same as attackedTerritoryID
    private long lastAttackTick;

    private int lastAttackedByTerritoryID;
    private int lastAttackedOnTick;

    private int lastTradingPartner;

    //this is a bag of territories that can attack this one
    private Bag attackableBy;

    private boolean attack;

    private boolean surroundedByFriendlies; //whether "safe"
    private boolean underling; //whether own no territories and ruled by another

    int urgentTrade;
    private EncyclopediaRisk eye;

    public Calvin(int id, int type){
        super(id, type);
        empireName = "Calvin";
        firstTurn = true;
        ticks = 0;

        // resetSiegeArray = true;

        urgentTrade = -1;

        eye = new EncyclopediaRisk();

        // siegeBag = new Bag();
    }


     @Override
    @SuppressWarnings("empty-statement")
    public void attack(){
         double attackRatio = 2.7;
         Territory randomNbor= null;

        //update spy info before deciding how to fight
        eye.update();
        Bag nbors = myTerritory.getNeighbors();  // bag for my neighbors
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        if (DEBUG){
            System.out.println("Me: " + myTerritory.getId() + " has " + myTerritory.getSoldiers() + " soldiers");
            System.out.println("My Neighbors:");
            for (int i=0; i < myTerritory.getNeighbors().size(); i++) {System.out.println("   " + myTerritory.getNeighbors().get(i) + " has " + ((Territory)myTerritory.getNeighbors().get(i)).getSoldiers() + " soldiers");}
            System.out.println("My Subordinates:");
            for (int i=0; i < myTerritory.getSubordinates().size(); i++) {System.out.println("   " + myTerritory.getSubordinates().get(i));}
        }
        int target = 0;
        if ( myTerritory.getSoldiers() > 0.0) {
            if (DEBUG) {System.out.println("Attacking with "+ myTerritory.getSoldiers() + " soldiers");}
            boolean done = false;


            int j = 0;
            while (!done) {
                int ran=new Random().nextInt(myTerritory.getNeighbors().numObjs); //random number generator
                 randomNbor=  ((Territory)nbors.get(ran)); //get random neighbor of stuff in bag
                 target = randomNbor.getId();
                if (myTerritory.getType()==randomNbor.getType() &&
                         (double)myTerritory.getSoldiers() / ((double)randomNbor.getSoldiers()+0.01) > attackRatio){
                    done = false;} else {done = true;}
                 j++;
                 if (j > nbors.size() * 2){done=true;}  // avoid infinite loops
            }
            if (DEBUG) {System.out.println("Target = " + target);}


            if ( (double)myTerritory.getSoldiers() / ((double)randomNbor.getSoldiers()+0.01) > attackRatio) {
               if (DEBUG){System.out.println("Attack! = " + target);}
             attackedTerritoryID = target;

             attackingSoldiers = myTerritory.getSoldiers();
                defendingSoldiers = 0;
             } else {
                attackedTerritoryID = 0;
                defendingSoldiers = myTerritory.getSoldiers();
                if (DEBUG){System.out.println("Insufficient advantage = No attack");}
             }
            } else {
               attackedTerritoryID = 0;
               defendingSoldiers = myTerritory.getSoldiers();
                if (DEBUG){System.out.println("No soldiers = no attack");}
            }
   /* if (myTerritory.getAlpha() <= 0.5){

        attackingSoldiers = myTerritory.getSoldiers()/4;
        defendingSoldiers = myTerritory.getSoldiers()*3/4;

    }
    else
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getFoodGrowth()/4;
        defendingSoldiers = myTerritory.getFoodGrowth()*3/4;
        */
        if (DEBUG){System.out.println("End of attack planning");}
     }


    /*
    * Should update the trade array. The positions if the array should contain information in the following order:
    * 0: ID of the potential trading partner
    * 1: Type of good demanded (1 for natural resources, 2 for peasants, and 3 for soldiers)
    * 2: Amount of the demanded good
    * 3: Type of good offered in exchange
    * 4: Amount of the offered good
    */
    
     public class TerritoryTradingComparator implements Comparator<Territory>
     {
         boolean soldier_natres = false;
         boolean soldier_peasant = false;
         boolean natres_peasant = false;
         boolean peasant_natres = false;
         boolean peasant_soldier = false;
         boolean natres_soldier = false;

         TerritoryTradingComparator(int desiredResource, int offeredResource)
         {
             if (desiredResource == 1 && offeredResource == 2)
                 natres_peasant = true;
             if (desiredResource == 1 && offeredResource == 3)
                 natres_soldier = true;
             if (desiredResource == 2 && offeredResource == 1)
                 peasant_natres = true;
             if (desiredResource == 2 && offeredResource == 3)
                 peasant_soldier = true;
             if (desiredResource == 3 && offeredResource == 1)
                 soldier_natres = true;
             if (desiredResource == 3 && offeredResource == 2)
                 soldier_peasant = true;
         }

         //returns 1 if a is greater, 0 if equal, -1 if b is greater,
         //where "greater" means more promising as a trade candidate
         public int compare(Territory a, Territory b)
         {
             double ratioA = 0, ratioB = 0;

             if(natres_peasant)
             {
                 ratioA = (a.getNatRes() - a.getPeasants()) * (1 - a.getAlpha());
                 ratioB = (b.getNatRes() - b.getPeasants()) * (1 - b.getAlpha());
             }
             if(natres_soldier)
             {
                 ratioA = (a.getNatRes() - a.getSoldiers()) * (1 - a.getAlpha());
                 ratioB = (b.getNatRes() - b.getSoldiers()) * (1 - b.getAlpha());
             }
             if(soldier_peasant)
             {
                 ratioA = (a.getSoldiers() - a.getPeasants()) * (1 - a.getAlpha());
                 ratioB = (b.getSoldiers() - b.getPeasants()) * (1 - b.getAlpha());
             }
             if(peasant_natres)
             {
                 ratioA = (a.getPeasants() - a.getNatRes()) * a.getAlpha();
                 ratioB = (b.getPeasants() - b.getNatRes()) * b.getAlpha();
             }
             if(peasant_soldier)
             {
                 ratioA = (a.getPeasants() / a.getSoldiers()) * a.getAlpha();
                 ratioB = (b.getPeasants() / b.getSoldiers()) * b.getAlpha();
             }
             if(soldier_natres)
             {
                 ratioA = (a.getSoldiers() - a.getNatRes()) * a.getAlpha();
                 ratioB = (b.getSoldiers() - b.getNatRes()) * b.getAlpha();
             }

             if (ratioA > ratioB) return 1;
             if (ratioA == ratioB) return 0;
             return -1;

         }
     }

     protected double getAmount(int resource)
     {
         if (resource == 1)
             return myTerritory.getNatRes();
         if (resource == 2)
             return myTerritory.getPeasants();
         return myTerritory.getSoldiers();
     }

     protected double getGrowth(int resource)
     {
         if (resource == 1)
             return myTerritory.getFoodGrowth();
         if (resource == 2)
             return myTerritory.getFarmGrowth();
         return eye.maxRegularDraft(myTerritory);
     }


    /*
     * Should update the trade array. The positions if the array should contain information in the following order:
     * 0: ID of the potential trading partner
     * 1: Type of good demanded (1 for natural resources, 2 for peasants, and 3 for soldiers)
     * 2: Amount of the demanded good
     * 3: Type of good offered in exchange
     * 4: Amount of the offered good
     */
    @Override
    protected void trade()
    {
        // An example of a random trade proposal: offer peasants in exchange of soldiers
        // Pick the id of the potential partner (without checking if it is my neighbor)

        double pertnerID = (myTerritory.getNeighbors().numObjs);
        trade[0] = pertnerID;
        // Choose the type of good demanded (not peasants, but soldiers - 3)
        double demandType = 3; //Soldiers
        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = 1; //Just one
        trade[2] = demand;
        // Choose the type of good offered in exchange (peasants - 2)
        double offerType = 2;
        trade[3] = offerType;
        // Choode the amount of goods offered in exchange (random but less than my total number of peasants)
        double offer = 1.1; // Testing whether others are testing for 'greater than' limits in exchange rates that work to my advantage
        trade[4] = offer;

        // This procedure updated the array trade, which contains the information
        // about trade proposals of the current lord

    }

     /*
     * Should update acceptTrade.
     */
    @Override
     protected void acceptTrade(Territory offerer, double demand, int typeDemand,
             double offer, int typeOffer){

//       accept all trades of less desirable for more desirable resource

         //this is the troop production multiplier that would come from the solicited goods
         double offerTroopValue = 0, demandTroopValue = 0, wouldBeLeft = 0; //, peasantTroopValue, natresTroopValue;
         //handle offer value
         if (typeOffer == 1)  //natres
         {
             offerTroopValue = Math.pow(offer, myTerritory.getAlpha());
         }
         else if (typeOffer == 2)  //peasants
         {
             offerTroopValue = Math.pow(offer, 1 - myTerritory.getAlpha());
         }
         else offerTroopValue = offer;  //in this case, value is offered troops
         //handle demand value
         if (typeDemand == 1)  //natres
         {
             demandTroopValue = Math.pow(demand, myTerritory.getAlpha());
             wouldBeLeft = myTerritory.getNatRes() - demand;
         }
         else if (typeDemand == 2)  //peasants
         {
             demandTroopValue = Math.pow(demand, 1 - myTerritory.getAlpha());
             wouldBeLeft = myTerritory.getPeasants() - demand;
         }
         else demandTroopValue = demand;  //in this case, value is demanded troops

         if (offerTroopValue > demandTroopValue && wouldBeLeft >= 1 && urgentTrade != typeDemand)
             acceptTrade = true;
         else acceptTrade = false;
     }

    /*
    * Should update acceptTrade.
    
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand, double offer, int typeOffer){
        // An example for accepting a trade proposal: I only accept offers of peasants when I have less than 3 peasants
        // I am not checking what I am giving in exchange or how much, so you should work on that
        if (typeOffer == 3 && typeDemand == 2){
            acceptTrade = true;// no matter the amounts
        }
        else acceptTrade = false;
    }
     * */
    

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax(){
        // Lets impose a tax rate that is inversely proportional to the number of subordinates I have:
        // That is, the more subordinates I have, the lower the tax rate.
        if(myTerritory.getSubordinates().isEmpty()){
            tax = 0;
        }
        else tax = 0.5;

        // Of course, if there are tax rates higher than .5, the system will set it to zero, so you should check on that
    
        if (firstTurn)
         {
             eye.setUpSpies();
             if (myTerritory.getAlpha() > 0.5)
                 troopResource = 1; //natres
             else troopResource = 2; //peasants
             tradeResource = (troopResource == 1) ? 2 : 1;

             if (myTerritory.getFarmGrowth() >= myTerritory.getFoodGrowth())
             {
                 strongResource = 2;
                 weakResource = 1;
             }
             else
             {
                 strongResource = 1;
                 weakResource = 2;
             }
             firstTurn = false;
         }

    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        // Example for picking the "chosen ones" to be benefited from my generous policy
        // First we empty the bag in case we don't want to keep old beneficiaries
        beneficiaries.clear();


        //if(myTerritory.getSuperior().getType()==myTerritory.getType()){

        // move ALL armies to the strongest subordinate

        if (myTerritory.getSubordinates().numObjs>=1){
            Territory strongest = new Territory();
            double strongestArmies = 0.0;
            for (int i=0; i<myTerritory.getSubordinates().numObjs;i++){
               if ( ((Territory)myTerritory.getSubordinates().get(i)).getSoldiers() > strongestArmies) {
                   strongestArmies = ((Territory)myTerritory.getSubordinates().get(i)).getSoldiers();
                   strongest = (Territory)myTerritory.getSubordinates().get(i);

               }

             }
        beneficiaries.add(strongest);

        // Now assign the corresponding amounts of soldiers to each beneficiary
        // Since there are three of them, we need to re-instantiate the retributions array to one of size 3
        retributions = new double[1];
        // move ALL soldiers
        retributions[0] = myTerritory.getSoldiers();
         if (DEBUG) {System.out.println("Moved " + myTerritory.getSoldiers() + " soldiers to " + strongest.getId());
            System.out.println("  it now has " + (myTerritory.getSoldiers()+strongestArmies) + " armies");
         }

        }

        // This procedure opdated the beneficiaries list and the retribution array, which gives to the system
        // the necessary info about the redistributive policy of the lords
    }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attacker, double soldiersAttacking){
        // Example of a defending strategy: if the attacker is my subordinate, and attacks me with
        // more soldiers than my stock, then I will surender. Otherwise, attack will all soldiers
        if(myTerritory.getSubordinates().contains(attacker) && soldiersAttacking > myTerritory.getSoldiers()){
            defendingSoldiers = 0;
        }
        else defendingSoldiers = (myTerritory.getSoldiers())*3/4;
    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // This method provides information about the outcome of the battle, so I will leave this open to you,
        // to do whatever you want to do with the info.

        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
    }

    /*
    * Provides the information about the trade outcome, you can do what ever you want with it.
    */
    @Override
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // This method provides information about the outcome of the trade, so I will leave this open to you,
        // to do whatever you want to do with the info.

        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
    }
     private class EncyclopediaRisk
     {
         Spy[] spies;

         EncyclopediaRisk()
         {
             spies = new Spy[42];
             for (int i = 0; i < 42; i++)
                 spies[i] = new Spy();
         }

         /*
          * This method searches out all territories in the game
          * and sets up spies "in" (really "for") each one to gather data.
          */
         private void setUpSpies()
         {

             //set up static collection of Territories on the first turn

             //set up a roll call to make sure get all of them
             HashSet <Territory> everyone = new HashSet <Territory>();
             boolean[] rollCall = new boolean[42];
             for (int i = 0; i < 42; i++)
             {
                 rollCall[i] = false;
             }

             //this represents fully scanned territories
             theLot = new Territory[42];

             //this represents territories that have been found
             Territory[] scanning = new Territory[42];
             scanning[myTerritory.getId() - 1] = myTerritory;

             int filled = 0;
             //as long as territories remain to be scanned, scan
             while (filled < 42)
             {
                 filled = 0;
                 //check for unscanned territories among those found
                 for (int z = 0; z < 42; z++)
                 {
                     //if this territory has been found but hasn't been scanned,
                     //add its neighbors to scanning array and add it to the
                     //finished array (theLot)
                     if ((theLot[z] == null) && (scanning[z] != null))
                     {
                             for (Object neighborToAdd : scanning[z].getNeighbors())
                                 scanning[((Territory)neighborToAdd).getId() - 1]
                                          = (Territory)neighborToAdd;
                             theLot[z] = scanning[z];
                     }
                     //if this territory has already been scanned, add 1 to tally
                     else if ((theLot[z] == scanning[z]) && (theLot[z] != null))
                         filled++;
                 }
             }

             //add all territories to a HashSet... just for fun
             for (Territory a : theLot)
             {
                 everyone.add(a);
             }
             //and check that they're all there
             for (Territory a: everyone) {
                 rollCall[a.getId()-1] = true;
                 if (DEBUG) {System.out.println("spy ID = " + a.getId());}
             }
                 for (int i = 0; i < 42; i++){

                     if (!rollCall[i]) System.out.println("Missed one!");
                     }
         }

         void update()
         {
             for(int i = 0; i < 42; i++)
                 spies[i].update();
         }

         void addBattle(long period, int attackerID, double soldiersAttack,
                         int defenderID, double soldiersDefend, boolean youWon)
         {
             int id = (attackerID == myTerritory.getId()) ? defenderID : attackerID;
             spies[id - 1].addBattle(period, attackerID, soldiersAttack, defenderID, soldiersDefend, youWon);
         }

         void addTrade(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted)
         {
             spies[proposerID - 1].addTrade(tradeCompleted);
         }

         //this takes into account maxSoldierGrowth but also current resources
         //and owned territories
         double strength(Territory t)
         {
             //nonsense
             return 3;
         }

         double taxTolerance(Territory t)
         {
             //nonsense
             return 4;
         }

         double maxImmediateDraft(Territory t)
         {
             return Math.pow(t.getNatRes(), t.getAlpha()) * Math.pow(t.getPeasants(), 1 - t.getAlpha());
         }

         double maxSensibleDraft(Territory t)
         {
             if (t.getAlpha() > 0.5)
                 return Math.pow(t.getNatRes(), t.getAlpha()) * Math.pow((t.getPeasants() < 1) ? t.getPeasants() : 1, 1 - t.getAlpha());
             else
                 return Math.pow((t.getNatRes() < 1) ? t.getNatRes() : 1, t.getAlpha()) * Math.pow(t.getPeasants(), 1 - t.getAlpha());
         }

         double maxRegularDraft(Territory t)
         {
             return Math.pow(t.getFoodGrowth(), t.getAlpha()) * Math.pow(t.getFarmGrowth(), 1 - t.getAlpha());
         }

         //note that taxes are exacted but all new resources have a chance to be used in troop production and trade and war
         //first... it is only the stocks at end of turn that are taxed

         double maxObservedArmy(Territory t)
         {
             return spies[t.getId()-1].maxObservedArmy;
         }

         //this is the army that can be supported by natural resources, ignoring taxes
         double maxStableGarrison(Territory t)
         {
             double maxRD = maxRegularDraft(t);
             if (maxRD > t.getNatRes())
                 return t.getNatRes();
             else return maxRD;
         }


         //the following needs some more thought
         double stableArmyGivenExternalTax(Territory t)
         {
             //this one's different
             Territory bigChef = t.getSuperior();
             if (bigChef != null)
                 return t.getFoodGrowth() / bigChef.getTaxRate();
             return t.getFoodGrowth();
             //check: the latter is not an actual cap, maybe send -1
         }

         double maxStableNatResGivenExternalTax(Territory t)
         {
             Territory chieftain = t.getSuperior();
             if (chieftain != null)
                 return t.getFoodGrowth() / chieftain.getTaxRate();
             return t.getFoodGrowth();
             //check: the latter is not an actual cap, maybe send -1
         }

         double maxStablePeasantsGivenExternalTax(Territory t)
         {
             Territory honchissimo = t.getSuperior();
             if (honchissimo != null)
                 return t.getFarmGrowth() / honchissimo.getTaxRate();
             return t.getFarmGrowth();
             //check: the latter is not an actual cap, maybe send -1
         }

         private class Spy
         {
             int id;
             int tits = 0; //number of times the emperor has followed your
             //previous treatment on the following turn...
             //ie, followed suit in remaining peaceful or attacking

             int tats = 0; //actually, is this just moves in the game so far?
             //no: moves during which we have been neighbors

             //window for tits and tats? record within say 5 ticks? 2 ticks?

             ////the idea here is to divide tits by tats to get a tit for
             ////tat ratio

             double myAverageAttackForce() {return myNetAttackForce / attacksOnThem;}
             double myBiggestAttackForce = 0;
             double mySmallestAttackForce = 0;

             double theirAverageAttackForce() {return theirNetAttackForce / attacksOnMe;}
             double theirAverageAttackForceWhileSub() {return theirNetAttackForceWhileSub / attacksOnMeWhileSub;}
             double theirBiggestAttackForce = 0;
             double theirSmallestAttackForce = 0;

             int attacksOnMe = 0;
             int attacksOnMeSucceeded = 0;
             int attacksOnThem = 0;
             int attacksOnThemSucceeded = 0;
             int myTroopsKilledInTheirAttacks = 0;
             double theirTroopsKilledInTheirAttacks = 0;
             double theirNetAttackForce = 0;
             double myTroopsKilledInMyAttacks = 0;
             double theirTroopsKilledInMyAttacks = 0;
             double myNetAttackForce = 0;

             int attacksOnMeWhileSub = 0;
             int attacksOnMeSucceededWhileSub = 0;
             double sumTaxRateWhenAttackingMeWhileSub = 0;
             double sumTaxRateWhenSuccessfullyAttackingMeWhileSub = 0;
             double myTroopsKilledInTheirAttacksWhileSub = 0;
             double theirTroopsKilledInTheirAttacksWhileSub = 0;
             double theirNetAttackForceWhileSub = 0;

             double averageTaxRateWhenAttackingMeWhileSub()
             {
                 return sumTaxRateWhenAttackingMeWhileSub / attacksOnMeWhileSub;
             }

             double averageTaxRateWhenSuccessfullyAttackingMeWhileSub()
             {
                 return sumTaxRateWhenSuccessfullyAttackingMeWhileSub / attacksOnMeSucceededWhileSub;
             }

             double attackingSuccessRate()
             {
                 return attacksOnThemSucceeded / attacksOnThem;
             }

             double attackingTroopEffectiveness()
             {
                 return ((myNetAttackForce - myTroopsKilledInMyAttacks) / myNetAttackForce - 0.5) * 4;
                 //(my troops sent - my troops killed) / my troops sent
                 //if this is 3/4, 100% success rate
                 //if this is 1/2, 0% success rate
             }

             double maxObservedArmy = 0;
             double averageObservedArmy = 0;

             //boolean acceptsBadTrades;
             int badTradesAccepted = 0;
             int badTradesRejected = 0;
             //boolean acceptsEvenTrades;
             int evenTradesAccepted = 0;
             int evenTradesRejected = 0;

             //this can be a measure of trading friendliness
             int tradesInitiated = 0;
             int badTradesTheyTried = 0;


             int attacksAfterBadTradesITried = 0;

             //this is to learn whether attacks against this territory/ruler work...
             double attackThreshold = 0;

             Spy()
             {
                 //System.out.println("creating spy ");
             }

             //look at this territory and update the data stored
             void update()
             {
                 double tsoldiers = theLot[id].getSoldiers();
                 if (tsoldiers > maxObservedArmy)
                     maxObservedArmy = tsoldiers;
                 averageObservedArmy = (averageObservedArmy * ticks + tsoldiers) / ticks;
                 //rounding error shouldn't be too big a deal, I hope
                 //System.out.println("Territory ID = "+ id + "; max army = " + maxObservedArmy + ", ave army = " + averageObservedArmy );
             }

             void addTrade(boolean wentThrough)
             {
                 //TODO: handle trades, and figure out whether to store (earlier, up)
                 //more facts about the trade, like whether it's a good or bad one, etc
             }

             void addBattle(long period, int attackerID, double soldiersAttack,
                 int defenderID, double soldiersDefend, boolean youWon)
             {
                 boolean subordinate = myTerritory.getSubordinates().contains( theLot[id] );

                 //TODO: might as well keep a vector of attack times on me? maybe?

                 //TODO: deal with tits and tats
                 if (youWon)
                 {
                     if(attackerID == myTerritory.getId())
                     {
                         attacksOnThem++;
                         attacksOnThemSucceeded++;
                         myNetAttackForce += attackingSoldiers;
                         myTroopsKilledInMyAttacks += attackingSoldiers / 4;
                         theirTroopsKilledInMyAttacks += soldiersDefend / 2;
                     }
                     else
                     {
                         attacksOnMe++;
                         theirNetAttackForce += soldiersAttack;
                         if (subordinate)
                         {
                             attacksOnMeWhileSub++;
                             theirNetAttackForceWhileSub += soldiersAttack;
                             myTroopsKilledInTheirAttacksWhileSub += defendingSoldiers / 4;
                             theirTroopsKilledInTheirAttacksWhileSub += soldiersAttack / 2;
                             sumTaxRateWhenAttackingMeWhileSub += tax;
                         }
                         myTroopsKilledInTheirAttacks += defendingSoldiers / 4;
                         theirTroopsKilledInTheirAttacks += soldiersAttack / 2;
                     }
                 }
                 else //they won
                 {
                     if(attackerID == myTerritory.getId())
                     {
                         attacksOnThem++;
                         myNetAttackForce += attackingSoldiers;
                         myTroopsKilledInMyAttacks += attackingSoldiers / 2;
                         theirTroopsKilledInMyAttacks += soldiersDefend / 4;
                     }
                     else
                     {
                         attacksOnMe++;
                         attacksOnMeSucceeded++;
                         theirNetAttackForce += soldiersAttack;
                         if (subordinate)
                         {
                             attacksOnMeWhileSub++;
                             attacksOnMeSucceededWhileSub++;
                             theirNetAttackForceWhileSub += soldiersAttack;
                             myTroopsKilledInTheirAttacksWhileSub += defendingSoldiers / 2;
                             theirTroopsKilledInTheirAttacksWhileSub += soldiersAttack / 4;
                             sumTaxRateWhenSuccessfullyAttackingMeWhileSub += tax;
                         }
                         myTroopsKilledInTheirAttacks += defendingSoldiers / 2;
                         theirTroopsKilledInTheirAttacks += soldiersAttack / 4;
                     }
                 }
             }

         }
     }
}
