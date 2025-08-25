//------------------------------------------------------------------------------
// B-tree implemented using block-based, asynchronous memory.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // B-tree implemented in a memory block on a silicon chip.
// Make add accept multiple arguments.
// Produce instruction level versions of each arithmetic operation
import java.util.*;

class Chip extends Test                                                         // A chip designed to manipulate a B-tree stored in a memory block
 {final String chipName;                                                        // The name of the chip
  final Children<Process>  processes = new Children<>();                        // A fixed set of processes for this chip in definition order
  final String         javaTraceFile = fn(Verilog.folder, "trace_java.txt");    // Java trace file for comparison with verilog
  final String      verilogTraceFile = fn(Verilog.folder, "trace_verilog.txt"); // Verilog trace file
  static boolean            chipStop = true;                                    // False when the chip is running, true when it is not
  int memoryProcessTransactionNumber = 0;                                       // Make transaction names unique
  int                           step;                                           // Current simulation step being executed
  int                       maxSteps = 10;                                      // Maximum number of steps to execute in the simulation
  boolean      nonBlockingAssignment = true;                                    // Use non blocking assignments in generated Verilog
  static boolean               debug = false;                                   // Debug when true

//D1 Chip                                                                       // A chip is constructed from a fixed number of communicating processes that execute code on the chip to produce the desired outputs from the inputs to the chip

  Chip(String Name)             {chipName = Name;}                              // Create a new chip
  Chip chip()                   {return this;}                                  // This chip
  static Chip chip(String Name) {return new Chip(Name);}                        // Create a new chip

  void chipRunJava()                                                            // Run the processes == programs defined on this chip using the Java implementation
   {for(Process p : processes) p.processInit();                                 // Initialize each process == program
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

  void chipRun()                                                                // Run the Java version followed by the Verilog version. The output of the Verilog version is compared with the expected output of the definitive Java version
   {chipRunJava();
    chipRunVerilog();
   }

  void R() {if ( chipStop) stop("Not running");}                                // Confirm that the simulation of the chip is running
  void N() {if (!chipStop) stop("Running");}                                    // Confirm that the simulation of the chip is not running

  String chipStopExpression()                                                   // The or of all the process stop fields as a process can only write directly to its own variables not to global ones.
   {final StringBuilder s = new StringBuilder();
    for (Process p: processes)                                                  // Each process
     {s.append(p.processStopName() + "||");
     }
    if (s.length() > 0) s.setLength(s.length()-2);
    return ""+s;
   }

//D2 Print                                                                      // Print the state of a chip

  public String chipPrintMemory()                                               // Print the memory of the Java emulation of the chip
   {final StringBuilder s = new StringBuilder();
        s.append(String.format(
         "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n",
          chipName, step, maxSteps, chipStop ? 0 : 1));

        s.append("  Processes:"+(" ".repeat(50)));
    for (int i = 0; i < 25; i++)
     {s.append(String.format(" %2d", i));
     }
    s.append("\n");

    for (Process p: processes)                                                  // Each process
     {if (p.hasMemory())                                                        // Print memory if this process has memory attached to it
       {s.append(String.format("    %-21s ", p.processName));
        s.append(String.format(
         "memory: %21d * %2d = %2d",
          p.memorySize, p.memoryWidth, p.memoryGet(0)));

        for (int i = 1; i < p.memorySize; i++)
         {s.append(String.format(" %2d", p.memoryGet(i)));
         }
        s.append("\n");
       }
      if (false)
       {s.append("      Registers :\n");
        for (Process.Register r: p.registers)                                   // Print registers associated with this process
         {s.append(String.format(
           "        %-32s = %1d\n",
          r.registerName(), r.registerGet()));
         }
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
         {s.append(String.format("      Memory: %1d * %1d = %1d",
            p.memorySize, p.memoryWidth, p.memoryGet(0)));

          for (int i = 1; i < p.memorySize; i++)
           {s.append(String.format(", %2d", p.memoryGet(i)));
           }
          s.append("\n");
         }

        s.append("      Registers :\n");
      for (Process.Register r: p.registers)                                     // Print registers associated with this process
       {s.append(String.format(
         "        Register: %-32s = %1d\n",
          r.registerName(), r.registerGet()));
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
             {s.append(String.format(
               "%s    %-38s = %1d\n",
               in, r.registerName(), r.registerGet()));
             }
           }

          if (t.transactionOutputRegisters.size() > 0)                          // Transaction outputs
           {s.append(in+"  Outputs     :\n");
            for (Process.Register r : t.transactionOutputRegisters)
             {s.append(String.format(
               "%s    %-38s = %1d\n",
               in, r.registerName(), r.registerGet()));
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

  void chipPrintFromVerilog(Verilog v)                                          // Verilog to print the state of the chip as a callable Verilog task
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
           {v.A("$fwrite(o, \"      Memory: %1d * %1d\", "+p.memorySize+", "+p.memoryWidth+");");
            v.A("$fwrite(o, \" = %1d\", "+p.processMemoryName()+"[0]);");
            for (int i = 1; i < p.memory.length; i++)
             {v.A("$fwrite(o, \", %2d\", "+p.processMemoryName()+"["+i+"]);");
             }
            v.A("$fwrite(o, \"\\n\");");
           }

          v.A("$fwrite(o, \"      Registers :\\n\");");
          for (Process.Register r: p.registers)                                 // Registers
           {v.A("$fwrite(o, \"        Register: %-32s = %1d\\n\", "+
             " \""+r.registerName()+"\", "+r.registerName()+");");
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
                for (Process.Register r : t.transactionInputRegisters)
                 {v.A("$fwrite(o, \"            %-38s = %1d\\n\", \""+
                      r.registerName()+"\", "+r.registerName()+");\n");
                 }
               }

              if (t.transactionOutputRegisters.size() > 0)                      // Transaction outputs
               {v.A("$fwrite(o, \"          Outputs     :\\n\");\n");
                for (Process.Register r : t.transactionOutputRegisters)
                 {v.A("$fwrite(o, \"            %-38s = %1d\\n\", \""+
                   r.registerName()+"\", "+r.registerName()+");\n");
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
module %s;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = %s;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = %d;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero
""", chipName, chipStopExpression(), maxSteps));

    v.indent(); v.indent(); v.indent();

    for(Process p: processes)                                                   // Single thread the processes in a constant order
     {v.A("processCurrent = "+p.processNumber+";");
      v.a("clock = 0; #1; clock = 1; #1; // "+p.processNameAndNumber());
     }
    v.A("if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match");
    v.end();
    v.A("if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted");
    v.end();

    v.parallel = nonBlockingAssignment;                                         // Use parallel assign in Verilog being matched with Java
    for(Process p: processes) p.generateProcessVerilog(v);                      // Generate

    chipPrintFromVerilog(v);
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
"rm -f %s %s; iverilog -g2012 -o %s %s && ./%s && rm ./%s",
n, verilogTraceFile, n, source, n, n);
    final var e = new ExecCommand(c);                                           // Run Verilog

    final FileCompareAndLocate fcl = new FileCompareAndLocate                   // Compare trace files
     (javaTraceFile, verilogTraceFile);

    if (fcl.matches) ok(true);                                                 // Passed
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
module %s(                                                                      // Test bench for database on a chip
  input                 clock,                                                  // Clock
  input                 reset,                                                  // Reset chip
""", chipName));
    v.indent();
    for(Process.Register r: registersInput ()) v.A(r.registerDeclareModuleParameter()+",");
    for(Process.Register r: registersOutput()) v.A(r.registerDeclareModuleParameter()+",");

    v.A("output wire            stop                                            // Program has stopped when this goes high");
    v.A(");");

    v.i("step");                                                                // Step of the simulation
    v.i("returnCode");                                                          // Return code
    v.A("assign stop = "+chipStopExpression()+";");                             // Stop if any process stops otherwise why do we need that process if we can continue without it

    v.new Always()                                                              // Execute next step in program
     {void Body()
       {v.new If("reset")
         {void Then()
           {v.assign("step", "-2");
           }
          void Else()
           {v.assign("step", "step + 1");
           }
         };
       }
     };

    for(Process p: processes) v.comment(p.processNameAndNumber());              // List processes

    for(Process p: processes) p.generateProcessVerilog(v);                      // Generate verilog

    v.endModule();


    final String source = fne(Verilog.folder, chipName, Verilog.ext);           // Source code in Verilog
    writeFile(source,  ""+v);
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

  class SiliconCompiler                                                         // Write to a file the silicon compiler python directives to place and route this design
   {final String    sourceFile = chipSynthesizeVerilog();                       // Source code written to a file
    final String    pythonFile = fne(Verilog.folder, chipName, Verilog.pyExt);  // Python commands to layout mask
    final String       sdcFile = fne(Verilog.folder, chipName, Verilog.sdcExt); // Constraints file
    final String    launchFile = fne(Verilog.folder, "launch", "sh");           // Launch file to run silicon compiler
    final StringBuilder python = new StringBuilder();
    final StringBuilder    sdc = new StringBuilder();
    final StringBuilder launch = new StringBuilder();

    SiliconCompiler()                                                           // Construct the silicon compiler commands
     {writePython();
      writeSdc();
      writeLaunch();
     }

    void writeLaunch()                                                          // Write launch file
     {launch.append(String.format("""
docker run --rm -it -v ~/btreeAsm/:/root/btreeAsm -w /root/btreeAsm btreeasm:v2 bash -ic "source /root/sc/bin/activate; python3 /root/btreeAsm/verilog/Btree.py"
"""));
     writeFile(launchFile, launch);
    }

    void writeSdc()                                                             // Write constraints file
     {sdc.append(String.format("""
create_clock -name clock -period 100 [get_ports {clock}]
"""));
     writeFile(sdcFile, sdc);
    }

    void writePython()                                                          // Construct the silicon compiler python commands
     {python.append(String.format("""
#!/usr/bin/env python3

from siliconcompiler import Chip                                                # import python package
from siliconcompiler.targets import freepdk45_demo

if __name__ == "__main__":
  chip = Chip('%s')                                                             # Create chip object.  The name is used to create the summary and mask image file
# chip.set('option', 'loglevel', 'warning')                                     # Warnings and above
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
    final BitSet[]           memory;                                            // Memory is represented as an array of bit vectors
    final BitSet[]           memoryBackUp;                                      // Before a Java run starts we back up the memory for this process so that we can start in the same state with the equivalent Verilog run allowing us to confirm that memory evolves in the same way for both Java and Verilog
    final Children<Transaction> transactions = new Children<>();                // Transactions representing work requests to this process
    final Children<Register> registers       = new Children<>();                // Registers used in this process
    final Stack<Instruction> code            = new Stack<>();                   // A fixed set of instructions for this process
    final Stack<Label>       labels          = new Stack<>();                   // Labels for instructions in this process
    int                      processPc       = 0;                               // The index of the next instruction to be executed
    int                      processRC       = 0;                               // The return code for this process
    boolean                  processStop     = false;                           // Stop this process - if any process gets stopped the whole chip is brought to a stop on the basis that it needs all of its processes to function
    boolean                  processTrace    = false;                           // Trace this process if true by writing location statements into the log file to identify where in the source code this instruction was generated

//D2 Instruction                                                                // An instruction represents code to be executed by a process in a single clock cycle == process step

    abstract class Instruction                                                  // Instructions implement the action of a program
     {final int instructionNumber;                                              // The number of this instruction
      final boolean mightJump;                                                  // The instruction might cause a jump
      final String traceBack = traceBack();                                     // Line at which this instruction was created
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

//D2 Block                                                                      // A block is a sequence of instructions that can be jumped out of to act like an if statament or restarted to act like a loop

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
     {v.new If(condition.registerName()+" >  0")
       {void Then() {v.assign(processPcName(), label.offset);}
        void Else() {Continue(v);}
       };
     }
    void GoZero     (Verilog v, Label label, Register condition)                // Go to a specified label if the value of a field is zero
     {v.new If(condition.registerName()+" == 0")
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

    abstract class If                                                           // An implementation of an if statement
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
        Then();
        new Instruction(true)
         {void action()
           {Goto(End);
           }
          void verilog(Verilog v)
           {Goto(v, End);
           }
         };
        Else.set();
        Else();
        End.set();
       }
      void Then() {}
      void Else() {}
     }

//D2 Register                                                                   // A register is a block of memory that can be accessed within the current clock cycle

    class Register                                                              // A register is a block of memory that can be accessed within the current clock cycle
     {final String registerBaseName;                                            // The base name of the register with a uniqu making number appended
      final String registerName;                                                // The name of the register
      final int    registerBits;                                                // Number of bits the register can hold
      BitSet  value = new BitSet();                                             // Current value of the register in Java
      boolean input = false, output = false;                                    // Register uis comnectd to input pins or output pins

      Register(String RegisterName, int RegisterBits)                           // Create the register
       {N();
        registerBaseName = RegisterName;
        final String n   = registerName = RegisterName + "_" + registers.size();
        registerBits     = RegisterBits;
        registers.put(n, this);                                                 // Save registers associated with each process
       }

      String registerName() {return processName + "_" + registerName;}          // Create a name for a register that includes its register name
      void input()                                                              // Mark a register a being connected to input pins
       {if (output) stop("Already used for output");
        input = true;
       }
      void output()                                                             // Mark a register a being connected to output pins
       {if (input) stop("Already used for input");
        output = true;
       }
      private String rn  () {return registerName();}                            // The shorter is his daughter
      private int    rg  () {return registerGet();}                             // The shorter is his daughter
      private void   rs  (int v) {registerSet(v);}                              // The shorter is his daughter
      Process registerProcess()  {return Process.this;}                         // Process associated with this register

      int registerGet()                                                         // Return the registerâs value as an integer
       {return value.length() == 0 ? 0 : (int) value.toLongArray()[0];          // Relies on the fact that this Java code is only used for testing, unlike the Verilog version
       }

      void registerSet(int Value)                                               // Set the value of the register from an integer
       {final int l = min(registerBits, Integer.SIZE-1);                        // The most bits we can hope to represent
        value.clear();                                                          // Zero the memory
        for (int i = 0; i < l; i++)                                             // Set each bit in the bitset if the corresponding bit in the value is set
         {if (((Value >> i) & 1) != 0) value.set(i);
         }
       }

      void registerSet(Verilog v, int Value)                                    // Set the value of the register from an integer
       {v.assign(registerName(), Value);
       }

      void RegisterSet(int Value)                                               // Set a register instruction
       {new Instruction()
         {void action()           {registerSet(Value);};
          void verilog(Verilog v) {registerSet(v, Value);};
         };
       }

      void copy(Register Source)                                                // Copy a source register into this register which we can do because each, and only each, process can write to its own registers
       {R();
        if (registerBits <= Source.registerBits)                                // Make sure the target register is big enough
         {if (false) stop("Target register is smaller than source register.\n",
           "Target register:", registerName, "has a size of:", registerBits,
           "while source register:", Source.registerName, "has a size of:",
            Source.registerBits);
         }

        value = (BitSet)Source.value.clone();                                   // Copy the source value into the target
       }

      void copy(Verilog v, Register Source) {v.assign(rn(), Source.rn());}      // Copy a source register into this register which we can do because each and only each process can write to its own registers

      void Copy(Register Source)                                                // Copy instruction
       {new Instruction()
         {void action()           {copy(Source);};
          void verilog(Verilog v) {copy(v, Source);};
         };
       }

      public String toString() {return registerName()+" = "+registerGet();}     // Print the register

      String registerDeclareModuleParameter()                                   // Declare a register in Verilog
       {if (input)
         {return "input wire ["+registerBits+"-1:0] "+registerBaseName;
         }
        else if (output)
         {return "output reg ["+registerBits+"-1:0] "+registerBaseName;
         }
        else                                                                    // The caller has not told us whether the parameter is for input or output
         {stop("Module parameter:", registerBaseName, "must be input or output");
          return null;
         }
       }

//D3 Arithmetic                                                                 // Operations on registers

      Process.Register zero() {R(); rs(0); return this;}                                                 // Zero a register in Java
      Process.Register one () {R(); rs(1); return this;}                                                 // One a register in Java
      Process.Register inc () {R(); rs(rg()+1); return this;}                                            // Increment a register in Java
      Process.Register dec () {R(); rs(rg()-1); return this;}                                            // Decrement a register in Java
      Process.Register not () {R(); rs(rg() != 0 ? 0 : 1); return this;}                                 // Not a register in Java
      Process.Register half() {R(); rs(rg() >> 1); return this;}                                         // Halve a register
      Process.Register add (Register source) {R(); rs(rg()+source.rg());   return this;}                 // Add the source register to the current register in Java
      Process.Register add1(Register source) {R(); rs(rg()+source.rg()+1); return this;}                 // Add the source register to the current register plus one in Java
      Process.Register add (int      source) {R(); rs(rg()+source);        return this;}                 // Add the source register to the current register in Java
      Process.Register average(Register source1, Register source2) {R(); rs((source1.rg()+source2.rg())/2); return this;} // Average of two registers

      Process.Register gt (Register a, Register b) {R(); rs(a.rg() >  b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register ge (Register a, Register b) {R(); rs(a.rg() >= b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register eq (Register a, Register b) {R(); rs(a.rg() == b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register ne (Register a, Register b) {R(); rs(a.rg() != b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register le (Register a, Register b) {R(); rs(a.rg() <= b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register lt (Register a, Register b) {R(); rs(a.rg() <  b.rg() ? 1 : 0); return this;}     // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      Process.Register gt (Register a, int b) {R(); rs(a.rg() >  b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register ge (Register a, int b) {R(); rs(a.rg() >= b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register eq (Register a, int b) {R(); rs(a.rg() == b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register ne (Register a, int b) {R(); rs(a.rg() != b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register le (Register a, int b) {R(); rs(a.rg() <= b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      Process.Register lt (Register a, int b) {R(); rs(a.rg() <  b ? 1 : 0); return this;}               // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void zero(Verilog v) {v.assign(rn(), "0");}                               // Zero a register in Verilog
      void one (Verilog v) {v.assign(rn(), "1");}                               // One a register in Verilog
      void inc (Verilog v) {v.assign(rn(), rn()+"+1");}                         // Increment a register in Verilog
      void dec (Verilog v) {v.assign(rn(), rn()+"-1");}                         // Decrement a register in Verilog
      void not (Verilog v) {v.assign(rn(), rn()+" != 0 ? 0 : 1");}              // Not a register in Verilog
      void half(Verilog v) {v.assign(rn(), rn()+" >> 1");}                      // Half a number
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
      void gt(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +"> "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +">="+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +"=="+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +"!="+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +"<="+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt(Verilog v, Register a, Register b) {v.assign(rn(), a.rn() +"< "+ b.rn()+" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0

      void gt(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +"> "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ge(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +">="+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void eq(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +"=="+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void ne(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +"!="+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void le(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +"<="+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0
      void lt(Verilog v, Register a, int b) {v.assign(rn(), a.rn() +"< "+ b +" ? 1 : 0");} // Set the target register to one if the test between the 'a' and 'b' register is true else 0

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

      void Zero()                                                               // Zero a register instruction
       {new Instruction()
         {void action()           {zero();};
          void verilog(Verilog v) {zero(v);};
         };
       }

      void One()                                                                // One a register instruction
       {new Instruction()
         {void action()           {one();};
          void verilog(Verilog v) {one(v);};
         };
       }

      void Inc()                                                                // Increment a register instruction
       {new Instruction()
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
       {new Instruction()
         {void action()           {add(   Source);};
          void verilog(Verilog v) {add(v, Source);};
         };
       }

      void Add1(Register Source)                                                // Add the value of a register plus one to the specified register as an instruction
       {new Instruction()
         {void action()           {add1(   Source);};
          void verilog(Verilog v) {add1(v, Source);};
         };
       }
      void Average(Register Source1, Register Source2)                          // Average of two registers as an instruction
       {new Instruction()
         {void action()           {average(   Source1, Source2);};
          void verilog(Verilog v) {average(v, Source1, Source2);};
         };
       }
     } // Register

    Register register(String RegisterName, int RegisterBits)                    // Create the register
     {N(); return new Register(RegisterName, RegisterBits);
     }

//D2 Process                                                                    // Start, step and stop a process while modifying its memory

    Process(String ProcessName) {this(ProcessName, 0, 0);}                      // Create a process without any memory attached to it

    Process(String ProcessName, int MemorySize, int MemoryWidth)                // Create a process with the specified memory attached to it
     {N();
      processName   = ProcessName;
      processNumber = processes.size();
      processes.put(processName, this);

      memorySize    = MemorySize;                                               // Create memory
      memoryWidth   = MemoryWidth;
      memory        = new BitSet[memorySize];
      memoryBackUp  = new BitSet[memorySize];
      for (int i = 0; i < memorySize; i++) memory[i] = new BitSet(memoryWidth);
     }

    void processInit()                                                          // Get ready to execute the program
     {N(); processPc = 0;                                                       // Program always starts at the first instruction
      memoryBackUp();                                                           // Back up memory for each process so we can start in the same state in Verilog
      for (Register r: registers) r.registerSet(0);                             // Clear all registers
      for (Transaction t: transactions)                                         // Initialize transactions
       {t.transactionRc          = 0;
        t.transactionFinishedAt  = -1;
        t.transactionRequestedAt = -1;
       }
     }

    void processStep()                                                          // Execute one step in the program
     {if (code.size() == 0) return;                                             // No code to run
      if (processPc >= code.size())                                             // Stop the run if we go off the end of the code
       {//err("Stopped by prcess", processName);
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

    void processClear() {code.clear();}                                         // Clear current process code. This facilitates testing by allowing a program to be written and executed incrementally.

    void processStop(int ReturnCode)                                            // Stop the chip
     {processRC = ReturnCode;
      processStop = chipStop = true;
     }

    void processStop(Verilog v, int ReturnCode)                                 // Stop the chip in Verilog
     {v.assign(processRCName(), ReturnCode);
      v.assign(processStopName(),       1);
     }

    void ProcessStop(int ReturnCode)                                            // Process stop instruction
       {new Instruction()
         {void action()           {processStop(ReturnCode);};
          void verilog(Verilog v) {processStop(v, ReturnCode);};
         };
       }

//D3 Verilog                                                                    // Generate a Verilog always block to implement this process

    String generateProcessVerilog(Verilog v)                                    // Generate Verilog code for this process
     {N();
      v.comment("Process: "+processName+"  "+processNameAndNumber());
      if (hasMemory())                                                          // Not all processes have memory attached to them: declare memory for those that do.
       {v.A("(* ram_style = \"block\" *)");
        v.A("reg ["+memoryWidth+"-1:0] "+processMemoryName()+"["+memorySize+"];");
       }

      for (Register r: registers)                                               // Registers associated with this process
       {v.A("reg ["+r.registerBits+"-1:0] "+r.registerName()+";");
       }

      for (Transaction t: transactions)                                         // Transactions associated with this process
       {final String n = t.transactionName;
        v.i(n+"_requestedAt");
        v.i(n+"_finishedAt");
        v.i(t.transactionRcName());
       }

      v.i(processPcName());
      v.i(processStopName());
      v.i(processRCName());
      v.new Always()
       {void Body()
         {v.new If("step < 0")                                                  // Execute next step in program
           {void Then()                                                         // Steps less than zero are used for initilization
             {v.assign(processPcName(),     "0");                               // Program counter for this process
              v.assign(processStopName(),   "0");                               // Stop process when true
              v.assign(processRCName(),     "0");                               // Return code after stopping
              for (Register r: registers)                                       // Clear all registers
               {if (!r.input && !r.output) v.assign(r.registerName(), "0");
               }
              for (Transaction t: transactions)                                 // Initialize transactions
               {v.assign(t.transactionName+"_finishedAt", "-1");
                v.assign(t.transactionRcName(), "0");
               }

              for (Process p: processes)                                        // Find transactions of which we are the source
               {for (Transaction t: p.transactions)
                 {if (t.transactionCallingProcess == Process.this)              // This transaction is a source of requests against this process
                   {final String n = t.transactionName;
                    v.assign(t.transactionName+"_requestedAt", "-1");           // Clear step at which the transaction was requested
                   }
                 }
               }

              if (hasMemory())                                                  // Load memory to match the state at the start of the Java run
               {for(int i = 0; i < memorySize; i++)
                 {v.assign(processMemoryName()+"["+i+"]", memoryGetBackUp(i));
                 }
               }

              v.end();
              if (v.synthesis) v.A("else begin                                   // Run the process in full parallel");
              else v.A(String.format("else if (processCurrent == %s) begin", processNumber));

              v.indent();
//            v.assign(processNextPcName(), "-1");
              v.A(String.format("case(%s)", processPcName()));                  // Execute instructions in process
              v.indent();

              for(Instruction i: code)                                          // Each instruction
               {v.A(""+i.instructionNumber+": begin");
                v.indent();
                final Verilog V = new Verilog();
                V.parallel  = v.parallel;
                V.synthesis = v.synthesis;
                i.verilog(V);
                //if (V.isEmpty()) err("No Verilog generated by this instruction:\n"+i.traceBack);

                if (!i.mightJump) V.inc(processPcName());                       // Increment program counter if the instruction does not already handle this

                if (processTrace)                                               // Add a location statement if this process is being traced. Domne in  line because Icarus does not pass the string correctly as a parameter
                 {if (!V.synthesis)                                             // Add a location statement if this process is being traced. Domne in  line because Icarus does not pass the string correctly as a parameter
                   {V.begin("f");
                    V.A("f = $fopen(\""+verilogTraceFile+"\", \"a\");");
                    V.A("$fdisplay(f, \"Location: "+i.traceBackOnOneLine()+"\");");
                    V.A("$fclose(f);");
                    V.end();
                   }
                 }
                v.A(V);
                v.end();
               }
              v.A("default: "+processStopName()+" "+v.assignOp()+" 1;");
              v.endCase();
             }
           };
         }
       };
      return ""+v;
     }

    String processNameAndNumber()                                               // Used to generate skip to comments
     {return String.format("process_%s_%04d", processName, processNumber);
     }

    String processPcName()     {return processName+"_pc";}                      // Program counter
//  String processNextPcName() {return processName+"_next_pc";}                 // Next program counter
    String processMemoryName() {return processName+"_memory";}                  // Name of the memory block used by this process
    String processStopName()   {return processName+"_stop";}                    // Name of the stop field in verilog for this process
    String processRCName()     {return processName+"_returnCode";}              // Name of the return code in verilog for this process

    boolean hasMemory()        {return memoryWidth > 0 && memorySize > 0;}      // Whether this process has any memory attached directly to it

//D3 Memory                                                                     // Process operations on memory

    protected int memoryGetBackUp(int Index)                                    // Get the value of the indexed location in the backup copy of memory made at the start of the Java run so we could confirm that memory evolves from a known state
     {final BitSet b = memoryBackUp[Index];                                     // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      return V.length > 0 ? (int)b.toLongArray()[0] : 0;                        // Take the first element if it exists relying on the fact that in the Java code we test with just sufficiently large numbers to test the Verilog in principle
     }

    protected int memoryGet(int Index)                                          // Get a memory element as an integer
     {final BitSet b = memory[Index];                                           // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      return V.length > 0 ? (int)b.toLongArray()[0] : 0;                        // Take the first element if it exists relying on the fact that in the Java code we test with just sufficiently large numbers to test the Verilog in principle
     }

    void memoryGet(Register Value, Register Index)                              // Get a memory element indexed by a register as an integer setting the memory cache register to the value of the element retrieved
     {final BitSet b = (BitSet)memory[Index.registerGet()].clone();             // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      Value.registerSet(V.length > 0 ? (int)b.toLongArray()[0] : 0);            // Take the first element if it exists relying on the fact that in the Java code we test with just sufficiently large numbers to test the verilog in priniple
     }

    void memoryGet(Verilog v, Register Value, Register Index)                   // Get a memory element indexed by a register as an integer setting the memory cache register to the value of the element retrieved
     {v.assign(Value.registerName(),                                            // Read memory inrto register
               processMemoryName()+"["+Index.registerName()+"]");
     }

    void memorySet(Register Value, Register Index)                              // Set a memory element indexed by a register from the associated cache memory register
     {memory[Index.registerGet()] = (BitSet)Value.value.clone();
     }

    void memorySet(Verilog v, Register Value, Register Index)                   // Set a memory element indexed by a register from the associated cache memory register in Verilog
     {v.assign(processMemoryName()+"["+Index.registerName()+"]",
       Value.registerName());
     }

    void memorySet(int Value, int Index)                                        // Set a memory element
     {final int l = min(memoryWidth, Integer.SIZE-1);                           // The most bits we can hope to represent
      final BitSet v = memory[Index];                                           // Memory element
      v.clear();                                                                // Zero the memory
      for (int i = 0; i < l; i++)                                               // Set each bit in the bitset if the corresponding bit in the value is set
       {if (((Value >> i) & 1) != 0) v.set(i);
       }
     }

    void memorySet(Verilog v, int Value, int Index)                             // Set a memory element in Verilog
     {v.assign(processMemoryName()+"["+Index+"]", Value);
     }

    void memoryBackUp()                                                         // Back up memory
     {for (int i = 0; i < memory.length; i++)
       {memoryBackUp[i] = (BitSet)memory[i].clone();
       }
     }

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
       {N();
        transactionName = Name;
        transactionCallingProcess = CallingProcess;
        transactionOpCode = OpCode;
        transactions.put(transactionName, this);
       }

      void transactionInputRegisters(Process.Register...InputRegisters)         // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
       {N();
        for(Process.Register r : InputRegisters)                                // Save input registers
         {transactionInputRegisters.put(r.registerName(), r);
         }
       }

      void transactionOutputRegisters(Process.Register...OutputRegisters)       // The registers used as outputs by this transaction. As they are going to be written into by the transaction they have to be owned by the process executing the transaction
       {N();
        for(Process.Register r : OutputRegisters)                               // Save output registers
         {if (r.registerProcess() != transactionProcess())                      // Check that the output registers are owned by the target process of the transaction as that process is the only process that can write into them
           {stop("Output transaction register:",
             r.registerName()+" must be owned by process: "+
             transactionProcess().processName+"\n");
           }
          transactionOutputRegisters.put(r.registerName(), r);
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
        N(); return "("+r+" > "+f + " && "+r + " != step)";
       }

      String transactionFinishedV()                                             // Whether the transaction has finished or not in verilog
       {final String r = transactionRequestedAt();
        final String f = transactionFinishedAt();
        N(); return "("+r+" < "+f+")";
       }

      void transactionSetExecutable()                                           // Mark a transaction as executable
       {R();
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

    Transaction transaction(String Name, Process CallingProcess, String OpCode) // Transactions allow one process to request services from another process by supplying the service name and the input and output registers
     {return new Transaction(Name, CallingProcess, OpCode);
     }

//D3 Store and Retrieve                                                         // Save memory to a string or reload memory from a string

    String processSave()                                                        // Save memory
     {final StringBuilder s = new StringBuilder();
      s.append(" "+memoryWidth);
      s.append(" "+memorySize);
      for (int i = 0; i < memorySize; i++)
       {s.append(" "+memoryGet(i));
       }
      return (""+s).trim()+"\n";
     }

    void processLoad(String line)                                               // Load memory from a string
     {final String[]w = line.trim().split("\\s+");
      final int   []n = new int[w.length];
      for (int i = 0; i < w.length; i++) n[i] = Integer.parseInt(w[i]);

      if (memoryWidth != n[0]) stop("Wrong width:", memoryWidth, n[0]);
      if (memorySize  != n[1]) stop("Wrong size",   memorySize,  n[1]);
      for (int i = 0; i < memorySize; i++)
       {memorySet(n[i+2], i);
       }
     }
   } // Process

  Process process(String ProcessName, int MemorySize, int MemoryWidth)          // Create a process with attached memory
   {return new Process(ProcessName, MemorySize, MemoryWidth);
   }

  Process process(String ProcessName)                                           // Create a process without attached memory
   {return new Process(ProcessName, 0, 0);
   }

//D2 Memory Process                                                             // Processes that manage memory used by other processes

  class Memory extends Process                                                  // A process whose main purpose is to maintain memory
   {final Map<String, Get> memoryGetFromProcess = new TreeMap<>();              // Locate a get transaction associated with this memory by name
    final Map<String, Set> memorySetIntoProcess = new TreeMap<>();              // Locate a set transaction associated with this memory by name
    Memory(String ProcessName, int MemorySize, int MemoryWidth)                 // Create a memory process
     {super(ProcessName, MemorySize, MemoryWidth);
      memoryProcessGenerate();                                                  // Generate the code to execute this process
     }
    class Get extends Process.Transaction                                       // Get a value from the memory controlled by this process
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The index of the element whose value is required
      final Register result;                                                    // The value of the memory at the specified index
      Get(Process Process)                                                      // Get a value from memory at the indicated index for the calling process
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          Process, "get");
        process = Process;                                                      // The calling process requesting a value from the memory of this memory process
        index   = transactionCallingProcess.register(transactionName+"_index", logTwo(memorySize));  // A register that will index the memory managed by this process
        result  = register(transactionName+"_result", memoryWidth);             // The register that will contain the result
        transactionInputRegisters(index);                                       // Make the index an input register
        transactionOutputRegisters(result);                                     // Make the result an output register
        memoryGetFromProcess.put(Process.processName, this);                    // Make it possible to reuse this transaction from the calling proccess rather than creating a new one each time
       }

      void executeTransaction(Register Index)                                   // Execute the request in Java
       {R(); index.copy(Index);
        transactionSetExecutable();
       }

      void executeTransaction(Verilog v, Register Index)                        // Execute the request in verilog
       {index.copy(v, Index);
        transactionSetExecutable(v);
       }

      void ExecuteTransaction(Register Index)                                   // Execute the requested memory read instruction
       {process.new Instruction()
         {void action ()          {executeTransaction(   Index);}
          void verilog(Verilog v) {executeTransaction(v, Index);}
         };
       }

      void waitResultOfTransaction()                                            // Wait for the request to finish
       {process.new Instruction(true)
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
     {if (memoryGetFromProcess.containsKey(Caller.processName))
       {return memoryGetFromProcess.get(Caller.processName);                    // Return existing transaction
       }
      return new Get(Caller);                                                   // Create a transaction to allow the caller to get items from this memory process
     }

    class Set extends Process.Transaction                                       // Set an indexed memory element to a specified value
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The index of the element whose value is required
      final Register value;                                                     // The value to be written into memory
      Set(Process Process)                                                      // Get the value from memory at the indicated index
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          Process, "set");
        process = Process;                                                      // The calling process requesting that a value be written into the memory of this process
        index   = transactionCallingProcess.register(transactionName+"_index", logTwo(memorySize)); // A register that will index the memory managed by this process
        value   = transactionCallingProcess.register(transactionName+"_value", memoryWidth);        // The register that will contain the value to be written into memory
        transactionInputRegisters(index);                                       // Make the index an input register
        transactionInputRegisters(value);                                       // Make the value an input register
        memorySetIntoProcess.put(Process.processName, this);                    // Make it possible to reuse this transaction from the calling proccess rather than creating a new one each time
       }

      void executeTransaction(Register Index, Register Value)                   // Execute the requested memory update request
       {R(); index.copy(Index);
        value.copy(Value);
        transactionSetExecutable();
       }

      void executeTransaction(Verilog v, Register Index, Register Value)        // Execute the requested memory update in Verilog
       {index.copy(v, Index);
        value.copy(v, Value);
        transactionSetExecutable(v);
       }

      void ExecuteTransaction(Register Index, Register Value)                   // Execute the requested memory update instruction
       {process.new Instruction()
         {void action ()          {executeTransaction(   Index, Value);}
          void verilog(Verilog v) {executeTransaction(v, Index, Value);}
         };
       }

      void waitResultOfTransaction()                                            // Wait for the update request to finish
       {process.new Instruction(true)
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
     {if (memorySetIntoProcess.containsKey(Caller.processName))
       {return memorySetIntoProcess.get(Caller.processName);                    // Return existing transaction
       }
      return new Set(Caller);                                                   // Create a transaction to allow the caller to set items into this memory process
     }

    void memoryProcessGenerate()                                                // Generate the code needed to support the transactions against this memory process
     {new Instruction(true)                                                     // Loop on this single insruction processing memory requests
       {void action()                                                           // Process memory requests in Java
         {for (var t : transactions)                                            // All transactions
           {if (t.transactionExecutable())                                      // Executable transactions
             {if (t.transactionOpCode.equals("get"))                            // Memory get requests
               {final Register I = t.transactionInputRegisters.elementAt(0);    // Address index register
                final Register O = t.transactionOutputRegisters.elementAt(0);   // Register to hold value of memory at index
                memoryGet(O, I);                                                // Set output register with value of memory at index
                t.transactionSetFinished();                                     // Mark the transaction as complete
                break;                                                          // Execute one memory request per clock
               }
              else if (t.transactionOpCode.equals("set"))                       // Set an indexed memory element to a specified value
               {final var I = t.transactionInputRegisters.elementAt(0);         // Address index register
                final var V = t.transactionInputRegisters.elementAt(1);         // Address value register
                memorySet(V, I);                                                // Set memory at indexed location
                t.transactionSetFinished();                                     // Mark the update transaction as complete
                break;                                                          // Execute one memory request per clock
               }
              else stop("Unknown memory process transaction request:", t.transactionOpCode); // Unknown request
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
                 {final Register I = t.transactionInputRegisters.elementAt(0);  // Address index register
                  final Register O = t.transactionOutputRegisters.elementAt(0); // Register to hold value of memory at index
                  memoryGet(v, O, I);                                           // Set output register with value of memory at index
                  t.transactionSetFinished(v);                                  // Mark the transaction as complete
                 }
                else if (t.transactionOpCode.equals("set"))                     // Set an indexed memory element to a specified value
                 {final var I = t.transactionInputRegisters.elementAt(0);       // Address index register
                  final var V = t.transactionInputRegisters.elementAt(1);       // Address value register
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

//D2 Tests                                                                      // Run tests

  static void test_stop()
   {final int B = 8, N = 16;
    var c = chip("Test");
    var p = c.process("Process");
    p.processTrace = true;
    p.ProcessStop(1);
    c.chipRun();
   }

  static void test_memoryProcessReuse()
   {final int B = 8, N = 16;
    var c  = chip("Test");
    var m  = c.new Memory("Memory", N, B);
    var r  = c.process("Requests");
    r.processTrace = true;
    var ri = r.register("index",  B);
    var rt = m.new Get(r);

    r.new Instruction()                                                         // Preload memory
     {void action()
       {for (int i = 0; i < N; i++)
         {m.memorySet(i+1, i);
         }
       }

      void verilog(Verilog v)
       {for (int i = 0; i < N; i++)
         {m.memorySet(v, i+1, i);
         }
       }
     };

    ri.RegisterSet(1);                                                          // Request the value of an indexed element of memory

    final Memory.Get g = m.memoryGetFromProcess(r);                             // Find the transaction so it can be reused
    g.ExecuteTransaction(ri);                                               // Request value of memory at the index
    g.waitResultOfTransaction();                                                // Request value of memory at the index

    c.chipRunJava();

    ok(rt.transactionOutputRegisters.firstElement().registerGet(), 2);
    //stop(c);
    ok(""+c, """
Chip: Test             step: 5, maxSteps: 10, running: 0
  Processes:
    Process: 0 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: 16 * 8 = 1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16
      Registers :
        Register: Memory_Memory_1_result_0         = 2
      Transactions:
        Transaction   : get      - Memory_1          requested at: 2, finished at: 3, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_1_index_1              = 1
          Outputs     :
            Memory_Memory_1_result_0               = 2
    Process: 1 - Requests              instructions: 4, pc: 4, rc: 0
      Registers :
        Register: Requests_index_0                 = 1
        Register: Requests_Memory_1_index_1        = 1
""");
    c.chipRunVerilog();
   }

  static void test_memoryProcessParallel()
   {final int B = 8, N = 16;
    var c  = chip("Test");
    var m  = c.new Memory("Memory", N, B);
    var r  = c.process("Requests");
        r.processTrace = true;
    var ri = r.register("index1",  B);
    var rt = m.new Get(r);

    var si = r.register("index2",  B);
    var st = m.new Get(r);

    var tv = r.register("value",   B);
    var tt = m.new Set(r);

    r.new Instruction()                                                         // Preload memory
     {void action()
       {for (int i = 0; i < N; i++)
         {m.memorySet(i+1, i);
         }
       }

      void verilog(Verilog v)
       {for (int i = 0; i < N; i++)
         {m.memorySet(v, i+1, i);
         }
       }
     };

    ri.RegisterSet(1);                                                          // Index of memory requested

    rt.ExecuteTransaction(ri);                                              // Request value of memory at the index

    si.RegisterSet(2);                                                      // Index of memory requested

    st.ExecuteTransaction(si);                                              // Request value of memory at the index
    st.waitResultOfTransaction();                                               // Request value of memory at the index
    rt.waitResultOfTransaction();                                               // Request value of memory at the index

    tv.RegisterSet(33);                                                     // Value to set into memory

    tt.ExecuteTransaction(si, tv);                                          // Request value of memory at the index
    tt.waitResultOfTransaction();                                               // Request value of memory at the index

    r.ProcessStop(1);                                                          // Halt the run

    c.maxSteps = 100;
    c.chipRun();

    ok(rt.transactionOutputRegisters.firstElement().registerGet(), 2);
    ok(st.transactionOutputRegisters.firstElement().registerGet(), 3);
    //stop(c);
    ok(""+c, """
Chip: Test             step: 11, maxSteps: 100, running: 0
  Processes:
    Process: 0 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: 16 * 8 = 1,  2, 33,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16
      Registers :
        Register: Memory_Memory_1_result_0         = 2
        Register: Memory_Memory_2_result_1         = 3
      Transactions:
        Transaction   : get      - Memory_1          requested at: 2, finished at: 3, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_1_index_1              = 1
          Outputs     :
            Memory_Memory_1_result_0               = 2
        Transaction   : get      - Memory_2          requested at: 4, finished at: 5, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_2_index_3              = 2
          Outputs     :
            Memory_Memory_2_result_1               = 3
        Transaction   : set      - Memory_3          requested at: 8, finished at: 9, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Requests_Memory_3_index_5              = 2
            Requests_Memory_3_value_6              = 33
    Process: 1 - Requests              instructions: 11, pc: 11, rc: 1
      Registers :
        Register: Requests_index1_0                = 1
        Register: Requests_Memory_1_index_1        = 1
        Register: Requests_index2_2                = 2
        Register: Requests_Memory_2_index_3        = 2
        Register: Requests_value_4                 = 33
        Register: Requests_Memory_3_index_5        = 2
        Register: Requests_Memory_3_value_6        = 33
""");
   }

  static void test_arithmeticFibonacci()
   {final int B = 16, N = 16;
    var C  = chip("Test");
    var p  = C.process("Main");
        p.processTrace = true;
    var a  = p.register("a",  B);
    var b  = p.register("b",  B);
    var c  = p.register("c",  B);
    var i  = p.register("i",  B);

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

      t.waitResultOfTransaction();                                              // Request value of memory at the index
     }

    p.ProcessStop(1);                                                           // Halt the run

    C.maxSteps = 100;
    C.chipRun();
    //stop(C);
    ok(""+C, """
Chip: Test             step: 82, maxSteps: 100, running: 0
  Processes:
    Process: 0 - Main                  instructions: 82, pc: 82, rc: 1
      Registers :
        Register: Main_a_0                         = 987
        Register: Main_b_1                         = 1597
        Register: Main_c_2                         = 1597
        Register: Main_i_3                         = 16
        Register: Main_Memory_1_index_4            = 15
        Register: Main_Memory_1_value_5            = 1597
    Process: 1 - Memory                instructions: 1, pc: 0, rc: 0
      Memory: 16 * 16 = 1,  2,  3,  5,  8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597
      Registers :
      Transactions:
        Transaction   : set      - Memory_1          requested at: 78, finished at: 79, returnCode: 0, executable: 0, finished: 1
          Inputs      :
            Main_Memory_1_index_4                  = 15
            Main_Memory_1_value_5                  = 1597
""");

    //stop(C.chipPrintMemory());

    ok(C.chipPrintMemory(), """
Chip: Test             step: 82, maxSteps: 100, running: 0
  Processes:                                                    0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
    Memory                memory:                    16 * 16 =  1  2  3  5  8 13 21 34 55 89 144 233 377 610 987 1597
""");

    ok(a.registerGet(), 987);
    final var s = C.new Synthesize();
    ok(s.e.err, "");
    ok(s.e.out, "");
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

    C.maxSteps = 100;
    C.chipRunJava();
    ok(s, " 1=>1 2=>2 3=>3 4=>5 5=>8 6=>13 7=>21");
    C.chipRunVerilog();
   }

  static void test_if()
   {final int B = 16;
    var C  = chip("Test");
    var p  = C.process("Main");
        p.processTrace = true;
    var a  = p.register("a",  B);
    var b  = p.register("b",  B);

    final StringBuilder s = new StringBuilder();                                //

    a.RegisterSet(1);
    b.RegisterSet(0);

    p.new Instruction() {void action() {b.ge(a, 1);}};

    p.new If (b)
     {void Then()
       {p.new Instruction() {void action() {s.append("1");}};
       }
      void Else()
       {p.new Instruction() {void action() {s.append("2");}};
       }
     };

    p.new Instruction() {void action() {b.lt(a, 1);}};

    p.new If (b)
     {void Then()
       {p.new Instruction() {void action() {s.append("3");}};
       }
      void Else()
       {p.new Instruction() {void action() {s.append("4");}};
       }
     };
    C.chipRunJava();
    ok(s, "14");
   }

  static void test_saveLoad()
   {final int B = 8, N = 16;
    var c = chip("Test");
    var m = c.new Memory("Memory", N, B);
    for (int i = 0; i < N; i++)
     {m.memorySet(i+1, i);
     }

    m.processLoad(m.processSave());
    //stop(c.chipPrintMemory());
    ok(c.chipPrintMemory(), """
Chip: Test             step: 0, maxSteps: 10, running: 0
  Processes:                                                    0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
    Memory                memory:                    16 *  8 =  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16
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

    C.maxSteps = 100;
    C.chipRun();
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

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_stop();
    test_memoryProcessReuse();
    test_memoryProcessParallel();
    test_arithmeticFibonacci();
    test_block();
    test_if();
    test_saveLoad();
    test_trace();
    test_zeroOne();
    test_add();
    test_average();
    test_comparisons();
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
