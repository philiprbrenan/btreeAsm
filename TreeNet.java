//------------------------------------------------------------------------------
// A tree network connecting leaves along branch paths in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Test                                                      // A tree network connecting leaves a long branches in logarithmic time
 {int tnSize;                                                                   // The log of the number of 3-way junctions in the network
  int tnWidth;                                                                  // The data width of each junction

  Junction[]tnJunctions;                                                        // The junctions used to construct the tree net
  int tnStep;                                                                  // The tree net is clocked

//D1 Construction                                                               // Construct a tree net

  TreeNet(int Size, int Width)                                                  // Create the tree net
   {tnSize = powerTwo(Size)-1; tnWidth = Width;
    tnJunctions = new Junction[tnSize];                                         // The junctions used to construct the tree net
    for (int i = 0; i < tnSize; i++) tnJunctions[i] = new Junction(i);
   }

  public String toString()                                                      // Print the tree net
   {final StringJoiner s = new StringJoiner("\n", "", "\n");
    for (int i = 0; i < tnSize; i++)
     {final Junction j = tnJunctions[i];
      if (j == null) continue;

      final Integer p = j.jParent, l = j.jLeft, r = j.jRight;
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  d = "  ".repeat(j.jLevel)+"*"+"  ".repeat(8-j.jLevel);

      final Message mu = j.jMessageUp;
      final Message md = j.jMessageDown;
      final String  mU = mu == null ? "" : mu.toString();
      final String  mD = md == null ? "" : md.toString();

      final Event eu = j.jEventUp;
      final Event ed = j.jEventDown;

      final String eU = eu == null ? "" : eu.toString();
      final String eD = ed == null ? "" : ed.toString();

      s.add(String.format("%4d %s %s %s %s  %-8s  %s-%s  %s-%s |",
        i, d, P, L, R, j.jAddress.aAddress, eU, mU, eD, mD));
     }
    return ""+s;
   }

//D1 Junction                                                                   // A junction in a tree net connects two child junctions to a parent junction.

  class Junction                                                                // A junction in a tree net connects two child junctions to a parent junction.
   {final int     jNumber;                                                      // The number of the junction
    final int     jLevel;                                                       // The level of the junction,.  teh root is a t level zero, the nbext at level one etc.
    final Address jAddress;                                                     // The address of this junction in the tree net
    final Integer jParent;                                                      // The index of the parent of this junction
    final Integer jLeft;                                                        // The index of the left child junction
    final Integer jRight;                                                       // The index of the right child junction
    final Message jMessageUp;                                                   // Message waiting to be sent up the tree
    final Message jMessageDown;                                                 // Message waiting to be sent down the tree
    final Event   jEventUp;                                                     // Event controlling the transmission of a message up the tree
    final Event   jEventDown;                                                   // Event controlling the transmission of a message down the tree

    Junction(int Number)
     {jNumber  = Number;
      jLevel   = logTwo(prevPowerOfTwo(1+Number));                              // Level of junction
      jAddress = new Address(jNumber);
      jParent  = jTop() ? 0 : (Number-1) / 2;                                   // Set Parent
      jLeft    = Number * 2 + 1 < tnSize ? Number * 2 + 1 : 0;                  // Left child
      jRight   = Number * 2 + 2 < tnSize ? Number * 2 + 2 : 0;                  // Right child
      jMessageUp      = new Message();                                          // Message waiting to be sent up the tree
      jMessageDown    = new Message();                                          // Message waiting to be sent to the left down the tree
      jEventUp        = new Event();                                            // Event sending message up
      jEventDown      = new Event();                                            // Event sending message down left
     }

    boolean jTop() {return jNumber == 0;}                                       // The root is always at index zero
    boolean jIsLeftOfParent() {return jNumber % 2 == 1;}                        // This junction is left of its parent junction

    void jTransmit()                                                            // Transmit a message through the node
     {if (jLeft != null)                                                        // Examine left node for a message to be sent
       {final Junction l = tnJunctions[jLeft];
        if (!l.jEventUp.eFinished())                                            // Left wants to send us a message
         {final Address t = l.jMessageUp.mTarget;                               // Target of message from left
          if (l.jAddress.goUp(t))                                               // Left message wants to go up
           {if (!jAddress.goRight(t))                                           // Left message wants to go up then right
             {jMessageUp.copy(l.jMessageUp);                                    // Move left message to up
                jEventUp.eSetExecuting();
              l.jEventUp.eSetFinished ();
             }
           }
         }
       }
     }
   }

//D1 Event                                                                      // A network event

  class Event                                                                   // A network event
   {int eStarted;                                                               // Time at which the event started as determined by the step
    int eFinished;                                                              // Time at which the event finished as determined by the step - but we make sure that it is never equal to the start time
    boolean eFinished ()  {return eStarted < eFinished && eStarted != tnStep && eFinished != tnStep;} // Event has finished and it is not in the step that started it nor in the step that finished it
    Event eSetExecuting() {eStarted  = tnStep; return this;}                    // Execution will begin on the next step
    Event eSetFinished()  {eFinished = tnStep; return this;}                    // The event will finish on the next step
    Event()                                                                     // Create an event
     {eStarted  = -2;                                                           // Event created at the current step
      eFinished = -1;                                                           // Event not yet completed
     }
    public String toString()                                                    // Status of event
     {final StringBuilder s = new StringBuilder();
      return String.format("%d", eFinished() ? 1 : 0);
      //return String.format("e(%4d %4d  %d)", eStarted, eFinished, eFinished() ? 1 : 0);
     }
   }

//D1 Address
                                                                                // The address of a junction is its path from the root ot the tree net
  class Address                                                                 // Level of the junction in the tree net
   {int    aIndex;                                                              // The number of the message
    String aAddress;                                                            // Address

    Address(int Index)
     {aIndex = Index;
      final StringBuilder s = new StringBuilder();
      for(int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");     // Path from zero to this address
      aAddress = ""+s.reverse();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n", aIndex, pLevel(), aAddress);
     }

    void copy(Address Source)                                                   // Copy one address into another
     {aIndex = Source.aIndex; aAddress = Source.aAddress;
     }

    int pLevel() {return aAddress.length();}                                    // The level of this junction with the root of the tree net at level zero and the next level down plus one

    boolean equal(Address Target) {return Target.aAddress.equals(aAddress);}    // Have we arrived at the specified target junction

    boolean goLeft(Address Target)                                              // Should we go left from the current junctoin to find the specified target
     {return Target.pLevel() > pLevel() &&                                      // Level of target must be further down
             Target.aAddress.startsWith(aAddress) &&                            // Target prefix must match the current junction
             Target.aAddress.charAt(pLevel()) == '0';                           // Go left
     }

    boolean goRight(Address Target)                                             // Should we go right from the current junctoin to find the specified target
     {return Target.pLevel() > pLevel() &&                                      // Level of target must be further down
             Target.aAddress.startsWith(aAddress) &&                            // Target prefix must match the current junction
             Target.aAddress.charAt(pLevel()) == '1';                           // Go right
     }

    boolean goUp(Address Target)                                                // Go up if we have not arrived at the target and cannot go left or right
     {return !equal(Target) && !goLeft(Target) && !goRight(Target);
     }
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {Address mSource;                                                            // The address of the sending source leaf
    Address mTarget;                                                            // The address of the recieving target leaf
    String  mText;                                                              // The text of the message

    Message()
     {mSource = new Address(0); mTarget = new Address(0); mText = "";
     }

    void mSet(int Source, int Target, String Text)
     {mSource.copy(new Address(Source));
      mTarget.copy(new Address(Target));
      mText = Text;
     }

    void copy(Message Source)
     {mSource = Source.mSource; mTarget = Source.mTarget; mText = Source.mText;
     }

    public String toString()
     {final String s = mSource == null ? "" : mSource.aAddress != null ? mSource.aAddress : "";
      final String t = mTarget == null ? "" : mTarget.aAddress != null ? mTarget.aAddress : "";
      final String T = mText   == null ? "" : mText;

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
    final Address a3  = T.tnJunctions[ 3].jAddress;
    final Address a6  = T.tnJunctions[ 6].jAddress;
    final Address a13 = T.tnJunctions[13].jAddress;
    final Address a14 = T.tnJunctions[14].jAddress;

    ok( a6.goLeft (a13));
    ok(!a6.goRight(a13));
    ok(!a6.goLeft (a14));
    ok( a6.goRight(a14));
    ok( a6.goUp   (a3 ));
    ok(!a6.goUp   (a14));
    ok( a6.equal  (a6 ));
    ok(!a6.equal  (a13));
    ok( T.tnJunctions[ 5].jIsLeftOfParent());
    ok(!T.tnJunctions[ 6].jIsLeftOfParent());
   }

  static void test_transmit()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3, 8);
    final StringBuilder s = new StringBuilder();

    T.tnJunctions[5].jMessageUp.mSet(5, 3, "AAAA");
    T.tnJunctions[5].jEventUp  .eSetExecuting();

    for   (T.tnStep = 0; T.tnStep < 3; ++T.tnStep)
     {for (int i = 0; i < T.tnJunctions.length; i++)
       {T.tnJunctions[i].jTransmit();
       }
      s.append("Step "+T.tnStep+"\n"+T);
     }
    //stop(s);
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
