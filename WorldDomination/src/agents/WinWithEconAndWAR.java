package agents;
import java.util.HashMap;
import java.util.Random;
import risk.*;
import sim.util.Bag;

/**  NAME AGENT TRADENORRIS. Haha.
 * So the Goal here is to have an agent that allocates resources across all
 * *own* territories until marginal product = marginal cost.
 *
 * Basically, think about the following: a territory can:
 *   a.  produce output
 *   b.  produce taxes by making war and conquering
 *   c.  produce X lost taxes/output by being conquered
 *
 * The decision will always be:
 *   a. Do I attack?
 *      -- Note that this is perhaps two decisions:
 *         1. "still implement yesterday's plan?" If so, do it.
 *         2. "plan to attack tomorrow" and position folks
 *      i.  If so, who?
 *   b. Do I trade?  (probably not) (actually, wait, ALWAYS make a lowball
 *      offer to everyone. Always. Never know when someone might take it.)
 *      -- make sure to always make "dumb trade" offer to everyone.
 *      -- offer people next door to you who are enemies an "equal food for
 *         person" trade. Always. The goal is to always drain people from a
 *         possibly enemy. Because they might not think to train new ones.
 *      Order of activity: see Imperial class.
 *      i.  If so, who?
 *      ii. When deciding how much to trade, trade to point that marginal
 *          cost/whatever is equaled. So each territory has a "peasants" output
 *          and a "food" output. Always try to trade to get max of these.
 *
 *   c. "How to tax and redistribute?"
 *      i. always always always tax the one fellow who said he'll always revolt,
 *         as much as possible.
 *      ii. Otherwise, need to think about taxing and asking for troops.
 *      iii. NOTE: need to always take "1/2" troops from each territory and put
 *           to (nearest) homeland. THEN, whenever going to attack,
 *           ONLY redistribute to conquered lands that are "on the boundary."
 *           ALWAYS drain the territories that *arn't* on the boundary.
 *           --> here is the idea: always weaken all other players, and always
 *           aim to throw off any people above you.
 *      iv. IF handed soldiers by redistribution when captured, ALWAYS send
 *          them away instantly to non-conquered territory.
 *          ALWAYS aim to "save resources" by sending them to non-conquered
 *          people, OR revolt immediately. OR look at tax rate, "troop ask rate."
 *
 *   Thing to leave off until the final round:
 *   -- alliance stuff
 *   -- any "transfer by trading across territories" hack.
 *
 *   c. How many peasants to turn into soldiers?
 *   d. Where to position those soldiers?
 *      -- Note: this is a prelude to making war the next turn.
 *
 *
 *
 *
 * @author npalmer4
 */


public class WinWithEconAndWAR extends Agent{
    int id;
    int type;
    double myAlpha;
    double lambda = 0.85;  //The learning parameter on "friendliness"

    HashMap<Integer, Float> friendlyParameter = new HashMap<Integer, Float>();
    
    public WinWithEconAndWAR(int id, int type){
        super(id, type);
        this.id = id;
        this.type = type;
        empireName = "WinWithEconAndWAR";
        //this.myAlpha = this.myTerritory.getAlpha();

        //WHERE AT: really need
//        for(int i=0; i<this.myTerritory.getNeighbors().size(); i++) {
//            Territory tempTerritory = (Territory)this.myTerritory.getNeighbors().get(i);
//            friendlyParameter.put(tempTerritory.getId(), 0.5f);
//            // Start out 50-50 with everyone.
//            // As things go along, each time an attack occurs, update this
//            // for everyone.
//
//        }

    }

    public void updateFriendlyParameterAttacked(int ID) {

    }

    public void checkAndAddFriendlyParameter(int ID) {
        /* This of course only occurs if someone attacks me. SO, they start off
         * with a swipe. So add the agent, and immediately call the other...
         *
         */

        this.updateFriendlyParameterAttacked(ID);
    }




