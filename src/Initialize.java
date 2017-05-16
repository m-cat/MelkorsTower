import java.util.List;
import java.util.Arrays;

public class Initialize {
  
  public static Region initializeRegion(String regionName, double currentTime) {
    
    List<String> currentRegion;
    
    if (regionName.equals("region1")) {
      currentRegion = Arrays.asList("0~The adventure begins~n~1~none~dog~~~64~400",
"1~A peaceful forest path~e s~2 0~gate none~~~~64~368",
"2~A fork in the road~n s w~4 3 1~none none gate~~pitchfork~~96~368",
"3~A dead end?~n~2~none~~~~96~400",
"4~A peaceful forest path~e se s u~5 6 2 7~none none none none~~~safezone~96~334",
"5~A peaceful forest path~w n~4 10~none none~~~~128~334",
"6~A winding dirt path~nw ne~4 8~none none~~~~128~368",
"7~Up in the trees~d s w~4 13 15~none none none~~~~50~288",
"8~A house to the north~sw se n~6 9 12~none none door~wilddog~~~162~334",
"9~A dead end?~nw~8~none~rabbit~~~194~366",
"10~An old guardian~s n~5 11~none lockedgate~oldman~~~128~301",
"11~The path continues~s n~10 16~lockedgate none~~~~128~265",
"12~Inside an empty house~s~8~door~~~~162~302",
"13~Up in the trees~n w~7 14~none none~sparrow~~~50~327",
"14~Up in the trees~e n~13 15~none none~sparrow~~~12~327",
"15~Up in the trees~s e~14 7~none none~sparrow~~~12~288",
"16~region2 1~s~11~none~~~~128~182");
    }
    else {// if (regionName.equals("region2")) {
      currentRegion = Arrays.asList("0~region1 11~n~1~none~~~~264~412",
"1~Sign: \"Left for Hawkridge Castle, Right for Malarkey Forest\"~s nw ne~0 3 2~none none none~~~~264~316",
"2~Before the forest~sw e~1 5~none none~~~safezone~304~274",
"3~A small, dirty path~se w~1 4~none none~~~~221~273",
"4~A small, dirty path~e nw~3 6~none none~sickboy~~~173~273",
"5~Malarkey Forest~w n~2 12~none none~rabbit~~~356~274",
"6~On Hawkridge Road~se sw ne~4 8 7~none none none~robber~~~130~230",
"7~On Hawkridge Road~sw ne~6 10~none none~~~~167~193",
"8~On Hawkridge Road~ne w~6 9~none none~~~~92~268",
"9~Before the castle~e w~8 15~none none~~~~36~268",
"10~On Hawkridge Road~sw e~7 11~none none~~~~204~156",
"11~On Hawkridge Road~w ne~10 13~none none~~~~256~156",
"12~Malarkey Forest~s ne~5 14~none none~~~~356~223",
"13~On Hawkridge Road~sw e~11 16~none none~~~~293~119",
"14~Malarkey Forest~sw ne~12 17~none none~~~~391~188",
"15~~e~9~none~~~~-48~268",
"16~On Hawkridge Road~w e~13 18~none none~~~~342~119",
"17~Malarkey Forest~sw nw e~14 18 20~none none none~~~~426~153",
"18~On Hawkridge Road~w se ne~16 17 19~none none none~~~~390~119",
"19~On Hawkridge Road~sw~18~none~~~~426~83",
"20~Malarkey Forest~w~17~none~~~~473~153");
    }
    
    Region reg = new Region();
    reg.queueCounter = 0;
    for (String s : currentRegion) {
      String[] args = s.split("~");
      reg.roomList.add(new Room(args[1]));
    }
    for (String s : currentRegion) {
      String[] args = s.split("~");
      String[] exitList = args[2].split(" ");
      Room r = reg.roomList.get(Integer.parseInt(args[0]));
      for (int i = 0; i < exitList.length; i ++) {
        r.addExit(exitList[i], reg.roomList.get(Integer.parseInt(args[3].split(" ")[i])), args[4].split(" ")[i]);
      }
      // add actors to room
      String[] actors = args[5].split(" ");
      for (int i = 0; i < actors.length; i ++) {
        if (actors[i].equals(""))
          continue;
        Actor a = new Actor(currentTime, reg.queueCounter++, actors[i]);
        reg.actorQueue.add(a);
        a.currentRoom = r;
        r.actorList.add(a);
      }
      // add items to room
      String[] items = args[6].split(" ");
      for (int i = 0; i < items.length; i ++) {
        if (items[i].equals(""))
          continue;
        r.itemList.add(new Item(items[i]));
      }
      // add roomChange information
      if (args[1].startsWith("region")) {
        r.newRegion = args[1].split(" ")[0];
        r.newRegionRoom = Integer.parseInt(args[1].split(" ")[1]);
      }
      // safezone?
      if (!args[7].equals("")) {
        r.safeZone = true;
      }
    }
    return reg;
  }
}