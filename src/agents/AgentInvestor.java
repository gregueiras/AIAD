package agents;

import behaviours.Print;
import behaviours.StateMachine;
import behaviours.WaitForMessage;
import behaviours.WaitForMessages;
import helper.State;
import helper.Transition;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Hashtable;
import market.Company;

public class AgentInvestor extends OurAgent {

  // The companies that the investor has in it's wallet, mapped by title
  private Hashtable<String, Company> wallet;

  // The list of known seller agents
  private AID[] sellerAgents;

  private String companyToBuy;

  private AID board;

  // Put agent initializations here
  protected void setup() {
    // Printout a welcome message
    System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");

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
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Behaviour printStart = new Print("Waiting for msg");
    Behaviour waitInform = new WaitForMessage(this,
            MessageTemplate.MatchConversationId("Hello"));

    Behaviour negotiation = new WaitForMessage(this, MessageTemplate.MatchConversationId(State.NEGOTIATE.toString()));

    Behaviour waitForEndShiftRound = new WaitForMessage(this,
            MessageTemplate.MatchConversationId(State.WAIT_END_SHIFT_ROUND.toString()));

    Behaviour wms = new WaitForMessages(this, ACLMessage.INFORM, 10);

    Behaviour printEnd = new Print("MSG Received");

    //Transition t1 = new Transition(printStart,waitInform );
    //Transition t2 = new Transition(waitInform, negotiation);
    //Transition t3 = new Transition(negotiation, printEnd);

    Transition t1 = new Transition(printStart,wms );

    Transition t41 = new Transition(wms, printEnd);
    //Transition t42 = new Transition(wms, printEnd, 1);

    StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t41);
    addBehaviour(sm);
  }

  // Put agent clean-up operations here
  protected void takeDown() {
    // Printout a dismissal message
    System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
  }

  @Override
  public void handleMessage(ACLMessage msg) {
    if(msg.getConversationId().equalsIgnoreCase(State.NEGOTIATE.toString())){
      handleNegotiateMsg(msg);
    } else
      System.out.println(msg.getPerformative() + ": " + msg.getContent());
  }

  private void handleNegotiateMsg(ACLMessage msg) {
    System.out.println("i am receiving: " + msg.getContent());
    ACLMessage reply = msg.createReply();
    reply.setInReplyTo(State.NEGOTIATE.toString());
    reply.setPerformative( ACLMessage.INFORM );
    reply.setContent("oi oi");
    send(reply);
    System.out.println("sent reply");
  }


  @Override
  public void registerAgent(AID[] agents, AgentType type) {
    switch (type){
      case BOARD:
        try {
          this.board = agents[0];
          System.out.println("THIS IS MY BOARD " + this.board);
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
  public int onEnd(State state, String content) {
    switch (state) {
      case WAIT_END_SHIFT_ROUND:
        if(content.equalsIgnoreCase(State.ROUND_END.toString()))
          return 1;
        else return 0;
      default:
        return 0;
    }
  }

  /**
   * Inner class RequestPerformer. This is the behaviour used by Book-buyer agents to request seller
   * agents the target book.
   */
  private class RequestPerformer extends Behaviour {

    private AID bestSeller; // The agent who provides the best offer
    private int bestPrice;  // The best offered price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private int step = 0;

    public void action() {
      switch (step) {
        case 0:
          // Send the cfp to all sellers
          ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
          for (AID sellerAgent : sellerAgents) {
            cfp.addReceiver(sellerAgent);
          }
          cfp.setContent("targetBookTitle");
          cfp.setConversationId("book-trade");
          cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
          myAgent.send(cfp);
          // Prepare the template to get proposals
          mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
              MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
          step = 1;
          break;
        case 1:
          // Receive all proposals/refusals from seller agents
          ACLMessage reply = myAgent.receive(mt);
          if (reply != null) {
            // Reply received
            if (reply.getPerformative() == ACLMessage.PROPOSE) {
              // This is an offer
              int price = Integer.parseInt(reply.getContent());
              if (bestSeller == null || price < bestPrice) {
                // This is the best offer at present
                bestPrice = price;
                bestSeller = reply.getSender();
              }
            }
            repliesCnt++;
            if (repliesCnt >= sellerAgents.length) {
              // We received all replies
              step = 2;
            }
          } else {
            block();
          }
          break;
        case 2:
          // Send the purchase order to the seller that provided the best offer
          ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
          order.addReceiver(bestSeller);
          order.setContent("targetBookTitle");
          order.setConversationId("book-trade");
          order.setReplyWith("order" + System.currentTimeMillis());
          myAgent.send(order);
          // Prepare the template to get the purchase order reply
          mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
              MessageTemplate.MatchInReplyTo(order.getReplyWith()));
          step = 3;
          break;
        case 3:
          // Receive the purchase order reply
          reply = myAgent.receive(mt);
          if (reply != null) {
            // Purchase order reply received
            if (reply.getPerformative() == ACLMessage.INFORM) {
              // Purchase successful. We can terminate
              System.out.println(
                  "targetBookTitle" + " successfully purchased from agent " + reply.getSender()
                      .getName());
              System.out.println("Price = " + bestPrice);
              myAgent.doDelete();
            } else {
              System.out.println("Attempt failed: requested book already sold.");
            }

            step = 4;
          } else {
            block();
          }
          break;
      }
    }

    public boolean done() {
      if (step == 2 && bestSeller == null) {
        System.out.println("Attempt failed: " + "targetBookTitle" + " not available for sale");
      }
      return ((step == 2 && bestSeller == null) || step == 4);
    }
  }  // End of inner class RequestPerformer
}
