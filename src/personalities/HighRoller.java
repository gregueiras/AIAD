package personalities;

import market.InvestmentType;

import java.util.Arrays;
import java.util.List;

public class HighRoller extends Personality {


    public HighRoller() {
        minCounterPriceSell = 70;
        yellowRatio = 1.8;
        redRatio = 2.4;
        greenRatio = 1.1;

        maxPriceBuy = 90;
        maxCounterPriceBuy = 150;
        minPriceSell = 90;
        minCounterPriceSell = 70;
        tit4tatRatio = 1.2;
        investmentPriority = Arrays.asList(InvestmentType.RED, InvestmentType.YELLOW, InvestmentType.GREEN, InvestmentType.BLUE);

    }
    public String getType(){
        return "HighRoller";
    }
}
