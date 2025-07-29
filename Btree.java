//------------------------------------------------------------------------------
// Btree using block memory, Written July 1-12, 2025
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.

import java.util.*;

class Btree extends Chip                                                        // Manipulate a btree in a block of memory
 {final int size;                                                               // The maximum number of nodes == branches or leaves in the btree
  final int maxStuckSize;                                                       // The maximum number of entries in the stuck.
  final int bitsPerKey;                                                         // The number of bits needed to define a key
  final int bitsPerData;                                                        // The number of bits needed to define a data field
  final int btreeAddressSize;                                                   // The number of bits needed to address a field in memory holding btree
  final int stuckAddressSize;                                                   // The number of bits needed to address a field in a stuck
  final Memory  stuckIsLeaf;                                                    // Whether the current stuck is acting as a leaf or a branch in the btree.
  final Memory  stuckIsFree;                                                    // Whether the stuck is on the free chain
  final Memory  freeNext;                                                       // Next stuck on the free chain. If this stuck is not on the free chain then this field is zero to show that this stuck in use. If the stuck is the root stuck which is never freed, then its next pointer points to the first free stuck on the free chain.
  final Memory  stuckSize;                                                      // Current size of stuck up to the maximum size
  final Memory[]stuckKeys;                                                      // Keys field
  final Memory[]stuckData;                                                      // Data field
  boolean supressMerge = false;                                                 // Supress merges during put to allow merge steps to be tested individually.  If this is on the trees built for testing are already merged so there is nothing to test.
  static boolean debug = false;                                                 // Debug if enabled

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
    stuckKeys    = new Memory[MaxStuckSize];                                    // Keys field
    stuckData    = new Memory[MaxStuckSize];                                    // Data field

    for (int i = 0; i < MaxStuckSize; i++)
     {stuckKeys[i] = new Memory("stuckKeys_"+i, Size, bitsPerKey);              // Keys field
      stuckData[i] = new Memory("stuckData_"+i, Size, bitsPerData);             // Data field
     }

chipStop = true;
    //iCreateFreeChain();                                                       // Create the free chain
   }
//
//  Process.Register index       (Process P) {return P.register("index", logTwo(size)+1);} // Create an index for a stuck in a btree
//  Process.Register isLeaf      (Process P) {return P.register("isLeaf",             1);} // Create a bit for is leaf
//  Process.Register isFull      (Process P) {return P.register("isFull",             1);} // Create a bit for is full
//  Process.Register isFullButOne(Process P) {return P.register("isFullButOne",       1);} // Create a bit for is full but one
//  Process.Register within      (Process P) {return P.register("within",             1);} // Create a bit for is within the body of the stuck and not at the top
//  Process.Register found       (Process P) {return P.register("found",              1);} // Create a bit for whether a key has been found
//  Process.Register leafFull    (Process P) {return P.register("leafFull",           1);} // Create a bit for a leaf is full
//  Process.Register branchFull  (Process P) {return P.register("branchFull",         1);} // Create a bit for a branch is full

//D2 Allocation                                                                 // Allocate stucks from the free chain
/*
  void createFreeChain()                                                        // Create the free chain
   {final Layout.Field index = index();
    for (int i = 1; i < size; i++)
     {freeStart.write(1);
      index.write(i);
      freeNext.write(i+1 == size ? 0 : i+1);
      stuckIsFree.write(1);
      freeNext   .write(index);
      stuckIsFree.write(index);
      setRootAsLeaf();
     }
   }

  void iCreateFreeChain()                                                       // Create the free chain
   {final Layout.Program p = L.startNewProgram();
    L.P.new Instruction()
     {void action()
       {createFreeChain();
       }
     };
    L.runProgram();
    L.continueProgram(p);
   }

  private void allocate(Process.Register ref, boolean leaf)                         // Allocate a stuck and set a ref to the allocated node
   {final Layout.Field index = index();
    if (freeStart.value == 0)                                                   // Check memory
     {stopProgram("Out of memory");
      return;
     }
    index.move(freeStart);                                                      // Head of free chain gives allocated stuck
    ref  .move(freeStart);                     ;                                // Head of free chain gives allocated stuck
    stuckIsFree.zero(index);                                                    // Show as in use
    freeNext .read(index);                                                      // Locate next stuck on free chain to become new first stuck on free chain
    freeStart.move(freeNext);                                                   // Next stuck on free chain becomes head of free chain
    freeNext .zero(index);                                                      // Clear the next field from the current stuck
    if (leaf) setLeaf(ref); else setBranch(ref);
   }

  private void iAllocate(Process.Register ref, boolean leaf)                        // Allocate a stuck and set a ref to the allocated node
   {L.P.new Instruction()
     {void action()
       {allocate(ref, leaf);
       }
     };
   }

  private void  allocateLeaf  (Process.Register ref) { allocate(ref, true);}        // Allocate a stuck, set a ref to the allocated node and mark it a leaf
  private void  allocateBranch(Process.Register ref) { allocate(ref, false);}       // Allocate a stuck, set a ref to the allocated node and mark it a branch

  private void iAllocateLeaf  (Process.Register ref) {iAllocate(ref, true);}        // Allocate a stuck, set a ref to the allocated node and mark it a leaf
  private void iAllocateBranch(Process.Register ref) {iAllocate(ref, false);}       // Allocate a stuck, set a ref to the allocated node and mark it a branch

  private void free(Process.Register ref)                                           // Free the indicated stuck to make it available for reuse
   {if (ref.value == 0)                                                         // The root stuck cannot be freed
     {stopProgram("Cannot free the root stuck");
      return;
     }
    freeNext   .move(freeStart);
    freeNext   .write(ref);                                                     // Append the free chain to this stuck
    freeStart  .move (ref);                                                     // This stuck becomes the first stuick on the free chain
    stuckIsFree.one  (ref);                                                     // Show as free
   }

  private void iFree(Process.Register ref)                                          // Free the indicated stuck to make it available for reuse
   {L.P.new Instruction()
     {void action()
       {free(ref);
       }
     };
   }
*/
//D2 Stuck                                                                      // Get and set stucks within btree

  class Stuck extends Process                                                   // A reference to a stuck in the memory of the btree plus a copy of its size, keys and data
   {final Register index     = register("index",    btreeAddressSize);          // The address of the stuck in main memory
    final Register size      = register("size",     stuckAddressSize);          // Size of the stuck locally
    final Register isLeaf    = register("isLeaf",   1);                         // Whether the stuck is a leaf
    final Register nextFree  = register("nextFree", btreeAddressSize);          // Free chain from root
    final Register[]keys     = new Register[maxStuckSize];                      // Keys in the stuck copied out of memory
    final Register[]data     = new Register[maxStuckSize];                      // Data in the stuck copied out of memory
    final Memory.Get gSize   = stuckSize.new Get(this);                         // Transaction to get the current size of the stuck
    final Memory.Set sSize   = stuckSize.new Set(this);                         // Transaction to set the current size of the stuck
    final Memory.Get gLeaf   = stuckIsLeaf.new Get(this);                       // Transaction to discoeve whether this stuck is acting as a leaf or a branch
    final Memory.Set sLeaf   = stuckIsLeaf.new Set(this);                       // Transaction to set the stuck in memory to  a leaf or a branch
    final Memory.Get[]gKeys  = new Memory.Get[maxStuckSize];                    // Transactions to get each key in the stuck
    final Memory.Set[]sKeys  = new Memory.Set[maxStuckSize];                    // Transactions to set each key in the stuck
    final Memory.Get[]gData  = new Memory.Get[maxStuckSize];                    // Transactions to get each data element in the stuck
    final Memory.Set[]sData  = new Memory.Set[maxStuckSize];                    // Transactions to set each data element in the stuck

    Stuck(String Name)                                                          // Mirror a stuck in memory with one in registers.
     {super(Name); N();
      for (int i = 0; i < maxStuckSize; i++)                                    // Create registers to hold stuck
       {keys[i]  = new Register("Key_"+i, bitsPerKey);                          // Keys in the stuck copied out of the memory of the btree into local registers
        data[i]  = new Register("Data_"+i, bitsPerData);                        // Data in the stuck copied out of the memory of the btree into local registers
        gKeys[i] = stuckKeys[i].new Get(this);                                  // Transactions to get each key in the stuck
        sKeys[i] = stuckKeys[i].new Set(this);                                  // Transactions to get each key in the stuck
        gData[i] = stuckData[i].new Get(this);                                  // Transactions to get each data element in the stuck
        sData[i] = stuckData[i].new Set(this);                                  // Transactions to get each data element in the stuck
       }
     }

//D3 Memory                                                                     // Get a stuck from memory or return it to memory

    void get(Register Index)                                                    // Copy a stuck indexed by a register out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retriveal of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {new Instruction()
       {void action()
         {index.copy(Index);
         }
       };
      get();
     }

    void getRoot()                                                              // Copy the root stuck out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retriveal of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {new Instruction()
       {void action()
         {index.registerSet(0);
         }
       };
      get();
     }

    void get()                                                                  // Copy the indexed stuck out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retriveal of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {new Instruction()                                                         // Request the details of the indexed stuck from memory
       {void action()
         {gSize.executeTransaction(index);
          gLeaf.executeTransaction(index);
          for (int i = 0; i < maxStuckSize; i++)
           {gKeys[i].executeTransaction(index);
            gData[i].executeTransaction(index);
           }
         }
       };

      gSize.waitResultOfTransaction();                                          // Wait for size from memory
      gLeaf.waitResultOfTransaction();                                          // Wait for leaf status from memeory

      for (int i = 0; i < maxStuckSize; i++)                                    // Wait for transactions for keys and data from memmory to complete
       {gKeys[i].waitResultOfTransaction();
        gData[i].waitResultOfTransaction();
       }

      new Instruction()                                                         // Load results from transactions into local registers
       {void action()
         {size.copy(gSize.transactionOutputRegisters.firstElement());
          isLeaf.copy(gSize.transactionOutputRegisters.firstElement());
          for (int i = 0; i < maxStuckSize; i++)
           {keys[i].copy(gKeys[i].transactionOutputRegisters.firstElement());
            data[i].copy(gData[i].transactionOutputRegisters.firstElement());
           }
         }
       };
     }

    void set()                                                                  // Update a stuck in memory from the registers describing his stuck
     {new Instruction()                                                         // Save registers into memory
       {void action()
         {sSize.executeTransaction(index, size);
          sLeaf.executeTransaction(index, isLeaf);
          for (int i = 0; i < maxStuckSize; i++)
           {sKeys[i].executeTransaction(index, keys[i]);
            sData[i].executeTransaction(index, data[i]);
           }
         }
       };

      sSize.waitResultOfTransaction();                                          // Wait for size to complete
      sLeaf.waitResultOfTransaction();                                          // Wait for leaf status to complete

      for (int i = 0; i < maxStuckSize; i++)                                    // Wait for transactions for keys and data from memmory to complete
       {sKeys[i].waitResultOfTransaction();
        sData[i].waitResultOfTransaction();
       }
     }

//D3 Print                                                                      // Print the stuck

    public String toString()                                                    // Print the stuck
     {final StringBuilder s = new StringBuilder();
      final int     ns = size  .registerGet();                                  // Size of stuck
      final int     il = isLeaf.registerGet() > 0 ? 1 : 0;                      // Is a leaf
      final String  nm = processName;                                           // Name of the stuck is the same as the name of the process
      s.append("Stuck: "+nm+" size: "+ns+", leaf: "+il+"\n");                   // Title

      for (int i = 0; i < ns; i++)                                              // Each key, data pair
       {final int k = keys[i].registerGet();
        final int d = data[i].registerGet();
        s.append(String.format("%2d  %4d => %4d\n", i, k, d));
       }
      return ""+s;
     }

    String dump()                                                               // Print all the entries in the stuck
     {final StringBuilder s = new StringBuilder();
      final int     ns = size  .registerGet();                                  // Size of stuck
      final int     il = isLeaf.registerGet() > 0 ? 1 : 0;                      // Is a leaf
      final String  nm = processName;                                           // Name of the stuck is the same as the name of the process
      s.append("Stuck: "+nm+" size: "+ns+", leaf: "+il+"\n");                   // Title

      for (int i = 0; i < maxStuckSize; i++)                                    // Each key, data pair
       {final int k = keys[i].registerGet();
        final int d = data[i].registerGet();
        s.append(String.format("%2d  %4d => %4d\n", i, k, d));
       }
      return ""+s;
     }

//D3 Actions                                                                    // Just the actions needed on a stuck to support a btree

    void push(Register Key, Register Data)                                      // Push a key, data pair to the local copy of the stuck
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N >= maxStuckSize)
           {chipStop(1);
            return;
           }
          keys[N].copy(Key);
          data[N].copy(Data);
          size.inc();
         }
       };
     }

    void pop(Register Key, Register Data)                                       // Pop a key, data pair from the local copy of the stuck
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N == 0)
           {chipStop(2);
            return;
           }
          Key.copy (keys[N-1]);
          Data.copy(data[N-1]);
          size.dec();
         }
       };
     }

    void setPastLastElement(Register Key, Register Data)                        // Push a key, data pair to the local copy of the stuck without changing the size
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N >= maxStuckSize - 1)
           {chipStop(3);
            return;
           }
          keys[N].copy(Key);
          data[N].copy(Data);
         }
       };
     }

    void firstElement(Register Key, Register Data)                              // Get the first key, data pair
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N == 0)
           {chipStop(4);
            return;
           }
          Key.copy (keys[0]);
          Data.copy(data[0]);
         }
       };
     }

    void lastElement(Register Key, Register Data)                               // Get the last key, data pair
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N >= maxStuckSize)
           {chipStop(5);
            return;
           }
          Key.copy (keys[N-1]);
          Data.copy(data[N-1]);
         }
       };
     }

    void pastLastElement(Register Key, Register Data)                           // Get the past last key, data pair
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (N >= maxStuckSize-1)
           {chipStop(6);
            return;
           }
          Key.copy (keys[N]);
          Data.copy(data[N]);
         }
       };
     }

    void elementAt(Register Index, Register Key, Register Data)                 // Get the indexed key, data pair
     {new Instruction()
       {void action()
         {final int N = Index.registerGet();
          if (N >= maxStuckSize)
           {chipStop(7);
            return;
           }
          Key.copy (keys[N]);
          Data.copy(data[N]);
         }
       };
     }

    void setElementAt(Register Index, Register Key, Register Data)              // Set the indexed key, data pair
     {new Instruction()
       {void action()
         {final int N = Index.registerGet();
          final int M = size.registerGet();
          if (N >= maxStuckSize)
           {chipStop(8);
            return;
           }
          if (N > M)
           {chipStop(9);
            return;
           }
          if (N == M)
           {size.inc();
           }
          keys[N].copy(Key);
          data[N].copy(Data);
         }
       };
     }

    void setDataAt(Register Index, Register Data)                               // Set the indexed data pair
     {new Instruction()
       {void action()
         {final int N = Index.registerGet();
          final int M = size.registerGet();
          if (N >= M)
           {chipStop(10);
            return;
           }
          data[N].copy(Data);
         }
       };
     }

    void insertElementAt(Register Index, Register Key, Register Data)              // Set the indexed key, data pair
     {new Instruction()
       {void action()
         {final int N = Index.registerGet();
          final int M = size.registerGet();
          if (N >= maxStuckSize-1)
           {chipStop(11);
            return;
           }
          if (N > M)
           {chipStop(12);
            return;
           }
          if (N != M)
           {size.inc();
           }
          for (int i = M; i > N; i--)
           {keys[i].copy(keys[i-1]);
            data[i].copy(data[i-1]);
           }
          keys[N].copy(Key);
          data[N].copy(Data);
         }
       };
     }

    void removeElementAt(Register Index, Register Key, Register Data)              // Set the indexed key, data pair
     {new Instruction()
       {void action()
         {final int N = Index.registerGet();
          final int M = size.registerGet();
          if (N >= M)
           {chipStop(13);
            return;
           }
          size.dec();
          Key.copy (keys[N]);
          Data.copy(data[N]);
          for (int i = N; i < M-1; i++)
           {keys[i].copy(keys[i+1]);
            data[i].copy(data[i+1]);
           }
         }
       };
     }

