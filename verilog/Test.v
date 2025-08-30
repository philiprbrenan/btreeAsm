//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Test;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = process_stop;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 10;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_process_0000
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: process  process_process_0000
  reg [8-1:0] process_a_0;
  reg [8-1:0] process_b_1;
  reg [8-1:0] process_d_2;
  reg [8-1:0] process_d_3;
  integer process_pc;
  integer process_stop;
  integer process_returnCode;
  integer process_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      process_pc <= 0;
      process_stop <= 0;
      process_returnCode <= 0;
      process_a_0 <= 0;
      process_b_1 <= 0;
      process_d_2 <= 0;
      process_d_3 <= 0;
    end
    else if (processCurrent == 0) begin
      case(process_pc)
        0: begin
          process_a_0 <= 1;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:2064:test_sum|  Chip.java:2090:oldTests|  Chip.java:2094:newTests|  Chip.java:2099:main|");
            $fclose(f);
          end
        end
        1: begin
          process_b_1 <= 2;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:2065:test_sum|  Chip.java:2090:oldTests|  Chip.java:2094:newTests|  Chip.java:2099:main|");
            $fclose(f);
          end
        end
        2: begin
          process_d_2 <= 3;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:2066:test_sum|  Chip.java:2090:oldTests|  Chip.java:2094:newTests|  Chip.java:2099:main|");
            $fclose(f);
          end
        end
        3: begin
          process_d_3 <= 4;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:2067:test_sum|  Chip.java:2090:oldTests|  Chip.java:2094:newTests|  Chip.java:2099:main|");
            $fclose(f);
          end
        end
        4: begin
          process_a_0 <= process_a_0 + process_b_1 + process_d_2 + process_d_3;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0883:<init>|  Chip.java:0882:Sum|  Chip.java:2069:test_sum|  Chip.java:2090:oldTests|  Chip.java:2094:newTests|  Chip.java:2099:main|");
            $fclose(f);
          end
        end
        default: process_stop <= 1;
      endcase
    end
  end
  task chipPrint;
    begin
      integer o;
      o = $fopen("verilog/trace_verilog.txt", "a");
      if (!o) o = $fopen("../verilog/trace_verilog.txt", "a");
      if (!o) $display("Cannot create trace folder: verilog/trace_verilog.txt");
      $fwrite(o, "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n", "Test", step, maxSteps, !stop);
      $fwrite(o, "  Processes:\n");

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "process", 5, process_pc, process_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_a_0", process_a_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_b_1", process_b_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_d_2", process_d_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_d_3", process_d_3);
      $fclose(o);
    end
  endtask
endmodule
