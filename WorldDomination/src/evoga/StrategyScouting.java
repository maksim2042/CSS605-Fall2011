/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evoga;

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

import ec.util.MersenneTwisterFast;
import agents.Evolver;

/**
 *
 * @author ckhyde
 */
public class StrategyScouting 
{
    private static MersenneTwisterFast rand = new MersenneTwisterFast();        
    public static Bag allTerritories = new Bag();    
    public static HashMap<Territory,Integer> initNumPhyNeighbor = new HashMap<Territory,Integer>();
    public static HashMap<Territory,Double> hierarchyThreat = new HashMap<Territory,Double>();
    public static HashMap<Territory,Double> hierarchyLevel = new HashMap<Territory,Double>();
    public static HashMap<Territory,Double> targetAttractiveness = new HashMap<Territory,Double>();
    
    
   /*
    * Shamelessly taken from livemore.LordUtility
    */
   //public static Bag getAllTerritories(Territory currTerr, Bag terrs) {
    public static void calcuWorldWideTerritories(Territory myTerritory) 
    {
        int numTotalTerritory = 42;        
                        
        if (!allTerritories.contains(myTerritory)) 
                    allTerritories.add(myTerritory);
        
        Bag tmpNeighborTerritories = myTerritory.getNeighbors();
        //allTerritories.addAll(myTerritory.getNeighbors());
               
        //int startNumTerr = terrs.numObjs;
        // For each neighbor - add neighbor if not already added
        //for(int i=0; i < myTerritory.getNeighbors().numObjs; i++) {
        for(int i=0; i < tmpNeighborTerritories.numObjs; i++) 
        {
                Territory tmpTerritory = (Territory)myTerritory.getNeighbors().get(i);                
                
                if (!allTerritories.contains(tmpTerritory)) 
                    allTerritories.add(tmpTerritory);                                                                                
        }        
        
        
        if (allTerritories.numObjs == numTotalTerritory)                        
            return;           
        else 
            StrategyScouting.calcuWorldWideTerritories((Territory)tmpNeighborTerritories.get(rand.nextInt(tmpNeighborTerritories.numObjs)));
        
        
    }    
    
    
    public static void calcuInitPhysicalTerritories() 
    {
        for (Object tr: allTerritories)
            initNumPhyNeighbor.put((Territory)tr, ((Territory)tr).getNeighbors().numObjs);        
    }        
    
          
//    /*
//     * Flattens the subordinate hierarchy into a single Bag.
//     * I am allowed to attack anything below me in my chain of territories so that they can be a direct subordinate.
//     *     if(attacker.getNeighborTerritories().contains(defender) || attacker.isAbove(defender))
//     * Removing subs from levels down stops others from being able to take wealth from that sub territory.
//     *     I don't want to share the wealth with others.
//     */
//    //public static Bag getHierarchyStructure(Territory curTerritory){
//    public static void getHierarchyStructure(Territory curTerritory, Map hierarchyMap, int hierarchyLevelCounter){
//        //Bag hierarchy = new Bag();
//        HashMap<Territory,Integer> tmpHierarchy = new HashMap<Territory,Integer>();
//        
//        int tmpLevelCounter = hierarchyLevelCounter;
//        tmpLevelCounter++;
//        
//        for (Object tr: curTerritory.getSubordinates())
//        {    
//            ((HashMap)hierarchyMap).put((Territory)tr, hierarchyLevelCounter);        
//            getHierarchyStructure((Territory)tr, hierarchyMap ,tmpLevelCounter);
//        }                   
//    }    
//    
//    public static Bag getSubordinateHierarchy(Territory curTerritory, int hierarchyLevelCounter)
//    {
//        Bag hierarchyStructure = new Bag();
//        
//        Bag tmpHierarchySubs = curTerritory.getSubordinates();
//        Bag tmpHierarchyLevels = new Bag();
//        
//        //hierarchy.addAll(subs);
//        hierarchyStructure.add(tmpHierarchySubs);
//        for (int i=0; i < subs.numObjs; i++) {
//            hierarchy.addAll( getSubordinateHierarchy((Territory)subs.get(i)) );
//        }
//        hierarchyStructure.add();
//        
//        return hierarchyStructure;
//    }
//    
    
