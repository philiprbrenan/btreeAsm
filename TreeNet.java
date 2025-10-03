//------------------------------------------------------------------------------
// A tree network that connects leaf pairs via branches in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Test                                                      // A tree network that connects leaf pairs via branches in logarithmic time, allowing us to compose a large chip as a networked collection of smaller chips.
 {final int       size;                                                         // The number of 3-way junctions in the network plus the root and leaves
  final Junction[]junctions;                                                    // The junctions used to construct the tree network
  final boolean []messageUp;                                                    // Message waiting to be sent up through the tree network
  final boolean []messageDown;                                                  // Message waiting to be sent down through the tree network
  final boolean []messageDownPending;                                           // A downward seeking message is cached here during simulation to prevent overruns

  final int     []messageUpNumber;                                              // The unique number of the message going up
  final int     []messageUpSource;                                              // The address of the sending source leaf
  final int     []messageUpTarget;                                              // The address of the receiving target leaf
  final int     []messageUpText;                                                // The text of the message

  final int     []messageDownNumber;                                            // The unique number of the message going down
  final int     []messageDownSource;                                            // The address of the sending source leaf
  final int     []messageDownTarget;                                            // The address of the receiving target leaf
  final int     []messageDownText;                                              // The text of the message

  final int     []messageDownPendingNumber;                                     // The unique number of the pending message going down
  final int     []messageDownPendingSource;                                     // The address of the sending source leaf
  final int     []messageDownPendingTarget;                                     // The address of the receiving target leaf
  final int     []messageDownPendingText;                                       // The text of the message

  final int     []level;                                                        // The level of this address in the tree network with the root at level zero and the next level at one etc.
  boolean         printCompact = true;                                          // Print network trace in compact format if true
  final String  []address;                                                      // Address in branch path steering format
  int             messageNumber;                                                // A unique number generator used to label messages
  int             step;                                                         // The tree network executes in steps

//D1 Construction                                                               // Construct a tree network

  TreeNet(int Size)                                                             // Create the tree network.  The number of leaves will be 2**(Size-1)
   {size = powerTwo(Size) - 1;
    junctions = new Junction[size];                                             // The junctions used to construct the tree network
    messageUp                = new boolean[size];                               // Message waiting to be sent up through the tree network
    messageDown              = new boolean[size];                               // Message waiting to be sent down through the tree network
    messageDownPending       = new boolean[size];                               // A downward seeking message is cached here during simulation to prevent overruns

    messageUpNumber          = new int[size];                                   // The unique number of the message going up
    messageUpSource          = new int[size];                                   // The address of the sending source leaf
    messageUpTarget          = new int[size];                                   // The address of the receiving target leaf
    messageUpText            = new int[size];                                   // The text of the message

    messageDownNumber        = new int[size];                                   // The unique number of the message going down
    messageDownSource        = new int[size];                                   // The address of the sending source leaf
    messageDownTarget        = new int[size];                                   // The address of the receiving target leaf
    messageDownText          = new int[size];                                   // The text of the message

    messageDownPendingNumber = new int[size];                                   // The unique number of the pending message going down
    messageDownPendingSource = new int[size];                                   // The address of the sending source leaf
    messageDownPendingTarget = new int[size];                                   // The address of the receiving target leaf
    messageDownPendingText   = new int[size];                                   // The text of the message

    level                    = new int[size];                                   // A downward seeking message is cached here during simulation to prevent overruns
    address                  = new String[size];                                // A downward seeking message is cached here during simulation to prevent overruns

    for (int i = 0; i < size; i++) junctions[i] = new Junction(i);              // Create the junctions and connect them together
   }

  public String toString()                                                      // Print the tree network
   {final StringJoiner s = new StringJoiner("\n", "", "\n");
    s.add(String.format("Jnct  At step: %4d        Up  Left Right  Addr      Up______  Down____ |", step));
    for (int i = 0; i < size; i++)                                              // Each junction in the tree network
     {final Junction j = junctions[i];
      if (j == null) continue;

      final Integer p = j.parent, l = j.left, r = j.right;                      // Relationship with other junctions
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  n = "  ".repeat(j.level)+"*"+"  ".repeat(8-j.level);

      final boolean u = messageUp  [i];                                         // Message detail
      final boolean d = messageDown[i];
      final String  U = u ? ""+messageUpText  [i] : "";
      final String  D = d ? ""+messageDownText[i] : "";

      if (!printCompact || u || d)                                              // Print if there has been activity on this junction or if all junctions are being printed
       {s.add(String.format("%4d  %s  %s  %s  %s  %-8s  %-8s  %-8s |",
          i, n, P, L, R, address[i], U, D));
       }
     }
    return ""+s;                                                                // Printed state of tree network at this point in time
   }

  void transmit()                                                               // Transmit each message one step through the tree network
   {final int N = junctions.length;
    for (int i = 0; i < N; i++) junctions[i].copyUp();                          // Copy a source message up one step so that it is closer to the target
    for (int i = 1; i < N; i++) junctions[i].clearUp();                         // Clear the source of an upward-moving message - the root cannot be such a source
    messageDown      [0] = messageUp      [0];                                  // Transfer the message from the upward seeking side of the tree to the downward seeking side
    messageDownNumber[0] = messageUpNumber[0];
    messageDownSource[0] = messageUpSource[0];
    messageDownTarget[0] = messageUpTarget[0];
    messageDownText  [0] = messageUpText  [0];

    messageUp  [0] = false;                                                     // Remove message from upward seeking side now that it has been transfered to the downward seeking side of the tree network
    for(int i = 1; i < N; i++) junctions[i].copyDown();                         // Copy a source message down one step so that it is closer to the target
    for(int i = 1; i < N; i++) junctions[i].clearDown();                        // Clear the source of a downward-moving message - the root cannot be such a source
    for(int i = 1; i < N; i++) junctions[i].clearShort();                       // Clear the source of a shiort circuited downward-moving message - the root cannot be such a source
   }

  boolean putMessage(int Source, int Target, int Text)                          // Add a new message at the indicated leaf if possible and return true else false
   {if (messageUp[Source]) return false;                                        // There is a message on the leaf already
    messageUp      [Source] = true;
    messageUpNumber[Source] = messageNumber++;                                  // Add the message
    messageUpSource[Source] = Source;                                           // Add the message
    messageUpTarget[Source] = Target;                                           // Add the message
    messageUpText  [Source] = Text;                                             // Add the message
    return true;                                                                // Success
   }

  class MessageOut                                                              // Remove a message from the tree network and record its details
   {final boolean valid;
    final int source;
    final int target;
    final int text;
    MessageOut(int Leaf)                                                        // Get any message that has arrived at the specified leaf
     {valid  = messageDown      [Leaf];                                         // Whether the message is valid
      source = messageDownSource[Leaf];                                         // Get any message
      target = messageDownTarget[Leaf];                                         // Get any message
      text   = messageDownText  [Leaf];                                         // Remove the message
               messageDown      [Leaf] = false;
     }
   }

//D2 Addressing                                                                 // Address of a junction in root to branch or leaf guidance path

  void address(int Index)
   {final StringBuilder s = new StringBuilder();
    for (int N = Index+1; N > 1; N /= 2) s.append(N % 2 == 1 ? "1" : "0");      // Path from zero to this address
    address[Index] = ""+s.reverse();
    level  [Index] = address[Index].length();
   }

  private boolean addressDown(int Source, int Target)                           // Is this an address that can be descended through towards the target
   {return level[Target] >= level[Source] &&                                    // Level of target must be here or further down
           address[Target].startsWith(address[Source]);                         // Target prefix must match that of the current junction
   }

//D1 Junction                                                                   // A junction in a tree network connects two child junctions to a parent junction.

  private class Junction                                                        // A junction in a tree network connects two child junctions to a parent junction.
   {final int     number;                                                       // The number of the junction
    final int     level;                                                        // The level of the junction.  The root is at level zero, the next at level one, etc.
    final Integer parent;                                                       // The index of the parent of this junction
    final Integer left;                                                         // The index of the left child junction
    final Integer right;                                                        // The index of the right child junction

    Junction(int Number)
     {number  = Number;
      address(number);                                                          // Convert the junction number into a path guidance string
      level   = logTwo(prevPowerOfTwo(1+Number));                               // Level of junction
      parent  = Number == 0 ? null   : (Number-1) / 2;                          // Set parent
      left    = Number * 2 + 1 < size ? Number * 2 + 1 : null;                  // Left child
      right   = Number * 2 + 2 < size ? Number * 2 + 2 : null;                  // Right child
     }

    private void clearUp()                                                      // Clear source of messages sent up through this junction
     {if (messageUp      [number] && messageUp      [parent] &&                 // Same message in parent and child means we can remove the child message
          messageUpNumber[number] == messageUpNumber[parent])
       {messageUp[number] = false;
       }
     }

    void copyUp()                                                               // Transmit messages up through this junction
     {final boolean up = messageUp[number];                                     // Message at this level if any
      if (step % 2 == 0 && left != null)                                        // Examine the left child for a message to be sent up. To avoid always giving messages on the left hand side priority over the right hand side we alternate between giving the left hand side and the right hand side priority
       {if (!up && messageUp[left])                                             // Left might want to send a message up
         {messageUp      [number] = messageUp      [left];
          messageUpNumber[number] = messageUpNumber[left];
          messageUpSource[number] = messageUpSource[left];
          messageUpTarget[number] = messageUpTarget[left];
          messageUpText  [number] = messageUpText  [left];
         }
       }
      else if (step % 2 == 1 && right != null)                                  // Examine the right child for a message to be sent up. To avoid always giving messages on the left hand side priority over the right hand side we alternate between giving the left hand side and the right hand side priority
       {if (!up && messageUp[right])                                            // Right might want to send a message up
         {messageUp      [number] = messageUp      [right];
          messageUpNumber[number] = messageUpNumber[right];
          messageUpSource[number] = messageUpSource[right];
          messageUpTarget[number] = messageUpTarget[right];
          messageUpText  [number] = messageUpText  [right];
         }
       }
     }

    void clearDown()                                                            // Clear source of messages sent down through this junction
     {if (!messageDownPending[number]) return;                                  // Skip if there is no downward seeking message pending for this junction
      messageDown       [number] = messageDownPending      [number];
      messageDownNumber [number] = messageDownPendingNumber[number];
      messageDownSource [number] = messageDownPendingSource[number];
      messageDownTarget [number] = messageDownPendingTarget[number];
      messageDownText   [number] = messageDownPendingText  [number];
      messageDownPending[number] = false;                                       // Move message from pending to active now that downward simulation step is complete
      messageDown       [parent] = false;                                       // Remove messsage from parent
     }

    void clearShort()                                                           // Clear short circuit source in parent
     {if ((left  != null &&
           messageDown      [left]  && messageUp[number] &&                     // Clear the upward seeking message if it was transferred to a downward seeking branch
           messageDownNumber[left]  == messageUpNumber[number])   ||
          (right != null &&
           messageDown      [right] && messageUp[number] &&
           messageDownNumber[right] == messageUpNumber[number]))
       {messageUp[number] = false;
       }
     }

    void copyDown()                                                             // Transmit messages down through this junction
     {if (messageDown[parent] && !messageDown[number])                                                              // Parent wants to send us a message
       {if (addressDown(number, messageDownTarget[parent]))                     // Message should go down through this junction. Cache the message for the moment to prevent overruns.
         {messageDownPending      [number] = true;
          messageDownPendingNumber[number] = messageDownNumber[parent];
          messageDownPendingSource[number] = messageDownSource[parent];
          messageDownPendingTarget[number] = messageDownTarget[parent];
          messageDownPendingText  [number] = messageDownText  [parent];
          messageDownPendingText  [number] = messageDownText  [parent];
         }
       }
      else if (messageUp[parent])                                               // Short circuit upward seeking message if its target is on this branch
       {if (addressDown(number, messageUpTarget[parent]))                       // Could the upward seeking message short circuit down this branch to reach its target?
         {messageDownPending      [number] = true;
          messageDownPendingNumber[number] = messageUpNumber[parent];
          messageDownPendingSource[number] = messageUpSource[parent];
          messageDownPendingTarget[number] = messageUpTarget[parent];
          messageDownPendingText  [number] = messageUpText  [parent];
         }
       }
     }
   }

//D1 Tests                                                                      // Test the tree network

  static void test_one()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3);
    final StringBuilder s = new StringBuilder(); T.printCompact = false;

    T.putMessage(5, 3, 1111);

    for (T.step = 0; T.step < 4; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0     3     4  0                            |
   2    *                   0     5     6  1                            |
   3      *                 1              00                           |
   4      *                 1              01                           |
   5      *                 2              10        1111               |
   6      *                 2              11                           |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0     3     4  0                            |
   2    *                   0     5     6  1         1111               |
   3      *                 1              00                           |
   4      *                 1              01                           |
   5      *                 2              10                           |
   6      *                 2              11                           |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0     3     4  0                   1111     |
   2    *                   0     5     6  1                            |
   3      *                 1              00                           |
   4      *                 1              01                           |
   5      *                 2              10                           |
   6      *                 2              11                           |
Jnct  At step:    3        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0     3     4  0                            |
   2    *                   0     5     6  1                            |
   3      *                 1              00                  1111     |
   4      *                 1              01                           |
   5      *                 2              10                           |
   6      *                 2              11                           |
""");
   }

  static void test_two()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3);
    final StringBuilder s = new StringBuilder();

    T.putMessage(5, 3, 1111);
    T.putMessage(6, 4, 2222);

    for (T.step = 0; T.step < 11; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
   5      *                 2              10        1111               |
   6      *                 2              11        2222               |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         1111               |
   6      *                 2              11        2222               |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0                   1111     |
   6      *                 2              11        2222               |
Jnct  At step:    3        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1              00                  1111     |
   6      *                 2              11        2222               |
Jnct  At step:    4        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         2222               |
   3      *                 1              00                  1111     |
Jnct  At step:    5        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         2222               |
   3      *                 1              00                  1111     |
Jnct  At step:    6        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0                   2222     |
   3      *                 1              00                  1111     |
Jnct  At step:    7        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1              00                  1111     |
   4      *                 1              01                  2222     |
Jnct  At step:    8        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1              00                  1111     |
   4      *                 1              01                  2222     |
Jnct  At step:    9        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1              00                  1111     |
   4      *                 1              01                  2222     |
Jnct  At step:   10        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1              00                  1111     |
   4      *                 1              01                  2222     |
""");
   }

  static void test_swap()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(2);
    T.printCompact = false;
    final StringBuilder s = new StringBuilder();

    T.putMessage(1, 2, 1111);
    T.putMessage(2, 1, 2222);

    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0              0         1111               |
   2    *                   0              1         2222               |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0              0                            |
   2    *                   0              1         2222      1111     |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
   0  *                           1     2                               |
   1    *                   0              0                   2222     |
   2    *                   0              1                   1111     |
