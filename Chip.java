//------------------------------------------------------------------------------
// Define the capabilities of a chip sufficent to support the btree algorithm
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // B-tree implemented in a memory block on a silicon chip.
// Try eliminating .clone() in case they are used excessively
// Tests for copy* methods  and check that they are actually needed
// Try to make all method and variable names start with the name of their class where this is feasible
import java.util.*;

class Chip extends Test                                                         // A chip designed to manipulate a B-tree stored in a memory block
 {final String chipName;                                                        // The name of the chip
  final Children<Process>  processes = new Children<>();                        // A fixed set of processes for this chip in definition order
  final String         javaTraceFile = fn(Verilog.folder, "trace_java.txt");    // Java trace file for comparison with verilog
  final String      verilogTraceFile = fn(Verilog.folder, "trace_verilog.txt"); // Verilog trace file
  final String         resultsFolder = fn("results/");                          // Results of each synthesis
  static boolean            chipStop = true;                                    // False when the chip is running, true when it is not
  int memoryProcessTransactionNumber = 0;                                       // Make transaction names unique
  int                           step;                                           // Current simulation step being executed
  int                       maxSteps = 10;                                      // Maximum number of steps to execute in the simulation
  static       boolean         debug = false;                                   // Debug when true
  final String         projectFolder = "btreeAsm";                              // Folder containing this project under home folder
  final String   openRoadDockerImage = "appaapps/openroad:latest";              // Docker image used to run OpenRoad
  String           remoteMachineName = "s";                                     // Remote machine name as defined in .ssh/config where the OpenRoad build should be run
  final static boolean   registerChecks = false;                                // Checks whether a register should single or arrayed - as this checking occurs frequently it is useful to disable it to speed up development
  final static boolean coverageAnalysis = false;                                // Enables coverage checks
  final static int   verilogIntegersize = 32;                                   // Enables coverage checks

//D1 Chip                                                                       // A chip is constructed from a fixed number of communicating processes that execute code on the chip to produce the desired outputs from the inputs to the chip

  Chip(String Name)             {if (coverageAnalysis) zz(); chipName = Name;}        // Create a new chip
  static Chip chip(String Name) {if (coverageAnalysis) zz(); return new Chip(Name);}  // Create a new chip

  void chipRunJava()                                                            // Run the processes == ograms defined on this chip using the Java implementation
   {if (coverageAnalysis) zz();
    for(Process p : processes) p.processInit();                                 // Initialize each process == program. Capture the memory set as up by Java so that it can be used to initialize memory in Verilog. Capture the Java execution trace so that the Verilog execution trace can be compared to it to ensure that the Verilog executes along the same path as the Java did.
    chipStop = false;                                                           // Show the program as running
    deleteFile(javaTraceFile);                                                  // Remove Java trace file
    for(step = 0; !chipStop && step < maxSteps; ++step)                         // Run each program
     {for(Process p : processes) p.processStep();                               // Step each program
      chipPrintJava();                                                          // Print chip state after each step
     }
    if (!chipStop)                                                              // Still running after too many steps
     {stop("Out of steps after:", maxSteps);
     }
   }

  void chipRun(int MaxSteps)                                                    // Run the Java version followed by the Verilog version. The output of the Verilog version is compared with the expected output of the definitive Java version
   {maxSteps = MaxSteps;
    chipRunJava();
    chipRunVerilog();
   }

  void chipRun() {chipRun(maxSteps);}                                           // Run the Java version followed by the Verilog version. The output of the Verilog version is compared with the expected output of the definitive Java version

  void R() {if ( chipStop) stop("Not running");}                                // Confirm that the simulation of the chip is running
  void N() {if (!chipStop) stop("Running");}                                    // Confirm that the simulation of the chip is not running

  String chipStopExpression()                                                   // The or of all the process stop fields as a process can only write directly to its own variables not to global ones.
   {if (coverageAnalysis) zz();
    final StringJoiner s = new StringJoiner(" || ");
    for (Process p: processes)                                                  // Each process
     {if (!p.hasMemory()) s.add("("+p.processStopName()+" != 0 ? 1 : 0)");
     }
    return ""+s;
   }

  int chipMaxMemory()                                                           // Maximum memory used over all processes
   {if (coverageAnalysis) zz();
    int m = 0;
    for(Process p: processes)
     {if (m < p.memorySize) m = p.memorySize;
     }
    return m;
   }

//D2 Print                                                                      // Print the state of a chip

  public String chipPrintMemory()                                               // Print the memory of the Java emulation of the chip
   {final StringBuilder s = new StringBuilder();
        s.append(String.format(
         "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n",
          chipName, step, maxSteps, chipStop ? 0 : 1));

        s.append("  Processes:\n");

    for (Process p: processes)                                                  // Each process
     {if (p.hasMemory())                                                        // Print memory if this process has memory attached to it
       {s.append(String.format("    %s\n", p.processName));

        s.append(String.format("      Memory: size: %1d, width: %1d, block: %1d\n",
          p.memorySize, p.memoryWidth, p.memoryBlockSize));

        s.append(String.format("        %2d", p.memoryGet(0, 0)));
        for(int j = 1; j < p.memoryBlockSize; j++)
         {s.append(String.format(", %2d", p.memoryGet(0, j)));
         }

        for  (int i = 1; i < p.memorySize;      i++)
         {for(int j = 0; j < p.memoryBlockSize; j++)
           {s.append(String.format(", %2d", p.memoryGet(i, j)));
           }
         }
        s.append("\n");
       }
     }

    return ""+s;
   }

  public String toString()                                                      // Print a description of the Java emulation of the chip
   {final StringBuilder s = new StringBuilder();
        s.append(String.format(
         "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n",
          chipName, step, maxSteps, (chipStop ? 0 : 1)));

        s.append("  Processes:\n");

    for (Process p: processes)                                                  // Each process
     {final String pt = String.format(
         "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n",
          p.processNumber, p.processName,   p.code.size(),
          p.processPc,     p.processRC);
      s.append(pt);

        if (p.hasMemory())                                                      // Print memory if this process has memory attached to it
         {s.append(String.format("      Memory: size: %1d, width: %1d, block: %1d\n",
            p.memorySize, p.memoryWidth, p.memoryBlockSize));

          s.append(String.format("        %2d", p.memoryGet(0, 0)));
          for(int j = 1; j < p.memoryBlockSize; j++)
           {s.append(String.format(", %2d", p.memoryGet(0, j)));
           }

          for  (int i = 1; i < p.memorySize;      i++)
           {for(int j = 0; j < p.memoryBlockSize; j++)
             {s.append(String.format(", %2d", p.memoryGet(i, j)));
             }
           }
          s.append("\n");
         }

        s.append("      Registers :\n");

      for (Process.Register r: p.registers)                                     // Print registers associated with this process
       {final String name = r.registerFullName;
        if (r.registerSingle())                                                 // Single register
         {if (r.registerTooWideForInt())
           {s.append(String.format("        %-50s = %s\n",  name, r.registerGetHex()));
           }
          else
           {s.append(String.format("        %-50s = %1d\n", name, r.registerGet()));
           }
         }
        else                                                                    // Array of registers
         {for (int i = 0; i < r.registerSize; i++)
           {s.append(String.format("        %-44s[%4d] = %1d\n", name, i, r.registerGet(i)));
           }
         }
       }
      if (p.transactions.size() > 0)                                            // There are transactions associated with this process
       {s.append("      Transactions:\n");
        for (Process.Transaction t: p.transactions)                             // Transactions associated with this process
         {final int ra = t.transactionRequestedAt;
          final int fa = t.transactionFinishedAt;
          final String in = "        ";
          s.append(String.format("%sTransaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n",
            in, t.transactionOpCode, t.transactionName,
            ra, fa, t.transactionRc,
            t.transactionExecutableAsInt(), t.transactionFinishedAsInt()));

          if (t.transactionInputRegisters.size() > 0)                           // Transaction inputs
           {s.append(in+"  Inputs      :\n");
            for (Process.Register r : t.transactionInputRegisters)
             {if (r.registerSingle())
               {if (r.registerTooWideForInt())
                 {s.append(String.format("%s    %-46s = %s\n",
                    in, r.registerFullName, r.registerGetHex()));
                 }
                else
                 {s.append(String.format("%s    %-46s = %1d\n",
                    in, r.registerFullName, r.registerGet()));
                 }
               }
              else
               {for (int i = 0; i < r.registerSize; i++)
                 {s.append(String.format("%s    %-40s[%4d] = %1d\n",
                    in, r.registerFullName, i, r.registerGet(i)));
                 }
               }
             }
           }

          if (t.transactionOutputRegisters.size() > 0)                          // Transaction outputs
           {s.append(in+"  Outputs     :\n");
            for (Process.Register r : t.transactionOutputRegisters)
             {if (r.registerSingle())
               {if (r.registerTooWideForInt())
                 {s.append(String.format("%s    %-46s = %s\n",
                    in, r.registerFullName, r.registerGetHex()));
                 }
                else
                 {s.append(String.format("%s    %-46s = %1d\n",
                    in, r.registerFullName, r.registerGet()));
                 }
               }
              else
               {for (int i = 0; i < r.registerSize; i++)
                 {s.append(String.format("%s    %-40s[%4d] = %1d\n",
                    in, r.registerFullName, i, r.registerGet(i)));
                 }
               }
             }
           }
         }
       }
     }

    return ""+s;
   }

  void chipPrintJava()                                                          // Print the current state of the Java emulation of the chip to a file
   {appendFile(javaTraceFile, toString());
   }

  void chipPrintVerilog(Verilog v)                                              // Verilog to print the state of the chip as a callable Verilog task
   {v.new Task("chipPrint")
     {void Body()
       {v.begin("o");
        v.A("o = $fopen(\""+verilogTraceFile+"\", \"a\");");
        v.A("if (!o) o = $fopen(\"../"+verilogTraceFile+"\", \"a\");");
        v.A("if (!o) $display(\"Cannot create trace folder: "+verilogTraceFile+"\");");

        v.A("$fwrite(o, \"Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\\n\", "+
             "\""+chipName+"\", step, maxSteps, !stop);");

        v.A("$fwrite(o, \"  Processes:\\n\");\n");

        for (Process p: processes)                                              // Each process
         {v.A("$fwrite(o, \"    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\\n\","+
             " "+p.processNumber+", \""+p.processName+"\", "+p.code.size()+", "+
             p.processPcName()+", "+p.processRCName()+");");

          if (p.memory.length > 0)                                              // Print memory
           {final int s = p.memorySize,  b = p.memoryBlockSize,
                      w = p.memoryWidth;

            final String n = p.processMemoryName();
//          v.A("$fwrite(o, \"      Memory: %b\\n\", "+n+"[0]);");
            v.A("$fwrite(o, \"      Memory: size: %1d, width: %1d, block: %1d\\n\", "+
               s+", "+w+", "+b+");");

            v.A("$fwrite(o, \"        %2d\", "+n+"[0][0+:"+w+"]);");            // First block
            for(int j = 1; j < b; j++)
             {v.A("$fwrite(o, \", %2d\", "+n+"[0]["+(j*w)+"+:"+w+"]);");        // Elements in first block
             }
            for   (int i = 1; i < s; i++)                                       // Second and subsequent blocks
             {for(int j = 0; j < b; j++)
               {v.A("$fwrite(o, \", %2d\", "+n+"["+i+"]["+(j*w)+"+:"+w+"]);");  // Elements in second and subsequent blocks
               }
             }
            v.A("$fwrite(o, \"\\n\");");
           }

          v.A("$fwrite(o, \"      Registers :\\n\");");
          for (Process.Register r: p.registers)                                 // Registers
           {if (r.registerSingle())
             {if (r.registerTooWideForInt())
               {v.A("$fwrite(o, \"        %-50s = %hx\\n\", "+
                    " \""+r.registerFullName+"\", "+r.registerFullName+");");
               }
              else
               {v.A("$fwrite(o, \"        %-50s = %1d\\n\", "+
                    " \""+r.registerFullName+"\", "+r.registerFullName+");");
               }
             }
            else
             {for (int i = 0; i < r.registerSize; i++)
               {v.A("$fwrite(o, \"        %-44s[%4d] = %1d\\n\", "+
                 " \""+r.registerFullName+"\", "+i+", "+r.registerName(i)+");");
               }
             }
           }

          if (p.transactions.size() > 0)                                        // Transactions
           {v.A("$fwrite(o, \"      Transactions:\\n\");");
            for (Process.Transaction t: p.transactions)
             {final String ra = t.transactionRequestedAt();
              final String fa = t.transactionFinishedAt();
              v.A("$fwrite(o, \"        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\\n\""+
                ", \""+t.transactionOpCode+"\", \""+t.transactionName+"\""+
                ", "+ra+", "+fa+", "+t.transactionRcName()+
                ", "+t.transactionExecutableV()+", "+t.transactionFinishedV()+");\n");

              if (t.transactionInputRegisters.size() > 0)                       // Transaction inputs
               {v.A("$fwrite(o, \"          Inputs      :\\n\");");
                for (Process.Register r: t.transactionInputRegisters)           // Registers
                 {if (r.registerSingle())
                   {if (r.registerTooWideForInt())
                     {v.A("$fwrite(o, \"            %-46s = %hx\\n\", "+
                       " \""+r.registerFullName+"\", "+r.registerFullName+");");
                     }
                    else
                     {v.A("$fwrite(o, \"            %-46s = %1d\\n\", "+
                       " \""+r.registerFullName+"\", "+r.registerFullName+");");
                     }
                   }
                  else
                   {for (int i = 0; i < r.registerSize; i++)
                     {v.A("$fwrite(o, \"            %-40s[%4d] = %1d\\n\", "+
                       " \""+r.registerFullName+"\", "+i+", "+r.registerName(i)+");");
                     }
                   }
                 }
               }

              if (t.transactionOutputRegisters.size() > 0)                      // Transaction outputs
               {v.A("$fwrite(o, \"          Outputs     :\\n\");\n");
                for (Process.Register r : t.transactionOutputRegisters)
                 {if (r.registerSingle())
                   {if (r.registerTooWideForInt())
                     {v.A("$fwrite(o, \"            %-46s = %hx\\n\", "+
                       " \""+r.registerFullName+"\", "+r.registerFullName+");");
                     }
                    else
                     {v.A("$fwrite(o, \"            %-46s = %1d\\n\", "+
                       " \""+r.registerFullName+"\", "+r.registerFullName+");");
                     }
                   }
                  else
                   {for (int i = 0; i < r.registerSize; i++)
                     {v.A("$fwrite(o, \"            %-40s[%4d] = %1d\\n\", "+
                       " \""+r.registerFullName+"\", "+i+", "+r.registerName(i)+");");
                     }
                   }
                 }
               }
             }
           }
         }
        v.A("$fclose(o);");
        v.end();
       }
     };
   }

//D2 Verilog                                                                    // Verilog describing the chip

//D3 Simulation                                                                 // Verilog used to simulate the chip

  String chipRunVerilogGenerate()                                               // Generate Verilog describing the chip
   {final Verilog v = new Verilog();
    v.A(String.format("""
//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
`default_nettype none
module %s;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer               reset;                                                  // Set chip registers to zero if true
  integer         memoryReset;                                                  // Set chip memory to zero if true
  integer                step;                                                  // Step of the code execution simulation
  integer          memoryStep;                                                  // Step of the memory clear simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = %s;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = %d;
    memoryReset = 1; reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1     // Set registers to zero

    for(memoryStep = 0; memoryStep < %d; memoryStep = memoryStep + 1) begin     // Step through memory steeing one elemnt to zero on each clock to allow yosys to infer registers
      clock = 0; #1; clock = 1; #1;                                             // One memory reset per clock to allow yosys to infer memeory rather than registers
    end
    memoryReset = 0; #1                                                         // Memory now cleared

    for(step = 0; step < maxSteps && !stop; step = step + 1) begin              // Step through code
""", chipName,  chipStopExpression(), maxSteps, chipMaxMemory()));

    v.indent(); v.indent(); v.indent();

    for(Process p: processes)                                                   // Single thread the processes in a constant order
     {v.A("processCurrent = "+p.processNumber+";");
      v.a("clock = 0; #1; clock = 1; #1; // "+p.processNameAndNumber());        // Process table of contents
     }
    v.A("chipPrint();");
    v.end();
    v.A("if (!stop) $finish(1); else $finish(0);");
    v.end();

    v.parallel = true;                                                          // Use parallel assign for each process being driven by the test bench
    for(Process p: processes) p.processVerilog(v);                              // Generate verilog for each process

    chipPrintVerilog(v);                                                        // Add code to print the state of the chip at each step
    v.endModule();

    final String source = fne(Verilog.folder, chipName, Verilog.ext);           // Source code in Verilog
    writeFile(source,  ""+v);
    return source;
   }

  void chipRunVerilog()                                                         // Run Verilog describing the chip confirming that it follows the same execution path as the Java
   {final String source = chipRunVerilogGenerate();                             // Source code in Verilog to run the test in a way that matches the java run
    deleteFile(verilogTraceFile);                                               // Remove Java trace file

    final var n = chipName;
    final var c = String.format(
"rm -f %s %s; iverilog -g2012 -o %s %s && ./%s && rm -f ./%s",
n, verilogTraceFile, n, source, n, n);
    final var e = new ExecCommand(c);                                           // Run Verilog

    final FileCompareAndLocate fcl = new FileCompareAndLocate                   // Compare trace files
     (javaTraceFile, verilogTraceFile);

    if (fcl.matches) ok(true);                                                  // Passed
    else if (fcl.location != null)
     {say("Traces do NOT match on line:", fcl.line, "\n", fcl.location);        // Location of instruction causing first failure
      ok(false);
      for (FileCompareAndLocate.Match l : fcl.q)
       {if (l.ahead)
         {say(String.format("%10d J: %s", l.line, l.a));
          if (!l.a.equals(l.b)) say(String.format("           V: %s", l.b));
         }
        else
         {say(String.format("%10d j: %s", l.line, l.a));
          if (!l.a.equals(l.b)) say(String.format("           v: %s", l.b));
         }
       }
     }
    else stop("Trace files do not contain location records");
   }

//D3 Synthesis                                                                  // Verilog used to synthesize the chip

  Stack<Process.Register> registersIO(boolean input)                            // Get the input or output registers so they can be attached to the pins of the chip
   {final Stack<Process.Register> s = new Stack<>();
    for(Process p: processes)                                                   // Input registers
     {for(Process.Register r: p.registers)
       {if ((input && r.input) || (!input && r.output)) s.push(r);
       }
     }
    return s;
   }
  Stack<Process.Register> registersInput () {return registersIO(true);}         // Get the input registers so they can be attached to the pins of the chip
  Stack<Process.Register> registersOutput() {return registersIO(false);}        // Get the output registers so they can be attached to the pins of the chip

  String chipSynthesizeVerilog()                                                // Generate Verilog used to synthesize the chip using OpenRoad
   {final Verilog v = new Verilog(); v.synthesis = true;
    v.A(String.format("""
//-----------------------------------------------------------------------------
// Database on a chip synthesis
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
`default_nettype none
module %s(                                                                      // Test bench for database on a chip
  input                 clock,                                                  // Clock
  input                 reset,                                                  // Reset chip
""", chipName));
    v.indent();
    for(Process.Register r: registersInput ()) v.A(r.registerDeclareModuleParameter()+",");
    for(Process.Register r: registersOutput()) v.A(r.registerDeclareModuleParameter()+",");

    v.A("output wire            stop                                            // Program has stopped when this goes high");
    v.A(");");

    v.i("step");                                                                // Step of the code execution simulation
    v.i("memoryStep");                                                          // Step of the memory clear simulation
    v.i("memoryReset");                                                         // Memory clear in progress if true
    v.i("returnCode");                                                          // Return code
    v.A("assign stop = "+chipStopExpression()+";");                             // Stop if any process stops otherwise why do we need that process if we can continue without it

    v.new Always()                                                              // Execute next step in program
     {void Body()
       {v.A("if (reset) begin"); v.indent();                                    // Reset if requested
        v.assign("step",        0);
        v.assign("memoryStep",  0);
        v.assign("memoryReset", 1);
        v.end();

        v.A("else if (memoryReset > 0) begin"); v.indent();                     // Clear memory after reset
        v.assign("memoryStep",  "memoryStep + 1");
        v.assign("memoryReset", "memoryStep < "+chipMaxMemory());               // Memory is still being cleared
        v.end();

        v.A("else begin"); v.indent();                                          // Execute code in each process until one of them requests stop
        v.assign("step", "step + 1");
        v.end();
       }
     };

    for(Process p: processes) v.comment(p.processNameAndNumber());              // List processes

    for(Process p: processes)                                                   // Generate verilog for processes that do not have memory
     {if (!p.hasMemory())                                                       // Declare memory for processes that have memeory attached to them
       {p.processVerilog(v);
       }
      else                                                                      // Mmeory process in a separate black box module
       {v.comment("Process: "+p.processName+"  "+p.processNameAndNumber());

        for (Process.Register r: p.registers)                                   // Transactions associated with this process
         {v.A("reg ["+r.registerBits+"-1:0] "+r.registerFullName+";");
         }

        for (Process.Transaction t: p.transactions)                             // Transactions associated with this process
         {final String n = t.transactionName;
          v.i(t.transactionRequestedAt());
          v.i(t.transactionFinishedAt());
          v.i(t.transactionRcName());

          for (Process.Register r : t.transactionInputRegisters)                // Inputs driving registers registers
           {//v.A("reg ["+r.registerBits+"-1:0] "+r.registerFullName+";");
           }
          for (Process.Register r : t.transactionOutputRegisters)               // Outputs driven by registers
           {//v.A("reg ["+r.registerBits+"-1:0] "+r.registerFullName+";");
           }
         }

        v.A(p.processName+" "+p.processName);
        v.A("(.clock(clock),");
        for (Process.Transaction t: p.transactions)                             // Transactions associated with this process
         {final String n = t.transactionName;
          for (Process.Register r : t.transactionInputRegisters)                // Inputs driving registers registers
           {v.A(" ."+r.registerName+"("+r.registerFullName+"),");
           }
          for (Process.Register r : t.transactionOutputRegisters)               // Outputs driven by registers
           {v.A(" ."+r.registerName+"("+r.registerFullName+"),");
           }
          v.A(" ."+t.transactionRequestedAt()+"("+t.transactionRequestedAt()+"),");
          v.A(" ."+t.transactionFinishedAt() +"("+t.transactionFinishedAt() +"),");
          v.A(" ."+t.transactionRcName()     +"("+t.transactionRcName()     +"),");
         }
        v.A(" .reset(reset));");
       }
     }

    v.endModule();                                                              // End non memory modules

    for(Process p: processes)                                                   // Generate verilog for processes that do have memory as separate modules
     {if (p.hasMemory())                                                        // Declare memory for processes that have memeory attached to them
       {v.module(p.processName+"(");
        v.A("input clock,");
        for (Process.Transaction t: p.transactions)                             // Transactions associated with this process
         {for (Process.Register r : t.transactionInputRegisters)                // Inputs driving registers registers
           {v.A("input wire["+r.registerBits+"-1:0] "+r.registerName+",");
           }
          for (Process.Register r : t.transactionOutputRegisters)               // Outputs driven by registers
           {v.A(" output reg["+r.registerBits+"-1:0] "+r.registerName+",");
           }

          final String n = t.transactionName;                                   // Transaction name
          final int    W = verilogIntegersize;                                  // Size of an integer in verilog
          v.A("input wire ["+W+"-1:0] "+t.transactionRequestedAt()+",");
          v.A("output reg ["+W+"-1:0] "+t.transactionFinishedAt() +",");
          v.A("output reg ["+W+"-1:0] "+t.transactionRcName()     +",");
         }
        v.A("input reset);");
        v.endModule();
       }
     }

    final String source = fne(Verilog.folder, chipName, Verilog.ext);           // Source code in Verilog
    writeFile(source,  ""+v);
//  new CompressFile(source, fne(source, "zip"));                               // Compress the verilog file as it sometimes get quite big
    return source;
   }

  class Synthesize                                                              // Generate Verilog describing the chip and synthesize it. Should extend Test.EXecCommand after Java 24
   {final String sourceFile = chipSynthesizeVerilog();                          // Source code written to a file
    final String jsonFile   = fne(Verilog.folder, chipName, "json");            // Save of synthesis results
    final String stdoutFile = fne(Verilog.folder, chipName, "txt");             // Yosys log
    final String c =                                                            // Yosys command
      String.format
       ("yosys -q -T -p \"read_verilog %s; synth -top %s; write_json %s\"",
        sourceFile, chipName, jsonFile, stdoutFile);
    final ExecCommand e = new ExecCommand(c);                                   // Run Verilog
   }

  abstract class SiliconCompiler                                                // Write to a file the silicon compiler python directives to place and route this design
   {final String    sourceFile = chipSynthesizeVerilog();                       // Source code written to a file
    final String    pythonFile = fne(Verilog.folder, chipName, Verilog.pyExt);  // Python commands to layout mask
    final String       sdcFile = fne(Verilog.folder, chipName, Verilog.sdcExt); // Constraints file
    final String       logFile = fne(Verilog.folder, chipName, "log");          // Log file from Silicon Compiler
    final String    launchFile = fne(Verilog.folder, chipName, "sh");           // Launch file to run silicon compiler
    final StringBuilder python = new StringBuilder();
    final StringBuilder    sdc = new StringBuilder();
    final StringBuilder launch = new StringBuilder();

    SiliconCompiler()                                                           // Construct the silicon compiler commands
     {writePython();
      writeSdc();
      writeLaunch();
     }

    abstract String description();                                              // Produce a description of the chip

    void writeLaunch()                                                          // Write launch file to run synthesis on a remote machine
     {final String v = Verilog.folder;                                          // Verilog Working folder
      final String f = fn(resultsFolder, description());                        // Folder in which the summary of the results of this run will be stored for posterity
      final String d = openRoadDockerImage;                                     // Docker image containing OpenRoad
      launch.append(String.format("""
REMOTE=%s                                                                       # Remote user and machine definition from .ssh/config
FOLDER=%s                                                                       # Folder containing project under user's home folder
DESIGN=%s                                                                       # The name of the chip being designed
WORKSP=%s                                                                       # Verilog workspace folder
rsync -r ~/$FOLDER/%s $REMOTE:~/$FOLDER                                            # Copy local project files to remote system
ssh -S none $REMOTE << EOF                                                      # Execute the following commands on the remote system using a non shared ssh connection
cd $FOLDER                                                                      # Project folder
mkdir -p %s                                                                     # Results folder where the summary of the OpenRoad run will be saved
docker pull %s                                                                  # OpenRoad docker image
# Start silicon compiler environment and run OpenRoad script in a docker container
docker run --rm                                                                 \\
  -v ~/$FOLDER/:/root/$FOLDER                                                   \\
  -w /root/$FOLDER                                                              \\
  %s                                                                            \\
  bash -ic "source /root/sc/bin/activate; python3 /root/$FOLDER/$WORKSP/$DESIGN.py"
cp "%s/build/$DESIGN/job0/$DESIGN.pkg.json" "%s"                                # Copy results out of docker to remote system
cp "%s/build/$DESIGN/job0/$DESIGN.png"      "%s"
cp "%s/build/$DESIGN/job0/job.log"          "%s"
EOF
rsync -r $REMOTE:~/$FOLDER/results/%s ~/$FOLDER/results                         # Copy results back to local system from remote system
""",
       remoteMachineName, projectFolder, chipName, v,
       "*", f,
       d, d,
       v, fne(f, chipName, "json"),
       v, fne(f, chipName, "png"),
       v, fne(f, chipName, "log"), "*"));
      writeFile(launchFile, launch);
     }

    void writeSdc()                                                             // Write constraints file
     {sdc.append(String.format("""
create_clock -name clock -period 100 [get_ports {clock}]
set_input_delay  -max 1 -clock clock [get_ports {reset}]
set_input_delay  -min 1 -clock clock [get_ports {reset}]
set_output_delay -max 1 -clock clock [get_ports {stop}]
set_output_delay -min 1 -clock clock [get_ports {stop}]
"""));

      for (Process.Register r: registersInput())                                // Clock delay for input pins
       {sdc.append(String.format("""
set_input_delay -max 1 -clock clock [get_ports %s]
set_input_delay -min 1 -clock clock [get_ports %s]
""", r.registerFullName, r.registerFullName));
       }

      for (Process.Register r: registersOutput())                               // Clock delay for output pins
       {sdc.append(String.format("""
set_output_delay -max 1 -clock clock [get_ports %s]
set_output_delay -min 1 -clock clock [get_ports %s]
""", r.registerFullName, r.registerFullName));
       }
      writeFile(sdcFile, sdc);
     }

    void writePython()                                                          // Construct the silicon compiler python commands
     {python.append(String.format("""
#!/usr/bin/env python3

from siliconcompiler import Chip                                                # import python package
from siliconcompiler.targets import freepdk45_demo

if __name__ == "__main__":
  chip = Chip('%s')                                                             # Create chip object.  The name is used to create the summary and mask image file
  chip.set('option', 'loglevel', 'warning')                                     # Warnings and above
# chip.set('option', 'loglevel', 'error')                                       # Warnings and above
  chip.set('option', 'builddir', '%s/build')                                    # Build folder
  chip.input('%s')                                                              # Source code
# chip.input('/home/azureuser/btreeBlock/verilog/$project/$instance/siliconCompiler/memory.v'   ) # Memory black box
  chip.input('%s')                                                              # Synopsys design constraints file
  chip.set('design', '%s')                                                      # Show the top most module
  chip.use(freepdk45_demo)                                                      # Load predefined technology and flow target
# chip.set('package', 'description', '$designDescription')                      # Description of design
  chip.clock('clock', period=10)                                                # Define clock speed of design was 100
  chip.set('option', 'remote', False)                                           # Run remote in the cloud
  chip.set('option', 'nodisplay', True)                                         # Do not open displays
# chip.set('constraint', 'density', $density)                                   # Lowering the density gives more area in which to route connections at the cost of wasting surface area and making the chip run slower. For find it seems best to leave this parameter alone
  chip.set('option', 'clean', True)                                             # Clean start else it reuses previous results
  chip.run()                                                                    # Run compilation of design and target
  chip.summary()                                                                # Create a summary - but at the moment it is only printed on stdout so for automation you have to get the same information from the summary pkg.json
  chip.snapshot()                                                               # Create the charming image of the chip along with its size, power, clock frequency
#  chip.show()
#  chip.set('option', 'define', 'CFG_ASIC=1')
#  chip.set('option', 'env', 'PDK_HOME', '/disk/mypdk')
#  chip.set('option', 'idir', './mylib')
#  chip.set('option', 'nodisplay', True)
""", chipName, Verilog.folder, sourceFile, sdcFile, chipName));
     writeFile(pythonFile, python);
    }
  }

//D2 Process

  class Process                                                                 // A process consists of memory, registers and a program
   {final String             processName;                                       // The name of the process
    final int                processNumber;                                     // The unique number of this process
    final int                memoryWidth;                                       // The width of each memory element in bits
    final int                memorySize;                                        // The number of memory elements in the memory available to this process
    final int                memoryBlockSize;                                   // Number of elements to read at a time
    final BitSet[]           memory;                                            // Memory is represented as an array of bit vectors
    final Children<Transaction> transactions = new Children<>();                // Transactions representing work requests to this process
    final Children<Register> registers       = new Children<>();                // Registers used in this process
    final Stack<Instruction> code            = new Stack<>();                   // A fixed set of instructions for this process
    final Stack<Label>       labels          = new Stack<>();                   // Labels for instructions in this process
    int                      processPc       = 0;                               // The index of the next instruction to be executed
    int                      processRC       = 0;                               // The return code for this process
    boolean                  processStop     = false;                           // Stop this process - if any process gets stopped the whole chip is brought to a stop on the basis that it needs all of its processes to function
    boolean                  processTrace    = false;                           // Trace this process if true by writing location statements into the log file to identify where in the source code this instruction was generated

//D2 Process                                                                    // Start, step and stop a process while modifying its memory

    Process(String ProcessName)                                                 // Create a process without any memory attached to it
     {this(ProcessName, 0, 0, 1);
      if (coverageAnalysis) zz();
     }

    Process                                                                     // Create a process with the specified memory attached to it
     (String ProcessName, int MemorySize, int MemoryWidth, int MemoryBlockSize)
     {N(); if (coverageAnalysis) zz();
      processName     = ProcessName;
      processNumber   = processes.size();
      processes.put(processName, this);

      memoryBlockSize = MemoryBlockSize;                                        // Block size of memory

      memorySize      = MemorySize;                                             // Create memory
      memoryWidth     = MemoryWidth;

      memory          = new BitSet [memorySize];
     }

    void processInit()                                                          // Get ready to execute the program
     {N(); if (coverageAnalysis) zz();
      processPc = 0;                                                            // Program always starts at the first instruction
      for (Register r: registers) r.registerSet(0);                             // Clear all registers so we start in a known state
      for (Transaction t: transactions)                                         // Initialize transactions
       {t.transactionRc          =  0;
        t.transactionFinishedAt  = -1;
        t.transactionRequestedAt = -1;
       }
      for (int i = 0; i < memorySize; i++)                                      // Clear memory associated with the process
       {memory[i] = new BitSet(memoryWidth * memoryBlockSize);
       }
     }

    void processStep()                                                          // Execute one step in the program
     {if (coverageAnalysis) zz();
      if (code.size() == 0) return;                                             // No code to run
      if (processPc >= code.size())                                             // Stop the run if we go off the end of the code
       {//err("Stopped by process", processName);
        processStop = chipStop = true;
        return;
       }
      final Instruction i = code.elementAt(processPc);                          // The action associated with the current instruction
      i.action();                                                               // Perform the action associated with the current instruction
      if (!i.mightJump) Continue();                                             // If the instruction does not set the next instruction to execute then set the next program instruction to execute for it
      if (processTrace)                                                         // Write a trace element to the log
       {appendFile(javaTraceFile, "Location: "+i.traceBackOnOneLine()+"\n");
       }
     }

    void processStop(int ReturnCode)                                            // Stop the chip
     {if (coverageAnalysis) zz();
      processRC = ReturnCode;
      processStop = chipStop = true;
     }

    void processStop(Verilog v, int ReturnCode)                                 // Stop the chip in Verilog
     {if (coverageAnalysis) zz();
      v.assign(processRCName(), ReturnCode);
      v.assign(processStopName(),       1);
     }

    void ProcessStop(int ReturnCode)                                            // Process stop instruction
     {if (coverageAnalysis) zz();
      new Instruction()
       {void action()           {processStop(ReturnCode);};
        void verilog(Verilog v) {processStop(v, ReturnCode);};
       };
     }

    String processNameAndNumber()                                               // Used to generate skip to comments
     {return String.format("process_%s_%04d", processName, processNumber);
     }

    String processPcName            () {return processName+"_pc";}              // Program counter
    String processMemoryName        () {return processName+"_memory";}          // Name of the memory block used by this process
    String processMemoryName   (int I) {return processName+"_memory["+I+"]";}   // Name of the memory block used by this process
    String processMemoryName(String I) {return processName+"_memory["+I+"]";}   // Name of the memory block used by this process
    String processStopName          () {return processName+"_stop";}            // Name of the stop field in verilog for this process
    String processRCName            () {return processName+"_returnCode";}      // Name of the return code in verilog for this process
    String processMemoryIndexName   () {return processName+"_memory_index";}    // Index variable to initialize memory
    String processMemoryValueName   () {return processName+"_memory_value";}    // Value variable to initialize memory

    boolean hasMemory()             {return memoryWidth > 0 && memorySize > 0;} // Whether this process has any memory attached directly to it

//D3 Instruction                                                                // An instruction represents code to be executed by a process in a single clock cycle == process step

    abstract class Instruction                                                  // Instructions implement the action of a program
     {final int instructionNumber;                                              // The number of this instruction
      final boolean mightJump;                                                  // The instruction might cause a jump
      final String traceBack = traceBack();                                     // Line at which this instruction was created
      Verilog verilogText;                                                      // The verilog implementing this instruction minus context information
      Verilog verilogContext;                                                   // The verilog code that saves the context of this instruction

      final String traceBackOnOneLine()                                         // Line at which this instruction was created represented with out new lines
       {return traceBack.replace("\n", "|").trim();
       }

      Instruction(boolean MightJump)                                            // Add this instruction to the process's code
       {N();
        instructionNumber = code.size();                                        // Number each instruction
        mightJump = MightJump;
        code.push(this);                                                        // Save instruction
       }

      Instruction() {this(false);}                                              // Add this instruction to the process's code

      abstract void action();                                                   // The action to be performed by the instruction
      void verilog(Verilog v) {}                                                // Verilog equivalent of the action implementing this instruction
     }

    class Label                                                                 // Label jump targets in the program
     {int offset;                                                               // The instruction location to which this labels applies
      Label()    {N(); set(); labels.push(this);}                               // A label assigned to an instruction location
      void set() {N(); offset = code.size();}                                   // Reassign the label to an instruction
     }

//D2 Block                                                                      // A block is a sequence of instructions that can be jumped out of to act like an if statement or restarted to act like a loop

    class Block                                                                 // A register is a block of memory that can be accessed within the current clock cycle
     {final Label start = new Label();                                          // Start of block
      final Label end   = new Label();                                          // End of block
      Block()                                                                   // Create a block of code
       {code();                                                                 // Add the code to the block
        end.set();                                                              // Mark the end of the block
       }
      void code() {}                                                            // Code of the block
     }

    void Goto     (Label label)                                                 // Goto a label unconditionally
     {R(); processPc = label.offset;
     }
    void GoNotZero(Label label, Register condition)                             // Go to a specified label if the value of a field is not zero
     {R();
      if (condition.registerGet() >  0) processPc = label.offset;
      else Continue();
     }
    void GoZero   (Label label, Register condition)                             // Go to a specified label if the value of a field is zero
     {R();
      if (condition.registerGet() == 0) processPc = label.offset;
      else Continue();
     }
    void Continue() {processPc++;}                                              // Continue with the next instruction

    void Goto       (Verilog v, Label label)                                    // Goto a label unconditionally
     {v.assign(processPcName(), label.offset);
     }
    void GoNotZero  (Verilog v, Label label, Register condition)                // Go to a specified label if the value of a field is not zero
     {v.new If(condition.registerFullName+" >  0")
       {void Then() {v.assign(processPcName(), label.offset);}
        void Else() {Continue(v);}
       };
     }
    void GoZero     (Verilog v, Label label, Register condition)                // Go to a specified label if the value of a field is zero
     {v.new If(condition.registerFullName+" == 0")
       {void Then() {v.assign(processPcName(), label.offset);}
        void Else() {Continue(v);}
       };
     }

    void Continue(Verilog v) {v.inc(processPcName());}                          // Continue with the next instruction

    void GOto(Label Label)                                                      // Goto instruction
     {new Instruction(true)
       {void action()           {Goto(   Label);};
        void verilog(Verilog v) {Goto(v, Label);};
       };
     }

    void GONotZero(Label Label, Register Condition)                             // Go if not zero instruction
     {new Instruction(true)
       {void action()           {GoNotZero(   Label, Condition);};
        void verilog(Verilog v) {GoNotZero(v, Label, Condition);};
       };
     }

    void GOZero(Label Label, Register Condition)                                // Go if zero instruction
     {new Instruction(true)
       {void action()           {GoZero(   Label, Condition);};
        void verilog(Verilog v) {GoZero(v, Label, Condition);};
       };
     }

    void COntinue()                                                             // Continue instruction
     {new Instruction(true)
       {void action()           {Continue();};
        void verilog(Verilog v) {Continue(v);};
       };
     }

    abstract class If                                                           // If statement as a sequence of instructions
     {final Label Else = new Label(), End = new Label();                        // Components of an if statement

      If (Process.Register Condition)                                           // If a condition
       {N();
        new Instruction(true)                                                   // Branch on the current value of condition
         {void action()
           {GoZero(Else, Condition);
           }
          void verilog(Verilog v)
           {GoZero(v, Else, Condition);
           }
         };
        Then();                                                                 // Then
        new Instruction(true)
         {void action()
           {Goto(End);
           }
          void verilog(Verilog v)
           {Goto(v, End);
           }
         };
        Else.set();
        Else();                                                                 // Else
        End.set();
       }
      void Then() {}                                                            // Body of then clause
      void Else() {}                                                            // Body of else clause
     }

//D2 Register                                                                   // A register is a block of memory that can be accessed within the current clock cycle

    class Register                                                              // A register is a block of memory that can be accessed within the current clock cycle
     {final String  registerBaseName;                                           // The base name of the register with a unique making number appended
      final String  registerName;                                               // The name of the register
      final String  registerFullName;                                           // The full name of the register includes the process name
      final int     registerSize;                                               // Size of array if an array register
      final int     registerBits;                                               // Number of bits the register can hold
      final boolean registerSingle;                                             // A single register if true else an array of registers
      BitSet        value = new BitSet();                                       // Current value of the register in Java
      final BitSet[]values;                                                     // Values if register is an array
      boolean input = false, output = false;                                    // Register is connected to input pins or output pins

      Register                                                                  // Create a register that can take an unknonw value state
       (String RegisterName,    int RegisterBits,
        boolean RegisterSingle, int RegisterSize)
       {N();
        registerBaseName     = RegisterName;
        registerSingle       = RegisterSingle;
        final String n       = registerName = RegisterName + "_" + registers.size();
        registerFullName     = processName + "_" + registerName;
        registerBits         = RegisterBits;
        registerSize         = RegisterSize;
        values = registerArrayed() ? new BitSet[registerSize] : null;           // Array of registers if requested
        if (registerArrayed())
         {for (int i = 0; i < RegisterSize; i++)                                // Create array of register values
           {values[i] = new BitSet();
           }
         }
        registers.put(n, this);                                                 // Save registers associated with each process
       }

      Register(String RegisterName, int RegisterBits)                           // Create a single register
       {this(RegisterName, RegisterBits, true, 0);
        if (coverageAnalysis) zz();
       }

      Register(String RegisterName, int RegisterBits, int RegisterSize)         // Create an arrayed register
       {this(RegisterName, RegisterBits, false, RegisterSize);
        if (coverageAnalysis) zz();
       }

      boolean registerSingle()  {return  registerSingle;}                       // Whether the register is a single element or an array of elements
      boolean registerArrayed() {return !registerSingle;}                       // Whether the register is an array of elements or a single element

      void registerCheckSingle()                                                // Confirm that the register is not an array
       {if (coverageAnalysis) zz();
        if (!registerSingle())
         {stop("Register:", registerName, "is arrayed, not single as required");
         }
       }

      void registerCheckArrayed()                                               // Confirm that the register is an array
       {if (coverageAnalysis) zz();
        if (registerSingle())
         {stop("Register:", registerName, "is single, not arrayed as required");
         }
       }

      void registerCheckSize(Register Source)
       {if (true) return;
        if (registerBits < Source.registerBits)                                 // Make sure the target register is big enough
         {stop("Target register is smaller than source register.\n",
           "Target register:", registerName, "has a size of:", registerBits,
           "while source register:", Source.registerName, "has a size of:",
            Source.registerBits);
         }
       }

      String registerName(int Index)                                            // The name of an element of an arrayed register
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        return processName + "_" + registerName+"["+Index+"]";
       }

      String registerName(String Index)                                         // The name of an element of an arrayed register
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        return processName + "_" + registerName+"["+Index+"]";
       }

      void input()                                                              // Mark a register a being connected to input pins
       {if (coverageAnalysis) zz();
        if (output) stop("Already used for output");
        input = true;
       }
      void output()                                                             // Mark a register a being connected to output pins
       {if (coverageAnalysis) zz();
        if (input) stop("Already used for input");
        output = true;
       }

      Process registerProcess()         {return Process.this;}                  // Process associated with this register

      private String rn  ()             {return registerFullName;}              // Shorten names
      private String rn  (int i)        {return registerName(i);}
      private int    rg  ()             {return registerGet();}
      private int    rg  (int i)        {return registerGet(i);}
      private void   rs  (int v)        {registerSet(v);}
      private void   rs  (int v, int i) {registerSet(v, i);}

      boolean registerTooWideForInt()   {return registerBits >= Integer.SIZE;}  // Whether the reister can be represented as an integer

      int registerGet()                                                         // Return the registerâs value as an integer.
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckSingle();
        if (registerChecks && registerTooWideForInt())
         {stop("Register too big to be an int");
         }
        return value.length() == 0 ? 0 : (int) value.toLongArray()[0];          // Relies on the fact that this Java code is only used for testing, unlike the Verilog version
       }

      String registerGetHex()                                                   // Return the registerâs value as a hex string.
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckSingle();
        return bitSetToHex(value, registerBits)+"x";
       }

      int registerGet(int Index)                                                // Return the value at the specified index in an arrayed set of registers
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        final BitSet value = values[Index];                                     // Indexed value
        return value.length() == 0 ? 0 : (int) value.toLongArray()[0];          // Convert to integer
       }

      void registerSetBitSet(BitSet value, int Value)                           // Set the value of a bit set
       {if (coverageAnalysis) zz();
        final int l = min(registerBits, Integer.SIZE-1);                        // The most bits we can hope to represent
        value.clear();                                                          // Zero the bit set
        for (int i = 0; i < l; i++)                                             // Set each bit in the bitset if the corresponding bit in the value is set
         {if (((Value >> i) & 1) != 0) value.set(i);
         }
       }

      void registerSet(int Value)                                               // Set the value of the register from an integer
       {if (coverageAnalysis) zz();
        if (registerSingle())                                                   // Set value of single register
         {registerSetBitSet(value, Value);
         }
        else
         {for (int i = 0; i < registerSize; i++)                                // Set all the elements of the register to the specified value
           {registerSetBitSet(values[i], Value);
           }
         }
       }

      void registerSet(Verilog v, int Value)                                    // Set the value of the register from an integer
       {if (coverageAnalysis) zz();
        if (registerSingle())                                                   // Set value of single register
         {v.assign(registerFullName, Value);
         }
        else
         {for (int i = 0; i < registerSize; i++)                                // Set all the elements of the register to the specified value
           {v.assign(registerName(i), Value);
           }
         }
       }

      void RegisterSet(int Value)                                               // Set a register instruction
       {new Instruction()
         {void action()           {registerSet(Value);};
          void verilog(Verilog v) {registerSet(v, Value);};
         };
       }

      void registerSet(int Value, int Index)                                    // Set the value of an element of a register array from an integer
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        registerSetBitSet(values[Index], Value);
       }

      void registerSet(Verilog v, int Value, int Index)                         // Set the value of an element of a register array from an integer
       {if (coverageAnalysis) zz();
        v.assign(registerName(Index), Value);
       }

      void RegisterSet(int Value, int Index)                                    // Set the value of an element of a register array from an integer as an instruction
       {new Instruction()
         {void action()           {registerSet(   Value, Index);};
          void verilog(Verilog v) {registerSet(v, Value, Index);};
         };
       }

