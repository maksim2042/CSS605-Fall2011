package agents;
import risk.*;
import sim.util.Bag;
public class MilitaryHistory {
    private int[] HistoricalRecord = new int[5];
    private static final int BATTLE_COUNT = 0;
    private static final int DEFENSE_COUNT = 1;
    private static final int ATTACK_COUNT = 2;
    private static final int DEFENSE_SUCCESS = 3;
    private static final int ATTACK_SUCCESS = 4;

    public static final int ATTACK_TERRITORY = 0;
    public static final int WITH_ATTACK_INTENSITY = 1;





    java.util.Map<Boolean,java.util.List<BattleOutcome>> attacks =
           new java.util.HashMap<Boolean,java.util.List<BattleOutcome>> ();
    java.util.Map<Boolean,java.util.List<BattleOutcome>> defenses =
           new java.util.HashMap<Boolean,java.util.List<BattleOutcome>> ();
    java.util.Map<Integer,java.util.List<BattleOutcome>> defensesByInvader =
           new java.util.HashMap<Integer,java.util.List<BattleOutcome>> ();
    java.util.Map<Integer,java.util.List<BattleOutcome>> attackeByTarget =
           new java.util.HashMap<Integer,java.util.List<BattleOutcome>> ();

    /** how many soldiers should I use for the attack?*/
    double[] attackPreparation(double myAttackIntensity, Territory myTerritory) {
        //how many soldiers do they currently have
        //what was their rate of increase under past attacks
        //can we over-stretch their resources

        //Who can I attack?
        Bag potentialTargets = new Bag();
        if (myTerritory.getSuperior() != null)  potentialTargets.add(myTerritory.getSuperior());
        for (int i=0; i < myTerritory.getNeighbors().numObjs; i++) {
            Territory temp = (Territory) myTerritory.getNeighbors().get(i);
            if (temp.getType() != myTerritory.getType()) potentialTargets.add(temp);
        }


        double terrWeakest = - 1; double weakestMil = 1000000000;
        double terrWealthiest = -1; double highWealth = -1;
        double terrPopulous = -1; double highPopulation = -1;

        for(int i=0; i < potentialTargets.numObjs; i++) {
            Territory temp = (Territory) potentialTargets.get(i);
            //assess resources
            double t_resources = temp.getNatRes();
            double t_soldiers = temp.getSoldiers();
            double t_peasants = temp.getPeasants();

            //find weakest target
            if (t_soldiers < weakestMil) {
                terrWeakest = temp.getId();
                weakestMil = temp.getSoldiers();
            }
            //find post populous target (risk for soldier creation) 
            if (t_resources > highPopulation ) {
                terrPopulous = temp.getId();
                highPopulation = temp.getPeasants();
            }
            //find wealthiest target
            if (t_resources > highWealth ) {
                terrWealthiest = temp.getId();
                highWealth = temp.getNatRes();
            }
            double potentialSoldiers =
                    (temp.getSoldiers() + Math.pow(temp.getNatRes(), temp.getAlpha()) * Math.pow(temp.getPeasants(), 1 - temp.getAlpha()));
        }

        //make a decision

        if (myTerritory.getSoldiers() < weakestMil) {
            double req = weakestMil - myTerritory.getSoldiers();
            req = 1.05 * req;
            myTerritory.produceSoldiers(req , req);
        }
        double attackingSoldiers = myTerritory.getSoldiers();
       // System.out.println("Attack " + attackedTerritoryID) ;
        return new double[] {terrWeakest ,attackingSoldiers};
    }

    class TerritoryResourceDesc implements Comparable {
        int territoryId;
        double soldiers;
        double peasants;
        double nresources;
        public TerritoryResourceDesc( int territoryId, double soldiers, double peasants, double nresources) {
            this.territoryId = territoryId;
            this.soldiers = soldiers;
            this.nresources = nresources;
            this.peasants = peasants;
        }
        public int compareTo(Object obj) {
            return new Double(this.soldiers).compareTo(new Double(((TerritoryResourceDesc)obj).soldiers));
        }
    }

    public void assessRisk(Bag subordinates) {
        for (int i = 0 ; i < subordinates.numObjs; i++) {

        }
        Bag neighbors = new Bag();
        
    }

    public void recordBattle(BattleOutcome battleOutcome, boolean defense) {
        HistoricalRecord[BATTLE_COUNT]++;
        if (defense) {
            HistoricalRecord[DEFENSE_COUNT]++;
            if (battleOutcome.youWon()) HistoricalRecord[DEFENSE_SUCCESS]++;
            if (!defenses.containsKey(battleOutcome.youWon())) {
                defenses.put(battleOutcome.youWon(),new java.util.LinkedList<BattleOutcome>()) ;
            }
            defenses.get(battleOutcome.youWon()).add(battleOutcome);
            if (!defensesByInvader.containsKey(battleOutcome.attackerID()))
                defensesByInvader.put(battleOutcome.attackerID(), new java.util.LinkedList<BattleOutcome>());
            defensesByInvader.get(battleOutcome.attackerID()).add(battleOutcome);
        }
        else {
            HistoricalRecord[ATTACK_COUNT]++;
            if (battleOutcome.youWon()) HistoricalRecord[ATTACK_SUCCESS]++;
            if (!attacks.containsKey(battleOutcome.youWon())) {
                attacks.put(battleOutcome.youWon(),new java.util.LinkedList<BattleOutcome>()) ;
            }
            attacks.get(battleOutcome.youWon()).add(battleOutcome);
            if (!attackeByTarget.containsKey(battleOutcome.deffenderID()))
                attackeByTarget.put(battleOutcome.deffenderID(), new java.util.LinkedList<BattleOutcome>());
            attackeByTarget.get(battleOutcome.deffenderID()).add(battleOutcome);
        }
        /*
        for (int x : HistoricalRecord)
            System.out.print(x + ":");
        System.out.println();
         */
    }
}


class BattleOutcome {
    private long period;
    private boolean youWon;
    private double soldiersAttack, soldiersDefend;
    private int deffenderID, attackerID;

    BattleOutcome(long period, int attackerID, double soldiersAttack,
        int deffenderID, double soldiersDefend, boolean youWon) {
        this.period = period;
        this.attackerID = attackerID;
        this.soldiersAttack = soldiersAttack;
        this.deffenderID = deffenderID;
        this.soldiersDefend = soldiersDefend;
        this.youWon = youWon;
    }
    long period() { return this.period; }    boolean youWon() { return this.youWon; }
    int attackerID() { return this.attackerID; }   int deffenderID() { return deffenderID; }
    double soldiersAttack() { return soldiersAttack; } double soldiersDefend () { return soldiersDefend; }
}

