import java.util.*;
import java.awt.Color;

class Actor implements Comparable<Actor> {
  
  String name, descriptiveName;
  Color color = Color.black;
  String type = "default"; // default, player, npc, pet, enemy
  String status; // dead, sleeping, idle, roaming, defending, attacking, hunting, fleeing
  String defaultStatus;
  String walkType = "wanders";
  // AI-related attributes
  int hostilityLevel = 1; // 1-5
  double fleeThreshold = 0;
  double fleeChance = 0;
  boolean hasBeenAttacked = false;
  int seesPlayerCount = 0;
  String toldString = "";
  int[] vars = {0,0,0,0,0}; // extra variables that might be useful for AI
  // combat-related attributes
  int hp, hpMax;
  double regenRate, regenCounter = 0;
  int baseDamage, baseDefense;
  double turnDelay;
  double tempDelay = 0;
  boolean isPoisoned = false;
  int poisonStrength;
  int poisonDuration;
  int gold;
  int exp = 0;
  int expGain;
  List<Attack> attackList;
  List<Double> attackProbs;
  Item weapon;
  Item armor;
  ArrayList<Item> inventory = new ArrayList<Item>();
  // sayings
  ArrayList<String> meetPlayerSayings = new ArrayList<String>();
  ArrayList<String> idleSayings = new ArrayList<String>();
  ArrayList<String> attackedSayings = new ArrayList<String>();
  // misc attributes
  double scheduledTime;
  Room currentRoom;
  long queueCounter = 0;
 
  public int compareTo(Actor other){
    if (Math.abs(scheduledTime - other.scheduledTime) < .001) {
      if (queueCounter < other.queueCounter)
        return -1;
      return 1;
    }
    if (scheduledTime < other.scheduledTime)
      return -1;
    else
      return 1;
  }
  
  public String act() {
    String returnString = "";
    if (!status.equals("dead")) {
      returnString += handleStatus();
      seesPlayerCount = seesPlayer();
      if (seesPlayerCount == 1) {
        if (status.equals("fleeing")) {
          returnString += Functions.randomString(attackedSayings)+"\n";
        }
        else if (meetPlayerSayings.size() > 0)
          returnString += Functions.randomString(meetPlayerSayings)+"\n";
      }
      // attack AI
      if (seesPlayerCount > 0 && hp < fleeThreshold*hpMax) {
        status = "fleeing";
      }
      else if (hostilityLevel == 1) {
        if (hasBeenAttacked) {
          if (attackedSayings.size() > 0)
            returnString += Functions.randomString(attackedSayings)+"\n";
          hasBeenAttacked = false;
        }
      }
      else if (hostilityLevel == 2) {
        if (hasBeenAttacked) {
          if (seesPlayerCount > 0) {
            status = "defending";
          }
          else {
            status = defaultStatus;
            hasBeenAttacked = false;
          }
        }
      }
      if (type.equals("npc")) {
        returnString += npcInteractions();
      }
      if (status.equals("idle")) {
        if (idleSayings.size() > 0 && Functions.pRand() < 0.2) {
          returnString += Functions.randomString(idleSayings)+"\n";
        }
      }
      else if (status.equals("defending")) {
        returnString += chooseAttack().attack(this, getPlayer());
      }
      else if (status.equals("roaming")) {
        if (Functions.pRand() < 0.2) {
          if (existsSuitableExit()) {
            returnString += switchRoom(pickSuitableExit());
          }
        }
      }
      else if (status.equals("fleeing")) {
        if (seesPlayerCount > 0) {
          if (Functions.pRand() < fleeChance)
            returnString += flee();
          else
            returnString += "The "+name+" tries to flee!\n";
        }
        if (((float)hp)/hpMax >= fleeThreshold*hpMax) {
          status = defaultStatus;
        }
      }
    }
    scheduledTime += turnDelay + tempDelay;
    tempDelay = 0;
    return returnString;
  }
  
