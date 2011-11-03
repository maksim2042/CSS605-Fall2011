/*
 * Author: Chris Kirkos
 */
package borg;

import risk.*;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;

public class StrategyUtility {


    public static Bag attackableTerritories(Territory sourceTerritory) {
        return attackableTerritories(sourceTerritory, false);
    }

    /*
     * Determines who the sourceTerritory can attack
     * Inspired by livermore.LordUtility.AttackableTerritories
     */
    public static Bag attackableTerritories(Territory sourceTerritory, boolean includeSelfType) {
            Bag attTerr = new Bag();
            Territory mySuperior = sourceTerritory.getSuperior();
            if (mySuperior != null) attTerr.add(mySuperior);
            for (int i=0; i < sourceTerritory.getNeighbors().numObjs; i++) {
                    Territory terr = (Territory) sourceTerritory.getNeighbors().get(i);
                    // include if different type
                    //if ( !includeSelfType && terr.getType() != sourceTerritory.getType()) { attTerr.add(terr); }
                    if(sourceTerritory.getType () == terr.getType()){
                        if(includeSelfType){
                            attTerr.add(terr);
                        }
                    }else{
                        attTerr.add(terr);
                    }
            }
            // all territories below current territory in hierarchy that are not ruled by the same ruler type as me
            Bag allSubs = StrategyUtility.getSubordinateHierarchy(sourceTerritory);
            for (int i=0; i < allSubs.numObjs; i++) {
                Territory sub = (Territory) allSubs.get(i);
                boolean cond1 = !sourceTerritory.getSubordinates().contains(sub); // I don't want to try to take over my direct subordinates that I have alread taken over
                boolean cond2 = includeSelfType ? true : sub.getType() != sourceTerritory.getType(); // I don't want to take over myself
                boolean cond3 = !attTerr.contains(sub); // No duplicates
                if( cond1 && cond2 && cond3 ){
                    attTerr.add(sub);
                }
            }
            return attTerr;
    }

      /*
     * Return all territories of the same type that sourceTerritory can attack.
     * If withSuperiorsOfDifferentType is true, only those that have a superior of a different type than sourceTerritory
     * will be returned (those under the rule of another ruler)
     */
    public static Bag attackableSelfTerritories(Territory sourceTerritory, boolean withSuperiorsOfDifferentType){
        Bag attTerr = new Bag();
        for (int i=0; i < sourceTerritory.getNeighbors().numObjs; i++) {
            Territory terr = (Territory) sourceTerritory.getNeighbors().get(i);
            boolean superiorCheck = !withSuperiorsOfDifferentType ||
                    (terr.getSuperior() != null && terr.getSuperior().getType() != sourceTerritory.getType());
            if ( terr.getType() == sourceTerritory.getType() && superiorCheck ) {
                attTerr.add(terr);
            }
        }
        Bag allSubs = StrategyUtility.getSubordinateHierarchy(sourceTerritory);
            for (int i=0; i < allSubs.numObjs; i++) {
                Territory sub = (Territory) allSubs.get(i);
                boolean superiorCheck = !withSuperiorsOfDifferentType ||
                        (sub.getSuperior() != null && sub.getSuperior().getType() != sourceTerritory.getType()); // if I am only including those who have superiors
                boolean cond1 = !sourceTerritory.getSubordinates().contains(sub); // I don't want to try to take over my direct subordinates that I have alread taken over
                boolean cond2 = sub.getType() == sourceTerritory.getType(); // only self types
                boolean cond3 = !attTerr.contains(sub); // No duplicates
                if( cond1 && cond2 && cond3 && superiorCheck){
                    attTerr.add(sub);
                }
            }
            return attTerr;
    }

    /*
     * Get all territories that can attack target
     */
    public static Bag potentialAttackers(Territory target){
        Bag attackers = new Bag();
        Bag allTerritories = new Bag();
        getAllTerritories(target, allTerritories);
        for(int i=0; i<allTerritories.numObjs; i++){
            Territory curTerr = (Territory)allTerritories.get(i);
            if( attackableTerritories(curTerr).contains(target) ){ // "if the territories curTerr can attack include target"
                attackers.add(curTerr);
            }
        }
        return attackers;
    }

