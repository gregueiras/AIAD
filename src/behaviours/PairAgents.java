package behaviours;

import agents.AgentBoard;
import agents.AgentType;
import helper.NegotiationPair;
import helper.Shift;
import helper.ShiftTable;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

public class PairAgents extends OneShotBehaviour {

    private AgentBoard agent;


    public PairAgents( AgentBoard agent) {
        this.agent = agent;
        super.setBehaviourName("Pair_Agents");
    }

    private ShiftTable pairAgents(List<AID> a1, List<AID> a2, AgentType a1Type, AgentType a2Type){
        int start = 0;
        int size = a1.size();
        ShiftTable shiftTable = new ShiftTable();
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
            }
            start++;
            shiftTable.addShift(shift);
        }

        return shiftTable;
    }

    @Override
    public void action() {
        ShiftTable rt;
       if(this.agent.getManagers().size() >= this.agent.getInvestors().size())
          rt = this.pairAgents(this.agent.getManagers(), this.agent.getInvestors(), AgentType.MANAGER, AgentType.INVESTOR);
       else
          rt = this.pairAgents(this.agent.getInvestors(), this.agent.getManagers(), AgentType.INVESTOR,  AgentType.MANAGER);

       if(rt != null)
           this.agent.setShiftTable(rt);

        System.out.println("ROUND TABLE: " + this.agent.getShiftTable());

    }
}
