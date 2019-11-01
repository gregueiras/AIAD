package behaviours;

import agents.AgentBoard;
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
        MessageTemplate mt = MessageTemplate.MatchConversationId("negotiation-end");
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
        if(this.nrResponses > 1) {
            return true;
        } else return false;
    }
}
