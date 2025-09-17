//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Test;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer               reset;                                                  // Reset chip to known state
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = (main_stop != 0 ? 1 : 0);                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 10;
    reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1                      // Reset to known state
    for(step = 0; step < maxSteps && !stop; step = step + 1) begin              // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_main_0000
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: main  process_main_0000
  (* nomem2reg *)
  reg [4-1:0] main_a_0[2];
  reg [8-1:0] main_b_1;
  (* nomem2reg *)
  reg [1-1:0] main_c_2[8];
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
        for(main_memory_index = 0; main_memory_index < 2; main_memory_index = main_memory_index + 1) begin
          main_a_0[main_memory_index]      <= 0;
        end
      end
      main_b_1         <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_c_2[main_memory_index]      <= 0;
        end
      end
    end
    else begin
      if (processCurrent == 0) begin
        case(main_pc)
          0: begin
            main_a_0[0]      <= 1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              0: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0683:<init>|  Chip.java:0990:<init>|  Chip.java:0989:RegisterSet|  Chip.java:2851:test_copyArrayToSingle|  Chip.java:2894:oldTests|  Chip.java:2898:newTests|  Chip.java:2904:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          1: begin
            main_a_0[1]      <= 2;
            main_pc          <= main_pc + 1;
            case (main_pc)
              1: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0683:<init>|  Chip.java:0990:<init>|  Chip.java:0989:RegisterSet|  Chip.java:2852:test_copyArrayToSingle|  Chip.java:2894:oldTests|  Chip.java:2898:newTests|  Chip.java:2904:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          2: begin
            main_b_1         <= {main_a_0[1], main_a_0[0]};
            main_pc          <= main_pc + 1;
            case (main_pc)
              2: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0683:<init>|  Chip.java:1181:<init>|  Chip.java:1180:registerCopySingleFromArray|  Chip.java:2853:test_copyArrayToSingle|  Chip.java:2894:oldTests|  Chip.java:2898:newTests|  Chip.java:2904:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          3: begin
            main_c_2[0]      <= main_b_1[0+:1];;
            main_c_2[1]      <= main_b_1[1+:1];;
            main_c_2[2]      <= main_b_1[2+:1];;
            main_c_2[3]      <= main_b_1[3+:1];;
            main_c_2[4]      <= main_b_1[4+:1];;
            main_c_2[5]      <= main_b_1[5+:1];;
            main_c_2[6]      <= main_b_1[6+:1];;
            main_c_2[7]      <= main_b_1[7+:1];;
            main_pc          <= main_pc + 1;
            case (main_pc)
              3: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0683:<init>|  Chip.java:1207:<init>|  Chip.java:1206:registerCopyArrayFromSingle|  Chip.java:2854:test_copyArrayToSingle|  Chip.java:2894:oldTests|  Chip.java:2898:newTests|  Chip.java:2904:main|");
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 4, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 0, main_a_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_a_0", 1, main_a_0[1]);
      $fwrite(o, "        %-50s = %1d\n",  "main_b_1", main_b_1);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 0, main_c_2[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 1, main_c_2[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 2, main_c_2[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 3, main_c_2[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 4, main_c_2[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 5, main_c_2[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 6, main_c_2[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_c_2", 7, main_c_2[7]);
      $fclose(o);
    end
  endtask
endmodule
