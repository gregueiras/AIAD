import agents.AgentBoard;
import agents.AgentInvestor;
import agents.AgentManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) {
    try {
      Runtime rt = Runtime.instance();

      Profile p1 = new ProfileImpl();
      //p1.setParameter(Profile.GUI, "true");

      ContainerController mainController = rt.createAgentContainer(p1);
      AgentController ac1 = mainController
          .createNewAgent("Manager1", AgentManager.class.getName(), null);

      AgentController ac2 = mainController
          .createNewAgent("Investor1", AgentInvestor.class.getName(), null);
      AgentController ac3 = mainController
          .createNewAgent("Investor2", AgentInvestor.class.getName(), null);
      AgentController ac4 = mainController
          .createNewAgent("Manager3", AgentManager.class.getName(), null);

      AgentController board = mainController.createNewAgent("BOARD", AgentBoard.class.getName(), null);

      String[] agents = {"Investor1", "Manager1", "BOARD", "Investor2", "Manager3"};
      StringBuilder agentsBuilder = new StringBuilder();
      for (String s : agents) {
        String agent = s + "@10.227.152.201:1099/JADE;";
        agentsBuilder.append(agent);
      }
      agentsBuilder.deleteCharAt(agentsBuilder.length() - 1);
      String agentsArgs = agentsBuilder.toString();
      System.err.println(agentsArgs);

      AgentController sniff = mainController.createNewAgent("sniffer", "jade.tools.sniffer.Sniffer",
          new Object[]{agentsArgs});
      sniff.start();
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ac1.start();

      ac2.start();
      ac3.start();
      ac4.start();

      board.start();

    } catch (StaleProxyException e) {
      e.printStackTrace();
    }
  }
}
