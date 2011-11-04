package livemore;

import risk.*;
import sim.util.Bag;

public class DefendStrategy {
	
	private double alpha, attackingSoldiers, defendingSoldiers;
	private Territory _myTerr, attackingTerr;
	
	public double getDefendingSoldiers() {
		return defendingSoldiers;
	}

	public void setDefendingSoldiers(double soldiers) {
		defendingSoldiers = soldiers;
	}

	// myTerr = home territory
	// alphaUse1 = 0 to 1 with 1 being emphasize using of 1 defender
	//		with a much Stronger attacker
	public DefendStrategy(Territory myTerr, double alphaUse1
			, Territory attacker, double attackingSoldiers) {
		alpha = alphaUse1;
		attackingTerr = attacker;
		this.attackingSoldiers = attackingSoldiers;
		this._myTerr = myTerr;
	}
	
	// Decide the strategy - how much to defend with
	public double DecideStrategy() {
		
		// Based on alpha, figure when to use 1 and when to use all
		// For now - when attackers have 4 * soldiers, then use 1
		if (attackingSoldiers > 15 && attackingSoldiers > defendingSoldiers * 4) {
			this.defendingSoldiers = 1;
		}
		else this.defendingSoldiers = _myTerr.getSoldiers();
		return defendingSoldiers;
	}
}

