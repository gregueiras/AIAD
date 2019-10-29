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

    public List<NegotiationPair> getPairs() {
        return pairs;
    }

    public void setPairs(List<NegotiationPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "pairs=" + pairs +
                '}';
    }
}
