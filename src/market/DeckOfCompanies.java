package market;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeckOfCompanies {
    List<Company> deck;

    DeckOfCompanies(){
        deck = Arrays.asList(
                new Company("Red1", 30, InvestmentType.RED, false),
                new Company("Blue1", 10, InvestmentType.BLUE, false),
                new Company("Yellow1", 25, InvestmentType.YELLOW, false),
                new Company("Green1", 20, InvestmentType.GREEN, false),
                new Company("Red2", 30, InvestmentType.RED, true),
                new Company("Blue2", 10, InvestmentType.BLUE, true),
                new Company("Yellow2", 25, InvestmentType.YELLOW, true),
                new Company("Green2", 20, InvestmentType.GREEN, true),
                new Company("Red3", 30, InvestmentType.RED, false),
                new Company("Blue3", 10, InvestmentType.BLUE, false),
                new Company("Yellow3", 25, InvestmentType.YELLOW, false),
                new Company("Green3", 20, InvestmentType.GREEN, false),
                new Company("Red4", 30, InvestmentType.RED, true),
                new Company("Blue4", 10, InvestmentType.BLUE, true),
                new Company("Yellow4", 25, InvestmentType.YELLOW, true),
                new Company("Green4", 20, InvestmentType.GREEN, true),
                new Company("Red5", 30, InvestmentType.RED, false),
                new Company("Blue5", 10, InvestmentType.BLUE, false),
                new Company("Yellow5", 25, InvestmentType.YELLOW, false),
                new Company("Green5", 20, InvestmentType.GREEN, false),
                new Company("Red6", 30, InvestmentType.RED, false),
                new Company("Blue6", 10, InvestmentType.BLUE, false),
                new Company("Yellow6", 25, InvestmentType.YELLOW, false),
                new Company("Green6", 20, InvestmentType.GREEN, false)
                );

        Collections.shuffle(deck);
    }

    DeckOfCompanies(List<Company> startingDeck){
        deck = startingDeck;
        Collections.shuffle(deck);
    }

    Company drawOne(){
        Company ret = deck.get(0);
        deck.remove(0);
        return ret;
    }

    void shuffleCompany(Company comp){
        deck.add(comp);
        Collections.shuffle(deck);
    }
}
