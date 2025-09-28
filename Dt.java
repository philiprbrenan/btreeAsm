//------------------------------------------------------------------------------
// A double btree - a Btree made of Btrees
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Double Btree in a block on the surface of a silicon chip.

import java.util.*;

class Dt extends Chip                                                           // A double btree - a double btree made of btrees
 {final int dtSize;                                                             // The maximum number of nodes == branches or leaves in the btree
  final int dtMaxStuckSize;                                                     // The maximum number of entries in the stuck.
  final int dtBitsPerKey;                                                       // The number of bits needed to define a key
  final int dtBitsPerData;                                                      // The number of bits needed to define a data field
  final int dtMaxTreeDepth        = 9;                                          // Maximum tree height
  final int dtLinesToPrintABranch = 4;                                          // The number of lines required to print a branch
  final int dtMaxPrintLevels      = 3;                                          // The maximum number of levels to print stuck - this avoids endless print loops when something goes wrong

  final Stuck[]dtStucks;                                                        // The stucks used to construct the double btree
  int          dtUsed;                                                          // Number of stucks used by allocation

  boolean              dtSuppressMerge    = false;                              // Suppress merges during put to allow merge steps to be tested individually.  If this is on the double btrees built for testing are already merged so there is nothing to test.
  static       boolean dtCreateTestTrees  = false;                              // Create trees to assist testing
  static       boolean dtDebug            = false;                              // Debug if enabled
  final static boolean dtCoverageAnalysis = false;                              // Enables coverage checks

//D1 Construction                                                               // Construct and layout the double btree

  Dt(int Size, int MaxStuckSize, int BitsPerKey, int BitsPerData)               // Create the double btree
   {super("Dt"); N();
    if (MaxStuckSize % 2 == 1) stop("The stuck size must be even, not:", MaxStuckSize);
    if (MaxStuckSize < 4)      stop("The stuck size must be greater than equal to 4, not:", MaxStuckSize);

    dtSize         = Size;                                                      // The maximum number of entries in the double btree
    dtMaxStuckSize = MaxStuckSize;                                              // The maximum number of entries in the stuck.
    dtBitsPerKey   = BitsPerKey;                                                // The number of bits needed to define a key
    dtBitsPerData  = BitsPerData;                                               // The number of bits needed to define a data field

    dtStucks       = new Stuck[Size];                                           // Stucks forming the double btree
    dtCreateRootStuck();                                                        // Create the free chain
   }

  Dt Dt() {return this;}

//D2 Allocation                                                                 // Allocate stucks from the free chain

  void dtCreateRootStuck()                                                      // Initialize the root stuck
   {final Stuck r = dtStucks[0] = new Stuck(0);                                 // Stuck zero is always the root
    dtUsed = 1;                                                                 // We have used one stuck for the root
    r.stuckSetLeaf();                                                           // The root starts as a stuckLeaf
   }

  Stuck dtStuckGet(int Index) {return dtStucks[Index];}                         // Get a stuck

  private Stuck dtAllocate(boolean stuckLeaf)                                   // Allocate a stuck
   {final Stuck r = dtStucks[0];
    final int   n = r.stuckFreeNext;
    if (n != 0)                                                                 // Reuse a stuck from the free chain
     {final Stuck N = dtStucks[n];
      r.stuckFreeNext = N.stuckFreeNext;
      N.stuckClear();
      N.stuckSetFree(false);
      if (stuckLeaf) N.stuckSetLeaf(); else N.stuckSetBranch();
      return N;
     }
    if (dtUsed < dtSize)                                                        // Allocate a new stuck
     {final Stuck N = dtStucks[dtUsed] = new Stuck(dtUsed);
      N.stuckClear();
      N.stuckSetFree(false);
      if (stuckLeaf) N.stuckSetLeaf(); else N.stuckSetBranch();
      dtUsed++;
      return N;
     }
    stop("No more memory available");                                           // Out of memory
    return null;
   }

  private void dtFree(Stuck S)                                                  // Free the referenced stuck and put it on the free chain
   {final Stuck r = dtStucks[0];
    S.stuckFreeNext = r.stuckFreeNext;
    r.stuckFreeNext = S.stuckNumber;
    S.stuckSetFree(true);
   }

  private Stuck dtAllocateLeaf  () {return dtAllocate(true);}                   // Allocate a stuck as a leaf
  private Stuck dtAllocateBranch() {return dtAllocate(false);}                  // Allocate a stuck as a branch

//D2 Save and Load                                                              // Save a double btree to a string and reload it from a string

  String dtSave()                                                               // Save a double btree to a string
   {final StringBuilder s = new StringBuilder();
    s.append(""+dtSize              +"\n");
    s.append(""+dtMaxStuckSize      +"\n");
    s.append(""+dtBitsPerKey        +"\n");
    s.append(""+dtBitsPerData       +"\n");
    return ""+s;
   }

  void dtLoad(String data)                                                      // Load a double btree from a string
   {final String[]l = data.split("\\n");
    final int   []n = new int[6];
    for (int i = 0; i < n.length; i++) n[i] = Integer.parseInt(l[i]);
    int j = 0;
    if (dtSize         != n[j++]) stop("Wrong size");
    if (dtMaxStuckSize != n[j++]) stop("Wrong maxStuckSize");
    if (dtBitsPerKey   != n[j++]) stop("Wrong bitsPerKey");
    if (dtBitsPerData  != n[j++]) stop("Wrong bitsPerData");
   }

//D2 Stuck                                                                      // Get and set stucks within btree

  class Stuck                                                                   // A reference to a stuck in the memory of the btree plus a copy of its size, keys and data
   {final   int     stuckNumber;                                                // Number of this stuck
    private final   TreeMap<Integer,Integer> stuckMap = new TreeMap<>();        // Keys to data map
    private int     stuckTop;                                                   // Every branch has a top data element, no stuckLeaf does, that is it's tragedy
    private int     stuckFreeNext;                                              // Next element on the free chain
    private boolean stuckLeaf;                                                  // Whether this is a stuckLeaf or a branch
    private boolean stuckFree;                                                  // Whether this stuck is free or in use
    boolean         stuckFound;                                                 // Whether the key was found
    int             stuckKey;                                                   // Data associated with the key if found
    int             stuckFoundKey;                                              // Data associated with the key if found
    int             stuckData;                                                  // Data associated with the key if found
    int             stuckDtIndex;                                               // Index of stuck in Btree in which the key should reside
    int             stuckIndex;                                                 // Index of stuck in Btree in which the key should reside
    boolean         stuckMergeSuccess;                                          // Whether a dtMerge was completed successfully or not

    Stuck(int Index)                                                            // Create a stuck recording its position in memory
     {stuckNumber = Index;
     }

    int     stuckSize     ()              {return stuckMap.size();}
    int     stuckFirstKey ()              {return stuckMap.firstKey();}
    int     stuckPrevKey  (int k)         {return stuckMap.lowerKey(k);}
    int     stuckNextKey  (int k)         {return stuckMap.higherKey(k);}
    int     stuckLastKey  ()              {return stuckMap.lastKey();}
    int     stuckTop      ()              {return stuckTop;}
    void    stuckSetTop   (int d)         {stuckTop = d;}
    void    stuckSetTop   (Stuck d)       {stuckTop = d.stuckNumber;}
    boolean stuckHas      (int k)         {return stuckMap.containsKey(k);}
    Integer stuckLe       (int k)         {return stuckMap.floorKey(k);}
    Integer stuckGe       (int k)         {return stuckMap.ceilingKey(k);}
    int     stuckGetData  (int k)         {return stuckMap.get(k);}
    void    stuckPut      (int k, int   d){stuckMap.put(k, d);}
    void    stuckPut      (int k, Stuck d){stuckMap.put(k, d.stuckNumber);}
    void    stuckPut      (Stuck Source)  {stuckMap.putAll(Source.stuckMap);}
    int     stuckRemove(int k)            {final int d = stuckGetData(k); stuckMap.remove(k); return d;}
    boolean stuckIsLeaf   ()              {return  stuckLeaf;};
    boolean stuckIsBranch ()              {return !stuckLeaf;};
    void    stuckSetLeaf  ()              {stuckLeaf = true;}
    void    stuckSetBranch()              {stuckLeaf = false;}
    void    stuckClear    ()              {stuckMap.clear();}
    void    stuckSetFree  (boolean Free)  {stuckFree = Free;}

//D3 Memory                                                                     // Get a stuck from memory or return it to memory

    Stuck stuckGetFromMemory(int Index)                                         // Copy a stuck indexed by a register out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retrieval of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {return dtStucks[Index];
     }

    void stuckGetFromMemoryRoot()                                               // Copy the root stuck out of memory into a set of registers. Currently this is done sequentially, but multiple stuck loads could be overlapped if this method was fragmented into smaller steps.  Most stuck methods do not actually require the retrieval of a full stuck from memory but doing so makes it easier to write an initial version of the btree algorithm at the cost of considerable inefficiency.
     {stuckGetFromMemory(0);
     }

    void stuckPutIntoMemory()                                                   // Update a stuck in memory from the registers describing this stuck optionally updating the isLeaf field.
     {
     }

    void stuckPutIntoMemory(int Index, boolean SetLeaf)                         // Update a stuck in memory from the registers describing this stuck optionally updating the isLeaf field.
     {
     }

    void stuckPutIntoMemory(int Index)              {}                          // Update a stuck in memory from the registers describing his stuck
    void stuckPutIntoMemory(boolean SetLeaf)        {}                          // Update a stuck in memory from the registers describing his stuck

    void stuckPutIntoMemoryRoot()                                               // Update the root stuck in memory
     {
     }

    void stuckPutIntoMemoryRoot(boolean SetLeaf)                                // Update the root stuck in memory setting it as a stuckLeaf or a branch
     {
     }

//D3 Print                                                                      // Print the stuck

    public String toString()
     {final StringBuilder S = new StringBuilder();
      S.append("Stuck: "+stuckNumber+", size: "+stuckSize()+", leaf: "+(stuckIsLeaf() ? 1 : 0)+", top: "+stuckTop+"\n");
      for(Integer k : stuckMap.keySet())
       {S.append(String.format("  %2d: %2d\n", k, stuckMap.get(k)));
       }
      final boolean f = stuckFound       ; S.append("Found     : "+(f ? 1 : 0)+"\n"); // Whether the key was found
      final int     k = stuckKey         ; S.append("Key       : "+k+"\n");           // Data associated with the key if found
      final int     K = stuckFoundKey    ; S.append("FoundKey  : "+K+"\n");           // Data associated with the key if found
      final int     d = stuckData        ; S.append("Data      : "+d+"\n");           // Data associated with the key if found
      final int     b = stuckDtIndex     ; S.append("DtIndex   : "+b+"\n");           // Index of stuck in Btree in which the key should reside
      final int     s = stuckIndex       ; S.append("StuckIndex: "+s+"\n");           // Index of stuck in Btree in which the key should reside
      final boolean m = stuckMergeSuccess; S.append("Merge     : "+(m ? 1 : 0)+"\n"); // Whether a dtMerge was completed successfully or not

      return ""+S;
     }

    String stuckDump()                                                          // Print all the entries in the stuck
     {final StringBuilder S = new StringBuilder();
      return ""+S;
     }

//D3 Copy                                                                       // Copy a stuck into itself or another stuck shifting the elements in the stuck by a predetermined amount

    void stuckCopy(Stuck Source)                                                // Copy a stuck from the specified source to this stuck
     {
     }

    void stuckCopyDown(Stuck Source, int Power)                                 // Copy a stuck into itself or another stuck shifting the elements down in the stuck by a predetermined amount
     {
     }

    void stuckCopyDown(int Delta)                                               // Copy a stuck down into itself a variable number of places
     {
     }

    void stuckCopyUp(Stuck Source, int Power)                                   // Copy a stuck into itself or another stuck shifting the elements up in the stuck by a predetermined amount
     {
     }

    void stuckCopyUp(int Delta)                                                 // Copy a stuck up into itself a variable number of places
     {
     }

//D4 Combine                                                                    // Combine stucks by "or"ing their elements

    void stuckCombine(Stuck Source)                                             // Combine a stuck from the specified source to this stuck
     {
     }

//D3 Actions                                                                    // Just the actions needed on a stuck to support a double btree

    void stuckPush(int Key, int Data)                                           // Push a key, data pair to the local copy of the stuck
     {if (stuckSize() >= dtMaxStuckSize) stop("Stuck overflow");
      if (stuckSize() > 0 && Key <= stuckFirstKey()) stop("Cannot push:", Key);
      stuckPut(Key, Data);
     }

    void stuckPush(int Key, Stuck Data) {stuckPush(Key, Data.stuckNumber);}     // Push a key, data pair to the local copy of the stuck

    void stuckPop()                                                             // Pop a key, data pair from the local copy of the stuck
     {if (stuckSize() == 0) stop("Cannot apply pop to an empty stuck");
      stuckKey  = stuckLastKey();
      stuckData = stuckMap.get(stuckKey);
      stuckMap.remove(stuckKey);
     }

    void stuckSetPastLastKey(int Key)                                           // Set the  key past the top of the stuck
     {
     }

    void stuckSetPastLastData(int Data)                                         // Set the data element past the top of the stuck
     {
     }

    void stuckSetPastLastElement(int Key, int Data)                             // Push a key, data pair to the local copy of the stuck without changing the size
     {
     }

    void stuckFirstElement()                                                    // Get the first key, data pair
     {stuckKey = stuckFirstKey();
      stuckData = stuckMap.get(stuckKey);
     }

    void stuckLastElement()                                                     // Get the last key, data pair
     {stuckKey = stuckLastKey();
      stuckData = stuckMap.get(stuckKey);
     }

    void stuckPastLastElement()                                                 // Get the past last key, data pair
     {
     }

    void stuckElementAt(int Index)                                              // Get the indexed key, data pair.. The index is the key of the item requested so this behaves like get.
     {stuckKey = Index;
      stuckData = stuckMap.get(stuckKey);
     }

    void stuckSetElementAt(int Key, int Data)                                   // Set the data of the indexed key
     {dtPut(Key, Data);
     }

    void stuckInsertElementAt(int Key, int Data)                                // Set the indexed key, data pair
     {dtPut(Key, Data);
     }

    void stuckRemoveElementAt(int Index)                                        // Remove the indexed key, data pair
     {stuckKey  = Index;
      stuckData = stuckGetData(Index);
      stuckMap.remove(Index);
     }

//D3 Search                                                                     // Search the stuck

    void stuckSearch_eq_parallel(int Key)                                       // Find the specified key if possible in the stuck
     {if (stuckMap.containsKey(Key))
       {stuckFound = true;
        stuckKey  = Key;
        stuckData = stuckGetData(Key);
       }
      else stuckFound = false;
     }

    void stuckSearch_le_parallel(int Key)                                       // Find the specified key if possible in the stuck
     {
     }

//D3 Split                                                                      // Split stucks in many and various ways

    void stuckSplitIntoTwo(Stuck Left, Stuck Right)                             // Copy the first half key, data pairs into the left stuck, the remainder into the right stuck.  The original source stuck is not modifiedr
     {for (int i = 0; i < dtMaxStuckSize / 2; i++)
       {final int k = stuckFirstKey();
        final int d = stuckGetData(k);
        Left.stuckPush(k, d);
        stuckRemove(k);
       }
      for (int i = 0; i < dtMaxStuckSize / 2; i++)
       {final int k = stuckFirstKey();
        final int d = stuckGetData(k);
        Right.stuckPush(k, d);
        stuckRemove(k);
       }
     }

    void stuckSplitIntoThree(Stuck Left, Stuck Right, int Copy)                 // Copy the specified number of key, data pairs into the left stuck, skip one pair, then copy the specified number onto into the right stuck
     {final int N = stuckSize() / 2;
      if (Copy > N) stop("Copy:", Copy, "must be less than or equal to:", N);
      for (int i = 0; i < Copy; ++i)
       {final int k = stuckFirstKey();
        final int d = stuckGetData(k);
        Left.stuckPut(k, d);
        stuckRemove(k);
       }
      final int K = stuckFirstKey();
      Left.stuckSetTop(stuckGetData(K));
      stuckRemove(K);

      for (int i = 0; i < Copy; ++i)
       {final int k = stuckLastKey();
        final int d = stuckGetData(k);
        Right.stuckPut(k, d);
        stuckRemove(k);
       }
      Right.stuckSetTop(stuckTop());
     }

    void stuckSplitLow(Stuck Left)                                              // Split a full stuck with an even number of elements so that the first half is moved into the left stuck leaving the remainder in the current stuck
     {for (int i = 0; i < dtMaxStuckSize / 2; i++)
       {final int k = stuckFirstKey();
        final int d = stuckGetData(k);
        Left.stuckPush(k, d);
        stuckRemove(k);
       }
     }

    int stuckSplitLowButOne(Stuck Left)                                         // Split an almost full stuck with an odd number of elements so that the first half is moved into the left stuck extended by the data of the central element, while the remainder beyond the central element are moved down.  The central key is returned as it would otherwise be lost.
     {if (stuckSize() != dtMaxStuckSize-1) stop("Branch must be full");
      final int N = (dtMaxStuckSize-1) / 2;
      for (int i = 0; i < N; ++i)
       {final int k = stuckFirstKey();
        final int d = stuckGetData(k);
        Left.stuckPut(k, d);
        stuckRemove(k);
       }
      final int K = stuckFirstKey();
      final int D = stuckGetData(K);
      Left.stuckSetTop(D);
      stuckRemove(K);
      return K;
     }

//D3 Merge                                                                      // Merge stucks in various ways

    void stuckMerge(Stuck Source)                                               // Concatenate the indicated stuck onto the end of the current one
     {if (stuckSize() + Source.stuckSize() <= dtMaxStuckSize)
       {stuckMap.putAll(Source.stuckMap);
        stuckMergeSuccess = true;
       }
      else stuckMergeSuccess = false;
     }

    void stuckMerge(Stuck Left, Stuck Right)                                    // Replace the current stuck with the concatenation of the two stucks indicated
     {if (stuckMergeSuccess = Left.stuckSize() + Right.stuckSize() <= dtMaxStuckSize)
       {stuckClear();
        stuckMap.putAll(Left.stuckMap);
        stuckMap.putAll(Right.stuckMap);
       }
     }

    void stuckMergeButOne(int Key, Stuck Source)                                // Concatenate the indicated stuck with a past last data element onto the end of the current stuck with a past last data element with the specified key inserted over the central past last data element separating the two.
     {if (stuckMergeSuccess = Source.stuckSize() + 1 + stuckSize() < dtMaxStuckSize)
       {stuckPush(Key, stuckTop());
        stuckPut(Source);
        stuckSetTop(Source.stuckTop());
       }
     }

    void stuckMergeButOne(Stuck Left, int Key, Stuck Right)                     // Concatenate the past last left and right stucks separated by the key over the past last data element of the left stuck into the target
     {if (stuckMergeSuccess = Left.stuckSize() + 1 + Right.stuckSize() < dtMaxStuckSize)
       {stuckClear();
        stuckPut(Left);
        stuckPush(Key, Left.stuckTop());
        stuckPut(Right);
        stuckSetTop(Right.stuckTop());
       }
     }

//D3 Is a Leaf                                                                  // Determine whether a stuck contains a stuckLeaf or a branch of a double btree

    class IsLeaf                                                                // Process a stuck depending on whether it is a stuckLeaf or a branch
     {void Leaf()   {}
      void Branch() {}
     }
   } // Stuck

