//------------------------------------------------------------------------------
// Btree using block, asynchronous memory.
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
package com.AppaApps.Silicon;                                                   // Btree in a block on the surface of a silicon chip.

import java.util.*;

class Chip extends Test                                                         // A chip designed to manipulate a btree in a block of memory
 {boolean running;                                                              // True when the chip is running
  final Map<String, Process> processes = new TreeMap<>();                       // A fixed set of processes for this chip

  class Process                                                                 // A process consists of memory, registers and a program
   {final String             processName;                                       // The name of the process
    final int                processNumber;                                     // The unique number of this process
    final int                memoryWidth;                                       // The width of each memory element in bits
    final int                memorySize;                                        // The number of memory elements in the memory available to this process
    final BitSet[]           memory;                                            // Memory is an array of elements of bits
    final Register           memoryRegister;                                    // A register that is ised to interact with memory
    final Stack<Transaction> transactions   = new Stack<>();                    // Transactions to watch for work requests
    final Map<String, Register> registers   = new TreeMap<>();                  // Registers used in this process
    final Stack<Instruction> code           = new Stack<>();                    // A fixed set of instructions for this process
    final Stack<Label>       labels         = new Stack<>();                    // Labels for instructions in this process
    int                      step           = 0;                                // Step in the program
    int                      maxSteps       = 200;                              // Maximum number of steps to execute
    int                      pc             = 0;                                // The index of the next instruction to be executed
    Integer                  nextPc         = null;                             // The next program counter requested
    String                   rc             = null;                             // The result of executing the program.  If null then no problems were detected

    Process(String ProcessName, int MemoryWidth, int MemorySize)                // Create a process
     {if (running)
       {stop("Cannot define process:", ProcessName, "when the chip is running");
       }
      processName = ProcessName;
      if (processes.containsKey(processName))
       {stop("Process:", processName, "already defined");
       }
      processNumber = processes.size();
      processes.put(processName, this);
      memorySize    = MemorySize;
      memoryWidth   = MemoryWidth;
      memory        = new BitSet[memorySize];
      for (int i = 0; i < memorySize; i++) memory[i] = new BitSet(memoryWidth);
      memoryRegister = new Register(null, memoryWidth);
     }

    abstract class Instruction                                                  // Instructions implement the action of a program
     {final String traceBack = traceBack();                                     // Line at which this instruction was created

      Instruction()                                                             // Add the instruction to the code
       {if (running)
         {stop("Cannot define instructions when the chip is running");
         }
        code.push(this);
       }
      abstract void action();                                                   // The action to be performed bythe instruction
     }

    class Label                                                                 // Lable jump targets in the program
     {
     }

    class Register                                                              // A register is a non indexable block of memory that can be read by any process but written to only by its owning process
     {final String registerName;                                                // The name of the register
      final int    bits;                                                        // Number of bits the register can hold
      final BitSet value = new BitSet();                                        // Current value of the register

      Register(String RegisterName, int Bits)                                   // Create the register
       {if (running)
         {stop("Cannot define register:", RegisterName, "when the chip is running");
         }
        registerName = RegisterName; bits = Bits;
        if (registers.containsKey(registerName))
         {stop("Register:", registerName, "already defined in process:",
           processName);
         }
        registers.put(registerName, this);
       }

      int asInt()                                                               // Get a register as an integer
       {return value.length() == 0 ? 0 : (int) value.toLongArray()[0];          // Relying on the fact that the Java is only ever test code unlike the Verilog.
       }

      void fromInt(int Value)                                                   // Set the value of the register from an integer
       {final int l = min(bits, Integer.SIZE-1);                                // The most bits we can hope to represent
        value.clear();                                                          // Zero the memory
        for (int i = 0; i < l; i++)                                             // Set each bit in the bitset if the corresponding bit in the value is set
         {if (((Value >> i) & 1) != 0) value.set(i);
         }
       }
     }

    void stepProgram()                                                          // Execute one step in the program
     {nextPc = null;                                                            // The executed instruction can optionally set this variable to change the execution flow
      code.elementAt(pc).action();
      if (nextPc != null) pc = nextPc; else pc++;                               // Interpret next program counter as either a redirection or continuation of flow
     }

    int memoryGet(int Index)                                                    // Get a memory element as an integer
     {return (int) memory[Index].toLongArray()[0];                              // Relying on the fact that the Java is only ever test code unlike the Verilog.
     }

    void memorySet(int Value, int Index)                                        // Set a memory element
     {final int l = min(memoryWidth, Integer.SIZE-1);                           // The most bits we can hope to represent
      final BitSet v = memory[Index];                                           // Memory element
      v.clear();                                                                // Zero the memory
      for (int i = 0; i < l; i++)                                               // Set each bit in the bitset if the corresponding bit in the value is set
       {if (((Value >> i) & 1) != 0) v.set(i);
       }
     }
   } // Process

  class Transaction                                                             // Transactions allow one process to request services from another process
   {final Stack<Process.Register> inputs  = new Stack<>();                      // The registers to read to obtain the input parameters required for this transaction
    final Stack<Process.Register> outputs = new Stack<>();                      // The registers to contain the outputs produced by this transaction
   }

  static void oldTests()                                                        // Tests thought to be in good shape
   {
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
