/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agents;

import java.util.Random;
import java.util.*;
import java.util.ArrayList;
import risk.*;
import sim.util.Bag;
import risk.Imperial;
import risk.HierarchiesGraph;
import risk.Territory;

/**
 *
 * @author cmetgher
 */
public class ChuckNorris extends Agent
{
    // the territories with the same type as me, me included
    private static Bag myTerritories = new Bag();        

    public ChuckNorris(int id, int type)
    {
        super(id, type);
        this.empireName = "Chuck Norris";        
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
    protected void trade()
    {
        double alpha = myTerritory.getAlpha();
        double farmGrowth = myTerritory.getFarmGrowth();
        double foodGrowth = myTerritory.getFoodGrowth();

        double soldiers = myTerritory.getSoldiers();
        double peasants = myTerritory.getPeasants();
        double natRes = myTerritory.getNatRes();

        // trade only if your soldiers risk to die from stavation
        if (natRes < soldiers)
        {
            int index = -1;
            double diff = soldiers - natRes;
            //Territory terr = null;
            for (Object o: myTerritory.getNeighbors())
            {
                Territory terr = (Territory)o;
                if ( soldiers - diff > terr.getSoldiers() + diff);
                    index = terr.getId();
            }
            if (index > 0)
            {
                trade [0] = index;
                trade [1] = 1;
                trade [2] = diff/2 * Math.min(Math.max(alpha, 1-alpha)/Math.min(alpha, 1-alpha), 1.5);
                trade [3] = 3;
                trade [4] = diff/2;
                return;
            }
        }        
    }

    /*
    * Should update acceptTrade.
    */
    @Override
    protected void acceptTrade(
            Territory offerer,
            double demand,
            int typeDemand,
            double offer,
            int typeOffer)
    {

        acceptTrade = false;

        double alpha = myTerritory.getAlpha();
        double farmGrowth = myTerritory.getFarmGrowth();
        double foodGrowth = myTerritory.getFoodGrowth();

        double soldiers = myTerritory.getSoldiers();
        double peasants = myTerritory.getPeasants();
        double natRes = myTerritory.getNatRes();

        switch(typeOffer)
        {
            case 1: // nat res offered
                if (typeDemand == 2) // peasants demanded
                {
                    if (farmGrowth > foodGrowth) // possibly will give peasants only if they grow faster
                    {
                        if( offer > demand || (foodGrowth * offer)/(farmGrowth * demand) > 1)
                        {
                            acceptTrade = true;
                        }
                    }
                }
                else
                if (typeDemand == 3) // soldiers demanded
                {
                    if (soldiers>natRes)
                    {
                        double diff = offer - demand;
                        double coeff = (alpha >= 0.4 && alpha <= 0.6) ? Math.max(alpha, 1-alpha)/Math.min(alpha, 1-alpha) : 1.5;
                        if (diff > (soldiers - natRes)* coeff)
                        {
                            acceptTrade = true;
                        }
                    }
                }
                break;
            case 2: // peasants offered
                if (typeDemand == 1)
                {
                    //give resources only if there are lots of them
                    // and peasantGrowth is much less than resources growth
                    if (natRes > (soldiers + peasants) * 6)
                    {
                        if( offer/demand > 1.5 || (farmGrowth * offer)/(foodGrowth * demand) > 1)
                        {
                            acceptTrade = true;
                        }
                    }
                }
                if (typeDemand == 3) // soldiers demanded
                {
                    if (soldiers>natRes)
                    {
                        double diff = offer - demand;
                        double coeff = (alpha >= 0.4 && alpha <= 0.6) ? Math.max(alpha, 1-alpha)/Math.min(alpha, 1-alpha) : 1.5;
                        if (diff > (soldiers - natRes)* coeff)
                        {
                            acceptTrade = true;
                        }
                    }
                }
                break;
            case 3: // soldiers offered
                if (soldiers < natRes)
                {
                    double diff = demand - offer;
                    double coeff = (alpha >= 0.4 && alpha <= 0.6) ? Math.max(alpha, 1-alpha)/Math.min(alpha, 1-alpha) : 1.5;
                    if (diff < (natRes - soldiers)* coeff)
                    {
                        acceptTrade = true;
                    }
                }                
                break;
            default: acceptTrade = false;
        }

    }

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax()
    {
        
        if (! myTerritories.contains(myTerritory))
        {
            myTerritories.add(myTerritory);
        }

        if (this.myTerritory.getSubordinates().numObjs < 20)
            tax = 0.5;
        else if (this.myTerritory.getSubordinates().numObjs < 35)
            tax = 0.4;
        else
            tax = 0.35;
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries()
    {
        beneficiaries.clear();

        double mySoldiers = myTerritory.getSoldiers();
        myTerritory.produceSoldiers(myTerritory.getNatRes()-mySoldiers, myTerritory.getPeasants());


        double soldiersToBeRetributed = myTerritory.getSoldiers() - mySoldiers;

        // if no more soldiers were produced, retribute what is in excess compared to resources
        if (soldiersToBeRetributed == 0)
        {
            soldiersToBeRetributed = myTerritory.getSoldiers() - myTerritory.getNatRes();
        }

        if (soldiersToBeRetributed <= 0)
            return;




        for (Object o : myTerritories)
        {
            if (((Territory)o).getId() != myTerritory.getId())
                    beneficiaries.add(o);
        }

        retributions = new double[beneficiaries.numObjs];
        for(int i =0; i< beneficiaries.numObjs;i++)
        {
            retributions[i] = soldiersToBeRetributed/beneficiaries.numObjs;
        }                 
    }

    /*
     * Gets an array with Ids of the lords of my type
     */
    private ArrayList<Integer> getMyIds()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i< myTerritories.numObjs; i++)
        {
            list.add(((Territory)myTerritories.get(i)).getId());
        }
        return list;
    }

