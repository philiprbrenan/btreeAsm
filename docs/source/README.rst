|Test|

Goal
====

Use `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
to generate
`synthesizable <https://en.wikipedia.org/wiki/Logic_synthesis>`__
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ to layout a
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ on the surface of a
specialized `Silicon <https://en.wikipedia.org/wiki/Silicon>`__
`chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ so that the
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
`find <https://en.wikipedia.org/wiki/Find_(Unix)>`__, put and delete
operations can be performed more quickly and efficiently than
`software <https://en.wikipedia.org/wiki/Software>`__ running on a
generic processor . Such a chip could act as an accelerator for database
operations, a: `Database on a
Chip <https://github.com/philiprbrenan/btreeAsm>`__ .

.. image:: https://raw.githubusercontent.com/philiprbrenan/btreeAsm/refs/heads/main/images/Btree.png
   :alt: put

--------------

Chip
====

A `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ implements
an `algorithm <https://en.wikipedia.org/wiki/Algorithm>`__ in
`Silicon <https://en.wikipedia.org/wiki/Silicon>`__. A
`chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ design
exists in one of two states:

-  **Design**: The stage where the layout of the
   `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ is
   described in
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
-  **Execution**: When the
   `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ runs a
   fixed set of parallel
   `processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   to perform work. This work is simulated in
   `Verilog <https://en.wikipedia.org/wiki/Verilog>`__

Process
-------

Each
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
is assigned a unique identifier at design time. A
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
includes:

-  A `program <https://en.wikipedia.org/wiki/Computer_program>`__, which
   in turn is comprised of
   `instructions <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__.
-  An optional block of
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__
-  Optional local
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__

Access Rules
------------

The `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ ``always``
blocks enforce access rule requirements for
`processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__:

-  A
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   can **only write** to its own
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ and
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__.
-  A
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   can **read** its own
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__ and
   the `registers <https://en.wikipedia.org/wiki/Processor_register>`__
   of **any** other
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   .

Memory
------

The `memory <https://en.wikipedia.org/wiki/Computer_memory>`__
associated with a
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
is comprised of one-dimensional, indexable
`arrays <https://en.wikipedia.org/wiki/Dynamic_array>`__:

-  Memory size is determined at **runtime**.

-  Memory contents persist even when the
   `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ is not
   executing.

-  Memory is typically accessed over multiple
   `clock <https://en.wikipedia.org/wiki/Clock_generator>`__ cycles by
   issuing transactions that copy
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ elements
   to or from local
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__ for
   faster access.

-  Each `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ is
   owned by a single
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   .

BlackBox Synthesis
~~~~~~~~~~~~~~~~~~

Memory is an ideal candidate for geometrical layout given its regular
structure over a large number of elements. OpenRAM exploits this
structure to synthesize large memories that can then be imbedded as
black boxes in other designs which would otherwise overwhelem the place
and route phases of
`synthesis <https://en.wikipedia.org/wiki/Logic_synthesis>`__.

Registers
---------

Registers are local blocks of
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ that:

-  Have a fixed size determined at **compile time**.

-  Are accessible within a **single**
   `clock <https://en.wikipedia.org/wiki/Clock_generator>`__ **cycle**.

-  Lose their values if the
   `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ is
   powered off.

-  Start set to zero when the
   `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ is
   powered on

Single versus arrayed
~~~~~~~~~~~~~~~~~~~~~

Registers can contain just a single element in a
`register <https://en.wikipedia.org/wiki/Processor_register>`__ or a
predefined number of elements in an arrayed
`register <https://en.wikipedia.org/wiki/Processor_register>`__. Single
and arrayed
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ can be
copied into other single and arrayed
`registers <https://en.wikipedia.org/wiki/Processor_register>`__, with
either the whole
`register <https://en.wikipedia.org/wiki/Processor_register>`__ being
copied or with specific elements being selected from arrayed
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ with
either integer and/or single
`register <https://en.wikipedia.org/wiki/Processor_register>`__ indices.

Unknown values
--------------

When the chip is powered on, the values of bits in
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ are unknown.
I considered these possibilities:

