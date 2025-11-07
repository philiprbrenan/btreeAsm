[![Test](https://github.com/philiprbrenan/btreeAsm/workflows/Test/badge.svg)](https://github.com/philiprbrenan/btreeAsm)

[Read the docs](https://btreeasm.readthedocs.io/en/latest/README.html)

# Goal

Use [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) to generate [synthesizable](https://en.wikipedia.org/wiki/Logic_synthesis) [Verilog](https://en.wikipedia.org/wiki/Verilog) to layout a [B-Tree](https://en.wikipedia.org/wiki/B-tree) on the surface of
a specialized [Silicon](https://en.wikipedia.org/wiki/Silicon) [chip](https://en.wikipedia.org/wiki/Integrated_circuit) so that the [B-Tree](https://en.wikipedia.org/wiki/B-tree) [find](https://en.wikipedia.org/wiki/Find_(Unix)), put and delete
operations can be performed more quickly and efficiently than [software](https://en.wikipedia.org/wiki/Software) running
on a generic processor . Such a chip could act as an accelerator for database
operations, a:  [Database on a Chip](https://github.com/philiprbrenan/btreeAsm) .

![put](https://raw.githubusercontent.com/philiprbrenan/btreeAsm/refs/heads/main/images/Btree.png)

---

# Chip

A [chip](https://en.wikipedia.org/wiki/Integrated_circuit) implements an [algorithm](https://en.wikipedia.org/wiki/Algorithm) in [Silicon](https://en.wikipedia.org/wiki/Silicon). 
A [chip](https://en.wikipedia.org/wiki/Integrated_circuit) design exists in one of two states:

- **Design**: The stage where the layout of the [chip](https://en.wikipedia.org/wiki/Integrated_circuit) is described in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) 
- **Execution**: When the [chip](https://en.wikipedia.org/wiki/Integrated_circuit) runs a fixed set of parallel [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) to perform work.  This work is simulated in [Verilog](https://en.wikipedia.org/wiki/Verilog) 
## Process

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is assigned a unique identifier at design time. A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) includes:

- A [program](https://en.wikipedia.org/wiki/Computer_program), which in turn is comprised of [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). 
- An optional block of [memory](https://en.wikipedia.org/wiki/Computer_memory) 
- Optional local [registers](https://en.wikipedia.org/wiki/Processor_register) 
## Access Rules

The [Verilog](https://en.wikipedia.org/wiki/Verilog) `always` blocks enforce access rule requirements for [processes](https://en.wikipedia.org/wiki/Process_management_(computing)): 
- A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **only write** to its own [memory](https://en.wikipedia.org/wiki/Computer_memory) and [registers](https://en.wikipedia.org/wiki/Processor_register). 
- A [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can **read** its own [registers](https://en.wikipedia.org/wiki/Processor_register) and the [registers](https://en.wikipedia.org/wiki/Processor_register) of **any** other [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .

## Memory

The [memory](https://en.wikipedia.org/wiki/Computer_memory) associated with a [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is comprised of one-dimensional, indexable [arrays](https://en.wikipedia.org/wiki/Dynamic_array): 
- Memory size is determined at **runtime**.

- Memory contents persist even when the [chip](https://en.wikipedia.org/wiki/Integrated_circuit) is not executing.

- Memory is typically accessed over multiple [clock](https://en.wikipedia.org/wiki/Clock_generator) cycles by issuing transactions that copy [memory](https://en.wikipedia.org/wiki/Computer_memory) elements to or from local [registers](https://en.wikipedia.org/wiki/Processor_register) for faster access.

- Each [memory](https://en.wikipedia.org/wiki/Computer_memory) is owned by a single [process](https://en.wikipedia.org/wiki/Process_management_(computing)) .

### BlackBox Synthesis

Memory is an ideal candidate for geometrical layout given its regular structure
over a large number of elements.  OpenRAM exploits this structure to
synthesize large memories that can then be imbedded as black boxes in other
designs which would otherwise overwhelem the place and route phases of [synthesis](https://en.wikipedia.org/wiki/Logic_synthesis). 
## Registers

Registers are local blocks of [memory](https://en.wikipedia.org/wiki/Computer_memory) that:

- Have a fixed size determined at **compile time**.

- Are accessible within a **single [clock](https://en.wikipedia.org/wiki/Clock_generator) cycle**.

- Lose their values if the [chip](https://en.wikipedia.org/wiki/Integrated_circuit) is powered off.

- Start set to zero when the [chip](https://en.wikipedia.org/wiki/Integrated_circuit) is powered on

### Single versus arrayed

Registers can contain just a single element in a [register](https://en.wikipedia.org/wiki/Processor_register) or a predefined
number of elements in an arrayed [register](https://en.wikipedia.org/wiki/Processor_register). Single and arrayed [registers](https://en.wikipedia.org/wiki/Processor_register) can be
copied into other single and arrayed [registers](https://en.wikipedia.org/wiki/Processor_register), with either the whole [register](https://en.wikipedia.org/wiki/Processor_register) being copied or with specific elements being selected from arrayed [registers](https://en.wikipedia.org/wiki/Processor_register) with either integer and/or single [register](https://en.wikipedia.org/wiki/Processor_register) indices.

## Unknown values

When the chip is powered on, the values of bits in [memory](https://en.wikipedia.org/wiki/Computer_memory) are unknown. I
considered these possibilities:

1. Track the unknown status of each [bit](https://en.wikipedia.org/wiki/Bit) in [memory](https://en.wikipedia.org/wiki/Computer_memory). 
2. Track the unknown status of each [word](https://en.wikipedia.org/wiki/Doc_(computing)) in [memory](https://en.wikipedia.org/wiki/Computer_memory). 
3. Set [memory](https://en.wikipedia.org/wiki/Computer_memory) to a known value so that there are no unknown values after power
on.

I chose to set [memory](https://en.wikipedia.org/wiki/Computer_memory) to known values on power on because:

- Verilog does track whether each [bit](https://en.wikipedia.org/wiki/Bit) in [memory](https://en.wikipedia.org/wiki/Computer_memory) has a known value or not so
there seems to be no point in reinventing the wheel by duplicating work already
being done by Verilog.

- The Java execution [speed](https://en.wikipedia.org/wiki/Speed) which is already slow enough enough would be slowed
even further, yet any bugs it might catch would be caught a few seconds later
by the Verilog simulation  so the extra execution time buys very little
compared to its cost.

- Memory is copied into [registers](https://en.wikipedia.org/wiki/Processor_register) and so [registers](https://en.wikipedia.org/wiki/Processor_register) would also have to track
whether their values were known or not leading to more complexity in the Java [code](https://en.wikipedia.org/wiki/Computer_program), which should, instead, be focussed on the [algorithms](https://en.wikipedia.org/wiki/Algorithm) used rather then
their bitwise implementation.




## Program Execution

Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) executes a single [program](https://en.wikipedia.org/wiki/Computer_program) composed of sequential [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture). A [chip](https://en.wikipedia.org/wiki/Integrated_circuit) may contain multiple [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes can be driven by transactions, which are parameter lists of [registers](https://en.wikipedia.org/wiki/Processor_register) provided by the calling [processes](https://en.wikipedia.org/wiki/Process_management_(computing)). 
Processes are execute in a fixed, round-robin order set at design time,
allowing the behavior of the [Verilog](https://en.wikipedia.org/wiki/Verilog) [code](https://en.wikipedia.org/wiki/Computer_program) to be validated by comparing the time
evolution of memories and [registers](https://en.wikipedia.org/wiki/Processor_register) under [Verilog](https://en.wikipedia.org/wiki/Verilog) with the trace of the same
under [Java](https://en.wikipedia.org/wiki/Java_(programming_language)). 
## Transactions

### Transactions Between Processes

A **transaction** allows one [process](https://en.wikipedia.org/wiki/Process_management_(computing)) to request work from another [process](https://en.wikipedia.org/wiki/Process_management_(computing)) by
providing a **parameter list** composed of [registers](https://en.wikipedia.org/wiki/Processor_register). The [transaction](https://en.wikipedia.org/wiki/Database_transaction) [registers](https://en.wikipedia.org/wiki/Processor_register) are categorized as follows:

- **Input registers** â owned by the calling [process](https://en.wikipedia.org/wiki/Process_management_(computing)). - **Output registers** â owned by the called [process](https://en.wikipedia.org/wiki/Process_management_(computing)). 
---

### Transaction Handling

Processes handle transactions in a **round-robin polling mode**, determined at
design time. Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) follows this sequence:

1. **Poll inbound transactions**
   The [process](https://en.wikipedia.org/wiki/Process_management_(computing)) checks its inbound transactions for pending work.
   A [transaction](https://en.wikipedia.org/wiki/Database_transaction) indicates that work is pending by setting its
   **`requested_at`** [register](https://en.wikipedia.org/wiki/Processor_register) to a step value **greater than** its
   **`finished_at`** [register](https://en.wikipedia.org/wiki/Processor_register). 
2. **When a [transaction](https://en.wikipedia.org/wiki/Database_transaction) with work is found:**

   - **Read input data**
     Copy data from the transactionâs input [registers](https://en.wikipedia.org/wiki/Processor_register) into the processâs own
     [registers](https://en.wikipedia.org/wiki/Processor_register). This is allowed because any [process](https://en.wikipedia.org/wiki/Process_management_(computing)) can [read](https://en.wikipedia.org/wiki/Reading_(computer)) any [register](https://en.wikipedia.org/wiki/Processor_register). 
   - **Execute the transaction**
     Perform the requested operation using the copied data.

   - **Write output data**
     Store the results of the execution into the transactionâs output [registers](https://en.wikipedia.org/wiki/Processor_register).      This is permitted because the target [process](https://en.wikipedia.org/wiki/Process_management_(computing)) owns the output [registers](https://en.wikipedia.org/wiki/Processor_register). 
   - **Mark the [transaction](https://en.wikipedia.org/wiki/Database_transaction) as complete**
     Update the **`finished_at`** [register](https://en.wikipedia.org/wiki/Processor_register) to the current step value. This value
     will now be greater than the **`requested_at`** [register](https://en.wikipedia.org/wiki/Processor_register), signaling that
     the [transaction](https://en.wikipedia.org/wiki/Database_transaction) has completed and its results are available to the caller.


3. The requesting [process](https://en.wikipedia.org/wiki/Process_management_(computing)) waits for the completion of a [transaction](https://en.wikipedia.org/wiki/Database_transaction) by spinning on an [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture). 
The output remains in the [transaction](https://en.wikipedia.org/wiki/Database_transaction) [registers](https://en.wikipedia.org/wiki/Processor_register) until they are overwritten by
the next use of the same [transaction](https://en.wikipedia.org/wiki/Database_transaction). 
## Deadlock Prevention

To avoid deadlocks, [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) may only request work from [processes](https://en.wikipedia.org/wiki/Process_management_(computing)) with lower [process](https://en.wikipedia.org/wiki/Process_management_(computing)) numbers.

## Verilog Implementation

The [chip](https://en.wikipedia.org/wiki/Integrated_circuit) is implemented in [Verilog](https://en.wikipedia.org/wiki/Verilog). Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) is mapped to an
`always` block that:

- Executes [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture) comprising in-flight transactions or polls its non-in-flight transactions for work.

- Is triggered by a common [clock](https://en.wikipedia.org/wiki/Clock_generator) to ensure synchronous execution.

- Shares a common [clock](https://en.wikipedia.org/wiki/Clock_generator) cycle count (step number) across all [processes](https://en.wikipedia.org/wiki/Process_management_(computing)), though each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) has its own [program](https://en.wikipedia.org/wiki/Computer_program) counter.

Programs are implemented using a `case` statement where:

- Each [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) corresponds to a case branch.

- Each [process](https://en.wikipedia.org/wiki/Process_management_(computing)) has a [program](https://en.wikipedia.org/wiki/Computer_program) counter that selects the next [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) to be executed.

This is **not** a general-purpose processor. It is a specialized [chip](https://en.wikipedia.org/wiki/Integrated_circuit) with **statically optimized** execution paths for minimal time and power
consumption.

## Java and Verilog Simulation

Writing [Verilog](https://en.wikipedia.org/wiki/Verilog) directly is time-consuming and error-prone. To mitigate this
problem:

- The [algorithm](https://en.wikipedia.org/wiki/Algorithm) is first written and debugged in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) using familiar programming paradigms and tools.

- The [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) is then **almost-automatically translated** into [Verilog](https://en.wikipedia.org/wiki/Verilog). 
- The [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) is executed to produce a trace showing how the [memory](https://en.wikipedia.org/wiki/Computer_memory) and [registers](https://en.wikipedia.org/wiki/Processor_register) of the chip should evolve in time.

- The [Verilog](https://en.wikipedia.org/wiki/Verilog) is executed and checked to confirm that it produces the same [memory](https://en.wikipedia.org/wiki/Computer_memory) and [register](https://en.wikipedia.org/wiki/Processor_register) trace as the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) does

This approach produces [Verilog](https://en.wikipedia.org/wiki/Verilog) **much more efficiently** and **reliably** than
writing it by hand. For example, I was able to implement a non-recursive
reverse [Iterator](https://en.wikipedia.org/wiki/Iterator) over the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) entirely in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)), without writing any [Verilog](https://en.wikipedia.org/wiki/Verilog) by
hand at all, and the generated [Verilog](https://en.wikipedia.org/wiki/Verilog) synthesized, placed, and routed
correctly on the first attempt.

However, similar to the MIT **Midnight Computer Wiring Society** motto:
*If you donât like how the machine works - change it*; if extra [instructions](https://en.wikipedia.org/wiki/Instruction_set_architecture) are needed, perhaps to increase parallelism, it is relatively easy to add a [Verilog](https://en.wikipedia.org/wiki/Verilog) implementation of an [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) sequence written in [Java](https://en.wikipedia.org/wiki/Java_(programming_language)), which can
then be reused from [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) to generate [Verilog](https://en.wikipedia.org/wiki/Verilog) as needed.

As a consequence of the matching of the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) and [Verilog](https://en.wikipedia.org/wiki/Verilog) traces, no additional
tests are required within the [Verilog](https://en.wikipedia.org/wiki/Verilog) itself; it is sufficient to [verify](https://en.wikipedia.org/wiki/Software_verification_and_validation) that
the [Verilog](https://en.wikipedia.org/wiki/Verilog) implementation updates the [memory](https://en.wikipedia.org/wiki/Computer_memory) and [registers](https://en.wikipedia.org/wiki/Processor_register) on the chip in
lockstep with the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) version to confirm that the [Verilog](https://en.wikipedia.org/wiki/Verilog) version accurately
implements the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) version.

If the [Verilog](https://en.wikipedia.org/wiki/Verilog) trace and the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) trace do not match, the [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) that is
causing the mismatch can be located by setting "processTrace" to **true** for
the [process](https://en.wikipedia.org/wiki/Process_management_(computing)) under development to get a traceback identifying the [instruction](https://en.wikipedia.org/wiki/Instruction_set_architecture) that is executing differently in the [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) version versus the [Verilog](https://en.wikipedia.org/wiki/Verilog) version.

---

# Double Btree Structure

Each [B-Tree](https://en.wikipedia.org/wiki/B-tree) has its own dedicated [memory](https://en.wikipedia.org/wiki/Computer_memory), which can be of any reasonable size
supported by the chip fabrication [process](https://en.wikipedia.org/wiki/Process_management_(computing)). However, as chip size increases, the
likelihood of manufacturing defects grows and access to individual [memory](https://en.wikipedia.org/wiki/Computer_memory) elements becomes slower.

To address these issues, we employ a double [B-Tree](https://en.wikipedia.org/wiki/B-tree) architecture. Each stuck of
the double [B-Tree](https://en.wikipedia.org/wiki/B-tree) is implemented as a separate chip containing a single [B-Tree](https://en.wikipedia.org/wiki/B-tree) . [B-Trees](https://en.wikipedia.org/wiki/B-tree) are naturally suited to representing a stuck, as shown in **Dt.java**.
The individual [B-Trees](https://en.wikipedia.org/wiki/B-tree) communicate through the star-topology [network](https://en.wikipedia.org/wiki/Computer_network) described
in **TreeNet.java**.

Constructing a double [B-Tree](https://en.wikipedia.org/wiki/B-tree) from multiple single [B-Trees](https://en.wikipedia.org/wiki/B-tree) reduces manufacturing
costs by improving yield (since smaller [chips](https://en.wikipedia.org/wiki/Integrated_circuit) are less prone to defects) and
increases [memory](https://en.wikipedia.org/wiki/Computer_memory) access [speed](https://en.wikipedia.org/wiki/Speed) through parallelism, as each single [B-Tree](https://en.wikipedia.org/wiki/B-tree) controls its own independent block of [memory](https://en.wikipedia.org/wiki/Computer_memory). These advantages come at the
expense of logarithmic [network](https://en.wikipedia.org/wiki/Computer_network) communication overhead between the single [B-Trees](https://en.wikipedia.org/wiki/B-tree) that together form the double [B-Tree](https://en.wikipedia.org/wiki/B-tree) .

---

# Stuck Data Structure

A **Stuck** serves as the **fundamental node representation** inside a **btree**.
Instead of using pointers to dynamically allocated elements, each node of the [B-Tree](https://en.wikipedia.org/wiki/B-tree) contains a **Stuck** that stores its **keys and associated values (or child pointers)** in a fixed-size, contiguous block.

A **Stuck** is a **fixed-size stack-like array** designed to hold **key-data pairs** in **sorted order by key**.

![Stuck Diagram](https://raw.githubusercontent.com/philiprbrenan/btreeAsm/refs/heads/main/images/Stuck.png)

A **Stuck** combines the **boundedness of a stack**, the **ordered nature of a sorted array**, and the **explicit size field** to manage contiguous usage efficiently.

---

## Storage Layout
- Implemented as a **contiguous [array](https://en.wikipedia.org/wiki/Dynamic_array) of slots**.
- Each slot contains:
  - **Key field** (`K bits`)
  - **Data field** (`D bits`)
- The structure has a **fixed capacity** `N`.

---

## Active Region
- The Stuck maintains a **size field** that records how many slots are currently in use.
- **Active elements** occupy indices `0` through `size-1`, starting from the **bottom**.
- All higher indices are **unused (empty)**.

---

## Ordering
- Keys in the active region are maintained in **sorted order**.
- Insertions require shifting elements upward to preserve ordering.
- Removals require shifting elements downwards to preserve ordering.

---

## Operations
- **Insert(Index, Key, Data)**
  Insert a new key/data pair into the correct position. May require shifting.
  If the structure is full, insertion fails.

- **Remove(Index)**
  Remove a key/data pair at the specified index, shifting elements down to maintain contiguity.

- **Search(Key)**
  Perform linear or binary search over the active region.

- **Size()**
  Return the number of active elements.

---

## Properties
- Fixed capacity (like an array).
- Maintains **keys in sorted order**.
- **Active elements are contiguous** from the bottom up.
- **Fast access** to elements by index.

---

## Role in a B-tree

1. **Key Storage**
   - Each [B-Tree](https://en.wikipedia.org/wiki/B-tree) node maintains its set of keys inside a Stuck.
   - The Stuck keeps keys **sorted**, which is crucial for search, insert, and delete operations in a [B-Tree](https://en.wikipedia.org/wiki/B-tree) .

2. **Efficient Search**
   - Because keys in a Stuck are ordered, **binary search** can be applied within the node.
   - This reduces search cost inside a node from linear to logarithmic.

3. **Insert & Split Support**
   - When inserting a key into a node, the Stuck shifts entries to maintain sorted order.
   - If the Stuck is full, the node **splits**: half the keys are promoted or moved to a new node, consistent with [B-Tree](https://en.wikipedia.org/wiki/B-tree) balancing rules.

4. **Delete & Merge Support**
   - On deletion, the Stuck shifts entries downward to close gaps.
   - If underflow occurs, merging or borrowing between nodes uses Stuck operations to re-balance.

5. **Child Pointers**
   - In internal nodes, each Stuck entry may also hold a **pointer/reference to a child Stuck** (i.e., another node).
   - Thus, the Stuck serves as both a **container of keys** and a **directory of child subtrees**.

## Why a Stuck is a Good Fit

- **Contiguous [memory](https://en.wikipedia.org/wiki/Computer_memory) layout** : cache-friendly.
- **Fixed capacity** : simplifies [memory](https://en.wikipedia.org/wiki/Computer_memory) management.
- **Size field** : explicitly tracks how full the node is.
- **Ordered** : maintains the keys in order so they can be searched quickly

---

# Silicon Compiler

The [Verilog](https://en.wikipedia.org/wiki/Verilog) produced by **Btree.java** is transformed into a chip [mask](https://en.wikipedia.org/wiki/Integrated_circuit_layout) using [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) .

## Run Silicon Compiler

Run [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) on **Btree** on any machine with **docker** and **git** installed.

```
git clone git@github.com:philiprbrenan/btreeAsm.git  # Download repo
(cd btreeAsm; bash Btree.sh)                         # Run repo in a docker container
```

## Create Silicon Compiler Docker Container

The following notes explain how to create the [Docker](https://en.wikipedia.org/wiki/Docker_(software)) container referenced above
to run [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) .

### OpenROAD

Prebuilt [Open Road](https://openroad-flow-scripts.readthedocs.io/en/latest/mainREADME.html) binaries are available for [Ubuntu](https://ubuntu.com/download/desktop) 22:


```
docker run -it --rm ubuntu:22.04 /bin/bash
```

Install the following **deb** [file](https://en.wikipedia.org/wiki/Computer_file): 
```
wget https://github.com/Precision-Innovations/OpenROAD/releases/download/2024-12-14/openroad_2.0-17598-ga008522d8_amd64-ubuntu-22.04.deb
apt install ...deb
openroad -V
```

### Silicon compiler

Create a [Python](https://www.python.org/) virtual environment, activate it, [install](https://en.wikipedia.org/wiki/Installation_(computer_programs)) [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) and
confirm the installation:

```
sudo apt update
sudo apt install python3-dev python3-pip python3-venv
python3 -m venv  ./sc
source ./sc/bin/activate
pip install --upgrade pip siliconcompiler
pip show siliconcompiler
python3 -c "import siliconcompiler;print(siliconcompiler.__version__)"
```

### Yosys

Download **yosys** and confirm correct installation:

```
wget https://github.com/YosysHQ/oss-cad-suite-build/releases/download/2025-08-24/oss-cad-suite-linux-x64-20250824.tgz
tar -xvf
export PATH="$PATH:/root/oss-cad-suite/bin/"
yosys -V
```

### Klayout

Install **Klayout** and confirm correct installation:

```
wget https://www.klayout.org/downloads/master/ubuntu22/klayout_master-1_amd64.deb
klayout -v
```

### Upload the container to Github containers

To save the configured container to [GitHub](https://github.com/philiprbrenan): 
#### 1. Log in to GHCR

```
echo <.githubToken.xxx> | docker login ghcr.io -u philiprbrenan --password-stdin
```

#### 2. Create an image

Make an image from the [Docker](https://en.wikipedia.org/wiki/Docker_(software)) container:

```
docker commit <container> btreeasm:v1
```

#### 3. Tag the image

Give the [Docker](https://en.wikipedia.org/wiki/Docker_(software)) image a name that [GitHub](https://github.com/philiprbrenan) can recognize:

```
docker tag btreeasm:v1 ghcr.io/philiprbrenan/btreeasm:latest
```

#### 4. Push the image

Push the [Docker](https://en.wikipedia.org/wiki/Docker_(software)) image to [GitHub](https://github.com/philiprbrenan): 
```
docker push ghcr.io/philiprbrenan/btreeasm:latest
```

## 5. (Optional) Make the package public

Make the [Docker](https://en.wikipedia.org/wiki/Docker_(software)) image public:

```
https://github.com/users/philiprbrenan/packages/container/package/btreeAsm
```

Then: Package Settings -> Change visibility -> Public


# Tree Network

**TreeNet.java** contains details of a [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) designed to connect a
plurality of single [B-Trees](https://en.wikipedia.org/wiki/B-tree) into a double [B-Tree](https://en.wikipedia.org/wiki/B-tree) . The [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) operates in
logarithmic time with respect to the number of leaves connected together.

Using a [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) allows commands, data, and results to be passed between
the leaves of the [network](https://en.wikipedia.org/wiki/Computer_network) that defines the double [B-Tree](https://en.wikipedia.org/wiki/B-tree). Each leaf is either a
processor requesting actions on the [B-Tree](https://en.wikipedia.org/wiki/B-tree) or a single [B-Tree](https://en.wikipedia.org/wiki/B-tree) itself responding
to these requests. A message sent across the [network](https://en.wikipedia.org/wiki/Computer_network) from a processor to a
single [B-Tree](https://en.wikipedia.org/wiki/B-tree) consists of a command and data, much like entering a command on
the terminal command line. The responses from the single [B-Tree](https://en.wikipedia.org/wiki/B-tree) leaves are then
returned to the processors via the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network).  Thus, a **find** processor
might ask for the data associated with a specified key if it is present in the
single [B-Tree](https://en.wikipedia.org/wiki/B-tree) targeted by the message.

This arrangement lets us get more work out of [memory](https://en.wikipedia.org/wiki/Computer_memory), which currently typically
only responds to a small number of commands such as [read](https://en.wikipedia.org/wiki/Reading_(computer)), [write](https://en.wikipedia.org/wiki/Write_(system_call)), or clear. It
also allows for a great deal more parallelism than conventional monolithic [memory](https://en.wikipedia.org/wiki/Computer_memory), because [memory](https://en.wikipedia.org/wiki/Computer_memory) requests can run in parallel and are only occasionally
blocked when collisions between upward-traveling messages need to be resolved.
Conventional memories can usually only [process](https://en.wikipedia.org/wiki/Process_management_(computing)) one or two requests at a time
due to the the need to unambiguously form the address of the location to be [read](https://en.wikipedia.org/wiki/Reading_(computer)) or written.

## Types of Tree Network

In a **duplex** [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) any leaf can send a message to any other leaf.

In a **simplex** [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network), any leaf node can send a message to the root
node and receive a response. This setup is particularly useful for [memory](https://en.wikipedia.org/wiki/Computer_memory) operations.

- For a [memory](https://en.wikipedia.org/wiki/Computer_memory) **read** command, the leaf sends an address to the root, which
manages the [memory](https://en.wikipedia.org/wiki/Computer_memory), and receives the value stored at that address.

- For a [memory](https://en.wikipedia.org/wiki/Computer_memory) **write** command, the leaf sends both an address and a new
value. The root updates the [memory](https://en.wikipedia.org/wiki/Computer_memory) at that location and sends back a
confirmation that the [write](https://en.wikipedia.org/wiki/Write_(system_call)) operation has been successfully completed.

Regardless of the type of [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)), the messages sent between nodes always take the
shortest possible path. Since there is only one such shortest path between any
two nodes in a [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)), a later message cannot overtake an earlier one. Thus,
messages always arrive at their destination in the same order as they were sent
from each node. Of course, if multiple sources are sending messages to the same
target node, their messages might become interleaved. However, when classified
by source node, they will still arrive at the target in the same order in which
they were sent.


## Transaction processing across a simplex [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) 
One method of performing transactions across a simplex [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) is to use
an input arrayed [register](https://en.wikipedia.org/wiki/Processor_register) to hold the data to be sent from a leaf node to the
root, and another output arrayed [register](https://en.wikipedia.org/wiki/Processor_register) to receive the corresponding results
received at that leaf from the root node of the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network). The contents of
the input [register](https://en.wikipedia.org/wiki/Processor_register) are loaded sequentially into the [network](https://en.wikipedia.org/wiki/Computer_network) at a leaf node. The
input data elements are transmitted to the root of the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) where they are
processed and the results are transmitted back across the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) from the
root to the orginal source node. The dimension of the arrayed input [register](https://en.wikipedia.org/wiki/Processor_register) need not be the same as the dimension of the output arrayed [register](https://en.wikipedia.org/wiki/Processor_register), but, we
do need to know these dimensions in advance.

Sends are performed sequentially. After a short delay, the results of these
requests return in order. The first response cannot occur in less than twice
the height of the [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)); therefore, any data arriving earlier indicates that an
error has occurred.

This technique allows the path width to [memory](https://en.wikipedia.org/wiki/Computer_memory) to be reduced to any positive
size smaller than the full width. In a simplex [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)), the return path does not
need to match the width of the outgoing path although so far, in this
project, the send and return paths have always had the same width.

## Manufacturing [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) networks

Using a [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) [network](https://en.wikipedia.org/wiki/Computer_network) architecture allows individual [B-Tree](https://en.wikipedia.org/wiki/B-tree) units to be
manufactured separately and later arranged in two- or three-dimensional [arrays](https://en.wikipedia.org/wiki/Dynamic_array), where they can be interconnected to form large double [B-Trees](https://en.wikipedia.org/wiki/B-tree). 
Tree networks also provide natural decoupling between components, simplifying
design, scaling, and fault isolation.

Moreover, sub-networks of a [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) structure can be linked over large distances
using photonic interconnects, enabling double [B-Tree](https://en.wikipedia.org/wiki/B-tree) systems that span much
larger physical areas and thus can contain much more data than would be
practical with traditional metal interconnects.

# Host and Coprocessor Interaction
 [Database on a Chip](https://github.com/philiprbrenan/btreeAsm) is envisaged as an accelerating [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) driven by commands from a
**host processor**, which writes these commands into [registers](https://en.wikipedia.org/wiki/Processor_register) in [memory](https://en.wikipedia.org/wiki/Computer_memory) shared between the processors.

## Possible Commands

```
clear
delete <key>
put <key> <data>

find <key>
first
last
next <key>
prev <key>
size
```

The **find** operation requires significantly less [Silicon](https://en.wikipedia.org/wiki/Silicon) than either **put**
or **delete** and is expected to be executed far more frequently and can be
executed in parallel: consequently each **btree** should be equipped with
multiple **find** sub-processors, each sub-processor connected via its own port
to the host processor.

A **port** is a set of [registers](https://en.wikipedia.org/wiki/Processor_register) in [memory](https://en.wikipedia.org/wiki/Computer_memory) shared between the host processor
and the [B-Tree](https://en.wikipedia.org/wiki/B-tree) [coprocessor](https://en.wikipedia.org/wiki/Coprocessor). The host signals that the data in the port is ready
by performing a **doorbell write** to a [register](https://en.wikipedia.org/wiki/Processor_register) in that port. A doorbell [write](https://en.wikipedia.org/wiki/Write_(system_call)) forms the address of the [register](https://en.wikipedia.org/wiki/Processor_register) but does not actually [write](https://en.wikipedia.org/wiki/Write_(system_call)) data. The address
formation alone signals the [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) to start processing the data in the
port.

Conversely, as the **clear**, **delete** and **put** operations can potentially
modify the [B-Tree](https://en.wikipedia.org/wiki/B-tree), these operations can only be performed **one at a time** per [B-Tree](https://en.wikipedia.org/wiki/B-tree). Consequently there should be only one processor for each operation
attached via a single port to each [B-Tree](https://en.wikipedia.org/wiki/B-tree). 
To enable parallel deletes and puts, multiple [B-Trees](https://en.wikipedia.org/wiki/B-tree) should be used to store
data rather than relying on a single central [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)). A **root** [B-Tree](https://en.wikipedia.org/wiki/B-tree) directs
incoming requests to the appropriate subtrees, distributing the data across
multiple [sub](https://perldoc.perl.org/perlsub.html) [B-Trees](https://en.wikipedia.org/wiki/B-tree). Each [sub](https://perldoc.perl.org/perlsub.html) [B-Tree](https://en.wikipedia.org/wiki/B-tree) can [process](https://en.wikipedia.org/wiki/Process_management_(computing)) one delete or put request in
parallel with the other [B-Trees](https://en.wikipedia.org/wiki/B-tree), allowing multiple operations to occur
simultaneously across the database.

To distribute keys across [sub](https://perldoc.perl.org/perlsub.html) [B-Trees](https://en.wikipedia.org/wiki/B-tree), the root [B-Tree](https://en.wikipedia.org/wiki/B-tree) should compute the [SHA-256](https://en.wikipedia.org/wiki/SHA-256) digest of incoming keys and use a preset set of bits from this digest to assign
keys uniformly across subtrees. If keys are wider than 256 bits, it might be
preferable to replace them with their [SHA-256](https://en.wikipedia.org/wiki/SHA-256) digests to avoid committing
excessive amounts of [Silicon](https://en.wikipedia.org/wiki/Silicon) area to processing wide keys.

The host processor is responsible for assigning work to the ports presented by
the [coprocessor](https://en.wikipedia.org/wiki/Coprocessor). Where possible, the host should schedule work on multiple [find](https://en.wikipedia.org/wiki/Find_(Unix)) ports to [maximize](https://en.wikipedia.org/wiki/Maximum_and_minimum) throughput by using parallel lookups.

## Command Execution

1. The **host processor** constructs a command and writes it into an
   appropriate input port on the [coprocessor](https://en.wikipedia.org/wiki/Coprocessor). 
2. The host performs a **doorbell write** to a memory-mapped [register](https://en.wikipedia.org/wiki/Processor_register) on the
   [coprocessor](https://en.wikipedia.org/wiki/Coprocessor), signaling that the port data is available.

3. The [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) reads and executes the request in the input port.

4. The [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) writes the execution results to the corresponding output
   port.

5. When the command execution is complete, the [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) performs its own
   **doorbell write** to a [register](https://en.wikipedia.org/wiki/Processor_register) in the shared [memory](https://en.wikipedia.org/wiki/Computer_memory) of the output port,
   signaling the host that results are ready.

The **input** and **output** ports are separate. It is the **host's
responsibility** to control the execution flow of commands sent to the [coprocessor](https://en.wikipedia.org/wiki/Coprocessor) and to retrieve the results provided.

# Status

- 2025-07-12 [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) implementation of the [B-Tree](https://en.wikipedia.org/wiki/B-tree) [algorithm](https://en.wikipedia.org/wiki/Algorithm) 
- 2025-08-15 [Verilog](https://en.wikipedia.org/wiki/Verilog) trace matches [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) trace

- 2025-08-16 [Verilog](https://en.wikipedia.org/wiki/Verilog) synthesized for [test](https://en.wikipedia.org/wiki/Software_testing) cases in Chip.java using yosys

- 2025-08-22 All generated [Verilog](https://en.wikipedia.org/wiki/Verilog) now uses non blocking assignment

- 2025-08-24 Place and route of synthesized [Verilog](https://en.wikipedia.org/wiki/Verilog) using [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) 
- 2025-08-25 Ran [Silicon Compiler](https://docs.siliconcompiler.com/en/latest/index.html) on [Amazon Web Services](http://aws.amazon.com) in a [Docker](https://en.wikipedia.org/wiki/Docker_(software)) container saved via an [IPv6 address](https://en.wikipedia.org/wiki/IPv6) on docker.io

- 2025-10-08 Synthesized Tree Network for inter-component connectivity

- 2025-10-10 Synthesized FindFirst, FindLast, FindNext, FindPrev

- 2025-10-11 Synthesized reverse [Iterator](https://en.wikipedia.org/wiki/Iterator) using a non [recursive](https://en.wikipedia.org/wiki/Recursion) [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) traversal

- 2025-10-21 Simplex [tree](https://en.wikipedia.org/wiki/Tree_(data_structure)) tested successfully, ready for connecting to [memory](https://en.wikipedia.org/wiki/Computer_memory) 