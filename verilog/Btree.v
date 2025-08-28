//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Btree;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop||alloc_stop;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 100;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_stuckIsLeaf_0000
      processCurrent = 1; clock = 0; #1; clock = 1; #1; // process_stuckIsFree_0001
      processCurrent = 2; clock = 0; #1; clock = 1; #1; // process_freeNext_0002
      processCurrent = 3; clock = 0; #1; clock = 1; #1; // process_stuckSize_0003
      processCurrent = 4; clock = 0; #1; clock = 1; #1; // process_stuckKeys_0004
      processCurrent = 5; clock = 0; #1; clock = 1; #1; // process_stuckData_0005
      processCurrent = 6; clock = 0; #1; clock = 1; #1; // process_alloc_0006
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0000
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[4*1];
  integer stuckIsLeaf_3_requestedAt;
  integer stuckIsLeaf_3_finishedAt;
  integer stuckIsLeaf_stuckIsLeaf_3_returnCode;
  integer stuckIsLeaf_pc;
  integer stuckIsLeaf_stop;
  integer stuckIsLeaf_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsLeaf_pc <= 0;
      stuckIsLeaf_stop <= 0;
      stuckIsLeaf_returnCode <= 0;
      stuckIsLeaf_3_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_3_returnCode <= 0;
      stuckIsLeaf_memory[0] <= 1;
      stuckIsLeaf_memory[1] <= 0;
      stuckIsLeaf_memory[2] <= 0;
      stuckIsLeaf_memory[3] <= 0;
    end
    else if (processCurrent == 0) begin
      case(stuckIsLeaf_pc)
        0: begin
          if ((stuckIsLeaf_3_requestedAt > stuckIsLeaf_3_finishedAt && stuckIsLeaf_3_requestedAt != step)) begin
            stuckIsLeaf_memory[alloc_stuckIsLeaf_3_index_5*1+0] <= alloc_stuckIsLeaf_3_value_6;
            stuckIsLeaf_3_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckIsLeaf_stop <= 1;
      endcase
    end
  end
  // Process: stuckIsFree  process_stuckIsFree_0001
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsFree_memory[4*1];
  integer stuckIsFree_4_requestedAt;
  integer stuckIsFree_4_finishedAt;
  integer stuckIsFree_stuckIsFree_4_returnCode;
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsFree_pc <= 0;
      stuckIsFree_stop <= 0;
      stuckIsFree_returnCode <= 0;
      stuckIsFree_4_finishedAt <= -1;
      stuckIsFree_stuckIsFree_4_returnCode <= 0;
      stuckIsFree_memory[0] <= 0;
      stuckIsFree_memory[1] <= 1;
      stuckIsFree_memory[2] <= 1;
      stuckIsFree_memory[3] <= 1;
    end
    else if (processCurrent == 1) begin
      case(stuckIsFree_pc)
        0: begin
          if ((stuckIsFree_4_requestedAt > stuckIsFree_4_finishedAt && stuckIsFree_4_requestedAt != step)) begin
            stuckIsFree_memory[alloc_stuckIsFree_4_index_7*1+0] <= alloc_stuckIsFree_4_value_8;
            stuckIsFree_4_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckIsFree_stop <= 1;
      endcase
    end
  end
  // Process: freeNext  process_freeNext_0002
  (* ram_style = "block" *)
  reg [3-1:0] freeNext_memory[4*1];
  reg [3-1:0] freeNext_freeNext_1_result_0;
  integer freeNext_1_requestedAt;
  integer freeNext_1_finishedAt;
  integer freeNext_freeNext_1_returnCode;
  integer freeNext_2_requestedAt;
  integer freeNext_2_finishedAt;
  integer freeNext_freeNext_2_returnCode;
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      freeNext_pc <= 0;
      freeNext_stop <= 0;
      freeNext_returnCode <= 0;
      freeNext_freeNext_1_result_0 <= 0;
      freeNext_1_finishedAt <= -1;
      freeNext_freeNext_1_returnCode <= 0;
      freeNext_2_finishedAt <= -1;
      freeNext_freeNext_2_returnCode <= 0;
      freeNext_memory[0] <= 1;
      freeNext_memory[1] <= 2;
      freeNext_memory[2] <= 3;
      freeNext_memory[3] <= 0;
    end
    else if (processCurrent == 2) begin
      case(freeNext_pc)
        0: begin
          if ((freeNext_1_requestedAt > freeNext_1_finishedAt && freeNext_1_requestedAt != step)) begin
            freeNext_freeNext_1_result_0 <= freeNext_memory[alloc_freeNext_1_index_2*1+0];
            freeNext_1_finishedAt <= step;
          end
          else if ((freeNext_2_requestedAt > freeNext_2_finishedAt && freeNext_2_requestedAt != step)) begin
            freeNext_memory[alloc_freeNext_2_index_3*1+0] <= alloc_freeNext_2_value_4;
            freeNext_2_finishedAt <= step;
          end
          else begin
          end
        end
        default: freeNext_stop <= 1;
      endcase
    end
  end
  // Process: stuckSize  process_stuckSize_0003
  (* ram_style = "block" *)
  reg [3-1:0] stuckSize_memory[4*1];
  integer stuckSize_pc;
  integer stuckSize_stop;
  integer stuckSize_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckSize_pc <= 0;
      stuckSize_stop <= 0;
      stuckSize_returnCode <= 0;
      stuckSize_memory[0] <= 0;
      stuckSize_memory[1] <= 0;
      stuckSize_memory[2] <= 0;
      stuckSize_memory[3] <= 0;
    end
    else if (processCurrent == 3) begin
      case(stuckSize_pc)
        0: begin
        end
        default: stuckSize_stop <= 1;
      endcase
    end
  end
  // Process: stuckKeys  process_stuckKeys_0004
  (* ram_style = "block" *)
  reg [8-1:0] stuckKeys_memory[4*4];
  integer stuckKeys_pc;
  integer stuckKeys_stop;
  integer stuckKeys_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckKeys_pc <= 0;
      stuckKeys_stop <= 0;
      stuckKeys_returnCode <= 0;
      stuckKeys_memory[0] <= 0;
      stuckKeys_memory[1] <= 0;
      stuckKeys_memory[2] <= 0;
      stuckKeys_memory[3] <= 0;
      stuckKeys_memory[4] <= 0;
      stuckKeys_memory[5] <= 0;
      stuckKeys_memory[6] <= 0;
      stuckKeys_memory[7] <= 0;
      stuckKeys_memory[8] <= 0;
      stuckKeys_memory[9] <= 0;
      stuckKeys_memory[10] <= 0;
      stuckKeys_memory[11] <= 0;
      stuckKeys_memory[12] <= 0;
      stuckKeys_memory[13] <= 0;
      stuckKeys_memory[14] <= 0;
      stuckKeys_memory[15] <= 0;
    end
    else if (processCurrent == 4) begin
      case(stuckKeys_pc)
        0: begin
        end
        default: stuckKeys_stop <= 1;
      endcase
    end
  end
  // Process: stuckData  process_stuckData_0005
  (* ram_style = "block" *)
  reg [8-1:0] stuckData_memory[4*4];
  integer stuckData_pc;
  integer stuckData_stop;
  integer stuckData_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckData_pc <= 0;
      stuckData_stop <= 0;
      stuckData_returnCode <= 0;
      stuckData_memory[0] <= 0;
      stuckData_memory[1] <= 0;
      stuckData_memory[2] <= 0;
      stuckData_memory[3] <= 0;
      stuckData_memory[4] <= 0;
      stuckData_memory[5] <= 0;
      stuckData_memory[6] <= 0;
      stuckData_memory[7] <= 0;
      stuckData_memory[8] <= 0;
      stuckData_memory[9] <= 0;
      stuckData_memory[10] <= 0;
      stuckData_memory[11] <= 0;
      stuckData_memory[12] <= 0;
      stuckData_memory[13] <= 0;
      stuckData_memory[14] <= 0;
      stuckData_memory[15] <= 0;
    end
    else if (processCurrent == 5) begin
      case(stuckData_pc)
        0: begin
        end
        default: stuckData_stop <= 1;
      endcase
    end
  end
  // Process: alloc  process_alloc_0006
  reg [3-1:0] alloc_index1_0;
  reg [3-1:0] alloc_index2_1;
  reg [2-1:0] alloc_freeNext_1_index_2;
  reg [2-1:0] alloc_freeNext_2_index_3;
  reg [3-1:0] alloc_freeNext_2_value_4;
  reg [2-1:0] alloc_stuckIsLeaf_3_index_5;
  reg [1-1:0] alloc_stuckIsLeaf_3_value_6;
  reg [2-1:0] alloc_stuckIsFree_4_index_7;
  reg [1-1:0] alloc_stuckIsFree_4_value_8;
  reg [3-1:0] alloc_root_9;
  reg [3-1:0] alloc_next_10;
  reg [1-1:0] alloc_isLeaf_11;
  reg [1-1:0] alloc_isFree_12;
  reg [3-1:0] alloc_root_13;
  reg [3-1:0] alloc_next_14;
  reg [1-1:0] alloc_isLeaf_15;
  reg [1-1:0] alloc_isFree_16;
  reg [3-1:0] alloc_next_17;
  reg [3-1:0] alloc_root_18;
  reg [1-1:0] alloc_isFree_19;
  reg [3-1:0] alloc_next_20;
  reg [3-1:0] alloc_root_21;
  reg [1-1:0] alloc_isFree_22;
  integer alloc_pc;
  integer alloc_stop;
  integer alloc_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      alloc_pc <= 0;
      alloc_stop <= 0;
      alloc_returnCode <= 0;
      alloc_index1_0 <= 0;
      alloc_index2_1 <= 0;
      alloc_freeNext_1_index_2 <= 0;
      alloc_freeNext_2_index_3 <= 0;
      alloc_freeNext_2_value_4 <= 0;
      alloc_stuckIsLeaf_3_index_5 <= 0;
      alloc_stuckIsLeaf_3_value_6 <= 0;
      alloc_stuckIsFree_4_index_7 <= 0;
      alloc_stuckIsFree_4_value_8 <= 0;
      alloc_root_9 <= 0;
      alloc_next_10 <= 0;
      alloc_isLeaf_11 <= 0;
      alloc_isFree_12 <= 0;
      alloc_root_13 <= 0;
      alloc_next_14 <= 0;
      alloc_isLeaf_15 <= 0;
      alloc_isFree_16 <= 0;
      alloc_next_17 <= 0;
      alloc_root_18 <= 0;
      alloc_isFree_19 <= 0;
      alloc_next_20 <= 0;
      alloc_root_21 <= 0;
      alloc_isFree_22 <= 0;
      stuckIsLeaf_3_requestedAt <= -1;
      stuckIsFree_4_requestedAt <= -1;
      freeNext_1_requestedAt <= -1;
      freeNext_2_requestedAt <= -1;
    end
    else if (processCurrent == 6) begin
      case(alloc_pc)
        0: begin
          alloc_root_9 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0792:<init>|  Chip.java:0791:Zero|  Btree.java:0076:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        1: begin
          alloc_freeNext_1_index_2 <= alloc_root_9;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1282:<init>|  Chip.java:1281:ExecuteTransaction|  Btree.java:0077:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        2: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0078:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        3: begin
          alloc_index1_0 <= freeNext_freeNext_1_result_0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:0079:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        4: begin
          if (alloc_index1_0 == 0) begin
            alloc_returnCode <= 20;
            alloc_stop <= 1;
          end
          alloc_isLeaf_11 <= 1;
          alloc_isFree_12 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0082:<init>|  Btree.java:0081:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        5: begin
          alloc_freeNext_1_index_2 <= alloc_index1_0;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_stuckIsLeaf_3_index_5 <= alloc_index1_0;
          alloc_stuckIsLeaf_3_value_6 <= alloc_isLeaf_11;
          stuckIsLeaf_3_requestedAt <= step;
          stuckIsLeaf_3_finishedAt <= -1;
          alloc_stuckIsFree_4_index_7 <= alloc_index1_0;
          alloc_stuckIsFree_4_value_8 <= alloc_isFree_12;
          stuckIsFree_4_requestedAt <= step;
          stuckIsFree_4_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0097:<init>|  Btree.java:0096:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        6: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0109:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        7: begin
          alloc_next_10 <= freeNext_freeNext_1_result_0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:0110:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        8: begin
          alloc_freeNext_2_index_3 <= alloc_root_9;
          alloc_freeNext_2_value_4 <= alloc_next_10;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0111:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        9: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0112:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        10: begin
          if ((stuckIsLeaf_3_requestedAt < stuckIsLeaf_3_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0113:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        11: begin
          if ((stuckIsFree_4_requestedAt < stuckIsFree_4_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0114:allocate|  Btree.java:4115:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        12: begin
          alloc_root_13 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0792:<init>|  Chip.java:0791:Zero|  Btree.java:0076:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        13: begin
          alloc_freeNext_1_index_2 <= alloc_root_13;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1282:<init>|  Chip.java:1281:ExecuteTransaction|  Btree.java:0077:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        14: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0078:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        15: begin
          alloc_index2_1 <= freeNext_freeNext_1_result_0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:0079:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        16: begin
          if (alloc_index2_1 == 0) begin
            alloc_returnCode <= 20;
            alloc_stop <= 1;
          end
          alloc_isLeaf_15 <= 0;
          alloc_isFree_16 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0082:<init>|  Btree.java:0081:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        17: begin
          alloc_freeNext_1_index_2 <= alloc_index2_1;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_stuckIsLeaf_3_index_5 <= alloc_index2_1;
          alloc_stuckIsLeaf_3_value_6 <= alloc_isLeaf_15;
          stuckIsLeaf_3_requestedAt <= step;
          stuckIsLeaf_3_finishedAt <= -1;
          alloc_stuckIsFree_4_index_7 <= alloc_index2_1;
          alloc_stuckIsFree_4_value_8 <= alloc_isFree_16;
          stuckIsFree_4_requestedAt <= step;
          stuckIsFree_4_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0097:<init>|  Btree.java:0096:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        18: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0109:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        19: begin
          alloc_next_14 <= freeNext_freeNext_1_result_0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:0110:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        20: begin
          alloc_freeNext_2_index_3 <= alloc_root_13;
          alloc_freeNext_2_value_4 <= alloc_next_14;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0111:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        21: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0112:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        22: begin
          if ((stuckIsLeaf_3_requestedAt < stuckIsLeaf_3_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0113:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        23: begin
          if ((stuckIsFree_4_requestedAt < stuckIsFree_4_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0114:allocate|  Btree.java:4116:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        24: begin
          alloc_root_18 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0792:<init>|  Chip.java:0791:Zero|  Btree.java:0127:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        25: begin
          alloc_freeNext_1_index_2 <= alloc_root_18;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1282:<init>|  Chip.java:1281:ExecuteTransaction|  Btree.java:0128:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        26: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0129:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        27: begin
          alloc_next_17 <= freeNext_freeNext_1_result_0;
          alloc_freeNext_2_index_3 <= alloc_root_18;
          alloc_freeNext_2_value_4 <= alloc_index1_0;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_isFree_19 <= 1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0132:<init>|  Btree.java:0131:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        28: begin
          alloc_stuckIsFree_4_index_7 <= alloc_index1_0;
          alloc_stuckIsFree_4_value_8 <= alloc_isFree_19;
          stuckIsFree_4_requestedAt <= step;
          stuckIsFree_4_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0144:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        29: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0145:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        30: begin
          alloc_freeNext_2_index_3 <= alloc_index1_0;
          alloc_freeNext_2_value_4 <= alloc_next_17;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0146:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        31: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0147:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        32: begin
          if ((stuckIsFree_4_requestedAt < stuckIsFree_4_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0148:free|  Btree.java:4141:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        33: begin
          alloc_root_21 <= 0;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0792:<init>|  Chip.java:0791:Zero|  Btree.java:0127:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        34: begin
          alloc_freeNext_1_index_2 <= alloc_root_21;
          freeNext_1_requestedAt <= step;
          freeNext_1_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1282:<init>|  Chip.java:1281:ExecuteTransaction|  Btree.java:0128:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        35: begin
          if ((freeNext_1_requestedAt < freeNext_1_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1289:<init>|  Chip.java:1288:waitResultOfTransaction|  Btree.java:0129:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        36: begin
          alloc_next_20 <= freeNext_freeNext_1_result_0;
          alloc_freeNext_2_index_3 <= alloc_root_21;
          alloc_freeNext_2_value_4 <= alloc_index2_1;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_isFree_22 <= 1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0132:<init>|  Btree.java:0131:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        37: begin
          alloc_stuckIsFree_4_index_7 <= alloc_index2_1;
          alloc_stuckIsFree_4_value_8 <= alloc_isFree_22;
          stuckIsFree_4_requestedAt <= step;
          stuckIsFree_4_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0144:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        38: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0145:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        39: begin
          alloc_freeNext_2_index_3 <= alloc_index2_1;
          alloc_freeNext_2_value_4 <= alloc_next_20;
          freeNext_2_requestedAt <= step;
          freeNext_2_finishedAt <= -1;
          alloc_pc <= alloc_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:1345:<init>|  Chip.java:1344:ExecuteTransaction|  Btree.java:0146:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        40: begin
          if ((freeNext_2_requestedAt < freeNext_2_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0147:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        41: begin
          if ((stuckIsFree_4_requestedAt < stuckIsFree_4_finishedAt)) begin
            alloc_pc <= alloc_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1352:<init>|  Chip.java:1351:waitResultOfTransaction|  Btree.java:0148:free|  Btree.java:4142:test_allocate|  Btree.java:7112:newTests|  Btree.java:7117:main|");
            $fclose(f);
          end
        end
        default: alloc_stop <= 1;
      endcase
    end
  end
  task chipPrint;
    begin
      integer o;
      o = $fopen("verilog/trace_verilog.txt", "a");
      if (!o) o = $fopen("../verilog/trace_verilog.txt", "a");
      if (!o) $display("Cannot create trace folder: verilog/trace_verilog.txt");
      $fwrite(o, "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n", "Btree", step, maxSteps, !stop);
      $fwrite(o, "  Processes:\n");

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "stuckIsLeaf", 1, stuckIsLeaf_pc, stuckIsLeaf_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[1]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[2]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_3", stuckIsLeaf_3_requestedAt, stuckIsLeaf_3_finishedAt, stuckIsLeaf_stuckIsLeaf_3_returnCode, (stuckIsLeaf_3_requestedAt > stuckIsLeaf_3_finishedAt && stuckIsLeaf_3_requestedAt != step), (stuckIsLeaf_3_requestedAt < stuckIsLeaf_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "alloc_stuckIsLeaf_3_index_5", alloc_stuckIsLeaf_3_index_5);

      $fwrite(o, "            %-38s = %1d\n", "alloc_stuckIsLeaf_3_value_6", alloc_stuckIsLeaf_3_value_6);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsFree", 1, stuckIsFree_pc, stuckIsFree_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 1, 1);
      $fwrite(o, "        %2d", stuckIsFree_memory[0]);
      $fwrite(o, ", %2d", stuckIsFree_memory[1]);
      $fwrite(o, ", %2d", stuckIsFree_memory[2]);
      $fwrite(o, ", %2d", stuckIsFree_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsFree_4", stuckIsFree_4_requestedAt, stuckIsFree_4_finishedAt, stuckIsFree_stuckIsFree_4_returnCode, (stuckIsFree_4_requestedAt > stuckIsFree_4_finishedAt && stuckIsFree_4_requestedAt != step), (stuckIsFree_4_requestedAt < stuckIsFree_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "alloc_stuckIsFree_4_index_7", alloc_stuckIsFree_4_index_7);

      $fwrite(o, "            %-38s = %1d\n", "alloc_stuckIsFree_4_value_8", alloc_stuckIsFree_4_value_8);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 2, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", freeNext_memory[0]);
      $fwrite(o, ", %2d", freeNext_memory[1]);
      $fwrite(o, ", %2d", freeNext_memory[2]);
      $fwrite(o, ", %2d", freeNext_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "freeNext_freeNext_1_result_0", freeNext_freeNext_1_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "freeNext_1", freeNext_1_requestedAt, freeNext_1_finishedAt, freeNext_freeNext_1_returnCode, (freeNext_1_requestedAt > freeNext_1_finishedAt && freeNext_1_requestedAt != step), (freeNext_1_requestedAt < freeNext_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "alloc_freeNext_1_index_2", alloc_freeNext_1_index_2);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "freeNext_freeNext_1_result_0", freeNext_freeNext_1_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "freeNext_2", freeNext_2_requestedAt, freeNext_2_finishedAt, freeNext_freeNext_2_returnCode, (freeNext_2_requestedAt > freeNext_2_finishedAt && freeNext_2_requestedAt != step), (freeNext_2_requestedAt < freeNext_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "alloc_freeNext_2_index_3", alloc_freeNext_2_index_3);

      $fwrite(o, "            %-38s = %1d\n", "alloc_freeNext_2_value_4", alloc_freeNext_2_value_4);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0]);
      $fwrite(o, ", %2d", stuckSize_memory[1]);
      $fwrite(o, ", %2d", stuckSize_memory[2]);
      $fwrite(o, ", %2d", stuckSize_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckKeys", 1, stuckKeys_pc, stuckKeys_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 8, 4);
      $fwrite(o, "        %2d", stuckKeys_memory[0]);
      $fwrite(o, ", %2d", stuckKeys_memory[1]);
      $fwrite(o, ", %2d", stuckKeys_memory[2]);
      $fwrite(o, ", %2d", stuckKeys_memory[3]);
      $fwrite(o, ", %2d", stuckKeys_memory[4]);
      $fwrite(o, ", %2d", stuckKeys_memory[5]);
      $fwrite(o, ", %2d", stuckKeys_memory[6]);
      $fwrite(o, ", %2d", stuckKeys_memory[7]);
      $fwrite(o, ", %2d", stuckKeys_memory[8]);
      $fwrite(o, ", %2d", stuckKeys_memory[9]);
      $fwrite(o, ", %2d", stuckKeys_memory[10]);
      $fwrite(o, ", %2d", stuckKeys_memory[11]);
      $fwrite(o, ", %2d", stuckKeys_memory[12]);
      $fwrite(o, ", %2d", stuckKeys_memory[13]);
      $fwrite(o, ", %2d", stuckKeys_memory[14]);
      $fwrite(o, ", %2d", stuckKeys_memory[15]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 5, "stuckData", 1, stuckData_pc, stuckData_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 8, 4);
      $fwrite(o, "        %2d", stuckData_memory[0]);
      $fwrite(o, ", %2d", stuckData_memory[1]);
      $fwrite(o, ", %2d", stuckData_memory[2]);
      $fwrite(o, ", %2d", stuckData_memory[3]);
      $fwrite(o, ", %2d", stuckData_memory[4]);
      $fwrite(o, ", %2d", stuckData_memory[5]);
      $fwrite(o, ", %2d", stuckData_memory[6]);
      $fwrite(o, ", %2d", stuckData_memory[7]);
      $fwrite(o, ", %2d", stuckData_memory[8]);
      $fwrite(o, ", %2d", stuckData_memory[9]);
      $fwrite(o, ", %2d", stuckData_memory[10]);
      $fwrite(o, ", %2d", stuckData_memory[11]);
      $fwrite(o, ", %2d", stuckData_memory[12]);
      $fwrite(o, ", %2d", stuckData_memory[13]);
      $fwrite(o, ", %2d", stuckData_memory[14]);
      $fwrite(o, ", %2d", stuckData_memory[15]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "alloc", 42, alloc_pc, alloc_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_index1_0", alloc_index1_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_index2_1", alloc_index2_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_freeNext_1_index_2", alloc_freeNext_1_index_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_freeNext_2_index_3", alloc_freeNext_2_index_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_freeNext_2_value_4", alloc_freeNext_2_value_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_stuckIsLeaf_3_index_5", alloc_stuckIsLeaf_3_index_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_stuckIsLeaf_3_value_6", alloc_stuckIsLeaf_3_value_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_stuckIsFree_4_index_7", alloc_stuckIsFree_4_index_7);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_stuckIsFree_4_value_8", alloc_stuckIsFree_4_value_8);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_root_9", alloc_root_9);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_next_10", alloc_next_10);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isLeaf_11", alloc_isLeaf_11);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isFree_12", alloc_isFree_12);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_root_13", alloc_root_13);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_next_14", alloc_next_14);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isLeaf_15", alloc_isLeaf_15);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isFree_16", alloc_isFree_16);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_next_17", alloc_next_17);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_root_18", alloc_root_18);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isFree_19", alloc_isFree_19);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_next_20", alloc_next_20);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_root_21", alloc_root_21);
      $fwrite(o, "        Register: %-32s = %1d\n",  "alloc_isFree_22", alloc_isFree_22);
      $fclose(o);
    end
  endtask
endmodule
