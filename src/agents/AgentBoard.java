package agents;

import behaviours.AssignCompanies;
import behaviours.AssignInvestors;
import behaviours.CreateRound;
import behaviours.EndNegotiation;
import behaviours.FindAgents;
import behaviours.Print;
import behaviours.SendMessage;
import behaviours.StateMachine;
import helper.Round;
import helper.Shift;
import helper.State;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import market.Company;
import market.CompanyFactory;
import market.InvestmentType;

public class AgentBoard extends OurAgent {

  private static final int COMPANY_NUMBER = 15;
  // The catalogue of books for sale (maps the title of a book to its price)
  private Map<InvestmentType, List<Company>> catalogue;

  private List<AID> investors;

  private List<AID> managers;

  private Round round;

  private Integer currentShift;

  private Integer currentRound;

  public List<AID> getInvestors() {
    return investors;
  }

  public List<AID> getManagers() {
    return managers;
  }

  public Round getRound() {
    return round;
  }

  public Shift getCurrentShift() {
    return this.round.getShift(this.currentShift);
  }

  public void incCurrentShift() {
    this.setCurrentShift(this.currentShift + 1);
  }

  public void setCurrentShift(Integer currentShift) {
    this.currentShift = currentShift;
  }

  public boolean isEndRound() {
    System.out.println("CS: " + this.currentShift + "  rs: " + this.round.getShifts().size());
    return this.currentShift >= (this.round.getShifts().size() - 1);
  }

  public void setRound(Round round) {
    this.round = round;
    this.currentShift = 0;
  }

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    catalogue = new HashMap<>();
    investors = new LinkedList<>();
    managers = new LinkedList<>();
    currentShift = 0;

    // Register the book-selling service in the yellow pages
    registerDFS();
    stateMachine();

  }

  private void stateMachine() {
    Behaviour findManagers = new FindAgents(AgentType.MANAGER, this);
    Behaviour findInvestors = new FindAgents(AgentType.INVESTOR, this);
    Behaviour createRound = new CreateRound(this);
    Behaviour assignCompanies = new AssignCompanies(this);
    Behaviour assignInvestors = new AssignInvestors(this);
    Behaviour printEnd = new Print("MSG Received");
    Behaviour endNegotiation = new EndNegotiation(this);
    Behaviour sendRoundEnd = new SendMessage(this, State.ROUND_END);
    Behaviour sendShiftEnd = new SendMessage(this, State.SHIFT_END);

    Transition t1 = new Transition(findManagers, findInvestors);
    Transition t2 = new Transition(findInvestors, createRound);
    Transition t2_1 = new Transition(createRound, assignCompanies);
    Transition t3 = new Transition(assignCompanies, assignInvestors);
    Transition t4 = new Transition(assignInvestors, endNegotiation);

    Transition t52 = new Transition(endNegotiation, sendRoundEnd, 1);
    Transition t51 = new Transition(endNegotiation, sendShiftEnd, 0);

    Transition t61 = new Transition(sendShiftEnd, assignInvestors);
    Transition t62 = new Transition(sendRoundEnd, printEnd);

    StateMachine sm = new StateMachine(this, findManagers, printEnd, t1, t2, t2_1, t3, t4, t52, t51,
        t61,
        t62);
    addBehaviour(sm);
  }

  private void registerDFS() {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(String.valueOf(AgentType.BOARD));
    sd.setName("JADE-Panic-Wall-Street");
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }


  // Put agent clean-up operations here
  protected void takeDown() {
    // Deregister from the yellow pages
    try {
      DFService.deregister(this);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
    // Close the GUI
    // Printout a dismissal message
    System.out.println("Seller-agent " + getAID().getName() + " terminating.");
  }


  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type) {
      case INVESTOR:
        this.investors = Arrays.asList(agents);
        break;
      case MANAGER:
        this.managers = Arrays.asList(agents);
        break;
      default:
        System.err.println("Invalid agent type");
        break;
    }
  }

  @Override
  public void sendMessage(State type) {
    System.out.println("stet:: " + type);
    switch (type) {
      case ROUND_END:
      case SHIFT_END:
        sendEndStateMsg(type);
        break;
      default:
        break;
    }
  }

  private void sendEndStateMsg(State state) {
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    msg.setSender(getAID());
    for (AID manager : this.managers) {
      msg.addReceiver(manager);
    }
    for (AID investor : this.investors) {
      msg.addReceiver(investor);
    }
    msg.setContent(state.toString());
    msg.setConversationId(State.WAIT_END_SHIFT_ROUND.toString());
    send(msg);
    System.out.println("send message: " + msg.getContent());

  }

  public Map<InvestmentType, List<Company>> getCatalogue() {
    return catalogue;
  }

  public void setCatalogue(Map<InvestmentType, List<Company>> catalogue) {
    this.catalogue = catalogue;
  }

  public Map<InvestmentType, List<Company>> generateCatalogue() {
    final int companyNumber = this.managers.size() * 4;

    for (InvestmentType type : InvestmentType.values()) {
      List<Company> list = new LinkedList<>();
      for (int i = 0; i < companyNumber; i++) {
        list.add(CompanyFactory.createCompany(type));
      }
      catalogue.put(type, list);
    }

    return catalogue;
  }


  @Override
  public int onEnd(State state, String content) {
    return 0;
  }

  @Override
  public void handleMessage(ACLMessage msg) {
    //TODO
  }
}
