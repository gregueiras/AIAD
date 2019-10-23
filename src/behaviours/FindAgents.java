package behaviours;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class FindAgents extends OneShotBehaviour {

  private String typeToSearch; // type of agent to search
  private boolean agentsFound;

  public FindAgents(String typeToSearch) {
    this.typeToSearch = typeToSearch;
    this.agentsFound = false;

    super.setBehaviourName("Find_" + typeToSearch);
  }

  @Override
  public void action() {
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(typeToSearch);
    template.addServices(sd);
    try {
      DFAgentDescription[] result = DFService.search(myAgent, template);
      System.out.println("Found the following " + typeToSearch + " agents:");
      AID[] sellerAgents = new AID[result.length];
      for (int i = 0; i < result.length; ++i) {
        agentsFound = true;
        sellerAgents[i] = result[i].getName();
        System.out.println(sellerAgents[i].getName());
      }
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  @Override
  public int onEnd() {
    if (agentsFound) {
      return 0;
    } else {
      return 1;
    }

  }
}
