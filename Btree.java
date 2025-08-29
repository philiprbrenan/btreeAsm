//------------------------------------------------------------------------------
// Btree using asynchronous, block memory.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.
// Squeeze out all redundant variables and check all code paths are being tested
// Improve chipPrintMemory to make the output more compact and readable
import java.util.*;

class Btree extends Chip                                                        // Manipulate a btree in a block of memory
 {final int size;                                                               // The maximum number of nodes == branches or leaves in the btree
  final int maxStuckSize;                                                       // The maximum number of entries in the stuck.
  final int bitsPerKey;                                                         // The number of bits needed to define a key
  final int bitsPerData;                                                        // The number of bits needed to define a data field
  final int btreeAddressSize;                                                   // The number of bits needed to address a field in memory holding btree
  final int stuckAddressSize;                                                   // The number of bits needed to address a field in a stuck
  final Memory stuckIsLeaf;                                                     // Whether the current stuck is acting as a leaf or a branch in the btree.
  final Memory stuckIsFree;                                                     // Whether the stuck is on the free chain
  final Memory freeNext;                                                        // Next stuck on the free chain. If this stuck is not on the free chain then this field is zero to show that this stuck in use. If the stuck is the root stuck which is never freed, then its next pointer points to the first free stuck on the free chain.
  final Memory stuckSize;                                                       // Current size of stuck up to the maximum size
  final Memory stuckKeys;                                                       // Keys field
  final Memory stuckData;                                                       // Data field
  boolean suppressMerge = false;                                                // Suppress merges during put to allow merge steps to be tested individually.  If this is on the trees built for testing are already merged so there is nothing to test.
  static boolean debug  = false;                                                // Debug if enabled

//D1 Construction                                                               // Construct and layout a btree

  Btree(int Size, int MaxStuckSize, int BitsPerKey, int BitsPerData)            // Create the Btree
   {super("Btree"); N();
    if (MaxStuckSize % 2 == 1) stop("The stuck size must be even, not:", MaxStuckSize);
    if (MaxStuckSize < 4)      stop("The stuck size must be greater than equal to 4, not:", MaxStuckSize);
    size         = Size;                                                        // The maximum number of entries in the btree.
    maxStuckSize = MaxStuckSize;                                                // The maximum number of entries in the stuck.
    bitsPerKey   = BitsPerKey;                                                  // The number of bits needed to define a key
    bitsPerData  = BitsPerData;                                                 // The number of bits needed to define a data field
    btreeAddressSize = logTwo(Size)+1;                                          // Bits needed to address a field in the btree
    stuckAddressSize = logTwo(MaxStuckSize)+1;                                  // Bits needed to address a field in a stuck
    stuckIsLeaf  = new Memory("stuckIsLeaf", Size, 1);                          // Whether the stuck is a leaf
    stuckIsFree  = new Memory("stuckIsFree", Size, 1);                          // Whether the stuck is on the free chain
    freeNext     = new Memory("freeNext"   , Size, btreeAddressSize);           // Next element reference on free chain
    stuckSize    = new Memory("stuckSize"  , Size, stuckAddressSize);           // Current size of stuck up to the maximum size
    stuckKeys    = new Memory("stuckKeys"  , Size, bitsPerKey , MaxStuckSize);  // Keys fields
    stuckData    = new Memory("stuckData"  , Size, bitsPerData, MaxStuckSize);  // Data fields

chipStop = true;
    createFreeChain();                                                          // Create the free chain
   }

  Process.Register btreeIndex(Process Process, String Name)
   {return Process.register(Name, btreeAddressSize);
   }

  Btree Btree() {return this;}

//D2 Allocation                                                                 // Allocate stucks from the free chain

  void createFreeChain()                                                        // Create the free chain before the chip starts running as this is a one time event
   {for (int i = 0; i < size-1; i++)
     {freeNext.memorySet(i+1, i);                                               // Free chain hangs from root
      stuckIsFree.memorySet(1, i+1);                                            // Start with the root as a leaf
     }
    stuckIsLeaf.memorySet(1, 0);                                                // Start with the root as a leaf
    stuckIsFree.memorySet(0, 0);                                                // Start with the root as a leaf
   }

  private void allocate(Process.Register ref, boolean leaf)                     // Allocate a stuck and set a ref to the allocated node
   {final Process       P         = ref.registerProcess();
    final Memory.Get    gFreeNext = freeNext.memoryGetFromProcess(P);           // Get next free stuck
    final Memory.Set    sFreeNext = freeNext   .memorySetIntoProcess(P);        // Set next free stuck
    final Memory.Set    sIsLeaf   = stuckIsLeaf.memorySetIntoProcess(P);        // Set leaf or branch
    final Memory.Set    sIsFree   = stuckIsFree.memorySetIntoProcess(P);        // Set stuck is free field
    final Process.Register   root = btreeIndex(P, "root");                      // Index of the first free stuck in the btree
    final Process.Register   next = btreeIndex(P, "next");                      // Index of the second free stuck in the btree
    final Process.Register isLeaf = P.register("isLeaf", 1);                    // Indicate whether the allocated stuck is a leaf or a branch
    final Process.Register isFree = P.register("isFree", 1);                    // Indicate that the allocated stuck is not free but in use
    root.Zero();
    gFreeNext.ExecuteTransaction(root);
    gFreeNext.waitResultOfTransaction();
    ref.Copy(gFreeNext.transactionOutputRegisters.firstElement());              // Save reference to allocated stuck

    P.new Instruction()                                                         // First free stuck if any is the allocated stuck
     {void action()
       {if (ref.registerGet() == 0) P.processStop(20);
        if (leaf) isLeaf.one(); else isLeaf.zero();                             // Leaf or branch
        isFree.zero();
       }
      void verilog(Verilog v)
       {v.new If (ref.registerName() + " == 0")
         {void Then() {P.processStop(v, 20);}
         };
        if (leaf) isLeaf.one(v); else isLeaf.zero(v);                           // Leaf or branch
        isFree.zero(v);
       }
     };

    P.new Instruction()                                                         // First free stuck if any is the allocated stuck
     {void action()
       {gFreeNext.executeTransaction(ref);                                      // Next free stuck
        sIsLeaf.executeTransaction(ref, isLeaf);                                // Set allocated stuck to leaf or branch
        sIsFree.executeTransaction(ref, isFree);                                // Set allocated stuck to leaf or branch
       }
      void verilog(Verilog v)
       {gFreeNext.executeTransaction(v, ref);                                   // Next free stuck
        sIsLeaf.executeTransaction(v, ref, isLeaf);                             // Set allocated stuck to leaf or branch
        sIsFree.executeTransaction(v, ref, isFree);                             // Set allocated stuck to leaf or branch
       }
     };

    gFreeNext.waitResultOfTransaction();
    next.Copy(gFreeNext.transactionOutputRegisters.firstElement());
    sFreeNext.ExecuteTransaction(root, next);                                   // Next free stuck is now first on free chain from root
    sFreeNext.waitResultOfTransaction();
    sIsLeaf  .waitResultOfTransaction();
    sIsFree  .waitResultOfTransaction();
   }

  private void free(Process.Register ref)                                       // Free the referenced stuck and put it on the free chain
   {final Process     P           = ref.registerProcess();
    final Process.Register next   = btreeIndex(P, "next");                      // Index of the second free stuck in the btree
    final Memory.Get  gFreeNext   = freeNext.memoryGetFromProcess(P);           // Get next free stuck
    final Memory.Set  sFreeRoot   = freeNext.memorySetIntoProcess(P);           // Set next free stuck
    final Memory.Set  sFreeNext   = freeNext.memorySetIntoProcess(P);           // Set next free stuck
    final Memory.Set    sIsFree   = stuckIsFree.memorySetIntoProcess(P);        // Set stuck is free field
    final Process.Register root   = btreeIndex(P, "root");                      // Index of the first free stuck in the btree

    final Process.Register isFree = P.register("isFree", 1);                    // Indicate that the allocated stuck is not free but in use
    root.Zero();                                                                // The free chain depends from the root which is never freed and so can never be on the free chain
    gFreeNext.ExecuteTransaction(root);
    gFreeNext.waitResultOfTransaction();

    P.new Instruction()                                                         // Next free stuck
     {void action()
       {next.copy(gFreeNext.transactionOutputRegisters.firstElement());
        sFreeRoot.executeTransaction(root, ref);                                // Root points to the stuck being freed
        isFree.one();
       }
      void verilog(Verilog v)
       {next.copy(v, gFreeNext.transactionOutputRegisters.firstElement());
        sFreeRoot.executeTransaction(v, root, ref);                             // Root points to the stuck being freed
        isFree.one(v);
       }
     };

    sIsFree.ExecuteTransaction(ref, isFree);                                    // Stuck Root points to the stuck being freed
    sFreeRoot.waitResultOfTransaction();
    sFreeNext.ExecuteTransaction(ref, next);                                    // Stuck Root points to the stuck being freed
    sFreeNext.waitResultOfTransaction();
    sIsFree  .waitResultOfTransaction();
   }

  private void allocateLeaf  (Process.Register ref) {allocate(ref, true);}      // Allocate a stuck, set a ref to the allocated node and mark it a leaf
  private void allocateBranch(Process.Register ref) {allocate(ref, false);}     // Allocate a stuck, set a ref to the allocated node and mark it a branch

//D2 Save and Load                                                              // Save a btree to a string and reload it from a string

  String btreeSave()                                                            // Save a btree to a string
   {final StringBuilder s = new StringBuilder();
    s.append(""+size              +"\n");
    s.append(""+maxStuckSize      +"\n");
    s.append(""+bitsPerKey        +"\n");
    s.append(""+bitsPerData       +"\n");
    s.append(""+btreeAddressSize  +"\n");
    s.append(""+stuckAddressSize  +"\n");
    s.append(""+stuckIsLeaf.processSave());
    s.append(""+stuckIsFree.processSave());
    s.append(""+freeNext   .processSave());
    s.append(""+stuckSize  .processSave());
    s.append(""+stuckKeys  .processSave());
    s.append(""+stuckData  .processSave());
    return ""+s;
   }

  void btreeLoad(String data)                                                   // Load a btree from a string
   {final String[]l = data.split("\\n");
    final int   []n = new int[6];
    for (int i = 0; i < n.length; i++) n[i] = Integer.parseInt(l[i]);
    int j = 0;
    if (Btree.this.size   != n[j++]) stop("Wrong size");
    if (maxStuckSize      != n[j++]) stop("Wrong maxStuckSize");
    if (bitsPerKey        != n[j++]) stop("Wrong bitsPerKey");
    if (bitsPerData       != n[j++]) stop("Wrong bitsPerData");
    if (btreeAddressSize  != n[j++]) stop("Wrong btreeAddressSize");
    if (stuckAddressSize  != n[j++]) stop("Wrong stuckAddressSize");
    stuckIsLeaf.processLoad (l[j++]);
    stuckIsFree.processLoad (l[j++]);
    freeNext   .processLoad (l[j++]);
    stuckSize  .processLoad (l[j++]);
    stuckKeys  .processLoad (l[j++]);
    stuckData  .processLoad (l[j++]);
   }

//D2 Stuck                                                                      // Get and set stucks within btree

  class Stuck                                                                   // A reference to a stuck in the memory of the btree plus a copy of its size, keys and data
   {final String  stuckName;                                                    // Name of stuck
    final Process P;                                                            // The process to add code to to manipulate this stuck
    final Process.Register index;                                               // The address of the stuck in main memory
    final Process.Register size;                                                // Size of the stuck locally
    final Process.Register isLeaf;                                              // Whether the stuck is a leaf
    final Process.Register nextFree;                                            // Free chain from root
    final Process.Register[]keys;                                               // Keys in the stuck copied out of memory
    final Process.Register[]compares;                                           // The results of the latest key comparison against every key in the stuck
    final Process.Register[]collapse;                                           // The collapse of the key numbers to find the one chosen
    final Process.Register[]data;                                               // Data in the stuck copied out of memory
    final Memory.Get gSize;                                                     // Transaction to get the current size of the stuck
    final Memory.Set sSize;                                                     // Transaction to set the current size of the stuck
    final Memory.Get gLeaf;                                                     // Transaction to discover whether this stuck is acting as a leaf or a branch
    final Memory.Set sLeaf;                                                     // Transaction to set the stuck in memory to  a leaf or a branch
    final Memory.Get gKeys;                                                     // Transactions to get each key in the stuck
    final Memory.Set sKeys;                                                     // Transactions to set each key in the stuck
    final Memory.Get gData;                                                     // Transactions to get each data element in the stuck
    final Memory.Set sData;                                                     // Transactions to set each data element in the stuck
                                                                                // Output registers so they must be owned by this process so that we can write into them
    final Process.Register Found;                                               // Whether the key was found
    final Process.Register Key;                                                 // Data associated with the key if found
    final Process.Register FoundKey;                                            // Data associated with the key if found
    final Process.Register Data;                                                // Data associated with the key if found
    final Process.Register BtreeIndex;                                          // Index of stuck in Btree in which the key should reside
    final Process.Register StuckIndex;                                          // Index of stuck in Btree in which the key should reside
    final Process.Register MergeSuccess;                                        // Whether a merge was completed successfully or not

    Stuck(Process Process, String Name)                                         // Mirror a stuck in memory with one in registers.
     {N();
      P            = Process;
      stuckName    = Name;
      index        = P.register("index",    btreeAddressSize);                  // The address of the stuck in main memory
      size         = P.register("size",     stuckAddressSize);                  // Size of the stuck locally
      isLeaf       = P.register("isLeaf",   1);                                 // Whether the stuck is a leaf
      nextFree     = P.register("nextFree", btreeAddressSize);                  // Free chain from root
      keys         = new Process.Register[maxStuckSize];                        // Keys in the stuck copied out of memory
      compares     = new Process.Register[maxStuckSize];                        // The results of key comparisons
      collapse     = new Process.Register[maxStuckSize];                        // The collapse of the key comparisons
      data         = new Process.Register[maxStuckSize];                        // Data in the stuck copied out of memory
      for (int i   = 0; i < maxStuckSize; i++)                                  // Create registers to hold stuck
       {keys[i]    = P.new Register("Key_"+i, bitsPerKey);                      // Keys in the stuck copied out of the memory of the btree into local registers
        compares[i]= P.new Register("KeyCompares_"+i, 1);                       // The result of comparing the search key with each stuck key
        collapse[i]= P.new Register("KeyCollapse_"+i, stuckAddressSize);        // The result of collapsing the comparisons
        data[i]    = P.new Register("Data_"+i, bitsPerData);                    // Data in the stuck copied out of the memory of the btree into local registers
       }
      gKeys = stuckKeys.memoryGetFromProcess(P);                                // Transactions to get each key in the stuck. Reuseing the transaction reduces generated Verilog code size by 30% at the cost of requiring each stuck Get/Set from/into memory to finish before the next one can start.
      sKeys = stuckKeys.memorySetIntoProcess(P);                                // Transactions to set each key in the stuck
      gData = stuckData.memoryGetFromProcess(P);                                // Transactions to get each data element in the stuck
      sData = stuckData.memorySetIntoProcess(P);                                // Transactions to set each data element in the stuck

      gSize = stuckSize  .memoryGetFromProcess(P);                              // Transaction to get the current size of the stuck
      sSize = stuckSize  .memorySetIntoProcess(P);                              // Transaction to set the current size of the stuck
      gLeaf = stuckIsLeaf.memoryGetFromProcess(P);                              // Transaction to discover whether this stuck is acting as a leaf or a branch
      sLeaf = stuckIsLeaf.memorySetIntoProcess(P);                              // Transaction to set the stuck in memory to  a leaf or a branch
      Found        = P.register("Found",        1);                             // Whether the key was found
      Key          = P.register("Key",          bitsPerKey);                    // Data associated with the key if found
      FoundKey     = P.register("FoundKey",     bitsPerKey);                    // Data associated with the key if found
      Data         = P.register("Data",         bitsPerData);                   // Data associated with the key if found
      BtreeIndex   = P.register("BtreeIndex",   btreeAddressSize);              // Index of stuck in Btree in which the key should reside
      StuckIndex   = P.register("StuckIndex",   stuckAddressSize);              // Index of stuck in Btree in which the key should reside
      MergeSuccess = P.register("MergeSuccess", 1);                             // Whether a merge was completed successfully or not
     }

//D3 Memory                                                                     // Get a stuck from memory or return it to memory

    void stuckGet(Process.Register Index)                                       // Copy a stuck indexed by a register out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retrieval of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {index.Copy(Index);
      stuckGet();
     }

    void stuckGetRoot()                                                         // Copy the root stuck out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retrieval of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {index.Zero();
      stuckGet();
     }

    void stuckGet()                                                             // Copy the indexed stuck out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retrieval of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {P.new Instruction()                                                       // Request the details of the indexed stuck from memory
       {void action()
         {gSize.executeTransaction(index);
          gLeaf.executeTransaction(index);
          gKeys.executeTransaction(index);
          gData.executeTransaction(index);
         }
        void verilog(Verilog v)
         {gSize.executeTransaction(v, index);
          gLeaf.executeTransaction(v, index);
          gKeys.executeTransaction(v, index);
          gData.executeTransaction(v, index);
         }
       };
      gSize.waitResultOfTransaction();                                          // Wait for size from memory
      gLeaf.waitResultOfTransaction();                                          // Wait for leaf status from memory

      gKeys.waitResultOfTransaction();                                          // Load results from transactions into local registers
      gData.waitResultOfTransaction();

      P.new Instruction()
       {void action()
         {size.copy(gSize.transactionOutputRegisters.firstElement());
          isLeaf.copy(gLeaf.transactionOutputRegisters.firstElement());
          for (int i = 0; i < maxStuckSize; i++)
           {keys[i].copy(gKeys.transactionOutputRegisters.elementAt(i));
            data[i].copy(gData.transactionOutputRegisters.elementAt(i));
           }
         }
        void verilog(Verilog v)
         {size.copy(v, gSize.transactionOutputRegisters.firstElement());
          isLeaf.copy(v, gLeaf.transactionOutputRegisters.firstElement());
          for (int i = 0; i < maxStuckSize; i++)
           {keys[i].copy(v, gKeys.transactionOutputRegisters.elementAt(i));
            data[i].copy(v, gData.transactionOutputRegisters.elementAt(i));
           }
         }
       };
     }

    void stuckPut(Process.Register Index, boolean SetLeaf)                      // Update a stuck in memory from the registers describing this stuck optionally updating the isLeaf field.
     {P.new Instruction()
       {void action()
         {sSize.executeTransaction(Index, size);
          if (SetLeaf) sLeaf.executeTransaction(Index, isLeaf);
          sKeys.executeTransaction(Index, keys);
          sData.executeTransaction(Index, data);
         }
        void verilog(Verilog v)
         {sSize.executeTransaction(v, Index, size);
          if (SetLeaf) sLeaf.executeTransaction(v, Index, isLeaf);
          sKeys.executeTransaction(v, Index, keys);
          sData.executeTransaction(v, Index, data);
         }
       };

      sSize.waitResultOfTransaction();                                          // Wait for size to complete
      if (SetLeaf) sLeaf.waitResultOfTransaction();                             // Wait for leaf status to complete

      sKeys.waitResultOfTransaction();                                          // Wait for transactions for keys and data from memory to complete
      sData.waitResultOfTransaction();
     }

    void stuckPut()                       {stuckPut(index, false);}             // Update a stuck in memory from the registers describing his stuck
    void stuckPut(Process.Register Index) {stuckPut(Index, false);}             // Update a stuck in memory from the registers describing his stuck
    void stuckPut(boolean SetLeaf)        {stuckPut(index, SetLeaf);}           // Update a stuck in memory from the registers describing his stuck

//D3 Print                                                                      // Print the stuck

    public String toString()                                                    // Print the stuck
     {final StringBuilder S = new StringBuilder();
      final int    ns = size  .registerGet();                                   // Size of stuck
      final int    il = isLeaf.registerGet() > 0 ? 1 : 0;                       // Is a leaf
      final String rt = index.registerGet() == 0 ? "root" : "index: "+index.registerGet();

      final String nm = stuckName;                                              // Name of the stuck is the same as the name of the process
      S.append("Stuck: "+nm+" size: "+ns+", leaf: "+il+", "+rt+"\n");           // Title

      for (int i = 0; i < ns; i++)                                              // Each key, data pair
       {final int K = keys[i].registerGet();
        final int D = data[i].registerGet();
        S.append(String.format("%2d  %4d => %4d\n", i, K, D));
       }
      return ""+S;
     }

    String dump()                                                               // Print all the entries in the stuck
     {final StringBuilder S = new StringBuilder();
      final int     ns = size  .registerGet();                                  // Size of stuck
      final int     il = isLeaf.registerGet() > 0 ? 1 : 0;                      // Is a leaf
      final String  rt = index.registerGet() == 0 ? "root" : "index: "+index.registerGet();
      final String  nm = stuckName;                                             // Name of the stuck is the same as the name of the process
      S.append("Stuck: "+nm+" size: "+ns+", leaf: "+il+", "+rt+"\n");           // Title

      for (int i = 0; i < maxStuckSize; i++)                                    // Each key, data pair
       {final int k = keys[i].registerGet();
        final int d = data[i].registerGet();
        S.append(String.format("%2d  %4d => %4d\n", i, k, d));
       }

      final int f = Found       .registerGet(); S.append("Found     : "+f+"\n");// Whether the key was found
      final int k = Key         .registerGet(); S.append("Key       : "+k+"\n");// Data associated with the key if found
      final int K = FoundKey    .registerGet(); S.append("FoundKey  : "+K+"\n");// Data associated with the key if found
      final int d = Data        .registerGet(); S.append("Data      : "+d+"\n");// Data associated with the key if found
      final int b = BtreeIndex  .registerGet(); S.append("BtreeIndex: "+b+"\n");// Index of stuck in Btree in which the key should reside
      final int s = StuckIndex  .registerGet(); S.append("StuckIndex: "+s+"\n");// Index of stuck in Btree in which the key should reside
      final int m = MergeSuccess.registerGet(); S.append("Merge     : "+m+"\n");// Whether a merge was completed successfully or not

      return ""+S;
     }

