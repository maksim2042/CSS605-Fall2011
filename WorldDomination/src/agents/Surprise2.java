/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import risk.Agent;
import risk.Territory;
import risk.*;
import sim.util.Bag;
import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.*;



/**
 *
 * @author Maurice
 */
public class Surprise2 extends Agent {
       
    private Territory t;
    
    public Surprise2 (int id, int type){
        super(id,type);
        empireName="Surprise2";
    }

        //Discover all states in which my soldiers exceed theirs by at least 20%
        //and attack the one with the highest Natural Resources in that set.
        //if my soldiers do not exceed anyone's by 20%, re-run the process at 
        //15% (Just used 15% for now)       
        //While neither condition is met, defend with 100% of soldiers.   
    public Bag weakMilitary (Bag territories) {
            Bag weakStates = new Bag();
    
            double soldierCount = myTerritory.getSoldiers();
            if (territories != null) {
                for (int i = 0; i < (territories.numObjs); i++) {
                    t = (Territory)territories.get(i);
            
                    if ((!(t == null)) && (t.getSoldiers() < (soldierCount-t.getSoldiers())*.10)) {
                        weakStates.add(t);
                    }
                }
            }
            
            return weakStates;
         }
    
        @Override
    protected void attack(){
             System.out.println("begin Attack");
             myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
            // figure out weaker states
            Bag w = weakMilitary(myTerritory.getNeighbors());
            Bag s = new Bag(myTerritory.getSubordinates());
        //remove any of my subordinates from the attack list
        for (int i = 0; i < s.size(); i++) {
            if (w.contains(s.get(i))) {
                w.remove(s.get(i));
            }    
        // from the weak states, figure out who has the most 
        //    natural resources + peasants. Attack them.
        Territory WeakHegemon=null;            
        if (t != null) 
            {
            double Wealth = 0;       
            for (int j = 0; j<w.size(); j++){
                t=(Territory)w.get(j);
                double OpponentWealth=(t.getNatRes()+ t.getPeasants());        
               if (OpponentWealth>Wealth){
                   WeakHegemon=t;
               }
            }
        attackingSoldiers = myTerritory.getSoldiers() * 1.00;
        attackedTerritoryID = WeakHegemon.getId();

               }
        }
        }
    @Override
    protected void trade(){};

    /*
    * Should update acceptTrade.
    */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){}

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax(){}

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){};

    /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */


    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attaker, double soldiersAttacking){
     if (myTerritory.getSoldiers() > soldiersAttacking) {
        defendingSoldiers = myTerritory.getSoldiers();
      } else {  
         // If their military is superior, defend with 1% of soldiers
          defendingSoldiers = myTerritory.getSoldiers() * 0.05;
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
    }

}
