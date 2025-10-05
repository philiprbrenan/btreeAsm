//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
`default_nettype none
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

  assign stop = (main_stop != 0 ? 1 : 0);                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 10;
    memoryReset = 1; reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1     // Set registers to zero

    for(memoryStep = 0; memoryStep < 0; memoryStep = memoryStep + 1) begin     // Step through memory steeing one elemnt to zero on each clock to allow yosys to infer registers
      clock = 0; #1; clock = 1; #1;                                             // One memory reset per clock to allow yosys to infer memeory rather than registers
    end
    memoryReset = 0; #1                                                         // Memory now cleared

    for(step = 0; step < maxSteps && !stop; step = step + 1) begin              // Step through code

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_main_0000
      chipPrint();
    end
    if (!stop) $finish(1); else $finish(0);
  end
  // Process: main  process_main_0000
  (* nomem2reg *)
  reg [64-1:0] main_a_0[4];
  reg [8-1:0] main_i_1;
  integer main_pc;
  integer main_stop;
  integer main_returnCode;
  integer main_memory_index;
  reg[0-1:0] main_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      main_pc          <= 0;
      main_stop        <= 0;
      main_returnCode  <= 0;
      main_memory_index                <= 0;
      main_memory_value                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_a_0[main_memory_index]      <= 0;
        end
      end
      main_i_1         <= 0;
    end
    else if (memoryReset > 0) begin
    end
    else begin
      if (processCurrent == 0) begin
        case(main_pc)
          0: begin
            main_i_1         <= 0;
            main_pc          <= main_pc + 1;
            case (main_pc)
              0: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1578:<init>|  Chip.java:1577:Zero|  Chip.java:2967:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          1: begin
            main_a_0[main_i_1]               <= 1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              1: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1146:<init>|  Chip.java:1145:RegisterSet|  Chip.java:2967:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          2, 4, 6: begin
            main_i_1         <= main_i_1 + 1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              2: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1632:<init>|  Chip.java:1631:Inc|  Chip.java:2968:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
              4: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1632:<init>|  Chip.java:1631:Inc|  Chip.java:2969:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
              6: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1632:<init>|  Chip.java:1631:Inc|  Chip.java:2970:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          3: begin
            main_a_0[main_i_1]               <= 2;
            main_pc          <= main_pc + 1;
            case (main_pc)
              3: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1146:<init>|  Chip.java:1145:RegisterSet|  Chip.java:2968:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          5: begin
            main_a_0[main_i_1]               <= 4;
            main_pc          <= main_pc + 1;
            case (main_pc)
              5: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1146:<init>|  Chip.java:1145:RegisterSet|  Chip.java:2969:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          7: begin
            main_a_0[main_i_1]               <= 8;
            main_pc          <= main_pc + 1;
            case (main_pc)
              7: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0824:<init>|  Chip.java:1146:<init>|  Chip.java:1145:RegisterSet|  Chip.java:2970:test_register_set|  Chip.java:3009:newTests|  Chip.java:3014:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          default: main_stop <= 1;
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 8, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 0, main_a_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 1, main_a_0[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 2, main_a_0[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 3, main_a_0[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_i_1", main_i_1);
      $fclose(o);
    end
  endtask
endmodule
