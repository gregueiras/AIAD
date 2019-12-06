package personalities;

import market.InvestmentType;

import java.util.Arrays;

public class Normal extends Personality {


    public Normal() {
        minCounterPriceSell = 70;
        yellowRatio = 1.5;
        redRatio = 2.0;
        greenRatio = 1.25;

        maxPriceBuy = 100;
        maxCounterPriceBuy = 150;
        minPriceSell = 90;
        minCounterPriceSell = 70;
        tit4tatRatio = 1.2;
        investmentPriority = Arrays.asList(InvestmentType.GREEN, InvestmentType.YELLOW, InvestmentType.BLUE, InvestmentType.RED);

    }

    @Override
    public String getType() {
        return "Normal";
   }
}