//D3 Copy                                                                       // Copy between registers

//D4 Register to Register                                                       // Register to register copy

      void copy(Register Source)                                                // Copy a source register into the target register.
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough

        if (registerSingle())
         {if (registerChecks) Source.registerCheckSingle();
          value = (BitSet)Source.value.clone();                                 // Copy the single source value into the target
         }
        else
         {if (registerChecks) Source.registerCheckArrayed();
          for (int i = 0; i < registerSize; i++)
           {values[i] = (BitSet)Source.values[i].clone();                       // Copy the source values into the target
           }
         }
       }

      void copy(Verilog v, Register Source)                                     // Copy a source register into this register.
       {if (coverageAnalysis) zz();
        if (registerSingle())
         {v.assign(registerFullName, Source.registerFullName);
         }
        else
         {final String i = processMemoryIndexName();
          v.new For(i, ""+registerSize)
           {void body()
             {v.assign(registerName(i), Source.registerName(i));
             }
           };
         }
       }

      void Copy(Register Source)                                                // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copy(Source);};
          void verilog(Verilog v) {copy(v, Source);};
         };
       }

//D4 Integer Indexed Register to Register                                       // Copy an integer indexed element of an arrayed source register to a target register

      void copy(Register Source, int RegisterIndex)                             // Copy an integer indexed source register into this target register.
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough
        if (registerChecks) Source.registerCheckArrayed();
        value     = (BitSet)Source.values[RegisterIndex].clone();               // Copy the source value into the target
       }

      void copy(Verilog v, Register Source, int RegisterIndex)                  // Copy a source register into this register which we can do because each and only each process can write to its own registers
       {if (coverageAnalysis) zz();
        v.assign(registerFullName, Source.registerName(RegisterIndex));
       }

      void Copy(Register Source, int RegisterIndex)                             // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copy(   Source, RegisterIndex);};
          void verilog(Verilog v) {copy(v, Source, RegisterIndex);};
         };
       }

