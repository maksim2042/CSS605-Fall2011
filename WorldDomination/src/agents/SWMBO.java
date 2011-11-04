/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agents;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import risk.*;
import sim.util.Bag;

/**
 *
 * @author pehiggins
 */
public class SWMBO extends Agent {
    private BattleHistories attackHistories;
    private BattleHistories defenseHistories;

    private Bag everyone = null;
    private Bag myLords = new Bag();

    private void buildEveryone() {
        // These are global variables:
        boolean full = false;
        Bag territories = new Bag();

        // This can be inserted in any method
        if(!full){
            territories.addAll(myTerritory.getNeighbors());
            while (territories.numObjs<41){
                int index = (new Random()).nextInt(territories.numObjs);
                Bag neigh2 = ((Territory)territories.get(index)).getNeighbors();
                for (int i=0; i<neigh2.numObjs; i++){
                    if(!territories.contains(neigh2.get(i)) && !neigh2.get(i).equals(myTerritory)){
                        territories.add(neigh2.get(i));
                    }
                }
            }
            full=true;
        }
        everyone = territories;
    }

    private void buildMyLords() { // originally not used
        Territory t = null;
        Bag territories = new Bag();
        territories.addAll(everyone);
        for (int i = 0; i < territories.numObjs; i++) {
            t = (Territory)territories.get(i);
            if (t.getType() < myTerritory.getType()) {
                territories.add(t);
            }
        }
       myLords.addAll(territories);
    }

    public SWMBO(int id, int type){
        super(id, type);
        empireName = "She Who Must Be Obeyed";
        attackHistories = new BattleHistories(true);  // originally not used
        defenseHistories = new BattleHistories(false);// originally not used
    }

    @Override
    public void attack(){
        System.out.println("begin Attack");
        // Attack the weakest of my non-subordinate neighbors or superiors
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        /*
         * First get a list of the weaker territories nearby - weaker means less soldiers
         * If the defending soliders are greater than my numbers than I will have a less than
         * 50% chance of wining. (mySoldiers/(mySoldiers + thierSoldiers) = .5 when mySoldiers
         * equals thierSoldiers e.g. 100/(100 + 100) = 100/200 = 1/2
         * if there are none weaker than me then attack the weakest neighbor with
         *     reduced troops
         * Of the weaker territories, pick the 'wealthiest' as defined by the
         *     base Risk program - #soldiers + #peasants + #natural resources
         */
        Bag territories = getWeakerTerritories(getNonSubordinateNeighbors());
        double tWealth = 0;
        double qWealth = 0;
        
        if (territories == null || getBestTarget(territories)== null)
        {   //This means I am the weakest in the neighborhood
            // Just attack my weakest neighbor, but only use half my soldiers

            //System.out.println("i am the weakest");
            attackingSoldiers = myTerritory.getSoldiers() * 0.5;
            attackedTerritoryID = getWeakestTerritory(getNonSubordinateNeighbors()).getId();
        } else 
        {  // make t the best target out of the weaker territories nearby
            Territory t = getBestTarget(territories);

            // compare best but weakest target to the best (richest) target
            tWealth = t.getNatRes() + t.getSoldiers() + t.getPeasants();

            if (getNonSubordinateNeighbors() != null)
            {   // overallBest is the best target of all nearby territories
                //System.out.println("getNonSubordinateNeighbors is not null");
                Territory overallBest = getBestTarget(getNonSubordinateNeighbors());

                qWealth = overallBest.getNatRes() + overallBest.getSoldiers()
                        + overallBest.getPeasants();
                if (qWealth > tWealth)
                {
                    t = overallBest;
                }
                if (t != null)
                {   // attack with everything I have
                    attackingSoldiers = myTerritory.getSoldiers();
                    attackedTerritoryID = t.getId();
                } else
                {
                    // Pick a random territory to attack even if it isn't
                    // contiguous and let the WarProtocol handle denying my attack
                    attackingSoldiers = myTerritory.getSoldiers() * 0.1;
                    attackedTerritoryID = (int) (Math.random() * 42);
                } 
            } 
        }
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
    public void trade(){
        // An example of a random trade proposal: offer peasants in exchange of soldiers
        // Pick the id of the potential partner (without checking if it is my neighbor)
//        double partnerID = (new Random()).nextInt(42)+1;
//        trade[0] = partnerID;
//        // Choose the type of good demanded (peasants - 3)
//        double demandType = 3;
//        trade[1] = demandType;
//        // Choose the amount of demanded goods
//        double demand = 3;
//        trade[2] = demand;
//        // Choose the type of good offered in exchange (peasants - 2)
//        double offerType = 2;
//        trade[3] = offerType;
//        // Choode the amount of goods offered in exchange (random but less than my total number of peasants)
//        double offer = (new Random()).nextDouble()* myTerritory.getPeasants();
//        trade[4] = offer;

        // This procedure updated the array trade, which contains the information
        // about trade proposals of the current lord
    }

    /*
    * Should update acceptTrade.
    */
    @Override
    public void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
        // Only accept trade offers from my own territories, unless the deal is really good
        if (offerer.getType() == myTerritory.getType()) {
            acceptTrade = true; // Automatically accept my own trade requests
        } else { // counter with a lousy return offer -- as long as what they're offering
                 // is more than what they're asking for and what they're asking for is no
                 // more than 10% of what I have of that resource
            if ((typeOffer == 1 && myTerritory.getNatRes() > demand * 10 && offer > demand) ||
                (typeOffer == 2 && myTerritory.getPeasants() > demand * 10 && offer > demand) ||
                (typeOffer == 3 && myTerritory.getSoldiers() > demand * 10 && offer > demand)){
                acceptTrade = true;
            } else {
                acceptTrade = false;
            }
        }
    }

