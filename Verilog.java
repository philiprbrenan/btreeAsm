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

  void begin(String...ints) {A("begin"); indent(); i(ints);}                    // Begin with some optional integer declarations
  void end()                {dedent(); A("end");}                               // End

  void assign(String a, String b)                                               // Assign
   {A(a + " = " + b + ";");
   }

  void assign(String a, int b)                                                  // Assign
   {A(a + " = " + b + ";");
   }

  class If                                                                      // If
   {If(String condition)
     {A("if ("+condition+") begin");
      indent();
      Then();
      end();
      A("else begin");
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

  class Case                                                                    // Case
   {Case(int N, String condition)                                               // Start at zero
     {A("case ("+condition+")");
      indent();
      for (int i = 0; i < N; i++)
       {A(""+i+": begin");
        indent();
        Choice(i);
        end();
       }
      A("default: begin");
      indent();
      final int d = lines.size();
      Default();
      final int D = lines.size();
      end();
      if (D == d)                                                               // Remove default if empty
       {lines.pop(); lines.pop();
       }
      dedent();
      A("endcase");
     }
    Case(int M, int N, String condition)                                        // Range start, end
     {A("case ("+condition+")");
      indent();
      for (int i = M; i < N; i++)
       {A(""+i+": begin");
        indent();
        Choice(i);
        end();
       }
      A("default: begin");
      indent();
      final int d = lines.size();
      Default();
      final int D = lines.size();
      end();
      if (D == d)                                                               // Remove default if empty
       {lines.pop(); lines.pop();
       }
      dedent();
      A("endcase");
     }
    void Choice(int i) {}
    void Default()     {}
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

  void comment(String s)                                                        // Comment
   {A("// "+s);
   }

  void display(String...s)                                                      // Display
   {if (s.length == 0) stop("Nothing to display");
    A("$display(\""+s[0]+"\"");
    for (int i = 1; i < s.length; i++)
     {a(", "+s[i]);
     }
    a(");");
   }

  void sum(String...s)                                                          // Sum ssintegers
   {if (s.length < 2) stop("Nothing to sum");
    A(s[0]+" = "+ s[1]);
    for (int i = 2; i < s.length; i++)
     {a(" + "+s[i]);
     }
    a(";");
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

  static void test_case()
   {final Verilog v = new Verilog();
    v.new Case(2, "i")
     {void Choice(int i) {v.assign("c",  i);}
      void Default()     {v.assign("c", -1);}
     };
    //stop(v);
    ok(""+v, """
case (i)
  0: begin
    c = 0;
  end
  1: begin
    c = 1;
  end
  default: begin
    c = -1;
  end
endcase
""");
   }

  static void test_comment()
   {final Verilog v = new Verilog();
    v.comment("Hello Word");
    ok(v, """
// Hello Word
""");
   }

  static void test_display()
   {final Verilog v = new Verilog();
    v.display("Hello %s", "a");
    ok(v, """
$display("Hello %s" , a );
""");
   }

  static void test_begin()
   {final Verilog v = new Verilog();
    v.begin("a", "b");
    v.end();
    //stop(v);
    ok(v, """
begin
  integer a;
  integer b;
end
""");
   }

  static void test_sum()
   {final Verilog v = new Verilog();
    v.begin("a", "b", "c");
    v.sum("a", "b", "c");
    v.end();
    //stop(v);
    ok(v, """
begin
  integer a;
  integer b;
  integer c;
  a = b  + c ;
end
""");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_ext();
    test_assign();
    test_if();
    test_for();
    test_case();
    test_comment();
    test_display();
    test_begin();
    test_sum();
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
