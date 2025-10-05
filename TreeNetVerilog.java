//------------------------------------------------------------------------------
// A tree network that connects leaf pairs via branches in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNetVerilog extends Chip                                               // A tree network that connects leaf pairs via branches in logarithmic time, allowing us to compose a large chip as a networked collection of smaller chips.
 {final int       size;                                                         // Total number of junctions in the binary tree, including root and leaves
  final int       addressWidth =  8;                                            // The width of a network address
  final int       messageWidth = 32;                                            // The width of a network message
  final Process   P = new Process("main");                                      // Process in which the network runs
  final BitSet  []address;                                                      // Address in branch path steering format
  final Process.Register Address;                                               // Address in branch path steering format

  final boolean []messageUp;                                                    // Message waiting to be sent upward through the tree network
  final boolean []messageDown;                                                  // Message waiting to be sent downward through the tree network
  final boolean []messageDownPending;                                           // A downward-seeking message is cached here during simulation to prevent overruns
  final Process.Register MessageUp;                                             // Address in branch path steering format
  final Process.Register MessageDown;                                           // Address in branch path steering format
  final Process.Register MessageDownPending;                                    // Address in branch path steering format

  final int     []messageUpNumber;                                              // The unique number of the message going upward
  final int     []messageUpSource;                                              // The address of the sending source leaf
  final int     []messageUpTarget;                                              // The address of the receiving target leaf
  final int     []messageUpText;                                                // The text of the message
  final Process.Register MessageUpNumber;                                       // Address in branch path steering format
  final Process.Register MessageUpSource;                                       // Address in branch path steering format
  final Process.Register MessageUpTarget;                                       // Address in branch path steering format
  final Process.Register MessageUpText;                                         // Address in branch path steering format

  final int     []messageDownNumber;                                            // The unique number of the message going downward
  final int     []messageDownSource;                                            // The address of the sending source leaf
  final int     []messageDownTarget;                                            // The address of the receiving target leaf
  final int     []messageDownText;                                              // The text of the message
  final Process.Register MessageDownNumber;                                     // Address in branch path steering format
  final Process.Register MessageDownSource;                                     // Address in branch path steering format
  final Process.Register MessageDownTarget;                                     // Address in branch path steering format
  final Process.Register MessageDownText;                                       // Address in branch path steering format

  final int     []messageDownPendingNumber;                                     // The unique number of the pending message going down
  final int     []messageDownPendingSource;                                     // The address of the sending source leaf
  final int     []messageDownPendingTarget;                                     // The address of the receiving target leaf
  final int     []messageDownPendingText;                                       // The text of the message
  final Process.Register MessageDownPendingNumber;                              // Address in branch path steering format
  final Process.Register MessageDownPendingSource;                              // Address in branch path steering format
  final Process.Register MessageDownPendingTarget;                              // Address in branch path steering format
  final Process.Register MessageDownPendingText;                                // Address in branch path steering format

  boolean         leftRightPriority = true;                                     // Alternate the priority of moving up on the left or right so that each child junction has equal access to its parent junction
  boolean         printCompact = true;                                          // Print the network trace in a compact format if true
  int             messageNumber;                                                // A unique number generator used to label messages
  int             step;                                                         // The tree network executes in steps
  final Process.Register LeftRightPriority;                                     // Address in branch path steering format
  final Process.Register MessageNumber;                                         // Address in branch path steering format
  final Process.Register Step;                                                  // Address in branch path steering format

//D1 Construction                                                               // Construct a tree network

  TreeNetVerilog(int Size)                                                      // Create the tree network.  The number of leaves will be 2**(Size-1)
   {super("TreeNet");
    size = powerTwo(Size) - 1;
    messageUp                = new boolean[size];                               // Message waiting to be sent upward through the tree network
    messageDown              = new boolean[size];                               // Message waiting to be sent downward through the tree network
    messageDownPending       = new boolean[size];                               // A downward-seeking message is cached here during simulation to prevent overruns

    messageUpNumber          = new int[size];                                   // The unique number of the message going upward
    messageUpSource          = new int[size];                                   // The address of the sending source leaf
    messageUpTarget          = new int[size];                                   // The address of the receiving target leaf
    messageUpText            = new int[size];                                   // The text of the message

    messageDownNumber        = new int[size];                                   // The unique number of the message going downward
    messageDownSource        = new int[size];                                   // The address of the sending source leaf
    messageDownTarget        = new int[size];                                   // The address of the receiving target leaf
    messageDownText          = new int[size];                                   // The text of the message

    messageDownPendingNumber = new int[size];                                   // The unique number of the pending message going down
    messageDownPendingSource = new int[size];                                   // The address of the sending source leaf
    messageDownPendingTarget = new int[size];                                   // The address of the receiving target leaf
    messageDownPendingText   = new int[size];                                   // The text of the message

    address                  = new BitSet[size];                                // The guidance path address to this junction

    Address                  = P.register("address",   1, size);
    MessageUp                = P.register("messageUp", 1, size);
    MessageDown              = P.register("messageUp", 1, size);
    MessageDownPending       = P.register("messageUp", 1, size);

    MessageUpNumber          = P.register("MessageUpNumber", addressWidth, size);
    MessageUpSource          = P.register("MessageUpSource", addressWidth, size);
    MessageUpTarget          = P.register("MessageUpTarget", addressWidth, size);
    MessageUpText            = P.register("MessageUpText",   addressWidth, size);

    MessageDownNumber        = P.register("MessageDownNumber", addressWidth, size);
    MessageDownSource        = P.register("MessageDownSource", addressWidth, size);
    MessageDownTarget        = P.register("MessageDownTarget", addressWidth, size);
    MessageDownText          = P.register("MessageDownText",   addressWidth, size);

    MessageDownPendingNumber = P.register("MessageDownPendingNumber", addressWidth, size);
    MessageDownPendingSource = P.register("MessageDownPendingSource", addressWidth, size);
    MessageDownPendingTarget = P.register("MessageDownPendingTarget", addressWidth, size);
    MessageDownPendingText   = P.register("MessageDownPendingText",   addressWidth, size);

    LeftRightPriority        = P.register("LeftRightPriority", 1);
    MessageNumber            = P.register("MessageNumber",    32);
    Step                     = P.register("Step",             32);

    for (int i = 0; i < size; i++) addressPath(i);                              // Create the addresses in the tree network
   }

  public String toString()                                                      // Print the tree network
   {final StringJoiner s = new StringJoiner("\n", "", "\n");

    s.add(String.format("Jnct  Level Step: %4d        Up  Left Right  Addr      Up______  Down____ |", step));

    for (int i = 0; i < size; i++)                                              // Each junction in the tree network
     {final Integer p = parent(i), l = left(i), r = right(i);                   // Relationship with other junctions
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  n = "  ".repeat(addressLevel(i))+"*"+
                        "  ".repeat(8-addressLevel(i));

      final boolean u = messageUp  [i];                                         // Message detail
      final boolean d = messageDown[i];
      final String  U = u ? ""+messageUpText  [i] : "";
      final String  D = d ? ""+messageDownText[i] : "";

      if (!printCompact || u || d)                                              // Print if there has been activity on this junction or if all junctions are being printed
       {s.add(String.format("%4d  %2d %s  %s  %s  %s  %-8s  %-8s  %-8s |",
          i, addressLevel(i), n, P, L, R, addressPrint(i), U, D));
       }
     }
    return ""+s;                                                                // Printed state of tree network at this point in time
   }

  void transmit()                                                               // Transmit each message one step through the tree network
   {for (int i = 0; i < size; i++) copyUp (i);                                  // Copy a source message up one step so that it is closer to the target
    for (int i = 1; i < size; i++) clearUp(i);                                  // Clear the source of an upward-moving message - the root cannot be such a source

    messageDown      [0] = messageUp      [0];                                  // Transfer the message from the upward-seeking side of the tree to the downward-seeking side
    messageDownNumber[0] = messageUpNumber[0];
    messageDownSource[0] = messageUpSource[0];
    messageDownTarget[0] = messageUpTarget[0];
    messageDownText  [0] = messageUpText  [0];
    messageUp        [0] = false;                                               // Remove the message from upward-seeking side now that it has been transferred to the downward-seeking side of the tree network

    for(int i = 1; i < size; i++) copyDown  (i);                                // Copy a source message down one step so that it is closer to the target
    for(int i = 1; i < size; i++) clearDown (i);                                // Clear the source of a downward-moving message - the root cannot be such a source
    for(int i = 1; i < size; i++) clearShort(i);                                // Clear the source of a short-circuited downward-moving message - the root cannot be such a source
    leftRightPriority = !leftRightPriority;                                     // Alternate left/right upward priority
   }

  void Transmit()                                                               // Transmit each message one step through the tree network
   {P.new Instruction()
     {void action()
       {for (int i = 0; i < size; i++) copyUp (i);                              // Copy a source message up one step so that it is closer to the target
       }
      void verilog(Verilog v)
       {
       }
     };
    P.new Instruction()
     {void action()
       {for (int i = 1; i < size; i++) clearUp(i);                              // Clear the source of an upward-moving message - the root cannot be such a source
       }
      void verilog(Verilog v)
       {
       }
     };

    P.new Instruction()
     {void action()
       {MessageDown      .copy(0, MessageUp      , 0);                                // Transfer the message from the upward-seeking side of the tree to the downward-seeking side
        MessageDownNumber.copy(0, MessageUpNumber, 0);
        MessageDownSource.copy(0, MessageUpSource, 0);
        MessageDownTarget.copy(0, MessageUpTarget, 0);
        MessageDownText  .copy(0, MessageUpText  , 0);
      }
     void verilog(Verilog v)
      {
      }
    };

    P.new Instruction()
     {void action()
       {MessageUp.zero();                                                       // Remove the message from upward-seeking side now that it has been transferred to the downward-seeking side of the tree network
       }
      void verilog(Verilog v)
       {
       }
     };

    P.new Instruction()
     {void action()
       {for(int i = 1; i < size; i++) copyDown  (i);                            // Copy a source message down one step so that it is closer to the target
       }
      void verilog(Verilog v)
       {
       }
     };
    P.new Instruction()
     {void action()
       {for(int i = 1; i < size; i++) clearDown (i);                            // Clear the source of a downward-moving message - the root cannot be such a source
       }
      void verilog(Verilog v)
       {
       }
     };
    P.new Instruction()
     {void action()
       {for(int i = 1; i < size; i++) clearShort(i);                            // Clear the source of a short-circuited downward-moving message - the root cannot be such a source
       }
      void verilog(Verilog v)
       {
       }
     };
    P.new Instruction()
     {void action()
       {LeftRightPriority.not();                                                // Alternate left/right upward priority
       }
      void verilog(Verilog v)
       {
       }
     };
   }

  boolean putMessage(int Source, int Target, int Text)                          // Add a new message at the indicated leaf if possible and return true else false
   {if (!addressLeaf(Source)) stop("Messages must enter via a leaf");
    if (messageUp[Source]) return false;                                        // There is a message on the leaf already
    else
     {messageUp      [Source] = true;                                           // Add the message
      messageUpNumber[Source] = messageNumber++;                                // Generate a unique message number for each message
      messageUpSource[Source] = Source;                                         // Source address
      messageUpTarget[Source] = Target;                                         // Target address
      messageUpText  [Source] = Text;                                           // Text of message
      return true;                                                              // Successfully added message to tree network
     }
   }

  Process.Register PutMessage                                                   // Add a new message at the indicated leaf if possible and return true else false
   (Process.Register Source, Process.Register Target, Process.Register Text)
   {final Process.Register r = P.register("putMessageResult", 1);
    final Process.Register m = P.register("putMessageFull",   1);
    m.CopyIs(MessageUp, Source);                                                // Whether the message slot is empty
    P.new If (m)
     {void Then()                                                               // There is a message on the leaf already
       {r.Zero();                                                               // The message was not added
       }
      void Else()
       {MessageUp      .RegisterSet(1, Source);                                 // Add the message
        MessageUpNumber.CopyIt(Source, MessageNumber);                          // Generate a unique message number for each message
        MessageUpSource.CopyIt(Source, Source);                                 // Source address
        MessageUpTarget.CopyIt(Source, Target);                                 // Target address
        MessageUpText  .CopyIt(Source, Text);                                   // Text of message
        MessageNumber.Inc();
        r.One();                                                                // Successfully added message to tree network
       }
     };
    return r;
   }

  class MessageOut                                                              // Remove a message from the tree network and record its details
   {final boolean valid;                                                        // Whether the message is valid or not
    final int source;                                                           // Source address
    final int target;                                                           // Target address
    final int text;                                                             // Text of message
    MessageOut(int Leaf)                                                        // Get a message from the specified junction
     {valid  = messageDown      [Leaf];                                         // Whether the message is valid
      source = messageDownSource[Leaf];                                         // Source address
      target = messageDownTarget[Leaf];                                         // Target address
      text   = messageDownText  [Leaf];                                         // Text of message
               messageDown      [Leaf] = false;                                 // Remove the message
     }
   }

  class MessageOutV                                                             // Remove a message from the tree network and record its details
   {final Process.Register Valid  = P.register("Valid",  1);                    // Whether the message is valid or not
    final Process.Register Source = P.register("Source", 1);                    // Source address
    final Process.Register Target = P.register("Target", 1);                    // Target address
    final Process.Register Text   = P.register("Text",   1);                    // Text of message
    MessageOutV(Process.Register Leaf)                                          // Get a message from the specified junction
     {Valid  .CopyIs(MessageDown      , Leaf);                                  // Whether the message is valid
      Source .CopyIs(MessageDownSource, Leaf);                                  // Source address
      Target .CopyIs(MessageDownTarget, Leaf);                                  // Target address
      Text   .CopyIs(MessageDownText  , Leaf);                                  // Text of message
                     MessageDown.RegisterSet(0, Leaf);                          // Remove the message
     }
   }

//D1 Addressing                                                                 // Guidance path from the root to a branch or leaf

  private int addressLevel(int Index) {return logTwo(prevPowerOfTwo(1+Index));} // The level of the indexed junction

  private void addressPath(int Index)                                           // Initialize the path to this address of a junction in the tree network
   {final BitSet B = new BitSet();                                              // Bit set representing the path to this junction from the root
    for (int N = Index+1, i = addressLevel(Index); N > 1; N /= 2, --i)
     {if (N % 2 == 1) B.set(i-1);
     }
    address[Index] = B;                                                         // Path from zero to this address as a bit set
    Address.RegisterSet(bitSetToInt(B), Index);                                 // Path from zero to this address as am int
   }

  private boolean addressDown(int Source, int Target)                           // Is this an address that can be descended through towards the target
   {final int N = addressLevel(Source);
    for (int i = 0; i < N; i++)
     {if (address[Source].get(i) != address[Target].get(i)) return false;
     }
    return true;
   }

  private String addressPrint(int Index)                                        // Print an address
   {final StringBuilder s = new StringBuilder();
    final int N = addressLevel(Index);
    for (int i = 0; i < N; i++) s.append(address[Index].get(i) ? "1" : "0");
    return ""+s;
   }

//D1 Leaves                                                                     // Messages may only leave and exit the tree via leaves

  boolean addressLeaf(int Index)                                                // Confirm that this junction is a leaf
   {return left (Index) == null && right(Index) == null;
   }

  int firstLeaf()                                                               // Find first leaf
   {for (int i = 0; i < size; i++) if (addressLeaf(i)) return i;
    stop("No leaves in tree network");
    return 0;
   }

  int lastLeaf()                                                                // Find last leaf
   {for (int i = size-1; i > 0; i--) if (addressLeaf(i)) return i;
    stop("No leaves in tree network");
    return 0;
   }

  int leaves() {return (size+1) / 2;}                                           // Number of leaves

//D1 Transmit                                                                   // Transmit a message through the network step by step

  private Integer parent(int N) {return N == 0 ? null : (N-1) / 2;}             // Index of the parent of this junction if there is one
  private Integer left  (int N) {return N * 2 + 1 < size ? N * 2 + 1 : null;}   // Index of the left child of this junction if there is one
  private Integer right (int N) {return N * 2 + 2 < size ? N * 2 + 2 : null;}   // Index of the right child of this junction if there is one

  private void clearUp(int N)                                                   // Clear source of messages sent upward through this junction
   {final int parent = parent(N);
    if (messageUp      [N] && messageUp      [parent] &&                        // Same message in parent and child means we can remove the child message
        messageUpNumber[N] == messageUpNumber[parent])
     {  messageUp      [N] = false;
     }
   }

  void copyUp(int N)                                                            // Copy a child message upward into this junction with alternating left/right priority
   {final boolean up = messageUp[N];                                            // Message at this level if any
    final Integer L  = left (N);                                                // Left child if any
    final Integer R  = right(N);                                                // Right child if any

    if (leftRightPriority && L != null)                                         // Examine the left child for a message to be sent up. To avoid always giving messages on the left-hand side priority over the right-hand side we alternate between giving the left-hand side and the right-hand side priority
     {if (!up && messageUp[L])                                                  // Left might want to send a message up
       {messageUp      [N] = messageUp      [L];
        messageUpNumber[N] = messageUpNumber[L];
        messageUpSource[N] = messageUpSource[L];
        messageUpTarget[N] = messageUpTarget[L];
        messageUpText  [N] = messageUpText  [L];
       }
     }
    else if (!leftRightPriority && R != null)                                   // Examine the right child for a message to be sent up. To avoid always giving messages on the left-hand side priority over the right-hand side we alternate between giving the left-hand side and the right-hand side priority
     {if (!up && messageUp[R])                                                  // Right might want to send a message up
       {messageUp      [N] = messageUp      [R];
        messageUpNumber[N] = messageUpNumber[R];
        messageUpSource[N] = messageUpSource[R];
        messageUpTarget[N] = messageUpTarget[R];
        messageUpText  [N] = messageUpText  [R];
       }
     }
   }

  void clearDown(int N)                                                         // Copy a parent message downward into this junction, or short-circuit an upward message if target is below
   {if (!messageDownPending[N]) return;                                         // Skip if there is no downward-seeking message pending for this junction
    messageDown       [N] = messageDownPending      [N];
    messageDownNumber [N] = messageDownPendingNumber[N];
    messageDownSource [N] = messageDownPendingSource[N];
    messageDownTarget [N] = messageDownPendingTarget[N];
    messageDownText   [N] = messageDownPendingText  [N];
    messageDownPending[N] = false;                                              // Move the message from pending to active now that downward simulation step is complete
    messageDown       [parent(N)] = false;                                      // Remove the message from parent
   }

  void clearShort(int N)                                                        // Clear any short-circuited source message from the parent
   {final Integer L = left (N);
    final Integer R = right(N);

    if ((L != null &&
         messageDown      [L] && messageUp      [N] &&                          // Clear the upward-seeking message from the parent if it was transferred to a downward-seeking branch
         messageDownNumber[L] == messageUpNumber[N])   ||
        (R != null &&
         messageDown      [R] && messageUp      [N] &&
         messageDownNumber[R] == messageUpNumber[N]))
     {messageUp[N] = false;
     }
   }

  void copyDown(int N)                                                          // Transmit messages down through this junction
   {final int P = parent(N);
    if (messageDown[P] && !messageDown[N])                                      // The parent wants to send us a message
     {if (addressDown(N, messageDownTarget[P]))                                 // The message should go down through this junction. Cache the message for the moment to prevent overruns.
       {messageDownPending      [N] = true;
        messageDownPendingNumber[N] = messageDownNumber[P];
        messageDownPendingSource[N] = messageDownSource[P];
        messageDownPendingTarget[N] = messageDownTarget[P];
        messageDownPendingText  [N] = messageDownText  [P];
       }
     }
    else if (messageUp[P])                                                      // Short-circuit upward-seeking message if its target is on this branch
     {if (addressDown(N, messageUpTarget[P]))                                   // Could the upward-seeking message short-circuit down this branch to reach its target?
       {messageDownPending      [N] = true;
        messageDownPendingNumber[N] = messageUpNumber[P];
        messageDownPendingSource[N] = messageUpSource[P];
        messageDownPendingTarget[N] = messageUpTarget[P];
        messageDownPendingText  [N] = messageUpText  [P];
       }
     }
   }

//D1 Tests                                                                      // Test the tree network

  static void test_one()
   {sayCurrentTestName();
    final TreeNetVerilog       T = new TreeNetVerilog(3);
    final StringBuilder s = new StringBuilder(); T.printCompact = false;

    T.putMessage(5, 3, 1111);

    for (T.step = 0; T.step < 4; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                            |
   2   1   *                   0     5     6  1                            |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10        1111               |
   6   2     *                 2              11                           |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                            |
   2   1   *                   0     5     6  1         1111               |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10                           |
   6   2     *                 2              11                           |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                   1111     |
   2   1   *                   0     5     6  1                            |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10                           |
   6   2     *                 2              11                           |
Jnct  Level Step:    3        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                            |
   2   1   *                   0     5     6  1                            |
   3   2     *                 1              00                  1111     |
   4   2     *                 1              01                           |
   5   2     *                 2              10                           |
   6   2     *                 2              11                           |
""");
    ok(T.leaves(),    4);
    ok(T.firstLeaf(), 3);
    ok(T.lastLeaf(),  6);
    ok(T.addressLeaf(1),  false);
    ok(T.addressLeaf(5),  true);
   }

  static void test_two()
   {sayCurrentTestName();
    final TreeNetVerilog       T = new TreeNetVerilog(3);
    final StringBuilder s = new StringBuilder();

    T.putMessage(T.lastLeaf()-1, T.firstLeaf(),   1111);
    T.putMessage(T.lastLeaf(),   T.firstLeaf()+1, 2222);

    for (T.step = 0; T.step < 8; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   5   2     *                 2              10        1111               |
   6   2     *                 2              11        2222               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         1111               |
   6   2     *                 2              11        2222               |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   1111     |
   6   2     *                 2              11        2222               |
Jnct  Level Step:    3        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1              00                  1111     |
   6   2     *                 2              11        2222               |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         2222               |
   3   2     *                 1              00                  1111     |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         2222               |
   3   2     *                 1              00                  1111     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   2222     |
   3   2     *                 1              00                  1111     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1              00                  1111     |
   4   2     *                 1              01                  2222     |
""");
   }

  static void test_swap()
   {sayCurrentTestName();
    final TreeNetVerilog T = new TreeNetVerilog(2);
    T.printCompact  = false;
    final StringBuilder s = new StringBuilder();

    T.putMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.putMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0         1111               |
   2   1   *                   0              1         2222               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                            |
   2   1   *                   0              1         2222      1111     |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
""");
   }

  static void test_reverse2()
   {sayCurrentTestName();
    final TreeNetVerilog       T = new TreeNetVerilog(4);
    final StringBuilder s = new StringBuilder();

    T.putMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.putMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    for (T.step = 0; T.step < 9; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000       1111               |
  14   3       *               6              111       2222               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        1111               |
  14   3       *               6              111       2222               |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        1111               |
   6   2     *                 2    13    14  11        2222               |
Jnct  Level Step:    3        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111               |
   6   2     *                 2    13    14  11        2222               |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111               |
   2   1   *                   0     5     6  1         2222               |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         2222      1111     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   2222     |
   6   2     *                 2    13    14  11                  1111     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00                  2222     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000                 2222     |
  14   3       *               6              111                 1111     |
""");
   }

  static void test_reverse8()
   {sayCurrentTestName();
    final TreeNetVerilog       T = new TreeNetVerilog(4);
    final StringBuilder s = new StringBuilder();
    final int F = T.firstLeaf(), L = T.lastLeaf(), N = T.leaves();
    for (int i = 1; i <= N; i++) T.putMessage(F+i-1, L-i+1, 1000*i+100*i+10*i+i);

    for (T.step = 0; T.step < 16; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000       1111               |
   8   3       *               3              001       2222               |
   9   3       *               4              010       3333               |
  10   3       *               4              011       4444               |
  11   3       *               5              100       5555               |
  12   3       *               5              101       6666               |
  13   3       *               6              110       7777               |
  14   3       *               6              111       8888               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        1111               |
   4   2     *                 1     9    10  01        3333               |
   5   2     *                 2    11    12  10        5555               |
   6   2     *                 2    13    14  11        7777               |
   8   3       *               3              001       2222               |
  10   3       *               4              011       4444               |
  12   3       *               5              101       6666               |
  14   3       *               6              111       8888               |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         3333               |
   2   1   *                   0     5     6  1         7777               |
   3   2     *                 1     7     8  00        1111               |
   5   2     *                 2    11    12  10        5555               |
   8   3       *               3              001       2222               |
  10   3       *               4              011       4444               |
  12   3       *               5              101       6666               |
  14   3       *               6              111       8888               |
Jnct  Level Step:    3        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         7777      3333     |
   3   2     *                 1     7     8  00        1111               |
   5   2     *                 2    11    12  10        5555               |
   8   3       *               3              001       2222               |
  10   3       *               4              011       4444               |
  12   3       *               5              101       6666               |
  14   3       *               6              111       8888               |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   7777     |
   3   2     *                 1     7     8  00        1111               |
   4   2     *                 1     9    10  01        4444               |
   5   2     *                 2    11    12  10        5555      3333     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222               |
  12   3       *               5              101       6666               |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111               |
   2   1   *                   0     5     6  1         5555               |
   3   2     *                 1     7     8  00                  7777     |
   4   2     *                 1     9    10  01        4444               |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222               |
  12   3       *               5              101       6666      3333     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111      5555     |
   3   2     *                 1     7     8  00        2222               |
   4   2     *                 1     9    10  01        4444               |
   5   2     *                 2    11    12  10        6666               |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001                 7777     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         6666      1111     |
   3   2     *                 1     7     8  00        2222               |
   4   2     *                 1     9    10  01        4444      5555     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001                 7777     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         4444      6666     |
   3   2     *                 1     7     8  00        2222               |
   6   2     *                 2    13    14  11        8888      1111     |
   8   3       *               3              001                 7777     |
  10   3       *               4              011                 5555     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    9        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1                   4444     |
   3   2     *                 1     7     8  00        2222               |
   4   2     *                 1     9    10  01                  6666     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001                 7777     |
  10   3       *               4              011                 5555     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   10        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         8888               |
   3   2     *                 1     7     8  00        2222               |
   5   2     *                 2    11    12  10                  4444     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   11        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         2222               |
   2   1   *                   0     5     6  1         8888               |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         2222      8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1                   2222     |
   3   2     *                 1     7     8  00                  8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   14        Up  Left Right  Addr      Up______  Down____ |
   6   2     *                 2    13    14  11                  2222     |
   7   3       *               3              000                 8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   15        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000                 8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  13   3       *               6              110                 2222     |
  14   3       *               6              111                 1111     |
""");
   }

  static void test_short()
   {sayCurrentTestName();
    final TreeNetVerilog T = new TreeNetVerilog(4);
    final StringBuilder s = new StringBuilder();

    T.putMessage(T.lastLeaf(),   T.lastLeaf()-1, 1111);
    T.putMessage(T.lastLeaf()-1, T.lastLeaf(),   2222);
    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T);
      T.transmit();
     }
    //stop(s);
    ok(""+s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110       2222               |
  14   3       *               6              111       1111               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
  14   3       *               6              111       1111      2222     |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
""");
   }

  static void test_sequence()
   {sayCurrentTestName();
    final TreeNetVerilog T  = new TreeNetVerilog(4);
    final int Source = T.lastLeaf(), Target = T.firstLeaf(), Steps = 28;
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
    test_reverse2();
    test_reverse8();
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
