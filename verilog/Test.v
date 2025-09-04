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
  reg [8-1:0] process_b_1[4];
  reg [8-1:0] process_c_2;
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
      begin
        integer i;
        for(i = 0; i < 4; i = i + 1) begin
          process_b_1[i] <= 0;
        end
      end
      process_c_2 <= 0;
    end
    else if (processCurrent == 0) begin
      case(process_pc)
        0: begin
          process_a_0 <= 1;
          process_pc <= process_pc + 1;
          case (process_pc)
            0: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0766:<init>|  Chip.java:0765:RegisterSet|  Chip.java:2267:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        1: begin
          process_b_1[0] <= process_a_0;
          process_pc <= process_pc + 1;
          case (process_pc)
            1: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0835:<init>|  Chip.java:0834:Copy|  Chip.java:2267:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        2: begin
          process_a_0 <= 3;
          process_pc <= process_pc + 1;
          case (process_pc)
            2: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0766:<init>|  Chip.java:0765:RegisterSet|  Chip.java:2268:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        3: begin
          process_b_1[1] <= process_a_0;
          process_pc <= process_pc + 1;
          case (process_pc)
            3: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0835:<init>|  Chip.java:0834:Copy|  Chip.java:2268:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        4: begin
          process_a_0 <= 5;
          process_pc <= process_pc + 1;
          case (process_pc)
            4: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0766:<init>|  Chip.java:0765:RegisterSet|  Chip.java:2269:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        5: begin
          process_b_1[2] <= process_a_0;
          process_pc <= process_pc + 1;
          case (process_pc)
            5: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0835:<init>|  Chip.java:0834:Copy|  Chip.java:2269:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        6: begin
          process_a_0 <= 7;
          process_pc <= process_pc + 1;
          case (process_pc)
            6: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0766:<init>|  Chip.java:0765:RegisterSet|  Chip.java:2270:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        7: begin
          process_b_1[3] <= process_a_0;
          process_pc <= process_pc + 1;
          case (process_pc)
            7: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0835:<init>|  Chip.java:0834:Copy|  Chip.java:2270:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
        end
        8: begin
          process_c_2 <= process_b_1[2];
          process_pc <= process_pc + 1;
          case (process_pc)
            8: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0812:<init>|  Chip.java:0811:Copy|  Chip.java:2271:test_register_array|  Chip.java:2304:oldTests|  Chip.java:2308:newTests|  Chip.java:2314:main|");
                $fclose(f);
              end
            end
          endcase
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "process", 9, process_pc, process_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "process_a_0", process_a_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "process_b_1", 0, process_b_1[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "process_b_1", 1, process_b_1[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "process_b_1", 2, process_b_1[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "process_b_1", 3, process_b_1[3]);
      $fwrite(o, "        %-50s = %1d\n",  "process_c_2", process_c_2);
      $fclose(o);
    end
  endtask
endmodule
