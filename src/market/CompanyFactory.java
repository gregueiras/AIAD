package market;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class CompanyFactory {

  private static int PRICE = 25;

  private static LinkedList<String> NAMES = new LinkedList<>(Arrays.asList(
      "BlackXOR",
      "Can Do Coffee Distributors",
      "Candy Ask",
      "Coffeebags",
      "Conciergency",
      "Danja",
      "Dramatic Action",
      "Futurity",
      "Gaptec",
      "Greenskates",
      "Highway Cruise Lines",
      "Information Plantation",
      "Intraglobal Holdings",
      "MOONEARTH.COM",
      "MarketGene.com",
      "Marketoid",
      "Microluxe",
      "North of Wilshire (NOW) Limousine Service",
      "On the Stand",
      "Opportunity Spam",
      "Opticom",
      "PAN – Personal Assistant Network",
      "Ronbert",
      "Ronelon",
      "Salvage Your Scents",
      "Señor Floss",
      "Silky Cats",
      "Sin Sin",
      "Smooth Travels",
      "SureZone",
      "Techcraft Engineering",
      "The Coffee Directive",
      "Two Faux",
      "WriteTech"));

  public static Company createCompany(InvestmentType type) {
    Random rand = new Random();
    String randomName = NAMES.get(rand.nextInt(NAMES.size()));

    return new Company(randomName, PRICE, type, false);
  }

}
