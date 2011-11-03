package agents;

import java.util.*;
import risk.*;

/**
 *
 * @author John Bjorn Nelson
 *
 */

public class Guevara extends Agent{
    private double attackX, attackOffset;
    private static Random rng = new Random();
    public static Set<Guevara> compatriots = new HashSet<Guevara>();

    public Guevara(int id, int type){
        super(id, type);
        empireName = "GuerrillaRadio";
        
        addThisToCompatriots();
        attackX = 0;
        attackOffset = rng.nextFloat() * 10.0;
    }

    /**
     * Just level resources randomly with my compatriots. 
     */
    @Override
    public void trade(){
        // You cannot trade with non-neighbors, contrary to my original intent.
        // However, if you have a neighbor that is a compatriot, you have an
        // anchoring advantage. Granted, this advantage only exists once
        // every other game or three, but it should be strong. Therefore,
        // if the compatriot neighbor exists, level your resources with that
        // neighbor.
        //
        // Initially, I was only trading with my compatriot if they were not
        // compromised. However, the Imperial class conducts trading immediately
        // prior to waging war, therefore, my compatriots can still expoit
        // transfered resources that will not be taxed by their imperialist
        // oppressors.
        Guevara partner = randomFrom(getGuevaraNeighborsAsSet());
        if(partner == null) return;
        
        trade[0] = partner.getId();
        trade[1] = 1; trade[2] = 0; trade[3] = 1; trade[4] = 0;

        double[] sums = {
            myTerritory.getNatRes() + partner.getTerritory().getNatRes(),
            myTerritory.getPeasants() + partner.getTerritory().getPeasants(),
            myTerritory.getSoldiers() + partner.getTerritory().getSoldiers()
        };
        
        double[] ratios = {
            myTerritory.getNatRes()   / sums[0],
            myTerritory.getPeasants() / sums[1],
            myTerritory.getSoldiers() / sums[2]
        };

        // Demand item my compatriot has more of.
        for(int i=0; i<3; ++i){
            if(ratios[i] < 0.5){ // I need this
                trade[1] = i+1;
                trade[2] = (0.5 - ratios[i]) * sums[i];
            }
        }

        // Supply item I have more of.
        for(int i=0; i<3; ++i){
            if(ratios[i] > 0.5){ // I have too much of this.
                trade[3] = i+1;
                trade[4] = (ratios[i] - 0.5) * sums[i];
            }
        }

    }

    

