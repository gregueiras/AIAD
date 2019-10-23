package market;

import java.util.Hashtable;

public class WalletExamples {
  public static Hashtable<String, Company> getEx1() {
    var c1 = new Company(COMPANY_RISK.HIGH, "Talho", 200);
    var c2 = new Company(COMPANY_RISK.HIGH, "Padaria", 150);
    var c3 = new Company(COMPANY_RISK.NORMAL, "Peixaria", 100);
    var c4 = new Company(COMPANY_RISK.LOW, "Serralharia", 50);

    var table = new Hashtable<String, Company>();
    table.put(c1.getName(), c1);
    table.put(c2.getName(), c2);
    table.put(c3.getName(), c3);
    table.put(c4.getName(), c4);

    return table;
  }
}
