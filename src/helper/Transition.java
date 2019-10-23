package helper;

import jade.core.behaviours.Behaviour;

public class Transition {

  private Behaviour initialState;
  private Behaviour finalState;
  private int event;
  private boolean isDefault = false;

  public Transition(Behaviour initialState, Behaviour finalState, int event) {
    this.initialState = initialState;
    this.finalState = finalState;
    this.event = event;
  }

  public Behaviour getFinalState() {
    return finalState;
  }

  public Transition(Behaviour initialState, Behaviour finalState) {
    this.initialState = initialState;
    this.finalState = finalState;
    this.isDefault = true;
  }

  Behaviour getInitialState() {
    return initialState;
  }

  String getInitialName() {
    return initialState.getBehaviourName();
  }

  String getFinalName() {
    return finalState.getBehaviourName();
  }

  int getEvent() {
    return event;
  }

  boolean isDefault() {
    return isDefault;
  }

  @Override
  public String toString() {
    return getInitialName() + " -" + getEvent() + "> " + getFinalName();
  }
}
