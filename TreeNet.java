//------------------------------------------------------------------------------
// A tree network connecting leaves along branch paths in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Test                                                      // A tree network connecting leaves a long branches in logarithmic time
 {int treSize;                                                                  // The log of the number of 3-way jjjctions in the network
  int treWidth;                                                                 // The data width of each jjjction

  Junction[]treJunctions;                                                       // The jjjctions used to construct the tree net
  int treStep;                                                                  // The tree net is clocked

//D1 Construction                                                               // Construct a tree net

  TreeNet(int Size, int Width)                                                  // Create the tree net
   {treSize = powerTwo(Size)-1; treWidth = Width;
    treJunctions = new Junction[treSize];                                       // The jjjctions used to construct the tree net
    for (int i = 0; i < treSize; i++) treJunctions[i] = new Junction(i);
   }

  public String toString()                                                      // Print the tree net
   {final StringJoiner s = new StringJoiner("\n", "", "\n");
    for (int i = 0; i < treSize; i++)
     {final Junction j = treJunctions[i];
      if (j == null) continue;

      final Integer p = j.jjjParent, l = j.jjjLeft, r = j.jjjRight;
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  d = "  ".repeat(j.jjjLevel)+"*"+"  ".repeat(8-j.jjjLevel);

      final Message mu = j.jjjMessageUp;
      final Message md = j.jjjMessageDown;
      final String  mU = mu == null ? "" : mu.toString();
      final String  mD = md == null ? "" : md.toString();

      final Event eu = j.jjjEventUp;
      final Event ed = j.jjjEventDown;

      final String eU = eu == null ? "" : eu.toString();
      final String eD = ed == null ? "" : ed.toString();

      s.add(String.format("%4d %s %s %s %s  %-8s  %s-%s  %s-%s |",
        i, d, P, L, R, j.jjjAddress.addAddress, eU, mU, eD, mD));
     }
    return ""+s;
   }

//D1 Junction                                                                   // A jjjction in a tree net connects two child jjjctions to a parent jjjction.

  class Junction                                                                // A jjjction in a tree net connects two child jjjctions to a parent jjjction.
   {final int     jjjNumber;
   // The number of the jjjction
    final int     jjjLevel;                                                     // The level of the jjjction,.  teh root is a t level zero, the nbext at level one etc.
    final Address jjjAddress;                                                   // The address of this jjjction in the tree net
    final Integer jjjParent;                                                    // The index of the parent of this jjjction
    final Integer jjjLeft;                                                      // The index of the left child jjjction
    final Integer jjjRight;                                                     // The index of the right child jjjction
    final Message jjjMessageUp;                                                 // Message waiting to be sent up the tree
    final Message jjjMessageDown;                                               // Message waiting to be sent down the tree
    final Event   jjjEventUp;                                                   // Event controlling the transmission of a message up the tree
    final Event   jjjEventDown;                                                 // Event controlling the transmission of a message down the tree

    Junction(int Number)
     {jjjNumber      = Number;
      jjjLevel       = logTwo(prevPowerOfTwo(1+Number));                        // Level of jjjction
      jjjAddress     = new Address(jjjNumber);
      jjjParent      = jjjTop() ? 0 : (Number-1) / 2;                           // Set Parent
      jjjLeft        = Number * 2 + 1 < treSize ? Number * 2 + 1 : 0;           // Left child
      jjjRight       = Number * 2 + 2 < treSize ? Number * 2 + 2 : 0;           // Right child
      jjjMessageUp   = new Message();                                           // Message waiting to be sent up the tree
      jjjMessageDown = new Message();                                           // Message waiting to be sent to the left down the tree
      jjjEventUp     = new Event();                                             // Event sending message up
      jjjEventDown   = new Event();                                             // Event sending message down left
     }

    boolean jjjTop() {return jjjNumber == 0;}                                   // The root is always at index zero
    boolean jjjIsLeftOfParent() {return jjjNumber % 2 == 1;}                    // This jjjction is left of its parent jjjction

    void jjjTransmit()                                                          // Transmit a message through the node
     {if (jjjLeft != null)                                                      // Examine left node for a message to be sent
       {final Junction l = treJunctions[jjjLeft];                               // Left child
        if (!l.jjjEventUp.eveFinished())                                        // Left wants to send us a message
         {say("BBBB", l.jjjNumber);
          final Address t = l.jjjMessageUp.mssTarget;                           // Target of message from left
          if (l.jjjAddress.addGoUp(t))                                          // Left message wants to go up
           {if (jjjAddress.addGoRight(t))                                       // Left message wants to go up then right
             {final Junction r = treJunctions[jjjRight];                        // Right child
              if (r.jjjEventDown.eveFinished())                                 // Right down buffer is available
               {r.jjjMessageDown.mssCopy(l.jjjMessageUp);                       // Move left message to right down
                r.jjjEventDown.eveSetExecuting();                               // Show right as wanting to move down
                l.jjjEventUp.eveSetFinished ();                                 // Show left message as having been moved right
               }
             }
            else if (jjjEventDown.eveFinished())                                // Up buffer is available for left message
             {jjjMessageUp.mssCopy(l.jjjMessageUp);                             // Move left message up
                jjjEventUp.eveSetExecuting();                                   // Current message wants to move up
              l.jjjEventUp.eveSetFinished ();                                   // Show left message as having been moved up
             }
           }
         }
       }
     }
   }

//D1 Event                                                                      // A network event

  class Event                                                                   // A network event
   {int eveStarted;                                                             // Time at which the event started as determined by the step
    int eveFinished;                                                            // Time at which the event finished as determined by the step - but we make sure that it is never equal to the start time
    boolean eveFinished ()  {return eveStarted < eveFinished && eveStarted != treStep && eveFinished != treStep;} // Event has finished and it is not in the step that started it nor in the step that finished it
    Event eveSetExecuting() {eveStarted  = treStep; return this;}               // Execution will begin on the next step
    Event eveSetFinished()  {eveFinished = treStep; return this;}               // The event will finish on the next step
    Event()                                                                     // Create an event
     {eveStarted  = -2;                                                         // Event created at the current step
      eveFinished = -1;                                                         // Event not yet completed
     }
    public String toString()                                                    // Status of event
     {final StringBuilder s = new StringBuilder();
      return String.format("%d", eveFinished() ? 1 : 0);
      //return String.format("e(%4d %4d  %d)", eStarted, eFinished, eFinished() ? 1 : 0);
     }
   }

//D1 Address
                                                                                // The address of a jjjction is its path from the root ot the tree net
  class Address                                                                 // Level of the jjjction in the tree net
   {int    addIndex;                                                            // The number of the message
    String addAddress;                                                          // Address

    Address(int Index)
     {addIndex = Index;
      final StringBuilder s = new StringBuilder();
      for(int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");     // Path from zero to this address
      addAddress = ""+s.reverse();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n", addIndex, addLevel(), addAddress);
     }

    void addCopy(Address Source)                                                // Copy one address into another
     {addIndex = Source.addIndex; addAddress = Source.addAddress;
     }

    int addLevel() {return addAddress.length();}                                // The level of this jjjction with the root of the tree net at level zero and the next level down plus one

    boolean addEqual(Address Target) {return Target.addAddress.equals(addAddress);}   // Have we arrived at the specified target jjjction

    boolean addGoLeft(Address Target)                                           // Should we go left from the current jjjctoin to find the specified target
     {return Target.addLevel() > addLevel() &&                                  // Level of target must be further down
             Target.addAddress.startsWith(addAddress) &&                        // Target prefix must match the current jjjction
             Target.addAddress.charAt(addLevel()) == '0';                       // Go left
     }

    boolean addGoRight(Address Target)                                          // Should we go right from the current jjjctoin to find the specified target
     {return Target.addLevel() > addLevel() &&                                  // Level of target must be further down
             Target.addAddress.startsWith(addAddress) &&                        // Target prefix must match the current jjjction
             Target.addAddress.charAt(addLevel()) == '1';                       // Go right
     }

    boolean addGoUp(Address Target)                                             // Go up if we have not arrived at the target and cannot go left or right
     {return !addEqual(Target) && !addGoLeft(Target) && !addGoRight(Target);
     }
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {Address mssSource;                                                          // The address of the sending source leaf
    Address mssTarget;                                                          // The address of the recieving target leaf
    String  mssText;                                                            // The text of the message

    Message()
     {mssSource = new Address(0); mssTarget = new Address(0); mssText = "";
     }

    void mssSet(int Source, int Target, String Text)
     {mssSource.addCopy(new Address(Source));
      mssTarget.addCopy(new Address(Target));
      mssText = Text;
     }

    void mssCopy(Message Source)
     {mssSource = Source.mssSource; mssTarget = Source.mssTarget; mssText = Source.mssText;
     }

    public String toString()
     {final String s = mssSource == null ? "" : mssSource.addAddress != null ? mssSource.addAddress : "";
      final String t = mssTarget == null ? "" : mssTarget.addAddress != null ? mssTarget.addAddress : "";
      final String T = mssText   == null ? "" : mssText;

      return String.format("%-4s  %-4s  %-4s", s, t, T);
     }
   }

//D1 Tests                                                                      // Test the double btree

  static void test_setUp()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4, 8);
    //stop(T);
    ok(""+T, """
   0 *                    0    1    2            1-                  1-                 |
   1   *                  0    3    4  0         1-                  1-                 |
   2   *                  0    5    6  1         1-                  1-                 |
   3     *                1    7    8  00        1-                  1-                 |
   4     *                1    9   10  01        1-                  1-                 |
   5     *                2   11   12  10        1-                  1-                 |
   6     *                2   13   14  11        1-                  1-                 |
   7       *              3    0    0  000       1-                  1-                 |
   8       *              3    0    0  001       1-                  1-                 |
   9       *              4    0    0  010       1-                  1-                 |
  10       *              4    0    0  011       1-                  1-                 |
  11       *              5    0    0  100       1-                  1-                 |
  12       *              5    0    0  101       1-                  1-                 |
  13       *              6    0    0  110       1-                  1-                 |
  14       *              6    0    0  111       1-                  1-                 |
""");
    final Address a3  = T.treJunctions[ 3].jjjAddress;
    final Address a6  = T.treJunctions[ 6].jjjAddress;
    final Address a13 = T.treJunctions[13].jjjAddress;
    final Address a14 = T.treJunctions[14].jjjAddress;

    ok( a6.addGoLeft (a13));
    ok(!a6.addGoRight(a13));
    ok(!a6.addGoLeft (a14));
    ok( a6.addGoRight(a14));
    ok( a6.addGoUp   (a3 ));
    ok(!a6.addGoUp   (a14));
    ok( a6.addEqual  (a13));
    ok( T.treJunctions[ 5].jjjIsLeftOfParent());
    ok(!T.treJunctions[ 6].jjjIsLeftOfParent());
   }

  static void test_transmit()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3, 8);
    final StringBuilder s = new StringBuilder();

    T.treJunctions[5].jjjMessageUp.mssSet(5, 3, "AAAA");
    T.treJunctions[5].jjjEventUp  .eveSetExecuting();

