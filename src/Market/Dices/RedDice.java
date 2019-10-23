package Market.Dices;

import java.util.ArrayList;
import java.util.Arrays;

public class RedDice extends Dice {
    public RedDice(){
        this.faces = new ArrayList<>(Arrays.asList(2,-2,3,-3,7,-7));
    }
}
