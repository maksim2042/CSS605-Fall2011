package livemore;

public class BattleOutcome {

	public int AttackerID;
	public int DefenderID;
	public long Period;
	public double SoldiersAttack;
	public double SoldiersDefend;
	public boolean YouWon;
    
	public BattleOutcome(long period, int attackerID, double soldiersAttack,
            int defenderID, double soldiersDefend, boolean youWon){
		this.AttackerID = attackerID;
		this.DefenderID = defenderID;
		this.Period = period;
		this.SoldiersAttack = soldiersAttack;
		this.SoldiersDefend = soldiersDefend;
		this.YouWon = youWon;
	}
}
