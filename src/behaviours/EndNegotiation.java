package behaviours;

import agents.AgentBoard;
import helper.State;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EndNegotiation extends SimpleBehaviour {

    private AgentBoard agent;
    private int nrResponses;

    public EndNegotiation(AgentBoard agent) {
        this.agent = agent;
        this.nrResponses = 0;
    }

    @Override
    public void action() {
        System.out.println("endNegotiation.action");
        MessageTemplate mt = MessageTemplate.MatchConversationId(State.INFORM_BOARD.toString());
        ACLMessage msg = this.agent.receive(mt);
        if(msg!=null){
            System.out.println("board received msg with content: " + msg.getContent());
            this.nrResponses++;
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        int maxNrResponses = this.agent.getManagers().size();
        if(this.nrResponses >= maxNrResponses ) {
            return true;
        } else return false;
    }
}
