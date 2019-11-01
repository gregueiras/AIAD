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
    }

    @Override
    public void action() {
        this.agent.sendMessage(this.state);
    }
}