package agents;

import behaviours.AssignCompanies;
import behaviours.AssignInvestors;
import behaviours.CreateRound;
import behaviours.EndNegotiation;
import behaviours.FindAgents;
import behaviours.OfferCompanies;
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
import java.util.Random;
import market.Company;
import market.CompanyFactory;
import market.InvestmentType;

public class AgentBoard extends OurAgent {

  private static final int COMPANY_NUMBER = 15;

  private static final int NR_ROUNDS = 3;

  // The catalogue of books for sale (maps the title of a book to its price)
  private Map<InvestmentType, List<Company>> catalogue;

  private List<AID> investors;

  private List<AID> managers;

  private Round round;

  private Integer currentShift;

  private Integer currentRound;

  private Random rand;


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

  public void incCurrentRound() {
    this.setCurrentRound(this.currentRound + 1);
  }

  private void setCurrentShift(Integer currentShift) {
    this.currentShift = currentShift;
  }

  private void setCurrentRound(Integer currentRound) {
    this.currentRound = currentRound;
  }

  public void resetCurrentShift() {
    this.currentShift = 0;
  }

  public boolean isEndRound() {
    return this.currentShift >= (this.round.getShifts().size() - 1);
  }

  public boolean isEndGame() {
    return this.currentRound >= (NR_ROUNDS - 1);
  }

  public void setRound(Round round) {
    this.round = round;
    this.currentShift = 0;
  }

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    catalogue = generateCatalogue();
    investors = new LinkedList<>();
    managers = new LinkedList<>();
    rand = new Random();
    this.resetCurrentShift();
    this.currentRound = 0;

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
    Behaviour sendGameEnd = new SendMessage(this, State.GAME_END);

    Behaviour offerCompanies = new OfferCompanies(this);

    Transition t1 = new Transition(findManagers, findInvestors);
    Transition t2 = new Transition(findInvestors, assignCompanies);
    Transition t3_1 = new Transition(assignCompanies, createRound);
    Transition t3_2 = new Transition(createRound, assignInvestors);
    Transition t4 = new Transition(assignInvestors, endNegotiation);

    Transition t5_1 = new Transition(endNegotiation, sendShiftEnd, 0);
    Transition t5_2 = new Transition(endNegotiation, sendRoundEnd, 1);
    Transition t5_3 = new Transition(endNegotiation, sendGameEnd, 2);

    Transition t6_1 = new Transition(sendShiftEnd, offerCompanies);
    Transition t6_11 = new Transition(offerCompanies, assignInvestors);
    Transition t6_2 = new Transition(sendRoundEnd, createRound);
    Transition t6_3 = new Transition(sendGameEnd, printEnd);

    StateMachine sm = new StateMachine(this, findManagers, printEnd, t1, t2, t3_1, t3_2, t4, t5_1,
        t5_2, t5_3, t6_1, t6_2, t6_3, t6_11);
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
      case GAME_END:
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
    msg.setConversationId(state.toString());
    send(msg);
    System.out.println("send message: " + msg.getContent());

  }

  public Map<InvestmentType, List<Company>> getCatalogue() {
    return catalogue;
  }

  public void setCatalogue(Map<InvestmentType, List<Company>> catalogue) {
    this.catalogue = catalogue;
  }

  public Company drawCompany(InvestmentType type) {
    List<Company> existingCompanies = catalogue.get(type);

    int index = rand.nextInt(existingCompanies.size());
    Company company = existingCompanies.remove(index);

    catalogue.put(type, existingCompanies);

    return company;
  }

  private Map<InvestmentType, List<Company>> generateCatalogue() {
    final int companyNumber = COMPANY_NUMBER * 4;
    HashMap<InvestmentType, List<Company>> catalogue = new HashMap<>();

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
  public int onEnd(State state) {
    return 0;
  }

  @Override
  public int handleMessage(ACLMessage msg) {
    //TODO
    return -1;
  }
}
