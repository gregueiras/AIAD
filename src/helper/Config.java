package helper;

public class Config {

  public int managers;
  public int investors;

  public Config(int managers, int investors) {
    this.managers = managers;
    this.investors = investors;
  }

  @Override
  public String toString() {
    return managers + " ;; " + investors;
  }
}
