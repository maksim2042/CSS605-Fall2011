package agents;

import java.util.ArrayList;

import ec.util.MersenneTwisterFast;
import rice.Brain;
import rice.WarRecords;
import risk.Agent;
import risk.Territory;
import sim.util.Bag;

public class Rice extends Agent {
	int rest=0;
int repeatAttack=0;
Integer targetID ;
	int rounds = 0;// number of rounds
	Bag warHistory = new Bag();
	Bag geoStatistics = new Bag();

	public Rice(int id, int type) {
		super(id, type);
		empireName = "rice";
	}

	// this is the first called function in each step, so add more analytical
	// functions here.
	protected void chooseTax() {
		//check
//		System.out.println("getNatRes():"+this.myTerritory.getNatRes());
//		System.out.println("getPeasants()"+this.myTerritory.getPeasants());
//		try {
//			Thread.sleep(1111);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		super.chooseTax();
		rounds++;
		tax = 0.5;

		Brain.inscription(super.myTerritory);

		Brain.geoInputs(this.geoStatistics, super.myTerritory, rounds);// gather
																		// raw
																		// infor.
																		// about
																		// the
																		// geography
		// ���������ʵû��,������.
	//	Brain.geoThinking(this.geoStatistics);// calculate the geo value

	//	Brain.warThinking(warHistory, rounds);// calculate hostility value to
												// see who deserve been attacked
	}

	protected void attack() {

		super.attack();
//		for (int i = Brain.enemyHitListReversedOrder.size() - 1; -1 < i; i--) {
//			for (int j = 0; j < this.myTerritory.getNeighbors().size(); j++) {
//				String abc = new Integer(((Territory) this.myTerritory
//						.getNeighbors().get(j)).getId()).toString();
//				String def = Brain.enemyList.get(i).toString();
//				if (abc.equals(def)) {// if somebody is the neighbour and
//										// attacked me before
//
//				}
//			}
//
//		}
		rest++;
		if (rest>1) {
			realAttack();
			rest=0;
		}
		
	}

	private	void  realAttack() {
//		repeatAttack++;
//		if (repeatAttack==2) {
//			repeatAttack=0;
//		}
//if (repeatAttack==0) {
	

	
		Bag randomeBag = myTerritory.getNeighbors();//populate with all possible targets
		for (int i = 0; i < myTerritory.getSubordinates().size(); i++) {
			Territory t=(Territory)myTerritory.getSubordinates().get(i);
			for (int j = 0; j <t.getNeighbors().size(); j++) {
				randomeBag.add(t.getNeighbors().get(j));
			}
		}
		
		//who has least number of soldiers
		int minIndexx=0;
		double minVaLUE=10000;
		for (int i = 0; i < randomeBag.size(); i++) {
			if (((Territory)randomeBag.get(i)).getSoldiers()<minVaLUE) {
				minIndexx=i;
				minVaLUE=((Territory)randomeBag.get(i)).getSoldiers();
			}
		}
		
		for (int i = 0; i < randomeBag.size(); i++) {
			//attack the one who has least soldiers and who I can conquer and who is not under my contrl yet
	if (((Territory)randomeBag.get(minIndexx)).getSuperior()==null||
			((Territory)randomeBag.get(minIndexx)).getSuperior().getId()!=this.getId()) {
		if(((Territory)randomeBag.get(i)).getNeighbors().contains(((Territory)randomeBag.get(minIndexx)))&&
				((Territory)randomeBag.get(i)).getSoldiers()>minVaLUE&&
				((Territory)randomeBag.get(i)).getType()!=this.getType()
				){
			targetID = new Integer(((Territory) randomeBag.get(i)).getId());
		}
	}
		
		}
		
		/*
		//@@@@@@@@@@@@@@@@@@@@@@@
		//count the num of appearance of a territory as the neighbor
		ArrayList<Integer> id=new ArrayList<>();
		ArrayList<Integer> numOfAppearance=new ArrayList<>();
		for (int i = 0; i < randomeBag.size(); i++) {
			Integer aI=((Territory)randomeBag.get(i)).getId();
			if (id.contains(aI)==false) {
				id.add(aI);
				numOfAppearance.add(1);
			}
			if (id.contains(aI)==true) {
				int index=id.indexOf(aI);
				numOfAppearance.set(index, numOfAppearance.get(index)+1);
			
			}
		}
		
		//find the most frequent neighbor
		int maxValue=0;
		int maxIndex=0;
		for (int i = 0; i < numOfAppearance.size(); i++) {
			if (numOfAppearance.get(i)>maxValue) {
				maxValue=numOfAppearance.get(i);
				maxIndex=i;
				
			}
			
			}
		*/
		//@@@@@@@@@@@@@@@@@@@@@@@
		
		
	
		//make this frequent neighbor the target
//		 targetID = new Integer(((Territory) randomeBag.get(minIndexx)).getId())//  ��ǰ��"maxIndex"
//				.toString();//ideally,have several my territory to attack the same neighbor
	//	}
		
		
//		System.out.println("targetID:"+targetID);
//		try {
//			Thread.sleep(1111);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	if (targetID!=null) {
		this.attackedTerritoryID=new Integer(targetID);//the target is finalized
		//$$$$$$$$$$$$$$$$$$$$$$
	}	


