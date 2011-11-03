package agents;

/**
 *
 * @author Brent Auble
 *
 * The class is used as a record of a battle, so it can be captured as an object and managed using a collection
 *
 * Here are the attributes of this class:
 *  period: step of the simulation in which the battle occured
 *  attackerID: id of the territory that took the offensive position during the battle
 *  soldiersAttacking: number of soldiers that attacked
 *  defenderID: id of the territory that took the defensive position during the battle
 *  soldiersDefending: number of soldiers that defended
 *  youWon: indicator if the current territory won or lost the battle (true if victorious, false if not)
 *
 * The only way to set the attributes is through the constructor, because once set, they shouldn't be changed
 *
 */
public class BattleHistory {
    private long period;
    private int attackerID;
    private double soldiersAttacking;
    private int defenderID;
    private double soldiersDefending;
    private boolean youWon;

    public BattleHistory(long period, int attackerID, double soldiersAttacking,
            int defenderID, double soldiersDefending, boolean youWon) {
        this.period = period;
        this.attackerID = attackerID;
        this.soldiersAttacking = soldiersAttacking;
        this.defenderID = defenderID;
        this.soldiersDefending = soldiersDefending;
        this.youWon = youWon;
    }

    public int getAttackerID() {
        return attackerID;
    }

    public int getDefenderID() {
        return defenderID;
    }

    public long getPeriod() {
        return period;
    }

    public double getSoldiersAttacking() {
        return soldiersAttacking;
    }

    public double getSoldiersDefending() {
        return soldiersDefending;
    }

    public boolean isYouWon() {
        return youWon;
    }

}
