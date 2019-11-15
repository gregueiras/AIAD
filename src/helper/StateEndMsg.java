package helper;

import jade.core.AID;
import market.InvestmentType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StateEndMsg implements Serializable {
    Map<InvestmentType, Integer> results;  //dice results
    Map<AID,Integer> investors; //current investor's capital
    Map<AID, Integer> managers; //current manager's capital

    Map<AID, Integer> winnerInvestors;
    Map<AID, Integer> winnerManagers;

    public StateEndMsg(Map<InvestmentType, Integer> results, Map<AID, Integer> investors, Map<AID, Integer> agents) {
        this.results = results;
        this.investors = investors;
        this.managers = agents;
        this.winnerInvestors = new HashMap<>();
        this.winnerManagers = new HashMap<>();
    }

    public Map<InvestmentType, Integer> getResults() {
        return results;
    }

    public void setResults(Map<InvestmentType, Integer> results) {
        this.results = results;
    }

    public Map<AID, Integer> getInvestors() {
        return investors;
    }

    public void setInvestors(Map<AID, Integer> investors) {
        this.investors = investors;
    }

    public Map<AID, Integer> getManagers() {
        return managers;
    }

    public void setManagers(Map<AID, Integer> managers) {
        this.managers = managers;
    }

    public Integer getInvestorCapital(AID agent){
        return this.investors.get(agent);
    }

    public Integer getManagerCapital(AID agent){
        return this.managers.get(agent);
    }

    public Map<AID, Integer> getWinnerInvestors() {
        return winnerInvestors;
    }

    public void setWinnerInvestors(Map<AID, Integer> winnerInvestors) {
        this.winnerInvestors = winnerInvestors;
    }

    public Map<AID, Integer> getWinnerManagers() {
        return winnerManagers;
    }

    public void setWinnerManagers(Map<AID, Integer> winnerManagers) {
        this.winnerManagers = winnerManagers;
    }
}
