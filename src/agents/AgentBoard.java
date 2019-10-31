package agents;

import behaviours.*;
import helper.Round;
import helper.Shift;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class AgentBoard extends OurAgent {

  // The catalogue of books for sale (maps the title of a book to its price)
  private Hashtable<String, Integer> catalogue;
  // The GUI by means of which the user can add books in the catalogue

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

  public void setCurrentShift(Integer currentShift) {
    this.currentShift = currentShift;
  }

  public void setRound(Round round) {
    this.round = round;
    this.currentShift = 0;
  }

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    catalogue = new Hashtable<>();
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
    Behaviour assignInvestors = new AssignInvestors(this);
    Behaviour printEnd = new Print("MSG Received");

    Transition t1 = new Transition(findManagers, findInvestors);
    Transition t2 = new Transition(findInvestors, createRound);
    Transition t3 = new Transition(createRound, assignInvestors);
    Transition t4 = new Transition(assignInvestors, printEnd);


    StateMachine sm = new StateMachine(this, findManagers, printEnd, t1, t2, t3, t4);
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
    switch (type){
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
  public void handleMessage(ACLMessage msg) {
    //TODO
  }

  /**
   * Inner class OfferRequestsServer. This is the behaviour used by Book-seller agents to serve
   * incoming requests for offer from buyer agents. If the requested book is in the local catalogue
   * the seller agent replies with a PROPOSE message specifying the price. Otherwise a REFUSE
   * message is sent back.
   */
  private class OfferRequestsServer extends CyclicBehaviour {

    public void action() {
      MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
      ACLMessage msg = myAgent.receive(mt);
      if (msg != null) {
        // CFP Message received. Process it
        String title = msg.getContent();
        ACLMessage reply = msg.createReply();

        Integer price = catalogue.get(title);
        if (price != null) {
          // The requested book is available for sale. Reply with the price
          reply.setPerformative(ACLMessage.PROPOSE);
          reply.setContent(String.valueOf(price.intValue()));
        } else {
          // The requested book is NOT available for sale.
          reply.setPerformative(ACLMessage.REFUSE);
          reply.setContent("not-available");
        }
        myAgent.send(reply);
      } else {
        block();
      }
    }
  }  // End of inner class OfferRequestsServer

  /**
   * Inner class PurchaseOrdersServer. This is the behaviour used by Book-seller agents to serve
   * incoming offer acceptances (i.e. purchase orders) from buyer agents. The seller agent removes
   * the purchased book from its catalogue and replies with an INFORM message to notify the buyer
   * that the purchase has been sucesfully completed.
   */
  private class PurchaseOrdersServer extends CyclicBehaviour {

    public void action() {
      MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
      ACLMessage msg = myAgent.receive(mt);
      if (msg != null) {
        // ACCEPT_PROPOSAL Message received. Process it
        String title = msg.getContent();
        ACLMessage reply = msg.createReply();

        Integer price = catalogue.remove(title);
        if (price != null) {
          reply.setPerformative(ACLMessage.INFORM);
          System.out.println(title + " sold to agent " + msg.getSender().getName());
        } else {
          // The requested book has been sold to another buyer in the meanwhile .
          reply.setPerformative(ACLMessage.FAILURE);
          reply.setContent("not-available");
        }
        myAgent.send(reply);
      } else {
        block();
      }
    }
  }  // End of inner class OfferRequestsServer
}
