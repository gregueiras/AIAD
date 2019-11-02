package behaviours;

import agents.AgentBoard;
import helper.NegotiationPair;
import helper.Shift;
import helper.State;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class AssignInvestors extends OneShotBehaviour {

    private AgentBoard agent;

    public AssignInvestors(AgentBoard agent) {
        this.agent = agent;
        super.setBehaviourName("Assign_Investors");
    }


    @Override
    public void action() {
        Shift shift = agent.getCurrentShift();

        for(NegotiationPair np: shift.getPairs()){
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setSender(this.agent.getAID());
            try {
                msg.setContentObject(np.getInvestor());
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg.addReceiver(np.getManager());
            msg.setConversationId(State.ASSIGN_INVESTOR.toString());
            agent.send(msg);
        }
        System.out.println("AssignInvestors.action");
    }
}