//D2 Horizontally                                                               // Print the double btree horizontally

    void dtPrintLeaf(int DtIndex, Stack<StringBuilder>P, int level)             // Print stuckLeaf horizontally
     {dtPadStrings(P, level);

      final StringBuilder s = new StringBuilder();                              // String builder
      final Stuck l = dtStuckGet(DtIndex);
      for  (Integer k : l.stuckMap.keySet()) s.append(""+k+",");

      if (s.length() > 0) s.setLength(s.length()-1);                            // Remove trailing comma if present
      s.append("="+DtIndex+" ");
      P.elementAt(level*dtLinesToPrintABranch).append(s.toString());
      dtPadStrings(P, level);
     }

    void dtPrintBranch(int DtIndex, Stack<StringBuilder>P, int level)           // Print branch horizontally
     {if (level > dtMaxPrintLevels) return;
      dtPadStrings(P, level);
      final int L = level * dtLinesToPrintABranch;                              // Start line at which to print branch
      final Stuck b = dtStuckGet(DtIndex);                                      // Load branch
      final int K = b.stuckSize();                                              // Size of branch

      if (K > 0)                                                                // Branch has key, next pairs
       {int i = 0;
        for  (Integer k : b.stuckMap.keySet())
         {final int key  = k;
          final int data = b.stuckGetData(k);
          if (dtStuckGet(data).stuckIsLeaf())
           {dtPrintLeaf  (data, P, level+1);
           }
          else
           {dtPrintBranch(data, P, level+1);
           }

          P.elementAt(L+0).append(""+key);                                      // Key
          P.elementAt(L+1).append(""+DtIndex+(i > 0 ?  "."+i : ""));            // Branch,key, next pair
          P.elementAt(L+2).append(""+data);
          ++i;
         }
       }
      else                                                                      // Branch is empty so print just the index of the branch
       {P.elementAt(L+0).append(""+DtIndex+"Empty");
       }
      final int top = b.stuckTop();                                             // Top next will always be present
      P.elementAt(L+3).append(top);                                             // Append top next

      final Stuck t = dtStuckGet(top);                                          // Load top
      if (t.stuckIsLeaf())                                                      // Print stuckLeaf
       {dtPrintLeaf  (top, P, level+1);
       }
      else                                                                      // Print branch
       {dtPrintBranch(top, P, level+1);
       }

      dtPadStrings(P, level);
     }

   String dtPrintBoxed()                                                        // Print a tree in a box
    {final String  s = toString();
     final int     n = longestLine(s)-1;
     final String[]L = s.split("\n");
     final StringBuilder t = new StringBuilder();
     t.append("+"); t.append("-".repeat(n)); t.append("+\n");
     for(String l : L) t.append("| "+l+"\n");
     t.append("+"); t.append("-".repeat(n)); t.append("+\n");
     return t.toString();
    }

  void dtPadStrings(Stack<StringBuilder> S, int level)                          // Pad the strings at each level of the double btree so we have a vertical face to continue with - a bit like Marc Brunel's tunneling shield
   {final int N = level * dtLinesToPrintABranch + dtMaxStuckSize;               // Number of lines we might want
    for (int i = S.size(); i <= N; ++i) S.push(new StringBuilder());            // Make sure we have a full deck of strings
    int m = 0;                                                                  // Maximum length
    for (StringBuilder s : S) m = m < s.length() ? s.length() : m;              // Find maximum length
    for (StringBuilder s : S)                                                   // Pad each string to maximum length
     {if (s.length() < m) s.append(" ".repeat(m - s.length()));                 // Pad string to maximum length
     }
   }

  String dtPrintCollapsed(Stack<StringBuilder> S)                               // Collapse horizontal representation into a string
   {final StringBuilder t = new StringBuilder();                                // Print the lines of the double btree that are not blank
    for  (StringBuilder s : S)
     {final String l = s.toString();
      if (l.isBlank()) continue;
      t.append(l+"|\n");
     }
    return t.toString();
   }

  String dtPrint()                                                              // Print a tree horizontally
   {final Stack<StringBuilder> P = new Stack<>();
    final Stuck r = dtStuckGet(0);
    if (r.stuckIsLeaf()) dtPrintLeaf(0, P, 0); else dtPrintBranch(0, P, 0);

    return dtPrintCollapsed(P);
   }

  public String toString() {return dtPrint();}                                  // Print the double btree

