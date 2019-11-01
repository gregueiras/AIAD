package behaviours;

import agents.OurAgent;
import helper.MessageType;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessage extends OneShotBehaviour {

    private OurAgent agent;
    private MessageType messageType;
    public SendMessage(OurAgent agent, MessageType messageType) {
        this.agent = agent;
        this.messageType = messageType;
    }

    @Override
    public void action() {
        this.agent.sendMessage(this.messageType);
    }
}