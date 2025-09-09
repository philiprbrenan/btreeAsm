//-----------------------------------------------------------------------------
// Database on a chip synthesis
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Btree(                                                                      // Test bench for database on a chip
  input                 clock,                                                  // Clock
  input                 reset,                                                  // Reset chip

  input wire [32-1:0] k,
  output wire            stop                                            // Program has stopped when this goes high
  );
  integer step;
  integer returnCode;
  assign stop = main_stop||stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop;
  always @ (posedge clock) begin
    if (reset) begin
      step <= -2;
    end
    else begin
      step <= step + 1;
    end
  end
  // process_main_0000
  // process_stuckIsLeaf_0001
  // process_stuckIsFree_0002
  // process_freeNext_0003
  // process_stuckSize_0004
  // process_stuckKeys_0005
  // process_stuckData_0006
  // Process: main  process_main_0000
  reg [11-1:0] main_index_0;
  reg [5-1:0] main_size_1;
  reg [1-1:0] main_isLeaf_2;
  reg [11-1:0] main_nextFree_3;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_4[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_5[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_6[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_7[16];
  reg [10-1:0] main_stuckKeys_1_index_8;
  reg [10-1:0] main_stuckKeys_2_index_9;
  (* nomem2reg *)
  reg [32-1:0] main_stuckKeys_2_value_10[16];
  reg [10-1:0] main_stuckData_3_index_11;
  reg [10-1:0] main_stuckData_4_index_12;
  (* nomem2reg *)
  reg [32-1:0] main_stuckData_4_value_13[16];
  reg [10-1:0] main_stuckSize_5_index_14;
  reg [10-1:0] main_stuckSize_6_index_15;
  (* nomem2reg *)
  reg [5-1:0] main_stuckSize_6_value_16[1];
  reg [10-1:0] main_stuckIsLeaf_7_index_17;
  reg [10-1:0] main_stuckIsLeaf_8_index_18;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsLeaf_8_value_19[1];
  reg [1-1:0] main_Found_20;
  reg [32-1:0] main_Key_21;
  reg [32-1:0] main_FoundKey_22;
  reg [32-1:0] main_Data_23;
  reg [11-1:0] main_BtreeIndex_24;
  reg [5-1:0] main_StuckIndex_25;
  reg [1-1:0] main_MergeSuccess_26;
  reg [11-1:0] main_index_27;
  reg [5-1:0] main_size_28;
  reg [1-1:0] main_isLeaf_29;
  reg [11-1:0] main_nextFree_30;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_31[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_32[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_33[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_34[16];
  reg [1-1:0] main_Found_35;
  reg [32-1:0] main_Key_36;
  reg [32-1:0] main_FoundKey_37;
  reg [32-1:0] main_Data_38;
  reg [11-1:0] main_BtreeIndex_39;
  reg [5-1:0] main_StuckIndex_40;
  reg [1-1:0] main_MergeSuccess_41;
  reg [11-1:0] main_index_42;
  reg [5-1:0] main_size_43;
  reg [1-1:0] main_isLeaf_44;
  reg [11-1:0] main_nextFree_45;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_46[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_47[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_48[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_49[16];
  reg [1-1:0] main_Found_50;
  reg [32-1:0] main_Key_51;
  reg [32-1:0] main_FoundKey_52;
  reg [32-1:0] main_Data_53;
  reg [11-1:0] main_BtreeIndex_54;
  reg [5-1:0] main_StuckIndex_55;
  reg [1-1:0] main_MergeSuccess_56;
  reg [11-1:0] main_index_57;
  reg [5-1:0] main_size_58;
  reg [1-1:0] main_isLeaf_59;
  reg [11-1:0] main_nextFree_60;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_61[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_62[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_63[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_64[16];
  reg [1-1:0] main_Found_65;
  reg [32-1:0] main_Key_66;
  reg [32-1:0] main_FoundKey_67;
  reg [32-1:0] main_Data_68;
  reg [11-1:0] main_BtreeIndex_69;
  reg [5-1:0] main_StuckIndex_70;
  reg [1-1:0] main_MergeSuccess_71;
  reg [11-1:0] main_index_72;
  reg [5-1:0] main_size_73;
  reg [1-1:0] main_isLeaf_74;
  reg [11-1:0] main_nextFree_75;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_76[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_77[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_78[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_79[16];
  reg [1-1:0] main_Found_80;
  reg [32-1:0] main_Key_81;
  reg [32-1:0] main_FoundKey_82;
  reg [32-1:0] main_Data_83;
  reg [11-1:0] main_BtreeIndex_84;
  reg [5-1:0] main_StuckIndex_85;
  reg [1-1:0] main_MergeSuccess_86;
  reg [11-1:0] main_index_87;
  reg [5-1:0] main_size_88;
  reg [1-1:0] main_isLeaf_89;
  reg [11-1:0] main_nextFree_90;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_91[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_92[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_93[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_94[16];
  reg [1-1:0] main_Found_95;
  reg [32-1:0] main_Key_96;
  reg [32-1:0] main_FoundKey_97;
  reg [32-1:0] main_Data_98;
  reg [11-1:0] main_BtreeIndex_99;
  reg [5-1:0] main_StuckIndex_100;
  reg [1-1:0] main_MergeSuccess_101;
  reg [11-1:0] main_index_102;
  reg [5-1:0] main_size_103;
  reg [1-1:0] main_isLeaf_104;
  reg [11-1:0] main_nextFree_105;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_106[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_107[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_108[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_109[16];
  reg [1-1:0] main_Found_110;
  reg [32-1:0] main_Key_111;
  reg [32-1:0] main_FoundKey_112;
  reg [32-1:0] main_Data_113;
  reg [11-1:0] main_BtreeIndex_114;
  reg [5-1:0] main_StuckIndex_115;
  reg [1-1:0] main_MergeSuccess_116;
  reg [11-1:0] main_index_117;
  reg [5-1:0] main_size_118;
  reg [1-1:0] main_isLeaf_119;
  reg [11-1:0] main_nextFree_120;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_121[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_122[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_123[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_124[16];
  reg [1-1:0] main_Found_125;
  reg [32-1:0] main_Key_126;
  reg [32-1:0] main_FoundKey_127;
  reg [32-1:0] main_Data_128;
  reg [11-1:0] main_BtreeIndex_129;
  reg [5-1:0] main_StuckIndex_130;
  reg [1-1:0] main_MergeSuccess_131;
  reg [11-1:0] main_index_132;
  reg [5-1:0] main_size_133;
  reg [1-1:0] main_isLeaf_134;
  reg [11-1:0] main_nextFree_135;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_136[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_137[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_138[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_139[16];
  reg [1-1:0] main_Found_140;
  reg [32-1:0] main_Key_141;
  reg [32-1:0] main_FoundKey_142;
  reg [32-1:0] main_Data_143;
  reg [11-1:0] main_BtreeIndex_144;
  reg [5-1:0] main_StuckIndex_145;
  reg [1-1:0] main_MergeSuccess_146;
  reg [6-1:0] main_sum_147;
  reg [1-1:0] main_can_148;
  reg [11-1:0] main_indexLeft_149;
  reg [11-1:0] main_indexRight_150;
  reg [32-1:0] main_midKey_151;
  reg [32-1:0] main_k_152;
  reg [11-1:0] main_index_153;
  reg [5-1:0] main_size_154;
  reg [1-1:0] main_isLeaf_155;
  reg [11-1:0] main_nextFree_156;
  (* nomem2reg *)
  reg [32-1:0] main_Keys_157[16];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_158[16];
  (* nomem2reg *)
  reg [5-1:0] main_Collapse_159[16];
  (* nomem2reg *)
  reg [32-1:0] main_Data_160[16];
  reg [1-1:0] main_Found_161;
  reg [32-1:0] main_Key_162;
  reg [32-1:0] main_FoundKey_163;
  reg [32-1:0] main_Data_164;
  reg [11-1:0] main_BtreeIndex_165;
  reg [5-1:0] main_StuckIndex_166;
  reg [1-1:0] main_MergeSuccess_167;
  integer main_pc;
  integer main_stop;
  integer main_returnCode;
  integer main_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      main_pc <= 0;
      main_stop <= 0;
      main_returnCode <= 0;
      main_index_0 <= 0;
      main_size_1 <= 0;
      main_isLeaf_2 <= 0;
      main_nextFree_3 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_4[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_5[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_6[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Data_7[main_memory_index] <= 0;
        end
      end
      main_stuckKeys_1_index_8 <= 0;
      main_stuckKeys_2_index_9 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_stuckKeys_2_value_10[main_memory_index] <= 0;
        end
      end
      main_stuckData_3_index_11 <= 0;
      main_stuckData_4_index_12 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_31[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_32[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_33[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_46[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_47[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_48[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_61[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_62[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_63[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_76[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_77[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_78[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_91[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_92[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_93[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_106[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_107[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_108[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_121[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_122[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_123[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_136[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_137[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_138[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
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
      main_index_153 <= 0;
      main_size_154 <= 0;
      main_isLeaf_155 <= 0;
      main_nextFree_156 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Keys_157[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Compares_158[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Collapse_159[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
          main_Data_160[main_memory_index] <= 0;
        end
      end
      main_Found_161 <= 0;
      main_Key_162 <= 0;
      main_FoundKey_163 <= 0;
      main_Data_164 <= 0;
      main_BtreeIndex_165 <= 0;
      main_StuckIndex_166 <= 0;
      main_MergeSuccess_167 <= 0;
      stuckIsLeaf_7_requestedAt <= -1;
      stuckIsLeaf_8_requestedAt <= -1;
      stuckSize_5_requestedAt <= -1;
      stuckSize_6_requestedAt <= -1;
      stuckKeys_1_requestedAt <= -1;
      stuckKeys_2_requestedAt <= -1;
      stuckData_3_requestedAt <= -1;
      stuckData_4_requestedAt <= -1;
    end
    else begin                                  // Run the process in full parallel
      case(main_pc)
        0: begin
          main_k_152 <= 1;
          main_pc <= main_pc + 1;
        end
        1: begin
          main_BtreeIndex_165 <= 0;
          main_pc <= main_pc + 1;
        end
        2: begin
          main_index_153 <= main_BtreeIndex_165;
          main_pc <= main_pc + 1;
        end
        3: begin
          main_stuckSize_5_index_14 <= main_index_153;
          stuckSize_5_requestedAt <= step;
          main_stuckIsLeaf_7_index_17 <= main_index_153;
          stuckIsLeaf_7_requestedAt <= step;
          main_stuckKeys_1_index_8 <= main_index_153;
          stuckKeys_1_requestedAt <= step;
          main_stuckData_3_index_11 <= main_index_153;
          stuckData_3_requestedAt <= step;
          main_pc <= main_pc + 1;
        end
        4: begin
          if ((stuckSize_5_requestedAt < stuckSize_5_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
        end
        5: begin
          if ((stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
        end
        6: begin
          if ((stuckKeys_1_requestedAt < stuckKeys_1_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
        end
        7: begin
          if ((stuckData_3_requestedAt < stuckData_3_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
        end
        8: begin
          main_size_154 <= stuckSize_stuckSize_5_result_0[0];
          main_isLeaf_155 <= stuckIsLeaf_stuckIsLeaf_7_result_0[0];
          begin
            for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
              main_Keys_157[main_memory_index] <= stuckKeys_stuckKeys_1_result_0[main_memory_index];
            end
          end
          begin
            for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
              main_Data_160[main_memory_index] <= stuckData_stuckData_3_result_0[main_memory_index];
            end
          end
          main_pc <= main_pc + 1;
        end
        9: begin
          if (main_isLeaf_155 == 0) begin
            main_pc <= 18;
          end
          else begin
            main_pc <= main_pc + 1;
          end
        end
        10: begin
          begin
            for(main_memory_index = 0; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
              main_Compares_158[main_memory_index] <= main_k_152 == main_Keys_157[main_memory_index] && main_memory_index < main_size_154;
              main_Collapse_159[main_memory_index] <= main_memory_index;
            end
          end
          main_pc <= main_pc + 1;
        end
        11, 19: begin
          for(main_memory_index = 0; main_memory_index < 15; main_memory_index = main_memory_index+2) begin
            if (main_Compares_158[main_memory_index+1]) begin
                main_Compares_158[main_memory_index] <= 1;
                main_Collapse_159[main_memory_index] <= main_Collapse_159[main_memory_index+1];
            end
          end
          main_pc <= main_pc + 1;
        end
        12, 20: begin
          for(main_memory_index = 0; main_memory_index < 14; main_memory_index = main_memory_index+4) begin
            if (main_Compares_158[main_memory_index+2]) begin
                main_Compares_158[main_memory_index] <= 1;
                main_Collapse_159[main_memory_index] <= main_Collapse_159[main_memory_index+2];
            end
          end
          main_pc <= main_pc + 1;
        end
        13, 21: begin
          for(main_memory_index = 0; main_memory_index < 12; main_memory_index = main_memory_index+8) begin
            if (main_Compares_158[main_memory_index+4]) begin
                main_Compares_158[main_memory_index] <= 1;
                main_Collapse_159[main_memory_index] <= main_Collapse_159[main_memory_index+4];
            end
          end
          main_pc <= main_pc + 1;
        end
        14, 22: begin
          for(main_memory_index = 0; main_memory_index < 8; main_memory_index = main_memory_index+16) begin
            if (main_Compares_158[main_memory_index+8]) begin
                main_Compares_158[main_memory_index] <= 1;
                main_Collapse_159[main_memory_index] <= main_Collapse_159[main_memory_index+8];
            end
          end
          main_pc <= main_pc + 1;
        end
        15: begin
          if (main_Compares_158[0]) begin
            main_Found_161 <= 1;
            main_StuckIndex_166 <= main_Collapse_159[0];
            main_Key_162 <= main_Keys_157[main_Collapse_159[0]];
            main_Data_164 <= main_Data_160[main_Collapse_159[0]];
          end
          else begin
            main_Found_161 <= 0;
          end
          main_pc <= main_pc + 1;
        end
        16, 17: begin
          main_pc <= 26;
        end
        18: begin
          main_Compares_158[0] <= main_k_152 <= main_Keys_157[0] && 0 < main_size_154;
          main_Collapse_159[0] <= 0;
          begin
            for(main_memory_index = 1; main_memory_index < 16; main_memory_index = main_memory_index + 1) begin
              main_Compares_158[main_memory_index] <= main_k_152 >  main_Keys_157[main_memory_index-1] && main_k_152 <= main_Keys_157[main_memory_index] && main_memory_index < main_size_154;
              main_Collapse_159[main_memory_index] <= main_memory_index;
            end
          end
          main_pc <= main_pc + 1;
        end
        23: begin
          if (main_Compares_158[0]) begin
            main_Found_161 <= 1;
            main_StuckIndex_166 <= main_Collapse_159[0];
            main_FoundKey_163 <= main_Keys_157[main_Collapse_159[0]];
            main_Data_164 <= main_Data_160[main_Collapse_159[0]];
          end
          else begin
            main_Found_161 <= 0;
            main_StuckIndex_166 <= main_size_154;
            main_Data_164 <= main_Data_160[main_size_154];
          end
          main_pc <= main_pc + 1;
        end
        24: begin
          main_BtreeIndex_165 <= main_Data_164;
          main_pc <= main_pc + 1;
        end
        25: begin
          main_pc <= 2;
        end
        default: main_stop <= 1;
      endcase
    end
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0001
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[1024*1];
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
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsLeaf_pc <= 0;
      stuckIsLeaf_stop <= 0;
      stuckIsLeaf_returnCode <= 0;
      begin
        for(stuckIsLeaf_memory_index = 0; stuckIsLeaf_memory_index < 1; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
          stuckIsLeaf_stuckIsLeaf_7_result_0[stuckIsLeaf_memory_index] <= 0;
        end
      end
      stuckIsLeaf_7_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_7_returnCode <= 0;
      stuckIsLeaf_8_finishedAt <= -1;
      stuckIsLeaf_stuckIsLeaf_8_returnCode <= 0;
      stuckIsLeaf_memory[0] <= 1;
      begin
        for(stuckIsLeaf_memory_index = 1; stuckIsLeaf_memory_index < 1024; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
          stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= 0;
        end
      end
    end
    else begin                                  // Run the process in full parallel
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
  // Process: stuckIsFree  process_stuckIsFree_0002
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsFree_memory[1024*1];
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  integer stuckIsFree_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsFree_pc <= 0;
      stuckIsFree_stop <= 0;
      stuckIsFree_returnCode <= 0;
      stuckIsFree_memory[0] <= 0;
      begin
        for(stuckIsFree_memory_index = 1; stuckIsFree_memory_index < 1024; stuckIsFree_memory_index = stuckIsFree_memory_index + 1) begin
          stuckIsFree_memory[stuckIsFree_memory_index] <= 1;
        end
      end
    end
    else begin                                  // Run the process in full parallel
      case(stuckIsFree_pc)
        0: begin
        end
        default: stuckIsFree_stop <= 1;
      endcase
    end
  end
  // Process: freeNext  process_freeNext_0003
  (* ram_style = "block" *)
  reg [11-1:0] freeNext_memory[1024*1];
  integer freeNext_pc;
  integer freeNext_stop;
  integer freeNext_returnCode;
  integer freeNext_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      freeNext_pc <= 0;
      freeNext_stop <= 0;
      freeNext_returnCode <= 0;
      begin
        for(freeNext_memory_index = 0; freeNext_memory_index < 1023; freeNext_memory_index = freeNext_memory_index + 1) begin
          freeNext_memory[freeNext_memory_index] <= 1+freeNext_memory_index;
        end
      end
      begin
        for(freeNext_memory_index = 1023; freeNext_memory_index < 1024; freeNext_memory_index = freeNext_memory_index + 1) begin
          freeNext_memory[freeNext_memory_index] <= -1023+freeNext_memory_index;
        end
      end
    end
    else begin                                  // Run the process in full parallel
      case(freeNext_pc)
        0: begin
        end
        default: freeNext_stop <= 1;
      endcase
    end
  end
  // Process: stuckSize  process_stuckSize_0004
  (* ram_style = "block" *)
  reg [5-1:0] stuckSize_memory[1024*1];
  (* nomem2reg *)
  reg [5-1:0] stuckSize_stuckSize_5_result_0[1];
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
      begin
        for(stuckSize_memory_index = 0; stuckSize_memory_index < 1; stuckSize_memory_index = stuckSize_memory_index + 1) begin
          stuckSize_stuckSize_5_result_0[stuckSize_memory_index] <= 0;
        end
      end
      stuckSize_5_finishedAt <= -1;
      stuckSize_stuckSize_5_returnCode <= 0;
      stuckSize_6_finishedAt <= -1;
      stuckSize_stuckSize_6_returnCode <= 0;
      begin
        for(stuckSize_memory_index = 0; stuckSize_memory_index < 1024; stuckSize_memory_index = stuckSize_memory_index + 1) begin
          stuckSize_memory[stuckSize_memory_index] <= 0;
        end
      end
    end
    else begin                                  // Run the process in full parallel
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
  // Process: stuckKeys  process_stuckKeys_0005
  (* ram_style = "block" *)
  reg [32-1:0] stuckKeys_memory[1024*16];
  (* nomem2reg *)
  reg [32-1:0] stuckKeys_stuckKeys_1_result_0[16];
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
      begin
        for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
          stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= 0;
        end
      end
      stuckKeys_1_finishedAt <= -1;
      stuckKeys_stuckKeys_1_returnCode <= 0;
      stuckKeys_2_finishedAt <= -1;
      stuckKeys_stuckKeys_2_returnCode <= 0;
      begin
        for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16384; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
          stuckKeys_memory[stuckKeys_memory_index] <= 0;
        end
      end
    end
    else begin                                  // Run the process in full parallel
      case(stuckKeys_pc)
        0: begin
          if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
            begin
              for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= stuckKeys_memory[main_stuckKeys_1_index_8*16+stuckKeys_memory_index];
              end
            end
            stuckKeys_1_finishedAt <= step;
          end
          else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
            begin
              for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                stuckKeys_memory[main_stuckKeys_2_index_9*16+stuckKeys_memory_index] <= main_stuckKeys_2_value_10[stuckKeys_memory_index];
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
  // Process: stuckData  process_stuckData_0006
  (* ram_style = "block" *)
  reg [32-1:0] stuckData_memory[1024*16];
  (* nomem2reg *)
  reg [32-1:0] stuckData_stuckData_3_result_0[16];
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
      begin
        for(stuckData_memory_index = 0; stuckData_memory_index < 16; stuckData_memory_index = stuckData_memory_index + 1) begin
          stuckData_stuckData_3_result_0[stuckData_memory_index] <= 0;
        end
      end
      stuckData_3_finishedAt <= -1;
      stuckData_stuckData_3_returnCode <= 0;
      stuckData_4_finishedAt <= -1;
      stuckData_stuckData_4_returnCode <= 0;
      begin
        for(stuckData_memory_index = 0; stuckData_memory_index < 16384; stuckData_memory_index = stuckData_memory_index + 1) begin
          stuckData_memory[stuckData_memory_index] <= 0;
        end
      end
    end
    else begin                                  // Run the process in full parallel
      case(stuckData_pc)
        0: begin
          if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
            begin
              for(stuckData_memory_index = 0; stuckData_memory_index < 16; stuckData_memory_index = stuckData_memory_index + 1) begin
                stuckData_stuckData_3_result_0[stuckData_memory_index] <= stuckData_memory[main_stuckData_3_index_11*16+stuckData_memory_index];
              end
            end
            stuckData_3_finishedAt <= step;
          end
          else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
            begin
              for(stuckData_memory_index = 0; stuckData_memory_index < 16; stuckData_memory_index = stuckData_memory_index + 1) begin
                stuckData_memory[main_stuckData_4_index_12*16+stuckData_memory_index] <= main_stuckData_4_value_13[stuckData_memory_index];
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
endmodule
