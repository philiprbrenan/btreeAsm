//------------------------------------------------------------------------------
// A tree network that connects leaf pairs via branches in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Test                                                      // A tree network that connects leaf pairs via branches in logarithmic time, allowing us to compose a large chip as a networked collection of smaller chips.
 {final int       ttSize;                                                       // The number of 3-way junctions in the network
  final Junction[]ttJunctions;                                                  // The junctions used to construct the tree network
  boolean         ttPrintCompact = true;                                        // Print network trace in compact format if true
  int             ttStep;                                                       // The tree network is stepped

//D1 Construction                                                               // Construct a tree network

  TreeNet(int Size)                                                             // Create the tree network.  The number of leaves will be 2**(Size-1)
   {ttSize = powerTwo(Size) - 1;
    ttJunctions = new Junction[ttSize];                                         // The junctions used to construct the tree network
    for (int i = 0; i < ttSize; i++) ttJunctions[i] = new Junction(i);
   }

  public String toString()                                                      // Print the tree network
   {final StringJoiner s = new StringJoiner("\n", "", "\n");
    s.add(String.format("Jnct  At step: %4d        Up  Left Right  Addr      Up_______________  Down____________  |", ttStep));
    for (int i = 0; i < ttSize; i++)                                            // Each junction in the tree network
     {final Junction j = ttJunctions[i];
      if (j == null) continue;

      final Integer p = j.jjParent, l = j.jjLeft, r = j.jjRight;                // Relationship with other junctions
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  n = "  ".repeat(j.jjLevel)+"*"+"  ".repeat(8-j.jjLevel);

      final Message u = j.jjMessageUp;                                          // Message detail
      final Message d = j.jjMessageDown;
      final String  U = u == null ? " ".repeat(17) : u.toString(i);
      final String  D = d == null ? " ".repeat(17) : d.toString(i);

      if (!ttPrintCompact || u != null || d != null)                            // Print if there has been activity on this junction or all junctions are being printed
       {s.add(String.format("%4d  %s  %s  %s  %s  %-8s  %s  %s |",
          i, n, P, L, R, j.jjAddress.aaAddress, U, D));
       }
     }
    return ""+s;                                                                // Printed state of tree network at this point in time
   }

  void ttTransmit()                                                             // Transmit each message one step through the tree network
   {for (int i = 0; i < ttJunctions.length; i++) ttJunctions[i].jjCopyUp();     // Copy a source message up one step so that it is closer to the target
    for (int i = 1; i < ttJunctions.length; i++) ttJunctions[i].jjClearUp();    // Clear the source of an upward-moving message - the root cannot be such a source
    ttJunctions[0].jjMessageDown = ttJunctions[0].jjMessageUp;                  // Transfer the message from the upward seeking side of the tree to the downward seeking side
    ttJunctions[0].jjMessageUp   = null;                                        // Remove message from upward seeking side now that it has been transfered to the downward seeking side of the tree network
    for(int i = 1; i < ttJunctions.length; i++) ttJunctions[i].jjCopyDown();    // Copy a source message down one step so that it is closer to the target
    for(int i = 1; i < ttJunctions.length; i++) ttJunctions[i].jjClearDown();   // Clear the source of a downward-moving message - the root cannot be such a source
   }

  Message ttGetMessage(int Leaf)                                                // Get any message that has arrived at the specified leaf
   {final Message m = ttJunctions[Leaf].jjMessageDown;                          // Get any message
    ttJunctions[Leaf].jjMessageDown = null;                                     // Remove the message
    return m;                                                                   // Return ther message
   }

  boolean ttPutMessage(int Source, int Target, String Text)                     // Add a new message at the indicated leaf if possible and return true else false
   {if (ttJunctions[Source].jjMessageUp != null) return false;                  // There is a message on the leaf already
    ttJunctions[Source].jjMessageUp = new Message(Source, Target, Text);        // Add the message
    return true;                                                                // Success
   }

//D1 Junction                                                                   // A junction in a tree network connects two child junctions to a parent junction.

  private class Junction                                                        // A junction in a tree network connects two child junctions to a parent junction.
   {final int     jjNumber;                                                     // The number of the junction
    final int     jjLevel;                                                      // The level of the junction.  The root is at level zero, the next at level one, etc.
    final Address jjAddress;                                                    // The address of this junction in the tree network
    final Integer jjParent;                                                     // The index of the parent of this junction
    final Integer jjLeft;                                                       // The index of the left child junction
    final Integer jjRight;                                                      // The index of the right child junction
    Message       jjMessageUp;                                                  // Message waiting to be sent up through the tree network
    Message       jjMessageDown;                                                // Message waiting to be sent down through the tree network
    Message       jjMessageDownPending;                                         // A downward seeking message is cached here during simulation to prevent overruns

    Junction(int Number)
     {jjNumber  = Number;
      jjLevel   = logTwo(prevPowerOfTwo(1+Number));                             // Level of junction
      jjAddress = new Address(jjNumber);
      jjParent  = jjTop() ? null : (Number-1) / 2;                              // Set parent
      jjLeft    = Number * 2 + 1 < ttSize ? Number * 2 + 1 : null;              // Left child
      jjRight   = Number * 2 + 2 < ttSize ? Number * 2 + 2 : null;              // Right child
     }

    boolean jjTop() {return jjNumber == 0;}                                     // The root is always at index zero

    private void jjClearUp()                                                    // Clear source of messages sent up through this junction
     {final Junction p = ttJunctions[jjParent];                                 // Parent
      if (jjMessageUp == p.jjMessageUp) jjMessageUp = null;                     // Same message in parent and child means we can remove the child message
     }

    void jjCopyUp()                                                             // Transmit messages up through this junction
     {final Message  U =   jjMessageUp;                                         // Message at this level if any
      if (ttStep % 2 == 0 && jjLeft != null)                                    // Examine the left child for a message to be sent up. To avoid always giving messages on the left hand side priority over the right hand side we alternate between giving the left hand side and the right hand side priority
       {final Junction l = ttJunctions[jjLeft];                                 // Left child
        final Message  u = l.jjMessageUp;                                       // A possible message from the left child
        if (U == null && u != null)                                             // Left might want to send a message up
         {jjMessageUp = u;
         }
       }
      else if (ttStep % 2 == 1 && jjRight != null)                              // Examine the right child for a message to be sent up. To avoid always giving messages on the left hand side priority over the right hand side we alternate between giving the left hand side and the right hand side priority
       {final Junction r = ttJunctions[jjRight];                                // Right child
        final Message  u = r.jjMessageUp;                                       // A possible message from the right child
        if (U == null && u != null)                                             // Right might want to send a message up
         {jjMessageUp = u;
         }
       }
     }

    void jjClearDown()                                                          // Clear source of messages sent down through this junction
     {if (jjMessageDownPending == null) return;                                 // Skip if there is no downward seeking message pending for this junction
      jjMessageDown = jjMessageDownPending;                                     // Move message into main downline
      jjMessageDownPending = null;                                              // Move message from pending to active now that downward simulation step is complete
      ttJunctions[jjParent].jjMessageDown = null;                               // Remove messsage from parent
     }

    void jjCopyDown()                                                           // Transmit messages down through this junction
     {final Junction p = ttJunctions[jjParent];                                 // Parent
      final Message  U = p.jjMessageUp;                                         // A possible message from the parent going up
      final Message  D = p.jjMessageDown;                                       // A possible message from the parent coming down
      final Message  d =   jjMessageDown;                                       // Message at this level if any
      if (D != null && d == null)                                               // Parent wants to send us a message
       {if (jjAddress.aaDown(D.mmTarget)) jjMessageDownPending = D;             // Message should go down through this junction, Cache the message for the moment to prevent overruns.
       }
      else if (U != null)                                                       // Short circuit upward seeking message if its target is on this branch
       {if (jjAddress.aaDown(U.mmTarget))
         {jjMessageDownPending = U;                                             // Place upward message on downward seeking path
          p.jjMessageUp = null;                                                 // Clear message from parent upward seeking path
         }
       }
     }
   }

//D1 Address
                                                                                // The address of a junction is its path from the root through the tree network to this junction
  private class Address                                                         // The address of  the junction in the tree network
   {final int    aaIndex;                                                       // The numeric representation of the address of the junction in the tree network
    final int    aaLevel;                                                       // The level of this address in the tree network with the root at level zero and the next level at one etc.
    final String aaAddress;                                                     // Address in branch path steering format

    Address(int Index)
     {aaIndex = Index;
      final StringBuilder s = new StringBuilder();
      for (int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");    // Path from zero to this address
      aaAddress = ""+s.reverse();
      aaLevel   = aaAddress.length();
     }

    public String toString()
     {return String.format("Address: %d %d %s\n",
                            aaIndex, aaLevel, aaAddress);
     }

    private boolean aaDown(Address Target)                                      // Is this an address that can be descended through towards the target
     {return Target.aaLevel >= aaLevel &&                                       // Level of target must be here or further down
             Target.aaAddress.startsWith(aaAddress);                            // Target prefix must match that of the current junction
     }
   }

//D1 Message                                                                    // A message sent from a source leaf to a target leaf

  class Message                                                                 // A message sent from a source leaf to a target leaf
   {final Address mmSource;                                                     // The address of the sending source leaf
    final Address mmTarget;                                                     // The address of the receiving target leaf
    final String  mmText;                                                       // The text of the message

    Message(int Source, int Target, String Text)                                // Add a new message to be transmitted through the tree network
     {mmSource = new Address(Source);                                           // Source junction accepting the message
      mmTarget = new Address(Target);                                           // Target junction to which the message is to be sent
      mmText   = Text;                                                          // Text of the message
      ttJunctions[Source].jjMessageUp = this;                                   // The message starts its journey at the source junction
     }

    public String toString()                                                    // Print a message
     {final int    s = mmSource.aaIndex;
      final int    t = mmTarget.aaIndex;
      final String T = mmText;

      final String r = String.format("%d->%d:%s", s, t, T);
      return String.format("%-16s", r);
     }

    String toString(int Position)                                               // Show the current position of the message in its path through the tree network
     {final int    s = mmSource.aaIndex;
      final int    t = mmTarget.aaIndex;
      final String T = mmText;

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

//D1 Tests                                                                      // Test the tree network

  static void test_one()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3);
    final StringBuilder s = new StringBuilder(); T.ttPrintCompact = false;

    T.new Message(5, 3, "AAAA");

    for (T.ttStep = 0; T.ttStep < 4; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
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

  static void test_two()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.new Message(13, 7, "AAAA");
    T.new Message(14, 8, "BBBB");

    for (T.ttStep = 0; T.ttStep < 11; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
     }
    //stop(s);
    ok(""+s, """
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
   1    *                   0     3     4  0                            13-1-7   AAAA     |
   6      *                 2    13    14  11        14-6-8   BBBB                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           13-3-7   AAAA     |
   6      *                 2    13    14  11        14-6-8   BBBB                        |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-8   BBBB                        |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            14-1-8   BBBB     |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:    9        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           14-3-8   BBBB     |
   7        *               3              000                          13-->>7  AAAA     |
Jnct  At step:   10        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          13-->>7  AAAA     |
   8        *               3              001                          14-->>8  BBBB     |
""");
   }

  static void test_swap()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(2);
    T.ttPrintCompact = false;
    final StringBuilder s = new StringBuilder();

    T.new Message(1, 2, "AAAA");
    T.new Message(2, 1, "BBBB");

    for (T.ttStep = 0; T.ttStep < 3; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0              0         1>>--2   AAAA                        |
   2    *                   0              1         2>>--1   BBBB                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0              0                                              |
   2    *                   0              1         2>>--1   BBBB      1-->>2   AAAA     |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   0  *                           1     2                                                 |
   1    *                   0              0                            2-->>1   BBBB     |
   2    *                   0              1                            1-->>2   AAAA     |
""");
   }

  static void test_reversePair()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.new Message(7, 14, "AAAA");
    T.new Message(14, 7, "BBBB");

    for (T.ttStep = 0; T.ttStep < 9; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
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
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   6      *                 2    13    14  11        14-6-7   BBBB                        |
Jnct  At step:    4        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   2    *                   0     5     6  1         14-2-7   BBBB                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-7   BBBB      7-2-14   AAAA     |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            14-1-7   BBBB     |
   6      *                 2    13    14  11                           7-6-14   AAAA     |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00                           14-3-7   BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
""");
   }

  static void test_reverse()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.new Message( 7, 14, "AAAA");
    T.new Message( 8, 13, "BBBB");
    T.new Message( 9, 12, "CCCC");
    T.new Message(10, 11, "DDDD");
    T.new Message(11, 10, "EEEE");
    T.new Message(12,  9, "FFFF");
    T.new Message(13,  8, "GGGG");
    T.new Message(14,  7, "HHHH");

    for (T.ttStep = 0; T.ttStep < 19; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000       7>>--14  AAAA                        |
   8        *               3              001       8>>--13  BBBB                        |
   9        *               4              010       9>>--12  CCCC                        |
  10        *               4              011       10>>--11 DDDD                        |
  11        *               5              100       11>>--10 EEEE                        |
  12        *               5              101       12>>--9  FFFF                        |
  13        *               6              110       13>>--8  GGGG                        |
  14        *               6              111       14>>--7  HHHH                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   4      *                 1     9    10  01        9-4-12   CCCC                        |
   5      *                 2    11    12  10        11-5-10  EEEE                        |
   6      *                 2    13    14  11        13-6-8   GGGG                        |
   8        *               3              001       8>>--13  BBBB                        |
  10        *               4              011       10>>--11 DDDD                        |
  12        *               5              101       12>>--9  FFFF                        |
  14        *               6              111       14>>--7  HHHH                        |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         9-1-12   CCCC                        |
   2    *                   0     5     6  1         13-2-8   GGGG                        |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   5      *                 2    11    12  10        11-5-10  EEEE                        |
   8        *               3              001       8>>--13  BBBB                        |
  10        *               4              011       10>>--11 DDDD                        |
  12        *               5              101       12>>--9  FFFF                        |
  14        *               6              111       14>>--7  HHHH                        |
Jnct  At step:    3        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         13-2-8   GGGG      9-2-12   CCCC     |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   5      *                 2    11    12  10        11-5-10  EEEE                        |
   8        *               3              001       8>>--13  BBBB                        |
  10        *               4              011       10>>--11 DDDD                        |
  12        *               5              101       12>>--9  FFFF                        |
  14        *               6              111       14>>--7  HHHH                        |
Jnct  At step:    4        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0                            13-1-8   GGGG     |
   3      *                 1     7     8  00        7-3-14   AAAA                        |
   4      *                 1     9    10  01        10-4-11  DDDD                        |
   5      *                 2    11    12  10        11-5-10  EEEE      9-5-12   CCCC     |
   6      *                 2    13    14  11        14-6-7   HHHH                        |
   8        *               3              001       8>>--13  BBBB                        |
  12        *               5              101       12>>--9  FFFF                        |
Jnct  At step:    5        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA                        |
   2    *                   0     5     6  1         11-2-10  EEEE                        |
   3      *                 1     7     8  00                           13-3-8   GGGG     |
   4      *                 1     9    10  01        10-4-11  DDDD                        |
   6      *                 2    13    14  11        14-6-7   HHHH                        |
   8        *               3              001       8>>--13  BBBB                        |
  12        *               5              101       12>>--9  FFFF      9-->>12  CCCC     |
Jnct  At step:    6        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         7-1-14   AAAA      11-1-10  EEEE     |
   3      *                 1     7     8  00        8-3-13   BBBB                        |
   4      *                 1     9    10  01        10-4-11  DDDD                        |
   5      *                 2    11    12  10        12-5-9   FFFF                        |
   6      *                 2    13    14  11        14-6-7   HHHH                        |
   8        *               3              001                          13-->>8  GGGG     |
  12        *               5              101                          9-->>12  CCCC     |
Jnct  At step:    7        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         12-2-9   FFFF      7-2-14   AAAA     |
   3      *                 1     7     8  00        8-3-13   BBBB                        |
   4      *                 1     9    10  01        10-4-11  DDDD      11-4-10  EEEE     |
   6      *                 2    13    14  11        14-6-7   HHHH                        |
   8        *               3              001                          13-->>8  GGGG     |
  12        *               5              101                          9-->>12  CCCC     |
Jnct  At step:    8        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         10-1-11  DDDD      12-1-9   FFFF     |
   3      *                 1     7     8  00        8-3-13   BBBB                        |
   6      *                 2    13    14  11        14-6-7   HHHH      7-6-14   AAAA     |
   8        *               3              001                          13-->>8  GGGG     |
  10        *               4              011                          11-->>10 EEEE     |
  12        *               5              101                          9-->>12  CCCC     |
Jnct  At step:    9        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1                            10-2-11  DDDD     |
   3      *                 1     7     8  00        8-3-13   BBBB                        |
   4      *                 1     9    10  01                           12-4-9   FFFF     |
   6      *                 2    13    14  11        14-6-7   HHHH                        |
   8        *               3              001                          13-->>8  GGGG     |
  10        *               4              011                          11-->>10 EEEE     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   10        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1         14-2-7   HHHH                        |
   3      *                 1     7     8  00        8-3-13   BBBB                        |
   5      *                 2    11    12  10                           10-5-11  DDDD     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   11        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         8-1-13   BBBB                        |
   2    *                   0     5     6  1         14-2-7   HHHH                        |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   12        Up  Left Right  Addr      Up_______________  Down____________  |
   1    *                   0     3     4  0         8-1-13   BBBB      14-1-7   HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   13        Up  Left Right  Addr      Up_______________  Down____________  |
   2    *                   0     5     6  1                            8-2-13   BBBB     |
   3      *                 1     7     8  00                           14-3-7   HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   14        Up  Left Right  Addr      Up_______________  Down____________  |
   6      *                 2    13    14  11                           8-6-13   BBBB     |
   7        *               3              000                          14-->>7  HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   15        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  13        *               6              110                          8-->>13  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   16        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  13        *               6              110                          8-->>13  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   17        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  13        *               6              110                          8-->>13  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
Jnct  At step:   18        Up  Left Right  Addr      Up_______________  Down____________  |
   7        *               3              000                          14-->>7  HHHH     |
   8        *               3              001                          13-->>8  GGGG     |
   9        *               4              010                          12-->>9  FFFF     |
  10        *               4              011                          11-->>10 EEEE     |
  11        *               5              100                          10-->>11 DDDD     |
  12        *               5              101                          9-->>12  CCCC     |
  13        *               6              110                          8-->>13  BBBB     |
  14        *               6              111                          7-->>14  AAAA     |
""");
   }

  static void test_short()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.new Message(14, 13, "AAAA");
    T.new Message(13, 14, "BBBB");

    for (T.ttStep = 0; T.ttStep < 3; ++T.ttStep)
     {s.append(T);
      T.ttTransmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  At step:    0        Up  Left Right  Addr      Up_______________  Down____________  |
  13        *               6              110       13>>--14 BBBB                        |
  14        *               6              111       14>>--13 AAAA                        |
Jnct  At step:    1        Up  Left Right  Addr      Up_______________  Down____________  |
  14        *               6              111       14>>--13 AAAA      13-->>14 BBBB     |
Jnct  At step:    2        Up  Left Right  Addr      Up_______________  Down____________  |
  13        *               6              110                          14-->>13 AAAA     |
  14        *               6              111                          13-->>14 BBBB     |
""");
   }

  static void test_sequence()
   {sayCurrentTestName();
    final int Source = 14, Target = 7, Steps = 28;
    final TreeNet T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();
    final StringJoiner  t = new StringJoiner(", ");
    final String [] words = "AAAA BBBB CCCC DDDD EEEE FFFF".split("\\s+");

    int i = 0;
    for (T.ttStep = 0; T.ttStep < Steps; ++T.ttStep)
     {if (i < words.length && T.ttPutMessage(Source, Target, words[i])) ++i;
      s.append(T);
      T.ttTransmit();
      final Message m = T.ttGetMessage(Target);
      if (m != null) t.add(m.mmText);
     }
    //stop(t);
    ok(t, "AAAA, BBBB, CCCC, DDDD, EEEE, FFFF");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_one();
    test_two();
    test_swap();
    test_reversePair();
    test_reverse();
    test_short();
    test_sequence();
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
