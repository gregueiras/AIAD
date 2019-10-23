import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {

  public static void main(String[] args) {

    try {
      Runtime rt = Runtime.instance();

      Profile p1 = new ProfileImpl();
      //p1.setParameter(Profile.GUI, "true");

      ContainerController mainController = rt.createAgentContainer(p1);
      AgentController ac1 = mainController
          .createNewAgent("Jeff", AgentManager.class.getName(), null);

/*
      AgentController ac2 = mainController
          .createNewAgent("Nerd", AgentManager.class.getName(), null);
*/
      ac1.start();
      //ac2.start();
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }
  }
}
