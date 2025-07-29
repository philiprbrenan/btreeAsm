//------------------------------------------------------------------------------
// An ordered map that prevents duplicates while preserving insertion order.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.

import java.util.*;

public class Children<E> extends Test implements Iterable<E>                    // An ordered map that prevents duplicates while preserving insertion order
 {private final Stack<E>       stack = new Stack<>();                           // Order of entries
  private final Map<String, E> map   = new TreeMap<>();                         // Prebent duplication of entries

  public E get(String key)                                                      // Get the data asscoiated with a key
   {if (!map.containsKey(key)) stop("No such key: " + key);
    return map.get(key);
   }

  public void put(String key, E value)                                          // Add a key-value pair if key is not already present
   {if (map.containsKey(key))
     {stop(value.getClass().getName()+" with name '" + key + "' already exists.");
     }
    stack.push(value);
    map.put(key, value);
   }

  public Iterator<E> iterator()                                                 // Iterator over the entries in insertion order
   {return stack.iterator();
   }

  public int size() {return stack.size();}                                      // String representation of the map in insertion order
  public E   elementAt(int p) {return stack.elementAt(p);}                      // Entry by index
  public E   firstElement()   {return stack.firstElement();}                      // Entry by index
  public E   lastElement ()   {return stack.lastElement();}                      // Entry by index

  public String toString()                                                      // String representation of the map in insertion order
   {if (stack.size() == 0) return "Empty";
    final StringBuilder s = new StringBuilder();
    for(E e : stack) s.append(""+e+", ");
    return ("{"+s).substring(0, s.length()-1)+"}";
   }

//D1 Tests                                                                      // Tests

  protected static Children<Integer> test_put()
   {final Children<Integer> m = new Children<>();
    m.put("c", 1);
    m.put("b", 2);
    m.put("a", 3);
    ok(m, "{1, 2, 3}");
    return m;
   }

  protected static void test_get()
   {final Children<Integer> m = test_put();
    ok(m.get("c"), 1);
    ok(m.get("b"), 2);
    ok(m.get("a"), 3);
   }

  protected static void test_iter()
   {final Children<Integer> m = new Children<>();
    m.put("c", 1);
    m.put("b", 2);
    m.put("a", 3);
    final StringBuilder s = new StringBuilder();
    for(Integer i: m) s.append(i);
    ok(s, "123");
   }

  protected static void test_at()
   {final Children<Integer> m = new Children<>();
    m.put("c", 1);
    m.put("b", 2);
    m.put("a", 3);
    ok(m.firstElement(), 1);
    ok(m.elementAt(0),   1);
    ok(m.elementAt(1),   2);
    ok(m.elementAt(2),   3);
    ok(m.lastElement(),  3);
   }

  protected static void test_dup()
   {final Children<Integer> m = new Children<>();
    m.put("a", 1);
    m.put("a", 2);
   }

  protected static void oldTests()                                              // Tests thought to be in good shape
   {test_put();
    test_get();
    test_iter();
    test_at();
    //test_dup();
   }

  protected static void newTests()                                              // Tests being worked on
   {oldTests();
   }

  public static void main(String[] args)                                        // Test if called as a program
   {try                                                                         // Get a traceback in a format clickable in Geany if something goes wrong to speed up debugging.
     {if (github_actions) oldTests(); else newTests();                          // Tests to run
      if (github_actions)                                                       // Coverage analysis
       {coverageAnalysis(12, "yyy.java");                                       // Used for printing
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
