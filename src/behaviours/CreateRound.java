package behaviours;

import agents.AgentBoard;
import agents.AgentType;
import helper.Logger;
import helper.NegotiationPair;
import helper.Round;
import helper.Shift;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import java.util.Collections;
import java.util.List;

public class CreateRound extends OneShotBehaviour {

    private AgentBoard agent;


    public CreateRound(AgentBoard agent) {
        this.agent = agent;
        super.setBehaviourName("Create_Round");
    }

    private Round createRound(List<AID> a1, List<AID> a2, AgentType a1Type, AgentType a2Type){
        int start = 0;
        int size = a1.size();
        Round round = new Round();
        while(start < size) {
            int count = 0;
            Shift shift = new Shift();
            for(int i = start;; i++){
                if(i >= size){
                    i = 0;
                }
                if(i == start){
                    count++;
                    if(count >=2)
                        break;
                }

                int index = (i+size-start)%size;
                if(index < a2.size()){
                    AID manager, investor;

                    if(a1Type == AgentType.MANAGER && a2Type == AgentType.INVESTOR){
                        manager = a1.get(i);
                        investor = a2.get(index);
                    } else if(a2Type == AgentType.MANAGER && a1Type == AgentType.INVESTOR) {
                        investor = a1.get(i);
                        manager = a2.get(index);
                    } else {
                        Logger.print(this.agent.getLocalName(), "Invalid pairing");
                        return null;
                    }

                    NegotiationPair pair = new NegotiationPair(manager,investor);
                    shift.addPair(pair);
                }
                else {
                    Logger.print(this.agent.getLocalName(), "more managers than investors");
                    if(a1Type == AgentType.MANAGER){
                        NegotiationPair pair = new NegotiationPair(a1.get(i),null);
                        shift.addPair(pair);
                    }
                }
            }
            start++;
            round.addShift(shift);
        }

        return round;
    }


    @Override
    public void action() {
        Logger.print(this.agent.getLocalName(), "CreateRound.action");

        Round r;
        List<AID> managers = this.agent.getListManagers();
        List<AID> investors = this.agent.getListInvestors();
        Collections.shuffle(managers);
        Collections.shuffle(investors);
        if(this.agent.getManagers().size() >= this.agent.getInvestors().size())
            r = this.createRound(managers, investors, AgentType.MANAGER, AgentType.INVESTOR);
        else
            r = this.createRound(investors, managers, AgentType.INVESTOR,  AgentType.MANAGER);
        if(r != null)
            this.agent.setRound(r);

        Logger.print(this.agent.getLocalName(), this.agent.getRound().toString());
    }
}
