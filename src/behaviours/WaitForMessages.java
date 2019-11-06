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
    private boolean stop;
    private String content;

    public WaitForMessages(OurAgent agent, int type, int maxNrMessages, State state) {
        this.agent = agent;
        this.nrMessages = 0;
        this.maxNrMessages = maxNrMessages;
        this.type = type;
        this.state = state;
        this.stop = false;
        this.content = "";
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
            if(msg.getConversationId().equals(State.WAIT_END_SHIFT_ROUND.toString()) && msg.getContent().equals(State.GAME_END.toString()))
            {
                this.stop = true;
                this.content = msg.getContent();
            }
            this.agent.handleMessage(msg);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
      return (this.nrMessages >= this.maxNrMessages || this.stop);
    }

    @Override
    public int onEnd() {
        return this.agent.onEnd(this.state, this.content);
    }
}
