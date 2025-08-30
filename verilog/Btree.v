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

  assign stop = stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop||copy_stop;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 200;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_stuckIsLeaf_0000
      processCurrent = 1; clock = 0; #1; clock = 1; #1; // process_stuckIsFree_0001
      processCurrent = 2; clock = 0; #1; clock = 1; #1; // process_freeNext_0002
      processCurrent = 3; clock = 0; #1; clock = 1; #1; // process_stuckSize_0003
      processCurrent = 4; clock = 0; #1; clock = 1; #1; // process_stuckKeys_0004
      processCurrent = 5; clock = 0; #1; clock = 1; #1; // process_stuckData_0005
      processCurrent = 6; clock = 0; #1; clock = 1; #1; // process_copy_0006
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0000
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[1*1];
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
      stuckIsLeaf_memory[0] <= 1;
    end
    else if (processCurrent == 0) begin
      case(stuckIsLeaf_pc)
        0: begin
          if ((stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step)) begin
            stuckIsLeaf_stuckIsLeaf_7_result_0 <= stuckIsLeaf_memory[copy_stuckIsLeaf_7_index_59*1+0];
            stuckIsLeaf_7_finishedAt <= step;
          end
          else if ((stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step)) begin
            stuckIsLeaf_memory[copy_stuckIsLeaf_8_index_60*1+0] <= copy_stuckIsLeaf_8_value_61;
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
  reg [1-1:0] stuckIsFree_memory[1*1];
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
  reg [1-1:0] freeNext_memory[1*1];
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  integer freeNext_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      freeNext_pc <= 0;
      freeNext_stop <= 0;
      freeNext_returnCode <= 0;
      for(freeNext_memory_index = 0; freeNext_memory_index < 1; freeNext_memory_index = freeNext_memory_index + 1) freeNext_memory[freeNext_memory_index] <= 0;
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
  reg [4-1:0] stuckSize_memory[1*1];
  reg [4-1:0] stuckSize_stuckSize_5_result_0;
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
      stuckSize_memory[0] <= 4;
    end
    else if (processCurrent == 3) begin
      case(stuckSize_pc)
        0: begin
          if ((stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step)) begin
            stuckSize_stuckSize_5_result_0 <= stuckSize_memory[copy_stuckSize_5_index_56*1+0];
            stuckSize_5_finishedAt <= step;
          end
          else if ((stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step)) begin
            stuckSize_memory[copy_stuckSize_6_index_57*1+0] <= copy_stuckSize_6_value_58;
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
  reg [8-1:0] stuckKeys_memory[1*8];
  reg [8-1:0] stuckKeys_stuckKeys_1_result_0;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_1;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_2;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_3;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_4;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_5;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_6;
  reg [8-1:0] stuckKeys_stuckKeys_1_result_7;
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
      stuckKeys_stuckKeys_1_result_4 <= 0;
      stuckKeys_stuckKeys_1_result_5 <= 0;
      stuckKeys_stuckKeys_1_result_6 <= 0;
      stuckKeys_stuckKeys_1_result_7 <= 0;
      stuckKeys_1_finishedAt <= -1;
      stuckKeys_stuckKeys_1_returnCode <= 0;
      stuckKeys_2_finishedAt <= -1;
      stuckKeys_stuckKeys_2_returnCode <= 0;
      stuckKeys_memory[0] <= 2;
      stuckKeys_memory[1] <= 4;
      stuckKeys_memory[2] <= 6;
      stuckKeys_memory[3] <= 8;
      stuckKeys_memory[4] <= 10;
      stuckKeys_memory[5] <= 12;
      stuckKeys_memory[6] <= 14;
      stuckKeys_memory[7] <= 16;
    end
    else if (processCurrent == 4) begin
      case(stuckKeys_pc)
        0: begin
          if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
            stuckKeys_stuckKeys_1_result_0 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+0];
            stuckKeys_stuckKeys_1_result_1 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+1];
            stuckKeys_stuckKeys_1_result_2 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+2];
            stuckKeys_stuckKeys_1_result_3 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+3];
            stuckKeys_stuckKeys_1_result_4 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+4];
            stuckKeys_stuckKeys_1_result_5 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+5];
            stuckKeys_stuckKeys_1_result_6 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+6];
            stuckKeys_stuckKeys_1_result_7 <= stuckKeys_memory[copy_stuckKeys_1_index_36*8+7];
            stuckKeys_1_finishedAt <= step;
          end
          else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+0] <= copy_stuckKeys_2_value_38;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+1] <= copy_stuckKeys_2_value_39;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+2] <= copy_stuckKeys_2_value_40;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+3] <= copy_stuckKeys_2_value_41;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+4] <= copy_stuckKeys_2_value_42;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+5] <= copy_stuckKeys_2_value_43;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+6] <= copy_stuckKeys_2_value_44;
            stuckKeys_memory[copy_stuckKeys_2_index_37*8+7] <= copy_stuckKeys_2_value_45;
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
  reg [8-1:0] stuckData_memory[1*8];
  reg [8-1:0] stuckData_stuckData_3_result_0;
  reg [8-1:0] stuckData_stuckData_3_result_1;
  reg [8-1:0] stuckData_stuckData_3_result_2;
  reg [8-1:0] stuckData_stuckData_3_result_3;
  reg [8-1:0] stuckData_stuckData_3_result_4;
  reg [8-1:0] stuckData_stuckData_3_result_5;
  reg [8-1:0] stuckData_stuckData_3_result_6;
  reg [8-1:0] stuckData_stuckData_3_result_7;
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
      stuckData_stuckData_3_result_4 <= 0;
      stuckData_stuckData_3_result_5 <= 0;
      stuckData_stuckData_3_result_6 <= 0;
      stuckData_stuckData_3_result_7 <= 0;
      stuckData_3_finishedAt <= -1;
      stuckData_stuckData_3_returnCode <= 0;
      stuckData_4_finishedAt <= -1;
      stuckData_stuckData_4_returnCode <= 0;
      stuckData_memory[0] <= 3;
      stuckData_memory[1] <= 5;
      stuckData_memory[2] <= 7;
      stuckData_memory[3] <= 9;
      stuckData_memory[4] <= 11;
      stuckData_memory[5] <= 13;
      stuckData_memory[6] <= 15;
      stuckData_memory[7] <= 17;
    end
    else if (processCurrent == 5) begin
      case(stuckData_pc)
        0: begin
          if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
            stuckData_stuckData_3_result_0 <= stuckData_memory[copy_stuckData_3_index_46*8+0];
            stuckData_stuckData_3_result_1 <= stuckData_memory[copy_stuckData_3_index_46*8+1];
            stuckData_stuckData_3_result_2 <= stuckData_memory[copy_stuckData_3_index_46*8+2];
            stuckData_stuckData_3_result_3 <= stuckData_memory[copy_stuckData_3_index_46*8+3];
            stuckData_stuckData_3_result_4 <= stuckData_memory[copy_stuckData_3_index_46*8+4];
            stuckData_stuckData_3_result_5 <= stuckData_memory[copy_stuckData_3_index_46*8+5];
            stuckData_stuckData_3_result_6 <= stuckData_memory[copy_stuckData_3_index_46*8+6];
            stuckData_stuckData_3_result_7 <= stuckData_memory[copy_stuckData_3_index_46*8+7];
            stuckData_3_finishedAt <= step;
          end
          else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
            stuckData_memory[copy_stuckData_4_index_47*8+0] <= copy_stuckData_4_value_48;
            stuckData_memory[copy_stuckData_4_index_47*8+1] <= copy_stuckData_4_value_49;
            stuckData_memory[copy_stuckData_4_index_47*8+2] <= copy_stuckData_4_value_50;
            stuckData_memory[copy_stuckData_4_index_47*8+3] <= copy_stuckData_4_value_51;
            stuckData_memory[copy_stuckData_4_index_47*8+4] <= copy_stuckData_4_value_52;
            stuckData_memory[copy_stuckData_4_index_47*8+5] <= copy_stuckData_4_value_53;
            stuckData_memory[copy_stuckData_4_index_47*8+6] <= copy_stuckData_4_value_54;
            stuckData_memory[copy_stuckData_4_index_47*8+7] <= copy_stuckData_4_value_55;
            stuckData_4_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckData_stop <= 1;
      endcase
    end
  end
  // Process: copy  process_copy_0006
  reg [1-1:0] copy_index_0;
  reg [4-1:0] copy_size_1;
  reg [1-1:0] copy_isLeaf_2;
  reg [1-1:0] copy_nextFree_3;
  reg [8-1:0] copy_Key_0_4;
  reg [1-1:0] copy_KeyCompares_0_5;
  reg [4-1:0] copy_KeyCollapse_0_6;
  reg [8-1:0] copy_Data_0_7;
  reg [8-1:0] copy_Key_1_8;
  reg [1-1:0] copy_KeyCompares_1_9;
  reg [4-1:0] copy_KeyCollapse_1_10;
  reg [8-1:0] copy_Data_1_11;
  reg [8-1:0] copy_Key_2_12;
  reg [1-1:0] copy_KeyCompares_2_13;
  reg [4-1:0] copy_KeyCollapse_2_14;
  reg [8-1:0] copy_Data_2_15;
  reg [8-1:0] copy_Key_3_16;
  reg [1-1:0] copy_KeyCompares_3_17;
  reg [4-1:0] copy_KeyCollapse_3_18;
  reg [8-1:0] copy_Data_3_19;
  reg [8-1:0] copy_Key_4_20;
  reg [1-1:0] copy_KeyCompares_4_21;
  reg [4-1:0] copy_KeyCollapse_4_22;
  reg [8-1:0] copy_Data_4_23;
  reg [8-1:0] copy_Key_5_24;
  reg [1-1:0] copy_KeyCompares_5_25;
  reg [4-1:0] copy_KeyCollapse_5_26;
  reg [8-1:0] copy_Data_5_27;
  reg [8-1:0] copy_Key_6_28;
  reg [1-1:0] copy_KeyCompares_6_29;
  reg [4-1:0] copy_KeyCollapse_6_30;
  reg [8-1:0] copy_Data_6_31;
  reg [8-1:0] copy_Key_7_32;
  reg [1-1:0] copy_KeyCompares_7_33;
  reg [4-1:0] copy_KeyCollapse_7_34;
  reg [8-1:0] copy_Data_7_35;
  reg [0-1:0] copy_stuckKeys_1_index_36;
  reg [0-1:0] copy_stuckKeys_2_index_37;
  reg [8-1:0] copy_stuckKeys_2_value_38;
  reg [8-1:0] copy_stuckKeys_2_value_39;
  reg [8-1:0] copy_stuckKeys_2_value_40;
  reg [8-1:0] copy_stuckKeys_2_value_41;
  reg [8-1:0] copy_stuckKeys_2_value_42;
  reg [8-1:0] copy_stuckKeys_2_value_43;
  reg [8-1:0] copy_stuckKeys_2_value_44;
  reg [8-1:0] copy_stuckKeys_2_value_45;
  reg [0-1:0] copy_stuckData_3_index_46;
  reg [0-1:0] copy_stuckData_4_index_47;
  reg [8-1:0] copy_stuckData_4_value_48;
  reg [8-1:0] copy_stuckData_4_value_49;
  reg [8-1:0] copy_stuckData_4_value_50;
  reg [8-1:0] copy_stuckData_4_value_51;
  reg [8-1:0] copy_stuckData_4_value_52;
  reg [8-1:0] copy_stuckData_4_value_53;
  reg [8-1:0] copy_stuckData_4_value_54;
  reg [8-1:0] copy_stuckData_4_value_55;
  reg [0-1:0] copy_stuckSize_5_index_56;
  reg [0-1:0] copy_stuckSize_6_index_57;
  reg [4-1:0] copy_stuckSize_6_value_58;
  reg [0-1:0] copy_stuckIsLeaf_7_index_59;
  reg [0-1:0] copy_stuckIsLeaf_8_index_60;
  reg [1-1:0] copy_stuckIsLeaf_8_value_61;
  reg [1-1:0] copy_Found_62;
  reg [8-1:0] copy_Key_63;
  reg [8-1:0] copy_FoundKey_64;
  reg [8-1:0] copy_Data_65;
  reg [1-1:0] copy_BtreeIndex_66;
  reg [4-1:0] copy_StuckIndex_67;
  reg [1-1:0] copy_MergeSuccess_68;
  reg [1-1:0] copy_index_69;
  reg [4-1:0] copy_size_70;
  reg [1-1:0] copy_isLeaf_71;
  reg [1-1:0] copy_nextFree_72;
  reg [8-1:0] copy_Key_0_73;
  reg [1-1:0] copy_KeyCompares_0_74;
  reg [4-1:0] copy_KeyCollapse_0_75;
  reg [8-1:0] copy_Data_0_76;
  reg [8-1:0] copy_Key_1_77;
  reg [1-1:0] copy_KeyCompares_1_78;
  reg [4-1:0] copy_KeyCollapse_1_79;
  reg [8-1:0] copy_Data_1_80;
  reg [8-1:0] copy_Key_2_81;
  reg [1-1:0] copy_KeyCompares_2_82;
  reg [4-1:0] copy_KeyCollapse_2_83;
  reg [8-1:0] copy_Data_2_84;
  reg [8-1:0] copy_Key_3_85;
  reg [1-1:0] copy_KeyCompares_3_86;
  reg [4-1:0] copy_KeyCollapse_3_87;
  reg [8-1:0] copy_Data_3_88;
  reg [8-1:0] copy_Key_4_89;
  reg [1-1:0] copy_KeyCompares_4_90;
  reg [4-1:0] copy_KeyCollapse_4_91;
  reg [8-1:0] copy_Data_4_92;
  reg [8-1:0] copy_Key_5_93;
  reg [1-1:0] copy_KeyCompares_5_94;
  reg [4-1:0] copy_KeyCollapse_5_95;
  reg [8-1:0] copy_Data_5_96;
  reg [8-1:0] copy_Key_6_97;
  reg [1-1:0] copy_KeyCompares_6_98;
  reg [4-1:0] copy_KeyCollapse_6_99;
  reg [8-1:0] copy_Data_6_100;
  reg [8-1:0] copy_Key_7_101;
  reg [1-1:0] copy_KeyCompares_7_102;
  reg [4-1:0] copy_KeyCollapse_7_103;
  reg [8-1:0] copy_Data_7_104;
  reg [1-1:0] copy_Found_105;
  reg [8-1:0] copy_Key_106;
  reg [8-1:0] copy_FoundKey_107;
  reg [8-1:0] copy_Data_108;
  reg [1-1:0] copy_BtreeIndex_109;
  reg [4-1:0] copy_StuckIndex_110;
  reg [1-1:0] copy_MergeSuccess_111;
  reg [4-1:0] copy_delta_112;
  integer copy_pc;
  integer copy_stop;
  integer copy_returnCode;
  integer copy_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      copy_pc <= 0;
      copy_stop <= 0;
      copy_returnCode <= 0;
      copy_index_0 <= 0;
      copy_size_1 <= 0;
      copy_isLeaf_2 <= 0;
      copy_nextFree_3 <= 0;
      copy_Key_0_4 <= 0;
      copy_KeyCompares_0_5 <= 0;
      copy_KeyCollapse_0_6 <= 0;
      copy_Data_0_7 <= 0;
      copy_Key_1_8 <= 0;
      copy_KeyCompares_1_9 <= 0;
      copy_KeyCollapse_1_10 <= 0;
      copy_Data_1_11 <= 0;
      copy_Key_2_12 <= 0;
      copy_KeyCompares_2_13 <= 0;
      copy_KeyCollapse_2_14 <= 0;
      copy_Data_2_15 <= 0;
      copy_Key_3_16 <= 0;
      copy_KeyCompares_3_17 <= 0;
      copy_KeyCollapse_3_18 <= 0;
      copy_Data_3_19 <= 0;
      copy_Key_4_20 <= 0;
      copy_KeyCompares_4_21 <= 0;
      copy_KeyCollapse_4_22 <= 0;
      copy_Data_4_23 <= 0;
      copy_Key_5_24 <= 0;
      copy_KeyCompares_5_25 <= 0;
      copy_KeyCollapse_5_26 <= 0;
      copy_Data_5_27 <= 0;
      copy_Key_6_28 <= 0;
      copy_KeyCompares_6_29 <= 0;
      copy_KeyCollapse_6_30 <= 0;
      copy_Data_6_31 <= 0;
      copy_Key_7_32 <= 0;
      copy_KeyCompares_7_33 <= 0;
      copy_KeyCollapse_7_34 <= 0;
      copy_Data_7_35 <= 0;
      copy_stuckKeys_1_index_36 <= 0;
      copy_stuckKeys_2_index_37 <= 0;
      copy_stuckKeys_2_value_38 <= 0;
      copy_stuckKeys_2_value_39 <= 0;
      copy_stuckKeys_2_value_40 <= 0;
      copy_stuckKeys_2_value_41 <= 0;
      copy_stuckKeys_2_value_42 <= 0;
      copy_stuckKeys_2_value_43 <= 0;
      copy_stuckKeys_2_value_44 <= 0;
      copy_stuckKeys_2_value_45 <= 0;
      copy_stuckData_3_index_46 <= 0;
      copy_stuckData_4_index_47 <= 0;
      copy_stuckData_4_value_48 <= 0;
      copy_stuckData_4_value_49 <= 0;
      copy_stuckData_4_value_50 <= 0;
      copy_stuckData_4_value_51 <= 0;
      copy_stuckData_4_value_52 <= 0;
      copy_stuckData_4_value_53 <= 0;
      copy_stuckData_4_value_54 <= 0;
      copy_stuckData_4_value_55 <= 0;
      copy_stuckSize_5_index_56 <= 0;
      copy_stuckSize_6_index_57 <= 0;
      copy_stuckSize_6_value_58 <= 0;
      copy_stuckIsLeaf_7_index_59 <= 0;
      copy_stuckIsLeaf_8_index_60 <= 0;
      copy_stuckIsLeaf_8_value_61 <= 0;
      copy_Found_62 <= 0;
      copy_Key_63 <= 0;
      copy_FoundKey_64 <= 0;
      copy_Data_65 <= 0;
      copy_BtreeIndex_66 <= 0;
      copy_StuckIndex_67 <= 0;
      copy_MergeSuccess_68 <= 0;
      copy_index_69 <= 0;
      copy_size_70 <= 0;
      copy_isLeaf_71 <= 0;
      copy_nextFree_72 <= 0;
      copy_Key_0_73 <= 0;
      copy_KeyCompares_0_74 <= 0;
      copy_KeyCollapse_0_75 <= 0;
      copy_Data_0_76 <= 0;
      copy_Key_1_77 <= 0;
      copy_KeyCompares_1_78 <= 0;
      copy_KeyCollapse_1_79 <= 0;
      copy_Data_1_80 <= 0;
      copy_Key_2_81 <= 0;
      copy_KeyCompares_2_82 <= 0;
      copy_KeyCollapse_2_83 <= 0;
      copy_Data_2_84 <= 0;
      copy_Key_3_85 <= 0;
      copy_KeyCompares_3_86 <= 0;
      copy_KeyCollapse_3_87 <= 0;
      copy_Data_3_88 <= 0;
      copy_Key_4_89 <= 0;
      copy_KeyCompares_4_90 <= 0;
      copy_KeyCollapse_4_91 <= 0;
      copy_Data_4_92 <= 0;
      copy_Key_5_93 <= 0;
      copy_KeyCompares_5_94 <= 0;
      copy_KeyCollapse_5_95 <= 0;
      copy_Data_5_96 <= 0;
      copy_Key_6_97 <= 0;
      copy_KeyCompares_6_98 <= 0;
      copy_KeyCollapse_6_99 <= 0;
      copy_Data_6_100 <= 0;
      copy_Key_7_101 <= 0;
      copy_KeyCompares_7_102 <= 0;
      copy_KeyCollapse_7_103 <= 0;
      copy_Data_7_104 <= 0;
      copy_Found_105 <= 0;
      copy_Key_106 <= 0;
      copy_FoundKey_107 <= 0;
      copy_Data_108 <= 0;
      copy_BtreeIndex_109 <= 0;
      copy_StuckIndex_110 <= 0;
      copy_MergeSuccess_111 <= 0;
      copy_delta_112 <= 0;
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
      case(copy_pc)
        0: begin
          copy_delta_112 <= 3;
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0676:<init>|  Chip.java:0675:RegisterSet|  Btree.java:2964:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        1: begin
          copy_index_0 <= 0;
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Chip.java:0819:<init>|  Chip.java:0818:Zero|  Btree.java:0266:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        2: begin
          copy_stuckSize_5_index_56 <= copy_index_0;
          stuckSize_5_requestedAt <= step;
          stuckSize_5_finishedAt <= -1;
          copy_stuckIsLeaf_7_index_59 <= copy_index_0;
          stuckIsLeaf_7_requestedAt <= step;
          stuckIsLeaf_7_finishedAt <= -1;
          copy_stuckKeys_1_index_36 <= copy_index_0;
          stuckKeys_1_requestedAt <= step;
          stuckKeys_1_finishedAt <= -1;
          copy_stuckData_3_index_46 <= copy_index_0;
          stuckData_3_requestedAt <= step;
          stuckData_3_finishedAt <= -1;
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0272:<init>|  Btree.java:0271:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        3: begin
          if ((stuckSize_5_requestedAt < stuckSize_5_finishedAt)) begin
            copy_pc <= copy_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1334:<init>|  Chip.java:1333:waitResultOfTransaction|  Btree.java:0285:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        4: begin
          if ((stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt)) begin
            copy_pc <= copy_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1334:<init>|  Chip.java:1333:waitResultOfTransaction|  Btree.java:0286:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        5: begin
          if ((stuckKeys_1_requestedAt < stuckKeys_1_finishedAt)) begin
            copy_pc <= copy_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1334:<init>|  Chip.java:1333:waitResultOfTransaction|  Btree.java:0288:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        6: begin
          if ((stuckData_3_requestedAt < stuckData_3_finishedAt)) begin
            copy_pc <= copy_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1334:<init>|  Chip.java:1333:waitResultOfTransaction|  Btree.java:0289:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        7: begin
          copy_size_1 <= stuckSize_stuckSize_5_result_0;
          copy_isLeaf_2 <= stuckIsLeaf_stuckIsLeaf_7_result_0;
          copy_Key_0_4 <= stuckKeys_stuckKeys_1_result_0;
          copy_Data_0_7 <= stuckData_stuckData_3_result_0;
          copy_Key_1_8 <= stuckKeys_stuckKeys_1_result_1;
          copy_Data_1_11 <= stuckData_stuckData_3_result_1;
          copy_Key_2_12 <= stuckKeys_stuckKeys_1_result_2;
          copy_Data_2_15 <= stuckData_stuckData_3_result_2;
          copy_Key_3_16 <= stuckKeys_stuckKeys_1_result_3;
          copy_Data_3_19 <= stuckData_stuckData_3_result_3;
          copy_Key_4_20 <= stuckKeys_stuckKeys_1_result_4;
          copy_Data_4_23 <= stuckData_stuckData_3_result_4;
          copy_Key_5_24 <= stuckKeys_stuckKeys_1_result_5;
          copy_Data_5_27 <= stuckData_stuckData_3_result_5;
          copy_Key_6_28 <= stuckKeys_stuckKeys_1_result_6;
          copy_Data_6_31 <= stuckData_stuckData_3_result_6;
          copy_Key_7_32 <= stuckKeys_stuckKeys_1_result_7;
          copy_Data_7_35 <= stuckData_stuckData_3_result_7;
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0292:<init>|  Btree.java:0291:stuckGet|  Btree.java:0267:stuckGetRoot|  Btree.java:2965:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        8: begin
          copy_Key_0_73 <= copy_Key_0_4;
          copy_Data_0_76 <= copy_Data_0_7;
          copy_Key_1_77 <= copy_Key_1_8;
          copy_Data_1_80 <= copy_Data_1_11;
          copy_Key_2_81 <= copy_Key_2_12;
          copy_Data_2_84 <= copy_Data_2_15;
          copy_Key_3_85 <= copy_Key_3_16;
          copy_Data_3_88 <= copy_Data_3_19;
          copy_Key_4_89 <= copy_Key_4_20;
          copy_Data_4_92 <= copy_Data_4_23;
          copy_Key_5_93 <= copy_Key_5_24;
          copy_Data_5_96 <= copy_Data_5_27;
          copy_Key_6_97 <= copy_Key_6_28;
          copy_Data_6_100 <= copy_Data_6_31;
          copy_Key_7_101 <= copy_Key_7_32;
          copy_Data_7_104 <= copy_Data_7_35;
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0400:<init>|  Btree.java:0399:Copy|  Btree.java:2966:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        9: begin
          if (copy_delta_112 & 4) begin
            copy_Key_7_101 <= copy_Key_3_85;
            copy_Data_7_104 <= copy_Data_3_88;
            copy_Key_6_97 <= copy_Key_2_81;
            copy_Data_6_100 <= copy_Data_2_84;
            copy_Key_5_93 <= copy_Key_1_77;
            copy_Data_5_96 <= copy_Data_1_80;
            copy_Key_4_89 <= copy_Key_0_73;
            copy_Data_4_92 <= copy_Data_0_76;
            copy_Key_0_73 <= 0;
            copy_Data_0_76 <= 0;
            copy_Key_1_77 <= 0;
            copy_Data_1_80 <= 0;
            copy_Key_2_81 <= 0;
            copy_Data_2_84 <= 0;
            copy_Key_3_85 <= 0;
            copy_Data_3_88 <= 0;
          end
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0515:<init>|  Btree.java:0514:CopyUp|  Btree.java:2967:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        10: begin
          if (copy_delta_112 & 2) begin
            copy_Key_7_101 <= copy_Key_5_93;
            copy_Data_7_104 <= copy_Data_5_96;
            copy_Key_6_97 <= copy_Key_4_89;
            copy_Data_6_100 <= copy_Data_4_92;
            copy_Key_5_93 <= copy_Key_3_85;
            copy_Data_5_96 <= copy_Data_3_88;
            copy_Key_4_89 <= copy_Key_2_81;
            copy_Data_4_92 <= copy_Data_2_84;
            copy_Key_3_85 <= copy_Key_1_77;
            copy_Data_3_88 <= copy_Data_1_80;
            copy_Key_2_81 <= copy_Key_0_73;
            copy_Data_2_84 <= copy_Data_0_76;
            copy_Key_0_73 <= 0;
            copy_Data_0_76 <= 0;
            copy_Key_1_77 <= 0;
            copy_Data_1_80 <= 0;
          end
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0515:<init>|  Btree.java:0514:CopyUp|  Btree.java:2967:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        11: begin
          if (copy_delta_112 & 1) begin
            copy_Key_7_101 <= copy_Key_6_97;
            copy_Data_7_104 <= copy_Data_6_100;
            copy_Key_6_97 <= copy_Key_5_93;
            copy_Data_6_100 <= copy_Data_5_96;
            copy_Key_5_93 <= copy_Key_4_89;
            copy_Data_5_96 <= copy_Data_4_92;
            copy_Key_4_89 <= copy_Key_3_85;
            copy_Data_4_92 <= copy_Data_3_88;
            copy_Key_3_85 <= copy_Key_2_81;
            copy_Data_3_88 <= copy_Data_2_84;
            copy_Key_2_81 <= copy_Key_1_77;
            copy_Data_2_84 <= copy_Data_1_80;
            copy_Key_1_77 <= copy_Key_0_73;
            copy_Data_1_80 <= copy_Data_0_76;
            copy_Key_0_73 <= 0;
            copy_Data_0_76 <= 0;
          end
          copy_pc <= copy_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0512:<init>|  Btree.java:0515:<init>|  Btree.java:0514:CopyUp|  Btree.java:2967:test_copy|  Btree.java:7224:newTests|  Btree.java:7229:main|");
            $fclose(f);
          end
        end
        default: copy_stop <= 1;
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
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckIsLeaf_7", stuckIsLeaf_7_requestedAt, stuckIsLeaf_7_finishedAt, stuckIsLeaf_stuckIsLeaf_7_returnCode, (stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step), (stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckIsLeaf_7_index_59", copy_stuckIsLeaf_7_index_59);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_8", stuckIsLeaf_8_requestedAt, stuckIsLeaf_8_finishedAt, stuckIsLeaf_stuckIsLeaf_8_returnCode, (stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step), (stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckIsLeaf_8_index_60", copy_stuckIsLeaf_8_index_60);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckIsLeaf_8_value_61", copy_stuckIsLeaf_8_value_61);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsFree", 1, stuckIsFree_pc, stuckIsFree_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 1, 1);
      $fwrite(o, "        %2d", stuckIsFree_memory[0]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 2, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 1, 1);
      $fwrite(o, "        %2d", freeNext_memory[0]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 4, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckSize_5", stuckSize_5_requestedAt, stuckSize_5_finishedAt, stuckSize_stuckSize_5_returnCode, (stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step), (stuckSize_5_requestedAt < stuckSize_5_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckSize_5_index_56", copy_stuckSize_5_index_56);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckSize_6", stuckSize_6_requestedAt, stuckSize_6_finishedAt, stuckSize_stuckSize_6_returnCode, (stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step), (stuckSize_6_requestedAt < stuckSize_6_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckSize_6_index_57", copy_stuckSize_6_index_57);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckSize_6_value_58", copy_stuckSize_6_value_58);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckKeys", 1, stuckKeys_pc, stuckKeys_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 8, 8);
      $fwrite(o, "        %2d", stuckKeys_memory[0]);
      $fwrite(o, ", %2d", stuckKeys_memory[1]);
      $fwrite(o, ", %2d", stuckKeys_memory[2]);
      $fwrite(o, ", %2d", stuckKeys_memory[3]);
      $fwrite(o, ", %2d", stuckKeys_memory[4]);
      $fwrite(o, ", %2d", stuckKeys_memory[5]);
      $fwrite(o, ", %2d", stuckKeys_memory[6]);
      $fwrite(o, ", %2d", stuckKeys_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_4", stuckKeys_stuckKeys_1_result_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_5", stuckKeys_stuckKeys_1_result_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_6", stuckKeys_stuckKeys_1_result_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_7", stuckKeys_stuckKeys_1_result_7);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_1_index_36", copy_stuckKeys_1_index_36);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_4", stuckKeys_stuckKeys_1_result_4);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_5", stuckKeys_stuckKeys_1_result_5);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_6", stuckKeys_stuckKeys_1_result_6);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_7", stuckKeys_stuckKeys_1_result_7);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_index_37", copy_stuckKeys_2_index_37);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_38", copy_stuckKeys_2_value_38);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_39", copy_stuckKeys_2_value_39);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_40", copy_stuckKeys_2_value_40);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_41", copy_stuckKeys_2_value_41);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_42", copy_stuckKeys_2_value_42);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_43", copy_stuckKeys_2_value_43);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_44", copy_stuckKeys_2_value_44);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckKeys_2_value_45", copy_stuckKeys_2_value_45);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 5, "stuckData", 1, stuckData_pc, stuckData_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 8, 8);
      $fwrite(o, "        %2d", stuckData_memory[0]);
      $fwrite(o, ", %2d", stuckData_memory[1]);
      $fwrite(o, ", %2d", stuckData_memory[2]);
      $fwrite(o, ", %2d", stuckData_memory[3]);
      $fwrite(o, ", %2d", stuckData_memory[4]);
      $fwrite(o, ", %2d", stuckData_memory[5]);
      $fwrite(o, ", %2d", stuckData_memory[6]);
      $fwrite(o, ", %2d", stuckData_memory[7]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_4", stuckData_stuckData_3_result_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_5", stuckData_stuckData_3_result_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_6", stuckData_stuckData_3_result_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_7", stuckData_stuckData_3_result_7);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_3_index_46", copy_stuckData_3_index_46);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_4", stuckData_stuckData_3_result_4);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_5", stuckData_stuckData_3_result_5);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_6", stuckData_stuckData_3_result_6);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_7", stuckData_stuckData_3_result_7);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_index_47", copy_stuckData_4_index_47);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_48", copy_stuckData_4_value_48);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_49", copy_stuckData_4_value_49);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_50", copy_stuckData_4_value_50);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_51", copy_stuckData_4_value_51);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_52", copy_stuckData_4_value_52);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_53", copy_stuckData_4_value_53);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_54", copy_stuckData_4_value_54);

      $fwrite(o, "            %-38s = %1d\n", "copy_stuckData_4_value_55", copy_stuckData_4_value_55);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "copy", 12, copy_pc, copy_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_index_0", copy_index_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_size_1", copy_size_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_isLeaf_2", copy_isLeaf_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_nextFree_3", copy_nextFree_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_0_4", copy_Key_0_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_0_5", copy_KeyCompares_0_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_0_6", copy_KeyCollapse_0_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_0_7", copy_Data_0_7);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_1_8", copy_Key_1_8);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_1_9", copy_KeyCompares_1_9);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_1_10", copy_KeyCollapse_1_10);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_1_11", copy_Data_1_11);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_2_12", copy_Key_2_12);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_2_13", copy_KeyCompares_2_13);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_2_14", copy_KeyCollapse_2_14);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_2_15", copy_Data_2_15);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_3_16", copy_Key_3_16);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_3_17", copy_KeyCompares_3_17);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_3_18", copy_KeyCollapse_3_18);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_3_19", copy_Data_3_19);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_4_20", copy_Key_4_20);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_4_21", copy_KeyCompares_4_21);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_4_22", copy_KeyCollapse_4_22);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_4_23", copy_Data_4_23);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_5_24", copy_Key_5_24);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_5_25", copy_KeyCompares_5_25);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_5_26", copy_KeyCollapse_5_26);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_5_27", copy_Data_5_27);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_6_28", copy_Key_6_28);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_6_29", copy_KeyCompares_6_29);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_6_30", copy_KeyCollapse_6_30);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_6_31", copy_Data_6_31);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_7_32", copy_Key_7_32);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_7_33", copy_KeyCompares_7_33);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_7_34", copy_KeyCollapse_7_34);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_7_35", copy_Data_7_35);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_1_index_36", copy_stuckKeys_1_index_36);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_index_37", copy_stuckKeys_2_index_37);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_38", copy_stuckKeys_2_value_38);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_39", copy_stuckKeys_2_value_39);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_40", copy_stuckKeys_2_value_40);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_41", copy_stuckKeys_2_value_41);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_42", copy_stuckKeys_2_value_42);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_43", copy_stuckKeys_2_value_43);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_44", copy_stuckKeys_2_value_44);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckKeys_2_value_45", copy_stuckKeys_2_value_45);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_3_index_46", copy_stuckData_3_index_46);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_index_47", copy_stuckData_4_index_47);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_48", copy_stuckData_4_value_48);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_49", copy_stuckData_4_value_49);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_50", copy_stuckData_4_value_50);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_51", copy_stuckData_4_value_51);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_52", copy_stuckData_4_value_52);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_53", copy_stuckData_4_value_53);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_54", copy_stuckData_4_value_54);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckData_4_value_55", copy_stuckData_4_value_55);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckSize_5_index_56", copy_stuckSize_5_index_56);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckSize_6_index_57", copy_stuckSize_6_index_57);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckSize_6_value_58", copy_stuckSize_6_value_58);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckIsLeaf_7_index_59", copy_stuckIsLeaf_7_index_59);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckIsLeaf_8_index_60", copy_stuckIsLeaf_8_index_60);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_stuckIsLeaf_8_value_61", copy_stuckIsLeaf_8_value_61);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Found_62", copy_Found_62);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_63", copy_Key_63);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_FoundKey_64", copy_FoundKey_64);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_65", copy_Data_65);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_BtreeIndex_66", copy_BtreeIndex_66);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_StuckIndex_67", copy_StuckIndex_67);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_MergeSuccess_68", copy_MergeSuccess_68);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_index_69", copy_index_69);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_size_70", copy_size_70);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_isLeaf_71", copy_isLeaf_71);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_nextFree_72", copy_nextFree_72);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_0_73", copy_Key_0_73);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_0_74", copy_KeyCompares_0_74);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_0_75", copy_KeyCollapse_0_75);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_0_76", copy_Data_0_76);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_1_77", copy_Key_1_77);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_1_78", copy_KeyCompares_1_78);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_1_79", copy_KeyCollapse_1_79);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_1_80", copy_Data_1_80);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_2_81", copy_Key_2_81);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_2_82", copy_KeyCompares_2_82);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_2_83", copy_KeyCollapse_2_83);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_2_84", copy_Data_2_84);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_3_85", copy_Key_3_85);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_3_86", copy_KeyCompares_3_86);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_3_87", copy_KeyCollapse_3_87);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_3_88", copy_Data_3_88);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_4_89", copy_Key_4_89);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_4_90", copy_KeyCompares_4_90);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_4_91", copy_KeyCollapse_4_91);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_4_92", copy_Data_4_92);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_5_93", copy_Key_5_93);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_5_94", copy_KeyCompares_5_94);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_5_95", copy_KeyCollapse_5_95);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_5_96", copy_Data_5_96);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_6_97", copy_Key_6_97);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_6_98", copy_KeyCompares_6_98);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_6_99", copy_KeyCollapse_6_99);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_6_100", copy_Data_6_100);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_7_101", copy_Key_7_101);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCompares_7_102", copy_KeyCompares_7_102);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_KeyCollapse_7_103", copy_KeyCollapse_7_103);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_7_104", copy_Data_7_104);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Found_105", copy_Found_105);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Key_106", copy_Key_106);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_FoundKey_107", copy_FoundKey_107);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_Data_108", copy_Data_108);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_BtreeIndex_109", copy_BtreeIndex_109);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_StuckIndex_110", copy_StuckIndex_110);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_MergeSuccess_111", copy_MergeSuccess_111);
      $fwrite(o, "        Register: %-32s = %1d\n",  "copy_delta_112", copy_delta_112);
      $fclose(o);
    end
  endtask
endmodule
