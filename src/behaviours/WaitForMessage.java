package behaviours;

import agents.OurAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

public class WaitForMessage extends MsgReceiver {

  private OurAgent agent;
  private int onEnd;

  public WaitForMessage(OurAgent a, MessageTemplate template, int onEnd) {
    super(a, template, INFINITE, null, null);

    this.onEnd = onEnd;
    this.agent = a;
  }

  @Override
  protected void handleMessage(ACLMessage msg) {
    try {
      this.agent.handleMessage(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int onEnd() {
    return this.onEnd;
  }
}
