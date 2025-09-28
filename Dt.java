//------------------------------------------------------------------------------
// A double btree - a Btree made of Btrees
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Double Btree

import java.util.*;

class Dt extends Chip                                                           // A double btree - a double btree made of btrees
 {final int dtSize;                                                             // The maximum number of nodes == branches or leaves in the double btree
  final int dtMaxStuckSize;                                                     // The maximum number of entries in each stuck.
  final int dtMaxTreeDepth        = 99;                                         // Maximum tree height
  final int dtLinesToPrintABranch =  4;                                         // The number of lines required to print a branch
  final int dtMaxPrintLevels      =  3;                                         // The maximum number of levels to print stuck - this avoids endless print loops when something goes wrong

  final Stuck[]dtStucks;                                                        // The stucks used to construct the double btree
  int          dtUsed;                                                          // Number of stucks used by allocation

  boolean              dtSuppressMerge    = false;                              // Suppress merges during put to allow merge steps to be tested individually.  If this is on the double btrees built for testing are already merged so there is nothing to test.
  static       boolean dtDebug            = false;                              // Debug if enabled
  final static boolean dtCoverageAnalysis = false;                              // Enables coverage checks

//D1 Construction                                                               // Construct and layout the double btree

  Dt(int Size, int MaxStuckSize)                                                // Create the double btree
   {super("Dt"); N();
    if (MaxStuckSize % 2 == 1) stop("The stuck size must be even, not:", MaxStuckSize);
    if (MaxStuckSize < 4)      stop("The stuck size must be greater than equal to 4, not:", MaxStuckSize);

    dtSize         = Size;                                                      // The maximum number of entries in the double btree
    dtMaxStuckSize = MaxStuckSize;                                              // The maximum number of entries in the stuck.

    dtStucks       = new Stuck[Size];                                           // Stucks forming the double btree
    dtCreateRootStuck();                                                        // Create the free chain
   }

//D2 Allocation                                                                 // Allocate stucks from the free chain

  void dtCreateRootStuck()                                                      // Initialize the root stuck which is always at index 0 in memory
   {final Stuck r = dtStucks[0] = new Stuck(0);                                 // Stuck zero is always the root
    dtUsed        = 1;                                                          // We have used one stuck for the root
    r.stuckSetLeaf();                                                           // The root starts as a leaf
   }

  Stuck dtStuckGet(int Index) {return dtStucks[Index];}                         // Get a stuck from memory via its index

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

  private void dtFree(Stuck S)                                                  // Free a stuck and put it on the free chain to make it available for reuse
   {final Stuck r = dtStucks[0];
    S.stuckFreeNext = r.stuckFreeNext;
    r.stuckFreeNext = S.stuckNumber;
    S.stuckSetFree(true);
   }

  private Stuck dtAllocateLeaf  () {return dtAllocate(true);}                   // Allocate a stuck as a leaf
  private Stuck dtAllocateBranch() {return dtAllocate(false);}                  // Allocate a stuck as a branch

//D2 Stuck                                                                      // Get and set stucks within the double btree

  class Stuck                                                                   // A reference to a stuck in the memory of the double btree plus a copy of its size, keys and data
   {final   int     stuckNumber;                                                // Index of this stuck in memory
    private final   TreeMap<Integer,Integer> stuckMap = new TreeMap<>();        // Keys to data map. This simulates the use of a btree much more efficiently than using the Java/Verilog version.  This allows us to see if a double btree can in fact be constructed by using a btree at each node - or - what additions are needed to the btree code to make such a construction possible.
    private int     stuckTop;                                                   // Every branch has a top data element, no leaf does, that is its tragedy
    private int     stuckFreeNext;                                              // Next element on the free chain
    private boolean stuckLeaf;                                                  // Whether this is a stuckLeaf or a branch
    private boolean stuckFree;                                                  // Whether this stuck is free or in use
    boolean         stuckFound;                                                 // Whether the key was found on the last search of the stuck
    int             stuckKey;                                                   // Last key used to search the stuck
    int             stuckFoundKey;                                              // Key found on the last search for a key greater than or equal to a specified key was requested
    int             stuckData;                                                  // Data associated with the last search
    int             stuckIndex;                                                 // Index of stuck in double btree in which the last key searched with should reside

    Stuck(int Index)                                                            // Create a stuck recording its position in memory
     {stuckNumber = Index;
     }

    int     stuckSize     ()              {return stuckMap.size();}             // Size of a stuck not counting the top element in branches
    int     stuckFirstKey ()              {return stuckMap.firstKey();}         // First key in a stuck
    int     stuckPrevKey  (int k)         {return stuckMap.lowerKey(k);}        // Previous key to the specified key in the stuck
    int     stuckNextKey  (int k)         {return stuckMap.higherKey(k);}       // Next key to the specified key in the stuck
    int     stuckLastKey  ()              {return stuckMap.lastKey();}          // Last key in the stuck
    int     stuckTop      ()              {return stuckTop;}                    // The top element of a branch which locates the child leaf or branch that contains all the entries with a key greater then any key in the stuck
    void    stuckSetTop   (int d)         {stuckTop = d;}                       // Set the top element
    void    stuckSetTop   (Stuck d)       {stuckTop = d.stuckNumber;}           // Set the top element to reference a specified stuck
    boolean stuckHas      (int k)         {return stuckMap.containsKey(k);}     // Whether the stuck contains the specified key
    Integer stuckGe       (int k)         {return stuckMap.ceilingKey(k);}      // If the stuck contains this key, then this key, else the next key in the stuck
    int     stuckGetData  (int k)         {return stuckMap.get(k);}             // The data held in the stuck under the specified key
    void    stuckPut      (int k, int   d){stuckMap.put(k, d);}                 // Put a key, data pair into the stuck
    void    stuckPut      (int k, Stuck d){stuckMap.put(k, d.stuckNumber);}     // Put a key and a stuck reference in to a stuck
    void    stuckPut      (Stuck Source)  {stuckMap.putAll(Source.stuckMap);}   // Put all the key, data pairs in the specified stuck into this stuck
    int     stuckRemove   (int k)         {final int d = stuckGetData(k);       // Remove a key from the stuck and return its associated data
                                           stuckMap.remove(k); return d;}
    boolean stuckIsLeaf   ()              {return  stuckLeaf;};                 // Whether this stuck represents a leaf
    boolean stuckIsBranch ()              {return !stuckLeaf;};                 // Whether this stuck represents a branch
    void    stuckSetLeaf  ()              {stuckLeaf = true;}                   // Set this stuck to represent a leaf
    void    stuckSetBranch()              {stuckLeaf = false;}                  // Set this stuck to represent a branch
    void    stuckClear    ()              {stuckMap.clear(); stuckTop = 0;}     // Clear this stuck
    void    stuckSetFree  (boolean Free)  {stuckFree = Free;}                   // Mark this stuck as free or in use

//D3 Memory                                                                     // Get a stuck from memory or return it to memory

    Stuck stuckGetFromMemory(int Index)                                         // Copy an indexed stuck out of memory
     {return dtStucks[Index];
     }

    void stuckGetFromMemoryRoot()                                               // Copy the root stuck out of memory
     {stuckGetFromMemory(0);
     }

    void stuckPutIntoMemory()                                                   // Update a stuck in memory
     {
     }

//D3 Print                                                                      // Print the stuck

    public String toString()
     {final StringBuilder S = new StringBuilder();
      S.append("Stuck: "+stuckNumber+", size: "+stuckSize()+", leaf: "+(stuckIsLeaf() ? 1 : 0)+", top: "+stuckTop+"\n");
      for(Integer k : stuckMap.keySet())
       {S.append(String.format("  %2d: %2d\n", k, stuckMap.get(k)));
       }
      final boolean f = stuckFound   ; S.append("Found     : "+(f ? 1:0)+"\n"); // Whether the key was found
      final int     k = stuckKey     ; S.append("Key       : "+k+"\n");         // Data associated with the key if found
      final int     K = stuckFoundKey; S.append("FoundKey  : "+K+"\n");         // Data associated with the key if found
      final int     d = stuckData    ; S.append("Data      : "+d+"\n");         // Data associated with the key if found
      final int     s = stuckIndex   ; S.append("StuckIndex: "+s+"\n");         // Index of stuck in the double btree in which the key should reside

      return ""+S;
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

    void stuckFirstElement()                                                    // Get the first key, data pair
     {stuckKey = stuckFirstKey();
      stuckData = stuckMap.get(stuckKey);
     }

    void stuckLastElement()                                                     // Get the last key, data pair
     {stuckKey = stuckLastKey();
      stuckData = stuckMap.get(stuckKey);
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

//D3 Split                                                                      // Split stucks in many and various ways

    void stuckSplitIntoTwo(Stuck Left, Stuck Right)                             // Copy the first half of the key, data pairs into the left stuck, the remainder into the right stuck.  The original source stuck is not modified
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

    int stuckSplitIntoThree(Stuck Left, Stuck Right, int Copy)                  // Copy the specified number of key, data pairs into the left stuck. Place the data of the  next pair as the top of the left stuck.  Return the key of this pair after copying the remainder to the right stuck.  make the top of the parent the top of the right stuck.
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
      return K;
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

    boolean stuckMerge(Stuck Source)                                            // Concatenate the indicated stuck onto the end of the current one
     {if (stuckSize() + Source.stuckSize() > dtMaxStuckSize) return false;
      stuckMap.putAll(Source.stuckMap);
      return true;
     }

    boolean stuckMerge(Stuck Left, Stuck Right)                                 // Replace the current stuck with the concatenation of the two stucks indicated
     {if (Left.stuckSize() + Right.stuckSize() > dtMaxStuckSize) return false;
      stuckClear();
      stuckMap.putAll(Left.stuckMap);
      stuckMap.putAll(Right.stuckMap);
      return true;
     }

    boolean stuckMergeButOne(int Key, Stuck Source)                             // Concatenate the indicated stuck with a past last data element onto the end of the current stuck with a past last data element with the specified key inserted over the central past last data element separating the two.
     {if (Source.stuckSize() + 1 + stuckSize() >= dtMaxStuckSize) return false;
      stuckPush(Key, stuckTop());
      stuckPut(Source);
      stuckSetTop(Source.stuckTop());
      return true;
     }

    boolean stuckMergeButOne(Stuck Left, int Key, Stuck Right)                  // Concatenate the past last left and right stucks separated by the key over the past last data element of the left stuck into the target
     {if (Left.stuckSize() + 1 + Right.stuckSize() >= dtMaxStuckSize) return false;
      stuckClear();
      stuckPut(Left);
      stuckPush(Key, Left.stuckTop());
      stuckPut(Right);
      stuckSetTop(Right.stuckTop());
      return true;
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
    if (dtDebug) say("SSSS 1111 leafRoot", this);
    p.stuckSplitIntoTwo(l, r);                                                  // Split the leaf root in two down the middle

    final int mk = (l.stuckLastKey() + r.stuckFirstKey()) / 2;                  // Mid key is the average of the largest left and smallest right keys
    p.stuckClear();
    p.stuckPush(mk, l);
    p.stuckSetTop(  r);
    p.stuckSetBranch();
    p.stuckPutIntoMemory();
    if (dtDebug) say("SSSS 2222 leafRoot", this);
   }

  private void dtSplitBranchRoot()                                              // Split a full root branch
   {final Stuck p = dtStuckGet(0);                                              // Load root as parent
    if (!p.stuckIsBranch()) stop("Root must be a branch");

    final Stuck l = dtAllocateBranch();                                         // Allocate left leaf
    final Stuck r = dtAllocateBranch();                                         // Allocate right leaf
    final int   m = (dtMaxStuckSize-1) / 2;
    if (p.stuckSize() < dtMaxStuckSize-1) stop("Root must be full");
    if (dtDebug) say("SSSS 1111 branchRoot", this);
    final int   K = p.stuckSplitIntoThree(l, r, m);                             // Split the branch root in two down the middle returning the central key
    l.stuckPutIntoMemory();                                                     // Save left branch
    r.stuckPutIntoMemory();                                                     // Save right branch
                                                                                // Update root with new children
    p.stuckClear();                                                             // Clear the root so we can add the left and right children to it.
    p.stuckPush(K, l);                                                          // Add reference to left child
    p.stuckSetTop(r);                                                           // Add reference to right child as top element past the end of the stuck
    p.stuckPutIntoMemory();                                                     // Save the root stuck back into the double btree whence it came
    if (dtDebug) say("SSSS 2222 branchRoot", this);
   }

  private void dtSplitLeafNotTop(Stuck Parent, int Index)                       // Split a full leaf that is not the root and is not the last child of its parent branch.  The parent branch must not be full
   {final Stuck p = Parent;
    final Stuck r = p.stuckGetFromMemory(p.stuckGetData(Index));                // The indexed child will be on the right

    if ( p.stuckIsLeaf())                     stop("Parent must be a branch");
    if ( p.stuckSize  () >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsLeaf())                     stop("Child must be a leaf");
    if ( r.stuckSize  () <  dtMaxStuckSize)   stop("Child must be full");

    final Stuck l = dtAllocateLeaf();
    if (dtDebug) say("SSSS 1111 leafNotTop", this);
    r.stuckSplitLow(l);                                                         // Split the leaf in two down the middle copying out the lower half
    r.stuckPutIntoMemory();                                                     // Allocate and save split out left of leaf
    l.stuckPutIntoMemory();                                                     // Save remainder of leaf

    final int ll = l.stuckLastKey();                                            // Last key of left child
    final int rf = r.stuckFirstKey();                                           // First key of right child
    final int mk = (ll  + rf) / 2;                                              // Mid key is average of surrounding keys
    p.stuckPut(mk, l);                                                          // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the double btree
    if (dtDebug) say("SSSS 2222 leafNotTop", this);
   }

  private void dtSplitLeafAtTop(Stuck Parent)                                   // Split a full leaf that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = Parent;
    final Stuck r = dtStuckGet(p.stuckTop());                                   // Top child stuck that is going to be split

    if (p.stuckIsLeaf ())                    stop("Parent must be a branch");
    if (p.stuckSize   () > dtMaxStuckSize-1) stop("Parent must not be full");   // Branches are one less in size than leaves but they have the top element to compensate
    if (!r.stuckIsLeaf())                    stop("Child must be a leaf");
    if ( r.stuckSize  () != dtMaxStuckSize)  stop("Child must be full");        // Leaves fill the stuck completely

    final Stuck l = dtAllocateLeaf();
    if (dtDebug) say("SSSS 1111 leafAtTop", this);
    r.stuckSplitLow(l);                                                         // Split the leaf in two down the middle copying out the lower half

    final int ll = l.stuckLastKey();                                            // Last key of left child
    final int rf = r.stuckFirstKey();                                           // First key of right child
    final int mk = (ll  + rf) / 2;                                              // Mid key is average of surrounding keys
    p.stuckPut(mk, l);                                                          // Add reference to left child
    if (dtDebug) say("SSSS 2222 leafAtTop", this);
   }

  private void dtSplitBranchNotTop(Stuck Parent, int Index)                     // Split a full branch that is not the root and is not the last child of its parent branch which is not full
   {final Stuck p = Parent;                                                     // Parent stuck containing a reference to the stuck to be split
    final Stuck r = dtStuckGet(p.stuckGetData(Index));                          // Key of child

    if (!p.stuckIsBranch())                     stop("Parent must be a branch");
    if ( p.stuckSize    () >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsBranch())                     stop("Child must be a branch");
    if ( r.stuckSize    () <  dtMaxStuckSize-1) stop("Child must not be full");

    final Stuck l = dtAllocateBranch();
    if (dtDebug) say("SSSS 1111 branchNotTop", this);
    final int Key = r.stuckSplitLowButOne(l);                                   // Split the branch into two branches and retain the splitting key
    l.stuckPutIntoMemory();                                                     // Allocate and save left branch
    r.stuckPutIntoMemory();                                                     // Save modified right branch
                                                                                // Update root with new children
    p.stuckPut(Key, l);                                                         // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the double btree
    if (dtDebug) say("SSSS 2222 branchNotTop", this);
   }

  private void dtSplitBranchAtTop(Stuck Parent)                                 // Split a full branch that is not the root and is the last child of its parent branch which is not full
   {final Stuck p = Parent;                                                     // Parent stuck
    final Stuck r = dtStuckGet(p.stuckTop());                                   // Top most child is on the right

    if (!p.stuckIsBranch())                   stop("Parent must be a branch");
    if ( p.stuckSize()   >= dtMaxStuckSize-1) stop("Parent must not be full");
    if (!r.stuckIsBranch())                   stop("Child must be a branch");
    if ( r.stuckSize()   <  dtMaxStuckSize-1) stop("Child branch must be full");

    final Stuck l = dtAllocateBranch();                                         // New left branch
    if (dtDebug) say("SSSS 1111 branchAtTop", this);
    final int  mk = r.stuckSplitLowButOne(l);                                   // Split the leaf in two down the middle copying out the lower half
    l.stuckPutIntoMemory();                                                     // Save left branch
    r.stuckPutIntoMemory();                                                     // Save modified right branch
                                                                                // Update root with new children
    p.stuckPut(mk, l);                                                          // Add reference to left child
    p.stuckPutIntoMemory();                                                     // Save the parent stuck back into the double btree
    if (dtDebug) say("SSSS 2222 branchAtTop", this);
   }

//D1 Merge                                                                      // Merge two nodes

  private boolean dtMergeLeavesIntoRoot()                                       // Merge two leaves into the root
   {final Stuck p = dtStuckGet(0);                                              // Parent stuck two of whose children might be dtMerged

    p.stuckGetFromMemoryRoot();                                                 // Load root

    if (p.stuckSize() != 1) return false;                                       // Number of entries in root

    final Stuck l = dtStuckGet(p.stuckGetData(p.stuckFirstKey()));              // Left split stuck
    final Stuck r = dtStuckGet(p.stuckTop());                                   // Right split stuck

    if (!l.stuckIsLeaf() || !r.stuckIsLeaf()) return false;                     // Check that the children are leaves
    if (dtDebug) say("MMMM LeavesIntoRoot 1111", this);

    if (!p.stuckMerge(l, r)) return false;                                      // Merge leaves into root
    p.stuckSetLeaf();                                                           // Mark the root as a stuckLeaf
    p.stuckPutIntoMemory();                                                     // Save the modified root back into the double btree
    dtFree(l); dtFree(r);                                                       // Free left and right leaves as they are no longer needed
    if (dtDebug) say("MMMM LeavesIntoRoot 2222", this);
    return true;                                                                // Success
   }

  private boolean dtMergeLeavesNotTop(Stuck Parent, int Left)                   // Merge the two consecutive leaves of a branch that is not the root. Neither of the leaves is the topmost stuckLeaf.
   {final Stuck p = Parent;
    if (!p.stuckHas(Left))        return false;                                 // Key must be in stuck
    if (p.stuckLastKey() == Left) return false;                                 // Key cannot be the last key in the body
    final int il = p.stuckGetData(Left);                                        // Index of left leaf
    final int rk = p.stuckNextKey(Left);                                        // Right leaf key
    final int ir = p.stuckGetData(rk);                                          // Indexes of right leaf

    final Stuck l = dtStuckGet(il);
    final Stuck r = dtStuckGet(ir);

    if (!l.stuckIsLeaf() || !r.stuckIsLeaf()) return false;                     // Check that the children are leaves
    if (dtDebug) say("MMMM LeavesNotTop 1111", this);

    if (!l.stuckMerge(r)) return false;                                         // Merge leaves into left child
    p.stuckRemove(Left);                                                        // Remove the left child moving the right child down into its space
    p.stuckPut(rk, il);                                                         // Replace the right child with the left child
    l.stuckPutIntoMemory();                                                     // Save the merge into left child back into the double btree
    p.stuckPutIntoMemory();                                                     // Save the parent back into the double btree
    dtFree(r);                                                                  // Free right leaf as it is no longer in use
    if (dtDebug) say("MMMM LeavesNotTop 2222", this);
    return true;
   }

  private boolean dtMergeLeavesAtTop(Stuck Parent)                              // Merge the top most two leaves of a branch that is not the root
   {final Stuck p = Parent;
    final int sz = p.stuckSize();                                               // Size of parent stuck
    if (sz == 0) return false;                                                  // Nothing to merge

    final int il = p.stuckGetData(p.stuckLastKey());
    final int ir = p.stuckTop;

    final Stuck l = dtStuckGet(il);
    final Stuck r = dtStuckGet(ir);

    if (!l.stuckIsLeaf() || !r.stuckIsLeaf()) return false;                     // Check that the children are leaves
    if (dtDebug) say("MMMM LeavesAtTop 1111", this);
    if (!l.stuckMerge(r)) return false;                                         // Merge leaves into left child
    p.stuckRemove(p.stuckLastKey());                                            // The left child is now topmost - we know this is ok because the parent has at least one entry
    p.stuckSetTop(il);
    l.stuckPutIntoMemory();                                                     // Save the modified left child back into the tree
    p.stuckPutIntoMemory();                                                     // Save the modified root back into the tree
    dtFree(r);                                                                  // Free right leaf as it is no longer in use
    if (dtDebug) say("MMMM LeavesAtTop 2222", this);
    return true;
   }

  private boolean dtMergeBranchesIntoRoot()                                     // Merge two branches into the root
   {final Stuck p = dtStuckGet(0);                                              // Root

    if (p.stuckSize() != 1) return false;                                       // One element only required in root

    final int mk = p.stuckFirstKey();                                           // Splitting key is the only key in the parent

    final Stuck l = dtStuckGet(p.stuckGetData(mk));                             // Index of left branch
    final Stuck r = dtStuckGet(p.stuckTop());                                   // Index of right branch

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Check that the children are leaves
    if (dtDebug) say("MMMM BranchesIntoRoot 1111", this);
    if (!p.stuckMergeButOne(l, mk, r))      return false;                       // Merge left branch, splitting key, right branch into root
    p.stuckPutIntoMemory();                                                     // Save the modified root back into the tree
    dtFree(l); dtFree(r);                                                       // Free left and right leaves as they are no longer needed
    if (dtDebug) say("MMMM BranchesIntoRoot 2222", this);
    return true;
   }

  private boolean dtMergeBranchesNotTop(Stuck Parent, int Left)                 // Merge the two consecutive child branches of a branch that is not the root. Neither of the child branches is the topmost stuckLeaf.
   {final Stuck   p = Parent;                                                   // Parent stuck

    if (p.stuckNumber == 0 && p.stuckSize() == 1) return false;                 // Cannot perform this merge on the root when it contains only one key, data pair.
    if (p.stuckSize() == 0)       return false;                                 // Cannot perform this merge on an empty stuck
    if (!p.stuckHas(Left))        return false;                                 // Key must be in stuck
    if (p.stuckLastKey() == Left) return false;                                 // Key cannot be the last key in the body

    final int il = p.stuckGetData(Left);                                        // Get the double btree index of the left child branch
    final int rk = p.stuckNextKey(Left);                                        // Get the double btree index of the right child branch
    final int ir = p.stuckGetData(rk);                                          // Get the double btree index of the right child branch

    final Stuck l = dtStuckGet(il);                                             // Load left  branch from the double btree
    final Stuck r = dtStuckGet(ir);                                             // Load right branch from the double btree

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Both children must be branches
    final int mk = p.stuckGetData(Left);                                        // Key associated with left child branch

    if (dtDebug) say("MMMM BranchesNotTop 1111", this);
    if (!l.stuckMergeButOne(Left, r)) return false;                             // Merge branches into left child

    p.stuckRemove(Left);                                                        // Remove the left child
    p.stuckPut(rk, il);                                                         // Update left child position with key of right child and index of left child
    l.stuckPutIntoMemory();                                                     // Save the modified left child back into the tree
    p.stuckPutIntoMemory();                                                     // Save the modified root back into the tree
    dtFree(r);                                                                  // Free right branch as it is no longer in use
    if (dtDebug) say("MMMM BranchesNotTop 2222", this);
    return true;
   }

  private boolean dtMergeBranchesAtTop(Stuck Parent)                            // Merge the top most two child branches of a branch that is not the root
   {final Stuck p = Parent;                                                     // Parent stuck
    if (p.stuckNumber == 0 && p.stuckSize() == 1) return false;                 // Cannot perform this merge on the root if it only has one key, data pair in the body of the stuck
    final int sz = p.stuckSize();                                               // Index of left branch known to be valid as the parent contains at least one entry resulting in two children
    if (sz == 0)              return false;                                     // Cannot perform this merge on an empty stuck

    final int il = p.stuckGetData(p.stuckLastKey());                            // Get the double btree index of the left branch branch
    final int ir = p.stuckTop();                                                // Get the double btree index of the right branch branch

    final Stuck l = dtStuckGet(il);                                             // Load left  branch from the double btree
    final Stuck r = dtStuckGet(ir);                                             // Load right branch from the double btree

    if (l.stuckIsLeaf() || r.stuckIsLeaf()) return false;                       // Both children must be branches

    if (dtDebug) say("MMMM BranchesAtTop 1111", this);
    if (!l.stuckMergeButOne(p.stuckLastKey(), r)) return false;                 // Merge leaves into left child
    p.stuckPop();
    p.stuckSetTop(il);                                                          // Make newly combined left branch top most
    l.stuckPutIntoMemory();                                                     // Save the modified left child back into the tree
    p.stuckPutIntoMemory();                                                     // Save the modified root back into the tree
    dtFree(r);                                                                  // Free right branch as it is no longer in use
    if (dtDebug) say("MMMM BranchesAtTop 2222", this);
    return true;
   }

//D1 Find                                                                       // Find a key in a double btree

  class Find                                                                    // Find the leaf stuck associated with a key in the double btree
   {final int findKey;
    boolean   findFound;                                                        // Whether the key being searched for was found
    int       findIndex;                                                        // Index of the stuck in which the key was found or would have been found if it was in the double tree
    int       findData;                                                         // Data associated with the key if the key was found

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
        final Integer k = s.stuckGe(Key);                                       // Find the smallest key that is greater than or equal to the search key
        p = k == null ? s.stuckTop() : s.stuckGetData(k);                       // Step to top if no key in the stuck is greater then or equal to the search key, else to the stuck indexed by the smallest key greater than or equal to the search key
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
    boolean   fiaSuccess;                                                       // The key was inserted or updated so that the data has been successfully stored under the key

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
      s.append("Success : "+fiaSuccess +"\n");                                  // The key was inserted or updated so that the data has been successfully stored under the key
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

//D1 Deletion                                                                   // Delete a key data pair from the double btree returning the data associated with the key

  Find dtDelete(int Key)                                                        // Find the leaf that contains this key, delete it and return the data associated with the key.
   {final Find f = new Find(Key);

    if (f.findFound)                                                            // Found the key in the tree
     {final Stuck s = dtStuckGet(f.findIndex);                                  // Stuck containing key
      s.stuckRemove(Key);                                                       // Remove the key
      s.stuckPutIntoMemory();                                                   // Save modified stuck back into the double btree
      dtMerge(Key);                                                             // Merge along key path
     }

    return f;
   }

//D1 Tests                                                                      // Test the double btree

  final static int[]random_32 = {12, 3, 27, 1, 23, 20, 8, 18, 2, 31, 25, 16, 13, 32, 11, 21, 5, 24, 4, 10, 26, 30, 9, 6, 29, 17, 28, 15, 14, 19, 7, 22};

  static void test_leafRoot()
   {sayCurrentTestName();
    final Dt    D = new Dt(4, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt    D = new Dt(32, 4);

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
    final Dt  D = new Dt(N, 4);
    D.dtSuppressMerge = true;                                                   // I had not written merge yet.

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
    final Dt  D = new Dt(N, 4);

    for (int i = 1; i <= N; i++)
     {D.dtPut(i, i+1);
     }
    //stop(D);
    ok(D, """
                                                      16                               24                               |
                                                      0                                0.1                              |
                                                      5                                11                               |
                                                                                       6                                |
          4          8               12                               20                                28              |
          5          5.1             5.2                              11                                6               |
          1          3               4                                8                                 9               |
                                     7                                10                                2               |
1,2,3,4=1  5,6,7,8=3    9,10,11,12=4    13,14,15,16=7   17,18,19,20=8   21,22,23,24=10    25,26,27,28=9   29,30,31,32=2 |
""");
   }

  static void test_put_descending()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4);

    for (int i = N; i > 0; i--) D.dtPut(i, i+1);
    //stop(D);
    ok(D, """
                        8                              16                                                                 |
                        0                              0.1                                                                |
                        11                             9                                                                  |
                                                       5                                                                  |
           4                           12                               20              24               28               |
           11                          9                                5               5.1              5.2              |
           12                          8                                4               3                2                |
           10                          7                                                                 1                |
1,2,3,4=12   5,6,7,8=10   9,10,11,12=8   13,14,15,16=7    17,18,19,20=4   21,22,23,24=3    25,26,27,28=2    29,30,31,32=1 |
""");
   }

  static void test_put_random()
   {sayCurrentTestName();
    final int N = random_32.length;
    final Dt  D = new Dt(N, 4);

    for (int i = 0; i < N; ++i) D.dtPut(random_32[i], i);
    //stop(D);
    ok(D, """
                           7                             15                                                      26                          |
                           0                             0.1                                                     0.2                         |
                           13                            5                                                       11                          |
                                                                                                                 6                           |
      2        4                         11                                19        21              24                            30        |
      13       13.1                      5                                 11        11.1            11.2                          6         |
      8        14                        9                                 12        3               17                            10        |
               1                         4                                                           7                             2         |
1,2=8   3,4=14     5,6,7=1   8,9,10,11=9   12,13,14,15=4    16,17,18,19=12   20,21=3     22,23,24=17     25,26=7    27,28,29,30=10   31,32=2 |
""");
   }

  static void test_delete_ascending()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4);
    for (int i = 1; i <= N; i++) D.dtPut(i, i+1);

    final StringBuilder s = new StringBuilder();
    for (int i = 1; i <= N; i++)
     {D.dtDelete(i);
      s.append(D);
     }

    //stop(s);
    ok(s, """
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

  static void test_delete_descending()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4);
    for (int i = 1; i <= N; i++) D.dtPut(i, i+1);

    final StringBuilder s = new StringBuilder();
    for (int i = N; i > 0; --i)
     {D.dtDelete(i);
      s.append(D);
     }

    //stop(s);
    ok(s, """
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

  static void test_delete_random()
   {sayCurrentTestName();
    final int N = 32;
    final Dt  D = new Dt(N, 4);
    for (int i = 1; i <= N; i++) D.dtPut(i, i+1);

    final StringBuilder s = new StringBuilder();
    for (int i = N; i > 0; --i)
     {D.dtDelete(random_32[i-1]);
      s.append(D);
     }

    //stop(s);
    ok(s, """
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

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_leafRoot();
    test_leavesNotTop();
    test_leafAtTop();
    test_branchRoot();
    test_branchesNotTop();
    test_branchesAtTop();
    test_find();
    test_findAndInsert();
    test_put_ascending();
    test_put_merge();
    test_put_descending();
    test_put_random();
    test_delete_ascending();
    test_delete_descending();
    test_delete_random();
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
