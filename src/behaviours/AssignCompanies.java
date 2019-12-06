package behaviours;

import agents.AgentBoard;
import helper.State;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import market.Company;
import market.InvestmentType;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AssignCompanies extends OneShotBehaviour {

  private boolean companiesAssigned;
  private Map<InvestmentType, Integer> initialDistribution;
  private AgentBoard agent;

  public AssignCompanies(AgentBoard agent, Map<InvestmentType, Integer> initialDistribution) {
    this.companiesAssigned = false;
    this.agent = agent;
    this.initialDistribution = initialDistribution;

    super.setBehaviourName("Assign_companies");
  }

  @Override
  public void action() {
    if (this.agent.getManagers() != null && !this.agent.getManagers()
        .isEmpty()) {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setSender(this.agent.getAID());

      for (AID aid : this.agent.getListManagers()) {

        Map<InvestmentType, List<Company>> companies = new HashMap<>();


        for (InvestmentType type : this.initialDistribution.keySet()) {
          List<Company> newCompanies = new LinkedList<>();
          int companyNumber = this.initialDistribution.get(type);

          for (int i = 0; i < companyNumber; i++) {
            Company c = agent.drawCompany(type);
            newCompanies.add(c);
          }

          companies.put(type, newCompanies);
        }

        try {
          msg.setConversationId(State.ASSIGN_COMPANIES.toString());
          msg.setContentObject((Serializable) companies);
          msg.addReceiver(aid);
          this.agent.send(msg);
          msg.removeReceiver(aid);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

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
