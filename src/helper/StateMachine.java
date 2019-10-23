package helper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class StateMachine extends FSMBehaviour {
  public StateMachine(Agent a, Behaviour initialState, Behaviour lastState, Transition... transitions) {
    super(a);

    registerFirstState(initialState, initialState.getBehaviourName()); // TODO: Check if initial/last state is really a OurBehaviour
    registerLastState( lastState, lastState.getBehaviourName());

    for(Transition transition : transitions) {
      registerState(transition.getInitialState(), transition.getInitialName());
      registerTransition(transition.getInitialName(), transition.getFinalName(), transition.getEvent());
//      System.out.println("Registered " + transition);
    } 

  }



}