1. Track the unknown status of each
   `bit <https://en.wikipedia.org/wiki/Bit>`__ in
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__.
2. Track the unknown status of each
   `word <https://en.wikipedia.org/wiki/Doc_(computing)>`__ in
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__.
3. Set `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ to a
   known value so that there are no unknown values after power on.

I chose to set
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ to known
values on power on because:

-  Verilog does track whether each
   `bit <https://en.wikipedia.org/wiki/Bit>`__ in
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ has a
   known value or not so there seems to be no point in reinventing the
   wheel by duplicating work already being done by Verilog.

-  The Java execution `speed <https://en.wikipedia.org/wiki/Speed>`__
   which is already slow enough enough would be slowed even further, yet
   any bugs it might catch would be caught a few seconds later by the
   Verilog simulation so the extra execution time buys very little
   compared to its cost.

-  Memory is copied into
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__ and
   so `registers <https://en.wikipedia.org/wiki/Processor_register>`__
   would also have to track whether their values were known or not
   leading to more complexity in the Java
   `code <https://en.wikipedia.org/wiki/Computer_program>`__, which
   should, instead, be focussed on the
   `algorithms <https://en.wikipedia.org/wiki/Algorithm>`__ used rather
   then their bitwise implementation.

Program Execution
-----------------

Each
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
executes a single
`program <https://en.wikipedia.org/wiki/Computer_program>`__ composed of
sequential
`instructions <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__.
A `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ may
contain multiple
`processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__.
Processes can be driven by transactions, which are parameter lists of
`registers <https://en.wikipedia.org/wiki/Processor_register>`__
provided by the calling
`processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__.
Processes are execute in a fixed, round-robin order set at design time,
allowing the behavior of the
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__
`code <https://en.wikipedia.org/wiki/Computer_program>`__ to be
validated by comparing the time evolution of memories and
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ under
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ with the trace of
the same under
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__.

Transactions
------------

A `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__
enables one
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
to request work from another
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
using a parameter `list <https://en.wikipedia.org/wiki/Linked_list>`__
comprised of
`registers <https://en.wikipedia.org/wiki/Processor_register>`__.
Processes handle transactions in round-robin polling mode, fixed at
design time. Each
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__:

1. Polls its inbound transactions for work.

2. When a
   `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__
   is found:

   -  Copies data from the transaction's parameter
      `registers <https://en.wikipedia.org/wiki/Processor_register>`__
      into its own
      `registers <https://en.wikipedia.org/wiki/Processor_register>`__.
   -  Processes the data by executing the indicated
      `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__.
   -  Writes the execution results into the transaction's output
      `registers <https://en.wikipedia.org/wiki/Processor_register>`__.
   -  Marks the
      `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__
      as complete so that its results can be used by the caller.

3. The requesting
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   waits for the completion of a
   `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__
   by spinning on an
   `instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__.
   The output remains in the
   `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__
   until they are overwritten by the next use of the same
   `transaction <https://en.wikipedia.org/wiki/Database_transaction>`__.

Deadlock Prevention
-------------------

To avoid deadlocks,
`processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
may only request work from
`processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
with lower
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
numbers.

Verilog Implementation
----------------------

The `chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ is
implemented in `Verilog <https://en.wikipedia.org/wiki/Verilog>`__. Each
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
is mapped to an ``always`` block that:

-  Executes
   `instructions <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
   comprising in-flight transactions or polls its non-in-flight
   transactions for work.

-  Is triggered by a common
   `clock <https://en.wikipedia.org/wiki/Clock_generator>`__ to ensure
   synchronous execution.

-  Shares a common
   `clock <https://en.wikipedia.org/wiki/Clock_generator>`__ cycle count
   (step number) across all
   `processes <https://en.wikipedia.org/wiki/Process_management_(computing)>`__,
   though each
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   has its own
   `program <https://en.wikipedia.org/wiki/Computer_program>`__ counter.

Programs are implemented using a ``case`` statement where:

-  Each
   `instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
   corresponds to a case branch.

-  Each
   `process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
   has a `program <https://en.wikipedia.org/wiki/Computer_program>`__
   counter that selects the next
   `instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
   to be executed.

This is **not** a general-purpose processor. It is a specialized
`chip <https://en.wikipedia.org/wiki/Integrated_circuit>`__ with
**statically optimized** execution paths for minimal time and power
consumption.

