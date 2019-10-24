package agents;

import behaviours.OurAgent;
import behaviours.Print;
import behaviours.WaitForMessage;
import helper.StateMachine;
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

  // Put agent initializations here
  protected void setup() {
    // Printout a welcome message
    System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");

    // Register the manager service in the yellow pages
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType("wall-street-investor");
    sd.setName("wall-Street-investor_" + getAID().getName());
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    // Get the title of the book to buy as a start-up argument
    Object[] args = getArguments();
    if (args != null && args.length > 0) {
      var targetBookTitle = (String) args[0];
      System.out.println("Target book is " + targetBookTitle);

      // Add a TickerBehaviour that schedules a request to seller agents every minute
      /*
      addBehaviour(new TickerBehaviour(this, 60000) {
        protected void onTick() {
          System.out.println("Trying to buy " + targetBookTitle);
          // Update the list of seller agents
          DFAgentDescription template = new DFAgentDescription();
          ServiceDescription sd = new ServiceDescription();
          sd.setType("wall-street-manager");
          template.addServices(sd);
          try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            System.out.println("Found the following seller agents:");
            sellerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
              sellerAgents[i] = result[i].getName();
              System.out.println(sellerAgents[i].getName());
            }
          } catch (FIPAException fe) {
            fe.printStackTrace();
          }

        }
      });
       */
    } else {
      Behaviour printStart = new Print("Waiting for msg");
      Behaviour waitInform = new WaitForMessage(this,
          MessageTemplate.MatchPerformative(ACLMessage.INFORM), this, 0);
      Behaviour printEnd = new Print("MSG Received");

      Transition t1 = new Transition(printStart, waitInform);
      Transition t2 = new Transition(waitInform, printEnd);

      StateMachine sm = new StateMachine(this, printStart, printEnd, t1, t2);
      addBehaviour(sm);
    }
  }

  // Put agent clean-up operations here
  protected void takeDown() {
    // Printout a dismissal message
    System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
  }

  @Override
  public void handleMessage(ACLMessage msg) {
    if (msg != null) {
      System.out.println(msg.getPerformative() + ": " + msg.getContent());
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