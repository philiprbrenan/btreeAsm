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
    maxSteps = 100;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_process_0000
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: process  process_process_0000
  reg [8-1:0] process_a_0;
  reg [8-1:0] process_b_1;
  reg [8-1:0] process_c_2;
  reg [8-1:0] process_d_3;
  reg [1-1:0] process_ge0_4;
  reg [1-1:0] process_gt0_5;
  reg [1-1:0] process_le0_6;
  reg [1-1:0] process_lt0_7;
  reg [1-1:0] process_ne0_8;
  reg [1-1:0] process_eq0_9;
  reg [1-1:0] process_ge1_10;
  reg [1-1:0] process_gt1_11;
  reg [1-1:0] process_le1_12;
  reg [1-1:0] process_lt1_13;
  reg [1-1:0] process_ne1_14;
  reg [1-1:0] process_eq1_15;
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
      process_c_2 <= 0;
      process_d_3 <= 0;
      process_ge0_4 <= 0;
      process_gt0_5 <= 0;
      process_le0_6 <= 0;
      process_lt0_7 <= 0;
      process_ne0_8 <= 0;
      process_eq0_9 <= 0;
      process_ge1_10 <= 0;
      process_gt1_11 <= 0;
      process_le1_12 <= 0;
      process_lt1_13 <= 0;
      process_ne1_14 <= 0;
      process_eq1_15 <= 0;
    end
    else if (processCurrent == 0) begin
      case(process_pc)
        0: begin
          process_a_0 <= 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0792:<init>|  Chip.java:0791:Zero|  Chip.java:1946:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        1: begin
          process_b_1 <= 1;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0799:<init>|  Chip.java:0798:One|  Chip.java:1947:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        2: begin
          process_c_2 <= 2;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:1948:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        3: begin
          process_d_3 <= 3;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Chip.java:1949:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        4: begin
          process_ge0_4 <= process_a_0>=process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0777:<init>|  Chip.java:0777:Ge|  Chip.java:1951:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        5: begin
          process_gt0_5 <= process_a_0> process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0776:<init>|  Chip.java:0776:Gt|  Chip.java:1952:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        6: begin
          process_le0_6 <= process_b_1<=process_a_0 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0780:<init>|  Chip.java:0780:Le|  Chip.java:1953:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        7: begin
          process_lt0_7 <= process_b_1< process_a_0 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0781:<init>|  Chip.java:0781:Lt|  Chip.java:1954:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        8: begin
          process_ne0_8 <= process_a_0!=process_a_0 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0779:<init>|  Chip.java:0779:Ne|  Chip.java:1955:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        9: begin
          process_eq0_9 <= process_a_0==process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0778:<init>|  Chip.java:0778:Eq|  Chip.java:1956:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        10: begin
          process_ge1_10 <= process_b_1>=process_a_0 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0777:<init>|  Chip.java:0777:Ge|  Chip.java:1958:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        11: begin
          process_gt1_11 <= process_b_1> process_a_0 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0776:<init>|  Chip.java:0776:Gt|  Chip.java:1959:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        12: begin
          process_le1_12 <= process_a_0<=process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0780:<init>|  Chip.java:0780:Le|  Chip.java:1960:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        13: begin
          process_lt1_13 <= process_a_0< process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0781:<init>|  Chip.java:0781:Lt|  Chip.java:1961:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        14: begin
          process_ne1_14 <= process_a_0!=process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0779:<init>|  Chip.java:0779:Ne|  Chip.java:1962:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
            $fclose(f);
          end
        end
        15: begin
          process_eq1_15 <= process_b_1==process_b_1 ? 1 : 0;
          process_pc <= process_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0778:<init>|  Chip.java:0778:Eq|  Chip.java:1963:test_comparisons|  Chip.java:1994:oldTests|  Chip.java:1998:newTests|  Chip.java:2003:main|");
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "process", 16, process_pc, process_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_a_0", process_a_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_b_1", process_b_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_c_2", process_c_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_d_3", process_d_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_ge0_4", process_ge0_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_gt0_5", process_gt0_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_le0_6", process_le0_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_lt0_7", process_lt0_7);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_ne0_8", process_ne0_8);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_eq0_9", process_eq0_9);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_ge1_10", process_ge1_10);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_gt1_11", process_gt1_11);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_le1_12", process_le1_12);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_lt1_13", process_lt1_13);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_ne1_14", process_ne1_14);
      $fwrite(o, "        Register: %-32s = %1d\n",  "process_eq1_15", process_eq1_15);
      $fclose(o);
    end
  endtask
endmodule
