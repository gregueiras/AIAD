package Market.Dices;

import com.sun.jdi.connect.Connector;

import java.util.List;
import java.util.Random;

public abstract class Dice {
    protected List<Integer> faces;

    //This function returns a random number from the faces of a dice.
    public int launchDice(){
        Random rand = new Random();
        int res = rand.nextInt(6);
        System.out.println(this.faces.get(res));
        return this.faces.get(res);
    }
    public List<Integer> getFaces() {
        return faces;
    }

    public void setFaces(List<Integer> faces) {
        this.faces = faces;
    }
}
