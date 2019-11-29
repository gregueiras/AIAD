package helper;

import java.util.List;

public class Config {

  public List<String> managers;
  public List<String> investors;

  public Config(List<String> managers, List<String> investors) {
    this.managers = managers;
    this.investors = investors;
  }

  @Override
  public String toString() {
    return managers + " ;; " + investors;
  }
}
