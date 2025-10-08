//------------------------------------------------------------------------------
// A tree network that connects leaf pairs via branches in logarithmic time
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree as a silicon chip

import java.util.*;

class TreeNet extends Chip                                                      // A tree network that connects leaf pairs via branches in logarithmic time, allowing us to compose a large chip as a networked collection of smaller chips. The network is driven by a CPU with specialized SIMD instructions to process each node in the network in parallel.
 {final static boolean javaOnly = false;                                        // Ignore verilog if true - used during testing to establish Java base case
  final int               size;                                                 // Total number of junctions in the binary tree, including root and leaves
  final int        addressWidth =  8;                                           // The width of a network address
  final int        messageWidth = 24;                                           // The width of a network message
  final Process               P = new Process("main");                          // Process in which the network runs
  final BitSet  []address;                                                      // Address in branch path steering format
  final BitSet  []addressMask;                                                  // Mask for the corresponding address
  final Process.Register Address;                                               // Arrayed register of addresses in branch path steering format for each junction
  final Process.Register AddressMask;                                           // Arrayed register of address masks for each junction

  final boolean []messageUp;                                                    // Message waiting to be sent upward through the tree network
  final boolean []messageDown;                                                  // Message waiting to be sent downward through the tree network
  final boolean []messageDownPending;                                           // A downward-seeking message is cached here during simulation to prevent overruns
  final Process.Register MessageUp;                                             // An upward-seeking message
  final Process.Register MessageDown;                                           // A downward-seeking message
  final Process.Register MessageDownPending;                                    // A pending downward seeking message

  final int     []messageUpNumber;                                              // The unique number of the upward-seeking message
  final int     []messageUpSource;                                              // The address of the sending source leaf of the upward-seeking message
  final int     []messageUpTarget;                                              // The address of the receiving target leaf of the upward-seeking message
  final int     []messageUpText;                                                // The text of the upward-seeking message
  final Process.Register MessageUpNumber;                                       // The unique number of the upward-seeking message as a register
  final Process.Register MessageUpSource;                                       // The address of the sending source leaf of the upward-seeking message as a register
  final Process.Register MessageUpTarget;                                       // The address of the receiving target leaf of the upward-seeking message as a register
  final Process.Register MessageUpText;                                         // The text of the upward-seeking message as a register

  final int     []messageDownNumber;                                            // The unique number of the downward-seeking message
  final int     []messageDownSource;                                            // The address of the sending source leaf of the downward-seeking message
  final int     []messageDownTarget;                                            // The address of the receiving target leaf of the downward-seeking message
  final int     []messageDownText;                                              // The text of the downward-seeking message
  final Process.Register MessageDownNumber;                                     // The unique number of the downward-seeking message as a register
  final Process.Register MessageDownSource;                                     // The address of the sending source leaf of the downward-seeking message as a register
  final Process.Register MessageDownTarget;                                     // The address of the receiving target leaf of the downward-seeking message as a register
  final Process.Register MessageDownText;                                       // The text of the downward-seeking message as a register

  final int     []messageDownPendingNumber;                                     // The unique number of the pending message
  final int     []messageDownPendingSource;                                     // The address of the sending source leaf of the pending message
  final int     []messageDownPendingTarget;                                     // The address of the receiving target leaf of the pending message
  final int     []messageDownPendingText;                                       // The text of the pending message
  final Process.Register MessageDownPendingNumber;                              // The unique number of the pending message as a register
  final Process.Register MessageDownPendingSource;                              // The address of the sending source leaf of the pending message as a register
  final Process.Register MessageDownPendingTarget;                              // The address of the receiving target leaf of the pending message as a register
  final Process.Register MessageDownPendingText;                                // The text of the pending message as a register

  boolean leftRightPriority = true;                                             // Alternate the priority of moving up on the left or right so that each child junction has equal access to its parent junction
  boolean      printCompact = true;                                             // Print the network trace in a compact format if true
  int        messageNumber;                                                     // A unique number generator used to label messages
  int                 step;                                                     // The tree network execution step
  final Process.Register LeftRightPriority;                                     // Alternate the priority of moving up on the left or right so that each child junction has equal access to its parent junction as a register
  final Process.Register MessageNumber;                                         // A unique number generator used to label messages as a register
  final Process.Register Step;                                                  // The current step in the simulation of the message flow
  final Process.Register putMessage = P.register("putMessage", 1);              // The tree network execution step as a register

//D1 Construction                                                               // Construct a tree network

  TreeNet(int Size)                                                             // Create the tree network.  The number of leaves will be 2**(Size-1)
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
    addressMask              = new BitSet[size];                                // The mask for the corresponding address showing how wide the address is

    Address                  = P.register("address",      addressWidth, size);
    AddressMask              = P.register("addressMask",  addressWidth, size);
    MessageUp                = P.register("messageUp", 1, size);
    MessageDown              = P.register("messageDown", 1, size);
    MessageDownPending       = P.register("messageDownPending", 1, size);

    MessageUpNumber          = P.register("MessageUpNumber", addressWidth, size);
    MessageUpSource          = P.register("MessageUpSource", addressWidth, size);
    MessageUpTarget          = P.register("MessageUpTarget", addressWidth, size);
    MessageUpText            = P.register("MessageUpText",   messageWidth, size);

    MessageDownNumber        = P.register("MessageDownNumber", addressWidth, size);
    MessageDownSource        = P.register("MessageDownSource", addressWidth, size);
    MessageDownTarget        = P.register("MessageDownTarget", addressWidth, size);
    MessageDownText          = P.register("MessageDownText",   messageWidth, size);

    MessageDownPendingNumber = P.register("MessageDownPendingNumber", addressWidth, size);
    MessageDownPendingSource = P.register("MessageDownPendingSource", addressWidth, size);
    MessageDownPendingTarget = P.register("MessageDownPendingTarget", addressWidth, size);
    MessageDownPendingText   = P.register("MessageDownPendingText",   messageWidth, size);

    LeftRightPriority        = P.register("LeftRightPriority", 1);
    MessageNumber            = P.register("MessageNumber",    32);
    Step                     = P.register("Step",             32);

    for (int i = 0; i < size; i++) addressPath(i);                              // Create the addresses in the tree network
   }

  public String print()                                                         // Print the tree network
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

  String printV()                                                               // Print the tree network
   {final StringJoiner s = new StringJoiner("\n", "", "\n");

    s.add(String.format("Jnct  Level Step: %4d        Up  Left Right  Addr      Up______  Down____ |", step));

    for (int i = 0; i < size; i++)                                              // Each junction in the tree network
     {final Integer p = parent(i), l = left(i), r = right(i);                   // Relationship with other junctions
      final String  P = p == null ? "    " : String.format("%4d", p);
      final String  L = l == null ? "    " : String.format("%4d", l);
      final String  R = r == null ? "    " : String.format("%4d", r);
      final String  n = "  ".repeat(addressLevel(i))+"*"+
                        "  ".repeat(8-addressLevel(i));

      final boolean u = MessageUp  .registerGet(i) > 0;                         // Message detail
      final boolean d = MessageDown.registerGet(i) > 0;
      final String  U = u ? ""+MessageUpText  .registerGet(i) : "";
      final String  D = d ? ""+MessageDownText.registerGet(i) : "";

      if (!printCompact || u || d)                                              // Print if there has been activity on this junction or if all junctions are being printed
       {s.add(String.format("%4d  %2d %s  %s  %s  %s  %-8s  %-8s  %-8s |",
          i, addressLevel(i), n, P, L, R, addressPrint(i), U, D));
       }
     }
    return ""+s;                                                                // Printed state of tree network at this point in time
   }

  void transmit()                                                               // Transmit each message one step through the tree network
   {for (int i = 0; i < size; i++) copyUp (i);                                  // Copy a source message up one step so that it is closer to the target
    for (int i = size; i > 1; i--) clearUp(i-1);                                // Clear the source of an upward-moving message - the root cannot be such a source. Clearing in reverse prevents a cleared field from being reused later in the same cycle

    messageDown      [0] = messageUp      [0];                                  // Transfer the message from the upward-seeking side of the tree to the downward-seeking side
    messageDownNumber[0] = messageUpNumber[0];
    messageDownSource[0] = messageUpSource[0];
    messageDownTarget[0] = messageUpTarget[0];
    messageDownText  [0] = messageUpText  [0];
    messageUp        [0] = false;                                               // Remove the message from upward-seeking side now that it has been transferred to the downward-seeking side of the tree network

    for(int i = 1; i < size; i++) copyDown  (i);                                // Copy a source message down one step so that it is closer to the target
    for(int i = 0; i < size; i++) clearCopyDown (i);                            // Clear an upward or downward-moving message transferred to downward pending from its parent - the root cannot be such a source
    for(int i = 1; i < size; i++) copyDownPending (i);                          // Clear the source of a downward-moving message - the root cannot be such a source
    for(int i = 1; i < size; i++) clearShort(i);                                // Clear the source of a short-circuited downward-moving message - the root cannot be such a source
    leftRightPriority = !leftRightPriority;                                     // Alternate left/right upward priority
   }

  void Transmit()                                                               // Transmit each message one step through the tree network
   {P.new Instruction()                                                         // Copy a source message up one step so that it is closer to the target
     {void action()
       {for (int i = 0; i < size; i++) CopyUp (i);
       }
      void verilog(Verilog v)
       {for (int i = 0; i < size; i++) CopyUp (v, i);
       }
     };
    P.new Instruction()                                                         // Clear the source of an upward-moving message - the root cannot be such a source. clearing in reverse prevents a cleared field from being reused later in the same cycle
     {void action()
       {for (int i = size; i > 1; i--) ClearUp(i-1);
       }
      void verilog(Verilog v)
       {for (int i = size; i > 1; i--) ClearUp(v, i-1);
       }
     };

    P.new Instruction()                                                         // Transfer the message from the upward-seeking side of the tree to the downward-seeking side
     {void action()
       {MessageDown      .copy(0, MessageUp      , 0);
        MessageDownNumber.copy(0, MessageUpNumber, 0);
        MessageDownSource.copy(0, MessageUpSource, 0);
        MessageDownTarget.copy(0, MessageUpTarget, 0);
        MessageDownText  .copy(0, MessageUpText  , 0);
       }
      void verilog(Verilog v)
       {MessageDown      .copy(v, 0, MessageUp      , 0);                       // Transfer the message from the upward-seeking side of the tree to the downward-seeking side
        MessageDownNumber.copy(v, 0, MessageUpNumber, 0);
        MessageDownSource.copy(v, 0, MessageUpSource, 0);
        MessageDownTarget.copy(v, 0, MessageUpTarget, 0);
        MessageDownText  .copy(v, 0, MessageUpText  , 0);
       }
     };

    P.new Instruction()                                                         // Remove the message from upward-seeking side now that it has been transferred to the downward-seeking side of the tree network
     {void action()
       {MessageUp.registerSet(0, 0);
       }
      void verilog(Verilog v)
       {MessageUp.registerSet(v, 0, 0);
       }
     };

    P.new Instruction()                                                         // Copy a source message down one step so that it is closer to the target
     {void action()
       {for(int i = 1; i < size; i++) CopyDown  (i);
       }
      void verilog(Verilog v)
       {for(int i = 1; i < size; i++) CopyDown  (v, i);
       }
     };
    P.new Instruction()                                                         // Clear the source of a downward-moving message - the root cannot be such a source
     {void action()
       {for(int i = 0; i < size; i++) ClearCopyDown (i);
       }
      void verilog(Verilog v)
       {for(int i = 0; i < size; i++) ClearCopyDown (v, i);
       }
     };
    P.new Instruction()                                                         // Copy pending downward-seeking message to main line
     {void action()
       {for(int i = 1; i < size; i++) CopyDownPending (i);
       }
      void verilog(Verilog v)
       {for(int i = 1; i < size; i++) CopyDownPending (v, i);
       }
     };
    P.new Instruction()                                                         // Clear the source of a short-circuited downward-moving message - the root cannot be such a source
     {void action()
       {for(int i = 1; i < size; i++) ClearShort(i);
       }
      void verilog(Verilog v)
       {for(int i = 1; i < size; i++) ClearShort(v, i);
       }
     };
    LeftRightPriority.Not();                                                    // Alternate left/right upward priority
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

  void PutMessage(Process.Register Result, int Source, int Target, int Text)    // Add a new message at the indicated leaf if possible and return true else false
   {if (MessageUp.registerGet(Source) > 0)
     {Result.zero();                                                            // The message was not added
     }
    else
     {MessageUp      .registerSet(1,             Source);                       // Add the message
      MessageUpNumber.copy       (Source, MessageNumber);                       // Generate a unique message number for each message
      MessageUpSource.registerSet(Source       , Source);                       // Source address
      MessageUpTarget.registerSet(Target       , Source);                       // Target address
      MessageUpText  .registerSet(Text         , Source);                       // Text of message
      MessageNumber  .inc();
      Result.one();                                                             // Successfully added message to tree network
     }
   }

  Process.Register PutMessage(int Source, int Target, int Text)                 // Add a new message at the indicated leaf if possible and return true else false
   {final Process.Register result = putMessage;
    P.new Instruction()
     {void action()
       {PutMessage(result, Source, Target, Text);
       }
      void verilog(Verilog v)
       {v.new If (MessageUp.registerName(Source))
         {void Then()
           {result.zero(v);                                                     // The message was not added
           }
          void Else()
           {MessageUp      .registerSet(v, 1,             Source);              // Add the message
            MessageUpNumber.copy       (v, Source, MessageNumber);              // Generate a unique message number for each message
            MessageUpSource.registerSet(v, Source       , Source);              // Source address
            MessageUpTarget.registerSet(v, Target       , Source);              // Target address
            MessageUpText  .registerSet(v, Text         , Source);              // Text of message
            MessageNumber  .inc(v);
            result.one(v);                                                      // Successfully added message to tree network
           }
         };
       }
     };
    return result;
   }

  Process.Register PutMessage                                                   // Add a new message at the indicated leaf if possible and return true else false
   (Process.Register Source, Process.Register Target, Process.Register Text)
   {final Process.Register result = putMessage;
    P.new Instruction()
     {void action()
       {PutMessage(result, Source.registerGet(), Target.registerGet(), Text.registerGet());
       }
      void verilog(Verilog v)
       {v.new If (MessageUp.registerName(Source.registerName()))                // Return if message input still full
         {void Then()
           {result.zero(v);                                                     // The message was not added
           }
          void Else()                                                           // Room for another message
           {MessageUp      .registerSet(v, 1,             Source);              // Add the message
            MessageUpNumber.copyIt     (v, Source, MessageNumber);              // Generate a unique message number for each message
            MessageUpSource.copyIt     (v, Source,        Source);              // Source address
            MessageUpTarget.copyIt     (v, Source,        Target);              // Target address
            MessageUpText  .copyIt     (v, Source,        Text);                // Text of message
            MessageNumber  .inc(v);
            result.one(v);                                                      // Successfully added message to tree network
           }
         };
       }
     };
    return result;
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
    final Process.Register Source = P.register("Source", addressWidth);         // Source address
    final Process.Register Target = P.register("Target", addressWidth);         // Target address
    final Process.Register Text   = P.register("Text",   messageWidth);         // Text of message
    Process.Register get(Process.Register Leaf)                                 // Get a message from the specified junction and return its validity
     {Valid  .CopyIs(MessageDown      , Leaf);                                  // Whether the message is valid
      Source .CopyIs(MessageDownSource, Leaf);                                  // Source address
      Target .CopyIs(MessageDownTarget, Leaf);                                  // Target address
      Text   .CopyIs(MessageDownText  , Leaf);                                  // Text of message
                     MessageDown.RegisterSet(0, Leaf);                          // Remove the message
      return Valid;
     }
   }

//D1 Addressing                                                                 // Guidance path from the root to a branch or leaf

  private int addressLevel(int Index) {return logTwo(prevPowerOfTwo(1+Index));} // The level of the indexed junction

  private void addressPath(int Index)                                           // Initialize the path to this address of a junction in the tree network
   {final BitSet B = new BitSet();                                              // Bit set representing the path to this junction from the root
    for (int N = Index+1, i = addressLevel(Index); N > 1; N /= 2, --i)
     {if (N % 2 == 1) B.set(i-1);
     }
    final int M = (1<<addressLevel(Index))-1;                                   // Mask for this address
    address    [Index] = B;                                                     // Path from zero to this address as a bit set
    addressMask[Index] = intToBitSet(M);                                        // Mask for this address
    Address    .RegisterSet(bitSetToInt(B), Index);                             // Path from zero to this address as an int
    AddressMask.RegisterSet(M, Index);                                          // Mask for this address
   }

  private boolean addressDown(int Source, int Target)                           // Is this an address that can be descended through towards the target
   {final BitSet s = (BitSet)address[Source].clone();
    final BitSet t = (BitSet)address[Target].clone();
    s.and(addressMask[Source]);
    t.and(addressMask[Source]);
    return s.equals(t);
   }

  private String AddressDown(int Source, String Target)                         // Is this an address that can be descended through towards the target
   {final String s = Address.registerName(Source);
    final String t = Address.registerName(Target);
    final String S = AddressMask.registerName(Source);
    return "("+s+" & "+S+") == ("+t+" & "+S+")";
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

  void copyUp(int N)                                                            // Copy a child message upward into this junction with alternating left/right priority - pure Java version
   {final Integer L = left (N);                                                 // Left child if any
    final Integer R = right(N);                                                 // Right child if any
    if (L == null || R == null) return;                                         // Assume a full tree

    final boolean up = messageUp[N];                                            // Message at this level if any
    final boolean gl = !up && messageUp[L];                                     // Go left
    final boolean gr = !up && messageUp[R];                                     // Go right

    class Left                                                                  // Move the left child up
     {Left()
       {messageUp      [N] = messageUp      [L];
        messageUpNumber[N] = messageUpNumber[L];
        messageUpSource[N] = messageUpSource[L];
        messageUpTarget[N] = messageUpTarget[L];
        messageUpText  [N] = messageUpText  [L];
       }
     }

    class Right                                                                 // Move the right child up
     {Right()
       {messageUp      [N] = messageUp      [R];
        messageUpNumber[N] = messageUpNumber[R];
        messageUpSource[N] = messageUpSource[R];
        messageUpTarget[N] = messageUpTarget[R];
        messageUpText  [N] = messageUpText  [R];
       }
     }

    if (leftRightPriority)                                                      // Alternate priority of left or right child
     {if (gl) new Left(); else if (gr) new Right();
     }
    else
     {if (gr) new Right(); else if (gl) new Left();
     }
   }

  void CopyUp(int N)                                                            // Copy a child message upward into this junction with alternating left/right priority - Java finite state machine
   {final Integer L = left (N);                                                 // Left child if any
    final Integer R = right(N);                                                 // Right child if any
    if (L == null || R == null) return;                                         // Assume a full tree

    final boolean up = MessageUp.registerGet(N) > 0;                            // Message at this level if any
    final boolean gl = !up && MessageUp.registerGet(L) > 0;                     // Left might want to send a message up
    final boolean gr = !up && MessageUp.registerGet(R) > 0;                     // Right might want to send a message up

    class Left                                                                  // Move the left child up
     {Left()
       {MessageUp      .copy(N, MessageUp      , L);
        MessageUpNumber.copy(N, MessageUpNumber, L);
        MessageUpSource.copy(N, MessageUpSource, L);
        MessageUpTarget.copy(N, MessageUpTarget, L);
        MessageUpText  .copy(N, MessageUpText  , L);
       }
     }

    class Right                                                                 // Move the right child up
     {Right()
       {MessageUp      .copy(N, MessageUp      , R);
        MessageUpNumber.copy(N, MessageUpNumber, R);
        MessageUpSource.copy(N, MessageUpSource, R);
        MessageUpTarget.copy(N, MessageUpTarget, R);
        MessageUpText  .copy(N, MessageUpText  , R);
       }
     }

    if (LeftRightPriority.registerGet() > 0)                                    // Alternate priority of left or right child
     {if (gl) new Left(); else if (gr) new Right();
     }
    else
     {if (gr) new Right(); else if (gl) new Left();
     }
   }

  void CopyUp(Verilog v, int N)                                                 // Copy a child message upward into this junction with alternating left/right priority - generated Verilog version
   {final Integer L = left (N);                                                 // Left child if any
    final Integer R = right(N);                                                 // Right child if any
    if (L == null || R == null) return;                                         // Assume a full tree

    final String up = MessageUp        .registerName(N);                        // Upward-seeking message available at this level
    final String lr = LeftRightPriority.registerName();                         // Left/right priority for choosing the next message if both children can supply a message
    final String lm = MessageUp.registerName(L);                                // Left child
    final String rm = MessageUp.registerName(R);                                // Right child
    final String gl = "(!"+up +" && " + lm+")";                                 // Left might want to send a message up
    final String gr = "(!"+up +" && " + rm+")";                                 // Right might want to send a message up

    class Left                                                                  // Move the left child up
     {Left()
       {MessageUp      .copy(v, N, MessageUp      , L);
        MessageUpNumber.copy(v, N, MessageUpNumber, L);
        MessageUpSource.copy(v, N, MessageUpSource, L);
        MessageUpTarget.copy(v, N, MessageUpTarget, L);
        MessageUpText  .copy(v, N, MessageUpText  , L);
       }
     }

    class Right                                                                 // Move the right child up
     {Right()
       {MessageUp      .copy(v, N, MessageUp      , R);
        MessageUpNumber.copy(v, N, MessageUpNumber, R);
        MessageUpSource.copy(v, N, MessageUpSource, R);
        MessageUpTarget.copy(v, N, MessageUpTarget, R);
        MessageUpText  .copy(v, N, MessageUpText  , R);
       }
     };

    v.new If (lr)                                                               // Alternate priority
     {void Then()
       {v.new If (gl)                                                           // Left might want to send a message up
         {void Then()
           {new Left();
           }
          void Else()
           {v.new If (gr)                                                       // Right might want to send a message up
             {void Then()
               {new Right();
               }
             };
           }
         };
       }

      void Else()
       {v.new If (gr)                                                           // Left might want to send a message up
         {void Then()
           {new Right();
           }
          void Else()
           {v.new If (gl)                                                       // Right might want to send a message up
             {void Then()
               {new Left();
               }
             };
           }
         };
       }
     };
   }

  private void clearUp(int N)                                                   // Clear the source of messages sent upward through this junction - pure Java version
   {final int parent = parent(N);
    if (messageUp      [N] && messageUp      [parent] &&                        // Same message in parent and child means we can remove the child message
        messageUpNumber[N] == messageUpNumber[parent])
     {  messageUp      [N] = false;
     }
   }

  private void ClearUp(int N)                                                   // Clear the source of messages sent upward through this junction - Java finite state machine
   {final int parent = parent(N);

    if (MessageUp      .registerGet(N)      > 0 &&                              // Same message in parent and child means we can remove the child message
        MessageUp      .registerGet(parent) > 0 &&
        MessageUpNumber.registerGet(N) ==
        MessageUpNumber.registerGet(parent))
     {  MessageUp      .registerSet(0, N);
     }
   }

  private void ClearUp(Verilog v, int N)                                        // Clear the source of messages sent upward through this junction - generated Verilog version
   {final int parent = parent(N);

    v.new If
       (MessageUp      .registerName(N)      +" && "+                           // Same message in parent and child means we can remove the child message
        MessageUp      .registerName(parent) +" && "+
        MessageUpNumber.registerName(N)      +" == "+
        MessageUpNumber.registerName(parent))
     {void Then()
       {MessageUp      .registerSet(v, 0, N);
       }
     };
   }

  void copyDown(int N)                                                          // Transmit downward-seeking and short-circuited messages down through this junction - pure Java version
   {final int P = parent(N);
    if (messageDown[P] && !messageDown[N] &&                                    // The parent wants to send us a message
        addressDown(N, messageDownTarget[P]))                                   // The message should go down through this junction. Cache the message for the moment to prevent overruns.
     {messageDownPending      [N] = true;
      messageDownPendingNumber[N] = messageDownNumber[P];
      messageDownPendingSource[N] = messageDownSource[P];
      messageDownPendingTarget[N] = messageDownTarget[P];
      messageDownPendingText  [N] = messageDownText  [P];
     }
    else if (messageUp[P] &&  addressDown(N, messageUpTarget[P]))               // Short-circuit the upward-seeking message if its target is on this branch
     {messageDownPending      [N] = true;
      messageDownPendingNumber[N] = messageUpNumber[P];
      messageDownPendingSource[N] = messageUpSource[P];
      messageDownPendingTarget[N] = messageUpTarget[P];
      messageDownPendingText  [N] = messageUpText  [P];
     }
   }

  void CopyDown(int N)                                                          // Transmit downward-seeking and short-circuited messages down through this junction - Java finite state machine
   {final int P = parent(N);
    if (MessageDown.registerGet(P) >  0 &&                                      // The parent wants to send us a message
        MessageDown.registerGet(N) == 0 &&
        addressDown(N, MessageDownTarget.registerGet(P)))                       // The message should go down through this junction. Cache the message for the moment to prevent overruns.
     {MessageDownPending      .registerSet(1, N);
      MessageDownPendingNumber.copy(N, MessageDownNumber, P);
      MessageDownPendingSource.copy(N, MessageDownSource, P);
      MessageDownPendingTarget.copy(N, MessageDownTarget, P);
      MessageDownPendingText  .copy(N, MessageDownText  , P);
     }
    else if (MessageUp.registerGet(P) > 0 &&                                    // Short-circuit the upward-seeking message if its target is on this branch
          addressDown(N, MessageUpTarget.registerGet(P)))                       // Could the upward-seeking message short-circuit down this branch to reach its target?
     {MessageDownPending      .registerSet(1, N);
      MessageDownPendingNumber.copy(N, MessageUpNumber, P);
      MessageDownPendingSource.copy(N, MessageUpSource, P);
      MessageDownPendingTarget.copy(N, MessageUpTarget, P);
      MessageDownPendingText  .copy(N, MessageUpText  , P);
     }
   }

  void CopyDown(Verilog v, int N)                                               // Transmit downward-seeking and short-circuited messages down through this junction - generated Verilog version
   {final int P = parent(N);
    final String pd = MessageDown.registerName(P);                              // Message moving down
    final String pu = MessageUp  .registerName(P);                              // Message moving up that we might be able to short-circuit
    final String cr = MessageDown.registerName(N);                              // Current message
    final String ad = AddressDown(N, MessageDownTarget.registerName(P));        // The message should go down through this junction. Cache the message for the moment to prevent overruns.
    final String au = AddressDown(N, MessageUpTarget  .registerName(P));        // Could the upward-seeking message short-circuit down this branch to reach its target?

    v.new If (pd+" && !"+cr+" && "+ad)                                          // The parent wants to send us a message
     {void Then()
       {MessageDownPending      .registerSet(v, 1, N);
        MessageDownPendingNumber.copy(v, N, MessageDownNumber, P);
        MessageDownPendingSource.copy(v, N, MessageDownSource, P);
        MessageDownPendingTarget.copy(v, N, MessageDownTarget, P);
        MessageDownPendingText  .copy(v, N, MessageDownText  , P);
       }
      void Else()
       {v.new If (pu+" && "+au)                                                 // Short-circuit the upward-seeking message if its target is on this branch
         {void Then()
           {MessageDownPending      .registerSet(v, 1, N);
            MessageDownPendingNumber.copy(v, N, MessageUpNumber, P);
            MessageDownPendingSource.copy(v, N, MessageUpSource, P);
            MessageDownPendingTarget.copy(v, N, MessageUpTarget, P);
            MessageDownPendingText  .copy(v, N, MessageUpText  , P);
           }
         };
       }
     };
   }

  void clearCopyDown(int N)                                                     // Clear downward-seeking and short-circuited messages moved down into the pending message area of the left or right child - pure Java version
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    if ((messageDownPending      [L] && messageDown      [N] &&                 // Clear a downward-seeking message from the parent if it was transferred to a pending downward-seeking branch
         messageDownPendingNumber[L] == messageDownNumber[N])   ||
        (messageDownPending      [R] && messageDown      [N] &&
         messageDownPendingNumber[R] == messageDownNumber[N]))
     {messageDown[N] = false;
     }

    else if ((messageDownPending      [L] && messageUp      [N] &&              // Clear the upward-seeking message if it was short-circuited to a pending downward-seeking branch
              messageDownPendingNumber[L] == messageUpNumber[N])   ||
             (messageDownPending      [R] && messageUp      [N] &&
              messageDownPendingNumber[R] == messageUpNumber[N]))
     {messageUp[N] = false;
     }
   }

  void ClearCopyDown(int N)                                                     // Clear downward-seeking and short-circuited messages moved down into the pending message area of the left or right child - Java finite state machine
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    if ((MessageDownPending      .registerGet(L) > 0 && MessageDown      .registerGet(N) > 0 &&   // Clear a downward-seeking message from the parent if it was transferred to a downward-seeking branch
         MessageDownPendingNumber.registerGet(L)     == MessageDownNumber.registerGet(N))    ||
        (MessageDownPending      .registerGet(R) > 0 && MessageDown      .registerGet(N) > 0 &&
         MessageDownPendingNumber.registerGet(R)     == MessageDownNumber.registerGet(N)))
     {           MessageDown.registerSet(0, N);
     }
    else if ((MessageDownPending      .registerGet(L) > 0 && MessageUp      .registerGet(N) > 0 &&  // Clear an upward-seeking message from the parent if it was short-circuited to a downward-seeking branch
              MessageDownPendingNumber.registerGet(L)     == MessageUpNumber.registerGet(N))    ||
             (MessageDownPending      .registerGet(R) > 0 && MessageUp      .registerGet(N) > 0 &&
              MessageDownPendingNumber.registerGet(R)     == MessageUpNumber.registerGet(N)))
     {           MessageUp.registerSet(0, N);
     }
   }

  void ClearCopyDown(Verilog v, int N)                                          // Clear downward-seeking and short-circuited messages moved down into the pending message area of the left or right child - generated Verilog version
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    v.new If                                                                    // Clear any downward-seeking message from the parent
     ("("+MessageDownPending      .registerName(L)+" && "+MessageDown      .registerName(N)+" && "+
          MessageDownPendingNumber.registerName(L)+" == "+MessageDownNumber.registerName(N)+") ||"+
      "("+MessageDownPending      .registerName(R)+" && "+MessageDown      .registerName(N)+" && "+
          MessageDownPendingNumber.registerName(R)+" == "+MessageDownNumber.registerName(N)+")")
     {void Then()
       {MessageDown.registerSet(v, 0, N);
       }
      void Else()
       {v.new If                                                                // Clear any upward-seeking message from the parent if it was short-circuited down to the left or right
         ("("+MessageDownPending      .registerName(L)+" && "+MessageUp      .registerName(N)+" && "+
              MessageDownPendingNumber.registerName(L)+" == "+MessageUpNumber.registerName(N)+") ||"+
          "("+MessageDownPending      .registerName(R)+" && "+MessageUp      .registerName(N)+" && "+
              MessageDownPendingNumber.registerName(R)+" == "+MessageUpNumber.registerName(N)+")")
         {void Then()
           {MessageUp.registerSet(v, 0, N);
           }
         };
       }
     };
   }

  void copyDownPending(int N)                                                   // Copy a parent message downward into this junction, or short-circuit an upward message if the target is below - pure Java version
   {if (!messageDownPending[N]) return;                                         // Skip if there is no downward-seeking message pending for this junction
    messageDown       [N] = messageDownPending      [N];
    messageDownNumber [N] = messageDownPendingNumber[N];
    messageDownSource [N] = messageDownPendingSource[N];
    messageDownTarget [N] = messageDownPendingTarget[N];
    messageDownText   [N] = messageDownPendingText  [N];
    messageDownPending[N] = false;                                              // Move the message from pending to active now that the downward simulation step is complete
   }

  void CopyDownPending(int N)                                                   // Copy a pending message into this junction - Java finite state machine
   {if (MessageDownPending.registerGet(N) == 0) return;                         // Skip if there is no downward-seeking message pending for this junction
    MessageDown       .copy(N, MessageDownPending      , N);
    MessageDownNumber .copy(N, MessageDownPendingNumber, N);
    MessageDownSource .copy(N, MessageDownPendingSource, N);
    MessageDownTarget .copy(N, MessageDownPendingTarget, N);
    MessageDownText   .copy(N, MessageDownPendingText  , N);
    MessageDownPending.registerSet(0, N);                                       // Move the message from pending to active now that the downward simulation step is complete
   }

  void CopyDownPending(Verilog v, int N)                                        // Copy a pending message into this junction - generated Verilog version
   {v.new If (MessageDownPending.registerName(N))
     {void Then()
       {MessageDown       .copy(v, N, MessageDownPending      , N);
        MessageDownNumber .copy(v, N, MessageDownPendingNumber, N);
        MessageDownSource .copy(v, N, MessageDownPendingSource, N);
        MessageDownTarget .copy(v, N, MessageDownPendingTarget, N);
        MessageDownText   .copy(v, N, MessageDownPendingText  , N);
        MessageDownPending.registerSet(v, 0, N);                                // Move the message from pending to active now that the downward simulation step is complete
       }
     };
   }

  void clearShort(int N)                                                        // Clear any short-circuited source message from the parent - pure Java version
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    if ((messageDown      [L] && messageUp      [N] &&                          // Clear the upward-seeking message from the parent if it was transferred to a downward-seeking branch
         messageDownNumber[L] == messageUpNumber[N])   ||
        (messageDown      [R] && messageUp      [N] &&
         messageDownNumber[R] == messageUpNumber[N]))
     {messageUp[N] = false;
     }
   }

  void ClearShort(int N)                                                        // Clear any short-circuited source message from the parent - Java finite state machine
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    if                                                                          // Clear the upward-seeking message from the parent if it was transferred to a downward-seeking branch
     ((MessageDown      .registerGet(L) > 0 && MessageUp      .registerGet(N) > 0 &&
       MessageDownNumber.registerGet(L)     == MessageUpNumber.registerGet(N))    ||
      (MessageDown      .registerGet(R) > 0 && MessageUp      .registerGet(N) > 0 &&
       MessageDownNumber.registerGet(R)     == MessageUpNumber.registerGet(N)))
     { MessageUp.registerSet(0, N);
     }
   }

  void ClearShort(Verilog v, int N)                                             // Clear any short-circuited source message from the parent
   {final Integer L = left (N);                                                 // Left child
    final Integer R = right(N);                                                 // Right child
    if (L == null || R == null) return;                                         // Assume a full tree

    v.new If                                                                    // Clear the upward-seeking message from the parent if it was transferred to a downward-seeking branch
     ("("+MessageDown      .registerName(L)+" && "+MessageUp      .registerName(N)+" && "+
          MessageDownNumber.registerName(L)+" == "+MessageUpNumber.registerName(N)+") ||"+
      "("+MessageDown      .registerName(R)+" && "+MessageUp      .registerName(N)+" && "+
          MessageDownNumber.registerName(R)+" == "+MessageUpNumber.registerName(N)+")")
     {void Then()
       {MessageUp.registerSet(v, 0, N);
       }
     };
   }

