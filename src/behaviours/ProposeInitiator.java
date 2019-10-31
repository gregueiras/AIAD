package behaviours;

import agents.AgentManager;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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

        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("negotiate"),
                MessageTemplate.MatchInReplyTo("negotiate"));
        ACLMessage reply = this.agent.receive(mt);

        while (reply == null) {
            reply = this.agent.receive(mt);
        }
        System.out.println("received : " + reply.getContent());

    }
}