//D3 Search                                                                     // Search the stuck

    void search_eq(Register Key, Register Found, Register Index, Register Data) // Find the specified key if possible in the stuck
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          Found.zero();
          for (int i = 0; i < N; ++i)
           {final int I = i;
             if (keys[i].registerGet() == Key.registerGet())
             {Found.one();
              Index.registerSet(I);
              Data.copy(data[I]);
              return;
             }
           }
         }
       };
     }


    void search_le(Register Key, Register Found, Register Index,                // Find the first key in the stuck so that the search key is less than or equal to this key
      Register FoundKey, Register FoundData)
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          Found.zero();
          for (int i = 0; i < N; ++i)
           {final int I = i;
             if (Key.registerGet() <= keys[i].registerGet())
             {Found.one();
              Index.registerSet(I);
              FoundKey .copy(keys[I]);
              FoundData.copy(data[I]);
              return;
             }
           }
         }
       };
     }

//D3 Split                                                                      // Split stucks in many and various ways

    void splitIntoTwo(Stuck Left, Stuck Right, int Copy)                        // Copy the first key, data pairs into the left stuck, the remainder into the right stuck.  The original source stuck is not modifiedr
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (Copy >= N)
           {chipStop(14);
            return;
           }

          for (int i = 0; i < Copy; ++i)
           {Left.keys[i].copy(keys[i]);
            Left.data[i].copy(data[i]);
           }
          Left .size.registerSet(Copy);

          for (int i = 0; i < N - Copy; ++i)
           {Right.keys[i].copy(keys[Copy+i]);
            Right.data[i].copy(data[Copy+i]);
           }
          Right.size.registerSet(N - Copy);
         }
       };
     }

    void splitIntoThree(Stuck Left, Stuck Right, int Copy)                      // Copy the specified number of key, data pairs into the left stuck, skip one pair, then copy the specified number onto into the right stuck
     {new Instruction()
       {void action()
         {final int N = size.registerGet();
          if (Copy >= N)
           {chipStop(15);
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
          Right.size.registerSet(N - Copy);
          Right.data[Copy].copy(data[2*Copy+1]);
         }
       };
     }

    void splitLow(Stuck Left)                                                   // Split a full stuck with an even number of elements so that the first half is moved into the left stuck leaving the remainder moved down in the current stuck
     {new Instruction()
       {void action()
         {if (maxStuckSize % 2 == 1)
           {chipStop(16);
            return;
           }
          if (maxStuckSize != size.registerGet())
           {chipStop(17);
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
       };
     }

    void splitLowButOne(Stuck Left, Register Key)                               // Split an almost full stuck with an odd number of elements so that the first half is moved into the left stuck extended by the data of the central element, while the remainder beyond the central element are moved down.  The central key is returned as it would otherwiose be lost.
     {new Instruction()
       {void action()
         {if (maxStuckSize % 2 == 1)
           {chipStop(18);
            return;
           }
          if (maxStuckSize-1 != size.registerGet())
           {chipStop(19);
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

          for (int i = 0; i < N; ++i)
           {keys[i].copy(keys[N+i+1]);
            data[i].copy(data[N+i+1]);
           }
          size.registerSet(N);
         }
       };
     }

//D3 Merge                                                                      // Merge stucks in various weays

    void merge(Stuck source, Register Success)                                  // Concatenate the indicated stuck on to the end of the current one
     {new Instruction()
       {void action()
         {final int S = source.size.registerGet();
          final int T =        size.registerGet();
          Success.zero();
          if (S + T > maxStuckSize)
           {return;
           }
          for (int i = 0; i < S; ++i)
           {keys[T+i].copy(source.keys[i]);
            data[T+i].copy(source.data[i]);
           }
          size.add(source.size);
          Success.one();
         }
       };
     }

    void merge(Stuck Left, Stuck Right, Register Success)                       // Replace the current stuck with the concatenation of teh two stucks indicated
     {new Instruction()
       {void action()
         {final int L = Left .size.registerGet();
          final int R = Right.size.registerGet();
          Success.zero();
          if (L + R > maxStuckSize)
           {return;
           }
          size.zero();
          Success.one();
         }
       };
      merge(Left,  Success);
      merge(Right, Success);
     }

   } // Stuck
/*

//D1 Attributes                                                                 // Get and set attributes

  void setRootAsLeaf()                                                          // Set the root to be a leaf
   {final Layout.Field i = index();
    i.zero();
    stuckIsLeaf.one(i);
   }

  void iSetRootAsLeaf()                                                         // Set the root to be a leaf
   {final Layout.Field i = index();
    i.iZero();
    stuckIsLeaf.iOne(i);
   }

  void setRootAsBranch()                                                        // Set the root to be a branch
   {final Layout.Field i = index();
    i.zero();
    stuckIsLeaf.zero(i);
   }

  void iSetRootAsBranch()                                                       // Set the root to be a branch
   {final Layout.Field i = index();
    i.iZero();
    stuckIsLeaf.iZero(i);
   }

  void setLeaf  (Layout.Field i)  {stuckIsLeaf.one (i);}                        // Set a stuck in the btree to be a leaf
  void setBranch(Layout.Field i)  {stuckIsLeaf.zero(i);}                        // Set a stuck in the btree to be a branch

  void iSetLeaf  (Layout.Field i) {stuckIsLeaf.iOne (i);}                       // Set a stuck in the btree to be a leaf
  void iSetBranch(Layout.Field i) {stuckIsLeaf.iZero(i);}                       // Set a stuck in the btree to be a branch

  void isLeaf(Layout.Field index, Layout.Field isLeaf)                          // Is leaf at indicated index
   {stuckIsLeaf.read(index);
    isLeaf.move(stuckIsLeaf);
   }

  void iIsLeaf(Layout.Field index, Layout.Field isLeaf)                         // Is leaf at indicated index
   {L.P.new Instruction()
     {void action()
       {isLeaf(index, isLeaf);
       }
     };
   }

  void isRootLeaf(Layout.Field isLeaf)                                          // Is the root a leaf?
   {L.P.new Instruction()
     {void action()
       {final Layout.Field i = index();
        i.zero();
        stuckIsLeaf.read(i);
        isLeaf.move(stuckIsLeaf);
       }
     };
   }

  void isRootLeafFull(Layout.Field isFull)                                      // Is the root assumed to be a leaf full?
   {L.P.new Instruction()
     {void action()
       {final Layout.Field i = index();
        i.zero();
        stuckSize.read(i);
        isFull.value = stuckSize.value >= maxStuckSize ? 1 : 0;
       }
     };
   }

  void iIsRootBranchFull(Layout.Field isFullButOne)                             // Is the root assumed to be a root full?
   {L.P.new Instruction()
     {void action()
       {final Layout.Field i = index();
        i.zero();
        stuckSize.read(i);
        isFullButOne.value = stuckSize.value >= maxStuckSize-1 ? 1 : 0;
       }
     };
   }

//D1 Print                                                                      // Print the tree

  class DumpStuck                                                               // Dump the stuck at the specified index
   {final int index;
    final int size;
    final int next;
    final boolean leaf;
    final boolean free;
    final Stack<Integer>keys = new Stack<>();
    final Stack<Integer>data = new Stack<>();
    final Integer top;
    final Stuck stuck = stuck();

    DumpStuck(int Index)
     {index = Index;
      Layout.Field at = index(); at.value = index;

      final Layout.Program p = L.startNewProgram();
      stuckIsLeaf.iRead(at);
      stuckIsFree.iRead(at);
      freeNext   .iRead(at);
      iCopyStuckFrom(stuck, at);
      L.runProgram();
      L.continueProgram(p);

      leaf = stuckIsLeaf.value > 0;

      free = stuckIsFree.value > 0;
      next = freeNext.value;
      size = stuck.stuckSize.value;

      for (int i = 0; i < size; i++)
       {final Layout.Program P = L.startNewProgram();
        stuck.stuckKeys.iRead(i);
        stuck.stuckData.iRead(i);
        L.runProgram();
        L.continueProgram(P);
        keys.push(stuck.stuckKeys.value);
        data.push(stuck.stuckData.value);
       }
      if (!leaf)
       {final Layout.Program P = L.startNewProgram();
        stuck.stuckData.iRead(size);
        L.runProgram();
        L.continueProgram(P);
        top = stuck.stuckData.value;
       }
      else top = null;
     }

    public String toString()
     {final StringBuilder s = new StringBuilder();
      s.append( "index: "+index);
      s.append("  size: "+size);
      s.append("  leaf: "+leaf);
      s.append("  free: "+free);
      s.append("  keys: "+keys);
      s.append("  data: "+data);
      if (top != null) s.append("  top: "+top);
      return ""+s;
     }
   }

  public String toString() {return print();}                                    // Print tree

  String dump()
   {final StringBuilder s = new StringBuilder();
    final Stuck t = stuck();
    final Layout.Field stuckIndex = t.index();
    s.append("Btree\n");
    for (int i = 0; i < size; i++)                                              // Each stuck in the btree
     {final DumpStuck d = new DumpStuck(i);                                     // Load stuck description
      if (d.free) continue;                                                     // Not in use as it is on the free chain

      s.append(String.format("Stuck: %2d   size: %d   free: %d   next: %2d  leaf: %d\n",
        i, d.size, d.free ? 1 : 0, d.next, d.leaf ? 1 : 0));
      s.append(""+d.stuck);
     }
    return ""+s;
   }

//D2 Horizontally                                                               // Print the tree horizontally

    final int linesToPrintABranch =  4;                                         // The number of lines required to print a branch
    final int maxPrintLevels      = 10;                                         // The maximum nu ber of levels to print =- this avoids endless print loops when something goes wrong

    void printLeaf(int BtreeIndex, Stack<StringBuilder>P, int level)            // Print leaf horizontally
     {padStrings(P, level);
      final DumpStuck     S = new DumpStuck(BtreeIndex);

      final StringBuilder s = new StringBuilder();                              // String builder
      for  (int i = 0; i < S.size; i++)
       {s.append(""+S.keys.elementAt(i)+",");
       }
      if (s.length() > 0) s.setLength(s.length()-1);                            // Remove trailing comma if present
      s.append("="+BtreeIndex+" ");
      P.elementAt(level*linesToPrintABranch).append(s.toString());
      padStrings(P, level);
     }

    void printBranch(int BtreeIndex, Stack<StringBuilder>P, int level)          // Print branch horizontally
     {if (level > maxPrintLevels) return;
      padStrings(P, level);
      final DumpStuck S = new DumpStuck(BtreeIndex);
      final int       L = level * linesToPrintABranch;                          // Start line at which to print branch
      final int       K = S.size;                                               // Size of branch

      if (K > 0)                                                                // Branch has key, next pairs
       {for  (int i = 0; i < K; i++)
         {final int key  = S.keys.elementAt(i);
          final int data = S.data.elementAt(i);
          final DumpStuck C = new DumpStuck(data);
          if (C.leaf)
           {printLeaf  (data, P, level+1);
           }
          else
           {printBranch(data, P, level+1);
           }

          P.elementAt(L+0).append(""+S.keys.elementAt(i));                      // Key
          P.elementAt(L+1).append(""+BtreeIndex+(i > 0 ?  "."+i : ""));         // Branch,key, next pair
          P.elementAt(L+2).append(""+S.data.elementAt(i));
         }
       }
      else                                                                      // Branch is empty so print just the index of the branch
       {P.elementAt(L+0).append(""+BtreeIndex+"Empty");
       }
      final int top = S.top;                                                    // Top next will always be present
      P.elementAt(L+3).append(top);                                             // Append top next

      final DumpStuck T = new DumpStuck(top);
      if (T.leaf)                                                               // Print leaf
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
   {z();
    final StringBuilder t = new StringBuilder();                                // Print the lines of the tree that are not blank
    for  (StringBuilder s : S)
     {z();
      final String l = s.toString();
      if (l.isBlank()) continue;
      t.append(l+"|\n");
     }
    return t.toString();
   }

  String print()                                                                // Print a tree horizontally
   {final Stack<StringBuilder> P = new Stack<>();
    final DumpStuck d = new DumpStuck(0);
    if (d.leaf) printLeaf(0, P, 0); else printBranch(0, P, 0);
    return printCollapsed(P);
   }

//D1 Split                                                                      // Split nodes in half to increase the number of nodes in the tree

  private void splitRootLeaf()                                                  // Split a full root leaf
   {final Stuck p = stuck(), l = stuck(), r = stuck();                          // Parent == root, left, right stucks
    final Layout.Field isFull = isFull();
    final Layout.Field cl = index(), cr = index();                              // Indexes of left and right children
    final Layout.Field pl = p.key(), pr = p.key(), plr = p.key();               // Parent key must be smaller than anything in right child yet greater than or equal to anything in the left child

    copyStuckFromRoot(p);                                                       // Load leaf root stuck from btree
    p.isFull(isFull);                                                           // Check whether the leaf root stuck is full
    if (!isFull.asBoolean())
     {L.P.stopProgram("A root leaf must be full before it can be split");
     }
    p.splitIntoTwo(l, r, maxStuckSize / 2);                                     // Split the leaf root in two down the middle
    allocateLeaf(cl); saveStuckInto(l, cl);                                     // Allocate and save left leaf
    allocateLeaf(cr); saveStuckInto(r, cr);                                     // Allocate and save right leaf

    l.lastElement();  pl.move(l.stuckKeys);                                     // Last element of left child
    r.firstElement(); pr.move(r.stuckKeys);                                     // First element of right child
    plr.value = (pl.value + pr.value) / 2;                                      // Mid point key
    p.clear();                                                                  // Clear the root so we can add the left and right children to it.
                                                                                // Update root with new children
    p.stuckKeys.move(plr); p.stuckData.move(cl); p.push();                      // Add reference to left child
    p.stuckData.move(cr);  p.setPastLastElement();                              // Add reference to right child
    saveStuckIntoRoot(p);  setRootAsBranch();                                   // Save the root stuck back into the btree and mark it as a branch
   }

  private void iSplitRootLeaf()                                                 // Split a full root leaf
   {L.P.new Instruction()
     {void action()
       {splitRootLeaf();
       }
     };
   }

  private void splitRootBranch()                                                // Split a full root branch
   {final Stuck p = stuck(), l = stuck(), r = stuck();                          // Parent == root, left, right stucks
    final Layout.Field isFullButOne = isFullButOne();
    final Layout.Field           cl = index(), cr = index();                    // Indexes of left and right children
    final int              midPoint = (maxStuckSize-1) / 2;                     // Mid point in parent

    copyStuckFromRoot(p);                                                       // Load branch root stuck from btree

    p.isFullButOne(isFullButOne);                                               // Check whether the branch root stuck is full
    if (!isFullButOne.asBoolean())
     {L.P.stopProgram("A root branch must be full before it can be split");
     }

    p.splitIntoThree(l, r, midPoint);                                           // Split the branch root in two down the middle
    allocateBranch(cl); saveStuckInto(l, cl);                                   // Allocate and save left branch
    allocateBranch(cr); saveStuckInto(r, cr);                                   // Allocate and save right branch
                                                                                // Update root with new children
    p.stuckKeys.read(midPoint);                                                 // Get splitting key
    p.stuckData.move(cl);                                                       // Refence to left child stuck

    p.clear();                                                                  // Clear the root so we can add the left and right children to it.
    p.push();                                                                   // Add reference to left child
    p.stuckData.move(cr); p.setPastLastElement();                               // Add reference to right child as top element past the end of the stuck
    saveStuckIntoRoot(p);                                                       // Save the root stuck back into the btree and mark it as a branch
   }

  private void iSplitRootBranch()                                               // Split a full root branch
   {L.P.new Instruction()
     {void action()
       {splitRootBranch();
       }
     };
   }

  private void splitLeafNotTop                                                  // Split a full leaf that is not the root and is not the last child of its parent branch which is not full
   (Layout.Field parentIndex, Layout.Field stuckIndex)
   {final Stuck p = stuck(), c = stuck(), l = stuck();                          // Parent which must be a branch which is not full, child at index which must be a full leaf, left and right splits of leaf
    final Layout.Field isFull = isFull(), isFullButOne = isFullButOne();
    final Layout.Field isLeaf = isLeaf();
    final Layout.Field cl = index(), cr = index();                              // Btree indexes of child and left and right children of child
    final Layout.Field ck = p.key(), pl = p.key(), pr = p.key(), plr = p.key(); // Key of child in parent, splitting key which must be smaller than anything in right child of child yet greater than or equal to anything in the left child of child

    copyStuckFrom(p, parentIndex);                                              // Load parent stuck from btree
    p.stuckKeys.read(stuckIndex); ck.move(p.stuckKeys);                         // Key of child
    p.stuckData.read(stuckIndex); cr.move(p.stuckData);                         // Reference to child
    copyStuckFrom(c, p.stuckData);                                              // Load child
    isLeaf(parentIndex, isLeaf);                                                // The parent stuck must be a branch

    if (isLeaf.asBoolean())
     {L.P.stopProgram("Parent must be a branch");
     };
    p.isFullButOne(isFullButOne);                                               // The parent stuck may not be full

    if (isFullButOne.asBoolean())
     {L.P.stopProgram("Parent must not be full");
     }
    isLeaf(cr, isLeaf);                                                         // The child stuck must be a leaf

    if (!isLeaf.asBoolean())
     {L.P.stopProgram("Child must be a leaf");
     }

    c.isFull(isFull);                                                           // The child leaf stuck must be full
    if (!isFull.asBoolean())
     {L.P.stopProgram("Child leaf must be full");
     }

    c.splitLow(l, maxStuckSize / 2);                                            // Split the leaf in two down the middle copying out the lower half
    allocateLeaf(cl); saveStuckInto(l, cl);                                     // Allocate and save left leaf
                      saveStuckInto(c, cr);                                     // Allocate and save left leaf

    l.lastElement();  pl.move(l.stuckKeys);                                     // Last element of left child
    c.firstElement(); pr.move(c.stuckKeys);                                     // First element of right child
    plr.value = (pl.value + pr.value) / 2;                                      // Mid point key
    p.stuckKeys.move(plr); p.stuckData.move(cl);
    p.insertElementAt(stuckIndex);                                              // Add reference to left child
    saveStuckInto(p, parentIndex);                                              // Save the parent stuck back into the btree
   }

  private void iSplitLeafNotTop                                                 // Split a full leaf that is not the root and is not the last child of its parent branch which is not full
   (Layout.Field parentIndex, Layout.Field stuckIndex)
   {L.P.new Instruction()
     {void action()
       {splitLeafNotTop(parentIndex, stuckIndex);
       }
     };
   }

  private void splitLeafAtTop(Layout.Field parentIndex)                         // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = stuck(), c = stuck(), l = stuck();                          // Parent which must be a branch which is not full, child at index which must be a full leaf, left and right splits of leaf
    final Layout.Field isFull       = isFull();
    final Layout.Field isFullButOne = isFullButOne();
    final Layout.Field isLeaf       = isLeaf();
    final Layout.Field cl = index(), cr = index();                              // Btree indexes of child and left and right children of child
    final Layout.Field pl = p.key(), pr = p.key(), plr = p.key();               // Key of child in parent, splitting key which must be smaller than anything in right child of child yet greater than or equal to anything in the left child of child

    copyStuckFrom(p, parentIndex);                                              // Load parent stuck from btree
    p.pastLastElement();                                                        // Key of child
    cr.move(p.stuckData);                                                       // Reference to child in btree
    copyStuckFrom(c, p.stuckData);                                              // Load child from btree

    isLeaf(parentIndex, isLeaf);                                                // The parent stuck must be a branch
    if (isLeaf.asBoolean())
     {L.P.stopProgram("Parent must be a branch");
     }

    p.isFullButOne(isFullButOne);                                               // The parent stuck may not be full
    if (isFullButOne.asBoolean())
     {L.P.stopProgram("Parent must not be full");
     }
                                                                                // Update root with new children
    isLeaf(cr, isLeaf);                                                         // The child stuck must be a leaf
    if (!isLeaf.asBoolean())
     {L.P.stopProgram("Child must be a leaf");
     }

    c.isFull(isFull);                                                           // The child stuck must be a leaf
    if (!isFull.asBoolean())
     {L.P.stopProgram("Child leaf must be full");
     }

    c.splitLow(l, maxStuckSize / 2);                                            // Split the leaf in two down the middle copying out the lower half
    allocateLeaf(cl); saveStuckInto(l, cl);                                     // Allocate and save left leaf
                      saveStuckInto(c, cr);                                     // Allocate and save left leaf


    l.lastElement();  pl.move(l.stuckKeys);                                     // Last element of left child
    c.firstElement(); pr.move(c.stuckKeys);                                     // First element of right child
    plr.value = (pl.value + pr.value) / 2;                                      // Mid point key which is always greater than or equal to the left high key and less than the right low key
    p.stuckKeys.move(plr); p.stuckData.move(cl); p.push();                      // Add reference to left child
    p.stuckKeys.zero();
    p.stuckData.move(cr);
    p.setPastLastElement();                                                     // Add reference to not split top child on the right
    saveStuckInto(p, parentIndex);                                              // Save the parent stuck back into the btree
   }

  private void iSplitLeafAtTop(Layout.Field parentIndex)                        // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   {L.P.new Instruction()
     {void action()                                                             // Compute mid point key
       {splitLeafAtTop(parentIndex);
       }
     };
   }

  private void splitBranchNotTop                                                // Split a full branch that is not the root and is not the last child of its parent branch which is not full
   (Layout.Field parentIndex, Layout.Field stuckIndex)
   {final Stuck p = stuck(), c = stuck(), l = stuck(), r = stuck();             // Parent which must be a branch which is not full, child at index which must be a full leaf, left and right splits of leaf
    final Layout.Field isFullButOne = isFullButOne();
    final Layout.Field isLeaf       = isLeaf();
    final Layout.Field cl           = index(), cr = index();                    // Btree indexes of child and left and right children of child
    final Layout.Field ck           = p.key();                                  // Key of child in parent, splitting key which must be smaller than anything in right child of child yet greater than or equal to anything in the left child of child
    final Layout.Field key          = p.key();                                  // The central key

    copyStuckFrom(p, parentIndex);                                              // Load parent stuck from btree
    p.stuckKeys.read(stuckIndex); ck.move(p.stuckKeys);                         // Key of child
    p.stuckData.read(stuckIndex); cr.move(p.stuckData);                         // Reference to child
    copyStuckFrom(c, p.stuckData);                                              // Load child

    isLeaf(parentIndex, isLeaf);                                                // The parent stuck must be a branch
    if (isLeaf.asBoolean())
     {L.P.stopProgram("Parent must be a branch");
     }

    p.isFullButOne(isFullButOne);                                               // The parent stuck may not be full
    if (isFullButOne.asBoolean())
     {L.P.stopProgram("Parent must not be full");
     }

    isLeaf(cr, isLeaf);                                                         // The child stuck must be a branch
    if (isLeaf.asBoolean())
     {L.P.stopProgram("Child must be a branch");
     }

    c.isFullButOne(isFullButOne);                                               // The child stuck must be a leaf
    if (!isFullButOne.asBoolean())
     {L.P.stopProgram("Child branch must be full");
     }

    c.splitLowButOne(l, (maxStuckSize-1) / 2, key);                             // Split the leaf in two down the middle copying out the lower half

    allocateBranch(cl); saveStuckInto(l, cl);                                   // Allocate and save left leaf
                        saveStuckInto(c, cr);                                   // Allocate and save left leaf
                                                                                // Update root with new children
    p.stuckKeys.move(key); p.stuckData.move(cl);
    p.insertElementAt(stuckIndex);                                              // Add reference to left child
    saveStuckInto(p, parentIndex);                                              // Save the parent stuck back into the btree
   }

  private void iSplitBranchNotTop                                               // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   (Layout.Field parentIndex, Layout.Field stuckIndex)
   {L.P.new Instruction()
     {void action()                                                             // Compute mid point key
       {splitBranchNotTop(parentIndex, stuckIndex);
       }
     };
   }

  private void splitBranchAtTop(Layout.Field parentIndex)                       // Split a full branch that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = stuck(), c = stuck(), l = stuck();                          // Parent which must be a branch which is not full, child at index which must be a full leaf, left and right splits of leaf
    final Layout.Field isFullButOne = isFullButOne();
    final Layout.Field isLeaf       = isLeaf();
    final Layout.Field cl           = index(), cr = index();                    // Btree indexes of child and left and right children of child
    final Layout.Field center       = p.key();                                  // The central key

    copyStuckFrom(p, parentIndex);                                              // Load parent stuck from btree
    p.pastLastElement();                                                        // Key of child
    cr.move(p.stuckData);                                                       // Reference to child in btree
    copyStuckFrom(c, p.stuckData);                                              // Load child from btree

    isLeaf(parentIndex, isLeaf);                                                // The parent stuck must be a branch
    if (isLeaf.asBoolean())
     {L.P.stopProgram("Parent must be a branch");
     }

    p.isFullButOne(isFullButOne);                                               // The parent stuck may not be full
    if (isFullButOne.asBoolean())
     {L.P.stopProgram("Parent must not be full");
     }

    isLeaf(cr, isLeaf);                                                         // The child stuck must be a leaf
    if (isLeaf.asBoolean())
     {L.P.stopProgram("Child must be a branch");
     }

    c.isFullButOne(isFullButOne);                                               // The child stuck must be a leaf
    if (!isFullButOne.asBoolean())
     {L.P.stopProgram("Child branch must be full");
     }

    c.splitLowButOne(l, (maxStuckSize-1) / 2, center);                          // Split the leaf in two down the middle copying out the lower half
    allocateBranch(cl); saveStuckInto(l, cl);                                   // Allocate and save left leaf
                        saveStuckInto(c, cr);                                   // Allocate and save left leaf
                                                                                // Update root with new children
    p.stuckKeys.move(center); p.stuckData.move(cl); p.push();                   // Add reference to left child
    p.stuckKeys.zero();       p.stuckData.move(cr); p.setPastLastElement();     // Add reference to not split top child on the right
    saveStuckInto(p, parentIndex);                                              // Save the parent stuck back into the btree
   }

  private void iSplitBranchAtTop(Layout.Field parentIndex)                      // Split a full branch that is not the root and is the last child of its parent branch which is not full
   {L.P.new Instruction()
     {void action()                                                             // Compute mid point key
       {splitBranchAtTop(parentIndex);
       }
     };
   }

//D1 Merge                                                                      // Merge two nodes

  private void iMergeLeavesIntoRoot(Layout.Field success)                       // Merge two leaves into the root
   {final Stuck p = stuck(), l = stuck(), r = stuck();                          // Root and left, right children
    final Layout.Field li  = index(), ri = index();                             // Btree indexes of left and right children of root

    L.P.new Block()
     {void code()
       {L.P.new Instruction()                                                   // Check that the root has one entry and thus two children
         {void action()
           {success.zero();                                                     // Assume failure
            copyStuckFromRoot(p);                                               // Load root
            if (p.stuckSize.value != 1) L.P.Goto(end);                          // Wrong number of entries in root
           };
         };
        L.P.new Instruction()                                                   // Check that the root has one entry and thus two children
         {void action()
           {p.stuckData.read(0); li.move(p.stuckData);                          // Index of left leaf
            p.stuckData.read(1); ri.move(p.stuckData);                          // Index of right leaf
           };
         };
        new IsLeaf(li)                                                          // Check that the children are leaves
         {void Leaf()
           {L.P.new Instruction()                                               // Check that the root has one entry and thus two children
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  leaf from btree
                copyStuckFrom(r, ri);                                           // Load right leaf from btree
                p.merge(l, r, success);                                         // Merge leaves into root
                if (success.asBoolean())                                        // Modify the root only if the merge succeeded
                 {saveStuckIntoRoot(p);                                         // Save the modified root back into the tree
                  setRootAsLeaf();                                              // Set the root to be a leaf
                  free(li); free(ri);                                           // Free left and right leaves as they are no longer needed
                 }
               }
             };
           }
         };
       }
     };
   }

  private void iMergeLeavesNotTop                                               // Merge the two consecutive leaves of a branch that is not the root. Neither of the leaves is the topmost leaf.
   (Layout.Field Parent, Layout.Field LeftLeaf, Layout.Field success)
   {final Stuck p = stuck(), l = stuck(), r  = stuck();                         // Parent, left and right children
    final Layout.Field li = index(), ri = index();                              // Btree indexes of left and right children of parent that we want to merge

    L.P.new Block()
     {void code()
       {L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {success.zero();                                                     // Assume failure
            copyStuckFrom(p, Parent);                                           // Load parent
            if (p.stuckSize.value < 2) L.P.Goto(end);
           }
         };

        L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {p.stuckData.read    (LeftLeaf); li.move(p.stuckData);               // Get the btree index of the left child leaf
            p.stuckData.readNext(LeftLeaf); ri.move(p.stuckData);               // Get the btree index of the right child leaf
           }
         };

        new IsLeaf(li)                                                          // Check that the children are leaves
         {void Leaf()
           {L.P.new Instruction()                                               // Check that the parent has a child at the specified index
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  leaf from btree
                copyStuckFrom(r, ri);                                           // Load right leaf from btree
                l.merge(r, success);                                            // Merge leaves into left child

                if (success.asBoolean())                                        // Modify the parent only if the merge succeeded
                 {p.removeElementAt(LeftLeaf);                                  // Remove the left child
                  p.stuckData.move(li); p.setDataAt(LeftLeaf);                  // Replace the right child with the left child
                  saveStuckInto(l, li);                                         // Save the modified left child back into the tree
                  saveStuckInto(p, Parent);                                     // Save the modified root back into the tree
                  free(ri);                                                     // Free right leaf as it is no longer in use
                 }
               }
             };
           }
         };
       }
     };
   }

  private void iMergeLeavesAtTop(Layout.Field Parent, Layout.Field success)     // Merge the top most two leaves of a branch that is not the root
   {final Stuck p = stuck(), l = stuck(), r  = stuck();                         // Parent, left and right children
    final Layout.Field ls = p.index(),    rs = p.index();                       // Indices in stuck of left and right children
    final Layout.Field li = index(),      ri = index();                         // Btree indexes of left and right children of parent that we want to merge

    L.P.new Block()
     {void code()
       {L.P.new Instruction()
         {void action()
           {success.zero();                                                     // Assume failure
            copyStuckFrom(p, Parent);                                           // Load parent
            L.P.GoZero(end, p.stuckSize);                                       // Stuck must have at least one entry
           }
         };

        L.P.new Instruction()
         {void action()
           {ls.move(p.stuckSize); ls.dec();                                     // Index of left leaf known to be valid as the parent contains at least one entry resulting in two children
            rs.move(p.stuckSize);                                               // Index of right leaf
            p.stuckData.read(ls); li.move(p.stuckData);                         // Get the btree index of the left child leaf
            p.stuckData.read(rs); ri.move(p.stuckData);                         // Get the btree index of the right child leaf
           }
         };

        new IsLeaf(li)                                                          // Check that the children are leaves
         {void Leaf()                                                           // Children are leaves
           {L.P.new Instruction()
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  leaf from btree
                copyStuckFrom(r, ri);                                           // Load right leaf from btree
                l.merge(r, success);                                            // Merge leaves into left child
                if (success.asBoolean())                                        // Modify the parent only if the merge succeeded
                 {p.stuckSize.dec();                                            // The left child is now topmost - we know this is ok because the parent has at elast one entry
                  saveStuckInto(l, li);                                         // Save the modified left child back into the tree
                  saveStuckInto(p, Parent);                                     // Save the modified root back into the tree
                  free(ri);                                                     // Free right leaf as it is no longer in use
                 }
               }
             };
           }
         };
       }
     };
   }

  private void iMergeBranchesIntoRoot(Layout.Field success)                     // Merge two branches into the root
   {final Stuck p = stuck(), l = stuck(),  r  = stuck();                        // Root and left, right children
    final Layout.Field li  = index(), ri = index();                             // Btree indexes of left and right children of root
    final Layout.Field k   = p.key();                                           // Splitting key

    iCopyStuckFromRoot(p);                                                      // Load root
    L.P.new Block()
     {void code()
       {L.P.new Instruction()                                                   // Check that the root has one entry and thus two children
         {void action()
           {success.zero();                                                     // Assume failure
            if (p.stuckSize.value != 1) L.P.Goto(end);
           };
         };

        L.P.new Instruction()                                                   // Check that the root has one entry and thus two children
         {void action()
           {p.stuckKeys.read(0); k .move(p.stuckKeys);                          // Splitting key
            p.stuckData.read(0); li.move(p.stuckData);                          // Index of left branch
            p.stuckData.read(1); ri.move(p.stuckData);                          // Index of right branch
           }
         };

        new IsLeaf(li)                                                          // Check that the children are leaves
         {void Branch()                                                         // Children are not leaves
           {L.P.new Instruction()
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  branch from btree
                copyStuckFrom(r, ri);                                           // Load right branch from btree
                p.mergeButOne(l, k, r, success);                                // Merge left branch, splitting key, right branch into root
                if (success.asBoolean())                                        // Modify the parent only if the merge succeeded
                 {saveStuckIntoRoot(p);                                         // Save the modified root back into the tree
                  free(li); free(ri);                                           // Free left and right leaves as they are no longer needed
                 }
               }
             };
           }
         };
       }
     };
   }

  private void iMergeBranchesNotTop
   (Layout.Field Parent, Layout.Field LeftBranch, Layout.Field success)         // Merge the two consecutive child branches of a branch that is not the root. Neither of the child branches is the topmost leaf.
   {final Stuck p = stuck(), l = stuck(), r  = stuck();                         // Parent, left and right children
    final Layout.Field li = index(), ri = index();                              // Btree indexes of left and right children of parent that we want to merge

    L.P.new Block()
     {void code()
       {L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {success.zero();                                                     // Assume failure
            copyStuckFrom(p, Parent);                                           // Load parent
            if (p.stuckSize.value < 2)
             {L.P.Goto(end);
             }
           };
         };

        L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {p.stuckData.read    (LeftBranch); li.move(p.stuckData);             // Get the btree index of the left child branch
            p.stuckData.readNext(LeftBranch); ri.move(p.stuckData);             // Get the btree index of the right child branch
           }
         };

        new IsLeaf(li)                                                          // Check that the children are branches
         {void Branch()
           {L.P.new Instruction()                                               // Check that the parent has a child at the specified index
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  branch from btree
                copyStuckFrom(r, ri);                                           // Load right branch from btree
                p.stuckKeys.read(LeftBranch);                                   // Key associated with left child branch
                l.mergeButOne(p.stuckKeys, r, success);                         // Merge branches into left child

                if (success.asBoolean())                                        // Modify the parent only if the merge succeeded
                 {p.removeElementAt(LeftBranch);                                // Remove the left child
                  p.stuckData.move(li); p.setDataAt(LeftBranch);                // Replace the right child with the left child
                  saveStuckInto(l, li);                                         // Save the modified left child back into the tree
                  saveStuckInto(p, Parent);                                     // Save the modified root back into the tree
                  free(ri);                                                     // Free right branch as it is no longer in use
                 }
               }
             };
           }
         };
       }
     };
   }

  private void iMergeBranchesAtTop(Layout.Field Parent, Layout.Field success)   // Merge the top most two child branches of a branch that is not the root
   {final Stuck p = stuck(), l = stuck(), r  = stuck();                         // Parent, left and right children
    final Layout.Field ls = p.index(),    rs = p.index();                       // Indices in stuck of left and right children
    final Layout.Field li = index(),      ri = index();                         // Btree indexes of left and right children of parent that we want to merge

    L.P.new Block()
     {void code()
       {L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {success.zero();                                                     // Assume failure
            copyStuckFrom(p, Parent);                                           // Load parent

            if (p.stuckSize.value == 0)
             {L.P.Goto(end);
             }
           };
         };

        L.P.new Instruction()                                                   // Check that the parent has a child at the specified index
         {void action()
           {ls.move(p.stuckSize); ls.dec();                                     // Index of left branch known to be valid as the parent contains at least one entry resulting in two children
            rs.move(p.stuckSize);                                               // Index of right branch
            p.stuckData.read(ls); li.move(p.stuckData);                         // Get the btree index of the left branch branch
            p.stuckData.read(rs); ri.move(p.stuckData);                         // Get the btree index of the right branch branch
           }
         };

        new IsLeaf(li)                                                          // Check that the children are branches
         {void Branch()                                                         // Children are branches
           {L.P.new Instruction()                                               // Check that the parent has a child at the specified index
             {void action()
               {copyStuckFrom(l, li);                                           // Load left  branch from btree
                copyStuckFrom(r, ri);                                           // Load right branch from btree
                p.pop();                                                        // Key associated with left child branch
                l.mergeButOne(p.stuckKeys, r, success);                         // Merge leaves into left child
                if (success.asBoolean())                                        // Modify the parent only if the merge succeeded
                 {p.stuckData.move(li);                                         // Index of left branch that now contains the combined branches
                  p.setPastLastData();                                          // Make newly combined left branch top most
                  saveStuckInto(l, li);                                         // Save the modified left child back into the tree
                  saveStuckInto(p, Parent);                                     // Save the modified root back into the tree
                  free(ri);                                                     // Free right branch as it is no longer in use
                 }
               }
             };
           }
         };
       }
     };
   }

//D1 Find                                                                       // Find a key in a btree

  class IsLeaf                                                                  // Process a stuck depending on wnether it is a leaf or a branch
   {IsLeaf(Layout.Field index)
     {stuckIsLeaf.iRead(index);
      final IsLeaf l = this;
      L.P.new If(stuckIsLeaf)
       {void Then() {l.Leaf();}
        void Else() {l.Branch();}
       };
     }
    void Leaf() {}
    void Branch() {}
   }

  public void find(Layout.Field Key, Layout.Field Found,                        // Find the leaf associated with a key in the tree
    Layout.Field Data, Layout.Field index, Layout.Field stuckIndex)
   {final Stuck        S = stuck();
    final Layout.Field s = index;
    s.iZero();                                                                  // Start at the root
    L.P.new Block()
     {void code()
       {L.P.new Instruction()
         {void action()
           {copyStuckFrom(S, s);                                                // Set search key
            S.stuckKeys.move(Key);
           }
         };

        new IsLeaf(s)
         {void Leaf()                                                           // At a leaf - search for exact match
           {L.P.new Instruction()
             {void action()
               {S.search_eq(Found, stuckIndex);                                 // Search
                L.P.GoZero(end, Found);                                         // Key not present
               }
             };
            L.P.new Instruction()
             {void action()
               {S.elementAt(stuckIndex);                                        // Look up data
                Data.move(S.stuckData);                                         // Save data
                L.P.Goto (end);                                                 // Successfully found the key
               }
             };
           }
          void Branch()                                                         // On a branch - step to next level down
           {L.P.new Instruction()
             {void action()
               {S.search_le(Found, stuckIndex);                                 // Search stuck for matching key
                s.move(S.stuckData);                                            // Index of next stuck down
                L.P.Goto(start);                                                // Key not present
               }
             };
           }
         };
       };
     };
   }

//D1 Insertion                                                                  // Insert a key, data pair into the tree if ther is room for it or update and existing key with a new datum

  private void findAndInsert(Layout.Field Found)                                // Find the leaf that should contain this key and insert or update it is possible setting Found to true if found else to false indicating that the key, data pair still needs to be inserted
   {final Stuck  S          = stuck();
    Layout.Field Key        = S.key();
    Layout.Field Data       = S.data();
    Layout.Field index      = index();
    Layout.Field stuckIndex = S.index();
    Layout.Field full       = S.full();

    L.P.new Block()
     {void code()
       {L.P.new Instruction()
         {void action()
           {Key .move(stuckKeys);
            Data.move(stuckData);
           }
         };

        find(Key, Found, Data, index, stuckIndex);                              // Find the leaf that should contain the key and possibly the key.

        L.P.new Instruction()
         {void action()
           {copyStuckFrom(S, index);                                            // Copy the stuck that should contain the key
            S.stuckKeys.move(Key);
            S.stuckData.move(Data);
            if (Found.asBoolean())                                              // Found the key in the leaf so update it with the new data
             {S.setElementAt(stuckIndex);
              saveStuckInto(S, index);
              Found.one();
              L.P.Goto(end);
             }

            S.isFull(full);                                                     // Check whether the stuck is full
            if (!full.asBoolean())
             {S.search_le(Found, stuckIndex);
              S.stuckKeys.move(Key);
              S.stuckData.move(Data);
              if (Found.asBoolean()) S.insertElementAt(stuckIndex);
              else S.push();
              saveStuckInto(S, index);
              Found.one();
             }
            else Found.zero();                                                  // The key has not been inserted
           }
         };
       }
     };
   }

  public void put()                                                             // Insert a key, data pair into the tree or update and existing key with a new datum
   {final Stuck        S          = stuck();
    final Layout.Field p          = index();                                    // Previous or parent position in the btree
    final Layout.Field s          = index();                                    // Current position in the btree
    final Layout.Field Key        = S.key();
    final Layout.Field Data       = S.data();
    final Layout.Field index      = index();
    final Layout.Field stuckIndex = S.index();
    final Layout.Field full       = S.full();
    final Layout.Field found      = S.found();
    final Layout.Field isLeaf     = isLeaf();
    final Layout.Field fullButOne = S.fullButOne();

    L.P.new Instruction()
     {void action()
       {Key.move(stuckKeys); Data.move(stuckData);                              // Save key and data to be inserted,  It is convenient for the caller to be able to use the predeclared fields but theya re overwrittne by subsequqnt activity and so they have to ba saved immediately.
       }
     };

    L.P.new Block()                                                             // The block is left as soon as possible
     {void code()
       {L.P.new Instruction()
         {void action()
           {stuckKeys.move(Key);
            stuckData.move(Data);
           }
         };

        findAndInsert(found);                                                   // Try direct insertion with no modifications to the shape of the tree
        L.P.iGoNotZero(end, found);                                             // Direct insertion succeeded

        isRootLeaf(isLeaf);                                                     // Failed to insert because the root is a leaf and must therefore be full
        L.P.new If (isLeaf)                                                     // Root is a leaf
         {void Then()
           {L.P.new Instruction()
             {void action()
               {splitRootLeaf();                                                // Split the leaf root to make room
                stuckKeys.move(Key);                                            // Key, data pair to be inserted
                stuckData.move(Data);
               }
             };

            findAndInsert(found);                                               // Splitting a leaf root will make more space in the tree
            L.P.iGoto(end);                                                     // Direct insertion succeeded
           }
         };
        iIsRootBranchFull(fullButOne);                                          // Root is a full branch so split it
        L.P.new If (fullButOne)
         {void Then()
           {L.P.new Instruction()
             {void action()
               {splitRootBranch();                                              // Split the branch root to make room
                L.P.Goto(start);                                                // Restart descent to make sure we are on the right path
               }
             };
           }
         };

        L.P.new Instruction()
         {void action()
           {s.zero(); p.zero();                                                 // Start at the root and step down through the tree to the key splitting as we go
            copyStuckFrom(S, s);                                                // Load root
           }
         };

        L.P.new Block()
         {void code()
           {L.P.new Instruction()
             {void action()
               {S.stuckKeys.move(Key);
                S.search_le(found, stuckIndex);                                 // Step down
                p.move(s);                                                      // Parent
                s.move(S.stuckData);                                            // Child
                copyStuckFrom(S, s);                                            // Load child
               }
             };

            new IsLeaf(s)                                                       // Child is a leaf or a branch
             {void Leaf()                                                       // At a leaf - search for exact match
               {L.P.new Instruction()
                 {void action()
                   {S.isFull(full);

                    if (full.asBoolean())                                       // Split leaf
                     {if (found.asBoolean())
                       {splitLeafNotTop(p, stuckIndex);                         // Split the child leaf known not to be top
                       }
                      else
                       {splitLeafAtTop(p);                                      // Split the child leaf known to be top
                       }
                     }

                    stuckKeys.move(Key);                                        // Key, data pair to be inserted
                    stuckData.move(Data);
                   }
                 };

                findAndInsert(found);                                           // Must be insertable now necuase we have split everything in the path of the key
                L.P.iGoto(end);                                                 // Successfully found the key
               }
              void Branch()                                                     // Child is a branch
               {L.P.new Instruction()
                 {void action()
                   {S.isFullButOne(fullButOne);
                    if (fullButOne.asBoolean())
                     {if (found.asBoolean())
                       {splitBranchNotTop(p, stuckIndex);                       // Split the child branch known not to be top
                       }
                      else
                       {splitBranchAtTop(p);                                    // Split the child branch known to be top
                       }
                      s.move(p);                                                // Restart at the parent so we enter the child stuck that contains the key
                      copyStuckFrom(S, s);                                      // Reload stuck so we start again at the parent level
                     }
                    L.P.Goto(start);                                            // Try again
                   }
                 };
               }
             };
           };
         };
       }
     };
    if (!supressMerge)                                                          // Switch of combined merge during testing so that the individual merges can tested
     {stuckKeys.iMove(Key);                                                     // Key being inserted or updated
      merge();                                                                  // Merge along path to key
     }
   }

  public void merge()                                                           // Merge stucks on either side of the path to the key
   {final Stuck        S          = stuck();
    final Layout.Field s          = index();                                    // Current position in the btree
    final Layout.Field Key        = S.key();
    final Layout.Field stuckIndex = S.index();
    final Layout.Field found      = S.found();
    final Layout.Field within     = within();                                   // In the body of the stuck and not at the top
    final Layout.Field isLeaf     = isLeaf();
    final Layout.Field success    = S.success();

    L.P.new Block()                                                             // The block is left as soon as possible
     {void code()
       {L.P.new Instruction()
         {void action()
           {Key.move(stuckKeys);                                                // Save path key
            s.zero();                                                           // Start at the root and step down through the tree along the path of the key merging on each side of the key as we go
           }
         };

        new IsLeaf(s)                                                           // Root is a leaf or a branch
         {void Leaf()                                                           // Root is a leaf - nothing to merge
           {L.P.iGoto(end);
           }
         };

        iMergeLeavesIntoRoot(success);                                          // Try merging leaves into root
        L.P.iGoNotZero(end,  success);                                          // A successful merge makes the root a leaf so we can return

        iMergeBranchesIntoRoot(success);                                        // Try merging branches into root

        iCopyStuckFrom(S, s);                                                   // Load root

        L.P.new Block()
         {void code()
           {iMergeLeavesAtTop  (s, success);                                    // Try merging leaves at top into parent
            iMergeBranchesAtTop(s, success);                                    // Try merging branches at top into parent
            for (int i = 0; i < maxStuckSize-1; i++)
             {final int I = i;
              L.P.new Instruction()                                             // Check we are in the body of the stuck
               {void action()
                 {stuckIndex.write(I);
                  within.value = I < S.stuckSize.value ? 1 : 0;
                 }
               };
              L.P.new If(within)                                                // Within body of stuck
               {void Then()
                 {iMergeLeavesNotTop  (s, stuckIndex, success);                 // Try merging leaves not at top into parent
                  iMergeBranchesNotTop(s, stuckIndex, success);                 // Try merging branches not at top into parent
                 }
               };
             }

            L.P.new Instruction()
             {void action()
               {S.stuckKeys.move(Key);                                          // Following the path made by this key
                S.search_le(found, stuckIndex);                                 // Step down
                s.move(S.stuckData);                                            // Child
                copyStuckFrom(S, s);                                            // Load child
               }
             };

            new IsLeaf(s)                                                       // Child is a leaf or a branch
             {void Leaf()                                                       // At a leaf - end of merging
               {L.P.iGoto(end);
               }
              void Branch()                                                     // Child is a branch - try again
               {L.P.iGoto(start);
               }
             };
           };
         };
       }
     };
   }

//D1 Deletion                                                                   // Delete a key data pair from the btree returning the data associated with the key

  private void delete(Layout.Field Data)                                        // Find the leaf that contains this key and delete it
   {final Stuck  S          = stuck();
    Layout.Field Key        = S.key();
    Layout.Field index      = index();
    Layout.Field stuckIndex = S.index();
    Layout.Field found      = found();

    L.P.new Block()
     {void code()
       {Key.iMove(stuckKeys);
        find(Key, found, Data, index, stuckIndex);                              // Find the leaf that should contain the key and possibly the key.
        iCopyStuckFrom(S, index);                                               // Copy the stuck that should contain the key
        L.P.new If (found)                                                      // Found the key in the leaf so remove it
         {void Then()
           {L.P.new Instruction()
             {void action()
               {S.removeElementAt(stuckIndex);                                  // Remove the key
                saveStuckInto(S, index);                                        // Save modified stuck back into btree
                stuckKeys.move(Key);                                            // Reload key
               }
             };
            merge();                                                            // Merge along key path
           }
         };
       }
     };
   }

//D1 Tests                                                                      // Test the btree
*/
  final static int[]random_100 = {27, 442, 545, 317, 511, 578, 391, 993, 858, 586, 472, 906, 658, 704, 882, 246, 261, 501, 354, 903, 854, 279, 526, 686, 987, 403, 401, 989, 650, 576, 436, 560, 806, 554, 422, 298, 425, 912, 503, 611, 135, 447, 344, 338, 39, 804, 976, 186, 234, 106, 667, 494, 690, 480, 288, 151, 773, 769, 260, 809, 438, 237, 516, 29, 376, 72, 946, 103, 961, 55, 358, 232, 229, 90, 155, 657, 681, 43, 907, 564, 377, 615, 612, 157, 922, 272, 490, 679, 830, 839, 437, 826, 577, 937, 884, 13, 96, 273, 1, 188};

  static void test_create1()
   {final Btree b = new Btree(1, 4, 8, 8);
    b.stuckIsLeaf .memorySet(1, 0);
    b.stuckSize   .memorySet(2, 0);
    b.stuckKeys[0].memorySet(2, 0); b.stuckData[0].memorySet(3, 0);
    b.stuckKeys[1].memorySet(4, 0); b.stuckData[1].memorySet(5, 0);

    Stuck s = b.new Stuck("Stuck");
    final Process.Register i = s.register("i", 3);
    i.registerSet(0);
    s.get(i);

    b.maxSteps = 20;
    b.chipRunJava();

    ok(s, """
Stuck: Stuck size: 2, leaf: 1
 0     2 =>    3
 1     4 =>    5
""");

    s.processClear();
    s.isLeaf .registerSet(0);
    s.size   .registerSet(1);
    s.keys[0].registerSet(3); s.data[0].registerSet(4);
    s.set();

    //stop(b.printMemory());
    ok(b.printMemory(), """
Chip: Btree            step: 14, maxSteps: 20, running: 0, returnCode: 0
  Processes:
    stuckIsLeaf           , memory: 1 * 1 = 1
    stuckIsFree           , memory: 1 * 1 = 0
    freeNext              , memory: 1 * 1 = 0
    stuckSize             , memory: 1 * 3 = 2
    stuckKeys_0           , memory: 1 * 8 = 2
    stuckData_0           , memory: 1 * 8 = 3
    stuckKeys_1           , memory: 1 * 8 = 4
    stuckData_1           , memory: 1 * 8 = 5
    stuckKeys_2           , memory: 1 * 8 = 0
    stuckData_2           , memory: 1 * 8 = 0
    stuckKeys_3           , memory: 1 * 8 = 0
    stuckData_3           , memory: 1 * 8 = 0
""");
    b.chipRunJava();
    //stop(b.printMemory());
    ok(b.printMemory(), """
Chip: Btree            step: 12, maxSteps: 20, running: 0, returnCode: 0
  Processes:
    stuckIsLeaf           , memory: 1 * 1 = 0
    stuckIsFree           , memory: 1 * 1 = 0
    freeNext              , memory: 1 * 1 = 0
    stuckSize             , memory: 1 * 3 = 1
    stuckKeys_0           , memory: 1 * 8 = 3
    stuckData_0           , memory: 1 * 8 = 4
    stuckKeys_1           , memory: 1 * 8 = 4
    stuckData_1           , memory: 1 * 8 = 5
    stuckKeys_2           , memory: 1 * 8 = 0
    stuckData_2           , memory: 1 * 8 = 0
    stuckKeys_3           , memory: 1 * 8 = 0
    stuckData_3           , memory: 1 * 8 = 0
""");
   }

  static Btree test_push()
   {final int B = 8, S = 4, K = 8, D = 8;
    final Btree b = new Btree(B, S+S, K, D);

    Stuck s = b.new Stuck("Stuck");
    final Process.Register k = s.register("k", K);
    final Process.Register d = s.register("d", D);

    s.getRoot();
    for (int i = 0; i < S; i++)
     {final int I = i;
      s.new Instruction()
       {void action()
         {k.registerSet(I); d.registerSet(I+1);
         }
       };
      s.push(k, d);
     }

    b.maxSteps = 30;
    b.chipRunJava();

    ok(s, """
Stuck: Stuck size: 4, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
""");

     return b;
   }

  static Btree test_pop()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");

    s.processClear();
    s.pop(k, d);

    b.maxSteps = 10;
    b.chipRunJava();

    ok(s, """
Stuck: Stuck size: 3, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
""");
     return b;
   }

  static void test_firstLastPast()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");

    s.processClear();
    s.firstElement(k, d);

    b.maxSteps = 10;
    b.chipRunJava();

    ok(k, "Stuck_k = 0");
    ok(d, "Stuck_d = 1");

    s.processClear();
    s.lastElement(k, d);

    b.maxSteps = 10;
    b.chipRunJava();

    ok(k, "Stuck_k = 3");
    ok(d, "Stuck_d = 4");

    s.processClear();
    s.pop(k, d);
    s.new Instruction()
     {void action()
       {k.registerSet(5); d.registerSet(55);
       }
     };
    s.setPastLastElement(k, d);

    b.maxSteps = 10;
    b.chipRunJava();

    //stop(s.dump());
    ok(s.dump(), """
Stuck: Stuck size: 3, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     5 =>   55
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
""");

    s.processClear();
    s.pastLastElement(k, d);

    b.maxSteps = 10;
    b.chipRunJava();

    ok(k, "Stuck_k = 5");
    ok(d, "Stuck_d = 55");
   }

  static void test_elementAt()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);

    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
      s.processClear();
      s.new Instruction()
       {void action()
         {i.registerSet(J);
         }
       };
      s.elementAt(i, k, d);

      b.maxSteps = 10;
      b.chipRunJava();

      ok(k, "Stuck_k = "+j);
      ok(d, "Stuck_d = "+(j+1));
     }
   }

  static void test_setElementAt()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);

    s.processClear();

    final int N = 4;
    for (int j = 0; j <= N; j++)
     {final int J = j;
      s.new Instruction()
       {void action()
         {i.registerSet(J);
          k.registerSet(J+1);
          d.registerSet((J+1)*2);
         }
       };
       s.setElementAt(i, k, d);
     }

    b.maxSteps = 100;
    b.chipRunJava();
    ok(s, """
Stuck: Stuck size: 5, leaf: 0
 0     1 =>    2
 1     2 =>    4
 2     3 =>    6
 3     4 =>    8
 4     5 =>   10
""");
   }

  static void test_setDataAt()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);

    s.processClear();

    final int N = 4;
    for (int j = 0; j <= N; j++)
     {final int J = j;
      s.new Instruction()
       {void action()
         {i.registerSet(J);
          d.registerSet((J+1)*2);
         }
       };
       s.setDataAt(i, d);
     }

    b.maxSteps = 100;
    b.chipRunJava();
    ok(s, """
Stuck: Stuck size: 4, leaf: 0
 0     0 =>    2
 1     1 =>    4
 2     2 =>    6
 3     3 =>    8
""");
   }

  static void test_setPastLastElement()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");

    s.processClear();

    s.new Instruction()
     {void action()
       {k.registerSet(5);
        d.registerSet(55);
       }
     };
    s.setPastLastElement(k, d);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s.dump());
    ok(""+s.dump(), """
Stuck: Stuck size: 4, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     5 =>   55
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
""");
   }

  static void test_insertElementAt()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);

    s.processClear();

    s.new Instruction()
     {void action()
       {i.registerSet(1);
        k.registerSet(5);
        d.registerSet(55);
       }
     };
    s.insertElementAt(i, k, d);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 5, leaf: 0
 0     0 =>    1
 1     5 =>   55
 2     1 =>    2
 3     2 =>    3
 4     3 =>    4
