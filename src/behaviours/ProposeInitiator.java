package behaviours;

import agents.AgentManager;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class ProposeInitiator extends OneShotBehaviour {

    private AgentManager agent;

    public ProposeInitiator(AgentManager agent) {
        this.agent = agent;
        super.setBehaviourName("Propose_Initiator");
    }

    @Override
    public void action() {
        System.out.println("ProposeInitiator.action " + this.agent.getInvestor());
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.agent.getAID());
        msg.setContent("ola ola");
        msg.addReceiver(this.agent.getInvestor());
        msg.setConversationId("negotiate");
        this.agent.send(msg);
        System.out.println("ProposeInitiator.action " + msg.getContent());

    }
}