//D3 Actions                                                                    // Just the actions needed on a stuck to support a btree

    void clear()                                                                // Set the size of the stuck to zero to clear it
     {R(); size.zero();
     }

    void clear(Verilog v)                                                       // Set the size of the stuck to zero to clear it
     {size.zero(v);
     }

    void Clear()                                                                // Set the size of the stuck to zero to clear as a single instruction
     {P.new Instruction()
       {void action()           {clear(); }
        void verilog(Verilog v) {clear(v);}
       };
     }

    void push(Process.Register Key, Process.Register Data)                      // Push a key, data pair to the local copy of the stuck
     {R(); final int N = size.registerGet();
      if (N >= maxStuckSize)
       {P.processStop(1);
       }
      else
       {keys[N].copy(Key);
        data[N].copy(Data);
        size.inc();
       }
     }

    void push(Verilog v, Process.Register Key, Process.Register Data)           // Push a key, data pair to the local copy of the stuck
     {v.new Case(maxStuckSize, size.registerName())
       {void Choice(int i)
         {keys[i].copy(v, Key);
          data[i].copy(v, Data);
         }
       };
      size.inc(v);
     }

    void Push(Process.Register Key, Process.Register Data)                      // Push a key, data pair to the local copy of the stuck as a single instruction
     {P.new Instruction()
       {void action()           {push(   Key, Data); }
        void verilog(Verilog v) {push(v, Key, Data);}
       };
     }

    void pop()                                                                  // Pop a key, data pair from the local copy of the stuck
     {R(); final int N = size.registerGet();
      if (N == 0)
       {P.processStop(2);
       }
      else
       {Key.copy (keys[N-1]);
        Data.copy(data[N-1]);
        size.dec();
       }
     }

    void pop(Verilog v)                                                         // Pop a key, data pair from the local copy of the stuck
     {v.new Case(1, maxStuckSize+1, size.registerName())
       {void Choice(int N)
         {Key.copy (v, keys[N-1]);
          Data.copy(v, data[N-1]);
         }
       };
      size.dec(v);
     }

    void Pop()                                                                  // Pop a key, data pair from the local copy of the stuck as a single instruction
     {P.new Instruction()
       {void action()           {pop( ); }
        void verilog(Verilog v) {pop(v);}
       };
     }

    void setPastLastElement(Process.Register Key, Process.Register Data)        // Push a key, data pair to the local copy of the stuck without changing the size
     {R(); final int N = size.registerGet();
      if (N >= maxStuckSize)
       {P.processStop(3);
       }
      else
       {keys[N].copy(Key);
        data[N].copy(Data);
       }
     }

    void setPastLastElement                                                     // Push a key, data pair to the local copy of the stuck without changing the size
     (Verilog v, Process.Register Key, Process.Register Data)
     {v.new Case(maxStuckSize, size.registerName())
       {void Choice(int i)
         {keys[i].copy(v, Key);
          data[i].copy(v, Data);
         }
       };
     }

    void SetPastLastElement(Process.Register Key, Process.Register Data)        // Push a key, data pair to the local copy of the stuck without changing the size as a single instruction
     {P.new Instruction()
       {void action()           {setPastLastElement(   Key, Data); }
        void verilog(Verilog v) {setPastLastElement(v, Key, Data);}
       };
     }

    void firstElement()                                                         // Get the first key, data pair
     {R(); final int N = size.registerGet();
      if (N == 0)
       {P.processStop(4);
       }
      else
       {Key.copy (keys[0]);
        Data.copy(data[0]);
       }
     }

    void firstElement(Verilog v)                                                // Get the first key, data pair
     {Key .copy(v, keys[0]);
      Data.copy(v, data[0]);
     }

    void FirstElement()                                                         // Get the first key, data pair as a single instruction
     {P.new Instruction()
       {void action()           {firstElement(); }
        void verilog(Verilog v) {firstElement(v);}
       };
     }

    void lastElement()                                                          // Get the last key, data pair
     {R(); final int N = size.registerGet();
      if (N >= maxStuckSize)
       {P.processStop(5);
       }
      else
       {Key.copy (keys[N-1]);
        Data.copy(data[N-1]);
       }
     }

    void lastElement(Verilog v)                                                 // Get the last key, data pair
     {v.new Case(1, maxStuckSize, size.registerName())
       {void Choice(int i)
         {Key.copy (v, keys[i-1]);
          Data.copy(v, data[i-1]);
         }
       };
     }

    void LastElement()                                                          // Get the last key, data pair as a single instruction
     {P.new Instruction()
       {void action()           {lastElement(); }
        void verilog(Verilog v) {lastElement(v);}
       };
     }

    void pastLastElement()                                                      // Get the past last key, data pair
     {R(); final int N = size.registerGet();
      if (N >= maxStuckSize-1)
       {P.processStop(6);
       }
      else
       {Key.copy (keys[N]);
        Data.copy(data[N]);
       }
     }

    void pastLastElement(Verilog v)                                             // Get the past last key, data pair
     {v.new Case(maxStuckSize, size.registerName())
       {void Choice(int i)
         {Key.copy (v, keys[i]);
          Data.copy(v, data[i]);
         }
       };
     }

    void PastLastElement()                                                      // Get the past last key, data pair as a single instruction
     {P.new Instruction()
       {void action()           {pastLastElement(); }
        void verilog(Verilog v) {pastLastElement(v);}
       };
     }

    void elementAt(Process.Register Index)                                      // Get the indexed key, data pair
     {R(); final int N = Index.registerGet();
      if (N >= maxStuckSize)
       {P.processStop(7);
        return;
       }
      Key.copy (keys[N]);
      Data.copy(data[N]);
     }

    void elementAt(Verilog v, Process.Register Index)                           // Get the indexed key, data pair
     {v.new Case(maxStuckSize, Index.registerName())
       {void Choice(int i)
         {Key.copy (v, keys[i]);
          Data.copy(v, data[i]);
         }
       };
     }

    void ElementAt(Process.Register Index)                                      // Get the indexed key, data pair as a single instruction
     {P.new Instruction()
       {void action()           {elementAt(   Index);}
        void verilog(Verilog v) {elementAt(v, Index);}
       };
     }

    void setElementAt                                                           // Set the indexed key, data pair
     (Process.Register Index, Process.Register Key, Process.Register Data)
     {R(); final int N = Index.registerGet();
      final int M = size.registerGet();
      if (N >= maxStuckSize)
       {P.processStop(8);
        return;
       }
      if (N > M)
       {P.processStop(9);
        return;
       }
      if (N == M)
       {size.inc();
       }
      keys[N].copy(Key);
      data[N].copy(Data);
     }

    void setElementAt(Verilog v,                                                // Set the indexed key, data pair
      Process.Register Index, Process.Register Key, Process.Register Data)
     {v.new If (Index.registerName()+" == "+size.registerName())
       {void Then()
         {size.inc(v);
         }
       };
      v.new Case(maxStuckSize, Index.registerName())
       {void Choice(int i)
         {keys[i].copy(v, Key);
          data[i].copy(v, Data);
         }
       };
     }

    void SetElementAt                                                           // Set the indexed key, data pair as a single instruction
     (Process.Register Index, Process.Register Key, Process.Register Data)
     {P.new Instruction()
       {void action()           {setElementAt(   Index, Key, Data);}
        void verilog(Verilog v) {setElementAt(v, Index, Key, Data);}
       };
     }

    void setDataAt(Process.Register Index, Process.Register Data)               // Set the indexed data pair
     {R(); final int N = Index.registerGet();
      final int M = size.registerGet();
      if (N >= M)
       {P.processStop(10);
        return;
       }
      data[N].copy(Data);
     }

    void setDataAt(Verilog v, Process.Register Index, Process.Register Data)    // Set the indexed data pair
     {v.new Case(maxStuckSize, Index.registerName())
       {void Choice(int i)
         {data[i].copy(v, Data);
         }
       };
     }

    void SetDataAt(Process.Register Index, Process.Register Data)               // Set the indexed data pair  as a single instruction
     {P.new Instruction()
       {void action()           {setDataAt(   Index, Data);}
        void verilog(Verilog v) {setDataAt(v, Index, Data);}
       };
     }

    void insertElementAt                                                        // Set the indexed key, data pair
     (Process.Register Index, Process.Register Key, Process.Register Data)
     {R(); final int N = Index.registerGet();
      final int S = size.registerGet();
      if (N >= maxStuckSize)                                                    // No reason left in stuck
       {P.processStop(11);
        return;
       }
      if (N > S)                                                                // An insertion would leave gap
       {P.processStop(12);
        return;
       }
      size.inc();                                                               // Increase number of elements
      for (int i = maxStuckSize-1; i > N; i--)
       {keys[i].copy(keys[i-1]);
        data[i].copy(data[i-1]);
       }
      keys[N].copy(Key);
      data[N].copy(Data);
     }

    void insertElementAt                                                        // Set the indexed key, data pair
     (Verilog v, Process.Register Index,
      Process.Register Key, Process.Register Data)
     {size.inc(v);                                                              // Increase number of elements
      for (int i = maxStuckSize-1; i > 0; i--)
       {final int I = i;
         v.new If(""+i+" > "+Index.registerName())
         {void Then()
           {keys[I].copy(v, keys[I-1]);
            data[I].copy(v, data[I-1]);
           }
         };
       }
      v.new Case(maxStuckSize, Index.registerName())
       {void Choice(int i)
         {keys[i].copy(v, Key);
          data[i].copy(v, Data);
         }
       };
     }

    void InsertElementAt                                                        // Set the indexed key, data pair as a single instruction
     (Process.Register Index, Process.Register Key, Process.Register Data)
     {P.new Instruction()
       {void action()           {insertElementAt(   Index, Key, Data);}
        void verilog(Verilog v) {insertElementAt(v, Index, Key, Data);}
       };
     }

    void removeElementAt(Process.Register Index)                                // Remove the indexed key, data pair from the stuck as a single instruction
     {R(); final int N = Index.registerGet();
      if (N >= size.registerGet())
       {P.processStop(13);
        return;
       }
      size.dec();
      Key.copy (keys[N]);
      Data.copy(data[N]);
      final int M = maxStuckSize-1;
      for (int i = N; i < M; i++)
       {keys[i].copy(keys[i+1]);
        data[i].copy(data[i+1]);
       }
     }

    void removeElementAt(Verilog v, Process.Register Index)                     // Remove the indexed key, data pair from the stuck as a single instruction
     {size.dec(v);
      v.new Case(maxStuckSize, Index.registerName())
       {void Choice(int i)
         {Key .copy(v, keys[i]);
          Data.copy(v, data[i]);
         }
       };
      for (int i = 0; i < maxStuckSize-1; i++)
       {final int I = i;
        v.new If (""+i + ">= "+Index.registerName())
         {void Then()
           {keys[I].copy(v, keys[I+1]);
            data[I].copy(v, data[I+1]);
           }
         };
       }
     }

    void RemoveElementAt(Process.Register Index)                                // Remove the indexed key, data pair from the stuck as a single instruction
     {P.new Instruction()
       {void action()           {removeElementAt(   Index);}
        void verilog(Verilog v) {removeElementAt(v, Index);}
       };
     }

//D3 Search                                                                     // Search the stuck

    void search_eq(Process.Register Key)                                        // Find the specified key if possible in the stuck
     {R(); final int N = size.registerGet();
      Found.zero();
      for (int i = 0; i < N; ++i)
       {if (Found.registerGet() == 0)
         {final int I = i;
          if (keys[i].registerGet() == Key.registerGet())
           {Found.one();
            StuckIndex.registerSet(I);
            Stuck.this.Key .copy(keys[I]);
            Stuck.this.Data.copy(data[I]);
           }
         }
       }
     }

    void search_eq(Verilog v, Process.Register Key)                             // Find the specified key if possible in the stuck
     {Found.zero(v);
      for (int i = 0; i < maxStuckSize; ++i)
       {final int I = i;
        v.new If ("!"+Found.registerName())
         {void Then()
           {v.new If (""+I+" < "+size.registerName())
             {void Then()
               {v.new If (keys[I].registerName()+" == "+Key.registerName())
                 {void Then()
                   {Found.one(v);
                    StuckIndex.registerSet(v, I);
                    Stuck.this.Key .copy(v, keys[I]);
                    Stuck.this.Data.copy(v, data[I]);
                   }
                 };
               }
             };
           }
         };
       }
     }

    void Search_eq(Process.Register Key)                                        // Find the specified key if possible in the stuck
     {P.new Instruction()
       {void action()
         {search_eq(Key);
         }
        void verilog(Verilog v)
         {search_eq(v, Key);
         }
       };
     }

    void search_eq_parallel(Process.Register Key)                               // Find the specified key if possible in the stuck
     {P.new Instruction()
       {void action()
         {final int N = size.registerGet();
          for (int i = 0; i < maxStuckSize; ++i)                                // Compare each key
           {final boolean eq = Key.registerGet() == keys[i].registerGet() && i < N;
            compares[i].registerSet(eq ? 1 : 0);
            collapse[i].registerSet(i);
           }
         }
        void verilog(Verilog v)
         {final String N = size.registerName();
          for (int i = 0; i < maxStuckSize; ++i)                                // Compare each key
           {final String eq = Key.registerName()+" == "+keys[i].registerName()+
              " && "+i+" < "+N;
            v.assign(compares[i].registerName(), eq);
            collapse[i].registerSet(v, i);
           }
         }
       };
      for(int i = 1; i < maxStuckSize; i *= 2)                                  // Collapse the comparison
       {final int I = i;
        P.new Instruction()
         {void action()
           {for (int j = 0; j < maxStuckSize-I; j += I+I)
             {if (compares[j+I].registerGet() > 0)
               {compares[j].one();
                collapse[j].copy(collapse[j+I]);
               }
             }
           }
          void verilog(Verilog v)
           {for (int j = 0; j < maxStuckSize-I; j += I+I)
             {final int J = j;
               v.new If (compares[J+I].registerName())
               {void Then()
                 {compares[J].one(v);
                  collapse[J].copy(v, collapse[J+I]);
                 }
               };
             }
           }
         };
       }
      P.new Instruction()                                                       // Set result
       {void action()
         {if (compares[0].registerGet() > 0)
           {Found.one();
            final int I = collapse[0].registerGet();
            StuckIndex.registerSet(I);
            Stuck.this.Key .copy(keys[I]);
            Stuck.this.Data.copy(data[I]);
           }
          else
           {Found.zero();
           }
         };
        void verilog(Verilog v)
         {v.new If (compares[0].registerName())
           {void Then()
             {Found.one(v);
              v.new Case(maxStuckSize, collapse[0].registerName())
               {void Choice(int I)
                 {StuckIndex.registerSet(v, I);
                  Stuck.this.Key .copy(v, keys[I]);
                  Stuck.this.Data.copy(v, data[I]);
                 }
               };
             }
            void Else()
             {Found.zero(v);
             }
           };
         }
       };
     }

    void search_le(Process.Register Key)                                        // Find the first key in the stuck so that the search key is less than or equal to this key
     {R(); final int N = size.registerGet();
      Found.zero();
      StuckIndex.registerSet(N);
      Data.copy(data[N]);
      for (int i = 0; i < N; ++i)
       {if (Found.registerGet() == 0)
         {final int I = i;
          if (Key.registerGet() <= keys[I].registerGet())
           {Found.one();
            StuckIndex.registerSet(I);
            FoundKey.copy(keys[I]);
            Data    .copy(data[I]);
           }
         }
       }
     }

    void search_le(Verilog v, Process.Register Key)                             // Find the first key in the stuck so that the search key is less than or equal to this key
     {Found.zero(v);
      StuckIndex.copy(v, size);                                                 // Default is top if no match found
      v.new Case(maxStuckSize, size.registerName())
       {void Choice(int i)
         {Data.copy(v, data[i]);
         }
       };

      for (int i = 0; i < maxStuckSize; ++i)                                    // Check each key
       {final int I = i;
        v.new If ("!"+Found.registerName())
         {void Then()
           {v.new If (""+I+" < "+size.registerName())
             {void Then()
               {v.new If (Key.registerName() +  "<= " +keys[I].registerName())
                 {void Then()
                   {Found.one(v);
                    StuckIndex.registerSet(v, I);
                    FoundKey.copy(v, keys[I]);
                    Data    .copy(v, data[I]);
                   }
                 };
               }
             };
           }
         };
       }
     }

    void Search_le(Process.Register Key)                                        // Find the first key in the stuck so that the search key is less than or equal to this key
     {P.new Instruction()
       {void action()
         {search_le(Key);
         }
        void verilog(Verilog v)
         {search_le(v, Key);
         }
       };
     }

    void search_le_parallel(Process.Register Key)                               // Find the specified key if possible in the stuck
     {P.new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (true)                                                             // Compare first key
           {final boolean le = Key.registerGet() <= keys[0].registerGet() && 0 < N;
            compares[0].registerSet(le ? 1 : 0);
            collapse[0].registerSet(0);
           }
          for (int i = 1; i < maxStuckSize; ++i)                                // Compare each key range
           {final boolean in =
              Key.registerGet() >  keys[i-1].registerGet() &&
              Key.registerGet() <= keys[i]  .registerGet() && i < N;
            compares[i].registerSet(in ? 1 : 0);
            collapse[i].registerSet(i);
           }
         }
        void verilog(Verilog v)
         {final String N = size.registerName();
          if (true)                                                             // Compare first key
           {final String le = Key.registerName()+" <= "+keys[0].registerName()+
              " && 0 < "+N;
            v.assign(compares[0].registerName(), le);
            collapse[0].registerSet(v, 0);
           }
          for (int i = 1; i < maxStuckSize; ++i)                                // Compare each key
           {final String K = Key.registerName();
             final String in =
              K + " >  " + keys[i-1].registerName() + " && " +
              K + " <= " + keys[i]  .registerName() + " && " +i+ " < "+N;
            v.assign(compares[i].registerName(), in);
            collapse[i].registerSet(v, i);
           }
         }
       };
      for(int i = 1; i < maxStuckSize; i *= 2)                                  // Collapse the comparison
       {final int I = i;
        P.new Instruction()
         {void action()
           {for (int j = 0; j < maxStuckSize-I; j += I+I)
             {if (compares[j+I].registerGet() > 0)
               {compares[j].one();
                collapse[j].copy(collapse[j+I]);
               }
             }
           }
          void verilog(Verilog v)
           {for (int j = 0; j < maxStuckSize-I; j += I+I)
             {final int J = j;
               v.new If (compares[J+I].registerName())
               {void Then()
                 {compares[J].one(v);
                  collapse[J].copy(v, collapse[J+I]);
                 }
               };
             }
           }
         };
       }
      P.new Instruction()                                                       // Set result
       {void action()
         {if (compares[0].registerGet() > 0)
           {Found.one();
            final int I = collapse[0].registerGet();
            StuckIndex.registerSet(I);
            Stuck.this.FoundKey .copy(keys[I]);
            Stuck.this.Data     .copy(data[I]);
           }
          else
           {final int N = size.registerGet();
            Found.zero();
            StuckIndex.registerSet(N);
            Stuck.this.Data    .copy(data[N]);
           }
         };
        void verilog(Verilog v)
         {v.new If (compares[0].registerName())
           {void Then()
             {Found.one(v);
              v.new Case(maxStuckSize, collapse[0].registerName())
               {void Choice(int I)
                 {StuckIndex.registerSet(v, I);
                  Stuck.this.FoundKey.copy(v, keys[I]);
                  Stuck.this.Data    .copy(v, data[I]);
                 }
               };
             }
            void Else()
             {Found.zero(v);
              v.new Case(maxStuckSize, size.registerName())
               {void Choice(int I)
                 {StuckIndex.registerSet(v, I);
                  Stuck.this.Data.copy(v, data[I]);
                 }
               };
             }
           };
         }
       };
     }

//D3 Split                                                                      // Split stucks in many and various ways

    void splitIntoTwo(Stuck Left, Stuck Right)                                  // Copy the first half key, data pairs into the left stuck, the remainder into the right stuck.  The original source stuck is not modifiedr
     {final int C = maxStuckSize / 2;                                           // Split the stuck in half
      P.new Instruction()
       {void action()
         {final int N = size.registerGet();                                     // Current size of stuck
          if (N != maxStuckSize)                                                // Stuck must be full
           {P.processStop(14);
           }

          for (int i = 0; i < C; ++i)                                           // Move left keys
           {Left.keys[i].copy(keys[i]);
            Left.data[i].copy(data[i]);
           }                                                                    // Left size
          Left .size.registerSet(C);

          for (int i = 0; i < C; ++i)                                           // Move right keys
           {Right.keys[i].copy(keys[C+i]);
            Right.data[i].copy(data[C+i]);
           }
          Right.size.registerSet(C);                                            // Right size
         }

        void verilog(Verilog v)
         {for (int i = 0; i < C; ++i)
           {Left.keys[i].copy(v, keys[i]);
            Left.data[i].copy(v, data[i]);
           }
          Left  .size.registerSet(v, C);

          for (int i = 0; i < C; ++i)
           {Right.keys[i].copy(v, keys[C+i]);
            Right.data[i].copy(v, data[C+i]);
            }
          Right.size.registerSet(v, C);
         }
       };
     }

    void splitIntoThree(Stuck Left, Stuck Right, int Copy)                      // Copy the specified number of key, data pairs into the left stuck, skip one pair, then copy the specified number onto into the right stuck
     {P.new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (Copy >= N)
           {P.processStop(15);
            return;
           }
          for (int i = 0; i < Copy; ++i)
           {Left.keys[i].copy(keys[i]);
            Left.data[i].copy(data[i]);
           }
          Left.size.registerSet(Copy);
          Left.data[Copy].copy(data[Copy]);

          for (int i = 0; i < Copy; ++i)
           {Right.keys[i].copy(keys[Copy+i+1]);
            Right.data[i].copy(data[Copy+i+1]);
           }
          Right.size.registerSet(N - Copy-1);
          Right.data[Copy].copy(data[2*Copy+1]);
         }

        void verilog(Verilog v)
         {for (int i = 0; i < Copy; ++i)
           {Left.keys[i].copy(v, keys[i]);
            Left.data[i].copy(v, data[i]);
           }
          Left.size.registerSet(v, Copy);
          Left.data[Copy].copy(v, data[Copy]);

          for (int i = 0; i < Copy; ++i)
           {Right.keys[i].copy(v, keys[Copy+i+1]);
            Right.data[i].copy(v, data[Copy+i+1]);
           }

          v.new Case(maxStuckSize, size.registerName())
           {void Choice(int N)
             {Right.size.registerSet(v, N - Copy-1);
              Right.data[Copy].copy(v, data[2*Copy+1]);
             }
           };
         }
       };
     }

    void splitLow(Stuck Left)                                                   // Split a full stuck with an even number of elements so that the first half is moved into the left stuck leaving the remainder moved down in the current stuck
     {P.new Instruction()
       {void action()
         {if (maxStuckSize % 2 == 1)
           {P.processStop(16);
            return;
           }
          if (maxStuckSize != size.registerGet())
           {P.processStop(17);
            return;
           }
          final int N = maxStuckSize / 2;
          for (int i = 0; i < N; ++i)
           {Left.keys[i].copy(keys[i]);
            Left.data[i].copy(data[i]);
           }
          Left.size.registerSet(N);

          for (int i = 0; i < N; ++i)
           {keys[i].copy(keys[N+i]);
            data[i].copy(data[N+i]);
           }
          size.registerSet(N);
         }
        void verilog(Verilog v)
         {final int N = maxStuckSize / 2;
          for (int i = 0; i < N; ++i)
           {Left.keys[i].copy(v, keys[i]);
            Left.data[i].copy(v, data[i]);
           }
          Left.size.registerSet(v, N);

          for (int i = 0; i < N; ++i)
           {keys[i].copy(v, keys[N+i]);
            data[i].copy(v, data[N+i]);
           }
          size.registerSet(v, N);
         }
       };
     }

    void splitLowButOne(Stuck Left, Process.Register Key)                       // Split an almost full stuck with an odd number of elements so that the first half is moved into the left stuck extended by the data of the central element, while the remainder beyond the central element are moved down.  The central key is returned as it would otherwise be lost.
     {P.new Instruction()
       {void action()
         {if (maxStuckSize % 2 == 1)
           {P.processStop(18);
            return;
           }
          if (maxStuckSize-1 != size.registerGet())
           {P.processStop(19);
            return;
           }
          final int N = (maxStuckSize-1) / 2;
          for (int i = 0; i < N; ++i)
           {Left.keys[i].copy(keys[i]);
            Left.data[i].copy(data[i]);
           }
          Left.size.registerSet(N);
          Left.data[N].copy(data[N]);
          Key.copy(keys[N]);

          for (int i = 0; i <= N; ++i)                                          // Move the top element element down as well
           {keys[i].copy(keys[N+i+1]);
            data[i].copy(data[N+i+1]);
           }
          size.registerSet(N);
         }
        void verilog(Verilog v)
         {final int N = (maxStuckSize-1) / 2;
          for (int i = 0; i < N; ++i)
           {Left.keys[i].copy(v, keys[i]);
            Left.data[i].copy(v, data[i]);
           }
          Left.size.registerSet(v, N);
          Left.data[N].    copy(v, data[N]);
          Key.copy(v, keys[N]);

          for (int i = 0; i <= N; ++i)                                          // Move the top element element down as well
           {keys[i].copy(v, keys[N+i+1]);
            data[i].copy(v, data[N+i+1]);
           }
          size.registerSet(v, N);
         }
       };
     }