//D2 Split                                                                      // Split nodes in half to increase the number of nodes in the double btree

  private void dtSplitLeafRoot()                                                // Split a full root stuckLeaf
   {final Stuck p = dtStuckGet(0);                                              // Load root as parent
    if (!p.stuckIsLeaf()) stop("Root must be a leaf");

    final Stuck l = dtAllocateLeaf();                                           // Allocate left leaf
    final Stuck r = dtAllocateLeaf();                                           // Allocate right leaf

    p.stuckSplitIntoTwo(l, r);                                                  // Split the leaf root in two down the middle

    final int mk = (l.stuckLastKey() + r.stuckFirstKey()) / 2;                  // Mid key is the average of the largest left and smalles right keys
    p.stuckClear();
    p.stuckPush(mk, l);
    p.stuckSetTop(  r);
    p.stuckSetBranch();
    p.stuckPutIntoMemory();
   }

  private void dtSplitBranchRoot()                                              // Split a full root branch
   {final Stuck p = dtStuckGet(0);                                              // Load root as parent
    if (!p.stuckIsBranch()) stop("Root must be a branch");

    final Stuck l = dtAllocateBranch();                                         // Allocate left leaf
    final Stuck r = dtAllocateBranch();                                         // Allocate right leaf
    final int   m = (dtMaxStuckSize-1) / 2;
    if (p.stuckSize() < dtMaxStuckSize-1) stop("Root must be full");
    p.stuckSplitIntoThree(l, r, m);                                             // Split the branch root in two down the middle
    l.stuckPutIntoMemory();                                                     // Save left branch
    r.stuckPutIntoMemory();                                                     // Save right branch
                                                                             // Update root with new children
    final int mk = (l.stuckLastKey() + r.stuckFirstKey()) / 2;                                                  // Get splitting key
    p.stuckClear();                                                             // Clear the root so we can add the left and right children to it.
    p.stuckPush(mk, l);                                                         // Add reference to left child
    p.stuckSetTop(r);                                                           // Add reference to right child as top element past the end of the stuck
    p.stuckPutIntoMemory();                                                               // Save the root stuck back into the btree whence it came
   }

  private void dtSplitLeafNotTop(Stuck Parent, int Index)                       // Split a full leaf that is not the root and is not the last child of its parent branch.  The parent branch must not be full
   {final Stuck p = Parent;

    final int ck = Index;                                                       // Key of leaf to split
    final int cd = p.stuckGetData(Index);                                       // Index of child to split
    final Stuck r = p.stuckGetFromMemory(cd);                                   // The indexed child will be on the right

    if ( p.stuckIsLeaf())                     stop("Parent must be a branch");
    if ( p.stuckSize  () >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsLeaf())                     stop("Child must be a leaf");
    if ( r.stuckSize  () <  dtMaxStuckSize)   stop("Child must be full");

    final Stuck l = dtAllocateLeaf();
    r.stuckSplitLow(l);                                                         // Split the leaf in two down the middle copying out the lower half
    r.stuckPutIntoMemory();                                                     // Allocate and save split out left of leaf
    l.stuckPutIntoMemory();                                                     // Save remainder of leaf

    final int ll = l.stuckLastKey();                                            // Last key of left child
    final int rf = r.stuckFirstKey();                                           // First key of right child
    final int mk = (ll  + rf) / 2;                                              // Mid key is average of surrounding keys
    p.stuckPut(mk, l); //// number                                                         // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the btree
   }

  private void dtSplitLeafAtTop(Stuck Parent)                                   // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = Parent;
    final Stuck r = dtStuckGet(p.stuckTop());                                   // Top child stuck that is going to be split

    if (p.stuckIsLeaf ())                    stop("Parent must be branch");
    if (p.stuckSize   () > dtMaxStuckSize-1) stop("Parent must not be full");   // Branches are one less in size than leaves buttheyhave the top element to compensate
    if (!r.stuckIsLeaf())                    stop("Child must be a leaf");
    if ( r.stuckSize  () != dtMaxStuckSize)  stop("Child must be a full");      // Leaves fill the stuck completely

    final Stuck l = dtAllocateLeaf();
    r.stuckSplitLow(l);                                                         // Split the leaf in two down the middle copying out the lower half

    final int ll = l.stuckLastKey();                                            // Last key of left child
    final int rf = r.stuckFirstKey();                                           // First key of right child
    final int mk = (ll  + rf) / 2;                                              // Mid key is average of surrounding keys
    p.stuckPut(mk, l);                                                          // Add reference to left child
   }

  private void dtSplitBranchNotTop(Stuck Parent, int Index)                     // Split a full branch that is not the root and is not the last child of its parent branch which is not full
   {final Stuck p = Parent;                                                     // Parent stuck containing a refernce to the stuck to be split
    final Stuck r = dtStuckGet(p.stuckGetData(Index));                          // Key of child

    if (!p.stuckIsBranch())                     stop("Parent must be a branch");
    if ( p.stuckSize    () >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsBranch())                     stop("Child must be a branch");
    if ( r.stuckSize    () <  dtMaxStuckSize-1) stop("Child must not full");

    final Stuck l = dtAllocateBranch();
    final int Key = r.stuckSplitLowButOne(l);                                   // Split the branch into two branches and retain the splitting key
    l.stuckPutIntoMemory();                                                     // Allocate and save left branch
    r.stuckPutIntoMemory();                                                     // SSave modified right branch
                                                                                // Update root with new children
    p.stuckPut(Key, l);                                                         // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the btree
   }

  private void dtSplitBranchAtTop(Stuck Parent)                                 // Split a full branch that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = Parent;                                                     // Parent stuck

    final Stuck r = dtStuckGet(p.stuckTop());                                   // Top most child is on the right

    if (!p.stuckIsBranch())                   stop("Parent must be a branch");
    if ( p.stuckSize()   >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsBranch())                   stop("Child must be a branch");
    if ( r.stuckSize()   <  dtMaxStuckSize-1) stop("Child branch must be full");

    final Stuck l = dtAllocateBranch();                                         // New left branch
    final int  mk = r.stuckSplitLowButOne(l);                                        // Split the leaf in two down the middle copying out the lower half
    l.stuckPutIntoMemory();                                                     // Save left branch
    r.stuckPutIntoMemory();                                                     // Save modified right branch
                                                                                // Update root with new children
    p.stuckPut(mk, l);                                                          // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the btree
   }

