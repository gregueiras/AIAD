package market.stocks;

import agents.OurAgent;
import market.InvestmentType;

public class Stock {
    private InvestmentType stockType;
    private OurAgent currentOwner;
    private boolean doubleValue;
    private int actualBid; //Might not be needed, its here just in case

    public Stock(InvestmentType stockType, OurAgent currentOwner, boolean doubleValue) {
        this.stockType = stockType;
        this.currentOwner = currentOwner;
        this.doubleValue = doubleValue;
        this.actualBid = -1;
    }

    public InvestmentType getStockType() {
        return stockType;
    }

    public OurAgent getCurrentOwner() {
        return currentOwner;
    }

    public boolean isDoubleValue() {
        return doubleValue;
    }

    public int getActualBid() {
        return actualBid;
    }

    public void setCurrentOwner(OurAgent currentOwner) {
        this.currentOwner = currentOwner;
    }


    public void setActualBid(int actualBid) {
        this.actualBid = actualBid;
    }
}
