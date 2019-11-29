package personalities;

public class PersonalityFactory {

  private static Personality create(String personality) {
    switch (personality) {
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

  public static Personality createPersonality(String personality) {
    return create(personality);
  }
}
