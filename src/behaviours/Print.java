package behaviours;

import jade.core.behaviours.OneShotBehaviour;

public class Print extends OneShotBehaviour {

  private String msg;
  private int onEnd;

  public Print(String msg, int onEnd) {
    this.msg = msg; this.onEnd = onEnd;
    super.setBehaviourName("Print_"+msg);
  }
  @Override
  public void action() {
    System.out.println(this.msg);
  }

  @Override
  public int onEnd() {
    return this.onEnd;
  }
}