//D4 Register Indexed Register to Register                                      // Copy a register indexed element of an arrayed source register to a target register

      void copyIs(Register Source, Register Index)                              // Copy a register indexed source register into this target register.
       {R(); if (coverageAnalysis) zz();
        if (registerChecks) registerCheckSize(Source);                          // Make sure the target register is big enough
        if (registerChecks) Source.registerCheckArrayed();
        if (registerChecks) Index .registerCheckSingle();
        final int i = Index.registerGet();
        value = (BitSet)Source.values[i].clone();                               // Copy the source value into the target
       }

      void copyIs(Verilog v, Register Source, Register Index)                   // Copy a register indexed element of an arrayed source register into this target register
       {if (coverageAnalysis) zz();
        v.assign(registerFullName, Source.registerName(Index.registerFullName));
       }

      void CopyIs(Register Source, Register Index)                              // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copyIs(   Source, Index);};
          void verilog(Verilog v) {copyIs(v, Source, Index);};
         };
       }

//D4 Source Register Indexed by Register and Offset to Register

      void copyIs(Register Source, Register Index, int Offset)                  // Copy an element of an arrayed register indexed by a register and an offset to a target register
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough
        if (registerChecks) Source.registerCheckArrayed();
        if (registerChecks) Index .registerCheckSingle();
        final int i = Index.registerGet()+Offset;                               // Index in source
        value     = (BitSet)Source.values[i].clone();                           // Copy the source value into the target
       }

      void copyIs(Verilog v, Register Source, Register Index, int Offset)       // Copy a register indexed element of an arrayed source register into this target register
       {if (coverageAnalysis) zz();
        v.assign(registerFullName,
          Source.registerName(Index.registerFullName+"+"+Offset));
       }

      void CopyIs(Register Source, Register Index, int Offset)                  // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copyIs(   Source, Index, Offset);};
          void verilog(Verilog v) {copyIs(v, Source, Index, Offset);};
         };
       }

