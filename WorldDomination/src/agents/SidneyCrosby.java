/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import java.util.ArrayList;
import java.util.Random;
import risk.*;
import sim.util.Bag;

/**
 *
 * @author Jeff
 */
public class SidneyCrosby extends Agent {

    static ArrayList<Territory> terris = new ArrayList<Territory>();
    boolean flag = false;
    private static Bag myTerritories = new Bag();

    public SidneyCrosby(int id, int type) {
        super(id, type);
        empireName = "Sidney Crosby";


    }

    @Override
    protected void trade() {

        double alpha = myTerritory.getAlpha();
        double farmGrowth = myTerritory.getFarmGrowth();
        double foodGrowth = myTerritory.getFoodGrowth();

        double soldiers = myTerritory.getSoldiers();
        double peasants = myTerritory.getPeasants();
        double natRes = myTerritory.getNatRes();
        
        //some ideas borrow from chuck norris, i am still working on trading...
        if (natRes < soldiers) {

            double diff = soldiers - natRes;

            trade[0] = findLargestResourceRatio();
            trade[1] = 1;
            trade[2] = diff / 2 * Math.min(Math.max(alpha, 1 - alpha) / Math.min(alpha, 1 - alpha), 1.5);
            trade[3] = 1;
            trade[4] = diff / 2;
            return;
        }
        System.out.println("Sidney Crosby trades!!!");
   
    }
    
    
    
    @Override
    protected void setRetributionsAndBeneficiaries() {
        for (Object o : terris) {
            if (((Territory) o).getType() == myTerritory.getType()) {
                if (!myTerritories.contains(o)) {
                    myTerritories.add(o);
                }
            }
        }

        beneficiaries.clear();

        double mySoldiers = myTerritory.getSoldiers();

        //if mysoldiers smaller than neighbours, add soldiers
        double maxNeighbourSoldiers = 0.0;
        double soldiersNeed = 0.0;
        for (Object o : myTerritory.getNeighbors()) {
            if (((Territory) o).getSoldiers() > maxNeighbourSoldiers) {
                maxNeighbourSoldiers = ((Territory) o).getSoldiers();
            }
        }

        if (maxNeighbourSoldiers > myTerritory.getSoldiers()) {
            soldiersNeed = maxNeighbourSoldiers - myTerritory.getSoldiers();
            myTerritory.produceSoldiers(myTerritory.getNatRes() - myTerritory.getSoldiers(), myTerritory.getPeasants());
            System.out.println(maxNeighbourSoldiers + "maxNeighbourSoldiers" + myTerritory.getSoldiers() + "myTerritory.getSoldiers()");
        }



        double soldiersToBeRetributed = myTerritory.getSoldiers() - mySoldiers;

        // belowe functions borrow from chuck norris, i am still working on that.
        if (soldiersToBeRetributed == 0) {
            soldiersToBeRetributed = myTerritory.getSoldiers() - myTerritory.getNatRes();
        }

        if (soldiersToBeRetributed <= 0) {
            return;
        }

        for (Object o : myTerritories) {
            if (((Territory) o).getId() != myTerritory.getId()) {
                beneficiaries.add(o);
            }
        }

        retributions = new double[beneficiaries.numObjs];
        for (int i = 0; i < beneficiaries.numObjs; i++) {
            retributions[i] = soldiersToBeRetributed / beneficiaries.numObjs;
        }
    }

    protected void attack() {

        attackedTerritoryID = 0;
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getSoldiers();
        double minNeighbourSoldiers = myTerritory.getSoldiers();
        Territory weakestTarget = null;
        if (myTerritory.getSubordinates().numObjs < 3) // while we have not many subordinates attack the weakest
        {
            for (Object o : myTerritory.getNeighbors()) {
                if (((Territory) o).getSoldiers() < minNeighbourSoldiers) {
                    minNeighbourSoldiers = ((Territory) o).getSoldiers();
                    weakestTarget = (Territory) o;
                }

            }


            if (weakestTarget != null) {

                attackedTerritoryID = weakestTarget.getId();

            }
        }


        Bag neighbors = myTerritory.getNeighbors();


        setRetributionsAndBeneficiaries();

    }



