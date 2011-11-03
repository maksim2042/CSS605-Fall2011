package agents;

import java.util.ArrayList;
import java.util.Collections;

import risk.Agent;
import risk.Territory;
import sim.engine.Schedule;
import sim.util.Bag;

public class Catenaccio extends Agent implements Comparable {

	
	/**
	 * 
	 */
	public int revengeTargetID=-2;
	
	public Territory revengeTarget = null;
	
	public boolean pallaLunga = false;
	
	public static ArrayList<Catenaccio> byImportance = new ArrayList<Catenaccio>();
	
	
	public Catenaccio(int id, int type) {
        super(id, type);
        
        empireName = "Sub Umbrae Floreo";
	}



	    /*
	    * Should update the beneficiaries bag and the retributions array.
	    */
	    protected void setRetributionsAndBeneficiaries(){}

	    /*
	    * Should update attackedTerritoryID and the attackingSoldiers
	    */
	    protected void attack(){
	    	
	    	System.out.println("turno!");
	    	
	    	
	    	if(myTerritory.getSuperior() == null && revengeTargetID != -2 && revengeTarget != myTerritory.getSuperior()){
	    		double toProduce = Math.min(myTerritory.getNatRes(), myTerritory.getPeasants());
		    	System.out.println("Attaaaaaaaaaaaaack");
	    		attackedTerritoryID = revengeTargetID;
	    		if(myTerritory.getSoldiers() < myTerritory.getFoodGrowth() + toProduce*.1)
	    			myTerritory.produceSoldiers(myTerritory.getSoldiers()-myTerritory.getFoodGrowth() + toProduce*.1,myTerritory.getSoldiers()- myTerritory.getFoodGrowth() + toProduce*.1);
	    		attackingSoldiers = myTerritory.getSoldiers();
	    		if(toProduce > 0){
	    			revengeTargetID = -2;
	    			revengeTarget = null;}
	    	}
	    	else
	    	if( myTerritory.getSuperior() != null && !pallaLunga )
	    	{
	    		System.out.println("counterattack!");
	    		 pallaLunga=true;
	    		 double toProduce = Math.min(myTerritory.getNatRes(), myTerritory.getPeasants());
	    		myTerritory.produceSoldiers(myTerritory.getFoodGrowth() + toProduce*.5, myTerritory.getFoodGrowth() + toProduce*.5);
	    		attackedTerritoryID = myTerritory.getSuperior().getId();
				attackingSoldiers=myTerritory.getSoldiers();
	    	}
	    	else
	    	if(pallaLunga || myTerritory.getNatRes()>100)
	    	{
	    		double maxResource = Double.MIN_NORMAL;
	    		Territory toAttack = null;
	    		for(int i=0; i< myTerritory.getNeighbors().size(); i++)
	    		{
	    			Territory scoped = (Territory)myTerritory.getNeighbors().get(i);
	    			if(scoped.getType() == myTerritory.getType()) continue;
	    			if(scoped.getNatRes() > maxResource)
	    			{
	    				maxResource = scoped.getNatRes();
	    				attackedTerritoryID= scoped.getId();
	    				if(myTerritory.getNatRes()>100){
	    				 double toProduce = Math.min(myTerritory.getNatRes(), myTerritory.getPeasants());
	    		    	myTerritory.produceSoldiers(myTerritory.getFoodGrowth() + toProduce*.5, myTerritory.getFoodGrowth() + toProduce*.5);}
	    				attackingSoldiers=myTerritory.getSoldiers();
	    			}
	    		}
	    		if(toAttack != null)
	    		{
		    		attackedTerritoryID = toAttack.getId();
	    		}
	    		if(myTerritory.getSoldiers()<2) pallaLunga=false;
	    	}
	    
	    	
	    }

	    protected void chooseTax(){
	    	if (myTerritory.getSubordinates() != null && !myTerritory.getSubordinates().isEmpty())
	    		tax=.5;
	    	else tax = 0;
	    }
	    
	    /*
	    * Should update the defendingSoldiers
	    */
	    protected void defend(Territory attaker, double soldiersAttacking){
	    	
	    	
	  

	    	
	   // 	System.out.println(myTerritory.getNatRes());
	   // 	System.out.println(myTerritory.getPeasants());
	    	if(attaker.getNeighbors().contains(myTerritory)){
	    		revengeTarget = attaker;
	    		revengeTargetID = attaker.getId();
	    		double expectedTax = Math.max(0.3, attaker.getTaxRate());
	    		double productionNeeded = Math.max(soldiersAttacking * 2, myTerritory.getFoodGrowth() + myTerritory.getNatRes() * expectedTax);
	    		if(soldiersAttacking < myTerritory.getFoodGrowth() + myTerritory.getNatRes() * expectedTax){
	    			myTerritory.produceSoldiers(productionNeeded, 
	    					productionNeeded);
	    			defendingSoldiers = myTerritory.getSoldiers();
	    		}

	    	}
	    	else{
	    		System.out.println("They are Jamming us!");
	    	}

	    	
	    	
	    }

	    /*
	    * Provides the information about the battle outcome, you can do what ever you want with it.
	    */
	    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
	            int deffenderID, double soldiersDefend, boolean youWon){
	    	
	    	if(youWon) System.out.println("Victory!");
	    	if(!youWon) System.out.println("Defeat!");
	    	if(myTerritory.getId() == deffenderID)
	    		System.out.println("They attacked with " + soldiersAttack + " and we defended with: " + soldiersDefend + '\n' 
	    				+ "my food growth is: " + myTerritory.getFoodGrowth() );
	    	else
	    		System.out.println("We attacked with " + soldiersAttack + " and they defended with: " + soldiersDefend);
	    	System.out.println("-----------------------------------------");
	    	
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
	    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
	        // period: step of the simulation in which the trade occured
	        // proposerID: id of the territory that created the trade proposal (it can be yourself)
	        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
	        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
	        // so you can store and use that information as you wish
	        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
	    }

		@Override
		public int compareTo(Object o) {
			return (int)(this.myTerritory.getFarmGrowth() - ((Catenaccio) o).myTerritory.getFarmGrowth());
		}




	
}