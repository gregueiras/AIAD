package helper;

import market.InvestmentType;

import java.util.HashMap;
import java.util.List;

public class Config {

  public List<String> managers;
  public List<String> investors;
  public HashMap<InvestmentType, Integer> companies;

  public Config(List<String> managers, List<String> investors, List<Integer> companies) {
    this.managers = managers;
    this.investors = investors;
    this.companies = new HashMap<>();

    int index = 0;
    for (var invType : InvestmentType.values()) {
      this.companies.put(invType, companies.get(index++));
    }
  }

  @Override
  public String toString() {
    return managers + " ;; " + investors;
  }
}
