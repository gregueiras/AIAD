package behaviours;

import agents.AgentManager;
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

  public NegotiateCompanies(AgentManager a) {
    super(a, createTemplate());
    this.agent = a;
    super.setBehaviourName("Negotiate_" + Math.random());
  }

  private static MessageTemplate createTemplate() {
    System.out.println("HEH Negotiate");

    return MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP)
    );
  }

  @Override
  protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException {
    System.out.println(
        "Agent " + myAgent.getName() + ": CFP received from " + cfp.getSender().getName());

    try {
      offeredCompany = (Company) cfp.getContentObject();
    } catch (UnreadableException e) {
      e.printStackTrace();
      throw new NotUnderstoodException(cfp);
    }

    int proposal = agent.getPerson().getPriceBuy(offeredCompany);
    // We provide a proposal
    System.out.println("Agent " + myAgent.getName() + ": Proposing " + proposal);
    ACLMessage propose = cfp.createReply();
    propose.setPerformative(ACLMessage.PROPOSE);
    propose.setContent(String.valueOf(proposal));
    return propose;

  }

  @Override
  protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
    System.out.println("Agent " + myAgent.getName() + ": Proposal accepted");

    this.agent.addCompany(offeredCompany);

    ACLMessage inform = accept.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    return inform;
  }

  @Override
  protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
    System.out.println("Agent " + myAgent.getName() + ": Proposal rejected");
  }

  @Override
  public int onEnd() {
    System.out.println("END NEGOTIATE");
    return super.onEnd();
  }
}