//D3 Merge                                                                      // Merge stucks in various ways

    void merge(Stuck source)                                                    // Concatenate the indicated stuck onto the end of the current one
     {P.new Instruction()
       {void action()
         {final int S = source.size.registerGet();
          final int T =        size.registerGet();
          MergeSuccess.zero();
          if (S + T > maxStuckSize)
           {return;
           }
          for (int i = 0; i < S; ++i)
           {keys[T+i].copy(source.keys[i]);
            data[T+i].copy(source.data[i]);
           }
          size.add(source.size);
          MergeSuccess.one();
         }
        void verilog(Verilog v)
         {MergeSuccess.zero(v);
          v.new Case(maxStuckSize+1, size.registerName())
           {void Choice(int T)
             {v.new Case(maxStuckSize+1-T, source.size.registerName())
               {void Choice(int S)
                 {if (S + T <= maxStuckSize)
                   {for (int i = 0; i < S; ++i)
                     {final int I = i;
                      keys[T+I].copy(v, source.keys[I]);
                      data[T+I].copy(v, source.data[I]);
                     }
                    size.add(v, S);
                    MergeSuccess.one(v);
                   }
                 };
               };
             }
           };
         }
       };
     }

    void merge(Stuck Left, Stuck Right)                                         // Replace the current stuck with the concatenation of the two stucks indicated
     {P.new Instruction()
       {void action()
         {final int L = Left .size.registerGet();
          final int R = Right.size.registerGet();
          MergeSuccess.zero();
          if (L + R <= maxStuckSize)
           {clear();
            MergeSuccess.one();
           }
         }
        void verilog(Verilog v)
         {MergeSuccess.zero(v);
          v.new Case(maxStuckSize+1, Left .size.registerName())
           {void Choice(int L)
             {v.new Case(maxStuckSize+1-L, Right.size.registerName())
               {void Choice(int R)
                 {if (L + R <= maxStuckSize)
                   {clear(v);
                    MergeSuccess.one(v);
                   }
                 }
               };
             }
           };
         }
       };

      P.new If(MergeSuccess)
       {void Then()
         {merge(Left);
          merge(Right);
         }
       };
     }

    void mergeButOne(Process.Register Key, Stuck Source)                        // Concatenate the indicated stuck with a past last data element onto the end of the current stuck with a past last data element with the specified key inserted over the central past last data element separating the two.
     {MergeSuccess.Zero();                                                      // Assume we cannot merge
      P.new Instruction()
       {void action()
         {final int S = Source.size.registerGet();
          final int T =        size.registerGet();
          if (S + T + 1 < maxStuckSize)
           {keys[T].copy(Key);                                                  // Add key over past last data element of target
            for (int i = 0; i < S; ++i)                                         // Concatenate each key, data pair from source
             {keys[T+i+1].copy(Source.keys[i]);
              data[T+i+1].copy(Source.data[i]);
             }
            data[S+T+1].copy(Source.data[S]);                                   // Past last data element from source
            size.add1(Source.size);                                             // New size of target
            MergeSuccess.one();
           }
         }

        void verilog(Verilog v)
         {v.new Case(maxStuckSize+1, size.registerName())
           {void Choice(int T)
             {v.new Case(maxStuckSize+1-T, Source.size.registerName())
               {void Choice(int S)
                 {if (S + T + 1 < maxStuckSize)
                   {keys[T].copy(v, Key);                                       // Add key over past last data element of target
                    for (int i = 0; i < S; ++i)                                 // Concatenate each key, data pair from source
                     {final int I = i;
                      keys[T+I+1].copy(v, Source.keys[I]);
                      data[T+I+1].copy(v, Source.data[I]);
                     }
                    data[S+T+1].copy(v, Source.data[S]);                        // Past last data element from source
                    size.add(v, S+1);                                           // New size of target
                    MergeSuccess.one(v);
                   }
                 }
               };
             }
           };
         }
       };
     }

    void mergeButOne(Stuck Left, Process.Register Key, Stuck Right)             // Concatenate the past last left and right stucks separated by the key over the past last data element of the left stuck into the target
     {P.new Instruction()
       {void action()
         {final int L = Left .size.registerGet();
          final int R = Right.size.registerGet();
          MergeSuccess.zero();
          if (L + R + 1 >= maxStuckSize)                                        // Check size
           {return;
           }

          for (int i = 0; i < L; ++i)                                           // Concatenate each key, data pair from source
           {keys[i].copy(Left.keys[i]);
            data[i].copy(Left.data[i]);
           }
          keys[L].copy(Key);                                                    // Place key over past last data element from left
          data[L].copy(Left.data[L]);

          for (int i = 0; i < R; ++i)                                           // Concatenate each key, data pair from right
           {keys[L+i+1].copy(Right.keys[i]);
            data[L+i+1].copy(Right.data[i]);
           }
          data[L+R+1].copy(Right.data[R]);                                      // Past last data element from right
          size.registerSet(L+R+1);                                              // New size of target
          MergeSuccess.one();
         }

        void verilog(Verilog v)
         {MergeSuccess.zero(v);
          v.new Case(maxStuckSize, Left.size.registerName())
           {void Choice(int L)
             {v.new Case(maxStuckSize-L, Right.size.registerName())
               {void Choice(int R)
                 {if (L + R + 1 < maxStuckSize)                                 // Check size
                   {for (int i = 0; i < L; ++i)                                 // Concatenate each key, data pair from source
                     {keys[i].copy(v, Left.keys[i]);
                      data[i].copy(v, Left.data[i]);
                     }
                    keys[L].copy(v, Key);                                       // Place key over past last data element from left
                    data[L].copy(v, Left.data[L]);

                    for (int i = 0; i < R; ++i)                                 // Concatenate each key, data pair from right
                     {keys[L+i+1].copy(v, Right.keys[i]);
                      data[L+i+1].copy(v, Right.data[i]);
                     }
                    data[L+R+1].copy(v, Right.data[R]);                         // Past last data element from right
                    size.registerSet(v, L+R+1);                                 // New size of target
                    MergeSuccess.one(v);
                   }
                 }
               };
             }
           };
         }
       };
     }

//D3 Is a Leaf                                                                  // Determine whether a stuck contains a leaf or a branch of a btree

    class IsLeaf                                                                // Process a stuck depending on whether it is a leaf or a branch
     {IsLeaf()
       {P.new Block()                                                           // Outer block contains branch code
         {void code()
           {final Process.Label bEnd = end;
            P.new Block()                                                       // Inner block contains leaf code
             {void code()
               {final Process.Label lEnd = end;
                P.new Instruction(true)
                 {void action()
                   {if (isLeaf.registerGet() == 0) P.Goto(lEnd);                // Not a leaf so go to code for branch
                    else P.Continue();
                   }
                  void verilog(Verilog v)                                       // Not a leaf so go to code for branch
                   {v.new If (isLeaf.registerName() + " == 0")
                     {void Then()
                       {P.Goto(v, lEnd);
                       }
                      void Else()
                       {v.inc(P.processPcName());
                       }
                     };
                   }
                 };
                Leaf();                                                         // On a leaf
                P.GOto(bEnd);
               }
             };
            Branch();                                                           // On a branch
           }
         };
       }
      void Leaf()   {}
      void Branch() {}
     }
   } // Stuck

//D2 Horizontally                                                               // Print the tree horizontally

    final int linesToPrintABranch =  4;                                         // The number of lines required to print a branch
    final int maxPrintLevels      =  3;                                         // The maximum number of levels to print `- this avoids endless print loops when something goes wrong

    void printLeaf(int BtreeIndex, Stack<StringBuilder>P, int level)            // Print leaf horizontally
     {padStrings(P, level);

      final StringBuilder s = new StringBuilder();                              // String builder
      for  (int i = 0; i < stuckSize.memoryGet(BtreeIndex); i++)
       {s.append(""+stuckKeys.memoryGet(BtreeIndex, i)+",");
       }
      if (s.length() > 0) s.setLength(s.length()-1);                            // Remove trailing comma if present
      s.append("="+BtreeIndex+" ");
      P.elementAt(level*linesToPrintABranch).append(s.toString());
      padStrings(P, level);
     }

    void printBranch(int BtreeIndex, Stack<StringBuilder>P, int level)          // Print branch horizontally
     {if (level > maxPrintLevels) return;
      padStrings(P, level);
      final int L = level * linesToPrintABranch;                                // Start line at which to print branch
      final int K = stuckSize.memoryGet(BtreeIndex);                            // Size of branch

      if (K > 0)                                                                // Branch has key, next pairs
       {for  (int i = 0; i < K; i++)
         {final int key  = stuckKeys.memoryGet(BtreeIndex, i);
          final int data = stuckData.memoryGet(BtreeIndex, i);
          if (stuckIsLeaf.memoryGet(data) > 0)
           {printLeaf  (data, P, level+1);
           }
          else
           {printBranch(data, P, level+1);
           }

          P.elementAt(L+0).append(""+stuckKeys.memoryGet(BtreeIndex, i));       // Key
          P.elementAt(L+1).append(""+BtreeIndex+(i > 0 ?  "."+i : ""));         // Branch,key, next pair
          P.elementAt(L+2).append(""+stuckData.memoryGet(BtreeIndex, i));
         }
       }
      else                                                                      // Branch is empty so print just the index of the branch
       {P.elementAt(L+0).append(""+BtreeIndex+"Empty");
       }
      final int top = stuckData.memoryGet(BtreeIndex, K);                       // Top next will always be present
      P.elementAt(L+3).append(top);                                             // Append top next

      if (stuckIsLeaf.memoryGet(top) > 0)                                       // Print leaf
       {printLeaf  (top, P, level+1);
       }
      else                                                                      // Print branch
       {printBranch(top, P, level+1);
       }

      padStrings(P, level);
     }

   String printBoxed()                                                          // Print a tree in a box
    {final String  s = toString();
     final int     n = longestLine(s)-1;
     final String[]L = s.split("\n");
     final StringBuilder t = new StringBuilder();
     t.append("+"); t.append("-".repeat(n)); t.append("+\n");
     for(String l : L) t.append("| "+l+"\n");
     t.append("+"); t.append("-".repeat(n)); t.append("+\n");
     return t.toString();
    }

  void padStrings(Stack<StringBuilder> S, int level)                            // Pad the strings at each level of the tree so we have a vertical face to continue with - a bit like Marc Brunel's tunneling shield
   {final int N = level * linesToPrintABranch + maxStuckSize;                   // Number of lines we might want
    for (int i = S.size(); i <= N; ++i) S.push(new StringBuilder());            // Make sure we have a full deck of strings
    int m = 0;                                                                  // Maximum length
    for (StringBuilder s : S) m = m < s.length() ? s.length() : m;              // Find maximum length
    for (StringBuilder s : S)                                                   // Pad each string to maximum length
     {if (s.length() < m) s.append(" ".repeat(m - s.length()));                 // Pad string to maximum length
     }
   }

  String printCollapsed(Stack<StringBuilder> S)                                 // Collapse horizontal representation into a string
   {final StringBuilder t = new StringBuilder();                                // Print the lines of the tree that are not blank
    for  (StringBuilder s : S)
     {final String l = s.toString();
      if (l.isBlank()) continue;
      t.append(l+"|\n");
     }
    return t.toString();
   }

  String btreePrint()                                                           // Print a tree horizontally
   {final Stack<StringBuilder> P = new Stack<>();
    if (stuckIsLeaf.memoryGet(0) > 0) printLeaf(0, P, 0); else printBranch(0, P, 0);
    return printCollapsed(P);
   }

//D2 Split                                                                      // Split nodes in half to increase the number of nodes in the tree

  private void splitRootLeaf(Process P)                                         // Split a full root leaf
   {final Stuck p = new Stuck(P, "splitRootLeafParent");                        // Parent stuck
    final Stuck l = new Stuck(P, "splitRootLeafLeft");                          // Left split stuck
    final Stuck r = new Stuck(P, "splitRootLeafRight");                         // Right split stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key

    p.stuckGetRoot();                                                           // Load parent

    P.new Block()
     {void code()
       {P.new Instruction(true)
         {void action()
           {if (p.isLeaf.registerGet() == 0) stop("Root must be a leaf");
            if (p.size.registerGet() < maxStuckSize) P.Goto(end);               // Check size of root
            else P.Continue();
           }
          void verilog(Verilog v)
           {v.new If (p.size.registerName() + " < " + maxStuckSize)
             {void Then()
               {P.Goto(v, end);
               }
              void Else()
               {P.Continue(v);
               }
             };
           }
         };

        p.splitIntoTwo(l, r);                                                   // Split the leaf root in two down the middle

        allocateLeaf(il); l.stuckPut(il);                                       // Allocate and save left leaf
        allocateLeaf(ir); r.stuckPut(ir);                                       // Allocate and save right leaf

        l.LastElement();
        r.FirstElement();
        mk.Average(l.Key, r.Key);
        p.Clear();
        p.Push(mk, il);
        p.SetPastLastElement(mk, ir);
        p.isLeaf.Zero();
        p.stuckPut(true);
       }
     };
   }

  private void splitRootBranch(Process P)                                       // Split a full root branch
   {final Stuck p = new Stuck(P, "splitRootBranchParent");                      // Parent stuck
    final Stuck l = new Stuck(P, "splitRootBranchLeft");                        // Left split stuck
    final Stuck r = new Stuck(P, "splitRootBranchRight");                       // Right split stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final int              midPoint = (maxStuckSize-1) / 2;                     // Mid point in parent

    p.stuckGetRoot();                                                           // Load branch root stuck from btree

    P.new Instruction()
     {void action()
       {if (p.size.registerGet() < maxStuckSize-1) stop("Root must be full");
       }
      void verilog(Verilog v) {v.A("");}
     };
    p.splitIntoThree(l, r, midPoint);                                           // Split the branch root in two down the middle
    allocateBranch(il); l.stuckPut(il);                                         // Allocate and save left branch
    allocateBranch(ir); r.stuckPut(ir);                                         // Allocate and save right branch
                                                                                // Update root with new children
    mk.Copy(p.keys[midPoint]);                                                  // Get splitting key
    p.Clear();                                                                  // Clear the root so we can add the left and right children to it.
    p.Push(mk, il);                                                             // Add reference to left child
    p.SetPastLastElement(mk, ir);                                               // Add reference to right child as top element past the end of the stuck
    p.stuckPut();                                                               // Save the root stuck back into the btree whence it came
   }

  private void splitLeafNotTop                                                  // Split a full leaf that is not the root and is not the last child of its parent branch which is not full
   (Process.Register ParentIndex, Process.Register StuckIndex)
   {Process     P = ParentIndex.registerProcess();
    final Stuck p = new Stuck(P, "splitLeafNotTopParent");                      // Parent stuck
    final Stuck c = new Stuck(P, "splitLeafNotTopChild");                       // Left split stuck
    final Stuck l = new Stuck(P, "splitLeafNotTopLeft");                        // Left split stuck
    final Stuck r = new Stuck(P, "splitLeafNotTopRight");                       // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key

    p.stuckGet(ParentIndex);                                                    // Load parent stuck from btree

    P.new Instruction()
     {void action()
       {ck.copy(p.keys[StuckIndex.registerGet()]);                              // Key of child
        cd.copy(p.data[StuckIndex.registerGet()]);                              // Index of child in btree
       }
      void verilog(Verilog v)
       {v.new Case(maxStuckSize, StuckIndex.registerName())
         {void Choice(int I)
           {ck.copy(v, p.keys[I]);                                              // Key of child
            cd.copy(v, p.data[I]);                                              // Index of child in btree
           }
         };
       }
     };
    c.stuckGet(cd);                                                             // Load child

    P.new Instruction()
     {void action()
       {if (p.isLeaf.registerGet() >  0)              stop("Parent must be a branch");
        if (p.size  .registerGet() >= maxStuckSize-1) stop("Parent must not be full");
        if (c.isLeaf.registerGet() == 0)              stop("Child must be a leaf");
        if (c.size  .registerGet() <  maxStuckSize)   stop("Child must be full");
       }
      void verilog(Verilog v) {v.A("");}
     };
    c.splitLow(l);                                                              // Split the leaf in two down the middle copying out the lower half
    allocateLeaf(il); c.stuckPut(cd);                                           // Allocate and save split out left of leaf
                      l.stuckPut(il);                                           // Save remainder of leaf

    l.LastElement();                                                            // Last element of left child
    c.FirstElement();                                                           // First element of right child
    mk.Average(l.Key, c.Key);                                                   // Mid key is average of surrounding keys
    p.InsertElementAt(StuckIndex, mk, il);                                  // Add reference to left child
    p.stuckPut();                                                               // Save the parent stuck back into the btree
   }

  private void splitLeafAtTop(Process.Register ParentIndex)                     // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   {Process     P = ParentIndex.registerProcess();
    final Stuck p = new Stuck(P, "splitLeafAtTopParent");                       // Parent which must be a branch which is not full
    final Stuck c = new Stuck(P, "splitLeafAtTopChild");                        // Child at index which must be a full leaf
    final Stuck l = new Stuck(P, "splitLeafAtTopLeft");                         // Left split of leaf
    final Process.Register ci  = P.register("childIndex" , btreeAddressSize);   // Btree index of child
    final Process.Register cl  = P.register("leftIndex" ,  btreeAddressSize);   // Btree index of left child of child
    final Process.Register mk  = P.register("midKey",            bitsPerKey);   // Mid key

    p.stuckGet(ParentIndex);                                                    // Load parent stuck from btree
    p.PastLastElement();                                                        // Get index of child
    ci.Copy(p.Data);                                                            // Index of child
    c.stuckGet(ci);                                                             // Load child from btree

    P.new Instruction()
     {void action()
       {if (p.isLeaf.registerGet() >  0)             stop("Parent must be branch");
        if (p.size  .registerGet() > maxStuckSize-1) stop("Parent must not be full"); // Branches are one less in size than leaves
        if (c.isLeaf.registerGet() == 0)             stop("Child must be a leaf");
        if (c.size.registerGet()   != maxStuckSize)  stop("Child must be a full");    // Leaves fill the stuck completely
       }
      void verilog(Verilog v) {v.A("");}
     };

    c.splitLow(l);                                                              // Split the leaf in two down the middle copying out the lower half
    allocateLeaf(cl); l.stuckPut(cl); c.stuckPut(ci);                           // Split leaf in two and save both parts

    l.LastElement();                                                            // Mid key
    c.FirstElement();
    mk.Average(l.Key, c.Key);
    p.Push(mk, cl);                                                             // Add reference to left child
    p.SetPastLastElement(mk, ci);                                               // Add reference to not split out top child on the right
    p.stuckPut();                                                               // Save the parent stuck back into the btree whence it came
   }

  private void splitBranchNotTop                                                // Split a full branch that is not the root and is not the last child of its parent branch which is not full
   (Process.Register ParentIndex, Process.Register StuckIndex)
   {Process     P = ParentIndex.registerProcess();
    final Stuck p = new Stuck(P, "splitBranchNotTopParent");                    // Parent stuck
    final Stuck c = new Stuck(P, "splitBranchNotTopChild");                     // Left split stuck
    final Stuck l = new Stuck(P, "splitBranchNotTopLeft");                      // Left split stuck
    final Stuck r = new Stuck(P, "splitBranchNotTopRight");                     // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key

    p.stuckGet(ParentIndex);                                                    // Load parent stuck from btree
    P.new Instruction()
     {void action()
       {ck.copy(p.keys[StuckIndex.registerGet()]);                              // Key of child
        cd.copy(p.data[StuckIndex.registerGet()]);                              // Data of child
       }
      void verilog(Verilog v)
       {v.new Case(maxStuckSize, StuckIndex.registerName())
         {void Choice(int I)
           {ck.copy(v, p.keys[I]);                                              // Key of child
            cd.copy(v, p.data[I]);                                              // Data of child
           }
         };
       }
     };
    c.stuckGet(cd);                                                             // Load child

    P.new Instruction()
     {void action()
       {if (p.isLeaf.registerGet() >  0) stop("Parent must be a branch");
        if (p.size  .registerGet() >= maxStuckSize-1) stop("Parent must mot be full");
        if (c.isLeaf.registerGet() >  0) stop("Child must be a branch");
        if (c.size  .registerGet() <  maxStuckSize-1) stop("Child must not full");
       }
      void verilog(Verilog v) {v.A("");}
     };

    c.splitLowButOne(l, ck);                                                    // Split the leaf in two down the middle copying out the lower half
    allocateBranch(il); l.stuckPut(il);                                         // Allocate and save left leaf
                        c.stuckPut(cd);                                         // Allocate and save left leaf
                                                                                // Update root with new children
    p.InsertElementAt(StuckIndex, ck, il);                                      // Add reference to left child
    p.stuckPut();                                                               // Save the parent stuck back into the btree
   }

  private void splitBranchAtTop(Process.Register ParentIndex)                   // Split a full branch that is not the root and is the last child of its parent branch which is not full
   {Process     P = ParentIndex.registerProcess();
    final Stuck p = new Stuck(P, "splitBranchAtTopParent");                     // Parent stuck
    final Stuck c = new Stuck(P, "splitBranchAtTopChild");                      // Left split stuck
    final Stuck l = new Stuck(P, "splitBranchAtTopLeft");                       // Left split stuck
    final Stuck r = new Stuck(P, "splitBranchAtTopRight");                      // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key

    p.stuckGet(ParentIndex);                                                    // Load parent stuck from btree

    p.PastLastElement();                                                        // Key of child
    cd.Copy(p.Data);                                                            // Reference to child in btree
    c.stuckGet(cd);                                                             // Load child from btree

    P.new Instruction()
     {void action()
       {if (p.isLeaf.registerGet() >  0) stop("Parent must be a branch");
        if (p.size.registerGet()   >= maxStuckSize-1) stop("Parent must not be full");
        if (c.isLeaf.registerGet() >  0) stop("Child must be a branch");
        if (c.size.registerGet()   <  maxStuckSize-1) stop("Child branch must be full");
       }
      void verilog(Verilog v) {v.A("");}
     };

    c.splitLowButOne(l, mk);                                                    // Split the leaf in two down the middle copying out the lower half
    allocateBranch(il); l.stuckPut(il);                                         // Allocate and save left leaf
                        c.stuckPut(cd);                                         // Allocate and save left leaf
                                                                                // Update root with new children
    p.Push(mk, il);                                                             // Add reference to left child
    p.SetPastLastElement(mk, cd);                                               // Add reference to not split top child on the right
    p.stuckPut();                                                               // Save the parent stuck back into the btree
   }

