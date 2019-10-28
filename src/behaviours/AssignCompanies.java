package behaviours;

import agents.AgentBoard;
import agents.OurAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import market.Company;
import market.InvestmentType;

import java.util.List;
import java.util.Map;

public class AssignCompanies extends OneShotBehaviour {

    private boolean companiesAssigned;
    private AgentBoard agent;
    private Map<InvestmentType,List<Company>> initialCompanies;

    public AssignCompanies(AgentBoard agent, Map<InvestmentType,List<Company>> initialCompanies) {
        this.companiesAssigned = false;
        this.agent = agent;
        this.initialCompanies = initialCompanies;

        super.setBehaviourName("Assign_companies");
    }

    @Override
    public void action(){
        if(this.agent.getManagers() != null && !this.agent.getManagers().isEmpty()) { //TODO: Recheck this line
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setSender(this.agent.getAID());
            msg.setContent("Your companies are..."); //TODO: How to send companies? Serializable, JSON or other method?

        }
    }

    @Override
    public int onEnd() {
        if (companiesAssigned) {
            return 0;
        } else {
            return 1;
        }
    }
}
