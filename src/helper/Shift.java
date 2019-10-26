package helper;

import java.util.LinkedList;
import java.util.List;

public class Shift {

    private List<NegotiationPair> pairs;


    public Shift() {
        this.pairs = new LinkedList<>();
    }

    public void addPair(NegotiationPair pair){
        this.pairs.add(pair);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "pairs=" + pairs +
                '}';
    }
}