//D4 Register to Integer Indexed Register                                       // Copy a source register to an integer indexed element of an arrayed target register

      void copy(int RegisterIndex, Register Source)                             // Copy the source register into the specified indexed register
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough
        if (registerChecks) Source.registerCheckSingle();
        if (registerChecks)        registerCheckArrayed();
        values[RegisterIndex] = (BitSet)Source.value.clone();                   // Copy the source value into the target
       }

      void copy(Verilog v, int RegisterIndex, Register Source)                  // Copy a source register into this register which we can do because each and only each process can write to its own registers
       {if (coverageAnalysis) zz();
        v.assign(registerName(RegisterIndex), Source.registerFullName);
       }

      void Copy(int RegisterIndex, Register Source)                             // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copy(   RegisterIndex, Source);};
          void verilog(Verilog v) {copy(v, RegisterIndex, Source);};
         };
       }

//D4 Register to register indexed Register                                      // Copy a source register to a register indexed element of this target register

      void copyIt(Register Index, Register Source)                              // Copy a source register to a register indexed element of this target register
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough
        if (registerChecks) Index .registerCheckSingle();
        if (registerChecks) Source.registerCheckSingle();
        if (registerChecks)        registerCheckArrayed();
        final int i = Index.registerGet();                                      // Index of source element
        values[i] = (BitSet)Source.value.clone();                               // Copy the source value into the target
       }

      void copyIt(Verilog v, Register Index, Register Source)                   // Copy a source register into this register which we can do because each and only each process can write to its own registers
       {if (coverageAnalysis) zz();
        v.assign(registerName(Index.registerFullName), Source.registerFullName);
       }

      void CopyIt(Register Index, Register Source)                              // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copyIt(   Index, Source);};
          void verilog(Verilog v) {copyIt(v, Index, Source);};
         };
       }

//D4 Integer Indexed Register to Integer Indexed Register                       // Copy an element of an integer indexed source register to an integer indexed element of an arrayed target register

      void copy(int TargetIndex, Register Source, int SourceIndex)              // Copy an element from an arrayed source to an element of an arrayed target register
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough
        if (registerChecks) Source.registerCheckArrayed();
        if (registerChecks)        registerCheckArrayed();
        values      [TargetIndex] = (BitSet)Source.values[SourceIndex].clone(); // Copy the source value into the target
       }

      void copy(Verilog v, int TargetIndex, Register Source, int SourceIndex)   // Copy an elemen of an arrayed source register into an element of an arrayed target register
       {if (coverageAnalysis) zz();
        v.assign(registerName(TargetIndex), Source.registerName(SourceIndex));
       }

      void Copy(int TargetIndex, Register Source, int SourceIndex)              // Copy instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {copy(   TargetIndex, Source, SourceIndex);};
          void verilog(Verilog v) {copy(v, TargetIndex, Source, SourceIndex);};
         };
       }

//D3 Single to and from Arrayed                                                 // Copy an arrayed register into a single register and vice versa

      void registerCopySingleFromArray(Process.Register Array)                  // Copy an arrayed register to a single register
       {R(); if (coverageAnalysis) zz();
        if (registerChecks)       registerCheckSingle ();                       // Check target is a single register
        if (registerChecks) Array.registerCheckArrayed();                       // Check source is an array register

        Process.Register s = this,    a = Array;                                // Shorten names
        final int A = a.registerBits, S = a.registerSize, B = s.registerBits;   // Sizes
        if (A * S != B)                                                         // Check source and target register sizes are compatible
         {stop("Register:", a.registerFullName, "is", A, "*", S,
          "==", A*S, "but", s.registerFullName, "is", B, "bits");
         }

        value = new BitSet(registerBits);                                       // Size of target single register
        for   (int i = 0, p = 0; i < S; i++)                                    // Each element of the arrayed register
         {for (int j = 0;        j < A; j++, p++)                               // Each bit of each element
           {value.set(p, Array.values[i].get(j));                               // Set corresponding target bit
           }
         }
       }

      void registerCopySingleFromArray(Verilog v, Process.Register Array)       // Copy an arrayed register to a single register
       {final int A = Array.registerBits, S = Array.registerSize;               // Sizes
        final StringJoiner j = new StringJoiner(", ", "{", "}");                // Concatenate the source register elements
        for (int i = S-1; i >= 0;  i--) j.add(Array.registerName(i));           // Concatenate the elements of the source register from high to low
        v.assign(registerFullName, ""+j);                                         // Assign concatenated source to target
       }

      void RegisterCopySingleFromArray(Process.Register Array)                  // Copy an arrayed register to a single register
       {new Instruction()
         {void action()           {registerCopySingleFromArray(   Array);}
          void verilog(Verilog v) {registerCopySingleFromArray(v, Array);}
         };
       }

      void registerCopyArrayFromSingle(Process.Register Single)                 // Copy a single register to an arrayed register
       {R(); if (coverageAnalysis) zz();
        if (registerChecks)  Single.registerCheckSingle();                      // Check target is a single register
        if (registerChecks)         registerCheckArrayed();                     // Check source is an array register
        Process.Register s = Single,  a = this;                                 // Shorten names
        final int A = a.registerBits, S = a.registerSize, B = s.registerBits;   // Sizes
        if (A * S != B)                                                         // Check source and target register sizes are compatible
         {stop("Register:", a.registerFullName, "is", A, "*", S,
          "==", A*S, "but", s.registerFullName, "is", B, "bits");
         }

        final BitSet r = s.value;
        for   (int i = 0, p = 0; i < S; i++)                                    // Each element of the arrayed register
         {for (int j = 0;        j < A; j++, p++)                               // Each bit of each element
           {a.values[i].set(j, s.value.get(p));                                 // Set corresponding target bit from source bit
           }
         }
       }

      void registerCopyArrayFromSingle(Verilog v, Process.Register Single)      // Copy a single register to an arrayed register
       {final int A = registerBits, S = registerSize;                           // Sizes
        for (int i = 0; i < S; ++i)                                             // Each element of the arrayed register
         {v.assign(       registerName(i),                                      // Copy corresponding bits from the single register into the corresponding element of the target register
                   Single.registerFullName+"["+(A*i)+"+:"+A+"]");
         }
       }

      void RegisterCopyArrayFromSingle(Process.Register Single)                 // Copy a single register to an arrayed register
       {new Instruction()
         {void action()           {registerCopyArrayFromSingle(   Single);}
          void verilog(Verilog v) {registerCopyArrayFromSingle(v, Single);}
         };
       }

