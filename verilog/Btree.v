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

  assign stop = (main_stop != 0 ? 1 : 0) || (stuckIsLeaf_stop != 0 ? 1 : 0) || (stuckIsFree_stop != 0 ? 1 : 0) || (freeNext_stop != 0 ? 1 : 0) || (stuckSize_stop != 0 ? 1 : 0) || (stuckKeys_stop != 0 ? 1 : 0) || (stuckData_stop != 0 ? 1 : 0) || (stucksUsed_stop != 0 ? 1 : 0);                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 100;
    reset = 1; clock = 0; #1; clock = 1; #1; reset = 0; #1                      // Reset to known state
    for(step = 0; step < maxSteps && !stop; step = step + 1) begin              // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

      processCurrent = 0; clock = 0; #1; clock = 1; #1; // process_main_0000
      processCurrent = 1; clock = 0; #1; clock = 1; #1; // process_stuckIsLeaf_0001
      processCurrent = 2; clock = 0; #1; clock = 1; #1; // process_stuckIsFree_0002
      processCurrent = 3; clock = 0; #1; clock = 1; #1; // process_freeNext_0003
      processCurrent = 4; clock = 0; #1; clock = 1; #1; // process_stuckSize_0004
      processCurrent = 5; clock = 0; #1; clock = 1; #1; // process_stuckKeys_0005
      processCurrent = 6; clock = 0; #1; clock = 1; #1; // process_stuckData_0006
      processCurrent = 7; clock = 0; #1; clock = 1; #1; // process_stucksUsed_0007
      if (step >= 0) chipPrint();                                            // Steps prior to zero are for initialization to make Java and Verilog match
    end
    if (!stop) $finish(1); else $finish(0);                                // Set return code depending on whether the simulation halted
  end
  // Process: main  process_main_0000
  reg [0-1:0] main_value_0;
  reg [4-1:0] main_index_1;
  reg [3-1:0] main_size_2;
  reg [1-1:0] main_isLeaf_3;
  reg [4-1:0] main_nextFree_4;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_5[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_6[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_7[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_8[4];
  reg [3-1:0] main_stuckKeys_1_index_9;
  reg [3-1:0] main_stuckKeys_2_index_10;
  (* nomem2reg *)
  reg [8-1:0] main_stuckKeys_2_value_11[4];
  reg [3-1:0] main_stuckData_3_index_12;
  reg [3-1:0] main_stuckData_4_index_13;
  (* nomem2reg *)
  reg [8-1:0] main_stuckData_4_value_14[4];
  reg [3-1:0] main_stuckSize_5_index_15;
  reg [3-1:0] main_stuckSize_6_index_16;
  (* nomem2reg *)
  reg [3-1:0] main_stuckSize_6_value_17[1];
  reg [3-1:0] main_stuckIsLeaf_7_index_18;
  reg [3-1:0] main_stuckIsLeaf_8_index_19;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsLeaf_8_value_20[1];
  reg [1-1:0] main_Found_21;
  reg [8-1:0] main_Key_22;
  reg [8-1:0] main_FoundKey_23;
  reg [8-1:0] main_Data_24;
  reg [4-1:0] main_BtreeIndex_25;
  reg [3-1:0] main_StuckIndex_26;
  reg [1-1:0] main_MergeSuccess_27;
  reg [4-1:0] main_index_28;
  reg [3-1:0] main_size_29;
  reg [1-1:0] main_isLeaf_30;
  reg [4-1:0] main_nextFree_31;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_32[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_33[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_34[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_35[4];
  reg [1-1:0] main_Found_36;
  reg [8-1:0] main_Key_37;
  reg [8-1:0] main_FoundKey_38;
  reg [8-1:0] main_Data_39;
  reg [4-1:0] main_BtreeIndex_40;
  reg [3-1:0] main_StuckIndex_41;
  reg [1-1:0] main_MergeSuccess_42;
  reg [4-1:0] main_index_43;
  reg [3-1:0] main_size_44;
  reg [1-1:0] main_isLeaf_45;
  reg [4-1:0] main_nextFree_46;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_47[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_48[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_49[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_50[4];
  reg [1-1:0] main_Found_51;
  reg [8-1:0] main_Key_52;
  reg [8-1:0] main_FoundKey_53;
  reg [8-1:0] main_Data_54;
  reg [4-1:0] main_BtreeIndex_55;
  reg [3-1:0] main_StuckIndex_56;
  reg [1-1:0] main_MergeSuccess_57;
  reg [4-1:0] main_index_58;
  reg [3-1:0] main_size_59;
  reg [1-1:0] main_isLeaf_60;
  reg [4-1:0] main_nextFree_61;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_62[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_63[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_64[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_65[4];
  reg [1-1:0] main_Found_66;
  reg [8-1:0] main_Key_67;
  reg [8-1:0] main_FoundKey_68;
  reg [8-1:0] main_Data_69;
  reg [4-1:0] main_BtreeIndex_70;
  reg [3-1:0] main_StuckIndex_71;
  reg [1-1:0] main_MergeSuccess_72;
  reg [4-1:0] main_index_73;
  reg [3-1:0] main_size_74;
  reg [1-1:0] main_isLeaf_75;
  reg [4-1:0] main_nextFree_76;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_77[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_78[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_79[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_80[4];
  reg [1-1:0] main_Found_81;
  reg [8-1:0] main_Key_82;
  reg [8-1:0] main_FoundKey_83;
  reg [8-1:0] main_Data_84;
  reg [4-1:0] main_BtreeIndex_85;
  reg [3-1:0] main_StuckIndex_86;
  reg [1-1:0] main_MergeSuccess_87;
  reg [4-1:0] main_index_88;
  reg [3-1:0] main_size_89;
  reg [1-1:0] main_isLeaf_90;
  reg [4-1:0] main_nextFree_91;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_92[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_93[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_94[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_95[4];
  reg [1-1:0] main_Found_96;
  reg [8-1:0] main_Key_97;
  reg [8-1:0] main_FoundKey_98;
  reg [8-1:0] main_Data_99;
  reg [4-1:0] main_BtreeIndex_100;
  reg [3-1:0] main_StuckIndex_101;
  reg [1-1:0] main_MergeSuccess_102;
  reg [4-1:0] main_index_103;
  reg [3-1:0] main_size_104;
  reg [1-1:0] main_isLeaf_105;
  reg [4-1:0] main_nextFree_106;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_107[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_108[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_109[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_110[4];
  reg [1-1:0] main_Found_111;
  reg [8-1:0] main_Key_112;
  reg [8-1:0] main_FoundKey_113;
  reg [8-1:0] main_Data_114;
  reg [4-1:0] main_BtreeIndex_115;
  reg [3-1:0] main_StuckIndex_116;
  reg [1-1:0] main_MergeSuccess_117;
  reg [4-1:0] main_index_118;
  reg [3-1:0] main_size_119;
  reg [1-1:0] main_isLeaf_120;
  reg [4-1:0] main_nextFree_121;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_122[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_123[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_124[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_125[4];
  reg [1-1:0] main_Found_126;
  reg [8-1:0] main_Key_127;
  reg [8-1:0] main_FoundKey_128;
  reg [8-1:0] main_Data_129;
  reg [4-1:0] main_BtreeIndex_130;
  reg [3-1:0] main_StuckIndex_131;
  reg [1-1:0] main_MergeSuccess_132;
  reg [4-1:0] main_index_133;
  reg [3-1:0] main_size_134;
  reg [1-1:0] main_isLeaf_135;
  reg [4-1:0] main_nextFree_136;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_137[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_138[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_139[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_140[4];
  reg [1-1:0] main_Found_141;
  reg [8-1:0] main_Key_142;
  reg [8-1:0] main_FoundKey_143;
  reg [8-1:0] main_Data_144;
  reg [4-1:0] main_BtreeIndex_145;
  reg [3-1:0] main_StuckIndex_146;
  reg [1-1:0] main_MergeSuccess_147;
  reg [4-1:0] main_sum_148;
  reg [1-1:0] main_can_149;
  reg [4-1:0] main_indexLeft_150;
  reg [4-1:0] main_indexRight_151;
  reg [8-1:0] main_midKey_152;
  reg [3-1:0] main_freeNext_9_index_153;
  (* nomem2reg *)
  reg [4-1:0] main_freeNext_9_value_154[1];
  reg [3-1:0] main_stuckIsFree_10_index_155;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsFree_10_value_156[1];
  reg [0-1:0] main_stucksUsed_11_index_157;
  (* nomem2reg *)
  reg [4-1:0] main_stucksUsed_11_value_158[1];
  reg [4-1:0] main_root_159;
  reg [3-1:0] main_rootSize_160;
  reg [1-1:0] main_true_161;
  reg [1-1:0] main_false_162;
  reg [4-1:0] main_rootUsed_163;
  reg [4-1:0] main_index_164;
  reg [3-1:0] main_size_165;
  reg [1-1:0] main_isLeaf_166;
  reg [4-1:0] main_nextFree_167;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_168[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_169[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_170[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_171[4];
  reg [1-1:0] main_Found_172;
  reg [8-1:0] main_Key_173;
  reg [8-1:0] main_FoundKey_174;
  reg [8-1:0] main_Data_175;
  reg [4-1:0] main_BtreeIndex_176;
  reg [3-1:0] main_StuckIndex_177;
  reg [1-1:0] main_MergeSuccess_178;
  reg [8-1:0] main_d_179;
  reg [4-1:0] main_n_180;
  reg [4-1:0] main_index_181;
  reg [3-1:0] main_size_182;
  reg [1-1:0] main_isLeaf_183;
  reg [4-1:0] main_nextFree_184;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_185[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_186[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_187[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_188[4];
  reg [1-1:0] main_Found_189;
  reg [8-1:0] main_Key_190;
  reg [8-1:0] main_FoundKey_191;
  reg [8-1:0] main_Data_192;
  reg [4-1:0] main_BtreeIndex_193;
  reg [3-1:0] main_StuckIndex_194;
  reg [1-1:0] main_MergeSuccess_195;
  reg [8-1:0] main_d_196;
  reg [4-1:0] main_n_197;
  reg [4-1:0] main_index_198;
  reg [3-1:0] main_size_199;
  reg [1-1:0] main_isLeaf_200;
  reg [4-1:0] main_nextFree_201;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_202[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_203[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_204[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_205[4];
  reg [1-1:0] main_Found_206;
  reg [8-1:0] main_Key_207;
  reg [8-1:0] main_FoundKey_208;
  reg [8-1:0] main_Data_209;
  reg [4-1:0] main_BtreeIndex_210;
  reg [3-1:0] main_StuckIndex_211;
  reg [1-1:0] main_MergeSuccess_212;
  reg [8-1:0] main_d_213;
  reg [4-1:0] main_n_214;
  reg [8-1:0] main_Left_215;
  reg [8-1:0] main_Right_216;
  reg [4-1:0] main_index_217;
  reg [3-1:0] main_size_218;
  reg [1-1:0] main_isLeaf_219;
  reg [4-1:0] main_nextFree_220;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_221[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_222[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_223[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_224[4];
  reg [1-1:0] main_Found_225;
  reg [8-1:0] main_Key_226;
  reg [8-1:0] main_FoundKey_227;
  reg [8-1:0] main_Data_228;
  reg [4-1:0] main_BtreeIndex_229;
  reg [3-1:0] main_StuckIndex_230;
  reg [1-1:0] main_MergeSuccess_231;
  reg [4-1:0] main_index_232;
  reg [3-1:0] main_size_233;
  reg [1-1:0] main_isLeaf_234;
  reg [4-1:0] main_nextFree_235;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_236[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_237[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_238[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_239[4];
  reg [1-1:0] main_Found_240;
  reg [8-1:0] main_Key_241;
  reg [8-1:0] main_FoundKey_242;
  reg [8-1:0] main_Data_243;
  reg [4-1:0] main_BtreeIndex_244;
  reg [3-1:0] main_StuckIndex_245;
  reg [1-1:0] main_MergeSuccess_246;
  reg [4-1:0] main_index_247;
  reg [3-1:0] main_size_248;
  reg [1-1:0] main_isLeaf_249;
  reg [4-1:0] main_nextFree_250;
  (* nomem2reg *)
  reg [8-1:0] main_Keys_251[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_252[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_253[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_254[4];
  reg [1-1:0] main_Found_255;
  reg [8-1:0] main_Key_256;
  reg [8-1:0] main_FoundKey_257;
  reg [8-1:0] main_Data_258;
  reg [4-1:0] main_BtreeIndex_259;
  reg [3-1:0] main_StuckIndex_260;
  reg [1-1:0] main_MergeSuccess_261;
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
      main_value_0     <= 0;
      main_index_1     <= 0;
      main_size_2      <= 0;
      main_isLeaf_3    <= 0;
      main_nextFree_4  <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_5[main_memory_index]   <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_6[main_memory_index]               <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_7[main_memory_index]               <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_8[main_memory_index]   <= 0;
        end
      end
      main_stuckKeys_1_index_9         <= 0;
      main_stuckKeys_2_index_10        <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_stuckKeys_2_value_11[main_memory_index]     <= 0;
        end
      end
      main_stuckData_3_index_12        <= 0;
      main_stuckData_4_index_13        <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_stuckData_4_value_14[main_memory_index]     <= 0;
        end
      end
      main_stuckSize_5_index_15        <= 0;
      main_stuckSize_6_index_16        <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckSize_6_value_17[main_memory_index]     <= 0;
        end
      end
      main_stuckIsLeaf_7_index_18      <= 0;
      main_stuckIsLeaf_8_index_19      <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckIsLeaf_8_value_20[main_memory_index]   <= 0;
        end
      end
      main_Found_21    <= 0;
      main_Key_22      <= 0;
      main_FoundKey_23                 <= 0;
      main_Data_24     <= 0;
      main_BtreeIndex_25               <= 0;
      main_StuckIndex_26               <= 0;
      main_MergeSuccess_27             <= 0;
      main_index_28    <= 0;
      main_size_29     <= 0;
      main_isLeaf_30   <= 0;
      main_nextFree_31                 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_32[main_memory_index]  <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_33[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_34[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_35[main_memory_index]  <= 0;
        end
      end
      main_Found_36    <= 0;
      main_Key_37      <= 0;
      main_FoundKey_38                 <= 0;
      main_Data_39     <= 0;
      main_BtreeIndex_40               <= 0;
      main_StuckIndex_41               <= 0;
      main_MergeSuccess_42             <= 0;
      main_index_43    <= 0;
      main_size_44     <= 0;
      main_isLeaf_45   <= 0;
      main_nextFree_46                 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_47[main_memory_index]  <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_48[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_49[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_50[main_memory_index]  <= 0;
        end
      end
      main_Found_51    <= 0;
      main_Key_52      <= 0;
      main_FoundKey_53                 <= 0;
      main_Data_54     <= 0;
      main_BtreeIndex_55               <= 0;
      main_StuckIndex_56               <= 0;
      main_MergeSuccess_57             <= 0;
      main_index_58    <= 0;
      main_size_59     <= 0;
      main_isLeaf_60   <= 0;
      main_nextFree_61                 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_62[main_memory_index]  <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_63[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_64[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_65[main_memory_index]  <= 0;
        end
      end
      main_Found_66    <= 0;
      main_Key_67      <= 0;
      main_FoundKey_68                 <= 0;
      main_Data_69     <= 0;
      main_BtreeIndex_70               <= 0;
      main_StuckIndex_71               <= 0;
      main_MergeSuccess_72             <= 0;
      main_index_73    <= 0;
      main_size_74     <= 0;
      main_isLeaf_75   <= 0;
      main_nextFree_76                 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_77[main_memory_index]  <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_78[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_79[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_80[main_memory_index]  <= 0;
        end
      end
      main_Found_81    <= 0;
      main_Key_82      <= 0;
      main_FoundKey_83                 <= 0;
      main_Data_84     <= 0;
      main_BtreeIndex_85               <= 0;
      main_StuckIndex_86               <= 0;
      main_MergeSuccess_87             <= 0;
      main_index_88    <= 0;
      main_size_89     <= 0;
      main_isLeaf_90   <= 0;
      main_nextFree_91                 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_92[main_memory_index]  <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_93[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_94[main_memory_index]              <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_95[main_memory_index]  <= 0;
        end
      end
      main_Found_96    <= 0;
      main_Key_97      <= 0;
      main_FoundKey_98                 <= 0;
      main_Data_99     <= 0;
      main_BtreeIndex_100              <= 0;
      main_StuckIndex_101              <= 0;
      main_MergeSuccess_102            <= 0;
      main_index_103   <= 0;
      main_size_104    <= 0;
      main_isLeaf_105  <= 0;
      main_nextFree_106                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_107[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_108[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_109[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_110[main_memory_index]                 <= 0;
        end
      end
      main_Found_111   <= 0;
      main_Key_112     <= 0;
      main_FoundKey_113                <= 0;
      main_Data_114    <= 0;
      main_BtreeIndex_115              <= 0;
      main_StuckIndex_116              <= 0;
      main_MergeSuccess_117            <= 0;
      main_index_118   <= 0;
      main_size_119    <= 0;
      main_isLeaf_120  <= 0;
      main_nextFree_121                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_122[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_123[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_124[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_125[main_memory_index]                 <= 0;
        end
      end
      main_Found_126   <= 0;
      main_Key_127     <= 0;
      main_FoundKey_128                <= 0;
      main_Data_129    <= 0;
      main_BtreeIndex_130              <= 0;
      main_StuckIndex_131              <= 0;
      main_MergeSuccess_132            <= 0;
      main_index_133   <= 0;
      main_size_134    <= 0;
      main_isLeaf_135  <= 0;
      main_nextFree_136                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_137[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_138[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_139[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_140[main_memory_index]                 <= 0;
        end
      end
      main_Found_141   <= 0;
      main_Key_142     <= 0;
      main_FoundKey_143                <= 0;
      main_Data_144    <= 0;
      main_BtreeIndex_145              <= 0;
      main_StuckIndex_146              <= 0;
      main_MergeSuccess_147            <= 0;
      main_sum_148     <= 0;
      main_can_149     <= 0;
      main_indexLeft_150               <= 0;
      main_indexRight_151              <= 0;
      main_midKey_152  <= 0;
      main_freeNext_9_index_153        <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_freeNext_9_value_154[main_memory_index]     <= 0;
        end
      end
      main_stuckIsFree_10_index_155    <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckIsFree_10_value_156[main_memory_index]                 <= 0;
        end
      end
      main_stucksUsed_11_index_157     <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stucksUsed_11_value_158[main_memory_index]  <= 0;
        end
      end
      main_root_159    <= 0;
      main_rootSize_160                <= 0;
      main_true_161    <= 0;
      main_false_162   <= 0;
      main_rootUsed_163                <= 0;
      main_index_164   <= 0;
      main_size_165    <= 0;
      main_isLeaf_166  <= 0;
      main_nextFree_167                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_168[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_169[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_170[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_171[main_memory_index]                 <= 0;
        end
      end
      main_Found_172   <= 0;
      main_Key_173     <= 0;
      main_FoundKey_174                <= 0;
      main_Data_175    <= 0;
      main_BtreeIndex_176              <= 0;
      main_StuckIndex_177              <= 0;
      main_MergeSuccess_178            <= 0;
      main_d_179       <= 0;
      main_n_180       <= 0;
      main_index_181   <= 0;
      main_size_182    <= 0;
      main_isLeaf_183  <= 0;
      main_nextFree_184                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_185[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_186[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_187[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_188[main_memory_index]                 <= 0;
        end
      end
      main_Found_189   <= 0;
      main_Key_190     <= 0;
      main_FoundKey_191                <= 0;
      main_Data_192    <= 0;
      main_BtreeIndex_193              <= 0;
      main_StuckIndex_194              <= 0;
      main_MergeSuccess_195            <= 0;
      main_d_196       <= 0;
      main_n_197       <= 0;
      main_index_198   <= 0;
      main_size_199    <= 0;
      main_isLeaf_200  <= 0;
      main_nextFree_201                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_202[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_203[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_204[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_205[main_memory_index]                 <= 0;
        end
      end
      main_Found_206   <= 0;
      main_Key_207     <= 0;
      main_FoundKey_208                <= 0;
      main_Data_209    <= 0;
      main_BtreeIndex_210              <= 0;
      main_StuckIndex_211              <= 0;
      main_MergeSuccess_212            <= 0;
      main_d_213       <= 0;
      main_n_214       <= 0;
      main_Left_215    <= 0;
      main_Right_216   <= 0;
      main_index_217   <= 0;
      main_size_218    <= 0;
      main_isLeaf_219  <= 0;
      main_nextFree_220                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_221[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_222[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_223[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_224[main_memory_index]                 <= 0;
        end
      end
      main_Found_225   <= 0;
      main_Key_226     <= 0;
      main_FoundKey_227                <= 0;
      main_Data_228    <= 0;
      main_BtreeIndex_229              <= 0;
      main_StuckIndex_230              <= 0;
      main_MergeSuccess_231            <= 0;
      main_index_232   <= 0;
      main_size_233    <= 0;
      main_isLeaf_234  <= 0;
      main_nextFree_235                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_236[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_237[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_238[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_239[main_memory_index]                 <= 0;
        end
      end
      main_Found_240   <= 0;
      main_Key_241     <= 0;
      main_FoundKey_242                <= 0;
      main_Data_243    <= 0;
      main_BtreeIndex_244              <= 0;
      main_StuckIndex_245              <= 0;
      main_MergeSuccess_246            <= 0;
      main_index_247   <= 0;
      main_size_248    <= 0;
      main_isLeaf_249  <= 0;
      main_nextFree_250                <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Keys_251[main_memory_index]                 <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_252[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_253[main_memory_index]             <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_254[main_memory_index]                 <= 0;
        end
      end
      main_Found_255   <= 0;
      main_Key_256     <= 0;
      main_FoundKey_257                <= 0;
      main_Data_258    <= 0;
      main_BtreeIndex_259              <= 0;
      main_StuckIndex_260              <= 0;
      main_MergeSuccess_261            <= 0;
      stuckIsLeaf_7_requestedAt        <= -1;
      stuckIsLeaf_8_requestedAt        <= -1;
      stuckIsFree_10_requestedAt       <= -1;
      freeNext_9_requestedAt           <= -1;
      stuckSize_5_requestedAt          <= -1;
      stuckSize_6_requestedAt          <= -1;
      stuckKeys_1_requestedAt          <= -1;
      stuckKeys_2_requestedAt          <= -1;
      stuckData_3_requestedAt          <= -1;
      stuckData_4_requestedAt          <= -1;
      stucksUsed_11_requestedAt        <= -1;
    end
    else begin
      if (processCurrent == 0) begin
        case(main_pc)
          0: begin
            main_index_217   <= 0;
            main_pc          <= main_pc + 1;
            case (main_pc)
              0: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Chip.java:1415:<init>|  Chip.java:1414:Zero|  Btree.java:0390:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          1: begin
            main_stuckSize_5_index_15        <= main_index_217;
            stuckSize_5_requestedAt          <= step;
            stuckSize_5_finishedAt           <= -1;
            main_stuckIsLeaf_7_index_18      <= main_index_217;
            stuckIsLeaf_7_requestedAt        <= step;
            stuckIsLeaf_7_finishedAt         <= -1;
            main_stuckKeys_1_index_9         <= main_index_217;
            stuckKeys_1_requestedAt          <= step;
            stuckKeys_1_finishedAt           <= -1;
            main_stuckData_3_index_12        <= main_index_217;
            stuckData_3_requestedAt          <= step;
            stuckData_3_finishedAt           <= -1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              1: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0396:<init>|  Btree.java:0395:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          2, 10, 17: begin
            if ((stuckSize_5_requestedAt < stuckSize_5_finishedAt)) begin
              main_pc          <= main_pc + 1;
            end
            case (main_pc)
              2: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0409:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              10: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0409:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              17: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0409:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          3, 11, 18: begin
            if ((stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt)) begin
              main_pc          <= main_pc + 1;
            end
            case (main_pc)
              3: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0410:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              11: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0410:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              18: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0410:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          4, 12, 19: begin
            if ((stuckKeys_1_requestedAt < stuckKeys_1_finishedAt)) begin
              main_pc          <= main_pc + 1;
            end
            case (main_pc)
              4: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0411:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              12: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0411:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              19: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0411:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          5, 13, 20: begin
            if ((stuckData_3_requestedAt < stuckData_3_finishedAt)) begin
              main_pc          <= main_pc + 1;
            end
            case (main_pc)
              5: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0412:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              13: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0412:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
              20: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:2068:<init>|  Chip.java:2067:waitResultOfTransaction|  Btree.java:0412:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          6: begin
            main_size_218    <= stuckSize_stuckSize_5_result_1[0];
            main_isLeaf_219  <= stuckIsLeaf_stuckIsLeaf_7_result_1[0];
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Keys_221[main_memory_index]                 <= stuckKeys_stuckKeys_1_result_1[main_memory_index];
              end
            end
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Data_224[main_memory_index]                 <= stuckData_stuckData_3_result_1[main_memory_index];
              end
            end
            main_pc          <= main_pc + 1;
            case (main_pc)
              6: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0415:<init>|  Btree.java:0414:stuckGet|  Btree.java:0391:stuckGetRoot|  Btree.java:4111:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          7: begin
            main_Left_215    <= 1;
            main_Right_216   <= 2;
            main_pc          <= main_pc + 1;
            case (main_pc)
              7: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:4113:<init>|  Btree.java:4112:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          8: begin
            main_index_232   <= main_Left_215;
            main_pc          <= main_pc + 1;
            case (main_pc)
              8: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Chip.java:1033:<init>|  Chip.java:1032:Copy|  Btree.java:0385:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          9: begin
            main_stuckSize_5_index_15        <= main_index_232;
            stuckSize_5_requestedAt          <= step;
            stuckSize_5_finishedAt           <= -1;
            main_stuckIsLeaf_7_index_18      <= main_index_232;
            stuckIsLeaf_7_requestedAt        <= step;
            stuckIsLeaf_7_finishedAt         <= -1;
            main_stuckKeys_1_index_9         <= main_index_232;
            stuckKeys_1_requestedAt          <= step;
            stuckKeys_1_finishedAt           <= -1;
            main_stuckData_3_index_12        <= main_index_232;
            stuckData_3_requestedAt          <= step;
            stuckData_3_finishedAt           <= -1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              9: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0396:<init>|  Btree.java:0395:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          14: begin
            main_size_233    <= stuckSize_stuckSize_5_result_1[0];
            main_isLeaf_234  <= stuckIsLeaf_stuckIsLeaf_7_result_1[0];
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Keys_236[main_memory_index]                 <= stuckKeys_stuckKeys_1_result_1[main_memory_index];
              end
            end
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Data_239[main_memory_index]                 <= stuckData_stuckData_3_result_1[main_memory_index];
              end
            end
            main_pc          <= main_pc + 1;
            case (main_pc)
              14: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0415:<init>|  Btree.java:0414:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4122:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          15: begin
            main_index_247   <= main_Right_216;
            main_pc          <= main_pc + 1;
            case (main_pc)
              15: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Chip.java:1033:<init>|  Chip.java:1032:Copy|  Btree.java:0385:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          16: begin
            main_stuckSize_5_index_15        <= main_index_247;
            stuckSize_5_requestedAt          <= step;
            stuckSize_5_finishedAt           <= -1;
            main_stuckIsLeaf_7_index_18      <= main_index_247;
            stuckIsLeaf_7_requestedAt        <= step;
            stuckIsLeaf_7_finishedAt         <= -1;
            main_stuckKeys_1_index_9         <= main_index_247;
            stuckKeys_1_requestedAt          <= step;
            stuckKeys_1_finishedAt           <= -1;
            main_stuckData_3_index_12        <= main_index_247;
            stuckData_3_requestedAt          <= step;
            stuckData_3_finishedAt           <= -1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              16: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0396:<init>|  Btree.java:0395:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          21: begin
            main_size_248    <= stuckSize_stuckSize_5_result_1[0];
            main_isLeaf_249  <= stuckIsLeaf_stuckIsLeaf_7_result_1[0];
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Keys_251[main_memory_index]                 <= stuckKeys_stuckKeys_1_result_1[main_memory_index];
              end
            end
            begin
              for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
                main_Data_254[main_memory_index]                 <= stuckData_stuckData_3_result_1[main_memory_index];
              end
            end
            main_pc          <= main_pc + 1;
            case (main_pc)
              21: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:0415:<init>|  Btree.java:0414:stuckGet|  Btree.java:0386:stuckGet|  Btree.java:4123:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          22: begin
            main_size_218    <= main_size_218 - 1;
            main_pc          <= main_pc + 1;
            case (main_pc)
              22: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:4126:<init>|  Btree.java:4125:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
                  $fclose(f);
                end
              end
            endcase
          end
          23: begin
            begin
              for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
                main_Keys_236[main_memory_index]                 <= main_Keys_221[main_memory_index];
                main_Data_239[main_memory_index]                 <= main_Data_224[main_memory_index];
                main_Keys_251[main_memory_index]                 <= main_Keys_221[main_memory_index+2];
                main_Data_254[main_memory_index]                 <= main_Data_224[main_memory_index+2];
              end
            end
            main_size_233    <= 1;
            main_Data_239[1]                 <= main_Data_224[1];
            main_size_248    <= main_size_218-2;
            main_Data_254[1]                 <= main_Data_224[3];
            main_pc          <= main_pc + 1;
            case (main_pc)
              23: begin
                begin
                  integer f;
                  f = $fopen("verilog/trace_verilog.txt", "a");
                  $fdisplay(f, "Location: Chip.java:0684:<init>|  Btree.java:1379:<init>|  Btree.java:1378:splitIntoThree|  Btree.java:4134:test_splitIntoThree|  Btree.java:7203:oldTests|  Btree.java:7235:newTests|  Btree.java:7243:main|");
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
  reg [1*1-1:0] stuckIsLeaf_memory[8];
  reg [1-1:0] stuckIsLeaf_value_0;
  (* nomem2reg *)
  reg [1-1:0] stuckIsLeaf_stuckIsLeaf_7_result_1[1];
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
      stuckIsLeaf_pc   <= 0;
      stuckIsLeaf_stop                 <= 0;
      stuckIsLeaf_returnCode           <= 0;
      stuckIsLeaf_memory_index         <= 0;
      stuckIsLeaf_memory_value         <= 0;
      stuckIsLeaf_value_0              <= 0;
      begin
        for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
          stuckIsLeaf_stuckIsLeaf_7_result_1[stuckIsLeaf_memory_index]     <= 0;
        end
      end
      stuckIsLeaf_7_finishedAt         <= -1;
      stuckIsLeaf_stuckIsLeaf_7_returnCode             <= 0;
      stuckIsLeaf_8_finishedAt         <= -1;
      stuckIsLeaf_stuckIsLeaf_8_returnCode             <= 0;
      stuckIsLeaf_memory[0]            <= {1'd1};
      stuckIsLeaf_memory[1]            <= {1'd1};
      stuckIsLeaf_memory[2]            <= {1'd1};
      stuckIsLeaf_memory[3]            <= {1'bx};
      stuckIsLeaf_memory[4]            <= {1'bx};
      stuckIsLeaf_memory[5]            <= {1'bx};
      stuckIsLeaf_memory[6]            <= {1'bx};
      stuckIsLeaf_memory[7]            <= {1'bx};
    end
    else begin
      if (processCurrent == 1) begin
        case(stuckIsLeaf_pc)
          0: begin
            if ((stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step)) begin
              begin
                for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
                  stuckIsLeaf_stuckIsLeaf_7_result_1[stuckIsLeaf_memory_index]     <= stuckIsLeaf_memory[main_stuckIsLeaf_7_index_18][stuckIsLeaf_memory_index*1+:1];
                end
              end
              stuckIsLeaf_7_finishedAt         <= step;
            end
            else if ((stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step)) begin
              stuckIsLeaf_memory[main_stuckIsLeaf_8_index_19]  <= {main_stuckIsLeaf_8_value_20[0]};
              stuckIsLeaf_8_finishedAt         <= step;
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
  reg [1*1-1:0] stuckIsFree_memory[8];
  reg [1-1:0] stuckIsFree_value_0;
  integer stuckIsFree_10_requestedAt;
  integer stuckIsFree_10_finishedAt;
  integer stuckIsFree_stuckIsFree_10_returnCode;
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  integer stuckIsFree_memory_index;
  reg[1-1:0] stuckIsFree_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckIsFree_pc   <= 0;
      stuckIsFree_stop                 <= 0;
      stuckIsFree_returnCode           <= 0;
      stuckIsFree_memory_index         <= 0;
      stuckIsFree_memory_value         <= 0;
      stuckIsFree_value_0              <= 0;
      stuckIsFree_10_finishedAt        <= -1;
      stuckIsFree_stuckIsFree_10_returnCode            <= 0;
      stuckIsFree_memory[0]            <= {1'bx};
      stuckIsFree_memory[1]            <= {1'bx};
      stuckIsFree_memory[2]            <= {1'bx};
      stuckIsFree_memory[3]            <= {1'bx};
      stuckIsFree_memory[4]            <= {1'bx};
      stuckIsFree_memory[5]            <= {1'bx};
      stuckIsFree_memory[6]            <= {1'bx};
      stuckIsFree_memory[7]            <= {1'bx};
    end
    else begin
      if (processCurrent == 2) begin
        case(stuckIsFree_pc)
          0: begin
            if ((stuckIsFree_10_requestedAt > stuckIsFree_10_finishedAt && stuckIsFree_10_requestedAt != step)) begin
              stuckIsFree_memory[main_stuckIsFree_10_index_155]                <= {main_stuckIsFree_10_value_156[0]};
              stuckIsFree_10_finishedAt        <= step;
            end
            else begin
            end
          end
          default: stuckIsFree_stop <= 1;
        endcase
      end
    end
  end
  // Process: freeNext  process_freeNext_0003
  (* ram_style = "block" *)
  reg [4*1-1:0] freeNext_memory[8];
  reg [4-1:0] freeNext_value_0;
  integer freeNext_9_requestedAt;
  integer freeNext_9_finishedAt;
  integer freeNext_freeNext_9_returnCode;
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  integer freeNext_memory_index;
  reg[4-1:0] freeNext_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      freeNext_pc      <= 0;
      freeNext_stop    <= 0;
      freeNext_returnCode              <= 0;
      freeNext_memory_index            <= 0;
      freeNext_memory_value            <= 0;
      freeNext_value_0                 <= 0;
      freeNext_9_finishedAt            <= -1;
      freeNext_freeNext_9_returnCode   <= 0;
      freeNext_memory[0]               <= {4'bx};
      freeNext_memory[1]               <= {4'bx};
      freeNext_memory[2]               <= {4'bx};
      freeNext_memory[3]               <= {4'bx};
      freeNext_memory[4]               <= {4'bx};
      freeNext_memory[5]               <= {4'bx};
      freeNext_memory[6]               <= {4'bx};
      freeNext_memory[7]               <= {4'bx};
    end
    else begin
      if (processCurrent == 3) begin
        case(freeNext_pc)
          0: begin
            if ((freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step)) begin
              freeNext_memory[main_freeNext_9_index_153]       <= {main_freeNext_9_value_154[0]};
              freeNext_9_finishedAt            <= step;
            end
            else begin
            end
          end
          default: freeNext_stop <= 1;
        endcase
      end
    end
  end
  // Process: stuckSize  process_stuckSize_0004
  (* ram_style = "block" *)
  reg [3*1-1:0] stuckSize_memory[8];
  reg [3-1:0] stuckSize_value_0;
  (* nomem2reg *)
  reg [3-1:0] stuckSize_stuckSize_5_result_1[1];
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
  reg[3-1:0] stuckSize_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stuckSize_pc     <= 0;
      stuckSize_stop   <= 0;
      stuckSize_returnCode             <= 0;
      stuckSize_memory_index           <= 0;
      stuckSize_memory_value           <= 0;
      stuckSize_value_0                <= 0;
      begin
        for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
          stuckSize_stuckSize_5_result_1[stuckSize_memory_index]           <= 0;
        end
      end
      stuckSize_5_finishedAt           <= -1;
      stuckSize_stuckSize_5_returnCode                 <= 0;
      stuckSize_6_finishedAt           <= -1;
      stuckSize_stuckSize_6_returnCode                 <= 0;
      stuckSize_memory[0]              <= {3'd4};
      stuckSize_memory[1]              <= {3'd4};
      stuckSize_memory[2]              <= {3'd4};
      stuckSize_memory[3]              <= {3'bx};
      stuckSize_memory[4]              <= {3'bx};
      stuckSize_memory[5]              <= {3'bx};
      stuckSize_memory[6]              <= {3'bx};
      stuckSize_memory[7]              <= {3'bx};
    end
    else begin
      if (processCurrent == 4) begin
        case(stuckSize_pc)
          0: begin
            if ((stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step)) begin
              begin
                for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
                  stuckSize_stuckSize_5_result_1[stuckSize_memory_index]           <= stuckSize_memory[main_stuckSize_5_index_15][stuckSize_memory_index*3+:3];
                end
              end
              stuckSize_5_finishedAt           <= step;
            end
            else if ((stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step)) begin
              stuckSize_memory[main_stuckSize_6_index_16]      <= {main_stuckSize_6_value_17[0]};
              stuckSize_6_finishedAt           <= step;
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
  reg [8*4-1:0] stuckKeys_memory[8];
  reg [32-1:0] stuckKeys_value_0;
  (* nomem2reg *)
  reg [8-1:0] stuckKeys_stuckKeys_1_result_1[4];
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
      stuckKeys_pc     <= 0;
      stuckKeys_stop   <= 0;
      stuckKeys_returnCode             <= 0;
      stuckKeys_memory_index           <= 0;
      stuckKeys_memory_value           <= 0;
      stuckKeys_value_0                <= 0;
      begin
        for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 4; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
          stuckKeys_stuckKeys_1_result_1[stuckKeys_memory_index]           <= 0;
        end
      end
      stuckKeys_1_finishedAt           <= -1;
      stuckKeys_stuckKeys_1_returnCode                 <= 0;
      stuckKeys_2_finishedAt           <= -1;
      stuckKeys_stuckKeys_2_returnCode                 <= 0;
      stuckKeys_memory[0]              <= {8'd3, 8'd2, 8'd1, 8'd0};
      stuckKeys_memory[1]              <= {8'd3, 8'd2, 8'd1, 8'd0};
      stuckKeys_memory[2]              <= {8'd3, 8'd2, 8'd1, 8'd0};
      stuckKeys_memory[3]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckKeys_memory[4]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckKeys_memory[5]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckKeys_memory[6]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckKeys_memory[7]              <= {8'bx, 8'bx, 8'bx, 8'bx};
    end
    else begin
      if (processCurrent == 5) begin
        case(stuckKeys_pc)
          0: begin
            if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
              begin
                for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 4; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                  stuckKeys_stuckKeys_1_result_1[stuckKeys_memory_index]           <= stuckKeys_memory[main_stuckKeys_1_index_9][stuckKeys_memory_index*8+:8];
                end
              end
              stuckKeys_1_finishedAt           <= step;
            end
            else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
              stuckKeys_memory[main_stuckKeys_2_index_10]      <= {main_stuckKeys_2_value_11[3], main_stuckKeys_2_value_11[2], main_stuckKeys_2_value_11[1], main_stuckKeys_2_value_11[0]};
              stuckKeys_2_finishedAt           <= step;
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
  reg [8*4-1:0] stuckData_memory[8];
  reg [32-1:0] stuckData_value_0;
  (* nomem2reg *)
  reg [8-1:0] stuckData_stuckData_3_result_1[4];
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
      stuckData_pc     <= 0;
      stuckData_stop   <= 0;
      stuckData_returnCode             <= 0;
      stuckData_memory_index           <= 0;
      stuckData_memory_value           <= 0;
      stuckData_value_0                <= 0;
      begin
        for(stuckData_memory_index = 0; stuckData_memory_index < 4; stuckData_memory_index = stuckData_memory_index + 1) begin
          stuckData_stuckData_3_result_1[stuckData_memory_index]           <= 0;
        end
      end
      stuckData_3_finishedAt           <= -1;
      stuckData_stuckData_3_returnCode                 <= 0;
      stuckData_4_finishedAt           <= -1;
      stuckData_stuckData_4_returnCode                 <= 0;
      stuckData_memory[0]              <= {8'd4, 8'd3, 8'd2, 8'd1};
      stuckData_memory[1]              <= {8'd4, 8'd3, 8'd2, 8'd1};
      stuckData_memory[2]              <= {8'd4, 8'd3, 8'd2, 8'd1};
      stuckData_memory[3]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckData_memory[4]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckData_memory[5]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckData_memory[6]              <= {8'bx, 8'bx, 8'bx, 8'bx};
      stuckData_memory[7]              <= {8'bx, 8'bx, 8'bx, 8'bx};
    end
    else begin
      if (processCurrent == 6) begin
        case(stuckData_pc)
          0: begin
            if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
              begin
                for(stuckData_memory_index = 0; stuckData_memory_index < 4; stuckData_memory_index = stuckData_memory_index + 1) begin
                  stuckData_stuckData_3_result_1[stuckData_memory_index]           <= stuckData_memory[main_stuckData_3_index_12][stuckData_memory_index*8+:8];
                end
              end
              stuckData_3_finishedAt           <= step;
            end
            else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
              stuckData_memory[main_stuckData_4_index_13]      <= {main_stuckData_4_value_14[3], main_stuckData_4_value_14[2], main_stuckData_4_value_14[1], main_stuckData_4_value_14[0]};
              stuckData_4_finishedAt           <= step;
            end
            else begin
            end
          end
          default: stuckData_stop <= 1;
        endcase
      end
    end
  end
  // Process: stucksUsed  process_stucksUsed_0007
  (* ram_style = "block" *)
  reg [4*1-1:0] stucksUsed_memory[1];
  reg [4-1:0] stucksUsed_value_0;
  integer stucksUsed_11_requestedAt;
  integer stucksUsed_11_finishedAt;
  integer stucksUsed_stucksUsed_11_returnCode;
  integer stucksUsed_pc;
  integer stucksUsed_stop;
  integer stucksUsed_returnCode;
  integer stucksUsed_memory_index;
  reg[4-1:0] stucksUsed_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      stucksUsed_pc    <= 0;
      stucksUsed_stop  <= 0;
      stucksUsed_returnCode            <= 0;
      stucksUsed_memory_index          <= 0;
      stucksUsed_memory_value          <= 0;
      stucksUsed_value_0               <= 0;
      stucksUsed_11_finishedAt         <= -1;
      stucksUsed_stucksUsed_11_returnCode              <= 0;
      stucksUsed_memory[0]             <= {4'bx};
    end
    else begin
      if (processCurrent == 7) begin
        case(stucksUsed_pc)
          0: begin
            if ((stucksUsed_11_requestedAt > stucksUsed_11_finishedAt && stucksUsed_11_requestedAt != step)) begin
              stucksUsed_memory[main_stucksUsed_11_index_157]  <= {main_stucksUsed_11_value_158[0]};
              stucksUsed_11_finishedAt         <= step;
            end
            else begin
            end
          end
          default: stucksUsed_stop <= 1;
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

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 24, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "main_value_0", main_value_0);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_1", main_index_1);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_2", main_size_2);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_3", main_isLeaf_3);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_4", main_nextFree_4);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_5", 0, main_Keys_5[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_5", 1, main_Keys_5[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_5", 2, main_Keys_5[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_5", 3, main_Keys_5[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_6", 0, main_Compares_6[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_6", 1, main_Compares_6[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_6", 2, main_Compares_6[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_6", 3, main_Compares_6[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_7", 0, main_Collapse_7[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_7", 1, main_Collapse_7[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_7", 2, main_Collapse_7[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_7", 3, main_Collapse_7[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_8", 0, main_Data_8[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_8", 1, main_Data_8[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_8", 2, main_Data_8[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_8", 3, main_Data_8[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_1_index_9", main_stuckKeys_1_index_9);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_2_index_10", main_stuckKeys_2_index_10);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 0, main_stuckKeys_2_value_11[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 1, main_stuckKeys_2_value_11[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 2, main_stuckKeys_2_value_11[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 3, main_stuckKeys_2_value_11[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_3_index_12", main_stuckData_3_index_12);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_4_index_13", main_stuckData_4_index_13);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_14", 0, main_stuckData_4_value_14[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_14", 1, main_stuckData_4_value_14[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_14", 2, main_stuckData_4_value_14[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_14", 3, main_stuckData_4_value_14[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckSize_5_index_15", main_stuckSize_5_index_15);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckSize_6_index_16", main_stuckSize_6_index_16);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckSize_6_value_17", 0, main_stuckSize_6_value_17[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsLeaf_7_index_18", main_stuckIsLeaf_7_index_18);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsLeaf_8_index_19", main_stuckIsLeaf_8_index_19);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckIsLeaf_8_value_20", 0, main_stuckIsLeaf_8_value_20[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_21", main_Found_21);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_22", main_Key_22);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_23", main_FoundKey_23);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_24", main_Data_24);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_25", main_BtreeIndex_25);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_26", main_StuckIndex_26);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_27", main_MergeSuccess_27);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_28", main_index_28);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_29", main_size_29);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_30", main_isLeaf_30);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_31", main_nextFree_31);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_32", 0, main_Keys_32[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_32", 1, main_Keys_32[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_32", 2, main_Keys_32[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_32", 3, main_Keys_32[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_33", 0, main_Compares_33[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_33", 1, main_Compares_33[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_33", 2, main_Compares_33[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_33", 3, main_Compares_33[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_34", 0, main_Collapse_34[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_34", 1, main_Collapse_34[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_34", 2, main_Collapse_34[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_34", 3, main_Collapse_34[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_35", 0, main_Data_35[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_35", 1, main_Data_35[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_35", 2, main_Data_35[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_35", 3, main_Data_35[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_36", main_Found_36);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_37", main_Key_37);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_38", main_FoundKey_38);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_39", main_Data_39);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_40", main_BtreeIndex_40);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_41", main_StuckIndex_41);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_42", main_MergeSuccess_42);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_43", main_index_43);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_44", main_size_44);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_45", main_isLeaf_45);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_46", main_nextFree_46);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_47", 0, main_Keys_47[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_47", 1, main_Keys_47[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_47", 2, main_Keys_47[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_47", 3, main_Keys_47[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_48", 0, main_Compares_48[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_48", 1, main_Compares_48[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_48", 2, main_Compares_48[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_48", 3, main_Compares_48[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_49", 0, main_Collapse_49[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_49", 1, main_Collapse_49[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_49", 2, main_Collapse_49[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_49", 3, main_Collapse_49[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_50", 0, main_Data_50[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_50", 1, main_Data_50[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_50", 2, main_Data_50[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_50", 3, main_Data_50[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_51", main_Found_51);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_52", main_Key_52);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_53", main_FoundKey_53);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_54", main_Data_54);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_55", main_BtreeIndex_55);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_56", main_StuckIndex_56);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_57", main_MergeSuccess_57);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_58", main_index_58);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_59", main_size_59);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_60", main_isLeaf_60);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_61", main_nextFree_61);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_62", 0, main_Keys_62[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_62", 1, main_Keys_62[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_62", 2, main_Keys_62[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_62", 3, main_Keys_62[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_63", 0, main_Compares_63[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_63", 1, main_Compares_63[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_63", 2, main_Compares_63[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_63", 3, main_Compares_63[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_64", 0, main_Collapse_64[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_64", 1, main_Collapse_64[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_64", 2, main_Collapse_64[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_64", 3, main_Collapse_64[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_65", 0, main_Data_65[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_65", 1, main_Data_65[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_65", 2, main_Data_65[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_65", 3, main_Data_65[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_66", main_Found_66);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_67", main_Key_67);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_68", main_FoundKey_68);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_69", main_Data_69);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_70", main_BtreeIndex_70);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_71", main_StuckIndex_71);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_72", main_MergeSuccess_72);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_73", main_index_73);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_74", main_size_74);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_75", main_isLeaf_75);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_76", main_nextFree_76);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_77", 0, main_Keys_77[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_77", 1, main_Keys_77[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_77", 2, main_Keys_77[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_77", 3, main_Keys_77[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_78", 0, main_Compares_78[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_78", 1, main_Compares_78[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_78", 2, main_Compares_78[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_78", 3, main_Compares_78[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_79", 0, main_Collapse_79[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_79", 1, main_Collapse_79[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_79", 2, main_Collapse_79[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_79", 3, main_Collapse_79[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_80", 0, main_Data_80[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_80", 1, main_Data_80[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_80", 2, main_Data_80[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_80", 3, main_Data_80[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_81", main_Found_81);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_82", main_Key_82);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_83", main_FoundKey_83);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_84", main_Data_84);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_85", main_BtreeIndex_85);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_86", main_StuckIndex_86);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_87", main_MergeSuccess_87);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_88", main_index_88);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_89", main_size_89);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_90", main_isLeaf_90);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_91", main_nextFree_91);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_92", 0, main_Keys_92[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_92", 1, main_Keys_92[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_92", 2, main_Keys_92[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_92", 3, main_Keys_92[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_93", 0, main_Compares_93[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_93", 1, main_Compares_93[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_93", 2, main_Compares_93[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_93", 3, main_Compares_93[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_94", 0, main_Collapse_94[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_94", 1, main_Collapse_94[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_94", 2, main_Collapse_94[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_94", 3, main_Collapse_94[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_95", 0, main_Data_95[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_95", 1, main_Data_95[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_95", 2, main_Data_95[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_95", 3, main_Data_95[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_96", main_Found_96);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_97", main_Key_97);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_98", main_FoundKey_98);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_99", main_Data_99);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_100", main_BtreeIndex_100);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_101", main_StuckIndex_101);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_102", main_MergeSuccess_102);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_103", main_index_103);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_104", main_size_104);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_105", main_isLeaf_105);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_106", main_nextFree_106);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_107", 0, main_Keys_107[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_107", 1, main_Keys_107[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_107", 2, main_Keys_107[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_107", 3, main_Keys_107[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_108", 0, main_Compares_108[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_108", 1, main_Compares_108[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_108", 2, main_Compares_108[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_108", 3, main_Compares_108[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_109", 0, main_Collapse_109[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_109", 1, main_Collapse_109[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_109", 2, main_Collapse_109[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_109", 3, main_Collapse_109[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_110", 0, main_Data_110[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_110", 1, main_Data_110[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_110", 2, main_Data_110[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_110", 3, main_Data_110[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_111", main_Found_111);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_112", main_Key_112);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_113", main_FoundKey_113);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_114", main_Data_114);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_115", main_BtreeIndex_115);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_116", main_StuckIndex_116);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_117", main_MergeSuccess_117);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_118", main_index_118);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_119", main_size_119);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_120", main_isLeaf_120);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_121", main_nextFree_121);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_122", 0, main_Keys_122[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_122", 1, main_Keys_122[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_122", 2, main_Keys_122[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_122", 3, main_Keys_122[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_123", 0, main_Compares_123[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_123", 1, main_Compares_123[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_123", 2, main_Compares_123[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_123", 3, main_Compares_123[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_124", 0, main_Collapse_124[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_124", 1, main_Collapse_124[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_124", 2, main_Collapse_124[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_124", 3, main_Collapse_124[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_125", 0, main_Data_125[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_125", 1, main_Data_125[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_125", 2, main_Data_125[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_125", 3, main_Data_125[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_126", main_Found_126);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_127", main_Key_127);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_128", main_FoundKey_128);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_129", main_Data_129);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_130", main_BtreeIndex_130);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_131", main_StuckIndex_131);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_132", main_MergeSuccess_132);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_133", main_index_133);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_134", main_size_134);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_135", main_isLeaf_135);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_136", main_nextFree_136);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_137", 0, main_Keys_137[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_137", 1, main_Keys_137[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_137", 2, main_Keys_137[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_137", 3, main_Keys_137[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_138", 0, main_Compares_138[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_138", 1, main_Compares_138[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_138", 2, main_Compares_138[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_138", 3, main_Compares_138[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_139", 0, main_Collapse_139[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_139", 1, main_Collapse_139[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_139", 2, main_Collapse_139[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_139", 3, main_Collapse_139[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_140", 0, main_Data_140[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_140", 1, main_Data_140[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_140", 2, main_Data_140[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_140", 3, main_Data_140[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_141", main_Found_141);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_142", main_Key_142);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_143", main_FoundKey_143);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_144", main_Data_144);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_145", main_BtreeIndex_145);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_146", main_StuckIndex_146);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_147", main_MergeSuccess_147);
      $fwrite(o, "        %-50s = %1d\n",  "main_sum_148", main_sum_148);
      $fwrite(o, "        %-50s = %1d\n",  "main_can_149", main_can_149);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexLeft_150", main_indexLeft_150);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexRight_151", main_indexRight_151);
      $fwrite(o, "        %-50s = %1d\n",  "main_midKey_152", main_midKey_152);
      $fwrite(o, "        %-50s = %1d\n",  "main_freeNext_9_index_153", main_freeNext_9_index_153);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_freeNext_9_value_154", 0, main_freeNext_9_value_154[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsFree_10_index_155", main_stuckIsFree_10_index_155);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckIsFree_10_value_156", 0, main_stuckIsFree_10_value_156[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stucksUsed_11_index_157", main_stucksUsed_11_index_157);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stucksUsed_11_value_158", 0, main_stucksUsed_11_value_158[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_root_159", main_root_159);
      $fwrite(o, "        %-50s = %1d\n",  "main_rootSize_160", main_rootSize_160);
      $fwrite(o, "        %-50s = %1d\n",  "main_true_161", main_true_161);
      $fwrite(o, "        %-50s = %1d\n",  "main_false_162", main_false_162);
      $fwrite(o, "        %-50s = %1d\n",  "main_rootUsed_163", main_rootUsed_163);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_164", main_index_164);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_165", main_size_165);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_166", main_isLeaf_166);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_167", main_nextFree_167);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_168", 0, main_Keys_168[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_168", 1, main_Keys_168[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_168", 2, main_Keys_168[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_168", 3, main_Keys_168[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_169", 0, main_Compares_169[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_169", 1, main_Compares_169[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_169", 2, main_Compares_169[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_169", 3, main_Compares_169[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_170", 0, main_Collapse_170[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_170", 1, main_Collapse_170[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_170", 2, main_Collapse_170[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_170", 3, main_Collapse_170[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_171", 0, main_Data_171[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_171", 1, main_Data_171[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_171", 2, main_Data_171[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_171", 3, main_Data_171[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_172", main_Found_172);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_173", main_Key_173);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_174", main_FoundKey_174);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_175", main_Data_175);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_176", main_BtreeIndex_176);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_177", main_StuckIndex_177);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_178", main_MergeSuccess_178);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_179", main_d_179);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_180", main_n_180);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_181", main_index_181);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_182", main_size_182);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_183", main_isLeaf_183);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_184", main_nextFree_184);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_185", 0, main_Keys_185[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_185", 1, main_Keys_185[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_185", 2, main_Keys_185[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_185", 3, main_Keys_185[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_186", 0, main_Compares_186[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_186", 1, main_Compares_186[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_186", 2, main_Compares_186[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_186", 3, main_Compares_186[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_187", 0, main_Collapse_187[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_187", 1, main_Collapse_187[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_187", 2, main_Collapse_187[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_187", 3, main_Collapse_187[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_188", 0, main_Data_188[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_188", 1, main_Data_188[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_188", 2, main_Data_188[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_188", 3, main_Data_188[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_189", main_Found_189);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_190", main_Key_190);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_191", main_FoundKey_191);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_192", main_Data_192);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_193", main_BtreeIndex_193);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_194", main_StuckIndex_194);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_195", main_MergeSuccess_195);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_196", main_d_196);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_197", main_n_197);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_198", main_index_198);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_199", main_size_199);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_200", main_isLeaf_200);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_201", main_nextFree_201);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_202", 0, main_Keys_202[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_202", 1, main_Keys_202[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_202", 2, main_Keys_202[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_202", 3, main_Keys_202[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_203", 0, main_Compares_203[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_203", 1, main_Compares_203[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_203", 2, main_Compares_203[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_203", 3, main_Compares_203[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_204", 0, main_Collapse_204[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_204", 1, main_Collapse_204[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_204", 2, main_Collapse_204[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_204", 3, main_Collapse_204[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_205", 0, main_Data_205[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_205", 1, main_Data_205[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_205", 2, main_Data_205[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_205", 3, main_Data_205[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_206", main_Found_206);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_207", main_Key_207);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_208", main_FoundKey_208);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_209", main_Data_209);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_210", main_BtreeIndex_210);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_211", main_StuckIndex_211);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_212", main_MergeSuccess_212);
      $fwrite(o, "        %-50s = %1d\n",  "main_d_213", main_d_213);
      $fwrite(o, "        %-50s = %1d\n",  "main_n_214", main_n_214);
      $fwrite(o, "        %-50s = %1d\n",  "main_Left_215", main_Left_215);
      $fwrite(o, "        %-50s = %1d\n",  "main_Right_216", main_Right_216);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_217", main_index_217);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_218", main_size_218);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_219", main_isLeaf_219);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_220", main_nextFree_220);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_221", 0, main_Keys_221[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_221", 1, main_Keys_221[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_221", 2, main_Keys_221[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_221", 3, main_Keys_221[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_222", 0, main_Compares_222[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_222", 1, main_Compares_222[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_222", 2, main_Compares_222[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_222", 3, main_Compares_222[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_223", 0, main_Collapse_223[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_223", 1, main_Collapse_223[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_223", 2, main_Collapse_223[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_223", 3, main_Collapse_223[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_224", 0, main_Data_224[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_224", 1, main_Data_224[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_224", 2, main_Data_224[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_224", 3, main_Data_224[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_225", main_Found_225);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_226", main_Key_226);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_227", main_FoundKey_227);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_228", main_Data_228);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_229", main_BtreeIndex_229);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_230", main_StuckIndex_230);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_231", main_MergeSuccess_231);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_232", main_index_232);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_233", main_size_233);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_234", main_isLeaf_234);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_235", main_nextFree_235);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_236", 0, main_Keys_236[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_236", 1, main_Keys_236[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_236", 2, main_Keys_236[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_236", 3, main_Keys_236[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_237", 0, main_Compares_237[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_237", 1, main_Compares_237[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_237", 2, main_Compares_237[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_237", 3, main_Compares_237[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_238", 0, main_Collapse_238[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_238", 1, main_Collapse_238[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_238", 2, main_Collapse_238[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_238", 3, main_Collapse_238[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_239", 0, main_Data_239[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_239", 1, main_Data_239[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_239", 2, main_Data_239[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_239", 3, main_Data_239[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_240", main_Found_240);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_241", main_Key_241);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_242", main_FoundKey_242);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_243", main_Data_243);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_244", main_BtreeIndex_244);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_245", main_StuckIndex_245);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_246", main_MergeSuccess_246);
      $fwrite(o, "        %-50s = %1d\n",  "main_index_247", main_index_247);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_248", main_size_248);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_249", main_isLeaf_249);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_250", main_nextFree_250);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_251", 0, main_Keys_251[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_251", 1, main_Keys_251[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_251", 2, main_Keys_251[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Keys_251", 3, main_Keys_251[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_252", 0, main_Compares_252[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_252", 1, main_Compares_252[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_252", 2, main_Compares_252[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_252", 3, main_Compares_252[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_253", 0, main_Collapse_253[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_253", 1, main_Collapse_253[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_253", 2, main_Collapse_253[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_253", 3, main_Collapse_253[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_254", 0, main_Data_254[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_254", 1, main_Data_254[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_254", 2, main_Data_254[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_254", 3, main_Data_254[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_255", main_Found_255);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_256", main_Key_256);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_257", main_FoundKey_257);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_258", main_Data_258);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_259", main_BtreeIndex_259);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_260", main_StuckIndex_260);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_261", main_MergeSuccess_261);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsLeaf", 1, stuckIsLeaf_pc, stuckIsLeaf_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0][0+:1]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[1][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[2][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[3][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[4][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[5][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[6][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsLeaf_memory[7][0+:1]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stuckIsLeaf_value_0", stuckIsLeaf_value_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_1", 0, stuckIsLeaf_stuckIsLeaf_7_result_1[0]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckIsLeaf_7", stuckIsLeaf_7_requestedAt, stuckIsLeaf_7_finishedAt, stuckIsLeaf_stuckIsLeaf_7_returnCode, (stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step), (stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsLeaf_7_index_18", main_stuckIsLeaf_7_index_18);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_1", 0, stuckIsLeaf_stuckIsLeaf_7_result_1[0]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_8", stuckIsLeaf_8_requestedAt, stuckIsLeaf_8_finishedAt, stuckIsLeaf_stuckIsLeaf_8_returnCode, (stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step), (stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsLeaf_8_index_19", main_stuckIsLeaf_8_index_19);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckIsLeaf_8_value_20", 0, main_stuckIsLeaf_8_value_20[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 2, "stuckIsFree", 1, stuckIsFree_pc, stuckIsFree_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 1, 1);
      $fwrite(o, "        %2d", stuckIsFree_memory[0][0+:1]);
      $fwrite(o, ", %2d", stuckIsFree_memory[1][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[2][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[3][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[4][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[5][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[6][0+:1]); //CCCC
      $fwrite(o, ", %2d", stuckIsFree_memory[7][0+:1]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stuckIsFree_value_0", stuckIsFree_value_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsFree_10", stuckIsFree_10_requestedAt, stuckIsFree_10_finishedAt, stuckIsFree_stuckIsFree_10_returnCode, (stuckIsFree_10_requestedAt > stuckIsFree_10_finishedAt && stuckIsFree_10_requestedAt != step), (stuckIsFree_10_requestedAt < stuckIsFree_10_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsFree_10_index_155", main_stuckIsFree_10_index_155);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckIsFree_10_value_156", 0, main_stuckIsFree_10_value_156[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 4, 1);
      $fwrite(o, "        %2d", freeNext_memory[0][0+:4]);
      $fwrite(o, ", %2d", freeNext_memory[1][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[2][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[3][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[4][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[5][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[6][0+:4]); //CCCC
      $fwrite(o, ", %2d", freeNext_memory[7][0+:4]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "freeNext_value_0", freeNext_value_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "freeNext_9", freeNext_9_requestedAt, freeNext_9_finishedAt, freeNext_freeNext_9_returnCode, (freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step), (freeNext_9_requestedAt < freeNext_9_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_freeNext_9_index_153", main_freeNext_9_index_153);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_freeNext_9_value_154", 0, main_freeNext_9_value_154[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 3, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0][0+:3]);
      $fwrite(o, ", %2d", stuckSize_memory[1][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[2][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[3][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[4][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[5][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[6][0+:3]); //CCCC
      $fwrite(o, ", %2d", stuckSize_memory[7][0+:3]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stuckSize_value_0", stuckSize_value_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckSize_stuckSize_5_result_1", 0, stuckSize_stuckSize_5_result_1[0]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckSize_5", stuckSize_5_requestedAt, stuckSize_5_finishedAt, stuckSize_stuckSize_5_returnCode, (stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step), (stuckSize_5_requestedAt < stuckSize_5_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckSize_5_index_15", main_stuckSize_5_index_15);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckSize_stuckSize_5_result_1", 0, stuckSize_stuckSize_5_result_1[0]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckSize_6", stuckSize_6_requestedAt, stuckSize_6_finishedAt, stuckSize_stuckSize_6_returnCode, (stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step), (stuckSize_6_requestedAt < stuckSize_6_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckSize_6_index_16", main_stuckSize_6_index_16);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckSize_6_value_17", 0, main_stuckSize_6_value_17[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 5, "stuckKeys", 1, stuckKeys_pc, stuckKeys_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 4);
      $fwrite(o, "        %2d", stuckKeys_memory[0][0+:8]);
      $fwrite(o, ", %2d", stuckKeys_memory[0][8+:8]);
      $fwrite(o, ", %2d", stuckKeys_memory[0][16+:8]);
      $fwrite(o, ", %2d", stuckKeys_memory[0][24+:8]);
      $fwrite(o, ", %2d", stuckKeys_memory[1][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[1][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[1][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[1][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[2][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[2][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[2][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[2][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[3][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[3][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[3][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[3][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[4][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[4][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[4][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[4][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[5][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[5][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[5][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[5][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[6][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[6][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[6][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[6][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[7][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[7][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[7][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckKeys_memory[7][24+:8]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stuckKeys_value_0", stuckKeys_value_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 0, stuckKeys_stuckKeys_1_result_1[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 1, stuckKeys_stuckKeys_1_result_1[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 2, stuckKeys_stuckKeys_1_result_1[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 3, stuckKeys_stuckKeys_1_result_1[3]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_1_index_9", main_stuckKeys_1_index_9);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 0, stuckKeys_stuckKeys_1_result_1[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 1, stuckKeys_stuckKeys_1_result_1[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 2, stuckKeys_stuckKeys_1_result_1[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_1", 3, stuckKeys_stuckKeys_1_result_1[3]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_2_index_10", main_stuckKeys_2_index_10);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 0, main_stuckKeys_2_value_11[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 1, main_stuckKeys_2_value_11[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 2, main_stuckKeys_2_value_11[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_11", 3, main_stuckKeys_2_value_11[3]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "stuckData", 1, stuckData_pc, stuckData_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 8, 8, 4);
      $fwrite(o, "        %2d", stuckData_memory[0][0+:8]);
      $fwrite(o, ", %2d", stuckData_memory[0][8+:8]);
      $fwrite(o, ", %2d", stuckData_memory[0][16+:8]);
      $fwrite(o, ", %2d", stuckData_memory[0][24+:8]);
      $fwrite(o, ", %2d", stuckData_memory[1][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[1][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[1][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[1][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[2][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[2][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[2][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[2][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[3][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[3][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[3][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[3][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[4][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[4][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[4][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[4][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[5][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[5][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[5][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[5][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[6][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[6][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[6][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[6][24+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[7][0+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[7][8+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[7][16+:8]); //CCCC
      $fwrite(o, ", %2d", stuckData_memory[7][24+:8]); //CCCC
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stuckData_value_0", stuckData_value_0);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 0, stuckData_stuckData_3_result_1[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 1, stuckData_stuckData_3_result_1[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 2, stuckData_stuckData_3_result_1[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 3, stuckData_stuckData_3_result_1[3]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_3_index_12", main_stuckData_3_index_12);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 0, stuckData_stuckData_3_result_1[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 1, stuckData_stuckData_3_result_1[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 2, stuckData_stuckData_3_result_1[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_1", 3, stuckData_stuckData_3_result_1[3]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_4_index_13", main_stuckData_4_index_13);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_14", 0, main_stuckData_4_value_14[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_14", 1, main_stuckData_4_value_14[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_14", 2, main_stuckData_4_value_14[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_14", 3, main_stuckData_4_value_14[3]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 7, "stucksUsed", 1, stucksUsed_pc, stucksUsed_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 1, 4, 1);
      $fwrite(o, "        %2d", stucksUsed_memory[0][0+:4]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "stucksUsed_value_0", stucksUsed_value_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stucksUsed_11", stucksUsed_11_requestedAt, stucksUsed_11_finishedAt, stucksUsed_stucksUsed_11_returnCode, (stucksUsed_11_requestedAt > stucksUsed_11_finishedAt && stucksUsed_11_requestedAt != step), (stucksUsed_11_requestedAt < stucksUsed_11_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stucksUsed_11_index_157", main_stucksUsed_11_index_157);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stucksUsed_11_value_158", 0, main_stucksUsed_11_value_158[0]);
      $fclose(o);
    end
  endtask
endmodule
