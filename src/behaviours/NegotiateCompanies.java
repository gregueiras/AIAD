package behaviours;

import agents.AgentManager;
import helper.Logger;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import market.Company;

public class NegotiateCompanies extends ContractNetResponder {

  private AgentManager agent;
  private Company offeredCompany;
  private boolean finished;

  public NegotiateCompanies(AgentManager a) {
    super(a, createTemplate());
    this.agent = a;
    this.finished = false;
    super.setBehaviourName("Negotiate_" + Math.random());
  }

  private static MessageTemplate createTemplate() {
    return MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP)
    );
  }

  @Override
  protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException {
    Logger.print(this.agent.getLocalName(),
        "Agent " + myAgent.getName() + ": CFP received from " + cfp.getSender().getName());

    try {
      offeredCompany = (Company) cfp.getContentObject();
    } catch (UnreadableException e) {
      e.printStackTrace();
      throw new NotUnderstoodException(cfp);
    }

    int proposal = agent.getPerson().getPriceBuy(offeredCompany);
    // We provide a proposal
    Logger
        .print(this.agent.getLocalName(), "Agent " + myAgent.getName() + ": Proposing " + proposal);
    ACLMessage propose = cfp.createReply();
    propose.setPerformative(ACLMessage.PROPOSE);
    propose.setContent(String.valueOf(proposal));
    return propose;

  }

  @Override
  protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
    Logger.print(this.agent.getLocalName(), "Agent " + myAgent.getName() + ": Proposal accepted");

    this.agent.addCompany(offeredCompany);

    ACLMessage inform = accept.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    this.agent.send(inform);
    this.finished = true;
    return inform;
  }

  @Override
  protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
    this.finished = true;
    Logger.print(this.agent.getLocalName(), "Agent " + myAgent.getName() + ": Proposal rejected");
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