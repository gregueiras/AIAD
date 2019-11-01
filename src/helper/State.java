package helper;

public enum State {
    ASSIGN_INVESTOR("assign_investor"), INFORM_BOARD("inform_board"), NEGOTIATE("negotiate"), DEFAULT("default");

    private final String val;

    private State(String val){
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