//D1 Merge                                                                      // Merge two nodes

  private boolean dtSkipMerge(Stuck Parent, int ParentIndex)                    // Whether a  dtMerge is permitted or not.
   {if (Parent.stuckSize() == 0) return false;
    final int first = Parent.stuckMap.firstKey();
    final boolean f = ParentIndex == first;                                     // Are we dealing with the first child
    final int r = Parent.stuckSize();                                           // Size of parent
    return r == 0 || (r == 1 && f);                                             // Conditions on which to skip the dtMerge.  dtMerge is not possible if there are no children to dtMerge, or in the case of one child, it will be handled by the dtMerge into top
   }

  private boolean dtMergeLeavesIntoRoot()                                       // Merge two leaves into the root
   {final Stuck p = dtStuckGet(0);                                              // Parent stuck two of whose children might be dtMerged

    p.stuckGetFromMemoryRoot();                                                 // Load root

    if (p.stuckSize() != 1) return false;                                       // Number of entries in root

    final int il = p.stuckGetData(p.stuckFirstKey());                           // Index of left stuckLeaf
    final int ir = p.stuckTop();                                                // Index of right stuckLeaf

    final Stuck l = dtStuckGet(il);                                             // Left split stuck
    final Stuck r = dtStuckGet(ir);                                             // Right split stuck    final Process.Register ck = P.new Register("childKey",   bitsPerKey);       // Index in memory of the left stuck

    if (l.stuckIsLeaf() && r.stuckIsLeaf())                                     // Check that the children are leaves
     {//say("MMMM LeavesIntoRoot 1111", this);
      p.stuckMerge(l, r);                                                       // Merge leaves into root
      if (p.stuckMergeSuccess)                                                  // Merge was successful
       {p.stuckSetLeaf();                                                       // Mark the root as a stuckLeaf
        p.stuckPutIntoMemory(true);                                             // Save the modified root back into the double btree
        dtFree(l); dtFree(r);                                                   // Free left and right leaves as they are no longer needed
//say("MMMM LeavesIntoRoot 2222", this);
        return true;                                                            // Success
       }
     }
    return false;
   }

  private boolean dtMergeLeavesNotTop(Stuck Parent, int Left)                   // Merge the two consecutive leaves of a branch that is not the root. Neither of the leaves is the topmost stuckLeaf.
   {final Stuck p = Parent;
    //if (p.stuckNumber == 0)       return false;                                 // Cannot perform this merge on the root
    if (!p.stuckHas(Left))        return false;                                 // Key must be in stuck
    if (p.stuckLastKey() == Left) return false;                                 // Key cannot be the last key in the body
    if (dtSkipMerge(p, Left))     return false;                                 // Skip impossible merges
    final int il = p.stuckGetData(Left);                                        // Index of left leaf
    final int rk = p.stuckNextKey(Left);                                        // Right leaf key
    final int ir = p.stuckGetData(rk);                                          // Indexes of right leaf

    final Stuck l = dtStuckGet(il);
    final Stuck r = dtStuckGet(ir);

    if (l.stuckIsBranch() || r.stuckIsBranch()) return false;                   // Check that the children are leaves
//say("MMMM LeavesNotTop 1111", this);
    l.stuckMerge(r);                                                            // Merge leaves into left child
    if (l.stuckMergeSuccess)                                                    // Modify the parent only if the merge succeeded
     {p.stuckRemove(Left);                                                      // Remove the left child moving the right child down into its space
      p.stuckPut(rk, il);                                                       // Replace the right child with the left child
      l.stuckPutIntoMemory();                                                   // Save the merge into left child back into the double btree
      p.stuckPutIntoMemory();                                                   // Save the parent back into the double btree
      dtFree(r);                                                                // Free right leaf as it is no longer in use
//say("MMMM LeavesNotTop 2222", this);
      return true;
     }

    return false;
   }

  private boolean dtMergeLeavesAtTop(Stuck Parent)                              // Merge the top most two leaves of a branch that is not the root
   {final Stuck p = Parent;
    if (p.stuckNumber == 0) return false;                                       // Cannot perform this merge on the root
    final int sz = p.stuckSize();                                               // Size of parent stuck
    if (sz == 0) return false;                                                  // Nothing to merge

    final int il = p.stuckGetData(p.stuckLastKey());
    final int ir = p.stuckTop;
    if (dtSkipMerge(p, il)) return false;

    final Stuck l = dtStuckGet(il);
    final Stuck r = dtStuckGet(ir);

    if (l.stuckIsLeaf() && r.stuckIsLeaf())                                     // Check that the children are leaves
     {//say("MMMM LeavesAtTop 1111", this);
      l.stuckMerge(r);                                                          // Merge leaves into left child
      if (l.stuckMergeSuccess)
       {p.stuckRemove(p.stuckLastKey());                                     // The left child is now topmost - we know this is ok because the parent has at elast one entry
        p.stuckSetTop(il);
        l.stuckPutIntoMemory();                                                 // Save the modified left child back into the tree
        p.stuckPutIntoMemory();                                                 // Save the modified root back into the tree
        dtFree(r);                                                              // Free right leaf as it is no longer in use
//say("MMMM LeavesAtTop 2222", this);
        return true;
       }
     }
    return false;
   }

  private boolean dtMergeBranchesIntoRoot()                                     // Merge two branches into the root
   {final Stuck p = dtStuckGet(0);                                              // Root

    if (p.stuckSize() != 1) return false;                                       // One element only required in root

    final int mk = p.stuckFirstKey();                                              // Splitting key is the only key in the parent
    final int il = p.stuckGetData(mk);                                              // Index of left branch
    final int ir = p.stuckTop();                                                // Index of right branch

    final Stuck l = dtStuckGet(il);                                             // Index of left branch
    final Stuck r = dtStuckGet(ir);                                             // Index of right branch

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Check that the children are leaves
//say("MMMM BranchesntoRoot 1111", this);
    p.stuckMergeButOne(l, mk, r);                                                    // Merge left branch, splitting key, right branch into root
    if (p.stuckMergeSuccess)
     {p.stuckPutIntoMemory();                                                   // Save the modified root back into the tree
      dtFree(l); dtFree(r);                                                     // Free left and right leaves as they are no longer needed
//say("MMMM BranchesntoRoot 2222", this);
      return true;
     }
    return false;
   }

  private boolean dtMergeBranchesNotTop(Stuck Parent, int Left)                 // Merge the two consecutive child branches of a branch that is not the root. Neither of the child branches is the topmost stuckLeaf.
   {final Stuck   p = Parent;                                                   // Parent stuck

    //if (p.stuckNumber == 0)       return false;                                 // Cannot perform this merge on the root
    if (p.stuckSize() == 0)       return false;                                 // Cannot perform this merge on an empty stuck
    if (!p.stuckHas(Left))        return false;                                 // Key must be in stuck
    if (p.stuckLastKey() == Left) return false;                                 // Key cannot be the last key in the body
    if (dtSkipMerge(p, Left))     return false;                                 // Skip impossible merges

    final int il = p.stuckGetData(Left);                                        // Get the btree index of the left child branch
    final int rk = p.stuckNextKey(Left);                                        // Get the btree index of the right child branch
    final int ir = p.stuckGetData(rk);                                          // Get the btree index of the right child branch

    final Stuck l = dtStuckGet(il);                                             // Load left  branch from btree
    final Stuck r = dtStuckGet(ir);                                             // Load right branch from btree

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Both children must be branches
    final int mk = p.stuckGetData(Left);                                        // Key associated with left child branch

//say("MMMM BranchesNotTop 1111", this);
    l.stuckMergeButOne(Left, r);                                                // Merge branches into left child

    if (l.stuckMergeSuccess)                                                    // Modify the parent only if the merge succeeded
     {p.stuckRemove(Left);                                                      // Remove the left child
      p.stuckPut(rk, il);                                                       // Update left child position with key of right child and index of left child
      l.stuckPutIntoMemory();                                                   // Save the modified left child back into the tree
      p.stuckPutIntoMemory();                                                   // Save the modified root back into the tree
      dtFree(r);                                                                // Free right branch as it is no longer in use
//say("MMMM BranchesNotTop 2222", this);
      return true;
     }

    return false;
   }

  private boolean dtMergeBranchesAtTop(Stuck Parent)                            // Merge the top most two child branches of a branch that is not the root
   {final Stuck   p = Parent;                                                   // Parent stuck
    if (p.stuckNumber == 0)   return false;                                     // Cannot perform this merge on the root
    final int sz = p.stuckSize();                                               // Index of left branch known to be valid as the parent contains at least one entry resulting in two children
    if (sz == 0)              return false;                                     // Cannot perform this merge on an empty stuck
    if (dtSkipMerge(p, sz-1)) return false;

    final int il = p.stuckGetData(p.stuckLastKey());                            // Get the btree index of the left branch branch
    final int ir = p.stuckTop();                                                // Get the btree index of the right branch branch

    final Stuck l = dtStuckGet(il);                                             // Load left  branch from btree
    final Stuck r = dtStuckGet(ir);                                             // Load right branch from btree

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Both children must be branches

    p.stuckPop();
    l.stuckMergeButOne(p.stuckKey, r);                                          // Merge leaves into left child
    if (l.stuckMergeSuccess)                                                    // Modify the parent only if the merge succeeded
     {p.stuckSetTop(il);                                                        // Make newly combined left branch top most
      l.stuckPutIntoMemory();                                                   // Save the modified left child back into the tree
      p.stuckPutIntoMemory();                                                   // Save the modified root back into the tree
      dtFree(r);                                                                // Free right branch as it is no longer in use
      return true;
     }

    return false;
   }