	this.attackingSoldiers=this.myTerritory.getSoldiers();
	
//############################
	/*
//determine the attacking soldiers
//check how many soldiers are available for each of my territories that boders with the target territory 
Bag anotherBag=myTerritory.getSubordinates();
anotherBag.add(myTerritory);
ArrayList<Double>availableSoldiers=new ArrayList<>();
for (int i = 0; i < anotherBag.size(); i++) {
	if (((Territory)anotherBag.get(i)).getNeighbors().contains(targetID)) {
		availableSoldiers.add(((Territory)anotherBag.get(i)).getSoldiers());
	}
}
//check whether my single biggest territory has more soldiers available or
//if each of my bordering territory contributes a small number of soldiers, collectively has more
double maxp=0;
double minp=0;
int totalNumOfNeighbors=0;
for (int i = 0; i < availableSoldiers.size(); i++) {
	totalNumOfNeighbors++;
	if (availableSoldiers.get(i)>maxp) {
		maxp=availableSoldiers.get(i);
	}
	if (availableSoldiers.get(i)>minp) {
		minp=availableSoldiers.get(i);
	}
}
if (maxp>minp*totalNumOfNeighbors) {
	this.attackingSoldiers=maxp;
}
else{attackingSoldiers=minp;}
*/

//******************8###############



//****************
/*
		double sumAttackerNumber = 0, averageAttackerNumber = 0;
		int q = 0;
		for (int k = 0; k < warHistory.size(); k++) {
			if (new Integer(((WarRecords) warHistory.get(k)).getAttackerID())
					.equals(targetID)) {
				q++;
				sumAttackerNumber += ((WarRecords) warHistory.get(k))
						.getSoldiersAttack();
			}
		}
		averageAttackerNumber = sumAttackerNumber / q;

		if (myTerritory.getSoldiers() > averageAttackerNumber * 3) {// only
																		// attack
																		// if
																		// I'm
																		// strong
			this.attackedTerritoryID = new Integer(targetID);
			this.attackingSoldiers = new Double(averageAttackerNumber) * 2;
		}
//*******************
		*/
	}

	protected void defend(Territory attaker, double soldiersAttacking) {
		super.defend(attaker, soldiersAttacking);
	
		
		//determine the least available number of soldiers
		//check how many soldiers are available for each of my territories that boders with the target territory 
		Bag anotherBag=myTerritory.getSubordinates();
		anotherBag.add(myTerritory);
		ArrayList<Double>availableSoldiers=new ArrayList<Double>();
		for (int i = 0; i < anotherBag.size(); i++) {
		
				availableSoldiers.add(((Territory)anotherBag.get(i)).getSoldiers());
			
		}
		
		
		double minp=1000000;
		for (int i = 0; i < availableSoldiers.size(); i++) {
			if (availableSoldiers.get(i)<=minp) {
				minp=availableSoldiers.get(i);
			}
		}
		
		this.defendingSoldiers = minp;
	}

	protected void battleOutcome(long period, int attackerID,
			double soldiersAttack, int deffenderID, double soldiersDefend,
			boolean youWon) {

		super.battleOutcome(period, attackerID, soldiersAttack, deffenderID,
				soldiersDefend, youWon);
		WarRecords wr = new WarRecords(period, attackerID, soldiersAttack,
				deffenderID, soldiersDefend, youWon);
		warHistory.add(wr);
	}

	/*
	 * Should update the beneficiaries bag and the retributions array. peasants
	 * and natural resources do not need to be explicitly redistributed
	 */
	// ���������ʵû��,������.
	protected void setRetributionsAndBeneficiaries() {

		super.setRetributionsAndBeneficiaries();
		if (rounds == 1) {// do nothing
			super.myTerritory.produceSoldiers(
					super.myTerritory.getFarmGrowth(),
					super.myTerritory.getFoodGrowth());
		} else {// reallocate resources
			double sumAttackers = 0;
			for (int i = 0; i < warHistory.size(); i++) {
				sumAttackers += ((WarRecords) warHistory.get(i))
						.getSoldiersAttack();
			}
			double averageAttacker = sumAttackers / warHistory.size();
			super.beneficiaries.add(super.myTerritory);
		}
	}

}