//  for   (T.treStep = 0; T.treStep < 3; ++T.treStep)
    for   (T.treStep = 0; T.treStep < 6; ++T.treStep)
     {say("AAAA", T.treStep);
      for (int i = 0; i < T.treJunctions.length; i++)
       {T.treJunctions[i].jjjTransmit();
       }
      s.append("Step "+T.treStep+"\n"+T);
     }
    stop(s);
    ok(s, """
Step 0
   0 *                    0    1    2            1-                  1-                 |
   1   *                  0    3    4  0         1-                  1-                 |
   2   *                  0    5    6  1         0-10    00    AAAA  1-                 |
   3     *                1    0    0  00        1-                  1-                 |
   4     *                1    0    0  01        1-                  1-                 |
   5     *                2    0    0  10        0-10    00    AAAA  1-                 |
   6     *                2    0    0  11        1-                  1-                 |
Step 1
   0 *                    0    1    2            1-                  1-                 |
   1   *                  0    3    4  0         1-                  1-                 |
   2   *                  0    5    6  1         0-10    00    AAAA  1-                 |
   3     *                1    0    0  00        1-                  1-                 |
   4     *                1    0    0  01        1-                  1-                 |
   5     *                2    0    0  10        0-10    00    AAAA  1-                 |
   6     *                2    0    0  11        1-                  1-                 |
Step 2
   0 *                    0    1    2            1-                  1-                 |
   1   *                  0    3    4  0         1-                  1-                 |
   2   *                  0    5    6  1         0-10    00    AAAA  1-                 |
   3     *                1    0    0  00        1-                  1-                 |
   4     *                1    0    0  01        1-                  1-                 |
   5     *                2    0    0  10        1-10    00    AAAA  1-                 |
   6     *                2    0    0  11        1-                  1-                 |
""");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_setUp();
    test_transmit();
   }

  static void newTests()                                                        // Tests being worked on
   {oldTests();
    test_transmit();
   }

  public static void main(String[] args)                                        // Test if called as a program
   {try                                                                         // Get a traceback in a format clickable in Geany if something goes wrong to speed up debugging.
     {if (github_actions) oldTests(); else newTests();                          // Tests to run
      if (coverageAnalysis) coverageAnalysis(12);                               // Coverage analysis
      testSummary();                                                            // Summarize test results
      System.exit(testsFailed);
     }
    catch(Exception e)                                                          // Get a traceback in a format clickable in Geany
     {System.err.println(e);
      System.err.println(fullTraceBack(e));
      System.exit(1);
     }
   }
 }
