//------------------------------------------------------------------------------
// B-tree implemented using block-based, asynchronous memory.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // B-tree implemented in a memory block on a silicon chip.

import java.util.*;

class Chip extends Test                                                         // A chip designed to manipulate a B-tree stored in a memory block
 {final String chipName;                                                        // The name of the chip
  final Children<Process>  processes = new Children<>();                        // A fixed set of processes for this chip in definition order
  final String      verilogTraceFile = fn(Verilog.folder, "v.txt");             // Verilog trace file
  final String         javaTraceFile = fn(Verilog.folder, "j.txt");             // Java trace file for comparison with verilog
  int memoryProcessTransactionNumber = 0;                                       // Make transaction names unique
  boolean                    running;                                           // True when the chip is running
  int                           step;                                           // Current simulation step being executed
  int                       maxSteps = 10;                                      // Maximum number of steps to execute in the simulation
  int                     returnCode;                                           // The return code from the first process to finish which effectively terminates the simulation
  static boolean               debug = false;                                   // Debug when true

  Chip(String Name)                                                             // Create a new chip
   {chipName = Name;
   }

//D1 Chip                                                                       // A chip is constructed from a fixed number of communicating processes that execute code on the chip to produce the desired outputs from the inputs to the chip

  static Chip chip(String Name) {return new Chip(Name);}                        // Create a new chip

  class Process                                                                 // A process consists of memory, registers and a program
   {final String             processName;                                       // The name of the process
    final int                processNumber;                                     // The unique number of this process
    final int                memoryWidth;                                       // The width of each memory element in bits
    final int                memorySize;                                        // The number of memory elements in the memory available to this process
    final BitSet[]           memory;                                            // Memory is represented as an array of bit vectors
    final Register           memoryRegister;                                    // A register that is used to interact with memory
    final Children<Transaction> transactions = new Children<>();                // Transactions representing work requests to this process
    final Children<Register> registers       = new Children<>();                // Registers used in this process
    final Stack<Instruction> code            = new Stack<>();                   // A fixed set of instructions for this process
    final Stack<Label>       labels          = new Stack<>();                   // Labels for instructions in this process
    int                      processPc       = 0;                               // The index of the next instruction to be executed
    int                      processNextPc   = -1;                              // The next program counter requested

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

//D2 Instruction                                                                // An instruction represents code to be executed by a process in a single clock cycle == process step

    abstract class Instruction                                                  // Instructions implement the action of a program
     {final int instructionNumber;                                              // The number of this instruction
      final String traceBack = traceBack();                                     // Line at which this instruction was created

      Instruction()                                                             // Add this instruction to the process's code
       {if (running)
         {stop("Cannot define instructions when the chip is running");
         }
        instructionNumber = code.size();
        code.push(this);
       }

      abstract void action();                                                   // The action to be performed by the instruction
      String verilog() {return "";}                                             // Verilog equivalent of the action implementing this instruction

      void instructionIterate() {processNextPc = instructionNumber;}            // Keep looping on this instruction
      String instructionIterateV()                                              // Keep looping on this instruction in verilog
       {return processNextPcName()+" = "+processPcName()+";";
       }
     }

    class Label                                                                 // Label jump targets in the program
     {int instruction;                                                          // The instruction location to which this labels applies
      Label()    {set(); labels.push(this);}                                    // A label assigned to an instruction location
      void set() {instruction = code.size();}                                   // Reassign the label to an instruction
     }

//D2 Register                                                                   // A register is an unindexable block of memory associated with a process

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

      String registerSetV(int Value)                                            // Set the value of the register from an integer
       {return registerName() + " = " + Value+ ";";
       }

      void copy(Register Source)                                                // Copy a source register into this register which we can do because each  and only each process can write to its own registers
       {if (registerBits <= Source.registerBits)                                // Make sure the registers have the same size
         {if (false) stop("Target register is smaller than source register.\n",
           "Target register:", registerName, "has a size of:", registerBits,
           "while source register:", Source.registerName, "has a size of:",
            Source.registerBits);
         }

        value = (BitSet)Source.value.clone();                                   // Copy the source value into the target
       }

      String copyV(Register Source)                                             // Copy a source register into this register which we can do because each  and only each process can write to its own registers
       {return registerName() + " = "+ Source.registerName()+";";
       }

      String registerName() {return processName + "_" + registerName;}          // Create a name for a register that includes its register name

      Process registerProcess() {return Process.this;}                          // Process associated with this register

//D3 Arithmetic                                                                 // Operations on registers

      void zero() {registerSet(0);}                                             // Zero a register
      void one () {registerSet(1);}                                             // One a register
      void inc () {registerSet(registerGet()+1);}                               // Increment a register
      void dec () {registerSet(registerGet()-1);}                               // Decrement a register
      void not () {registerSet(registerGet() != 0 ? 0 : 1);}                    // Not a register
      void add (Register source)                                                // Add a register to the current register
       {registerSet(registerGet()+source.registerGet());
       }
     } // Register

    Register register(String RegisterName, int RegisterBits)                    // Create the register
     {return new Register(RegisterName, RegisterBits);
     }

    void initProgram()                                                          // Get ready to execute the program
     {processPc = 0;                                                            // Program always starts at the first instruction
     }

    void stepProgram()                                                          // Execute one step in the program
     {processNextPc = -1;                                                       // The executed instruction can optionally set this variable to change the execution flow
      if (code.size() == 0) return;                                             // No code to run
      if (processPc >= code.size())
       {running = false;
        return;
       }
      code.elementAt(processPc).action();
      if (processNextPc >= 0) processPc = processNextPc; else processPc++;      // Interpret next program counter as either a redirection or continuation of flow
     }

    protected int memoryGetNoSet(int Index)                                     // Get a memory element as an integer withoiut setting the memory cache register
     {final BitSet b = memory[Index];                                           // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      return V.length > 0 ? (int)b.toLongArray()[0] : 0;                        // Take the first element if it exists relying on the fact that in the java code we test with just sufficiently large numbers to test the verilog in priniple
     }

    int memoryGet(int Index)                                                    // Get a memory element as an integer setting the memory cache register to the value of the element retrieved
     {final BitSet b = memoryRegister.value = (BitSet)memory[Index].clone();    // Read memory as bit set
      final long[]V = b.toLongArray();                                          // Convert bitset to long
      return V.length > 0 ? (int)b.toLongArray()[0] : 0;                        // Take the first element if it exists relying on the fact that in the java code we test with just sufficiently large numbers to test the verilog in priniple
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

//D2 Transaction                                                                // A transaction allows other processes on the chip to request services from this process

    class Transaction                                                           // Transactions allow one process to request services from another process
     {final String transactionName;                                             // Name of the transaction
      final Children<Process.Register> transactionInputRegisters  = new Children<>(); // The registers used to provide inputs to this transaction. As they are only going to be read during the transaction they can be owned by any process
      final Children<Process.Register> transactionOutputRegisters = new Children<>(); // The registers used as outputs by this transaction. As they are going to be written into by the transaction they have to be owned by the process executing the transaction
      int transactionRequestedAt = -1;                                          // The step at which the transaction started avoiding the use of a boxed type as no direct equivalent in verilog
      int transactionFinishedAt  = -1;                                          // The step at which the transaction finished
      int transactionRc;                                                        // Return code from executing transaction
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

      void transactionOutputRegisters(Process.Register...OutputRegisters)       // The registers used as outputs by this transaction. As they are going to be written into by the transaction they have to be owned by the process executing the transaction
       {for(Process.Register r : OutputRegisters)                               // Save output registers
         {if (r.registerProcess() != transactionProcess())                      // Check that the putput registers are owned by the target process of the transaction as that process is the only process that can write into them
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
        return r < f;                                                           // The transaction has been finished and is not currently executing
       }

      int transactionExecutableAsInt() {return transactionExecutable() ? 1 : 0;}// Whether the transaction is executable or not as an integer
      int transactionFinishedAsInt  () {return transactionFinished  () ? 1 : 0;}// Whether the transaction has finished or not as an integer

      String transactionRequestedAt() {return transactionName+"_requestedAt";}  // Name of the requested at field for a transaction
      String transactionFinishedAt()  {return transactionName+"_finishedAt";}   // Name of the finished at field for a transaction

      String transactionExecutableV()                                           // Whether the transaction is executable or not in verilog
       {final String r = transactionRequestedAt();
        final String f = transactionFinishedAt();
        return "("+r+" > "+f + " && "+r + " != step)";
       }

      String transactionFinishedV()                                             // Whether the transaction has finished or not in verilog
       {final String r = transactionRequestedAt();
        final String f = transactionFinishedAt();
        return "("+r+" < "+f+")";
       }

      void transactionSetExecutable()                                           // Mark a transaction as executable
       {transactionRequestedAt = step;
       }
      String transactionSetExecutableV()                                        // Mark a transaction as executable
       {return transactionRequestedAt()+" = step;";
       }
      void transactionSetFinished()                                             // Mark a transaction as finished
       {transactionFinishedAt = step;
       }
      String transactionSetFinishedV()                                          // Mark a transaction as finished
       {return transactionFinishedAt()+" = step;";
       }
      String transactionRcName()                                                // Name of the return code field for a transaction
       {return transactionProcess().processName+"_"+transactionName+"_returnCode";
       }

      Process transactionProcess() {return Process.this;}                       // Process associated with this transaction
     } // Transaction

    Transaction transaction(String Name, Process CallingProcess, String OpCode) // Transactions allow one process to request services from another process
     {return new Transaction(Name, CallingProcess, OpCode);
     }

//D2 Process                                                                    // A process represents a thread of execution on the chip

    String processPcName()     {return processName+"_pc";}                      // Program counter
    String processNextPcName() {return processName+"_next_pc";}                 // Next program counter
    String processMemoryName() {return processName+"_memory";}                  // Name of the memory block used by this process

    String generateProcessVerilog()                                             // Generate Verilog code for this process
     {final StringBuilder v = new StringBuilder();

      v.append("\n// Process: "+processName+"\n\n");
      v.append("  reg ["+memoryWidth+"] "+processMemoryName()+"["+memorySize+"];\n");

      for (Register r: registers)
       {v.append("  reg ["+r.registerBits+"] "+r.registerName()+";\n");
       }

      for (Transaction t: transactions)                                         // Declare transactions
       {final String n = t.transactionName;
        v.append("  integer "+n+"_requestedAt;\n");
        v.append("  integer "+n+"_finishedAt;\n");
        v.append("  integer "+t.transactionRcName()+";\n");
       }

      v.append("""
  integer $pc;                                                                  // Program counter
  integer $next_pc;                                                             // Next program counter
  always @ (posedge clock) begin                                                // Execute next step in program
    if (step < 0) begin                                                         // Steps less than zero are used for initilization
      integer i;
      $pc = 0;
      $next_pc = 0;                                                             // Next program counter
""");
      for (Register r: registers)
       {v.append("      "+r.registerName()+" = 0;\n");
       }
      for (Transaction t: transactions)                                         // Initialize transaction
       {final String n = t.transactionName;
        v.append("      "+n+"_finishedAt = -1;\n");
        v.append("      "+t.transactionRcName()+" = 0;\n");
       }
      for (Process p: processes)                                                // Find transactions of which we are the source
       {for (Transaction t: p.transactions)
         {if (t.transactionCallingProcess == Process.this)                      // This transaction is a source of requests against this process                                                          //
           {final String n = t.transactionName;
            v.append("      "+n+"_requestedAt = -1;\n");                        // Clear step at which the transaction was requested
           }
         }
       }

      v.append("       for(i = 0; i < "+memorySize+"; i = i + 1) "+processMemoryName()+"[i] = i+1;\n");
      v.append("""
    end
    else if (processCurrent == $processNumber) begin                            // Run the process if it is the current one
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
      v.append("        default: stop = 1;\n");
      v.append("      endcase\n");
      v.append("      if ($next_pc != -1) $pc = $next_pc; else $pc = $pc + 1;   // Interpret next program counter as either a redirection or continuation of flow\n");
      v.append("    end\n");
      v.append("  end\n");
      replaceAll(v, "$pc",      processPcName());
      replaceAll(v, "$next_pc", processNextPcName());
      replaceAll(v, "$processNumber", ""+processNumber);
      return ""+v;
     }
   } // Process

  Process process(String ProcessName, int MemoryWidth, int MemorySize)          // Create a process
   {return new Process(ProcessName, MemoryWidth, MemorySize);
   }

//D2 Memory Process                                                             // Processes that maintain the state of a memory

  class MemoryProcess extends Process                                           // A process whose sole purpose is to maintain memory
   {MemoryProcess(String ProcessName, int MemoryWidth, int MemorySize)          // Create a process
     {super(ProcessName, MemoryWidth, MemorySize);
      memoryProcessGenerate();                                                  // Generate the code to execute this process
     }
    class Get extends Process.Transaction                                       // Get a value from the memory controlled by this process
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The index of the element whose value is required
      final Register result;                                                    // The value of the memory at the specified index
      Get(Process Process)                                                      // Get the value from memory at the indicated index
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          MemoryProcess.this, "get");
        process = Process;                                                      // The calling process requesting a value from the memory of this memory process
        index   = register(transactionName+"_index", logTwo(memorySize));       // A register that will index the memory managed by this process
        result  = register(transactionName+"_result", memoryWidth);             // The register that will contain the result
        transactionInputRegisters(index);                                       // Make the index an input register
        transactionOutputRegisters(result);                                     // Make the result an output register
       }

      void executeTransaction(Register Index)                                   // Execute the request in java
       {index.copy(Index);
        transactionSetExecutable();
       }

      String executeTransactionV(Register Index)                                // Execute the request in verilog
       {return index.copyV(Index) + "  " + transactionSetExecutableV();
       }

      void waitResultOfTransaction(int indent)                                  // Wait for the request to finish
       {process.new Instruction()
         {void action()
           {if (!transactionFinished()) instructionIterate();
           }
          String verilog()
           {return "  ".repeat(indent)+"if (!"+transactionFinishedV()+") "+instructionIterateV()+";";
           }
         };
       }
     } // Get

    class Set extends Process.Transaction                                       // Set an indexed memory element to a specified value
     {final Process  process;                                                   // The calling process
      final Register index;                                                     // The index of the element whose value is required
      final Register value;                                                     // The value to be written into memory
      Set(Process Process)                                                      // Get the value from memory at the indicated index
       {super(processName+"_"+(++memoryProcessTransactionNumber),
          MemoryProcess.this, "set");
        process = Process;                                                      // The calling process requesting a value from the memory of this memory process
        index   = register(transactionName+"_index", logTwo(memorySize));       // A register that will index the memory managed by this process
        value   = register(transactionName+"_value", memoryWidth);              // The register that will contain the result
        transactionInputRegisters(index);                                       // Make the index an input register
        transactionInputRegisters(value);                                       // Make the value an input register
       }

      void executeTransaction(Register Index, Register Value)                   // Execute the request
       {index.copy(Index);
        value.copy(Value);
        transactionSetExecutable();
       }

      String executeTransactionV(Register Index, Register Value)                // Execute the request
       {return index.copyV(Index) + "  " +value.copyV(Value) + "  " + transactionSetExecutableV();
       }

      void waitResultOfTransaction(int indent)                                  // Wait for the request to finish
       {process.new Instruction()
         {void action()
           {if (!transactionFinished()) instructionIterate();
           }
          String verilog()
           {return "  ".repeat(indent)+"if (!"+transactionFinishedV()+") "+instructionIterateV()+";";
           }
         };
       }
     } // Set

    void memoryProcessGenerate()                                                // Generate the code needed to support the transactions against this memory process
     {new Instruction()                                                         // Process memory requests
       {void action()
         {for (var t : transactions)
           {if (t.transactionExecutable())
             {if (t.transactionOpCode.equals("get"))
               {final Register I = t.transactionInputRegisters.elementAt(0);    // Address index register
                final int      i = I.registerGet();                             // Value of index register
                final int      v = memoryGet(i);                                // Value of memory at index
                final Register O = t.transactionOutputRegisters.elementAt(0);   // Register to hold value of memory at index
                O.registerSet(v);                                               // Set output register with value of memory at index
                t.transactionSetFinished();                                     // Mark the transaction as complete
               }
              else if (t.transactionOpCode.equals("set"))                       // Set an indexed memory element to a specified value
               {final var I = t.transactionInputRegisters.elementAt(0);         // Address index register
                final var V = t.transactionInputRegisters.elementAt(1);         // Address value register
                final int i = I.registerGet();                                  // Value of index register
                final int v = V.registerGet();                                  // Value of memory at index
                memoryRegister.copy(V);                                         // Set output register with value of memory at index
                memorySet(i);                                                   // Register to hold value of memory at index
                t.transactionSetFinished();                                     // Mark the transaction as complete
               }
              else stop("Unknown memory process transaction request:", t.transactionOpCode); // Set an indexed memory element to a specified value
             }
           }
          instructionIterate();                                                 // Keep looping on this instruction
         }
        String verilog()
         {final StringBuilder s = new StringBuilder();
          final String m = memoryRegister.registerName();
          final String M = processMemoryName();
          for (var t : transactions)
           {s.append("if ("+t.transactionExecutableV()+") begin\n");
            if (t.transactionOpCode.equals("get"))
             {final Register I = t.transactionInputRegisters.elementAt(0);    // Address index register
              final String   i = I.registerName();                            // Value of index register
              final Register O = t.transactionOutputRegisters.elementAt(0);   // Register to hold value of memory at index
              final String   o = O.registerName();                            // Value of index register
              s.append(" "+o+" = "+M+"["+i+"];");                             // Set output register with value of memory at index
              s.append(" "+m+" = "+o+";");                                    // Set output register with value of memory at index
              s.append(" "+t.transactionSetFinishedV());                      // Mark the transaction as complete
             }
            else if (t.transactionOpCode.equals("set"))                       // Set an indexed memory element to a specified value
             {final var I = t.transactionInputRegisters.elementAt(0);         // Address index register
              final var V = t.transactionInputRegisters.elementAt(1);         // Address value register
              final String i = I.registerName();                              // Value of index register
              final String v = V.registerName();                              // Value of memory at index
              s.append(" "+M+"["+i+"] = "+v+";");                             // Set output register with value of memory at index
              s.append(" "+m+" = "+v+";");                                    // Set output register with value of memory at index
              s.append(" "+t.transactionSetFinishedV());                      // Mark the transaction as complete
             }
            s.append("\nend\n");
           }
          s.append(" "+instructionIterateV());                                  // Keep looping on this instruction
          return ""+s;
         }
       };
     }
   }

//D2 Chip                                                                       // Actions that affect the whole chip

  void chipRunPrograms()                                                        // Run the processes == programs defined on this chip using thekr Java implementation
   {for(Process p : processes) p.initProgram();                                 // Initialize each program
    running = true;                                                             // Show the program as running
    deleteFile(javaTraceFile);                                                  // Remove java trace file
    for(step = 0; running && step < maxSteps; ++step)                           // Run each program
     {for(Process p : processes) p.stepProgram();                               // Step each program
      chipPrint();
     }
    if (running)                                                                // Still running after too many steps
     {say(this);
      stop("Out of steps after:", maxSteps);
     }
   }

  void chipStop(int ReturnCode)                                                 // Stop the chip
   {returnCode = ReturnCode; running = false;
   }

  String chipStopV(int ReturnCode)                                              // Stop the chip in verilog
   {return "returnCode = "+ReturnCode+"; stop = 1;";
   }

//D2 Print                                                                      // Print the state of a chip

  public String toString()                                                      // Print a description of the java emulation of the chip
   {final StringBuilder s = new StringBuilder();
        s.append(String.format(
         "Chip: %-16s step: %4d, maxSteps: %4d, running: %d, returnCode: %d\n",
          chipName, step, maxSteps, (running ? 1 : 0), returnCode));

        s.append("  Processes:\n");

    for (Process p: processes)                                                  // Each process
     {  s.append(String.format(
         "    Process: %4d - %-16s instructions: %4d, pc: %4d, nextPc: %4d, memory: %4d * %4d",
          p.processNumber, p.processName, p.code.size(), p.processPc, p.processNextPc,
          p.memorySize, p.memoryWidth));

        if (p.memory.length > 0)                                                // Print memory
         {final StringBuilder m = new StringBuilder();
          for (int i = 0; i < p.memory.length; i++)
           {final int v =  p.memoryGetNoSet(i);
            m.append(", "+v);
           }
          s.append(" = "+m.substring(2)+"\n");
         }

        s.append("      Registers :\n");
      for (Process.Register r: p.registers)
       {s.append(String.format(
         "        Register: %-32s = %4d\n",
          r.registerName(), r.registerGet()));
       }
      if (p.transactions.size() > 0)
       {s.append("      Transactions:\n");
        for (Process.Transaction t: p.transactions)
         {final int ra = t.transactionRequestedAt;
          final int fa = t.transactionFinishedAt;
          final String in = "        ";
          s.append(String.format("%sTransaction   : %-8s - %-16s  requested at: %4d, finished at: %4d, returnCode: %2d, executable: %1d, finished: %1d\n",
            in, t.transactionOpCode, t.transactionName,
            ra, fa, t.transactionRc,
            t.transactionExecutableAsInt(), t.transactionFinishedAsInt()));

          s.append(in+"  Inputs      :\n");                                     // Transaction inputs
          for (Process.Register r : t.transactionInputRegisters)
           {s.append(String.format(
             "%s    %-22s = %4d\n",
             in, r.registerName(), r.registerGet()));
           }

          s.append(in+"  Outputs     :\n");                                     // Transaction outputs
          for (Process.Register r : t.transactionOutputRegisters)
           {s.append(String.format(
             "%s    %-22s = %4d\n",
             in, r.registerName(), r.registerGet()));
           }
         }
       }
     }

    return ""+s;
   }

  void chipPrint()                                                              // Print the current state of the java emulation of the chip to a file
   {appendFile(javaTraceFile, toString());
   }

  public String generateChipPrintV()                                            // Verilog to print the state of the chip as a callable Verilog task
   {final StringBuilder s = new StringBuilder();
        s.append(" task chipPrint;\n");
        s.append("   begin\n");
        s.append("   integer o;\n");
        s.append("   o = $fopen(\""+verilogTraceFile+"\", \"a\");\n");
        s.append("   if (!o) $display(\"Cannot create trace folder: "+verilogTraceFile+"\");");

        s.append(
         "$fwrite(o, \"Chip: %-16s step: %4d, maxSteps: %4d, running: %1d, returnCode: %0d\\n\", "+
         "\""+chipName+"\", step, maxSteps, !stop, returnCode);\n");

        s.append("$fwrite(o, \"  Processes:\\n\");\n");

      for (Process p: processes)                                                // Each process
       {s.append(
         "$fwrite(o, \"    Process: %4d - %-16s instructions: %4d, pc: %4d, nextPc: %4d, memory: %4d * %4d\", "+
         " "+p.processNumber+", \""+p.processName+"\", "+p.code.size()+", "+p.processPcName()+", "+p.processNextPcName()+", "+
         " "+p.memorySize+", "+p.memoryWidth+");\n");

        if (p.memory.length > 0)                                                // Print memory
         {final StringBuilder m = new StringBuilder();
          m.append("$fwrite(o, \" = %0d\", "+p.processMemoryName()+"[0]);\n");
          for (int i = 1; i < p.memory.length; i++)
           {m.append("$fwrite(o, \", %0d\", "+p.processMemoryName()+"["+i+"]);\n");
           }
          m.append("$fwrite(o, \"\\n\");\n");
          s.append(m);
         }

        s.append("$fwrite(o, \"      Registers :\\n\");\n");
      for (Process.Register r: p.registers)                                     // Registers
       {s.append(
         "$fwrite(o, \"        Register: %-32s = %4d\\n\", "+
         " \""+r.registerName()+"\", "+r.registerName()+");\n");
       }
      if (p.transactions.size() > 0)                                            // Transactions
       {s.append("$fwrite(o, \"      Transactions:\\n\");\n");
        for (Process.Transaction t: p.transactions)
         {final String ra = t.transactionRequestedAt();
          final String fa = t.transactionFinishedAt();
          s.append("$fwrite(o, \"        Transaction   : %-8s - %-16s  requested at: %4d, finished at: %4d, returnCode: %2d, executable: %1d, finished: %1d\\n\""+
            ", \""+t.transactionOpCode+"\", \""+t.transactionName+"\""+
            ", "+ra+", "+fa+", "+t.transactionRcName()+
            ", "+t.transactionExecutableV()+", "+t.transactionFinishedV()+");\n");

          s.append("$fwrite(o, \"          Inputs      :\\n\");\n");            // Transaction inputs
          for (Process.Register r : t.transactionInputRegisters)
           {s.append(
             "$fwrite(o, \"            %-22s = %4d\\n\", \""+
             r.registerName()+"\", "+r.registerName()+");\n");
           }

          s.append("$fwrite(o, \"          Outputs     :\\n\");\n");            // Transaction outputs
          for (Process.Register r : t.transactionOutputRegisters)
           {s.append(
             "$fwrite(o, \"            %-22s = %4d\\n\", \""+
             r.registerName()+"\", "+r.registerName()+");\n");
           }
         }
       }
     }
    s.append("""
      $fclose(o);
    end
  endtask
""");
    return ""+s;
   }

//D2 Verilog                                                                    // Verilog describing the chip

  String chipGenerateVerilog()                                                  // Generate verilog describing the chip
   {final StringBuilder v = new StringBuilder();
    v.append("""
//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module $chip_name;                                                              // Test bench for database on a chip
  integer                stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  initial begin
    stop = 0;
    returnCode = 0;
    maxSteps = $max_steps;
    for(step = -1; step < maxSteps && !stop; step = step + 1) begin             // Steps below zero are used for initilization so that Java and Verilog start in synch at step zero
""");

    for(Process p: processes)                                                   // Single thread the processes in a constant order
     {v.append("processCurrent = "+p.processNumber+";");
      v.append("""
      clock = 0; #1; clock = 1; #1;
""");
     }
    v.append("""
      if (step >= 0) chipPrint();                                               // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                     // Set return code depending on whether the simulation halted
  end
""");

    for(Process p: processes) v.append(p.generateProcessVerilog());             // Generate

    v.append(generateChipPrintV());

    v.append("""
endmodule
""");
    replaceAll(v, "$chip_name", chipName);
    replaceAll(v, "$max_steps", ""+maxSteps);

    final String source = fne(Verilog.folder, chipName, Verilog.ext);           // Source code in verilog
    writeFile(source,  ""+v);
    final String trace  = fne(Verilog.folder, "v", "txt");                      // Verilog execution trace

    final var c = "rm -f "+chipName+" "+verilogTraceFile+
                  " ;  iverilog -g2012 -o "+chipName+" "+source+
                  " && timeout 1m ./"      +chipName;
    final var e = new ExecCommand(c);
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
     {m.memoryRegister.registerSet(i+1);
      m.memorySet(i);
     }

    m.new Instruction()                                                         // Process memory requests
     {void action()
       {for (var t : m.transactions)
         {if (t.transactionExecutable())                                        // Each transaction that is currently ready to be executed
           {if (t.transactionOpCode.equals("get"))                              // Get a value from memory
             {final var I = t.transactionInputRegisters.elementAt(0);           // Address index register
              final int i = I.registerGet();                                    // Value of index register
              final int v = m.memoryGet(i);                                     // Value of memory at index
              final var O = t.transactionOutputRegisters.elementAt(0);          // Register to hold value of memory at index
              O.registerSet(v);                                                 // Set output register with value of memory at index
              t.transactionSetFinished();                                       // Mark the transaction as complete
             }
           }
         }
        instructionIterate();                                                   // Keep looping on this instruction
       }

      String verilog()                                                          // Process memory requests
       {final StringBuilder s = new StringBuilder();

        for (Process.Transaction t: m.transactions)                             // Declare transactions
         {final Process.Register i = t.transactionInputRegisters.elementAt(0);  // Register 0 is the register that contains the input index
          final Process.Register o = t.transactionOutputRegisters.elementAt(0); // Register 1 is the target register
          if (t.transactionOpCode.equals("get"))
           {final String in = "             ";                                  // Indentation
            final String vl = m.processMemoryName()+"["+i.registerName()+"]";   // Reference to memory to get the value of the indexed element
            s.append(in+"if ("+t.transactionExecutableV()+") begin\n");         // Check that this transaction is executable
            s.append(in+"  "+o.registerName()+" = "+vl+";\n");                  // Get the indexed value
            s.append(in+"  "+t.transactionSetFinishedV()+"\n");                 // Mark the transaction as complete
            s.append(in+"end\n");
           }
         }

        s.append("             "+instructionIterateV()+"\n");                   // Stay on this instruction indefinitely as long as the chip is running
        return ""+s;                                                            // Generated verilog to get a value from the memory of this process
       }
     };

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {if      (c.step <  2) {}                                                // Wait a bit
        else if (c.step == 2)                                                   // Make memeory request
         {ri.registerSet(1);                                                    // Index of memory requested
          t.transactionSetExecutable();                                         // Request value of memory at the index
         }
        else if (t.transactionFinished())                                       // Wait for memory request to finish
         {c.chipStop(1);                                                        // Halt the run
         }
        instructionIterate();                                                   // Keep looping on this instruction
       }

      String verilog()
       {final StringBuilder s = new StringBuilder();
        final String in = "             ";
        s.append(in+"if (step <  2) begin end\n");
        s.append(in+"else if (step == 2) begin\n");
        s.append(in+"  "+ri.registerName()+" = 1;\n");
        s.append(in+"  "+t.transactionSetExecutableV()+"\n");
        s.append(in+"end\n");
        s.append(in+"else if ("+t.transactionFinishedV()+") begin\n");
        s.append(in+"  integer o;\n");
        s.append(in+"  o = $fopen(\"result.txt\", \"w\");\n");
        s.append(in+"  $display(  \"Step=%d\\nValue=%d\\n\", step, "+mo.registerName()+" );\n");
        s.append(in+"  $fwrite(o, \"Step=%d\\nValue=%d\\n\", step, "+mo.registerName()+" );\n");
        s.append(in+"  $fclose(o);\n");
        s.append(in+"  "+c.chipStopV(1)+";\n");
        s.append(in+"end\n");
        s.append(in+""+instructionIterateV()+"\n");
        return ""+s;
       }
     };

    c.chipRunPrograms();
    //stop(c);
    ok(c, """
Chip: Test             step:    4, maxSteps:   10, running: 0, returnCode: 1
  Processes:
    Process:    0 - Memory           instructions:    1, pc:    0, nextPc:    0, memory:   16 *    8 = 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
      Registers :
        Register: Memory_Memory_Value              =    2
        Register: Memory_value                     =    2
      Transactions:
        Transaction   : get      - GetValueFromMemory  requested at:    2, finished at:    3, returnCode:  0, executable: 0, finished: 1
          Inputs      :
            Request_index          =    1
          Outputs     :
            Memory_value           =    2
    Process:    1 - Request          instructions:    1, pc:    0, nextPc:    0, memory:    1 *    8 = 0
      Registers :
        Register: Request_Memory_Value             =    0
        Register: Request_index                    =    1
""");
    ok(c.returnCode, 1);
    //say(c.chipGenerateVerilog());
   }

  static void test_memoryProcess()
   {final int B = 8, M = 1, N = 16;
    var c  = chip("Test");
    var m  = c.new MemoryProcess("Memory", B, N);
    var r  = c.process("Requests", B, M);

    var ri = r.register("index1",  B);
    var rt = m.new Get(r);

    var si = r.register("index2",  B);
    var st = m.new Get(r);

    var tv = r.register("value",   B);
    var tt = m.new Set(r);

    for (int i = 0; i < N; i++)                                                 // Preload memory
     {m.memoryRegister.registerSet(i+1);
      m.memorySet(i);
     }
    m.memoryRegister.registerSet(0);                                            // Clean up dtate after loading memory to match verilog

    for (int i = 0; i < M; i++)                                                 // Preload memory
     {r.memoryRegister.registerSet(i+1);
      r.memorySet(i);
     }
    r.memoryRegister.registerSet(0);                                            // Clean up dtate after loading memory to match verilog

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {ri.registerSet(1);                                                      // Index of memory requested
        rt.executeTransaction(ri);                                              // Request value of memory at the index
       }
      String verilog()
       {return "          "+ri.registerSetV(1) + "  " + rt.executeTransactionV(ri);
       }
     };

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {si.registerSet(2);                                                      // Index of memory requested
        st.executeTransaction(si);                                              // Request value of memory at the index
       }
      String verilog()
       {return "          "+si.registerSetV(2) + "  " + st.executeTransactionV(si);
       }
     };
    st.waitResultOfTransaction(5);                                               // Request value of memory at the index
    rt.waitResultOfTransaction(5);                                               // Request value of memory at the index

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {tv.registerSet(33);                                                     // Value to set into memory
        tt.executeTransaction(si, tv);                                          // Request value of memory at the index
       }
      String verilog()
       {return "          "+tv.registerSetV(33) + "  " + tt.executeTransactionV(si, tv);
       }
     };
    tt.waitResultOfTransaction(5);                                              // Request value of memory at the index

    r.new Instruction()                                                         // Request the value of an indexed element of memory
     {void action()
       {c.chipStop(1);                                                          // Halt the run
       }
      String verilog()
       {return "          "+c.chipStopV(1);
       }
     };

    m.memoryProcessGenerate();
    c.chipRunPrograms();

    ok(rt.transactionOutputRegisters.firstElement().registerGet(), 2);
    ok(st.transactionOutputRegisters.firstElement().registerGet(), 3);
    //stop(c);
    ok(""+c, """
Chip: Test             step:    7, maxSteps:   10, running: 0, returnCode: 1
  Processes:
    Process:    0 - Memory           instructions:    2, pc:    0, nextPc:    0, memory:   16 *    8 = 1, 2, 33, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
      Registers :
        Register: Memory_Memory_Value              =   33
        Register: Memory_Memory_1_index            =    1
        Register: Memory_Memory_1_result           =    2
        Register: Memory_Memory_2_index            =    2
        Register: Memory_Memory_2_result           =    3
        Register: Memory_Memory_3_index            =    2
        Register: Memory_Memory_3_value            =   33
      Transactions:
        Transaction   : get      - Memory_1          requested at:    0, finished at:    1, returnCode:  0, executable: 0, finished: 1
          Inputs      :
            Memory_Memory_1_index  =    1
          Outputs     :
            Memory_Memory_1_result =    2
        Transaction   : get      - Memory_2          requested at:    1, finished at:    2, returnCode:  0, executable: 0, finished: 1
          Inputs      :
            Memory_Memory_2_index  =    2
          Outputs     :
            Memory_Memory_2_result =    3
        Transaction   : set      - Memory_3          requested at:    4, finished at:    5, returnCode:  0, executable: 0, finished: 1
          Inputs      :
            Memory_Memory_3_index  =    2
            Memory_Memory_3_value  =   33
          Outputs     :
    Process:    1 - Requests         instructions:    7, pc:    7, nextPc:   -1, memory:    1 *    8 = 1
      Registers :
        Register: Requests_Memory_Value            =    0
        Register: Requests_index1                  =    1
        Register: Requests_index2                  =    2
        Register: Requests_value                   =   33
""");
    c.chipGenerateVerilog();
   }

  static void test_arithmeticFibonacci()
   {final int B = 16, N = 16;
    var C  = chip("Test");
    var p  = C.process("Main", 0, 0);

    var a  = p.register("a",  B);
    var b  = p.register("b",  B);
    var c  = p.register("c",  B);
    var i  = p.register("i",  B);

    var m  = C.new MemoryProcess("Memory", B, N);                               // Mmeory controller
    var t  = m.new Set(p);                                                      // Create a transaction to update memory

    p.new Instruction()                                                         // Initialize
     {void action()
       {a.zero(); b.one(); i.zero();
       }
      String verilog()
       {return "";
       }
     };

    for (int j = 0; j < N; j++)                                                 // Fibonacci numbers
     {p.new Instruction()
       {void action()
         {c.copy(a); c.add(b); a.copy(b); b.copy(c);
          t.executeTransaction(i, c);
         }
        String verilog()
         {return "";
         }
       };
      p.new Instruction()
       {void action()
         {i.inc();
         }
        String verilog()
         {return "";
         }
       };

      t.waitResultOfTransaction(5);                                             // Request value of memory at the index
     }

    m.memoryProcessGenerate();

    C.maxSteps = 100;
    C.chipRunPrograms();
    //stop(C);
    ok(""+C, """
Chip: Test             step:   50, maxSteps:  100, running: 0, returnCode: 0
  Processes:
    Process:    0 - Memory           instructions:    2, pc:    0, nextPc:    0, memory:   16 *   16 = 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597
      Registers :
        Register: Memory_Memory_Value              = 1597
        Register: Memory_Memory_1_index            =   15
        Register: Memory_Memory_1_value            = 1597
      Transactions:
        Transaction   : set      - Memory_1          requested at:   46, finished at:   47, returnCode:  0, executable: 0, finished: 1
          Inputs      :
            Memory_Memory_1_index  =   15
            Memory_Memory_1_value  = 1597
          Outputs     :
    Process:    1 - Main             instructions:   49, pc:   49, nextPc:   -1, memory:    0 *    0      Registers :
        Register: Main_Memory_Value                =    0
        Register: Main_a                           =  987
        Register: Main_b                           = 1597
        Register: Main_c                           = 1597
        Register: Main_i                           =   16
""");
    //c.chipGenerateVerilog();
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {test_memory();
    test_memoryProcess();
    test_arithmeticFibonacci();
   }

  static void newTests()                                                        // Tests being worked on
   {oldTests();
    //test_memory();
    //test_memoryProcess();
    //test_arithmeticFibonacci();
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
