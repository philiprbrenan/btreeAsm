//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Test;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer               reset;                                                  // Set chip registers to zero if true
  integer         memoryReset;                                                  // Set chip memory to zero if true
  integer                step;                                                  // Step of the code execution simulation
  integer          memoryStep;                                                  // Step of the memory clear simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = (Memory_stop != 0 ? 1 : 0) || (Requests_stop != 0 ? 1 : 0);                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 100;
    memoryReset = 1; reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1     // Set registers to zero

    for(memoryStep = 0; memoryStep < 8; memoryStep = memoryStep + 1) begin     // Step through memory steeing one elemnt to zero on each clock to allow yosys to infer registers
      clock = 0; #1; clock = 1; #1;                                             // One memory reset per clock to allow yosys to infer memeory rather than registers
    end
    memoryReset = 0; #1                                                         // Memory now cleared

    for(step = 0; step < maxSteps && !stop; step = step + 1) begin              // Step through code

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_Memory_0000
      processCurrent = 1; clock = 0; #1; clock = 1; #1; // process_Requests_0001
      chipPrint();
    end
    if (!stop) $finish(1); else $finish(0);
  end
  // Process: Memory  process_Memory_0000
  (* ram_style = "block" *)
  reg [4*2-1:0] Memory_memory[8];
  reg [8-1:0] Memory_Memory_1_result_0;
  integer Memory_1_requestedAt;
  integer Memory_1_finishedAt;
  integer Memory_Memory_1_returnCode;
  integer Memory_2_requestedAt;
  integer Memory_2_finishedAt;
  integer Memory_Memory_2_returnCode;
  integer Memory_pc;
  integer Memory_stop;
  integer Memory_returnCode;
  integer Memory_memory_index;
  reg[4-1:0] Memory_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      Memory_pc        <= 0;
      Memory_stop      <= 0;
      Memory_returnCode                <= 0;
      Memory_memory_index              <= 0;
      Memory_memory_value              <= 0;
      Memory_Memory_1_result_0         <= 0;
      Memory_1_finishedAt              <= -1;
      Memory_Memory_1_returnCode       <= 0;
      Memory_2_finishedAt              <= -1;
      Memory_Memory_2_returnCode       <= 0;
    end
    else if (memoryReset) begin
      if (memoryStep < 8) begin
        Memory_memory[memoryStep]        <= 0;
      end
    end
    else begin
      if (processCurrent == 0) begin
        case(Memory_pc)
          0: begin
            if ((Memory_1_requestedAt > Memory_1_finishedAt && Memory_1_requestedAt != step)) begin
              Memory_Memory_1_result_0         <= Memory_memory[Requests_Memory_1_index_4];
              Memory_1_finishedAt              <= step;
            end
            else if ((Memory_2_requestedAt > Memory_2_finishedAt && Memory_2_requestedAt != step)) begin

              Memory_memory[Requests_Memory_2_index_7]         <= Requests_Memory_2_value_8;