//D1 Tests                                                                      // Test the tree network

  static void test_one()
   {sayCurrentTestName();
    final TreeNet T = new TreeNet(3);
    final StringBuilder  s = new StringBuilder(); T.printCompact = false;

    T.putMessage(5, 3, 1111);

    for (T.step = 0; T.step < 4; ++T.step)
     {s.append(T.print());
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

  StringBuilder test_transmission(int TransmitSteps, int ExecSteps)             // Transmit for the specified number of steps using the specified number of execution steps
   {final StringBuilder s = new StringBuilder();
    for (step = 0; step < TransmitSteps; ++step)
     {P.new Instruction()
       {void action()
         {s.append(printV());
         }
       };
      Transmit();
     }
    maxSteps = ExecSteps;
    if (javaOnly) chipRunJava(); else chipRun();
    return s;
   }

  static void test_oneV()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(3);
    T.P.processTrace = true;
    T.printCompact   = false;
    T.PutMessage(5, 3, 1111);

    final StringBuilder s = T.test_transmission(4, 1000);

    //stop(s);
    ok(s, """
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                            |
   2   1   *                   0     5     6  1                            |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10        1111               |
   6   2     *                 2              11                           |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                            |
   2   1   *                   0     5     6  1         1111               |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10                           |
   6   2     *                 2              11                           |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0     3     4  0                   1111     |
   2   1   *                   0     5     6  1                            |
   3   2     *                 1              00                           |
   4   2     *                 1              01                           |
   5   2     *                 2              10                           |
   6   2     *                 2              11                           |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
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
    final TreeNet       T = new TreeNet(3);
    final StringBuilder s = new StringBuilder();

    T.P.processTrace = true;
    T.putMessage(T.lastLeaf()-1, T.firstLeaf(),   1111);
    T.putMessage(T.lastLeaf(),   T.firstLeaf()+1, 2222);

    for (T.step = 0; T.step < 6; ++T.step)
     {s.append(T.print());
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
   2   1   *                   0     5     6  1         2222               |
   3   2     *                 1              00                  1111     |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   2222     |
   3   2     *                 1              00                  1111     |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1              00                  1111     |
   4   2     *                 1              01                  2222     |
""");
   }

  static void test_twoV()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(3);
    T.P.processTrace = true;
    T.PutMessage(T.lastLeaf()-1, T.firstLeaf(),   1111);
    T.PutMessage(T.lastLeaf(),   T.firstLeaf()+1, 2222);

    final StringBuilder s = T.test_transmission(6, 100);
    //stop(s);
    ok(""+s, """
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   5   2     *                 2              10        1111               |
   6   2     *                 2              11        2222               |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         2222               |
   5   2     *                 2              10        1111               |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   2222     |
   5   2     *                 2              10        1111               |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         1111               |
   4   2     *                 1              01                  2222     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   1111     |
   4   2     *                 1              01                  2222     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1              00                  1111     |
   4   2     *                 1              01                  2222     |
""");
   }

  static void test_swap()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(2);
    final StringBuilder s = new StringBuilder();

    T.printCompact   = false;
    T.P.processTrace = true;
    T.putMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.putMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T.print());
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

  static void test_swapV()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(2);
    T.printCompact   = false;
    T.P.processTrace = true;
    T.PutMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.PutMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    final StringBuilder s = T.test_transmission(8, 100);
    //stop(s);
    ok(""+s, """
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0         1111               |
   2   1   *                   0              1         2222               |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0         1111      2222     |
   2   1   *                   0              1                            |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   0   0 *                           1     2                               |
   1   1   *                   0              0                   2222     |
   2   1   *                   0              1                   1111     |
""");
   }

  static void test_reverse2()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.P.processTrace = true;
    T.putMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.putMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    for (T.step = 0; T.step < 7; ++T.step)
     {s.append(T.print());
      T.transmit();
     }
    //stop(s);
    ok(s, """
Jnct  Level Step:    0        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000       1111               |
  14   3       *               6              111       2222               |
Jnct  Level Step:    1        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        1111               |
   6   2     *                 2    13    14  11        2222               |
Jnct  Level Step:    2        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111               |
   2   1   *                   0     5     6  1         2222               |
Jnct  Level Step:    3        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         2222      1111     |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   2222     |
   6   2     *                 2    13    14  11                  1111     |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00                  2222     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000                 2222     |
  14   3       *               6              111                 1111     |
""");
   }

  static void test_reverse2V()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(4);
    T.P.processTrace = true;
    T.PutMessage(T.firstLeaf(), T.lastLeaf(),  1111);
    T.PutMessage(T.lastLeaf(),  T.firstLeaf(), 2222);

    final StringBuilder s = T.test_transmission(7, 1000);
    //stop(s);
    ok(s, """
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000       1111               |
  14   3       *               6              111       2222               |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        1111               |
   6   2     *                 2    13    14  11        2222               |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111               |
   2   1   *                   0     5     6  1         2222               |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111      2222     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1                   1111     |
   3   2     *                 1     7     8  00                  2222     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   6   2     *                 2    13    14  11                  1111     |
   7   3       *               3              000                 2222     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000                 2222     |
  14   3       *               6              111                 1111     |
""");
   }

  static void test_reverse8()
   {sayCurrentTestName();
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();
    final int F = T.firstLeaf(), L = T.lastLeaf(), N = T.leaves();

    T.P.processTrace = true;
    for (int i = 1; i <= N; i++) T.putMessage(F+i-1, L-i+1, 1000*i+100*i+10*i+i);

    for (T.step = 0; T.step < 13; ++T.step)
     {s.append(T.print());
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
   4   2     *                 1     9    10  01        4444               |
   5   2     *                 2    11    12  10        5555               |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222               |
  12   3       *               5              101       6666               |
Jnct  Level Step:    4        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         4444      7777     |
   3   2     *                 1     7     8  00        1111               |
   5   2     *                 2    11    12  10        5555      3333     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222               |
  12   3       *               5              101       6666               |
Jnct  Level Step:    5        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         5555      4444     |
   3   2     *                 1     7     8  00        1111      7777     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222               |
  12   3       *               5              101       6666      3333     |
Jnct  Level Step:    6        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111      5555     |
   5   2     *                 2    11    12  10        6666      4444     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001       2222      7777     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    7        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         6666      1111     |
   3   2     *                 1     7     8  00        2222               |
   4   2     *                 1     9    10  01                  5555     |
   6   2     *                 2    13    14  11        8888               |
   8   3       *               3              001                 7777     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    8        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         2222      6666     |
   6   2     *                 2    13    14  11        8888      1111     |
   8   3       *               3              001                 7777     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
Jnct  Level Step:    9        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         8888      2222     |
   4   2     *                 1     9    10  01                  6666     |
   8   3       *               3              001                 7777     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   10        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0                   8888     |
   6   2     *                 2    13    14  11                  2222     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   11        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00                  8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  13   3       *               6              110                 2222     |
  14   3       *               6              111                 1111     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
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

  static void test_reverse8V()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(4);
    T.P.processTrace = true;

    final int F = T.firstLeaf(), L = T.lastLeaf(), N = T.leaves();
    for (int i = 1; i <= N; i++) T.PutMessage(F+i-1, L-i+1, 1000*i+100*i+10*i+i);

    final StringBuilder s = T.test_transmission(13, 200);
    //stop(s);
    ok(s, """
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   7   3       *               3              000       1111               |
   8   3       *               3              001       2222               |
   9   3       *               4              010       3333               |
  10   3       *               4              011       4444               |
  11   3       *               5              100       5555               |
  12   3       *               5              101       6666               |
  13   3       *               6              110       7777               |
  14   3       *               6              111       8888               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   3   2     *                 1     7     8  00        2222               |
   4   2     *                 1     9    10  01        4444               |
   5   2     *                 2    11    12  10        6666               |
   6   2     *                 2    13    14  11        8888               |
   7   3       *               3              000       1111               |
   9   3       *               4              010       3333               |
  11   3       *               5              100       5555               |
  13   3       *               6              110       7777               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         2222               |
   2   1   *                   0     5     6  1         6666               |
   4   2     *                 1     9    10  01        4444               |
   6   2     *                 2    13    14  11        8888               |
   7   3       *               3              000       1111               |
   9   3       *               4              010       3333               |
  11   3       *               5              100       5555               |
  13   3       *               6              110       7777               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         2222      6666     |
   3   2     *                 1     7     8  00        1111               |
   4   2     *                 1     9    10  01        4444               |
   5   2     *                 2    11    12  10        5555               |
   6   2     *                 2    13    14  11        8888               |
   9   3       *               4              010       3333               |
  13   3       *               6              110       7777               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         5555      2222     |
   3   2     *                 1     7     8  00        1111               |
   4   2     *                 1     9    10  01        4444      6666     |
   6   2     *                 2    13    14  11        8888               |
   9   3       *               4              010       3333               |
  13   3       *               6              110       7777               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         4444      5555     |
   3   2     *                 1     7     8  00        1111               |
   6   2     *                 2    13    14  11        8888      2222     |
   9   3       *               4              010       3333      6666     |
  13   3       *               6              110       7777               |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         8888      4444     |
   3   2     *                 1     7     8  00        1111               |
   4   2     *                 1     9    10  01        3333      5555     |
   9   3       *               4              010                 6666     |
  13   3       *               6              110       7777      2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         3333      8888     |
   3   2     *                 1     7     8  00        1111               |
   5   2     *                 2    11    12  10                  4444     |
   6   2     *                 2    13    14  11        7777               |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  13   3       *               6              110                 2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1         7777      3333     |
   3   2     *                 1     7     8  00        1111      8888     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  13   3       *               6              110                 2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   1   1   *                   0     3     4  0         1111      7777     |
   5   2     *                 2    11    12  10                  3333     |
   7   3       *               3              000                 8888     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  13   3       *               6              110                 2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   2   1   *                   0     5     6  1                   1111     |
   3   2     *                 1     7     8  00                  7777     |
   7   3       *               3              000                 8888     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  13   3       *               6              110                 2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
   6   2     *                 2    13    14  11                  1111     |
   7   3       *               3              000                 8888     |
   8   3       *               3              001                 7777     |
   9   3       *               4              010                 6666     |
  10   3       *               4              011                 5555     |
  11   3       *               5              100                 4444     |
  12   3       *               5              101                 3333     |
  13   3       *               6              110                 2222     |
Jnct  Level Step:   13        Up  Left Right  Addr      Up______  Down____ |
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
    final TreeNet       T = new TreeNet(4);
    final StringBuilder s = new StringBuilder();

    T.P.processTrace = true;
    T.putMessage(T.lastLeaf(),   T.lastLeaf()-1, 1111);
    T.putMessage(T.lastLeaf()-1, T.lastLeaf(),   2222);
    for (T.step = 0; T.step < 3; ++T.step)
     {s.append(T.print());
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

  static void test_shortV()
   {sayCurrentTestName();
    final TreeNet  T = new TreeNet(4);
    T.P.processTrace = true;
    T.PutMessage(T.lastLeaf(),   T.lastLeaf()-1, 1111);
    T.PutMessage(T.lastLeaf()-1, T.lastLeaf(),   2222);

    final StringBuilder s = T.test_transmission(12, 200);
    //stop(s);
    ok(""+s, """
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110       2222               |
  14   3       *               6              111       1111               |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110       2222      1111     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
Jnct  Level Step:   12        Up  Left Right  Addr      Up______  Down____ |
  13   3       *               6              110                 1111     |
  14   3       *               6              111                 2222     |
""");
   }

  static void test_sequence()
   {sayCurrentTestName();
    final TreeNet        T = new TreeNet(4);
    final int       Source = T.lastLeaf(), Target = T.firstLeaf(), Steps = 28;
    final StringBuilder  s = new StringBuilder();
    final StringJoiner   t = new StringJoiner(", ");
    final int []     words = {1111, 2222, 3333, 4444, 5555, 6666};

    T.P.processTrace = true;

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

  static void test_sequenceV()
   {sayCurrentTestName();
    final int [] words = {1111, 2222, 3333, 4444, 5555, 6666};                  // The good news was sent from Aix La Chapelle unto Ghent

    final TreeNet                T = new TreeNet(4);
    final StringJoiner           t = new StringJoiner(", ");
    final Process.Register  Result = T.P.register("result",  1);
    final Process.Register       i = T.P.register("i",       8);
    final Process.Register       o = T.P.register("o",       8);
    final Process.Register inputs  = T.P.register("inputs",  T.messageWidth, words.length);
    final Process.Register outputs = T.P.register("outputs", T.messageWidth, words.length);
    final Process.Register test    = T.P.register("test",    1);
    final Process.Register source  = T.P.register("source",  T.addressWidth);
    final Process.Register target  = T.P.register("target",  T.addressWidth);
    final Process.Register text    = T.P.register("target",  T.messageWidth);
    final MessageOutV   messageOut = T.new MessageOutV();

    final int   Source = T.lastLeaf(), Target = T.firstLeaf(), Steps = 28;

    T.P.processTrace = true;

    source.RegisterSet(Source);
    target.RegisterSet(Target);

    for (int j = 0; j < words.length; j++) inputs.RegisterSet(words[j], j);

    for (T.step = 0; T.step < Steps; ++T.step)
     {T.P.new If (test.Lt(i, words.length))
       {void Then()
         {text.CopyIs(inputs, i);
          T.P.new If (T.PutMessage(source, target, text))
           {void Then() {i.Inc();}
           };
         }
       };

      T.Transmit();

      T.P.new If (messageOut.get(target))
       {void Then()
         {outputs.CopyIt(o, messageOut.Text);
          o.Inc();
         }
       };
     }
    T.maxSteps = 2000;
    T.chipRun();
    ok(outputs, "main_outputs_25 =  1111 2222 3333 4444 5555 6666");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_one();
    test_two();
    test_swap();
    test_reverse2();
    test_reverse8();
    test_short();
    test_sequence();
    test_oneV();
    test_twoV();
    test_swapV();
    test_reverse2V();
    test_reverse8V();
    test_shortV();
    test_sequenceV();
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
