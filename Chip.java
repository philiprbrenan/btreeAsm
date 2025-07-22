//------------------------------------------------------------------------------
// Btree using block, asynchronous memory.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.

import java.util.*;

class Chip extends Test                                                         // A chip designed to manipulate a btree in a block of memory
 {final String chipName;                                                        // The name of the chip
  final Children<Process> processes = new Children<>();                         // A fixed set of processes for this chip in definition order
  boolean running;                                                              // True when the chip is running
  int step;                                                                     // Step we are executing in the run
  int maxSteps = 10;                                                            // Maximum number of steps to executre in the simulation
  int returnCode;                                                               // The return code from the first process to finish which effectively terminates the simulation

  Chip(String Name)                                                             // Create a new chip
   {chipName = Name;
   }

  static Chip chip(String Name) {return new Chip(Name);}                        // Create a new chip

  class Process                                                                 // A process consists of memory, registers and a program
   {final String             processName;                                       // The name of the process
    final int                processNumber;                                     // The unique number of this process
    final int                memoryWidth;                                       // The width of each memory element in bits
    final int                memorySize;                                        // The number of memory elements in the memory available to this process
    final BitSet[]           memory;                                            // Memory is an array of elements of bits
    final Register           memoryRegister;                                    // A register that is used to interact with memory
    final Children<Transaction> transactions = new Children<>();                // Transactions to watch for work requests
    final Children<Register> registers       = new Children<>();                // Registers used in this process
    final Stack<Instruction> code            = new Stack<>();                   // A fixed set of instructions for this process
    final Stack<Label>       labels          = new Stack<>();                   // Labels for instructions in this process
    int                      step            = 0;                               // Step in the program
    int                      maxSteps        = 200;                             // Maximum number of steps to execute
    int                      pc              = 0;                               // The index of the next instruction to be executed
    Integer                  nextPc          = null;                            // The next program counter requested
    String                   rc              = null;                            // The result of executing the program.  If null then no problems were detected

    Process(String ProcessName, int MemoryWidth, int MemorySize)                // Create a process
     {if (running)
       {stop("Cannot define process:", ProcessName, "when the chip is running");
       }
      processName   = ProcessName;
      processNumber = processes.size();
      processes.put(processName, this);

      memorySize    = MemorySize;
      memoryWidth   = MemoryWidth;
      memory        = new BitSet[memorySize];
      for (int i = 0; i < memorySize; i++) memory[i] = new BitSet(memoryWidth);
      memoryRegister = new Register("Memory_Value", memoryWidth);
     }

    abstract class Instruction                                                  // Instructions implement the action of a program
     {final int instructionNumber;                                              // The number of this instruction
      final String traceBack = traceBack();                                     // Line at which this instruction was created

      Instruction()                                                             // Add the instruction to the code
       {if (running)
         {stop("Cannot define instructions when the chip is running");
         }
        instructionNumber = code.size();
        code.push(this);
       }

      abstract void action();                                                   // The action to be performed by the instruction
      String verilog() {return "";}                                             // Verilog equivalent of the action implementing this instruction

      void remainOnThisInstruction() {nextPc = instructionNumber;}              // Keep looping on this instruction
     }

    class Label                                                                 // Lable jump targets in the program
     {
     }

    class Register                                                              // A register is a non indexable block of memory that can be read by any process but written to only by its owning process
     {final String registerName;                                                // The name of the register
      final int    registerBits;                                                // Number of bits the register can hold
      BitSet value = new BitSet();                                              // Current value of the register

      Register(String RegisterName, int RegisterBits)                           // Create the register
       {if (running)
         {stop("Cannot define register:", RegisterName, "when the chip is running");
         }
        registerName = RegisterName; registerBits = RegisterBits;
        registers.put(registerName, this);
       }

      int getRegister()                                                         // Get a register as an integer
       {return value.length() == 0 ? 0 : (int) value.toLongArray()[0];          // Relying on the fact that the Java is only ever test code unlike the Verilog.
       }

      void setRegister(int Value)                                               // Set the value of the register from an integer
       {final int l = min(registerBits, Integer.SIZE-1);                        // The most bits we can hope to represent
        value.clear();                                                          // Zero the memory
        for (int i = 0; i < l; i++)                                             // Set each bit in the bitset if the corresponding bit in the value is set
         {if (((Value >> i) & 1) != 0) value.set(i);
         }
       }

      void copy(Register Source)                                                // Copy a source register into this register which we can do because each  and only each process can write to its own registers
       {if (registerBits != Source.registerBits)                                // Make sure the registers have the same size
         {stop("Different sized registers cannot be assigned to each other.",
           "Target register:", registerName, "has a size of:", registerBits,
           "while source register:", Source.registerName, "has a size of:",
            Source.registerBits);
         }

        value = (BitSet)Source.value.clone();                                   // Copy the source value into the target
       }

      String registerName() {return processName + "_" + registerName;}          // Create a name for a register that includes its register name

      Process registerProcess() {return Process.this;}                          // Process associated with this register
     } // Register

    Register register(String RegisterName, int RegisterBits)                    // Create the register
     {return new Register(RegisterName, RegisterBits);
     }

    void initProgram()                                                          // Get ready to execute the program
     {pc = 0;                                                                   // Program always starts at the first instruction
     }

    void stepProgram()                                                          // Execute one step in the program
     {nextPc = null;                                                            // The executed instruction can optionally set this variable to change the execution flow
      if (pc >= code.size())
       {running = false;
        return;
       }
      code.elementAt(pc).action();
      if (nextPc != null) pc = nextPc; else pc++;                               // Interpret next program counter as either a redirection or continuation of flow
     }

    int memoryGet(int Index)                                                    // Get a memory element as an integer setting the memory register associated with the memory as well
     {memoryRegister.value = (BitSet)memory[Index].clone();                     // Load the memory register
      return (int) memory[Index].toLongArray()[0];                              // Relying on the fact that the Java is only ever test code unlike the Verilog.
     }

    void memorySet(int Index)                                                   // Set a memory element from the associated memory register
     {final int l = min(memoryWidth, Integer.SIZE-1);                           // The most bits we can hope to represent
      memory[Index] = (BitSet)memoryRegister.value.clone();                     // Memory element
     }

    void memorySet(int Value, int Index)                                        // Set a memory element
     {final int l = min(memoryWidth, Integer.SIZE-1);                           // The most bits we can hope to represent
      final BitSet v = memory[Index];                                           // Memory element
      v.clear();                                                                // Zero the memory
      for (int i = 0; i < l; i++)                                               // Set each bit in the bitset if the corresponding bit in the value is set
       {if (((Value >> i) & 1) != 0) v.set(i);
       }
     }

    class Transaction                                                           // Transactions allow one process to request services from another process
     {final String transactionName;                                             // Name of the transaction
      final Children<Process.Register> transactionInputRegisters  = new Children<>(); // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
      final Children<Process.Register> transactionOutputRegisters = new Children<>(); // The registers used as outputs by this transaction. As they are going to be written into bythe transaction they have to be owned by the process executing the transaction
      Integer transactionRequestedAt;                                           // The step at which the transaction started
      Integer transactionFinishedAt;                                            // The step at which the transaction finished
      int     transactionRc;                                                    // Return code from executing ransaction
      final Process transactionCallingProcess;                                  // The process requesting a service provided by this process via this transaction
      final String  transactionOpCode;                                          // The service requested by the caller

      Transaction(String Name, Process CallingProcess, String OpCode)           // Transactions allow one process to request services from another process
       {transactionName = Name;
        transactionCallingProcess = CallingProcess;
        transactionOpCode = OpCode;
        transactions.put(transactionName, this);
       }

      void transactionInputRegisters(Process.Register...InputRegisters)         // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
       {for(Process.Register r : InputRegisters)                                // Save input registers
         {transactionInputRegisters.put(r.registerName(), r);
         }
       }

      void transactionOutputRegisters(Process.Register...OutputRegisters)       // The registers used as outputs by this transaction. As they are going to be written into bythe transaction they have to be owned by the process executing the transaction
       {for(Process.Register r : OutputRegisters)                               // Save output registers
         {if (r.registerProcess() != transactionProcess()) stop("Output transaction register:",
           r.registerName()+" must be owned by process: "+transactionProcess().processName+"\n");
          transactionOutputRegisters.put(r.registerName(), r);
         }
       }

      boolean transactionExecutable()                                           // Whether the transaction is executable or not
       {final Integer r = transactionRequestedAt;
        final Integer f = transactionFinishedAt;
        return r != null && (f == null || f < r);                               // The transaction has been requested and has not yet completed
       }

      boolean transactionFinished()                                             // Whether the transaction hs finished executing
       {final Integer r = transactionRequestedAt;
        final Integer f = transactionFinishedAt;
        return f != null && !transactionExecutable();                           // The transaction has been finished and is not currently executing
       }

      Process transactionProcess() {return Process.this;}                       // Process associated with this transaction

     } // Transaction

    Transaction transaction(String Name, Process CallingProcess, String OpCode) // Transactions allow one process to request services from another process
     {return new Transaction(Name, CallingProcess, OpCode);
     }

    String processPcName()     {return processName+"_pc";}                      // Program counter
    String processNextPcName() {return processName+"_next_pc";}                 // Next program counter

    String generateProcessVerilog()                                             // Verilog representing a process
     {final StringBuilder v = new StringBuilder();

      v.append("\n// Process: "+processName+"\n\n");
      v.append("  reg ["+memoryWidth+"] "+processName+"_memory["+memorySize+"];\n");

      for (Register r: registers)
       {v.append("  reg ["+r.registerBits+"] "+r.registerName()+";\n");
       }

      for (Transaction t: transactions)                                        // Declare transactions
       {final String n = t.transactionName;
        v.append("  integer "+n+"_requestedAt;\n");
        v.append("  integer "+n+"_finishedAt;\n");
       }

      v.append("""
  integer $pc;                                                                  // Program counter
  integer $next_pc;                                                             // Next program counter
  always @ (posedge clock) begin                                                // Execute next step in program
    if (step == 0) begin
       $pc = 0;
""");
      for (Transaction t: transactions)                                         // Initialize transaction
       {final String n = t.transactionName;
        v.append("       "+n+"_finishedAt = -1;\n");
       }
      for (Process p: processes)                                                // Find transactions of which we are the source
       {for (Transaction t: p.transactions)
         {if (t.transactionCallingProcess == Process.this)                      // This transaction is a source of requests against this process                                                          //
           {final String n = t.transactionName;
            v.append("       "+n+"_requestedAt = -1;\n");                       // Clear step at which the transaction was requested
           }
         }
       }

      v.append("""
    end
    else begin
      $next_pc = -1;
      case($pc)                                                                 // Execute instructions
""");
      for(Instruction i: code)
       {final StringBuilder s = new StringBuilder();
        s.append("        "+i.instructionNumber+": begin\n");
        s.append(i.verilog()+"\n");
        s.append("        end\n");
        v.append(s);
       }
      v.append("        default: stop <= 1;\n");
      v.append("      endcase\n");
      v.append("      if ($next_pc != -1) $pc = $next_pc; else $pc = $pc + 1;   // Interpret next program counter as either a redirection or continuation of flow\n");
      v.append("    end\n");
      v.append("  end\n");
      replaceAll(v, "$pc",      processPcName());
      replaceAll(v, "$next_pc", processNextPcName());
      return ""+v;
     }
   } // Process

  Process process(String ProcessName, int MemoryWidth, int MemorySize)          // Create a process
   {return new Process(ProcessName, MemoryWidth, MemorySize);
   }

  public String toString()                                                      // Print a description of the chip
   {final StringBuilder s = new StringBuilder();
        s.append("Chip: "+chipName+"\n");
        s.append("  Processes:\n");
    for (Process p: processes)
     {  s.append("    Process: "+p.processName+"\n");
        s.append("      Registers:\n");
      for (Process.Register r: p.registers)
       {s.append("        Register: "+r.registerName+", process: "+r.registerProcess().processName+", value: "+r.getRegister()+"\n");
       }
      if (p.transactions.size() > 0)
       {s.append("      Transactions:\n");
      for (Process.Transaction t: p.transactions)
       {final Integer ra = t.transactionRequestedAt;
        final Integer fa = t.transactionFinishedAt;
        s.append("        Transaction   : "+t.transactionName+ "\n");
        s.append("          executable  : "+t.transactionExecutable()+ "\n");
        s.append("          finished    : "+t.transactionFinished()+ "\n");
        if (ra != null) s.append("          requested at: "+ra+"\n");
        if (fa != null) s.append("          finished  at: "+fa+"\n");

        s.append("          Inputs      :\n");                                  // Transaction inputs
        for (Process.Register r : t.transactionInputRegisters)
         {s.append("            "+r.registerName()+" = "+r.getRegister()+"\n");
         }

        s.append("          Outputs     :\n");                                  // Transaction outputs
        for (Process.Register r : t.transactionOutputRegisters)
         {s.append("            "+r.registerName()+" = "+r.getRegister()+"\n");
         }
       }
       }
     }

    return ""+s;
   }

  void runPrograms()                                                            // Run the processes == programs efined on this chip
   {for(Process p : processes) p.initProgram();                                 // Initialize each program                                               //
    running = true;                                                             // Show the program as running
    for(step = 0; running && step < maxSteps; ++step)                           // Run each program
     {for(Process p : processes) p.stepProgram();                               // Step each program
     }
    if (running)                                                                // Still running after too many steps
     {stop("Out of steps after:", maxSteps);
     }
   }

  void stopProgram(int ReturnCode)                                              // Return code
   {running = false;
    returnCode = ReturnCode;
   }

  String generateVerilog()                                                      // Generate verilog describing the chip
   {final StringBuilder v = new StringBuilder();
    v.append("""
//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module $chip_name;                                                              // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer                step;                                                  // Step of the simulation

  initial begin
    stop = 0;
    for(step = 0; step < $max_steps && !stop; step = step + 1) begin
      clock = 0; #1; clock = 1; #1;
    end
  end
""");

    for(Process p: processes) v.append(p.generateProcessVerilog());             // Generate

    v.append("""
endmodule
""");
    replaceAll(v, "$chip_name", chipName);
    replaceAll(v, "$max_steps", ""+maxSteps);
    writeFile(fne(Verilog.folder, chipName, Verilog.ext),  ""+v);
    return ""+v;
   }

  static void test_memory()
   {final int B = 8, N = 16;
    var c  = chip("Test");
    var m  = c.process("Memory", B, N);
    var r  = c.process("Request", B, 1);
    var ri = r.register("index", B);
    var mo = m.register("value", B);
    var t  = m.transaction("GetValueFromMemory", r, "get");
             t.transactionInputRegisters (ri);
             t.transactionOutputRegisters(mo);
    for (int i = 0; i < N; i++)                                                 // Preload memory
     {m.memoryRegister.setRegister(i+1);
      m.memorySet(i);
     }

    m.new Instruction()                                                         // Process mmeory requests
     {void action()
       {for (var t : m.transactions)
         {if (t.transactionExecutable())
           {final int i = t.transactionInputRegisters.elementAt(0).getRegister(); // Index of memory requested
            m.memoryGet(i);
            t.transactionOutputRegisters.elementAt(0).copy(m.memoryRegister);   // Register to hold value at index
            t.transactionFinishedAt = c.step;
           }
         }
        remainOnThisInstruction();                                              // Keep looping on this instruction
       }
      String verilog()                                                          // Process memory requests
       {final StringBuilder s = new StringBuilder();

        for (Process.Transaction t: m.transactions)                             // Declare transactions
         {final String n = t.transactionName;
          final String p = m.processName;
          final Process.Register i = t.transactionInputRegisters.elementAt(0);  // Register 0 is the register that contains the input index
          final Process.Register o = t.transactionOutputRegisters.elementAt(0); // Register 1 is the target register
          if (t.transactionOpCode.equals("get"))
           {s.append("             if ("+n+"_requestedAt > "+n+"_finishedAt) begin\n");
            s.append("               "+r.processName+"_"+o.registerName+" = "+p+"_memory["+r.processName+"_"+i.registerName+"];\n");
            s.append("               "+n+"_finishedAt = $pc;\n");
            s.append("             end\n");
           }
         }

        return ""+s;
       }
     };

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {if      (c.step <  2) {}                                                // Wait a bit
        else if (c.step == 2)                                                   // Wait a bit
         {ri.setRegister(1);                                                    // Index of memory requested
          t.transactionRequestedAt = c.step;                                    // Request value of memory at the index
         }
        else if (t.transactionFinished())                                       // Wait for memory request to finish
         {c.stopProgram(1);                                                     // Halt the run
         }
        remainOnThisInstruction();                                              // Keep looping on this instruction
       }
      String verilog()
       {return """
      $display("22");
""";
       }
     };

    c.runPrograms();
    //stop(c);
    ok(c, """
Chip: Test
  Processes:
    Process: Memory
      Registers:
        Register: Memory_Value, process: Memory, value: 2
        Register: value, process: Memory, value: 2
      Transactions:
        Transaction   : GetValueFromMemory
          executable  : false
          finished    : true
          requested at: 2
          finished  at: 3
          Inputs      :
            Request_index = 1
          Outputs     :
            Memory_value = 2
    Process: Request
      Registers:
        Register: Memory_Value, process: Request, value: 0
        Register: index, process: Request, value: 1
""");
    ok(c.returnCode, 1);
    say(c.generateVerilog());
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_memory();
   }

  static void newTests()                                                        // Tests being worked on
   {//oldTests();
    test_memory();
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