    @Override
    public void chooseTax(){
        /*
         * If I have a superior then only tax at .25 otherwise tax at .5
         * tax is the way you get natRes and peasants.  However, I don't know where the natRes
         * grows again ...
         */

        if (myTerritory.getSuperior() != null){
            tax = 0.25;
            
        } else {
            tax = 0.5;
            
        }
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    public void setRetributionsAndBeneficiaries()
    {
        /*
         * I set this to divide up 1/4 of my soldiers distributed through each subordinates
         * but then ran it for a while.  It appears that if I don't distribute
         * anything, the empire fairs better peh
         *
         * This is probably because I am giving away my soldiers to territories that could
         * 'leave' my domain taking my soliders with them.
         *
         * I can subtract soldiers from the subordinates;  Then in Imperial it should add up
         * to a total and negative number, which should add them to my soldier.s
         */

        // try to find subordinates of subordinates
        Bag subs = new Bag();
        double addToMySoldiers = 0;
        beneficiaries.clear();

        //System.out.println("Number of subordinates "+ myTerritory.getSubordinates().size());
        if (myTerritory.getSubordinates().size() >= 1)
        {
            /*
             * Distribute to all subs
             */
          //System.out.println("assign bene to subs");

          // create an array of the size of the number of my subordiantes
          retributions = new double[myTerritory.getSubordinates().size()];
          //retrieve the bag of subordiantes via the Territory methods
          subs = myTerritory.getSubordinates();
          for (int i = 0;i < myTerritory.getSubordinates().size(); i++)
          {
            beneficiaries.add(subs.get(i));
            //System.out.println("moved subordiantes to beneficiearies");
            //Lets reduce the subs soldiers by half
            Territory bing = new Territory();
            bing = (Territory)subs.get(i); // enables us to get the soldiers for this subordinate
            addToMySoldiers += bing.getSoldiers()/2;
            retributions[i] = -bing.getSoldiers()/2 ; // take away 1/2 thier soldiers

            //System.out.println("retributions soldiers = "+ retributions[i]);
            //System.out.println("soldiers added from "+ myTerritory.getId()+" to "+
           //         myTerritory.getSubordinates().get(i));
          }
        }
        //System.out.println("These are the soldiers that should be added to me "+ addToMySoldiers);
     }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    public void defend(Territory attacker, double soldiersAttacking){
      // regardless of winner or loser the defender will lose 1/4 of the troops in the battle
      // So if I am outnumbered then the less I apply to the battle the less I loose.
      // These troops never get taken away so it's better to loose few.

      if (myTerritory.getSoldiers() > soldiersAttacking) {
        defendingSoldiers = myTerritory.getSoldiers();
      } else {  // If I'm outnumbered, only throw a tenth of my soldiers at defense
          defendingSoldiers = myTerritory.getSoldiers() * 0.10;
      }

    }

    public void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)

    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    public void battleOutcome(long period, int attackerID, double soldiersAttack,
            int defenderID, double soldiersDefend, boolean youWon){
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // defenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorious, false if not)
        BattleHistory bh = new BattleHistory(period, attackerID, soldiersAttack,
            defenderID, soldiersDefend, youWon);
        if (attackerID == this.getId()) {  // this is the result of my attack on someone
            attackHistories.add(bh);
        } else {
            defenseHistories.add(bh);
        }
     }

