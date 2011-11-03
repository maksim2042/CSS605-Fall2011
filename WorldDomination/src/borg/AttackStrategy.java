/*
 * Author: Chris Kirkos
 */
package borg;

import risk.*;
import sim.util.Bag;
import java.util.HashMap;
import java.util.Set;
//import livemore.LordUtility;

public class AttackStrategy {

    protected Territory myTerr;

    public int territoryToAttack=0;
    //public double potentialOpponentSoldiers=0.0;
    //public double newSoldiersRequired=0.0;

    // never fight if the opponent has this much change of winning
    protected static double opponentWinPotentialThreshold = 0.7;

    public static double getWinPotentialThreshold(){
        return opponentWinPotentialThreshold;
    }

    public AttackStrategy(Territory myTerr){
        this.myTerr = myTerr;
    }

    public boolean willAttack(double winPotential){
        return winPotential > opponentWinPotentialThreshold ? true : false;
    }

    protected boolean superiorIsEnemy(){
        return this.myTerr.getSuperior() != null && this.myTerr.getSuperior().getType() != this.myTerr.getType();
    }

    /*
     * Attack Priorities:
     * 1. Free any subordinate
     * 1a. Free any friend who is subordinate to a neighbor
     * 1b. Free any friend who is more than 1 level under me
     * 2. Vengeance on common/frequent attacker
     *     Figure out which territories are owned by rulers of top attacker
     * 3. Common neighbors of >1 of my territories
     * 4. Territories of rulers with high total
     * 5. Territories with many subordinates
     */
    public int chooseTerritoryToAttack(){
        this.territoryToAttack = -1;

        // If I can take over my superior, do it
        if(superiorIsEnemy()){
            //double chance = StrategyUtility.getWinPotential(myTerr, myTerr.getSuperior());
            //if(chance > 0.01){
                this.territoryToAttack = this.myTerr.getSuperior().getId();
                //System.out.println("Attack phase 1");
                return this.territoryToAttack;
           // }
        }

        
        // I will try to attack and free any subordinate neighbor or > 1 level deep subordinate of mine who is my type
        //if(this.myTerr.getSuperior() == null){ //|| superiorIsEnemy()
            Bag selfTerrs = StrategyUtility.attackableSelfTerritories(this.myTerr, true);
            if(selfTerrs.numObjs>0){
                HashMap<Territory, Double> selfValuationDescOrder = StrategyUtility.sortByValueDescending(
                    StrategyUtility.evaluateTerritoryValues(selfTerrs) );
                Territory selfAttackT = selfValuationDescOrder.keySet().toArray(new Territory[selfTerrs.numObjs])[0];
                this.territoryToAttack = selfAttackT.getId();
                //System.out.println("Self attack: " + String.valueOf(selfAttackT.getId()) + " is ruled by " + String.valueOf(selfAttackT.getSuperior().getId()));
                //System.out.println("Attack phase 2");
                return this.territoryToAttack;
            }
        //}
        

        // Attack territories that have a lot of subordinates
        Bag territories = StrategyUtility.attackableTerritories(this.myTerr);
        // Calc potential wins for each attackable Territory
        /*
        HashMap<Territory,Double> territoryPotentials = new HashMap<Territory,Double>();
        for(int i=0; i<territories.numObjs; i++){
            Territory cur = (Territory) territories.get(i);
            territoryPotentials.put(cur, Double.valueOf(StrategyUtility.getWinPotential(this.myTerr, cur)));
        }
        HashMap<Territory,Double> attackPotentials = StrategyUtility.sortByValueDescending(territoryPotentials);
        */

        HashMap<Territory, Double> valuationDescOrder = StrategyUtility.sortByValueDescending(
                StrategyUtility.evaluateTerritoryValues(territories) );
        

        // Find friend subordinates in subordinate list, 
        // and eval their superior territories for potential attack
        // at the same time, find a potential alternate to attack
        int priority1TerritoryID = -1, priority2TerritoryID = -1;
        Territory territory1 = null, territory2 = null;
        double priority1AttackPotential = 0.0, priority2AttackPotential = 0.0;
        Set<Territory> aterr = valuationDescOrder.keySet(); //attackPotentials
        for(Territory ter : aterr){
            if(ter.getType() != this.myTerr.getType()){
                double tempPotential = valuationDescOrder.get(ter);
                if( willAttack(tempPotential) ){ // check winPotential for possible attack
                    if(StrategyUtility.hasFriendAsSubordinate(ter, this.myTerr.getType())){ // "if ter has a subordinate of type myTerr.getType()"
                        // best potential
                        if(tempPotential > priority1AttackPotential){
                            priority1TerritoryID = ter.getId();
                            territory1 = ter;
                            priority1AttackPotential = tempPotential;
                        }
                    }else{
                        if(tempPotential > priority2AttackPotential){
                            priority2TerritoryID = ter.getId();
                            territory2 = ter;
                            priority2AttackPotential = tempPotential;
                        }
                    }
                }
            }
        }

        //double soldiersReqdForAttack = 0.0;

        if(priority1AttackPotential > 0.0){
            this.territoryToAttack = priority1TerritoryID;
            //soldiersReqdForAttack = StrategyUtility.numDefendingSoldiers(territory1, 1-opponentWinPotentialThreshold);
                        
        }else if(priority2AttackPotential > 0.0){
            this.territoryToAttack = priority2TerritoryID;
            //soldiersReqdForAttack = StrategyUtility.numDefendingSoldiers(territory2, 1-opponentWinPotentialThreshold);
        }else{
            return 0;
        }

        //if(this.territoryToAttack == -1){
       //     Bag subs = StrategyUtility.getSubordinateHierarchy(this.myTerr);
       //     if(subs.isEmpty()){

      //      }//else if(subs.size() <= 3){
            //    threshold = 0.6;
            //}

      //  }

        //this.newSoldiersRequired = soldiersReqdForAttack - this.myTerr.getSoldiers();

        //System.out.println("Attack phase 3");
        return this.territoryToAttack;
    }

    
    

