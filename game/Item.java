import java.util.ArrayList;

class Item {
  String name;
  String type; // weapon, armor, consumable, container
  boolean isEquipped = false;
  
  // consumable attributes
  int healAmount=0;
  
  // weapon attributes
  String attackMsgPlayer;
  String attackMsgOther;
  int damageBoost = 0;
  double damageMultiplier = 1.0;
  double attackDelay = 0;
  double critChance = 0;
  double poisonChance = 0;
  double stunChance = 0;
  
  // container attributes
  ArrayList<Item> containedItems = new ArrayList<Item>();
  
  Item (String kind) {
    if (kind.equals("biscuit")) {
      type = "consumable";
      name = "biscuit";
      healAmount = 10;
    }
    else if (kind.equals("sword")) {
      type = "weapon";
      name = "sword";
      attackMsgPlayer = "slash at";
      attackMsgOther = "slashes at";
      damageBoost = 2;
      damageMultiplier = 1.5;
      attackDelay = .2;
      critChance = .1;
    }
    else if (kind.equals("pitchfork")) {
      type = "weapon";
      name = "pitchfork";
      attackMsgPlayer = "stab";
      attackMsgOther = "stabs";
      damageBoost = 2;
      damageMultiplier = 1.1;
      attackDelay = .5;
      critChance = .2;
    }
    else if (kind.equals("goldencufflinks")) {
      type = "armor";
      name = "golden cufflinks";
    }
    else if (kind.startsWith("chest")) {
      type = "container";
      name = "chest";
      containedItems.add(new Item(kind.split("-")[1]));
    }
  }
}
