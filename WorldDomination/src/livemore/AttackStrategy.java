package livemore;

import risk.*;
import sim.util.Bag;

public class AttackStrategy {
	
	private double alpha, attackingSoldiers;
	private Territory myTerr, attackedTerr;
	
	public double getAttackingSoldiers() {
		return attackingSoldiers;
	}

	public Territory getAttackedTerr() {
		return attackedTerr;
	}

	public void setAttackingSoldiers(double soldiers) {
		attackingSoldiers = soldiers;
	}

	public void setAttackedTerr(Territory terr) {
		attackedTerr = terr;
	}

	// myTerr = home territory
	// alphaWeakStrong = 0 to 1 with 1 being emphasize the Weakest defender
	//		rather than the Strongest (most subordinates)
	public AttackStrategy(Territory myTerr, double alphaWeakStrong) {
		alpha = alphaWeakStrong;
		this.myTerr = myTerr;
	}
	
	// Decide the strategy - who to attack and how much
	public void DecideStrategy() {
		
		double lowest = Integer.MAX_VALUE;
		
		// For each attackable territory - figure how many soldiers, how many subords
		Bag attackableTerrs = LordUtility.AttackableTerritories(myTerr);
		for(int i=0; i < attackableTerrs.numObjs; i++) {
			Territory terr = (Territory) attackableTerrs.get(i);
			// for now just do the weakest
			if (terr.getSoldiers() < lowest) {
				this.attackedTerr = terr;
				lowest = terr.getSoldiers();
			}
		}
		// attacking soldiers are always max if (< 15 or < 4 times defending soldiers) 
		// 		otherwise it is 4 times defending soldiers
		if (this.attackedTerr != null) {
			if (lowest == 0.0) this.attackingSoldiers = 0.2;
			if (lowest <= 0.02) this.attackingSoldiers = 0.5;
			else if (myTerr.getSoldiers() < 15) this.attackingSoldiers = myTerr.getSoldiers();
			else this.attackingSoldiers = myTerr.getSoldiers() > (this.attackedTerr.getSoldiers() * 4)
				? this.attackedTerr.getSoldiers() * 4 : myTerr.getSoldiers();
		}
	}
}
