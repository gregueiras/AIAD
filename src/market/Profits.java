package market;

import java.util.List;

public abstract class Profits {
    protected List<Integer> lineOfProfits;
    protected int profitsPosition = 3;

    public abstract int roll_dice();
}