//D3 Combine                                                                    // Or two registers together

      void combine(Register Source)                                             // Copy the source register into the target if the source is not zero
       {R(); if (coverageAnalysis) zz();
        registerCheckSize(Source);                                              // Make sure the target register is big enough

        if (registerSingle())                                                   // Single register
         {if (!Source.value.isEmpty()) value = (BitSet)Source.value.clone();    // Combine the source value into the target
         }
        else                                                                    // Array register
         {for (int i = 0; i < registerSize; i++)
           {if (!Source.values[i].isEmpty())
             {values[i] = (BitSet)Source.values[i].clone();                     // Combine the source value into the target
             }
           }
         }
       }

      void combine(Verilog v, Register Source)                                  // Combine a source register into this register
       {if (coverageAnalysis) zz();
        if (registerSingle())                                             // Single register
         {v.new If(Source.registerFullName)
           {void Then()
             {v.assign(rn(), Source.rn());
             }
           };
         }
        else                                                                    // Array register
         {final String i = processMemoryIndexName();
          v.new For(i, ""+registerSize)
           {void body()
             {v.new If (Source.registerName(i))
               {void Then()
                 {v.assign(registerName(i), Source.registerName(i));
                 }
               };
             }
           };
         }
       }

      void Combine(Register Source)                                             // Combine instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {combine(Source);};
          void verilog(Verilog v) {combine(v, Source);};
         };
       }

      public String toString()                                                  // Print the register
       {if (registerSingle)                                                     // Single register value
         {if (registerTooWideForInt())
           {return registerFullName+" = "+registerGetHex();
           }
          return registerFullName+" = "+registerGet();
         }
        final StringBuilder s = new StringBuilder(registerFullName+" = ");      // Arrayed register values
        for (int i = 0; i < registerSize; i++)
         {s.append(String.format(" %2d", registerGet(i)));
         }
        return ""+s;
       }

      String registerDeclareModuleParameter()                                   // Declare a register in Verilog
       {if (coverageAnalysis) zz();
        final String n = registerBaseName;
        if (input)  return "input wire ["+registerBits+"-1:0] "+n;
        if (output) return "output reg ["+registerBits+"-1:0] "+n;
        stop("Module parameter:", n, "must be input or output");                // The caller has not told us whether the parameter is for input or output
        return null;
       }