$display("AAA %d  %d  %d", Requests_Memory_2_value_8, Requests_Memory_2_index_7, Memory_memory[Requests_Memory_2_index_7]);
              Memory_2_finishedAt              <= step;
            end
            else begin
            end
          end
          default: Memory_stop <= 1;
        endcase
      end
    end
  end
  // Process: Requests  process_Requests_0001
  reg [8-1:0] Requests_n_0;
  (* nomem2reg *)
  reg [4-1:0] Requests_o_1[2];
  reg [8-1:0] Requests_rs_2;
  (* nomem2reg *)
  reg [4-1:0] Requests_ra_3[2];
  reg [3-1:0] Requests_Memory_1_index_4;
  reg [8-1:0] Requests_ss_5;
  (* nomem2reg *)
  reg [4-1:0] Requests_sa_6[2];
  reg [3-1:0] Requests_Memory_2_index_7;
  reg [8-1:0] Requests_Memory_2_value_8;
  integer Requests_pc;
  integer Requests_stop;
  integer Requests_returnCode;
  integer Requests_memory_index;
  reg[0-1:0] Requests_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      Requests_pc      <= 0;
      Requests_stop    <= 0;
      Requests_returnCode              <= 0;
      Requests_memory_index            <= 0;
      Requests_memory_value            <= 0;
      Requests_n_0     <= 0;
      begin
        for(Requests_memory_index = 0; Requests_memory_index < 2; Requests_memory_index = Requests_memory_index + 1) begin
          Requests_o_1[Requests_memory_index]              <= 0;
        end
      end
      Requests_rs_2    <= 0;
      begin
        for(Requests_memory_index = 0; Requests_memory_index < 2; Requests_memory_index = Requests_memory_index + 1) begin
          Requests_ra_3[Requests_memory_index]             <= 0;
        end
      end
      Requests_Memory_1_index_4        <= 0;
      Requests_ss_5    <= 0;
      begin
        for(Requests_memory_index = 0; Requests_memory_index < 2; Requests_memory_index = Requests_memory_index + 1) begin
          Requests_sa_6[Requests_memory_index]             <= 0;
        end
      end
      Requests_Memory_2_index_7        <= 0;
      Requests_Memory_2_value_8        <= 0;
      Memory_1_requestedAt             <= -1;
      Memory_2_requestedAt             <= -1;
    end
    else if (memoryReset) begin
    end
    else begin
      if (processCurrent == 1) begin
        case(Requests_pc)
          0: begin
            Requests_sa_6[0]                 <= 1;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              0: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          1: begin
            Requests_sa_6[1]                 <= 2;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              1: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          2: begin
            Requests_n_0     <= 0;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              2: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          3, 10, 17, 24, 31, 38, 45, 52: begin
            Requests_ss_5    <= {Requests_sa_6[1], Requests_sa_6[0]};
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              3: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              10: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              17: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              24: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              31: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              38: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              45: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              52: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1184:<init>|  Chip.java:1183:RegisterCopySingleFromArray|  Chip.java:2050:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          4, 11, 18, 25, 32, 39, 46, 53: begin
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              4: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              11: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              18: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              25: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              32: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              39: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              46: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              53: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:2052:<init>|  Chip.java:2051:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          5, 12, 19, 26, 33, 40, 47, 54: begin
            Requests_Memory_2_index_7        <= Requests_n_0;
            Requests_Memory_2_value_8        <= Requests_ss_5;
            Memory_2_requestedAt             <= step;
            Memory_2_finishedAt              <= -1;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              5: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              12: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              19: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              26: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              33: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              40: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              47: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              54: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1939:<init>|  Chip.java:1938:ExecuteTransaction|  Chip.java:2056:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          6, 13, 20, 27, 34, 41, 48, 55: begin
            if ((Memory_2_requestedAt < Memory_2_finishedAt)) begin
              Requests_pc      <= Requests_pc + 1;
            end
            case (Requests_pc)
              6: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              13: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              20: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              27: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              34: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              41: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              48: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              55: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1946:<init>|  Chip.java:1945:waitResultOfTransaction|  Chip.java:2057:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          7: begin
            Requests_sa_6[0]                 <= 3;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              7: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          8: begin
            Requests_sa_6[1]                 <= 4;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              8: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          9, 56: begin
            Requests_n_0     <= 1;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              9: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
              56: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2097:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          14: begin
            Requests_sa_6[0]                 <= 5;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              14: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          15: begin
            Requests_sa_6[1]                 <= 6;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              15: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          16: begin
            Requests_n_0     <= 2;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              16: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          21: begin
            Requests_sa_6[0]                 <= 7;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              21: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          22: begin
            Requests_sa_6[1]                 <= 8;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              22: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          23: begin
            Requests_n_0     <= 3;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              23: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          28: begin
            Requests_sa_6[0]                 <= 9;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              28: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          29: begin
            Requests_sa_6[1]                 <= 10;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              29: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          30: begin
            Requests_n_0     <= 4;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              30: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          35: begin
            Requests_sa_6[0]                 <= 11;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              35: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          36: begin
            Requests_sa_6[1]                 <= 12;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              36: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          37: begin
            Requests_n_0     <= 5;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              37: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          42: begin
            Requests_sa_6[0]                 <= 13;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              42: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          43: begin
            Requests_sa_6[1]                 <= 14;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              43: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          44: begin
            Requests_n_0     <= 6;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              44: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          49: begin
            Requests_sa_6[0]                 <= 15;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              49: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          50: begin
            Requests_sa_6[1]                 <= 16;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              50: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0983:<init>|  Chip.java:0982:RegisterSet|  Chip.java:2048:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          51: begin
            Requests_n_0     <= 7;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              51: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:0967:<init>|  Chip.java:0966:RegisterSet|  Chip.java:2049:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          57: begin
            Requests_Memory_1_index_4        <= Requests_n_0;
            Memory_1_requestedAt             <= step;
            Memory_1_finishedAt              <= -1;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              57: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1879:<init>|  Chip.java:1878:ExecuteTransaction|  Chip.java:2098:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          58: begin
            if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
              Requests_pc      <= Requests_pc + 1;
            end
            case (Requests_pc)
              58: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1886:<init>|  Chip.java:1885:waitResultOfTransaction|  Chip.java:2099:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          59: begin
            Requests_o_1[0]  <= Memory_Memory_1_result_0[0+:4];;
            Requests_o_1[1]  <= Memory_Memory_1_result_0[4+:4];;
            Requests_pc      <= Requests_pc + 1;
            case (Requests_pc)
              59: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0712:<init>|  Chip.java:1218:<init>|  Chip.java:1217:RegisterCopyArrayFromSingle|  Chip.java:2100:test_memory|  Chip.java:2760:newTests|  Chip.java:2765:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          default: Requests_stop <= 1;
        endcase
      end
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "Memory", 1, Memory_pc, Memory_returnCode);
      $fwrite(o, "      Memory: %b\n", Memory_memory[0]);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 4, 2);
      $fwrite(o, "        %2d", Memory_memory[0][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[0][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[1][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[1][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[2][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[2][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[3][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[3][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[4][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[4][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[5][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[5][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[6][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[6][4+:4]);
      $fwrite(o, ", %2d", Memory_memory[7][0+:4]);
      $fwrite(o, ", %2d", Memory_memory[7][4+:4]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "Memory_Memory_1_result_0", Memory_Memory_1_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "Memory_1", Memory_1_requestedAt, Memory_1_finishedAt, Memory_Memory_1_returnCode, (Memory_1_requestedAt > Memory_1_finishedAt && Memory_1_requestedAt != step), (Memory_1_requestedAt < Memory_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "Requests_Memory_1_index_4", Requests_Memory_1_index_4);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-46s = %1d\n",  "Memory_Memory_1_result_0", Memory_Memory_1_result_0);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "Memory_2", Memory_2_requestedAt, Memory_2_finishedAt, Memory_Memory_2_returnCode, (Memory_2_requestedAt > Memory_2_finishedAt && Memory_2_requestedAt != step), (Memory_2_requestedAt < Memory_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "Requests_Memory_2_index_7", Requests_Memory_2_index_7);
      $fwrite(o, "            %-46s = %1d\n",  "Requests_Memory_2_value_8", Requests_Memory_2_value_8);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "Requests", 60, Requests_pc, Requests_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "Requests_n_0", Requests_n_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_o_1", 0, Requests_o_1[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_o_1", 1, Requests_o_1[1]);
      $fwrite(o, "        %-50s = %1d\n",  "Requests_rs_2", Requests_rs_2);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_ra_3", 0, Requests_ra_3[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_ra_3", 1, Requests_ra_3[1]);
      $fwrite(o, "        %-50s = %1d\n",  "Requests_Memory_1_index_4", Requests_Memory_1_index_4);
      $fwrite(o, "        %-50s = %1d\n",  "Requests_ss_5", Requests_ss_5);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_sa_6", 0, Requests_sa_6[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "Requests_sa_6", 1, Requests_sa_6[1]);
      $fwrite(o, "        %-50s = %1d\n",  "Requests_Memory_2_index_7", Requests_Memory_2_index_7);
      $fwrite(o, "        %-50s = %1d\n",  "Requests_Memory_2_value_8", Requests_Memory_2_value_8);
      $fclose(o);
    end
  endtask
endmodule
