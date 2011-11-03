package livemore;

import risk.*;
import sim.util.Bag;


public class StateOfTheWorld {
	
	private Territory myTerr;
	private Bag rulers;
	
	
	public StateOfTheWorld(Territory myTerr) {
		this.myTerr = myTerr;
	}
	
	@Override
	public String toString() {
		String output = "";
		
		Bag allTerrs = LordUtility.GetAllTerritories(myTerr);
		
		
		
		return output;
	}
}