    public static Bag getHierarchicalSuperiorsTerritories(Territory currTerritory) 
    {
        Bag superiorsTerritories = new Bag();
    
        //Territory directSuperior = currTerritory.getSuperior();
    
        //if (directSuperior != null)
        if (currTerritory.getSuperior() != null)
        {    
            Territory directSuperior = currTerritory.getSuperior();
            superiorsTerritories.add(directSuperior);
            
    
            if (directSuperior.getSuperior() != null)
                superiorsTerritories.addAll(getHierarchicalSuperiorsTerritories(directSuperior));
        }    
    
        return superiorsTerritories;
    }        
    
    
    public static Bag getHierarchicalSubordinatesTerritories(Territory terr)
    {
        Bag hierarchy = new Bag();
        
        Bag subs = terr.getSubordinates();
        
        if (!subs.isEmpty())
            hierarchy.addAll(subs);
        
        for (int i=0; i < subs.numObjs; i++) 
        {
            hierarchy.addAll( getHierarchicalSubordinatesTerritories((Territory)subs.get(i)) );
        }
        
        return hierarchy;
    }
    
   
    public static double getHierarchyLevelThreat(Territory terr, int hierarchyLevelCounter, double LevelThreat)
            //public static double getHierarchyLevelThreat(Territory terr, int hierarchyLevelCounter, double levelThreat)
    {
        double tmpLevelThreat = LevelThreat;
        //double levelThreat = 0;
        //double tmpLevelThreat = 0;
      
        //int tmpLevelCounter = hierarchyLevelCounter;
        //tmpLevelCounter++;
            
        
        //levelThreat += 1/tmpLevelCounter;
        //levelThreat += 1/hierarchyLevelCounter;
        
        //tmpLevelThreat = 100;
        //tmpLevelThreat[0] += 1/hierarchyLevelCounter;
//      System.out.println("Threat Before" + tmpLevelThreat);
        //Bag hierarchy = new Bag();
        Bag subs = terr.getSubordinates();
        //hierarchy.addAll(subs);
        
        //if ((!subs.isEmpty()) || (hierarchyLevelCounter > 0))
        //{    
            hierarchyLevelCounter++;
            tmpLevelThreat += 1/(double)hierarchyLevelCounter;
        //}
        //else 
            //tmpLevelThreat += 0;
        
            
            
                    
        for (int i=0; i < subs.numObjs; i++) 
        {
            //levelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter);
            //tmpLevelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, levelThreat);
            //tmpLevelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter);
            //tmpLevelThreat[0] += 1/getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, tmpLevelThreat);
            tmpLevelThreat = getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, tmpLevelThreat);
            //hierarchy.addAll( getSubordinateHierarchy((Territory)subs.get(i)) );
          
        }
      
//        System.out.println("hierarchyLevelCounter " + hierarchyLevelCounter);
//        System.out.println("Threat After" + tmpLevelThreat);
        //return levelThreat;
        //return tmpLevelThreat;
        //return hierarchyLevelCounter;
        //return 1/hierarchyLevelCounter;
        return tmpLevelThreat;
  }    
    
    public static void calcuAllHierarchyLevelThreat() 
    {
        int tmpHierarchyLevelCounter = 0;
        double tmpLevelThreat = 0;
        //double[] tmpLevelThreat = new double[1];
        
        
        
        for (Object tr: allTerritories)
        {
            tmpLevelThreat = 0;
            tmpHierarchyLevelCounter = 0;
            
            if (!((Territory)tr).getSubordinates().isEmpty())                
                //getHierarchyLevelThreat((Territory)tr, tmpHierarchyLevelCounter, tmpLevelThreat);
                hierarchyThreat.put((Territory)tr, getHierarchyLevelThreat((Territory)tr, tmpHierarchyLevelCounter, tmpLevelThreat));        
                //hierarchyThreat.put((Territory)tr, tmpLevelThreat[0]);                    
            else
                hierarchyThreat.put((Territory)tr, 0d);        
        }       
    }    
    
    public static double calcuHierarchyLevelThreat(Territory currTerritory) 
    {
        int tmpHierarchyLevelCounter = 0;
        double tmpLevelThreat = 0;
        //double[] tmpLevelThreat = new double[1];                
            
        if (!currTerritory.getSubordinates().isEmpty())                            
            return getHierarchyLevelThreat(currTerritory, tmpHierarchyLevelCounter, tmpLevelThreat);                        
        else
            return 0;        
              
    }     
    
    
    public static double getHierarchyLevel(Territory terr, int hierarchyLevelCounter, double LevelThreat)
            //public static double getHierarchyLevelThreat(Territory terr, int hierarchyLevelCounter, double levelThreat)
    {
        double tmpLevelThreat = LevelThreat;
        //double levelThreat = 0;
        //double tmpLevelThreat = 0;
      
        //int tmpLevelCounter = hierarchyLevelCounter;
        //tmpLevelCounter++;
            
        
        //levelThreat += 1/tmpLevelCounter;
        //levelThreat += 1/hierarchyLevelCounter;
        
        //tmpLevelThreat = 100;
        //tmpLevelThreat[0] += 1/hierarchyLevelCounter;
//      System.out.println("Threat Before" + tmpLevelThreat);
        //Bag hierarchy = new Bag();
        Bag subs = terr.getSubordinates();
        //hierarchy.addAll(subs);
        
        //if ((!subs.isEmpty()) || (hierarchyLevelCounter > 0))
        //{    
            hierarchyLevelCounter++;
            tmpLevelThreat += hierarchyLevelCounter;
        //}
        //else 
            //tmpLevelThreat += 0;
        
            
            
                    
        for (int i=0; i < subs.numObjs; i++) 
        {
            //levelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter);
            //tmpLevelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, levelThreat);
            //tmpLevelThreat += getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter);
            //tmpLevelThreat[0] += 1/getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, tmpLevelThreat);
            tmpLevelThreat = getHierarchyLevelThreat((Territory)subs.get(i), hierarchyLevelCounter, tmpLevelThreat);
            //hierarchy.addAll( getSubordinateHierarchy((Territory)subs.get(i)) );
          
        }
      
//        System.out.println("hierarchyLevelCounter " + hierarchyLevelCounter);
//        System.out.println("Threat After" + tmpLevelThreat);
        //return levelThreat;
        //return tmpLevelThreat;
        //return hierarchyLevelCounter;
        //return 1/hierarchyLevelCounter;
        return tmpLevelThreat;
  }    
    
    
    public static double calcuHierarchyLevel(Territory currTerritory) 
    {
        int tmpHierarchyLevelCounter = 0;
        double tmpLevelThreat = 0;
        //double[] tmpLevelThreat = new double[1];                
            
        if (!currTerritory.getSubordinates().isEmpty())                            
            return getHierarchyLevel(currTerritory, tmpHierarchyLevelCounter, tmpLevelThreat);                        
        else
            return 0;        
              
    }         
           
    public static Bag getTargetTerritories(Territory currTerritory)
    {
        Bag targetTerritories = new Bag();
        Bag tmpTerritories = new Bag();
        
        // the set of target territories is a union of all direct superiors along the hierarchy...
        targetTerritories.addAll(StrategyScouting.getHierarchicalSuperiorsTerritories(currTerritory));
        
        // plus all (direct or indirect) subordinates (only a head of a hierarchy can attack its (direct or indirect) subordinates)...        
        if (currTerritory.getSuperior() == null)
            targetTerritories.addAll(StrategyScouting.getHierarchicalSubordinatesTerritories(currTerritory));
        
        // plus (not only physical) neighbors
        tmpTerritories = currTerritory.getNeighbors();
                
        for (Object tr: tmpTerritories)
        {    
            // No duplicate & Not attack again my own direct territories
            if ((!targetTerritories.contains((Territory)tr)) && (!currTerritory.getSubordinates().contains((Territory)tr)))
                targetTerritories.add((Territory)tr);
        }                                    
                       
        return targetTerritories;
    }   
    
      
    public static Territory getHead(Territory currTerritory)
    {
        //Territory directSuperior = currTerritory.getSuperior();
        
        //if (directSuperior == null)
        if (currTerritory.getSuperior() == null)    
            return currTerritory;
        else
            //directSuperior = getHead(directSuperior);
            return getHead(currTerritory.getSuperior());                            
    }     
 
    // Check if a territory is in the same hierarchical structure of the current one.
    // Check if target is in source's hierarchy
    // Taken from non-public method
    public static boolean isSameHierarchy(Territory currTerritory, Territory targetTerritory)
    {
        if (StrategyScouting.getHead(currTerritory).equals(StrategyScouting.getHead(targetTerritory)))
            return true;
        else 
            return false;
    }     
    
    public static Bag getSameOrNotHierarchy(Territory currTerritory, Bag candidateTerritories, boolean flagSame)
    {
        Bag sameOrNotHierarchyTerritories = new Bag();
        
        for (Object tr: candidateTerritories)
            if (flagSame == true)
                if (StrategyScouting.isSameHierarchy(currTerritory,(Territory)tr))
                    sameOrNotHierarchyTerritories.add((Territory)tr);
            else
                if (!StrategyScouting.isSameHierarchy(currTerritory,(Territory)tr))
                    sameOrNotHierarchyTerritories.add((Territory)tr);     
        
        return sameOrNotHierarchyTerritories;
    }     
    
    public static Bag getSameTypeTerritories(Territory currTerritory, Bag candidateTerritories)
    {
        Bag sameTypeTerritories = new Bag();
        
        for (Object tr: candidateTerritories)
            if (((Territory)tr).getType() == currTerritory.getType())
                sameTypeTerritories.add((Territory)tr);
        
        return sameTypeTerritories;
    }        
    
    public static boolean isHierarchySubHasSameType(Territory currTerritory, Territory targetTerritory)
    {
        //Bag otherNeighborTerritories = new Bag();
        
        if (!(StrategyScouting.getSameTypeTerritories(currTerritory, StrategyScouting.getHierarchicalSubordinatesTerritories(targetTerritory))).isEmpty())
            return true;        
        else
            return false;
    }        
    
    public static boolean isNeighborHasSameType(Territory currTerritory, Territory targetTerritory)
    {
        //Bag otherNeighborTerritories = new Bag();
        
        
        if (!(StrategyScouting.getSameTypeTerritories(currTerritory, targetTerritory.getNeighbors())).isEmpty())
            return true;        
        else
            return false;
    }        
    
    /*
     * Returns the amount of soldiers a territory has + can make with their current resources
     */
    public static double getImmediateArmy(Territory currTerritory)
    {
        //return terr.getSoldiers() + cobbDoug(terr.getNatRes(), terr.getPeasants(), terr.getAlpha());
        return currTerritory.getSoldiers() + (Math.pow(currTerritory.getNatRes(),currTerritory.getAlpha()) * Math.pow(currTerritory.getPeasants(),(1 - currTerritory.getAlpha())) );
    }    
    

    /*
     * For a given territory, determine their resource value and
     * potential value projected 30 ticks into future.
     */
    public static double getPotentialArmy(Territory currTerritory){
        return getImmediateArmy(currTerritory) + ( Math.pow(currTerritory.getFoodGrowth(),currTerritory.getAlpha()) * Math.pow(currTerritory.getFarmGrowth(), currTerritory.getAlpha()) * 30 );        
    } 

    /*
     * Evaluates the territory's value, and the potential tax from all of its subordinates as well.
     */
    public static double getPotentialHierarchicalResourceForArmy(Territory currTerritory)
    {
        double terrValue = 0.0;
        
        Bag hierarchicalSubordinates = getHierarchicalSubordinatesTerritories(currTerritory);
        
        //for(int i=0; i<hierarchicalSubordinates.numObjs; i++)
        for(Object tr: hierarchicalSubordinates)
        {            
            terrValue += getPotentialHierarchicalResourceForArmy((Territory)tr) * 0.5; // assume 50% tax rate
        }
        
        terrValue += getPotentialArmy(currTerritory);
        
        return terrValue;
    }    
    
    /*
     * For a given territory, determine their resource value and
     * potential value projected 30 ticks into future.
     */
    public static double getPotentialResource(Territory currTerritory){
        return ( Math.pow(currTerritory.getFoodGrowth(),currTerritory.getAlpha()) * Math.pow(currTerritory.getFarmGrowth(), currTerritory.getAlpha()) * 30 );        
    } 

    /*
     * Evaluates the territory's value, and the potential tax from all of its subordinates as well.
     */
    public static double getPotentialHierarchicalResource(Territory currTerritory)
    {
        double terrValue = 0.0;
        
        Bag hierarchicalSubordinates = getHierarchicalSubordinatesTerritories(currTerritory);
        
        //for(int i=0; i<hierarchicalSubordinates.numObjs; i++)
        for(Object tr: hierarchicalSubordinates)
        {            
            terrValue += getPotentialHierarchicalResource((Territory)tr) * 0.5; // assume 50% tax rate
        }
        
        terrValue += getPotentialResource(currTerritory);
        
        return terrValue;
    }        
    
    public static HashMap<Territory, Double> calcuTargetAttractiveness(Bag candidateTerritories, double[] betas)
    {
        double attractiveness = 0;
        
        double sumInitPhyNeighborValue = 0;
        double sumHierThreatValue = 0;
        double sumHierResourceValue = 0;
        double sumHierLevel = 0;
        
        double maxInitPhyNeighborValue = 0;
        double maxHierThreatValue = 0;
        double maxHierResourceValue = 0;
        double maxHierLevel = 0;        
        
        HashMap<Territory, Double> attractivenessMap = new HashMap<Territory,Double>();                
        
        for(Object tr: candidateTerritories)
        {         
            if (initNumPhyNeighbor.get((Territory)tr) > maxInitPhyNeighborValue)
                maxInitPhyNeighborValue = initNumPhyNeighbor.get((Territory)tr);
            
            if (calcuHierarchyLevelThreat((Territory)tr) > maxHierThreatValue)
                maxHierThreatValue = calcuHierarchyLevelThreat((Territory)tr);
            
            if (getPotentialHierarchicalResource((Territory)tr) > maxHierResourceValue)
                maxHierResourceValue = getPotentialHierarchicalResource((Territory)tr);                            
            
            if (calcuHierarchyLevel((Territory)tr) > maxHierLevel)
                maxHierLevel = calcuHierarchyLevel((Territory)tr);
        }
        
        for(Object tr: candidateTerritories)
        {                                 
            sumInitPhyNeighborValue += (maxInitPhyNeighborValue - initNumPhyNeighbor.get((Territory)tr));
            sumHierThreatValue += (maxHierThreatValue - calcuHierarchyLevelThreat((Territory)tr));
            sumHierResourceValue += getPotentialHierarchicalResource((Territory)tr);        
            sumHierLevel += (maxHierLevel - calcuHierarchyLevel((Territory)tr));
        }
                         
        
        System.out.print("Beta 1: " + betas[0] + " Beta 2: " + betas[1] + " Beta 3: " + betas[2] + "Beta 4: " + betas[3]);
        
        //for(int i=0; i<candidateTerritories.numObjs; i++)
        for(Object tr: candidateTerritories)
        {                  
            // normalize attractiveness
            attractiveness = Math.pow((maxInitPhyNeighborValue - initNumPhyNeighbor.get((Territory)tr) + 1)/(sumInitPhyNeighborValue + 1), betas[0]) 
                    * Math.pow((maxHierThreatValue - calcuHierarchyLevelThreat((Territory)tr) + 1)/(sumHierThreatValue + 1), betas[1])
                    * Math.pow((getPotentialHierarchicalResource((Territory)tr) + 1)/(sumHierResourceValue + 1), betas[2])
                    * Math.pow((maxHierLevel - calcuHierarchyLevel((Territory)tr) + 1)/(sumHierLevel + 1), betas[3]);                    
            
            attractivenessMap.put((Territory)tr, attractiveness);
        }
        
        return attractivenessMap;
    }   
                
    
    public static boolean BetterPosition(Territory currTerritory, Territory targetTerritory, double expectLeastWinningChance)
    {        
        if ((getImmediateArmy(currTerritory) / (getImmediateArmy(currTerritory) + getImmediateArmy(targetTerritory))) >= expectLeastWinningChance)
            return true;
        else        
            return false;
    }        
    
    /*
     * Evaluates potential attackers, returns the soldier availability for this territory.
     * If positive, soldiers are available above threshold for trade/resource realloc
     * If negative, this territory needs soldiers/resources to be able to win against strongest
     * opponent with opponentWinPotentialThreshold chance of winning.
     */
    public static double soldierDifficiency(Territory currTerritory, Territory targetTerritory, double opponentWinPotentialThreshold)
    {
        double numDifficiency = 0.0;
        // get most likely attackers sorted by liklihood of attack
        //HashMap<Territory, Double> attackers = likelyAttackers(currTerritory);
        //if(attackers.size() > 0){
            // No one has > oWPThreshold chance of beating me, so I am free to lend resources
            //Territory mostLikelyAttacker = attackers.keySet().toArray(new Territory [attackers.keySet().size()])[0]; // highest risk attacker
            // Determine resource availability
            double opponentPotentialSoldiers = getImmediateArmy(targetTerritory);
            // I never want a potential attacker to have greater than oWPThreshold chance to beat me
            double requiredSoldiers = (opponentPotentialSoldiers/opponentWinPotentialThreshold)-opponentPotentialSoldiers;
            numDifficiency = getImmediateArmy(currTerritory) - requiredSoldiers;
        //}
        return numDifficiency;
    }    
    
    
