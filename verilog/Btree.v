//-----------------------------------------------------------------------------
// Database on a chip test bench
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Btree;                                                                      // Test bench for database on a chip
  reg                    stop;                                                  // Program has stopped when this goes high
  reg                   clock;                                                  // Clock
  integer               reset;                                                  // Reset chip to known state
  integer                step;                                                  // Step of the simulation
  integer            maxSteps;                                                  // Maximum number of steps to execute
  integer          returnCode;                                                  // Return code
  integer      processCurrent;                                                  // To ensure we get the same results in Java and Verilog we have to run the processes single threaded in a constant order

  assign stop = main_stop||stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 100;
    reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1                      // Reset to known state
    for(step = -65; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_main_0000
      processCurrent = 1; clock = 0; #1; clock = 1; #1; // process_stuckIsLeaf_0001
      processCurrent = 2; clock = 0; #1; clock = 1; #1; // process_stuckIsFree_0002
      processCurrent = 3; clock = 0; #1; clock = 1; #1; // process_freeNext_0003
      processCurrent = 4; clock = 0; #1; clock = 1; #1; // process_stuckSize_0004
      processCurrent = 5; clock = 0; #1; clock = 1; #1; // process_stuckKeys_0005
      processCurrent = 6; clock = 0; #1; clock = 1; #1; // process_stuckData_0006
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: main  process_main_0000
  reg [4-1:0] main_index_0;
  reg [4-1:0] main_size_1;
  reg [1-1:0] main_isLeaf_2;
  reg [4-1:0] main_nextFree_3;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_4[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_5[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_6[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_7[8];
  reg [3-1:0] main_stuckKeys_1_index_8;
  reg [3-1:0] main_stuckKeys_2_index_9;
  (* nomem2reg *)
  reg [8-1:0] main_stuckKeys_2_value_10[8];
  reg [3-1:0] main_stuckData_3_index_11;
  reg [3-1:0] main_stuckData_4_index_12;
  (* nomem2reg *)
  reg [8-1:0] main_stuckData_4_value_13[8];
  reg [3-1:0] main_stuckSize_5_index_14;
  reg [3-1:0] main_stuckSize_6_index_15;
  (* nomem2reg *)
  reg [4-1:0] main_stuckSize_6_value_16[1];
  reg [3-1:0] main_stuckIsLeaf_7_index_17;
  reg [3-1:0] main_stuckIsLeaf_8_index_18;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsLeaf_8_value_19[1];
  reg [1-1:0] main_Found_20;
  reg [8-1:0] main_Key_21;
  reg [8-1:0] main_FoundKey_22;
  reg [8-1:0] main_Data_23;
  reg [4-1:0] main_BtreeIndex_24;
  reg [4-1:0] main_StuckIndex_25;
  reg [1-1:0] main_MergeSuccess_26;
  reg [4-1:0] main_index_27;
  reg [4-1:0] main_size_28;
  reg [1-1:0] main_isLeaf_29;
  reg [4-1:0] main_nextFree_30;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_31[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_32[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_33[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_34[8];
  reg [1-1:0] main_Found_35;
  reg [8-1:0] main_Key_36;
  reg [8-1:0] main_FoundKey_37;
  reg [8-1:0] main_Data_38;
  reg [4-1:0] main_BtreeIndex_39;
  reg [4-1:0] main_StuckIndex_40;
  reg [1-1:0] main_MergeSuccess_41;
  reg [4-1:0] main_index_42;
  reg [4-1:0] main_size_43;
  reg [1-1:0] main_isLeaf_44;
  reg [4-1:0] main_nextFree_45;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_46[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_47[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_48[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_49[8];
  reg [1-1:0] main_Found_50;
  reg [8-1:0] main_Key_51;
  reg [8-1:0] main_FoundKey_52;
  reg [8-1:0] main_Data_53;
  reg [4-1:0] main_BtreeIndex_54;
  reg [4-1:0] main_StuckIndex_55;
  reg [1-1:0] main_MergeSuccess_56;
  reg [4-1:0] main_index_57;
  reg [4-1:0] main_size_58;
  reg [1-1:0] main_isLeaf_59;
  reg [4-1:0] main_nextFree_60;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_61[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_62[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_63[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_64[8];
  reg [1-1:0] main_Found_65;
  reg [8-1:0] main_Key_66;
  reg [8-1:0] main_FoundKey_67;
  reg [8-1:0] main_Data_68;
  reg [4-1:0] main_BtreeIndex_69;
  reg [4-1:0] main_StuckIndex_70;
  reg [1-1:0] main_MergeSuccess_71;
  reg [4-1:0] main_index_72;
  reg [4-1:0] main_size_73;
  reg [1-1:0] main_isLeaf_74;
  reg [4-1:0] main_nextFree_75;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_76[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_77[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_78[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_79[8];
  reg [1-1:0] main_Found_80;
  reg [8-1:0] main_Key_81;
  reg [8-1:0] main_FoundKey_82;
  reg [8-1:0] main_Data_83;
  reg [4-1:0] main_BtreeIndex_84;
  reg [4-1:0] main_StuckIndex_85;
  reg [1-1:0] main_MergeSuccess_86;
  reg [4-1:0] main_index_87;
  reg [4-1:0] main_size_88;
  reg [1-1:0] main_isLeaf_89;
  reg [4-1:0] main_nextFree_90;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_91[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_92[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_93[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_94[8];
  reg [1-1:0] main_Found_95;
  reg [8-1:0] main_Key_96;
  reg [8-1:0] main_FoundKey_97;
  reg [8-1:0] main_Data_98;
  reg [4-1:0] main_BtreeIndex_99;
  reg [4-1:0] main_StuckIndex_100;
  reg [1-1:0] main_MergeSuccess_101;
  reg [4-1:0] main_index_102;
  reg [4-1:0] main_size_103;
  reg [1-1:0] main_isLeaf_104;
  reg [4-1:0] main_nextFree_105;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_106[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_107[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_108[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_109[8];
  reg [1-1:0] main_Found_110;
  reg [8-1:0] main_Key_111;
  reg [8-1:0] main_FoundKey_112;
  reg [8-1:0] main_Data_113;
  reg [4-1:0] main_BtreeIndex_114;
  reg [4-1:0] main_StuckIndex_115;
  reg [1-1:0] main_MergeSuccess_116;
  reg [4-1:0] main_index_117;
  reg [4-1:0] main_size_118;
  reg [1-1:0] main_isLeaf_119;
  reg [4-1:0] main_nextFree_120;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_121[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_122[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_123[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_124[8];
  reg [1-1:0] main_Found_125;
  reg [8-1:0] main_Key_126;
  reg [8-1:0] main_FoundKey_127;
  reg [8-1:0] main_Data_128;
  reg [4-1:0] main_BtreeIndex_129;
  reg [4-1:0] main_StuckIndex_130;
  reg [1-1:0] main_MergeSuccess_131;
  reg [4-1:0] main_index_132;
  reg [4-1:0] main_size_133;
  reg [1-1:0] main_isLeaf_134;
  reg [4-1:0] main_nextFree_135;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_136[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_137[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_138[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_139[8];
  reg [1-1:0] main_Found_140;
  reg [8-1:0] main_Key_141;
  reg [8-1:0] main_FoundKey_142;
  reg [8-1:0] main_Data_143;
  reg [4-1:0] main_BtreeIndex_144;
  reg [4-1:0] main_StuckIndex_145;
  reg [1-1:0] main_MergeSuccess_146;
  reg [5-1:0] main_sum_147;
  reg [1-1:0] main_can_148;
  reg [4-1:0] main_indexLeft_149;
  reg [4-1:0] main_indexRight_150;
  reg [8-1:0] main_midKey_151;
  reg [4-1:0] main_index_152;
  reg [4-1:0] main_size_153;
  reg [1-1:0] main_isLeaf_154;
  reg [4-1:0] main_nextFree_155;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_156[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_157[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_158[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_159[8];
  reg [1-1:0] main_Found_160;
  reg [8-1:0] main_Key_161;
  reg [8-1:0] main_FoundKey_162;
  reg [8-1:0] main_Data_163;
  reg [4-1:0] main_BtreeIndex_164;
  reg [4-1:0] main_StuckIndex_165;
  reg [1-1:0] main_MergeSuccess_166;
  reg [8-1:0] main_d_167;
  reg [4-1:0] main_n_168;
  reg [4-1:0] main_index_169;
  reg [4-1:0] main_size_170;
  reg [1-1:0] main_isLeaf_171;
  reg [4-1:0] main_nextFree_172;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_173[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_174[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_175[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_176[8];
  reg [1-1:0] main_Found_177;
  reg [8-1:0] main_Key_178;
  reg [8-1:0] main_FoundKey_179;
  reg [8-1:0] main_Data_180;
  reg [4-1:0] main_BtreeIndex_181;
  reg [4-1:0] main_StuckIndex_182;
  reg [1-1:0] main_MergeSuccess_183;
  reg [8-1:0] main_d_184;
  reg [4-1:0] main_n_185;
  reg [4-1:0] main_index_186;
  reg [4-1:0] main_size_187;
  reg [1-1:0] main_isLeaf_188;
  reg [4-1:0] main_nextFree_189;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_190[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_191[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_192[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_193[8];
  reg [1-1:0] main_Found_194;
  reg [8-1:0] main_Key_195;
  reg [8-1:0] main_FoundKey_196;
  reg [8-1:0] main_Data_197;
  reg [4-1:0] main_BtreeIndex_198;
  reg [4-1:0] main_StuckIndex_199;
  reg [1-1:0] main_MergeSuccess_200;
  reg [8-1:0] main_d_201;
  reg [4-1:0] main_n_202;
  reg [4-1:0] main_index_203;
  reg [4-1:0] main_size_204;
  reg [1-1:0] main_isLeaf_205;
  reg [4-1:0] main_nextFree_206;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_207[8];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_208[8];
  (* nomem2reg *)
  reg [4-1:0] main_Collapse_209[8];
  (* nomem2reg *)
  reg [8-1:0] main_Data_210[8];
  reg [1-1:0] main_Found_211;
  reg [8-1:0] main_Key_212;
  reg [8-1:0] main_FoundKey_213;
  reg [8-1:0] main_Data_214;
  reg [4-1:0] main_BtreeIndex_215;
  reg [4-1:0] main_StuckIndex_216;
  reg [1-1:0] main_MergeSuccess_217;
  integer main_pc;
  integer main_stop;
  integer main_returnCode;
  integer main_memory_index;
  reg[0-1:0] main_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      main_pc <= 0;
      main_stop <= 0;
      main_returnCode <= 0;
      main_memory_index <= 0;
      main_memory_value <= 0;
      main_index_0 <= 0;
      main_size_1 <= 0;
      main_isLeaf_2 <= 0;
      main_nextFree_3 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_4[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_5[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_6[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_7[main_memory_index] <= 0;
        end
      end
      main_stuckKeys_1_index_8 <= 0;
      main_stuckKeys_2_index_9 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_stuckKeys_2_value_10[main_memory_index] <= 0;
        end
      end
      main_stuckData_3_index_11 <= 0;
      main_stuckData_4_index_12 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_stuckData_4_value_13[main_memory_index] <= 0;
        end
      end
      main_stuckSize_5_index_14 <= 0;
      main_stuckSize_6_index_15 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckSize_6_value_16[main_memory_index] <= 0;
        end
      end
      main_stuckIsLeaf_7_index_17 <= 0;
      main_stuckIsLeaf_8_index_18 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckIsLeaf_8_value_19[main_memory_index] <= 0;
        end
      end
      main_Found_20 <= 0;
      main_Key_21 <= 0;
      main_FoundKey_22 <= 0;
      main_Data_23 <= 0;
      main_BtreeIndex_24 <= 0;
      main_StuckIndex_25 <= 0;
      main_MergeSuccess_26 <= 0;
      main_index_27 <= 0;
      main_size_28 <= 0;
      main_isLeaf_29 <= 0;
      main_nextFree_30 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_31[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_32[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_33[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_34[main_memory_index] <= 0;
        end
      end
      main_Found_35 <= 0;
      main_Key_36 <= 0;
      main_FoundKey_37 <= 0;
      main_Data_38 <= 0;
      main_BtreeIndex_39 <= 0;
      main_StuckIndex_40 <= 0;
      main_MergeSuccess_41 <= 0;
      main_index_42 <= 0;
      main_size_43 <= 0;
      main_isLeaf_44 <= 0;
      main_nextFree_45 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_46[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_47[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_48[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_49[main_memory_index] <= 0;
        end
      end
      main_Found_50 <= 0;
      main_Key_51 <= 0;
      main_FoundKey_52 <= 0;
      main_Data_53 <= 0;
      main_BtreeIndex_54 <= 0;
      main_StuckIndex_55 <= 0;
      main_MergeSuccess_56 <= 0;
      main_index_57 <= 0;
      main_size_58 <= 0;
      main_isLeaf_59 <= 0;
      main_nextFree_60 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_61[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_62[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_63[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_64[main_memory_index] <= 0;
        end
      end
      main_Found_65 <= 0;
      main_Key_66 <= 0;
      main_FoundKey_67 <= 0;
      main_Data_68 <= 0;
      main_BtreeIndex_69 <= 0;
      main_StuckIndex_70 <= 0;
      main_MergeSuccess_71 <= 0;
      main_index_72 <= 0;
      main_size_73 <= 0;
      main_isLeaf_74 <= 0;
      main_nextFree_75 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_76[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_77[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_78[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_79[main_memory_index] <= 0;
        end
      end
      main_Found_80 <= 0;
      main_Key_81 <= 0;
      main_FoundKey_82 <= 0;
      main_Data_83 <= 0;
      main_BtreeIndex_84 <= 0;
      main_StuckIndex_85 <= 0;
      main_MergeSuccess_86 <= 0;
      main_index_87 <= 0;
      main_size_88 <= 0;
      main_isLeaf_89 <= 0;
      main_nextFree_90 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_91[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_92[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_93[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_94[main_memory_index] <= 0;
        end
      end
      main_Found_95 <= 0;
      main_Key_96 <= 0;
      main_FoundKey_97 <= 0;
      main_Data_98 <= 0;
      main_BtreeIndex_99 <= 0;
      main_StuckIndex_100 <= 0;
      main_MergeSuccess_101 <= 0;
      main_index_102 <= 0;
      main_size_103 <= 0;
      main_isLeaf_104 <= 0;
      main_nextFree_105 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_106[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_107[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_108[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_109[main_memory_index] <= 0;
        end
      end
      main_Found_110 <= 0;
      main_Key_111 <= 0;
      main_FoundKey_112 <= 0;
      main_Data_113 <= 0;
      main_BtreeIndex_114 <= 0;
      main_StuckIndex_115 <= 0;
      main_MergeSuccess_116 <= 0;
      main_index_117 <= 0;
      main_size_118 <= 0;
      main_isLeaf_119 <= 0;
      main_nextFree_120 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_121[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_122[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_123[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_124[main_memory_index] <= 0;
        end
      end
      main_Found_125 <= 0;
      main_Key_126 <= 0;
      main_FoundKey_127 <= 0;
      main_Data_128 <= 0;
      main_BtreeIndex_129 <= 0;
      main_StuckIndex_130 <= 0;
      main_MergeSuccess_131 <= 0;
      main_index_132 <= 0;
      main_size_133 <= 0;
      main_isLeaf_134 <= 0;
      main_nextFree_135 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_136[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_137[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_138[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_139[main_memory_index] <= 0;
        end
      end
      main_Found_140 <= 0;
      main_Key_141 <= 0;
      main_FoundKey_142 <= 0;
      main_Data_143 <= 0;
      main_BtreeIndex_144 <= 0;
      main_StuckIndex_145 <= 0;
      main_MergeSuccess_146 <= 0;
      main_sum_147 <= 0;
      main_can_148 <= 0;
      main_indexLeft_149 <= 0;
      main_indexRight_150 <= 0;
      main_midKey_151 <= 0;
      main_index_152 <= 0;
      main_size_153 <= 0;
      main_isLeaf_154 <= 0;
      main_nextFree_155 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_156[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_157[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_158[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_159[main_memory_index] <= 0;
        end
      end
      main_Found_160 <= 0;
      main_Key_161 <= 0;
      main_FoundKey_162 <= 0;
      main_Data_163 <= 0;
      main_BtreeIndex_164 <= 0;
      main_StuckIndex_165 <= 0;
      main_MergeSuccess_166 <= 0;
      main_d_167 <= 0;
      main_n_168 <= 0;
      main_index_169 <= 0;
      main_size_170 <= 0;
      main_isLeaf_171 <= 0;
      main_nextFree_172 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_173[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_174[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_175[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_176[main_memory_index] <= 0;
        end
      end
      main_Found_177 <= 0;
      main_Key_178 <= 0;
      main_FoundKey_179 <= 0;
      main_Data_180 <= 0;
      main_BtreeIndex_181 <= 0;
      main_StuckIndex_182 <= 0;
      main_MergeSuccess_183 <= 0;
      main_d_184 <= 0;
      main_n_185 <= 0;
      main_index_186 <= 0;
      main_size_187 <= 0;
      main_isLeaf_188 <= 0;
      main_nextFree_189 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_190[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_191[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_192[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_193[main_memory_index] <= 0;
        end
      end
      main_Found_194 <= 0;
      main_Key_195 <= 0;
      main_FoundKey_196 <= 0;
      main_Data_197 <= 0;
      main_BtreeIndex_198 <= 0;
      main_StuckIndex_199 <= 0;
      main_MergeSuccess_200 <= 0;
      main_d_201 <= 0;
      main_n_202 <= 0;
      main_index_203 <= 0;
      main_size_204 <= 0;
      main_isLeaf_205 <= 0;
      main_nextFree_206 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Keys_207[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Compares_208[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Collapse_209[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
          main_Data_210[main_memory_index] <= 0;
        end
      end
      main_Found_211 <= 0;
      main_Key_212 <= 0;
      main_FoundKey_213 <= 0;
      main_Data_214 <= 0;
      main_BtreeIndex_215 <= 0;
      main_StuckIndex_216 <= 0;
      main_MergeSuccess_217 <= 0;
      stuckIsLeaf_7_requestedAt <= -1;
      stuckIsLeaf_8_requestedAt <= -1;
      stuckSize_5_requestedAt <= -1;
      stuckSize_6_requestedAt <= -1;
      stuckKeys_1_requestedAt <= -1;
      stuckKeys_2_requestedAt <= -1;
      stuckData_3_requestedAt <= -1;
      stuckData_4_requestedAt <= -1;
    end
    else begin
      if (step < 0) begin
      end
      else if (processCurrent == 0) begin
        case(main_pc)
          0: begin
            main_index_203 <= 0;
            main_pc <= main_pc + 1;
            case (main_pc)
              0: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Chip.java:1186:<init>|  Chip.java:1185:Zero|  Btree.java:0320:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          1: begin
            main_stuckSize_5_index_14 <= main_index_203;
            stuckSize_5_requestedAt <= step;
            stuckSize_5_finishedAt <= -1;
            main_stuckIsLeaf_7_index_17 <= main_index_203;
            stuckIsLeaf_7_requestedAt <= step;
            stuckIsLeaf_7_finishedAt <= -1;
            main_stuckKeys_1_index_8 <= main_index_203;
            stuckKeys_1_requestedAt <= step;
            stuckKeys_1_finishedAt <= -1;
            main_stuckData_3_index_11 <= main_index_203;
            stuckData_3_requestedAt <= step;
            stuckData_3_finishedAt <= -1;
            main_pc <= main_pc + 1;
            case (main_pc)
              1: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:0326:<init>|  Btree.java:0325:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          2: begin
            if ((stuckSize_5_requestedAt < stuckSize_5_finishedAt)) begin
              main_pc <= main_pc + 1;
            end
            case (main_pc)
              2: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1915:<init>|  Chip.java:1914:waitResultOfTransaction|  Btree.java:0339:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          3: begin
            if ((stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt)) begin
              main_pc <= main_pc + 1;
            end
            case (main_pc)
              3: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1915:<init>|  Chip.java:1914:waitResultOfTransaction|  Btree.java:0340:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          4: begin
            if ((stuckKeys_1_requestedAt < stuckKeys_1_finishedAt)) begin
              main_pc <= main_pc + 1;
            end
            case (main_pc)
              4: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1915:<init>|  Chip.java:1914:waitResultOfTransaction|  Btree.java:0341:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          5: begin
            if ((stuckData_3_requestedAt < stuckData_3_finishedAt)) begin
              main_pc <= main_pc + 1;
            end
            case (main_pc)
              5: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:1915:<init>|  Chip.java:1914:waitResultOfTransaction|  Btree.java:0342:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          6: begin
            main_size_204 <= stuckSize_stuckSize_5_result_0[0];
            main_isLeaf_205 <= stuckIsLeaf_stuckIsLeaf_7_result_0[0];
            begin
              for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
                main_Keys_207[main_memory_index] <= stuckKeys_stuckKeys_1_result_0[main_memory_index];
              end
            end
            begin
              for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
                main_Data_210[main_memory_index] <= stuckData_stuckData_3_result_0[main_memory_index];
              end
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              6: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:0345:<init>|  Btree.java:0344:stuckGet|  Btree.java:0321:stuckGetRoot|  Btree.java:3847:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          7: begin
            main_Key_212 <= 3;
            main_pc <= main_pc + 1;
            case (main_pc)
              7: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Chip.java:0838:<init>|  Chip.java:0837:RegisterSet|  Btree.java:3848:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          8: begin
            begin
              for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index + 1) begin
                main_Compares_208[main_memory_index] <= main_Key_212 == main_Keys_207[main_memory_index] && main_memory_index < main_size_204;
                main_Collapse_209[main_memory_index] <= main_memory_index;
              end
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              8: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:1022:<init>|  Btree.java:1021:search_eq_parallel|  Btree.java:3849:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          9: begin
            for(main_memory_index = 0; main_memory_index < 7; main_memory_index = main_memory_index+2) begin
              if (main_Compares_208[main_memory_index+1]) begin
                  main_Compares_208[main_memory_index] <= 1;
                  main_Collapse_209[main_memory_index] <= main_Collapse_209[main_memory_index+1];
              end
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              9: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:1055:<init>|  Btree.java:1054:search_eq_parallel|  Btree.java:3849:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          10: begin
            for(main_memory_index = 0; main_memory_index < 6; main_memory_index = main_memory_index+4) begin
              if (main_Compares_208[main_memory_index+2]) begin
                  main_Compares_208[main_memory_index] <= 1;
                  main_Collapse_209[main_memory_index] <= main_Collapse_209[main_memory_index+2];
              end
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              10: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:1055:<init>|  Btree.java:1054:search_eq_parallel|  Btree.java:3849:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          11: begin
            for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index+8) begin
              if (main_Compares_208[main_memory_index+4]) begin
                  main_Compares_208[main_memory_index] <= 1;
                  main_Collapse_209[main_memory_index] <= main_Collapse_209[main_memory_index+4];
              end
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              11: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:1055:<init>|  Btree.java:1054:search_eq_parallel|  Btree.java:3849:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          12: begin
            if (main_Compares_208[0]) begin
              main_Found_211 <= 1;
              main_StuckIndex_216 <= main_Collapse_209[0];
              main_Key_212 <= main_Keys_207[main_Collapse_209[0]];
              main_Data_214 <= main_Data_210[main_Collapse_209[0]];
            end
            else begin
              main_Found_211 <= 0;
            end
            main_pc <= main_pc + 1;
            case (main_pc)
              12: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0585:<init>|  Btree.java:1089:<init>|  Btree.java:1088:search_eq_parallel|  Btree.java:3849:test_search_eq|  Btree.java:7113:newTests|  Btree.java:7119:main|");
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
  // Process: stuckIsLeaf  process_stuckIsLeaf_0001
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[8*1];
  (* nomem2reg *)
  reg [1-1:0] stuckIsLeaf_stuckIsLeaf_7_result_0[1];
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
  reg[1-1:0] stuckIsLeaf_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckIsLeaf_pc <= 0;
      stuckIsLeaf_stop <= 0;
      stuckIsLeaf_returnCode <= 0;
      stuckIsLeaf_memory_index <= 0;
      stuckIsLeaf_memory_value <= 0;
      begin
        for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
          stuckIsLeaf_stuckIsLeaf_7_result_0[stuckIsLeaf_memory_index] <= 0;
        end
      end
      stuckIsLeaf_7_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_7_returnCode <= 0;
      stuckIsLeaf_8_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_8_returnCode <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            stuckIsLeaf_memory_index <= 0;
            stuckIsLeaf_memory_value <= 1;
          end
          1: begin
            stuckIsLeaf_memory_index <= 1;
            stuckIsLeaf_memory_value <= 0;
          end
          2: begin
            stuckIsLeaf_memory_index <= 2;
            stuckIsLeaf_memory_value <= 0;
          end
          3: begin
            stuckIsLeaf_memory_index <= 3;
            stuckIsLeaf_memory_value <= 0;
          end
          4: begin
            stuckIsLeaf_memory_index <= 4;
            stuckIsLeaf_memory_value <= 0;
          end
          5: begin
            stuckIsLeaf_memory_index <= 5;
            stuckIsLeaf_memory_value <= 0;
          end
          6: begin
            stuckIsLeaf_memory_index <= 6;
            stuckIsLeaf_memory_value <= 0;
          end
          7: begin
            stuckIsLeaf_memory_index <= 7;
            stuckIsLeaf_memory_value <= 0;
          end
        endcase
        stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= stuckIsLeaf_memory_value;
      end
      else if (processCurrent == 1) begin
        case(stuckIsLeaf_pc)
          0: begin
            if ((stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step)) begin
              begin
                for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
                  stuckIsLeaf_stuckIsLeaf_7_result_0[stuckIsLeaf_memory_index] <= stuckIsLeaf_memory[main_stuckIsLeaf_7_index_17*1+stuckIsLeaf_memory_index];
                end
              end
              stuckIsLeaf_7_finishedAt <= step;
            end
            else if ((stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step)) begin
              begin
                for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
                  stuckIsLeaf_memory[main_stuckIsLeaf_8_index_18*1+stuckIsLeaf_memory_index] <= main_stuckIsLeaf_8_value_19[stuckIsLeaf_memory_index];
                end
              end
              stuckIsLeaf_8_finishedAt <= step;
            end
            else begin
            end
          end
          default: stuckIsLeaf_stop <= 1;
        endcase
      end
    end
  end
  // Process: stuckIsFree  process_stuckIsFree_0002
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsFree_memory[8*1];
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  integer stuckIsFree_memory_index;
  reg[1-1:0] stuckIsFree_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckIsFree_pc <= 0;
      stuckIsFree_stop <= 0;
      stuckIsFree_returnCode <= 0;
      stuckIsFree_memory_index <= 0;
      stuckIsFree_memory_value <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            stuckIsFree_memory_index <= 0;
            stuckIsFree_memory_value <= 0;
          end
          1: begin
            stuckIsFree_memory_index <= 1;
            stuckIsFree_memory_value <= 1;
          end
          2: begin
            stuckIsFree_memory_index <= 2;
            stuckIsFree_memory_value <= 1;
          end
          3: begin
            stuckIsFree_memory_index <= 3;
            stuckIsFree_memory_value <= 1;
          end
          4: begin
            stuckIsFree_memory_index <= 4;
            stuckIsFree_memory_value <= 1;
          end
          5: begin
            stuckIsFree_memory_index <= 5;
            stuckIsFree_memory_value <= 1;
          end
          6: begin
            stuckIsFree_memory_index <= 6;
            stuckIsFree_memory_value <= 1;
          end
          7: begin
            stuckIsFree_memory_index <= 7;
            stuckIsFree_memory_value <= 1;
          end
        endcase
        stuckIsFree_memory[stuckIsFree_memory_index] <= stuckIsFree_memory_value;
      end
      else if (processCurrent == 2) begin
        case(stuckIsFree_pc)
          0: begin
          end
          default: stuckIsFree_stop <= 1;
        endcase
      end
    end
  end
  // Process: freeNext  process_freeNext_0003
  (* ram_style = "block" *)
  reg [4-1:0] freeNext_memory[8*1];
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  integer freeNext_memory_index;
  reg[4-1:0] freeNext_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      freeNext_pc <= 0;
      freeNext_stop <= 0;
      freeNext_returnCode <= 0;
      freeNext_memory_index <= 0;
      freeNext_memory_value <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            freeNext_memory_index <= 0;
            freeNext_memory_value <= 1;
          end
          1: begin
            freeNext_memory_index <= 1;
            freeNext_memory_value <= 2;
          end
          2: begin
            freeNext_memory_index <= 2;
            freeNext_memory_value <= 3;
          end
          3: begin
            freeNext_memory_index <= 3;
            freeNext_memory_value <= 4;
          end
          4: begin
            freeNext_memory_index <= 4;
            freeNext_memory_value <= 5;
          end
          5: begin
            freeNext_memory_index <= 5;
            freeNext_memory_value <= 6;
          end
          6: begin
            freeNext_memory_index <= 6;
            freeNext_memory_value <= 7;
          end
          7: begin
            freeNext_memory_index <= 7;
            freeNext_memory_value <= 0;
          end
        endcase
        freeNext_memory[freeNext_memory_index] <= freeNext_memory_value;
      end
      else if (processCurrent == 3) begin
        case(freeNext_pc)
          0: begin
          end
          default: freeNext_stop <= 1;
        endcase
      end
    end
  end
  // Process: stuckSize  process_stuckSize_0004
  (* ram_style = "block" *)
  reg [4-1:0] stuckSize_memory[8*1];
  (* nomem2reg *)
  reg [4-1:0] stuckSize_stuckSize_5_result_0[1];
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
  reg[4-1:0] stuckSize_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckSize_pc <= 0;
      stuckSize_stop <= 0;
      stuckSize_returnCode <= 0;
      stuckSize_memory_index <= 0;
      stuckSize_memory_value <= 0;
      begin
        for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
          stuckSize_stuckSize_5_result_0[stuckSize_memory_index] <= 0;
        end
      end
      stuckSize_5_finishedAt <= -1;
      stuckSize_stuckSize_5_returnCode <= 0;
      stuckSize_6_finishedAt <= -1;
      stuckSize_stuckSize_6_returnCode <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            stuckSize_memory_index <= 0;
            stuckSize_memory_value <= 4;
          end
          1: begin
            stuckSize_memory_index <= 1;
            stuckSize_memory_value <= 4;
          end
          2: begin
            stuckSize_memory_index <= 2;
            stuckSize_memory_value <= 4;
          end
          3: begin
            stuckSize_memory_index <= 3;
            stuckSize_memory_value <= 0;
          end
          4: begin
            stuckSize_memory_index <= 4;
            stuckSize_memory_value <= 0;
          end
          5: begin
            stuckSize_memory_index <= 5;
            stuckSize_memory_value <= 0;
          end
          6: begin
            stuckSize_memory_index <= 6;
            stuckSize_memory_value <= 0;
          end
          7: begin
            stuckSize_memory_index <= 7;
            stuckSize_memory_value <= 0;
          end
        endcase
        stuckSize_memory[stuckSize_memory_index] <= stuckSize_memory_value;
      end
      else if (processCurrent == 4) begin
        case(stuckSize_pc)
          0: begin
            if ((stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step)) begin
              begin
                for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
                  stuckSize_stuckSize_5_result_0[stuckSize_memory_index] <= stuckSize_memory[main_stuckSize_5_index_14*1+stuckSize_memory_index];
                end
              end
              stuckSize_5_finishedAt <= step;
            end
            else if ((stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step)) begin
              begin
                for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
                  stuckSize_memory[main_stuckSize_6_index_15*1+stuckSize_memory_index] <= main_stuckSize_6_value_16[stuckSize_memory_index];
                end
              end
              stuckSize_6_finishedAt <= step;
            end
            else begin
            end
          end
          default: stuckSize_stop <= 1;
        endcase
      end
    end
  end
  // Process: stuckKeys  process_stuckKeys_0005
  (* ram_style = "block" *)
  reg [8-1:0] stuckKeys_memory[8*8];
  (* nomem2reg *)
  reg [8-1:0] stuckKeys_stuckKeys_1_result_0[8];
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
  reg[8-1:0] stuckKeys_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckKeys_pc <= 0;
      stuckKeys_stop <= 0;
      stuckKeys_returnCode <= 0;
      stuckKeys_memory_index <= 0;
      stuckKeys_memory_value <= 0;
      begin
        for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 8; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
          stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= 0;
        end
      end
      stuckKeys_1_finishedAt <= -1;
      stuckKeys_stuckKeys_1_returnCode <= 0;
      stuckKeys_2_finishedAt <= -1;
      stuckKeys_stuckKeys_2_returnCode <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            stuckKeys_memory_index <= 0;
            stuckKeys_memory_value <= 0;
          end
          1: begin
            stuckKeys_memory_index <= 1;
            stuckKeys_memory_value <= 1;
          end
          2: begin
            stuckKeys_memory_index <= 2;
            stuckKeys_memory_value <= 2;
          end
          3: begin
            stuckKeys_memory_index <= 3;
            stuckKeys_memory_value <= 3;
          end
          4: begin
            stuckKeys_memory_index <= 4;
            stuckKeys_memory_value <= 0;
          end
          5: begin
            stuckKeys_memory_index <= 5;
            stuckKeys_memory_value <= 0;
          end
          6: begin
            stuckKeys_memory_index <= 6;
            stuckKeys_memory_value <= 0;
          end
          7: begin
            stuckKeys_memory_index <= 7;
            stuckKeys_memory_value <= 0;
          end
          8: begin
            stuckKeys_memory_index <= 8;
            stuckKeys_memory_value <= 0;
          end
          9: begin
            stuckKeys_memory_index <= 9;
            stuckKeys_memory_value <= 1;
          end
          10: begin
            stuckKeys_memory_index <= 10;
            stuckKeys_memory_value <= 2;
          end
          11: begin
            stuckKeys_memory_index <= 11;
            stuckKeys_memory_value <= 3;
          end
          12: begin
            stuckKeys_memory_index <= 12;
            stuckKeys_memory_value <= 0;
          end
          13: begin
            stuckKeys_memory_index <= 13;
            stuckKeys_memory_value <= 0;
          end
          14: begin
            stuckKeys_memory_index <= 14;
            stuckKeys_memory_value <= 0;
          end
          15: begin
            stuckKeys_memory_index <= 15;
            stuckKeys_memory_value <= 0;
          end
          16: begin
            stuckKeys_memory_index <= 16;
            stuckKeys_memory_value <= 0;
          end
          17: begin
            stuckKeys_memory_index <= 17;
            stuckKeys_memory_value <= 1;
          end
          18: begin
            stuckKeys_memory_index <= 18;
            stuckKeys_memory_value <= 2;
          end
          19: begin
            stuckKeys_memory_index <= 19;
            stuckKeys_memory_value <= 3;
          end
          20: begin
            stuckKeys_memory_index <= 20;
            stuckKeys_memory_value <= 0;
          end
          21: begin
            stuckKeys_memory_index <= 21;
            stuckKeys_memory_value <= 0;
          end
          22: begin
            stuckKeys_memory_index <= 22;
            stuckKeys_memory_value <= 0;
          end
          23: begin
            stuckKeys_memory_index <= 23;
            stuckKeys_memory_value <= 0;
          end
          24: begin
            stuckKeys_memory_index <= 24;
            stuckKeys_memory_value <= 0;
          end
          25: begin
            stuckKeys_memory_index <= 25;
            stuckKeys_memory_value <= 0;
          end
          26: begin
            stuckKeys_memory_index <= 26;
            stuckKeys_memory_value <= 0;
          end
          27: begin
            stuckKeys_memory_index <= 27;
            stuckKeys_memory_value <= 0;
          end
          28: begin
            stuckKeys_memory_index <= 28;
            stuckKeys_memory_value <= 0;
          end
          29: begin
            stuckKeys_memory_index <= 29;
            stuckKeys_memory_value <= 0;
          end
          30: begin
            stuckKeys_memory_index <= 30;
            stuckKeys_memory_value <= 0;
          end
          31: begin
            stuckKeys_memory_index <= 31;
            stuckKeys_memory_value <= 0;
          end
          32: begin
            stuckKeys_memory_index <= 32;
            stuckKeys_memory_value <= 0;
          end
          33: begin
            stuckKeys_memory_index <= 33;
            stuckKeys_memory_value <= 0;
          end
          34: begin
            stuckKeys_memory_index <= 34;
            stuckKeys_memory_value <= 0;
          end
          35: begin
            stuckKeys_memory_index <= 35;
            stuckKeys_memory_value <= 0;
          end
          36: begin
            stuckKeys_memory_index <= 36;
            stuckKeys_memory_value <= 0;
          end
          37: begin
            stuckKeys_memory_index <= 37;
            stuckKeys_memory_value <= 0;
          end
          38: begin
            stuckKeys_memory_index <= 38;
            stuckKeys_memory_value <= 0;
          end
          39: begin
            stuckKeys_memory_index <= 39;
            stuckKeys_memory_value <= 0;
          end
          40: begin
            stuckKeys_memory_index <= 40;
            stuckKeys_memory_value <= 0;
          end
          41: begin
            stuckKeys_memory_index <= 41;
            stuckKeys_memory_value <= 0;
          end
          42: begin
            stuckKeys_memory_index <= 42;
            stuckKeys_memory_value <= 0;
          end
          43: begin
            stuckKeys_memory_index <= 43;
            stuckKeys_memory_value <= 0;
          end
          44: begin
            stuckKeys_memory_index <= 44;
            stuckKeys_memory_value <= 0;
          end
          45: begin
            stuckKeys_memory_index <= 45;
            stuckKeys_memory_value <= 0;
          end
          46: begin
            stuckKeys_memory_index <= 46;
            stuckKeys_memory_value <= 0;
          end
          47: begin
            stuckKeys_memory_index <= 47;
            stuckKeys_memory_value <= 0;
          end
          48: begin
            stuckKeys_memory_index <= 48;
            stuckKeys_memory_value <= 0;
          end
          49: begin
            stuckKeys_memory_index <= 49;
            stuckKeys_memory_value <= 0;
          end
          50: begin
            stuckKeys_memory_index <= 50;
            stuckKeys_memory_value <= 0;
          end
          51: begin
            stuckKeys_memory_index <= 51;
            stuckKeys_memory_value <= 0;
          end
          52: begin
            stuckKeys_memory_index <= 52;
            stuckKeys_memory_value <= 0;
          end
          53: begin
            stuckKeys_memory_index <= 53;
            stuckKeys_memory_value <= 0;
          end
          54: begin
            stuckKeys_memory_index <= 54;
            stuckKeys_memory_value <= 0;
          end
          55: begin
            stuckKeys_memory_index <= 55;
            stuckKeys_memory_value <= 0;
          end
          56: begin
            stuckKeys_memory_index <= 56;
            stuckKeys_memory_value <= 0;
          end
          57: begin
            stuckKeys_memory_index <= 57;
            stuckKeys_memory_value <= 0;
          end
          58: begin
            stuckKeys_memory_index <= 58;
            stuckKeys_memory_value <= 0;
          end
          59: begin
            stuckKeys_memory_index <= 59;
            stuckKeys_memory_value <= 0;
          end
          60: begin
            stuckKeys_memory_index <= 60;
            stuckKeys_memory_value <= 0;
          end
          61: begin
            stuckKeys_memory_index <= 61;
            stuckKeys_memory_value <= 0;
          end
          62: begin
            stuckKeys_memory_index <= 62;
            stuckKeys_memory_value <= 0;
          end
          63: begin
            stuckKeys_memory_index <= 63;
            stuckKeys_memory_value <= 0;
          end
        endcase
        stuckKeys_memory[stuckKeys_memory_index] <= stuckKeys_memory_value;
      end
      else if (processCurrent == 5) begin
        case(stuckKeys_pc)
          0: begin
            if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
              begin
                for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 8; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                  stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= stuckKeys_memory[main_stuckKeys_1_index_8*8+stuckKeys_memory_index];
                end
              end
              stuckKeys_1_finishedAt <= step;
            end
            else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
              begin
                for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 8; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                  stuckKeys_memory[main_stuckKeys_2_index_9*8+stuckKeys_memory_index] <= main_stuckKeys_2_value_10[stuckKeys_memory_index];
                end
              end
              stuckKeys_2_finishedAt <= step;
            end
            else begin
            end
          end
          default: stuckKeys_stop <= 1;
        endcase
      end
    end
  end
  // Process: stuckData  process_stuckData_0006
  (* ram_style = "block" *)
  reg [8-1:0] stuckData_memory[8*8];
  (* nomem2reg *)
  reg [8-1:0] stuckData_stuckData_3_result_0[8];
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
  reg[8-1:0] stuckData_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckData_pc <= 0;
      stuckData_stop <= 0;
      stuckData_returnCode <= 0;
      stuckData_memory_index <= 0;
      stuckData_memory_value <= 0;
      begin
        for(stuckData_memory_index = 0; stuckData_memory_index < 8; stuckData_memory_index = stuckData_memory_index + 1) begin
          stuckData_stuckData_3_result_0[stuckData_memory_index] <= 0;
        end
      end
      stuckData_3_finishedAt <= -1;
      stuckData_stuckData_3_returnCode <= 0;
      stuckData_4_finishedAt <= -1;
      stuckData_stuckData_4_returnCode <= 0;
    end
    else begin
      if (step < 0) begin
        case (-(step+1))
          0: begin
            stuckData_memory_index <= 0;
            stuckData_memory_value <= 1;
          end
          1: begin
            stuckData_memory_index <= 1;
            stuckData_memory_value <= 2;
          end
          2: begin
            stuckData_memory_index <= 2;
            stuckData_memory_value <= 3;
          end
          3: begin
            stuckData_memory_index <= 3;
            stuckData_memory_value <= 4;
          end
          4: begin
            stuckData_memory_index <= 4;
            stuckData_memory_value <= 0;
          end
          5: begin
            stuckData_memory_index <= 5;
            stuckData_memory_value <= 0;
          end
          6: begin
            stuckData_memory_index <= 6;
            stuckData_memory_value <= 0;
          end
          7: begin
            stuckData_memory_index <= 7;
            stuckData_memory_value <= 0;
          end
          8: begin
            stuckData_memory_index <= 8;
            stuckData_memory_value <= 1;
          end
          9: begin
            stuckData_memory_index <= 9;
            stuckData_memory_value <= 2;
          end
          10: begin
            stuckData_memory_index <= 10;
            stuckData_memory_value <= 3;
          end
          11: begin
            stuckData_memory_index <= 11;
            stuckData_memory_value <= 4;
          end
          12: begin
            stuckData_memory_index <= 12;
            stuckData_memory_value <= 0;
          end
          13: begin
            stuckData_memory_index <= 13;
            stuckData_memory_value <= 0;
          end
          14: begin
            stuckData_memory_index <= 14;
            stuckData_memory_value <= 0;
          end
          15: begin
            stuckData_memory_index <= 15;
            stuckData_memory_value <= 0;
          end
          16: begin
            stuckData_memory_index <= 16;
            stuckData_memory_value <= 1;
          end
          17: begin
            stuckData_memory_index <= 17;
            stuckData_memory_value <= 2;
          end
          18: begin
            stuckData_memory_index <= 18;
            stuckData_memory_value <= 3;
          end
          19: begin
            stuckData_memory_index <= 19;
            stuckData_memory_value <= 4;
          end
          20: begin
            stuckData_memory_index <= 20;
            stuckData_memory_value <= 0;
          end
          21: begin
            stuckData_memory_index <= 21;
            stuckData_memory_value <= 0;
          end
          22: begin
            stuckData_memory_index <= 22;
            stuckData_memory_value <= 0;
          end
          23: begin
            stuckData_memory_index <= 23;
            stuckData_memory_value <= 0;
          end
          24: begin
            stuckData_memory_index <= 24;
            stuckData_memory_value <= 0;
          end
          25: begin
            stuckData_memory_index <= 25;
            stuckData_memory_value <= 0;
          end
          26: begin
            stuckData_memory_index <= 26;
            stuckData_memory_value <= 0;
          end
          27: begin
            stuckData_memory_index <= 27;
            stuckData_memory_value <= 0;
          end
          28: begin
            stuckData_memory_index <= 28;
            stuckData_memory_value <= 0;
          end
          29: begin
            stuckData_memory_index <= 29;
            stuckData_memory_value <= 0;
          end
          30: begin
            stuckData_memory_index <= 30;
            stuckData_memory_value <= 0;
          end
          31: begin
            stuckData_memory_index <= 31;
            stuckData_memory_value <= 0;
          end
          32: begin
            stuckData_memory_index <= 32;
            stuckData_memory_value <= 0;
          end
          33: begin
            stuckData_memory_index <= 33;
            stuckData_memory_value <= 0;
          end
          34: begin
            stuckData_memory_index <= 34;
            stuckData_memory_value <= 0;
          end
          35: begin
            stuckData_memory_index <= 35;
            stuckData_memory_value <= 0;
          end
          36: begin
            stuckData_memory_index <= 36;
            stuckData_memory_value <= 0;
          end
          37: begin
            stuckData_memory_index <= 37;
            stuckData_memory_value <= 0;
          end
          38: begin
            stuckData_memory_index <= 38;
            stuckData_memory_value <= 0;
          end
          39: begin
            stuckData_memory_index <= 39;
            stuckData_memory_value <= 0;
          end
          40: begin
            stuckData_memory_index <= 40;
            stuckData_memory_value <= 0;
          end
          41: begin
            stuckData_memory_index <= 41;
            stuckData_memory_value <= 0;
          end
          42: begin
            stuckData_memory_index <= 42;
            stuckData_memory_value <= 0;
          end
          43: begin
            stuckData_memory_index <= 43;
            stuckData_memory_value <= 0;
          end
          44: begin
            stuckData_memory_index <= 44;
            stuckData_memory_value <= 0;
          end
          45: begin
            stuckData_memory_index <= 45;
            stuckData_memory_value <= 0;
          end
          46: begin
            stuckData_memory_index <= 46;
            stuckData_memory_value <= 0;
          end
          47: begin
            stuckData_memory_index <= 47;
            stuckData_memory_value <= 0;
          end
          48: begin
            stuckData_memory_index <= 48;
            stuckData_memory_value <= 0;
          end
          49: begin
            stuckData_memory_index <= 49;
            stuckData_memory_value <= 0;
          end
          50: begin
            stuckData_memory_index <= 50;
            stuckData_memory_value <= 0;
          end
          51: begin
            stuckData_memory_index <= 51;
            stuckData_memory_value <= 0;
          end
          52: begin
            stuckData_memory_index <= 52;
            stuckData_memory_value <= 0;
          end
          53: begin
            stuckData_memory_index <= 53;
            stuckData_memory_value <= 0;
          end
          54: begin
            stuckData_memory_index <= 54;
            stuckData_memory_value <= 0;
          end
          55: begin
            stuckData_memory_index <= 55;
            stuckData_memory_value <= 0;
          end
          56: begin
            stuckData_memory_index <= 56;
            stuckData_memory_value <= 0;
          end
          57: begin
            stuckData_memory_index <= 57;
            stuckData_memory_value <= 0;
          end
          58: begin
            stuckData_memory_index <= 58;
            stuckData_memory_value <= 0;
          end
          59: begin
            stuckData_memory_index <= 59;
            stuckData_memory_value <= 0;
          end
          60: begin
            stuckData_memory_index <= 60;
            stuckData_memory_value <= 0;
          end
          61: begin
            stuckData_memory_index <= 61;
            stuckData_memory_value <= 0;
          end
          62: begin
            stuckData_memory_index <= 62;
            stuckData_memory_value <= 0;
          end
          63: begin
            stuckData_memory_index <= 63;
            stuckData_memory_value <= 0;
          end
        endcase
        stuckData_memory[stuckData_memory_index] <= stuckData_memory_value;
      end
      else if (processCurrent == 6) begin
        case(stuckData_pc)
          0: begin
            if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
              begin
                for(stuckData_memory_index = 0; stuckData_memory_index < 8; stuckData_memory_index = stuckData_memory_index + 1) begin
                  stuckData_stuckData_3_result_0[stuckData_memory_index] <= stuckData_memory[main_stuckData_3_index_11*8+stuckData_memory_index];
                end
              end
              stuckData_3_finishedAt <= step;
            end
            else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
              begin
                for(stuckData_memory_index = 0; stuckData_memory_index < 8; stuckData_memory_index = stuckData_memory_index + 1) begin
                  stuckData_memory[main_stuckData_4_index_12*8+stuckData_memory_index] <= main_stuckData_4_value_13[stuckData_memory_index];
                end
              end
              stuckData_4_finishedAt <= step;
            end
            else begin
            end
          end
          default: stuckData_stop <= 1;
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
      $fwrite(o, "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n", "Btree", step, maxSteps, !stop);
      $fwrite(o, "  Processes:\n");

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 13, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "main_index_0", main_index_0);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_1", main_size_1);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_2", main_isLeaf_2);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_3", main_nextFree_3);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 0, main_Keys_4[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 1, main_Keys_4[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 2, main_Keys_4[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 3, main_Keys_4[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 4, main_Keys_4[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 5, main_Keys_4[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 6, main_Keys_4[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_4", 7, main_Keys_4[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 0, main_Compares_5[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 1, main_Compares_5[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 2, main_Compares_5[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 3, main_Compares_5[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 4, main_Compares_5[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 5, main_Compares_5[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 6, main_Compares_5[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 7, main_Compares_5[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 0, main_Collapse_6[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 1, main_Collapse_6[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 2, main_Collapse_6[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 3, main_Collapse_6[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 4, main_Collapse_6[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 5, main_Collapse_6[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 6, main_Collapse_6[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 7, main_Collapse_6[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 0, main_Data_7[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 1, main_Data_7[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 2, main_Data_7[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 3, main_Data_7[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 4, main_Data_7[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 5, main_Data_7[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 6, main_Data_7[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 7, main_Data_7[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_1_index_8", main_stuckKeys_1_index_8);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_2_index_9", main_stuckKeys_2_index_9);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 0, main_stuckKeys_2_value_10[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 1, main_stuckKeys_2_value_10[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 2, main_stuckKeys_2_value_10[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 3, main_stuckKeys_2_value_10[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 4, main_stuckKeys_2_value_10[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 5, main_stuckKeys_2_value_10[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 6, main_stuckKeys_2_value_10[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 7, main_stuckKeys_2_value_10[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_3_index_11", main_stuckData_3_index_11);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_4_index_12", main_stuckData_4_index_12);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 0, main_stuckData_4_value_13[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 1, main_stuckData_4_value_13[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 2, main_stuckData_4_value_13[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 3, main_stuckData_4_value_13[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 4, main_stuckData_4_value_13[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 5, main_stuckData_4_value_13[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 6, main_stuckData_4_value_13[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 7, main_stuckData_4_value_13[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckSize_5_index_14", main_stuckSize_5_index_14);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckSize_6_index_15", main_stuckSize_6_index_15);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckSize_6_value_16", 0, main_stuckSize_6_value_16[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsLeaf_7_index_17", main_stuckIsLeaf_7_index_17);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsLeaf_8_index_18", main_stuckIsLeaf_8_index_18);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckIsLeaf_8_value_19", 0, main_stuckIsLeaf_8_value_19[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_20", main_Found_20);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_21", main_Key_21);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_22", main_FoundKey_22);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_23", main_Data_23);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_24", main_BtreeIndex_24);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_25", main_StuckIndex_25);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_26", main_MergeSuccess_26);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_27", main_index_27);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_28", main_size_28);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_29", main_isLeaf_29);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_30", main_nextFree_30);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 0, main_Keys_31[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 1, main_Keys_31[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 2, main_Keys_31[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 3, main_Keys_31[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 4, main_Keys_31[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 5, main_Keys_31[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 6, main_Keys_31[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_31", 7, main_Keys_31[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 0, main_Compares_32[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 1, main_Compares_32[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 2, main_Compares_32[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 3, main_Compares_32[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 4, main_Compares_32[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 5, main_Compares_32[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 6, main_Compares_32[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 7, main_Compares_32[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 0, main_Collapse_33[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 1, main_Collapse_33[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 2, main_Collapse_33[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 3, main_Collapse_33[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 4, main_Collapse_33[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 5, main_Collapse_33[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 6, main_Collapse_33[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 7, main_Collapse_33[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 0, main_Data_34[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 1, main_Data_34[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 2, main_Data_34[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 3, main_Data_34[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 4, main_Data_34[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 5, main_Data_34[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 6, main_Data_34[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 7, main_Data_34[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_35", main_Found_35);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_36", main_Key_36);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_37", main_FoundKey_37);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_38", main_Data_38);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_39", main_BtreeIndex_39);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_40", main_StuckIndex_40);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_41", main_MergeSuccess_41);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_42", main_index_42);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_43", main_size_43);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_44", main_isLeaf_44);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_45", main_nextFree_45);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 0, main_Keys_46[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 1, main_Keys_46[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 2, main_Keys_46[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 3, main_Keys_46[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 4, main_Keys_46[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 5, main_Keys_46[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 6, main_Keys_46[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_46", 7, main_Keys_46[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 0, main_Compares_47[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 1, main_Compares_47[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 2, main_Compares_47[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 3, main_Compares_47[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 4, main_Compares_47[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 5, main_Compares_47[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 6, main_Compares_47[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 7, main_Compares_47[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 0, main_Collapse_48[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 1, main_Collapse_48[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 2, main_Collapse_48[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 3, main_Collapse_48[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 4, main_Collapse_48[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 5, main_Collapse_48[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 6, main_Collapse_48[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 7, main_Collapse_48[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 0, main_Data_49[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 1, main_Data_49[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 2, main_Data_49[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 3, main_Data_49[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 4, main_Data_49[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 5, main_Data_49[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 6, main_Data_49[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 7, main_Data_49[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_50", main_Found_50);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_51", main_Key_51);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_52", main_FoundKey_52);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_53", main_Data_53);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_54", main_BtreeIndex_54);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_55", main_StuckIndex_55);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_56", main_MergeSuccess_56);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_57", main_index_57);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_58", main_size_58);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_59", main_isLeaf_59);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_60", main_nextFree_60);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 0, main_Keys_61[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 1, main_Keys_61[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 2, main_Keys_61[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 3, main_Keys_61[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 4, main_Keys_61[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 5, main_Keys_61[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 6, main_Keys_61[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_61", 7, main_Keys_61[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 0, main_Compares_62[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 1, main_Compares_62[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 2, main_Compares_62[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 3, main_Compares_62[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 4, main_Compares_62[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 5, main_Compares_62[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 6, main_Compares_62[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 7, main_Compares_62[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 0, main_Collapse_63[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 1, main_Collapse_63[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 2, main_Collapse_63[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 3, main_Collapse_63[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 4, main_Collapse_63[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 5, main_Collapse_63[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 6, main_Collapse_63[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 7, main_Collapse_63[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 0, main_Data_64[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 1, main_Data_64[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 2, main_Data_64[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 3, main_Data_64[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 4, main_Data_64[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 5, main_Data_64[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 6, main_Data_64[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 7, main_Data_64[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_65", main_Found_65);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_66", main_Key_66);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_67", main_FoundKey_67);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_68", main_Data_68);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_69", main_BtreeIndex_69);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_70", main_StuckIndex_70);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_71", main_MergeSuccess_71);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_72", main_index_72);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_73", main_size_73);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_74", main_isLeaf_74);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_75", main_nextFree_75);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 0, main_Keys_76[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 1, main_Keys_76[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 2, main_Keys_76[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 3, main_Keys_76[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 4, main_Keys_76[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 5, main_Keys_76[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 6, main_Keys_76[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_76", 7, main_Keys_76[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 0, main_Compares_77[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 1, main_Compares_77[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 2, main_Compares_77[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 3, main_Compares_77[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 4, main_Compares_77[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 5, main_Compares_77[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 6, main_Compares_77[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 7, main_Compares_77[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 0, main_Collapse_78[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 1, main_Collapse_78[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 2, main_Collapse_78[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 3, main_Collapse_78[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 4, main_Collapse_78[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 5, main_Collapse_78[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 6, main_Collapse_78[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 7, main_Collapse_78[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 0, main_Data_79[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 1, main_Data_79[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 2, main_Data_79[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 3, main_Data_79[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 4, main_Data_79[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 5, main_Data_79[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 6, main_Data_79[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 7, main_Data_79[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_80", main_Found_80);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_81", main_Key_81);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_82", main_FoundKey_82);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_83", main_Data_83);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_84", main_BtreeIndex_84);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_85", main_StuckIndex_85);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_86", main_MergeSuccess_86);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_87", main_index_87);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_88", main_size_88);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_89", main_isLeaf_89);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_90", main_nextFree_90);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 0, main_Keys_91[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 1, main_Keys_91[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 2, main_Keys_91[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 3, main_Keys_91[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 4, main_Keys_91[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 5, main_Keys_91[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 6, main_Keys_91[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_91", 7, main_Keys_91[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 0, main_Compares_92[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 1, main_Compares_92[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 2, main_Compares_92[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 3, main_Compares_92[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 4, main_Compares_92[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 5, main_Compares_92[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 6, main_Compares_92[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 7, main_Compares_92[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 0, main_Collapse_93[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 1, main_Collapse_93[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 2, main_Collapse_93[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 3, main_Collapse_93[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 4, main_Collapse_93[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 5, main_Collapse_93[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 6, main_Collapse_93[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 7, main_Collapse_93[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 0, main_Data_94[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 1, main_Data_94[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 2, main_Data_94[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 3, main_Data_94[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 4, main_Data_94[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 5, main_Data_94[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 6, main_Data_94[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 7, main_Data_94[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_95", main_Found_95);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_96", main_Key_96);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_97", main_FoundKey_97);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_98", main_Data_98);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_99", main_BtreeIndex_99);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_100", main_StuckIndex_100);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_101", main_MergeSuccess_101);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_102", main_index_102);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_103", main_size_103);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_104", main_isLeaf_104);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_105", main_nextFree_105);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 0, main_Keys_106[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 1, main_Keys_106[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 2, main_Keys_106[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 3, main_Keys_106[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 4, main_Keys_106[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 5, main_Keys_106[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 6, main_Keys_106[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_106", 7, main_Keys_106[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 0, main_Compares_107[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 1, main_Compares_107[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 2, main_Compares_107[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 3, main_Compares_107[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 4, main_Compares_107[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 5, main_Compares_107[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 6, main_Compares_107[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 7, main_Compares_107[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 0, main_Collapse_108[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 1, main_Collapse_108[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 2, main_Collapse_108[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 3, main_Collapse_108[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 4, main_Collapse_108[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 5, main_Collapse_108[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 6, main_Collapse_108[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 7, main_Collapse_108[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 0, main_Data_109[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 1, main_Data_109[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 2, main_Data_109[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 3, main_Data_109[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 4, main_Data_109[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 5, main_Data_109[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 6, main_Data_109[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 7, main_Data_109[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_110", main_Found_110);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_111", main_Key_111);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_112", main_FoundKey_112);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_113", main_Data_113);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_114", main_BtreeIndex_114);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_115", main_StuckIndex_115);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_116", main_MergeSuccess_116);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_117", main_index_117);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_118", main_size_118);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_119", main_isLeaf_119);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_120", main_nextFree_120);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 0, main_Keys_121[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 1, main_Keys_121[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 2, main_Keys_121[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 3, main_Keys_121[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 4, main_Keys_121[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 5, main_Keys_121[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 6, main_Keys_121[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_121", 7, main_Keys_121[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 0, main_Compares_122[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 1, main_Compares_122[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 2, main_Compares_122[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 3, main_Compares_122[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 4, main_Compares_122[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 5, main_Compares_122[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 6, main_Compares_122[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_122", 7, main_Compares_122[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 0, main_Collapse_123[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 1, main_Collapse_123[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 2, main_Collapse_123[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 3, main_Collapse_123[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 4, main_Collapse_123[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 5, main_Collapse_123[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 6, main_Collapse_123[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_123", 7, main_Collapse_123[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 0, main_Data_124[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 1, main_Data_124[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 2, main_Data_124[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 3, main_Data_124[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 4, main_Data_124[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 5, main_Data_124[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 6, main_Data_124[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_124", 7, main_Data_124[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_125", main_Found_125);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_126", main_Key_126);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_127", main_FoundKey_127);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_128", main_Data_128);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_129", main_BtreeIndex_129);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_130", main_StuckIndex_130);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_131", main_MergeSuccess_131);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_132", main_index_132);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_133", main_size_133);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_134", main_isLeaf_134);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_135", main_nextFree_135);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 0, main_Keys_136[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 1, main_Keys_136[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 2, main_Keys_136[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 3, main_Keys_136[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 4, main_Keys_136[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 5, main_Keys_136[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 6, main_Keys_136[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_136", 7, main_Keys_136[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 0, main_Compares_137[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 1, main_Compares_137[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 2, main_Compares_137[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 3, main_Compares_137[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 4, main_Compares_137[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 5, main_Compares_137[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 6, main_Compares_137[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_137", 7, main_Compares_137[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 0, main_Collapse_138[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 1, main_Collapse_138[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 2, main_Collapse_138[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 3, main_Collapse_138[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 4, main_Collapse_138[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 5, main_Collapse_138[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 6, main_Collapse_138[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_138", 7, main_Collapse_138[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 0, main_Data_139[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 1, main_Data_139[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 2, main_Data_139[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 3, main_Data_139[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 4, main_Data_139[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 5, main_Data_139[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 6, main_Data_139[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_139", 7, main_Data_139[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_140", main_Found_140);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_141", main_Key_141);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_142", main_FoundKey_142);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_143", main_Data_143);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_144", main_BtreeIndex_144);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_145", main_StuckIndex_145);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_146", main_MergeSuccess_146);
      $fwrite(o, "        %-50s = %1d\n",  "main_sum_147", main_sum_147);
      $fwrite(o, "        %-50s = %1d\n",  "main_can_148", main_can_148);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexLeft_149", main_indexLeft_149);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexRight_150", main_indexRight_150);
      $fwrite(o, "        %-50s = %1d\n",  "main_midKey_151", main_midKey_151);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_152", main_index_152);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_153", main_size_153);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_154", main_isLeaf_154);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_155", main_nextFree_155);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 0, main_Keys_156[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 1, main_Keys_156[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 2, main_Keys_156[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 3, main_Keys_156[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 4, main_Keys_156[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 5, main_Keys_156[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 6, main_Keys_156[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_156", 7, main_Keys_156[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 0, main_Compares_157[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 1, main_Compares_157[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 2, main_Compares_157[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 3, main_Compares_157[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 4, main_Compares_157[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 5, main_Compares_157[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 6, main_Compares_157[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_157", 7, main_Compares_157[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 0, main_Collapse_158[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 1, main_Collapse_158[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 2, main_Collapse_158[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 3, main_Collapse_158[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 4, main_Collapse_158[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 5, main_Collapse_158[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 6, main_Collapse_158[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_158", 7, main_Collapse_158[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 0, main_Data_159[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 1, main_Data_159[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 2, main_Data_159[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 3, main_Data_159[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 4, main_Data_159[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 5, main_Data_159[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 6, main_Data_159[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_159", 7, main_Data_159[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_160", main_Found_160);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_161", main_Key_161);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_162", main_FoundKey_162);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_163", main_Data_163);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_164", main_BtreeIndex_164);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_165", main_StuckIndex_165);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_166", main_MergeSuccess_166);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_167", main_d_167);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_168", main_n_168);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_169", main_index_169);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_170", main_size_170);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_171", main_isLeaf_171);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_172", main_nextFree_172);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 0, main_Keys_173[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 1, main_Keys_173[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 2, main_Keys_173[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 3, main_Keys_173[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 4, main_Keys_173[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 5, main_Keys_173[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 6, main_Keys_173[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_173", 7, main_Keys_173[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 0, main_Compares_174[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 1, main_Compares_174[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 2, main_Compares_174[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 3, main_Compares_174[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 4, main_Compares_174[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 5, main_Compares_174[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 6, main_Compares_174[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_174", 7, main_Compares_174[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 0, main_Collapse_175[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 1, main_Collapse_175[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 2, main_Collapse_175[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 3, main_Collapse_175[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 4, main_Collapse_175[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 5, main_Collapse_175[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 6, main_Collapse_175[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_175", 7, main_Collapse_175[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 0, main_Data_176[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 1, main_Data_176[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 2, main_Data_176[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 3, main_Data_176[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 4, main_Data_176[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 5, main_Data_176[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 6, main_Data_176[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_176", 7, main_Data_176[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_177", main_Found_177);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_178", main_Key_178);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_179", main_FoundKey_179);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_180", main_Data_180);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_181", main_BtreeIndex_181);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_182", main_StuckIndex_182);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_183", main_MergeSuccess_183);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_184", main_d_184);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_185", main_n_185);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_186", main_index_186);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_187", main_size_187);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_188", main_isLeaf_188);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_189", main_nextFree_189);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 0, main_Keys_190[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 1, main_Keys_190[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 2, main_Keys_190[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 3, main_Keys_190[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 4, main_Keys_190[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 5, main_Keys_190[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 6, main_Keys_190[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_190", 7, main_Keys_190[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 0, main_Compares_191[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 1, main_Compares_191[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 2, main_Compares_191[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 3, main_Compares_191[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 4, main_Compares_191[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 5, main_Compares_191[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 6, main_Compares_191[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_191", 7, main_Compares_191[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 0, main_Collapse_192[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 1, main_Collapse_192[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 2, main_Collapse_192[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 3, main_Collapse_192[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 4, main_Collapse_192[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 5, main_Collapse_192[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 6, main_Collapse_192[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_192", 7, main_Collapse_192[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 0, main_Data_193[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 1, main_Data_193[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 2, main_Data_193[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 3, main_Data_193[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 4, main_Data_193[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 5, main_Data_193[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 6, main_Data_193[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_193", 7, main_Data_193[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_194", main_Found_194);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_195", main_Key_195);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_196", main_FoundKey_196);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_197", main_Data_197);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_198", main_BtreeIndex_198);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_199", main_StuckIndex_199);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_200", main_MergeSuccess_200);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_201", main_d_201);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_202", main_n_202);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_203", main_index_203);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_204", main_size_204);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_205", main_isLeaf_205);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_206", main_nextFree_206);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 0, main_Keys_207[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 1, main_Keys_207[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 2, main_Keys_207[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 3, main_Keys_207[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 4, main_Keys_207[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 5, main_Keys_207[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 6, main_Keys_207[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_207", 7, main_Keys_207[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 0, main_Compares_208[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 1, main_Compares_208[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 2, main_Compares_208[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 3, main_Compares_208[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 4, main_Compares_208[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 5, main_Compares_208[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 6, main_Compares_208[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_208", 7, main_Compares_208[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 0, main_Collapse_209[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 1, main_Collapse_209[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 2, main_Collapse_209[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 3, main_Collapse_209[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 4, main_Collapse_209[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 5, main_Collapse_209[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 6, main_Collapse_209[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_209", 7, main_Collapse_209[7]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 0, main_Data_210[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 1, main_Data_210[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 2, main_Data_210[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 3, main_Data_210[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 4, main_Data_210[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 5, main_Data_210[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 6, main_Data_210[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_210", 7, main_Data_210[7]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_211", main_Found_211);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_212", main_Key_212);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_213", main_FoundKey_213);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_214", main_Data_214);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_215", main_BtreeIndex_215);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_216", main_StuckIndex_216);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_217", main_MergeSuccess_217);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsLeaf", 1, stuckIsLeaf_pc, stuckIsLeaf_returnCode);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_0", 0, stuckIsLeaf_stuckIsLeaf_7_result_0[0]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckIsLeaf_7", stuckIsLeaf_7_requestedAt, stuckIsLeaf_7_finishedAt, stuckIsLeaf_stuckIsLeaf_7_returnCode, (stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step), (stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsLeaf_7_index_17", main_stuckIsLeaf_7_index_17);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_0", 0, stuckIsLeaf_stuckIsLeaf_7_result_0[0]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_8", stuckIsLeaf_8_requestedAt, stuckIsLeaf_8_finishedAt, stuckIsLeaf_stuckIsLeaf_8_returnCode, (stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step), (stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsLeaf_8_index_18", main_stuckIsLeaf_8_index_18);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckIsLeaf_8_value_19", 0, main_stuckIsLeaf_8_value_19[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 2, "stuckIsFree", 1, stuckIsFree_pc, stuckIsFree_returnCode);
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
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "freeNext", 1, freeNext_pc, freeNext_returnCode);
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
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 4, 1);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckSize_stuckSize_5_result_0", 0, stuckSize_stuckSize_5_result_0[0]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckSize_5", stuckSize_5_requestedAt, stuckSize_5_finishedAt, stuckSize_stuckSize_5_returnCode, (stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step), (stuckSize_5_requestedAt < stuckSize_5_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckSize_5_index_14", main_stuckSize_5_index_14);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckSize_stuckSize_5_result_0", 0, stuckSize_stuckSize_5_result_0[0]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckSize_6", stuckSize_6_requestedAt, stuckSize_6_finishedAt, stuckSize_stuckSize_6_returnCode, (stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step), (stuckSize_6_requestedAt < stuckSize_6_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckSize_6_index_15", main_stuckSize_6_index_15);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckSize_6_value_16", 0, main_stuckSize_6_value_16[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 5, "stuckKeys", 1, stuckKeys_pc, stuckKeys_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 8);
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
      $fwrite(o, ", %2d", stuckKeys_memory[32]);
      $fwrite(o, ", %2d", stuckKeys_memory[33]);
      $fwrite(o, ", %2d", stuckKeys_memory[34]);
      $fwrite(o, ", %2d", stuckKeys_memory[35]);
      $fwrite(o, ", %2d", stuckKeys_memory[36]);
      $fwrite(o, ", %2d", stuckKeys_memory[37]);
      $fwrite(o, ", %2d", stuckKeys_memory[38]);
      $fwrite(o, ", %2d", stuckKeys_memory[39]);
      $fwrite(o, ", %2d", stuckKeys_memory[40]);
      $fwrite(o, ", %2d", stuckKeys_memory[41]);
      $fwrite(o, ", %2d", stuckKeys_memory[42]);
      $fwrite(o, ", %2d", stuckKeys_memory[43]);
      $fwrite(o, ", %2d", stuckKeys_memory[44]);
      $fwrite(o, ", %2d", stuckKeys_memory[45]);
      $fwrite(o, ", %2d", stuckKeys_memory[46]);
      $fwrite(o, ", %2d", stuckKeys_memory[47]);
      $fwrite(o, ", %2d", stuckKeys_memory[48]);
      $fwrite(o, ", %2d", stuckKeys_memory[49]);
      $fwrite(o, ", %2d", stuckKeys_memory[50]);
      $fwrite(o, ", %2d", stuckKeys_memory[51]);
      $fwrite(o, ", %2d", stuckKeys_memory[52]);
      $fwrite(o, ", %2d", stuckKeys_memory[53]);
      $fwrite(o, ", %2d", stuckKeys_memory[54]);
      $fwrite(o, ", %2d", stuckKeys_memory[55]);
      $fwrite(o, ", %2d", stuckKeys_memory[56]);
      $fwrite(o, ", %2d", stuckKeys_memory[57]);
      $fwrite(o, ", %2d", stuckKeys_memory[58]);
      $fwrite(o, ", %2d", stuckKeys_memory[59]);
      $fwrite(o, ", %2d", stuckKeys_memory[60]);
      $fwrite(o, ", %2d", stuckKeys_memory[61]);
      $fwrite(o, ", %2d", stuckKeys_memory[62]);
      $fwrite(o, ", %2d", stuckKeys_memory[63]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 0, stuckKeys_stuckKeys_1_result_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 1, stuckKeys_stuckKeys_1_result_0[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 2, stuckKeys_stuckKeys_1_result_0[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 3, stuckKeys_stuckKeys_1_result_0[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 4, stuckKeys_stuckKeys_1_result_0[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 5, stuckKeys_stuckKeys_1_result_0[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 6, stuckKeys_stuckKeys_1_result_0[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 7, stuckKeys_stuckKeys_1_result_0[7]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_1_index_8", main_stuckKeys_1_index_8);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 0, stuckKeys_stuckKeys_1_result_0[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 1, stuckKeys_stuckKeys_1_result_0[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 2, stuckKeys_stuckKeys_1_result_0[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 3, stuckKeys_stuckKeys_1_result_0[3]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 4, stuckKeys_stuckKeys_1_result_0[4]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 5, stuckKeys_stuckKeys_1_result_0[5]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 6, stuckKeys_stuckKeys_1_result_0[6]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 7, stuckKeys_stuckKeys_1_result_0[7]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_2_index_9", main_stuckKeys_2_index_9);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 0, main_stuckKeys_2_value_10[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 1, main_stuckKeys_2_value_10[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 2, main_stuckKeys_2_value_10[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 3, main_stuckKeys_2_value_10[3]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 4, main_stuckKeys_2_value_10[4]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 5, main_stuckKeys_2_value_10[5]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 6, main_stuckKeys_2_value_10[6]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 7, main_stuckKeys_2_value_10[7]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "stuckData", 1, stuckData_pc, stuckData_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 8);
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
      $fwrite(o, ", %2d", stuckData_memory[32]);
      $fwrite(o, ", %2d", stuckData_memory[33]);
      $fwrite(o, ", %2d", stuckData_memory[34]);
      $fwrite(o, ", %2d", stuckData_memory[35]);
      $fwrite(o, ", %2d", stuckData_memory[36]);
      $fwrite(o, ", %2d", stuckData_memory[37]);
      $fwrite(o, ", %2d", stuckData_memory[38]);
      $fwrite(o, ", %2d", stuckData_memory[39]);
      $fwrite(o, ", %2d", stuckData_memory[40]);
      $fwrite(o, ", %2d", stuckData_memory[41]);
      $fwrite(o, ", %2d", stuckData_memory[42]);
      $fwrite(o, ", %2d", stuckData_memory[43]);
      $fwrite(o, ", %2d", stuckData_memory[44]);
      $fwrite(o, ", %2d", stuckData_memory[45]);
      $fwrite(o, ", %2d", stuckData_memory[46]);
      $fwrite(o, ", %2d", stuckData_memory[47]);
      $fwrite(o, ", %2d", stuckData_memory[48]);
      $fwrite(o, ", %2d", stuckData_memory[49]);
      $fwrite(o, ", %2d", stuckData_memory[50]);
      $fwrite(o, ", %2d", stuckData_memory[51]);
      $fwrite(o, ", %2d", stuckData_memory[52]);
      $fwrite(o, ", %2d", stuckData_memory[53]);
      $fwrite(o, ", %2d", stuckData_memory[54]);
      $fwrite(o, ", %2d", stuckData_memory[55]);
      $fwrite(o, ", %2d", stuckData_memory[56]);
      $fwrite(o, ", %2d", stuckData_memory[57]);
      $fwrite(o, ", %2d", stuckData_memory[58]);
      $fwrite(o, ", %2d", stuckData_memory[59]);
      $fwrite(o, ", %2d", stuckData_memory[60]);
      $fwrite(o, ", %2d", stuckData_memory[61]);
      $fwrite(o, ", %2d", stuckData_memory[62]);
      $fwrite(o, ", %2d", stuckData_memory[63]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 0, stuckData_stuckData_3_result_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 1, stuckData_stuckData_3_result_0[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 2, stuckData_stuckData_3_result_0[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 3, stuckData_stuckData_3_result_0[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 4, stuckData_stuckData_3_result_0[4]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 5, stuckData_stuckData_3_result_0[5]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 6, stuckData_stuckData_3_result_0[6]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 7, stuckData_stuckData_3_result_0[7]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_3_index_11", main_stuckData_3_index_11);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 0, stuckData_stuckData_3_result_0[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 1, stuckData_stuckData_3_result_0[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 2, stuckData_stuckData_3_result_0[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 3, stuckData_stuckData_3_result_0[3]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 4, stuckData_stuckData_3_result_0[4]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 5, stuckData_stuckData_3_result_0[5]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 6, stuckData_stuckData_3_result_0[6]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 7, stuckData_stuckData_3_result_0[7]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_4_index_12", main_stuckData_4_index_12);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 0, main_stuckData_4_value_13[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 1, main_stuckData_4_value_13[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 2, main_stuckData_4_value_13[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 3, main_stuckData_4_value_13[3]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 4, main_stuckData_4_value_13[4]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 5, main_stuckData_4_value_13[5]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 6, main_stuckData_4_value_13[6]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 7, main_stuckData_4_value_13[7]);
      $fclose(o);
    end
  endtask
endmodule
