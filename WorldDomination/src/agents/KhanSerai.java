package agents;
import java.util.Random;
import risk.*;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class KhanSerai extends Agent{

    public KhanSerai(int id, int type){
        super(id, type);
        empireName = "KhanSerai";
    }

    Bag myLords= new Bag();                     //carrying my flag
    Bag allLords= new Bag();                    // all territories
    Bag targets= new Bag();
    Bag traders= new Bag();


    boolean full = false;

    public void allLands()    {
     if(!full){

       Bag nbors = myTerritory.getNeighbors();      //holder for neighboring territories
       allLords.add(myTerritory);                    //add self (bag is not null)
       allLords.addAll(nbors);                      // add all neighbors to bag

       while (allLords.numObjs<42) {                //max size of bag is 42
       int ran=new Random().nextInt(allLords.numObjs); //random number generator
       Bag nbors2=  ((Territory)allLords.get(ran)).getNeighbors(); //get random neighbor of stuff in bag

        for (int k = 0; k < nbors2.numObjs; k++){ //check not already added
            if (!allLords.contains(nbors2.get(k))
             && !(nbors2.get(k)).equals(myTerritory))
            {
            allLords.add(nbors2.get(k));             //add to Bag
            }
           }
        }
     }  full=true;
 //    System.out.println("Lords: " + allLords.numObjs);
   }

    public Bag myLands()    {
       allLands();
          for (int k = 0; k < allLords.numObjs; k++)
        {if (((Territory)allLords.get(k)).getType()== myTerritory.getType() &&
                 !((Territory)allLords.get(k)).equals(myTerritory) &&
                 !myLords.contains(allLords.get(k))){
            myLords.add(allLords.get(k));
         }
        }
       return myLords;

    }

    public Bag findTargets(){

     Bag nbors = myTerritory.getNeighbors();   //my neighbors
    Territory up= myTerritory.getSuperior();  //my superior
    Bag subs = myTerritory.getSubordinates(); //territories i have conquered

            targets.clear();
//            System.out.println("number of targets: " + targets.numObjs);

     if (up!=null && !targets.contains(up)) {  //up.getType()!=myTerritory.getType() &&
         targets.add(up);
//         System.out.println("up id: " +up.getId());
     }

            //case 1: neighbors who are not subs
            for (int i=0; i<nbors.numObjs; i++) {
               if (  !targets.contains(nbors.get(i))
                   && !subs.contains(nbors.get(i))
//                  && ((Territory)nbors.get(i)).getType()!=myTerritory.getType()
                   )
                { targets.add(nbors.get(i));
                } //System.out.println("id of target" + nbors.get(i));
                //System.out.println("no of targets0: " + targets.numObjs);
               }
            //case 2: neighbors of subs who are not my type
            if (subs==null){
//                System.out.println("no subordinates");
            }
             else {
                for (int j=0; j<subs.numObjs; j++){
                Bag nOfSubs=((Territory)subs.get(j)).getNeighbors(); //get neighbors of subs

                for (int x=0; x<nOfSubs.numObjs; x++){
                if (!targets.contains(nOfSubs.get(x))){
                    targets.add(nOfSubs.get(x));
                 }
              }
           }
//              System.out.println("targets: nbors + nborOfNbor " + targets.numObjs);
         }


        return(targets);
    }

    public Bag findTraders(){

     Bag nbors = myTerritory.getNeighbors();   //my neighbors
     Bag subs = myTerritory.getSubordinates(); //territories i have conquered

     traders.clear();

     //add all neighbors to bag
            for (int i=0; i<nbors.numObjs; i++) {
               if (  !traders.contains(nbors.get(i))){
                traders.add(nbors.get(i));
                }
               }
     //add all neighbors of subs as well
       if(subs==null){
//          System.out.println("no subs");
       } else if (subs!=null){
           for (int j=0; j<subs.numObjs; j++){
          Bag nOfSubs=((Territory)subs.get(j)).getNeighbors();

           for (int k=0; k<nOfSubs.numObjs; k++)
               if (!traders.contains(nOfSubs.get(k))){
               traders.add(nOfSubs.get(k));
               }
           }
       }
//        System.out.println("findTraders() traders= " + traders.numObjs);
//        System.out.println("findTraders() targets= " + targets.numObjs);
//        System.out.println("findTraders() neighbor= " + nbors.numObjs);
        return traders;
     }


@Override
    public void attack(){

           findTargets();
           myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
           double myForce=myTerritory.getSoldiers();
           attackingSoldiers= myForce;

    //       int attackID=(new Random()).nextInt(findTargets().numObjs)+1;
           for (int t=0; t<findTargets().numObjs; t++){
               double forceCheck=((Territory)findTargets().get(t)).getSoldiers();
               if(myForce>=forceCheck){
                   attackedTerritoryID=((Territory)findTargets().get(t)).getId();
               }
           }

//        attackedTerritoryID = (new Random()).nextInt(findTargets().numObjs)+1;
//        System.out.println("target attacked: " + attackedTerritoryID);
//        System.out.println("ticktockticktock*********************************");
}
 //          defendingSoldiers = myForce- attackingSoldiers;
//            System.out.println("nat res"+ myTerritory.getNatRes());
//            System.out.println("peasants"+ myTerritory.getPeasants());
//            System.out.println("soldiers"+ myTerritory.getSoldiers());
//        // An example of a random attack
//        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
//        attackingSoldiers = myTerritory.getSoldiers();
//        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
//    }

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
        findTraders();
        double ppl= myTerritory.getPeasants();
        double moolah= myTerritory.getNatRes();
        double forces= myTerritory.getSoldiers();

        // An example of a random trade proposal: offer peasants in exchange of soldiers
        // Pick the id of the potential partner
        double partnerID = (new Random()).nextInt(findTraders().numObjs)+1;
//        System.out.println("trade()| partner: " + partnerID);
        trade[0] = partnerID;
        // Choose the type of good demanded (peasants)

        double demandType=1;
        if (ppl <1){demandType = 2;}
        else if (moolah <1){demandType = 1;}
        else if (forces<1){demandType = 3;}

        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = 3;
        trade[2] = demand;
        // Choose the type of good offered in exchange nat res)
        double offerType = 2;
        trade[3] = offerType;
        // Choode the amount of goods offered in exchange (random but less than my total number of peasants)
        double offer = (new Random()).nextDouble()* myTerritory.getPeasants();
        trade[4] = offer;

        // This procedure updated the array trade, which contains the information
        // about trade proposals of the current lord
    }

    /*
    * Should update acceptTrade.
    */
    @Override
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
        // An example for accepting a trade proposal: I only accept offers of peasants when I have less than 3 peasants
        // I am not checking what I am giving in exchange or how much, so you should work on that

        if(typeDemand == 3) {acceptTrade=false;} //always refuse trading soldiers
        else if (typeDemand == 2 && myTerritory.getPeasants() < 3){
            acceptTrade = true;
        }
        else if (typeDemand == 1 && myTerritory.getNatRes() < 1)
        { acceptTrade = true;

        } else acceptTrade=false;
    }

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax(){

        //HK: taxes are set within a min-max range
        //HK: with the tax rate inversely proportional to the number of subordinates


       double minTax=0.15;
       double maxTax=0.35;
       Bag taxee= myTerritory.getSubordinates();
       int myBase= taxee.numObjs;


         {
       if (myBase==0) {tax=0;}             //tax rate is 0 for no subs
       else if (myBase <=2) {tax=maxTax;} //if less than 2 subs, tax shld be no more than 30%
       else if (myBase>6) {tax=minTax;}  //if more than 6 subs, tax shld be no less than 15%
       else {tax=(double)1/myBase;}     //need to cast as double otherwise it's an int (0)
                                       //tax rate inversely proportional to the number of subordinates
  //     System.out.println("subs: " + myBase + " tax: " + tax );
            }
       }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){

       myLands();                                    //calling myLands to activate it
       myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());

       beneficiaries.clear();                      // clear out beneficiaries from Agent class
       beneficiaries=myLands();                      // assign beneficiaries

       double[] ret= new double[myLords.numObjs];           // array of retributions for all of my color
        retributions= new double[myLords.numObjs];          // reset "retributions" from Agent class
              for (int r = 0; r <myLords.numObjs ; r++){
                  ret[r]=(myTerritory.getSoldiers()/5)/myLords.numObjs ; //distribute 1/5 of my soldiers evenly
              }
              retributions= ret;                         // assign retributions
