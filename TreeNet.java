//------------------------------------------------------------------------------
// A tree network connecting leaves a long branches in logarithmic time
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
      s.add(String.format("%4d %s %s %s %s  %-8s |",
        i, d, P, L, R, j.jAddress.pAddress));
     }
    return ""+s;
   }

//D1 Event                                                                      // A network event

  class Event                                                                   // A network event
   {int eStarted;                                                               // Time at which the event started as determined by the step
    int eFinished;                                                              // Time at which the event finished as determined by the step - but we make sure that it is never equal to the start time
    boolean eFinished () {return eStarted < eFinished && eStarted != tnStep && eFinished != tnStep;} // Event has finished and it is not in the step that started it nor in the step that finished it
    void eSetExecuting()  {eStarted  = tnStep;}                                 // Execution will begin on the next step
    void eSetFinished()   {eFinished = tnStep;}                                 // The event will finish on the next step
   }

//D1 Path
                                                                                // Path to a junction
  class Address                                                                 // Level of the junction in the tree net
   {int    pIndex;                                                              // The number of the message
    String pAddress;                                                            // Address

    Address(int Index)
     {pIndex = Index;
      final StringBuilder s = new StringBuilder();
      for(int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");     // Path from zero to this address
      pAddress = ""+s.reverse();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n", pIndex, pLevel(), pAddress);
     }

    int pLevel() {return pAddress.length();}                                    // The level of this junction with the root of the tree net at level zero and the next level down plus one

    boolean equal(Address Target) {return Target.pAddress.equals(pAddress);}    // Have we arrived at the specified target junction

    boolean goLeft(Address Target)                                              // Should we go left from the current junctoin to find the specified target
     {return Target.pLevel() > pLevel() &&                                      // Level of target must be further down
             Target.pAddress.startsWith(pAddress) &&                            // Target prefix must match the current junction
             Target.pAddress.charAt(pLevel()) == '0';                           // Go left
     }

    boolean goRight(Address Target)                                             // Should we go right from the current junctoin to find the specified target
     {return Target.pLevel() > pLevel() &&                                      // Level of target must be further down
             Target.pAddress.startsWith(pAddress) &&                            // Target prefix must match the current junction
             Target.pAddress.charAt(pLevel()) == '1';                           // Go right
     }

    boolean goUp(Address Target)                                                // Go up if we have not arrived at the target and cannot go left or right
     {return !equal(Target) && !goLeft(Target) && !goRight(Target);
     }
   }

//D1 Junction                                                                   // A junction in a tree net connects two child junctions to a parent junction.

  class Junction                                                                // A junction in a tree net connects two child junctions to a parent junction.
   {int     jNumber;                                                            // The number of the junction
    int     jLevel;                                                             // The level of the junction,.  teh root is a t level zero, the nbext at level one etc.
    Address jAddress;                                                           // The address of this junction in the tree net
    Integer jParent;                                                            // The index of the parent of this junction
    Integer jLeft;                                                              // The index of the left child junction
    Integer jRight;                                                             // The index of the right child junction
    Message jMessageArrived;                                                    // Message waiting here to be consumed
    Message jMessageUp;                                                         // Message waiting to be sent up the tree
    Message jMessageLeft;                                                       // Message waiting to be sent to the left down the tree
    Message jMessageRight;                                                      // Message waiting to be sent to the right down the tree
    Event   jEventArrived;                                                      // Event showing whether the message addressed to this junction has been consumed
    Event   jEventUp;                                                           // Event sending message up
    Event   jEventLeft;                                                         // Event sending message down left
    Event   jEventRight;                                                        // Event sending message down right

    Junction(int Number)
     {jNumber = Number;
      if (!jTop()) jParent = (Number-1) / 2;                                    // Set Parent
      if (Number * 2 + 1 < tnSize) jLeft  = Number * 2 + 1;                     // Left child
      if (Number * 2 + 2 < tnSize) jRight = Number * 2 + 2;                     // Right child
      jLevel = logTwo(prevPowerOfTwo(1+Number));                                // Level of junction
      jAddress = new Address(jNumber);
     }

    boolean jTop() {return jNumber == 0;}                                       // The root is always at index zero
    boolean jIsLeftOfParent() {return jNumber % 2 == 1;}                        // This junction is left of its parent junction

    void Transmit()                                                             // Transmit a message through the node
     {if (jLeft != null)                                                        // Examine left node for a message to be sent
       {final Junction l = tnJunctions[jLeft];
        if (!l.jEventUp.eFinished())                                            // Left wants to send us a message
         {final Address t = l.jMessageUp.mTarget;                               // Target of message from left
          if (jAddress.goUp(t) && jEventUp.eFinished())                         // Left message wants to go up and the up buffer is not busy
           {jMessageUp.copy(l.jMessageUp);                                      // Move left message to up
            l.jEventUp.eSetFinished();
           }
         }
       }
     }
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {Address mSource;                                                            // The address of the sending source leaf
    Address mTarget;                                                            // The address of the recieving target leaf
    int[]   mText = new int[tnWidth];                                           // The text of the message

    Message(Address Source, Address Target, int...Text)
     {mSource = Source; mTarget = Target;
      final int M = min(mText.length, Text.length);
      for (int i = 0; i < M; i++) mText[i] = Text[i];
     }

    void copy(Message Source)                                                   // Make a copy of the current message
     {mSource = Source.mSource;
      mTarget = Source.mTarget;
      final int M = min(Source.mText.length, mText.length);
      for (int i = 0; i < M; i++) mText[i] = Source.mText[i];
     }
   }

//D1 Tests                                                                      // Test the double btree

  static void test_setUp()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4, 8);
    //stop(T);
    ok(""+T, """
   0 *                         1    2           |
   1   *                  0    3    4  0        |
   2   *                  0    5    6  1        |
   3     *                1    7    8  00       |
   4     *                1    9   10  01       |
   5     *                2   11   12  10       |
   6     *                2   13   14  11       |
   7       *              3            000      |
   8       *              3            001      |
   9       *              4            010      |
  10       *              4            011      |
  11       *              5            100      |
  12       *              5            101      |
  13       *              6            110      |
  14       *              6            111      |
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

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_setUp();
   }

  static void newTests()                                                        // Tests being worked on
   {oldTests();
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
