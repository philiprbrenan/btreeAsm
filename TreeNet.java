//------------------------------------------------------------------------------
// A tree network connecting leaves along branchs in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Test                                                      // A tree network connecting leaves a long branches in logarithmic time
 {final int       tttSize;                                                      // The log of the number of 3-way junctions in the network
  final int       tttWidth;                                                     // The data width of each junction
  final Junction[]tttJunctions;                                                 // The junctions used to construct the tree net
  boolean         tttPrintCompact = true;                                       // Print network trace in compact format if true
  int             tttStep;                                                      // The tree net is clocked

//D1 Construction                                                               // Construct a tree net

  TreeNet(int Size, int Width)                                                  // Create the tree net
   {tttSize = powerTwo(Size)-1; tttWidth = Width;
    tttJunctions = new Junction[tttSize];                                       // The junctions used to construct the tree net
    for (int i = 0; i < tttSize; i++) tttJunctions[i] = new Junction(i);
   }

  public String toString()                                                      // Print the tree net
   {final StringJoiner s = new StringJoiner("\n", "", "\n");
    s.add(String.format("Jnct  At step: %4d        Up  Left Right  Addr      Up_______________  Down____________  |", tttStep));
    for (int i = 0; i < tttSize; i++)
     {final Junction j = tttJunctions[i];
      if (j == null) continue;

      final Integer p = j.jjjParent, l = j.jjjLeft, r = j.jjjRight;
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  n = "  ".repeat(j.jjjLevel)+"*"+"  ".repeat(8-j.jjjLevel);

      final Message u = j.jjjMessageUp;
      final Message d = j.jjjMessageDown;
      final String  U = u == null ? " ".repeat(17) : u.toString(i);
      final String  D = d == null ? " ".repeat(17) : d.toString(i);

      if (!tttPrintCompact || u != null || d != null)
       {s.add(String.format("%4d  %s  %s  %s  %s  %-8s  %s  %s |",
          i, n, P, L, R, j.jjjAddress.aaaAddress, U, D));
       }
     }
    return ""+s;
   }

  void tttTransmit()                                                            // Transmit a message one step through the tree net
   {for(int i = 0; i < tttJunctions.length; i++) tttJunctions[i].jjjCopyUp();   // Copy a source message up one step so that it is closer to the target
    for(int i = 1; i < tttJunctions.length; i++) tttJunctions[i].jjjClearUp();  // Clear the source of an upward moving message - the root cannot be such a source
    tttJunctions[0].jjjMessageDown = tttJunctions[0].jjjMessageUp;              // Peri helion
    tttJunctions[0].jjjMessageUp   = null;                                      // Peri helion
    for(int i = 1; i < tttJunctions.length; i++) tttJunctions[i].jjjCopyDown(); // Copy a source message down one step so that it is closer to the target
    for(int i = 1; i < tttJunctions.length; i++) tttJunctions[i].jjjClearDown();// Clear the source of a downward moving message - the root cannot be such a source
   }

//D1 Junction                                                                   // A junction in a tree net connects two child junctions to a parent junction.

  class Junction                                                                // A junction in a tree net connects two child junctions to a parent junction.
   {final int     jjjNumber;                                                    // The number of the junction
    final int     jjjLevel;                                                     // The level of the junction,.  teh root is a t level zero, the nbext at level one etc.
    final Address jjjAddress;                                                   // The address of this junction in the tree net
    final Integer jjjParent;                                                    // The index of the parent of this junction
    final Integer jjjLeft;                                                      // The index of the left child junction
    final Integer jjjRight;                                                     // The index of the right child junction
    Message       jjjMessageUp;                                                 // Message waiting to be sent up through the tree net
    Message       jjjMessageDown;                                               // Message waiting to be sent down through the tree net
    Message       jjjMessageDown2;                                              // The message is forst moved here to prevent overrun

    Junction(int Number)
     {jjjNumber      = Number;
      jjjLevel       = logTwo(prevPowerOfTwo(1+Number));                        // Level of junction
      jjjAddress     = new Address(jjjNumber);
      jjjParent      = jjjTop() ? null : (Number-1) / 2;                        // Set Parent
      jjjLeft        = Number * 2 + 1 < tttSize ? Number * 2 + 1 : null;        // Left child
      jjjRight       = Number * 2 + 2 < tttSize ? Number * 2 + 2 : null;        // Right child
     }

    boolean jjjTop() {return jjjNumber == 0;}                                   // The root is always at index zero

    void jjjClearUp()                                                           // Clear source of messages sent up
     {final Junction p = tttJunctions[jjjParent];                               // Parent
      if (jjjMessageUp == p.jjjMessageUp) jjjMessageUp = null;                  // Same message in parent and child means we can remove the child message
     }

    void jjjCopyUp()                                                            // Transmit messages up through this junction
     {final Message  U =   jjjMessageUp;                                        // Message at this level if any
      if (tttStep % 3 == 0 && jjjLeft != null)                                  // Examine the left child for a message to be sent up
       {final Junction l = tttJunctions[jjjLeft];                               // Left child
        final Message  u = l.jjjMessageUp;                                      // A possible message from the left child
        if (U == null && u != null)                                             // Left might want to send a message up
         {jjjMessageUp = u;
         }
       }
      else if (tttStep % 3 == 1 && jjjRight != null)                            // Examine the right child for a message to be sent up
       {final Junction r = tttJunctions[jjjRight];                              // Right child
        final Message  u = r.jjjMessageUp;                                      // A possible message from the right child
        if (U == null && u != null)                                             // Right might want to send a message up
         {jjjMessageUp = u;
         }
       }
     }

    void jjjClearDown()                                                         // Clear source of messages sent down
     {if (jjjMessageDown2 != null)
       {jjjMessageDown  = jjjMessageDown2;                                      // Move message into main downline
        jjjMessageDown2 = null;                                                 // Move message from secondary downline wherw we cached it to stop overrun
        tttJunctions[jjjParent].jjjMessageDown = null;                          // Remove messsage from parent
       }
     }

    void jjjCopyDown()                                                          // Transmit messages down through this junction
     {final Junction p = tttJunctions[jjjParent];                               // Parent
      final Message  D = p.jjjMessageDown;                                      // A possible message from the parent
      final Message  d =   jjjMessageDown;                                      // Message at this level if any
      if (D != null && d == null)                                               // Parent wants to send us a message
       {if (jjjAddress.aaaDown(D.mmmTarget)) jjjMessageDown2 = D;               // Message sgould go down through this junction
       }
     }
   }

//D1 Address
                                                                                // The address of a junction is its path from the root ot the tree net
  class Address                                                                 // Level of the junction in the tree net
   {final int    aaaIndex;                                                      // The numeric representation of the address
    final String aaaAddress;                                                    // Address in branch path steering format

    Address(int Index)
     {aaaIndex = Index;
      final StringBuilder s = new StringBuilder();
      for(int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");     // Path from zero to this address
      aaaAddress = ""+s.reverse();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n", aaaIndex, aaaLevel(), aaaAddress);
     }

    int aaaLevel() {return aaaAddress.length();}                                // The level of this junction with the root of the tree net at level zero and the next level down plus one

    boolean aaaDown(Address Target)                                             // Is this address that can be ascended towards the target
     {return Target.aaaLevel() >= aaaLevel() &&                                 // Level of target must be here or further down
             Target.aaaAddress.startsWith(aaaAddress);                          // Target prefix must match that of the current junction
     }
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {final Address mmmSource;                                                    // The address of the sending source leaf
    final Address mmmTarget;                                                    // The address of the recieving target leaf
    final String  mmmText;                                                      // The text of the message

    Message(int Source, int Target, String Text)                                // Add a new message into the network
     {mmmSource = new Address(Source);
      mmmTarget = new Address(Target);
      mmmText   = Text;
      tttJunctions[Source].jjjMessageUp = this;                                 // The message starts at at the source junction
     }

    public String toString()                                                    // Print a message
     {final int    s = mmmSource.aaaIndex;
      final int    t = mmmTarget.aaaIndex;
      final String T = mmmText;

      final String r = String.format("%d->%d:%s", s, t, T);
      return String.format("%-16s", r);
     }

    String toString(int Position)                                               // Show the current position of the message in its path through the tree net
     {final int    s = mmmSource.aaaIndex;
      final int    t = mmmTarget.aaaIndex;
      final String T = mmmText;

      if (s == Position)
       {final String r = String.format("%d>>--%d", s, t);
        return String.format("%-8s %-8s", r, T);
       }

      if (t == Position)
       {final String r = String.format("%d-->>%d", s, t);
        return String.format("%-8s %-8s", r, T);
       }

      final String r = String.format("%d-%d-%d", s, Position, t);
      return String.format("%-8s %-8s", r, T);
     }
   }

//D1 Tests                                                                      // Test the double btree

  static void test_transmit()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3, 8);
    final StringBuilder s = new StringBuilder(); T.tttPrintCompact = false;

    T.new Message(5, 3, "AAAA");

    for   (T.tttStep = 0; T.tttStep < 4; ++T.tttStep)
     {s.append(T);
      T.tttTransmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0     3     4  0                                              |
   2    *                   0     5     6  1                                              |
   3      *                 1              00                                             |
   4      *                 1              01                                             |
   5      *                 2              10        5>>--3   AAAA                        |
   6      *                 2              11                                             |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0     3     4  0                                              |
   2    *                   0     5     6  1         5-2-3    AAAA                        |
   3      *                 1              00                                             |
   4      *                 1              01                                             |
   5      *                 2              10                                             |
   6      *                 2              11                                             |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0     3     4  0                            5-1-3    AAAA     |
   2    *                   0     5     6  1                                              |
   3      *                 1              00                                             |
   4      *                 1              01                                             |
   5      *                 2              10                                             |
   6      *                 2              11                                             |
Jnct  At step:    3        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0     3     4  0                                              |
   2    *                   0     5     6  1                                              |
   3      *                 1              00                           5-->>3   AAAA     |
   4      *                 1              01                                             |
   5      *                 2              10                                             |
   6      *                 2              11                                             |
""");
   }

  static void test_transmit2()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4, 8);
    final StringBuilder s = new StringBuilder();

    T.new Message(13, 7, "AAAA");
    T.new Message(14, 8, "BBBB");

    for   (T.tttStep = 0; T.tttStep < 14; ++T.tttStep)
     {s.append(T);
      T.tttTransmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
  13        *               6              110       13>>--7  AAAA                        |
  14        *               6              111       14>>--8  BBBB                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11        13-6-7   AAAA                        |
  14        *               6              111       14>>--8  BBBB                        |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-7   AAAA                        |
  14        *               6              111       14>>--8  BBBB                        |
Jnct  At step:    3        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-7   AAAA                        |
  14        *               6              111       14>>--8  BBBB                        |
Jnct  At step:    4        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-7   AAAA                        |
  14        *               6              111       14>>--8  BBBB                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            13-1-7   AAAA     |
   6      *                 2    13    14  11        14-6-8   BBBB                        |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           13-3-7   AAAA     |
   6      *                 2    13    14  11        14-6-8   BBBB                        |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11        14-6-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:    9        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:   10        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:   11        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            14-1-8   BBBB     |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:   12        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           14-3-8   BBBB     |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:   13        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          13-->>7  AAAA     |
   8        *               3              001                          14-->>8  BBBB     |
""");
   }

  static void test_transmit2Reverse()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4, 8);
    final StringBuilder s = new StringBuilder();

    T.new Message(7, 14, "AAAA");
    T.new Message(14, 7, "BBBB");

    for   (T.tttStep = 0; T.tttStep < 11; ++T.tttStep)
     {s.append(T);
      T.tttTransmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000       7>>--14  AAAA                        |
  14        *               6              111       14>>--7  BBBB                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
  14        *               6              111       14>>--7  BBBB                        |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   6      *                 2    13    14  11        14-6-7   BBBB                        |
Jnct  At step:    3        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   6      *                 2    13    14  11        14-6-7   BBBB                        |
Jnct  At step:    4        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   6      *                 2    13    14  11        14-6-7   BBBB                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   2    *                   0     5     6  1         14-2-7   BBBB                        |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   2    *                   0     5     6  1         14-2-7   BBBB                        |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-7   BBBB      7-2-14   AAAA     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            14-1-7   BBBB     |
   6      *                 2    13    14  11                           7-6-14   AAAA     |
Jnct  At step:    9        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           14-3-7   BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   10        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
""");
   }

  static void test_swap()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4, 8);
    final StringBuilder s = new StringBuilder();

    T.new Message(13, 14, "AAAA");
    T.new Message(14, 13, "BBBB");

    for   (T.tttStep = 0; T.tttStep < 14; ++T.tttStep)
     {s.append(T);
      T.tttTransmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
  13        *               6              110       13>>--14 AAAA                        |
  14        *               6              111       14>>--13 BBBB                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11        13-6-14  AAAA                        |
  14        *               6              111       14>>--13 BBBB                        |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-14  AAAA                        |
  14        *               6              111       14>>--13 BBBB                        |
Jnct  At step:    3        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-14  AAAA                        |
  14        *               6              111       14>>--13 BBBB                        |
Jnct  At step:    4        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-14  AAAA                        |
  14        *               6              111       14>>--13 BBBB                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1                            13-2-14  AAAA     |
   6      *                 2    13    14  11        14-6-13  BBBB                        |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11        14-6-13  BBBB      13-6-14  AAAA     |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11        14-6-13  BBBB                        |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-13  BBBB                        |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:    9        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-13  BBBB                        |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:   10        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-13  BBBB                        |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:   11        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1                            14-2-13  BBBB     |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:   12        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11                           14-6-13  BBBB     |
  14        *               6              111                          13-->>14 AAAA     |
Jnct  At step:   13        Up  Left Right  Addr      Up_______________  Down____________  |
  13        *               6              110                          14-->>13 BBBB     |
  14        *               6              111                          13-->>14 AAAA     |
""");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_transmit();
    test_transmit2();
    test_transmit2Reverse();
    test_swap();
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
