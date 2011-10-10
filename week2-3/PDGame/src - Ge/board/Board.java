
package board;

import agent.Agent;
import agent.Logics;
import agent.Logics.Logic6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import force.Help;
import force.Help.Report;

public class Board {
	public void setNumberOfRounds() throws IllegalArgumentException,
	IOException {
		System.out.println("\nplease enter number of rounds to play:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int rounds = new Integer(br.readLine());

		numberOfRounds = rounds;
	}

	int numberOfRounds = 0;

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	void interaction(Agent a, Agent b) throws InterruptedException, IOException {
		int aa = 0, bb = 0;
		aa = a.o.operate(a.l.makeDecision(a));
		bb = b.o.operate(b.l.makeDecision(b));

		ArrayList<String> c = new ArrayList(); 
		ArrayList<Integer> r = new ArrayList(); 
		ArrayList<Integer> p = new ArrayList(); 

		p.add(aa);
		p.add(bb);
		h.getProcess().add(p);

		if (aa == 0 && bb == 0) {
			r.add(1);
			r.add(1);
			r.add(2);
			c.add(a.getID());
			c.add(b.getID());
		}
		if (aa == 0 && bb == 1) {
			r.add(0);
			r.add(5);
			r.add(5);
			c.add(a.getID());
			c.add(b.getID());
		}
		if (aa == 1 && bb == 0) {
			r.add(5);
			r.add(0);
			r.add(5);
			c.add(a.getID());
			c.add(b.getID());
		}
		if (aa == 1 && bb == 1) {
			r.add(3);
			r.add(3);
			r.add(6);
			c.add(a.getID());
			c.add(b.getID());
		}

		a.notifyPlayers(a, b, aa, bb, r.get(0), r.get(1));

		b.notifyPlayers(b, a, bb, aa, r.get(1), r.get(0));

		h.getResults().add(r);
		h.getCompetitors().add(c);
	}

	public void play(Agent a, Agent b) throws InterruptedException, IOException {
		a.resetExperience();
		b.resetExperience();

		for (int i = 1; i <= numberOfRounds; i++) {
			interaction(a, b);
		}
	}

	History h = new History();

	public History getH() {
		return h;
	}
}