  // NPC interactions and AI
  public String npcInteractions() {
    String returnString = "";
    if (descriptiveName.equals("old man")) {
      if (vars[0] == 0 && !toldString.equals("")) {
        vars[0] = 1;
        returnString += "The man says \"Ah, so "+toldString+" has come to defeat Melkor..."
          + "\nWho is Melkor, you ask? Aren't you the prophesied one? Get going!\""
          + "\nThe man unlocks the gate.\n";
        toldString = "";
        currentRoom.unlockDoor(currentRoom.getExit("n"));
        currentRoom.openDoor(currentRoom.getExit("n"));
        meetPlayerSayings.clear();
        meetPlayerSayings.add("The man says \"You best get going.\"");
        idleSayings.clear();
        idleSayings.add("The man mutters something about a savior.\n");
        idleSayings.add("The man mutters something about a dark lord.\n");
      }
      if (vars[0] == 1 && !toldString.equals("")) {
        returnString += "The man rolls his eyes.\n";
        toldString = "";
      }
    }
    else if (descriptiveName.equals("boy with the flu")) {
      if (!toldString.equals("")) {
        returnString += "The boy sneezes.\n";
        toldString = "";
      }
    }
    return returnString;
  }
  
  public int seesPlayer() {
    for (Actor a : currentRoom.actorList) {
      if (a.type.equals("player"))
        return seesPlayerCount + 1;
    }
    return 0;
  }
  
  public Actor getPlayer() {
    for (Actor a : currentRoom.actorList) {
      if (a.type.equals("player"))
        return a;
    }
    return currentRoom.actorList.get(0); // shouldn't happen
  }
  
  public String handleStatus() {
    String returnString = "";
    if (!status.equals("dead")) {
      // handle regeneration
      regenCounter += regenRate;
      if (regenCounter > 0) {
        increaseHP((int)regenCounter);
        regenCounter -= (int)regenCounter;
      }
      // handle poison
      if (isPoisoned) {
        hp -= poisonStrength;
        poisonDuration --;
        if (poisonDuration == 0) {
          isPoisoned = false;
          poisonStrength = 0;
        }
        if (hp <= 0)
          returnString += die();
      }
    }
    return returnString;
  }
  
  public void increaseHP(int c) {
    hp += c;
    if (hp > hpMax)
      hp = hpMax;
  }
  
  public String flee() {
    if (existsSuitableExit()) {
      switchRoom(pickSuitableExit());
      return "-fThe "+name+" has fled!\n";
    }
    return "";
  }
  
  public Attack chooseAttack() {
    double r = Functions.pRand();
    double c = 0;
    for (int i = 0; i < attackList.size(); i ++) {
      c += attackProbs.get(i);
      if (r < c) {
        return attackList.get(i);
      }
    }
    return attackList.get(attackList.size()-1);
  }
  
  public String die() {
    status = "dead";
    return "The "+name+" has died!\n";
  }
  
  public String kill(Actor target) {
    String returnString = "";
    if (type.equals("player")) {
        if (target.gold != 0 || target.expGain != 0)
          returnString += "You have gained ";
        if (target.gold != 0) {
          gold += target.gold;
          returnString += Integer.toString(target.gold)+" gold";
          if (target.expGain != 0) {
            returnString += " and ";
          }
          target.gold = 0;
        }
        if (target.expGain != 0) {
          exp += target.expGain;
          returnString += Integer.toString(target.expGain)+" exp";
        }
        if (target.gold != 0 || target.expGain != 0)
          returnString += ".\n";
        if (target.inventory.size() != 0) {
          inventory.addAll(target.inventory);
          returnString += "You have looted a "+target.inventory.remove(0).name;
          if (target.inventory.size() > 0) {
            while (target.inventory.size() > 1) {
              returnString += ", a "+target.inventory.remove(0).name;
            }
            returnString += "and a "+target.inventory.remove(0).name;
          }
          returnString += ".\n";
        }
    }
    return returnString;
  }
  
  public void enter(Room r) {
    currentRoom = r;
    currentRoom.actorList.add(this);
  }
  
  public String switchRoom(Room r) {
    String returnString = "";
    if (seesPlayerCount != 0) {
      returnString += "-fThe "+descriptiveName+" "+walkType+" away.\n";
    }
    currentRoom.actorList.remove(this);
    currentRoom = r;
    currentRoom.actorList.add(this);
    seesPlayerCount = seesPlayer();
    if (seesPlayerCount != 0)
      returnString += Functions.capitalize(Functions.aOrAn(descriptiveName))+" "+descriptiveName+" "+walkType+" into view.\n";
    return returnString;
  }
  
  public boolean existsSuitableExit() {
    for (int i = 0; i < currentRoom.getNumNeighbors(); i ++) {
      if (!currentRoom.doorClosed(i) && !currentRoom.getNeighbor(i).safeZone) {
        return true;
      }
    }
    return false;
  }
  
