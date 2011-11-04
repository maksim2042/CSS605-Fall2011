package agents;
import java.util.Random;
import risk.*;
import sim.util.Bag;


/**
 *
 * 
 */
public class ItsGoodToBeTheKing extends Agent {
    //TheKingsLog log;
    Bag potential_traders = new Bag();


    //accounting
    private static MilitaryHistory militaryHistory = new MilitaryHistory();
    private static CentralScrutinizerForTrade tradingAdvisor = new CentralScrutinizerForTrade();
    //temp variables
    private int lastAttacker = -1;
    /**
     *
     * @param id
     * @param type
     */
    public ItsGoodToBeTheKing(int id, int type) {
        super(id, type);

        empireName = "TisGoodToBeTheKing";
     //   log = new TheKingsLog();
    }

    /**
     *
     */
    @Override
    protected void trade(){
        findTraders();
        double peasants= myTerritory.getPeasants();
        double resources= myTerritory.getNatRes();
        double soldiers= myTerritory.getSoldiers();


        double partnerID = (new Random()).nextInt(findTraders().numObjs)+1;

        trade[0] = partnerID;

        double avgResources = (soldiers + peasants + resources) / 3.0;
        double demandType=1;
        if (peasants <1){demandType = 2;}
        else if (resources <1){demandType = 1;}
        else if (soldiers<1){demandType = 3;}
        else {
            
            if (soldiers < avgResources) {
                demandType = 3;
            } else if (peasants < avgResources) {
                demandType = 2;
            } else if (resources < avgResources) {
                demandType = 1;
            }

        }
        int offerType = 1;
        double offerAmt = Math.random() * .5 * resources;
        if (peasants > resources) {
            offerType = 2;
            offerAmt = Math.random() * .5 * soldiers;
       }
        trade[1] = demandType;

        double demand = (Math.random() * avgResources * .6) + (avgResources / 2) ;
        trade[2] = demand;

        
        trade[3] = offerType;

        double offer = Math.min(offerAmt,demand * .9);
        trade[4] = offer;
    }

    /*
    * Should update acceptTrade.
    */
    /**
     *
     * @param offerer
     * @param demand
     * @param typeDemand
     * @param offer
     * @param typeOffer
     */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer)
    {
            acceptTrade =
                    tradingAdvisor.acceptTrade(myTerritory, offerer, demand, typeDemand, offer, typeOffer);
    }

    /*
    * Should update tax.
    */
    /**
     *
     */
    @Override
    protected void chooseTax(){
        tax = Math.random() * 0.5;
    }
    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    /**
     *
     */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        beneficiaries.clear();

        if (myTerritory.getSubordinates().numObjs> 2 ){
            beneficiaries.add(myTerritory.getSubordinates().get((new Random()).nextInt(myTerritory.getSubordinates().numObjs)));

            retributions = new double[3];
            double benefactorCount = beneficiaries.numObjs;
            retributions[0] =
                    (myTerritory.getNatRes()) * Math.random() * .81 * (1/benefactorCount);
            retributions[1] =
                    (myTerritory.getPeasants())  * Math.random() * .81 * (1/benefactorCount);
            retributions[2] =
                    (myTerritory.getSoldiers())  * Math.random() * .61 * (1/benefactorCount);
        }
    }
    /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */
    /**
     *
     */
    @Override
    protected void attack(){
        double [] attackParams = militaryHistory.attackPreparation(0, myTerritory);
        attackedTerritoryID = (int)attackParams[MilitaryHistory.ATTACK_TERRITORY];
        attackingSoldiers = attackParams[MilitaryHistory.WITH_ATTACK_INTENSITY];
    }

    /*
    * Should update the defendingSoldiers
    */
    /**
     *
     * @param attaker
     * @param soldiersAttacking
     */
    @Override
    protected void defend(Territory attaker, double soldiersAttacking){
        //based on current resources, what is the probability of winning
        //what is the cost of a reasonably expected victory
        //what are the second order effects (loss vs win with reduced resources)
        lastAttacker = attaker.getId();
        double requirement = 0;
        double soldierCount = myTerritory.getSoldiers();
        double peasantCount = myTerritory.getPeasants() ;
        if (soldiersAttacking > soldierCount ) {
            requirement = 1.5 * soldiersAttacking - soldierCount;
            if ( requirement > myTerritory.getPeasants() || requirement >= myTerritory.getNatRes()) {
                myTerritory.produceSoldiers(requirement, requirement);
            } else {
                requirement = Math.max(myTerritory.getPeasants(), myTerritory.getNatRes());
                myTerritory.produceSoldiers(requirement, requirement);
            }

        }
        defendingSoldiers = myTerritory.getSoldiers();
       // System.out.println("me:" + myTerritory.getId() + " repel " + attaker.getId() + " with " + defendingSoldiers) ;
    }

    public Territory getMyTerritory() {
        return myTerritory;
    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    /**
     *
     * @param period
     * @param attackerID
     * @param soldiersAttack
     * @param deffenderID
     * @param soldiersDefend
     * @param youWon
     */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        militaryHistory.recordBattle(
                new BattleOutcome(period,attackerID,soldiersAttack,deffenderID,soldiersDefend,youWon)
                ,(lastAttacker == attackerID));
        lastAttacker = -1;  //reset
    }

    /*
    * Provides the information about the trade outcome, it is called every time a trade process is finished and you can do what ever you want with it.
    */
    /**
     *
     * @param period
     * @param proposerID
     * @param tradeProposal
     * @param tradeCompleted
     */
    @Override
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
    }

    public Bag findTraders(){

        Bag _neighbors = myTerritory.getNeighbors();   //my neighbors
        Bag _subordinates = myTerritory.getSubordinates(); //territories i have conquered

        potential_traders.clear();
        
        for (int i = 0; i <  _neighbors.numObjs;   i++) {
           if (  !potential_traders.contains(_neighbors.get(i))){
                potential_traders.add(_neighbors.get(i));
            }
        }

       if  (_subordinates != null)  {
          for (int j=0; j <_subordinates.numObjs; j++){

            Bag subordinate_neighbors =((Territory)_subordinates.get(j)).getNeighbors();

            for (int k=0; k<subordinate_neighbors.numObjs; k++)
               
               if (!potential_traders.contains(subordinate_neighbors.get(k))){
                    potential_traders.add(subordinate_neighbors.get(k));
               }
            }
       }
       return potential_traders;
     }
}


