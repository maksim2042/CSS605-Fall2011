package agents;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import risk.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;

/**
 *
 * @author Omar A. Guerrero
 * @author Clarence Dillon (thanks, Omar)
 */

/*
 * This is the Lord subclass that inherits attributes and methods from the Agent
 * class. You should override the necessary methods and re-assign the relevant
 * attributes to reflect your lord's strategy.
 */
public class ClarenceYourLord extends Agent{

    // TODO Devine state: aggressive or benign

    // TODO if aggressive, don't attack strong nighbors but attack weak neighbors
    // + defend moderatelly but Spoil defeat by giving away resources (not/not tothe attacker)

    // TODO if benign


    double aggression = .9;
    double defensiveness = .333;
    private double myPeasants, myResources, myAttSoldiers, myDefSoldiers;
    private int myNextTarget, myGrudge;
    int myColor;
    int myIdentity;
    private static ArrayList<Agent> woprAlliance;
    private static ArrayList<Agent> fairGame;


    public ClarenceYourLord(int id, int type){
        super(id, type);
        empireName = "W.O.P.R.";
    }

    // Make an array list to hold information about who my other lords are
    // but, make it not have to re-instantiate each time MyLord is called;
    // also, decide which lord gets to be the overlord.
    private void myAlliance (int order, int idNumber, Agent myLord) {
        // Create an arraylist if it does not yet exist
//        if (woprAlliance == null) {
//            order = 0;
//            woprAlliance = new ArrayList<Agent>();
//            woprAlliance.add(order, myLord);
//            order = 1;
//            idNumber = myTerritory.getId();
//        }
//        for (Agent a:woprAlliance) {
//            order = indexOf(a);
//
//            for (int i = 0; i < 7; i++) {
//                if (idNumber < a.getId()) {
//                    indexOf(a) = indexOf(a) + 1;
//
//                }
//                if (i < order)
//            }
//            if (a.getId() == myTerritory.getId()) {break;}
//
//        }

//            for (int j = 0; j < 7; j++)
//
//
//            {
//                if ((myTerritory.getId() == i) && (j == order)) {break;}
//                    order = i;
//                if ((myTerritory.getId() == i) && (j < order) )
//
//                    idNumber = myTerritory.getId();
//
//            }


    }



//    private void Structure(int neighbor) {
//        neighbors = new ArrayList<Agent>();
//        for (int i=1; i<43; i++) {
//        neighbors.add(new neighbor);
//
//    }

    @Override
    public void attack(){
        // Do not attack myself
        boolean fairGame = false;

        myIdentity = this.getId();
        myColor = this.getType();
        myResources = myTerritory.getNatRes();
        myPeasants = myTerritory.getPeasants();

        // DO NOT attack if neighbor is my same type or is ruled by me
//        /if (myTerritory.getNeighbors().getType() == myColor)) {
//
//        }

        myTerritory.produceSoldiers((myResources * .8), (myPeasants * .9));

        // In my experiments, defense seems to be a bit under-rated, but not entirely under-rated
        myDefSoldiers = attackingSoldiers * .4;

        attackingSoldiers = myTerritory.getSoldiers() * aggression;

        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;

//        System.out.println("\t" + myIdentity + " attacked " + attackedTerritoryID);
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
    protected void trade(){
        // An example of a random trade proposal: offer peasants in exchange of soldiers
        // Pick the id of the potential partner (without checking if it is my neighbor)

        double pertnerID = (myTerritory.getNeighbors().numObjs);
        trade[0] = pertnerID;
        // Choose the type of good demanded (not peasants, but soldiers - 3)
        double demandType = 3; //Soldiers
        trade[1] = demandType;
        // Choose the amount of demanded goods
        double demand = 1; //Just one
        trade[2] = demand;
        // Choose the type of good offered in exchange (peasants - 2)
        double offerType = 2;
        trade[3] = offerType;
        // Choode the amount of goods offered in exchange (random but less than my total number of peasants)
        double offer = 1.1; // Testing whether others are testing for 'greater than' limits in exchange rates that work to my advantage
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

        if ((typeOffer == 3 && ((typeDemand <= 2) && (2 >= (offer/demand))))) { //I want to at least break even...
            acceptTrade = true;
        }
        else acceptTrade = false;
    }

    /*
    * Should update tax.
    */
    @Override
    protected void chooseTax(){
        // Lets impose a tax rate that is inversely proportional to the number of subordinates I have:
        // That is, the more subordinates I have, the lower the tax rate.
        if(myTerritory.getSubordinates().isEmpty()){
            tax = 0;
        }

//        if (getType() == myColor) {
//            tax = 0;
//            System.out.println("I taxed " + this.getType() + " " + tax);
//        }
        else tax = .5;

        // Of course, if there are tax rates higher than .5, the system will set it to zero, so you should check on that
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    @Override
    protected void setRetributionsAndBeneficiaries(){
        // Example for picking the "chosen ones" to be benefited from my generous policy
        // First we empty the bag in case we don't want to keep old beneficiaries
        beneficiaries.clear();
        // Pick a random territory from my subordinates
        if (myTerritory.getSubordinates().numObjs>=3){
            beneficiaries.add(myTerritory.getSubordinates().get((new Random()).nextInt(3)));

            // Now assign the corresponding amounts of soldiers to each beneficiary
            // Since there are three of them, we need to re-instantiate the retributions array to one of size 3
            retributions = new double[3];
            // Lets transfer equal amounts of soldiers, which will add up half of my soldiers stock
            retributions[0] = (myTerritory.getSoldiers()/2)/3;
            retributions[1] = (myTerritory.getSoldiers()/2)/3;
            retributions[2] = (myTerritory.getSoldiers()/2)/3;
        }

        // This procedure opdated the beneficiaries list and the retribution array, which gives to the system
        // the necessary info about the redistributive policy of the lords
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
        }
        else defendingSoldiers = myTerritory.getSoldiers();
    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    @Override
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int defenderID, double soldiersDefend, boolean youWon)  {
        // This method provides information about the outcome of the battle, so I will leave this open to you,
        // to do whatever you want to do with the info.

        String result;
        if (youWon == true) {result = "won";}
        else {result = "lost";}
//        System.out.println(myTerritory.getNatRes());
//        System.out.println("Period " + period + ", " + attackerID + " with " + soldiersAttack + "; " + defenderID + "defended with " + soldiersDefend + " you " + result);

//        try {
//            PrintWriter pw = new PrintWriter(new FileWriter("battleOutcomes.txt"));
//            pw.print("Period " + period + " attacked from " + attackerID + " with " + soldiersAttack + "; " + defenderID + "defended with " + soldiersDefend + " you" + result);
//
//        } catch (IOException ex) {
//            Logger.getLogger(YourLord.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // defenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
    }

//    public static void printedAlliance() throws Exception {
//       PrintWriter pw = new PrintWriter(new FileWriter("woprAlliance.txt"));
//       Reader pr = new Reader(FileReader("woprAlliance.txt"));
//       int []alliance = new int[7];
//
//       pw.print();//+ " attacked from " + attackerID + " with " + soldiersAttack + "; " + defenderID + "defended with " + soldiersDefend + " you" + result);
//
//            myStep++;
//            pw.print(myStep + ' ');
//            pw.println();
//            pw.println();
//        pw.close(); // Without this, the output file may be empty
//    }


//    Properties p = new Properties();
//    p.putProperty("foo", 5);
//    p.getProperty("foo");
//    p.store("outcomes.txt");
//    p.load(newInputStream("outcomes.txt"));


}

