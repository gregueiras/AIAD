package personalities;

import market.InvestmentType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Crazy extends Personality{


    public Crazy() {
        yellowRatio = (Math.random() + 1);
        redRatio = (Math.random() + 1);
        greenRatio = (Math.random() + 1);
        blueRatio = (Math.random() + 1);

        maxPriceBuy = (int)(Math.random()*100);
        maxCounterPriceBuy = maxPriceBuy + 50;
        minPriceSell = (int)(Math.random()*100);
        minCounterPriceSell = (int)(Math.random()*100 - 35);

        tit4tatRatio = (Math.random() + 1);;
        investmentPriority = Arrays.asList(InvestmentType.BLUE, InvestmentType.GREEN, InvestmentType.YELLOW, InvestmentType.RED);
        Collections.shuffle(investmentPriority);

    }
}
