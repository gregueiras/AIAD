package market;

import java.util.Hashtable;

public class WalletExamples {
  public static Hashtable<String, Company> getEx1() {
    var c1 = new Company(CompanyRisk.HIGH, "Talho", 200, InvestmentType.RED, false);
    var c2 = new Company(CompanyRisk.HIGH, "Padaria", 150, InvestmentType.RED, true);
    var c3 = new Company(CompanyRisk.NORMAL, "Peixaria", 100, InvestmentType.YELLOW, false);
    var c4 = new Company(CompanyRisk.LOW, "Serralharia", 50, InvestmentType.BLUE, false);

    var table = new Hashtable<String, Company>();
    table.put(c1.getName(), c1);
    table.put(c2.getName(), c2);
    table.put(c3.getName(), c3);
    table.put(c4.getName(), c4);

    return table;
  }
}
