package risk;
import java.awt.Color;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
public class Territory {
    protected Bag physicalNeighbors =  new Bag(); // This is the list of initial neighbors, it never changes and it is used as an internal reference for updating the neighbors bag when your empire expands. You do not need to use it.
    private Bag neighbors = new Bag(); // The updated list of neighbors of the territory, includying the new ones that you get when you conquer other territories
    private Bag subordinates = new Bag(); // The list of territories that you have conquered
    private Territory superior = null; // The territory that has conquered you. If it is null that means no one has conquered you (you don't pay taxes).
    private Agent ruler = null; // This is the actual Lord that rules over the territory.
    private int id; // ID of the territory
    private int type; // A number that indicates the type of the lord. For example, each one of your lords is of the same type, so you can use this to identify if a territory is ruled by a lord of your own type.
    private double peasants; // Number of peasants
    private double natRes; // Number of natural resources
    private double alpha; // The alpha parameter of the production function
    private double peasantsGrowth; // The number of peasants that are borned every period in the territory
    private double natResGrowth; // The number of natural resources that are borned every period in the territory
    private double soldiers; // The number of soldiers that exist in the territory
    protected Color rulerColor; // The color of the ruler of the territory, you don't need to use this.


// HERE GOES A BUNCH OF PUBLIC METHODS THAT YOUR LORDS CAN USE TO OBTAIN INFORMATIONN ABOUT THE TERRITORIES:

    public Territory(){}

    //Constructor of the territory
    public Territory(int id, double farmGrowth, double foodGrowth, double alpha){
        this.id = id;
        this.peasantsGrowth = farmGrowth;
        this.natResGrowth = foodGrowth;
        this.alpha = alpha;
        soldiers = 0;
    }

    /**
     * This method can be called in order to produce soldiers. You need to provide the amount of natural resources
     * and peasants. Once the soldiers are created, the inputes used are subsatcted from the stock.
     */
    public void produceSoldiers(double natRes, double peasants){
        if(peasants>this.peasants){
            peasants = this.peasants;
        }
        if(peasants<0){
            peasants = 0;
        }
        if(natRes>this.natRes){
            natRes = this.natRes;
        }
        if(natRes<0){
            natRes = 0;
        }
        soldiers = (soldiers + Math.pow(natRes, alpha) * Math.pow(peasants, 1 - alpha));
        addPeasants(-peasants);
        addNatRes(-natRes);
    }

    //Returns the ID of the territory (it corresponds to the index shown in the instructions' map)
    public int getId(){
        return id;
    }

    /**
     * Returns a bag with the territories that are the neighbors (Bag is a collection class from the MASON
     * library so you should check the corresponding API to see its methods) (remember that collections hold objects,
     * so remember to cast them into Territory)
     */
    public Bag getNeighbors() {
        Bag neighborsCopy = new Bag();
        for (int i=0; i<neighbors.numObjs; i++){
            neighborsCopy.add(neighbors.get(i));
        }
        return neighborsCopy;
    }

    // Returns the territory that is the superior in the hierarchy (null if there is no such territory)
    public Territory getSuperior() {
        return superior;
    }

    // Returns a bag with the subordinate territories.
    public Bag getSubordinates(){
        Bag subordinatesCopy = new Bag();
        for (int i=0; i<subordinates.numObjs; i++){
            subordinatesCopy.add(subordinates.get(i));
        }
        return subordinatesCopy;
    }

    /**
     * Returns the type of the lord in command. Type is the number of the corresponding empire, therefore,
     * territories of lords from the same empire are of the same type.
     */
    public int getType(){
        return type;
    }

    // Returns the number of peasants.
    public double getPeasants() {
        return peasants;
    }

    // Returns the number of natural resources.
    public double getNatRes() {
        return natRes;
    }

    // Returns the number of soldiers.
    public double getSoldiers() {
        return soldiers;
    }

    // Returns the alpha of the Cobb-Douglass production function.
    public double getAlpha() {
        return alpha;
    }

    // Returns the number of natural resources that grow every period.
    public double getFarmGrowth() {
        return peasantsGrowth;
    }


    // Returns the number of peasants that grow every period.
    public double getFoodGrowth() {
        return natResGrowth;
    }

    // Returns the id of the territory. Redundant with getID() but, what the hell...
    @Override
    public String toString(){
        return Integer.toString(id);
    }

    // Returns the tax tare imposed by the lord of this territory
    public double getTaxRate(){
        return ruler.getTax();
    }

