package agents;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import risk.*;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class Kull extends Agent{
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
//            System.out.println(territories.numObjs); // just to show that the bag has been filled
        }
        everyone = territories;
    }

    private void buildMyLords() {
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

    public Kull(int id, int type){
        super(id, type);
        empireName = "Valusia";
        attackHistories = new BattleHistories(true);
        defenseHistories = new BattleHistories(false);
//        buildEveryone();
    }

    @Override
    public void attack(){
        // double newSoldiers = 0;
        // Only produce as many soldiers as I can feed
        // Math.pow(natRes, alpha) * Math.pow(peasants, 1 - alpha)
        // Attack the weakest of my non-subordinate neighbors or superiors
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        // Attack a weaker neighbor who has the most natural resources
        Bag territories = getWeakerTerritories(getNonSubordinateNeighbors());
        if (territories == null) {
            // Just attack my weakest neighbor, but only use half my soldiers because I'm weaker than this one
            attackingSoldiers = myTerritory.getSoldiers() * 0.5;
            attackedTerritoryID = getWeakestTerritory(getNonSubordinateNeighbors()).getId();
        } else { 
            Territory t = getMostNatRes(territories);
            if (t != null) {
                attackingSoldiers = myTerritory.getSoldiers();
                attackedTerritoryID = t.getId();
            } else {
                // Pick a random territory to attack even if it isn't contiguous and let the WarProtocol handle denying my attack
                attackingSoldiers = myTerritory.getSoldiers() * 0.1;
                attackedTerritoryID = (int) (Math.random() * 42);
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
        // Since I have no control over subordinates other than by taxing them,
        // I'm going to exert maximum control and tax to the max...
        // If I decide to support them in some other way, I'll do it through "retributions"
        if (everyone == null) {
            buildEveryone();
        }

        tax = 0.5;
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    public void setRetributionsAndBeneficiaries(){
        // Example for picking the "chosen ones" to be benefited from my generous policy
        // First we empty the bag in case we don't want to keep old beneficiaries
        // beneficiaries.clear();
//        // Pick 3 random territories from my subordinates
//        if (myTerritory.getSubordinates().numObjs>=3){
//            beneficiaries.add(myTerritory.getSubordinates().get((new Random()).nextInt(3)));
//
//            // Now assign the corresponding amounts of soldiers to each beneficiary
//            // Since there are three of them, we need to re-instantiate the retributions array to one of size 3
//            retributions = new double[3];
//            // Lets transfer equal amounts of soldiers, which will add up half of my soldiers stock
//            retributions[0] = (myTerritory.getSoldiers()/2)/3;
//            retributions[1] = (myTerritory.getSoldiers()/2)/3;
//            retributions[2] = (myTerritory.getSoldiers()/2)/3;
//        }

        // This procedure opdated the beneficiaries list and the retribution array, which gives to the system
        // the necessary info about the redistributive policy of the lords
    }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    public void defend(Territory attacker, double soldiersAttacking){
        if (attacker.getType() == myTerritory.getType()) {
            // Automatically surrender to myself
            defendingSoldiers = 0;
        } else {  // If I'm stronger, defend with everything
            if (myTerritory.getSoldiers() > soldiersAttacking) {
                defendingSoldiers = myTerritory.getSoldiers();
            } else {  // If I'm outnumbered, only throw a quarter of my soldiers at defense
                defendingSoldiers = myTerritory.getSoldiers() * 0.25;
            }
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
        Bag b = new Bag(myTerritory.getNeighbors());

        //b.removeAll(myTerritory.getSubordinates());
        Bag s = new Bag(myTerritory.getSubordinates());
        for (int i = 0; i < s.size(); i++) {
            if (b.contains(s.get(i))) {
                b.remove(s.get(i));
            }
        }

        if (! b.contains(myTerritory.getSuperior())) { // Add my superior if it isn't already a neighbor
            b.add(myTerritory.getSuperior());
        }
//        if (! b.contains(myTerritory.getHead())) { // Add the head of my hierarchy if it isn't already a neighbor
//            b.add(myTerritory.getSuperior());
//        }

        return b;
    }

    // Returns the head of the hierarchy
//    private Territory getHead(){
//        Territory superior = null;
//        if (superior!=null){
//            return superior.getHead();
//        }
//        else{
//            return this;
//        }
//    }

    private Territory getBestNeighborTarget() {
        return getBestTarget(myTerritory.getNeighbors());
    }

    private Territory getBestTarget(Bag territories) {
        // This identifies the best territory to target for attack
        Territory t = null;
        Territory best = null;
        double soldierCount = 1000000000;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    soldierCount = t.getSoldiers();
                    best = t;
                }
            }
        }
        return best;
    }

    private Territory getStrongestNeighbor() {
        return getStrongestTerritory(myTerritory.getNeighbors());
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

    private Territory getWeakestNeighbor() {
        return getWeakestTerritory(myTerritory.getNeighbors());
    }

    private Territory getWeakestTerritory(Bag territories) {
        Territory t = null;
        Territory weakest = null;
        double soldierCount = 1000000000;
        if (territories != null && !territories.isEmpty()) {
//            System.out.println(territories.numObjs);
            for (int i = 0; i < territories.numObjs; i++) {
//                System.out.println(i);
                t = (Territory)territories.get(i);
//                System.out.println("  " + t.getId());
                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    soldierCount = t.getSoldiers();
                    weakest = t;
                }
            }
        }
        return weakest;
    }

    private Bag getWeakerNeighbors() {
        return getWeakerTerritories(myTerritory.getNeighbors());
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

   private Territory getMostNeighborPeasants() {
        return getMostPeasants(myTerritory.getNeighbors());
    }

    private Territory getMostPeasants(Bag territories) {
        Territory t = null;
        Territory t2Return = null;
        double targetValue = 0;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getPeasants() > targetValue)) {
                    targetValue = t.getPeasants();
                    t2Return = t;
                }
            }
        }
        return t2Return;
    }

    private Territory getMostNeighborNatRes() {
        return getMostNatRes(myTerritory.getNeighbors());
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

    private Territory getMostNeighborPeasantGrowth() {
        return getMostPeasantGrowth(myTerritory.getNeighbors());
    }

    private Territory getMostPeasantGrowth(Bag territories) {
        Territory t = null;
        Territory t2Return = null;
        double targetValue = 0;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getFarmGrowth() > targetValue)) {
                    targetValue = t.getFarmGrowth();
                    t2Return = t;
                }
            }
        }
        return t2Return;
    }

    // Of the territory's neighbors, returns the territory that has the largest number of Natural Resources
    private Territory getMostNeighborNatResGrowth() {
        return getMostNatResGrowth(myTerritory.getNeighbors());
    }

    // Returns the territory in the passed in Bag of territories that has the largest number of Natural Resources
    private Territory getMostNatResGrowth(Bag territories) {
        Territory t = null;
        Territory t2Return = null;
        double targetValue = 0;
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getFoodGrowth() > targetValue)) {
                    targetValue = t.getFoodGrowth();
                    t2Return = t;
                }
            }
        }
        return t2Return;
    }



} // Kull
