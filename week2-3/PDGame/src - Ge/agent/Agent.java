
package agent;

public class Agent {
	public   Logics l=new Logics();
	public    Operations o=new Operations();
	public   Experiences e=new Experiences();
	String ID="default ID";

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
	public  void notifyPlayers(Agent player,Agent adversary,int playerOperation,int adversaryOperation,
	int playerPenalty,int adversaryPenalty) throws InterruptedException{
		e.notifiedExperience(adversary, playerOperation, adversaryOperation, playerPenalty, adversaryPenalty);
	}

	public void resetExperience(){
		e=new Experiences();
	}

	public void resetLogics(){
		l=new Logics();
	}
}
