package helper;

import jade.core.AID;

public class NegotiationPair {

    private AID manager;
    private AID investor;

    public NegotiationPair(AID manager, AID investor) {
        this.manager = manager;
        this.investor = investor;
    }

    public AID getManager() {
        return manager;
    }

    public void setManager(AID manager) {
        this.manager = manager;
    }

    public AID getInvestor() {
        return investor;
    }

    public void setInvestor(AID investor) {
        this.investor = investor;
    }

    @Override
    public String toString() {
        return "NegotiationPair{" +
                "manager=" + manager +
                ", investor=" + investor +
                '}';
    }
}
