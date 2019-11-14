package behaviours;

import agents.AgentBoard;
import helper.Logger;
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
        MessageTemplate mt = MessageTemplate.MatchConversationId(State.INFORM_BOARD.toString());
        ACLMessage msg = this.agent.receive(mt);
        if(msg!=null){
            this.nrResponses++;
            this.agent.handleEndNegotiationMsg(msg);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        int maxNrResponses = this.agent.getManagers().size();
        return this.nrResponses >= maxNrResponses;
    }

    public int onEnd() {
        this.nrResponses = 0;
        Logger.print(this.agent.getLocalName(), "CURRENT ROUND: " + this.agent.getCurrentRound());
        if(!this.agent.isEndRound()) { // is not end round, so it should update the current shift to the next in the round
            this.agent.incCurrentShift();
            return 0;
        }
        else {
            if(!this.agent.isEndGame()) { // is not end game, so it should update the current round to the next in the game
                this.agent.incCurrentRound();
                this.agent.resetCurrentShift(); //sets the current shift to 0
                return 1;
            }

            return 2; // is end of game
        }
    }
}
