import agents.AgentBoard;
import agents.AgentInvestor;
import agents.AgentManager;
import com.google.gson.Gson;
import helper.Config;
import helper.Logger;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

  private static Config readJSON(int scenario) {
    try {
      Gson gson = new Gson();
      FileReader fr = new FileReader("scenarios/scenario" + scenario + ".json");

      return gson.fromJson(fr, Config.class);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    CommandLine cmd = setupCommandLine(args);
    int scenario;
    int managers;
    int investors;

    String managersString = cmd.getOptionValue("managers");
    String investorsString = cmd.getOptionValue("investors");
    String scenarioString = cmd.getOptionValue("scenario");

    if (managersString != null && investorsString != null) {

      managers = Integer.parseInt(managersString);
      investors = Integer.parseInt(investorsString);

      if (managers <= 0 || investors <= 0) {
        System.err.println("Invalid managers or investors number");
        System.exit(1);
      }

    } else {
      if (scenarioString != null) {
        scenario = Integer.parseInt(scenarioString);
      } else {
        scenario = 1;
      }

      Config config = readJSON(scenario);
      assert config != null;
      managers = config.managers;
      investors = config.investors;
    }

    try {
      Runtime rt = Runtime.instance();
      Profile p1 = new ProfileImpl();
      ContainerController mainController = rt.createAgentContainer(p1);

      ArrayList<AgentController> controllers = new ArrayList<>();
      ArrayList<String> agents = new ArrayList<>();

      for (int i = 0; i < managers; ++i) {
        String agent = "Manager" + i;
        AgentController ac = mainController
            .createNewAgent(agent, AgentManager.class.getName(), null);
        controllers.add(ac);
        agents.add(agent);
      }

      for (int i = 0; i < investors; ++i) {
        String agent = "Investor" + i;
        AgentController ac = mainController
            .createNewAgent(agent, AgentInvestor.class.getName(), null);
        controllers.add(ac);
        agents.add(agent);
      }

      AgentController board = mainController
          .createNewAgent("BOARD", AgentBoard.class.getName(), null);
      controllers.add(board);
      agents.add("BOARD");

      String agentsArgs = createAgentsString(agents);

      AgentController sniff = mainController.createNewAgent("sniffer", "jade.tools.sniffer.Sniffer",
          new Object[]{agentsArgs});
      sniff.start();

      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      for (AgentController ac : controllers) {
        ac.start();
      }

      Logger.print("main", "This is a test");
      Logger.print("main", "Huge Success");
    } catch (UnknownHostException | StaleProxyException e) {
      e.printStackTrace();
    }
  }

  private static String createAgentsString(ArrayList<String> agents) throws UnknownHostException {
    StringBuilder agentsBuilder = new StringBuilder();
    for (String s : agents) {
      String agent = s + "@" + InetAddress.getLocalHost().getHostAddress() + ":1099/JADE;";
      agentsBuilder.append(agent);
    }
    agentsBuilder.deleteCharAt(agentsBuilder.length() - 1);
    return agentsBuilder.toString();
  }

  private static CommandLine setupCommandLine(String[] args) {
    Options options = new Options();

    Option managers = new Option("m", "managers", true, "number of managers");
    managers.setRequired(false);
    options.addOption(managers);

    Option investors = new Option("i", "investors", true, "number of investors");
    investors.setRequired(false);
    options.addOption(investors);

    Option scenario = new Option("s", "scenario", true, "number of scenario");
    scenario.setRequired(false);
    options.addOption(scenario);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd = null;

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("utility-name", options);

      System.exit(1);
    }
    return cmd;
  }
}
