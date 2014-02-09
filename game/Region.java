import java.util.ArrayList;
import java.util.PriorityQueue;

public class Region {
  ArrayList<Room> roomList = new ArrayList<Room>();
  PriorityQueue<Actor> actorQueue = new PriorityQueue<Actor>();
  long queueCounter = 0;
}