package market;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketBoard {
    List<Integer> redLine = new ArrayList<>(Arrays.asList(-20,-10,0,30,40,50,60,70));
    List<Integer> yellowLine = new ArrayList<>(Arrays.asList(-10,0,0,30,40,40,60,60));
    List<Integer> greenLine = new ArrayList<>(Arrays.asList(0,10,20,30,30,40,50,60));
    List<Integer> blueLine = new ArrayList<>(Arrays.asList(20,20,20,30,30,30,40,40));
}