//========================    

    /*
     * Returns the potential for the attacker to win in range [0,1].
     * Determines the winning chances if all resources from both territories are used to make soldiers.
     */
    public static double getWinPotential(Territory attacker, Territory defender){
        double attackerPotentialSoldiers = getImmediateArmy(attacker);
        double defenderPotentialSoldiers = getImmediateArmy(defender);
        return attackerPotentialSoldiers/(attackerPotentialSoldiers+defenderPotentialSoldiers);
    }

    public static double getWinPotential(double soldiersAttacking, Territory defender){
        double defenderPotentialSoldiers = getImmediateArmy(defender);
        return soldiersAttacking/(soldiersAttacking+defenderPotentialSoldiers);
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
    
//    
//    /*
//     * Get all territories that can attack target
//     */
//    public static Bag potentialAttackers(Territory target){
//        Bag attackers = new Bag();
//        Bag allTerritories = new Bag();
//        getAllTerritories(target, allTerritories);
//        for(int i=0; i<allTerritories.numObjs; i++){
//            Territory curTerr = (Territory)allTerritories.get(i);
//            if( attackableTerritories(curTerr).contains(target) ){ // "if the territories curTerr can attack include target"
//                attackers.add(curTerr);
//            }
//        }
//        return attackers;
//    }  
    

//    /*
//     * Determine which attackers are likely to attack.
//     * Returns a sorted (desc by attack potential) map of all territories that can attack 'terr'
//     * along with their attack potentials.
//     */
//    public static HashMap<Territory,Double> likelyAttackers(Territory terr){
//        //Bag likely = new Bag();
//        HashMap<Territory,Double> likely = new HashMap<Territory,Double>();
//        Bag potential = potentialAttackers(terr);
//        for(int i=0; i<potential.numObjs; i++){
//            Territory attacker = (Territory)potential.get(i);
//            double winPot = getWinPotential(attacker, terr);
//            //if( winPot > 0.5 ){
//            likely.put(attacker, winPot);
//            //}
//        }
//        return sortByValueDescending(likely);
//    }    
    
}
