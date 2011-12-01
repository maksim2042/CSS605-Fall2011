package rice;

public class WarRecords {
	  // period: step of the simulation in which the battle occured
    // attackerID: id of the territory that took the offensive position during the battle
    // soldiersAttack: number of soldiers that attacked
    // deffenderID: id of the territory that took the defensive position during the battle
    // soldiersDefend: number of soldiers that defended
    // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)
	long period; int attackerID; double soldiersAttack;
    int deffenderID; double soldiersDefend; boolean youWon;

	public WarRecords(long period, int attackerID, double soldiersAttack,
			int deffenderID, double soldiersDefend, boolean youWon) {
		super();
		this.period = period;
		this.attackerID = attackerID;
		this.soldiersAttack = soldiersAttack;
		this.deffenderID = deffenderID;
		this.soldiersDefend = soldiersDefend;
		this.youWon = youWon;
	}

	public long getPeriod() {
		return period;
	}

	public int getAttackerID() {
		return attackerID;
	}

	public double getSoldiersAttack() {
		return soldiersAttack;
	}

	public int getDeffenderID() {
		return deffenderID;
	}

	public double getSoldiersDefend() {
		return soldiersDefend;
	}

	public boolean isYouWon() {
		return youWon;
	}
    
}
