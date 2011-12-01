package rice;

import sim.util.Bag;

public class NaturalBlessings {
	
	int ID;
	
	//from territory
	double natResGrowth; // The number of natural resources that are borned every period in the territory
	
	//from territory
	double peasantsGrowth; // The number of peasants that are borned every period in the territory
	
	//solider overhead,from territory.
	//natRes -= getSoldiers();
	
	//to record which round this territory is in possession
	Bag roundsInPossession=new Bag();
	
	//priority score
	double value=0;

	
	int numberOfNeighbours=0;

	public int getNumberOfNeighbours() {
		return numberOfNeighbours;
	}

	public void setNumberOfNeighbours(int numberOfNeighbours) {
		this.numberOfNeighbours = numberOfNeighbours;
	}

	public int getID() {
		return ID;
	}

	public NaturalBlessings(int iD, double natResGrowth, double peasantsGrowth,
			int roundsInPossession, int i) {
		super();
		ID = iD;
		this.natResGrowth = natResGrowth;
		this.peasantsGrowth = peasantsGrowth;
		this.numberOfNeighbours=i;
		if (natResGrowth<peasantsGrowth) {value=natResGrowth;
			
		}else{value=peasantsGrowth;}
	}

	public void setID(int iD) {
		ID = iD;
	}

	public double getNatResGrowth() {
		return natResGrowth;
	}

	public void setNatResGrowth(double natResGrowth) {
		this.natResGrowth = natResGrowth;
	}

	public double getPeasantsGrowth() {
		return peasantsGrowth;
	}

	public void setPeasantsGrowth(double peasantsGrowth) {
		this.peasantsGrowth = peasantsGrowth;
	}

	public Bag getRoundsInPossession() {
		return roundsInPossession;
	}

	public void setRoundsInPossession(Bag roundsInPossession) {
		this.roundsInPossession = roundsInPossession;
	}

	public double getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