    /*
    * Should update attackedTerritoryID and the attackingSoldiers
    */
    public void attack(){
        /*
         * Deciding who to attack:
         * Perhaps attack everyone with 1 soldier, then ID the group to attack/
         * defend against next turn.
         * Rank neighbors from top-to-bottom by number of soldiers. 
         * Neighbor-to-attack is easy: attack the one with the least soldiers.
         * If there is a tie, attack randomly.
         * Neighbor-to-defend: Reinforce strategically. If one has N neighbors,
         * rank them by order of soldiers. In an ideal world, construct a
         * discrete distribution of enemies each territory is facing and distribute
         * soldiers to all territories based on this distribution.
         *
         * Recall that still only going to give subordinates (1/4) - (1/2) the soldiers that
         * are given to own continents.
         *
         * Recall also, the list of attackign priorities.
         *
         *
         * Additional attackign priority:
         * (1)  figure out which neighbors have the best resources, and attack those
         *      first.
         *
         */
        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants()); 
        Double temp = myTerritory.getSoldiers();
        attackingSoldiers = Math.floor(temp*2/3);
        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;

    }

    /*
    * Should update the trade array. The positions if the array should contain information in the following order:
    * 0: ID of the potential trading partner
    * 1: Type of good demanded (1 for natural resources, 2 for peasants, and 3 for soldiers)
    * 2: Amount of the demanded good
    * 3: Type of good offered in exchange
    * 4: Amount of the offered good
    */
    protected void trade(){
        // The goal here is to make all possible trade offeres that would benefit me, to everyone
        // in the game.
        // RECALL that the beneficial trade is *always* on the margin of production.
        // This, of course, means I need to determine the margin of production.
        // Will need to approximate a the derivative around wherever I've chosen
        // to produce, then decide how much more to ask for, for one unit of
        // whatever.
        //
        // The major thing to add here is the trading-at-the-margin. 
        //
        //
        //
        // Need to make all beneficial offeres to all other parties. Period.

        // Ah, it appears as though only one possible offer can be made at a time.
        // Hmmm.
        int counter = 0, tradeTries = 10;
        boolean thisIsMe = false;
        this.trade[0] = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
        // Select a random neighbor.
        thisIsMe = this.trade[0] == this.myTerritory.getId();

        while (thisIsMe & counter < tradeTries) {
            // only look for a trading partner 10 times.
            this.trade[0] = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
            thisIsMe = this.trade[0] == this.myTerritory.getId();
            counter++;
        }

        if (thisIsMe & tradeTries == 10) {
            //Don't trade with self.
            this.trade[1] = 0;
            this.trade[2] = 0;
            this.trade[2] = 0;
            this.trade[4] = 0;
        } else {
            // make a random beneficial-to-me offer.
            this.trade[1] = 2;
            this.trade[2] = 2;
            this.trade[2] = 2;
            this.trade[4] = 1;
        }



    }

    /*
    * Should update acceptTrade.
    */
    protected void acceptTrade(Territory offerer, double demand, int typeDemand,
            double offer, int typeOffer){
        // The


    }

    /*
    * Should update tax.
    */
    protected void chooseTax(){
        this.tax = 0.5d;
        // way to make this adjustable: by-person. Create a "dislike agent"
        // parameter and put it in a hash-table, indexed by agents.
        // Update that parameter each time an agent attacks me.
        // Make the learnign parameter on this process not terribly high --
        // I presume agents won't change how they are acting as quickly as
        // people would.
        // The tricky thing will be to figure out how
    }

    /*
    * Should update the beneficiaries bag and the retributions array.
    */
    protected void setRetributionsAndBeneficiaries(){
        /* Reinforce own lands a lot. Look above at the "attacking" strategies,
         * and strategies in own notes.  Own lands are important to keep safe.
         * Final-reweight will always be towards protecting own lands...
         *
         * Idea is to be friendly towards people being friendly. Keep a "friendly"
         * parameter, that one updates frequenty. This can be the same one as used above.
         */


    }

    /*
    * Should update the defendingSoldiers
    */
    protected void defend(Territory attaker, double soldiersAttacking){
        // Currently, defend with min(2*soldiersAttacking, everyone I have)
        defendingSoldiers = Math.min(myTerritory.getSoldiers(), 2.0*soldiersAttacking);

    }

    /*
    * Provides the information about the battle outcome, you can do what ever you want with it.
    */
    protected void battleOutcome(long period, int attackerID, double soldiersAttack,
            int deffenderID, double soldiersDefend, boolean youWon){
        // period: step of the simulation in which the battle occured
        // attackerID: id of the territory that took the offensive position during the battle
        // soldiersAttack: number of soldiers that attacked
        // deffenderID: id of the territory that took the defensive position during the battle
        // soldiersDefend: number of soldiers that defended
        // youWon: indicator if the current territory won or lost the battle (true if victorius, false if not)

        /*
         * 
         *
         */

    }

    /*
    * Provides the information about the trade outcome, it is called every time a trade process is finished and you can do what ever you want with it.
    */
    protected void tradeOutcome(long period, int proposerID, double[] tradeProposal, boolean tradeCompleted){
        // period: step of the simulation in which the trade occured
        // proposerID: id of the territory that created the trade proposal (it can be yourself)
        // tradeProposal: the actual trade proposal that was either sent or received by your lord.
        // The information in the tradeProposal array is organized in the exact same way as in your own tradeProposal
        // so you can store and use that information as you wish
        // tradeCompleted: an indicator if the transaction was completed (true) or not (false)
    }

    public int getType(){
        return this.type;
    }

    public int getId(){
        return this.id;
    }

    public String getName() {
        return empireName;
    }

    public double[] getTrade() {
        return trade;
    }

    public boolean isAcceptTrade() {
        return acceptTrade;
    }

    public double getTax() {
        return tax;
    }

    void setMyTerritory(Territory myTerritory) {
        this.myTerritory = myTerritory;
    }

    protected Bag getBeneficiaries() {
        return beneficiaries;
    }

    protected double[] getRetributions() {
        return retributions;
    }


}