    /*
     * Determine which attackers are likely to attack.
     * Returns a sorted (desc by attack potential) map of all territories that can attack 'terr'
     * along with their attack potentials.
     */
    public static HashMap<Territory,Double> likelyAttackers(Territory terr){
        //Bag likely = new Bag();
        HashMap<Territory,Double> likely = new HashMap<Territory,Double>();
        Bag potential = potentialAttackers(terr);
        for(int i=0; i<potential.numObjs; i++){
            Territory attacker = (Territory)potential.get(i);
            double winPot = getWinPotential(attacker, terr);
            //if( winPot > 0.5 ){
            likely.put(attacker, winPot);
            //}
        }
        return sortByValueDescending(likely);
    }

    public static double cobbDoug(double natRes, double peasants, double alpha){
        return Math.pow(natRes, alpha) * Math.pow(peasants, 1 - alpha);
    }

    // Attack Protocol:
    // if(rand.nextDouble() >= (attackIntensity)/(attackIntensity+defenseIntensity))
    //     attackSucceeded = false;

    /*
     * Returns the amount of soldiers a territory has + can make with their current resources
     */
    public static double getPotentialSoldiers(Territory terr){
        return terr.getSoldiers() + cobbDoug(terr.getNatRes(), terr.getPeasants(), terr.getAlpha());
    }

    /*
     * Returns the potential for the attacker to win in range [0,1].
     * Determines the winning chances if all resources from both territories are used to make soldiers.
     */
    public static double getWinPotential(Territory attacker, Territory defender){
        double attackerPotentialSoldiers = getPotentialSoldiers(attacker);
        double defenderPotentialSoldiers = getPotentialSoldiers(defender);
        return attackerPotentialSoldiers/(attackerPotentialSoldiers+defenderPotentialSoldiers);
    }

    public static double getWinPotential(double soldiersAttacking, Territory defender){
        double defenderPotentialSoldiers = getPotentialSoldiers(defender);
        return soldiersAttacking/(soldiersAttacking+defenderPotentialSoldiers);
    }

    /*
     * Returns the amount of soldiers needed to defend against opponentTerritory
     * such that the opponentTerritory will have opponentWinPotential chance at winning,
     * assuming the oponent will use full force (max resources to produce soldiers).
     */
    public static double numDefendingSoldiers(Territory opponentTerritory, double opponentWinPotential){
        double opponentPotentialSoldiers = getPotentialSoldiers(opponentTerritory);
        return numDefendingSoldiers(opponentPotentialSoldiers, opponentWinPotential);
        //return (opponentPotentialSoldiers/opponentWinPotential)-opponentPotentialSoldiers;
    }

    public static double numDefendingSoldiers(double opponentSoldiers, double opponentWinPotential){
        return (opponentSoldiers/opponentWinPotential)-opponentSoldiers;
    }

    /*
     * Win potential if you dumped all reasources from each friend territory of the same type into 1
     * and fought type vs type
     */
    public static double getRulerWinPotential(Territory attackingTerritory, Territory defendingTerritory){
        Bag allTerritories = new Bag();
        getAllTerritories(defendingTerritory, allTerritories);
        int attackerType = attackingTerritory.getType();
        int defenderType = defendingTerritory.getType();
        double attackerPotential = 0.0, defenderPotential = 0.0;

        for(int i=0; i<allTerritories.numObjs; i++){
            Territory curTerr = (Territory) allTerritories.get(i);
            int curTerrType = curTerr.getType();
            double terrPotential = getPotentialSoldiers(curTerr); //curTerr.getSoldiers() + cobbDoug(curTerr.getNatRes(), curTerr.getPeasants(), curTerr.getAlpha());
            if( curTerrType == attackerType  ){
                attackerPotential += terrPotential;
            }else if(curTerrType == defenderType){
                defenderPotential += terrPotential;
            }
        }
        return attackerPotential/(attackerPotential+defenderPotential);
    }

    /*
     * For a given territory, determine their resource value and
     * potential value projected 30 ticks into future.
     */
    public static double territoryValue(Territory terr){
        return getPotentialSoldiers(terr) + ( cobbDoug(terr.getFoodGrowth() ,terr.getFarmGrowth(), terr.getAlpha()) * 30 );        
    }

