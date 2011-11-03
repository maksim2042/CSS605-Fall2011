package livemore;

import risk.*;
import sim.util.Bag;

public class BenefitStrategy {
	
	private double sizeSuperiorWeight, subordRatioWeight; 
	private double benefitSoldiers;
	private Territory myTerr, benefitTerr;
	
	public double getBenefitSoldiers() {
		return benefitSoldiers;
	}

	public Territory getBenefitTerr() {
		return benefitTerr;
	}

	public void setBenefitSoldiers(double soldiers) {
		benefitSoldiers = soldiers;
	}

	public void setBenefitTerr(Territory terr) {
		benefitTerr = terr;
	}

	// myTerr = home territory
	// alphaWeakStrong = 0 to 1 with 1 being emphasize the Weakest defender
	//		rather than the Strongest (most subordinates)
	public BenefitStrategy(Territory myTerr, double sizeSuperiorWeight
			, double subordRatioWeight) {
		this.sizeSuperiorWeight = sizeSuperiorWeight;
		this.subordRatioWeight = subordRatioWeight;
		this.myTerr = myTerr;
	}
	
	// Decide the strategy - who to benefit and how much
	public void DecideStrategy() {
		
		// For now - just get the one best to overthrow, add 10% of own
		
		this.benefitTerr = LordUtility.BestChance2Overthrow(myTerr);
		this.benefitSoldiers = myTerr.getSoldiers() * .1;
	}
}

