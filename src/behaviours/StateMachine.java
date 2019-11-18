package behaviours;

import helper.Logger;
import helper.Transition;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class StateMachine extends FSMBehaviour {

  private Agent agent;

  public StateMachine(Agent a, Behaviour initialState, Behaviour lastState, Transition... transitions) {
    super(a);
    this.agent = a;

    registerFirstState(initialState, initialState.getBehaviourName());
    registerLastState( lastState, lastState.getBehaviourName());

    for(Transition transition : transitions) {
      registerState(transition.getInitialState(), transition.getInitialName());

      if (transition.isDefault()) {
        registerDefaultTransition(transition.getInitialName(), transition.getFinalName());
      } else {
        registerTransition(transition.getInitialName(), transition.getFinalName(),
            transition.getEvent());
      }
      //      System.out.println("Registered " + transition);
    }
  }

  public int onEnd() {
    Logger.print(this.agent.getLocalName(), "FSM behaviour completed.");
    this.agent.doDelete();
    return super.onEnd();
  }



}
