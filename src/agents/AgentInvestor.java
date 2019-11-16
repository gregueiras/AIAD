package agents;

import behaviours.Print;
import behaviours.StateMachine;
import behaviours.WaitForMessages;
import helper.Logger;
import helper.StateEndMsg;
import helper.State;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import market.Company;
import market.InvestmentType;
import personalities.Normal;
import personalities.Personality;

public class AgentInvestor extends OurAgent {

  // The companies that the investor has in it's wallet, mapped by title
  //private Hashtable<String, Company> wallet;

  private Map<InvestmentType, List<Company>> wallet;

  private Normal person;

  private String companyToBuy;

  private AID board;

  private int currentCapital;

  private final static int INITIAL_CAPITAL = 120; //mil


  public void addCompany(Company c) {
    InvestmentType type = c.getType();

    List<Company> companies = this.wallet.get(type);
    companies.add(c);

    this.wallet.put(type, companies);
  }

  private void initializeWallet(){
    this.wallet = new HashMap<>();
    this.wallet.put(InvestmentType.RED, new LinkedList<>());
    this.wallet.put(InvestmentType.BLUE, new LinkedList<>());
    this.wallet.put(InvestmentType.YELLOW, new LinkedList<>());
    this.wallet.put(InvestmentType.GREEN, new LinkedList<>());
  }
    // Put agent initializations here
  protected void setup() {
    // Printout a welcome message
    Logger.print(this.getLocalName(), "Hallo! Buyer-agent " + getAID().getName() + " is ready.");
    this.currentCapital = INITIAL_CAPITAL;
    person = new Normal();
    this.initializeWallet();
    // Register the manager service in the yellow pages
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(String.valueOf(AgentType.INVESTOR));
    sd.setName("wall-Street-investor_" + getAID().getName());
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
    Behaviour printStart = new Print("Waiting for msg");

      Behaviour wms = new WaitForMessages(this, ACLMessage.INFORM);

      Behaviour printEnd = new Print("MSG Received");

    Transition t1= new Transition(printStart, wms);
    Transition t2 = new Transition(wms, printEnd, 2);



   StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2);
    addBehaviour(sm);
  }

  // Put agent clean-up operations here
  protected void takeDown() {
    // Printout a dismissal message
    Logger.print(this.getLocalName(), "Buyer-agent " + getAID().getName() + " terminating.");
  }

  @Override
  public int handleMessage(ACLMessage msg){
    Logger.print(this.getLocalName(), this.getName() + ": " + msg.getContent());
    if(msg.getConversationId().equals(State.NEGOTIATE.toString()))
       return handleNegotiateMsg(msg);
    if(msg.getConversationId().equals(State.GAME_END.toString()))
       return 2;
    if (msg.getConversationId().equals(State.ROUND_END.toString()))
      return handleRoundEndMsg(msg);

    return -1;
  }

  private int handleNegotiateMsg(ACLMessage msg) {
    //Logger.print(this.getLocalName(), "i am receiving: " + msg.getContent());
    AID sellerID = msg.getSender();
    HashMap<InvestmentType, List<Company>> offer = null;
    try {
      offer = (HashMap<InvestmentType, List<Company>>) msg.getContentObject();
      Logger.print(this.getLocalName(), "offer:" + offer);
        for(InvestmentType type: offer.keySet()) {
            for (Company c : offer.get(type)) {
              if (person.acceptBuyOffer(c) && (c.getCurrentOwner().compareTo(sellerID) == 0) && this.currentCapital > (c.getPrice() /** (c.isDoubleValue() ? 2.0 : 1.0)*/)) {
                c.setCurrentOwner(getAID());
                this.currentCapital -= (c.getPrice() /** (c.isDoubleValue() ? 2.0 : 1.0)*/);
                Logger.print(this.getLocalName(), "Accepted Company " + c.toString());
              }
            }
        }
    } catch (UnreadableException e) {
      e.printStackTrace();
    }
    Logger.print(this.getLocalName(), "-> New balance: " + this.currentCapital + "\n");
    ACLMessage reply = msg.createReply();
    reply.setInReplyTo(State.NEGOTIATE.toString());
    reply.setPerformative( ACLMessage.INFORM );
      try {
          reply.setContentObject(offer);
      } catch (IOException e) {
          e.printStackTrace();
      }
      send(reply);
    return -1;
  }

    private int handleRoundEndMsg(ACLMessage msg) {
      try {
        StateEndMsg stateEndMsg = (StateEndMsg) msg
                .getContentObject();
        this.currentCapital = stateEndMsg.getInvestorCapital(this.getAID());
        Logger.print(this.getLocalName(),
            getAID().getName() + " current capital:  " + this.currentCapital);

      } catch (UnreadableException e) {
        e.printStackTrace();
      }
      return -1;
    }

  List<Company> processOffer(HashMap<InvestmentType, List<Company>> offer){
    return null;
  }

  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type){
      case BOARD:
        try {
          this.board = agents[0];
        }
        catch(Exception e){
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

  }

  @Override
  public int onEnd(State statE) {
        return 0;
  }

}
