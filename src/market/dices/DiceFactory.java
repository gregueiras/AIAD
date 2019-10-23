package market.dices;

import java.util.Arrays;

public class DiceFactory {
    public Dice createDice(DiceType type)
    {
        Dice retDice;
        switch(type){
            case BLUE:
                retDice = new Dice(Arrays.asList(0,-1,0,1,-1,1));
                break;
            case RED:
                retDice = new Dice(Arrays.asList(2,-2,3,-3,7,-7));
                break;
            case YELLOW:
                retDice = new Dice(Arrays.asList(1,-1,2,-2,3,-3));
                break;
            case GREEN:
                retDice = new Dice(Arrays.asList(0,2,-1,1,2,-2));
                break;
            default:
                retDice = null;
        }
        return retDice;
    }
}