""");
   }

  static void test_removeElementAt()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);

    s.processClear();

    s.new Instruction()
     {void action()
       {i.registerSet(1);
       }
     };
    s.removeElementAt(i, k, d);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 3, leaf: 0
 0     0 =>    1
 1     2 =>    3
 2     3 =>    4
""");
    ok(k, "Stuck_k = 1");
    ok(d, "Stuck_d = 2");
   }

  static void test_search_eq()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    final Process.Register i = s.register("i", b.stuckAddressSize);
    final Process.Register f = s.register("f", 1);

    s.processClear();

    s.new Instruction()
     {void action()
       {k.registerSet(11);
       }
     };
    s.search_eq(k, f, i, d);

    b.maxSteps = 100;
    b.chipRunJava();
    ok(f, "Stuck_f = 0");

    final int N = 4;
    for (int j = 0; j < N; j++)
     {final int J = j;
       s.processClear();

      s.new Instruction()
       {void action()
         {k.registerSet(J);
         }
       };
      s.search_eq(k, f, i, d);

      b.maxSteps = 100;
      b.chipRunJava();
      ok(f, "Stuck_f = 1");
      ok(i, "Stuck_i = "+J);
      ok(k, "Stuck_k = "+J);
      ok(d, "Stuck_d = "+(J+1));
     }
   }

  static void test_search_le()
   {final int B = 8, S = 4, K = 8, D = 8;
    final Btree b = new Btree(B, S+S, K, D);

    Stuck s = b.new Stuck("Stuck");
    final Process.Register k = s.register("k", K);
    final Process.Register l = s.register("l", K);
    final Process.Register d = s.register("d", D);
    final Process.Register i = s.register("i", b.stuckAddressSize);
    final Process.Register f = s.register("f", 1);

    s.getRoot();
    for (int j = 0; j < S; j++)
     {final int J = j*10;
      s.new Instruction()
       {void action()
         {k.registerSet(J); d.registerSet(J+1);
         }
       };
      s.push(k, d);
     }

    b.maxSteps = 30;
    b.chipRunJava();
    ok(s, """
Stuck: Stuck size: 4, leaf: 0
 0     0 =>    1
 1    10 =>   11
 2    20 =>   21
 3    30 =>   31
""");
    s.processClear();

    s.new Instruction()
     {void action()
       {k.registerSet(11);
       }
     };
    s.search_le(k, f, i, l, d);

    b.maxSteps = 100;
    b.chipRunJava();

    ok(k, "Stuck_k = 11");
    ok(f, "Stuck_f = 1");
    ok(i, "Stuck_i = 2");
    ok(l, "Stuck_l = 20");
    ok(d, "Stuck_d = 21");
   }

  static void test_splitIntoTwo()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    Stuck l = b.new Stuck("Left");
    Stuck r = b.new Stuck("Right");

    s.processClear();

    s.splitIntoTwo(l, r, 2);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 4, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
