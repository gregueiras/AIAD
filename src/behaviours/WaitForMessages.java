package behaviours;

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
    private State state;

    public WaitForMessages(OurAgent agent, int type, int maxNrMessages, State state) {
        this.agent = agent;
        this.nrMessages = 0;
        this.maxNrMessages = maxNrMessages;
        this.type = type;
        this.state = state;
    }

    public WaitForMessages(OurAgent agent, int type, int maxNrMessages) {
        this.agent = agent;
        this.nrMessages = 0;
        this.maxNrMessages = maxNrMessages;
        this.type = type;
        this.state = State.DEFAULT;
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
      return this.nrMessages >= this.maxNrMessages;
    }

    @Override
    public int onEnd() {
        return this.agent.onEnd(this.state, "");
    }
}