    // Returns a copy of the list of lucky territories that have been chosen by the current one, to receive
    // a generous amount of soldiers
    public Bag getBeneficiaries(){
        Bag beneCopy = new Bag();
        for (int i=0; i<ruler.beneficiaries.numObjs; i++){
            beneCopy.add(ruler.getBeneficiaries().get(i));
        }
        return beneCopy;
    }


//***************************************************************************************************************************************************************************

// THE FOLLOWING ARE METHODS THAT ARE ACCESIBLE ONLY TO THE RISK PACKAGE, SO YOUR LORDS
// HAVE NO ACCESS TO THEM, THEY ARE MADE FOR INTERNAL PURPOSES OF THE SYSTEM:

    // Grow peasants and natural resources according to the established rates.
    protected void grow(){
        peasants += peasantsGrowth;
        natRes += natResGrowth;
    }

    // Feed the soldiers with the stock of natural resources. If not all the soldiers can be fed, the exceeding ones will die.
    void feedSoldiers(){
        if (getSoldiers()<=getNatRes()){
            natRes -= getSoldiers();
        } else{
            soldiers = getNatRes();
            natRes=0;
        }
    }

    // Returns the lord that is ruling
    Agent getRuler(){
        return ruler;
    }

    // Returns the neighboring territories
    Bag getNeighborTerritories(){
        return neighbors;
    }

    // Returns the subordinates
    Bag getConquered() {
        return subordinates;
    }

    // Returns the head of the hierarchy
    Territory getHead(){
        if (superior!=null){
            return superior.getHead();
        }
        else{
            return this;
        }
    }

    // Adds a subordinate to the list
    void addSubordinate(Territory subordinate){
        if(!subordinates.contains(subordinate) && !this.equals(subordinate)){
            subordinates.add(subordinate);
        }
    }

    // Removes a subordinate from the list
    void removeSubordinate(Territory subordinate){
        subordinates.remove(subordinate);
    }

    // Returns the number of territories of a different type that are in a lower level of the hierarchy
    double countDiffSubordinates(int type){
        double numOfSub = 0;
        for (int i=0; i<subordinates.numObjs; i++){
            Territory subordinate = (Territory)subordinates.get(i);
            if(subordinate.getType()!=type){
                numOfSub++;
            }
            numOfSub += subordinate.countDiffSubordinates(type);
        }
        return numOfSub;
    }

    // Returns the number of territories of the same type that are in a higher level of the hierarchy
    double countSameSuperiors(int type){
        double sameSup = 0;
        if(superior!=null){
                if(superior.getType()==type){
                    sameSup++;
                }
            sameSup += superior.countSameSuperiors(type);
        }
        return sameSup;
    }

    // Checks if a territory is in a higher level in the hierarchy than the current one
    boolean isAbove(Territory territory){
        boolean isAbove = false;
        if(superior!=null){
            if (superior.equals(territory)){
                isAbove = true;
            }
            else{
                isAbove = superior.isAbove(territory);
            }
        }
        return isAbove;
    }

    // Points the list of subordinates to another list
    void setSubordinates(Bag subordinates) {
        this.subordinates = subordinates;
    }

    // Sets the territory that has conquered the current one
    void setSuperior(Territory superior) {
        this.superior = superior;
    }

    // Sets the lord of the current territory
    void setRuler(Agent ruler){
        type = ruler.getType();
        this.ruler = ruler;
    }

    // Updates the neighbors of the territory.
    void updateNeighbors() throws CloneNotSupportedException{
        neighbors = (Bag) physicalNeighbors.clone();
        for (int i=0; i<subordinates.numObjs; i++){
            Territory subordinate = (Territory)subordinates.get(i);
            for(int j=0; j<subordinate.getNeighbors().numObjs; j++){
                if(!neighbors.contains(subordinate.getNeighbors().get(j)) && !this.equals(subordinate.getNeighbors().get(j))){
                    neighbors.add(subordinate.getNeighbors().get(j));
                }
                neighbors.remove(this);
            }
        }
    }

    // Add peasants to the current stock
    void addPeasants(double peasants) {
        this.peasants += peasants;
    }

    // Add natural resources to the current stock
    void addNatRes(double natRes) {
        this.natRes += natRes;
    }

    // Add soldiers to the current stock
    void addSoldiers(double soldiers) {
        this.soldiers += soldiers;
    }

    // Check if a territory is in the same hierarchical structure of the current one.
    boolean isInHierarchy(Territory territory){
        boolean isHere = false;
        for(int i=0; i<subordinates.numObjs; i++){
            Territory subordinate = (Territory)subordinates.get(i);
            if (isHere==false){
                if(subordinate.equals(territory)){
                    isHere = true;
                    break;
                }
                else{
                    isHere = subordinate.isInHierarchy(territory);
                }
            }
        }
        return isHere;
    }

}