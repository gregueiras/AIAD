package agents;

import behaviours.FindAgents;
import behaviours.NegotiateCompanies;
import behaviours.Print;
import behaviours.SendMessage;
import behaviours.StateMachine;
import behaviours.WaitForMessage;
import behaviours.WaitForMessages;
import helper.Logger;
import helper.State;
import helper.StateEndMsg;
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
import java.util.*;

import market.Company;
import market.InvestmentType;
import personalities.Normal;
import personalities.Personality;

public class AgentManager extends OurAgent {

  // The companies that the manager has in it's wallet, mapped by title
  private Map<InvestmentType, List<Company>> wallet;

  private AID board;

  private AID investor;

  private Personality person;

  private boolean skipShift;

  private int currentCapital;

  private final static int INITIAL_CAPITAL = 0;

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    //wallet = WalletExamples.getEx1();
    wallet = new HashMap<>();
    skipShift = false;
    person = new Normal();
    this.currentCapital = INITIAL_CAPITAL;
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
    Behaviour negotiate = new NegotiateCompanies(this);

    Transition t1 = new Transition(printStart, findBoard);
    Transition t2 = new Transition(findBoard, wms);
    Transition t3_1 = new Transition(wms, proposeInitiator, 0);
    Transition t3_2 = new Transition(wms, informBoard, 1);
    Transition t8 = new Transition(wms, printEnd, 2);
    Transition t9 = new Transition(wms, negotiate, State.ROUND_END.ordinal());
    Transition t10 = new Transition(negotiate, wms);
    Transition t5 = new Transition(proposeInitiator, proposeReply);
    Transition t6 = new Transition(proposeReply, informBoard);
    Transition t7 = new Transition(informBoard, wms);

    StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2, t3_1, t3_2, t5, t6, t7,
        t8, t9, t10);
    addBehaviour(sm);
  }

  @Override
  public int handleMessage(ACLMessage msg) {
      if(msg.getConversationId().equals(State.NEGOTIATE.toString())){
          return handleNegotiationMsg(msg);
      }
    if (msg.getConversationId().equals(State.ASSIGN_INVESTOR.toString())) {
      return handleAssignInvestorMsg(msg);
    }
    if (msg.getConversationId().equals(State.ASSIGN_COMPANIES.toString())) {
      return handleAssignCompaniesMsg(msg);
    }
    if (msg.getConversationId().equals(State.GAME_END.toString())) {
      return 2;
    }
    if (msg.getConversationId().equals(State.ROUND_END.toString())) {
      return handleRoundEndMsg(msg);
    }

    Logger.print(this.getLocalName(), msg.getPerformative() + ": " + msg.getContent());
    return -1;
  }

  private String printSoldCompanies(HashMap<InvestmentType, List<Company>> before, HashMap<InvestmentType, List<Company>> after){
      String ret = "Companies Sold this round:\n";
      for(InvestmentType key : before.keySet())
      {
          List<Company> beforeList  = before.get(key);
          List<Company> afterList  = after.get(key);

          if(beforeList == null || afterList == null || beforeList.size() != afterList.size())
              ret += "Inconsistency Detected: Companies have disappeared from the wallet or there are no Companies.\n";
          else {

              for (int i = 0; i < beforeList.size(); i++)
              {
                  if(beforeList.get(i).getCurrentOwner().compareTo(afterList.get(i).getCurrentOwner()) != 0) {
                    ret += afterList.get(i).toString() + "\n";
                    this.currentCapital += afterList.get(i).getPrice() /** (afterList.get(i).isDoubleValue() ? 2.0 : 1.0)*/;
                  }
              }

          }
      }
      ret += "---END_OF_SOLD_COMPANIES_REPORT---\n" + "Current balance: " + this.currentCapital;
      return ret;

  }

  private int handleNegotiationMsg(ACLMessage msg){
      int ret = -1;
      try{
          if(msg.getContentObject() != null){
              HashMap<InvestmentType,List<Company>> tempWallet = (HashMap<InvestmentType, List<Company>>) msg.getContentObject();
              Logger.print(this.getLocalName(), printSoldCompanies((HashMap<InvestmentType, List<Company>>) this.wallet, tempWallet));
              this.wallet = tempWallet;
              ret = 0;
          }
      }catch(UnreadableException e){
          e.printStackTrace();
      }
      return ret;
  }


  private int handleRoundEndMsg(ACLMessage msg){
    try {
      StateEndMsg stateEndMsg = (StateEndMsg) msg
              .getContentObject();
      this.currentCapital = stateEndMsg.getManagerCapital(this.getAID());
      Logger.print(this.getLocalName(),
              getAID().getName() + " current capital:  " + this.currentCapital);

    } catch (UnreadableException e) {
      e.printStackTrace();
    }
    return State.ROUND_END.ordinal();
  }

  private int handleAssignInvestorMsg(ACLMessage msg) {
    String name = "unknown";
    int ret = 0;
    try {
      if (msg.getContentObject() != null) {
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
    Logger.print(this.getLocalName(), getAID().getName() + " assign investor:  " + name);
    return ret;
  }

  private int handleAssignCompaniesMsg(ACLMessage msg) {
    try {
      Map<InvestmentType, List<Company>> companies = (HashMap<InvestmentType, List<Company>>) msg
          .getContentObject();
      this.wallet = companies;
      for (Map.Entry<InvestmentType, List<Company>> entry : this.wallet.entrySet()) {
        List<Company> cs = entry.getValue();
        for(Company c : cs){
          c.setCurrentOwner(this.getAID());
        }
      }
      Logger.print(this.getLocalName(),getAID().getName() + " assign companies:  " + this.wallet);
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
    Logger.print(this.getLocalName(), "Seller-agent " + getAID().getName() + " terminating.");
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
    switch (type) {
      case BOARD:
        try {
          this.board = agents[0];
        } catch (Exception e) {
          Logger.print(this.getLocalName(), e.toString());
        }
        break;
      default:
        Logger.print(this.getLocalName(), "Invalid agent type");
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
        if (this.skipShift) {
          return 1;
        } else {
          return 0;
        }
      default:
        return 0;
    }
  }


  private void sendMsgNegotiate(ACLMessage msg) throws IOException {
    msg.setSender(getAID());
    msg.setContentObject((HashMap<InvestmentType, List<Company>>) this.wallet);
    msg.addReceiver(getInvestor());
    msg.setConversationId(State.NEGOTIATE.toString());
    send(msg);
    msg.reset();
  }

  private void sendMsgInformBoard(ACLMessage msg) {
    msg.setSender(getAID());
    msg.addReceiver(getBoard());
    msg.setConversationId(State.INFORM_BOARD.toString());
    try {
      msg.setContentObject((HashMap<InvestmentType, List<Company>>) this.wallet);
    } catch (IOException e) {
      e.printStackTrace();
    }
    send(msg);
    Logger.print(this.getLocalName(), "agent " + getName() + " Informing board");
    msg.reset();
  }

  public Personality getPerson() {
    return person;
  }

  public void addCompany(Company c) {
    InvestmentType type = c.getType();
    c.setCurrentOwner(this.getAID());
    List<Company> companies = this.wallet.get(type);
    companies.add(c);

    this.wallet.put(type, companies);
  }
}
