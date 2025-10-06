//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
`default_nettype none
module TreeNet;                                                                      // Test bench for database on a chip
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
    maxSteps = 100;
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
  reg [1-1:0] main_putMessage_0;
  (* nomem2reg *)
  reg [1-1:0] main_address_1[7];
  (* nomem2reg *)
  reg [1-1:0] main_messageUp_2[7];
  (* nomem2reg *)
  reg [1-1:0] main_messageUp_3[7];
  (* nomem2reg *)
  reg [1-1:0] main_messageUp_4[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageUpNumber_5[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageUpSource_6[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageUpTarget_7[7];
  (* nomem2reg *)
  reg [32-1:0] main_MessageUpText_8[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownNumber_9[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownSource_10[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownTarget_11[7];
  (* nomem2reg *)
  reg [32-1:0] main_MessageDownText_12[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownPendingNumber_13[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownPendingSource_14[7];
  (* nomem2reg *)
  reg [8-1:0] main_MessageDownPendingTarget_15[7];
  (* nomem2reg *)
  reg [32-1:0] main_MessageDownPendingText_16[7];
  reg [1-1:0] main_LeftRightPriority_17;
  reg [32-1:0] main_MessageNumber_18;
  reg [32-1:0] main_Step_19;
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
      main_putMessage_0                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_address_1[main_memory_index]                <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_messageUp_2[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_messageUp_3[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_messageUp_4[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageUpNumber_5[main_memory_index]        <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageUpSource_6[main_memory_index]        <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageUpTarget_7[main_memory_index]        <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageUpText_8[main_memory_index]          <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownNumber_9[main_memory_index]      <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownSource_10[main_memory_index]     <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownTarget_11[main_memory_index]     <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownText_12[main_memory_index]       <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownPendingNumber_13[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownPendingSource_14[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownPendingTarget_15[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index + 1) begin
          main_MessageDownPendingText_16[main_memory_index]                <= 0;
        end
      end
      main_LeftRightPriority_17        <= 0;
      main_MessageNumber_18            <= 0;
      main_Step_19     <= 0;
    end
    else if (memoryReset > 0) begin
    end
    else begin
      if (processCurrent == 0) begin
        case(main_pc)
          0: begin
            main_address_1[0]                <= 0;
            main_pc          <= main_pc + 1;
          end
          1: begin
            main_address_1[1]                <= 0;
            main_pc          <= main_pc + 1;
          end
          2: begin
            main_address_1[2]                <= 1;
            main_pc          <= main_pc + 1;
          end
          3: begin
            main_address_1[3]                <= 0;
            main_pc          <= main_pc + 1;
          end
          4: begin
            main_address_1[4]                <= 2;
            main_pc          <= main_pc + 1;
          end
          5: begin
            main_address_1[5]                <= 1;
            main_pc          <= main_pc + 1;
          end
          6: begin
            main_address_1[6]                <= 3;
            main_pc          <= main_pc + 1;
          end
          7: begin
            if (main_messageUp_2[5]) begin
              main_putMessage_0                <= 0;
            end
            else begin
              main_messageUp_2[5]              <= 1;
              main_MessageUpNumber_5[5]        <= main_MessageNumber_18;
              main_MessageUpSource_6[5]        <= 5;
              main_MessageUpTarget_7[5]        <= 3;
              main_MessageUpText_8[5]          <= 1111;
              main_MessageNumber_18            <= main_MessageNumber_18 + 1;
              main_putMessage_0                <= 1;
            end
            main_pc          <= main_pc + 1;
          end
          8: begin
            if (main_messageUp_2[6]) begin
              main_putMessage_0                <= 0;
            end
            else begin
              main_messageUp_2[6]              <= 1;
              main_MessageUpNumber_5[6]        <= main_MessageNumber_18;
              main_MessageUpSource_6[6]        <= 6;
              main_MessageUpTarget_7[6]        <= 4;
              main_MessageUpText_8[6]          <= 2222;
              main_MessageNumber_18            <= main_MessageNumber_18 + 1;
              main_putMessage_0                <= 1;
            end
            main_pc          <= main_pc + 1;
          end
          9, 10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 43, 45, 46, 47, 48, 49, 50, 51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 63, 64, 65, 66, 67, 68, 69, 70, 72, 73, 74, 75, 76, 77, 78, 79: begin
            main_pc          <= main_pc + 1;
          end
          17, 26, 35, 44, 53, 62, 71, 80: begin
            main_LeftRightPriority_17        <= main_LeftRightPriority_17 != 0 ? 0 : 1;
            main_pc          <= main_pc + 1;
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
      $fwrite(o, "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n", "TreeNet", step, maxSteps, !stop);
      $fwrite(o, "  Processes:\n");

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 81, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "main_putMessage_0", main_putMessage_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 0, main_address_1[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 1, main_address_1[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 2, main_address_1[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 3, main_address_1[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 4, main_address_1[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 5, main_address_1[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_address_1", 6, main_address_1[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 0, main_messageUp_2[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 1, main_messageUp_2[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 2, main_messageUp_2[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 3, main_messageUp_2[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 4, main_messageUp_2[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 5, main_messageUp_2[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_2", 6, main_messageUp_2[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 0, main_messageUp_3[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 1, main_messageUp_3[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 2, main_messageUp_3[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 3, main_messageUp_3[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 4, main_messageUp_3[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 5, main_messageUp_3[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_3", 6, main_messageUp_3[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 0, main_messageUp_4[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 1, main_messageUp_4[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 2, main_messageUp_4[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 3, main_messageUp_4[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 4, main_messageUp_4[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 5, main_messageUp_4[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_messageUp_4", 6, main_messageUp_4[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 0, main_MessageUpNumber_5[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 1, main_MessageUpNumber_5[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 2, main_MessageUpNumber_5[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 3, main_MessageUpNumber_5[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 4, main_MessageUpNumber_5[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 5, main_MessageUpNumber_5[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpNumber_5", 6, main_MessageUpNumber_5[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 0, main_MessageUpSource_6[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 1, main_MessageUpSource_6[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 2, main_MessageUpSource_6[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 3, main_MessageUpSource_6[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 4, main_MessageUpSource_6[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 5, main_MessageUpSource_6[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpSource_6", 6, main_MessageUpSource_6[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 0, main_MessageUpTarget_7[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 1, main_MessageUpTarget_7[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 2, main_MessageUpTarget_7[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 3, main_MessageUpTarget_7[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 4, main_MessageUpTarget_7[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 5, main_MessageUpTarget_7[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpTarget_7", 6, main_MessageUpTarget_7[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 0, main_MessageUpText_8[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 1, main_MessageUpText_8[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 2, main_MessageUpText_8[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 3, main_MessageUpText_8[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 4, main_MessageUpText_8[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 5, main_MessageUpText_8[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageUpText_8", 6, main_MessageUpText_8[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 0, main_MessageDownNumber_9[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 1, main_MessageDownNumber_9[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 2, main_MessageDownNumber_9[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 3, main_MessageDownNumber_9[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 4, main_MessageDownNumber_9[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 5, main_MessageDownNumber_9[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownNumber_9", 6, main_MessageDownNumber_9[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 0, main_MessageDownSource_10[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 1, main_MessageDownSource_10[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 2, main_MessageDownSource_10[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 3, main_MessageDownSource_10[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 4, main_MessageDownSource_10[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 5, main_MessageDownSource_10[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownSource_10", 6, main_MessageDownSource_10[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 0, main_MessageDownTarget_11[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 1, main_MessageDownTarget_11[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 2, main_MessageDownTarget_11[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 3, main_MessageDownTarget_11[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 4, main_MessageDownTarget_11[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 5, main_MessageDownTarget_11[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownTarget_11", 6, main_MessageDownTarget_11[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 0, main_MessageDownText_12[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 1, main_MessageDownText_12[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 2, main_MessageDownText_12[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 3, main_MessageDownText_12[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 4, main_MessageDownText_12[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 5, main_MessageDownText_12[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownText_12", 6, main_MessageDownText_12[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 0, main_MessageDownPendingNumber_13[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 1, main_MessageDownPendingNumber_13[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 2, main_MessageDownPendingNumber_13[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 3, main_MessageDownPendingNumber_13[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 4, main_MessageDownPendingNumber_13[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 5, main_MessageDownPendingNumber_13[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingNumber_13", 6, main_MessageDownPendingNumber_13[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 0, main_MessageDownPendingSource_14[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 1, main_MessageDownPendingSource_14[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 2, main_MessageDownPendingSource_14[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 3, main_MessageDownPendingSource_14[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 4, main_MessageDownPendingSource_14[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 5, main_MessageDownPendingSource_14[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingSource_14", 6, main_MessageDownPendingSource_14[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 0, main_MessageDownPendingTarget_15[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 1, main_MessageDownPendingTarget_15[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 2, main_MessageDownPendingTarget_15[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 3, main_MessageDownPendingTarget_15[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 4, main_MessageDownPendingTarget_15[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 5, main_MessageDownPendingTarget_15[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingTarget_15", 6, main_MessageDownPendingTarget_15[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 0, main_MessageDownPendingText_16[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 1, main_MessageDownPendingText_16[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 2, main_MessageDownPendingText_16[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 3, main_MessageDownPendingText_16[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 4, main_MessageDownPendingText_16[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 5, main_MessageDownPendingText_16[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_MessageDownPendingText_16", 6, main_MessageDownPendingText_16[6]);
      $fwrite(o, "        %-50s = %1d\n",  "main_LeftRightPriority_17", main_LeftRightPriority_17);
      $fwrite(o, "        %-50s = %hx\n",  "main_MessageNumber_18", main_MessageNumber_18);
      $fwrite(o, "        %-50s = %hx\n",  "main_Step_19", main_Step_19);
      $fclose(o);
    end
  endtask
endmodule
