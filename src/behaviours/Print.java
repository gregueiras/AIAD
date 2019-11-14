package behaviours;


import helper.Logger;
import jade.core.behaviours.OneShotBehaviour;

public class Print extends OneShotBehaviour {

  private String msg;
  private int onEnd;

  private Print(String msg, int onEnd) {
    this.msg = msg;
    this.onEnd = onEnd;

    super.setBehaviourName("Print_" + msg);
  }

  public Print(String msg) {
    this(msg, 0);
  }

  @Override
  public void action() {
    System.out.println(this.msg);
    Logger.print(this.getAgent().getLocalName(), this.msg);
  }

  @Override
  public int onEnd() {
    return this.onEnd;
  }
}
