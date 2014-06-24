package candidates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import force.Help;
import force.Help.Report;

import agent.Agent;
import agent.Logics;
import agent.Logics_Interface;
import agent.Logics.Logic3;
import agent.Logics.Logic6;

public class PrepareCandidates {
	ArrayList<Agent> preparedCandidates = new ArrayList<Agent>();
	
	public ArrayList<Agent> getPreparedCandidates() {
		return preparedCandidates;
	}
	
	public void reportCandidates() throws IOException{
		Report r=new Help().new Report("candidates listing.txt");
		
		for (int i = 0; i < preparedCandidates.size(); i++) {
			r.report("candidate number:"+i+"           ID:"+preparedCandidates.get(i).getID()+
			"                 strategy code:"+		preparedCandidates.get(i).l.getCurStrategy().getNameOfLogic());
			
		}
	}
	
	public void prepareCandidatesTeam1() throws IOException {
		
		String IDa, IDb,IDc,IDd,IDe;
		System.out
		.println("The Game Starts:\nplease enter the ID for agent A:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		IDa = br.readLine();
		System.out.println("\nplease enter the ID for agent B:");
		IDb = br.readLine();
		System.out.println("\nplease enter the ID for agent C:");
		IDc = br.readLine();
		System.out.println("\nplease enter the ID for agent D:");
		IDd = br.readLine();
		System.out.println("\nplease enter the ID for agent E:");
		IDe = br.readLine();
		
		Agent a = new Agent();
		Agent b = new Agent();
		Agent c = new Agent();
		Agent d = new Agent();
		Agent e = new Agent();
		a.setID(IDa);
		b.setID(IDb);
		c.setID(IDc);
		d.setID(IDd);
		e.setID(IDe);

		Logics_Interface aLogc = new Logics().new Logic3();
		Logics_Interface bLogc = new Logics().new Logic2();
		Logics_Interface cLogc = new Logics().new Logic1();
		Logics_Interface dLogc = new Logics().new Logic4();
		Logics_Interface eLogc = new Logics().new Logic6();
		
		a.l.useStrategy(aLogc);
		b.l.useStrategy(bLogc);
		c.l.useStrategy(cLogc);
		d.l.useStrategy(dLogc);
		e.l.useStrategy(eLogc);
		
		
		
		
		System.out.println("*****strategy******");
		System.out.println("player " + a.getID() + " using strategy:"
		+ aLogc.getNameOfLogic());
		System.out.println("player " + b.getID() + " using strategy:"
		+ bLogc.getNameOfLogic());
		System.out.println("player " + c.getID() + " using strategy:"
		+ cLogc.getNameOfLogic());
		System.out.println("player " + d.getID() + " using strategy:"
		+ dLogc.getNameOfLogic());
		System.out.println("player " + e.getID() + " using strategy:"
		+ eLogc.getNameOfLogic());
		
		preparedCandidates.add(a);
		preparedCandidates.add(b);
		preparedCandidates.add(c);
		preparedCandidates.add(d);
		preparedCandidates.add(e);
		
	}
}
