package behaviours;

import agents.AgentBoard;
import helper.Logger;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import market.Company;
import market.InvestmentType;

public class OfferCompanies extends ContractNetInitiator {

  private int nResponders;
  private AgentBoard agent;
  private boolean finished;

  public OfferCompanies(AgentBoard agent) {
    super(agent, createMessage());
    this.agent = agent;
    nResponders = this.agent.getManagers().size();
    this.finished = false;

    super.setBehaviourName("Offer_" + Math.random());
  }

  private static ACLMessage createMessage() {
    ACLMessage msg = new ACLMessage(ACLMessage.CFP);
    msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

    return msg;
  }

  @Override
  public Vector prepareCfps(ACLMessage message) {
    ACLMessage msg = createMessage();
    Company company = agent.drawCompany(InvestmentType.YELLOW);
    try {
      msg.setContentObject(company);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (AID manager : agent.getManagers()) {
      msg.addReceiver(manager);
    }

    Vector<ACLMessage> vector = new Vector<>();
    vector.add(msg);

    return vector;
  }

  @Override
  protected void handlePropose(ACLMessage propose, Vector v) {
    Logger.print(this.agent.getLocalName(),
        "Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
  }

  @Override
  protected void handleRefuse(ACLMessage refuse) {
    Logger.print(this.agent.getLocalName(), "Agent " + refuse.getSender().getName() + " refused");
  }

  @Override
  protected void handleFailure(ACLMessage failure) {
    if (failure.getSender().equals(myAgent.getAMS())) {
      // FAILURE notification from the JADE runtime: the receiver
      // does not exist
      Logger.print(this.agent.getLocalName(), "Responder does not exist");
    } else {
      Logger.print(this.agent.getLocalName(), "Agent " + failure.getSender().getName() + " failed");
    }
    // Immediate failure --> we will not receive a response from this agent
    nResponders--;
  }

  @Override
  protected void handleAllResponses(Vector responses, Vector acceptances) {
    if (responses.size() < nResponders) {
      // Some responder didn't reply within the specified timeout
      Logger.print(this.agent.getLocalName(),
          "Timeout expired: missing " + (nResponders - responses.size()) + " responses");
    }
    // Evaluate proposals.
    int bestProposal = -1;
    AID bestProposer = null;
    ACLMessage accept = null;
    Enumeration e = responses.elements();
    while (e.hasMoreElements()) {
      ACLMessage msg = (ACLMessage) e.nextElement();
      if (msg.getPerformative() == ACLMessage.PROPOSE) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
        acceptances.addElement(reply);
        int proposal = Integer.parseInt(msg.getContent());
        if (proposal > bestProposal) {
          bestProposal = proposal;
          bestProposer = msg.getSender();
          accept = reply;
        }
      }
    }
    // Accept the proposal of the best proposer
    if (accept != null) {
      Logger.print(this.agent.getLocalName(),
          "Accepting proposal " + bestProposal + " from responder " + bestProposer.getName());
      accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
    }
  }

  @Override
  protected void handleInform(ACLMessage inform) {
    this.finished = true;
    Logger.print(this.agent.getLocalName(),
        "Agent " + inform.getSender().getName() + " successfully performed the requested action");
  }

  @Override
  protected boolean checkTermination(boolean currentDone, int currentResult) {
    return this.finished;
  }

  @Override
  public int onEnd() {
    this.finished = false;
    this.reset();

    return super.onEnd();
  }
}