//D1 Find                                                                       // Find a key in a double btree

  class Find                                                                    // Find the leaf stuck associated with a key in the double btree
   {final int findKey;
    boolean   findFound;                                                        // Whether the key being searched for was found
    int       findIndex;                                                        // Index of the stuck in which the key was found or would have been found if it was in the double tree
    int       findData;                                                         // Data asoociated with the key if the key was found

    Find(int Key)                                                               // Find the specified key
     {findKey = Key;
      int p = 0;                                                                // Start at the root

      for (int i = 0; i < dtMaxTreeDepth; i++)                                  // Step down through the tree
       {final Stuck s = dtStuckGet(p);                                          // Load current stuck
        if (s.stuckIsLeaf())                                                    // Arrived at a leaf
         {findIndex = p;                                                        // Index of the stuck that should contain this leaf
          findFound = s.stuckHas(Key);                                          // At a leaf - search for exact match
          if (findFound) findData = s.stuckGetData(Key);
          return;
         }
        final Integer k = s.stuckGe(Key);                                       // Find the smallest key that is gretaert than or equal to the search key
        p = k == null ? s.stuckTop() : s.stuckGetData(k);                      // Step to top if no key in the stuck is greater then or equal to the search key, else to the stuck indexed by the smallest key greater than or equal to the search key
       }
      stop("Fallen off the end of the tree searching for key:", Key);           // Tree has an error in its construction which has allowed us to fall off the end without encountering a leaf
     }

    public String toString()                                                    // Print status of find result
     {final StringBuilder s = new StringBuilder();
      s.append("Key  : "+findKey  +"\n");                                       // Key being searched for
      s.append("Found: "+findFound+"\n");                                       // Whether the key being searched for was found
      s.append("Data : "+findData +"\n");                                       // Data associated with the key if the key was found in the double tree
      s.append("Index: "+findIndex+"\n");                                       // Index of the stuck in which the key was found or would have been found if it was in the double tree
      return ""+s;
     }
   } // Find

