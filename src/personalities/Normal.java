package personalities;

import market.Company;

public class Normal extends Personality {

  protected int minCounterPriceSell = 70; // the min price an agent can propose a counter offer to sell
  protected double yellowRatio = 1.5;
  protected double redRatio = 2.0;
  protected double greenRatio = 1.25;
  //if a company has doubleValue the price is doubled
  private int maxPriceBuy = 100; // the max price an agent can accept to buy
  private int maxCounterPriceBuy = 150; //the max price an agent can propose a counter offer to buy
  private int minPriceSell = 90; // the min price an agent can accept to sell
  private double tit4tatRatio = 1.2; // the ratio the agent lowers/raises the price of negotiation in a counter offer

  boolean acceptBuyOffer(Company c) {
    return c.getPrice() < (maxPriceBuy * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0));
  }

  Company counterBuyOffer(Company c) {
    if (c.getPrice() < (maxCounterPriceBuy * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))) {
      Company counter = new Company(c.getName(), (int) (c.getPrice() * (2 - tit4tatRatio)),
          c.getType(), c.isDoubleValue());
      return counter;
    }
    return null;
  }

  boolean acceptSellOffer(Company c) {
    return c.getPrice() > (minPriceSell * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0));
  }

  Company counterSellOffer(Company c) {
    if (c.getPrice() > (minPriceSell * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))) {
      Company counter = new Company(c.getName(), (int) (c.getPrice() * (tit4tatRatio)), c.getType(),
          c.isDoubleValue());
      return counter;
    }
    return null;
  }
}
