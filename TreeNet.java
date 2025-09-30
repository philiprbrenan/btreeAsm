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
  int tnClock;                                                                  // The tree net is clocked

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
   {int eStart;                                                                 // Time at which the event started as determined by the clock
    int eFinished;                                                              // Time at which the event finished as determined by the clock
    boolean eFinished () {return eStart < eFinished;}                           // Event has finished                                // The code with which the event finished
    boolean eExecuting() {return eStart > eFinished;}                           // Event is in progress
   }

//D1 Path
                                                                                // Path to a junction
  class Address                                                                 // Level of the junction in the tree net
   {final int    pIndex;                                                        // The number of the message
    final int    pLevel;                                                        // The number of the message
    final String pAddress;                                                      // Address

    Address(int Index)
     {pIndex = Index;
      final StringBuilder s = new StringBuilder();
      for(int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");     // Path from zero to this address
      pLevel = Index == 0 ? 0 : logTwo(prevPowerOfTwo(Index));                  // Address width
      pAddress = ""+s.reverse();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n", pIndex, pLevel, pAddress);
     }
   }

//D1 Junction                                                                   // A junction in a tree net connects two child junctions to a parent junction.

  class Junction                                                                // A junction in a tree net connects two child junctions to a parent junction.
   {final int     jNumber;                                                      // The number of the junction
    final int     jLevel;                                                       // The level of the junction,.  teh root is a t level zero, the nbext at level one etc.
    final Address jAddress;                                                     // The address of this junction in the tree net
    Integer       jParent;                                                      // The index of the parent of this junction
    Integer       jLeft;                                                        // The index of the left child junction
    Integer       jRight;                                                       // The index of the right child junction
    Message       jSendUp;                                                      // Message waiting to be sent up the tree
    Message       jSendLeft;                                                    // Message waiting to be sent to the left down the tree
    Message       jSendRight;                                                   // Message waiting to be sent to the right down the tree
    Event         jEventUp;                                                     // Event sending message up
    Event         jEventLeft;                                                   // Event sending message down left
    Event         jEventRight;                                                  // Event sending message down right

    Junction(int Number)
     {jNumber = Number;
      if (!jTop()) jParent = (Number-1) / 2;                                        // Set Parent
      if (Number * 2 + 1 < tnSize) jLeft  = Number * 2 + 1;                     // Left child
      if (Number * 2 + 2 < tnSize) jRight = Number * 2 + 2;                     // Right child
      jLevel = logTwo(prevPowerOfTwo(1+Number));                                  // Level of junction
      jAddress = new Address(jNumber);
     }

    boolean jTop() {return jNumber == 0;}                                       // the root is always at index zero
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {int mNumber;                                                                // The number of the message
    Address mSource;                                                            // The key of the sending source leaf
    Address mTarget;                                                            // The key of the recieving target leaf
    int[]   mText = new int[tnWidth];                                           // The text of the message
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
