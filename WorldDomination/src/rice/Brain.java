package rice;

import java.util.ArrayList;

import rice.A.ArraylistSort;
import rice.A.OccurrenceCounting;
import risk.Territory;
import sim.util.Bag;
import agents.Rice;

 public  class Brain {
	 public static ArrayList<ArrayList<Double>> enemyList;
	 	 static boolean econOrArmy=false;
public static	 ArrayList<Integer>enemyHitListReversedOrder;//the last guy in this list should be hit first.
	 
	 //to gather all the geo information needed
	public static void geoInputs(Bag geoStatistics, Territory territory, int rounds) {
		
		otherGeoInputs(geoStatistics, territory,rounds);
		for (int i = 0; i < territory.getSubordinates().size(); i++) {
			otherGeoInputs(geoStatistics, (Territory)territory.getSubordinates().get(i),rounds);
		}
		
	}

	 static void otherGeoInputs(Bag geoStatistics, Territory territory, int rounds) {
		 boolean knowThisLandBefore=false;
			for (int i = 0; i < geoStatistics.size(); i++) {
				//if already contain it
				if (((Territory)geoStatistics.get(i)).getId()==territory.getId()) {
					knowThisLandBefore=true;
					break;
				}
			}
			if (knowThisLandBefore=false) {
				NaturalBlessings nb=
						new NaturalBlessings(territory.getId(), territory.getFarmGrowth(),
								territory.getFoodGrowth(), rounds,
								territory.getNeighbors().size()	) ;
				
				geoStatistics.add(nb);
			}
			
		}

	public static void geoThinking(Bag geoStatistics) {
		double peasantGrowth=0;
		double naturalResourceGrowth=0;
		for (int i = 0; i < geoStatistics.size(); i++) {
			naturalResourceGrowth=naturalResourceGrowth+((NaturalBlessings)geoStatistics.get(i)).getNatResGrowth();
			peasantGrowth=peasantGrowth+((NaturalBlessings)geoStatistics.get(i)).peasantsGrowth;
		}
		double ratio=peasantGrowth/naturalResourceGrowth;//������������û��bottleneck�����״̬,�������һ����Եĸ�����Դ���Եļ�ֵ
		for (int i = 0; i < geoStatistics.size(); i++) {//����ÿ��territory�ļ�ֵ
			double aValue=
					(((NaturalBlessings)geoStatistics.get(i)).getNatResGrowth()*ratio+
					((NaturalBlessings)geoStatistics.get(i)).getPeasantsGrowth())/
					((NaturalBlessings)geoStatistics.get(i)).getNumberOfNeighbours();
		}
		
	}

	//count how many times each attackerID attacked and rank the most problematic guy the priority target
	public static void warThinking(Bag warHistory, int rounds) {
		ArrayList<Double> attackers=new ArrayList<Double>();
		for (int i = 0; i < warHistory.size(); i++) {
			WarRecords w=((WarRecords)warHistory.get(i));
		//����ֻ��10��֮�ڵ�ս���������ж�ս��������.
			if ((rounds-w.period)>=10) {
				double a=((WarRecords)warHistory.get(i)).getAttackerID();
				attackers.add(a);
			}
		
		}
		OccurrenceCounting oc=new A().new OccurrenceCounting();
		enemyList=	oc.oneDimesionOccurenceCounting(attackers);
		ArrayList<Double>timesOfAttacks=new ArrayList<Double>();
		for (int i = 0; i < enemyList.size(); i++) {
			timesOfAttacks.add(enemyList.get(i).get(1));
			
		}
		ArraylistSort als=new A().new ArraylistSort();
		als.sortArraylistMembers(timesOfAttacks);
		als.howSorted();
		enemyHitListReversedOrder=new ArrayList<Integer>();//hit the last guy first
		for (int i = 0; i < enemyList.size(); i++) {
			double enemyID=enemyList.get(als.getHowSorted().get(i)).get(0);
	
			enemyHitListReversedOrder.add(	(int)Math.floor(enemyID));
		}
		
	}
	

//one step to develop economy, the next develops the army.
	public static void inscription(Territory myTerritory) {
		otherInscription(myTerritory,econOrArmy);
		for (int i = 0; i < myTerritory.getSubordinates().size(); i++) {
			otherInscription((Territory)myTerritory.getSubordinates().get(i),econOrArmy);
		}
		
		if (econOrArmy==false) {econOrArmy=true;
			
		}
		else{econOrArmy=false;}
	}
	static	void otherInscription(Territory myTerritory,boolean determine){
//		if (determine==false) {
//			myTerritory.produceSoldiers(myTerritory.getFarmGrowth()*0.6,//����ٷֱȺ���Ҫ
//					myTerritory.getFoodGrowth()*0.6);
//		}
//		if (determine==true) {
//			myTerritory.produceSoldiers(myTerritory.getFarmGrowth()*1.4,//����ٷֱȺ���Ҫ
//					myTerritory.getFoodGrowth()*1.4);
//		}
//		
		myTerritory.produceSoldiers(myTerritory.getNatRes(),myTerritory.getPeasants());
	}
		
}
