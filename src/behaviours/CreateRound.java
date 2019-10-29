package behaviours;

import agents.AgentBoard;
import agents.AgentType;
import helper.NegotiationPair;
import helper.Shift;
import helper.Round;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import javax.sound.midi.SysexMessage;
import java.util.Collections;
import java.util.LinkedList;
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
                        System.err.println("Invalid pairing");
                        return null;
                    }

                    NegotiationPair pair = new NegotiationPair(manager,investor);
                    shift.addPair(pair);
                }
                else {
                    System.out.println("more managers than investors");
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
        Round r;
        List<AID> managers = this.agent.getManagers();
        List<AID> investors = this.agent.getInvestors();
        Collections.shuffle(managers);
        Collections.shuffle(investors);
        if(this.agent.getManagers().size() >= this.agent.getInvestors().size())
            r = this.createRound(managers, investors, AgentType.MANAGER, AgentType.INVESTOR);
        else
            r = this.createRound(investors, managers, AgentType.INVESTOR,  AgentType.MANAGER);
        if(r != null)
            this.agent.setRound(r);

        System.out.println("ROUND TABLE: " + this.agent.getRound());

    }
}
