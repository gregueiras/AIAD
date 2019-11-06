package helper;

public enum State {
  ASSIGN_INVESTOR("assign_investor"),
  INFORM_BOARD("inform_board"),
  NEGOTIATE("negotiate"),
  DEFAULT("default"),
  SHIFT_END("shift_end"),
  ROUND_END("round_end"),
  GAME_END("game_end"),
  WAIT_END_SHIFT_ROUND("wait_end_shift_round"),
  ASSIGN_COMPANIES("assign_companies");

  private final String val;

  State(String val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return val;
  }
}
