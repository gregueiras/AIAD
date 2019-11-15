package market;

import java.io.Serializable;

public enum InvestmentType implements Serializable {
  RED, BLUE, YELLOW, GREEN;

  @Override
  public String toString() {
    return this.name();
  }
}
