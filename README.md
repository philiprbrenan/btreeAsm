<div>
   <p><a href="https://github.com/philiprbrenan/btreeAsm"><img src="https://github.com/philiprbrenan/btreeAsm/workflows/Test/badge.svg"></a>
</div>

# Chip

A chip implements an algorithm in [Silicon](https://en.wikipedia.org/wiki/Silicon). 
A chip exists in one of two states:

- **Design**: The stage where the chip's layout is defined on [Silicon](https://en.wikipedia.org/wiki/Silicon). 
- **Execution**: When the chip runs a fixed set of parallel [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) to perform work.

## Process

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is assigned a unique identifier at design time. A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) includes:

- A [program](https://en.wikipedia.org/wiki/Computer_program), which in turn is comprised of [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). 
- An optional block of [memory](https://en.wikipedia.org/wiki/Computer_memory) 
- Optional local [registers](https://en.wikipedia.org/wiki/Processor_register) 
### Access Rules

The [Verilog](https://en.wikipedia.org/wiki/Verilog) `always` blocks enforce access rule requirements for [processes](https://en.wikipedia.org/wiki/Process_management_(computing)): 
- A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **only write** to its own [memory](https://en.wikipedia.org/wiki/Computer_memory) and [registers](https://en.wikipedia.org/wiki/Processor_register). 
- A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **read** its own [registers](https://en.wikipedia.org/wiki/Processor_register) and the [registers](https://en.wikipedia.org/wiki/Processor_register) of **any** other [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .

## Memory

The [memory](https://en.wikipedia.org/wiki/Computer_memory) associated with a [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is comprised of one-dimensional, indexable [arrays](https://en.wikipedia.org/wiki/Dynamic_array): 
- Memory size is determined at **runtime**.

- Memory contents persist even when the chip is not executing.

- Memory is typically accessed over multiple [clock](https://en.wikipedia.org/wiki/Clock_generator) cycles by issuing transactions that copy [memory](https://en.wikipedia.org/wiki/Computer_memory) elements to or from local [registers](https://en.wikipedia.org/wiki/Processor_register) for faster access.

- Each memeory is owned by a [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .

## Registers

Registers are local blocks of [memory](https://en.wikipedia.org/wiki/Computer_memory) :

- Have a fixed size determined at **compile time**.

- Are accessible within a **single [clock](https://en.wikipedia.org/wiki/Clock_generator) cycle**.

- Lose their values if the chip is powered off.

## Program Execution

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) executes a single [program](https://en.wikipedia.org/wiki/Computer_program) composed of sequential [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). A chip may contain multiple [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes can be driven by **transactions**, which are parameter lists of [registers](https://en.wikipedia.org/wiki/Processor_register) provided by the calling [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes execute in a **fixed round-robin order**, allowing behavior in [Verilog](https://en.wikipedia.org/wiki/Verilog) to be compared with [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) simulations reliably.

### Transactions

A **transaction** enables one [process](https://en.wikipedia.org/wiki/Process_management_(computing)) to request work from another [process](https://en.wikipedia.org/wiki/Process_management_(computing)) using a parameter [list](https://en.wikipedia.org/wiki/Linked_list) comprised of [registers](https://en.wikipedia.org/wiki/Processor_register). 
Processes handle transactions in **round-robin polling** mode, fixed at design time. Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)): 
1. Polls its inbound transactions for work.

2. When a [transaction](https://en.wikipedia.org/wiki/Database_transaction) is found:

   - Copies data from the transaction's parameter [registers](https://en.wikipedia.org/wiki/Processor_register) into its own [registers](https://en.wikipedia.org/wiki/Processor_register). 
   - Processes the data.

   - Writes results into the transaction's output [registers](https://en.wikipedia.org/wiki/Processor_register). 
   - Marks the [transaction](https://en.wikipedia.org/wiki/Database_transaction) as complete.

3. The requesting [process](https://en.wikipedia.org/wiki/Process_management_(computing)) waits for the completion of a [transaction](https://en.wikipedia.org/wiki/Database_transaction) by spinning on an [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture). 
The output remains in the [transaction](https://en.wikipedia.org/wiki/Database_transaction) [registers](https://en.wikipedia.org/wiki/Processor_register) until they are overwritten by the next use of the same [transaction](https://en.wikipedia.org/wiki/Database_transaction). 
### Deadlock Prevention

To avoid deadlocks, [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) may **only request work from [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) with lower [process](https://en.wikipedia.org/wiki/Process_management_(computing)) numbers**.

## Verilog Implementation

The chip is implemented in [Verilog](https://en.wikipedia.org/wiki/Verilog). Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is mapped to an `always` block that:

- Executes [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture) comprising in-flight transactions or polls its non-in-flight transactions for work.

- Is triggered by a common [clock](https://en.wikipedia.org/wiki/Clock_generator) to ensure **synchronous execution** .

- Shares a common [clock](https://en.wikipedia.org/wiki/Clock_generator) cycle count (step number) across all [processes](https://en.wikipedia.org/wiki/Process_management_(computing)), though each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) has its own [program](https://en.wikipedia.org/wiki/Computer_program) counter.

Programs are implemented using a `case` statement where:

- Each [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) corresponds to a case branch.

- The process's [program](https://en.wikipedia.org/wiki/Computer_program) counter selects the next [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) to execute.

This is **not** a general-purpose processor. It is a **specialized chip** with **statically optimized** execution paths for minimal time and power consumption.

## Java Simulation

Writing [Verilog](https://en.wikipedia.org/wiki/Verilog) directly is time-consuming and error-prone. Therefore:

- Algorithms are first written and debugged in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) using familiar programming paradigms and tools.

- [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) [code](https://en.wikipedia.org/wiki/Computer_program) is then **semi-automatically translated** into [Verilog](https://en.wikipedia.org/wiki/Verilog). 
- This approach produces [Verilog](https://en.wikipedia.org/wiki/Verilog) [code](https://en.wikipedia.org/wiki/Computer_program) **more efficiently** and **reliably** than writing it by hand.
