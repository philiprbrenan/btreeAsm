<div>
   <p><a href="https://github.com/philiprbrenan/btreeAsm"><img src="https://github.com/philiprbrenan/btreeAsm/workflows/Test/badge.svg"></a>
</div>

# Chip

A chip implements an algorithm in [Silicon](https://en.wikipedia.org/wiki/Silicon). 
A chip exists in one of two states:

- **Design**: The stage where the chip's layout is defined on [Silicon](https://en.wikipedia.org/wiki/Silicon). - **Execution**: When the chip runs a fixed set of parallel [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) to perform work.

## Process

Each **process** is assigned a unique identifier at design time. A process includes:

- A [program](https://en.wikipedia.org/wiki/Computer_program), which in turn is comprised of instructions.
- An optional block of [memory](https://en.wikipedia.org/wiki/Computer_memory) - Optional local registers

### Access Rules

Verilog's `always` blocks enforce access rule requirements for [processes](https://en.wikipedia.org/wiki/Process_management_(computing)): 
- A process can **only write** to its own [memory](https://en.wikipedia.org/wiki/Computer_memory) and registers.
- A process can **read** its own registers and the registers of **any** other process.

## Memory

Memory is comprised of one-dimensional, indexable arrays:

- Memory size is determined at **runtime**.
- Memory contents persist even when the chip is not executing.
- Memory is typically accessed over multiple clock cycles by issuing transactions that copy [memory](https://en.wikipedia.org/wiki/Computer_memory) elements to or from local registers for faster access.
- Each memeory is owned by a process.

## Registers

Registers are local blocks of [memory](https://en.wikipedia.org/wiki/Computer_memory): 
- Have a fixed size determined at **compile time**.
- Are accessible within a **single clock cycle**.
- Lose their values if the chip is powered off.

## Program Execution

Each process executes a **single program** composed of sequential instructions. A chip may contain multiple [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes can be driven by **transactions**, which are parameter lists of registers provided by the calling [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes execute in a **fixed round-robin order**, allowing behavior in Verilog to be compared with Java simulations reliably.

### Transactions

A **transaction** enables one process to request work from another process using a parameter [list](https://en.wikipedia.org/wiki/Linked_list) comprised of registers.

Processes handle transactions in **round-robin polling** mode, fixed at design time. Each process:

1. Polls its inbound transactions for work.
2. When a [transaction](https://en.wikipedia.org/wiki/Database_transaction) is found:
   - Copies data from the transaction's parameter registers into its own registers.
   - Processes the data.
   - Writes results into the transaction's output registers.
   - Marks the [transaction](https://en.wikipedia.org/wiki/Database_transaction) as complete.
3. The requesting process waits for the completion of a [transaction](https://en.wikipedia.org/wiki/Database_transaction) by spinning on an instruction.

The output remains in the [transaction](https://en.wikipedia.org/wiki/Database_transaction) registers until they are overwritten by the next use of the same [transaction](https://en.wikipedia.org/wiki/Database_transaction). 
### Deadlock Prevention

To avoid deadlocks, [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) may **only request work from [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) with lower process numbers**.

## Verilog Implementation

The chip is implemented in **Verilog**. Each process is mapped to an `always` block that:

- Executes instructions comprising in-flight transactions or polls its non-in-flight transactions for work.
- Is triggered by a common clock to ensure **synchronous execution**.
- Shares a common clock cycle count (step number) across all [processes](https://en.wikipedia.org/wiki/Process_management_(computing)), though each process has its own [program](https://en.wikipedia.org/wiki/Computer_program) counter.

Programs are implemented using a `case` statement where:

- Each instruction corresponds to a case branch.
- The process's [program](https://en.wikipedia.org/wiki/Computer_program) counter selects the next instruction to execute.

This is **not** a general-purpose processor. It is a **specialized chip** with **statically optimized** execution paths for minimal time and power consumption.

## Java Simulation

Writing Verilog directly is time-consuming and error-prone. Therefore:

- Algorithms are first written and debugged in **Java** using familiar programming paradigms and tools.
- Java [code](https://en.wikipedia.org/wiki/Computer_program) is then **semi-automatically translated** into Verilog.
- This approach produces Verilog [code](https://en.wikipedia.org/wiki/Computer_program) **more efficiently** and **reliably** than writing it by hand.
