package agents;

import behaviours.Print;
import behaviours.StateMachine;
import behaviours.WaitForMessages;
import personalities.Normal;
import personalities.Personality;
import helper.State;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.*;

import jade.lang.acl.UnreadableException;
import market.Company;
import market.InvestmentType;

public class AgentInvestor extends OurAgent {

  // The companies that the investor has in it's wallet, mapped by title
  //private Hashtable<String, Company> wallet;

  private Map<InvestmentType, List<Company>> wallet;

  private Personality person;

  private String companyToBuy;

  private AID board;

  private int currentCapital;

  private final static int INITIAL_CAPITAL = 120; //mil

  private void incCurrentCapital(int inc){
    this.currentCapital += inc;
  }

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
    System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");
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
    System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
  }

  @Override
  public int handleMessage(ACLMessage msg){
      System.out.println(this.getName() + ": " + msg.getContent());
    if(msg.getConversationId().equals(State.NEGOTIATE.toString()))
       return handleNegotiateMsg(msg);
    if(msg.getConversationId().equals(State.GAME_END.toString()))
       return 2;
    if (msg.getConversationId().equals(State.ROUND_END.toString()))
      return handleRoundEndMsg(msg);

    return -1;
  }

  private int handleNegotiateMsg(ACLMessage msg) {
    //System.out.println("i am receiving: " + msg.getContent());
    try {
      HashMap<InvestmentType, List<Company>> offer = (HashMap<InvestmentType, List<Company>>) msg.getContentObject();
      System.out.println("offer:" + offer);
    } catch (UnreadableException e) {
      e.printStackTrace();
    }

    ACLMessage reply = msg.createReply();
    reply.setInReplyTo(State.NEGOTIATE.toString());
    reply.setPerformative( ACLMessage.INFORM );
    reply.setContent("oi oi");
    send(reply);
    return -1;
  }

    private int handleRoundEndMsg(ACLMessage msg) {
      try {
        Map<InvestmentType, Integer> dicesResult = (HashMap<InvestmentType, Integer>) msg
                .getContentObject();
        for (Map.Entry<InvestmentType, Integer> entry : dicesResult.entrySet()) {
          Integer result = entry.getValue();
          InvestmentType type = entry.getKey();
          int nrCompanies = this.wallet.get(type).size();
          this.incCurrentCapital(nrCompanies*result);
        }
        System.out.println(getAID().getName() + " current capital:  " + this.currentCapital);

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

  }

  @Override
  public int onEnd(State statE) {
        return 0;
  }

}