    // Gets all territories who are neighbors or (hopefully) above me in the hierarchy, excluding subordinates
    // Essentially, this will be the set of attackable territories
    private Bag getNonSubordinateNeighbors() {
        
        /*
         * peh This creates a 'bag of territories' that are non-subordinate
         * neighbors.
         */
        Bag b = new Bag(myTerritory.getNeighbors());
        Bag s = new Bag(myTerritory.getSubordinates());
        
        for (int i = 0; i < s.size(); i++) {
            /*
             * peh
             * Added a check to see if any neighbors are already on the team
             *    t = (Territory)s.get(i);
             *    if (b.contains(s.get(i)) || t.getType() == myTerritory.getType())
             * this seemed to reduce effectiveness so it was removed
             */
            if (b.contains(s.get(i))) {
                b.remove(s.get(i));
            }
        }
//        if (! b.contains(myTerritory.getSuperior())) {
//            // Add my superior if it isn't already a neighbor
//            b.add(myTerritory.getSuperior());
//        }
        return b;
    }

    private Territory getBestTarget(Bag terri) {
        /*
         * peh this method was not originally used
         * I change the criteria of 'best' from the number of soldiers
         * to the same definition of wealth that is used by the base Risk
         * program - #soldiers + #peasants + #natural resources
         */

        Territory t = null;
        Territory best = null;
        double bestWealth = 0;
        
            for (int i = 0; i < terri.numObjs; i++)
            {
                t = (Territory)terri.get(i);
                //System.out.println(i +" t is " + t +" and "+terri.get(i));
                if (t != null) 
                {
                    double tWealth = (t.getSoldiers()+ t.getNatRes()+
                        t.getPeasants());
                    if ((!(t == null)) && (bestWealth < tWealth)) {
                        bestWealth = tWealth;
                        best = t;
                        //System.out.println("best reset");
                    }
                } else
                {
                    System.out.println("t in getBestTarget is "+ t);
                }
            }
        return best;
    }

    private Territory getStrongestTerritory(Bag territories) {
        Territory t = null;
        Territory strongest = null;
        double soldierCount = 0;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getSoldiers() > soldierCount)) {
                    soldierCount = t.getSoldiers();
                    strongest = t;
                }
            }
        }
        return strongest;
    }

    private Territory getWeakestTerritory(Bag territories) {
        Territory t = null;
        Territory weakest = null;
        double soldierCount = 1000000000;
        if (territories != null && !territories.isEmpty()) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);

                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    soldierCount = t.getSoldiers();
                    weakest = t;
                }
            }
        }
        return weakest;
    }

    private Bag getWeakerTerritories(Bag territories) {
        Territory t = null;
        Bag weakerTerritories = new Bag();
        double soldierCount = myTerritory.getSoldiers();
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    weakerTerritories.add(t);
                }
            }
        }
        return weakerTerritories;
    }

    private Territory getMostNatRes(Bag territories) {
        Territory t = null;
        Territory t2Return = null;
        double targetValue = 0;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getNatRes() > targetValue)) {
                    targetValue = t.getNatRes();
                    t2Return = t;
                }
            }
        }
        return t2Return;
    }


    /*
     * The following aren't used ... just yet.
     */

//    private Territory getMostPeasants(Bag territories) {
//        Territory t = null;
//        Territory t2Return = null;
//        double targetValue = 0;
//        if (territories != null) {
//            for (int i = 0; i < territories.numObjs; i++) {
//                t = (Territory)territories.get(i);
//                if ((!(t == null)) && (t.getPeasants() > targetValue)) {
//                    targetValue = t.getPeasants();
//                    t2Return = t;
//                }
//            }
//        }
//        return t2Return;
//    }


//    private Territory getMostPeasantGrowth(Bag territories) {
//        Territory t = null;
//        Territory t2Return = null;
//        double targetValue = 0;
//        if (territories != null) {
//            for (int i = 0; i < territories.numObjs; i++) {
//                t = (Territory)territories.get(i);
//                if ((!(t == null)) && (t.getFarmGrowth() > targetValue)) {
//                    targetValue = t.getFarmGrowth();
//                    t2Return = t;
//                }
//            }
//        }
//        return t2Return;
//    }

    // Of the territory's neighbors, returns the territory that has the largest
    // number of Natural Resources


    // Returns the territory in the passed in Bag of territories that has the
    // largest number of Natural Resources
//    private Territory getMostNatResGrowth(Bag territories) {
//        Territory t = null;
//        Territory t2Return = null;
//        double targetValue = 0;
//        if (territories != null) {
//            for (int i = 0; i < territories.numObjs; i++) {
//                t = (Territory)territories.get(i);
//                if ((!(t == null)) && (t.getFoodGrowth() > targetValue)) {
//                    targetValue = t.getFoodGrowth();
//                    t2Return = t;
//                }
//            }
//        }
//        return t2Return;
//    }



}
