package agents;
import java.util.Random;

import livemore.*;
import risk.*;
import sim.util.Bag;
import org.jgap.Chromosome;
import org.jgap.FitnessFunction;


/**
 *
 * @author Omar A. Guerrero
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class Livermore extends Agent{

    private Bag battleOutcomes = new Bag();

	public Livermore(int id, int type){
        super(id, type);
        empireName = "Livermore";
    }

    @Override
    public void attack(){

    	myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());

    	AttackStrategy strategy = new AttackStrategy(myTerritory, 1.0);

        strategy.DecideStrategy();

    	attackingSoldiers = strategy.getAttackedTerr() != null ? strategy.getAttackingSoldiers()
    			: 0.0;

        attackedTerritoryID = strategy.getAttackedTerr() != null ? strategy.getAttackedTerr().getId()
        		: myTerritory.getId();
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
        /*
    	double pertnerID = (new Random()).nextInt(42)+1;
        trade[0] = pertnerID;
        // Choose the type of good demanded (soldiers - 3)
        double demandType = 3;
        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = 10;
        trade[2] = demand;
        // Choose the type of good offered in exchange (peasants - 2)
        double offerType = 2;
        trade[3] = offerType;
        // Choose the amount of goods offered in exchange (random but less than my total number of peasants)
        double offer = 0.5;
        trade[4] = offer;
		*/
        // This procedure updated the array trade, which contains the information
        // about trade proposals of the current lord
    }

    /*
    * Should update acceptTrade.
    */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
        // An example for accepting a trade proposal: I only accept offers of peasants when I have less than 3 peasants
        // I am not checking what I am giving in exchange or how much, so you should work on that
        //if (typeOffer == 3 && myTerritory.getPeasants() < 3){
        acceptTrade = false;
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
        else tax = .5;

    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        // Example for picking the "chosen ones" to be benefited from my generous policy
        // First we empty the bag in case we don't want to keep old beneficiaries
        beneficiaries.clear();

        BenefitStrategy strategy = new BenefitStrategy(myTerritory, 1.0, 1.0);
        strategy.DecideStrategy();
        if (strategy.getBenefitTerr() != null) {
        	beneficiaries.add(strategy.getBenefitTerr());
        	retributions = new double[1];
        	retributions[0] = strategy.getBenefitSoldiers();
        }
    }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attacker, double soldiersAttacking){

    	DefendStrategy strategy = new DefendStrategy(myTerritory, 1.0, attacker, soldiersAttacking);
    	strategy.DecideStrategy();
    	defendingSoldiers = strategy.getDefendingSoldiers();
    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // This method provides information about the outcome of the battle, so I will leave this open to you,
        // to do whatever you want to do with the info.

    	// Keep memory of outcomes?
    	this.battleOutcomes.add(new BattleOutcome(period, attackerID, soldiersAttack,
            deffenderID, soldiersDefend, youWon));


    }
    @Override
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){

    }
}
