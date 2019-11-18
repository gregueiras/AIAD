package personalities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PersonalityFactory {

    public PersonalityFactory(){

    }

    public Personality giveRandomPersonality(){
        List<String> personalityList = Arrays.asList("Normal","HighRoller","SafeBetter", "Crazy");
        Collections.shuffle(personalityList);

        switch (personalityList.get(0)){
            case "Normal":
                return new Normal();
            case "HighRoller":
                return new HighRoller();
            case "SafeBetter":
                return new SafeBetter();
            case "Crazy":
                return new Crazy();
            default:
                return new Normal();
        }
    }
}
