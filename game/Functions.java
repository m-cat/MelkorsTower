import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Functions {
  
  public static double pRand() {
    Random generator = new Random();
    return generator.nextDouble();
  }
  
  public static int iRand(int low, int high) {
    Random generator = new Random();
    return generator.nextInt(high-low+1)+low;
  }
  
  public static boolean arrayContains(String[] array, String s) {
    for (int i = 0; i < array.length; i ++) {
      if (array[i].equals(s)) {
        return true;
      }
    }
    return false;
  }
  public static boolean arrayContains(char[] array, char c) {
    for (int i = 0; i < array.length; i ++) {
      if (array[i] == c) {
        return true;
      }
    }
    return false;
  }
  
  public static String randomString(ArrayList<String> strings) {
    return strings.get(Functions.iRand(0,strings.size()-1));
  }
  
  // returns 'an' if name starts with vowel, 'a' otherwise
  public static String aOrAn(String name) {
    char[] vowels = {'a','e','i','o','u'};
    if (arrayContains(vowels,name.charAt(0)))
      return "an";
    else
      return "a";
  }
  
  public static String capitalize(String word) {
    char[] charArray = word.toCharArray();
    charArray[0] = Character.toUpperCase(charArray[0]);
    return new String(charArray);
  }
  
  public static String[] cleanSplit(String command) {
    List<String> args = new ArrayList<String>();
    // delete duplicate spaces
    while (command.indexOf("  ") >= 0) {
      command = command.replaceAll("  ", " ");
    }
    int j = 0, lastCut = 0;
    char lastQuoteType = ' ';
    for (int i = 0; i < command.length(); i ++) {
      char c = command.charAt(i);
      if (c == ' ' && j == 0) {
        if (command.charAt(lastCut) == command.charAt(i-1) && (command.charAt(lastCut) == '\'' || command.charAt(lastCut) == '\"'))
          args.add(command.substring(lastCut+1,i-1));
        else
          args.add(command.substring(lastCut,i));
        lastCut = i+1;
      }
      else if (c == '\"' || c == '\'') {
        if (j == 0 || lastQuoteType != c) {
          j ++;
        }
        else {
          j --;
        }
        lastQuoteType = c;
        if (j == 0)
          lastQuoteType = ' ';
      }
    }
    if (command.charAt(lastCut) == command.charAt(command.length()-1) && (command.charAt(lastCut) == '\'' || command.charAt(lastCut) == '\"'))
      args.add(command.substring(lastCut+1,command.length()-1));
    else
      args.add(command.substring(lastCut,command.length()));
    return args.toArray(new String[args.size()]);
  }
  
  public static String shortToLongDir(String dir) {
    String[] shortDir = {"n","ne","e","se","s","sw","w","nw","u","d"};
    String[] longDir = {"north","northeast","east","southeast","south","southwest","west","northwest","up","down"};
    Arrays.sort(shortDir); Arrays.sort(longDir);
    if (Arrays.binarySearch(longDir,dir) >= 0) {
      return dir;
    }
    int i = Arrays.binarySearch(shortDir,dir);
    assert (i >= 0);
    return longDir[i];
  }
  
  public static String longToShortDir(String dir) {
    String[] shortDir = {"n","ne","e","se","s","sw","w","nw","u","d"};
    String[] longDir = {"north","northeast","east","southeast","south","southwest","west","northwest","up","down"};
    Arrays.sort(shortDir); Arrays.sort(longDir);
    if (Arrays.binarySearch(shortDir,dir) >= 0) {
      return dir;
    }
    int i = Arrays.binarySearch(longDir,dir);
    if (i < 0)
      i = -(i + 1);
    if (i >= longDir.length || !longDir[i].startsWith(dir))
      return "";
    return shortDir[i];
  }
  
  public static String oppositeDirection(String dir) {
    String directions = "nsudewnenwsesw";
    String directions2 = "snduweswsenwne";
    return directions2.substring(directions.indexOf(dir), dir.length());
  }
  
}

