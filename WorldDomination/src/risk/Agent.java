package risk;

import java.awt.Color;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
/*
 * This is the generic class for the agents. Your King class will be a subclass
 * of Agent. Every attribute and method here will be inherited to your king.
 * Every behavior of an agent is read by the system through the attributes, not
 * by the methods. The methods are just a form of uptading an attribute. The
 * system will call those methods in every step of the game, so that your king's
 * attributes are updated. Therefore, your task is to override the relevant
 * methods, in such way that your strategies will be reflected through the
 * periodical re-assignation of the attributes.
*/



public class Agent {
    private int id;
    private int type;
    protected String empireName = ""; //The name of your empire
    protected double tax=0;//The tax rate that you impose in case your King becomes the head of a hierarchy. It has to be in the range (0,0.5).
    protected int attackedTerritoryID;//The id of the territory that this lord will attack
    protected double attackingSoldiers;//The number of soldiers that are assigned to attack.
    protected double defendingSoldiers;//The number of soldiers that are assigned to defend.
    protected double[] trade = new double[5];//Contains the relevant information for proposing trade
    protected boolean acceptTrade = false;//Indicates if a trade proposal is accepted
    protected Color rulerColor;
    protected Territory myTerritory;//The territory object that is under control of this Lord
    protected Bag beneficiaries = new Bag();//A Bag with the territories (the objects) that will receive soldiers as part of the redistributed policy of this lord.
    protected double[] retributions = new double[1];//An array containing the amounts of soldiers to be redistributed to the chosen territories. The order of the amounts has to be the same as the one of the benefited territories in the beneficiaries Bag.


    public Agent(int id, int type){
        this.id = id;
        this.type = type;
        switch(type){
            case 1: rulerColor = Color.black;
            break;
            case 2: rulerColor = Color.red;
            break;
            case 3: rulerColor = Color.blue;
            break;
            case 4: rulerColor = Color.yellow;
            break;
            case 5: rulerColor = Color.pink;
            break;
            case 6: rulerColor = Color.green;
            break;
            case 7: rulerColor = Color.orange;
            break;
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
    protected void trade(){}

    /*
    * Should update acceptTrade.
    */
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){}

    /*
    * Should update tax.
    */
    protected void chooseTax(){}

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    protected void setRetributionsAndBeneficiaries(){}

    /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */
    protected void attack(){}

    /*
    * Should update the defendingSoldiers
    */
    protected void defend(Territory attaker, double soldiersAttacking){}

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
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
    }

    public int getType(){
        return type;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return empireName;
    }

    public double[] getTrade() {
        return trade;
    }

    public boolean isAcceptTrade() {
        return acceptTrade;
    }

    public double getTax() {
        return tax;
    }

    void setMyTerritory(Territory myTerritory) {
        this.myTerritory = myTerritory;
    }

    protected Bag getBeneficiaries() {
        return beneficiaries;
    }

    protected double[] getRetributions() {
        return retributions;
    }

}
