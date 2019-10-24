package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public abstract class OurAgent extends Agent {

  protected abstract void handleMessage(ACLMessage msg);
}
