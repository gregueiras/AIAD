package Market.Dices;

import java.util.ArrayList;
import java.util.Arrays;

public class YellowDice extends Dice {
    public YellowDice(){
        this.faces = new ArrayList<>(Arrays.asList(1,-1,2,-2,3,-3));
    }
}
