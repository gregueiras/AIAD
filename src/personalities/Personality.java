package personalities;

import market.Company;
import market.InvestmentType;

import java.util.List;

public class Personality {

    protected int maxPriceBuy; // the max price an agent can accept to buy
    protected int maxCounterPriceBuy; //the max price an agent can propose a counter offer to buy
    protected int minPriceSell; // the min price an agent can accept to sell
    protected int minCounterPriceSell; // the min price an agent can propose a counter offer to sell
    protected double tit4tatRatio; // the ratio the agent lowers/raises the price of negotiation in a counter offer
    protected double yellowRatio;
    protected double redRatio;
    protected double greenRatio;
    protected double blueRatio;

    public List<InvestmentType> getInvestmentPriority() {
        return investmentPriority;
    }

    protected List<InvestmentType> investmentPriority;


    public boolean acceptBuyOffer(Company c) {
        if (c.getPrice() < (maxPriceBuy * getTypeRatio(c)))
            return true;
        return false;
    }

    public Company counterBuyOffer(Company c) {
        if (c.getPrice() < (maxCounterPriceBuy * getTypeRatio(c))) {
            Company counter = new Company(c);
            counter.setPrice((int) (c.getPrice() * (2 - tit4tatRatio)));
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


    protected double getTypeRatio(Company c) {
        switch (c.getType()) {
            case RED:
                return redRatio;
            case BLUE:
                return blueRatio;
            case YELLOW:
                return yellowRatio;
            case GREEN:
                return greenRatio;
            default:
                return -1;
        }
    }

    public Company randomOffer(Company c, double floor) {
        double rand = Math.random() + floor;
        int newValue = (int) (rand * c.getPrice());
        Company offer = new Company(c);
        offer.setPrice(newValue);
        return offer;
    }


    public int getPriceBuy(Company c) {
        return (int) Math.round(getTypeRatio(c) * maxPriceBuy);
    }

    public String getType() {
        return "";
    }
}
