package personalities;

import market.InvestmentType;

import java.util.Arrays;
import java.util.List;

public class SafeBetter extends Personality {


    public SafeBetter() {
        minCounterPriceSell = 70;
        yellowRatio = 1.4;
        redRatio = 1.3;
        greenRatio = 1.4;
        blueRatio = 1.2;

        maxPriceBuy = 75;
        maxCounterPriceBuy = 150;
        minPriceSell = 90;
        minCounterPriceSell = 70;
        tit4tatRatio = 1.2;
        investmentPriority = Arrays.asList(InvestmentType.BLUE, InvestmentType.GREEN, InvestmentType.YELLOW, InvestmentType.RED);

    }

    public String getType(){
        return "SafeBetter";
    }
}
