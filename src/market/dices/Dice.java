package market.dices;

import helper.Logger;
import java.util.List;
import java.util.Random;

public class Dice {
    private List<Integer> faces;

    Dice(List<Integer> faces){
        this.faces = faces;
    }

    //This function returns a random number from the faces of a dice.
    public int launchDice(){
        Random rand = new Random();
        int res = rand.nextInt(6);
        Logger.print("dice", this.faces.get(res));
        return this.faces.get(res);
    }
    public List<Integer> getFaces() {
        return faces;
    }

    public void setFaces(List<Integer> faces) {
        this.faces = faces;
    }
}