    /*
     * Evaluates the territory's value, and the potential tax from all of its subordinates as well.
     */
    public static double territoryValueWithSubordinates(Territory terr){
        double terrValue = 0.0;
        Bag subs = getSubordinateHierarchy(terr);
        for(int i=0; i<subs.numObjs; i++){
            Territory sub = (Territory) subs.get(i);
            terrValue += territoryValueWithSubordinates(sub) * 0.5; // assume 50% tax rate
        }
        terrValue += territoryValue(terr);
        return terrValue;
    }

    /*
     * Determine the value a territory, given a win over them in battle
     */
    public static double postWinTerritoryValue(Territory terr){
        // After a win in battle of terr, soldiers get cut in half
        // + project 30 iterations into future for amount gained from this land
        double tValue = (getPotentialSoldiers(terr)/2.0) +
                ( cobbDoug(terr.getFoodGrowth() ,terr.getFarmGrowth(), terr.getAlpha()) * 30 );
        return tValue;
    }

    /*
     * Determines if a Territory of type friendType is a subordinate of Territory terr.
     */
    public static boolean hasFriendAsSubordinate(Territory terr, int friendType){
        for(int i=0; i<terr.getSubordinates().numObjs; i++){
            if( ((Territory)terr.getSubordinates().get(i)).getType() == friendType){
                return true;
            }
        }
        return false;
    }

    /*
     * Evaluates potential attackers, returns the soldier availability for this territory.
     * If positive, soldiers are available above threshold for trade/resource realloc
     * If negative, this territory needs soldiers/resources to be able to win against strongest
     * opponent with opponentWinPotentialThreshold chance of winning.
     */
    public static double soldierAvailability(Territory terr, double opponentWinPotentialThreshold){
        double availableSoldiers = 0.0;
        // get most likely attackers sorted by liklihood of attack
        HashMap<Territory, Double> attackers = StrategyUtility.likelyAttackers(terr);
        if(attackers.size() > 0){
            // No one has > oWPThreshold chance of beating me, so I am free to lend resources
            Territory mostLikelyAttacker = attackers.keySet().toArray(new Territory [attackers.keySet().size()])[0]; // highest risk attacker
            // Determine resource availability
            double opponentPotentialSoldiers = StrategyUtility.getPotentialSoldiers(mostLikelyAttacker);
            // I never want a potential attacker to have greater than oWPThreshold chance to beat me
            double requiredSoldiers = (opponentPotentialSoldiers/opponentWinPotentialThreshold)-opponentPotentialSoldiers;
            availableSoldiers = getPotentialSoldiers(terr) - requiredSoldiers;
        }
        return availableSoldiers;
    }

    /*
     * Returns a list of territories and their value to provide resources
     */
    public static HashMap<Territory, Double> evaluateTerritoryValues(Bag territories){
        HashMap<Territory, Double> valuation = new HashMap<Territory,Double>();
        for(int i=0; i<territories.numObjs; i++){
            Territory curTerr = (Territory) territories.get(i);
            valuation.put(curTerr, StrategyUtility.territoryValueWithSubordinates(curTerr));
        }
        return valuation;
    }

    /*
     * Given a territory id, return a territory object (immutable) for that id
     */
    public static Territory getTerritory(Territory source, int id){
        Bag territories = new Bag();
        getAllTerritories(source, territories);
        for(int i=0; i<territories.numObjs; i++){
            Territory terr = (Territory) territories.get(i);
            if(terr.getId() == id){
                return terr;
            }
        }
        return null;
    }

    /*
     * Return HashMap sorted by _values_ in _Ascending_ order.
     */
    public static HashMap sortByValue(Map map) {
         List list = new LinkedList(map.entrySet());
         Collections.sort(list, new Comparator() {
              public int compare(Object o1, Object o2) {
                   return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
              }
         });
        // logger.info(list);
        HashMap result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
             Map.Entry entry = (Map.Entry)it.next();
             result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static HashMap sortByValueDescending(Map map) {
         List list = new LinkedList(map.entrySet());
         Collections.sort(list, new Comparator() {
              public int compare(Object o1, Object o2) {
                   return ((Comparable) ((Map.Entry) (o2)).getValue())
                  .compareTo(((Map.Entry) (o1)).getValue());
              }
         });
        // logger.info(list);
        HashMap result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
             Map.Entry entry = (Map.Entry)it.next();
             result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static HashMap getSortedMap(HashMap hmap){
        HashMap map = new LinkedHashMap();
        List mapKeys = new ArrayList(hmap.keySet());
        List mapValues = new ArrayList(hmap.values());
        hmap.clear();
        TreeSet sortedSet = new TreeSet(mapValues);
        Object[] sortedArray = sortedSet.toArray();
        int size = sortedArray.length;
        // a) Ascending sort
        for (int i=0; i<size; i++){
            map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), sortedArray[i]);
        }
        return map;
    }