""");
    //stop(l);
    ok(l, """
Stuck: Left size: 2, leaf: 0
 0     0 =>    1
 1     1 =>    2
""");
    //stop(r);
    ok(r, """
Stuck: Right size: 2, leaf: 0
 0     2 =>    3
 1     3 =>    4
""");
   }

  static void test_splitIntoThree()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    Stuck l = b.new Stuck("Left");
    Stuck r = b.new Stuck("Right");

    s.processClear();
    s.new Instruction()
     {void action()
       {s.size.dec();
       }
     };

    s.splitIntoThree(l, r, 1);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 3, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
""");
    //stop(l.dump());
    ok(l.dump(), """
Stuck: Left size: 1, leaf: 0
 0     0 =>    1
 1     0 =>    2
 2     0 =>    0
 3     0 =>    0
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
""");
    //stop(r.dump());
    ok(r.dump(), """
Stuck: Right size: 2, leaf: 0
 0     2 =>    3
 1     0 =>    4
 2     0 =>    0
 3     0 =>    0
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
""");
   }

  static void test_splitLow()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    Stuck l = b.new Stuck("Left");

    s.processClear();
    s.new Instruction()
     {void action()
       {s.size.zero();
       }
     };

    for (int i = 0; i < b.maxStuckSize; i++)
     {final int I = i;
      s.new Instruction()
       {void action()
         {k.registerSet(I); d.registerSet(I+1);
         }
       };
      s.push(k, d);
     }

    s.splitLow(l);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 4, leaf: 0
 0     4 =>    5
 1     5 =>    6
 2     6 =>    7
 3     7 =>    8