    /*
     * Selects the weakest target from the neighbors
     */
    private Territory selectWeakestTarget()
    {
        Territory potentialTarget = null;
        double weakest = Double.MAX_VALUE;

        
        if (myTerritory.getNeighbors().numObjs>0)
        {
            for (Object o: myTerritory.getNeighbors())
            {             
                Territory neighbor = (Territory)o;

                if (neighbor.getType() == myTerritory.getType())
                {
                    continue; // do not attak territories with under my own "dictatorship"
                }


                Territory superior = neighbor.getSuperior();
                if (superior != null && getMyIds().contains(superior.getId()))
                {
                    continue; //it's under Lord's territory already
                }

                // maximum possible opposition
                double maximumPower = computePower(neighbor);
                if (maximumPower < weakest)
                {
                    weakest = maximumPower;
                    potentialTarget = neighbor;
                }
                
            }
        }
        return potentialTarget;
    }

    private double computePower(Territory t)
    {
        return t.getSoldiers() +
                                Math.pow(t.getNatRes(),t.getAlpha()) *
                                Math.pow(t.getPeasants(), 1- t.getAlpha());
    }



    /*
     * Returns the territory with most subordinates number that can be attacked with the specified number of soldiers
     * and expected to win.
     * If computePower is false, then just return the Territory with most subordinates
     * If checkType is false, then return don't check the Type of the superior territory.
     */
    private Territory selectTargetBySubordinates(double mySoldiers, boolean computePower, boolean checkType)
    {
        Territory t = null;
        int subordinatesCount = 0;
        for (Object o: myTerritory.getNeighbors())
        {
            Territory terr = (Territory)o;
            Territory superior = terr.getSuperior();
            double power = computePower? computePower(terr): 0.00001;



            if (checkType &&
                    (terr.getType() == myTerritory.getType()
                    || (superior != null) && superior.getType() == myTerritory.getType()))
            {
                continue;
            }

            if (power < mySoldiers && mySoldiers/power > 1.5)
            {
                if (terr.getSubordinates().numObjs > subordinatesCount)// && terr.getSubordinates().numObjs < subordinatesNumber )
                {
                    subordinatesCount = terr.getSubordinates().numObjs;
                    t = terr;
                }
            }
        }
        return t;
    }

