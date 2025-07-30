<div>
   <p><a href="https://github.com/philiprbrenan/btreeAsm"><img src="https://github.com/philiprbrenan/btreeAsm/workflows/Test/badge.svg"></a>
</div>

# Chip

A chip implements an algorithm in [Silicon](https://en.wikipedia.org/wiki/Silicon). 
A chip exists in one of two states:
<ol>
<li>**Design**: The stage where the chip's layout is defined on [Silicon](https://en.wikipedia.org/wiki/Silicon). <li>**Execution**: When the chip runs a fixed set of parallel [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) to perform work.
</ol>

## Process

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is assigned a unique identifier at design time. A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) includes:
<ol>
<li>A [program](https://en.wikipedia.org/wiki/Computer_program), which in turn is comprised of [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). <li>An optional block of [memory](https://en.wikipedia.org/wiki/Computer_memory) <li>Optional local [registers](https://en.wikipedia.org/wiki/Processor_register) </ol>

### Access Rules

The [Verilog](https://en.wikipedia.org/wiki/Verilog) `always` blocks enforce access rule requirements for [processes](https://en.wikipedia.org/wiki/Process_management_(computing)): <ol>
<li>A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **only write** to its own [memory](https://en.wikipedia.org/wiki/Computer_memory) and [registers](https://en.wikipedia.org/wiki/Processor_register). <li>A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **read** its own [registers](https://en.wikipedia.org/wiki/Processor_register) and the [registers](https://en.wikipedia.org/wiki/Processor_register) of **any** other [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .
</ol>

## Memory

The [memory](https://en.wikipedia.org/wiki/Computer_memory) associated with a [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is comprised of one-dimensional, indexable [arrays](https://en.wikipedia.org/wiki/Dynamic_array): <ol>
<li>Memory size is determined at **runtime**.
<li>Memory contents persist even when the chip is not executing.
<li>Memory is typically accessed over multiple clock cycles by issuing transactions that copy [memory](https://en.wikipedia.org/wiki/Computer_memory) elements to or from local [registers](https://en.wikipedia.org/wiki/Processor_register) for faster access.
<li>Each memeory is owned by a [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .
</ol>

## Registers

Registers are local blocks of [memory](https://en.wikipedia.org/wiki/Computer_memory) :

<ol>
<li>Have a fixed size determined at **compile time**.
<li>Are accessible within a **single clock cycle**.
<li>Lose their values if the chip is powered off.
</ol>

## Program Execution

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) executes a single [program](https://en.wikipedia.org/wiki/Computer_program) composed of sequential [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). A chip may contain multiple [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes can be driven by **transactions**, which are parameter lists of [registers](https://en.wikipedia.org/wiki/Processor_register) provided by the calling [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes execute in a **fixed round-robin order**, allowing behavior in [Verilog](https://en.wikipedia.org/wiki/Verilog) to be compared with [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) simulations reliably.

### Transactions

A **transaction** enables one [process](https://en.wikipedia.org/wiki/Process_management_(computing)) to request work from another [process](https://en.wikipedia.org/wiki/Process_management_(computing)) using a parameter [list](https://en.wikipedia.org/wiki/Linked_list) comprised of [registers](https://en.wikipedia.org/wiki/Processor_register). 
Processes handle transactions in **round-robin polling** mode, fixed at design time. Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)): 
<ol>
<li>Polls its inbound transactions for work.
<li>When a [transaction](https://en.wikipedia.org/wiki/Database_transaction) is found:
<ol>
   <li>Copies data from the transaction's parameter [registers](https://en.wikipedia.org/wiki/Processor_register) into its own [registers](https://en.wikipedia.org/wiki/Processor_register).    <li>Processes the data.
   <li>Writes results into the transaction's output [registers](https://en.wikipedia.org/wiki/Processor_register).    <li>Marks the [transaction](https://en.wikipedia.org/wiki/Database_transaction) as complete.
</ol>
<li>The requesting [process](https://en.wikipedia.org/wiki/Process_management_(computing)) waits for the completion of a [transaction](https://en.wikipedia.org/wiki/Database_transaction) by spinning on an [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture). </ol>

The output remains in the [transaction](https://en.wikipedia.org/wiki/Database_transaction) [registers](https://en.wikipedia.org/wiki/Processor_register) until they are overwritten by the next use of the same [transaction](https://en.wikipedia.org/wiki/Database_transaction). 
### Deadlock Prevention

To avoid deadlocks, [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) may **only request work from [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) with lower [process](https://en.wikipedia.org/wiki/Process_management_(computing)) numbers**.

## Verilog Implementation

The chip is implemented in [Verilog](https://en.wikipedia.org/wiki/Verilog). Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is mapped to an `always` block that:
<ol>
<li>Executes [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture) comprising in-flight transactions or polls its non-in-flight transactions for work.
<li>Is triggered by a common clock to ensure **synchronous execution**.
<li>Shares a common clock cycle count (step number) across all [processes](https://en.wikipedia.org/wiki/Process_management_(computing)), though each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) has its own [program](https://en.wikipedia.org/wiki/Computer_program) counter.
</ol>
Programs are implemented using a `case` statement where:
<ol>
<li>Each [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) corresponds to a case branch.
<li>The process's [program](https://en.wikipedia.org/wiki/Computer_program) counter selects the next [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) to execute.
</ol>
This is **not** a general-purpose processor. It is a **specialized chip** with **statically optimized** execution paths for minimal time and power consumption.

## Java Simulation

Writing [Verilog](https://en.wikipedia.org/wiki/Verilog) directly is time-consuming and error-prone. Therefore:
<ol>
<li>Algorithms are first written and debugged in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) using familiar programming paradigms and tools.
<li>java [code](https://en.wikipedia.org/wiki/Computer_program) is then **semi-automatically translated** into [Verilog](https://en.wikipedia.org/wiki/Verilog). <li>This approach produces [Verilog](https://en.wikipedia.org/wiki/Verilog) [code](https://en.wikipedia.org/wiki/Computer_program) **more efficiently** and **reliably** than writing it by hand.
</ol>
