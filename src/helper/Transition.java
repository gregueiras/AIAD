package helper;

import jade.core.behaviours.Behaviour;

public class Transition {

  private Behaviour initialState;
  private Behaviour finalState;
  private int event;

  public Behaviour getInitialState() {
    return initialState;
  }

  public Behaviour getFinalState() {
    return finalState;
  }

  public String getInitialName() {
    return initialState.getBehaviourName();
  }

  public String getFinalName() {
    return finalState.getBehaviourName();
  }

  public int getEvent() {
    return event;
  }

  public Transition(Behaviour initialState, Behaviour finalState, int event) {
    this.initialState = initialState;
    this.finalState = finalState;
    this.event = event;


  }

  @Override
  public String toString() {
    return getInitialName() + " -" + getEvent() + "> " + getFinalName();
  }
}
