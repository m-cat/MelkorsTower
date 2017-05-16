class Attack {
  String name;
  String attackMsgPlayer;
  String attackMsgOther;
  int damageBoost = 0;
  double damageMultiplier = 1;
  double attackDelay = 0;
  double critChance = 0;
  double poisonChance = 0;
  double stunChance = 0;
  
  public String attack(Actor attacker, Actor target) {
    boolean playerAttacking = attacker.type.equals("player");
    boolean playerDefending = target.type.equals("player");
    if (target.status.equals("dead")) {
      assert playerAttacking;
      return "You "+attackMsgPlayer+" the "+target.name+"'s corpse.";
    }
    String returnString = "";
    double damage = (attacker.baseDamage+damageBoost)*damageMultiplier;
    if (Functions.pRand() < critChance) {
      damage *= 1.5;
      if (playerAttacking)
        returnString += "Your attack crits!\n";
      else
        returnString += "The "+attacker.name+"\'s attack crits!\n";
    }
    damage -= target.baseDefense;
    if (damage < 0)
      damage = 0;
    target.hp -= (int)damage;
    if (playerAttacking)
      returnString += "You "+attackMsgPlayer+" the "+target.name+" for "+Integer.toString((int)damage)+" damage.\n";
    else if (playerDefending)
      returnString += "The "+attacker.name+" "+attackMsgOther+" you for "+Integer.toString((int)damage)+" damage.\n";
    if (target.hp <= 0) {
      returnString += target.die();
      returnString += attacker.kill(target);
    }
    else {
      target.hasBeenAttacked = true;
    }
    while (playerAttacking && returnString.charAt(returnString.length()-1) == '\n')
      returnString = returnString.substring(0,returnString.length()-1);
    return returnString;
  }
}

class AttackWeapon extends Attack {
  AttackWeapon(Item w) {
    assert w.type.equals("weapon");
    name = w.name;
    attackMsgPlayer = w.attackMsgPlayer;
    attackMsgOther = w.attackMsgOther;
    damageBoost = w.damageBoost;
    damageMultiplier = w.damageMultiplier;
    attackDelay = w.attackDelay;
    critChance = w.critChance;
    poisonChance = w.poisonChance;
    stunChance = w.stunChance;
  }
}

class AttackPunch extends Attack {
  AttackPunch() {
    name = "punch";
    attackMsgPlayer = "punch";
    attackMsgOther = "punches";
    damageBoost = 0;
  }
}

class AttackKick extends Attack {
  AttackKick() {
    name = "kick";
    attackMsgPlayer = "kick";
    attackMsgOther = "kicks";
    damageBoost = 1;
  }
}

class AttackGrowl extends Attack {
  AttackGrowl() {
    name = "growl";
    attackMsgPlayer = "growl";
    attackMsgOther = "growls";
    damageMultiplier = 0;
  }
}

class AttackBite extends Attack {
  AttackBite() {
    name = "bite";
    attackMsgPlayer = "bite";
    attackMsgOther = "bites";
    damageBoost = 1;
    critChance = .5;
  }
}

class AttackScratch extends Attack {
  AttackScratch() {
    name = "scratch";
    attackMsgPlayer = "scratch";
    attackMsgOther = "scratches";
    damageBoost = 1;
  }
}

class AttackPoisonspit extends Attack {
  AttackPoisonspit() {
    name = "poisonspit";
    attackMsgPlayer = "spit at";
    attackMsgOther = "spits at";
    poisonChance = .5;
  }
}