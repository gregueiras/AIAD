package agents;

public enum AgentType {
    INVESTOR("Investor"), MANAGER("Manager"), BOARD("Boards");

    private final String val;

    private AgentType(String val){
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
