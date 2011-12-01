package agents;
import java.util.*;
import risk.*;
import sim.util.*;
import javax.swing.JOptionPane;
import java.util.Random.*;

/**
 *
 * @author Win Farrell
 */

public class DoctorNo extends Agent{
 
    private static Bag myTerritories = new Bag();
    final IntBag allHoldouts = new IntBag();
    final IntBag allBridges = new IntBag();
    final IntBag allMasses = new IntBag();
    static int[] arrayHoldouts = {41, 42, 34, 26, 11, 13};
    static int[] arrayBridges = {9,14,16,39,38,8,10,12,21};
    static int[] arrayMasses = {22,24,23,25,1,2,19,17,20,15}; // build rings of fire
   
    
    
    public DoctorNo(int id, int type){
        super(id, type);
        empireName = "DoctorNo";
         allHoldouts.addAll(0, arrayHoldouts);
         allBridges.addAll(0, arrayBridges);
         allMasses.addAll(0, arrayMasses);
         
    }
    
    
    
        public void sayDrNo()
        {
            JOptionPane.showMessageDialog(null,"Hello, World", "Greeter",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    
    
    @Override
    public void attack(){
        System.out.println("SPECTER"); //The Americans are fools. I offered my services, they refused. So did the East. Now they can both pay for their mistake. ");
        // Attack bridges and masses -- but defend holdouts!!
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        Bag territories = getVulnerableTerritories(getNeighborsToAttack());
        IntBag holdouts = new IntBag();
        IntBag bridges = new IntBag();
        IntBag masses = new IntBag();
        double targetWealth = 0; 
        
        if (territories.numObjs != 0) 
        {   
            // Attack the weakest neighbor with three-quarters (variable) of my soldiers
            attackingSoldiers = myTerritory.getSoldiers() * 0.85;
            for (int i =0; i< territories.numObjs; i++) {
               Territory t = (Territory) territories.get(i);
               if (allHoldouts.contains(t.getId())) {
                   holdouts.add(t.getId());
               }
               else if (allBridges.contains(t.getId())) {
                   bridges.add(t.getId());
               }
               else if (allMasses.contains(t.getId())) {
                   masses.add(t.getId());
               }
            }
            Random rand = new Random();
            if (bridges.numObjs > 0) {
               attackedTerritoryID = bridges.get(rand.nextInt(bridges.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " attacking a bridge: " + attackedTerritoryID); 
            }
            else if (holdouts.numObjs > 0) {
               attackedTerritoryID = holdouts.get(rand.nextInt(holdouts.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " attacking a holdout: " + attackedTerritoryID);
            }
            else if (masses.numObjs > 0) {
               attackedTerritoryID = masses.get(rand.nextInt(masses.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " attacking a mass element: " + attackedTerritoryID);
            }
        }else attackedTerritoryID = 0 ;
   } 

    @Override
    public void defend(Territory attacker, double soldiersAttacking){
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        Bag territories = getVulnerableTerritories(getNeighborsToAttack());
        IntBag holdouts = new IntBag();
        IntBag bridges = new IntBag();
        IntBag masses = new IntBag();
      if (attacker.getType() == myTerritory.getType()){
            defendingSoldiers = 0;
        }
      if
         (myTerritory.getSoldiers() > soldiersAttacking) {
        defendingSoldiers = myTerritory.getSoldiers();
      } 
      if (territories.numObjs != 0) 
        {   
            // Defend with three-quarters (variable) of my soldiers
            defendingSoldiers = myTerritory.getSoldiers() * 0.95;
            for (int i =0; i< territories.numObjs; i++) {
               Territory t = (Territory) territories.get(i);
               if (allHoldouts.contains(t.getId())) {
                   holdouts.add(t.getId());
               }
               else if (allBridges.contains(t.getId())) {
                   bridges.add(t.getId());
               }
               else if (allMasses.contains(t.getId())) {
                   masses.add(t.getId());
               }
            }
            Random rand = new Random();
            if (holdouts.numObjs > 0) {
               attackedTerritoryID = holdouts.get(rand.nextInt(holdouts.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " defending a holdout: " + attackedTerritoryID); 
            }
            else if (bridges.numObjs > 0) {
               attackedTerritoryID = bridges.get(rand.nextInt(bridges.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " defending a bridge: " + attackedTerritoryID);
            }
            else if (masses.numObjs > 0) {
               attackedTerritoryID = masses.get(rand.nextInt(masses.numObjs));
               System.out.println("from territory #"+ myTerritory.getId() + " defending a mass element: " + attackedTerritoryID);
            }
        }else {  // Put about half of my soldiers at defense; parameter sweep again
          defendingSoldiers = myTerritory.getSoldiers() * 0.15;
      }
   } 
    

    
    private Bag getNeighborsToAttack() {
        //don't attack subordinates!
        Bag b = new Bag(myTerritory.getNeighbors());
        Bag s = new Bag(myTerritory.getSubordinates());
        for (int i = 0; i < s.size(); i++) {
            if (b.contains(s.get(i))) {
                b.remove(s.get(i));
            }
        }
        return b;
    }
    
private Territory getWeakestTerritory(Bag territories) {
        Territory t = null;
        Territory weakest = null;
        double soldierCount = 10;
        if (territories != null && !territories.isEmpty()) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    soldierCount = t.getSoldiers();
                    weakest = t;
                }
            }
        }
        return weakest;
    }

private Bag getVulnerableTerritories(Bag territories) {
        Territory t = null;
        Bag vulnerableTerritories = new Bag();
        double soldierCount = myTerritory.getSoldiers();
        if (territories != null) {
            for (int i = 0; i < territories.numObjs; i++) {
                t = (Territory)territories.get(i);
                if ((!(t == null)) && (t.getSoldiers() < soldierCount)) {
                    vulnerableTerritories.add(t);
                }
            }
        }
        return vulnerableTerritories;
    }

@Override
    protected void chooseTax(){ // this was found to be optimal with repeated iterations over a parameter sweep
        if (! myTerritories.contains(myTerritory)){
            myTerritories.add(myTerritory);
        }
        if (this.myTerritory.getSubordinates().numObjs < 5)
            tax = 0.2;
        else
            tax = 0.5;
    }

@Override // update the beneficiaries bag and the retributions array
    protected void setRetributionsAndBeneficiaries()
    {
        beneficiaries.clear();
        IntBag holdouts = new IntBag();
        IntBag bridges = new IntBag();
        IntBag masses = new IntBag();
        double targetWealth = 0;
        double mySoldiers = myTerritory.getSoldiers();
        myTerritory.produceSoldiers(myTerritory.getNatRes()-mySoldiers, myTerritory.getPeasants());


        double soldiersToBeRetributed = myTerritory.getSoldiers() - mySoldiers;

        // if no more soldiers were produced, retribute what is in excess compared to resources
        if (soldiersToBeRetributed > 0) // was '=='
        {
            soldiersToBeRetributed = myTerritory.getSoldiers() - myTerritory.getNatRes();
        }

        if (soldiersToBeRetributed <= 0)
            return;
        //put soldiers in bridges if offense, or in holdouts if defense; if territories < 3 then defense
        for (Object o : myTerritories)
        { // change the following for holdouts and bridges, take from masses; build to rings of fire
            if (((Territory)o).getId() != myTerritory.getId())
                    beneficiaries.add(o); // holdouts or bridges depending on # of territories held
        }

        retributions = new double[beneficiaries.numObjs];
        for(int i =0; i< beneficiaries.numObjs;i++)
        {
            retributions[i] = soldiersToBeRetributed/beneficiaries.numObjs;
        }                 
    }
        
@Override
    public void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
    if(youWon)
   System.out.println("V t= "+ period + " A tID " + attackerID +
   " D tID " + deffenderID);
		
    else{
   System.out.println("D! t= "+ period + " A tID " + attackerID + " with " + soldiersAttack + " troops, "+
   "  d tID " + deffenderID + " who had " + soldiersDefend);
		}
        
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
    }

@Override
    protected void trade(){
    System.out.println("very experimental");
    
        //use this to build bridges on offense and holdouts on defense

        double partnerID = (myTerritory.getNeighbors().numObjs);
        trade[0] = partnerID;
        double demandType = 3; //Soldiers
        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = 1; //Just one
        trade[2] = demand;
        // peasants
        double offerType = 2;
        trade[3] = offerType;
        // Choose the amount of goods offered in exchange 
        double offer = 2.5; // Testing parameter; 
        trade[4] = offer;
    }

    
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){

        if (typeOffer == 2 && typeDemand == 3) { 
            acceptTrade = true;
        }
        else acceptTrade = false;
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