    /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */
    @Override
    protected void attack()
    {

        attackedTerritoryID = 0;
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getSoldiers();       

        if (myTerritory.getSubordinates().numObjs < 3) // while we have not many subordinates attack the weakest
        {
            Territory weakestTarget = selectWeakestTarget();

            // if it is powerfull enough search a victim through its subordinates
            if (weakestTarget != null)
            {
                double power = computePower(weakestTarget);
                if (power < attackingSoldiers && attackingSoldiers/power > 1.5)
                {
                    attackedTerritoryID = weakestTarget.getId();
                }
            }
        }else // if we are strong enoug to attack more powerfull territories then try to
        {
            Territory target = selectTargetBySubordinates(attackingSoldiers, true, true);
            if (target != null)
            {
                attackedTerritoryID = target.getId();
            }
        }

        // if nothing selected try to attack the territory which has most subordinates
        // disregarding the number of soldiers it has, just take care not to attack allies yet
        if (attackedTerritoryID == 0)
        {
            Territory target = selectTargetBySubordinates(attackingSoldiers, false, true);
            if (target != null)
            {
                attackedTerritoryID = target.getId();
            }
        }

        Bag neighbors = myTerritory.getNeighbors();       

        // if there is still no choice then try to pick randomly a nighbor that is not rulled by the current Lord
        if (attackedTerritoryID == 0)
        {
            int tries = 0;
            while(tries < neighbors.numObjs && attackedTerritoryID == 0)
            {
                tries ++;
                int index = new Random().nextInt(neighbors.numObjs);
                Territory neighbor = (Territory)neighbors.get(index);
                Territory superior = neighbor.getSuperior();
                if (superior != null && superior.getType() == myTerritory.getType() )
                {
                    continue; //it's under Lord's territory already
                }

                attackedTerritoryID = neighbor.getId();                
            }            
        }


        // if even now has no territory to attack, attack first attackable
        if (attackedTerritoryID == 0)
        {
            for (int i =0; i< myTerritory.getNeighbors().numObjs; i++)
            {
                Territory territory = (Territory)myTerritory.getNeighbors().get(i);
                Territory superior = territory.getSuperior();
                if (superior != null && superior.getType() == myTerritory.getType() )
                {
                    continue; //it's under Lord's territory already
                }
                attackedTerritoryID = territory.getId();
            }
        }

        // if even now has no territory to attack, attack first an ally, but not the overall ruler
        // or attack the superior
        if (attackedTerritoryID == 0)
        {
            Territory mySuperior = myTerritory.getSuperior();

            if (mySuperior != null)
            {
                if (mySuperior.getType() != myTerritory.getType())
                {
                    attackedTerritoryID = mySuperior.getId();
                }else //if (mySuperior.getType() == myTerritory.getType())
                {
                    for (int i =0; i< myTerritory.getNeighbors().numObjs; i++)
                    {
                        Territory territory = (Territory)myTerritory.getNeighbors().get(i);
                        Territory superior = territory.getSuperior();
                        if (superior != null && superior.getId() == myTerritory.getId() )
                        {
                            continue; //it's under Lord's territory already
                        }

                        attackedTerritoryID = territory.getId();
                        break;
                    }
                }
            }else
            {
                attackedTerritoryID = new Random().nextInt(myTerritory.getNeighbors().numObjs);
            }
        }


        // if all territories are under my control attack random
        if (myTerritory.getSubordinates().numObjs >= 41)
        {
            int index = new Random().nextInt(myTerritory.getNeighbors().numObjs);
            attackedTerritoryID = ((Territory)myTerritory.getNeighbors().get(index)).getId();
        }

    }

    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attaker, double soldiersAttacking)
    {

        // do not fight against your type
        // it's the natural state of things
        if (attaker.getType() == myTerritory.getType())
        {
            defendingSoldiers = 0;
        }else
        {
            myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
            defendingSoldiers = myTerritory.getSoldiers();
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

        if(youWon)
        {
            String additionalText = "";
            if (deffenderID == myTerritory.getId())
            {
                additionalText = " defending";
            }
            else
            {
                additionalText = " attacking";
            }

            System.out.println("Victory "+additionalText+ ": time="+ period + "; attackerId =" + attackerID +
             		"; soldiersAttak = "+ soldiersAttack+ "; defender=" + deffenderID +"; defenderSoldiers=" +defendingSoldiers );
        }
        else
        {
            String additionalText = "";
            if (deffenderID == myTerritory.getId())
            {
                additionalText = " defending";
            }else
            {
                additionalText = " attacking";
            }

            System.out.println("Defeat"+additionalText+": time="+ period + "; attackerId =" + attackerID +
             		"; soldiersAttak = "+ soldiersAttack+ "; defender=" + deffenderID +"; defenderSoldiers=" +defendingSoldiers );
        }          
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

        trade = new double[5];

        if(tradeCompleted)
        {            
            System.out.println("Traded: time="+ period + "; proposer =" + proposerID);
        }
        else
        {
            System.out.println("NotTraded: time="+ period + "; proposer =" + proposerID);
        }

    }

}
