import java.util.*;
import java.awt.Color;

class Player extends Actor {
  
  int level;
  
  protected ConsolePanel console;
 
  Player(double currentTime, long q, ConsolePanel c) {
    scheduledTime = currentTime + turnDelay;
    queueCounter = q;
    name = "player";
    type = "player";
    status = "idle";
    hpMax = 12;
    hp = hpMax;
    regenRate = .2;
    baseDamage = 4;
    baseDefense = 2;
    turnDelay = 1.0;
    level = 1;
    attackList = Arrays.asList(new AttackPunch(), new AttackKick());
    
    console = c;
  }
  
  public String act() {
    super.handleStatus();
    String returnString = "";
    String status = "\nL:"+Integer.toString(level)+" HP:"+Integer.toString(hp)+"/"+Integer.toString(hpMax)+"\n> ";
    console.put(status);
    String command = console.getln();
    console.putln();
    while (processCommand(command) == -1) {
      console.put(status);
      command = console.getln();
      console.putln();
    }
    scheduledTime += turnDelay + tempDelay;
    tempDelay = 0;
    return returnString;
  }
  
  public void enter(Room r) {
    currentRoom = r;
    currentRoom.actorList.add(this);
    console.putln(r.display());
  }
    
  public String switchRoom(Room r) {
    currentRoom.actorList.remove(this);
    currentRoom = r;
    currentRoom.actorList.add(this);
    if (!currentRoom.newRegion.equals(""))
      return "";
    console.putln(r.display());
    return "";
  }
  
  public int take(int i) {
    Item it = currentRoom.itemList.remove(i);
    console.putln("You pick up the "+it.name+".");
    inventory.add(it);
    if ((it.type.equals("weapon") && weapon == null) || (it.type.equals("armor") && armor == null)) {
      equip(it);
    }
    return 0;
  }
  
  public int equip(Item i) {
    if (i.type.equals("weapon")) {
      if (weapon != null) {
        weapon.isEquipped = false;
      }
      weapon = i;
      i.isEquipped = true;
      attackList = Arrays.asList(new AttackWeapon(i), new AttackKick());
      console.putln("You equip the "+i.name+".");
      return 0;
    }
    else if (i.type.equals("armor")) {
      if (armor != null) {
        armor.isEquipped = false;
      }
      armor = i;
      i.isEquipped = true;
      console.putln("You equip the "+i.name+".");
      return 0;
    }
    else {
      console.putln("Could not equip the "+i.name);
      return -1;
    }
  }
  
  public int processCommand(String command) {
    command = command.trim();
    if (command.equals(""))
      return -1;
    String[] args = {"", "", ""};
    String[] args2 = Functions.cleanSplit(command);
    for (int i = 0; i < Math.min(3,args2.length); i ++) {
      args[i] = args2[i];
    }
    String c = args[0].toLowerCase();
    String[] commandsList = {"n","north","ne","northeast","e","east","se","southeast","s","south","sw","southwest","w","west","nw","northwest","up","u","down","d",
                             "go","move","walk","o","open","c","close","retrace","wait",
                             "attack","kick","tell",
                             "l","look","examine","inventory","take","loot","equip","unequip","drop","eat",
                             "help"};
    Arrays.sort(commandsList);
    if (Arrays.binarySearch(commandsList, c) < 0) {
      int insertion = -(Arrays.binarySearch(commandsList,c)+1);
      if (insertion >= commandsList.length || !commandsList[insertion].startsWith(c)) {
        console.putln("Invalid command.");
        return -1;
      }
      if (insertion+1 < commandsList.length && commandsList[insertion+1].startsWith(c)) {
        console.put("Do you mean "+commandsList[insertion]);
        insertion++;
        while(insertion+1 < commandsList.length && commandsList[insertion+1].startsWith(c)) {
          console.put(", "+commandsList[insertion]);
          insertion++;
        }
        console.putln(" or "+commandsList[insertion]+"?");
        return -1;
      }
      c = commandsList[insertion];
    }
    String dir = processDirection(c);
    if (!dir.equals("")) {
      return commandSwitchRoom(dir); // directional movement
    }
    else if (c.equals("go") || c.equals("move") || c.equals("walk")) {
      return commandSwitchRoom(processDirection(args[1]));
    }
    else if (c.equals("open") || c.equals("o")) {
      return commandOpen(processDirection(args[1]), true);
    }
    else if (c.equals("close") || c.equals("c")) {
      return commandOpen(processDirection(args[1]), false);
    }
    else if (c.equals("attack")) {
      return commandAttack(processActorName(args[1]),0);
    }
    else if (c.equals("kick")) {
      return commandAttack(processActorName(args[1]),1);
    }
    else if (c.equals("tell")) {
      return commandTell(processActorName(args[1]),args[2]);
    }
    else if (c.equals("retrace")) {
      return commandRetrace();
    }
    else if (c.equals("wait")) {
      return commandWait();
    }
    else if (c.equals("l") || c.equals("look")) {
      console.putln(currentRoom.display());
      return -1;
    }
    else if (c.equals("examine")) {
      commandExam(processActorItemName(args[1]));
      return -1;
    }
    else if (c.equals("inventory")) {
      inventoryDisplay();
      return -1;
    }
    else if (c.equals("take")) {
      return commandTake();
    }
    else if (c.equals("loot")) {
      return commandLoot();
    }
    else if (c.equals("equip")) {
      return commandEquip();
    }
    else if (c.equals("unequip")) {
      return commandUnequip();
    }
    else if (c.equals("drop")) {
      return commandDrop();
    }
    else if (c.equals("eat")) {
      return commandEat();
    }
    else if (c.equals("help")) {
      helpDisplay();
      return -1;
    }
    
    return -1;
  }
  