""");
    //stop(l);
    ok(l, """
Stuck: Left size: 4, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
""");
   }

  static void test_splitLowButOne()
   {final Btree b = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    final Process.Register k = s.registers.get("k");
    final Process.Register d = s.registers.get("d");
    Stuck l = b.new Stuck("Left");
    Stuck r = b.new Stuck("Right");

    s.processClear();
    s.new Instruction()
     {void action()
       {s.size.zero();
       }
     };

    for (int i = 0; i < b.maxStuckSize-1; i++)
     {final int I = i;
      s.new Instruction()
       {void action()
         {k.registerSet(I); d.registerSet(I+1);
         }
       };
      s.push(k, d);
     }

    s.splitLowButOne(l, k);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 3, leaf: 0
 0     4 =>    5
 1     5 =>    6
 2     6 =>    7
""");

    //stop(l.dump());
    ok(l.dump(), """
Stuck: Left size: 3, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     0 =>    4
 4     0 =>    0
 5     0 =>    0
 6     0 =>    0
 7     0 =>    0
""");

    ok(k, "Stuck_k = 3");
   }

  static void test_merge()
   {final Btree b = test_push();
    final Btree B = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    Stuck S = (Stuck)B.processes.get("Stuck");
    final Process.Register o = s.register("o", 1);

    s.processClear();
    s.merge(S, o);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 8, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     3 =>    4
""");

    ok(o, "Stuck_o = 1");
   }

  static void test_merge2()
   {final Btree b = test_push();
    final Btree L = test_push();
    final Btree R = test_push();

    Stuck s = (Stuck)b.processes.get("Stuck");
    Stuck l = (Stuck)L.processes.get("Stuck");
    Stuck r = (Stuck)R.processes.get("Stuck");
    final Process.Register o = s.register("o", 1);

    s.processClear();
    s.merge(l, r, o);

    b.maxSteps = 100;
    b.chipRunJava();
    //stop(s);
    ok(s, """
Stuck: Stuck size: 8, leaf: 0
 0     0 =>    1
 1     1 =>    2
 2     2 =>    3
 3     3 =>    4
 4     0 =>    1
 5     1 =>    2
 6     2 =>    3
 7     3 =>    4
""");

    ok(o, "Stuck_o = 1");
   }

/*
  static Btree test_create()
   {final Btree b = new Btree(32, 4, 8, 8);
    ok(b.dump(), """
Btree
Stuck:  0   size: 0   free: 0   next:  0  leaf: 1
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
""");
    return b;
   }

  static void test_leaf()
   {final Btree b = test_create();
    final Layout.Field index = b.index();
    final Layout.Field a     = b.variable("a", 4);

    index.iWrite(0);
    b.new IsLeaf(index)
     {void Leaf  () {a.iWrite(1);}
      void Branch() {a.iWrite(2);}
     };
    b.runProgram();
    ok(a, "a: value=1");
   }

  static void test_allocFree()
   {final Btree b = test_create();
    final Layout.Field x = b.index();
    final Layout.Field y = b.index();

    ok(b.freeStart, "freeStart: value=1");
    ok(b.freeNext,  "freeNext: value=0, 0=0, 1=2, 2=3, 3=4, 4=5, 5=6, 6=7, 7=8, 8=9, 9=10, 10=11, 11=12, 12=13, 13=14, 14=15, 15=16, 16=17, 17=18, 18=19, 19=20, 20=21, 21=22, 22=23, 23=24, 24=25, 25=26, 26=27, 27=28, 28=29, 29=30, 30=31, 31=0");
    ok(b.dump(), """
Btree
Stuck:  0   size: 0   free: 0   next:  0  leaf: 1
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
""");

    b.iAllocate(x, false);
    b.iAllocate(y, false);
    b.runProgram();

    ok(x, "index: value=1");
    ok(y, "index: value=2");

    ok(b.freeStart, "freeStart: value=3");
    ok(b.freeNext,  "freeNext: value=0, 0=0, 1=0, 2=0, 3=4, 4=5, 5=6, 6=7, 7=8, 8=9, 9=10, 10=11, 11=12, 12=13, 13=14, 14=15, 15=16, 16=17, 17=18, 18=19, 19=20, 20=21, 21=22, 22=23, 23=24, 24=25, 25=26, 26=27, 27=28, 28=29, 29=30, 30=31, 31=0");

    ok(b.dump(), """
Btree
Stuck:  0   size: 0   free: 0   next:  0  leaf: 1
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
Stuck:  1   size: 0   free: 0   next:  0  leaf: 0
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
Stuck:  2   size: 0   free: 0   next:  0  leaf: 0
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
""");

    b.clearProgram();
    b.iFree(x);
    b.iFree(y);
    b.runProgram();
    ok(b.freeStart, "freeStart: value=2");
    ok(b.freeNext,  "freeNext: value=1, 0=0, 1=3, 2=1, 3=4, 4=5, 5=6, 6=7, 7=8, 8=9, 9=10, 10=11, 11=12, 12=13, 13=14, 14=15, 15=16, 16=17, 17=18, 18=19, 19=20, 20=21, 21=22, 22=23, 23=24, 24=25, 25=26, 26=27, 27=28, 28=29, 29=30, 30=31, 31=0");
    ok(b.dump(), """
Btree
Stuck:  0   size: 0   free: 0   next:  0  leaf: 1
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
""");
   }

  static Btree test_btree()
   {final Btree b = test_create();
    final Layout.Field s = b.index();
    final Layout.Field t = b.index();
    final Layout.Field x = b.index();
    final Layout.Field y = b.index();

    final Stuck S = b.stuck();
    final Stuck T = b.stuck();
    final Stuck X = b.stuck();
    final Stuck Y = b.stuck();
    final Stuck Z = b.stuck();
    b.iAllocate(s, true);
    b.iAllocate(t, true);
    b.iAllocate(x, true);
    b.iAllocate(y, true);
    b.runProgram();

    b.clearProgram();
    S.stuckKeys.iWrite( 1); S.stuckData.iWrite( 2); S.iPush();
    S.stuckKeys.iWrite( 2); S.stuckData.iWrite( 4); S.iPush();
    S.stuckKeys.iWrite( 3); S.stuckData.iWrite( 6); S.iPush();
    S.stuckKeys.iWrite( 4); S.stuckData.iWrite( 8); S.iPush();
    b.runProgram();

    b.clearProgram();
    T.stuckKeys.iWrite(11); T.stuckData.iWrite(12); T.iPush();
    T.stuckKeys.iWrite(12); T.stuckData.iWrite(14); T.iPush();
    T.stuckKeys.iWrite(13); T.stuckData.iWrite(16); T.iPush();
    T.stuckKeys.iWrite(14); T.stuckData.iWrite(18); T.iPush();
    b.runProgram();

    b.clearProgram();
    X.stuckKeys.iWrite(21); X.stuckData.iWrite(22); X.iPush();
    X.stuckKeys.iWrite(22); X.stuckData.iWrite(24); X.iPush();
    X.stuckKeys.iWrite(23); X.stuckData.iWrite(26); X.iPush();
    X.stuckKeys.iWrite(24); X.stuckData.iWrite(28); X.iPush();
    b.runProgram();

    b.clearProgram();
    Y.stuckKeys.iWrite(31); Y.stuckData.iWrite(32); Y.iPush();
    Y.stuckKeys.iWrite(32); Y.stuckData.iWrite(34); Y.iPush();
    Y.stuckKeys.iWrite(33); Y.stuckData.iWrite(36); Y.iPush();
    Y.stuckKeys.iWrite(34); Y.stuckData.iWrite(38); Y.iPush();
    b.runProgram();

    b.clearProgram();
    Z.stuckKeys.iWrite(10); Z.stuckData.iWrite(s.value); Z.iPush();
    Z.stuckKeys.iWrite(20); Z.stuckData.iWrite(t.value); Z.iPush();
    Z.stuckKeys.iWrite(30); Z.stuckData.iWrite(x.value); Z.iPush();
    Z.stuckKeys.iWrite(0);  Z.stuckData.iWrite(y.value); Z.iSetPastLastElement();
    b.runProgram();

    b.clearProgram();
    b.iSaveStuckInto(S, s);  b.iSetLeaf(s);
    b.iSaveStuckInto(T, t);  b.iSetLeaf(t);
    b.iSaveStuckInto(X, x);  b.iSetLeaf(x);
    b.iSaveStuckInto(Y, y);  b.iSetLeaf(y);
    b.iSaveStuckIntoRoot(Z); b.iSetRootAsBranch();
    b.runProgram();
  //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=4, 0=1, 1=2, 2=3, 3=4
Stuck:  1   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=4, 0=1, 1=2, 2=3, 3=4
stuckData: value=8, 0=2, 1=4, 2=6, 3=8
Stuck:  2   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=14, 0=11, 1=12, 2=13, 3=14
stuckData: value=18, 0=12, 1=14, 2=16, 3=18
Stuck:  3   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=24, 0=21, 1=22, 2=23, 3=24
stuckData: value=28, 0=22, 1=24, 2=26, 3=28
Stuck:  4   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=34, 0=31, 1=32, 2=33, 3=34
stuckData: value=38, 0=32, 1=34, 2=36, 3=38
""");
    return b;
   }

  static void test_find()
   {final Btree b = test_btree();
    final Stuck s = b.stuck();
    final Layout.Field Key   = s.key();
    final Layout.Field Data  = s.data();
    final Layout.Field Found = s.found();
    final Layout.Field stuckIndex = s.index();
    final Layout.Field index = b.index();

    b.L.P.maxSteps = 500;

    b.clearProgram();
    Key.iWrite(5);
    b.find(Key, Found, Data, index, stuckIndex);
    b.runProgram();

    //stop(Found);
    ok(Found, "found: value=0");

    b.clearProgram();
    Key.iWrite(4);
    b.find(Key, Found, Data, index, stuckIndex);
    b.runProgram();

    //stop(Found, Data, index, stuckIndex);
    ok(Found, "found: value=1");
    ok(Data,  "data: value=8");
    ok(index, "index: value=1");
    ok(stuckIndex, "stuckIndex: value=3");

    b.clearProgram();
    Key.iWrite(14);
    b.find(Key, Found, Data, index, stuckIndex);
    b.runProgram();

    //stop(Found, Data, index, stuckIndex);
    ok(Found, "found: value=1");
    ok(Data,  "data: value=18");
    ok(index, "index: value=2");
    ok(stuckIndex, "stuckIndex: value=3");

    b.clearProgram();
    Key.iWrite(23);
    b.find(Key, Found, Data, index, stuckIndex);
    b.runProgram();

    //stop(Found, Data, index, stuckIndex);
    ok(Found, "found: value=1");
    ok(Data,  "data: value=26");
    ok(index, "index: value=3");
    ok(stuckIndex, "stuckIndex: value=2");

    b.clearProgram();
    Key.iWrite(32);
    b.find(Key, Found, Data, index, stuckIndex);
    b.runProgram();

    //stop(Found, Data, index, stuckIndex);
    ok(Found, "found: value=1");
    ok(Data,  "data: value=34");
    ok(index, "index: value=4");
    ok(stuckIndex, "stuckIndex: value=1");
   }

  static Btree test_findAndInsert()
   {final Btree b = test_create();
    final Layout.Field Found = b.found();

    b.L.P.maxSteps = 500;

    b.clearProgram();
    b.stuckKeys.iWrite(20);
    b.stuckData.iWrite(21);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 1   free: 0   next:  0  leaf: 1
stuckSize: value=1
stuckKeys: value=20, 0=20, 1=0, 2=0, 3=0
stuckData: value=21, 0=21, 1=0, 2=0, 3=0
""");

    b.clearProgram();
    b.stuckKeys.iWrite(10);
    b.stuckData.iWrite(1);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=0, 3=0
stuckData: value=21, 0=1, 1=21, 2=0, 3=0
""");

    b.clearProgram();
    b.stuckKeys.iWrite(30);
    b.stuckData.iWrite(31);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 1
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=31, 0=1, 1=21, 2=31, 3=0
""");

    Layout.Field index = b.index();
    b.clearProgram();
    b.iAllocateLeaf(index);
    b.iSetRootAsBranch();
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=21, 2=31, 3=0
Stuck:  1   size: 0   free: 0   next:  0  leaf: 1
stuckSize: value=0
stuckKeys: value=0, 0=0, 1=0, 2=0, 3=0
stuckData: value=0, 0=0, 1=0, 2=0, 3=0
""");

    b.clearProgram();
    b.stuckKeys.iWrite(4);
    b.stuckData.iWrite(5);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=21, 2=31, 3=0
