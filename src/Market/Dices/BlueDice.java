package Market.Dices;

import java.sql.Array;
import java.util.*;

public class BlueDice extends Dice {

    public BlueDice(){
            this.faces = new ArrayList<>(Arrays.asList(0,-1,0,1,-1,1));
    }


}
