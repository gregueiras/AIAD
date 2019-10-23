import behaviours.Print;
import helper.StateMachine;
import helper.Transition;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AgentManager extends Agent {

  // The companies that the manager has in it's wallet, mapped by title
  private Hashtable<String, Company> wallet;
  private static int nextCounter = 0;

  // Put agent initializations here
  protected void setup() {
    // Create the catalogue
    wallet = WalletExamples.getEx1();
    int counter = nextCounter++;

    // Register the book-selling service in the yellow pages
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType("wall-street-manager" + counter);
    sd.setName("JADE-Wall-Street" + counter);
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }

    Behaviour printHW = new Print("Hello World!", 1);
    Behaviour printHey = new Print("Hey!", 3);
    Behaviour printBye = new Print("Hasta la vista, baby!", 2);
    Behaviour printHey2 = new Print("Hey!2", 0);

    Transition t1 = new Transition(printHW, printHey, 1);
    Transition t2 = new Transition(printHey, printBye, 3);
    Transition t3 = new Transition(printBye, printHey2, 2);

    StateMachine sm = new StateMachine(this, printHW, printBye, t1, t2, t3);

    addBehaviour(sm);
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




  private class SellCompanies extends ContractNetInitiator {

    public SellCompanies(Agent a, ACLMessage cfp) {
      super(a, cfp);
    }

    protected void handlePropose(ACLMessage propose, Vector v) {
      System.out.println("Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
      System.out.println("Agent "+refuse.getSender().getName()+" refused");
    }

    protected void handleFailure(ACLMessage failure) {
      if (failure.getSender().equals(myAgent.getAMS())) {
        // FAILURE notification from the JADE runtime: the receiver
        // does not exist
        System.out.println("Responder does not exist");
      }
      else {
        System.out.println("Agent "+failure.getSender().getName()+" failed");
      }
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

      // Evaluate proposals.
      int bestProposal = -1;
      AID bestProposer = null;
      ACLMessage accept = null;
      Enumeration e = responses.elements();
      while (e.hasMoreElements()) {
        ACLMessage msg = (ACLMessage) e.nextElement();
        if (msg.getPerformative() == ACLMessage.PROPOSE) {
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
          acceptances.addElement(reply);
          int proposal = Integer.parseInt(msg.getContent());
          if (proposal > bestProposal) {
            bestProposal = proposal;
            bestProposer = msg.getSender();
            accept = reply;
          }
        }
      }
      // Accept the proposal of the best proposer
      if (accept != null) {
        System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
      }
    }

    protected void handleInform(ACLMessage inform) {
      System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
    }
  }

}
