package behaviours;

import agents.AgentBoard;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import market.InvestmentType;
import market.dices.Dice;
import market.dices.DiceFactory;
import market.profits.Profits;

import javax.sound.midi.SysexMessage;
import java.util.Map;

public class RollDices extends OneShotBehaviour {

    private AgentBoard agent;

    public RollDices(AgentBoard agent) {
        DiceFactory diceFactory = new DiceFactory();
        this.agent = agent;
        super.setBehaviourName("Roll_Dices_" + this.agent.getName());
    }

    @Override
    public void action() {
        for (Map.Entry<InvestmentType, Profits> entry : this.agent.getProfitsResults().entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            Profits profits = entry.getValue();
            profits.roll_dice();
        }
        System.out.println("ROLL DICES: " + this.agent.getProfitsResults());
    }
}
