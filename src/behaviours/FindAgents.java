package behaviours;

import agents.AgentType;
import agents.OurAgent;
import helper.Logger;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import personalities.Personality;

import java.util.HashMap;

public class FindAgents extends OneShotBehaviour {

  private AgentType typeToSearch; // type of agent to search
  private boolean agentsFound;
  private OurAgent agent;


  public FindAgents(AgentType typeToSearch, OurAgent agent) {
    this.typeToSearch = typeToSearch;
    this.agentsFound = false;
    this.agent = agent;

    super.setBehaviourName("Find_" + typeToSearch);
  }

  @Override
  public void action() {
    Logger.print(this.agent.getLocalName(), "FindAgents.action");

    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(String.valueOf(typeToSearch));
    template.addServices(sd);
    try {
      DFAgentDescription[] result = DFService.search(myAgent, template);
      Logger.print(this.agent.getLocalName(), "Found the following " + typeToSearch + " agents:");
      AID[] sellerAgents = new AID[result.length];

      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setSender(this.agent.getAID());
      msg.setContent("Hello");
      msg.setConversationId("Hello");

      for (int i = 0; i < result.length; ++i) {
        agentsFound = true;
        sellerAgents[i] = result[i].getName();

        msg.addReceiver(sellerAgents[i]);
        Logger.print(this.agent.getLocalName(), sellerAgents[i].getName());
      }
      this.agent.registerAgent(sellerAgents, this.typeToSearch);
      this.agent.send(msg);

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
