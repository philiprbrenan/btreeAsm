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

  assign stop = stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop||Stuck_stop;                                                             // Or of process stop fields

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
      processCurrent = 6; clock = 0; #1; clock = 1; #1; // process_Stuck_0006
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0000
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[8*1];
  reg [1-1:0] stuckIsLeaf_stuckIsLeaf_7_result_0;
  integer stuckIsLeaf_7_requestedAt;
  integer stuckIsLeaf_7_finishedAt;
  integer stuckIsLeaf_stuckIsLeaf_7_returnCode;
  integer stuckIsLeaf_8_requestedAt;
  integer stuckIsLeaf_8_finishedAt;
  integer stuckIsLeaf_stuckIsLeaf_8_returnCode;
  integer stuckIsLeaf_pc;
  integer stuckIsLeaf_stop;
  integer stuckIsLeaf_returnCode;
  integer stuckIsLeaf_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsLeaf_pc <= 0;
      stuckIsLeaf_stop <= 0;
      stuckIsLeaf_returnCode <= 0;
      stuckIsLeaf_stuckIsLeaf_7_result_0 <= 0;
      stuckIsLeaf_7_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_7_returnCode <= 0;
      stuckIsLeaf_8_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_8_returnCode <= 0;
      for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= 0;
      stuckIsLeaf_memory[1] <= 1;
      stuckIsLeaf_memory[2] <= 1;
      stuckIsLeaf_memory[3] <= 1;
      for(stuckIsLeaf_memory_index = 4; stuckIsLeaf_memory_index < 8; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= 0;
    end
    else if (processCurrent == 0) begin
      case(stuckIsLeaf_pc)
        0: begin
          if ((stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step)) begin
            stuckIsLeaf_stuckIsLeaf_7_result_0 <= stuckIsLeaf_memory[Stuck_stuckIsLeaf_7_index_36*1+0];
            stuckIsLeaf_7_finishedAt <= step;
          end
          else if ((stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step)) begin
            stuckIsLeaf_memory[Stuck_stuckIsLeaf_8_index_37*1+0] <= Stuck_stuckIsLeaf_8_value_38;
            stuckIsLeaf_8_finishedAt <= step;
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
  reg [1-1:0] stuckIsFree_memory[8*1];
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  integer stuckIsFree_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsFree_pc <= 0;
      stuckIsFree_stop <= 0;
      stuckIsFree_returnCode <= 0;
      for(stuckIsFree_memory_index = 0; stuckIsFree_memory_index < 1; stuckIsFree_memory_index = stuckIsFree_memory_index + 1) stuckIsFree_memory[stuckIsFree_memory_index] <= 0;
      stuckIsFree_memory[1] <= 1;
      stuckIsFree_memory[2] <= 1;
      stuckIsFree_memory[3] <= 1;
      stuckIsFree_memory[4] <= 1;
      stuckIsFree_memory[5] <= 1;
      stuckIsFree_memory[6] <= 1;
      stuckIsFree_memory[7] <= 1;
    end
    else if (processCurrent == 1) begin
      case(stuckIsFree_pc)
        0: begin
        end
        default: stuckIsFree_stop <= 1;
      endcase
    end
  end
  // Process: freeNext  process_freeNext_0002
  (* ram_style = "block" *)
  reg [4-1:0] freeNext_memory[8*1];
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  integer freeNext_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      freeNext_pc <= 0;
      freeNext_stop <= 0;
      freeNext_returnCode <= 0;
      freeNext_memory[0] <= 1;
      freeNext_memory[1] <= 2;
      freeNext_memory[2] <= 3;
      freeNext_memory[3] <= 4;
      freeNext_memory[4] <= 5;
      freeNext_memory[5] <= 6;
      freeNext_memory[6] <= 7;
      for(freeNext_memory_index = 7; freeNext_memory_index < 8; freeNext_memory_index = freeNext_memory_index + 1) freeNext_memory[freeNext_memory_index] <= 0;
    end
    else if (processCurrent == 2) begin
      case(freeNext_pc)
        0: begin
        end
        default: freeNext_stop <= 1;
      endcase
    end
  end
  // Process: stuckSize  process_stuckSize_0003
  (* ram_style = "block" *)
  reg [3-1:0] stuckSize_memory[8*1];
  reg [3-1:0] stuckSize_stuckSize_5_result_0;
  integer stuckSize_5_requestedAt;
  integer stuckSize_5_finishedAt;
  integer stuckSize_stuckSize_5_returnCode;
  integer stuckSize_6_requestedAt;
  integer stuckSize_6_finishedAt;
  integer stuckSize_stuckSize_6_returnCode;
  integer stuckSize_pc;
  integer stuckSize_stop;
  integer stuckSize_returnCode;
  integer stuckSize_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckSize_pc <= 0;
      stuckSize_stop <= 0;
      stuckSize_returnCode <= 0;
      stuckSize_stuckSize_5_result_0 <= 0;
      stuckSize_5_finishedAt <= -1;
      stuckSize_stuckSize_5_returnCode <= 0;
      stuckSize_6_finishedAt <= -1;
      stuckSize_stuckSize_6_returnCode <= 0;
      stuckSize_memory[0] <= 2;
      stuckSize_memory[1] <= 3;
      stuckSize_memory[2] <= 4;
      stuckSize_memory[3] <= 4;
      for(stuckSize_memory_index = 4; stuckSize_memory_index < 8; stuckSize_memory_index = stuckSize_memory_index + 1) stuckSize_memory[stuckSize_memory_index] <= 0;
    end
    else if (processCurrent == 3) begin
      case(stuckSize_pc)
        0: begin
          if ((stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step)) begin
            stuckSize_stuckSize_5_result_0 <= stuckSize_memory[Stuck_stuckSize_5_index_33*1+0];
            stuckSize_5_finishedAt <= step;
          end
          else if ((stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step)) begin
            stuckSize_memory[Stuck_stuckSize_6_index_34*1+0] <= Stuck_stuckSize_6_value_35;
            stuckSize_6_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckSize_stop <= 1;
      endcase
    end
  end
  // Process: stuckKeys  process_stuckKeys_0004
  (* ram_style = "block" *)
  reg [8-1:0] stuckKeys_memory[8*4];
  reg [8-1:0] stuckKeys_stuckKeys_1_result_0;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_1;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_2;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_3;
  integer stuckKeys_1_requestedAt;
  integer stuckKeys_1_finishedAt;
  integer stuckKeys_stuckKeys_1_returnCode;
  integer stuckKeys_2_requestedAt;
  integer stuckKeys_2_finishedAt;
  integer stuckKeys_stuckKeys_2_returnCode;
  integer stuckKeys_pc;
  integer stuckKeys_stop;
  integer stuckKeys_returnCode;
  integer stuckKeys_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckKeys_pc <= 0;
      stuckKeys_stop <= 0;
      stuckKeys_returnCode <= 0;
      stuckKeys_stuckKeys_1_result_0 <= 0;
      stuckKeys_stuckKeys_1_result_1 <= 0;
      stuckKeys_stuckKeys_1_result_2 <= 0;
      stuckKeys_stuckKeys_1_result_3 <= 0;
      stuckKeys_1_finishedAt <= -1;
      stuckKeys_stuckKeys_1_returnCode <= 0;
      stuckKeys_2_finishedAt <= -1;
      stuckKeys_stuckKeys_2_returnCode <= 0;
      stuckKeys_memory[0] <= 10;
      stuckKeys_memory[1] <= 20;
      stuckKeys_memory[2] <= 30;
      stuckKeys_memory[3] <= 40;
      stuckKeys_memory[4] <= 1;
      stuckKeys_memory[5] <= 3;
      stuckKeys_memory[6] <= 5;
      stuckKeys_memory[7] <= 7;
      stuckKeys_memory[8] <= 11;
      stuckKeys_memory[9] <= 12;
      stuckKeys_memory[10] <= 13;
      stuckKeys_memory[11] <= 14;
      stuckKeys_memory[12] <= 21;
      stuckKeys_memory[13] <= 22;
      stuckKeys_memory[14] <= 23;
      stuckKeys_memory[15] <= 24;
      for(stuckKeys_memory_index = 16; stuckKeys_memory_index < 32; stuckKeys_memory_index = stuckKeys_memory_index + 1) stuckKeys_memory[stuckKeys_memory_index] <= 0;
    end
    else if (processCurrent == 4) begin
      case(stuckKeys_pc)
        0: begin
          if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
            stuckKeys_stuckKeys_1_result_0 <= stuckKeys_memory[Stuck_stuckKeys_1_index_21*4+0];
            stuckKeys_stuckKeys_1_result_1 <= stuckKeys_memory[Stuck_stuckKeys_1_index_21*4+1];
            stuckKeys_stuckKeys_1_result_2 <= stuckKeys_memory[Stuck_stuckKeys_1_index_21*4+2];
            stuckKeys_stuckKeys_1_result_3 <= stuckKeys_memory[Stuck_stuckKeys_1_index_21*4+3];
            stuckKeys_1_finishedAt <= step;
          end
          else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
            stuckKeys_memory[Stuck_stuckKeys_2_index_22*4+0] <= Stuck_stuckKeys_2_value_23;
            stuckKeys_memory[Stuck_stuckKeys_2_index_22*4+1] <= Stuck_stuckKeys_2_value_24;
            stuckKeys_memory[Stuck_stuckKeys_2_index_22*4+2] <= Stuck_stuckKeys_2_value_25;
            stuckKeys_memory[Stuck_stuckKeys_2_index_22*4+3] <= Stuck_stuckKeys_2_value_26;
            stuckKeys_2_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckKeys_stop <= 1;
      endcase
    end
  end
  // Process: stuckData  process_stuckData_0005
  (* ram_style = "block" *)
  reg [8-1:0] stuckData_memory[8*4];
  reg [8-1:0] stuckData_stuckData_3_result_0;
  reg [8-1:0] stuckData_stuckData_3_result_1;
  reg [8-1:0] stuckData_stuckData_3_result_2;
  reg [8-1:0] stuckData_stuckData_3_result_3;
  integer stuckData_3_requestedAt;
  integer stuckData_3_finishedAt;
  integer stuckData_stuckData_3_returnCode;
  integer stuckData_4_requestedAt;
  integer stuckData_4_finishedAt;
  integer stuckData_stuckData_4_returnCode;
  integer stuckData_pc;
  integer stuckData_stop;
  integer stuckData_returnCode;
  integer stuckData_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckData_pc <= 0;
      stuckData_stop <= 0;
      stuckData_returnCode <= 0;
      stuckData_stuckData_3_result_0 <= 0;
      stuckData_stuckData_3_result_1 <= 0;
      stuckData_stuckData_3_result_2 <= 0;
      stuckData_stuckData_3_result_3 <= 0;
      stuckData_3_finishedAt <= -1;
      stuckData_stuckData_3_returnCode <= 0;
      stuckData_4_finishedAt <= -1;
      stuckData_stuckData_4_returnCode <= 0;
      stuckData_memory[0] <= 1;
      stuckData_memory[1] <= 2;
      stuckData_memory[2] <= 3;
      stuckData_memory[3] <= 4;
      stuckData_memory[4] <= 1;
      stuckData_memory[5] <= 33;
      stuckData_memory[6] <= 55;
      stuckData_memory[7] <= 77;
      stuckData_memory[8] <= 2;
      stuckData_memory[9] <= 12;
      stuckData_memory[10] <= 22;
      stuckData_memory[11] <= 32;
      stuckData_memory[12] <= 3;
      stuckData_memory[13] <= 13;
      stuckData_memory[14] <= 23;
      stuckData_memory[15] <= 33;
      for(stuckData_memory_index = 16; stuckData_memory_index < 32; stuckData_memory_index = stuckData_memory_index + 1) stuckData_memory[stuckData_memory_index] <= 0;
    end
    else if (processCurrent == 5) begin
      case(stuckData_pc)
        0: begin
          if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
            stuckData_stuckData_3_result_0 <= stuckData_memory[Stuck_stuckData_3_index_27*4+0];
            stuckData_stuckData_3_result_1 <= stuckData_memory[Stuck_stuckData_3_index_27*4+1];
            stuckData_stuckData_3_result_2 <= stuckData_memory[Stuck_stuckData_3_index_27*4+2];
            stuckData_stuckData_3_result_3 <= stuckData_memory[Stuck_stuckData_3_index_27*4+3];
            stuckData_3_finishedAt <= step;
          end
          else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
            stuckData_memory[Stuck_stuckData_4_index_28*4+0] <= Stuck_stuckData_4_value_29;
            stuckData_memory[Stuck_stuckData_4_index_28*4+1] <= Stuck_stuckData_4_value_30;
            stuckData_memory[Stuck_stuckData_4_index_28*4+2] <= Stuck_stuckData_4_value_31;
            stuckData_memory[Stuck_stuckData_4_index_28*4+3] <= Stuck_stuckData_4_value_32;
            stuckData_4_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckData_stop <= 1;
      endcase
    end
  end
  // Process: Stuck  process_Stuck_0006
  reg [8-1:0] Stuck_k_0;
  reg [4-1:0] Stuck_index_1;
  reg [3-1:0] Stuck_size_2;
  reg [1-1:0] Stuck_isLeaf_3;
  reg [4-1:0] Stuck_nextFree_4;
  reg [8-1:0] Stuck_Key_0_5;
  reg [1-1:0] Stuck_KeyCompares_0_6;
  reg [3-1:0] Stuck_KeyCollapse_0_7;
  reg [8-1:0] Stuck_Data_0_8;
  reg [8-1:0] Stuck_Key_1_9;
  reg [1-1:0] Stuck_KeyCompares_1_10;
  reg [3-1:0] Stuck_KeyCollapse_1_11;
  reg [8-1:0] Stuck_Data_1_12;
  reg [8-1:0] Stuck_Key_2_13;
  reg [1-1:0] Stuck_KeyCompares_2_14;
  reg [3-1:0] Stuck_KeyCollapse_2_15;
  reg [8-1:0] Stuck_Data_2_16;
  reg [8-1:0] Stuck_Key_3_17;
  reg [1-1:0] Stuck_KeyCompares_3_18;
  reg [3-1:0] Stuck_KeyCollapse_3_19;
  reg [8-1:0] Stuck_Data_3_20;
  reg [3-1:0] Stuck_stuckKeys_1_index_21;
  reg [3-1:0] Stuck_stuckKeys_2_index_22;
  reg [8-1:0] Stuck_stuckKeys_2_value_23;
  reg [8-1:0] Stuck_stuckKeys_2_value_24;
  reg [8-1:0] Stuck_stuckKeys_2_value_25;
  reg [8-1:0] Stuck_stuckKeys_2_value_26;
  reg [3-1:0] Stuck_stuckData_3_index_27;
  reg [3-1:0] Stuck_stuckData_4_index_28;
  reg [8-1:0] Stuck_stuckData_4_value_29;
  reg [8-1:0] Stuck_stuckData_4_value_30;
  reg [8-1:0] Stuck_stuckData_4_value_31;
  reg [8-1:0] Stuck_stuckData_4_value_32;
  reg [3-1:0] Stuck_stuckSize_5_index_33;
  reg [3-1:0] Stuck_stuckSize_6_index_34;
  reg [3-1:0] Stuck_stuckSize_6_value_35;
  reg [3-1:0] Stuck_stuckIsLeaf_7_index_36;
  reg [3-1:0] Stuck_stuckIsLeaf_8_index_37;
  reg [1-1:0] Stuck_stuckIsLeaf_8_value_38;
  reg [1-1:0] Stuck_Found_39;
  reg [8-1:0] Stuck_Key_40;
  reg [8-1:0] Stuck_FoundKey_41;
  reg [8-1:0] Stuck_Data_42;
  reg [4-1:0] Stuck_BtreeIndex_43;
  reg [3-1:0] Stuck_StuckIndex_44;
  reg [1-1:0] Stuck_MergeSuccess_45;
  integer Stuck_pc;
  integer Stuck_stop;
  integer Stuck_returnCode;
  integer Stuck_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      Stuck_pc <= 0;
      Stuck_stop <= 0;
      Stuck_returnCode <= 0;
      Stuck_k_0 <= 0;
      Stuck_index_1 <= 0;
      Stuck_size_2 <= 0;
      Stuck_isLeaf_3 <= 0;
      Stuck_nextFree_4 <= 0;
      Stuck_Key_0_5 <= 0;
      Stuck_KeyCompares_0_6 <= 0;
      Stuck_KeyCollapse_0_7 <= 0;
      Stuck_Data_0_8 <= 0;
      Stuck_Key_1_9 <= 0;
      Stuck_KeyCompares_1_10 <= 0;
      Stuck_KeyCollapse_1_11 <= 0;
      Stuck_Data_1_12 <= 0;
      Stuck_Key_2_13 <= 0;
      Stuck_KeyCompares_2_14 <= 0;
      Stuck_KeyCollapse_2_15 <= 0;
      Stuck_Data_2_16 <= 0;
      Stuck_Key_3_17 <= 0;
      Stuck_KeyCompares_3_18 <= 0;
      Stuck_KeyCollapse_3_19 <= 0;
      Stuck_Data_3_20 <= 0;
      Stuck_stuckKeys_1_index_21 <= 0;
      Stuck_stuckKeys_2_index_22 <= 0;
      Stuck_stuckKeys_2_value_23 <= 0;
      Stuck_stuckKeys_2_value_24 <= 0;
      Stuck_stuckKeys_2_value_25 <= 0;
      Stuck_stuckKeys_2_value_26 <= 0;
      Stuck_stuckData_3_index_27 <= 0;
      Stuck_stuckData_4_index_28 <= 0;
      Stuck_stuckData_4_value_29 <= 0;
      Stuck_stuckData_4_value_30 <= 0;
      Stuck_stuckData_4_value_31 <= 0;
      Stuck_stuckData_4_value_32 <= 0;
      Stuck_stuckSize_5_index_33 <= 0;
      Stuck_stuckSize_6_index_34 <= 0;
      Stuck_stuckSize_6_value_35 <= 0;
      Stuck_stuckIsLeaf_7_index_36 <= 0;
      Stuck_stuckIsLeaf_8_index_37 <= 0;
      Stuck_stuckIsLeaf_8_value_38 <= 0;
      Stuck_Found_39 <= 0;
      Stuck_Key_40 <= 0;
      Stuck_FoundKey_41 <= 0;
      Stuck_Data_42 <= 0;
      Stuck_BtreeIndex_43 <= 0;
      Stuck_StuckIndex_44 <= 0;
      Stuck_MergeSuccess_45 <= 0;
      stuckIsLeaf_7_requestedAt <= -1;
      stuckIsLeaf_8_requestedAt <= -1;
      stuckSize_5_requestedAt <= -1;
      stuckSize_6_requestedAt <= -1;
      stuckKeys_1_requestedAt <= -1;
      stuckKeys_2_requestedAt <= -1;
      stuckData_3_requestedAt <= -1;
      stuckData_4_requestedAt <= -1;
    end
    else if (processCurrent == 6) begin
      case(Stuck_pc)
        0: begin
          Stuck_k_0 <= 3;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Btree.java:5037:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        1: begin
          Stuck_BtreeIndex_43 <= 0;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0819:<init>|  Chip.java:0818:Zero|  Btree.java:2443:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        2: begin
          Stuck_index_1 <= Stuck_BtreeIndex_43;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:0261:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        3: begin
          Stuck_stuckSize_5_index_33 <= Stuck_index_1;
          stuckSize_5_requestedAt <= step;
          stuckSize_5_finishedAt <= -1;
          Stuck_stuckIsLeaf_7_index_36 <= Stuck_index_1;
          stuckIsLeaf_7_requestedAt <= step;
          stuckIsLeaf_7_finishedAt <= -1;
          Stuck_stuckKeys_1_index_21 <= Stuck_index_1;
          stuckKeys_1_requestedAt <= step;
          stuckKeys_1_finishedAt <= -1;
          Stuck_stuckData_3_index_27 <= Stuck_index_1;
          stuckData_3_requestedAt <= step;
          stuckData_3_finishedAt <= -1;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0272:<init>|  Btree.java:0271:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        4: begin
          if ((stuckSize_5_requestedAt < stuckSize_5_finishedAt)) begin
            Stuck_pc <= Stuck_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1357:<init>|  Chip.java:1356:waitResultOfTransaction|  Btree.java:0285:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        5: begin
          if ((stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt)) begin
            Stuck_pc <= Stuck_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1357:<init>|  Chip.java:1356:waitResultOfTransaction|  Btree.java:0286:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        6: begin
          if ((stuckKeys_1_requestedAt < stuckKeys_1_finishedAt)) begin
            Stuck_pc <= Stuck_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1357:<init>|  Chip.java:1356:waitResultOfTransaction|  Btree.java:0288:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        7: begin
          if ((stuckData_3_requestedAt < stuckData_3_finishedAt)) begin
            Stuck_pc <= Stuck_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1357:<init>|  Chip.java:1356:waitResultOfTransaction|  Btree.java:0289:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        8: begin
          Stuck_size_2 <= stuckSize_stuckSize_5_result_0;
          Stuck_isLeaf_3 <= stuckIsLeaf_stuckIsLeaf_7_result_0;
          Stuck_Key_0_5 <= stuckKeys_stuckKeys_1_result_0;
          Stuck_Data_0_8 <= stuckData_stuckData_3_result_0;
          Stuck_Key_1_9 <= stuckKeys_stuckKeys_1_result_1;
          Stuck_Data_1_12 <= stuckData_stuckData_3_result_1;
          Stuck_Key_2_13 <= stuckKeys_stuckKeys_1_result_2;
          Stuck_Data_2_16 <= stuckData_stuckData_3_result_2;
          Stuck_Key_3_17 <= stuckKeys_stuckKeys_1_result_3;
          Stuck_Data_3_20 <= stuckData_stuckData_3_result_3;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0292:<init>|  Btree.java:0291:stuckGet|  Btree.java:0262:stuckGet|  Btree.java:2447:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        9: begin
          if (Stuck_isLeaf_3 == 0) begin
            Stuck_pc <= 16;
          end
          else begin
            Stuck_pc <= Stuck_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Btree.java:1587:<init>|  Btree.java:1586:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        10: begin
          Stuck_KeyCompares_0_6 <= Stuck_k_0 == Stuck_Key_0_5 && 0 < Stuck_size_2;
          Stuck_KeyCollapse_0_7 <= 0;
          Stuck_KeyCompares_1_10 <= Stuck_k_0 == Stuck_Key_1_9 && 1 < Stuck_size_2;
          Stuck_KeyCollapse_1_11 <= 1;
          Stuck_KeyCompares_2_14 <= Stuck_k_0 == Stuck_Key_2_13 && 2 < Stuck_size_2;
          Stuck_KeyCollapse_2_15 <= 2;
          Stuck_KeyCompares_3_18 <= Stuck_k_0 == Stuck_Key_3_17 && 3 < Stuck_size_2;
          Stuck_KeyCollapse_3_19 <= 3;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0954:<init>|  Btree.java:0953:search_eq_parallel|  Btree.java:2450:Leaf|  Btree.java:1602:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        11: begin
          if (Stuck_KeyCompares_1_10) begin
            Stuck_KeyCompares_0_6 <= 1;
            Stuck_KeyCollapse_0_7 <= Stuck_KeyCollapse_1_11;
          end
          if (Stuck_KeyCompares_3_18) begin
            Stuck_KeyCompares_2_14 <= 1;
            Stuck_KeyCollapse_2_15 <= Stuck_KeyCollapse_3_19;
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0975:<init>|  Btree.java:0974:search_eq_parallel|  Btree.java:2450:Leaf|  Btree.java:1602:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        12: begin
          if (Stuck_KeyCompares_2_14) begin
            Stuck_KeyCompares_0_6 <= 1;
            Stuck_KeyCollapse_0_7 <= Stuck_KeyCollapse_2_15;
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0975:<init>|  Btree.java:0974:search_eq_parallel|  Btree.java:2450:Leaf|  Btree.java:1602:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        13: begin
          if (Stuck_KeyCompares_0_6) begin
            Stuck_Found_39 <= 1;
            case (Stuck_KeyCollapse_0_7)
              0: begin
                Stuck_StuckIndex_44 <= 0;
                Stuck_Key_40 <= Stuck_Key_0_5;
                Stuck_Data_42 <= Stuck_Data_0_8;
              end
              1: begin
                Stuck_StuckIndex_44 <= 1;
                Stuck_Key_40 <= Stuck_Key_1_9;
                Stuck_Data_42 <= Stuck_Data_1_12;
              end
              2: begin
                Stuck_StuckIndex_44 <= 2;
                Stuck_Key_40 <= Stuck_Key_2_13;
                Stuck_Data_42 <= Stuck_Data_2_16;
              end
              3: begin
                Stuck_StuckIndex_44 <= 3;
                Stuck_Key_40 <= Stuck_Key_3_17;
                Stuck_Data_42 <= Stuck_Data_3_20;
              end
            endcase
          end
          else begin
            Stuck_Found_39 <= 0;
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0997:<init>|  Btree.java:0996:search_eq_parallel|  Btree.java:2450:Leaf|  Btree.java:1602:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        14: begin
          Stuck_pc <= 22;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0571:<init>|  Chip.java:0570:GOto|  Btree.java:2451:Leaf|  Btree.java:1602:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        15: begin
          Stuck_pc <= 22;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0571:<init>|  Chip.java:0570:GOto|  Btree.java:1603:code|  Chip.java:0530:<init>|  Btree.java:1584:<init>|  Btree.java:1583:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        16: begin
          Stuck_KeyCompares_0_6 <= Stuck_k_0 <= Stuck_Key_0_5 && 0 < Stuck_size_2;
          Stuck_KeyCollapse_0_7 <= 0;
          Stuck_KeyCompares_1_10 <= Stuck_k_0 >  Stuck_Key_0_5 && Stuck_k_0 <= Stuck_Key_1_9 && 1 < Stuck_size_2;
          Stuck_KeyCollapse_1_11 <= 1;
          Stuck_KeyCompares_2_14 <= Stuck_k_0 >  Stuck_Key_1_9 && Stuck_k_0 <= Stuck_Key_2_13 && 2 < Stuck_size_2;
          Stuck_KeyCollapse_2_15 <= 2;
          Stuck_KeyCompares_3_18 <= Stuck_k_0 >  Stuck_Key_2_13 && Stuck_k_0 <= Stuck_Key_3_17 && 3 < Stuck_size_2;
          Stuck_KeyCollapse_3_19 <= 3;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:1090:<init>|  Btree.java:1089:search_le_parallel|  Btree.java:2454:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        17: begin
          if (Stuck_KeyCompares_1_10) begin
            Stuck_KeyCompares_0_6 <= 1;
            Stuck_KeyCollapse_0_7 <= Stuck_KeyCollapse_1_11;
          end
          if (Stuck_KeyCompares_3_18) begin
            Stuck_KeyCompares_2_14 <= 1;
            Stuck_KeyCollapse_2_15 <= Stuck_KeyCollapse_3_19;
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:1126:<init>|  Btree.java:1125:search_le_parallel|  Btree.java:2454:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        18: begin
          if (Stuck_KeyCompares_2_14) begin
            Stuck_KeyCompares_0_6 <= 1;
            Stuck_KeyCollapse_0_7 <= Stuck_KeyCollapse_2_15;
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:1126:<init>|  Btree.java:1125:search_le_parallel|  Btree.java:2454:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        19: begin
          if (Stuck_KeyCompares_0_6) begin
            Stuck_Found_39 <= 1;
            case (Stuck_KeyCollapse_0_7)
              0: begin
                Stuck_StuckIndex_44 <= 0;
                Stuck_FoundKey_41 <= Stuck_Key_0_5;
                Stuck_Data_42 <= Stuck_Data_0_8;
              end
              1: begin
                Stuck_StuckIndex_44 <= 1;
                Stuck_FoundKey_41 <= Stuck_Key_1_9;
                Stuck_Data_42 <= Stuck_Data_1_12;
              end
              2: begin
                Stuck_StuckIndex_44 <= 2;
                Stuck_FoundKey_41 <= Stuck_Key_2_13;
                Stuck_Data_42 <= Stuck_Data_2_16;
              end
              3: begin
                Stuck_StuckIndex_44 <= 3;
                Stuck_FoundKey_41 <= Stuck_Key_3_17;
                Stuck_Data_42 <= Stuck_Data_3_20;
              end
            endcase
          end
          else begin
            Stuck_Found_39 <= 0;
            case (Stuck_size_2)
              0: begin
                Stuck_StuckIndex_44 <= 0;
                Stuck_Data_42 <= Stuck_Data_0_8;
              end
              1: begin
                Stuck_StuckIndex_44 <= 1;
                Stuck_Data_42 <= Stuck_Data_1_12;
              end
              2: begin
                Stuck_StuckIndex_44 <= 2;
                Stuck_Data_42 <= Stuck_Data_2_16;
              end
              3: begin
                Stuck_StuckIndex_44 <= 3;
                Stuck_Data_42 <= Stuck_Data_3_20;
              end
            endcase
          end
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:1148:<init>|  Btree.java:1147:search_le_parallel|  Btree.java:2454:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        20: begin
          Stuck_BtreeIndex_43 <= Stuck_Data_42;
          Stuck_pc <= Stuck_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0697:<init>|  Chip.java:0696:Copy|  Btree.java:2455:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        21: begin
          Stuck_pc <= 2;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0571:<init>|  Chip.java:0570:GOto|  Btree.java:2456:Branch|  Btree.java:1606:code|  Chip.java:0530:<init>|  Btree.java:1581:<init>|  Btree.java:1580:<init>|  Btree.java:2449:<init>|  Btree.java:2448:code|  Chip.java:0530:<init>|  Btree.java:2446:<init>|  Btree.java:2445:findSearch|  Btree.java:5040:test_find|  Btree.java:7249:oldTests|  Btree.java:7264:newTests|  Btree.java:7270:main|");
            $fclose(f);
          end
        end
        default: Stuck_stop <= 1;
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
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[1]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[2]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[3]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[4]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[5]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[6]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckIsLeaf_7", stuckIsLeaf_7_requestedAt, stuckIsLeaf_7_finishedAt, stuckIsLeaf_stuckIsLeaf_7_returnCode, (stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step), (stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckIsLeaf_7_index_36", Stuck_stuckIsLeaf_7_index_36);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_8", stuckIsLeaf_8_requestedAt, stuckIsLeaf_8_finishedAt, stuckIsLeaf_stuckIsLeaf_8_returnCode, (stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step), (stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckIsLeaf_8_index_37", Stuck_stuckIsLeaf_8_index_37);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckIsLeaf_8_value_38", Stuck_stuckIsLeaf_8_value_38);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsFree", 1, stuckIsFree_pc, stuckIsFree_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 1, 1);
      $fwrite(o, "        %2d", stuckIsFree_memory[0]);
      $fwrite(o, ", %2d", stuckIsFree_memory[1]);
      $fwrite(o, ", %2d", stuckIsFree_memory[2]);
      $fwrite(o, ", %2d", stuckIsFree_memory[3]);
      $fwrite(o, ", %2d", stuckIsFree_memory[4]);
      $fwrite(o, ", %2d", stuckIsFree_memory[5]);
      $fwrite(o, ", %2d", stuckIsFree_memory[6]);
      $fwrite(o, ", %2d", stuckIsFree_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 2, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 4, 1);
      $fwrite(o, "        %2d", freeNext_memory[0]);
      $fwrite(o, ", %2d", freeNext_memory[1]);
      $fwrite(o, ", %2d", freeNext_memory[2]);
      $fwrite(o, ", %2d", freeNext_memory[3]);
      $fwrite(o, ", %2d", freeNext_memory[4]);
      $fwrite(o, ", %2d", freeNext_memory[5]);
      $fwrite(o, ", %2d", freeNext_memory[6]);
      $fwrite(o, ", %2d", freeNext_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 3, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0]);
      $fwrite(o, ", %2d", stuckSize_memory[1]);
      $fwrite(o, ", %2d", stuckSize_memory[2]);
      $fwrite(o, ", %2d", stuckSize_memory[3]);
      $fwrite(o, ", %2d", stuckSize_memory[4]);
      $fwrite(o, ", %2d", stuckSize_memory[5]);
      $fwrite(o, ", %2d", stuckSize_memory[6]);
      $fwrite(o, ", %2d", stuckSize_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckSize_5", stuckSize_5_requestedAt, stuckSize_5_finishedAt, stuckSize_stuckSize_5_returnCode, (stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step), (stuckSize_5_requestedAt < stuckSize_5_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckSize_5_index_33", Stuck_stuckSize_5_index_33);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckSize_6", stuckSize_6_requestedAt, stuckSize_6_finishedAt, stuckSize_stuckSize_6_returnCode, (stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step), (stuckSize_6_requestedAt < stuckSize_6_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckSize_6_index_34", Stuck_stuckSize_6_index_34);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckSize_6_value_35", Stuck_stuckSize_6_value_35);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckKeys", 1, stuckKeys_pc, stuckKeys_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 4);
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
      $fwrite(o, ", %2d", stuckKeys_memory[16]);
      $fwrite(o, ", %2d", stuckKeys_memory[17]);
      $fwrite(o, ", %2d", stuckKeys_memory[18]);
      $fwrite(o, ", %2d", stuckKeys_memory[19]);
      $fwrite(o, ", %2d", stuckKeys_memory[20]);
      $fwrite(o, ", %2d", stuckKeys_memory[21]);
      $fwrite(o, ", %2d", stuckKeys_memory[22]);
      $fwrite(o, ", %2d", stuckKeys_memory[23]);
      $fwrite(o, ", %2d", stuckKeys_memory[24]);
      $fwrite(o, ", %2d", stuckKeys_memory[25]);
      $fwrite(o, ", %2d", stuckKeys_memory[26]);
      $fwrite(o, ", %2d", stuckKeys_memory[27]);
      $fwrite(o, ", %2d", stuckKeys_memory[28]);
      $fwrite(o, ", %2d", stuckKeys_memory[29]);
      $fwrite(o, ", %2d", stuckKeys_memory[30]);
      $fwrite(o, ", %2d", stuckKeys_memory[31]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_1_index_21", Stuck_stuckKeys_1_index_21);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_2_index_22", Stuck_stuckKeys_2_index_22);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_2_value_23", Stuck_stuckKeys_2_value_23);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_2_value_24", Stuck_stuckKeys_2_value_24);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_2_value_25", Stuck_stuckKeys_2_value_25);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckKeys_2_value_26", Stuck_stuckKeys_2_value_26);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 5, "stuckData", 1, stuckData_pc, stuckData_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 4);
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
      $fwrite(o, ", %2d", stuckData_memory[16]);
      $fwrite(o, ", %2d", stuckData_memory[17]);
      $fwrite(o, ", %2d", stuckData_memory[18]);
      $fwrite(o, ", %2d", stuckData_memory[19]);
      $fwrite(o, ", %2d", stuckData_memory[20]);
      $fwrite(o, ", %2d", stuckData_memory[21]);
      $fwrite(o, ", %2d", stuckData_memory[22]);
      $fwrite(o, ", %2d", stuckData_memory[23]);
      $fwrite(o, ", %2d", stuckData_memory[24]);
      $fwrite(o, ", %2d", stuckData_memory[25]);
      $fwrite(o, ", %2d", stuckData_memory[26]);
      $fwrite(o, ", %2d", stuckData_memory[27]);
      $fwrite(o, ", %2d", stuckData_memory[28]);
      $fwrite(o, ", %2d", stuckData_memory[29]);
      $fwrite(o, ", %2d", stuckData_memory[30]);
      $fwrite(o, ", %2d", stuckData_memory[31]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_3_index_27", Stuck_stuckData_3_index_27);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_4_index_28", Stuck_stuckData_4_index_28);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_4_value_29", Stuck_stuckData_4_value_29);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_4_value_30", Stuck_stuckData_4_value_30);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_4_value_31", Stuck_stuckData_4_value_31);

      $fwrite(o, "            %-38s = %1d\n", "Stuck_stuckData_4_value_32", Stuck_stuckData_4_value_32);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "Stuck", 22, Stuck_pc, Stuck_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_k_0", Stuck_k_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_index_1", Stuck_index_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_size_2", Stuck_size_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_isLeaf_3", Stuck_isLeaf_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_nextFree_4", Stuck_nextFree_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Key_0_5", Stuck_Key_0_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCompares_0_6", Stuck_KeyCompares_0_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCollapse_0_7", Stuck_KeyCollapse_0_7);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Data_0_8", Stuck_Data_0_8);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Key_1_9", Stuck_Key_1_9);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCompares_1_10", Stuck_KeyCompares_1_10);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCollapse_1_11", Stuck_KeyCollapse_1_11);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Data_1_12", Stuck_Data_1_12);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Key_2_13", Stuck_Key_2_13);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCompares_2_14", Stuck_KeyCompares_2_14);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCollapse_2_15", Stuck_KeyCollapse_2_15);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Data_2_16", Stuck_Data_2_16);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Key_3_17", Stuck_Key_3_17);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCompares_3_18", Stuck_KeyCompares_3_18);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_KeyCollapse_3_19", Stuck_KeyCollapse_3_19);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Data_3_20", Stuck_Data_3_20);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_1_index_21", Stuck_stuckKeys_1_index_21);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_2_index_22", Stuck_stuckKeys_2_index_22);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_2_value_23", Stuck_stuckKeys_2_value_23);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_2_value_24", Stuck_stuckKeys_2_value_24);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_2_value_25", Stuck_stuckKeys_2_value_25);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckKeys_2_value_26", Stuck_stuckKeys_2_value_26);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_3_index_27", Stuck_stuckData_3_index_27);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_4_index_28", Stuck_stuckData_4_index_28);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_4_value_29", Stuck_stuckData_4_value_29);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_4_value_30", Stuck_stuckData_4_value_30);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_4_value_31", Stuck_stuckData_4_value_31);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckData_4_value_32", Stuck_stuckData_4_value_32);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckSize_5_index_33", Stuck_stuckSize_5_index_33);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckSize_6_index_34", Stuck_stuckSize_6_index_34);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckSize_6_value_35", Stuck_stuckSize_6_value_35);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckIsLeaf_7_index_36", Stuck_stuckIsLeaf_7_index_36);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckIsLeaf_8_index_37", Stuck_stuckIsLeaf_8_index_37);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_stuckIsLeaf_8_value_38", Stuck_stuckIsLeaf_8_value_38);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Found_39", Stuck_Found_39);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Key_40", Stuck_Key_40);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_FoundKey_41", Stuck_FoundKey_41);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_Data_42", Stuck_Data_42);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_BtreeIndex_43", Stuck_BtreeIndex_43);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_StuckIndex_44", Stuck_StuckIndex_44);
      $fwrite(o, "        Register: %-32s = %1d\n",  "Stuck_MergeSuccess_45", Stuck_MergeSuccess_45);
      $fclose(o);
    end
  endtask
endmodule
