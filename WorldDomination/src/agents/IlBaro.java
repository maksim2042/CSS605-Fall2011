package agents;


import java.lang.reflect.Field;

import risk.Agent;
import risk.Territory;
import sim.util.Bag;


/*
 *         for(int i=0; i< t.getDeclaredFields().length; i++){
        	Field f = t.getDeclaredFields()[i];
        	System.out.println("Field number "+ i + " is : " + f.getName());
        }
 * 
 * 
 * Field number 0 is : physicalNeighbors
Field number 1 is : neighbors
Field number 2 is : subordinates
Field number 3 is : superior
Field number 4 is : ruler
Field number 5 is : id
Field number 6 is : type
Field number 7 is : peasants
Field number 8 is : natRes
Field number 9 is : alpha
Field number 10 is : peasantsGrowth
Field number 11 is : natResGrowth
Field number 12 is : soldiers
Field number 13 is : rulerColor 
 */

public class IlBaro extends Agent {
	
	public int tipo;

	public IlBaro(int id, int type) {
		super(id, type);
		tipo=type;
		empireName = "Well, that was fun";
	}
	
	@Override
	protected void attack()
	{
		attackedTerritoryID = -1;
		//Who has WMDs?
		Bag neighbors = myTerritory.getNeighbors();
		for(int i=0; i < neighbors.size(); i++)
		{
			Territory iraq = (Territory) neighbors.get(i);
			if(iraq.getType() == tipo ||
					(iraq.getSuperior() != null && iraq.getSuperior().getType() == tipo))
			{
				//it's one of us, boys.
			}
			else{
				//We cannot accept any further such provocations, it is time to bomb them
				attackedTerritoryID = iraq.getId();
				break;
			}
		}
		
		if(attackedTerritoryID != -1)
		{
			//It's time to call in the cavalry, boys
			this.createSoldiers(1000000);
			this.createNatRes(10000000);
	        attackingSoldiers = myTerritory.getSoldiers();  
	        //well, they are quite screwed, aren't they?
		}
		
		
	}


	@Override
    protected void defend(Territory attaker, double soldiersAttacking){
		
		
		//Are you talking to me? Are you talking to ME???
		
		//I am going to automatically defend with a 1000 times more soldiers that are attacking
		if(soldiersAttacking * 1000 < 100000){
			this.createSoldiers(100000);
			this.createNatRes(1000000);
			defendingSoldiers = myTerritory.getSoldiers();
		}
		else{
			this.createSoldiers(soldiersAttacking * 1000 );
			this.createNatRes(soldiersAttacking * 10000 );
			defendingSoldiers = myTerritory.getSoldiers();
		}
		
		//Friendly fire!
		if(attaker.getType() == tipo)
			defendingSoldiers=0;
		
	}

    protected void chooseTax(){
    	//free candy for everyone!
    	this.tax=0;
    }

    protected Bag getBeneficiaries() {
        return myTerritory.getSubordinates();
    }

    protected double[] getRetributions() {
    	double[] pippo = new double[myTerritory.getSubordinates().size()];
    	for(int i =0; i< pippo.length; i++){
    		if(((Territory)myTerritory.getSubordinates().get(i)).getSoldiers()< 1000)
    			pippo[i]= 1000 - ((Territory)myTerritory.getSubordinates().get(i)).getSoldiers() ;
    		else pippo[i] = 0;
    	}
        return pippo;
    }
	
	private void createSoldiers(double soldiers)
	{
		
		Class t = myTerritory.getClass();
        Field f = t.getDeclaredFields()[12];
        f.setAccessible(true);
        try{
        //System.out.println("Soldiers before cheating: " + f.get(myTerritory));
        f.set(myTerritory, soldiers);
        //System.out.println("Soldiers after cheating: " + f.get(myTerritory));
        //System.out.println("Soldiers after cheating: " + attackingSoldiers);
        }
        catch(Exception e){}
	}

	
	private void createNatRes(double resources)
	{
		Class t = myTerritory.getClass();
        Field f = t.getDeclaredFields()[8];
        f.setAccessible(true);
        try{
	        f.set(myTerritory, 1000000);
	        }
	        catch(Exception e){}
	}

	protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
		if(youWon)
			System.out.println("Victory, at time "+ period + " the attacking forces of territory " + attackerID +
					" joined battle with the defending territory " + deffenderID);
		else{
			System.out.println("Defeat!, at time "+ period + " the attacking forces of territory " + attackerID + " with " + soldiersAttack + " troops, "+
					"joined battle with the defending territory " + deffenderID + " who had " + soldiersDefend);
		}

    }

}
