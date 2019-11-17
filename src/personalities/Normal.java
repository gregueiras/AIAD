package personalities;

import market.Company;
import market.InvestmentType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
}
