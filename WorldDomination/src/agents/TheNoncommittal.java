package agents;
import java.util.Random;
import risk.*;
import sim.util.Bag;
import risk.Imperial;
import risk.HierarchiesGraph;
import risk.Territory;

import java.util.*;

import ec.util.MersenneTwisterFast;

/**
 *
 * @author Omar A. Guerrero
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class TheNoncommittal extends Agent{

    static boolean firstTurn;
    private static Territory[] theLot;

    private MersenneTwisterFast rand = new MersenneTwisterFast();

    private static int[] siegeArray;
    private static boolean resetSiegeArray;
    private static Bag siegeBag;

    //don't see a way to access this yet
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

    private void setIndisputableFlags()
    {
        Bag neighbs = myTerritory.getNeighbors();
        surroundedByFriendlies = true;
        for (Object t : neighbs)
        {
            if (((Territory)t).getType() != getType())
                surroundedByFriendlies = false;
        }
        underling = false;
        if (myTerritory.getSubordinates().isEmpty() && myTerritory.getSuperior() != null)
            underling = true;
    }

    public TheNoncommittal(int id, int type){
        super(id, type);
        empireName = "The Noncommittal";

        firstTurn = true;
        ticks = 0;

        resetSiegeArray = true;

        urgentTrade = -1;

        eye = new EncyclopediaRisk();

        siegeBag = new Bag();
    }

    @Override
    public void attack(){

        //update spy info before deciding how to fight
        eye.update();

        //TODO: call flag update methods

        boolean siegeAlready = false;

        //DO I ATTACK?

        //TODO: beat this:
        attack = rand.nextBoolean();

        double troopLevelAttackThreshold = 2;


        if (eye.maxSensibleDraft(myTerritory) > troopLevelAttackThreshold)
            attack = true;

        if (attack)
        {
        //WHO DO I ATTACK?

            //this Bag represents a copy of the neighbors
            Bag canAttack = myTerritory.getNeighbors();
            for (Object a : canAttack)
                if (myTerritory.getSubordinates().contains( a )
                                || getType() == ((Territory) a).getType())
                    canAttack.remove( a );
            //now it should represent neighbors who are not my type

            if (!canAttack.isEmpty())
            {
                //TODO: beat this:
                canAttack.shuffle(rand);
                attackedTerritoryID = ( (Territory)canAttack.top() ).getId();

                //check for sieges underway
                if (!siegeBag.isEmpty())
                    for (Object a : siegeBag)
                    {
                        Territory aSuperior = ((Territory)a).getSuperior();
                        int aSupType = -1;
                        if (aSuperior != null) aSupType = aSuperior.getType();
                        if (canAttack.contains(a) && aSupType != myTerritory.getType())
                        {
                            //if it has just been attacked but not conquered by my type and is my neighbor
                            attackedTerritoryID = ((Territory)a).getId();
                            siegeAlready = true;
                            break;
                        }
                    }

                //otherwise find a possible siege
                if (!siegeAlready)
                {
                    for (Object a : canAttack)
                        if (siegeArray[((Territory)a).getId()-1] > 1)
                        {
                            siegeBag.add( a );
                            attackedTerritoryID = ((Territory)a).getId();
                            break;
                        }
                }

                //check: I'm adding stuff to siegeBag--make sure it gets taken out

                //check for coups


        //HOW MUCH?
                if (troopResource == 1)
                {
                    myTerritory.produceSoldiers(myTerritory.getNatRes(), 1);
                }
                else
                {
                    myTerritory.produceSoldiers( 1, myTerritory.getPeasants() );
                }
                attackingSoldiers = myTerritory.getSoldiers();

                //good record-keeping
                lastAttackTick = ticks;
            }

        }
        else
        {
            //do anything if defending?
            //we're past trading days so... I think not
            attackingSoldiers = 0;
        }

        //reset the flag so that first of this agent's territories called next tick to chooseTax() will reinitialize the common siegeArray
        if (!resetSiegeArray)
            resetSiegeArray = true;
    }

    /*
     * Should update the defendingSoldiers
     */
     protected void defend(Territory attacker, double soldiersAttacking){

         //set defending soldiers to something reasonable... or unreasonable

         if (soldiersAttacking > getAmount(3) / 2 && getAmount(1) > 0 && getAmount(2) > 0)
             myTerritory.produceSoldiers( getAmount(1), getAmount(2) );

         defendingSoldiers = getAmount(3);

         lastAttackedByTerritoryID = attacker.getId();
     }

     /*
     * Provides the information about the battle outcome, you can do what ever you want with it.
     */
     protected void battleOutcome(long period, int attackerID, double soldiersAttack,
             int defenderID, double soldiersDefend, boolean youWon){
         // period: step of the simulation in which the battle occured
         // attackerID: id of the territory that took the offensive position during the battle
         // soldiersAttack: number of soldiers that attacked
         // deffenderID: id of the territory that took the defensive position during the battle
         // soldiersDefend: number of soldiers that defended
         // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)

         //remove a concluded siege
         if (attackerID == myTerritory.getId() && youWon)
             for(Object a : siegeBag)
                 if (((Territory)a).getId() == defenderID)
                     siegeBag.remove( a );
         //check: this may run through some unnecessary elements, but I'm doing it to be safe

         //update statistics
         eye.addBattle(period, attackerID, soldiersAttack, defenderID, soldiersDefend, youWon);

         if (attack) attackingSoldiers = getAmount(3);
     }

     /*
     * Provides the information about the trade outcome, it is called every time a trade process is finished and you can do what ever you want with it.
     */
     protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
         // period: step of the simulation in which the trade occured
         // proposerID: id of the territory that created the trade proposal (it can be yourself)
         // tradeProposal: the actual trade proposal that was either sent or received by your lord.
         // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
         // so you can store and use that information as you wish
         // tradeCompleted: an indicator if the transaction was completed (true) or not (false)

         //update stats
         eye.addTrade(period, proposerID, tradeProposal, tradeCompleted);
     }

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
    protected void trade()
    {

    }

     /*
     * Should update acceptTrade.
     */
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
     * Should update tax.
     */
     protected void chooseTax(){

         //TODO: beat this
         tax = 0.10;

         ticks++;

         //set up static collection of Territories on the first turn
         //and other initializing business
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

         //sets up siegeArray for attack() later
         if (resetSiegeArray)
         {
             siegeArray = new int[42];
             //check: should be all zeros?, but make sure
             for (int i = 0; i < 42; i++)
                 siegeArray[i] = 0;
             resetSiegeArray = false;
         }
         for (Object a : myTerritory.getNeighbors())
             siegeArray[((Territory)a).getId() - 1]++;
     }

     /*
     * Should update the beneficiaries bag and the retributions array.
     */
     protected void setRetributionsAndBeneficiaries(){

     }


     /*
      * This class keeps track of territory statistics. It is one somewhat-seeing eye.
      */
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
             for (Territory a: everyone) rollCall[a.getId()-1] = true;
                 for (int i = 0; i < 42; i++)
                     if (!rollCall[i]) System.out.println("Missed one!");
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
                 //System.out.println("creating");
             }

             //look at this territory and update the data stored
             void update()
             {
                 double tsoldiers = theLot[id].getSoldiers();
                 if (tsoldiers > maxObservedArmy)
                     maxObservedArmy = tsoldiers;
                 averageObservedArmy = (averageObservedArmy * ticks + tsoldiers) / ticks;
                 //rounding error shouldn't be too big a deal, I hope
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
