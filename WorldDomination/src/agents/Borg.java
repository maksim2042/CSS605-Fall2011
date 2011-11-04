/*
 * Author: Chris Kirkos
 */
package agents;

import java.util.ArrayList;
import java.util.HashMap;

import risk.*;
import sim.util.Bag;

import borg.AttackStrategy;
import borg.StrategyUtility;

public class Borg extends Agent {

    protected static Bag myAgents = new Bag();

    protected static double selfAttackVal = 0.000001;
    protected static double productionConstraint = 0.95;

    public Borg(int id, int type){
        super(id, type);
        empireName = "Borg";
    }

    protected final void addSelf(){
        if(!myAgents.contains(this)){
            myAgents.add(this);
        }
    }

    protected Bag getAllBorgTerritories(){
        return this.getAllBorgTerritories(true);
    }

    protected Bag getAllBorgTerritories(boolean includeSelf){
        Bag territories = new Bag();
        for(int i=0; i<myAgents.numObjs; i++){
            Borg b = (Borg) myAgents.get(i);
            boolean cond1 = includeSelf == true ? true : b != this;
            if(b != null && cond1){
                territories.add(b.myTerritory);
            }
        }
        return territories;
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
        // reset array
        this.trade[0] = 0;
        this.trade[1] = 0;
        this.trade[2] = 0;
        this.trade[2] = 0;
        this.trade[4] = 0;

        //System.out.println( String.format("%1$d)  Soldiers: %2$f  NatRes: %3$f  Peasants: %4$f  Alpha: %5$f", this.myTerritory.getId(), this.myTerritory.getSoldiers(), this.myTerritory.getNatRes(), this.myTerritory.getPeasants(), this.myTerritory.getAlpha() ) );
        /*

        // if I have too many soldiers and not enough resources, give soldiers to my friends

        HashMap<Territory, Double> availability = new HashMap<Territory,Double>();
        HashMap<Territory, Double> valuation = new HashMap<Territory,Double>();

        Bag myTerritories = this.getAllBorgTerritories();
        //HashMap<Territory, Double> valuation = StrategyUtility.evaluateTerritoryValues(myTerritories);
        
        for(int i=0; i<myTerritories.numObjs; i++){ //this.CG.getTerritories()
            Territory curTerr = (Territory) myTerritories.get(i);
            double terrAvail = StrategyUtility.soldierAvailability(curTerr, AttackStrategy.getWinPotentialThreshold());
            availability.put(curTerr, terrAvail);
            valuation.put(curTerr, StrategyUtility.territoryValueWithSubordinates(curTerr));
        }

        HashMap<Territory, Double> valuationOrdered = StrategyUtility.sortByValueDescending(valuation);
        Territory[] valuationOrderedArray = valuationOrdered.keySet().toArray(new Territory [valuationOrdered.keySet().size()]);

        // if I have resources to give
        if( availability.get(this.myTerritory) > 0 ){
            // find a friend to give to
            // TODO: only give to a territory that needs <= what I can offer ???
            for(Territory terrInNeed : valuationOrderedArray){
                if( terrInNeed != this.myTerritory && availability.get(terrInNeed).doubleValue() < 0.0 ){ // if this territory is in need
                    double amtAvailable = availability.get(this.myTerritory).doubleValue();

                    if(amtAvailable > this.myTerritory.getSoldiers()){
                        amtAvailable = this.myTerritory.getSoldiers();
                        //double amtSoldiersNeeded = amtAvailable - this.myTerritory.getSoldiers();
                        //this.myTerritory.produceSoldiers(amtSoldiersNeeded*productionConstraint, amtSoldiersNeeded*productionConstraint);
                    }

                    double amtDebt = availability.get(terrInNeed).doubleValue();
                    double amtTransfer = amtAvailable + amtDebt >= 0 ? amtDebt : amtAvailable;
                    
                    double resourceType = 3; //(1 for natural resources, 2 for peasants, and 3 for soldiers)
                    //if(terrInNeed.getPeasants() > terrInNeed.getNatRes()) resourceType = 1;
                    // or check their alpha, if it is < 0.5, then more natres is needed
                    this.trade[0] = terrInNeed.getId();
                    this.trade[1] = 3;
                    this.trade[2] = 0;
                    this.trade[3] = resourceType;
                    this.trade[4] = amtTransfer;
                    break; // we found someone, and can only make 1 trade, so leave the loop
                }
            }
        }*/
    }

