package agents;
import java.util.Random;
import java.util.ArrayList;
import risk.*;
import sim.util.Bag;

/* @author Muse */
public class Citizen extends Agent{
        Bag myTerritories = new Bag();
    
    public Citizen(int id, int type){
        super(id, type);
        empireName = "Citizen";
    }
    
    @Override
    protected void chooseTax(){
        //tax rate varies based on how many subordinates you have - more subordinates, lower taxes
       double minTax=0.15;
       double maxTax=0.4;
       int myBase= myTerritory.getSubordinates().numObjs;
       if (myBase <=2) {tax=0.4;} //if less than 2 subs, set tax at 40%. squeeze out every last cent.
       else if (myBase>6) {tax=0.15;}  //
       else {tax=1.0/myBase;}     //for 3-5 subs, scale inversely.
       System.out.println("pay up, yo. subs: " + myBase + " tax: " + tax );
            }
    
    @Override
    protected void setRetributionsAndBeneficiaries(){
            // if you have subordinates, set any of them with less than half your wealth as beneficiaries
            // divide one-third of your stock of soldiers among all your beneficiaries
        beneficiaries.clear();
        if (! myTerritories.contains (myTerritory))
            {myTerritories.add(myTerritory);}  
    
     if (myTerritory.getSubordinates().numObjs > 0){
     for(int i=0; i < myTerritory.getSubordinates().numObjs; i++) {
                //HOW TO GET NatRes OF A SUBORDINATE?
     if (((Territory)myTerritory.getSubordinates().get(i)).getNatRes() * 2 < myTerritory.getNatRes()) 
   //  ((Territory)myTerritory.getSubordinates().get(i)).getNatRes();
         { beneficiaries.add(myTerritory.getSubordinates().get(i)); }
     
     }
      double[] ret = new double [beneficiaries.numObjs];
        retributions = new double[beneficiaries.numObjs];
        for (int i = 0; i < beneficiaries.numObjs ; i++) {
            ret[i] = (myTerritory.getSoldiers())/3/beneficiaries.numObjs;
        }
        System.out.println("beneficent munificence");
    }
    }

        //         
     //       }
        //}
//  ATTEMPTS...   for(int i=0; i < subordinates.numObjs; i++) {
//                if(subordinates.get(i).getNatRes() * 2 < myTerritory.getNatRes()) 
//                   //     myTerritory.getNatRes() > (2 * myTerritory.getSoldiers())
////               }
////               double forceCheck=((Territory)findTargets().get(t)).getSoldiers()


//    @Override
//    //until I fix the NatRes problem above, beneficiaries just = subordinates.
//    protected Bag getBeneficiaries() {
//        return myTerritory.getSubordinates();
//    }

    private ArrayList<Integer>getMyIds() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i< myTerritories.numObjs; i++) {
            list.add(((Territory)myTerritories.get(i)).getId());
        }
        return list;    
    }
    
    @Override
    protected void defend(Territory attacker, double soldiersAttacking) {
    //don't attack others of your type; otherwise, defense is handled in "attack"
        if (attacker.getType() == myTerritory.getType())
           {defendingSoldiers = 0;}
        if (myTerritory.getSoldiers() > soldiersAttacking) 
            {defendingSoldiers = myTerritory.getSoldiers()*3/4;}
        else {defendingSoldiers = myTerritory.getSoldiers() * 0.10;}
    }

    private Bag getAttackable() {
        //make a list of attackables. put all neighbors in it. then take out
        //all your subordinates so you don't attack them, and any of your own type,
        //and add in your superior if it's not already there.
        Bag attackable = new Bag (myTerritory.getNeighbors());
        Bag subs = new Bag (myTerritory.getSubordinates());

        for (Object o: subs) {
            if (attackable.contains(o)) attackable.remove(o);
        }

        for (Object o: attackable) {
            Territory target = (Territory) o;
            if (target.getType()==this.getType()) attackable.remove(o);
        }


        /*
        for (int i=0; i<attackable.size(); i++) {
            if(attackable.contains(subs.get(i)))
                { attackable.remove(subs.get(i)); }
            
            if (((Territory)attackable.get(i)).getType() == myTerritory.getType())
                 {attackable.remove(i)  ;  }

        }*/
        
        if (! attackable.contains(myTerritory.getSuperior())) {
            attackable.add(myTerritory.getSuperior());
        }
        return attackable;
    }
    
    private Territory getWeakestTerritory(Bag attackable) {
        //iterate over all the attackable territories until finding one with
        //the least soldiers.
        Territory t = null;
        Territory weakest = null;
        double minSoldiers = 1000000000;
        for (int i=0; i< attackable.numObjs; i++) {
            t = (Territory)attackable.get(i);
            if ((!(t == null)) && (t.getSoldiers() < minSoldiers)) {
                minSoldiers = t.getSoldiers();
                weakest = t;
            }
        }
        return weakest;
    }
    
    @Override
    public void attack(){
        // If Alpha is low (not more than 0.5), turn all resources and peasants into soldiers.
        //If Alpha is higher, turn all resources grown that turn and all peasants into soldiers,        
    if (myTerritory.getAlpha() <= 0.5){
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());}
    else {myTerritory.produceSoldiers(myTerritory.getFoodGrowth(), myTerritory.getPeasants());}
    //in either case, set 1/4 of them to attack your weakest attackable territory. 
    attackedTerritoryID = getWeakestTerritory(getAttackable()).getId();
    attackingSoldiers = myTerritory.getSoldiers()/4;
    }
}