//D1 Insertion                                                                  // Insert a key, data pair into the double btree if there is room for it or update and existing key with a new datum

  class FindAndInsert extends Find                                              // Find the leaf stuck that should contain this key and insert or update it if possible
   {final int fiaKey;                                                           // The key to be added
    final int fiaData;                                                          // The data to be added
    boolean   fiaUpdated;                                                       // The key was updated
    boolean   fiaInserted;                                                      // The key was inserted
    boolean   fiaSuccess;                                                       // The key was inserted or updated so taht teh data has been successfully stored under the key

    FindAndInsert(int Key, int Data)                                            // Find the leaf stuck that should contain this key and insert or update it if possible
     {super(Key);                                                               // Find the leaf that should contain the key and possibly the key.
      fiaKey = Key; fiaData = Data;                                             // Save key and data to be added
      final Stuck l = dtStuckGet(findIndex);                                    // The stuck that should contain the key

      if (findFound)                                                            // Found the key in the leaf so update it with the new data
       {l.stuckPut(Key, Data);
        fiaUpdated  = true;
        fiaSuccess  = true;
        return;
       }

      if (l.stuckSize() < dtMaxStuckSize)                                       // Key does not exist in leaf but there is room to insert
       {l.stuckPut(Key, Data);
        fiaInserted = true;
        fiaSuccess  = true;
        return;
       }
      fiaInserted  = false;                                                     // Not inserted or updated
     }

    public String toString()                                                    // Print status of find result
     {final StringBuilder s = new StringBuilder();
      s.append("Found   : "+findFound  +"\n");                                  // Whether the key being searched for was found
      s.append("Index   : "+findIndex  +"\n");                                  // Index of the stuck in which the key was found or would have been found if it was in the double tree
      s.append("Key     : "+fiaKey     +"\n");                                  // The key to be added
      s.append("Data    : "+fiaData    +"\n");                                  // The data to be added
      s.append("Updated : "+fiaUpdated +"\n");                                  // The key was updated
      s.append("Inserted: "+fiaInserted+"\n");                                  // The key was inserted
      s.append("Success : "+fiaSuccess +"\n");                                  // The key was inserted or updated so taht teh data has been successfully stored under the key
      return ""+s;
     }
   } // FindAndInsert

  public void dtPut(int Key, int Data)                                          // Insert a key, data pair into the double btree or update and existing key with a new datum
   {final FindAndInsert f = new FindAndInsert(Key, Data);                       // Find and insert

    if (f.fiaSuccess) return;                                                   // Direct insertion succeeded so nothing more to do

    if (f.findIndex == 0)                                                       // Failed to insert because the root is a leaf which must be full else the operation would have succeeded
     {dtSplitLeafRoot();                                                        // Split the leaf root to make room
      new FindAndInsert(Key, Data);                                             // Splitting a leaf root will make more space in the tree so this operation will now succeed
      return;                                                                   // Direct insertion succeeded so nothing more to do
     }

    Stuck S = dtStuckGet(0);                                                    // Start at the root now known to be a split branch
    if (S.stuckSize() >= dtMaxStuckSize-1) dtSplitBranchRoot();                 // Split the root if it full - we already know it is a branch

    for (int i = 0; i < dtMaxTreeDepth; i++)                                    // Step down through the tree splitting full branches as we go
     {final Integer k = S.stuckGe(Key);                                         // Known to be a not full branch
      final Stuck s = dtStuckGet(k == null ? S.stuckTop() : S.stuckGetData(k)); // Step down
      if (s.stuckIsLeaf())                                                      // Stepped to a leaf that is known to be full otherwise the first find and insert would have worked
       {if (k == null) dtSplitLeafAtTop(S); else dtSplitLeafNotTop(S, k);       // Split the leaf known to be full
        new FindAndInsert(Key, Data);                                           // Find and insert will work now because the target leaf is no longer full having been split
        if (!dtSuppressMerge) dtMerge(Key);                                     // Merge along path to key unless merge has not yet been written
        return;
       }

      if (s.stuckSize() >= dtMaxStuckSize-1)                                    // Child is known to be a branch. Check size of branch
       {if (k == null) dtSplitBranchAtTop(S); else dtSplitBranchNotTop(S, k);   // Split the branch if it is full
        continue;                                                               // Restart descent to make sure we are on the right path
       }
//    S = dtStuckGet(s.stuckNumber);                                            // Step down to branch now known not to be full
      S = s;                                                                    // Step down to branch now known not to be full
     }
   }

  public void dtMerge(int Key)                                                  // Merge stucks on either side of the path to the key
   {Stuck S = dtStuckGet(0);                                                    // Load current stuck
    if (S.stuckIsLeaf()) return;                                                // Root is a leaf - nothing to merge
    if (dtMergeLeavesIntoRoot()) return;                                        // Try merging leaves into root

    dtMergeBranchesIntoRoot();                                                  // Try merging branches into root

    for (int i = 0; i < dtMaxTreeDepth; i++)                                    // Step down through tree
     {if (S.stuckSize() == 0) continue;                                         // Nothing to merge
      dtMergeLeavesAtTop  (S);                                                  // Try merging leaves at top into parent -  this forces non top siblings into top
      dtMergeBranchesAtTop(S);                                                  // Try merging branches at top into parent -  this forces non top siblings into top

      if (S.stuckHas(Key))                                                      // The stuck contains the key.  We try merging on the left, right and in the middle
       {if (Key != S.stuckLastKey())                                            // Try merge on right
         {final int k = S.stuckNextKey(Key);
          dtMergeLeavesNotTop  (S, k);
          dtMergeBranchesNotTop(S, k);
         }
        if (S.stuckSize() > 0 && Key != S.stuckFirstKey())                      // Try merge on left
         {final int k = S.stuckPrevKey(Key);
          dtMergeLeavesNotTop  (S, k);
          dtMergeBranchesNotTop(S, k);
         }
        dtMergeLeavesNotTop  (S, Key);                                          // Try merge with current key
        dtMergeBranchesNotTop(S, Key);
        continue;
       }

      if (S.stuckSize() > 0 && Key > S.stuckFirstKey())                         // In the body but no matching key
       {final int k = S.stuckPrevKey(Key);                                      // Merge previous child
        dtMergeLeavesNotTop  (S, k);
        dtMergeBranchesNotTop(S, k);
        if (S.stuckSize() > 0 && k > S.stuckFirstKey())                         // Merge second previous child
         {final int K = S.stuckPrevKey(k);
          dtMergeLeavesNotTop  (S, K);
          dtMergeBranchesNotTop(S, K);
         }
       }
      if (S.stuckSize() > 0 && Key < S.stuckLastKey())                          // In the body but no matching key
       {final int k = S.stuckNextKey(Key);                                      // Merge next child
        dtMergeLeavesNotTop  (S, k);
        dtMergeBranchesNotTop(S, k);
        if (S.stuckSize() > 0 && k < S.stuckLastKey())                          // Merge second next child
         {final int K = S.stuckNextKey(k);
          dtMergeLeavesNotTop  (S, K);
          dtMergeBranchesNotTop(S, K);
         }
       }

      final Integer k = S.stuckGe(Key);                                         // Known to be a branch
      final Stuck s = dtStuckGet(k == null ? S.stuckTop() : S.stuckGetData(k)); // Step down
      if (s.stuckIsLeaf()) return;                                              // Stepped to a leaf so merge is finished
      S = s;                                                                    // Continue to next branch
     }
   }

//D1 Deletion                                                                   // Delete a key data pair from the btree returning the data associated with the key

  private Find delete(int Key)                                                  // Find the stuckLeaf that contains this key and delete it
   {return null;
   }

