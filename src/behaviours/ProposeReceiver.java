package behaviours;

import agents.OurAgent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

public class ProposeReceiver extends WaitForMessage {
    public ProposeReceiver(OurAgent a) {
        super(a, MessageTemplate.MatchConversationId("negotiate"), 0);
    }

    @Override
    protected void handleMessage(ACLMessage msg) {
        System.out.println("i am receiving: " + msg.getContent());
        ACLMessage reply = msg.createReply();
        reply.setInReplyTo("negotiate");
        reply.setPerformative( ACLMessage.INFORM );
        reply.setContent("oi oi");
        this.agent.send(reply);
        System.out.println("sent reply");
    }
}
