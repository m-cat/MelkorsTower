import java.util.ArrayList;

class Room {
  
  String description;
  int id;
  boolean safeZone = false; // if true, enemies cannot enter
  ArrayList<Actor> actorList = new ArrayList<Actor>();
  ArrayList<Exit> exitList = new ArrayList<Exit>();
  ArrayList<Item> itemList = new ArrayList<Item>();
  String newRegion = "";
  int newRegionRoom;
  
  public class Exit {
    
    public class Door {
      String doorType;
      boolean doorClosed=false;
      boolean doorLocked=false;
      
      Door(String type) {
        if (type.equals("door")) {
          doorType = "wooden door";
          doorClosed = true;
        }
        else if (type.equals("gate")) {
          doorType = "gate";
          doorClosed = true;
        }
        else if (type.equals("lockedgate")) {
          doorType = "gate";
          doorClosed = true;
          doorLocked = true;
        }
        else {
          doorType = type;
        }
      }
    }
    
    String direction;
    Room neighbor;
    boolean hasDoor=false;
    Door door=null;
    boolean exitHidden=false;
    
    Exit(String d, Room n, String doorType) {
      direction = d;
      neighbor = n;
      if (doorType.equals("none"))
        return;
      hasDoor = true;
      door = new Door(doorType);
      // add same door to corresponding exit of neighbor
      for (Exit e : neighbor.exitList) {
        if (e.neighbor == Room.this) {
          e.hasDoor = true;
          e.door = door;
        }
      }
    }
    
    public boolean doorClosed() {
      return hasDoor && door.doorClosed;
    }
    public boolean doorLocked() {
      return hasDoor && door.doorLocked;
    }
  }
  
  Room(String d) {
    description = d;
  }
  
  public void addExit(String d, Room n, String doorType) {
    exitList.add(new Exit(d, n, doorType));
  }
  
  public boolean doorClosed(int i) {
    return exitList.get(i).doorClosed();
  }
  
  public boolean doorLocked(int i) {
    return exitList.get(i).doorLocked();
  }
  
  public boolean hasDoor(int i) {
    return exitList.get(i).hasDoor;
  }
  
  public String doorType(int i) {
    return exitList.get(i).door.doorType;
  }
  
  public void openDoor(int i) {
    exitList.get(i).door.doorClosed = false;
  }
  
  public void closeDoor(int i) {
    exitList.get(i).door.doorClosed = true;
  }
  
  public void unlockDoor(int i) {
    exitList.get(i).door.doorLocked = false;
  }
  
  public void lockDoor(int i) {
    exitList.get(i).door.doorLocked = true;
  }
  
  public int getNumNeighbors() {
    return exitList.size();
  }
  
  public Room getNeighbor(int i) {
    return exitList.get(i).neighbor;
  }
  
  public String display() {
    String printString = "";
    printString += description+"\n";
    // print map
    printString += displayMap();
    // print actors
    if (actorList.size() > 1) {
      printString += "You see here ";
      for (Actor a : actorList) {
        if (!a.type.equals("player")) {
          if (a.status.equals("dead")) {
            printString += "a dead "+a.name+", ";
          }
          else {
            printString += Functions.aOrAn(a.descriptiveName)+" "+a.descriptiveName+", ";
          }
        }
      }
      printString = printString.substring(0,printString.length()-2)+".\n";
    }
    // print items
    if (itemList.size() > 0) {
      printString += "Items here: ";
      for (Item i : itemList) {
        printString += Functions.aOrAn(i.name)+" "+i.name+", ";
      }
      printString = printString.substring(0,printString.length()-2)+"\n";
    }
    // print exits
    printString += "Exits: ";
    String[] sortedExits = {"nw","w","sw","u","n","s","d","ne","e","se"};
    for (int i = 0; i < sortedExits.length; i ++) {
      if (containsExit(sortedExits[i])) {
        if (doorClosed(getExit(sortedExits[i])))
          printString += "["+Functions.shortToLongDir(sortedExits[i]) + "] ";
        else if (hasDoor(getExit(sortedExits[i])))
          printString += "("+Functions.shortToLongDir(sortedExits[i]) + ") ";
        else
          printString += Functions.shortToLongDir(sortedExits[i]) + " ";
      }
    }
    return printString;
  }
  
  public String displayMap() {
    String returnString = "";
    if (containsExit("nw",false))
      returnString += "\u2196 ";
    else if (containsExit("|nw",true))
      returnString += "+";
    else
      returnString += "  ";
    if (containsExit("n",false))
      returnString += "\u2191";
    else if (containsExit("n",true))
      returnString += "+";
    else
      returnString += " ";
    if (containsExit("ne",false))
      returnString += " \u2197\n";
    else if (containsExit("ne",true))
      returnString += "+\n";
    else
      returnString += "  \n";
    if (containsExit("w",false))
      returnString += "\u2190";
    else if (containsExit("w",true))
      returnString += "+";
    else
      returnString += " ";
    if (containsExit("u",false))
      returnString += "\u2191";
    else if (containsExit("u",true))
      returnString += "+";
    else if (containsExit("w"))
      returnString += "-";
    else
      returnString += " ";
    
    returnString += "O";
    
    if (containsExit("d",false))
      returnString += "\u2193";
    else if (containsExit("d",true))
      returnString += "+";
    else if (containsExit("e"))
      returnString += "-";
    else
      returnString += " ";
    if (containsExit("e",false))
      returnString += "\u2192\n";
    else if (containsExit("e",true))
      returnString += "+\n";
    else
      returnString += " \n";
    if (containsExit("sw",false))
      returnString += "\u2199 ";
    else if (containsExit("sw",true))
      returnString += "+";
    else
      returnString += "  ";
    if (containsExit("s",false))
      returnString += "\u2193";
    else if (containsExit("s",true))
      returnString += "+";
    else
      returnString += " ";
    if (containsExit("se",false))
      returnString += " \u2198\n";
    else if (containsExit("se",true))
      returnString += "+\n";
    else
      returnString += "  \n";
    return returnString;
  }
  
  public boolean containsExit(String dir, boolean doorClosed) {
    for (Exit e : exitList) {
      if (e.direction.equals(dir)) {
        return (e.doorClosed() == doorClosed);
      }
    }
    return false;
  }
  
  public boolean containsExit(String dir) {
    for (Exit e : exitList) {
      if (e.direction.equals(dir)) {
        return true;
      }
    }
    return false;
  }
  
  public int getExit(String dir) {
    for (int i = 0; i < exitList.size(); i ++) {
      if (exitList.get(i).direction.equals(dir))
        return i;
    }
    return -1;
  }
  
}