Java and Verilog Simulation
---------------------------

Writing `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ directly is
time-consuming and error-prone. To mitigate this problem:

-  The `algorithm <https://en.wikipedia.org/wiki/Algorithm>`__ is first
   written and debugged in
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   using familiar programming paradigms and tools.

-  The
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   is then **almost-automatically translated** into
   `Verilog <https://en.wikipedia.org/wiki/Verilog>`__.

-  The
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   is executed to produce a trace showing how the
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ and
   `registers <https://en.wikipedia.org/wiki/Processor_register>`__ of
   the chip should evolve in time.

-  The `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ is executed
   and checked to confirm that it produces the same
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ and
   `register <https://en.wikipedia.org/wiki/Processor_register>`__ trace
   as the
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   does

This approach produces
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ **much more
efficiently** and **reliably** than writing it by hand. For example, I
was able to implement a non-recursive reverse
`Iterator <https://en.wikipedia.org/wiki/Iterator>`__ over the
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__ entirely
in `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__,
without writing any `Verilog <https://en.wikipedia.org/wiki/Verilog>`__
by hand at all, and the generated
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ synthesized, placed,
and routed correctly on the first attempt.

However, similar to the MIT "Midnight Rewiring Society," if extra
`instructions <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
are needed, perhaps to increase parallelism, it is relatively easy to
add a `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ implementation
of an
`instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
sequence written in
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__,
which can then be reused from
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__ as
needed.

As a consequence of the matching of the
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__ and
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ traces, no
additional tests are required within the
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ itself; it is
sufficient to
`verify <https://en.wikipedia.org/wiki/Software_verification_and_validation>`__
that the `Verilog <https://en.wikipedia.org/wiki/Verilog>`__
implementation updates the
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ and
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ on the
chip in lockstep with the
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
version to confirm that the
`Verilog <https://en.wikipedia.org/wiki/Verilog>`__ version accurately
implements the
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
version.

If the `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ trace and the
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
trace do not match, the
`instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
that is causing the mismatch can be located by setting "processTrace" to
**true** for the
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
under development to get a traceback identifying the
`instruction <https://en.wikipedia.org/wiki/Instruction_set_architecture>`__
that is executing differently in the
`Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
version versus the `Verilog <https://en.wikipedia.org/wiki/Verilog>`__
version.

--------------

Double Btree Structure
======================

Each `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ has its own
dedicated `memory <https://en.wikipedia.org/wiki/Computer_memory>`__,
which can be of any reasonable size supported by the chip fabrication
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__.
However, as chip size increases, the likelihood of manufacturing defects
grows and access to individual
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ elements
becomes slower.

To address these issues, we employ a double
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ architecture. Each
stuck of the double `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ is
implemented as a separate chip containing a single
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ .
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ are naturally suited
to representing a stuck, as shown in **Dt.java**. The individual
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ communicate through
the star-topology
`network <https://en.wikipedia.org/wiki/Computer_network>`__ described
in **TreeNet.java**.

Constructing a double `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
from multiple single `B-Trees <https://en.wikipedia.org/wiki/B-tree>`__
reduces manufacturing costs by improving yield (since smaller
`chips <https://en.wikipedia.org/wiki/Integrated_circuit>`__ are less
prone to defects) and increases
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ access
`speed <https://en.wikipedia.org/wiki/Speed>`__ through parallelism, as
each single `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ controls
its own independent block of
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__. These
advantages come at the expense of logarithmic
`network <https://en.wikipedia.org/wiki/Computer_network>`__
communication overhead between the single
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ that together form
the double `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ .

--------------

Stuck Data Structure
====================

A **Stuck** serves as the **fundamental node representation** inside a
**btree**. Instead of using pointers to dynamically allocated elements,
each node of the `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
contains a **Stuck** that stores its **keys and associated values (or
child pointers)** in a fixed-size, contiguous block.

A **Stuck** is a **fixed-size stack-like array** designed to hold
**key-data pairs** in **sorted order by key**.

.. image:: https://raw.githubusercontent.com/philiprbrenan/btreeAsm/refs/heads/main/images/Stuck.png
   :alt: Stuck Diagram