//D1 Tests                                                                      // Test the btree

  final static int[]random_32 = {12, 3, 27, 1, 23, 20, 8, 18, 2, 31, 25, 16, 13, 32, 11, 21, 5, 24, 4, 10, 26, 30, 9, 6, 29, 17, 28, 15, 14, 19, 7, 22};

  static void test_leafRoot()
   {sayCurrentTestName();
    final Dt    D = new Dt(4, 4, 8, 8);

    final Stuck r = D.dtStuckGet(0);
                r.stuckSetBranch();
    final Stuck a = D.dtAllocateLeaf();
    final Stuck b = D.dtAllocateLeaf();

    a.stuckPush(1, 1); a.stuckPush(2, 2);
    b.stuckPush(3, 3); b.stuckPush(4, 4);

    r.stuckPush(2, a.stuckNumber); r.stuckSetTop(b.stuckNumber);
    //stop(D);
    ok (D, """
      2      |
      0      |
      1      |
      2      |
1,2=1  3,4=2 |
""");

    D.dtMergeLeavesIntoRoot();
    //stop(D);
    ok (D, """
1,2,3,4=0 |
""");

    D.dtSplitLeafRoot();
    //stop(D);
    ok(D, """
      2      |
      0      |
      2      |
      1      |
1,2=2  3,4=1 |
""");
   }

  static void test_leavesNotTop()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateLeaf();
    final Stuck b = D.dtAllocateLeaf();
    final Stuck c = D.dtAllocateLeaf();

    a.stuckPush(1, 1); a.stuckPush(2, 2);
    b.stuckPush(3, 3); b.stuckPush(4, 4);
    c.stuckPush(5, 5); c.stuckPush(6, 6);

    s.stuckPush(2, a.stuckNumber);
    s.stuckPush(4, b.stuckNumber);
    s.stuckSetTop( c.stuckNumber);

    //stop(D);
    ok(D, """
      2      4        |
      0      0.1      |
      1      2        |
             3        |
1,2=1  3,4=2    5,6=3 |
""");

    final boolean m = D.dtMergeLeavesNotTop(s, s.stuckFirstKey());
    //stop(D);
    ok(D, """
          4      |
          0      |
          1      |
          3      |
1,2,3,4=1  5,6=3 |
""");
    ok(m, true);

    D.dtSplitLeafNotTop(s, s.stuckFirstKey());
    //stop(D);
    ok(D, """
      2      4        |
      0      0.1      |
      2      1        |
             3        |
1,2=2  3,4=1    5,6=3 |
""");
   }

  static void test_leafAtTop()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateLeaf();
    final Stuck b = D.dtAllocateLeaf();
    final Stuck c = D.dtAllocateLeaf();

    a.stuckPush(1, 1); a.stuckPush(2, 2);
    b.stuckPush(3, 3); b.stuckPush(4, 4);
    c.stuckPush(5, 5); c.stuckPush(6, 6);

    s.stuckPush(2, a.stuckNumber);
    s.stuckPush(4, b.stuckNumber);
    s.stuckSetTop( c.stuckNumber);

    //stop(D);
    ok(D, """
      2      4        |
      0      0.1      |
      1      2        |
             3        |
1,2=1  3,4=2    5,6=3 |
""");

    final boolean m = D.dtMergeLeavesAtTop(s);
    //stop(D);
    ok(D, """
      2          |
      0          |
      1          |
      2          |
1,2=1  3,4,5,6=2 |
""");
    ok(m, true);

    D.dtSplitLeafAtTop(s);
    //stop(D);
    ok(D, """
      2      4        |
      0      0.1      |
      1      3        |
             2        |
1,2=1  3,4=3    5,6=2 |
""");
   }

  static void test_branchRoot()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateBranch();
    final Stuck b = D.dtAllocateBranch();
    final Stuck c = D.dtAllocateBranch();

    final Stuck a1 = D.dtAllocateLeaf();
    final Stuck a2 = D.dtAllocateLeaf();

    final Stuck b1 = D.dtAllocateLeaf();
    final Stuck b2 = D.dtAllocateLeaf();

    a.stuckPush(100, a1.stuckNumber); a.stuckSetTop(a2.stuckNumber);
    b.stuckPush(300, b1.stuckNumber); b.stuckSetTop(b2.stuckNumber);

    a1.stuckPush( 91,  91); a1.stuckPush( 92,  92); a1.stuckPush( 93,  93); a1.stuckPush( 94,  94);
    a2.stuckPush(111, 111); a2.stuckPush(112, 112); a2.stuckPush(113, 113); a2.stuckPush(114, 114);

    b1.stuckPush(291, 291); b1.stuckPush(292, 292); b1.stuckPush(293, 293); b1.stuckPush(294, 294);
    b2.stuckPush(311, 311); b2.stuckPush(312, 312); b2.stuckPush(313, 313); b2.stuckPush(314, 314);

    s.stuckPush(200, a.stuckNumber);
    s.stuckSetTop(   b.stuckNumber);
    //stop(D);
    ok(D, """
                                   200                                       |
                                   0                                         |
                                   1                                         |
                                   2                                         |
              100                                       300                  |
              1                                         2                    |
              4                                         6                    |
              5                                         7                    |
91,92,93,94=4    111,112,113,114=5    291,292,293,294=6    311,312,313,314=7 |
""");

    final boolean m = D.dtMergeBranchesIntoRoot();
    //stop(D);
    ok(D, """
              100                  200                  300                  |
              0                    0.1                  0.2                  |
              4                    5                    6                    |
                                                        7                    |
91,92,93,94=4    111,112,113,114=5    291,292,293,294=6    311,312,313,314=7 |
""");
    ok(m, true);

    D.dtSplitBranchRoot();
    //stop(D);
    ok(D, """
                                   200                                       |
                                   0                                         |
                                   2                                         |
                                   1                                         |
              100                                       300                  |
              2                                         1                    |
              4                                         6                    |
              5                                         7                    |
91,92,93,94=4    111,112,113,114=5    291,292,293,294=6    311,312,313,314=7 |
""");
   }

  static void test_branchesNotTop()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateBranch();
    final Stuck b = D.dtAllocateBranch();
    final Stuck c = D.dtAllocateBranch();

    final Stuck a1 = D.dtAllocateLeaf();
    final Stuck a2 = D.dtAllocateLeaf();

    final Stuck b1 = D.dtAllocateLeaf();
    final Stuck b2 = D.dtAllocateLeaf();
    final Stuck b3 = D.dtAllocateLeaf();

    final Stuck c1 = D.dtAllocateLeaf();
    final Stuck c2 = D.dtAllocateLeaf();
    final Stuck c3 = D.dtAllocateLeaf();

    a.stuckPush(100, a1.stuckNumber); a.stuckSetTop(a2.stuckNumber);
    b.stuckPush(300, b1.stuckNumber); b.stuckSetTop(b2.stuckNumber);
    c.stuckPush(500, c1.stuckNumber); c.stuckSetTop(c2.stuckNumber);

    a1.stuckPush( 11,  11); a1.stuckPush( 12,  12); a1.stuckPush( 13,  13); a1.stuckPush( 14,  14);
    a2.stuckPush(121, 121); a2.stuckPush(122, 122); a2.stuckPush(123, 123); a2.stuckPush(124, 124);

    b1.stuckPush(291, 291); b1.stuckPush(292, 292); b1.stuckPush(293, 293); b1.stuckPush(294, 294);
    b2.stuckPush(321, 321); b2.stuckPush(322, 322); b2.stuckPush(323, 323); b2.stuckPush(324, 324);
    b3.stuckPush(411, 411); b3.stuckPush(412, 412); b3.stuckPush(413, 413); b3.stuckPush(414, 414);

    c1.stuckPush(491, 491); c1.stuckPush(492, 492); c1.stuckPush(493, 493); c1.stuckPush(494, 494);
    c2.stuckPush(511, 511); c2.stuckPush(512, 512); c2.stuckPush(513, 513); c2.stuckPush(514, 514);
    c3.stuckPush(611, 611); c3.stuckPush(612, 612); c3.stuckPush(613, 613); c3.stuckPush(614, 614);

    s.stuckPush(200, a.stuckNumber);
    s.stuckPush(400, b.stuckNumber);
    s.stuckSetTop(   c.stuckNumber);
    //stop(D);
    ok(D, """
                                   200                                       400                                        |
                                   0                                         0.1                                        |
                                   1                                         2                                          |
                                                                             3                                          |
              100                                       300                                       500                   |
              1                                         2                                         3                     |
              4                                         6                                         9                     |
              5                                         7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");

    final boolean m = D.dtMergeBranchesNotTop(s, 200);
    //stop(D);
    ok(D, """
                                                                             400                                        |
                                                                             0                                          |
                                                                             1                                          |
                                                                             3                                          |
              100                  200                  300                                       500                   |
              1                    1.1                  1.2                                       3                     |
              4                    5                    6                                         9                     |
                                                        7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");
    ok(m, true);

    D.dtSplitBranchNotTop(s, 400);
    //stop(D);
    ok(D, """
                                   200                                       400                                        |
                                   0                                         0.1                                        |
                                   2                                         1                                          |
                                                                             3                                          |
              100                                       300                                       500                   |
              2                                         1                                         3                     |
              4                                         6                                         9                     |
              5                                         7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");
   }

  static void test_branchesAtTop()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateBranch();
    final Stuck b = D.dtAllocateBranch();
    final Stuck c = D.dtAllocateBranch();

    final Stuck a1 = D.dtAllocateLeaf();
    final Stuck a2 = D.dtAllocateLeaf();

    final Stuck b1 = D.dtAllocateLeaf();
    final Stuck b2 = D.dtAllocateLeaf();
    final Stuck b3 = D.dtAllocateLeaf();

    final Stuck c1 = D.dtAllocateLeaf();
    final Stuck c2 = D.dtAllocateLeaf();
    final Stuck c3 = D.dtAllocateLeaf();

    a.stuckPush(100, a1.stuckNumber); a.stuckSetTop(a2.stuckNumber);
    b.stuckPush(300, b1.stuckNumber); b.stuckSetTop(b2.stuckNumber);
    c.stuckPush(500, c1.stuckNumber); c.stuckSetTop(c2.stuckNumber);

    a1.stuckPush( 11,  11); a1.stuckPush( 12,  12); a1.stuckPush( 13,  13); a1.stuckPush( 14,  14);
    a2.stuckPush(121, 121); a2.stuckPush(122, 122); a2.stuckPush(123, 123); a2.stuckPush(124, 124);

    b1.stuckPush(291, 291); b1.stuckPush(292, 292); b1.stuckPush(293, 293); b1.stuckPush(294, 294);
    b2.stuckPush(321, 321); b2.stuckPush(322, 322); b2.stuckPush(323, 323); b2.stuckPush(324, 324);
    b3.stuckPush(411, 411); b3.stuckPush(412, 412); b3.stuckPush(413, 413); b3.stuckPush(414, 414);

    c1.stuckPush(491, 491); c1.stuckPush(492, 492); c1.stuckPush(493, 493); c1.stuckPush(494, 494);
    c2.stuckPush(511, 511); c2.stuckPush(512, 512); c2.stuckPush(513, 513); c2.stuckPush(514, 514);
    c3.stuckPush(611, 611); c3.stuckPush(612, 612); c3.stuckPush(613, 613); c3.stuckPush(614, 614);

    s.stuckPush(200, a.stuckNumber);
    s.stuckPush(400, b.stuckNumber);
    s.stuckSetTop(   c.stuckNumber);
    //stop(D);
    ok(D, """
                                   200                                       400                                        |
                                   0                                         0.1                                        |
                                   1                                         2                                          |
                                                                             3                                          |
              100                                       300                                       500                   |
              1                                         2                                         3                     |
              4                                         6                                         9                     |
              5                                         7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");

    final boolean m = D.dtMergeBranchesAtTop(s);
    //stop(D);
    ok(D, """
                                   200                                                                                  |
                                   0                                                                                    |
                                   1                                                                                    |
                                   2                                                                                    |
              100                                       300                  400                  500                   |
              1                                         2                    2.1                  2.2                   |
              4                                         6                    7                    9                     |
              5                                                                                   10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");
    ok(m, true);

    D.dtSplitBranchAtTop(s);
    //stop(D);
    ok(D, """
                                   200                                       400                                        |
                                   0                                         0.1                                        |
                                   1                                         3                                          |
                                                                             2                                          |
              100                                       300                                       500                   |
              1                                         3                                         2                     |
              4                                         6                                         9                     |
              5                                         7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");

   }

  static void test_find()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final Stuck s = D.dtStuckGet(0);
                s.stuckSetBranch();
    final Stuck a = D.dtAllocateBranch();
    final Stuck b = D.dtAllocateBranch();
    final Stuck c = D.dtAllocateBranch();

    final Stuck a1 = D.dtAllocateLeaf();
    final Stuck a2 = D.dtAllocateLeaf();

    final Stuck b1 = D.dtAllocateLeaf();
    final Stuck b2 = D.dtAllocateLeaf();
    final Stuck b3 = D.dtAllocateLeaf();

    final Stuck c1 = D.dtAllocateLeaf();
    final Stuck c2 = D.dtAllocateLeaf();
    final Stuck c3 = D.dtAllocateLeaf();

    a.stuckPush(100, a1.stuckNumber); a.stuckSetTop(a2.stuckNumber);
    b.stuckPush(300, b1.stuckNumber); b.stuckSetTop(b2.stuckNumber);
    c.stuckPush(500, c1.stuckNumber); c.stuckSetTop(c2.stuckNumber);

    a1.stuckPush( 11,  11); a1.stuckPush( 12,  12); a1.stuckPush( 13,  13); a1.stuckPush( 14,  14);
    a2.stuckPush(121, 121); a2.stuckPush(122, 122); a2.stuckPush(123, 123); a2.stuckPush(124, 124);

    b1.stuckPush(291, 291); b1.stuckPush(292, 292); b1.stuckPush(293, 293); b1.stuckPush(294, 294);
    b2.stuckPush(321, 321); b2.stuckPush(322, 322); b2.stuckPush(323, 323); b2.stuckPush(324, 324);
    b3.stuckPush(411, 411); b3.stuckPush(412, 412); b3.stuckPush(413, 413); b3.stuckPush(414, 414);

    c1.stuckPush(491, 491); c1.stuckPush(492, 492); c1.stuckPush(493, 493); c1.stuckPush(494, 494);
    c2.stuckPush(511, 511); c2.stuckPush(512, 512); c2.stuckPush(513, 513); c2.stuckPush(514, 514);
    c3.stuckPush(611, 611); c3.stuckPush(612, 612); c3.stuckPush(613, 613); c3.stuckPush(614, 614);

    s.stuckPush(200, a.stuckNumber);
    s.stuckPush(400, b.stuckNumber);
    s.stuckSetTop(   c.stuckNumber);
    //stop(D);
    ok(D, """
                                   200                                       400                                        |
                                   0                                         0.1                                        |
                                   1                                         2                                          |
                                                                             3                                          |
              100                                       300                                       500                   |
              1                                         2                                         3                     |
              4                                         6                                         9                     |
              5                                         7                                         10                    |
11,12,13,14=4    121,122,123,124=5    291,292,293,294=6    321,322,323,324=7    491,492,493,494=9    511,512,513,514=10 |
""");

    final Find f = D.new Find(322);
    //stop(f);
    ok(f, """
Key  : 322
Found: true
Data : 322
Index: 7
""");

    final Find n = D.new Find(290);
    //stop(n);
    ok(n, """
Key  : 290
Found: false
Data : 0
Index: 6
""");
   }

  static void test_findAndInsert()
   {sayCurrentTestName();
    final Dt    D = new Dt(32, 4, 8, 8);

    final FindAndInsert f1 = D.new FindAndInsert(1, 1+1);
    final FindAndInsert f2 = D.new FindAndInsert(2, 2+2);
    final FindAndInsert f3 = D.new FindAndInsert(3, 3+3);
    final FindAndInsert f4 = D.new FindAndInsert(4, 4+4);
    final FindAndInsert f5 = D.new FindAndInsert(5, 5+5);

    //say(f1);
    //say(f5);
    //stop(D);
    ok(f1, """
Found   : false
Index   : 0
Key     : 1
Data    : 2
Updated : false
Inserted: true
Success : true
""");
    ok(f5, """
Found   : false
Index   : 0
Key     : 5
Data    : 10
Updated : false
Inserted: false
Success : false
""");
    ok(D, """
1,2,3,4=0 |
""");
   }

  static void test_put_ascending()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4, 8, 8);
    D.dtSuppressMerge = true;                                                   // I have not written merge yet.

    for (int i = 0; i < N; i++) D.dtPut(i, i+1);
    //stop(D);
    ok(D, """
                            7                                        15                                                                                    |
                            0                                        0.1                                                                                   |
                            14                                       22                                                                                    |
                                                                     15                                                                                    |
             3                                 11                                           19                    23                                       |
             14                                22                                           15                    15.1                                     |
             5                                 12                                           20                    24                                       |
             9                                 17                                                                 6                                        |
      1              5              9                     13                     17                    21                      25         27               |
      5              9              12                    17                     20                    24                      6          6.1              |
      1              4              8                     11                     16                    19                      23         25               |
      3              7              10                    13                     18                    21                                 2                |
0,1=1  2,3=3   4,5=4  6,7=7   8,9=8   10,11=10   12,13=11   14,15=13    16,17=16   18,19=18   20,21=19   22,23=21     24,25=23   26,27=25    28,29,30,31=2 |
""");
   }

  static void test_put_merge()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4, 8, 8);

    for (int i = 1; i <= N; i++)
     {D.dtPut(i, i+1);
     }
    //stop(D);
    ok(D, """
                                                      16                               24                               |
                                                      0                                0.1                              |
                                                      5                                11                               |
                                                                                       6                                |
          4          7               12                               20                                28              |
          5          5.1             5.2                              11                                6               |
          1          3               4                                8                                 9               |
                                     7                                10                                2               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=9   29,30,31,32=2 |
""");
   }

  static void test_put_descending()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4, 8, 8);

    for (int i = N; i > 0; i--) D.dtPut(i, i+1);
    //stop(D);
    ok(D, """
                                    10                                                                27                         |
                                    0                                                                 0.1                        |
                                    12                                                                9                          |
                                                                                                      6                          |
           4        6                               14              18               22                                30        |
           12       12.1                            9               9.1              9.2                               6         |
           11       13                              8               7                4                                 3         |
                    10                                                               1                                 2         |
1,2,3,4=11   5,6=13     7,8,9,10=10   11,12,13,14=8   15,16,17,18=7    19,20,21,22=4    23,24,25,26=1    27,28,29,30=3   31,32=2 |
""");
   }

  static void test_put_random()
   {sayCurrentTestName();
    final int N = random_32.length;
    final Dt  D = new Dt(N, 4, 8, 8);

    for (int i = 0; i < N; ++i) D.dtPut(random_32[i], i);
    //stop(D);
    ok(D, """
                                                      16                                                  |
                                                      0                                                   |
                                                      16                                                  |
                                                      17                                                  |
                           7                                                   19                         |
                           16                                                  17                         |
                           14                                                  18                         |
                           5                                                   6                          |
      2        4                         11                      16                             30        |
      14       14.1                      5                       18                             6         |
      8        15                        9                       13                             10        |
               1                         4                       12                             2         |
1,2=8   3,4=15     5,6,7=1   8,9,10,11=9   12,13,14=4   15,16=13   17,18,19=12   27,28,29,30=10   31,32=2 |
""");
   }

