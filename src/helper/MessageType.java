package helper;

public enum MessageType {
    INFORM_BOARD("Investor"), NEGOTIATE("Manager");

    private final String val;

    private MessageType(String val){
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
