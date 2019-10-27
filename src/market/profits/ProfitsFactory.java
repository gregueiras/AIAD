package market.profits;

import market.InvestmentType;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ProfitsFactory {

    public Profits createProfits(InvestmentType type)
    {
        Profits ret;
        switch (type) {
            case RED:
                ret = new Profits(Arrays.asList(-20,-10,0,30,40,50,60,70));
                break;
            case YELLOW:
                ret = new Profits(Arrays.asList(-10,0,0,30,40,40,60,60));
                break;
            case GREEN:
                ret = new Profits(Arrays.asList(0,10,20,30,30,40,50,60));
                break;
            case BLUE:
                ret = new Profits(Arrays.asList(20,20,20,30,30,30,40,40));
                break;
            default:
                ret = null;
                System.err.println("Invalid profit type.");
                break;

        }
        return ret;
    }
}