   /*
    * Should update acceptTrade (boolean).
    */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
            this.acceptTrade = false;
            //System.out.println("Trade Offer -> Offeror type: " + String.valueOf(offerer.getType() + " My type: " + String.valueOf(this.myTerritory.getType())));
            if(offerer.getType() == this.myTerritory.getType() && myAgents.contains(offerer) && offerer!=this.myTerritory){ //&& this.getAllBorgTerritories().contains(offerer)
                this.acceptTrade = true;
                System.out.println("Trade Acceptance -> Offeror type: " + String.valueOf(offerer.getType() + " My type: " + String.valueOf(this.myTerritory.getType())));
            }
            if(offerer==this.myTerritory){
                System.out.println("I'm trading with myself, what the hell.");
            }
    }

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax(){
        // first to get called
        this.addSelf();
        // I'll decide what to do with resources
        this.tax = 0.5;
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        this.beneficiaries.clear();

        // any subordinate who has high growth rate should get some soldiers
        // much less than their ability to attack me, though
        // 1. Give soldiers to subordinates of my type
        // 2. Give excess soldiers to any subs who have high resource value
        // give back tax to subs of my type

        // give solders to the one with highest alpha and natural resource growth
        // extraSoldiersCanMaintain = theirCurrentSoldiers - theirCurrentNaturalResources+GrowthRate
        // if extraSoldiersCanMaintain > 0: give amount extraSoldiersCanMaintain OR soldiersWillLose, whichever is bigger
        //if(this.myTerritory.getNatRes() == 0){
        //HashMap<Territory,Double> retribs = new HashMap<Territory, Double>();

        
        ArrayList<Double> retribs = new ArrayList<Double>();

        Bag myTerritories = this.getAllBorgTerritories();
        Bag subs = StrategyUtility.getSubordinateHierarchy(this.myTerritory);

        HashMap<Territory, Double> availability = new HashMap<Territory,Double>();
        HashMap<Territory, Double> valuation = new HashMap<Territory,Double>();

        availability.put(this.myTerritory, StrategyUtility.soldierAvailability(this.myTerritory, 0.9));
        
         for(int i=0; i<myTerritories.numObjs; i++){
            if(subs.contains(myTerritories.get(i))){
                 Territory curTerr = (Territory) myTerritories.get(i);
                double terrAvail = StrategyUtility.soldierAvailability(curTerr, (AttackStrategy.getWinPotentialThreshold()));
                availability.put(curTerr, terrAvail);
                valuation.put(curTerr, StrategyUtility.territoryValueWithSubordinates(curTerr));
            }
        }

        HashMap<Territory, Double> valuationOrdered = StrategyUtility.sortByValueDescending(valuation);
        Territory[] valuationOrderedArray = valuationOrdered.keySet().toArray(new Territory [valuationOrdered.keySet().size()]);
        double amtAvailable = availability.get(this.myTerritory).doubleValue();
        if(amtAvailable > this.myTerritory.getSoldiers()){
            amtAvailable = this.myTerritory.getSoldiers();
        }
        //System.out.println("Amount available: " + String.valueOf(amtAvailable));

        for(Territory terrInNeed : valuationOrderedArray){
            if( terrInNeed != this.myTerritory && availability.get(terrInNeed).doubleValue() < 0.0 && amtAvailable > 0.0){ // if this territory is in need
                double amtDebt = availability.get(terrInNeed).doubleValue();
                double amtTransfer = Math.abs( amtAvailable + amtDebt >= 0 ? amtDebt : amtAvailable );
                retribs.add(new Double(amtTransfer));
                this.beneficiaries.add(terrInNeed);
                //System.out.println(String.valueOf(this.myTerritory.getId()) + " giving " + String.valueOf(amtTransfer) + " soldiers to " + String.valueOf(terrInNeed.getId()));
                amtAvailable =- amtTransfer;
                if(amtAvailable <= 0) break;
            }
       }

        /*
        double soldiersIWillLose = ( this.myTerritory.getSoldiers() - this.myTerritory.getNatRes() ) * 0.15;
        if(soldiersIWillLose>0){
            Bag myTerritories = this.getAllBorgTerritories();
            Bag subs = StrategyUtility.getSubordinateHierarchy(this.myTerritory);
            for(int i=0; i<myTerritories.numObjs; i++){
                Territory curTerr = (Territory) myTerritories.get(i);
                //double extraSoldiersCanMaintain = curTerr.getSoldiers() - (curTerr.getNatRes() + curTerr.getFoodGrowth());
                if(subs.contains(curTerr) ){ // && extraSoldiersCanMaintain > 0){
                //if(curTerr != this.myTerritory && extraSoldiersCanMaintain > 0){
                    //double soldiersToGive = extraSoldiersCanMaintain - soldiersIWillLose >=0 ? soldiersIWillLose : extraSoldiersCanMaintain;
                    double soldiersToGive = soldiersIWillLose;
                    retribs.add(new Double(soldiersToGive));
                    this.beneficiaries.add(curTerr);
                    System.out.println(String.valueOf(this.myTerritory.getId()) + " giving " + String.valueOf(soldiersToGive) + " soldiers to " + String.valueOf(curTerr.getId()));
                    soldiersIWillLose -= soldiersToGive;
                    if(soldiersIWillLose <= 0) break;
                }
            }
         * 
         */

            this.retributions = new double[retribs.size()];
            for(int i=0; i<retribs.size(); i++){
                this.retributions[i] = retribs.get(i).doubleValue();
            }
        //}
        

    }

   /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */
    @Override
    protected void attack(){
        AttackStrategy attack = new AttackStrategy(this.myTerritory);
        this.attackedTerritoryID = attack.chooseTerritoryToAttack();

        if(this.attackedTerritoryID != this.myTerritory.getId()){
            double prodamt = this.myTerritory.getNatRes()*productionConstraint;
            this.myTerritory.produceSoldiers(prodamt, prodamt);
            //System.out.println("Produce -> NatRes: " + String.valueOf(prodamt) + " Peseants:" + String.valueOf(this.myTerritory.getPeasants()));
            this.attackingSoldiers = this.myTerritory.getSoldiers();
        }else{
            this.attackingSoldiers = selfAttackVal;
        }
    }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attacker, double soldiersAttacking){
        if(this.getAllBorgTerritories(false).contains(attacker) && soldiersAttacking==selfAttackVal){
            this.defendingSoldiers = 0;
        }else{
            double attackerWinPotential = StrategyUtility.getWinPotential(soldiersAttacking, this.myTerritory);
            double reqdDefendingSoldiers = StrategyUtility.numDefendingSoldiers(attacker, 1-attackerWinPotential);

            double soldiersToProduce = reqdDefendingSoldiers - this.myTerritory.getSoldiers();
            if(soldiersToProduce>0){
                // TODO: better choice of number of natRes and peseants
                // check to make sure I have # natRes and # peseants
                //this.myTerritory.produceSoldiers(this.myTerritory.getNatRes()*productionConstraint, this.myTerritory.getPeasants());
                double prodamt = this.myTerritory.getNatRes()*productionConstraint;
                this.myTerritory.produceSoldiers(prodamt, prodamt);
                //System.out.println("Produce: " + String.valueOf(prodamt));
            }
            this.defendingSoldiers = this.myTerritory.getSoldiers();
            // TODO: conserve soldiers instead of just using them all
        }
    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
        //this.CG.addAttackInstance(attackerID);
        
        /*
        Territory attackerT = StrategyUtility.getTerritory(this.myTerritory, attackerID);
        Territory defenderT = StrategyUtility.getTerritory(this.myTerritory, deffenderID);
        if(attackerT.getType() == defenderT.getType()){
            System.out.println("Self takeover: " + String.valueOf(attackerT.getId()) + " freeing "
                    + String.valueOf(defenderT.getId()) + " Outcome: " + String.valueOf(youWon));
            //System.out.println("  " + String.valueOf(defenderT.getId()) + " was ruled by " + String.valueOf(defenderT.getSuperior().getId()));
        }
         * 
         */
    }

    /*
    * Provides the information about the trade outcome, it is called every time a trade process is finished and you can do what ever you want with it.
    */
    @Override
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
        /*
        if(tradeCompleted==true){
            Bag terrs = new Bag();
            StrategyUtility.getAllTerritories(this.myTerritory, terrs);
            for(int i=0; i<terrs.numObjs; i++){
                Territory t = (Territory) terrs.get(i);
               /if(t.getId() == proposerID){
                    System.out.println("Self trade from " + t.getRuler().getName() + " to " + this.myTerritory.getRuler().getName());
                }
            }
            //System.out.println("Trade from " + String.valueOf(proposerID) + " to " + String.valueOf(this.myTerritory.getId()));
        }
         *
         */
    }

}



//DefenseStrategy defense = new DefenseStrategy(this.myTerritory, attaker, soldiersAttacking);
        //double reqdDefendingSoldiers = defense.getDefendingSoldiers();