Stuck:  1   size: 1   free: 0   next:  0  leaf: 1
stuckSize: value=1
stuckKeys: value=4, 0=4, 1=0, 2=0, 3=0
stuckData: value=5, 0=5, 1=0, 2=0, 3=0
""");

    b.clearProgram();
    b.stuckKeys.iWrite(5);
    b.stuckData.iWrite(6);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=21, 2=31, 3=0
Stuck:  1   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=5, 0=4, 1=5, 2=0, 3=0
stuckData: value=6, 0=5, 1=6, 2=0, 3=0
""");

    b.clearProgram();
    b.stuckKeys.iWrite(3);
    b.stuckData.iWrite(4);
    b.findAndInsert(Found);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=21, 2=31, 3=0
Stuck:  1   size: 3   free: 0   next:  0  leaf: 1
stuckSize: value=3
stuckKeys: value=5, 0=3, 1=4, 2=5, 3=0
stuckData: value=6, 0=4, 1=5, 2=6, 3=0
""");
    return b;
   }

  static void test_isLeaf()
   {final Btree b = test_btree();
    final Layout.Field isLeaf     = b.isLeaf();
    final Layout.Field index = b.index();

    b.clearProgram();
    b.isRootLeaf(isLeaf);
    b.runProgram();
    ok(isLeaf, "isLeaf: value=0");

    b.clearProgram();
    index.iZero();
    b.iIsLeaf(index, isLeaf);
    b.runProgram();
    ok(isLeaf, "isLeaf: value=0");

    b.clearProgram();
    index.iOne();
    b.iIsLeaf(index, isLeaf);
    b.runProgram();
    ok(isLeaf, "isLeaf: value=1");
   }

  static void test_splitLeafRoot()
   {final Btree b = test_create();
    final Layout.Field Found = b.found();

    b.L.P.maxSteps = 500;

    b.clearProgram(); b.stuckKeys.iWrite(10); b.stuckData.iWrite(11); b.findAndInsert(Found); b.runProgram();
    b.clearProgram(); b.stuckKeys.iWrite(20); b.stuckData.iWrite(21); b.findAndInsert(Found); b.runProgram();
    b.clearProgram(); b.stuckKeys.iWrite(30); b.stuckData.iWrite(31); b.findAndInsert(Found); b.runProgram();
    b.clearProgram(); b.stuckKeys.iWrite(40); b.stuckData.iWrite(41); b.findAndInsert(Found); b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=40, 0=10, 1=20, 2=30, 3=40
stuckData: value=41, 0=11, 1=21, 2=31, 3=41
""");

    b.clearProgram();
    b.iSplitRootLeaf();
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=25, 0=25, 1=25, 2=30, 3=40
stuckData: value=2, 0=1, 1=2, 2=31, 3=41
Stuck:  1   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=0, 3=0
stuckData: value=21, 0=11, 1=21, 2=0, 3=0
Stuck:  2   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=40, 0=30, 1=40, 2=0, 3=0
stuckData: value=41, 0=31, 1=41, 2=0, 3=0
""");
   }

  static void test_splitBranchRoot()
   {final Btree b = test_findAndInsert();

    b.L.P.maxSteps = 500;
//stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=30, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=21, 2=31, 3=0
Stuck:  1   size: 3   free: 0   next:  0  leaf: 1
stuckSize: value=3
stuckKeys: value=5, 0=3, 1=4, 2=5, 3=0
stuckData: value=6, 0=4, 1=5, 2=6, 3=0
""");

    b.clearProgram();
    b.iSplitRootBranch();
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=20, 0=20, 1=20, 2=30, 3=0
stuckData: value=3, 0=2, 1=3, 2=31, 3=0
Stuck:  1   size: 3   free: 0   next:  0  leaf: 1
stuckSize: value=3
stuckKeys: value=5, 0=3, 1=4, 2=5, 3=0
stuckData: value=6, 0=4, 1=5, 2=6, 3=0
Stuck:  2   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=10, 0=10, 1=0, 2=0, 3=0
stuckData: value=21, 0=1, 1=21, 2=0, 3=0
Stuck:  3   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=30, 0=30, 1=0, 2=0, 3=0
stuckData: value=0, 0=31, 1=0, 2=0, 3=0
""");
   }

  static void test_splitLeafNotTop()
   {final Btree b = test_create();
    final Stuck r = b.stuck();
    final Stuck l = b.stuck();
    final Layout.Field Found = b.found();
    final Layout.Field R     = b.index();
    final Layout.Field I     = r.index();
    final Layout.Field L     = b.index();

    b.L.P.maxSteps = 500;

    b.clearProgram(); r.stuckKeys.iWrite(10); r.stuckData.iWrite(1); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(20); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(30); r.stuckData.iWrite(0); r.iSetPastLastElement(); b.runProgram();
    b.clearProgram(); b.iSaveStuckIntoRoot(r);                                                 b.runProgram();

    b.clearProgram(); l.stuckKeys.iWrite(1); l.stuckData.iWrite(1); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(2); l.stuckData.iWrite(2); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(3); l.stuckData.iWrite(3); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(4); l.stuckData.iWrite(4); l.iPush(); b.runProgram();

    b.clearProgram();
    b.iSaveStuckIntoRoot(r);                     b.iSetRootAsBranch();
    b.iAllocateLeaf(L); b.iSaveStuckInto(l, L);  b.iSetLeaf(L);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 2   free: 0   next:  0  leaf: 0
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=0, 2=0, 3=0
Stuck:  1   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=4, 0=1, 1=2, 2=3, 3=4
stuckData: value=4, 0=1, 1=2, 2=3, 3=4
""");

    b.clearProgram();
    R.iZero();
    I.iZero();
    b.iSplitLeafNotTop(R, I);
    b.runProgram();
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=20, 0=2, 1=10, 2=20, 3=30
stuckData: value=0, 0=2, 1=1, 2=0, 3=0
Stuck:  1   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=4, 0=3, 1=4, 2=3, 3=4
stuckData: value=4, 0=3, 1=4, 2=3, 3=4
Stuck:  2   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=2, 0=1, 1=2, 2=0, 3=0
stuckData: value=2, 0=1, 1=2, 2=0, 3=0
""");
   }

  static void test_splitLeafAtTop()
   {final Btree b = test_create();
    final Stuck r = b.stuck();
    final Stuck l = b.stuck();
    final Layout.Field Found = b.found();
    final Layout.Field R     = b.index();
    final Layout.Field L     = b.index();

    b.L.P.maxSteps = 500;

    b.clearProgram(); r.stuckKeys.iWrite(10); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(20); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(30); r.stuckData.iWrite(1); r.iSetPastLastElement(); b.runProgram();
    b.clearProgram(); b.iSaveStuckIntoRoot(r);                                                 b.runProgram();

    b.clearProgram(); l.stuckKeys.iWrite(1); l.stuckData.iWrite(1); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(2); l.stuckData.iWrite(2); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(3); l.stuckData.iWrite(3); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(4); l.stuckData.iWrite(4); l.iPush(); b.runProgram();

    b.clearProgram();
    b.iSaveStuckIntoRoot(r);                       b.iSetRootAsBranch();
    b.iAllocateBranch(L); b.iSaveStuckInto(l, L);  b.iSetLeaf(L);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 2   free: 0   next:  0  leaf: 0
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=30, 3=0
stuckData: value=1, 0=0, 1=0, 2=1, 3=0
Stuck:  1   size: 4   free: 0   next:  0  leaf: 1
stuckSize: value=4
stuckKeys: value=4, 0=1, 1=2, 2=3, 3=4
stuckData: value=4, 0=1, 1=2, 2=3, 3=4
""");

    b.clearProgram();
    R.iZero();
    b.iSplitLeafAtTop(R);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=2, 0=10, 1=20, 2=2, 3=0
