
/*  Original code by:
    
    David Eck
    Department of Mathematics and Computer Science
    Hobart and William Smith Colleges
    Geneva, NY 14456
    eck@hws.edu
    (http://math.hws.edu/eck/cs124/javanotes4/source/ConsoleApplet.java)
    
    
    Modified code by:
    
    Marcin Swieczkowski
    
*/



import java.awt.*;
import java.awt.event.*;

public class ConsoleApplet extends java.applet.Applet
                           implements Runnable, ActionListener {
                           
   protected String title = "Melkor's Tower";

   protected String getTitle() {
      return title;
   }
       
   protected  ConsolePanel console;  // console for use in program()
   
  public void program () {
    boolean addSpace = false;
    
    console.putln("Welcome to Melkor's Tower!");
    //console.putln("Type \"save\" to save your progress and \"load\" to restore it.");
    console.putln("Type \"help\" if you're new or need information about commands.\n");
    double currentTime = 0;
    Region currentRegion = Initialize.initializeRegion("region1", 0);
    Player p = new Player(0, currentRegion.queueCounter++, console);
    currentRegion.actorQueue.add(p);
    p.enter(currentRegion.roomList.get(0)); // starting room
    
    while (true) {
      assert(currentRegion.actorQueue.size() > 0);
      Actor a = currentRegion.actorQueue.remove();
      currentTime = a.scheduledTime;
      String msg = a.act();
      boolean forceDisplay = false;
      if (msg.indexOf("-f") >= 0) { // flag to force display of message
        msg = msg.replaceAll("-f", "");
        forceDisplay = true;
      }
      if (!p.currentRoom.newRegion.equals("")) { // entered new region 
        int newRegionRoom = p.currentRoom.newRegionRoom;
        currentRegion = Initialize.initializeRegion(p.currentRoom.newRegion, currentTime);
        p.enter(currentRegion.roomList.get(newRegionRoom));
      }
      if (a.type.equals("player")) {
        addSpace = true;
      }
      else if (!msg.equals("") && (a.currentRoom.actorList.contains(p) || forceDisplay)) {
        if (addSpace) {
          addSpace = false;
          console.putln();
        }
        console.putln(msg.trim());
      }
      currentRegion.actorQueue.add(a);
      a.queueCounter = currentRegion.queueCounter++;
    }
  }
   

   // The remainder of this file consists of implementation details that
   // you don't have to understand in order to write your own console applets.
   
   // private Button runButton;  // user presses this to run the program
   
   private Thread programThread = null;     // thread for running the program; the run()
                                            //    method calls program()
   private boolean programRunning = false;
   private boolean firstTime = true;  //    set to false the first time program is run
   
   public void run() {   // just run the program()
      programRunning = true;
      program();
      programRunning = false;
      stopProgram();
   }
   
   synchronized private void startProgram() {
      //runButton.setLabel("Abort Program");
      if (!firstTime) {
         console.clear();
         try { Thread.sleep(300); }  // some delay before restarting the program
         catch (InterruptedException e) { }
      }
      firstTime = false;
      programThread = new Thread(this);
      programThread.start();
   }
   
   synchronized private void stopProgram() {
      if (programRunning) {
         programThread.stop();
         try { programThread.join(1000); }
         catch (InterruptedException e) { }
      }
      console.clearBuffers();
      programThread = null;
      programRunning = false;
      //runButton.setLabel("Run Again");
      //runButton.requestFocus();
   }

   public void init() {
   
      setBackground(Color.black);
      resize(480,480);
   
      setLayout(new BorderLayout(2,2));
      console = new ConsolePanel();
      add("Center",console);
      
      Panel temp = new Panel();
      temp.setBackground(Color.white);
      Label lab = new Label(getTitle());
      temp.add(lab);
      lab.setForeground(new Color(128, 0, 0));
      add("North", temp);
      lab.setFont(new Font("Courier",Font.BOLD,14));
      
      //runButton = new Button("Run the Program");
      //temp = new Panel();
      //temp.setBackground(Color.white);
      //temp.add(runButton);
      //runButton.addActionListener(this);
      //add("South",temp);
      
      startProgram();
      
   }

   public Insets getInsets() {
      return new Insets(2,2,2,2);
   }
      
   public void stop() {
      if (programRunning) {
         stopProgram();
         console.putln();
         console.putln("*** PROGRAM HALTED");
      }
   }
   
   synchronized public void actionPerformed(ActionEvent evt) { 
            // Only possible action is a click on the button.
            // If program is running, stop it; otherwise, run it.
      if (programThread != null) {
         stopProgram();
         console.putln();
         console.putln("*** PROGRAM ABORTED BY USER");
      }
      else
         startProgram();
   }
   
}
