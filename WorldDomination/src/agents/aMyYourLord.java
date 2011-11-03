package agents;
import java.util.Random;
import risk.*;

/**
 *
 * @author Omar A. Guerrero
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class aMyYourLord extends Agent{

    public aMyYourLord(int id, int type){
        super(id, type);
        empireName = "MyYourEmpire";
    }


     @Override
    @SuppressWarnings("empty-statement")
    public void attack(){
        // An example of a random attack

    if (myTerritory.getAlpha() <= 0.5){
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getSoldiers()/4;
        defendingSoldiers = myTerritory.getSoldiers()*3/4;
        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
    }
    else
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getFoodGrowth()/4;
        defendingSoldiers = myTerritory.getFoodGrowth()*3/4;
        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
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
    protected void trade(){
        // An example of a random trade proposal: offer peasants in exchange of soldiers
        // Pick the id of the potential partner (without checking if it is my neighbor)
        double pertnerID = (new Random()).nextInt(42)+1;
        trade[0] = pertnerID;
        // Choose the type of good demanded (peasants - 3)
        double demandType = 3;
        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = Math.random();
        trade[2] = demand;
        // Choose the type of good offered in exchange (peasants - 2)
        double offerType = 2;
        trade[3] = offerType;
        // Choode the amount of goods offered in exchange (random but less than my total number of peasants)
        double offer = (new Random()).nextDouble()*myTerritory.getPeasants();
        trade[4] = offer;

        // This procedure updated the array trade, which contains the information
        // about trade proposals of the current lord
    }

    /*
    * Should update acceptTrade.
    */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand, double offer, int typeOffer){
        // An example for accepting a trade proposal: I only accept offers of peasants when I have less than 3 peasants
        // I am not checking what I am giving in exchange or how much, so you should work on that
        if (typeOffer == 3 && typeDemand == 2){
            acceptTrade = true;// no matter the amounts
        }
        else acceptTrade = false;
    }

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
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        // Example for picking the "chosen ones" to be benefited from my generous policy
        // First we empty the bag in case we don't want to keep old beneficiaries
        beneficiaries.clear();
        // Pick 3 random territories from my subordinates
        if (myTerritory.getSubordinates().numObjs>=1){
            beneficiaries.add(myTerritory.getSubordinates().get((new Random()).nextInt(1)));

            // Now assign the corresponding amounts of soldiers to each beneficiary
            // Since there are three of them, we need to re-instantiate the retributions array to one of size 3
            retributions = new double[myTerritory.getSubordinates().numObjs];
            // Lets transfer equal amounts of soldiers, which will add up half of my soldiers stock
            retributions[myTerritory.getSubordinates().numObjs-1] = (myTerritory.getSoldiers())/(myTerritory.getSubordinates().numObjs);

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
}
