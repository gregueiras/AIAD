package market.profits;

import market.dices.Dice;

import java.util.List;

public class Profits {
    protected List<Integer> lineOfProfits;
    protected int profitsPosition;
    private Dice dice;

    public Profits(List<Integer> line){
        profitsPosition = 3;
        lineOfProfits = line;
    }

    public void roll_dice(){
        int res = dice.launchDice();
        profitsPosition += res;
        if(profitsPosition < 0)
            profitsPosition = 0;
        else if(profitsPosition > 7)
            profitsPosition = 7;
    }

    public int getActualProfit(){
        return lineOfProfits.get(profitsPosition);
    }

    public List<Integer> getLineOfProfits() {
        return lineOfProfits;
    }

    public void setLineOfProfits(List<Integer> lineOfProfits) {
        this.lineOfProfits = lineOfProfits;
    }

    public int getProfitsPosition() {
        return profitsPosition;
    }

    public void setProfitsPosition(int profitsPosition) {
        this.profitsPosition = profitsPosition;
    }

    public Dice getDice() {
        return dice;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }
}
