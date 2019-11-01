package behaviours;

import agents.OurAgent;
import helper.State;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

public class WaitForMessage extends MsgReceiver {

  protected OurAgent agent;
  private State state;

  public WaitForMessage(OurAgent a, MessageTemplate template, State state) {
    super(a, template, INFINITE, null, null);

    this.state = state;
    this.agent = a;
  }

  public WaitForMessage(OurAgent a, MessageTemplate template) {
    super(a, template, INFINITE, null, null);

    this.state = State.DEFAULT;
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
    if(this.state != State.DEFAULT)
      return this.agent.onEnd(state);
    return 0;
  }
}
