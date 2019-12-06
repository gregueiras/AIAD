package agents;

import behaviours.*;
import helper.*;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import market.Company;
import market.CompanyFactory;
import market.InvestmentType;
import market.profits.Profits;
import market.profits.ProfitsFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AgentBoard extends OurAgent {

  private static final int COMPANY_NUMBER = 15;

  private static final int NR_ROUNDS = 5;

  // The catalogue of books for sale (maps the title of a book to its price)
  private Map<InvestmentType, List<Company>> catalogue;

  private Map<InvestmentType, Profits> profitsBoard;

  private ConcurrentMap<AID, Integer> investors;

  private ConcurrentMap<AID, Integer> managers;

  private ConcurrentMap<AID, Map<InvestmentType,Set<String>>> investments;

    private Map<InvestmentType, Integer> initialCompanyDistribution;

  private Round round;

  private Integer currentShift;

  private Integer currentRound;

  private Random rand;

  public Map<AID, Integer> getInvestors() {
    return investors;
  }

  public List<AID> getListInvestors(){
    return new ArrayList<>(this.investors.keySet());
  }

  public Map<AID, Integer> getManagers() {
    return managers;
  }

  public List<AID> getListManagers(){
    return new ArrayList<>(this.managers.keySet());
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

  public Integer getCurrentRound() {
    return currentRound;
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

  private void initializeProfitsBoard(){
      this.profitsBoard = ProfitsFactory.createAllProfits();
  }

  private HashMap<String, Integer> personalitiesInvestors;

  private HashMap<String, Integer> personalitiesManagers;

  private Map<InvestmentType, Profits> getProfitsBoard() {
      return profitsBoard;
  }


  public void setRound(Round round) {
    this.round = round;
    this.currentShift = 0;
  }

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    catalogue = generateCatalogue();
    investors = new ConcurrentHashMap<>();
    managers = new ConcurrentHashMap<>();
    this.investments = new ConcurrentHashMap<>();

    this.personalitiesInvestors = new HashMap<>();
    this.personalitiesManagers = new HashMap<>();

    rand = new Random();
    this.resetCurrentShift();
    this.currentRound = 0;
    this.initializeProfitsBoard();

      Object[] args = (Object[]) getArguments();
      this.initialCompanyDistribution = (Map<InvestmentType, Integer>) args[0];

    // Register the book-selling service in the yellow pages
    registerDFS();
    stateMachine();

  }

  private void stateMachine() {
    Behaviour findManagers = new FindAgents(AgentType.MANAGER, this);
    Behaviour findInvestors = new FindAgents(AgentType.INVESTOR, this);
    Behaviour createRound = new CreateRound(this);
      Behaviour assignCompanies = new AssignCompanies(this, initialCompanyDistribution);
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

    Transition t6_1 = new Transition(sendShiftEnd, assignInvestors);
    Transition t6_11 = new Transition(sendRoundEnd, offerCompanies);
    Transition t6_2 = new Transition(offerCompanies, createRound);
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
    Logger.print(this.getLocalName(), "Seller-agent " + getAID().getName() + " terminating.");
  }


  public void associate(HashMap<String, Integer> map, String agentPersonality){
    if(map.get(agentPersonality) == null)
      map.put(agentPersonality, 1);
    else{
      map.put(agentPersonality, map.get(agentPersonality) + 1);
    }
  }

  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type) {
      case INVESTOR:
        for(AID agent: agents){
          this.investors.put(agent, 120);
          String agentPersonality = (agent.getName().split("#")[1]).split("@")[0];
          associate(this.personalitiesInvestors, agentPersonality);
          initializeInvestment(agent);
        }
        break;
      case MANAGER:
        for(AID agent: agents){
          this.managers.put(agent, 0);

          String agentPersonality = (agent.getName().split("#")[1]).split("@")[0];
          System.out.println(agentPersonality);
          associate(this.personalitiesManagers, agentPersonality);
        }
        break;
      default:
        Logger.print(this.getLocalName(), "Invalid agent type");
        break;
    }
  }

  private void initializeInvestment(AID agent) {
    Map<InvestmentType,Set<String>> initialMap = new HashMap<>();
    initialMap.put(InvestmentType.YELLOW, new HashSet<>());
    initialMap.put(InvestmentType.RED, new HashSet<>());
    initialMap.put(InvestmentType.BLUE, new HashSet<>());
    initialMap.put(InvestmentType.GREEN, new HashSet<>());
    this.investments.put(agent, initialMap);
  }

  @Override
  public void sendMessage(State type) {
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

    for (Map.Entry<AID, Integer> entry : this.managers.entrySet()) {
      AID manager = entry.getKey();
      msg.addReceiver(manager);
    }

    for (Map.Entry<AID, Integer> entry : this.investors.entrySet()) {
      AID investor = entry.getKey();
      msg.addReceiver(investor);
    }

    if(state.equals(State.ROUND_END)){
      this.rollDices();
      createRoundEndMsg(msg);
    } else
      if(state.equals(State.GAME_END)){
        this.rollDices();
        createGameEndMsg(msg);
      } else
         msg.setContent(state.toString());

    msg.setConversationId(state.toString());
    send(msg);
    Logger.print(this.getLocalName(),
        "send message: " + state.toString() + " -> " + msg.getContent());
    //this.resetManagersCapital();
  }

  private Map<InvestmentType, Integer> getInvestmentResults() {
    Map<InvestmentType, Integer> results = new HashMap<>();
    for (Map.Entry<InvestmentType, Profits> entry : this.getProfitsBoard().entrySet()) {
      Profits profits = entry.getValue();
      InvestmentType type = entry.getKey();
      results.put(type, profits.getActualProfit());
    }
    return results;
  }

  private void createRoundEndMsg(ACLMessage msg) {
    Map<InvestmentType, Integer> results = getInvestmentResults();
    StateEndMsg stateEndMsg = new StateEndMsg(results,this.investors, this.managers);
    try {
        msg.setContentObject(stateEndMsg);
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  private void createGameEndMsg(ACLMessage msg) {
    Map<InvestmentType, Integer> results = getInvestmentResults();
    Map<AID, Integer> winnerManagers = this.findWinnerManagers();
    Map<AID, Integer> winnerInvestors = this.findWinnerInvestors();
    StateEndMsg content = new StateEndMsg(results, this.investors, this.managers);
    content.setWinnerInvestors(winnerInvestors);
    content.setWinnerManagers(winnerManagers);
    try {
      msg.setContentObject(content);
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  private boolean isNewInvestment(AID agent, InvestmentType type, Company company){
    Map<InvestmentType, Set<String>> t = this.investments.get(agent);
    Set<String> companies = t.get(type);
    return !companies.contains(company.getId());
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

  private void rollDices(){
    for (Map.Entry<InvestmentType, Profits> entry : this.profitsBoard.entrySet()) {
      InvestmentType type = entry.getKey();
      Profits profits = entry.getValue();
      profits.roll_dice();
      for(Map.Entry<AID, Map<InvestmentType, Set<String>>> e : this.investments.entrySet()){
        AID agent = e.getKey();
        Map<InvestmentType, Set<String>> value = e.getValue();
        int nrCompanies = value.get(type).size();
        Integer investorCapital = this.investors.get(agent) + profits.getActualProfit()*nrCompanies;
        this.investors.put(agent, investorCapital);
      }
    }
    Logger.print(this.getLocalName(), "ROLL DICES: " + this.profitsBoard);

  }

  public void handleEndNegotiationMsg(ACLMessage msg) {
    AID manager = msg.getSender();
    Integer managerCapital = this.managers.get(manager);
    try {
      Map<InvestmentType, List<Company>> wallet = (HashMap<InvestmentType, List<Company>>) msg.getContentObject();
      for (Map.Entry<InvestmentType, List<Company>> entry : wallet.entrySet()) {
        List<Company>  companies = entry.getValue();
        InvestmentType type = entry.getKey();
        for(Company company: companies){
          Integer price = company.getPrice();
          AID owner = company.getCurrentOwner();
          Boolean isDouble = company.isDoubleValue();
          if(owner.compareTo(manager) != 0 && this.isNewInvestment(owner, type, company)) {
            Integer investorCapital = this.investors.get(owner) - price;

            this.investors.put(owner, investorCapital);
            this.incInvestment(owner,type, company);

            managerCapital += price;
          }
        }
      }
      this.managers.put(manager, managerCapital);
    } catch (UnreadableException e) {
      e.printStackTrace();
    }
  }

  private void incInvestment(AID agent, InvestmentType type, Company company) {
    Map<InvestmentType, Set<String>>  inv = this.investments.get(agent);
    Set<String> companies = inv.get(type);
    companies.add(company.getId());
    inv.put(type, companies);
    this.investments.put(agent, inv);
  }

  private  Map<AID,Integer> findWinnerAgents(Map<AID, Integer> agents){
    Map<AID,Integer> result = new HashMap<>();
    Map.Entry<AID, Integer> maxEntry = null;
    for (Map.Entry<AID, Integer> entry : agents.entrySet())
    {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
        maxEntry = entry;
    }
    for (Map.Entry<AID, Integer> entry : agents.entrySet())
    {
      if (entry.getValue().compareTo(maxEntry.getValue()) == 0)
        result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  private  Map<AID,Integer> findWinnerInvestors(){
    return this.findWinnerAgents(this.investors);
  }

  private  Map<AID,Integer> findWinnerManagers(){
    return this.findWinnerAgents(this.managers);
  }

  private void resetManagersCapital(){
    for (Map.Entry<AID, Integer> entry : this.managers.entrySet())
    {
      this.managers.put(entry.getKey(), 0);
    }
  }


  @Override
  public int onEnd(State state) {
    return 0;
  }

  @Override
  public int handleMessage(ACLMessage msg) {

    return -1;
  }
}
