/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evoga;

import risk.*;
import sim.util.Bag;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author ckhyde
 */
public class AttackStrategy 
{
    protected Territory myTerritory;    
    public int targetSelectedID = -1;    
    
    public AttackStrategy(Territory myTerritory)
    {
        this.myTerritory = myTerritory;
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
    //public int selectTarget()
    public Territory selectTarget(double fightingThreshold, double[] betas)
    {
        targetSelectedID = -1;
        
        Bag candidateTerritories = new Bag();
        Bag tmpTerritories = new Bag();
        
        candidateTerritories = StrategyScouting.getTargetTerritories(myTerritory);
        HashMap<Territory, Double> attractivenessDescSorted;

        
//        if ((myTerritory.getSuperior() != null) && (myTerritory.getSuperior().getType() != myTerritory.getType()))
//                if (StrategyScouting.BetterPosition(myTerritory, myTerritory.getSuperior(), fightingThreshold))
//                    return myTerritory.getSuperior();
        
        
        // if attacker is a head...
        if (myTerritory.getSuperior() == null)
        {   
            // attacker can attack its subordinates, so that attack the subordinates of its own type
            tmpTerritories.addAll(StrategyScouting.getSameTypeTerritories(myTerritory, StrategyScouting.getSameOrNotHierarchy(myTerritory, candidateTerritories, true)));
            
            if (!tmpTerritories.isEmpty())
            {    
                attractivenessDescSorted = StrategyScouting.sortByValueDescending(StrategyScouting.calcuTargetAttractiveness(tmpTerritories, betas));
                //Territory targetTerritory = attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs])[0];
                for (Territory tr: attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs]))
                {    
                    if (StrategyScouting.BetterPosition(myTerritory, tr, fightingThreshold))
                    {
                        //this.targetSelectedID = targetTerritory.getId();                
                        System.out.println("Inner, Head, Same Type: " + tr.getId());                
                        //return tr.getId();    
                        return tr;    
                        //return targetTerritory.getId();
                    }    
                }                                            
                
                //this.targetSelectedID = targetTerritory.getId();                
                //System.out.println("Inner, Head, Same Type :" + targetTerritory.getId());                
                
            }            
            tmpTerritories.clear();
            
            
            // otherwise attack the other territories outside its own hierarchy but still of the same type
            tmpTerritories.addAll(StrategyScouting.getSameTypeTerritories(myTerritory, StrategyScouting.getSameOrNotHierarchy(myTerritory, candidateTerritories, false)));
            
            if (!tmpTerritories.isEmpty())
            {    
                attractivenessDescSorted = StrategyScouting.sortByValueDescending(StrategyScouting.calcuTargetAttractiveness(tmpTerritories, betas));
                //Territory targetTerritory = attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs])[0];
                for (Territory tr: attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs]))
                {    
                    if (StrategyScouting.BetterPosition(myTerritory, tr, fightingThreshold))
                    {
                        //this.targetSelectedID = targetTerritory.getId();                  
                        System.out.println("Outer, Head, Same Type :" + tr.getId());                
                        //return tr.getId();    
                        return tr;    
                        //return targetTerritory.getId();
                    }    
                }                                            
                
                //this.targetSelectedID = targetTerritory.getId();                
                //System.out.println("Inner, Head, Same Type :" + targetTerritory.getId());  
            }            
            tmpTerritories.clear();
            
            
            // otherwise attack the other territories outside its own hierarchy but has subordinates (not its own) of the same type
            tmpTerritories.addAll(StrategyScouting.getSameOrNotHierarchy(myTerritory, candidateTerritories, false));
            
            for (Object tr: tmpTerritories)               
                if (!StrategyScouting.isHierarchySubHasSameType(myTerritory, (Territory)tr))
                    tmpTerritories.remove((Territory)tr);
                                                
            if (!tmpTerritories.isEmpty())
            {    
                attractivenessDescSorted = StrategyScouting.sortByValueDescending(StrategyScouting.calcuTargetAttractiveness(tmpTerritories, betas));
                //Territory targetTerritory = attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs])[0];
                for (Territory tr: attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs]))
                {    
                    if (StrategyScouting.BetterPosition(myTerritory, tr, fightingThreshold))
                    {
                        //this.targetSelectedID = targetTerritory.getId();                
                        System.out.println("Outer, Head, Subs have Same Type: " + tr.getId());                
                        //return tr.getId();    
                        return tr;    
                        //return targetTerritory.getId();
                    }    
                }                                            
                
                //this.targetSelectedID = targetTerritory.getId();                
                //System.out.println("Inner, Head, Same Type :" + targetTerritory.getId());  
            }
            tmpTerritories.clear();
            
//            // otherwise attack the other territories outside its own hierarchy but has neighbors (neigher its own nor its subordinate) of the same type
//            tmpTerritories.addAll(StrategyScouting.getSameOrNotHierarchy(myTerritory, candidateTerritories, false));
//            
//            for (Object tr: tmpTerritories)               
//                if (!StrategyScouting.isNeighborHasSameType(myTerritory, (Territory)tr))
//                    tmpTerritories.remove((Territory)tr);
//                                                
//            if (!tmpTerritories.isEmpty())
//            {    
//                attractivenessDescSorted = StrategyScouting.sortByValueDescending(StrategyScouting.calcuTargetAttractiveness(tmpTerritories, betas));
//                //Territory targetTerritory = attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs])[0];
//                for (Territory tr: attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs]))
//                {    
//                    if (StrategyScouting.BetterPosition(myTerritory, tr, fightingThreshold))
//                    {
//                        //this.targetSelectedID = targetTerritory.getId();                 
//                        System.out.println("Outer, Head, Neighbors have Same Type: " + tr.getId());                
//                        //return tr.getId();    
//                        return tr;   
//                        //return targetTerritory.getId();
//                    }    
//                }                                            
//                
//                //this.targetSelectedID = targetTerritory.getId();                
//                //System.out.println("Inner, Head, Same Type :" + targetTerritory.getId());  
//            }
//            tmpTerritories.clear();
                
        }
        // if attacker is not a head...
        else
        {            
            
            // the rest completely depending on the attractiveness
            if (!candidateTerritories.isEmpty())
            {    
                attractivenessDescSorted = StrategyScouting.sortByValueDescending(StrategyScouting.calcuTargetAttractiveness(candidateTerritories, betas));
                //Territory targetTerritory = attractivenessDescSorted.keySet().toArray(new Territory[tmpTerritories.numObjs])[0];
                for (Territory tr: attractivenessDescSorted.keySet().toArray(new Territory[candidateTerritories.numObjs]))
                {    
                    if (StrategyScouting.BetterPosition(myTerritory, tr, fightingThreshold))
                    {
                        //this.targetSelectedID = targetTerritory.getId();                 
                        System.out.println("Whatever with larger attractiveness: " + tr.getId());                
                        //return tr.getId();    
                        return tr;   
                        //return targetTerritory.getId();
                    }    
                }                                            
                
                //this.targetSelectedID = targetTerritory.getId();                
                //System.out.println("Inner, Head, Same Type :" + targetTerritory.getId());  
            }
        }   
        
        // cannot find any feasible target
        return null;
        //return -1;                                  
    }    
}
