package behaviours;

import agents.AgentBoard;
import agents.OurAgent;
import helper.State;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitForMessages extends SimpleBehaviour {

    private OurAgent agent;
    private int nrMessages;
    private int maxNrMessages;
    private int type;

    public WaitForMessages(OurAgent agent, int type, int maxNrMessages) {
        this.agent = agent;
        this.nrMessages = 0;
        this.maxNrMessages = maxNrMessages;
        this.type = type;
    }

    @Override
    public void action() {
        System.out.println("waifForMessages.action");
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = this.agent.receive(mt);
        if(msg!=null){
            this.nrMessages++;
            this.agent.handleMessage(msg);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        if(this.nrMessages >= this.maxNrMessages ) {
            return true;
        } else return false;
    }

    @Override
    public int onEnd() {
        return 0;


    }
}
