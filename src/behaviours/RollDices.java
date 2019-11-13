package behaviours;

import agents.AgentBoard;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import market.InvestmentType;
import market.dices.Dice;
import market.dices.DiceFactory;

import javax.sound.midi.SysexMessage;
import java.util.Map;

public class RollDices extends OneShotBehaviour {

    private Map<InvestmentType, Dice> dices;
    private AgentBoard agent;

    public RollDices(AgentBoard agent) {
        DiceFactory diceFactory = new DiceFactory();
        this.dices = diceFactory.createAllDice();
        this.agent = agent;
        super.setBehaviourName("Roll_Dices_" + this.agent.getName());
    }

    @Override
    public void action() {
        for (Map.Entry<InvestmentType, Dice> entry : this.dices.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            InvestmentType type = entry.getKey();
            Dice dice = entry.getValue();
            int result = dice.launchDice();
            this.agent.setDiceResult(type, result);
        }
        System.out.println("ROLL DICES: " + this.agent.getDiceResults());
    }
}
