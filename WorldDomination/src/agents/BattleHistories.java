/*
 * This is a class to collect individual BattleHistory records for a territory and
 * provide additional information about particular territories
 *
 * It can be used (as separate objects, because the stats won't make sense otherwise)
 *
 */

package agents;

import sim.util.Bag;

/**
 *
 * @author Brent Auble
 */
public class BattleHistories {
    private boolean isAttacker = false;  // indicates whether this is keeping the attacker's history or the defender's
    private Bag battles = new Bag();
    public long[] attacks = new long[43];  // the number of attacks by/against this territory
    public long[] lastPeriod = new long[43];  // the most recent time period when this territory (was) attacked
    public long[] wins = new long[43];  // the number of times an attack succeeded
    public double[] avgAttackers = new double[43];  // the average number of soldiers used to attack
    public double[] minAttackers = new double[43];  // the minimum number of soldiers used to attack
    public double[] maxAttackers = new double[43];  // the maximum number of soldiers used to attack
    public double[] avgDefenders = new double[43];  // the average number of soldiers used to defend
    public double[] minDefenders = new double[43];  // the minimum number of soldiers used to defend
    public double[] maxDefenders = new double[43];  // the maximum number of soldiers used to defend
    public double[] attackRatio = new double[43];  // the ratio of soldiers used to attack over those used to defend

    public BattleHistories(boolean isAttacker) {
        this.isAttacker = isAttacker;
    }

    public void add(BattleHistory battle) {
        battles.add(battle);
        int territoryID;
        if (isAttacker) { 
            territoryID = battle.getAttackerID();
        } else {
            territoryID = battle.getDefenderID();
        }
        attacks[territoryID]++;
        lastPeriod[territoryID] = battle.getPeriod();
        if (battle.isYouWon()) { wins[territoryID]++; }
        if (attacks[territoryID] <= 1) {
            // it's the first attack by/against this territory, so the average is the current # of attackers/defenders
            avgAttackers[territoryID] = battle.getSoldiersAttacking();
            minAttackers[territoryID] = battle.getSoldiersAttacking();
            maxAttackers[territoryID] = battle.getSoldiersAttacking();
            avgDefenders[territoryID] = battle.getSoldiersDefending();
            minDefenders[territoryID] = battle.getSoldiersDefending();
            maxDefenders[territoryID] = battle.getSoldiersDefending();
            if (battle.getSoldiersDefending() != 0) {
                attackRatio[territoryID] = battle.getSoldiersAttacking() / battle.getSoldiersDefending();
            }
        } else {
            // The average can be calculated by multiplying the previous average by the number of attacks
            // minus 1 (since we've already incremented the number of attacks), adding in the current number
            // of attackers/defenders and dividing by the total number attacks (including the current one).
            avgAttackers[territoryID] = ((avgAttackers[territoryID] * (attacks[territoryID]-1))
                    + battle.getSoldiersAttacking()) / attacks[territoryID];
            if (battle.getSoldiersAttacking() < minAttackers[territoryID]) {
                minAttackers[territoryID] = battle.getSoldiersAttacking(); }
            if (battle.getSoldiersAttacking() > maxAttackers[territoryID]) {
                maxAttackers[territoryID] = battle.getSoldiersAttacking(); }
            avgDefenders[territoryID] = ((avgDefenders[territoryID] * (attacks[territoryID]-1))
                    + battle.getSoldiersDefending()) / attacks[territoryID];
            if (battle.getSoldiersDefending() < minDefenders[territoryID]) {
                minDefenders[territoryID] = battle.getSoldiersDefending(); }
            if (battle.getSoldiersDefending() > maxDefenders[territoryID]) {
                maxDefenders[territoryID] = battle.getSoldiersDefending(); }

            if (battle.getSoldiersDefending() != 0) {
                attackRatio[territoryID] = battle.getSoldiersAttacking() / battle.getSoldiersDefending();
                attackRatio[territoryID] = ((avgAttackers[territoryID] * (attacks[territoryID]-1))
                        + battle.getSoldiersAttacking()) / attacks[territoryID];
            }
      }
    }

    public Bag getAllAttacksBy(int territoryID) {
        // Returns a bag containing all attacks by the specified territoryID
        // Note that this can be used by the attacker to return all of their attacks
        Bag b = new Bag();
        BattleHistory tempBattle;

        for (int i = 0; i < battles.numObjs; i++) {
            tempBattle = ((BattleHistory) battles.get(i));

            if (tempBattle.getAttackerID() == territoryID) {
                b.add(tempBattle);
            }
        }
        return b;
    }

     public Bag getAllDefensesBy(int territoryID) {
        // Returns a bag containing all defenses by the specified territoryID
        // Note that this can be used by the defender to return all of the attacks against them
        Bag b = new Bag();
        BattleHistory tempBattle;

        for (int i = 0; i < battles.numObjs; i++) {
            tempBattle = ((BattleHistory) battles.get(i));

            if (tempBattle.getDefenderID() == territoryID) {
                b.add(tempBattle);
            }
        }
        return b;
    }

    public boolean isIsAttacker() {
        return isAttacker;
    }

   public double getAttackers(int territoryID) {
        return avgAttackers[territoryID];
    }

    public long getAttacks(int territoryID) {
        return attacks[territoryID];
    }

    public double getDefenders(int territoryID) {
        return avgDefenders[territoryID];
    }

    public long getLastPeriod(int territoryID) {
        return lastPeriod[territoryID];
    }

    public double getMaxAttackers(int territoryID) {
        return maxAttackers[territoryID];
    }

    public double getMaxDefenders(int territoryID) {
        return maxDefenders[territoryID];
    }

    public double getMinAttackers(int territoryID) {
        return minAttackers[territoryID];
    }

    public double getMinDefenders(int territoryID) {
        return minDefenders[territoryID];
    }

    public long getWins(int territoryID) {
        return wins[territoryID];
    }

    public double getWinRate(int territoryID) {
        // the percent of times an attack by/agains this territory succeeded
        if (attacks[territoryID] > 0) {
            return wins[territoryID]/attacks[territoryID];
        }
        return -1;
    }

}
