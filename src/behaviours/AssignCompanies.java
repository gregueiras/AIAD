package behaviours;

import agents.AgentBoard;
import helper.State;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import market.Company;
import market.InvestmentType;

public class AssignCompanies extends OneShotBehaviour {

  private static int COMPANY_NUMBER = 2; // Number of companies of each type to assign
  private boolean companiesAssigned;
  private AgentBoard agent;

  public AssignCompanies(AgentBoard agent) {
    this.companiesAssigned = false;
    this.agent = agent;

    super.setBehaviourName("Assign_companies");
  }

  @Override
  public void action() {
    if (this.agent.getManagers() != null && !this.agent.getManagers()
        .isEmpty()) { //TODO: Recheck this line
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setSender(this.agent.getAID());

      Map<InvestmentType, List<Company>> catalogue = this.agent.generateCatalogue();

      for (AID aid : this.agent.getManagers()) {

        Map<InvestmentType, List<Company>> companies = new HashMap<>();

        Random rand = new Random();

        for (InvestmentType type : InvestmentType.values()) {
          List<Company> existingCompanies = catalogue.get(type);
          List<Company> newCompanies = new LinkedList<>();

          for (int i = 0; i < COMPANY_NUMBER; i++) {
            int index = rand.nextInt(existingCompanies.size());
            Company c = existingCompanies.remove(index);
            newCompanies.add(c);
          }

          catalogue.put(type, existingCompanies);
          companies.put(type, newCompanies);
        }

        try {
          msg.setConversationId(State.ASSIGN_COMPANIES.toString());
          msg.setContentObject(
              (Serializable) companies); //TODO: How to send companies? Serializable, JSON or other method?
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