  public String processDirection(String dir) {
    if (dir.equals(""))
      return "";
    return Functions.longToShortDir(dir.toLowerCase());
  }
  
  public String processActorName(String name) {
    if (name.equals(""))
      return "";
    for (Actor a : currentRoom.actorList) {
      if (!a.type.equals("player") && (a.name.startsWith(name.toLowerCase()) || a.descriptiveName.startsWith(name.toLowerCase()))) {
        return a.name;
      }
    }
    return "Could not find \""+name+"\".";
  }
  
  public String processActorItemName(String name) {
    if (name.equals("")) {
      return ""; // TODO
    }
    return "";
  }
  
  // open = true to open door, false to close it
  public int commandOpen(String direction, boolean open) {
    if (direction.equals("")) {
      if (open)
        console.putln("Open in what direction?");
      else
        console.putln("Close in what direction?");
      return -1;
    }
    for (int i = 0; i < currentRoom.exitList.size(); i ++) {
      if (currentRoom.exitList.get(i).direction.equals(direction)) {
        if (!currentRoom.hasDoor(i)) {
          if (open)
            console.putln("There is nothing to open there.");
          else
            console.putln("There is nothing to close there.");
          return -1;
        }
        if (open && !currentRoom.doorClosed(i)) {
          console.putln("That "+currentRoom.doorType(i)+" is already open.");
          return -1;
        }
        else if (!open && currentRoom.doorClosed(i)) {
          console.putln("That "+currentRoom.doorType(i)+" is already closed.");
          return -1;
        }
        if (open) {
          if (currentRoom.doorLocked(i)) {
            console.putln("You could not open the locked "+currentRoom.doorType(i)+".");
            return 0; // trying to open a locked door takes a turn
          }
          console.putln("You open the "+currentRoom.doorType(i)+".");
          currentRoom.openDoor(i);
        }
        else {
          console.putln("You close the "+currentRoom.doorType(i)+".");
          currentRoom.closeDoor(i);
        }
        return 0;
      }
    }
    console.putln("There is no exit there.");
    return -1;
  }
  
  public int commandSwitchRoom(String direction) {
    if (direction.equals("")) {
      console.putln("Go where?");
      return -1;
    }
    for (int i = 0; i < currentRoom.exitList.size(); i ++) {
      if (currentRoom.exitList.get(i).direction.equals(direction)) {
        if (currentRoom.doorClosed(i)) {
          console.putln("There is a "+currentRoom.doorType(i)+" in the way.");
          return -1;
        }
        switchRoom(currentRoom.exitList.get(i).neighbor);
        return 0;
      }
    }
    console.putln("Could not go that way.");
    return -1;
  }
  
  public int commandRetrace() {
    // TODO
    return 0;
  }
  
  public int commandWait() {
    console.putln("You wait.");
    return 0;
  }
  
  public void commandExam(String name) {
    if (name.equals("")) {
      console.putln("Examine what?");
      return;
    }
  }
  
  // attack
  public int commandAttack(String target, int attack) {
    if (target.startsWith("Could")) {
      console.putln(target);
      return -1;
    }
    else if (!target.equals("")) {
      for (Actor a : currentRoom.actorList) {
        if (a.name.equals(target)) {
          console.putln(attackList.get(attack).attack(this,a));
          return 0;
        }
      }
    }
    assert (currentRoom.actorList.size() > 0);
    if (currentRoom.actorList.size() == 1) {
      console.putln("There's nothing here to attack.");
      return -1;
    }
    else if (currentRoom.actorList.size() == 2) {
      Actor a = currentRoom.actorList.get(0);
      if (a.type.equals("player"))
        a = currentRoom.actorList.get(1);
      console.putln(attackList.get(attack).attack(this,a));
      return 0;
    }
    ArrayList<Actor> livingTargets = new ArrayList<Actor>();
    for (Actor a : currentRoom.actorList) {
      if (!a.type.equals("player") && !a.status.equals("dead")) {
        livingTargets.add(a);
      }
    }
    if (livingTargets.size() > 1) {
      console.putln("Attack what?");
      return -1;
    }
    console.put(attackList.get(attack).attack(this, livingTargets.get(0)));
    return 0;
  }
  
  // tell
  public int commandTell(String target, String msg) {
    if (target.startsWith("Could")) {
      console.putln(target);
      return -1;
    }
    if (target.equals("")) {
      console.putln("Tell whom?");
      return -1;
    }
    else if (msg.equals("")) {
      console.putln("Tell the "+target+" what?");
      return -1;
    }
    for (Actor a : currentRoom.actorList) {
      if (a.name.equals(target)) {
        console.putln("You tell the "+target+" \""+msg+"\".");
        a.toldString = msg;
        return 0;
      }
    }
    return 0;
  }
  
  // display inventory
  public void inventoryDisplay() {
    console.putln("Inventory:");
    for (Item i : inventory) {
      console.put(" - "+i.name);
      if (i.isEquipped)
        console.put(" (e)");
      console.putln();
    }
    console.putln("\nGold: "+Integer.toString(gold));
  }
  
  public int commandTake() {
    if (currentRoom.itemList.size() == 0) {
      console.putln("There's nothing here to take.");
      return -1;
    }
    else if (currentRoom.itemList.size() > 1) {
      console.putln("Take what?");
      return -1;
    }
    take(0);
    return 0;
  }
  
  public int commandLoot() {
    // TODO
    return 0;
  }
  
  public int commandEquip() {
    // TODO
    return 0;
  }
  
  public int commandUnequip() {
    // TODO
    return 0;
  }
  
  public int commandDrop() {
    // TODO
    return 0;
  }
  
  public int commandEat() {
    // TODO
    return 0;
  }
  
  public void helpDisplay() {
    console.putln(Help.helpDisplay());
  }
}