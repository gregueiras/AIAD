package agents;

import behaviours.*;
import helper.MessageType;
import helper.Transition;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import market.Company;
import market.WalletExamples;

import javax.sound.midi.SysexMessage;

public class AgentManager extends OurAgent {

  // The companies that the manager has in it's wallet, mapped by title
  private Hashtable<String, Company> wallet;

  private AID board;

  private AID investor;

  private boolean skipShift;
  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    wallet = WalletExamples.getEx1();
    skipShift = false;
    // Register the manager service in the yellow pages
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(String.valueOf(AgentType.MANAGER));
    sd.setName("wall-Street-manager_" + getAID().getName());
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    Behaviour printStart = new Print("Waiting for msg");

    Behaviour findBoard = new FindAgents(AgentType.BOARD, this);

    Behaviour waitInform = new WaitForMessage(this,
            MessageTemplate.MatchPerformative(ACLMessage.INFORM), 0);

    Behaviour waitAssignInvestor = new WaitForMessage(this,
            MessageTemplate.MatchConversationId("assign-investor"), 0);

    Behaviour proposeInitiator = new SendMessage(this, MessageType.NEGOTIATE);

    Behaviour proposeReply = new WaitForMessage(this,
            MessageTemplate.and(MessageTemplate.MatchConversationId("negotiate"),
                    MessageTemplate.MatchInReplyTo("negotiate")), 0);

    SequentialBehaviour negotiation = new SequentialBehaviour();
    negotiation.addSubBehaviour(proposeInitiator);
    negotiation.addSubBehaviour(proposeReply);

    Behaviour informBoard = new SendMessage(this, MessageType.INFORM_BOARD);

    Behaviour printEnd = new Print("MSG Received");

    Transition t1 = new Transition(printStart, findBoard);

    Transition t2 = new Transition(findBoard, waitInform);

    Transition t3 = new Transition(waitInform, waitAssignInvestor);

    Transition t4 = new Transition(waitAssignInvestor, negotiation);

    Transition t5 = new Transition(negotiation, informBoard);

    Transition t6 = new Transition(informBoard, printEnd);


    StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2, t3, t4, t5, t6);
    addBehaviour(sm);
  }

  @Override
  public void handleMessage(ACLMessage msg) {
    if(msg.getConversationId().equalsIgnoreCase("assign-investor")){
      handleAssignInvestorMsg(msg);
    } else
    System.out.println(msg.getPerformative() + ": " + msg.getContent());
  }

  private void handleAssignInvestorMsg(ACLMessage msg) {
    String name = "unknown";
    try {
      if(msg.getContentObject() != null) {
        AID investor = (AID) msg.getContentObject();
        name = investor.getName();
        this.investor = investor;
        this.skipShift = false;
      } else {
        this.skipShift = true;
      }

    } catch (UnreadableException e) {
      e.printStackTrace();
    }
    System.out.println(getAID().getName() + " assign investor:  " + name);
  }


  // Put agent clean-up operations here
  protected void takeDown() {
    // Deregister from the yellow pages
    try {
      DFService.deregister(this);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    // Printout a dismissal message
    System.out.println("Seller-agent " + getAID().getName() + " terminating.");
  }

  public AID getInvestor() {
    return investor;
  }

  public AID getBoard() {
    return board;
  }

  private class SellCompanies extends ContractNetInitiator {

    public SellCompanies(Agent a, ACLMessage cfp) {
      super(a, cfp);
    }

    protected void handlePropose(ACLMessage propose, Vector v) {
      System.out
          .println("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
      System.out.println("Agent " + refuse.getSender().getName() + " refused");
    }

    protected void handleFailure(ACLMessage failure) {
      if (failure.getSender().equals(myAgent.getAMS())) {
        // FAILURE notification from the JADE runtime: the receiver
        // does not exist
        System.out.println("Responder does not exist");
      } else {
        System.out.println("Agent " + failure.getSender().getName() + " failed");
      }
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

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
        System.out.println(
            "Accepting proposal " + bestProposal + " from responder " + bestProposer.getName());
        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
      }
    }

    protected void handleInform(ACLMessage inform) {
      System.out.println(
          "Agent " + inform.getSender().getName() + " successfully performed the requested action");
    }
  }

  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type){
      case BOARD:
        try {
          this.board = agents[0];
        }
        catch(Exception e){
          System.err.println(e);
        }
        break;
      default:
        System.err.println("Invalid agent type");
        break;
    }
  }

  @Override
  public void sendMessage(MessageType type) {
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    switch (type) {
      case INFORM_BOARD:
        sendMsgInformBoard(msg);
        break;
      case NEGOTIATE:
        sendMsgNegotiate(msg);
        break;
    }
  }

  private void sendMsgNegotiate(ACLMessage msg) {
    System.out.println("ProposeInitiator.action " + getInvestor());
    msg.setSender(getAID());
    msg.setContent("ola ola");
    msg.addReceiver(getInvestor());
    msg.setConversationId("negotiate");
    send(msg);
  }

  private void sendMsgInformBoard(ACLMessage msg) {
    msg.setSender(getAID());
    msg.setContent("end of negotiation");
    msg.addReceiver(getBoard());
    msg.setConversationId("negotiation-end");
    send(msg);
    System.out.println("Informing board");
  }

}