//D3 Arithmetic                                                                 // Operations on registers

      void one () {R(); rs(1);}                                                 // One a register in Java
      void inc () {R(); rs(rg()+1);           }                                 // Increment a register in Java
      void dec () {R(); rs(rg()-1);           }                                 // Decrement a register in Java
      void not () {R(); rs(rg() != 0 ? 0 : 1);}                                 // Not a register in Java
      void half() {R(); rs(rg() >> 1);        }                                 // Halve a register
      void add (Register S) {R(); rs(rg()+S.rg());  }                           // Add the source register to the current register in Java
      void add1(Register S) {R(); rs(rg()+S.rg()+1);}                           // Add the source register to the current register plus one in Java
      void add (int      S) {R(); rs(rg()+S);       }                           // Add the source register to the current register in Java
      void average(Register S1, Register S2) {R(); rs((S1.rg()+S2.rg())/2);}    // Average of two registers

      void gt (Register a, Register b) {R(); rs(a.rg() >  b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge (Register a, Register b) {R(); rs(a.rg() >= b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq (Register a, Register b) {R(); rs(a.rg() == b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne (Register a, Register b) {R(); rs(a.rg() != b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le (Register a, Register b) {R(); rs(a.rg() <= b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt (Register a, Register b) {R(); rs(a.rg() <  b.rg() ? 1 : 0);}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void gt (Register a, int b) {R(); rs(a.rg() >  b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge (Register a, int b) {R(); rs(a.rg() >= b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq (Register a, int b) {R(); rs(a.rg() == b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne (Register a, int b) {R(); rs(a.rg() != b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le (Register a, int b) {R(); rs(a.rg() <= b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt (Register a, int b) {R(); rs(a.rg() <  b ? 1 : 0);}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void one (Verilog v)        {v.assign(rn(),         "1");}                // One a register in Verilog
      void inc (Verilog v)        {v.assign(rn(), rn()+" + 1");}                // Increment a register in Verilog
      void dec (Verilog v)        {v.assign(rn(), rn()+" - 1");}                // Decrement a register in Verilog
      void not (Verilog v)        {v.assign(rn(), rn()+" != 0 ? 0 : 1");}       // Not a register in Verilog
      void half(Verilog v)        {v.assign(rn(), rn()+" >> 1");}               // Half a number
      void add (Verilog v, Register source)                                     // Add the source register to the current register in Verilog
       {v.assign(rn(), rn() + " + " +source.rn());
       }
      void add1(Verilog v, Register source)                                     // Add the source register to the current register plus one in Verilog
       {v.assign(rn(), rn() + " + 1 + " +source.rn());
       }
      void add (Verilog v, int source)                                          // Add the source register to the current register in Verilog
       {v.assign(rn(), rn() + " + " +source);
       }
      void average(Verilog v, Register source1, Register source2)               // Average of two registers
       {v.assign(rn(), "(" + source1.rn() + " + " + source2.rn() + ") / 2");
       }
      void gt(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" >  "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" >= "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" == "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" != "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" <= "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +" <  "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void gt(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" >  "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" >= "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" == "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" != "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" <= "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +" <  "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void Gt(Register a, Register b) {new Instruction() {void action() {gt(a, b);} void verilog(Verilog v) {gt(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Ge(Register a, Register b) {new Instruction() {void action() {ge(a, b);} void verilog(Verilog v) {ge(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Eq(Register a, Register b) {new Instruction() {void action() {eq(a, b);} void verilog(Verilog v) {eq(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Ne(Register a, Register b) {new Instruction() {void action() {ne(a, b);} void verilog(Verilog v) {ne(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Le(Register a, Register b) {new Instruction() {void action() {le(a, b);} void verilog(Verilog v) {le(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Lt(Register a, Register b) {new Instruction() {void action() {lt(a, b);} void verilog(Verilog v) {lt(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction

      void Gt(Register a, int b)      {new Instruction() {void action() {gt(a, b);} void verilog(Verilog v) {gt(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Ge(Register a, int b)      {new Instruction() {void action() {ge(a, b);} void verilog(Verilog v) {ge(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Eq(Register a, int b)      {new Instruction() {void action() {eq(a, b);} void verilog(Verilog v) {eq(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Ne(Register a, int b)      {new Instruction() {void action() {ne(a, b);} void verilog(Verilog v) {ne(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Le(Register a, int b)      {new Instruction() {void action() {le(a, b);} void verilog(Verilog v) {le(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction
      void Lt(Register a, int b)      {new Instruction() {void action() {lt(a, b);} void verilog(Verilog v) {lt(v, a, b);}};} // Set the target register to one if the test between the 'a' and 'b' register is true else 0 as an instruction

      void zero()                                                               // Zero a register in Java
       {R(); if (coverageAnalysis) zz();
        if (registerSingle())
         {rs(0);
         }
        else
         {for (int i = 0; i < registerSize; i++)
           {registerSet(0, i);
           }
         }
       }

      void zero(Verilog v)                                                      // Zero a register in Verilog
       {if (coverageAnalysis) zz();
        if (registerSingle()) v.assign(rn(),  "0");
        else
         {final String i = processMemoryIndexName();
          v.new For(i, ""+registerSize)
           {void body()
             {v.assign(rn()+"["+i+"]", "0");
             }
           };
         }
       }

      void Zero()                                                               // Zero a register instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {zero();};
          void verilog(Verilog v) {zero(v);};
         };
       }

      void zero(int i)                                                          // Zero an element of a register array in Java
       {R(); if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        rs(0, i);
       }

      void zero(Verilog v, int i)                                               // Zero an element of a register array in Verilog
       {if (coverageAnalysis) zz();
        v.assign(rn(i), "0");
       }

      void Zero(int Index)                                                      // Zero a register instruction
       {if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        new Instruction()
         {void action()           {zero(   Index);};
          void verilog(Verilog v) {zero(v, Index);};
         };
       }

      void One()                                                                // One a register instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {one();};
          void verilog(Verilog v) {one(v);};
         };
       }

      void one(int i)                                                           // One an element of a register array in Java
       {R(); if (coverageAnalysis) zz();
        if (registerChecks) registerCheckArrayed();
        rs(1, i);
       }
      void one(Verilog v, int i)                                                // One an element of a register array in Verilog
       {if (coverageAnalysis) zz();
        v.assign(rn(i), "1");
       }

      void One(int Index)                                                       // One as an instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {one(   Index);};
          void verilog(Verilog v) {one(v, Index);};
         };
       }

      void Inc()                                                                // Increment a register instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {inc();};
          void verilog(Verilog v) {inc(v);};
         };
       }

      void Dec()                                                                // Decrement a register instruction
       {new Instruction()
         {void action()           {dec();};
          void verilog(Verilog v) {dec(v);};
         };
       }

      void Add(Register Source)                                                 // Add the value of a register to the specified register as an instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {add(   Source);};
          void verilog(Verilog v) {add(v, Source);};
         };
       }

      void Add1(Register Source)                                                // Add the value of a register plus one to the specified register as an instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {add1(   Source);};
          void verilog(Verilog v) {add1(v, Source);};
         };
       }

      void Average(Register Source1, Register Source2)                          // Average of two registers as an instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {average(   Source1, Source2);};
          void verilog(Verilog v) {average(v, Source1, Source2);};
         };
       }

      void sum(Process.Register...Source)                                       // Sum the source registers in Java
       {R(); if (coverageAnalysis) zz();
        int sum = 0;
        for (int i = 0; i < Source.length; i++) sum += Source[i].registerGet();
        registerSet(sum);
       }

      void sum(Verilog v, Process.Register...Source)                            // Sum the source registers in Verilog
       {if (coverageAnalysis) zz();
        if (Source.length == 0) return;

        final StringJoiner s = new StringJoiner(" + ");
        for (Process.Register r: Source) s.add(r.registerFullName);
        v.assign(registerFullName, ""+s);
       }

      void Sum(Register...Source)                                               // Sum the source registers as an instruction
       {if (coverageAnalysis) zz();
        new Instruction()
         {void action()           {sum(   Source);};
          void verilog(Verilog v) {sum(v, Source);};
         };
       }
     } // Register

    Register register(String RegisterName, int RegisterBits)                    // Create a single register
     {N(); if (coverageAnalysis) zz();
      return new Register(RegisterName, RegisterBits);
     }

    Register register(String RegisterName, int RegisterBits, int RegisterSize)  // Create an arrayed register
     {N(); if (coverageAnalysis) zz();
      return new Register(RegisterName, RegisterBits, RegisterSize);
     }

//D3 Verilog                                                                    // Generate a Verilog always block to implement this process

    String processVerilog(Verilog v)                                            // Generate Verilog code for this process
     {N(); if (coverageAnalysis) zz();
      v.comment("Process: "+processName+"  "+processNameAndNumber());
      if (hasMemory())                                                          // Declare memory for processes that have memeory attached to them
       {v.A("(* ram_style = \"block\" *)");
//M     v.A("reg ["+memoryWidth+"-1:0] "+processMemoryName()+"["+memorySize+"*"+memoryBlockSize+"];");
        v.A("reg ["+memoryWidth+"*"+memoryBlockSize+"-1:0] "+processMemoryName(memorySize)+";");
       }

      for (Register r: registers)                                               // Declare registers associated with this process
       {if (r.registerSingle())                                                 // Declare single registers
         {v.A("reg ["+r.registerBits+"-1:0] "+r.registerFullName+";");
         }
        else                                                                    // Declare arayed registers
         {v.A("(* nomem2reg *)");                                               // Otherwise Yosys will try to treat this as memory and then complain when it cannot do so
          v.A("reg ["+r.registerBits+"-1:0] "+r.registerName(r.registerSize)+";");
         }
       }

      for (Transaction t: transactions)                                         // Transactions associated with this process
       {final String n = t.transactionName;
        v.i(t.transactionRequestedAt());
        v.i(t.transactionFinishedAt());
        v.i(t.transactionRcName());
       }

      v.i(processPcName(), processStopName(), processRCName(),                  // Declare control variables
        processMemoryIndexName());
      v.A(String.format("reg[%d-1:0] %s;", memoryWidth, processMemoryValueName())); // Value to be loaded into a memory element

      v.new Always()                                                            // Always block for this process
       {void Body()
         {v.A("if (reset) begin"); v.indent();                                  // Clear all control variables and registers on reset
          v.assign(processPcName(),          "0");                              // Program counter for this process
          v.assign(processStopName(),        "0");                              // Stop process when true
          v.assign(processRCName(),          "0");                              // Return code after stopping
          v.assign(processMemoryIndexName(), "0");                              // Index of memory to load into
          v.assign(processMemoryValueName(), "0");                              // Value to be loaded into memory

          for (Register r: registers)                                           // Clear all registers
           {if (!r.input && !r.output)
             {if (!r.registerArrayed())                                         // Initialize single register
               {v.assign(r.registerFullName,   "0");
               }
              else                                                              // Initialize arrayed register
               {final String i = processMemoryIndexName();
                v.new For(i, ""+r.registerSize)
                 {void body()
                   {v.assign(r.registerName(i), "0");
                   }
                 };
               }
             }
           }

          for (Transaction t: transactions)                                     // Initialize transactions
           {v.assign(t.transactionFinishedAt(), "-1");
            v.assign(t.transactionRcName(),  "0");
           }

          for (Process p: processes)                                            // Find transactions of which we are the source
           {for (Transaction t: p.transactions)
             {if (t.transactionCallingProcess == Process.this)                  // This transaction is a source of requests against this process
               {final String n = t.transactionName;
                v.assign(t.transactionRequestedAt(), "-1");                     // Clear step at which the transaction was requested
               }
             }
           }
          v.end();

          v.A("else if (memoryReset > 0) begin"); v.indent();                   // Clear all memory associated with this process
          if (hasMemory())
           {v.A("if (memoryStep < "+memorySize+") begin"); v.indent();
            v.assign(processMemoryName("memoryStep"), "0");                     // Clear memory
            v.end();
           }
          v.end();

          v.A("else begin"); v.indent();                                        // Execute code in each process
          if (v.synthesis) v.A("begin");                                        // Run all processes in parallel
          else v.A(String.format                                                // Run each process to match Java execution
           ("if (processCurrent == %s) begin", processNumber));

          v.indent();
          v.A(String.format("case(%s)", processPcName()));                      // Execute instructions in process
          v.indent();

          class VerilogInstruction                                              // Verilog for an instruction
           {VerilogInstruction(Instruction i)                                   // Create the text for an instruction written in verilog
             {final Verilog t = new Verilog();
              t.parallel  = v.parallel;
              t.synthesis = v.synthesis;
              i.verilog(t);
            //if (t.isEmpty()) err("No Verilog generated by this instruction:\n"+i.traceBack);
              if (!i.mightJump) t.inc(processPcName());                         // Increment program counter if the instruction does not already handle this
              i.verilogText = t;                                                // Text of instruction minus context information

              final Verilog c = new Verilog();                                  // Verilog for instruction context
              c.begin("f");
              c.A("f = $fopen(\""+verilogTraceFile+"\", \"a\");");
              c.A("$fdisplay(f, \"Location: "+i.traceBackOnOneLine()+"\");");
              c.A("$fclose(f);");
              c.end();
              i.verilogContext = c;                                             // Verilog representation of context of instruction
             }
           }

          final StringToNumbers sn = new StringToNumbers();                     // Use each block of code just once
          for(Instruction i: code)                                              // Create verilog for each instructuction
           {new VerilogInstruction(i);
            sn.put(""+i.verilogText, i.instructionNumber);
           }
          sn.order();                                                           // Collapse identical verilog text minus the context

          for(Instruction i: code)                                              // Each instruction
           {if (!sn.isFirst(i.instructionNumber)) continue;
            final StringToNumbers.Order o = sn.order(i.instructionNumber);      // Identical statements
            final String n = o.joinKeys();                                      // Statement numbers attached to this statement
            v.A(""+n+": begin");                                                // The statements that have this code
            v.indent();
            v.A(i.verilogText);                                                 // Text of instruction

            if (processTrace)                                                   // Add a location statement if this process is being traced. Done in line because Icarus does not pass the string correctly as a parameter.
             {if (!v.synthesis)
               {v.new Case(code.size(), processPcName())
                 {void Choice(int N)
                   {if (o.has(N))                                               // An identical statement
                     {final Verilog V = code.elementAt(N).verilogContext;
                      v.A(V);
                     }
                   }
                  boolean allowChoice(int N) {return o.has(N);}
                  boolean allowDefault()     {return false;}
                 };
               }
             }
            v.end();
           }
          v.A("default: "+processStopName()+" "+v.assignOp()+" 1;");
          v.endCase();
          v.end();
          v.end();
         }
       };
      return ""+v;
     }

//D3 Memory                                                                     // Process operations on memory

    protected int memoryGet(int Index)                                          // Get a memory element as an integer.
     {final BitSet b = memory[Index];                                           // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      return V.length > 0 ? (int)V[0] : 0;                                      // Take the first element if it exists relying on the fact that in the Java code we test with just sufficiently large numbers to test the Verilog in principle
     }

    protected int memoryGet(int Index, int Offset)                              // Get a memory element as an integer
     {final int i = Index, o = Offset, w = memoryWidth;                         // Index of memory to be read
      final BitSet b = memory[i].get(o*w, (o+1)*w);                             // Read memory as bit set
      final long[] V = b.toLongArray();                                         // Convert bitset to long
      return V.length > 0 ? (int)V[0] : 0;                                      // Take the first element if it exists relying on the fact that in the Java code we test with just sufficiently large numbers to test the Verilog in principle
     }

    void memoryGet(Register Value, Register Index)                              // Get a memory element indexed by a register as an integer setting the memory cache register to the value of the element retrieved
     {R(); if (coverageAnalysis) zz();
      if (Value.registerArrayed())                                              // Single register from memory
       {stop("Single register required for interactions with memory");
       }
      if (Value.registerBits != memoryBlockSize * memoryWidth)                  // Confirm the size of the register being read into
       {stop("Register has wrong size for memory");
       }
      Value.value = (BitSet)memory[Index.registerGet()].clone();                // Read memory at index
     }

    void memoryGet(Verilog v, Register Value, Register Index)                   // Get a memory element indexed by a register as an integer setting the memory cache register to the value of the element retrieved
     {if (coverageAnalysis) zz();
      final String n = processMemoryIndexName();                                // Memory index name
      final String i = Index.registerFullName;                                    // Index register
      v.assign(Value.registerFullName, processMemoryName(i));                     // Get indexed element
     }

    void memorySet(Register Value, Register Index)                              // Set a memory element indexed by a register
     {R(); if (coverageAnalysis) zz();
      if (Value.registerArrayed())                                              // Single register from memory
       {stop("Single register required for interactions with memory");
       }
      if (Value.registerBits != memoryBlockSize * memoryWidth)                  // Confirm the size of the register being read into
       {stop("Register has wrong size for memory");
       }
      memory[Index.registerGet()] = (BitSet)Value.value.clone();                // Read memory at index
     }

    void memorySet(Verilog v, Register Value, Register Index)                   // Set a memory element indexed by a register.
     {v.assign(processMemoryName(Index.registerFullName),
               Value.registerFullName);
     }

    void MemorySet(Register Value, Register Index)                              // Set a memory element indexed by a register as an instruction
     {if (coverageAnalysis) zz();
      new Instruction()
       {void action ()          {memorySet(   Value, Index);}
        void verilog(Verilog v) {memorySet(v, Value, Index);}
       };
     };

//D3 Transaction                                                                // A transaction allows other processes on the chip to request services from this process

    class Transaction                                                           // Transactions allow one process to request services from another process
     {final String transactionName;                                             // Name of the transaction
      final Children<Process.Register> transactionInputRegisters  = new Children<>(); // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
      final Children<Process.Register> transactionOutputRegisters = new Children<>(); // The registers used as outputs by this transaction. As they are going to be written into by the transaction they have to be owned by the process executing the transaction
      int transactionRequestedAt = -1;                                          // The step at which the transaction started -  avoiding the use of a boxed type as no direct equivalent in verilog
      int transactionFinishedAt  = -1;                                          // The step at which the transaction finished
      int transactionRc;                                                        // Return code from executing transaction
      final Process transactionCallingProcess;                                  // The process requesting a service provided by this process via this transaction
      final String  transactionOpCode;                                          // The service requested by the caller

      Transaction(String Name, Process CallingProcess, String OpCode)           // Transactions allow one process to request services from another process
       {N(); if (coverageAnalysis) zz();
        transactionName = Name;
        transactionCallingProcess = CallingProcess;
        transactionOpCode = OpCode;
        transactions.put(transactionName, this);
       }

      void transactionInputRegisters(Process.Register...InputRegisters)         // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
       {N(); if (coverageAnalysis) zz();
        for(Process.Register r : InputRegisters)                                // Save input registers
         {transactionInputRegisters.put(r.registerFullName, r);
         }
       }

      void transactionOutputRegisters(Process.Register...OutputRegisters)       // The registers used as outputs by this transaction. As they are going to be written into by the transaction they have to be owned by the process executing the transaction
       {N(); if (coverageAnalysis) zz();
        for(Process.Register r : OutputRegisters)                               // Save output registers
         {if (r.registerProcess() != transactionProcess())                      // Check that the output registers are owned by the target process of the transaction as that process is the only process that can write into them
           {stop("Output transaction register:",
             r.registerFullName+" must be owned by process: "+
             transactionProcess().processName+"\n");
           }
          transactionOutputRegisters.put(r.registerFullName, r);
         }
       }

      boolean transactionExecutable()                                           // Whether the transaction is executable or not
       {final int r = transactionRequestedAt;
        final int f = transactionFinishedAt;
        return r > f && r != step;                                              // The transaction has been requested in an earlier step and has not yet completed
       }

      boolean transactionFinished()                                             // Whether the transaction has finished executing
       {final int r = transactionRequestedAt;
        final int f = transactionFinishedAt;
        return r < f;                                                           // The transaction has finished and is not currently executing
       }

      int transactionExecutableAsInt() {return transactionExecutable() ? 1 : 0;}// Whether the transaction is executable or not as an integer
      int transactionFinishedAsInt  () {return transactionFinished  () ? 1 : 0;}// Whether the transaction has finished or not as an integer

      String transactionRequestedAt() {N(); return transactionName+"_requestedAt";} // Name of the requested at field for a transaction
      String transactionFinishedAt()  {N(); return transactionName+"_finishedAt";}  // Name of the finished at field for a transaction

      String transactionExecutableV()                                           // Whether the transaction is executable or not in verilog
       {final String r = transactionRequestedAt();
        final String f = transactionFinishedAt();
        N(); zz(); return "("+r+" > "+f + " && "+r + " != step)";
       }

      String transactionFinishedV()                                             // Whether the transaction has finished or not in verilog
       {N(); if (coverageAnalysis) zz();
        final String r = transactionRequestedAt();
        final String f = transactionFinishedAt();
        return "("+r+" < "+f+")";
       }

      void transactionSetExecutable()                                           // Mark a transaction as executable
       {R(); if (coverageAnalysis) zz();
        if (transactionRequestedAt > transactionFinishedAt)
         {stop("Transaction already running");
         }
        transactionRequestedAt = step;
        transactionFinishedAt  = -1;                                            // This allows processes to be cleared and restarted. Otherwise the transaction looks as it has finished with the previous values in its registers.
       }
      void transactionSetExecutable(Verilog v)                                  // Mark a transaction as executable in verilog
       {v.assign(transactionRequestedAt(), "step");
        if (!v.synthesis) v.assign(transactionFinishedAt(), -1);                // We cannot use the process clear feature that is so useful for testing the java version in verilog being synthesized
       }
      void transactionSetFinished()                                             // Mark a transaction as finished
       {R(); transactionFinishedAt = step;
       }
      void transactionSetFinished(Verilog v)                                    // Mark a transaction as finished in verilog
       {v.assign(transactionFinishedAt(), "step");
       }
      String transactionRcName()                                                // Name of the return code field for a transaction
       {N(); return transactionProcess().processName+"_"+
         transactionName+"_returnCode";
       }

      Process transactionProcess() {return Process.this;}                       // Process associated with this transaction
     } // Transaction
   } // Process

  Process process                                                               // Create a process with attached memory
   (String ProcessName, int MemorySize, int MemoryWidth, int BlockSize)
   {return new Process(ProcessName, MemorySize, MemoryWidth, BlockSize);
   }

  Process process(String ProcessName)                                           // Create a process without attached memory
   {return new Process(ProcessName, 0, 0, 1);
   }

//D2 Memory Process                                                             // Processes that manage memory used by other processes

  class Memory extends Process                                                  // A process whose main purpose is to maintain memory
   {final Map<String, Get> memoryGetFromProcess = new TreeMap<>();              // Locate a get transaction associated with this memory by name
    final Map<String, Set> memorySetIntoProcess = new TreeMap<>();              // Locate a set transaction associated with this memory by name

    Memory(String ProcessName, int MemorySize, int MemoryWidth, int BlockSize)  // Create a memory process
     {super(ProcessName, MemorySize, MemoryWidth, BlockSize);
      memoryProcessGenerate();                                                  // Generate the code to execute this process
     }

    Memory(String ProcessName, int MemorySize, int MemoryWidth)                 // Create a memory process to read a single element at a time
     {this(ProcessName, MemorySize, MemoryWidth, 1);
     }

//D3 Store and Retrieve                                                         // Save memory to a string or reload memory from a string

    String memorySave()                                                         // Save memory to a string
     {if (coverageAnalysis) zz();
      final int S = memorySize, W = memoryWidth, B = memoryBlockSize;           // Shorten names
      final StringJoiner s = new StringJoiner(" "+W+" "+S+" "+B, " ", "\n");    // Save dimensions so they can be checked on reload
      for(int i = 0; i < S; i++)                                                // Transform memory blocks into bits
       {final BitSet b = memory[i];
        for (int j = 0; j < W * B; j++) s.add(b.get(j) ? "1" : "0");            // Save each bit
       }
      return ""+s;                                                              // String of bits representing memory
     }

    void memoryLoad(Process P, String line)                                     // Load memory from a string
     {N(); if (coverageAnalysis) zz();
      final int     S = memorySize, W = memoryWidth, B = memoryBlockSize;       // Shorten names
      final String[]w = line.trim().split("\\s+");                              // Input line as words
      final int   []n = new int[w.length];                                      // Input line as numbers
      for (int i = 0; i < w.length; i++) n[i] = Integer.parseInt(w[i]);         // Parse words in input string into numbers

      if (W != n[0]) stop("Wrong width:",     W, n[0]);
      if (S != n[1]) stop("Wrong size",       S, n[1]);
      if (B != n[2]) stop("Wrong block size", B, n[2]);

      final int N = W * B;                                                      // The width of each memory element
      if (3 + memorySize * N != n.length) stop("Wrong number of bits");

      if (W >= Integer.SIZE)                                                    // Check that we can load the memory into an integer array.  Real implementations are likely to use much bigger memeory elements but integers are sufficient for initial testing.
       {stop("Memory elements too big to be held in integers");
       }

      final int[][]M = new int[S][B];                                           // Array of values to load into memeory reconstructed from string input

      for(int i = 0; i < memorySize; i++)                                       // Convert bits to integers
       {for (int j = 0; j < N; j++)
         {if (n[3+i*N+j] > 0) M [i] [j / W] |= (1 << (j % W));                  // Or each bit into the corresponding integer
         }

        for(int j = 0; j < B; j++)  M[i][j] &= (1 << W)-1;                      // Mask integers to match memory size so that they wrap in a way that matches verilog
       }
      memoryLoad(P, M);                                                         // Reload memory from the array of integers.
     }

    void memoryLoad(Process r, int [][]Load)                                    // Load memory from the specified process
     {N(); if (coverageAnalysis) zz();
      final int B = memoryWidth, N = memorySize, S = memoryBlockSize;           // Shorten names
      Register n  = r.register("n",  1+logTwo(N));                              // A register wide enough to address the memory elements

      Register ss = r.register("ss",  B*S);                                     // The single array to be loaded into memeory. Using a single array makes memory updates more efficient.
      Register sa = r.register("sa",  B, S);                                    // An arrayed register into which the user supplied values will be loaded before being copied into the single register
      Set      st = new Set(r);                                                 // The set transaction used to request updates to memory

      for   (int i = 0; i < memorySize;      i++)                               // Load arrayed register
       {for (int j = 0; j < memoryBlockSize; j++)
         {sa.RegisterSet((i < Load.length && j < Load[i].length ? Load[i][j] : 0), j);
         }
        n.RegisterSet(i);
        ss.RegisterCopySingleFromArray(sa);                                     // Linearize the arrayed register into the single register
        st.ExecuteTransaction(n, ss);                                           // Pass the single register to memeory through a transaction
        st.WaitResultOfTransaction();                                           // Wait for the update to complete
       }
     }

//D3 Get transaction                                                            // A transaction to read memory thus insuring that memory is only ever read from one always block

    class Get extends Process.Transaction                                       // Get a value from the memory controlled by this process
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The index of the element whose value is required
      final Register result;                                                    // The value of the memory from the specified index to the index plus the block size
      Get(Process Process)                                                      // Get values from memory  from the specified index to the index plus the block size for the calling process
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          Process, "get");
        process = Process;                                                      // The calling process requesting a value from the memory of this memory process
        index   = transactionCallingProcess.register                            // Register to index the memory to be read
         (transactionName+"_index", logTwo(memorySize));
        result  = new Register                                                  // Register to contain the result of the memory read
         (transactionName+"_result", memoryWidth * memoryBlockSize);
        transactionInputRegisters(index);                                       // Make the index an input register
        transactionOutputRegisters(result);                                     // Make the result an output register
        memoryGetFromProcess.put(Process.processName, this);                    // Make it possible to reuse this transaction from the calling process rather than creating a new one each time
       }

      void executeTransaction(Register Index)                                   // Execute the request in Java
       {R(); if (coverageAnalysis) zz();
        index.copy(Index);
        transactionSetExecutable();
       }

      void executeTransaction(Verilog v, Register Index)                        // Execute the request in verilog
       {if (coverageAnalysis) zz();
        index.copy(v, Index);
        transactionSetExecutable(v);
       }

      void ExecuteTransaction(Register Index)                                   // Execute the requested memory read instruction
       {if (coverageAnalysis) zz();
        process.new Instruction()
         {void action ()          {executeTransaction(   Index);}
          void verilog(Verilog v) {executeTransaction(v, Index);}
         };
       }

      void WaitResultOfTransaction()                                            // Wait for the request to finish
       {if (coverageAnalysis) zz();
        process.new Instruction(true)
         {void action()
           {if (transactionFinished()) process.Continue();
           }
          void verilog(Verilog v)
           {v.new If (transactionFinishedV())
             {void Then() {process.Continue(v);}
             };
           }
         };
       }
     } // Get

    Memory.Get memoryGetFromProcess(Process Caller)                             // Reuse the existing transaction if it exists else create a new one
     {if (coverageAnalysis) zz();
      if (memoryGetFromProcess.containsKey(Caller.processName))
       {return memoryGetFromProcess.get(Caller.processName);                    // Return existing transaction
       }
      return new Get(Caller);                                                   // Create a transaction to allow the caller to get items from this memory process
     }

//D3 Set transaction                                                            // A transaction to write memory thus insuring that memory is only ever written from one always block

    class Set extends Process.Transaction                                       // Set an indexed memory element to a specified value
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The elements from index to index plus block size will be set
      final Register value;                                                     // The values to be written into memory held in an arrayed register
      Set(Process Process)                                                      // Set the values of elements in memory from he indicated index to the index plus block size
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          Process, "set");
        process = Process;                                                      // The calling process requesting that a value be written into the memory of this process
        index   = transactionCallingProcess.register                            // Register to index the memory to be written
         (transactionName+"_index", logTwo(memorySize));
        value = transactionCallingProcess.new Register                          // Register containing the value to be written into memory
           (transactionName+"_value", memoryWidth * memoryBlockSize);

        transactionInputRegisters(index);                                       // Make the index an input register
        transactionInputRegisters(value);                                       // Make the value an input register
        memorySetIntoProcess.put(Process.processName, this);                    // Make it possible to reuse this transaction from the calling process rather than creating a new one each time
       }

      void executeTransaction(Register Index, Register Value)                   // Execute the requested memory update request
       {R(); if (coverageAnalysis) zz();
        index.copy(Index);
        if (Value.registerSingle()) value.copy(Value);
        else value.registerCopySingleFromArray(Value);
        transactionSetExecutable();
       }

      void executeTransaction(Verilog v, Register Index, Register Value)        // Execute the requested memory update in Verilog
       {if (coverageAnalysis) zz();
        index.copy(v, Index);
        if (Value.registerSingle()) value.copy(v, Value);
        else value.registerCopySingleFromArray(v, Value);
        transactionSetExecutable(v);
       }

      void ExecuteTransaction(Register Index, Register Value)                   // Execute the requested memory update instruction
       {if (coverageAnalysis) zz();
        process.new Instruction()
         {void action ()          {executeTransaction(   Index, Value);}
          void verilog(Verilog v) {executeTransaction(v, Index, Value);}
         };
       }

      void WaitResultOfTransaction()                                            // Wait for the update request to finish
       {if (coverageAnalysis) zz();
        process.new Instruction(true)
         {void action()
           {if (transactionFinished()) process.Continue();
           }
          void verilog(Verilog v)
           {v.new If(transactionFinishedV())
             {void Then() {process.Continue(v);}
             };
           }
         };
       }
     } // Set

    Memory.Set memorySetIntoProcess(Process Caller)                             // Reuse the existing transaction if it exists else create a new one
     {if (coverageAnalysis) zz();
      if (memorySetIntoProcess.containsKey(Caller.processName))
       {return memorySetIntoProcess.get(Caller.processName);                    // Return existing transaction
       }
      return new Set(Caller);                                                   // Create a transaction to allow the caller to set items into this memory process
     }

//D3 Get/Set memeory                                                            // Perform a read from memory or a write into memory using a single always block to control each memory

    void memoryProcessGenerate()                                                // Generate the code needed to support the transactions against this memory process
     {if (coverageAnalysis) zz();
      new Instruction(true)                                               // Loop on this single instruction processing memory requests
       {void action()                                                           // Process memory requests in Java
         {for (var t : transactions)                                            // All transactions
           {if (t.transactionExecutable())                                      // Executable transactions
             {if (t.transactionOpCode.equals("get"))                            // Memory get requests
               {final Register I = t.transactionInputRegisters .elementAt(0);   // Address index register which is single
                final Register V = t.transactionOutputRegisters.elementAt(0);   // Address value register which is arrayed
                memoryGet(V, I);                                                // Set output register with value of memory at index
                t.transactionSetFinished();                                     // Mark the transaction as complete
                break;                                                          // Execute one memory request per clock
               }
              else if (t.transactionOpCode.equals("set"))                       // Set an indexed memory element to a specified value
               {final Register I = t.transactionInputRegisters.elementAt(0);    // Address index register which is single
                final Register V = t.transactionInputRegisters.elementAt(1);    // Address value register which is arrayed
                memorySet(V, I);                                                // Set memory at indexed location
                t.transactionSetFinished();                                     // Mark the update transaction as complete
                break;                                                          // Execute one memory request per clock
               }
              else stop("Unknown memory process transaction request:",          // Unknown request
                         t.transactionOpCode);
             }
           }
         }

        void verilog(Verilog v)                                                 // Process memory requests in verilog
         {final String M = processMemoryName();
          for (var t : transactions)                                            // Each transaction
           {v.new ElseIf(t.transactionExecutableV(),                            // Else if chain
               t == transactions.firstElement(),
               t == transactions.lastElement())
             {void Then()
               {if (t.transactionOpCode.equals("get"))                          // Memory get requests
                 {final Register I = t.transactionInputRegisters .elementAt(0); // Address index register
                  final Register O = t.transactionOutputRegisters.elementAt(0); // Register to hold value of memory at index
                  memoryGet(v, O, I);                                           // Set output register with value of memory at index
                  t.transactionSetFinished(v);                                  // Mark the transaction as complete
                 }
                else if (t.transactionOpCode.equals("set"))                     // Set an indexed memory element to a specified value
                 {final Register I = t.transactionInputRegisters.elementAt(0);  // Address index register
                  final Register V = t.transactionInputRegisters.elementAt(1);  // Address value register
                  memorySet(v, V, I);                                           // Update memory at indexed location
                  t.transactionSetFinished(v);                                  // Mark the transaction as complete
                 }
               }
             };
           }
         }
       };
     }
   }

//D0 Tests                                                                      // Run tests

  static void test_stop()
   {final int B = 8, N = 16;
    var c = chip("Test");
    var p = c.process("Process");
    p.processTrace = true;
    p.ProcessStop(1);
    c.chipRun();
   }

  static void test_memory()
   {final int B = 4, N = 8, S = 2;
    var c  = chip("Test");
    var m  = c.new Memory("Memory", N, B, S);
    var r  = c.process("Requests");
        r.processTrace = true;

    var n  = r.register("n",   B*S);
    var o  = r.register("o",   B, S);

    var rs = r.register("rs",  B*S);
    var ra = r.register("ra",  B, S);
    var rt = m.new Get(r);

    final int[][]l = new int[N][S];
    for   (int i = 0; i < N; i++)
      for (int j = 0; j < S; j++) l[i][j] = S*i+j+1;
    m.memoryLoad(r, l);

    c.chipRun(1000);
    //stop(c);
    ok(""+c, """
Chip: Test             step: 49, maxSteps: 1000, running: 0
  Processes:
    Process: 0 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: size: 8, width: 4, block: 2
         1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  0
      Registers :
        Memory_Memory_1_result_0                           = 0
      Transactions:
        Transaction   : get      - Memory_1          requested at: -1, finished at: -1, returnCode: 0, executable: 0, finished: 0
          Inputs      :
            Requests_Memory_1_index_4                      = 0
          Outputs     :
            Memory_Memory_1_result_0                       = 0
        Transaction   : set      - Memory_2          requested at: 46, finished at: 47, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_2_index_8                      = 7
            Requests_Memory_2_value_9                      = 15
    Process: 1 - Requests              instructions: 48, pc: 48, rc: 0
      Registers :
        Requests_n_0                                       = 0
        Requests_o_1                                [   0] = 0
        Requests_o_1                                [   1] = 0
        Requests_rs_2                                      = 0
        Requests_ra_3                               [   0] = 0
        Requests_ra_3                               [   1] = 0
        Requests_Memory_1_index_4                          = 0
        Requests_n_5                                       = 7
        Requests_ss_6                                      = 15
        Requests_sa_7                               [   0] = 15
        Requests_sa_7                               [   1] = 0
        Requests_Memory_2_index_8                          = 7
        Requests_Memory_2_value_9                          = 15
""");

    n.RegisterSet(1);                                                           // Request the value of an indexed element of memory
    rt.ExecuteTransaction(n);                                                   // Request value of memory at the index
    rt.WaitResultOfTransaction();                                               // Request value of memory at the index
    o.RegisterCopyArrayFromSingle(rt.transactionOutputRegisters.firstElement());// Unpack results from memory

    c.chipRun(1000);
    //stop(c);
    ok(""+c, """
Chip: Test             step: 53, maxSteps: 1000, running: 0
  Processes:
    Process: 0 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: size: 8, width: 4, block: 2
         1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  0
      Registers :
        Memory_Memory_1_result_0                           = 67
      Transactions:
        Transaction   : get      - Memory_1          requested at: 49, finished at: 50, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_1_index_4                      = 1
          Outputs     :
            Memory_Memory_1_result_0                       = 67
        Transaction   : set      - Memory_2          requested at: 46, finished at: 47, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_2_index_8                      = 7
            Requests_Memory_2_value_9                      = 15
    Process: 1 - Requests              instructions: 52, pc: 52, rc: 0
      Registers :
        Requests_n_0                                       = 1
        Requests_o_1                                [   0] = 3
        Requests_o_1                                [   1] = 4
        Requests_rs_2                                      = 0
        Requests_ra_3                               [   0] = 0
        Requests_ra_3                               [   1] = 0
        Requests_Memory_1_index_4                          = 1
        Requests_n_5                                       = 7
        Requests_ss_6                                      = 15
        Requests_sa_7                               [   0] = 15
        Requests_sa_7                               [   1] = 0
        Requests_Memory_2_index_8                          = 7
        Requests_Memory_2_value_9                          = 15
""");
   }

  static void test_arithmeticFibonacci()
   {final int B = 16, N = 16;
    var C  = chip("Test");
    var p  = C.process("Main");
        p.processTrace = true;
    var a  = p.register("a", B);
    var b  = p.register("b", B);
    var c  = p.register("c", B);
    var i  = p.register("i", logTwo(B));

    var m  = C.new Memory("Memory", B, N);                                      // Memory controller
    var t  = m.new Set(p);                                                      // Create a transaction to update memory

    p.new Instruction()                                                         // Initialize
     {void action()
       {a.zero(); b.one(); i.zero();
       }
      void verilog(Verilog v)
       {a.zero(v); b.one(v); i.zero(v);
       }
     };

    for (int j = 0; j < N; j++)                                                 // Fibonacci numbers
     {c.Copy(a);

      p.new Instruction()
       {void action()
         {c.add(b); a.copy(b);
         }
        void verilog(Verilog v)
         {c.add(v, b); a.copy(v, b);
         }
       };
      p.new Instruction()
       {void action()
         {b.copy(c);
          t.executeTransaction(i, c);
         }
        void verilog(Verilog v)
         {b.copy(v, c);
          t.executeTransaction(v, i, c);
         }
       };

      i.Inc();

      t.WaitResultOfTransaction();                                              // Request value of memory at the index
     }

    p.ProcessStop(1);                                                           // Halt the run

    C.chipRun(100);
    //stop(C);
    ok(""+C, """
Chip: Test             step: 82, maxSteps: 100, running: 0
  Processes:
    Process: 0 - Main                  instructions: 82, pc: 82, rc: 1
      Registers :
        Main_a_0                                           = 987
        Main_b_1                                           = 1597
        Main_c_2                                           = 1597
        Main_i_3                                           = 0
        Main_Memory_1_index_4                              = 15
        Main_Memory_1_value_5                              = 1597
    Process: 1 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: size: 16, width: 16, block: 1
         1,  2,  3,  5,  8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597
      Registers :
      Transactions:
        Transaction   : set      - Memory_1          requested at: 78, finished at: 79, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Main_Memory_1_index_4                          = 15
            Main_Memory_1_value_5                          = 1597
""");

    //stop(C.chipPrintMemory());

    //stop(C.chipPrintMemory());
    ok(C.chipPrintMemory(), """
Chip: Test             step: 82, maxSteps: 100, running: 0
  Processes:
    Memory
      Memory: size: 16, width: 16, block: 1
         1,  2,  3,  5,  8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597
""");

    ok(a.registerGet(), 987);

    final Chip.SiliconCompiler S = C.new SiliconCompiler()                      // Create silicon compiler files
     {String description()
       {return C.chipName;
       }
     };
    say(S.launchFile);
    say(readFile(S.sourceFile).size());
   }

  static void test_block()
   {final int B = 16, N = 6;
    var C  = chip("Test");
    var p  = C.process("Main");
        p.processTrace = true;
    var a  = p.register("a",  B);
    var b  = p.register("b",  B);
    var c  = p.register("c",  B);
    var i  = p.register("i",  B);
    var k  = p.register("k",  B);

    final StringBuilder s = new StringBuilder();
    p.new Instruction()
     {void action()
       {a.registerSet(0);
        b.registerSet(1);
        c.registerSet(0);
        k.registerSet(0);
        i.registerSet(0);
       }
      void verilog(Verilog v)
       {a.registerSet(v, 0);
        b.registerSet(v, 1);
        c.registerSet(v, 0);
        k.registerSet(v, 0);
        i.registerSet(v, 0);
       }
     };

    p.new Block()
     {void code()
       {c.Copy(a); i.Inc();
        p.new Instruction()
         {void action()
           {c.add(b);
            k.gt(i, N);
           }
          void verilog(Verilog v)
           {c.add (v, b);
            k.gt(v, i, N);
           }
         };
        p.new Instruction(true)
         {void action()
           {s.append(" "+i.registerGet()+"=>"+c.registerGet());
            p.GoNotZero(end, k);
           }
          void verilog(Verilog v)
           {p.GoNotZero(v, end, k);
           }
         };

        a.Copy(b);

        p.new Instruction(true)
         {void action()
           {b.copy(c); p.Goto(start);
           }
          void verilog(Verilog v)
           {b.copy(v, c);
            p.Goto(v, start);
           }
         };
       }
     };

    C.chipRun(100);
    ok(s, " 1=>1 2=>2 3=>3 4=>5 5=>8 6=>13 7=>21");
   }

  static void test_if()
   {final int N = 16;
    var Z = chip("Test");
    var P = Z.process("Main");
        P.processTrace = true;
    var a = P.register("a", N);
    var b = P.register("b", 1);
    var B = P.register("B", N);
    var c = P.register("c", 1);
    var C = P.register("C", N);

    a.RegisterSet(1);

    b.Ge(a, 1);
    P.new If (b)
     {void Then() {B.RegisterSet(1);}
      void Else() {B.RegisterSet(2);}
     };

    c.Lt(a, 1);

    P.new If (c)
     {void Then() {C.RegisterSet(3);}
      void Else() {C.RegisterSet(4);}
     };

    Z.chipRun(20);
    //stop(Z);
    ok(""+Z, """
Chip: Test             step: 9, maxSteps: 20, running: 0
  Processes:
    Process: 0 - Main                  instructions: 11, pc: 11, rc: 0
      Registers :
        Main_a_0                                           = 1
        Main_b_1                                           = 1
        Main_B_2                                           = 1
        Main_c_3                                           = 0
        Main_C_4                                           = 4
""");
   }

  static void test_saveLoad()
   {final int B = 8, N = 16;
    var c = chip("Test");
    var p = c.new Process("main");
    var m = c.new Memory("Memory1", N, B);
    var n = c.new Memory("Memory2", N, B);

    final int[][]a = new int[N][1];

    for (int i = 0; i < N; i++) a[i][0] = i+1;
    m.memoryLoad(p, a);

    c.chipRun(100);
    //stop(c.chipPrintMemory());
    ok(c.chipPrintMemory(), """
Chip: Test             step: 97, maxSteps: 100, running: 0
  Processes:
    Memory1
      Memory: size: 16, width: 8, block: 1
         1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16
    Memory2
      Memory: size: 16, width: 8, block: 1
         0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
""");

    final String memory = """
8 16 1 1 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 1 0 0 0 0 0 1 0 1 0 0 0 0 0 0 1 1 0 0 0 0 0 1 1 1 0 0 0 0 0 0 0 0 1 0 0 0 0 1 0 0 1 0 0 0 0 0 1 0 1 0 0 0 0 1 1 0 1 0 0 0 0 0 0 1 1 0 0 0 0 1 0 1 1 0 0 0 0 0 1 1 1 0 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 1 0 0 0
""";
    //stop(m.memorySave());                                                      // Save the memory associated with this process so it can be reloaded later
    ok(m.memorySave(), memory);

    n.memoryLoad(p, memory);
    c.chipRun(1000);
    //stop(c.chipPrintMemory());
    ok(c.chipPrintMemory(), """
Chip: Test             step: 193, maxSteps: 1000, running: 0
  Processes:
    Memory1
      Memory: size: 16, width: 8, block: 1
         1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16
    Memory2
      Memory: size: 16, width: 8, block: 1
         1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16
""");

   }

  static void test_trace()
   {var c = chip("Test");
    var p = c.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);

    a.RegisterSet(1);

    p.new Instruction()
     {void action()
       {a.registerSet(1);
       }
      void verilog(Verilog v)
       {a.registerSet(v, 1);                                                    // Change to show that tracing works
       }
     };

    a.RegisterSet(1);
    c.chipRun();
   }

  static void test_zeroOne()
   {var c = chip("Test");
    var p = c.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);

    a.Zero();
    b.One();
    a.Inc();
    a.Inc();

    c.chipRun();
    ok(a.registerGet(), 2);
    ok(b.registerGet(), 1);
   }

  static void test_add()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);
    var c = p.register("c",  8);
    var d = p.register("d",  8);

    a.One();
    b.One();
    b.Inc();
    c.Copy(a); c.Add (b);
    d.Copy(a); d.Add1(b);

    C.chipRun();
    ok(a.registerGet(), 1);
    ok(b.registerGet(), 2);
    ok(c.registerGet(), 3);
    ok(d.registerGet(), 4);
   }

  static void test_average()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);
    var c = p.register("c",  8);

    a.RegisterSet(2);
    b.RegisterSet(5);
    c.Average(a, b);

    C.chipRun();
    ok(a.registerGet(), 2);
    ok(b.registerGet(), 5);
    ok(c.registerGet(), 3);
   }

  static void test_comparisons()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);
    var c = p.register("c",  8);
    var d = p.register("d",  8);

    var ge0 = p.register("ge0", 1);
    var gt0 = p.register("gt0", 1);
    var le0 = p.register("le0", 1);
    var lt0 = p.register("lt0", 1);
    var ne0 = p.register("ne0", 1);
    var eq0 = p.register("eq0", 1);

    var ge1 = p.register("ge1", 1);
    var gt1 = p.register("gt1", 1);
    var le1 = p.register("le1", 1);
    var lt1 = p.register("lt1", 1);
    var ne1 = p.register("ne1", 1);
    var eq1 = p.register("eq1", 1);

    a.Zero();
    b.One();
    c.RegisterSet(2);
    d.RegisterSet(3);

    ge0.Ge(a, b);
    gt0.Gt(a, b);
    le0.Le(b, a);
    lt0.Lt(b, a);
    ne0.Ne(a, a);
    eq0.Eq(a, b);

    ge1.Ge(b, a);
    gt1.Gt(b, a);
    le1.Le(a, b);
    lt1.Lt(a, b);
    ne1.Ne(a, b);
    eq1.Eq(b, b);

    C.chipRun(100);
    ok(ge0.registerGet(), 0);
    ok(gt0.registerGet(), 0);
    ok(le0.registerGet(), 0);
    ok(lt0.registerGet(), 0);
    ok(ne0.registerGet(), 0);
    ok(eq0.registerGet(), 0);

    ok(ge1.registerGet(), 1);
    ok(gt1.registerGet(), 1);
    ok(le1.registerGet(), 1);
    ok(lt1.registerGet(), 1);
    ok(ne1.registerGet(), 1);
    ok(eq1.registerGet(), 1);
   }

  static void test_combine()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);
    var A = p.register("A",  8);
    var B = p.register("B",  8);

    a.RegisterSet(0);
    b.RegisterSet(2);
    A.RegisterSet(1);
    B.RegisterSet(1);

    A.Combine(a);
    B.Combine(b);

    C.chipRun();

    ok(A.registerGet(), 1);
    ok(B.registerGet(), 2);
   }

  static void test_sum()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.register("a",  8);
    var b = p.register("b",  8);
    var c = p.register("d",  8);
    var d = p.register("d",  8);

    a.RegisterSet(1);
    b.RegisterSet(2);
    c.RegisterSet(3);
    d.RegisterSet(4);

    a.Sum(a, b, c, d);

    C.chipRun();

    ok(a.registerGet(), 10);
   }

  static void test_register_array()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.new Register("a", 8, 4);
    var b = p.new Register("b", 8, 4);
    var c = p.new Register("c", 8);
    var d = p.new Register("d", 8, 4);
    var e = p.new Register("e", 8, 4);

    b.RegisterSet(1, 0);
    b.RegisterSet(3, 1);
    b.RegisterSet(5, 2);
    b.RegisterSet(7, 3);
    d.RegisterSet(0, 0);
    d.RegisterSet(4, 1);
    d.RegisterSet(0, 2);
    d.RegisterSet(8, 3);
    c.Copy(b, 2);
    b.Combine(d);
    a.Copy(d);
    a.Zero(1);
    e.Copy(d);
    e.Zero();

    C.chipRun(100);
    //stop(C);
    ok(C, """
Chip: Test             step: 15, maxSteps: 100, running: 0
  Processes:
    Process: 0 - process               instructions: 14, pc: 14, rc: 0
      Registers :
        process_a_0                                 [   0] = 0
        process_a_0                                 [   1] = 0
        process_a_0                                 [   2] = 0
        process_a_0                                 [   3] = 8
        process_b_1                                 [   0] = 1
        process_b_1                                 [   1] = 4
        process_b_1                                 [   2] = 5
        process_b_1                                 [   3] = 8
        process_c_2                                        = 5
        process_d_3                                 [   0] = 0
        process_d_3                                 [   1] = 4
        process_d_3                                 [   2] = 0
        process_d_3                                 [   3] = 8
        process_e_4                                 [   0] = 0
        process_e_4                                 [   1] = 0
        process_e_4                                 [   2] = 0
        process_e_4                                 [   3] = 0
""");
   }

  static void test_register_array_one()
   {var C = chip("Test");
    var p = C.new Process("process");
    p.processTrace = true;
    var a = p.new Register("a", 8, 4);
    var b = p.new Register("b", 8, 4);

    a.Zero();
    a.One (1);
    a.One (3);
    b.Copy(a);
    a.Zero();
    C.chipRun();
    //stop(b);
    ok(b, "process_b_1 =   0  1  0  1");
    //stop(C);
    ok(C, """
Chip: Test             step: 6, maxSteps: 10, running: 0
  Processes:
    Process: 0 - process               instructions: 5, pc: 5, rc: 0
      Registers :
        process_a_0                                 [   0] = 0
        process_a_0                                 [   1] = 0
        process_a_0                                 [   2] = 0
        process_a_0                                 [   3] = 0
        process_b_1                                 [   0] = 0
        process_b_1                                 [   1] = 1
        process_b_1                                 [   2] = 0
        process_b_1                                 [   3] = 1
""");
   }

  static void test_copyArrayToSingle()
   {var C = chip("Test");
    var p = C.new Process("main");
        p.processTrace = true;
    var a = p.new Register("a", 4, 2);
    var b = p.new Register("b", 8);
    var c = p.new Register("c", 1, 8);

    a.RegisterSet(1, 0);
    a.RegisterSet(2, 1);
    b.RegisterCopySingleFromArray(a);
    c.RegisterCopyArrayFromSingle(b);

    C.chipRun();
    //stop(C);
    ok(""+C, """
Chip: Test             step: 5, maxSteps: 10, running: 0
  Processes:
    Process: 0 - main                  instructions: 4, pc: 4, rc: 0
      Registers :
        main_a_0                                    [   0] = 1
        main_a_0                                    [   1] = 2
        main_b_1                                           = 33
        main_c_2                                    [   0] = 1
        main_c_2                                    [   1] = 0
        main_c_2                                    [   2] = 0
        main_c_2                                    [   3] = 0
        main_c_2                                    [   4] = 0
        main_c_2                                    [   5] = 1
        main_c_2                                    [   6] = 0
        main_c_2                                    [   7] = 0
""");
   }

  static void test_register_hex()
   {var C = chip("Test");
    var p = C.new Process("main");
        p.processTrace = true;
    var a = p.new Register("a", 64);

    a.RegisterSet(17);
    C.chipRun();
    //stop(a.registerGetHex());
    ok(a.registerGetHex(), "0000000000000011x");
    //stop(C);
    ok(""+C, """
Chip: Test             step: 2, maxSteps: 10, running: 0
  Processes:
    Process: 0 - main                  instructions: 1, pc: 1, rc: 0
      Registers :
        main_a_0                                           = 0000000000000011x
""");
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_stop();
    test_memory();
    test_arithmeticFibonacci();
    test_block();
    test_if();
    test_saveLoad();
    test_trace();
    test_zeroOne();
    test_add();
    test_average();
    test_comparisons();
    test_combine();
    test_sum();
    test_register_array();
    test_register_array_one();
    test_copyArrayToSingle();
    test_register_hex();
   }

  static void newTests()                                                        // Tests being worked on
   {//oldTests();
    test_arithmeticFibonacci();
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