  public Room pickSuitableExit() {
    assert existsSuitableExit();
    while (true) {
      int i = Functions.iRand(0,currentRoom.getNumNeighbors()-1);
      if (!currentRoom.doorClosed(i) && !currentRoom.getNeighbor(i).safeZone) {
        return currentRoom.getNeighbor(i);
      }
    }
  }
  
  Actor() {
    return;
  }
  
  Actor(double currentTime, long q, String kind) {
    queueCounter = q;
    
    if (kind.equals("dog")) {
      name = "dog";
      descriptiveName = "tame dog";
      color = new Color(165, 42, 42);
      type = "npc";
      defaultStatus = "idle";
      hostilityLevel = 1;
      fleeThreshold = 0.5;
      fleeChance = 0.3;
      hpMax = 8;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 1;
      turnDelay = 0.8;
      gold = 0;
      expGain = 2;
      inventory.add(new Item("biscuit"));
      attackedSayings.add("The dog whimpers.");
      idleSayings.add("The dog wags its tail.");
    }
    if (kind.equals("wilddog")) {
      name = "dog";
      descriptiveName = "wild dog";
      color = new Color(165, 42, 42);
      type = "enemy";
      defaultStatus = "roaming";
      hostilityLevel = 2;
      fleeThreshold = .3;
      fleeChance = 0.3;
      hpMax = 8;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 2;
      turnDelay = 0.8;
      gold = 0;
      expGain = 4;
      attackList = Arrays.asList(new AttackGrowl(), new AttackScratch(), new AttackBite());
      attackProbs = Arrays.asList(.2, .5, .3);
      inventory.add(new Item("biscuit"));
    }
    else if (kind.equals("rabbit")) {
      name = "rabbit";
      descriptiveName = "rabbit";
      color = new Color(165, 42, 42);
      type = "enemy";
      defaultStatus = "roaming";
      hostilityLevel = 1;
      fleeThreshold = 1.0;
      fleeChance = 0.3;
      hpMax = 4;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 1;
      turnDelay = .5;
      gold = 0;
      expGain = 2;
    }
    else if (kind.equals("sparrow")) {
      name = "sparrow";
      descriptiveName = "sparrow";
      color = new Color(165, 42, 42);
      type = "enemy";
      defaultStatus = "roaming";
      walkType = "flies";
      hostilityLevel = 1;
      fleeThreshold = 1.0;
      fleeChance = 0.3;
      hpMax = 4;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 1;
      turnDelay = .5;
      gold = 0;
      expGain = 2;
    }
    else if (kind.equals("oldman")) {
      name = "man";
      descriptiveName = "old man";
      color = new Color(165, 42, 42);
      type = "npc";
      defaultStatus = "idle";
      hostilityLevel = 1;
      hpMax = 999;
      hp = hpMax;
      regenRate = 5;
      baseDamage = 3;
      baseDefense = 50;
      turnDelay = 1.0;
      gold = 0;
      expGain = 0;
      inventory.add(new Item("goldencufflinks"));
      meetPlayerSayings.add("The man says \"Hello, traveler. What is your name?\"\n(Type 'tell man NAME' to answer.)");
      idleSayings.add("The man seems impatient.");
      idleSayings.add("The man picks his nose and scowls.");
      attackedSayings.add("The man looks at you sternly.");
    }
    else if (kind.equals("sickboy")) {
      name = "boy";
      descriptiveName = "boy with the flu";
      color = new Color(165, 42, 42);
      type = "npc";
      defaultStatus = "idle";
      hostilityLevel = 1;
      fleeThreshold = 1.0;
      fleeChance = 0.3;
      hpMax = 12;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 1;
      turnDelay = 1.0;
      gold = 6;
      expGain = 5;
      meetPlayerSayings.add("The boy smiles at you and sneezes.");
      idleSayings.add("The boy blows his nose.");
      idleSayings.add("The boy coughs.");
      attackedSayings.add("The boy says \"AAAHHHHHHHHHHHHHHHHH!\"");
    }
    else if (kind.equals("robber")) {
      name = "robber";
      descriptiveName = "highway robber";
      color = new Color(165, 42, 42);
      type = "enemy";
      defaultStatus = "roaming";
      hostilityLevel = 4;
      fleeThreshold = 0.2;
      hpMax = 12;
      hp = hpMax;
      regenRate = .2;
      baseDamage = 3;
      baseDefense = 1;
      turnDelay = 1.0;
      gold = 20;
      expGain = 10;
    }
    status = defaultStatus;
    scheduledTime = currentTime + turnDelay;
  }
}
