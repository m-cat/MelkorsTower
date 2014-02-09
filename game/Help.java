
public class Help {
  public static String helpDisplay() {
    String returnString = "Move with 'go [direction]' or just type the direction.\n";
    returnString += "Fight things with 'attack'.\n";
    returnString += "Pick things up with 'take'.\n";
    returnString += "Look around with 'look'.\n";
    returnString += "Check your inventory with 'inv'.\n";
    returnString += "Open/close doors with 'open' and 'close'.\n";
    returnString += "Wait a turn with 'wait'.";
    return returnString;
  }
}
