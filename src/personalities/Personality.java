package personalities;

import market.Company;

public abstract class Personality {

  private int maxPriceBuy; // the max price an agent can accept to buy
    protected int maxCounterPriceBuy; //the max price an agent can propose a counter offer to buy
    protected int minPriceSell; // the min price an agent can accept to sell
    protected int minCounterPriceSell; // the min price an agent can propose a counter offer to sell
    protected double tit4tatRatio; // the ratio the agent lowers/raises the price of negotiation in a counter offer
  private double yellowRatio;
  private double redRatio;
  private double greenRatio;

    abstract boolean acceptBuyOffer(Company c);
    abstract Company counterBuyOffer(Company c);
    abstract boolean acceptSellOffer(Company c);
    abstract Company counterSellOffer(Company c);

  abstract double getTypeRatio(Company c);



  public int getPriceBuy(Company c) {
    return (int) Math.round(getTypeRatio(c) * maxPriceBuy);
  }
}
