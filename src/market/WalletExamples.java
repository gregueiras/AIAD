package market;

import java.util.Hashtable;

public class WalletExamples {

  public static Hashtable<String, Company> getEx1() {
    var c1 = new Company("Talho", 200, InvestmentType.RED, false);
    var c2 = new Company("Padaria", 150, InvestmentType.BLUE, true);
    var c3 = new Company("Peixaria", 100, InvestmentType.GREEN, false);
    var c4 = new Company("Serralharia", 50, InvestmentType.YELLOW, false);

    var table = new Hashtable<String, Company>();
    table.put(c1.getName(), c1);
    table.put(c2.getName(), c2);
    table.put(c3.getName(), c3);
    table.put(c4.getName(), c4);

    return table;
  }
}
