//------------------------------------------------------------------------------
// Verilog constants and formatter
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2024
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.

import java.util.*;

class Verilog extends Test                                                      // Verilog constants and formatter
 {static final String folder  = "verilog";                                      // Verilog folder
  static final String ext     = "v";                                            // File extension recognized by Vivado
  static final String header  = "vh";                                           // Header file extension name recognized by Vivado
  static final String testExt = "tb";                                           // Extension for test bench
  static final String constraintsExt = "xdc";                                   // Extension for constraints file

  int indent = 0;                                                               // Current indentation
  final Stack<Line> lines = new Stack<>();                                      // Lines of verilog

  Verilog() {}                                                                  // Start with no indentation
  Verilog(int Indent) {indent = Indent;}                                        // Start with some indentation

  void indent() {indent++;}                                                     // Increase indentation
  void dedent()                                                                 // Decrease indentation
   {if (indent <= 0) stop("Indentation is already zero");
    --indent;
   }

  public String toString()                                                      // Verilog as a string
   {final StringBuilder s = new StringBuilder();
    for(Line l: lines)                                                          // Print the lines of verilog
     {s.append("  ".repeat(l.lineIndent)+l.line+"\n");
     }
    return ""+s;
   }

  class Line
   {final StringBuilder line = new StringBuilder();                             // A line of verilog
    int lineIndent = indent;                                                    // Indentation of this line
    Line() {lines.push(this);}
    void a(String s) {line.append(s);}
   }

  void A(String s)                                                              // Start of the line
   {final Line l = new Line();
    l.a(s);
   }

  void a(String s)                                                              // Continue a line
   {if (lines.size() == 0) stop("No line has been started");
    final Line l = lines.lastElement();
    l.a(" "+s);
   }

  void i(String...variables)                                                    // Declare integers
   {for(int i = 0; i < variables.length; ++i)
     {A("integer "+variables[i]+";");
     }
   }

  void begin()     {A("begin"); indent();}                                      // Begin
  void end()       {dedent(); A("end");}                                        // End

  void assign(String a, String b)                                               // Assign
   {A(a + " = " + b + ";");
   }

  void assign(String a, int b)                                                  // Assign
   {A(a + " = " + b + ";");
   }

  class If                                                                      // If
   {If(String condition)
     {A("if ("+condition+") begin");                                            //
      indent();
      final int t = lines.size();
      Then();
      final int T = lines.size();
      if (T == t) stop("Missing then");
      end();
      A("else begin");                                                          //
      indent();
      final int e = lines.size();
      Else();
      final int E = lines.size();
      end();
      if (E == e)                                                               // Suppress empty else
       {lines.pop(); lines.pop();
       }
     }
    void Then() {}
    void Else() {}
   }

  class For                                                                     // For
   {For(String variable, String condition)
     {begin();
      i(variable);
      A("for("+variable+" = 0; "+condition+"; "+variable + " = "+variable + " + 1)");                                                          //
      indent();
      body();
      end();
      end();
     }
    void body() {}
   }

//D0 Tests                                                                      // Testing

  static void test_ext()
   {ok(Verilog.folder .equals("verilog"));
    ok(Verilog.ext    .equals("v"));
    ok(Verilog.header .equals("vh"));
    ok(Verilog.testExt.equals("tb"));
    ok(Verilog.constraintsExt.equals("xdc"));
   }

  static void test_assign()
   {final Verilog v = new Verilog(2);
    v.assign("a", "b");
    ok(""+v, """
    a = b;
""");
   }

  static void test_if()
   {final Verilog v = new Verilog();
    v.new If ("a > b")
     {void Then() {v.assign("c", "a");}
      void Else() {v.assign("c", "b");}
     };
    ok(""+v, """
if (a > b) begin
  c = a;
end
else begin
  c = b;
end
""");
   }

  static void test_for()
   {final Verilog v = new Verilog();
    v.new For ("i", "i < n")
     {void body() {v.assign("c", "c + i");}
     };
    //stop(v);
    ok(""+v, """
begin
  integer i;
  for(i = 0; i < n; i = i + 1)
    c = c + i;
  end
end
""");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_ext();
    test_assign();
    test_if();
    test_for();
   }

  static void newTests()                                                        // Tests being worked on
   {oldTests();
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