    /**
     * A compatriot would never offer a bad deal. That is a capitalist pig
     * tendency. Always accept trades from a compatriot. Always reject all
     * other trades, even if it came from a territory now under our control.
     * It's probably a trap!
     */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
        acceptTrade = isCompatriotId(offerer.getId());
    }

    /**
     * Never surrender. If we must die, let's die defending Guevara!
     */
    @Override
    public void defend(Territory attacker, double soldiersAttacking){
        defendingSoldiers = myTerritory.getSoldiers();
    }

    @Override
    public void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        
    }

    /**
     * Tax all subordinates at maximum rate. All revenue belongs to the state
     * and must be redistributed equally amongst all beneficiaries of Guevara's
     * benevolence.
     */
    @Override
    protected void chooseTax(){ tax = 0.5; }

    /**
     * Never arm subordinates.
     */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        beneficiaries.clear();
    }

    /**
     * This function should serve to frustrate most ML efforts.
     *
     * @return a value between 0 and 1.
     */
    private double getXAttackGuard(){
        attackX += 0.03;
        return 1.0 - (Math.sin(attackOffset + attackX)+1.0)/2.0;
    }
    
    @Override
    public void attack(){
        double attackableProbability = getXAttackGuard();
        //System.out.print(attackableProbability + " ");
        if(attackableProbability < rng.nextFloat()){
            // No reason to allow our oppressors to take more of our resources.
            // If we are compromised, scorch the earth and fight an unwinnable
            // war for the sake of the revolution.
            if(this.isCompromised()){
                myTerritory.produceSoldiers(myTerritory.getNatRes(),
                    myTerritory.getPeasants());
                attackingSoldiers = myTerritory.getSoldiers();
                attackedTerritoryID = myTerritory.getSuperior().getId();
            }
            return;
        }

        // The attack guard has been by-passed. Take opportunities for attack.
        
        Integer bestTarget = null;
        double bestScore = 1;

        double potentialSoldiers = computePotentialSoldiers();
        if(potentialSoldiers < 1.0) return;

        // Attack neighbors who
        //
        // * are not of the same type
        // * are not already controlled
        // * are unprotected by soldiers
        //   or
        //   have the highest likelyhood of subjegation
        for(Object ter : myTerritory.getNeighbors()){
            Territory potentialTarget = (Territory)ter;
            double score = 0.0;

            // No need to attack already liberated territories.
            if(potentialTarget.getType() == myTerritory.getType())
                continue;

            if(potentialTarget.getSoldiers() == 0){
                score = 100.0 * potentialTarget.getNatRes();
            }else{
                score = Math.min(50, 
                  potentialSoldiers / potentialTarget.getSoldiers());
            }

            if(score > bestScore){
                bestScore = score;
                bestTarget = potentialTarget.getId();
            }
        }
        
        if(bestTarget != null){
            // Attack with as much force as possible.
            myTerritory.produceSoldiers(myTerritory.getNatRes(),
                myTerritory.getPeasants());
            attackingSoldiers = myTerritory.getSoldiers();
            attackedTerritoryID = bestTarget;
        }
        //print(compatriots);
         
    }

    protected Guevara randomFrom(Set<Guevara> set){
        if(set.isEmpty()) return null;
        return (Guevara)set.toArray()[(new Random()).nextInt(set.size())];
    }

    protected boolean isSubordinate(){
        return myTerritory.getSuperior() != null;
    }

    private double computePotentialSoldiers(){
        return myTerritory.getSoldiers() + 
            Math.pow(myTerritory.getNatRes(), myTerritory.getAlpha()) *
            Math.pow(myTerritory.getPeasants(), 1 -  myTerritory.getAlpha());
    }

    /**
     * Liberated from all tyrants, not us, of course.
     */
    private Set<Guevara> getLiberatedCompatriots(){
        Set<Guevara> liberatedCompatriots = new HashSet<Guevara>();
        for(Guevara compatriot : compatriots){
            if(compatriot.isLiberated())
                liberatedCompatriots.add(compatriot);
        }
        return liberatedCompatriots;
    }

    /**
     * As in, does this territory have a superior.
     */
    private boolean isLiberated(){
        return myTerritory.getSuperior() == null;
    }

    private boolean isCompromised(){
        return !isLiberated();
    }

    private int neighborOffsetFromId(int id){
        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
        for(int i=0; i < myTerritory.getNeighbors().numObjs; ++i)
            if(((Territory)myTerritory.getNeighbors().toArray()[i]).getId() == id)
                return i+1;
        return 1;
    }
    /**
     * Is the ID recognized as a compatriot.
     */
    private boolean isCompatriotId(int id){
        for(Guevara compatriot : compatriots)
            if(id == compatriot.getId())
                return true;
        return false;
    }

    /**
     * Print a set of Guevara's.
     */
    private void print(Set<Guevara> set){
        for(Guevara compatriot : set){
            System.out.print(compatriot.getId() + " ");
        }
        System.out.println("");
    }

    /**
     * This library really doesn't conform to the PoLS. For some reason,
     * the Guevara class is being instantiated 11 times and resulting in
     * 10 non-equal classes. I suspect usage of Java's ugly clone semantics,
     * probably related to loggging of scores. I don't feel like untangling it,
     * so I'll add this convoluted addToCompatriots method.
     */
    private void addThisToCompatriots(){
        // I think ID < 7 means something other than the game?
        if(this.getId() < 7) return;

        for(Guevara compatriot : compatriots)
            if(compatriot.getId() == this.getId())
                return;
        compatriots.add(this);
    }

    private Guevara getCompatriotById(int id){
        for(Guevara compatriot : compatriots)
            if(compatriot.getId() == id)
                return compatriot;
        return null;
    }

    private Set<Guevara> getGuevaraNeighborsAsSet(){
        Set<Guevara> results = new HashSet<Guevara>();
        for(Object x : myTerritory.getNeighbors()){
            Territory t = (Territory) x; // Wow, bags are ugly.
            if(t.getType() == this.getTax())
                results.add(getCompatriotById(t.getId()));
        }
        return results;

    }

    private Set<Guevara> excludeThis(Set<Guevara> set){
        Set<Guevara> result = new HashSet<Guevara>(set);
        result.remove(this);
        return result;
    }

    private Set<Guevara> intersection(Set<Guevara> a, Set<Guevara> b){
        Set<Guevara> result = new HashSet<Guevara>();
        for(Guevara x : a){
            if(b.contains(x))
                result.add(x);
        }
        return result;
    }

    private Territory getTerritory(){
        return myTerritory;
    }
}
