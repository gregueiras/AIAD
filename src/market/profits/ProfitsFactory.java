package market.profits;

import market.InvestmentType;
import market.dices.DiceFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfitsFactory {

    public static Profits createProfits(InvestmentType type)
    {
        Profits ret;
        switch (type) {
            case RED:
                ret = new Profits(Arrays.asList(-20,-10,0,30,40,50,60,70));
                ret.setDice(DiceFactory.createDice(InvestmentType.RED));
                break;
            case YELLOW:
                ret = new Profits(Arrays.asList(-10,0,0,30,40,40,60,60));
                ret.setDice(DiceFactory.createDice(InvestmentType.YELLOW));
                break;
            case GREEN:
                ret = new Profits(Arrays.asList(0,10,20,30,30,40,50,60));
                ret.setDice(DiceFactory.createDice(InvestmentType.GREEN));
                break;
            case BLUE:
                ret = new Profits(Arrays.asList(20,20,20,30,30,30,40,40));
                ret.setDice(DiceFactory.createDice(InvestmentType.BLUE));
                break;
            default:
                ret = null;
                System.err.println("Invalid profit type.");
                break;

        }
        return ret;
    }

    public static Map<InvestmentType, Profits> createAllProfits(){

        Map<InvestmentType,Profits> allProfits = new HashMap<InvestmentType,Profits>();

        allProfits.put(InvestmentType.RED, createProfits(InvestmentType.RED));
        allProfits.put(InvestmentType.BLUE, createProfits(InvestmentType.BLUE));
        allProfits.put(InvestmentType.GREEN, createProfits(InvestmentType.GREEN));
        allProfits.put(InvestmentType.YELLOW, createProfits(InvestmentType.YELLOW));

        return allProfits;
    }
}