stuckData: value=1, 0=0, 1=0, 2=2, 3=1
Stuck:  1   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=4, 0=3, 1=4, 2=3, 3=4
stuckData: value=4, 0=3, 1=4, 2=3, 3=4
Stuck:  2   size: 2   free: 0   next:  0  leaf: 1
stuckSize: value=2
stuckKeys: value=2, 0=1, 1=2, 2=0, 3=0
stuckData: value=2, 0=1, 1=2, 2=0, 3=0
""");
   }

  static void test_splitBranchNotTop()
   {final Btree b = test_create();
    final Stuck r = b.stuck();
    final Stuck l = b.stuck();
    final Layout.Field Found = b.found();
    final Layout.Field R     = b.index();
    final Layout.Field I     = r.index();
    final Layout.Field L     = b.index();

    b.L.P.maxSteps = 500;

    b.clearProgram(); r.stuckKeys.iWrite(10); r.stuckData.iWrite(1); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(20); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(30); r.stuckData.iWrite(0); r.iSetPastLastElement(); b.runProgram();
    b.clearProgram(); b.iSaveStuckIntoRoot(r);                                                b.runProgram();

    b.clearProgram(); l.stuckKeys.iWrite(1); l.stuckData.iWrite(2); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(2); l.stuckData.iWrite(3); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(3); l.stuckData.iWrite(4); l.iPush(); b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(4); l.stuckData.iWrite(5); l.iSetPastLastElement(); b.runProgram();

    b.clearProgram();
    b.iSaveStuckIntoRoot(r);                      b.iSetRootAsBranch();
    b.iAllocateBranch(L); b.iSaveStuckInto(l, L); b.iSetBranch(L);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 2   free: 0   next:  0  leaf: 0
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=30, 3=0
stuckData: value=0, 0=1, 1=0, 2=0, 3=0
Stuck:  1   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=3, 0=1, 1=2, 2=3, 3=4
stuckData: value=5, 0=2, 1=3, 2=4, 3=5
""");

    b.clearProgram();
    L.iZero();
    I.iZero();
    b.iSplitBranchNotTop(L, I);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=20, 0=2, 1=10, 2=20, 3=30
stuckData: value=0, 0=2, 1=1, 2=0, 3=0
Stuck:  1   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=3, 0=3, 1=2, 2=3, 3=4
stuckData: value=5, 0=4, 1=5, 2=4, 3=5
Stuck:  2   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=1, 0=1, 1=0, 2=0, 3=0
stuckData: value=3, 0=2, 1=3, 2=0, 3=0
""");
   }

  static void test_splitBranchAtTop()
   {final Btree b = test_create();
    final Stuck r = b.stuck();
    final Stuck l = b.stuck();
    final Layout.Field Found = b.found();
    final Layout.Field R     = b.index();
    final Layout.Field L     = b.index();

    b.L.P.maxSteps = 500;

    b.clearProgram(); r.stuckKeys.iWrite(10); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(20); r.stuckData.iWrite(0); r.iPush();               b.runProgram();
    b.clearProgram(); r.stuckKeys.iWrite(30); r.stuckData.iWrite(1); r.iSetPastLastElement(); b.runProgram();
    b.clearProgram(); b.iSaveStuckIntoRoot(r);                                                 b.runProgram();

    b.clearProgram(); l.stuckKeys.iWrite(1); l.stuckData.iWrite(1); l.iPush();                b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(2); l.stuckData.iWrite(2); l.iPush();                b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(3); l.stuckData.iWrite(3); l.iPush();                b.runProgram();
    b.clearProgram(); l.stuckKeys.iWrite(4); l.stuckData.iWrite(4); l.iSetPastLastElement();  b.runProgram();

    b.clearProgram();
    b.iSaveStuckIntoRoot(r);                       b.iSetRootAsBranch();
    b.iAllocateBranch(L); b.iSaveStuckInto(l, L);  b.iSetBranch(L);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 2   free: 0   next:  0  leaf: 0
stuckSize: value=2
stuckKeys: value=20, 0=10, 1=20, 2=30, 3=0
stuckData: value=1, 0=0, 1=0, 2=1, 3=0
Stuck:  1   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=3, 0=1, 1=2, 2=3, 3=4
stuckData: value=4, 0=1, 1=2, 2=3, 3=4
""");

    b.clearProgram();
    R.iZero();
    b.iSplitBranchAtTop(R);
    b.runProgram();
    //stop(b.dump());
    ok(b.dump(), """
Btree
Stuck:  0   size: 3   free: 0   next:  0  leaf: 0
stuckSize: value=3
stuckKeys: value=2, 0=10, 1=20, 2=2, 3=0
stuckData: value=1, 0=0, 1=0, 2=2, 3=1
Stuck:  1   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=3, 0=3, 1=2, 2=3, 3=4
stuckData: value=4, 0=3, 1=4, 2=3, 3=4
Stuck:  2   size: 1   free: 0   next:  0  leaf: 0
stuckSize: value=1
stuckKeys: value=1, 0=1, 1=0, 2=0, 3=0
stuckData: value=2, 0=1, 1=2, 2=0, 3=0
""");
   }

  static void test_isRootLeafFull()
   {final Btree b = test_create();
    final Layout.Field f = b.leafFull();
    final Layout.Field F = b.branchFull();

    b.clearProgram(); b.stuckKeys.iWrite(10); b.stuckData.iWrite(11); b.findAndInsert(f); b.isRootLeafFull(f); b.iIsRootBranchFull(F); b.runProgram(); ok(f, "leafFull: value=0"); ok(F, "branchFull: value=0");
    b.clearProgram(); b.stuckKeys.iWrite(20); b.stuckData.iWrite(21); b.findAndInsert(f); b.isRootLeafFull(f); b.iIsRootBranchFull(F); b.runProgram(); ok(f, "leafFull: value=0"); ok(F, "branchFull: value=0");
    b.clearProgram(); b.stuckKeys.iWrite(30); b.stuckData.iWrite(31); b.findAndInsert(f); b.isRootLeafFull(f); b.iIsRootBranchFull(F); b.runProgram(); ok(f, "leafFull: value=0"); ok(F, "branchFull: value=1");
    b.clearProgram(); b.stuckKeys.iWrite(40); b.stuckData.iWrite(41); b.findAndInsert(f); b.isRootLeafFull(f); b.iIsRootBranchFull(F); b.runProgram(); ok(f, "leafFull: value=1"); ok(F, "branchFull: value=1");
   }

  static void test_put()
   {final Btree b = test_create();

    b.L.P.maxSteps = 2000;

    final int N = 32;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
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

  static void test_putReverse()
   {final Btree b = test_create();

    b.L.P.maxSteps = 2000;

    final int N = 32;
    for (int i = N; i > 0; i--)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
                                                           16                                                                |
                                                           0                                                                 |
                                                           11                                                                |
                                                           6                                                                 |
           4            8                12                                20              24               28               |
           11           11.1             11.2                              6               6.1              6.2              |
           12           10               8                                 4               3                2                |
                                         7                                                                  1                |
1,2,3,4=12   5,6,7,8=10     9,10,11,12=8     13,14,15,16=7   17,18,19,20=4   21,22,23,24=3    25,26,27,28=2    29,30,31,32=1 |
""");
   }

  static void test_putRandom()
   {final Btree b = new Btree(64, 4, 16, 16);

    b.L.P.maxSteps = 8000;

    final int N = 32;
    for (int i = 0; i < random_100.length; ++i)
     {b.clearProgram();
      b.stuckKeys.iWrite(random_100[i]);
      b.stuckData.iWrite(i);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
                                                                                                                                                                                                                        379                                                                                                                    528                                                                                                                                                                                                                                                                  |
                                                                                                                                                                                                                        0                                                                                                                      0.1                                                                                                                                                                                                                                                                  |
                                                                                                                                                                                                                        48                                                                                                                     34                                                                                                                                                                                                                                                                   |
                                                                                                                                                                                                                                                                                                                                               39                                                                                                                                                                                                                                                                   |
                                                               143                                                              253                                                     341                                                             429                                                   497                                                                     582                                                                          718                                                                      894                                                                     |
                                                               48                                                               48.1                                                    48.2                                                            34                                                    34.1                                                                    39                                                                           39.1                                                                     39.2                                                                    |
                                                               36                                                               25                                                      46                                                              14                                                    41                                                                      21                                                                           11                                                                       3                                                                       |
                                                                                                                                                                                        10                                                                                                                    5                                                                                                                                                                                                                             6                                                                       |
              34               87               104                          156               210                235                          266               283                                   356                            402                                 440          457                                   507                                     568                                    630                   672                688                              805           819                   856                                  909                   946               988          |
              36               36.1             36.2                         25                25.1               25.2                         46                46.1                                  10                             14                                  41           41.1                                  5                                       21                                     11                    11.1               11.2                             3             3.1                   3.2                                  6                     6.1               6.2          |
              37               24               45                           49                26                 38                           47                30                                    17                             15                                  44           8                                     33                                      18                                     27                    13                 42                               29            43                    32                                   40                    12                35           |
                                                16                                                                9                                              19                                    1                              20                                               28                                    22                                      7                                                                               4                                                                    23                                                                           2            |
1,13,27,29=37   39,43,55,72=24     90,96,103=45     106,135=16    151,155=49    157,186,188=26     229,232,234=38     237,246=9     260,261=47    272,273,279=30     288,298,317,338=19     344,354=17    358,376,377=1    391,401=15    403,422,425=20    436,437,438=44    442,447=8     472,480,490,494=28     501,503=33    511,516,526=22    545,554,560,564=18    576,577,578=7    586,611,612,615=27    650,657,658,667=13     679,681,686=42     690,704=4     769,773,804=29    806,809=43    826,830,839,854=32    858,882,884=23     903,906,907=40    912,922,937,946=12    961,976,987=35    989,993=2 |
""");
   }

  static void test_mergeLeavesIntoRoot()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;

    final int N = 6;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    index.value = 2;
    b.clearProgram();
    b.iCopyStuckFrom(s, index);
    s.iPop(); s.iPop();
    b.iSaveStuckInto(s, index);
    b.runProgram();
    //stop(b);
    ok(b, """
      2      |
      0      |
      1      |
      2      |
1,2=1  3,4=2 |
""");
    b.clearProgram();
    b.iMergeLeavesIntoRoot(success);
    b.runProgram();
    //stop(b);
    ok(b, """
1,2,3,4=0 |
""");
   }

  static void test_mergeLeavesNotTop()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field stuckIndex = s.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;
    b.supressMerge = true;

    final int N = 10;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
      2      4        6             |
      0      0.1      0.2           |
      1      3        4             |
                      2             |
1,2=1  3,4=3    5,6=4    7,8,9,10=2 |
""");
    index.value = 0;
    stuckIndex.value = 0;
    b.clearProgram();
    b.iMergeLeavesNotTop(index, stuckIndex, success);
    b.runProgram();
    ok(success, "success: value=1");
    //stop(b);
    ok(b, """
          4      6             |
          0      0.1           |
          1      4             |
                 2             |
1,2,3,4=1  5,6=4    7,8,9,10=2 |
""");
   }

  static void test_mergeLeavesAtTop()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;
    b.supressMerge = true;

    final int N = 6;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    index.value = 2;
    b.clearProgram();
    b.iCopyStuckFrom(s, index);
    s.iPop(); s.iPop();
    b.iSaveStuckInto(s, index);
    b.runProgram();
    //stop(b);
    ok(b, """
      2      |
      0      |
      1      |
      2      |
1,2=1  3,4=2 |
""");
    index.value = 0;
    b.clearProgram();
    b.iMergeLeavesAtTop(index, success);
    b.runProgram();
    ok(success, "success: value=1");
    //stop(b);
    ok(b, """
0Empty          |
1               |
      1,2,3,4=1 |
""");
   }

  static void test_mergeBranchesIntoRoot()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;
    b.supressMerge = true;

    final int N = 11;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    index.value = 6;
    b.clearProgram();
    b.iCopyStuckFrom(s, index);
    s.iPop();
    b.iSaveStuckInto(s, index);
    b.runProgram();
    //stop(b);
    ok(b, """
             4             |
             0             |
             5             |
             6             |
      2             6      |
      5             6      |
      1             4      |
      3             7      |