//D1 Merge                                                                      // Merge two nodes

  private void mergePermitted                                                   // Whether a  merge is permitted or not.
   (Process P, Process.Register ParentIndex, Stuck p, Process.Label end)        // Merge two leaves into the root
   {P.new Instruction(true)
     {void action()
       {final int s = ParentIndex.registerGet();
        final int r = p.size.registerGet();                                     // Size of parent stuck
        if      (s == 0 && r > 1) P.Continue();                                 // Can be used on root if there is more than one entry
        else if (s == 0 || r < 1) P.Goto(end);                                  // Cannot be used on root or on empty branches
        else P.Continue();
       }

      void verilog(Verilog v)                                                   // Whether a  merge is permitted or not.
       {final String  s = ParentIndex.registerName();
        final String  r = p.size.registerName();
        v    .new If (s + " == 0 && " + r + " > 1")                             // Can be used on root if there is more than one entry
         {void Then()
           {P.Continue(v);
           }
          void Else()
           {v.new If (s + " == 0 || " + r + " < 1")                             // Cannot be used on root or on empty branches
             {void Then()
               {P.Goto(v, end);
               }
              void Else()
               {P.Continue(v);
               }
             };
           }
         };
       }
     };
   }

  private Process.Register mergeLeavesIntoRoot(Process P)                       // Merge two leaves into the root
   {final Stuck p = new Stuck(P, "mergeLeavesIntoRootParent");                  // Parent stuck
    final Stuck l = new Stuck(P, "mergeLeavesIntoRootLeft");                    // Left split stuck
    final Stuck r = new Stuck(P, "mergeLeavesIntoRootRight");                   // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test
    //P.new Instruction() {void action() {say("AAAA 11 mergeLeavesIntoRoot");}};

    P.new Block()
     {void code()
       {success.Zero();                                                         // Assume failure
        p.stuckGetRoot();                                                       // Load root

        test.Eq(p.size, 1);                                                 // Number of entries in root
        P.GOZero(end, test);                                                    // Wrong number of entries in root

        P.new Instruction()                                                     // Check that the root has one entry and thus two children
         {void action()
           {il.copy(p.data[0]);                                                 // Index of left leaf
            ir.copy(p.data[1]);                                                 // Index of right leaf
           };
          void verilog(Verilog v)
           {il.copy(v, p.data[0]);                                              // Index of left leaf
            ir.copy(v, p.data[1]);                                              // Index of right leaf
           };
         };

        l.stuckGet(il);                                                         // Load left  leaf from btree
        r.stuckGet(ir);                                                         // Load right leaf from btree

        l.new IsLeaf()                                                          // Check that the children are leaves
         {void Leaf()
           {r.new IsLeaf()                                                      // Check that the children are leaves
             {void Leaf()
               {p.merge(l, r);                                                  // Merge leaves into root
                P.new If (p.MergeSuccess)                                       // Check that the root has one entry and thus two children
                 {void Then()
                   {p.isLeaf.One();
                    p.stuckPut(true);                                           // Save the modified root back into the tree
                    free(il); free(ir);                                         // Free left and right leaves as they are no longer needed
                    success.One();                                              // Success
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 11", btreePrint());}};
    return success;
   }

  private Process.Register mergeLeavesNotTop                                    // Merge the two consecutive leaves of a branch that is not the root. Neither of the leaves is the topmost leaf.
   (Stuck Stuck, Process.Register ParentIndex, Process.Register LeftLeaf)
   {final Process P = ParentIndex.registerProcess();
    //final Stuck   p = new Stuck(P, "mergeLeavesIntoRootParent");              // Parent stuck
    final Stuck   p = Stuck;
    final Stuck   l = new Stuck(P, "mergeLeavesIntoRootLeft");                  // Left split stuck
    final Stuck   r = new Stuck(P, "mergeLeavesIntoRootRight");                 // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test

    //P.new Instruction() {void action() {say("AAAA 22 mergeLeavesNotTop", ParentIndex, LeftLeaf);}};

    //p.stuckGet(ParentIndex);                                                  // Load parent
    P.new Block()
     {void code()
       {mergePermitted(P, ParentIndex, p, end);

        P.new Instruction()                                                     // Indexes of left and right leaves with the indicated child being the left leaf
         {void action()
           {il.copy(p.data[LeftLeaf.registerGet()]);                            // Get the btree index of the left child leaf
            ir.copy(p.data[LeftLeaf.registerGet() + 1]);                        // Get the btree index of the right child leaf
           }
          void verilog(Verilog v)
           {v.new Case(maxStuckSize-1, LeftLeaf.registerName())
             {void Choice(int i)
               {il.copy(v, p.data[i]);                                          // Get the btree index of the left child leaf
                ir.copy(v, p.data[i + 1]);                                      // Get the btree index of the right child leaf
               }
             };
           }
         };

        l.stuckGet(il);
        r.stuckGet(ir);

        l.new IsLeaf()                                                          // Check that the children are leaves
         {void Leaf()
           {r.new IsLeaf()
             {void Leaf()
               {l.merge(r);                                                     // Merge leaves into left child

                P.new If (l.MergeSuccess)                                       // Modify the parent only if the merge succeeded
                 {void Then()
                   {p.RemoveElementAt(LeftLeaf);                                // Remove the left child moving the right child down into its space
                    p.ElementAt(LeftLeaf);                                      // Get details of what was the right child
                    p.SetElementAt(LeftLeaf, p.Key, il);                        // Replace the right child with the left child
                    success.One();                                              // Success: The 95th Rifles - "First onto the field of battle, last off".
                    l.stuckPut();                                               // Save the merge into left child back into the tree
                    p.stuckPut();                                               // Save the parent back into the tree
                    free(ir);                                                   // Free right leaf as it is no longer in use
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 22", btreePrint());}};
    return success;
   }

  private Process.Register mergeLeavesAtTop                                     // Merge the top most two leaves of a branch that is not the root
   (Stuck Stuck, Process.Register ParentIndex)
   {final Process P = ParentIndex.registerProcess();
    //final Stuck   p = new Stuck(P, "mergeLeavesIntoRootParent");              // Parent stuck
    final Stuck   p = Stuck;                                                    // Parent stuck
    final Stuck   l = new Stuck(P, "mergeLeavesIntoRootLeft");                  // Left split stuck
    final Stuck   r = new Stuck(P, "mergeLeavesIntoRootRight");                 // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register sz = P.new Register("size",       stuckAddressSize); // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test

    //P.new Instruction() {void action() {say("AAAA 33 mergeLeavesAtTop", ParentIndex);}};
    P.new Instruction()                                                         // Assume we cannot merge
     {void action()           {success.zero();}
      void verilog(Verilog v) {success.zero(v);}
     };

    //p.stuckGet(ParentIndex);                                                  // Load parent

    P.new Block()
     {void code()
       {mergePermitted(P, ParentIndex, p, end);
        sz.Copy(p.size);                                                        // Index of left leaf known to be valid as the parent contains at least one entry resulting in two children

        P.new Instruction()
         {void action()
           {il.copy(p.data[sz.registerGet()-1]);                                // Get the btree index of the left child leaf
            ir.copy(p.data[sz.registerGet()]);                                  // Get the btree index of the right child leaf
           }
          void verilog(Verilog v)
           {v.new Case(1, maxStuckSize, sz.registerName())
             {void Choice(int i)
               {il.copy(v, p.data[i-1]);                                        // Get the btree index of the left child leaf
                ir.copy(v, p.data[i]);                                          // Get the btree index of the right child leaf
               }
             };
           }
         };

        l.stuckGet(il);
        r.stuckGet(ir);

        l.new IsLeaf()                                                          // Check that the children are leaves
         {void Leaf()                                                           // Children are leaves
           {r.new IsLeaf()                                                      // Check that the children are leaves
             {void Leaf()                                                       // Children are leaves
               {l.merge(r);                                                     // Merge leaves into left child
                P.new If (l.MergeSuccess)
                 {void Then()
                   {P.new Instruction()
                     {void action()
                       {p.size.dec();                                           // The left child is now topmost - we know this is ok because the parent has at elast one entry
                        success.one();
                       }
                      void verilog(Verilog v)
                       {p.size.dec(v);                                          // The left child is now topmost - we know this is ok because the parent has at elast one entry
                        success.one(v);
                       }
                     };
                    l.stuckPut();                                               // Save the modified left child back into the tree
                    p.stuckPut();                                               // Save the modified root back into the tree
                    free(ir);                                                   // Free right leaf as it is no longer in use
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 33", btreePrint());}};
    return success;
   }

  private Process.Register mergeBranchesIntoRoot(Process P)                     // Merge two branches into the root
   {final Stuck   p = new Stuck(P, "mergeLeavesIntoRootParent");                // Parent stuck
    final Stuck   l = new Stuck(P, "mergeLeavesIntoRootLeft");                  // Left split stuck
    final Stuck   r = new Stuck(P, "mergeLeavesIntoRootRight");                 // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register ls = P.new Register("leftChild",  stuckAddressSize); // Index in memory of the left stuck
    final Process.Register rs = P.new Register("rightChild", stuckAddressSize); // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test

    //P.new Instruction() {void action() {say("AAAA 44 mergeLeavesIntoRoot");}};

    p.stuckGetRoot();                                                           // Load parent

    P.new Block()
     {void code()
       {P.new Instruction(true)                                                 // Check that the root has one entry and thus two children
         {void action()
           {success.zero();                                                     // Assume failure
            if (p.size.registerGet() != 1) P.Goto(end); else P.Continue();
           };
          void verilog(Verilog v)
           {success.zero(v);                                                    // Assume failure
            v.new If (p.size.registerName() + " != 1")
             {void Then()
               {P.Goto(v, end);
               }
              void Else()
               {P.Continue(v);
               }
             };
           }
         };

        P.new Instruction()                                                     // Check that the root has one entry and thus two children
         {void action()
           {mk.copy(p.keys[0]);                                                 // Splitting key
            il.copy(p.data[0]);                                                 // Index of left branch
            ir.copy(p.data[1]);                                                 // Index of right branch
           }
          void verilog(Verilog v)
           {mk.copy(v, p.keys[0]);                                              // Splitting key
            il.copy(v, p.data[0]);                                              // Index of left branch
            ir.copy(v, p.data[1]);                                              // Index of right branch
           }
         };

        l.new IsLeaf()                                                          // Check that the children are leaves
         {void Branch()
           {r.new IsLeaf()
             {void Branch()
               {l.stuckGet(il);                                                 // Load left  branch from btree
                r.stuckGet(ir);                                                 // Load right branch from btree
                p.mergeButOne(l, mk, r);                                        // Merge left branch, splitting key, right branch into root
                P.new If (p.MergeSuccess)
                 {void Then()
                   {p.stuckPut();                                               // Save the modified root back into the tree
                    free(il); free(ir);                                         // Free left and right leaves as they are no longer needed
                    success.One();
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 44", btreePrint());}};
    return success;
   }

  private Process.Register mergeBranchesNotTop                                  // Merge the two consecutive child branches of a branch that is not the root. Neither of the child branches is the topmost leaf.
   (Stuck Stuck, Process.Register ParentIndex, Process.Register LeftBranch)
   {final Process P = ParentIndex.registerProcess();
    //final Stuck p = new Stuck(P, "mergeBranchesNotTopParent");                // Parent stuck
    final Stuck   p = Stuck;                                                    // Parent stuck
    final Stuck   l = new Stuck(P, "mergeBranchesNotTopLeft");                  // Left split stuck
    final Stuck   r = new Stuck(P, "mergeBranchesNotTopRight");                 // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register ls = P.new Register("leftChild",  stuckAddressSize); // Index in memory of the left stuck
    final Process.Register rs = P.new Register("rightChild", stuckAddressSize); // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test

    //P.new Instruction() {void action() {say("AAAA 55 mergeBranchesNotTop", ParentIndex, LeftBranch);}};

    //p.stuckGet(ParentIndex);                                                  // Load parent

    P.new Block()
     {void code()
       {mergePermitted(P, ParentIndex, p, end);
        success.Zero();                                                         // Assume failure

        P.new Instruction()                                                     // Check that the parent has a child at the specified index
         {void action()
           {il.copy(p.data[LeftBranch.registerGet()+0]);                        // Get the btree index of the left child branch
            ir.copy(p.data[LeftBranch.registerGet()+1]);                        // Get the btree index of the right child branch
           }
          void verilog(Verilog v)
           {v.new Case(maxStuckSize-1, LeftBranch.registerName())
             {void Choice(int i)
               {il.copy(v, p.data[i+0]);                                        // Get the btree index of the left child branch
                ir.copy(v, p.data[i+1]);                                        // Get the btree index of the right child branch
               }
             };
           }
         };

        l.stuckGet(il);                                                         // Load left  branch from btree
        r.stuckGet(ir);                                                         // Load right branch from btree

        l.new IsLeaf()                                                          // Check that the children are branches
         {void Branch()
           {r.new IsLeaf()                                                      // Check that the children are branches
             {void Branch()
               {P.new Instruction()                                             // Check that the parent has a child at the specified index
                 {void action()
                   {mk.copy(p.keys[LeftBranch.registerGet()]);                  // Key associated with left child branch
                   }
                  void verilog(Verilog v)
                   {v.new Case(maxStuckSize, LeftBranch.registerName())
                     {void Choice(int i)
                       {mk.copy(v, p.keys[i]);                                  // Key associated with left child branch
                       }
                     };
                   }
                 };

                l.mergeButOne(mk, r);                                           // Merge branches into left child

                P.new If (l.MergeSuccess)                                       // Modify the parent only if the merge succeeded
                 {void Then()                                                   // Check that the parent has a child at the specified index
                   {p.RemoveElementAt(LeftBranch);                              // Remove the left child
                    p.ElementAt(LeftBranch);                                    // Details of right child which has now been moved down into the position formerly occupied by the left child
                    p.SetElementAt(LeftBranch, p.Key, il);                      // Update left child position with key of right child and index of left child
                    success.One();                                              // Merge was successful
                    l.stuckPut();                                               // Save the modified left child back into the tree
                    p.stuckPut();                                               // Save the modified root back into the tree
                    free(ir);                                                   // Free right branch as it is no longer in use
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 55", btreePrint());}};
    return success;
   }

  private Process.Register mergeBranchesAtTop                                   // Merge the top most two child branches of a branch that is not the root
   (Stuck Stuck, Process.Register ParentIndex)
   {final Process P = ParentIndex.registerProcess();
    //final Stuck   p = new Stuck(P, "mergeLeavesIntoRootParent");              // Parent stuck
    final Stuck   p = Stuck;                                                    // Parent stuck
    final Stuck   l = new Stuck(P, "mergeLeavesIntoRootLeft");                  // Left split stuck
    final Stuck   r = new Stuck(P, "mergeLeavesIntoRootRight");                 // Right split stuck
    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck
    final Process.Register sz = P.new Register("size",       stuckAddressSize); // Index in memory of the left stuck
    final Process.Register cd = P.new Register("childData",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register il = P.new Register("indexLeft",  btreeAddressSize); // Index in memory of the left stuck
    final Process.Register ir = P.new Register("indexRight", btreeAddressSize); // Index in memory of the right stuck
    final Process.Register mk = P.new Register("midKey",     bitsPerKey);       // Mid key
    final Process.Register success = P.new Register("success", 1);              // Success of merge - the result of this operation
    final Process.Register test    = P.new Register("test",    1);              // A generic test

    //P.new Instruction() {void action() {say("AAAA 66 mergeBranchesAtTop", ParentIndex);}};

    //p.stuckGet(ParentIndex);                                                  // Load parent

    P.new Block()
     {void code()
       {mergePermitted(P, ParentIndex, p, end);
        success.Zero();                                                         // Assume failure
        sz.Copy(p.size);                                                        // Index of left branch known to be valid as the parent contains at least one entry resulting in two children

        P.new Instruction()                                                     // Check that the parent has a child at the specified index
         {void action()
           {il.copy(p.data[sz.registerGet()-1]);                                // Get the btree index of the left branch branch
            ir.copy(p.data[sz.registerGet()  ]);                                // Get the btree index of the right branch branch
           }
          void verilog(Verilog v)
           {v.new Case(1, maxStuckSize, sz.registerName())
             {void Choice(int i)
               {il.copy(v, p.data[i-1]);                                        // Get the btree index of the left branch branch
                ir.copy(v, p.data[i  ]);                                        // Get the btree index of the right branch branch
               }
             };
           }
         };

        l.stuckGet(il);                                                         // Load left  branch from btree
        r.stuckGet(ir);                                                         // Load right branch from btree

        l.new IsLeaf()                                                          // Check that the children are branches
         {void Branch()                                                         // Children are branches
           {r.new IsLeaf()                                                      // Check that the children are branches
             {void Branch()                                                     // Children are branches
               {p.Pop();
                l.mergeButOne(p.Key, r);                                        // Merge leaves into left child
                P.new If(l.MergeSuccess)                                        // Modify the parent only if the merge succeeded
                 {void Then()
                   {P.new Instruction()
                     {void action()
                       {success.one();
                        p.setPastLastElement(p.Key, il);                        // Make newly combined left branch top most
                       }
                      void verilog(Verilog v)
                       {success.one(v);
                        p.setPastLastElement(v, p.Key, il);                     // Make newly combined left branch top most
                       }
                     };

                    l.stuckPut();                                               // Save the modified left child back into the tree
                    p.stuckPut();                                               // Save the modified root back into the tree
                    free(ir);                                                   // Free right branch as it is no longer in use
                   }
                 };
               }
             };
           }
         };
       }
     };
    //P.new Instruction() {void action() {say("BBBB 66", btreePrint());}};
    return success;
   }

//D1 Find                                                                       // Find a key in a btree

  class Find extends Stuck                                                      // Find the leaf stuck associated with a key in the tree
   {final Process P;                                                            // Process to contain generated code
     Find(Process Process)
     {super(Process, "stuck");
      P = Process;
     }
    Stuck stuck() {return (Stuck)this;}                                         // Stuck found

    void findSearch(Process.Register Key)
     {BtreeIndex.Zero();                                                        // Start at the root

      P.new Block()
       {void code()
         {stuckGet(BtreeIndex);                                                 // Load current stuck
          new IsLeaf()
           {void Leaf()                                                         // At a leaf - search for exact match
             {search_eq_parallel(Key);                                          // Search
              P.GOto(end);                                                      // Key not present
             }
            void Branch()                                                       // On a branch - step to next level down
             {search_le_parallel(Key);                                          // Search stuck for matching key
              BtreeIndex.Copy(Data);                                            // Data found at index
              P.GOto(start);                                                    // Key not present
             }
           };
         };
       };
     }
   } // Find

//D1 Insertion                                                                  // Insert a key, data pair into the tree if there is room for it or update and existing key with a new datum

  class FindAndInsert extends Find                                              // Find the leaf stuck that should contain this key and insert or update it if possible
   {final Process P;

    FindAndInsert(Process Process)
     {super(Process);
      P = Process;
     }

    Stuck stuck() {return (Stuck)this;}

    void findAndInsert(Process.Register Key, Process.Register Data)             // Find the leaf stuck that should contain this key and insert or update it if possible
     {final Process.Register i       = P.new Register("i", stuckAddressSize);
      final Process.Register notFull = P.new Register("notFull", 1);            // The leaf is not full

      P.new Block()
       {void code()
         {findSearch(Key);                                                      // Find the leaf that should contain the key and possibly the key.

          P.new If (Found)                                                      // Found the key in the leaf so update it with the new data
           {void Then()
             {SetElementAt(StuckIndex, Key, Data);
              P.Continue();
             }
            void Else()                                                         // Key does not exiust in leaf
             {notFull.Lt(size, maxStuckSize);

              P.new If (notFull)                                                // Leaf not full so we can insert into this leaf
               {void Then()                                                     // Position in leaf - we know it is not present and thatthere is room for the key, data pair in the leaf
                 {search_le_parallel(Key);                                               // Find pinsert position in leaf
                  InsertElementAt(StuckIndex, Key, Data);                       // Insert into leaf
                  Found.One();                                                  // Show success
                  P.COntinue();                                                 // Finished
                 }
                void Else()
                 {P.GOto(end);                                                  // No insertion so no need to update memory
                 }
               };
             }
           };
          stuckPut(false);                                                      // Stuck modified so write it back into memory
         }
       };
     }
   } // FindAndInsert

  public void put(Process.Register Key, Process.Register Data)                  // Insert a key, data pair into the tree or update and existing key with a new datum
   {final Process          P     = Key.registerProcess();                       // Process in which to write code
    final Stuck            S     = new Stuck(P, "putParent");                   // Parent stuck
    final Stuck            s     = new Stuck(P, "putChild");                    // Child stuck
    final FindAndInsert    f     = new FindAndInsert(P);                        // Find and insert
    final Process.Register c     = P.new Register("child",  btreeAddressSize);  // Current child in tree
    final Process.Register p     = P.new Register("parent", btreeAddressSize);  // Parent of current child in tree
    final Process.Register ci    = P.new Register("childInparent", stuckAddressSize); // Position of child in parent
    final Process.Register found = P.new Register("found",  1);                 // Whether the child was found in its parent (true) or defaulted to top (false)
    final Process.Register full  = P.new Register("full", 1);                   // Whether a stuck is full or not

    f.findAndInsert(Key, Data);                                                 // Try direct insertion with no modifications to the shape of the tree

    P.new Block()                                                               // The block is left as soon as possible
     {void code()
       {final Process.Label oStart = start, oEnd = end;
        P.GONotZero(oEnd, f.Found);                                             // Direct insertion succeeded so nothing more to do

        P.new If(f.BtreeIndex)                                                  // Failed to insert because the root is a leaf which must be full else the operation would have succeeded
         {void Else()
           {splitRootLeaf(P);                                                   // Split the leaf root to make room
            f.findAndInsert(Key, Data);                                         // Splitting a leaf root will make more space in the tree so this operation will now succeed
            P.GOto(oEnd);                                                       // Direct insertion succeeded so nothing more to do
           }
         };

        S.stuckGetRoot();                                                       // Start at the root now known to be a split branch

        full.Ge(S.size, maxStuckSize-1);

        P.new If (full)                                                         // If root branch is full split it using the dedicated method and restart
         {void Then()
           {splitRootBranch(P);                                                 // Split the full branch root
            P.GOto(start);                                                      // Restart descent to make sure we are on the right path
           }
         };

        p.Zero();

        P.new Block()                                                           // Step down through the tree by iterating this block
         {void code()
           {final Process.Label loopStart = start, loopEnd = end;
            S.stuckGet(p);
            S.search_le_parallel(Key);                                          // Step down
            P.new Instruction()
             {void action()
               {c.copy(S.Data);    ci.copy(S.StuckIndex);    found.copy(S.Found);
               }
              void verilog(Verilog v)
               {c.copy(v, S.Data); ci.copy(v, S.StuckIndex); found.copy(v, S.Found);
               }
             };

            s.stuckGet(c);                                                      // Child - inefficient way to find out if it is a leaf or not

            s.new IsLeaf()
             {void Leaf()                                                       // At a leaf - search for exact match
               {full.Ge(s.size, maxStuckSize);

                P.new If (full)
                 {void Then()
                   {P.new If (found)
                     {void Then()                                               // Split the child leaf known not to be top under its parent
                       {splitLeafNotTop(p, ci);
                       }
                      void Else()
                       {splitLeafAtTop(p);                                      // Split the child leaf known to be top under its parent
                       }
                     };
                   }
                 };
                f.findAndInsert(Key, Data);                                     // Must be insertable now because we have split everything in the path of the key
                P.GOto(loopEnd);                                                // On leaf so descent has been completed
               }

              void Branch()                                                     // On a branch
               {full.Ge(s.size, maxStuckSize-1);

                P.new If (full)
                 {void Then()                                                   // Full branch
                   {P.new If (found)                                            // Was the child found within its parent or is it top
                     {void Then()
                       {splitBranchNotTop(p, ci);                               // Split the child branch known not to be top
                       }
                      void Else ()
                       {splitBranchAtTop(p);                                    // Split the child branch known to be top
                       }
                     };
                   }                                                            // No split occurred so we can safely step down (from the heights)
                  void Else()
                   {p.Copy(c);                                                  // Step down from parent to child
                   }
                 };

                P.GOto(loopStart);                                              // Process next level of tree
               }
             };
           }
         };
       }
     };

    if (!suppressMerge) merge(Key);                                             // Merge along path to key
   }

  public void merge(Process.Register Key)                                       // Merge stucks on either side of the path to the key
   {final Process          P           = Key.registerProcess();
    final Stuck            S           = new Stuck(P, "merge");
    final Process.Register s           = P.new Register("position", btreeAddressSize); // Current position in the btree
    final Process.Register stuckIndex  = P.new Register("index",    stuckAddressSize); // Position within current stuck
    final Process.Register stuckIndex1 = P.new Register("index1",   stuckAddressSize); // One step left of the current position
    final Process.Register within      = P.new Register("within",   1);         // Success of merge - the result of this operation
    final Process.Register isLeaf      = P.new Register("isLeaf",   1);         // Success of merge - the result of this operation

    P.new Block()                                                               // The block is left as soon as possible
     {void code()
       {s.Zero();                                                               // Position in btree

        S.stuckGetRoot();                                                       // Load current stuck

        S.new IsLeaf()                                                          // Root is a leaf - nothing to merge
         {void Leaf()
           {P.GOto(end);
           }
         };
        P.new If (mergeLeavesIntoRoot(P))                                       // Try merging leaves into root
         {void Then()
           {P.GOto(end);                                                        // The root is now a leaf so there is nothing else to do
           }
         };

        P.new If (mergeBranchesIntoRoot(P))                                     // Try merging branches into root
         {void Then()
           {S.stuckGetRoot();                                                   // Reload root if the merge was successful
           }
         };

        P.new Block()                                                           // Step down through tree
         {void code()
           {mergeLeavesAtTop  (S, s);                                       // Try merging leaves at top into parent -  this forces non top siblings into top
            mergeBranchesAtTop(S, s);                                       // Try merging branches at top into parent -  this forces non top siblings into top
            S.stuckGet(s);
            S.search_le_parallel(Key);                                          // Step down from parent to child

            P.new If (S.Found)                                                  // Found the key in the body of the stuck
             {void Then()
               {P.new If (S.StuckIndex)                                         // Found the key in the body of the stuck
                 {void Then()
                   {stuckIndex1.Copy(S.StuckIndex);                             // Try merging to the right of the key
                    stuckIndex1.Inc();

                    within.Lt(stuckIndex1, S.size);
                    P.new If (within)                                           // There is a sibling to the right
                     {void Then()
                       {mergeLeavesNotTop  (S, s, stuckIndex1);
                        mergeBranchesNotTop(S, s, stuckIndex1);
                       }
                     };
                    mergeLeavesNotTop  (S, s, S.StuckIndex);                    // Merge around key
                    mergeBranchesNotTop(S, s, S.StuckIndex);

                    stuckIndex1.Copy(S.StuckIndex);                             // Try merging left of the key
                    stuckIndex1.Dec();
                    mergeLeavesNotTop  (S, s, stuckIndex1);
                    mergeBranchesNotTop(S, s, stuckIndex1);
                   }
                  void Else()
                   {stuckIndex1.registerSet(1);
                    mergeLeavesNotTop  (S, s, stuckIndex1);
                    mergeBranchesNotTop(S, s, stuckIndex1);

                    mergeLeavesNotTop  (S, s, S.StuckIndex);                    // Merge around first key
                    mergeBranchesNotTop(S, s, S.StuckIndex);
                   }
                 };
               }
              void Else()                                                       // At the top of the stuck
               {P.new If(S.size)
                 {void Then()
                   {stuckIndex1.Copy(S.size);
                    stuckIndex1.Dec();

                    //mergeLeavesNotTop  (S, s, stuckIndex1);                     // Try merging last leleaves not at top into parent
                    //mergeBranchesNotTop(S, s, stuckIndex1);                     // Try merging branches not at top into parent

                    P.new If(stuckIndex1)
                     {void Then()
                       {stuckIndex1.Dec();
                        mergeLeavesNotTop  (S, s, stuckIndex1);                 // Try merging leaves not at top into parent
                        mergeBranchesNotTop(S, s, stuckIndex1);                 // Try merging branches not at top into parent
                       }
                     };
                   }
                 };
               }
             };

            S.stuckGet(s);                                                      // Reload in case any changes have been made
            S.search_le_parallel(Key);                                          // Step down from parent to child
            s.Copy(S.Data);                                                     // Index of child

            S.stuckGet(s);                                                      // Load child

            S.new IsLeaf()                                                      // Child is a leaf or a branch
             {void Leaf()                                                       // At a leaf - end of merging
               {P.GOto(end);
               }
              void Branch()                                                     // Child is a branch - try again
               {P.GOto(start);
               }
             };
           };
         };
       }
     };
   }

  public void mergeSquared(Process.Register Key)                                // Merge stucks on either side of the path to the key
   {final Process          P          = Key.registerProcess();
    final Stuck            S          = new Stuck(P, "merge");
    final Process.Register s          = P.new Register("position", btreeAddressSize); // Current position in the btree
    final Process.Register stuckIndex = P.new Register("index",    stuckAddressSize); // Position within current stuck
    final Process.Register within     = P.new Register("within",   1);          // Success of merge - the result of this operation
    final Process.Register isLeaf     = P.new Register("isLeaf",   1);          // Success of merge - the result of this operation

    P.new Block()                                                               // The block is left as soon as possible
     {void code()
       {s.Zero();

        S.stuckGetRoot();                                                       // Load current stuck

        S.new IsLeaf()                                                          // Root is a leaf - nothing to merge
         {void Leaf()
           {P.GOto(end);
           }
         };
        P.new If (mergeLeavesIntoRoot(P))                                       // Try merging leaves into root
         {void Then()
           {P.GOto(end);                                                        // The root is now a leaf so there is nothing else to do
           }
         };

        P.new If (mergeBranchesIntoRoot(P))                                     // Try merging branches into root
         {void Then()
           {S.stuckGetRoot();                                                   // Reload root if the merge was successful
           }
         };

        P.new Block()                                                           // Step down through tree
         {void code()
           {mergeLeavesAtTop  (S, s);                                           // Try merging leaves at top into parent -  this forces non top siblings into top
            mergeBranchesAtTop(S, s);                                           // Try merging branches at top into parent -  this forces non top siblings into top

            for (int i = maxStuckSize-2; i >= 0; i--)                           // Each pair of sibling stucks from high to low so that any merges do not affect the current position
             {final int I = i;
              S.stuckGet(s);                                                    // Reload after any  changes
              P.new Instruction()                                               // Check we are in the active body of the stuck
               {void action()
                 {stuckIndex.registerSet(I);
                  within.ge(S.size, I+1);
                 }
                void verilog(Verilog v)
                 {stuckIndex.registerSet(v, I);
                  within.ge(v, S.size, I+1);
                 }
               };

              P.new If (within)                                                 // Within body of stuck
               {void Then()
                 {mergeLeavesNotTop  (S, s, stuckIndex);                        // Try merging leaves not at top into parent
                  mergeBranchesNotTop(S, s, stuckIndex);                        // Try merging branches not at top into parent
                 }
               };
             }

            S.stuckGet(s);                                                      // Reload in case any changes have been made

            S.search_le_parallel(Key);                                          // Step down from parent to child
            s.Copy(S.Data);                                                     // Index of child

            S.stuckGet(s);                                                      // Load child

            S.new IsLeaf()                                                      // Child is a leaf or a branch
             {void Leaf()                                                       // At a leaf - end of merging
               {P.GOto(end);
               }
              void Branch()                                                     // Child is a branch - try again
               {P.GOto(start);
               }
             };
           };
         };
       }
     };
   }

//D1 Deletion                                                                   // Delete a key data pair from the btree returning the data associated with the key

  private Find delete(Process.Register Key)                                     // Find the leaf that contains this key and delete it
   {final Process P = Key.registerProcess();
    final Find    f = new Find(P);

    P.new Block()
     {void code()
       {f.findSearch(Key);                                                      // Find the leaf that should contain the key
        P.new If (f.Found)                                                      // Found the key in the leaf so remove it
         {void Then()
           {P.new Instruction()
             {void action()
               {f.removeElementAt(f.StuckIndex);                                // Remove the key
               }
              void verilog(Verilog v)
               {f.removeElementAt(v, f.StuckIndex);                             // Remove the key
               }
             };
            f.stuckPut();                                                       // Save modified stuck back into btree
            merge(Key);                                                         // Merge along key path
           }
         };
       }
     };
    return f;
   }

//D1 Tests                                                                      // Test the btree

  final static int[]random_32 = {12, 3, 27, 1, 23, 20, 8, 18, 2, 31, 25, 16, 13, 32, 11, 21, 5, 24, 4, 10, 26, 30, 9, 6, 29, 17, 28, 15, 14, 19, 7, 22};

  static void test_create1()
   {final Btree   b = new Btree(1, 4, 8, 8);
    final Process P = b.new Process("test");
    b.stuckIsLeaf .memorySet(1, 0);
    b.stuckSize   .memorySet(2, 0);
    b.stuckKeys.memorySet(2, 0, 0); b.stuckData.memorySet(3, 0, 0);
    b.stuckKeys.memorySet(4, 0, 1); b.stuckData.memorySet(5, 0, 1);

    Stuck s = b.new Stuck(P, "Stuck");
    s.stuckGetRoot();

    b.maxSteps = 200;
    b.chipRunJava();                                                            // We are creating the tree not testing it

    //stop(s);
    ok(s, """
Stuck: Stuck size: 2, leaf: 1, root
 0     2 =>    3
 1     4 =>    5
""");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.isLeaf .registerSet(0);
        s.size   .registerSet(1);
        s.keys[0].registerSet(3); s.data[0].registerSet(4);
       }
     };
    s.stuckPut(true);

    //stop(b.chipPrintMemory());
    ok(b.chipPrintMemory(), """
Chip: Btree            step: 8, maxSteps: 200, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1
    stuckIsFree
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0
    freeNext
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0
    stuckSize
      Memory: size:  1, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        2
    stuckKeys
      Memory: size:  1, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        2  4  0  0
    stuckData
      Memory: size:  1, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        3  5  0  0
""");

    b.chipRunJava();

    //stop(b.chipPrintMemory());
    ok(b.chipPrintMemory(), """
Chip: Btree            step: 14, maxSteps: 200, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0
    stuckIsFree
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0
    freeNext
      Memory: size:  1, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0
    stuckSize
      Memory: size:  1, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1
    stuckKeys
      Memory: size:  1, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        3  4  0  0
    stuckData
      Memory: size:  1, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        4  5  0  0
""");
   }

  static Btree test_create2()
   {final Btree b = new Btree(2, 4, 8, 8);

    //stop(b.chipPrintMemory());
    ok(b.chipPrintMemory(), """
Chip: Btree            step: 0, maxSteps: 10, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  2, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  0
    stuckIsFree
      Memory: size:  2, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  1
    freeNext
      Memory: size:  2, width:  2, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  0
    stuckSize
      Memory: size:  2, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0
    stuckKeys
      Memory: size:  2, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0
    stuckData
      Memory: size:  2, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0
""");
    return b;
   }

  static Btree test_push() {return test_push(4, 8);}

  static Btree test_push(int S, int F)
   {final int B = 8, K = 8, D = 8;
    final Btree   b = new Btree(B, F, K, D);
    final Process P = b.new Process("Stuck");
    final String[]names = {"Stuck", "left", "right"};

    for (int j = 0; j < names.length; j++)
     {final int J = j;
      P.processClear();
      final Stuck s = b.new Stuck(P, names[j]);
      final Process.Register k = s.Key;
      final Process.Register d = P.register("d", D);
      final Process.Register n = P.register("n", b.btreeAddressSize);

      n.RegisterSet(J);
      s.stuckGet(n);

      for (int i = 0; i < S; i++)
       {final int I = i;
        P.new Instruction()
         {void action()
           {k.registerSet(I); d.registerSet(I+1);
           }
          void verilog(Verilog v)
           {k.registerSet(v, I); d.registerSet(v, I+1);
           }
         };
        P.new Instruction()
         {void action()
           {s.push(k, d);
           }
          void verilog(Verilog v)
           {s.push(v, k, d);
           }
         };
       }
      s.stuckPut();

      b.maxSteps = 100;
      b.chipRunJava();
     }
    return b;
   }

  static void test_clear()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "root");
    P.processTrace = true;
    s.stuckGetRoot();
    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: root size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
""");

    P.processClear();
    P.new Instruction()
     {void action()
       {s.clear();
       }
      void verilog(Verilog v)
       {s.clear(v);
       }
     };
    b.maxSteps = 100;
    b.chipRun();

    //stop(s);
    ok(s, """
Stuck: root size: 0, leaf: 0, root
""");
   }

  static Btree test_pop()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "Stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };

    b.maxSteps = 100;
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 3, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 3
FoundKey  : 0
Data      : 4
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
     return b;
   }

  static void test_pop_4()
   {sayCurrentTestName();
    final Btree   b = test_push(4, 4);
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "Stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };
    s.stuckPut();
    b.maxSteps = 100;
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 3, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 3
FoundKey  : 0
Data      : 4
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };
    s.stuckPut();
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 2, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 2
FoundKey  : 0
Data      : 3
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };
    s.stuckPut();
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 1, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 1
FoundKey  : 0
Data      : 2
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };
    s.stuckPut();
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 0, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 1
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
   }

  static void test_firstLastPast()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.firstElement();
       }
      void verilog(Verilog v)
       {s.firstElement(v);
       }
     };

    b.maxSteps = 100;
    b.chipRun();
    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 1
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
    ok(d.registerGet(), 1);

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.lastElement();
       }
      void verilog(Verilog v)
       {s.lastElement(v);
       }
     };

    b.chipRun();

    ok(k.registerGet(), 3);
    ok(d.registerGet(), 4);

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
        k.registerSet(5); d.registerSet(55);
       }
      void verilog(Verilog v)
       {s.pop(v);
        k.registerSet(v, 5); d.registerSet(v, 55);
       }
     };
    P.new Instruction()
     {void action()
       {s.setPastLastElement(k, d);
       }
      void verilog(Verilog v)
       {s.setPastLastElement(v, k, d);
       }
     };

    s.stuckPut();
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 3, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     5 =>   55
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 5
FoundKey  : 0
Data      : 55
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pastLastElement();
       }
      void verilog(Verilog v)
       {s.pastLastElement(v);
       }
     };

    b.chipRun();

    ok(k.registerGet(),  5);
    ok(d.registerGet(), 55);
   }

  static void test_elementAt()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register i = P.register("i", b.stuckAddressSize);
    final StringBuilder    S = new StringBuilder();
    P.processTrace = true;

    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
      P.processClear();
      s.stuckGetRoot();
      i.RegisterSet(J);
      P.new Instruction()
       {void action()
         {s.elementAt(i);
         }
        void verilog(Verilog v)
         {s.elementAt(v, i);
         }
       };

      b.maxSteps = 100;
      b.chipRun();

      S.append(s.dump());
     }
    ok(S, """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 1
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 1
FoundKey  : 0
Data      : 2
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 2
FoundKey  : 0
Data      : 3
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 3
FoundKey  : 0
Data      : 4
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
   }

  static void test_setElementAt()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.stuckAddressSize);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    final int N = 4;
    for (int j = 0; j <= N; j++)
     {final int J = j;
      P.new Instruction()
       {void action()
         {i.registerSet(J);
          k.registerSet(J+1);
          d.registerSet((J+1)*2);
         }
        void verilog(Verilog v)
         {i.registerSet(v, J);
          k.registerSet(v, J+1);
          d.registerSet(v, (J+1)*2);
         }
       };
      P.new Instruction()
       {void action()
         {s.setElementAt(i, k, d);
         }
        void verilog(Verilog v)
         {s.setElementAt(v, i, k, d);
         }
       };
     }

    b.maxSteps = 100;
    b.chipRun();
    ok(s, """
Stuck: stuck size: 5, leaf: 1, root
 0     1 =>    2
 1     2 =>    4
 2     3 =>    6
 3     4 =>    8
 4     5 =>   10
""");
   }

  static void test_setDataAt()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.stuckAddressSize);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
      P.new Instruction()
       {void action()
         {i.registerSet(J);
          d.registerSet((J+1)*2);
         }
        void verilog(Verilog v)
         {i.registerSet(v, J);
          d.registerSet(v, (J+1)*2);
         }
       };
      P.new Instruction()
       {void action()
         {s.setDataAt(i, d);
         }
        void verilog(Verilog v)
         {s.setDataAt(v, i, d);
         }
       };
     }

    b.maxSteps = 100;
    b.chipRun();
    ok(s, """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    2
 1     1 =>    4
 2     2 =>    6
 3     3 =>    8
""");
   }

  static void test_setPastLastElement()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {k.registerSet(5);
        d.registerSet(55);
       }
      void verilog(Verilog v)
       {k.registerSet(v, 5);
        d.registerSet(v, 55);
       }
     };
    P.new Instruction()
     {void action()
       {s.setPastLastElement(k, d);
       }
      void verilog(Verilog v)
       {s.setPastLastElement(v, k, d);
       }
     };

    b.maxSteps = 100;
    b.chipRun();
    //stop(s.dump());
    ok(""+s.dump(), """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     5 =>   55
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
   }

  static void test_insertElementAt()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.stuckAddressSize);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {i.registerSet(1);
        k.registerSet(5);
        d.registerSet(55);
       }
     void verilog(Verilog v)
       {i.registerSet(v, 1);
        k.registerSet(v, 5);
        d.registerSet(v, 55);
       }
     };
    P.new Instruction()
     {void action()
       {s.insertElementAt(i, k, d);
       }
     void verilog(Verilog v)
       {s.insertElementAt(v, i, k, d);
       }
     };

    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 5, leaf: 1, root
 0     0 =>    1
 1     5 =>   55
 2     1 =>    2
 3     2 =>    3
 4     3 =>    4
""");
   }

  static void test_removeElementAt()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    final Process.Register i = P.register("i", b.stuckAddressSize);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    i.RegisterSet(1);
    P.new Instruction()
     {void action()
       {s.removeElementAt(i);
       }
      void verilog(Verilog v)
       {s.removeElementAt(v, i);
       }
     };

    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 3, leaf: 1, root
 0     0 =>    1
 1     2 =>    3
 2     3 =>    4
""");
    ok(k.registerGet(), 1);
    ok(d.registerGet(), 2);
   }

  static void test_search_eq()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    final Process.Register i = s.StuckIndex;
    final Process.Register f = s.Found;
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();

    k.RegisterSet(11);

    s.Search_eq(k);

    b.maxSteps = 100;
    b.chipRun();
    ok(f.registerGet(), 0);

    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
      P.processClear();
      s.stuckGetRoot();
      k.RegisterSet(J);
      s.Search_eq(k);

      b.maxSteps = 100;
      b.nonBlockingAssignment = false;
      b.chipRun();
      ok(f.registerGet(), 1);
      ok(i.registerGet(), J);
      ok(k.registerGet(), J);
      ok(d.registerGet(), J+1);
     }
   }

  static void test_search_eq_parallel()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    final Process.Register i = s.StuckIndex;
    final Process.Register f = s.Found;
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();

    k.RegisterSet(11);

    s.Search_eq(k);

    b.maxSteps = 100;
    b.chipRun();
    ok(f.registerGet(), 0);

    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
      P.processClear();
      s.stuckGetRoot();
      k.RegisterSet(J);
      s.search_eq_parallel(k);

      b.maxSteps = 100;
      b.chipRun();
      ok(f.registerGet(), 1);
      ok(i.registerGet(), J);
      ok(k.registerGet(), J);
      ok(d.registerGet(), J+1);
     }
   }

  static void test_search_le()
   {sayCurrentTestName();
    final int B = 8, S = 4, K = 8, D = 8;
    final Btree   b = new Btree(B, S+S, K, D);
    final Process P = b.new Process("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register l = P.register("l", K);
    final Process.Register d = P.register("d", D);
    final Process.Register i = P.register("i", b.stuckAddressSize);
    final Process.Register f = P.register("f", 1);
    P.processTrace = true;

    s.stuckGetRoot();
    for (int j = 0; j < S; j++)
     {final int J = j*10;
      k.RegisterSet(J); d.RegisterSet(J+1);

      s.Push(k, d);
     }
    s.stuckPut();
    b.maxSteps = 300;
    b.chipRun();
    ok(s, """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
""");

    P.processClear();
    s.stuckGetRoot();
    k.RegisterSet(11);
    s.Search_le(k);

    b.maxSteps = 100;
    b.nonBlockingAssignment = false;
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 1
Key       : 11
FoundKey  : 20
Data      : 21
BtreeIndex: 0
StuckIndex: 2
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    k.RegisterSet(21);
    s.Pop();
    s.Search_le(k);

    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 3, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 30
FoundKey  : 0
Data      : 31
BtreeIndex: 0
StuckIndex: 3
Merge     : 0
""");
   }

  static void test_search_le_parallel()
   {sayCurrentTestName();
    final int B = 8, S = 4, K = 8, D = 8;
    final Btree   b = new Btree(B, S+S, K, D);
    final Process P = b.new Process("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register l = P.register("l", K);
    final Process.Register d = P.register("d", D);
    final Process.Register i = P.register("i", b.stuckAddressSize);
    final Process.Register f = P.register("f", 1);
    P.processTrace = true;

    s.stuckGetRoot();
    for (int j = 0; j < S; j++)
     {final int J = j*10;
      k.RegisterSet(J); d.RegisterSet(J+1);

      s.Push(k, d);
     }
    s.stuckPut();
    b.maxSteps = 300;
    b.chipRun();
    ok(s, """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
""");

    P.processClear();
    s.stuckGetRoot();
    k.RegisterSet(11);
    s.search_le_parallel(k);

    b.maxSteps = 100;
    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 1
Key       : 11
FoundKey  : 20
Data      : 21
BtreeIndex: 0
StuckIndex: 2
Merge     : 0
""");

    P.processClear();
    s.stuckGetRoot();
    k.RegisterSet(21);
    s.Pop();
    s.Search_le(k);

    b.chipRun();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 3, leaf: 1, root
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
Found     : 0
Key       : 30
FoundKey  : 0
Data      : 31
BtreeIndex: 0
StuckIndex: 3
Merge     : 0
""");
   }

  static void test_splitIntoTwo()
   {sayCurrentTestName();
    final Btree            b = test_push(4, 4);
    final Process          P = b.processes.get("Stuck");
    final Process.Register L = P.new Register("Left",  8);
    final Process.Register R = P.new Register("Right", 8);
    final Stuck            s = b.new Stuck(P, "stuck");
    final Stuck            l = b.new Stuck(P, "left");
    final Stuck            r = b.new Stuck(P, "right");

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {L.registerSet(1);
        R.registerSet(2);
       }
      void verilog(Verilog v)
       {L.registerSet(v, 1);
        R.registerSet(v, 2);
       }
     };
    l.stuckGet(L);
    r.stuckGet(R);
    s.splitIntoTwo(l, r);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 4, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
""");
    //stop(l);
    ok(l, """
Stuck: left size: 2, leaf: 0, index: 1
 0     0 =>    1
 1     1 =>    2
""");
    //stop(r);
    ok(r, """
Stuck: right size: 2, leaf: 0, index: 2
 0     2 =>    3
 1     3 =>    4
""");
   }

  static void test_splitIntoThree()
   {sayCurrentTestName();
    final Btree            b = test_push(4,4);
    final Process          P = b.processes.get("Stuck");
    final Process.Register L = P.new Register("Left",  8);
    final Process.Register R = P.new Register("Right", 8);
    final Stuck            s = b.new Stuck(P, "stuck");
    final Stuck            l = b.new Stuck(P, "left");
    final Stuck            r = b.new Stuck(P, "right");
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {L.registerSet(1);
        R.registerSet(2);
       }
      void verilog(Verilog v)
       {L.registerSet(v, 1);
        R.registerSet(v, 2);
       }
     };
    l.stuckGet(L);
    r.stuckGet(R);

    P.new Instruction()
     {void action()
       {s.size.dec();
       }
      void verilog(Verilog v)
       {s.size.dec(v);
       }
     };

    s.splitIntoThree(l, r, 1);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 3, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
    //stop(l.dump());
    ok(l.dump(), """
Stuck: left size: 1, leaf: 0, index: 1
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
    //stop(r.dump());
    ok(r.dump(), """
Stuck: right size: 1, leaf: 0, index: 2
 0     2 =>    3
 1     1 =>    4
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
   }

  static void test_splitLow()
   {sayCurrentTestName();
    final Btree            b = test_push(4, 4);
    final Process          P = b.processes.get("Stuck");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Stuck            l = b.new Stuck(P, "left");
    final Stuck            r = b.new Stuck(P, "right");
    final Process.Register L = P.new Register("Left",  8);
    final Process.Register R = P.new Register("Right", 8);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();
    s.size.Zero();

    for (int i = 0; i < b.maxStuckSize; i++)
     {final int I = i;
      k.RegisterSet(I); d.RegisterSet(I+1);
      s.Push(k, d);
     }

    L.RegisterSet(1);
    R.RegisterSet(2);

    l.stuckGet(L);
    r.stuckGet(R);

    s.splitLow(l);

    b.maxSteps = 100;

    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 2, leaf: 1, root
 0     2 =>    3
 1     3 =>    4
""");
    //stop(l);
    ok(l, """
Stuck: left size: 2, leaf: 0, index: 1
 0     0 =>    1
 1     1 =>    2
""");
   }

  static void test_splitLowButOne()
   {sayCurrentTestName();
    final Btree   b = test_push(4, 4);
    final Process P = b.processes.get("Stuck");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Stuck            l = b.new Stuck(P, "left");
    final Stuck            r = b.new Stuck(P, "right");
    final Process.Register L = P.new Register("Left",  8);
    final Process.Register R = P.new Register("Right", 8);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();
    s.size.Zero();

    for (int i = 0; i < b.maxStuckSize; i++)
     {final int I = i;
      k.RegisterSet(I); d.RegisterSet(I+1); s.Push(k, d);
     }

    L.RegisterSet(1); R.RegisterSet(2); s.Pop();

    l.stuckGet(L);
    r.stuckGet(R);

    s.splitLowButOne(l, k);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 1, leaf: 1, root
 0     2 =>    3
 1     3 =>    4
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 3
FoundKey  : 0
Data      : 4
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");

    //stop(l.dump());
    ok(l.dump(), """
Stuck: left size: 1, leaf: 0, index: 1
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 0
""");
   }

  static void test_merge()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Btree   B = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Stuck   S = b.new Stuck(P, "Stuck");
    final Process.Register O = P.new Register("One",  8);
    final Process.Register o = P.register("o", 8);

    P.processClear();
    s.stuckGetRoot();

    P.new Instruction()
     {void action()
       {O.registerSet(1);
       }
      void verilog(Verilog v)
       {O.registerSet(v, 1);
       }
     };
    S.stuckGet(O);

    s.merge(S);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 8, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     3 =>    4
""");
   }

  static void test_merge2()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Stuck   l = b.new Stuck(P, "left");
    final Stuck   r = b.new Stuck(P, "right");
    final Process.Register L = P.register("l", 8);
    final Process.Register R = P.register("r", 8);
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();

    P.new Instruction()
     {void action()
       {L.registerSet(1);
        R.registerSet(2);
       }
      void verilog(Verilog v)
       {L.registerSet(v, 1);
        R.registerSet(v, 2);
       }
     };
    s.stuckGetRoot();
    l.stuckGet(L);
    r.stuckGet(R);

    s.merge(l, r);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s);
    ok(s, """
Stuck: stuck size: 8, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     3 =>    4
""");
   }

  static void test_mergeButOne()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Stuck   S = b.new Stuck(P, "Stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register o = P.register("o", 8);
    P.processTrace = true;

    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {o.registerSet(1);
       }
      void verilog(Verilog v)
       {o.registerSet(v, 1);
       }
     };

    S.stuckGet(o);

    P.new Instruction()
     {void action()
       {k.registerSet(11);
        s.size.dec();
        S.size.dec();
       }
      void verilog(Verilog v)
       {k.registerSet(v, 11);
        s.size.dec(v);
        S.size.dec(v);
       }
     };

    s.mergeButOne(k, S);

    b.maxSteps = 100;

    b.chipRun();
    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 7, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3    11 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     0 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 1
""");
   }

  static void test_mergeButOne2()
   {sayCurrentTestName();
    final Btree b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Stuck   l = b.new Stuck(P, "left");
    final Stuck   r = b.new Stuck(P, "right");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register L = P.register("L", 8);
    final Process.Register R = P.register("R", 8);

    P.processClear();
    P.new Instruction()
     {void action()
       {L.registerSet(1);
        R.registerSet(2);
       }
      void verilog(Verilog v)
       {L.registerSet(v, 1);
        R.registerSet(v, 2);
       }
     };
    s.stuckGetRoot();
    l.stuckGet(L);
    r.stuckGet(R);

    P.new Instruction()
     {void action()
       {s.clear();
        l.size.dec();
        r.size.dec();
        k.registerSet(11);
       }
      void verilog(Verilog v)
       {s.clear(v);
        l.size.dec(v);
        r.size.dec(v);
        k.registerSet(v, 11);
       }
     };

    s.mergeButOne(l, k, r);

    b.maxSteps = 100;
    b.chipRun();
    //stop(s.dump());
    ok(s.dump(), """
Stuck: stuck size: 7, leaf: 1, root
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3    11 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     0 =>    4
Found     : 0
Key       : 0
FoundKey  : 0
Data      : 0
BtreeIndex: 0
StuckIndex: 0
Merge     : 1
""");
   }

  static void test_allocate()
   {sayCurrentTestName();
    final Btree            b = new Btree(4, 4, 8, 8);
    final Process          p = b.process("alloc");
    final Process.Register i = b.btreeIndex(p, "index1");
    final Process.Register j = b.btreeIndex(p, "index2");
    p.processTrace = true;
    //stop(b.chipPrintMemory());
    ok(""+b.chipPrintMemory(), """
Chip: Btree            step: 0, maxSteps: 10, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  0  0  0
    stuckIsFree
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  1  1  1
    freeNext
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  2  3  0
    stuckSize
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0
    stuckKeys
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
    stuckData
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
""");
    p.processClear();
    b.allocate(i, true);
    b.allocate(j, false);
    //stop(b.chipPrintMemory());
    if (false) p.new Instruction()
     {void action()
       {ok(b.chipPrintMemory(), """
Chip: Btree            step: 0, maxSteps: 10, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  0  0  0
    stuckIsFree
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  1  1  1
    freeNext
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  2  3  0
    stuckSize
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0
    stuckKeys
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
    stuckData
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
""");
        ok(i, "alloc_index1_0 = 1");
        ok(j, "alloc_index2_1 = 2");
       }
      void verilog(Verilog v) {v.A("");}
     };
    b.free(i);
    b.free(j);
    b.maxSteps = 100;
    b.chipRun();
    //stop(b.chipPrintMemory());
    ok(b.chipPrintMemory(), """
Chip: Btree            step: 43, maxSteps: 100, running: 0
  Processes:
    stuckIsLeaf
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        1  1  0  0
    stuckIsFree
      Memory: size:  4, width:  1, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  1  1  1
    freeNext
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        2  3  1  0
    stuckSize
      Memory: size:  4, width:  3, block:  1
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0
    stuckKeys
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
    stuckData
      Memory: size:  4, width:  8, block:  4
        0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
""");
    ok(i, "alloc_index1_0 = 1");
   }

  static void test_mergeLeavesIntoRoot()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("findAndInsert");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);

    final FindAndInsert f = b.new FindAndInsert(P);
    b.maxSteps = 2000;
    P.processTrace = true;

    k.RegisterSet(1); d.RegisterSet(2); f.findAndInsert(k, d);
    k.RegisterSet(2); d.RegisterSet(3); f.findAndInsert(k, d);
    k.RegisterSet(4); d.RegisterSet(5); f.findAndInsert(k, d);
    k.RegisterSet(3); d.RegisterSet(4); f.findAndInsert(k, d);

    b.splitRootLeaf(P);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
      2      |
      0      |
      1      |
      2      |
1,2=1  3,4=2 |
""");

    P.processClear();
    Process.Register r = b.mergeLeavesIntoRoot(P);
    b.chipRun();
    //stop(b.chipPrintMemory());
    //stop(b.btreePrint());
    ok(r.registerGet(), 1);
    ok(b.btreePrint(), """
1,2,3,4=0 |
""");
   }

  static void test_mergeLeavesNotTop()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("findAndInsert");
    final Stuck            s = b.new Stuck(P, "findAndInsert");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);
    P.processTrace = true;

    final FindAndInsert f = b.new FindAndInsert(P);
    b.maxSteps = 2000;

    k.RegisterSet(10); d.RegisterSet(20); f.findAndInsert(k, d);
    k.RegisterSet(20); d.RegisterSet(30); f.findAndInsert(k, d);
    k.RegisterSet(40); d.RegisterSet(50); f.findAndInsert(k, d);
    k.RegisterSet(30); d.RegisterSet(40); f.findAndInsert(k, d);

    b.splitRootLeaf(P);

    k.RegisterSet(50); d.RegisterSet(60); f.findAndInsert(k, d);
    k.RegisterSet(60); d.RegisterSet(70); f.findAndInsert(k, d);

    i.RegisterSet(0);
    b.splitLeafAtTop(i);

    i.RegisterSet(0); j.RegisterSet(0);
    s.stuckGet(i);
    final Process.Register r = b.mergeLeavesNotTop(s, i, j);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
              45        |
              0         |
              1         |
              2         |
10,20,30,40=1   50,60=2 |
""");
    ok(r.registerGet(), 1);
   }

  static void test_mergeLeavesAtTop()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    b.btreeLoad(test_put_save_9());
    final Process          P = b.new Process("findAndInsert");
    final Stuck            p = b.new Stuck(P, "findAndInsert");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    P.processTrace = true;

    i.RegisterSet(2);
    p.stuckGet(i);
    p.Pop();
    p.stuckPut();
    b.maxSteps = 100;
    b.chipRun();

    //stop(b.btreePrint());
    ok(b.btreePrint(), """
          4      6        |
          0      0.1      |
          1      3        |
                 2        |
1,2,3,4=1  5,6=3    7,8=2 |
""");

    P.processClear();
    i.RegisterSet(0);
    p.stuckGet(i);
    final Process.Register r = b.mergeLeavesAtTop(p, i);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
          4          |
          0          |
          1          |
          3          |
1,2,3,4=1  5,6,7,8=3 |
""");
    ok(r.registerGet(), 1);
   }

  static String test_mergeLeavesNotTop_dump()
   {return """
32
4
8
8
6
3
1 32 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
6 32 4 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 2 2 2 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 25 10 50 30 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 45 20 60 40 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 45 0 50 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 40 0 60 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 1 20 60 40 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 3 30 70 50 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 2 0 60 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 50 0 70 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }

  static void test_mergeBranchesIntoRoot()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("findAndInsert");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);
    P.processTrace = true;

    final FindAndInsert f = b.new FindAndInsert(P);
    b.maxSteps = 2000;

    k.RegisterSet(10); d.RegisterSet(20); f.findAndInsert(k, d);
    k.RegisterSet(20); d.RegisterSet(30); f.findAndInsert(k, d);
    k.RegisterSet(40); d.RegisterSet(50); f.findAndInsert(k, d);
    k.RegisterSet(30); d.RegisterSet(40); f.findAndInsert(k, d);

    b.splitRootLeaf(P);

    k.RegisterSet(50); d.RegisterSet(60); f.findAndInsert(k, d);
    k.RegisterSet(60); d.RegisterSet(70); f.findAndInsert(k, d);
    i.RegisterSet(0);
    b.splitLeafAtTop(i);

    k.RegisterSet(70); d.RegisterSet(80); f.findAndInsert(k, d);
    k.RegisterSet(80); d.RegisterSet(90); f.findAndInsert(k, d);

    b.splitLeafAtTop(i);

    b.splitRootBranch(P);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                  45                  |
                  0                   |
                  5                   |
                  6                   |
        25                  65        |
        5                   6         |
        1                   4         |
        3                   2         |
10,20=1   30,40=3   50,60=4   70,80=2 |
""");

    P.processClear();
    final Process.Register r = b.mergeBranchesIntoRoot(P);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
        25        45         65         |
        0         0.1        0.2        |
        1         3          4          |
                             2          |
10,20=1   30,40=3    50,60=4    70,80=2 |
""");
   ok(r.registerGet(), 1);
  }

  static void test_mergeBranchesNotTop()
   {sayCurrentTestName();
    final Btree            b = test_put_reload();
    final Process          P = b.new Process("findAndInsert");
    final Stuck            s = b.new Stuck(P, "findAndInsert");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);
    P.processTrace = true;

    i.RegisterSet(0); j.RegisterSet(0);
    b.splitBranchNotTop(i, j);
    b.maxSteps = 2000;
    b.chipRun();

    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                     8                             16                                                                    |
                     0                             0.1                                                                   |
                     6                             5                                                                     |
                                                   11                                                                    |
          4                        12                               20               24                28                |
          6                        5                                11               11.1              11.2              |
          1                        4                                8                10                9                 |
          3                        7                                                                   2                 |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=7    17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
""");

    P.processClear();
    i.RegisterSet(0); j.RegisterSet(0);
    s.stuckGet(i);
    b.mergeBranchesNotTop(s, i, j);
    b.maxSteps = 2000;
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                                                      16                                                                   |
                                                      0                                                                    |
                                                      6                                                                    |
                                                      11                                                                   |
          4          8               12                               20               24                28                |
          6          6.1             6.2                              11               11.1              11.2              |
          1          3               4                                8                10                9                 |
                                     7                                                                   2                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
""");
   }

  static void test_mergeBranchesAtTop()
   {sayCurrentTestName();
    final Btree            b = test_put_reload();
    final Process          P = b.new Process("findAndInsert");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);
    P.processTrace = true;

    i.RegisterSet(0);
    b.splitBranchAtTop(i);
    b.maxSteps = 2000;
    b.chipRun();

    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                                                      16                               24                               |
                                                      0                                0.1                              |
                                                      5                                6                                |
                                                                                       11                               |
          4          8               12                               20                                28              |
          5          5.1             5.2                              6                                 11              |
          1          3               4                                8                                 9               |
                                     7                                10                                2               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=9   29,30,31,32=2 |
""");

    P.processClear();
    i.RegisterSet(0);
    s.stuckGet(i);
    b.mergeBranchesAtTop(s, i);
    b.maxSteps = 2000;
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                                                      16                                                                 |
                                                      0                                                                  |
                                                      5                                                                  |
                                                      6                                                                  |
          4          8               12                               20               24               28               |
          5          5.1             5.2                              6                6.1              6.2              |
          1          3               4                                8                10               9                |
                                     7                                                                  2                |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=9    29,30,31,32=2 |
""");
   }

  static Btree test_createTree()
   {final Btree b = new Btree(8, 4, 8, 8);
    final Process P = b.new Process("Stuck");
    b.stuckIsLeaf .memorySet( 0, 0);
    b.stuckIsLeaf .memorySet( 1, 1);
    b.stuckIsLeaf .memorySet( 1, 2);
    b.stuckIsLeaf .memorySet( 1, 3);

    b.stuckSize   .memorySet( 2, 0);
    b.stuckSize   .memorySet( 3, 1);
    b.stuckSize   .memorySet( 4, 2);
    b.stuckSize   .memorySet( 4, 3);

    b.stuckKeys.memorySet(10, 0, 0); b.stuckData.memorySet( 1, 0, 0);
    b.stuckKeys.memorySet(01, 1, 0); b.stuckData.memorySet( 1, 1, 0);
    b.stuckKeys.memorySet(11, 2, 0); b.stuckData.memorySet( 2, 2, 0);
    b.stuckKeys.memorySet(21, 3, 0); b.stuckData.memorySet( 3, 3, 0);

    b.stuckKeys.memorySet(20, 0, 1); b.stuckData.memorySet( 2, 0, 1);
    b.stuckKeys.memorySet(03, 1, 1); b.stuckData.memorySet(33, 1, 1);
    b.stuckKeys.memorySet(12, 2, 1); b.stuckData.memorySet(12, 2, 1);
    b.stuckKeys.memorySet(22, 3, 1); b.stuckData.memorySet(13, 3, 1);

    b.stuckKeys.memorySet(30, 0, 2); b.stuckData.memorySet( 3, 0, 2);
    b.stuckKeys.memorySet(05, 1, 2); b.stuckData.memorySet(55, 1, 2);
    b.stuckKeys.memorySet(13, 2, 2); b.stuckData.memorySet(22, 2, 2);
    b.stuckKeys.memorySet(23, 3, 2); b.stuckData.memorySet(23, 3, 2);

    b.stuckKeys.memorySet(40, 0, 3); b.stuckData.memorySet( 4, 0, 3);
    b.stuckKeys.memorySet(07, 1, 3); b.stuckData.memorySet(77, 1, 3);
    b.stuckKeys.memorySet(14, 2, 3); b.stuckData.memorySet(32, 2, 3);
    b.stuckKeys.memorySet(24, 3, 3); b.stuckData.memorySet(33, 3, 3);

    //stop(b.print());
    ok(b.btreePrint(), """
        10              20               |
        0               0.1              |
        1               2                |
                        3                |
1,3,5=1   11,12,13,14=2    21,22,23,24=3 |
""");
    return b;
   }

  static void test_find()
   {sayCurrentTestName();
    final Btree            b = test_createTree();
    final Process          P = b.processes.get("Stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    P.processTrace = true;
    k.RegisterSet(3);

    final Find f = b.new Find(P);
    f.findSearch(k);
    b.maxSteps = 100;
    b.chipRun();

    //stop(f.dump());
    ok(f.dump(), """
Stuck: stuck size: 3, leaf: 1, index: 1
 0     1 =>    1
 1     3 =>   33
 2     5 =>   55
 3     7 =>   77
Found     : 1
Key       : 3
FoundKey  : 10
Data      : 33
BtreeIndex: 1
StuckIndex: 1
Merge     : 0
""");
   }

  static void test_findAndInsert()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("findAndInsert");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);

    final FindAndInsert f = b.new FindAndInsert(P);
    b.maxSteps = 2000;
    P.processTrace = true;
    P.processClear();

    k.RegisterSet(10); d.RegisterSet(20); f.findAndInsert(k, d);
    k.RegisterSet(20); d.RegisterSet(30); f.findAndInsert(k, d);
    k.RegisterSet(40); d.RegisterSet(50); f.findAndInsert(k, d);
    k.RegisterSet(30); d.RegisterSet(40); f.findAndInsert(k, d);

    b.splitRootLeaf(P);
    k.RegisterSet(50); d.RegisterSet(60); f.findAndInsert(k, d);
    k.RegisterSet(60); d.RegisterSet(70); f.findAndInsert(k, d);

    i.RegisterSet(0);
    b.splitLeafAtTop(i);

    k.RegisterSet(70); d.RegisterSet(80); f.findAndInsert(k, d);
    k.RegisterSet(80); d.RegisterSet(90); f.findAndInsert(k, d);

    i.RegisterSet(0);
    b.splitLeafAtTop(i);
    b.splitRootBranch(P);

    k.RegisterSet(62); d.RegisterSet(63); f.findAndInsert(k, d);
    k.RegisterSet(64); d.RegisterSet(65); f.findAndInsert(k, d);

    i.RegisterSet(6); j.RegisterSet(0);
    b.splitLeafNotTop(i, j);

    k.RegisterSet( 90); d.RegisterSet(100); f.findAndInsert(k, d);
    k.RegisterSet(100); d.RegisterSet(110); f.findAndInsert(k, d);

    i.RegisterSet(6);
    b.splitLeafAtTop(i);

    i.RegisterSet(0);
    b.splitBranchAtTop(i);

    k.RegisterSet(21); d.RegisterSet(22); f.findAndInsert(k, d);
    k.RegisterSet(22); d.RegisterSet(23); f.findAndInsert(k, d);

    i.RegisterSet(5);  j.RegisterSet(0);
    b.splitLeafNotTop(i, j);

    k.RegisterSet(12); d.RegisterSet(13); f.findAndInsert(k, d);
    k.RegisterSet(14); d.RegisterSet(15); f.findAndInsert(k, d);

    i.RegisterSet(5);  j.RegisterSet(0);
    b.splitLeafNotTop(i, j);

    i.RegisterSet(0); j.RegisterSet(0);
    b.splitBranchNotTop(i, j);
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                    20                  45                   65                    |
                    0                   0.1                  0.2                   |
                    12                  5                    9                     |
                                                             6                     |
         13                   25                   61                   85         |
         12                   5                    9                    6          |
         11                   1                    7                    8          |
         10                   3                    4                    2          |
10,12=11   14,20=10   21,22=1   30,40=3    50,60=7   62,64=4    70,80=8   90,100=2 |
""");
   }

  static void test_splitRootLeaf()
   {sayCurrentTestName();
    final Btree            b = new Btree(4, 4, 8, 8);
    final Process          P = b.new Process("splitRootLeaf");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register j = P.register("j", b.stuckAddressSize);
    P.processTrace = true;

    b.maxSteps = 2000;
    s.stuckGetRoot();
    P.processClear();
    k.RegisterSet(10); d.RegisterSet(20); s.Push(k, d);
    k.RegisterSet(20); d.RegisterSet(30); s.Push(k, d);
    k.RegisterSet(30); d.RegisterSet(40); s.Push(k, d);
    k.RegisterSet(40); d.RegisterSet(50); s.Push(k, d);
    s.stuckPut();

    b.splitRootLeaf(P);
    b.chipRun();
    //stop(b.chipPrintMemory());
    //stop(b.btreePrint());
   }

  static void test_delete_ascending()
   {sayCurrentTestName();
    final Btree b = new Btree(32, 4, 8, 8);
    final int   N = 32;
    b.btreeLoad(test_put_save_32());

    final Process          P = b.new Process("delete");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register n = P.register("n", 1);
    final StringBuilder    t = new StringBuilder();

    P.processTrace = true;
    b.maxSteps = 30000;

    k.RegisterSet(0);

    P.new Block()
     {void code()
       {k.Inc();
        b.delete(k);
        P.new Instruction()
         {void action()
           {t.append(b.btreePrint());
            n.lt(k, N);
           }
          void verilog(Verilog v)
           {n.lt(v, k, N);
           }
         };
        P.GONotZero(start, n);
       }
     };

    b.chipRun();

    //stop(t);
    ok(""+t, """
                                                    16                                                                   |
                                                    0                                                                    |
                                                    5                                                                    |
                                                    11                                                                   |
        4          8               12                               20               24                28                |
        5          5.1             5.2                              11               11.1              11.2              |
        1          3               4                                8                10                9                 |
                                   7                                                                   2                 |
2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                  16                                                                   |
                                                  0                                                                    |
                                                  5                                                                    |
                                                  11                                                                   |
      4          8               12                               20               24                28                |
      5          5.1             5.2                              11               11.1              11.2              |
      1          3               4                                8                10                9                 |
                                 7                                                                   2                 |
3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                16                                                                   |
                                                0                                                                    |
                                                5                                                                    |
                                                11                                                                   |
    4          8               12                               20               24                28                |
    5          5.1             5.2                              11               11.1              11.2              |
    1          3               4                                8                10                9                 |
                               7                                                                   2                 |
4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                         16                                                                   |
                                         0                                                                    |
                                         5                                                                    |
                                         11                                                                   |
          8             12                               20               24                28                |
          5             5.1                              11               11.1              11.2              |
          1             4                                8                10                9                 |
                        7                                                                   2                 |
5,6,7,8=1  9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                       16                                                                   |
                                       0                                                                    |
                                       5                                                                    |
                                       11                                                                   |
        8             12                               20               24                28                |
        5             5.1                              11               11.1              11.2              |
        1             4                                8                10                9                 |
                      7                                                                   2                 |
6,7,8=1  9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                     16                                                                   |
                                     0                                                                    |
                                     5                                                                    |
                                     11                                                                   |
      8             12                               20               24                28                |
      5             5.1                              11               11.1              11.2              |
      1             4                                8                10                9                 |
                    7                                                                   2                 |
7,8=1  9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                   16                                                                   |
                                   0                                                                    |
                                   5                                                                    |
                                   11                                                                   |
    8             12                               20               24                28                |
    5             5.1                              11               11.1              11.2              |
    1             4                                8                10                9                 |
                  7                                                                   2                 |
8=1  9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                             16                                                                   |
                             0                                                                    |
                             5                                                                    |
                             11                                                                   |
             12                              20               24                28                |
             5                               11               11.1              11.2              |
             1                               8                10                9                 |
             7                                                                  2                 |
9,10,11,12=1   13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                           16                                                                   |
                           0                                                                    |
                           5                                                                    |
                           11                                                                   |
           12                              20               24                28                |
           5                               11               11.1              11.2              |
           1                               8                10                9                 |
           7                                                                  2                 |
10,11,12=1   13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                        16                                                                   |
                        0                                                                    |
                        5                                                                    |
                        11                                                                   |
        12                              20               24                28                |
        5                               11               11.1              11.2              |
        1                               8                10                9                 |
        7                                                                  2                 |
11,12=1   13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                     16                                                                   |
                     0                                                                    |
                     5                                                                    |
                     11                                                                   |
     12                              20               24                28                |
     5                               11               11.1              11.2              |
     1                               8                10                9                 |
     7                                                                  2                 |
12=1   13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                    16                                                                   |
                    0                                                                    |
                    5                                                                    |
                    11                                                                   |
5Empty                              20               24                28                |
                                    11               11.1              11.2              |
                                    8                10                9                 |
1                                                                      2                 |
      13,14,15,16=1   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                 16                                                                   |
                 0                                                                    |
                 5                                                                    |
                 11                                                                   |
5Empty                           20               24                28                |
                                 11               11.1              11.2              |
                                 8                10                9                 |
1                                                                   2                 |
      14,15,16=1   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
              16                                                                   |
              0                                                                    |
              5                                                                    |
              11                                                                   |
5Empty                        20               24                28                |
                              11               11.1              11.2              |
                              8                10                9                 |
1                                                                2                 |
      15,16=1   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
           16                                                                   |
           0                                                                    |
           5                                                                    |
           11                                                                   |
5Empty                     20               24                28                |
                           11               11.1              11.2              |
                           8                10                9                 |
1                                                             2                 |
      16=1   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
         16                                                                   |
         0                                                                    |
         5                                                                    |
         11                                                                   |
5Empty                   20               24                28                |
                         11               11.1              11.2              |
                         8                10                9                 |
1                                                           2                 |
      =1   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
         16                                                                |
         0                                                                 |
         5                                                                 |
         11                                                                |
5Empty                20               24                28                |
                      11               11.1              11.2              |
                      8                10                9                 |
1                                                        2                 |
      =1   18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
         16                                                             |
         0                                                              |
         5                                                              |
         11                                                             |
5Empty             20               24                28                |
                   11               11.1              11.2              |
                   8                10                9                 |
1                                                     2                 |
      =1   19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
         16                                                          |
         0                                                           |
         5                                                           |
         11                                                          |
5Empty          20               24                28                |
                11               11.1              11.2              |
                8                10                9                 |
1                                                  2                 |
      =1   20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
         16                                                |
         0                                                 |
         5                                                 |
         11                                                |
5Empty                   24              28                |
                         11              11.1              |
                         8               9                 |
1                                        2                 |
      =1   21,22,23,24=8   25,26,27,28=9     29,30,31,32=2 |
           24              28               |
           0               0.1              |
           1               9                |
                           2                |
22,23,24=1   25,26,27,28=9    29,30,31,32=2 |
        24              28               |
        0               0.1              |
        1               9                |
                        2                |
23,24=1   25,26,27,28=9    29,30,31,32=2 |
     24              28               |
     0               0.1              |
     1               9                |
                     2                |
24=1   25,26,27,28=9    29,30,31,32=2 |
              28              |
              0               |
              1               |
              2               |
25,26,27,28=1   29,30,31,32=2 |
           28              |
           0               |
           1               |
           2               |
26,27,28=1   29,30,31,32=2 |
        28              |
        0               |
        1               |
        2               |
27,28=1   29,30,31,32=2 |
     28              |
     0               |
     1               |
     2               |
28=1   29,30,31,32=2 |
29,30,31,32=0 |
30,31,32=0 |
31,32=0 |
32=0 |
=0 |
""");
   }

  static void test_delete_random()
   {sayCurrentTestName();
    final Btree            b = test_put_reload();
    final Process          P = b.new Process("delete");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register l = P.register("l", 1);
    final StringBuilder    t = new StringBuilder();
    b.maxSteps = 30000;
    P.processTrace = true;
    final int N = 32;

    i.RegisterSet(0);

    P.new Block()
     {void code()
       {P.new Instruction()
         {void action()
           {k.registerSet(random_32[i.registerGet()]);
            i.inc();
           }
          void verilog(Verilog v)
           {v.new Case(random_32.length, i.registerName())
             {void Choice(int I)
               {k.registerSet(v, random_32[I]);
                i.inc(v);
               }
             };
           }
         };

        b.delete(k);
        P.new Instruction()
         {void action()
           {t.append(b.btreePrint());
            l.lt(i, N);
           }
          void verilog(Verilog v)
           {l.lt(v, i, N);
           }
         };
        P.GONotZero(start, l);
       }
     };
    b.chipRun();

    //stop(t);
    ok(t, """
                                                   16                                                                   |
                                                   0                                                                    |
                                                   5                                                                    |
                                                   11                                                                   |
          4          8            12                               20               24                28                |
          5          5.1          5.2                              11               11.1              11.2              |
          1          3            4                                8                10                9                 |
                                  7                                                                   2                 |
1,2,3,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                 16                                                                   |
                                                 0                                                                    |
                                                 5                                                                    |
                                                 11                                                                   |
        4          8            12                               20               24                28                |
        5          5.1          5.2                              11               11.1              11.2              |
        1          3            4                                8                10                9                 |
                                7                                                                   2                 |
1,2,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                 16                                                                |
                                                 0                                                                 |
                                                 5                                                                 |
                                                 11                                                                |
        4          8            12                               20               24             28                |
        5          5.1          5.2                              11               11.1           11.2              |
        1          3            4                                8                10             9                 |
                                7                                                                2                 |
1,2,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,28=9     29,30,31,32=2 |
                                               16                                                                |
                                               0                                                                 |
                                               5                                                                 |
                                               11                                                                |
      4          8            12                               20               24             28                |
      5          5.1          5.2                              11               11.1           11.2              |
      1          3            4                                8                10             9                 |
                              7                                                                2                 |
2,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,28=9     29,30,31,32=2 |
                                               16                                                             |
                                               0                                                              |
                                               5                                                              |
                                               11                                                             |
      4          8            12                               20            24             28                |
      5          5.1          5.2                              11            11.1           11.2              |
      1          3            4                                8             10             9                 |
                              7                                                             2                 |
2,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19,20=8   21,22,24=10     25,26,28=9     29,30,31,32=2 |
                                               16                                                          |
                                               0                                                           |
                                               5                                                           |
                                               11                                                          |
      4          8            12                            20            24             28                |
      5          5.1          5.2                           11            11.1           11.2              |
      1          3            4                             8             10             9                 |
                              7                                                          2                 |
2,4=1  5,6,7,8=3    9,10,11=4    13,14,15,16=7   17,18,19=8   21,22,24=10     25,26,28=9     29,30,31,32=2 |
                                             16                                                          |
                                             0                                                           |
                                             5                                                           |
                                             11                                                          |
      4        8            12                            20            24             28                |
      5        5.1          5.2                           11            11.1           11.2              |
      1        3            4                             8             10             9                 |
                            7                                                          2                 |
2,4=1  5,6,7=3    9,10,11=4    13,14,15,16=7   17,18,19=8   21,22,24=10     25,26,28=9     29,30,31,32=2 |
                                             16                                                       |
                                             0                                                        |
                                             5                                                        |
                                             11                                                       |
      4        8            12                         20            24             28                |
      5        5.1          5.2                        11            11.1           11.2              |
      1        3            4                          8             10             9                 |
                            7                                                       2                 |
2,4=1  5,6,7=3    9,10,11=4    13,14,15,16=7   17,19=8   21,22,24=10     25,26,28=9     29,30,31,32=2 |
                                      16                                                       |
                                      0                                                        |
                                      5                                                        |
                                      11                                                       |
          8          12                         20            24             28                |
          5          5.1                        11            11.1           11.2              |
          1          4                          8             10             9                 |
                     7                                                       2                 |
4,5,6,7=1  9,10,11=4    13,14,15,16=7   17,19=8   21,22,24=10     25,26,28=9     29,30,31,32=2 |
                                      16                                                    |
                                      0                                                     |
                                      5                                                     |
                                      11                                                    |
          8          12                         20            24             28             |
          5          5.1                        11            11.1           11.2           |
          1          4                          8             10             9              |
                     7                                                       2              |
4,5,6,7=1  9,10,11=4    13,14,15,16=7   17,19=8   21,22,24=10     25,26,28=9     29,30,32=2 |
                                      16                                                 |
                                      0                                                  |
                                      5                                                  |
                                      11                                                 |
          8          12                         20            24          28             |
          5          5.1                        11            11.1        11.2           |
          1          4                          8             10          9              |
                     7                                                    2              |
4,5,6,7=1  9,10,11=4    13,14,15,16=7   17,19=8   21,22,24=10     26,28=9     29,30,32=2 |
                                   16                                                 |
                                   0                                                  |
                                   5                                                  |
                                   11                                                 |
          8          12                      20            24          28             |
          5          5.1                     11            11.1        11.2           |
          1          4                       8             10          9              |
                     7                                                 2              |
4,5,6,7=1  9,10,11=4    13,14,15=7   17,19=8   21,22,24=10     26,28=9     29,30,32=2 |
                                16                                                 |
                                0                                                  |
                                5                                                  |
                                11                                                 |
          8          12                   20            24          28             |
          5          5.1                  11            11.1        11.2           |
          1          4                    8             10          9              |
                     7                                              2              |
4,5,6,7=1  9,10,11=4    14,15=7   17,19=8   21,22,24=10     26,28=9     29,30,32=2 |
                                16                                        |
                                0                                         |
                                5                                         |
                                11                                        |
          8          12                   20            24                |
          5          5.1                  11            11.1              |
          1          4                    8             10                |
                     7                                  9                 |
4,5,6,7=1  9,10,11=4    14,15=7   17,19=8   21,22,24=10     26,28,29,30=9 |
                        16                                        |
                        0                                         |
                        5                                         |
                        11                                        |
          8                       20            24                |
          5                       11            11.1              |
          1                       8             10                |
          4                                     9                 |
4,5,6,7=1  9,10,14,15=4   17,19=8   21,22,24=10     26,28,29,30=9 |
                        16                              |
                        0                               |
                        5                               |
                        11                              |
          8                             24              |
          5                             11              |
          1                             8               |
          4                             9               |
4,5,6,7=1  9,10,14,15=4   17,19,22,24=8   26,28,29,30=9 |
        8             16               24               |
        0             0.1              0.2              |
        1             4                8                |
                                       9                |
4,6,7=1  9,10,14,15=4    17,19,22,24=8    26,28,29,30=9 |
        8             16            24               |
        0             0.1           0.2              |
        1             4             8                |
                                    9                |
4,6,7=1  9,10,14,15=4    17,19,22=8    26,28,29,30=9 |
      8             16            24               |
      0             0.1           0.2              |
      1             4             8                |
                                  9                |
6,7=1  9,10,14,15=4    17,19,22=8    26,28,29,30=9 |
      8          16            24               |
      0          0.1           0.2              |
      1          4             8                |
                               9                |
6,7=1  9,14,15=4    17,19,22=8    26,28,29,30=9 |
      8          16            24            |
      0          0.1           0.2           |
      1          4             8             |
                               9             |
6,7=1  9,14,15=4    17,19,22=8    28,29,30=9 |
      8          16            24         |
      0          0.1           0.2        |
      1          4             8          |
                               9          |
6,7=1  9,14,15=4    17,19,22=8    28,29=9 |
            16           24         |
            0            0.1        |
            1            8          |
                         9          |
6,7,14,15=1   17,19,22=8    28,29=9 |
          16           24         |
          0            0.1        |
          1            8          |
                       9          |
7,14,15=1   17,19,22=8    28,29=9 |
          16              |
          0               |
          1               |
          8               |
7,14,15=1   17,19,22,28=8 |
          16           |
          0            |
          1            |
          8            |
7,14,15=1   19,22,28=8 |
          16        |
          0         |
          1         |
          8         |
7,14,15=1   19,22=8 |
7,14,19,22=0 |
7,19,22=0 |
7,22=0 |
22=0 |
=0 |
""");
   }

  static void test_delete_descending()
   {sayCurrentTestName();
    final Btree            b = test_put_reload();
    final Process          P = b.new Process("delete");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register l = P.register("l", 1);
    final StringBuilder    t = new StringBuilder();
    b.maxSteps = 40000;
    P.processTrace = true;
    final int N = 32;

    i.RegisterSet(0);

    P.new Block()
     {void code()
       {P.new Instruction()
         {void action()
           {k.registerSet(N-i.registerGet());
            i.inc();
           }
          void verilog(Verilog v)
           {v.new Case(random_32.length, i.registerName())
             {void Choice(int I)
               {k.registerSet(v, N-I);
                i.inc(v);
               }
             };
           }
         };

        b.delete(k);
        P.new Instruction()
         {void action()
           {t.append(b.btreePrint());
            l.lt(i, N);
           }
          void verilog(Verilog v)
           {l.lt(v, i, N);
           }
         };
        P.GONotZero(start, l);
       }
     };
    b.chipRun();

    //stop(t);
    ok(t, """
                                                      16                                                                |
                                                      0                                                                 |
                                                      5                                                                 |
                                                      11                                                                |
          4          8               12                               20               24                28             |
          5          5.1             5.2                              11               11.1              11.2           |
          1          3               4                                8                10                9              |
                                     7                                                                   2              |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31=2 |
                                                      16                                                             |
                                                      0                                                              |
                                                      5                                                              |
                                                      11                                                             |
          4          8               12                               20               24                28          |
          5          5.1             5.2                              11               11.1              11.2        |
          1          3               4                                8                10                9           |
                                     7                                                                   2           |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30=2 |
                                                      16                                                          |
                                                      0                                                           |
                                                      5                                                           |
                                                      11                                                          |
          4          8               12                               20               24                28       |
          5          5.1             5.2                              11               11.1              11.2     |
          1          3               4                                8                10                9        |
                                     7                                                                   2        |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29=2 |
                                                      16                                                 |
                                                      0                                                  |
                                                      5                                                  |
                                                      11                                                 |
          4          8               12                               20               24                |
          5          5.1             5.2                              11               11.1              |
          1          3               4                                8                10                |
                                     7                                                 9                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9 |
                                                      16                                              |
                                                      0                                               |
                                                      5                                               |
                                                      11                                              |
          4          8               12                               20               24             |
          5          5.1             5.2                              11               11.1           |
          1          3               4                                8                10             |
                                     7                                                 9              |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27=9 |
                                                      16                                           |
                                                      0                                            |
                                                      5                                            |
                                                      11                                           |
          4          8               12                               20               24          |
          5          5.1             5.2                              11               11.1        |
          1          3               4                                8                10          |
                                     7                                                 9           |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26=9 |
                                                      16                                        |
                                                      0                                         |
                                                      5                                         |
                                                      11                                        |
          4          8               12                               20               24       |
          5          5.1             5.2                              11               11.1     |
          1          3               4                                8                10       |
                                     7                                                 9        |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25=9 |
                                                      16                               |
                                                      0                                |
                                                      5                                |
                                                      11                               |
          4          8               12                               20               |
          5          5.1             5.2                              11               |
          1          3               4                                8                |
                                     7                                10               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10 |
                                                      16                            |
                                                      0                             |
                                                      5                             |
                                                      11                            |
          4          8               12                               20            |
          5          5.1             5.2                              11            |
          1          3               4                                8             |
                                     7                                10            |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23=10 |
                                                      16                         |
                                                      0                          |
                                                      5                          |
                                                      11                         |
          4          8               12                               20         |
          5          5.1             5.2                              11         |
          1          3               4                                8          |
                                     7                                10         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22=10 |
                                                      16                      |
                                                      0                       |
                                                      5                       |
                                                      11                      |
          4          8               12                               20      |
          5          5.1             5.2                              11      |
          1          3               4                                8       |
                                     7                                10      |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21=10 |
                                                      16                     |
                                                      0                      |
                                                      5                      |
                                                      11                     |
          4          8               12                 11Empty              |
          5          5.1             5.2                                     |
          1          3               4                                       |
                                     7                  8                    |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7          17,18,19,20=8 |
                                                      16                  |
                                                      0                   |
                                                      5                   |
                                                      11                  |
          4          8               12                 11Empty           |
          5          5.1             5.2                                  |
          1          3               4                                    |
                                     7                  8                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7          17,18,19=8 |
                                                      16               |
                                                      0                |
                                                      5                |
                                                      11               |
          4          8               12                 11Empty        |
          5          5.1             5.2                               |
          1          3               4                                 |
                                     7                  8              |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7          17,18=8 |
                                                      16            |
                                                      0             |
                                                      5             |
                                                      11            |
          4          8               12                 11Empty     |
          5          5.1             5.2                            |
          1          3               4                              |
                                     7                  8           |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7          17=8 |
                                                      16          |
                                                      0           |
                                                      5           |
                                                      11          |
          4          8               12                 11Empty   |
          5          5.1             5.2                          |
          1          3               4                            |
                                     7                  8         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7          =8 |
                                                   16          |
                                                   0           |
                                                   5           |
                                                   11          |
          4          8               12              11Empty   |
          5          5.1             5.2                       |
          1          3               4                         |
                                     7               8         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15=7          =8 |
                                                16          |
                                                0           |
                                                5           |
                                                11          |
          4          8               12           11Empty   |
          5          5.1             5.2                    |
          1          3               4                      |
                                     7            8         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14=7          =8 |
                                             16          |
                                             0           |
                                             5           |
                                             11          |
          4          8               12        11Empty   |
          5          5.1             5.2                 |
          1          3               4                   |
                                     7         8         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13=7          =8 |
                                     16          |
                                     0           |
                                     5           |
                                     11          |
          4          8                 11Empty   |
          5          5.1                         |
          1          3                           |
                     4                 8         |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4          =8 |
          4          8            |
          0          0.1          |
          1          3            |
                     4            |
1,2,3,4=1  5,6,7,8=3    9,10,11=4 |
          4          8         |
          0          0.1       |
          1          3         |
                     4         |
1,2,3,4=1  5,6,7,8=3    9,10=4 |
          4          8      |
          0          0.1    |
          1          3      |
                     4      |
1,2,3,4=1  5,6,7,8=3    9=4 |
          4          |
          0          |
          1          |
          3          |
1,2,3,4=1  5,6,7,8=3 |
          4        |
          0        |
          1        |
          3        |
1,2,3,4=1  5,6,7=3 |
          4      |
          0      |
          1      |
          3      |
1,2,3,4=1  5,6=3 |
          4    |
          0    |
          1    |
          3    |
1,2,3,4=1  5=3 |
1,2,3,4=0 |
1,2,3=0 |
1,2=0 |
1=0 |
=0 |
""");
   }

  static void test_delete_random_descending()
   {sayCurrentTestName();
    final Btree            b = test_put_reload();
    final Process          P = b.new Process("delete");
    final Stuck            s = b.new Stuck(P, "stuck");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register l = P.register("l", 1);
    final StringBuilder    t = new StringBuilder();
    b.maxSteps = 30000;
    P.processTrace = true;

    final int N = 32;

    i.RegisterSet(0);

    P.new Block()
     {void code()
       {P.new Instruction()
         {void action()
           {k.registerSet(random_32[N-1-i.registerGet()]);
            i.inc();
           }
          void verilog(Verilog v)
           {v.new Case(random_32.length, i.registerName())
             {void Choice(int I)
               {k.registerSet(v, random_32[N-1-I]);
               }
             };
            i.inc(v);
           }
         };

        b.delete(k);
        P.new Instruction()
         {void action()
           {t.append(b.btreePrint());
            l.lt(i, N);
           }
          void verilog(Verilog v)
           {l.lt(v, i, N);
           }
         };
        P.GONotZero(start, l);
       }
     };
    b.chipRun();

    //stop(t);
    ok(t, """
                                                      16                                                                |
                                                      0                                                                 |
                                                      5                                                                 |
                                                      11                                                                |
          4          8               12                               20            24                28                |
          5          5.1             5.2                              11            11.1              11.2              |
          1          3               4                                8             10                9                 |
                                     7                                                                2                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                    16                                                                |
                                                    0                                                                 |
                                                    5                                                                 |
                                                    11                                                                |
          4        8               12                               20            24                28                |
          5        5.1             5.2                              11            11.1              11.2              |
          1        3               4                                8             10                9                 |
                                   7                                                                2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                    16                                                             |
                                                    0                                                              |
                                                    5                                                              |
                                                    11                                                             |
          4        8               12                            20            24                28                |
          5        5.1             5.2                           11            11.1              11.2              |
          1        3               4                             8             10                9                 |
                                   7                                                             2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,14,15,16=7   17,18,20=8   21,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                                 16                                                             |
                                                 0                                                              |
                                                 5                                                              |
                                                 11                                                             |
          4        8               12                         20            24                28                |
          5        5.1             5.2                        11            11.1              11.2              |
          1        3               4                          8             10                9                 |
                                   7                                                          2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,15,16=7   17,18,20=8   21,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                              16                                                             |
                                              0                                                              |
                                              5                                                              |
                                              11                                                             |
          4        8               12                      20            24                28                |
          5        5.1             5.2                     11            11.1              11.2              |
          1        3               4                       8             10                9                 |
                                   7                                                       2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,16=7   17,18,20=8   21,23,24=10     25,26,27,28=9     29,30,31,32=2 |
                                              16                                                          |
                                              0                                                           |
                                              5                                                           |
                                              11                                                          |
          4        8               12                      20            24             28                |
          5        5.1             5.2                     11            11.1           11.2              |
          1        3               4                       8             10             9                 |
                                   7                                                    2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,16=7   17,18,20=8   21,23,24=10     25,26,27=9     29,30,31,32=2 |
                                              16                                                       |
                                              0                                                        |
                                              5                                                        |
                                              11                                                       |
          4        8               12                   20            24             28                |
          5        5.1             5.2                  11            11.1           11.2              |
          1        3               4                    8             10             9                 |
                                   7                                                 2                 |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,26,27=9     29,30,31,32=2 |
                                              16                                                    |
                                              0                                                     |
                                              5                                                     |
                                              11                                                    |
          4        8               12                   20            24             28             |
          5        5.1             5.2                  11            11.1           11.2           |
          1        3               4                    8             10             9              |
                                   7                                                 2              |
1,2,3,4=1  5,6,8=3    9,10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,26,27=9     30,31,32=2 |
                                            16                                                    |
                                            0                                                     |
                                            5                                                     |
                                            11                                                    |
          4      8               12                   20            24             28             |
          5      5.1             5.2                  11            11.1           11.2           |
          1      3               4                    8             10             9              |
                                 7                                                 2              |
1,2,3,4=1  5,8=3    9,10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,26,27=9     30,31,32=2 |
                                          16                                                    |
                                          0                                                     |
                                          5                                                     |
                                          11                                                    |
          4      8             12                   20            24             28             |
          5      5.1           5.2                  11            11.1           11.2           |
          1      3             4                    8             10             9              |
                               7                                                 2              |
1,2,3,4=1  5,8=3    10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,26,27=9     30,31,32=2 |
                                          16                                                 |
                                          0                                                  |
                                          5                                                  |
                                          11                                                 |
          4      8             12                   20            24             28          |
          5      5.1           5.2                  11            11.1           11.2        |
          1      3             4                    8             10             9           |
                               7                                                 2           |
1,2,3,4=1  5,8=3    10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,26,27=9     31,32=2 |
                                          16                                        |
                                          0                                         |
                                          5                                         |
                                          11                                        |
          4      8             12                   20            24                |
          5      5.1           5.2                  11            11.1              |
          1      3             4                    8             10                |
                               7                                  9                 |
1,2,3,4=1  5,8=3    10,11,12=4    13,16=7   18,20=8   21,23,24=10     25,27,31,32=9 |
                                  16                                        |
                                  0                                         |
                                  5                                         |
                                  11                                        |
          4      8                          20            24                |
          5      5.1                        11            11.1              |
          1      3                          8             10                |
                 4                                        9                 |
1,2,3,4=1  5,8=3    11,12,13,16=4   18,20=8   21,23,24=10     25,27,31,32=9 |
                                16                                        |
                                0                                         |
                                5                                         |
                                11                                        |
        4      8                          20            24                |
        5      5.1                        11            11.1              |
        1      3                          8             10                |
               4                                        9                 |
1,2,3=1  5,8=3    11,12,13,16=4   18,20=8   21,23,24=10     25,27,31,32=9 |
                                16                              |
                                0                               |
                                5                               |
                                11                              |
        4      8                                24              |
        5      5.1                              11              |
        1      3                                8               |
               4                                9               |
1,2,3=1  5,8=3    11,12,13,16=4   18,20,21,23=8   25,27,31,32=9 |
                         16                              |
                         0                               |
                         5                               |
                         11                              |
          8                              24              |
          5                              11              |
          1                              8               |
          4                              9               |
1,2,3,8=1  11,12,13,16=4   18,20,21,23=8   25,27,31,32=9 |
          8              16            24               |
          0              0.1           0.2              |
          1              4             8                |
                                       9                |
1,2,3,8=1  11,12,13,16=4    18,20,23=8    25,27,31,32=9 |
          8           16            24               |
          0           0.1           0.2              |
          1           4             8                |
                                    9                |
1,2,3,8=1  12,13,16=4    18,20,23=8    25,27,31,32=9 |
          8           16            24            |
          0           0.1           0.2           |
          1           4             8             |
                                    9             |
1,2,3,8=1  12,13,16=4    18,20,23=8    25,27,31=9 |
          8        16            24            |
          0        0.1           0.2           |
          1        4             8             |
                                 9             |
1,2,3,8=1  12,16=4    18,20,23=8    25,27,31=9 |
          8              24            |
          0              0.1           |
          1              4             |
                         9             |
1,2,3,8=1  12,18,20,23=4    25,27,31=9 |
          8              24         |
          0              0.1        |
          1              4          |
                         9          |
1,2,3,8=1  12,18,20,23=4    27,31=9 |
          8              24      |
          0              0.1     |
          1              4       |
                         9       |
1,2,3,8=1  12,18,20,23=4    27=9 |
        8              24      |
        0              0.1     |
        1              4       |
                       9       |
1,3,8=1  12,18,20,23=4    27=9 |
        8              |
        0              |
        1              |
        4              |
1,3,8=1  12,20,23,27=4 |
      8              |
      0              |
      1              |
      4              |
1,3=1  12,20,23,27=4 |
      8           |
      0           |
      1           |
      4           |
1,3=1  12,23,27=4 |
1,3,12,27=0 |
3,12,27=0 |
3,12=0 |
12=0 |
=0 |
""");
   }

  static void test_verilog_push()
   {sayCurrentTestName();
    final Btree   b = new Btree(4, 4, 8, 8);
    final Process P = b.new Process("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    P.processTrace = true;

    for (int i = 0; i < b.maxStuckSize; i++)
     {final int I = i;
      P.new Instruction()
       {void action()
         {k.registerSet(I+1); d.registerSet(I+2); s.push(k, d);
         }
        void verilog(Verilog v)
         {k.registerSet(v, I+1); d.registerSet(v, I+2); s.push(v, k, d);
         }
       };
     }
    b.chipRun();
   }

  static void test_verilog_pop()
   {sayCurrentTestName();
    final Btree   b = test_push();
    final Process P = b.processes.get("Stuck");
    final Stuck   s = b.new Stuck(P, "stuck");
    final Process.Register k = s.Key;
    final Process.Register d = s.Data;
    P.processTrace = true;
    P.processClear();
    s.stuckGetRoot();
    P.new Instruction()
     {void action()
       {s.pop();
       }
      void verilog(Verilog v)
       {s.pop(v);
       }
     };

    b.maxSteps = 1000;
    b.chipRun();
   }

  static void test_put_ascending()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("put");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", 8);
    final Process.Register l = P.register("l", 1);
    P.processTrace  = true;
    b.maxSteps      = 40000;
    b.suppressMerge = true;                                                     // Suppress merges as they have not been developed yet

    final int N = 32;
    k.RegisterSet(0);
    d.RegisterSet(0);
    P.new Block()
     {void code()
       {k.Inc(); d.Copy(k); d.Inc();
        b.put(k, d);
        l.Lt(k, N);
        P.GONotZero(start, l);
       }
     };
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                            8                                         16                                                                                    |
                            0                                         0.1                                                                                   |
                            14                                        22                                                                                    |
                                                                      15                                                                                    |
             4                                  12                                           20                    24                                       |
             14                                 22                                           15                    15.1                                     |
             5                                  12                                           20                    24                                       |
             9                                  17                                                                 6                                        |
      2              6               10                    14                     18                    22                      26         28               |
      5              9               12                    17                     20                    24                      6          6.1              |
      1              4               8                     11                     16                    19                      23         25               |
      3              7               10                    13                     18                    21                                 2                |
1,2=1  3,4=3   5,6=4  7,8=7   9,10=8   11,12=10   13,14=11   15,16=13    17,18=16   19,20=18   21,22=19   23,24=21     25,26=23   27,28=25    29,30,31,32=2 |
""");
   }

  static void test_put_merge()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("put");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register i = P.register("i", 8);
    final Process.Register l = P.register("l", 1);
    final StringBuilder    s = new StringBuilder();
    P.processTrace = true;
    b.maxSteps     = 200000;
    b.suppressMerge = false;

    final int N = 32;
    k.registerSet(0);
    d.registerSet(0);
    P.new Block()
     {void code()
       {k.Inc(); d.Copy(k); d.Inc();
        b.put(k, d);
        if (false)                                                               // Create test trees
         {P.new Instruction()
           {void action()
             {say("  static String test_put_save_"+k.registerGet()+"()\n   {return \"\"\"", b.btreeSave() + "\"\"\";\n   }\n");
             }
           };
         }
        P.new Instruction()
         {void action()
           {s.append(b.btreePrint());
            l.lt(k, N);
           }
          void verilog(Verilog v)
           {l.lt(v, k, N);
           }
         };
        P.GONotZero(start, l);
       }
     };
    b.chipRun();
    //stop(s);
    ok(s, """
1=0 |
1,2=0 |
1,2,3=0 |
1,2,3,4=0 |
      2        |
      0        |
      1        |
      2        |
1,2=1  3,4,5=2 |
      2          |
      0          |
      1          |
      2          |
1,2=1  3,4,5,6=2 |
          4        |
          0        |
          1        |
          2        |
1,2,3,4=1  5,6,7=2 |
          4          |
          0          |
          1          |
          2          |
1,2,3,4=1  5,6,7,8=2 |
          4      6          |
          0      0.1        |
          1      3          |
                 2          |
1,2,3,4=1  5,6=3    7,8,9=2 |
          4      6             |
          0      0.1           |
          1      3             |
                 2             |
1,2,3,4=1  5,6=3    7,8,9,10=2 |
          4          8            |
          0          0.1          |
          1          3            |
                     2            |
1,2,3,4=1  5,6,7,8=3    9,10,11=2 |
          4          8               |
          0          0.1             |
          1          3               |
                     2               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=2 |
          4          8         10            |
          0          0.1       0.2           |
          1          3         4             |
                               2             |
1,2,3,4=1  5,6,7,8=3    9,10=4    11,12,13=2 |
          4          8         10               |
          0          0.1       0.2              |
          1          3         4                |
                               2                |
1,2,3,4=1  5,6,7,8=3    9,10=4    11,12,13,14=2 |
                     8                          |
                     0                          |
                     5                          |
                     6                          |
          4                        12           |
          5                        6            |
          1                        4            |
          3                        2            |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15=2 |
                     8                             |
                     0                             |
                     5                             |
                     6                             |
          4                        12              |
          5                        6               |
          1                        4               |
          3                        2               |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=2 |
                     8                                     |
                     0                                     |
                     5                                     |
                     6                                     |
          4                        12        14            |
          5                        6         6.1           |
          1                        4         7             |
          3                                  2             |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14=7    15,16,17=2 |
                     8                                        |
                     0                                        |
                     5                                        |
                     6                                        |
          4                        12        14               |
          5                        6         6.1              |
          1                        4         7                |
          3                                  2                |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14=7    15,16,17,18=2 |
                     8                                           |
                     0                                           |
                     5                                           |
                     6                                           |
          4                        12              16            |
          5                        6               6.1           |
          1                        4               7             |
          3                                        2             |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=7    17,18,19=2 |
                     8                                              |
                     0                                              |
                     5                                              |
                     6                                              |
          4                        12              16               |
          5                        6               6.1              |
          1                        4               7                |
          3                                        2                |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=7    17,18,19,20=2 |
                     8                                                      |
                     0                                                      |
                     5                                                      |
                     6                                                      |
          4                        12              16         18            |
          5                        6               6.1        6.2           |
          1                        4               7          8             |
          3                                                   2             |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=7    17,18=8    19,20,21=2 |
                     8                                                         |
                     0                                                         |
                     5                                                         |
                     6                                                         |
          4                        12              16         18               |
          5                        6               6.1        6.2              |
          1                        4               7          8                |
          3                                                   2                |
1,2,3,4=1  5,6,7,8=3  9,10,11,12=4   13,14,15,16=7    17,18=8    19,20,21,22=2 |
                                                      16                           |
                                                      0                            |
                                                      5                            |
                                                      6                            |
          4          8               12                               20           |
          5          5.1             5.2                              6            |
          1          3               4                                8            |
                                     7                                2            |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23=2 |
                                                      16                              |
                                                      0                               |
                                                      5                               |
                                                      6                               |
          4          8               12                               20              |
          5          5.1             5.2                              6               |
          1          3               4                                8               |
                                     7                                2               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=2 |
                                                      16                                       |
                                                      0                                        |
                                                      5                                        |
                                                      6                                        |
          4          8               12                               20         22            |
          5          5.1             5.2                              6          6.1           |
          1          3               4                                8          10            |
                                     7                                           2             |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22=10    23,24,25=2 |
                                                      16                                          |
                                                      0                                           |
                                                      5                                           |
                                                      6                                           |
          4          8               12                               20         22               |
          5          5.1             5.2                              6          6.1              |
          1          3               4                                8          10               |
                                     7                                           2                |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22=10    23,24,25,26=2 |
                                                      16                                             |
                                                      0                                              |
                                                      5                                              |
                                                      6                                              |
          4          8               12                               20               24            |
          5          5.1             5.2                              6                6.1           |
          1          3               4                                8                10            |
                                     7                                                 2             |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27=2 |
                                                      16                                                |
                                                      0                                                 |
                                                      5                                                 |
                                                      6                                                 |
          4          8               12                               20               24               |
          5          5.1             5.2                              6                6.1              |
          1          3               4                                8                10               |
                                     7                                                 2                |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=2 |
                                                      16                                                        |
                                                      0                                                         |
                                                      5                                                         |
                                                      6                                                         |
          4          8               12                               20               24         26            |
          5          5.1             5.2                              6                6.1        6.2           |
          1          3               4                                8                10         9             |
                                     7                                                            2             |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26=9    27,28,29=2 |
                                                      16                                                           |
                                                      0                                                            |
                                                      5                                                            |
                                                      6                                                            |
          4          8               12                               20               24         26               |
          5          5.1             5.2                              6                6.1        6.2              |
          1          3               4                                8                10         9                |
                                     7                                                            2                |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26=9    27,28,29,30=2 |
                                                      16                               24                            |
                                                      0                                0.1                           |
                                                      5                                11                            |
                                                                                       6                             |
          4          8               12                               20                                28           |
          5          5.1             5.2                              11                                6            |
          1          3               4                                8                                 9            |
                                     7                                10                                2            |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=9   29,30,31=2 |
                                                      16                                                                   |
                                                      0                                                                    |
                                                      5                                                                    |
                                                      11                                                                   |
          4          8               12                               20               24                28                |
          5          5.1             5.2                              11               11.1              11.2              |
          1          3               4                                8                10                9                 |
                                     7                                                                   2                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
""");
   }

  static Btree test_put_reload()
   {final Btree b = new Btree(32, 4, 8, 8);
    b.btreeLoad(test_put_save_32());
    //stop(b.btreePrint());                                                     // Create test_put_print
    ok(b.btreePrint(), test_put_print());
    return b;
   }

  static String test_put_print()
   {return """
                                                      16                                                                   |
                                                      0                                                                    |
                                                      5                                                                    |
                                                      11                                                                   |
          4          8               12                               20               24                28                |
          5          5.1             5.2                              11               11.1              11.2              |
          1          3               4                                8                10                9                 |
                                     7                                                                   2                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31,32=2 |
""";
   }

  static String test_put_save_1()
   {return """
32
4
8
8
6
3
1 32 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_2()
   {return """
32
4
8
8
6
3
1 32 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_3()
   {return """
32
4
8
8
6
3
1 32 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 3 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_4()
   {return """
32
4
8
8
6
3
1 32 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 3 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 3 4 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_5()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 3 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 2 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 2 3 4 1 2 0 0 3 4 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 4 5 2 3 0 0 4 5 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_6()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 3 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 2 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 2 2 3 4 1 2 0 0 3 4 5 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 4 5 2 3 0 0 4 5 6 7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_7()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 3 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 4 4 4 1 2 3 4 5 6 7 5 3 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 5 5 2 3 4 5 6 7 8 6 4 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_8()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 3 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 4 4 4 1 2 3 4 5 6 7 8 3 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 2 5 5 2 3 4 5 6 7 8 9 4 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_9()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 4 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 4 3 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 6 6 4 1 2 3 4 7 8 9 7 5 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 2 5 2 3 4 5 8 9 10 8 6 7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_10()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 4 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 4 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 6 6 4 1 2 3 4 7 8 9 10 5 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 2 5 2 3 4 5 8 9 10 11 6 7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_11()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 4 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 4 3 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 8 8 8 1 2 3 4 9 10 11 9 5 6 7 8 7 8 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 2 2 2 3 4 5 10 11 12 10 6 7 8 9 8 9 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_12()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 4 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 4 4 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 8 8 8 1 2 3 4 9 10 11 12 5 6 7 8 7 8 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 2 2 2 3 4 5 10 11 12 13 6 7 8 9 8 9 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_13()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 5 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 3 4 3 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 8 10 10 1 2 3 4 11 12 13 11 5 6 7 8 9 10 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 4 2 2 3 4 5 12 13 14 12 6 7 8 9 10 11 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_14()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 5 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 3 4 4 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 4 8 10 10 1 2 3 4 11 12 13 14 5 6 7 8 9 10 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 1 3 4 2 2 3 4 5 12 13 14 15 6 7 8 9 10 11 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_15()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 7 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 1 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 13 14 15 13 5 6 7 8 9 10 11 12 4 0 0 0 12 12 0 0 11 12 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 14 15 16 14 6 7 8 9 10 11 12 13 1 3 0 0 4 2 0 0 12 13 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_16()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 7 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 1 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 13 14 15 16 5 6 7 8 9 10 11 12 4 0 0 0 12 12 0 0 11 12 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 14 15 16 17 6 7 8 9 10 11 12 13 1 3 0 0 4 2 0 0 12 13 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_17()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 8 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 1 2 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 15 16 17 15 5 6 7 8 9 10 11 12 4 0 0 0 12 14 14 0 13 14 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 16 17 18 16 6 7 8 9 10 11 12 13 1 3 0 0 4 7 2 0 14 15 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_18()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 8 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 1 2 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 15 16 17 18 5 6 7 8 9 10 11 12 4 0 0 0 12 14 14 0 13 14 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 16 17 18 19 6 7 8 9 10 11 12 13 1 3 0 0 4 7 2 0 14 15 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_19()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 8 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 1 2 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 17 18 19 17 5 6 7 8 9 10 11 12 4 0 0 0 12 16 16 16 13 14 15 16 15 16 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 18 19 20 18 6 7 8 9 10 11 12 13 1 3 0 0 4 7 2 2 14 15 16 17 16 17 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_20()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 8 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 1 2 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 17 18 19 20 5 6 7 8 9 10 11 12 4 0 0 0 12 16 16 16 13 14 15 16 15 16 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 18 19 20 21 6 7 8 9 10 11 12 13 1 3 0 0 4 7 2 2 14 15 16 17 16 17 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_21()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 1 3 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 19 20 21 19 5 6 7 8 9 10 11 12 4 0 0 0 12 16 18 18 13 14 15 16 17 18 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 20 21 22 20 6 7 8 9 10 11 12 13 1 3 0 0 4 7 8 2 14 15 16 17 18 19 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_22()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 1 3 4 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 8 8 10 10 1 2 3 4 19 20 21 22 5 6 7 8 9 10 11 12 4 0 0 0 12 16 18 18 13 14 15 16 17 18 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 4 2 2 3 4 5 20 21 22 23 6 7 8 9 10 11 12 13 1 3 0 0 4 7 8 2 14 15 16 17 18 19 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_23()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 10 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 3 1 4 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 21 22 23 21 5 6 7 8 9 10 11 12 4 8 12 0 20 20 18 18 13 14 15 16 17 18 19 20 12 0 0 0 19 20 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 22 23 24 22 6 7 8 9 10 11 12 13 1 3 4 7 8 2 2 2 14 15 16 17 18 19 20 21 4 7 0 0 20 21 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_24()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 10 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 3 1 4 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 21 22 23 24 5 6 7 8 9 10 11 12 4 8 12 0 20 20 18 18 13 14 15 16 17 18 19 20 12 0 0 0 19 20 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 22 23 24 25 6 7 8 9 10 11 12 13 1 3 4 7 8 2 2 2 14 15 16 17 18 19 20 21 4 7 0 0 20 21 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_25()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 3 2 4 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 23 24 25 23 5 6 7 8 9 10 11 12 4 8 12 0 20 22 22 18 13 14 15 16 17 18 19 20 12 0 0 0 21 22 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 24 25 26 24 6 7 8 9 10 11 12 13 1 3 4 7 8 10 2 2 14 15 16 17 18 19 20 21 4 7 0 0 22 23 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_26()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 3 2 4 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 23 24 25 26 5 6 7 8 9 10 11 12 4 8 12 0 20 22 22 18 13 14 15 16 17 18 19 20 12 0 0 0 21 22 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 24 25 26 27 6 7 8 9 10 11 12 13 1 3 4 7 8 10 2 2 14 15 16 17 18 19 20 21 4 7 0 0 22 23 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_27()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 3 2 4 4 2 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 25 26 27 25 5 6 7 8 9 10 11 12 4 8 12 0 20 24 24 24 13 14 15 16 17 18 19 20 23 24 0 0 21 22 23 24 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 26 27 28 26 6 7 8 9 10 11 12 13 1 3 4 7 8 10 2 2 14 15 16 17 18 19 20 21 24 25 0 0 22 23 24 25 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_28()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 9 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 3 2 4 4 2 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 25 26 27 28 5 6 7 8 9 10 11 12 4 8 12 0 20 24 24 24 13 14 15 16 17 18 19 20 23 24 0 0 21 22 23 24 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 26 27 28 29 6 7 8 9 10 11 12 13 1 3 4 7 8 10 2 2 14 15 16 17 18 19 20 21 24 25 0 0 22 23 24 25 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_29()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 11 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 3 4 4 3 3 4 4 2 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 27 28 29 27 5 6 7 8 9 10 11 12 4 8 12 0 20 24 26 26 13 14 15 16 17 18 19 20 25 26 0 0 21 22 23 24 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 28 29 30 28 6 7 8 9 10 11 12 13 1 3 4 7 8 10 9 2 14 15 16 17 18 19 20 21 26 27 0 0 22 23 24 25 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_30()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 11 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 3 3 4 4 2 4 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 16 10 10 1 2 3 4 27 28 29 30 5 6 7 8 9 10 11 12 4 8 12 0 20 24 26 26 13 14 15 16 17 18 19 20 25 26 0 0 21 22 23 24 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 6 2 2 2 3 4 5 28 29 30 31 6 7 8 9 10 11 12 13 1 3 4 7 8 10 9 2 14 15 16 17 18 19 20 21 26 27 0 0 22 23 24 25 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_31()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 12 2 3 4 5 6 7 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 2 4 3 4 4 3 1 4 4 4 4 1 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 24 24 10 1 2 3 4 29 30 31 29 5 6 7 8 9 10 11 12 4 8 12 0 28 28 26 26 13 14 15 16 17 18 19 20 25 26 27 28 21 22 23 24 20 0 0 0 27 28 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 11 6 2 2 3 4 5 30 31 32 30 6 7 8 9 10 11 12 13 1 3 4 7 9 2 2 2 14 15 16 17 18 19 20 21 26 27 28 29 22 23 24 25 8 10 0 0 28 29 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }
  static String test_put_save_32()
   {return """
32
4
8
8
6
3
1 32 1 0 1 1 1 1 0 0 1 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
1 32 1 0 0 0 0 0 0 1 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 32 1 6 2 3 4 5 6 12 8 9 11 9 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 0
3 32 1 1 4 4 4 4 3 1 4 4 4 4 3 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 16 24 24 10 1 2 3 4 29 30 31 32 5 6 7 8 9 10 11 12 4 8 12 0 28 28 26 26 13 14 15 16 17 18 19 20 25 26 27 28 21 22 23 24 20 24 28 0 27 28 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
8 32 4 5 11 6 2 2 3 4 5 30 31 32 33 6 7 8 9 10 11 12 13 1 3 4 7 9 2 2 2 14 15 16 17 18 19 20 21 26 27 28 29 22 23 24 25 8 10 9 2 28 29 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
""";
   }

  static void test_put_descending()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("put");
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register l = P.register("l", 1);
    P.processTrace = true;
    b.maxSteps     = 20000;
    b.suppressMerge = true;                                                     // Suppress merges as they have not been developed yet

    final int N = 32;
    k.RegisterSet(N);
    d.RegisterSet(0);
    P.new Block()
     {void code()
       {d.Copy(k); d.Inc();
        b.put(k, d);
        k.Dec();
        l.Gt(k, 0);
        P.GONotZero(start, l);
       }
     };
    b.chipRun();
    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                                                                            16                                        24                                       |
                                                                            0                                         0.1                                      |
                                                                            22                                        14                                       |
                                                                                                                      15                                       |
                               8                    12                                            20                                       28                  |
                               22                   22.1                                          14                                       15                  |
                               24                   20                                            12                                       5                   |
                                                    17                                            9                                        6                   |
           4        6                    10                      14                    18                   22                   26                  30        |
           24       24.1                 20                      17                    12                   9                    5                   6         |
           25       23                   19                      16                    11                   8                    4                   1         |
                    21                   18                      13                    10                   7                    3                   2         |
1,2,3,4=25   5,6=23     7,8=21   9,10=19   11,12=18     13,14=16   15,16=13   17,18=11   19,20=10   21,22=8   23,24=7    25,26=4   27,28=3   29,30=1   31,32=2 |
""");
   }

  static void test_put_random()
   {sayCurrentTestName();
    final Btree            b = new Btree(32, 4, 8, 8);
    final Process          P = b.new Process("putReverse");
    final Process.Register i = P.register("i", b.btreeAddressSize);
    final Process.Register k = P.register("k", b.bitsPerKey);
    final Process.Register d = P.register("d", b.bitsPerData);
    final Process.Register l = P.register("l", 1);
    P.processTrace  = true;
    b.maxSteps      = 30000;
    b.suppressMerge = true;                                                     // Suppress merges as they have not been developed yet

    i.RegisterSet(random_32.length);

    P.new Block()
     {void code()
       {P.new Instruction()
         {void action()
           {k.registerSet(random_32[i.registerGet()-1]);
            i.dec();
           }
          void verilog(Verilog v)
           {v.new Case(1, random_32.length+1, i.registerName())
             {void Choice(int I)
               {k.registerSet(v, random_32[I-1]);
                i.dec(v);
               }
             };
           }
         };

        b.put(k, d);
        l.Gt(i, 0);
        P.GONotZero(start, l);
       }
     };
    b.chipRun();

    //stop(b.btreePrint());
    ok(b.btreePrint(), """
                                                                    16                                                                     |
                                                                    0                                                                      |
                                                                    15                                                                     |
                                                                    16                                                                     |
                                         10                                                                   25                           |
                                         15                                                                   16                           |
                                         11                                                                   13                           |
                                         5                                                                    6                            |
         3        5           8                        13                           20            23                       28              |
         11       11.1        11.2                     5                            13            13.1                     6               |
         17       12          8                        10                           3             14                       7               |
                              4                        1                                          9                        2               |
1,2,3=17   4,5=12     6,7,8=8     9,10=4   11,12,13=10   14,15,16=1   17,18,19,20=3   21,22,23=14     24,25=9   26,27,28=7   29,30,31,32=2 |
""");
   }

  static void test_verilog_put()
   {sayCurrentTestName();
    final Btree            b = new Btree(1024, 32, 32, 32);
    final Process          P = b.new Process("verilogPut");
    final Process.Register k = P.register("k", b.bitsPerKey);   k.input();
    final Process.Register d = P.register("d", b.bitsPerData);  d.input();

    k.RegisterSet(1);
    d.RegisterSet(11);
    b.chipRunJava();                                                            // Set memory
    b.put(k, d);
//  final Chip.Synthesize S = b.new Synthesize();
//  ok(S.e.out, "");
//  ok(S.e.err, "");
    final Chip.SiliconCompiler S = b.new SiliconCompiler()                      // Create silicon compiler files
     {String description()
       {return String.format("%s_%d_%d_%d_%d",
          b.chipName, b.size, b.maxStuckSize, b.bitsPerKey, b.bitsPerData);
       }
     };
    say(S.launchFile);
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_create1();
    test_create2();
    test_push();
    test_clear();
    test_pop();
    test_pop_4();
    test_firstLastPast();
    test_elementAt();
    test_setElementAt();
    test_setDataAt();
    test_setPastLastElement();
    test_insertElementAt();
    test_removeElementAt();
    test_search_eq();
    test_search_eq_parallel();
    test_search_le();
    test_splitIntoTwo();
    test_splitIntoThree();
    test_splitLow();
    test_splitLowButOne();
    test_merge();
    test_merge2();
    test_mergeButOne();
    test_mergeButOne2();
    test_allocate();
    test_splitRootLeaf();
    test_mergeLeavesIntoRoot();
    test_mergeLeavesNotTop();
    test_mergeLeavesAtTop();
    test_mergeBranchesIntoRoot();
    test_mergeBranchesNotTop();
    test_mergeBranchesAtTop();
    test_find();
    test_findAndInsert();
    test_delete_ascending();
    test_delete_random();
    test_delete_descending();
    test_delete_random_descending();
    test_put_ascending();
    test_put_merge();
    test_put_reload();
    test_put_descending();
    test_put_random();
    test_verilog_put();
   }

  static void newTests()                                                        // Tests being worked on
   {//oldTests();
    test_verilog_put();
   }

  public static void main(String[] args)                                        // Test if called as a program
   {try                                                                         // Get a traceback in a format clickable in Geany if something goes wrong to speed up debugging.
     {if (github_actions) oldTests(); else newTests();                          // Tests to run
      if (github_actions)                                                       // Coverage analysis
       {//coverageAnalysis(sourceFileName(), 12);
       }
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
