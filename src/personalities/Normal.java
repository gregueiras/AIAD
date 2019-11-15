package personalities;

import market.Company;

import java.util.Random;

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

  public boolean acceptBuyOffer(Company c) {
    if (c.getPrice() < (maxPriceBuy * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0)))
      return true;
    return false;
  }

    public Company counterBuyOffer(Company c) {
    if (c.getPrice() < (maxCounterPriceBuy * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))) {
      Company counter = new Company(c);
      counter.setPrice((int)(c.getPrice() * (2 - tit4tatRatio)));
      return counter;
    }
    return null;
  }

    public boolean acceptSellOffer(Company c) {
    return c.getPrice() > (minPriceSell * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0));
  }

    public Company counterSellOffer(Company c) {
    if (c.getPrice() > (minPriceSell * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))) {
      Company counter = new Company(c);
      counter.setPrice((int) (c.getPrice() * (tit4tatRatio)));
      return counter;
    }
    return null;
  }

  @Override
  double getTypeRatio(Company c) {
    switch (c.getType()) {
      case RED:
        return redRatio;
      case BLUE:
        return 1.0;
      case YELLOW:
        return yellowRatio;
      case GREEN:
        return greenRatio;
      default:
        return -1;
    }
  }

  public Company randomOffer(Company c, double floor){
    double rand =  Math.random() + floor;
    int newValue = (int) (rand * c.getPrice());
    Company offer = new Company(c);
    offer.setPrice(newValue);
    return offer;
  }


}