//              System.out.println("beneficiary: "+ beneficiaries.numObjs + " retribution: " +ret[0]); //check it's working

    }


    /*
    * Should update the defendingSoldiers
    */
    @Override
    protected void defend(Territory attacker, double soldiersAttacking){
        // Example of a defending strategy: if the attacker is my subordinate, and attacks me with
        // more soldiers than my stock, then I will surender. Otherwise, attack will all soldiers
        if(myTerritory.getSubordinates().contains(attacker) && soldiersAttacking > myTerritory.getSoldiers()){
            defendingSoldiers = 0;
//            System.out.println("surrender to sub");
        }
        else if (attacker.getType() ==myTerritory.getType()){
            defendingSoldiers = 0;
//            System.out.println("surrender to mytype");
        } else if (soldiersAttacking>myTerritory.getSoldiers()+ 5){
            defendingSoldiers = 0;
//            System.out.println("surrender in shame :-(  d: " + defendingSoldiers + " a: " + soldiersAttacking);
        }

        else {
                defendingSoldiers = myTerritory.getSoldiers();
//                System.out.println("fight to the end! d: " + defendingSoldiers + " a: " + soldiersAttacking);
        }
    }
    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // This method provides information about the outcome of the battle, so I will leave this open to you,
        // to do whatever you want to do with the info.

        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
    }

    /*
    * Provides the information about the trade outcome, you can do what ever you want with it.
    */
    @Override
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // This method provides information about the outcome of the trade, so I will leave this open to you,
        // to do whatever you want to do with the info.

        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
    }
}
