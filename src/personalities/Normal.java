package personalities;

import market.Company;

public class Normal extends Personality {
    //if a company has doubleValue the price is doubled
    protected int maxPriceBuy = 100; // the max price an agent can accept to buy
    protected int maxCounterPriceBuy = 150; //the max price an agent can propose a counter offer to buy
    protected int minPriceSell = 90; // the min price an agent can accept to sell
    protected int minCounterPriceSell = 70; // the min price an agent can propose a counter offer to sell
    protected double tit4tatRatio = 1.2; // the ratio the agent lowers/raises the price of negotiation in a counter offer
    protected double yellowRatio = 1.5;
    protected double redRatio = 2.0;
    protected double greenRatio = 1.25;

    double getTypeRatio(Company c){
        switch(c.getType()){
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

    boolean acceptBuyOffer(Company c){
         if(c.getPrice() < (maxPriceBuy * getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0)))
            return true;
         return false;
     }

     Company counterBuyOffer(Company c){
        if(c.getPrice() < (maxCounterPriceBuy* getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))){
            Company counter = new Company(c.getName(),(int)(c.getPrice()*(2-tit4tatRatio)),c.getType(),c.isDoubleValue());
            return counter;
         }
         return null;
     }

     boolean acceptSellOffer(Company c){
        if(c.getPrice() > (minPriceSell* getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))){
            return true;
        }
        return false;
     }

    Company counterSellOffer(Company c) {
        if(c.getPrice() > (minPriceSell* getTypeRatio(c) * (c.isDoubleValue() ? 2.0 : 1.0))){
            Company counter = new Company(c.getName(),(int)(c.getPrice()*(tit4tatRatio)),c.getType(),c.isDoubleValue());
            return counter;
        }
        return null;
    }
}