    //public double makeSoldiers(int numberSoldiers){
        // soldiers = (soldiers + Math.pow(natRes, alpha) * Math.pow(peasants, 1 - alpha));
        // p^0.6*r^0.4 - 5 == 0
        // ( r^a * p^(1-a) ) - 5 == 0
        // if a is smaller than 0.5, more peasants than resources
        // r=p always produces r soldiers

    //}

   

    /*
     * determines the amount of soldiers necessary for attacking terrToAttack
     * attempts to obtain such an amount
     * sets this.attackingSoldiers
     *
    public int chooseSoldiers(Territory terrToAttack){



    }
    */

    //double attackPotential = attackers.get(mostLikelyAttacker);
    //double myPotentialSoldiers = StrategyUtility.getPotentialSoldiers(this.myTerr);

    /*
     * Inspired by livermore.LordUtility.AttackableTerritories
     
    public Bag AttackableTerritories() {
            Bag attTerr = new Bag();
            Territory mySuperior = this.myTerr.getSuperior();
            if (mySuperior != null) attTerr.add(mySuperior);
            for (int i=0; i < this.myTerr.getNeighbors().numObjs; i++) {
                    Territory terr = (Territory) this.myTerr.getNeighbors().get(i);
                    // include if different type
                    if (terr.getType() != this.myTerr.getType()) attTerr.add(terr);
            }
            // all territories below current territory in hierarchy that are not ruled by the same ruler type as me
            Bag allSubs = StrategyUtility.getSubordinateHierarchy(myTerr);
            for (int i=0; i < allSubs.numObjs; i++) {
                Territory sub = (Territory) allSubs.get(i);
                boolean cond1 = !myTerr.getSubordinates().contains(sub); // I don't want to try to take over my direct subordinates that I have alread taken over
                boolean cond2 = sub.getType() != this.myTerr.getType(); // I don't want to take over myself
                boolean cond3 = !attTerr.contains(sub); // No duplicates
                if( cond1 && cond2 && cond3 ){
                    attTerr.add(sub);
                }
            }
            return attTerr;
    }
    */
    

}