""");
   }

  static void test_reversePair()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.putMessage(7, 14, 1111);
    T.putMessage(14, 7, 2222);

    for (T.step = 0; T.step < 9; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000       1111               |
  14        *               6              111       2222               |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1     7     8  00        1111               |
  14        *               6              111       2222               |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1     7     8  00        1111               |
   6      *                 2    13    14  11        2222               |
Jnct  At step:    3        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         1111               |
   6      *                 2    13    14  11        2222               |
Jnct  At step:    4        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         1111               |
   2    *                   0     5     6  1         2222               |
Jnct  At step:    5        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         2222      1111     |
Jnct  At step:    6        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0                   2222     |
   6      *                 2    13    14  11                  1111     |
Jnct  At step:    7        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1     7     8  00                  2222     |
  14        *               6              111                 1111     |
Jnct  At step:    8        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000                 2222     |
  14        *               6              111                 1111     |
""");
   }

  static void test_reverse()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.putMessage( 7, 14, 1111);
    T.putMessage( 8, 13, 2222);
    T.putMessage( 9, 12, 3333);
    T.putMessage(10, 11, 4444);
    T.putMessage(11, 10, 5555);
    T.putMessage(12,  9, 6666);
    T.putMessage(13,  8, 7777);
    T.putMessage(14,  7, 8888);

    for (T.step = 0; T.step < 19; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000       1111               |
   8        *               3              001       2222               |
   9        *               4              010       3333               |
  10        *               4              011       4444               |
  11        *               5              100       5555               |
  12        *               5              101       6666               |
  13        *               6              110       7777               |
  14        *               6              111       8888               |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
   3      *                 1     7     8  00        1111               |
   4      *                 1     9    10  01        3333               |
   5      *                 2    11    12  10        5555               |
   6      *                 2    13    14  11        7777               |
   8        *               3              001       2222               |
  10        *               4              011       4444               |
  12        *               5              101       6666               |
  14        *               6              111       8888               |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         3333               |
   2    *                   0     5     6  1         7777               |
   3      *                 1     7     8  00        1111               |
   5      *                 2    11    12  10        5555               |
   8        *               3              001       2222               |
  10        *               4              011       4444               |
  12        *               5              101       6666               |
  14        *               6              111       8888               |
Jnct  At step:    3        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         7777      3333     |
   3      *                 1     7     8  00        1111               |
   5      *                 2    11    12  10        5555               |
   8        *               3              001       2222               |
  10        *               4              011       4444               |
  12        *               5              101       6666               |
  14        *               6              111       8888               |
Jnct  At step:    4        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0                   7777     |
   3      *                 1     7     8  00        1111               |
   4      *                 1     9    10  01        4444               |
   5      *                 2    11    12  10        5555      3333     |
   6      *                 2    13    14  11        8888               |
   8        *               3              001       2222               |
  12        *               5              101       6666               |
Jnct  At step:    5        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         1111               |
   2    *                   0     5     6  1         5555               |
   3      *                 1     7     8  00                  7777     |
   4      *                 1     9    10  01        4444               |
   6      *                 2    13    14  11        8888               |
   8        *               3              001       2222               |
  12        *               5              101       6666      3333     |
Jnct  At step:    6        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         1111      5555     |
   3      *                 1     7     8  00        2222               |
   4      *                 1     9    10  01        4444               |
   5      *                 2    11    12  10        6666               |
   6      *                 2    13    14  11        8888               |
   8        *               3              001                 7777     |
  12        *               5              101                 3333     |
Jnct  At step:    7        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         6666      1111     |
   3      *                 1     7     8  00        2222               |
   4      *                 1     9    10  01        4444      5555     |
   6      *                 2    13    14  11        8888               |
   8        *               3              001                 7777     |
  12        *               5              101                 3333     |
Jnct  At step:    8        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         4444      6666     |
   3      *                 1     7     8  00        2222               |
   6      *                 2    13    14  11        8888      1111     |
   8        *               3              001                 7777     |
  10        *               4              011                 5555     |
  12        *               5              101                 3333     |
Jnct  At step:    9        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1                   4444     |
   3      *                 1     7     8  00        2222               |
   4      *                 1     9    10  01                  6666     |
   6      *                 2    13    14  11        8888               |
   8        *               3              001                 7777     |
  10        *               4              011                 5555     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   10        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1         8888               |
   3      *                 1     7     8  00        2222               |
   5      *                 2    11    12  10                  4444     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   11        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         2222               |
   2    *                   0     5     6  1         8888               |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   12        Up  Left Right  Addr      Up______  Down____ |
   1    *                   0     3     4  0         2222      8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   13        Up  Left Right  Addr      Up______  Down____ |
   2    *                   0     5     6  1                   2222     |
   3      *                 1     7     8  00                  8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   14        Up  Left Right  Addr      Up______  Down____ |
   6      *                 2    13    14  11                  2222     |
   7        *               3              000                 8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  14        *               6              111                 1111     |
Jnct  At step:   15        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000                 8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  13        *               6              110                 2222     |
  14        *               6              111                 1111     |
Jnct  At step:   16        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000                 8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  13        *               6              110                 2222     |
  14        *               6              111                 1111     |
Jnct  At step:   17        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000                 8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  13        *               6              110                 2222     |
  14        *               6              111                 1111     |
Jnct  At step:   18        Up  Left Right  Addr      Up______  Down____ |
   7        *               3              000                 8888     |
   8        *               3              001                 7777     |
   9        *               4              010                 6666     |
  10        *               4              011                 5555     |
  11        *               5              100                 4444     |
  12        *               5              101                 3333     |
  13        *               6              110                 2222     |
  14        *               6              111                 1111     |
""");
   }

  static void test_short()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.putMessage(14, 13, 1111);
    T.putMessage(13, 14, 2222);

    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  At step:    0        Up  Left Right  Addr      Up______  Down____ |
  13        *               6              110       2222               |
  14        *               6              111       1111               |
Jnct  At step:    1        Up  Left Right  Addr      Up______  Down____ |
  14        *               6              111       1111      2222     |
Jnct  At step:    2        Up  Left Right  Addr      Up______  Down____ |
  13        *               6              110                 1111     |
  14        *               6              111                 2222     |
""");
   }

  static void test_sequence()
   {sayCurrentTestName();
    final int Source = 14, Target = 7, Steps = 28;
    final TreeNet T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();
    final StringJoiner  t = new StringJoiner(", ");
    final int []    words = {1111, 2222, 3333, 4444, 5555, 6666};

    int i = 0;
    for (T.step = 0; T.step < Steps; ++T.step)
     {if (i < words.length && T.putMessage(Source, Target, words[i])) ++i;
      s.append(T);
      T.transmit();
      final MessageOut m = T.new MessageOut(Target);
      if (m.valid) t.add(""+m.text);
     }
    //stop(t);
    ok(t, "1111, 2222, 3333, 4444, 5555, 6666");
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
