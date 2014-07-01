
package world;

import agent.Agent;
import agent.Logics;
import agent.Logics.Logic3;
import agent.Logics_Interface;
import board.Board;
import board.CompetitionSequence;
import board.CompetitionSequence.PlayWithEveryOne;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import candidates.PrepareCandidates;

public class RunTheWorld {
	public static void main(String[] args) throws IOException,
	InterruptedException {
		PrepareCandidates pc = new PrepareCandidates();
		pc.prepareCandidatesTeam1();

		CompetitionSequence cs = new CompetitionSequence(
		pc.getPreparedCandidates());
		PlayWithEveryOne pwe = cs.new PlayWithEveryOne();

		Board board = new Board();

		board.setNumberOfRounds();

		pwe.cs_PlayWithEveryOne(board);

		board.getH().reportResultsSummary();
		board.getH().reportDetails();
		pc.reportCandidates();
	}
}
