
package agent;

import force.Analysis;
import force.Help;
import force.Help.Report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logics {
	Logics_Interface curStrategy = new Logic0();

	public Logics_Interface getCurStrategy() {
		return curStrategy;
	}

	public void useStrategy(Logics_Interface a) {
		curStrategy = a;
	}

	public int makeDecision(Agent anAgent) throws IOException {
		int returnValue = curStrategy.logic(anAgent);

		return returnValue;
	}

	class Logic0 implements Logics_Interface {
		String nameOfLogic = "default";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			return -1;
		}
	}

	public class Logic1 implements Logics_Interface {
		String nameOfLogic = "always cooperative";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			return 0;
		}
	}

	public class Logic2 implements Logics_Interface {
		String nameOfLogic = "always antagonistic";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			return 1;
		}
	}

	public class Logic3 implements Logics_Interface {
		String nameOfLogic = "random";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			float value = (float) Math.random();
			if (value > 0.5) {
				return 1;
				} else {
				return 0;
			}
		}
	}

	public class Logic4 implements Logics_Interface {
		String nameOfLogic = "tit for tat";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			if (anAgent.e.getAdversaryOperationAL().size() > 0) {
				return anAgent.e.getAdversaryOperationAL().get(
				anAgent.e.getAdversaryOperationAL().size() - 1);
			}
			return 1;
		}
	}

	public class Logic5 implements Logics_Interface {
		String nameOfLogic = "human";

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		public int logic(Agent anAgent) {
			String playerValue;
			BufferedReader br = new BufferedReader(new InputStreamReader(
			System.in));

			System.out
			.println("please input your decision(0 meanse cooperative, 1 means defective)");

			try {
				playerValue = br.readLine();
				int pv = Integer.valueOf(playerValue);

				while (pv != 0 && pv != 1) {
					System.out.println("try again");
					playerValue = br.readLine();
					pv = Integer.valueOf(playerValue);
				}
				return pv;

				} catch (IOException ex) {
				Logger.getLogger(Logics.class.getName()).log(Level.SEVERE,
				null, ex);
			}
			return 0;
		}
	}

	public class Logic6 implements Logics_Interface {
		String nameOfLogic = "G";
		int currentTentativeOperation = 0;

		public String getNameOfLogic() {
			return nameOfLogic;
		}

		int trialResponsesForLearningTheOpponent() {
			if (timesOfAskingForLogic == 0) {
				return currentTentativeOperation;
			} else if (statistics.get(timesOfAskingForLogic - 1).get(0) == statistics
			.get(timesOfAskingForLogic).get(0)) {
				changeOperation();
			} 
			return currentTentativeOperation;
		}

		int statisticsSize = 0;
		int timesOfAskingForLogic = 0;
		boolean firstTimeRun = true;
		boolean isResponseDataComplete = false;
		boolean isEvaluated = false;
		ArrayList<Integer> preJudgement = new ArrayList<Integer>();
		Agent meIdentity=null;

		public int logic(Agent anAgent) throws IOException {
			meIdentity=anAgent;

			if (firstTimeRun == true) {
				tricks(anAgent, 1, 3, 1, 5); 
				firstTimeRun = false;
			}

			if (timesOfAskingForLogic < statisticsSize) {
				int aTemp = 0;
				aTemp = trialResponsesForLearningTheOpponent();
				timesOfAskingForLogic++;
				return aTemp;
				} else {
				if (isResponseDataComplete == false) {
					for (int i = 0; i < statistics.size(); i++) {
						statistics.get(i).add(
						anAgent.e.getPlayerPenaltyAL().get(i)-anAgent.e.getAdversaryPenaltyAL()
						.get(i)
						);
					}
					isResponseDataComplete = true;
					reportStatistics();
				}

				if (isEvaluated == false) {
					preJudgement = evaluation(statistics);
					isEvaluated = true;
				}

				return judgement(anAgent, preJudgement);
			}
		}

		void reportStatistics() throws IOException{
			if(isResponseDataComplete==true){
				Report r=new Help().new Report(
				meIdentity.getID()+"_"+meIdentity.e.getAdversaryAL().get(0)+".txt");
				r.reportI2Horizontal(statistics);
			}
		}

		void changeOperation() {
			if (currentTentativeOperation == 0) {
				currentTentativeOperation= 1;
				} else {
				currentTentativeOperation =0;
			}
		}

		ArrayList<ArrayList<Integer>> statistics = new ArrayList<ArrayList<Integer>>();

		public ArrayList<ArrayList<Integer>> getStatistics() {
			return statistics;
		}

		int bestChiangingTimes = 0, bestRepeatingTimes = 0,
		currentRepeatingTimes = 0;

		void trick(Agent anAgent, int changingTimes, int repeatTimes) {
			for (int i = 1; i <= changingTimes; i++) {
				for (int j = 1; j <= repeatTimes; j++) {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(i);
					temp.add(j);
					statistics.add(temp);
					statisticsSize++;
				}
			}
		}

		void tricks(Agent anAgent, int changingTimesMin, int changingTimesMax,
		int repeatTimesMin, int repeatTimesMax) {
			for (int k = changingTimesMin; k < changingTimesMax; k++) {
				for (int l = repeatTimesMin; l < repeatTimesMax; l++) {
					trick(anAgent, k, l);
				}
			}
		}

		ArrayList<Integer> evaluation(ArrayList<ArrayList<Integer>> fourFor) {
			Report r = new Help().new Report("validation.txt");
			ArrayList<ArrayList<Integer>> alalI = fourFor;
			for (int ii = 0; ii < alalI.size(); ii++) {
				try {
					r.reportI1(alalI.get(ii));
					r.report("****");
					} catch (IOException e) {
					e.printStackTrace();
				}
			}

			ArrayList<ArrayList<Double>> promisingTactic = new ArrayList<ArrayList<Double>>();

			for (int i = 0; i < fourFor.size(); i++) {
				ArrayList<Integer> arrayList = fourFor.get(i);

				if (arrayList.get(2) <= 0) {
					promisingTactic.add(Help
					.integerArraylistTodoubleArraylist(arrayList));
				}
			}

			if (promisingTactic.size() > 0) {
				ArrayList<Double> changeTimes = new ArrayList<Double>();
				ArrayList<Double> repeatTimes = new ArrayList<Double>();
				for (int i = 0; i < promisingTactic.size(); i++) {
					changeTimes.add(promisingTactic.get(i).get(0));
					repeatTimes.add(promisingTactic.get(i).get(1));
				}

				ArrayList<ArrayList<Integer>> eval=
				new Help().doubleMatrixToIntegerMatrix(
				new Analysis().new GeneralAnalysis().new OccurrenceCounting().oneDimesionOccurenceCounting(changeTimes));

				ArrayList<Integer> returnEval=new ArrayList<Integer>();
				returnEval.add(eval.get(0).get(eval.size()-2));
				returnEval.add(eval.get(1).get(eval.size()-2));
				return returnEval;
				} else {
				return new ArrayList<Integer>();
			}
		}

		private int judgement(Agent agent, ArrayList<Integer> evaluation) {
			if (evaluation.size() > 0) {
				bestChiangingTimes = evaluation.get(0);
				bestRepeatingTimes = evaluation.get(1);
				if (currentRepeatingTimes < bestRepeatingTimes) {
					currentRepeatingTimes++;
					return currentTentativeOperation;
					} else {
					currentRepeatingTimes = 0;
					changeOperation();
					return currentTentativeOperation;
				}
				} else {
				return new Logic3().logic(agent);
			}
		}
	}
}
