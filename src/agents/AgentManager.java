package agents;

import behaviours.FindAgents;
import behaviours.Print;
import behaviours.SendMessage;
import behaviours.StateMachine;
import behaviours.WaitForMessage;
import behaviours.WaitForMessages;
import helper.State;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import market.Company;
import market.InvestmentType;
import market.WalletExamples;

public class AgentManager extends OurAgent {

  // The companies that the manager has in it's wallet, mapped by title
  private Map<InvestmentType, List<Company>> wallet;

  private AID board;

  private AID investor;

  private boolean skipShift;
  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    //wallet = WalletExamples.getEx1();
    wallet = new HashMap<>();
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
    Behaviour proposeInitiator = new SendMessage(this, State.NEGOTIATE);
    Behaviour proposeReply = new WaitForMessage(this,
            MessageTemplate.and(MessageTemplate.MatchConversationId(State.NEGOTIATE.toString()),
                    MessageTemplate.MatchInReplyTo(State.NEGOTIATE.toString())));
    SequentialBehaviour negotiation = new SequentialBehaviour();
    negotiation.addSubBehaviour(proposeInitiator);
    negotiation.addSubBehaviour(proposeReply);
    Behaviour informBoard = new SendMessage(this, State.INFORM_BOARD);
    Behaviour printEnd = new Print("MSG Received");
    Behaviour wms = new WaitForMessages(this, ACLMessage.INFORM);


    Transition t1 = new Transition(printStart, findBoard);
    Transition t2 = new Transition(findBoard, wms);
    Transition t3_1 = new Transition(wms, proposeInitiator, 0);
    Transition t3_2 = new Transition(wms, informBoard, 1);
    Transition t8 = new Transition(wms, printEnd, 2);
    Transition t5 = new Transition(proposeInitiator, proposeReply);
    Transition t6 = new Transition(proposeReply, informBoard);
    Transition t7 = new Transition(informBoard, wms);

    StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2,  t3_1, t3_2,  t5, t6, t7, t8);
    addBehaviour(sm);
  }

  @Override
  public int handleMessage(ACLMessage msg) {
    if(msg.getConversationId().equals(State.ASSIGN_INVESTOR.toString()))
      return handleAssignInvestorMsg(msg);
    if(msg.getConversationId().equals(State.ASSIGN_COMPANIES.toString()))
      return handleAssignCompaniesMsg(msg);
    if(msg.getConversationId().equals(State.GAME_END.toString()))
      return 2;
    System.out.println(msg.getPerformative() + ": " + msg.getContent());
    return -1;
  }

  private int handleAssignInvestorMsg(ACLMessage msg) {
    String name = "unknown";
    int ret = 0;
    try {
      if(msg.getContentObject() != null) {
        AID investor = (AID) msg.getContentObject();
        name = investor.getName();
        this.investor = investor;
        this.skipShift = false;
      } else {
        this.skipShift = true;
        ret = 1; //the manager will skip this shift so it should send an inform message to the board (skips negotiate state)
      }
    } catch (UnreadableException e) {
      e.printStackTrace();
    }
    System.out.println(getAID().getName() + " assign investor:  " + name);
    return ret;
  }

  private int handleAssignCompaniesMsg(ACLMessage msg) {
    try {
      Map<InvestmentType, List<Company>> companies  = (HashMap<InvestmentType, List<Company>>) msg.getContentObject();
      this.wallet = companies;
      System.out.println(getAID().getName() + " assign companies:  " + this.wallet);

    } catch (UnreadableException e) {
      e.printStackTrace();
    }
    return -1;
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

  private AID getInvestor() {
    return investor;
  }

  private AID getBoard() {
    return board;
  }

  public boolean isSkipShift() {
    return skipShift;
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
  public void sendMessage(State type) {
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    switch (type) {
      case INFORM_BOARD:
        sendMsgInformBoard(msg);
        break;
      case NEGOTIATE:
        try {
          sendMsgNegotiate(msg);
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
    }
  }

  @Override
  public int onEnd(State state) {
    switch (state) {
      case ASSIGN_INVESTOR:
        if(this.skipShift) return 1;
        else return 0;
      default:
        return 0;
    }
  }



  private void sendMsgNegotiate(ACLMessage msg) throws IOException {
      System.out.println("ProposeInitiator.action " + getInvestor());
      msg.setSender(getAID());
      msg.setContentObject((HashMap<InvestmentType, List<Company>>) this.wallet);
      msg.addReceiver(getInvestor());
      msg.setConversationId(State.NEGOTIATE.toString());
      send(msg);
    msg.reset();
  }

  private void sendMsgInformBoard(ACLMessage msg) {
    msg.setSender(getAID());
    msg.setContent("end of negotiation");
    msg.addReceiver(getBoard());
    msg.setConversationId(State.INFORM_BOARD.toString());
    send(msg);
    System.out.println("agent " + getName()+ " Informing board");
    msg.reset();
  }

}