1,2=1  3,4=3  5,6=4  7,8=7 |
""");
    b.clearProgram();
    b.iMergeBranchesIntoRoot(success);
    b.runProgram();
    ok(success, "success: value=1");
    //stop(b);
    ok(b, """
      2      4        6        |
      0      0.1      0.2      |
      1      3        4        |
                      7        |
1,2=1  3,4=3    5,6=4    7,8=7 |
""");
   }

  static void test_mergeBranchesNotTop()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field stuckIndex = s.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;
    b.supressMerge = true;

    final int N = 20;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
             4             8                    12                                      |
             0             0.1                  0.2                                     |
             5             9                    12                                      |
                                                6                                       |
      2             6                10                     14         16               |
      5             9                12                     6          6.1              |
      1             4                8                      11         13               |
      3             7                10                                2                |
1,2=1  3,4=3  5,6=4  7,8=7    9,10=8   11,12=10    13,14=11   15,16=13    17,18,19,20=2 |
""");
    index.value = 0;
    stuckIndex.value = 0;
    b.clearProgram();
    b.iMergeBranchesNotTop(index, stuckIndex, success);
    b.runProgram();
    ok(success, "success: value=1");
    //stop(b);
    ok(b, """
                               8                  12                                      |
                               0                  0.1                                     |
                               5                  12                                      |
                                                  6                                       |
      2      4        6                10                     14         16               |
      5      5.1      5.2              12                     6          6.1              |
      1      3        4                8                      11         13               |
                      7                10                                2                |
1,2=1  3,4=3    5,6=4    7,8=7  9,10=8   11,12=10    13,14=11   15,16=13    17,18,19,20=2 |
""");
   }

  static void test_mergeBranchesAtTop()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field index = b.index();
    final Layout.Field success = s.success();
    b.L.P.maxSteps = 2000;
    b.supressMerge = true;

    final int N = 15;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    index.value = 6;
    b.clearProgram();
    b.iCopyStuckFrom(s, index);
    s.iPop();
    b.iSaveStuckInto(s, index);
    b.runProgram();
    //stop(b);
    ok(b, """
             4             8                    |
             0             0.1                  |
             5             9                    |
                           6                    |
      2             6                10         |
      5             9                6          |
      1             4                8          |
      3             7                10         |
1,2=1  3,4=3  5,6=4  7,8=7    9,10=8   11,12=10 |
""");

    index.value = 0;
    b.clearProgram();
    b.iMergeBranchesAtTop(index, success);
    b.runProgram();
    ok(success, "success: value=1");
    //stop(b);
    ok(b, """
             4                                   |
             0                                   |
             5                                   |
             9                                   |
      2             6      8         10          |
      5             9      9.1       9.2         |
      1             4      7         8           |
      3                              10          |
1,2=1  3,4=3  5,6=4  7,8=7    9,10=8    11,12=10 |
""");
   }

  static void test_merge()
   {final Btree b = test_create();
    b.L.P.maxSteps = 9000;
    b.supressMerge = true;

    final int N = 32;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
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

    b.stuckKeys.value = 1; b.clearProgram(); b.merge(); b.runProgram();
    //stop(b);
    ok(b, """
                                                                   16                                                                                   |
                                                                   0                                                                                    |
                                                                   14                                                                                   |
                                                                   15                                                                                   |
                     8                                                                   20                    24                                       |
                     14                                                                  15                    15.1                                     |
                     5                                                                   20                    24                                       |
                     12                                                                                        6                                        |
          4                   10         12           14                      18                    22                      26         28               |
          5                   12         12.1         12.2                    20                    24                      6          6.1              |
          1                   8          10           11                      16                    19                      23         25               |
          4                                           13                      18                    21                                 2                |
1,2,3,4=1  5,6,7,8=4   9,10=8   11,12=10     13,14=11     15,16=13   17,18=16   19,20=18   21,22=19   23,24=21     25,26=23   27,28=25    29,30,31,32=2 |
""");

    b.stuckKeys.value = 10; b.clearProgram(); b.merge(); b.runProgram();
    //stop(b);
    ok(b, """
                                                     16                                                                                   |
                                                     0                                                                                    |
                                                     14                                                                                   |
                                                     15                                                                                   |
                     8                                                     20                    24                                       |
                     14                                                    15                    15.1                                     |
                     5                                                     20                    24                                       |
                     12                                                                          6                                        |
          4                         12                          18                    22                      26         28               |
          5                         12                          20                    24                      6          6.1              |
          1                         8                           16                    19                      23         25               |
          4                         11                          18                    21                                 2                |
1,2,3,4=1  5,6,7,8=4   9,10,11,12=8   13,14,15,16=11   17,18=16   19,20=18   21,22=19   23,24=21     25,26=23   27,28=25    29,30,31,32=2 |
""");

    b.stuckKeys.value = 18; b.clearProgram(); b.merge(); b.runProgram();
    //stop(b);
    ok(b, """
                                                     16                                                                       |
                                                     0                                                                        |
                                                     14                                                                       |
                                                     15                                                                       |
                     8                                                                 24                                     |
                     14                                                                15                                     |
                     5                                                                 20                                     |
                     12                                                                6                                      |
          4                         12                                20                          26         28               |
          5                         12                                20                          6          6.1              |
          1                         8                                 16                          23         25               |
          4                         11                                19                                     2                |
1,2,3,4=1  5,6,7,8=4   9,10,11,12=8   13,14,15,16=11   17,18,19,20=16   21,22,23,24=19   25,26=23   27,28=25    29,30,31,32=2 |
""");

    b.stuckKeys.value = 26; b.clearProgram(); b.merge(); b.runProgram();
    //stop(b);
    ok(b, """
                                                       16                                24                                |
                                                       0                                 0.1                               |
                                                       5                                 20                                |
                                                                                         6                                 |
          4          8               12                                 20                                 28              |
          5          5.1             5.2                                20                                 6               |
          1          4               8                                  16                                 23              |
                                     11                                 19                                 2               |
1,2,3,4=1  5,6,7,8=4    9,10,11,12=8    13,14,15,16=11   17,18,19,20=16   21,22,23,24=19    25,26,27,28=23   29,30,31,32=2 |
""");

    b.stuckKeys.value = 32; b.clearProgram(); b.merge(); b.runProgram();
    //stop(b);
    ok(b, """
                                                       16                                                                     |
                                                       0                                                                      |
                                                       5                                                                      |
                                                       20                                                                     |
          4          8               12                                 20               24                 28                |
          5          5.1             5.2                                20               20.1               20.2              |
          1          4               8                                  16               19                 23                |
                                     11                                                                     2                 |
1,2,3,4=1  5,6,7,8=4    9,10,11,12=8    13,14,15,16=11   17,18,19,20=16   21,22,23,24=19     25,26,27,28=23     29,30,31,32=2 |
""");
   }

  static void test_delete()
   {final Btree b = test_create();
    final Stuck s = b.stuck();
    final Layout.Field d = s.data();
    b.L.P.maxSteps = 9000;

    final int N = 32;
    for (int i = 1; i <= N; i++)
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.stuckData.iWrite(i+1);
      b.put();
      b.runProgram();
     }
    //stop(b);
    ok(b, """
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

    final StringBuilder t = new StringBuilder();
    for (int i = N; i >= 2; i -= 2)                                             // Delete evens in reverse
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.delete(d);
      b.runProgram();
      t.append(""+b);
      ok(d, "data: value="+(i+1));
     }
    for (int i = 1; i <= N; i += 2)                                             // Delete odds
     {b.clearProgram();
      b.stuckKeys.iWrite(i);
      b.delete(d);
      b.runProgram();
      t.append(""+b);
      ok(d, "data: value="+(i+1));
     }
    //stop(t);
    ok(""+t, """
                                                      16                                                                |
                                                      0                                                                 |
                                                      6                                                                 |
                                                      11                                                                |
          4          8               12                               20               24                28             |
          6          6.1             6.2                              11               11.1              11.2           |
          1          3               4                                8                10                9              |
                                     7                                                                   2              |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,30,31=2 |
                                                      16                                                             |
                                                      0                                                              |
                                                      6                                                              |
                                                      11                                                             |
          4          8               12                               20               24                28          |
          6          6.1             6.2                              11               11.1              11.2        |
          1          3               4                                8                10                9           |
                                     7                                                                   2           |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27,28=9     29,31=2 |
                                                      16                                                          |
                                                      0                                                           |
                                                      6                                                           |
                                                      11                                                          |
          4          8               12                               20               24             28          |
          6          6.1             6.2                              11               11.1           11.2        |
          1          3               4                                8                10             9           |
                                     7                                                                2           |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,26,27=9     29,31=2 |
                                                      16                                                 |
                                                      0                                                  |
                                                      6                                                  |
                                                      11                                                 |
          4          8               12                               20               24                |
          6          6.1             6.2                              11               11.1              |
          1          3               4                                8                10                |
                                     7                                                 9                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10     25,27,29,31=9 |
                                                      16                                              |
                                                      0                                               |
                                                      6                                               |
                                                      11                                              |
          4          8               12                               20            24                |
          6          6.1             6.2                              11            11.1              |
          1          3               4                                8             10                |
                                     7                                              9                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23=10     25,27,29,31=9 |
                                                      16                                           |
                                                      0                                            |
                                                      6                                            |
                                                      11                                           |
          4          8               12                               20         24                |
          6          6.1             6.2                              11         11.1              |
          1          3               4                                8          10                |
                                     7                                           9                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,23=10     25,27,29,31=9 |
                                                      16                                        |
                                                      0                                         |
                                                      6                                         |
                                                      11                                        |
          4          8               12                            20         24                |
          6          6.1             6.2                           11         11.1              |
          1          3               4                             8          10                |
                                     7                                        9                 |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19=8   21,23=10     25,27,29,31=9 |
                                                      16                              |
                                                      0                               |
                                                      6                               |
                                                      11                              |
          4          8               12                               24              |
          6          6.1             6.2                              11              |
          1          3               4                                8               |
                                     7                                9               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,19,21,23=8   25,27,29,31=9 |
                                                   16                              |
                                                   0                               |
                                                   6                               |
                                                   11                              |
          4          8               12                            24              |
          6          6.1             6.2                           11              |
          1          3               4                             8               |
                                     7                             9               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15=7   17,19,21,23=8   25,27,29,31=9 |
                                                16                              |
                                                0                               |
                                                6                               |
                                                11                              |
          4          8               12                         24              |
          6          6.1             6.2                        11              |
          1          3               4                          8               |
                                     7                          9               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,15=7   17,19,21,23=8   25,27,29,31=9 |
                                             16                              |
                                             0                               |
                                             6                               |
                                             11                              |
          4          8            12                         24              |
          6          6.1          6.2                        11              |
          1          3            4                          8               |
                                  7                          9               |
1,2,3,4=1  5,6,7,8=3    9,10,11=4    13,15=7   17,19,21,23=8   25,27,29,31=9 |
                                     16                              |
                                     0                               |
                                     6                               |
                                     11                              |
          4          8                               24              |
          6          6.1                             11              |
          1          3                               8               |
                     4                               9               |
1,2,3,4=1  5,6,7,8=3    9,11,13,15=4   17,19,21,23=8   25,27,29,31=9 |
                                   16                              |
                                   0                               |
                                   6                               |
                                   11                              |
          4        8                               24              |
          6        6.1                             11              |
          1        3                               8               |
                   4                               9               |
1,2,3,4=1  5,6,7=3    9,11,13,15=4   17,19,21,23=8   25,27,29,31=9 |
                                 16                              |
                                 0                               |
                                 6                               |
                                 11                              |
          4      8                               24              |
          6      6.1                             11              |
          1      3                               8               |
                 4                               9               |
1,2,3,4=1  5,7=3    9,11,13,15=4   17,19,21,23=8   25,27,29,31=9 |
                               16                              |
                               0                               |
                               6                               |
                               11                              |
        4      8                               24              |
        6      6.1                             11              |
        1      3                               8               |
               4                               9               |
1,2,3=1  5,7=3    9,11,13,15=4   17,19,21,23=8   25,27,29,31=9 |
                        16                              |
                        0                               |
                        6                               |
                        11                              |
          8                             24              |
          6                             11              |
          1                             8               |
          4                             9               |
1,3,5,7=1  9,11,13,15=4   17,19,21,23=8   25,27,29,31=9 |
        8             16               24               |
        0             0.1              0.2              |
        1             4                8                |
                                       9                |
3,5,7=1  9,11,13,15=4    17,19,21,23=8    25,27,29,31=9 |
      8             16               24               |
      0             0.1              0.2              |
      1             4                8                |
                                     9                |
5,7=1  9,11,13,15=4    17,19,21,23=8    25,27,29,31=9 |
    8             16               24               |
    0             0.1              0.2              |
    1             4                8                |
                                   9                |
7=1  9,11,13,15=4    17,19,21,23=8    25,27,29,31=9 |
             16              24               |
             0               0.1              |
             1               8                |
                             9                |
9,11,13,15=1   17,19,21,23=8    25,27,29,31=9 |
           16              24               |
           0               0.1              |
           1               8                |
                           9                |
11,13,15=1   17,19,21,23=8    25,27,29,31=9 |
        16              24               |
        0               0.1              |
        1               8                |
                        9                |
13,15=1   17,19,21,23=8    25,27,29,31=9 |
     16              24               |
     0               0.1              |
     1               8                |
                     9                |
15=1   17,19,21,23=8    25,27,29,31=9 |
              24              |
              0               |
              1               |
              9               |
17,19,21,23=1   25,27,29,31=9 |
           24              |
           0               |
           1               |
           9               |
19,21,23=1   25,27,29,31=9 |
        24              |
        0               |
        1               |
        9               |
21,23=1   25,27,29,31=9 |
     24              |
     0               |
     1               |
     9               |
23=1   25,27,29,31=9 |
25,27,29,31=0 |
27,29,31=0 |
29,31=0 |
31=0 |
=0 |
""");
   }
*/
  static void oldTests()                                                        // Tests thought to be in good shape
   {test_create1();
    test_push();
    test_pop();
    test_firstLastPast();
    test_elementAt();
    test_setElementAt();
    test_setDataAt();
    test_setPastLastElement();
    test_insertElementAt();
    test_removeElementAt();
    test_search_eq();
    test_search_le();
    test_splitIntoTwo();
    test_splitIntoThree();
    test_splitLow();
    test_splitLowButOne();
    test_merge();

    //test_create();
    //test_leaf();
    //test_allocFree();
    //test_btree();
    //test_find();
    //test_findAndInsert();
    //test_isLeaf();
    //test_splitLeafRoot();
    //test_splitBranchRoot();
    //test_splitLeafNotTop();
    //test_splitLeafAtTop();
    //test_splitBranchNotTop();
    //test_splitBranchAtTop();
    //test_isRootLeafFull();
    //test_put();
    //test_putReverse();
    //test_putRandom();
    //test_mergeLeavesIntoRoot();
    //test_mergeLeavesAtTop();
    //test_mergeLeavesNotTop();
    //test_mergeBranchesIntoRoot();
    //test_mergeBranchesAtTop();
    //test_mergeBranchesNotTop();
    //test_merge();
    //test_delete();
   }

  static void newTests()                                                        // Tests being worked on
   {//oldTests();
    //test_splitLow();
    test_merge2();
   }

  public static void main(String[] args)                                        // Test if called as a program
   {try                                                                         // Get a traceback in a format clickable in Geany if something goes wrong to speed up debugging.
     {if (github_actions) oldTests(); else newTests();                          // Tests to run
      if (github_actions)                                                       // Coverage analysis
       {coverageAnalysis(sourceFileName(), 12);
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
