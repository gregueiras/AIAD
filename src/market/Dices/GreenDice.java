package market.Dices;

import java.util.ArrayList;
import java.util.Arrays;

public class GreenDice extends Dice{
    public GreenDice(){
        this.faces = new ArrayList<>(Arrays.asList(0,2,-1,1,2,-2));
    }

}