A **Stuck** combines the **boundedness of a stack**, the **ordered
nature of a sorted array**, and the **explicit size field** to manage
contiguous usage efficiently.

--------------

Storage Layout
--------------

-  Implemented as a **contiguous**
   `array <https://en.wikipedia.org/wiki/Dynamic_array>`__ **of slots**.
-  Each slot contains:

   -  **Key field** (``K bits``)
   -  **Data field** (``D bits``)

-  The structure has a **fixed capacity** ``N``.

--------------

Active Region
-------------

-  The Stuck maintains a **size field** that records how many slots are
   currently in use.
-  **Active elements** occupy indices ``0`` through ``size-1``, starting
   from the **bottom**.
-  All higher indices are **unused (empty)**.

--------------

Ordering
--------

-  Keys in the active region are maintained in **sorted order**.
-  Insertions require shifting elements upward to preserve ordering.
-  Removals require shifting elements downwards to preserve ordering.

--------------

Operations
----------

-  **Insert(Index, Key, Data)** Insert a new key/data pair into the
   correct position. May require shifting. If the structure is full,
   insertion fails.

-  **Remove(Index)** Remove a key/data pair at the specified index,
   shifting elements down to maintain contiguity.

-  **Search(Key)** Perform linear or binary search over the active
   region.

-  **Size()** Return the number of active elements.

--------------

Properties
----------

-  Fixed capacity (like an array).
-  Maintains **keys in sorted order**.
-  **Active elements are contiguous** from the bottom up.
-  **Fast access** to elements by index.

--------------

Role in a B-tree
----------------

1. **Key Storage**

   -  Each `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ node
      maintains its set of keys inside a Stuck.
   -  The Stuck keeps keys **sorted**, which is crucial for search,
      insert, and delete operations in a
      `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ .

2. **Efficient Search**

   -  Because keys in a Stuck are ordered, **binary search** can be
      applied within the node.
   -  This reduces search cost inside a node from linear to logarithmic.

3. **Insert & Split Support**

   -  When inserting a key into a node, the Stuck shifts entries to
      maintain sorted order.
   -  If the Stuck is full, the node **splits**: half the keys are
      promoted or moved to a new node, consistent with
      `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ balancing rules.

4. **Delete & Merge Support**

   -  On deletion, the Stuck shifts entries downward to close gaps.
   -  If underflow occurs, merging or borrowing between nodes uses Stuck
      operations to re-balance.

5. **Child Pointers**

   -  In internal nodes, each Stuck entry may also hold a
      **pointer/reference to a child Stuck** (i.e., another node).
   -  Thus, the Stuck serves as both a **container of keys** and a
      **directory of child subtrees**.

Why a Stuck is a Good Fit
-------------------------

-  **Contiguous**
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__ **layout**
   : cache-friendly.
-  **Fixed capacity** : simplifies
   `memory <https://en.wikipedia.org/wiki/Computer_memory>`__
   management.
-  **Size field** : explicitly tracks how full the node is.
-  **Ordered** : maintains the keys in order so they can be searched
   quickly

--------------

Silicon Compiler
================

The `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ produced by
**Btree.java** is transformed into a chip
`mask <https://en.wikipedia.org/wiki/Integrated_circuit_layout>`__ using
`Silicon
Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__ .

Run Silicon Compiler
--------------------

Run `Silicon
Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__ on
**Btree** on any machine with **docker** and **git** installed.