/*
  static void oldTests()                                                        // Tests thought to be in good shape
   {if (createTestTrees)
     {say("Create test trees");
      test_put_merge();
      return;
     }

    test_copy();

    test_merge_two();
    test_create1();
    test_create2();
    test_push_and_check();
    test_clear();
    test_pop();
    test_pop_4();
    test_firstLastPast();
    test_elementAt();
    test_setElementAt();
    test_setPastLastElement();
    test_insertElementAt();
    test_removeElementAt();
    test_search_eq();
    test_search_eq_partial();
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
    test_dtSplitLeafRoot();
    test_leafRoot();
    test_leavesNotTop();
    test_leafAtTop();
    test_branchRoot();
    test_branchesNotTop();
    test_branchesAtTop();
    test_find();
    test_findAndInsert();
    test_delete_ascending();
    test_delete_random();
    test_delete_descending();
    test_delete_random_descending();
    test_put_ascending();
    test_put_merge();
    test_put_reload_and_check();
    test_put_descending();
    test_put_random();
    test_verilog_delete();
    test_verilog_find();
    test_verilog_put();
   }
*/
  static void newTests()                                                        // Tests being worked on
   {//oldTests();
    //test_leafRoot();
    //test_leavesNotTop();
    //test_leafAtTop();
    //test_branchRoot();
    //test_branchesNotTop();
    //test_branchesAtTop();
    //test_find();
    //test_findAndInsert();
    //test_put_ascending();
    //test_put_merge();
    //test_put_descending();
    //test_put_random();
    test_delete_ascending();
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