    /*
     * Flattens the subordinate hierarchy into a single Bag.
     * I am allowed to attack anything below me in my chain of territories so that they can be a direct subordinate.
     *     if(attacker.getNeighborTerritories().contains(defender) || attacker.isAbove(defender))
     * Removing subs from levels down stops others from being able to take wealth from that sub territory.
     *     I don't want to share the wealth with others.
     */
    public static Bag getSubordinateHierarchy(Territory terr){
        Bag hierarchy = new Bag();
        Bag subs = terr.getSubordinates();
        hierarchy.addAll(subs);
        for (int i=0; i < subs.numObjs; i++) {
            hierarchy.addAll( getSubordinateHierarchy((Territory)subs.get(i)) );
        }
        return hierarchy;
    }

    // Check if a territory is in the same hierarchical structure of the current one.
    // Check if target is in source's hierarchy
    // Taken from non-public method
    public static boolean isInHierarchy(Territory source, Territory target){
        boolean isHere = false;
        for(int i=0; i<source.getSubordinates().numObjs; i++){
            Territory subordinate = (Territory)source.getSubordinates().get(i);
            if (isHere==false){
                if(subordinate.equals(target)){
                    isHere = true;
                    break;
                }
                else{
                    isHere = isInHierarchy(source, subordinate);
                }
            }
        }
        return isHere;
    }

   /*
    * Is territoryA above territoryB in B's hierarchy?
    * Modified version of Territory.isAbove
    */
   public static boolean isAbove(Territory territoryA, Territory territoryB){
        boolean isAbove = false;
        if(territoryA!=null && territoryB!=null){
            if (territoryB.getSuperior().equals(territoryA)){
                isAbove = true;
            }
            else{
                isAbove = isAbove(territoryA, territoryB.getSuperior());
            }
        }
        return isAbove;
    }

   /*
    * Shamelessly taken from livemore.LordUtility
    */
   public static Bag getAllTerritories(Territory currTerr, Bag terrs) {
        int numTerr = 42;
        int startNumTerr = terrs.numObjs;
        // For each neighbor - add neighbor if not already added
        for(int i=0; i < currTerr.getNeighbors().numObjs; i++) {
                Territory terr = (Territory) currTerr.getNeighbors().get(i);
                if (!terrs.contains(terr)) terrs.add(terr);
        }
        // return if maximum (42) hit or if no change in number
        if (terrs.numObjs == numTerr || startNumTerr == terrs.numObjs)
                return terrs;

                // For each neighbor - call this check if not already called
        for(int i=0; i < currTerr.getNeighbors().numObjs; i++) {
                Territory neighbor = (Territory) currTerr.getNeighbors().get(i);
                getAllTerritories(neighbor, terrs);
        }

        return terrs;
    }

}


// #################
//                SCRAP
// #################

        //double attackerPotential = 0.0, defenderPotential = 0.0;
        //attackerPotential += attacker.getSoldiers() + cobbDoug(attacker.getNatRes(), attacker.getPeasants(), attacker.getAlpha());
        //defenderPotential += defender.getSoldiers() + cobbDoug(defender.getNatRes(), defender.getPeasants(), defender.getAlpha());


    /*
     * Calculates the win potential given all territory's force is used by both attacker and defender.
     *
    public static double getWinPotential1(Territory attacker, Territory defender){
        double totalA = 0.0, totalD = 0.0;
        double aR = attacker.getNatRes();
        double aP = attacker.getPeasants();
        double aS = attacker.getSoldiers();
        double dR = defender.getNatRes();
        double dP = defender.getPeasants();
        double dS = defender.getSoldiers();

        double aMinRP = aR <= aP ? aR : aP;
        double dMinRP = dR <= dP ? dR : dP;

        totalA = aS + aMinRP;
        totalD = dS + dMinRP;

        return totalA/totalD;
    }


     
     */