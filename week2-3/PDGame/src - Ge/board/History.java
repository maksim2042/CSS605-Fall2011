
package board;

import agent.Agent;

import java.io.IOException;
import java.util.ArrayList;

import force.Help;
import force.Help.Report;

public class History {
	ArrayList<ArrayList<String>> competitors = new ArrayList(); 
	public ArrayList<ArrayList<String>> getCompetitors() {
		return competitors;
	}
	ArrayList<  ArrayList<Integer>> process=new ArrayList();

	public ArrayList<ArrayList<Integer>> getProcess() {
		return process;
	}

	public void setProcess(ArrayList<ArrayList<Integer>> process) {
		this.process = process;
	}
	ArrayList<   ArrayList<Integer>> results=new ArrayList();

	public ArrayList<ArrayList<Integer>> getResults() {
		return results;
	}

	public void setResults(ArrayList<ArrayList<Integer>> results) {
		this.results = results;
	}

	public   void  reportDetails() throws IOException{
		Report r=new Help().new Report("prisoners' dilemma.txt");
		r.reportHorizontal("\n******************************\n played " + results.size() + " rounds\nthe game is ended");
		r.reportHorizontal("\n*****************for the game process\n0 means cooperative,1 means adversary");
		r.report("\n*****************details*****************\n"+
		"my move, opponent's move, my penalty, opponent's penalty, collective penalty");
		for (int i = 0; i < results.size(); i++) {
			r.reportI1Horizontal(process.get(i));
			r.reportI1Horizontal(results.get(i));
			r.reportS1Horizontal(competitors.get(i));
			r.report("\n");
		}
	}

	public void reportResultsSummary() throws IOException{
		ArrayList<String> mark=new ArrayList<String>();
		ArrayList<Integer> first=new ArrayList<Integer>(), second=new ArrayList<Integer>();

		for (int i = 0; i < results.size(); i++) {
			String concantenatedMeasure=competitors.get(i).get(0)+","+competitors.get(i).get(1)+":";
			if(mark.contains(concantenatedMeasure)){
				int p=mark.indexOf(concantenatedMeasure);
				first.set(p, first.get(p)+results.get(i).get(0))	;
				second.set(p, second.get(p)+ results.get(i).get(1))	;
			}
			else{
				mark.add(concantenatedMeasure);
				first.add(new Integer( results.get(i).get(0)));
				second.add(new Integer( results.get(i).get(1)));
			}
		}

		Report r=new Help().new Report("prisoners' dilemma summary.txt");
		for (int i = 0; i < mark.size(); i++) {
			r.reportHorizontal(mark.get(i));
			r.reportHorizontal(String.valueOf(first.get(i)));
			r.reportHorizontal(String.valueOf(second.get(i)));
			r.report("\n");
		}
	}
}
