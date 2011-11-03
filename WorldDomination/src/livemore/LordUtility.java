package livemore;
import risk.*;
import sim.util.Bag;

public class LordUtility {
	
	public static Bag AllTerritories;
	
	public static Bag AttackableTerritories(Territory myTerr) {
		Bag attTerr = new Bag();
		Territory mySuperior = myTerr.getSuperior();
		if (mySuperior != null) attTerr.add(mySuperior);
		for (int i=0; i < myTerr.getNeighbors().numObjs; i++) {
			Territory terr = (Territory) myTerr.getNeighbors().get(i);
			// include if different type
			if (terr.getType() != myTerr.getType()) attTerr.add(terr);
		}
		return attTerr;
	}
	
	// Figure the friendly territory which has best chance to win in battles.
	//    Criteria is: same type, diff type superior, greatest soldier/superior ratio
	public static Territory BestChance2Overthrow(Territory myTerr) {
		double highScore = 0;
		Territory best = null;
		Bag friends = GetAllFriendTerr(myTerr);
		for (int i=0; i < friends.numObjs; i++) {
			Territory terr = (Territory) friends.get(i);
			Territory superior = terr.getSuperior();
			// Don't include if territory is unconquered
			if (superior == null) continue;
			if (terr.getType() == myTerr.getType() 
					&& superior.getType() != myTerr.getType() ) {
				double soldier2Superior = terr.getSoldiers() / superior.getSoldiers();
				if (soldier2Superior > highScore) {
					highScore = soldier2Superior;
					best = terr;
				}
			}
		}
		return best;
	}
	public static Bag GetAllFriendTerr(Territory myTerr) {
		Bag friends = new Bag();
		Bag allTerr = GetAllTerritories(myTerr);
		// For each territory - add all of same type
		for (int i=0; i < allTerr.numObjs; i++) {
			Territory terr = (Territory) allTerr.get(i);
			if (terr.getType() == myTerr.getType() && !terr.equals(myTerr)) 
				friends.add(terr);
		}
		return friends;
	}
	public static Bag GetAllTerritories(Territory currTerr) {
		if (AllTerritories == null) 
			AllTerritories = GetAllTerritories(currTerr, new Bag());
		return AllTerritories;
	}

	public static Bag GetAllTerritories(Territory currTerr, Bag terrs) {
		int numTerr = 42;
		int startNumTerr = terrs.numObjs;
		// For each neighbor - add neighbor if not already added
        for(int i=0; i < currTerr.getNeighbors().numObjs; i++) {
        	Territory terr = (Territory) currTerr.getNeighbors().get(i);
        	if (!terrs.contains(terr)) terrs.add(terr);
        }
        // return if maximum (42) hit or if no change in number
        if (terrs.numObjs == numTerr || startNumTerr == terrs.numObjs) 
        	return terrs;
        
		// For each neighbor - call this check if not already called
        for(int i=0; i < currTerr.getNeighbors().numObjs; i++) {
        	Territory neighbor = (Territory) currTerr.getNeighbors().get(i);
    		GetAllTerritories(neighbor, terrs);
        }
		
		return terrs;
	}
	
	public static boolean Trade4PeasantsOverNatRes(Territory currTerr) {
		if (currTerr.getAlpha() < .3 ) return true;
		else return false;
	}
}