::

   [Git](https://en.wikipedia.org/wiki/Git) clone git@github.com:philiprbrenan/btreeAsm.git  # Download repo
   (cd btreeAsm; [Bash](https://en.wikipedia.org/wiki/Bash_(Unix_shell)) Btree.sh)                         # Run repo in a [Docker](https://en.wikipedia.org/wiki/Docker_(software)) container

Create Silicon Compiler Docker Container
----------------------------------------

The following notes explain how to create the
`Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__ container
referenced above to run `Silicon
Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__ .

OpenROAD
~~~~~~~~

Prebuilt `Open
Road <https://openroad-flow-scripts.readthedocs.io/en/latest/mainREADME.html>`__
binaries are available for
`Ubuntu <https://ubuntu.com/download/desktop>`__ 22:

::

   [Docker](https://en.wikipedia.org/wiki/Docker_(software)) run -it --rm ubuntu:22.04 /bin/bash

Install the following **deb**
`file <https://en.wikipedia.org/wiki/Computer_file>`__:

::

   wget https://github.com/Precision-Innovations/OpenROAD/releases/download/2024-12-14/openroad_2.0-17598-ga008522d8_amd64-ubuntu-22.04.deb
   [<b>sudo apt-get</b>](https://en.wikipedia.org/wiki/APT_(software)) [install](https://en.wikipedia.org/wiki/Installation_(computer_programs)) ...deb
   openroad -V

.. _silicon-compiler-1:

Silicon compiler
~~~~~~~~~~~~~~~~

Create a `Python <https://www.python.org/>`__ virtual environment,
activate it,
`install <https://en.wikipedia.org/wiki/Installation_(computer_programs)>`__
`Silicon
Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__ and
confirm the installation:

::

   sudo [<b>sudo apt-get</b>](https://en.wikipedia.org/wiki/APT_(software)) update
   sudo [<b>sudo apt-get</b>](https://en.wikipedia.org/wiki/APT_(software)) [install](https://en.wikipedia.org/wiki/Installation_(computer_programs)) python3-dev python3-pip python3-venv
   python3 -m venv  ./sc
   source ./sc/bin/activate
   pip [install](https://en.wikipedia.org/wiki/Installation_(computer_programs)) --upgrade pip siliconcompiler
   pip show siliconcompiler
   python3 -c "import siliconcompiler;print(siliconcompiler.__version__)"

Yosys
~~~~~

Download **yosys** and confirm correct installation:

::

   wget https://github.com/YosysHQ/oss-cad-suite-build/releases/download/2025-08-24/oss-cad-suite-linux-x64-20250824.tgz
   [Tar](https://en.wikipedia.org/wiki/Tar_(computing)) -xvf
   export PATH="$PATH:/root/oss-cad-suite/bin/"
   yosys -V

Klayout
~~~~~~~

Install **Klayout** and confirm correct installation:

::

   wget https://www.klayout.org/downloads/master/ubuntu22/klayout_master-1_amd64.deb
   klayout -v

Upload the container to Github containers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To save the configured container to
`GitHub <https://github.com/philiprbrenan>`__:

.. _1-log-in-to-ghcr:

1. Log in to GHCR
^^^^^^^^^^^^^^^^^

::

   echo <.githubToken.xxx> | [Docker](https://en.wikipedia.org/wiki/Docker_(software)) [login](https://en.wikipedia.org/wiki/Login) ghcr.io -u philiprbrenan --password-stdin

.. _2-create-an-image:

2. Create an image
^^^^^^^^^^^^^^^^^^

Make an image from the
`Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__ container:

::

   [Docker](https://en.wikipedia.org/wiki/Docker_(software)) commit <container> btreeasm:v1

.. _3-tag-the-image:

3. Tag the image
^^^^^^^^^^^^^^^^

Give the `Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__
image a name that `GitHub <https://github.com/philiprbrenan>`__ can
recognize:

::

   [Docker](https://en.wikipedia.org/wiki/Docker_(software)) tag btreeasm:v1 ghcr.io/philiprbrenan/btreeasm:latest

.. _4-push-the-image:

4. Push the image
^^^^^^^^^^^^^^^^^

Push the `Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__
image to `GitHub <https://github.com/philiprbrenan>`__:

::

   [Docker](https://en.wikipedia.org/wiki/Docker_(software)) push ghcr.io/philiprbrenan/btreeasm:latest

.. _5-optional-make-the-package-public:

5. (Optional) Make the package public
-------------------------------------

Make the `Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__
image public:

::

   https://github.com/users/philiprbrenan/packages/container/package/btreeAsm

Then: Package Settings -> Change visibility -> Public

Tree Network
============

**TreeNet.java** contains details of a
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
`network <https://en.wikipedia.org/wiki/Computer_network>`__ designed to
connect a plurality of single
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ into a double
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ . The
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
`network <https://en.wikipedia.org/wiki/Computer_network>`__ operates in
logarithmic time with respect to the number of leaves connected
together.

Using a `tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
`network <https://en.wikipedia.org/wiki/Computer_network>`__ allows
commands, data, and results to be passed between the leaves of the
`network <https://en.wikipedia.org/wiki/Computer_network>`__ that
defines the double `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__.
Each leaf is either a processor requesting actions on the
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ or a single
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ itself responding to
these requests. A message sent across the
`network <https://en.wikipedia.org/wiki/Computer_network>`__ from a
processor to a single `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
consists of a command and data, much like entering a command on the
terminal command line. The responses from the single
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ leaves are then
returned to the processors via the
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
`network <https://en.wikipedia.org/wiki/Computer_network>`__. Thus, a
**find** processor might ask for the data associated with a specified
key if it is present in the single
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ targeted by the
message.

This arrangement lets us get more work out of
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__, which
currently typically only responds to a small number of commands such as
`read <https://en.wikipedia.org/wiki/Reading_(computer)>`__,
`write <https://en.wikipedia.org/wiki/Write_(system_call)>`__, or clear.
It also allows for a great deal more parallelism than conventional
monolithic `memory <https://en.wikipedia.org/wiki/Computer_memory>`__,
because `memory <https://en.wikipedia.org/wiki/Computer_memory>`__
requests can run in parallel and are only occasionally blocked when
collisions between upward-traveling messages need to be resolved.
Conventional memories can usually only
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
one or two requests at a time due to the the need to unambiguously form
the address of the location to be
`read <https://en.wikipedia.org/wiki/Reading_(computer)>`__ or written.

Types of Tree Network
---------------------

Manufacturing `tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__ networks
-------------------------------------------------------------------------------------

Using a `tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
`network <https://en.wikipedia.org/wiki/Computer_network>`__
architecture allows individual
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ units to be
manufactured separately and later arranged in two- or three-dimensional
`arrays <https://en.wikipedia.org/wiki/Dynamic_array>`__, where they can
be interconnected to form double
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ of any size.

Tree networks also provide natural decoupling between components,
simplifying design, scaling, and fault isolation.

Moreover, sub-networks of a
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__ structure
can be linked over large distances using photonic interconnects,
enabling double `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
systems that span much larger physical areas and thus can contain much
more data than would be practical with traditional metal interconnects.

Host and Coprocessor Interaction
================================

`Database on a Chip <https://github.com/philiprbrenan/btreeAsm>`__ is
envisaged as an accelerating
`coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__ driven by
commands from a **host processor**, which writes these commands into
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ in
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ shared
between the processors.

Possible Commands
-----------------

::

   clear
   delete <key>
   put <key> <data>

   [find](https://en.wikipedia.org/wiki/Find_(Unix)) <key>
   first
   last
   next <key>
   prev <key>
   size

The **find** operation requires significantly less
`Silicon <https://en.wikipedia.org/wiki/Silicon>`__ than either **put**
or **delete** and is expected to be executed far more frequently and can
be executed in parallel: consequently each **btree** should be equipped
with multiple **find** sub-processors, each sub-processor connected via
its own port to the host processor.

A **port** is a set of
`registers <https://en.wikipedia.org/wiki/Processor_register>`__ in
`memory <https://en.wikipedia.org/wiki/Computer_memory>`__ shared
between the host processor and the
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
`coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__. The host
signals that the data in the port is ready by performing a **doorbell
write** to a
`register <https://en.wikipedia.org/wiki/Processor_register>`__ in that
port. A doorbell
`write <https://en.wikipedia.org/wiki/Write_(system_call)>`__ forms the
address of the
`register <https://en.wikipedia.org/wiki/Processor_register>`__ but does
not actually
`write <https://en.wikipedia.org/wiki/Write_(system_call)>`__ data. The
address formation alone signals the
`coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__ to start
processing the data in the port.

Conversely, as the **clear**, **delete** and **put** operations can
potentially modify the
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__, these operations can
only be performed **one at a time** per
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__. Consequently there
should be only one processor for each operation attached via a single
port to each `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__. To
enable parallel deletes and puts, multiple
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__ should be used to
store data rather than relying on a single central
`tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__. A
**root** `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ directs
incoming requests to the appropriate subtrees, distributing the data
across multiple `sub <https://perldoc.perl.org/perlsub.html>`__
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__. Each
`sub <https://perldoc.perl.org/perlsub.html>`__
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ can
`process <https://en.wikipedia.org/wiki/Process_management_(computing)>`__
one delete or put request in parallel with the other
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__, allowing multiple
operations to occur simultaneously across the database.

To distribute keys across
`sub <https://perldoc.perl.org/perlsub.html>`__
`B-Trees <https://en.wikipedia.org/wiki/B-tree>`__, the root
`B-Tree <https://en.wikipedia.org/wiki/B-tree>`__ should compute the
`SHA-256 <https://en.wikipedia.org/wiki/SHA-256>`__ digest of incoming
keys and use a preset set of bits from this digest to assign keys
uniformly across subtrees. If keys are wider than 256 bits, it might be
preferable to replace them with their
`SHA-256 <https://en.wikipedia.org/wiki/SHA-256>`__ digests to avoid
committing excessive amounts of
`Silicon <https://en.wikipedia.org/wiki/Silicon>`__ area to processing
wide keys.

The host processor is responsible for assigning work to the ports
presented by the
`coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__. Where
possible, the host should schedule work on multiple
`find <https://en.wikipedia.org/wiki/Find_(Unix)>`__ ports to
`maximize <https://en.wikipedia.org/wiki/Maximum_and_minimum>`__
throughput by using parallel lookups.

Command Execution
-----------------

1. The **host processor** constructs a command and writes it into an
   appropriate input port on the
   `coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__.

2. The host performs a **doorbell write** to a memory-mapped
   `register <https://en.wikipedia.org/wiki/Processor_register>`__ on
   the `coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__,
   signaling that the port data is available.

3. The `coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__ reads
   and executes the request in the input port.

4. The `coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__
   writes the execution results to the corresponding output port.

5. When the command execution is complete, the
   `coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__ performs
   its own **doorbell write** to a
   `register <https://en.wikipedia.org/wiki/Processor_register>`__ in
   the shared `memory <https://en.wikipedia.org/wiki/Computer_memory>`__
   of the output port, signaling the host that results are ready.

The **input** and **output** ports are separate. It is the **host's
responsibility** to control the execution flow of commands sent to the
`coprocessor <https://en.wikipedia.org/wiki/Coprocessor>`__ and to
retrieve the results provided.

Status
======

-  2025-07-12
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   implementation of the
   `B-Tree <https://en.wikipedia.org/wiki/B-tree>`__
   `algorithm <https://en.wikipedia.org/wiki/Algorithm>`__

-  2025-08-15 `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ trace
   matches
   `Java <https://en.wikipedia.org/wiki/Java_(programming_language)>`__
   trace

-  2025-08-16 `Verilog <https://en.wikipedia.org/wiki/Verilog>`__
   synthesized for
   `test <https://en.wikipedia.org/wiki/Software_testing>`__ cases in
   Chip.java using yosys

-  2025-08-22 All generated
   `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ now uses non
   blocking assignment

-  2025-08-24 Place and route of synthesized
   `Verilog <https://en.wikipedia.org/wiki/Verilog>`__ using `Silicon
   Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__

-  2025-08-25 Ran `Silicon
   Compiler <https://docs.siliconcompiler.com/en/latest/index.html>`__
   on `Amazon Web Services <http://aws.amazon.com>`__ in a
   `Docker <https://en.wikipedia.org/wiki/Docker_(software)>`__
   container saved via an `IPv6
   address <https://en.wikipedia.org/wiki/IPv6>`__ on docker.io

-  2025-10-08 Synthesized Tree Network for inter-component connectivity

-  2025-10-10 Synthesized FindFirst, FindLast, FindNext, FindPrev

-  2025-10-11 Synthesized reverse
   `Iterator <https://en.wikipedia.org/wiki/Iterator>`__ using a non
   `recursive <https://en.wikipedia.org/wiki/Recursion>`__
   `tree <https://en.wikipedia.org/wiki/Tree_(data_structure)>`__
   traversal

.. |Test| image:: https://github.com/philiprbrenan/btreeAsm/workflows/Test/badge.svg
   :target: https://github.com/philiprbrenan/btreeAsm
