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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import market.InvestmentType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import personalities.Personality;
import personalities.PersonalityFactory;

public class Main {

  private static final Random RANDOM = new Random();

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
    List<String> managers;
    List<String> investors;
    HashMap<InvestmentType, Integer> companies;

    String scenarioString = cmd.getOptionValue("scenario");

    Config config;
    if (scenarioString != null) {
      scenario = Integer.parseInt(scenarioString);
      config = readJSON(scenario);
    } else {
      List<String> man = generateCombination(3);
      List<String> inv = generateCombination(3);
      List<Integer> invT = generateCompanies(4);
      config = new Config(man, inv, invT);
    }

    assert config != null;
    managers = config.managers;
    investors = config.investors;
    companies = config.companies;

    try {
      Runtime rt = Runtime.instance();
      Profile p1 = new ProfileImpl();
      ContainerController mainController = rt.createMainContainer(p1);

      ArrayList<AgentController> controllers = new ArrayList<>();
      ArrayList<String> agents = new ArrayList<>();

      for (int i = 0; i < managers.size(); ++i) {
        String personalityString = managers.get(i);
        String agent = "Manager" + i + "#" + personalityString;

        Personality personality = PersonalityFactory.createPersonality(personalityString);

        AgentController ac = mainController
            .createNewAgent(agent, AgentManager.class.getName(), new Personality[]{personality});
        controllers.add(ac);
        agents.add(agent);
      }

      for (int i = 0; i < investors.size(); ++i) {
        String personalityString = investors.get(i);
        String agent = "Investor" + i + "#" + personalityString;

        Personality personality = PersonalityFactory.createPersonality(personalityString);

        AgentController ac = mainController
            .createNewAgent(agent, AgentInvestor.class.getName(), new Personality[]{personality});
        controllers.add(ac);
        agents.add(agent);
      }

      System.err.println(managers);
      System.err.println(investors);
      System.err.println(companies);

      AgentController board = mainController
              .createNewAgent("BOARD", AgentBoard.class.getName(), new Object[]{companies});
      controllers.add(board);
      agents.add("BOARD");

      /*
      String agentsArgs = createAgentsString(agents);

      AgentController sniff = mainController.createNewAgent("sniffer", "jade.tools.sniffer.Sniffer",
          new Object[]{agentsArgs});
      sniff.start();
      */

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
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }
  }

  private static List<Integer> generateCompanies(int maxCompanies) {
    List<Integer> res = new ArrayList<>();

    int SIZE = InvestmentType.values().length;
    for (int i = 0; i < SIZE; i++) {
      int r = RANDOM.nextInt(maxCompanies);
      res.add(r);
    }

    return res;
  }


  private static List<String> generateCombination(int i) {
    List<String> res = new ArrayList<>();

    String[] VALUES = {"Crazy", "HighRoller", "Normal", "SafeBetter"};
    int SIZE = VALUES.length;

    for (int j = 0; j < i; j++) {
      res.add(VALUES[RANDOM.nextInt(SIZE)]);
    }

    return res;
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
