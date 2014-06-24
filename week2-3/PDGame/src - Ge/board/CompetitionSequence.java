package board;

import java.io.IOException;
import java.util.ArrayList;

import force.Help;
import force.Help.Report;

import agent.Agent;
import agent.Logics;
import agent.Logics.Logic6;

public class CompetitionSequence {
	ArrayList<Agent> candidates;
	
	public CompetitionSequence(ArrayList<Agent> candidatesList) {
		candidates=candidatesList;
		
	}
	
	public	class PlayWithEveryOne{
		public	void	cs_PlayWithEveryOne(Board aBoard) throws IOException{
			
			for (int i = 0; i < candidates.size()-1; i++) {
				for (int j = i+1; j < candidates.size(); j++) {
					try {
						
						

						
						

						if(candidates.get(i).l.getCurStrategy().getNameOfLogic()=="G"){
							candidates.get(i).resetLogics();
							candidates.get(i).l.useStrategy(new Logics().new Logic6());
							
							

							
						}
						if(candidates.get(j).l.getCurStrategy().getNameOfLogic()=="G"){
							candidates.get(j).resetLogics();
							candidates.get(j).l.useStrategy(new Logics().new Logic6());
							
							
							

							
						}
						
						aBoard.play(candidates.get(i), candidates.get(j));
						
						
						
						} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
}
