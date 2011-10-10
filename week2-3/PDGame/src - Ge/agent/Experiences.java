
package agent;

import java.util.ArrayList;

public class Experiences {
	public ArrayList<String> getAdversaryAL() {
		return adversaryAL;
	}

	public ArrayList<Integer> getAdversaryOperationAL() {
		return adversaryOperationAL;
	}

	public ArrayList<Integer> getAdversaryPenaltyAL() {
		return adversaryPenaltyAL;
	}

	public ArrayList<Integer> getPlayerOperationAL() {
		return playerOperationAL;
	}

	public ArrayList<Integer> getPlayerPenaltyAL() {
		return playerPenaltyAL;
	}
	ArrayList<String> adversaryAL=new ArrayList<String>();
	ArrayList<Integer> playerOperationAL=new ArrayList<Integer>();
	ArrayList<Integer> adversaryOperationAL=new ArrayList<Integer>();
	ArrayList<Integer> playerPenaltyAL=new ArrayList<Integer>();
	ArrayList<Integer> adversaryPenaltyAL=new ArrayList<Integer>();

	void   notifiedExperience(Agent adversary,int playerOperation,int adversaryOperation,
	Integer playerPenalty, Integer adversaryPenalty){
		adversaryAL.add(adversary.getID());
		playerOperationAL.add(playerOperation);
		adversaryOperationAL.add(adversaryOperation);
		playerPenaltyAL.add(playerPenalty);
		adversaryPenaltyAL.add(adversaryPenalty);
	}
}
