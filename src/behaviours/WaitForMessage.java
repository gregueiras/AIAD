package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

public class WaitForMessage extends MsgReceiver {

  private Agent agent;
  private OurAgent function;
  private int onEnd;

  public WaitForMessage(Agent a, MessageTemplate template, OurAgent function, int onEnd) {
    super(a, template, INFINITE, null, null);

    this.function = function;
    this.onEnd = onEnd;

  }

  @Override
  protected void handleMessage(ACLMessage msg) {
    try {
      this.function.handleMessage(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int onEnd() {
    return this.onEnd;
  }
}
