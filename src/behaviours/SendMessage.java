package behaviours;

import agents.OurAgent;
import helper.State;
import jade.core.behaviours.OneShotBehaviour;

public class SendMessage extends OneShotBehaviour {

    private OurAgent agent;
    private State state;
    public SendMessage(OurAgent agent, State state) {
        this.agent = agent;
        this.state = state;
        super.setBehaviourName("Create_Round_" + state.toString());
    }

    @Override
    public void action() {
        System.out.println("SendMessage.action: " + state.toString());
        this.agent.sendMessage(this.state);
    }

    @Override
    public int onEnd(){
        return 0;
    }
}