    protected void chooseTax(){tax = 0.5;};
    

    protected void defend(Territory attaker, double soldiersAttacking){defendingSoldiers = myTerritory.getSoldiers();}
     
    
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
            System.out.println("-------Sidney Crosby Trade Log-------");
            System.out.println("Period: "+ period);
            System.out.println("ProposerID: "+ proposerID);
            System.out.println("Trade Proposal: "+ tradeProposal);
        }
        else
        {
            System.out.println("ehhhhhhhhhhhhh");
        }

    }
     
     private void getMap() {
        if (flag == false) {
            flag = true;
            for (Object o : myTerritory.getNeighbors()) {
                Territory terr = (Territory) o;

                for (Object o1 : terr.getNeighbors()) {
                    Territory terr1 = (Territory) o1;
                    if (!terris.contains(terr1)) {
                        terris.add(terr1);
                    }
                    for (Object o2 : terr.getNeighbors()) {
                        Territory terr2 = (Territory) o2;
                        if (!terris.contains(terr2)) {
                            terris.add(terr2);
                        }
                        for (Object o3 : terr.getNeighbors()) {
                            Territory terr3 = (Territory) o3;
                            if (!terris.contains(terr3)) {
                                terris.add(terr3);
                            }
                            for (Object o4 : terr.getNeighbors()) {
                                Territory terr4 = (Territory) o4;
                                if (!terris.contains(terr4)) {
                                    terris.add(terr4);
                                }
                                for (Object o5 : terr.getNeighbors()) {
                                    Territory terr5 = (Territory) o5;
                                    if (!terris.contains(terr5)) {
                                        terris.add(terr5);
                                    }
//                                    for (Object o6 : terr.getNeighbors()) {
//                                        Territory terr6 = (Territory) o6;
//                                        if (!terris.contains(terr6)) {
//                                            terris.add(terr6);
//                                        }
//                                        for (Object o7 : terr.getNeighbors()) {
//                                            Territory terr7 = (Territory) o7;
//                                            if (!terris.contains(terr7)) {
//                                                terris.add(terr7);
//                                            }
//                                            for (Object o8 : terr.getNeighbors()) {
//                                                Territory terr8 = (Territory) o8;
//                                                if (!terris.contains(terr8)) {
//                                                    terris.add(terr8);
//                                                }
//                                                for (Object o9 : terr.getNeighbors()) {
//                                                    Territory terr9 = (Territory) o9;
//                                                    if (!terris.contains(terr9)) {
//                                                        terris.add(terr9);
//                                                    }
//                                                    for (Object o10 : terr.getNeighbors()) {
//                                                        Territory terr10 = (Territory) o10;
//                                                        if (!terris.contains(terr10)) {
//                                                            terris.add(terr10);
//                                                        }
//
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }

                                }

                            }

                        }

                    }

                }

                if (!terris.contains(terr)) {
                    terris.add(terr);
                }
            }
        }
    }

    private void printMap() {
        for (Territory territory : terris) {
            System.out.println(territory.getId() + " NatRes:" + territory.getNatRes() + " Soldiers:" + territory.getSoldiers() + "!!");

        }
    }

    private int findLargestResourceRatio() {
        getMap();
        Territory territory = null;
        Double resource = 0.0;
        int index = -1;
        int id = -1;
        if (!terris.isEmpty()) {
            //for (Territory t : terris) {
            for (int i = 0; i < terris.size(); i++) {
                //Territory t = (Territory)o;
                if ((terris.get(i).getNatRes() >= resource) && (terris.get(i).getType() != myTerritory.getType())) {
                    resource = terris.get(i).getNatRes();
                    index = i;
                }
            }
            if (territory != null) {
                System.out.println(territory.getId() + " " + territory.getNatRes() + " " + territory.getSoldiers() + "!!");
                return territory.getId();
            }

        }
        printMap();
        //System.out.println(index+"index test result");
        id = terris.get(index).getId();
        //System.out.println("IIIIIIIIIIIIIIIIID"+id);
        return id;
    }
}
