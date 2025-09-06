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

  assign stop = main_stop||stuckIsLeaf_stop||stuckIsFree_stop||freeNext_stop||stuckSize_stop||stuckKeys_stop||stuckData_stop;                                                             // Or of process stop fields

  initial begin
    returnCode = 0;
    maxSteps = 100;
    for(step = -1; step < 0 || step < maxSteps && !stop; step = step + 1) begin // Steps below zero are run unconditionally to initialize each process so that Java and Verilog start in sync at step zero

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
  reg [3-1:0] main_index_0;
  reg [3-1:0] main_size_1;
  reg [1-1:0] main_isLeaf_2;
  reg [3-1:0] main_nextFree_3;
  (* nomem2reg *)
  reg [8-1:0] main_Key_4[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_5[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_6[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_7[4];
  reg [2-1:0] main_stuckKeys_1_index_8;
  reg [2-1:0] main_stuckKeys_2_index_9;
  (* nomem2reg *)
  reg [8-1:0] main_stuckKeys_2_value_10[4];
  reg [2-1:0] main_stuckData_3_index_11;
  reg [2-1:0] main_stuckData_4_index_12;
  (* nomem2reg *)
  reg [8-1:0] main_stuckData_4_value_13[4];
  reg [2-1:0] main_stuckSize_5_index_14;
  reg [2-1:0] main_stuckSize_6_index_15;
  (* nomem2reg *)
  reg [3-1:0] main_stuckSize_6_value_16[1];
  reg [2-1:0] main_stuckIsLeaf_7_index_17;
  reg [2-1:0] main_stuckIsLeaf_8_index_18;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsLeaf_8_value_19[1];
  reg [1-1:0] main_Found_20;
  reg [8-1:0] main_Key_21;
  reg [8-1:0] main_FoundKey_22;
  reg [8-1:0] main_Data_23;
  reg [3-1:0] main_BtreeIndex_24;
  reg [3-1:0] main_StuckIndex_25;
  reg [1-1:0] main_MergeSuccess_26;
  reg [3-1:0] main_index_27;
  reg [3-1:0] main_size_28;
  reg [1-1:0] main_isLeaf_29;
  reg [3-1:0] main_nextFree_30;
  (* nomem2reg *)
  reg [8-1:0] main_Key_31[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_32[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_33[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_34[4];
  reg [1-1:0] main_Found_35;
  reg [8-1:0] main_Key_36;
  reg [8-1:0] main_FoundKey_37;
  reg [8-1:0] main_Data_38;
  reg [3-1:0] main_BtreeIndex_39;
  reg [3-1:0] main_StuckIndex_40;
  reg [1-1:0] main_MergeSuccess_41;
  reg [3-1:0] main_index_42;
  reg [3-1:0] main_size_43;
  reg [1-1:0] main_isLeaf_44;
  reg [3-1:0] main_nextFree_45;
  (* nomem2reg *)
  reg [8-1:0] main_Key_46[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_47[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_48[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_49[4];
  reg [1-1:0] main_Found_50;
  reg [8-1:0] main_Key_51;
  reg [8-1:0] main_FoundKey_52;
  reg [8-1:0] main_Data_53;
  reg [3-1:0] main_BtreeIndex_54;
  reg [3-1:0] main_StuckIndex_55;
  reg [1-1:0] main_MergeSuccess_56;
  reg [3-1:0] main_index_57;
  reg [3-1:0] main_size_58;
  reg [1-1:0] main_isLeaf_59;
  reg [3-1:0] main_nextFree_60;
  (* nomem2reg *)
  reg [8-1:0] main_Key_61[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_62[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_63[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_64[4];
  reg [1-1:0] main_Found_65;
  reg [8-1:0] main_Key_66;
  reg [8-1:0] main_FoundKey_67;
  reg [8-1:0] main_Data_68;
  reg [3-1:0] main_BtreeIndex_69;
  reg [3-1:0] main_StuckIndex_70;
  reg [1-1:0] main_MergeSuccess_71;
  reg [3-1:0] main_index_72;
  reg [3-1:0] main_size_73;
  reg [1-1:0] main_isLeaf_74;
  reg [3-1:0] main_nextFree_75;
  (* nomem2reg *)
  reg [8-1:0] main_Key_76[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_77[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_78[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_79[4];
  reg [1-1:0] main_Found_80;
  reg [8-1:0] main_Key_81;
  reg [8-1:0] main_FoundKey_82;
  reg [8-1:0] main_Data_83;
  reg [3-1:0] main_BtreeIndex_84;
  reg [3-1:0] main_StuckIndex_85;
  reg [1-1:0] main_MergeSuccess_86;
  reg [3-1:0] main_index_87;
  reg [3-1:0] main_size_88;
  reg [1-1:0] main_isLeaf_89;
  reg [3-1:0] main_nextFree_90;
  (* nomem2reg *)
  reg [8-1:0] main_Key_91[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_92[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_93[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_94[4];
  reg [1-1:0] main_Found_95;
  reg [8-1:0] main_Key_96;
  reg [8-1:0] main_FoundKey_97;
  reg [8-1:0] main_Data_98;
  reg [3-1:0] main_BtreeIndex_99;
  reg [3-1:0] main_StuckIndex_100;
  reg [1-1:0] main_MergeSuccess_101;
  reg [3-1:0] main_index_102;
  reg [3-1:0] main_size_103;
  reg [1-1:0] main_isLeaf_104;
  reg [3-1:0] main_nextFree_105;
  (* nomem2reg *)
  reg [8-1:0] main_Key_106[4];
  (* nomem2reg *)
  reg [1-1:0] main_Compares_107[4];
  (* nomem2reg *)
  reg [3-1:0] main_Collapse_108[4];
  (* nomem2reg *)
  reg [8-1:0] main_Data_109[4];
  reg [1-1:0] main_Found_110;
  reg [8-1:0] main_Key_111;
  reg [8-1:0] main_FoundKey_112;
  reg [8-1:0] main_Data_113;
  reg [3-1:0] main_BtreeIndex_114;
  reg [3-1:0] main_StuckIndex_115;
  reg [1-1:0] main_MergeSuccess_116;
  reg [4-1:0] main_sum_117;
  reg [1-1:0] main_can_118;
  reg [3-1:0] main_indexLeft_119;
  reg [3-1:0] main_indexRight_120;
  reg [8-1:0] main_midKey_121;
  reg [3-1:0] main_index1_122;
  reg [3-1:0] main_index2_123;
  reg [2-1:0] main_freeNext_9_index_124;
  reg [2-1:0] main_freeNext_10_index_125;
  (* nomem2reg *)
  reg [3-1:0] main_freeNext_10_value_126[1];
  reg [2-1:0] main_stuckIsFree_11_index_127;
  (* nomem2reg *)
  reg [1-1:0] main_stuckIsFree_11_value_128[1];
  reg [3-1:0] main_root_129;
  reg [3-1:0] main_next_130;
  reg [1-1:0] main_isLeaf_131;
  reg [1-1:0] main_isFree_132;
  reg [3-1:0] main_root_133;
  reg [3-1:0] main_next_134;
  reg [1-1:0] main_isLeaf_135;
  reg [1-1:0] main_isFree_136;
  reg [3-1:0] main_next_137;
  reg [3-1:0] main_root_138;
  reg [1-1:0] main_isFree_139;
  reg [3-1:0] main_next_140;
  reg [3-1:0] main_root_141;
  reg [1-1:0] main_isFree_142;
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_4[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_5[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_6[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Data_7[main_memory_index] <= 0;
        end
      end
      main_stuckKeys_1_index_8 <= 0;
      main_stuckKeys_2_index_9 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_stuckKeys_2_value_10[main_memory_index] <= 0;
        end
      end
      main_stuckData_3_index_11 <= 0;
      main_stuckData_4_index_12 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_31[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_32[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_33[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_46[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_47[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_48[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_61[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_62[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_63[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_76[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_77[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_78[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_91[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_92[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_93[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Key_106[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Compares_107[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
          main_Collapse_108[main_memory_index] <= 0;
        end
      end
      begin
        for(main_memory_index = 0; main_memory_index < 4; main_memory_index = main_memory_index + 1) begin
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
      main_sum_117 <= 0;
      main_can_118 <= 0;
      main_indexLeft_119 <= 0;
      main_indexRight_120 <= 0;
      main_midKey_121 <= 0;
      main_index1_122 <= 0;
      main_index2_123 <= 0;
      main_freeNext_9_index_124 <= 0;
      main_freeNext_10_index_125 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_freeNext_10_value_126[main_memory_index] <= 0;
        end
      end
      main_stuckIsFree_11_index_127 <= 0;
      begin
        for(main_memory_index = 0; main_memory_index < 1; main_memory_index = main_memory_index + 1) begin
          main_stuckIsFree_11_value_128[main_memory_index] <= 0;
        end
      end
      main_root_129 <= 0;
      main_next_130 <= 0;
      main_isLeaf_131 <= 0;
      main_isFree_132 <= 0;
      main_root_133 <= 0;
      main_next_134 <= 0;
      main_isLeaf_135 <= 0;
      main_isFree_136 <= 0;
      main_next_137 <= 0;
      main_root_138 <= 0;
      main_isFree_139 <= 0;
      main_next_140 <= 0;
      main_root_141 <= 0;
      main_isFree_142 <= 0;
      stuckIsLeaf_7_requestedAt <= -1;
      stuckIsLeaf_8_requestedAt <= -1;
      stuckIsFree_11_requestedAt <= -1;
      freeNext_9_requestedAt <= -1;
      freeNext_10_requestedAt <= -1;
      stuckSize_5_requestedAt <= -1;
      stuckSize_6_requestedAt <= -1;
      stuckKeys_1_requestedAt <= -1;
      stuckKeys_2_requestedAt <= -1;
      stuckData_3_requestedAt <= -1;
      stuckData_4_requestedAt <= -1;
    end
    else if (processCurrent == 0) begin
      case(main_pc)
        0: begin
          // AAAA
          main_root_129 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            0: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1095:<init>|  Chip.java:1094:Zero|  Btree.java:0108:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        1: begin
          main_freeNext_9_index_124 <= main_root_129;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            1: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1752:<init>|  Chip.java:1751:ExecuteTransaction|  Btree.java:0109:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        2, 6, 14, 18, 26, 35: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          case (main_pc)
            2: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0110:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            6: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0140:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            14: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0110:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            18: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0140:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            26: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0160:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            35: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1759:<init>|  Chip.java:1758:waitResultOfTransaction|  Btree.java:0160:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        3: begin
          main_index1_122 <= freeNext_freeNext_9_result_0[0];
          main_pc <= main_pc + 1;
          case (main_pc)
            3: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0893:<init>|  Chip.java:0892:Copy|  Btree.java:0111:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        4: begin
          if (main_index1_122 == 0) begin
            main_returnCode <= 20;
            main_stop <= 1;
          end
          main_isLeaf_131 <= 1;
          // AAAA
          main_isFree_132 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            4: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0113:<init>|  Btree.java:0112:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        5: begin
          main_freeNext_9_index_124 <= main_index1_122;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_stuckIsLeaf_8_index_18 <= main_index1_122;
          main_stuckIsLeaf_8_value_19[0] <= main_isLeaf_131;
          stuckIsLeaf_8_requestedAt <= step;
          stuckIsLeaf_8_finishedAt <= -1;
          main_stuckIsFree_11_index_127 <= main_index1_122;
          main_stuckIsFree_11_value_128[0] <= main_isFree_132;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            5: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0128:<init>|  Btree.java:0127:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        7: begin
          main_next_130 <= freeNext_freeNext_9_result_0[0];
          main_pc <= main_pc + 1;
          case (main_pc)
            7: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0893:<init>|  Chip.java:0892:Copy|  Btree.java:0141:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        8: begin
          main_freeNext_10_index_125 <= main_root_129;
          main_freeNext_10_value_126[0] <= main_next_130;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            8: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0142:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        9, 21, 29, 31, 38, 40: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          case (main_pc)
            9: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0143:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            21: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0143:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            29: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0176:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            31: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0178:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            38: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0176:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            40: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0178:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        10, 22: begin
          if ((stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          case (main_pc)
            10: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0144:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            22: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0144:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        11, 23, 32, 41: begin
          if ((stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          case (main_pc)
            11: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0145:allocate|  Btree.java:4387:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            23: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0145:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            32: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0179:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
            41: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:1829:<init>|  Chip.java:1828:waitResultOfTransaction|  Btree.java:0179:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        12: begin
          // AAAA
          main_root_133 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            12: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1095:<init>|  Chip.java:1094:Zero|  Btree.java:0108:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        13: begin
          main_freeNext_9_index_124 <= main_root_133;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            13: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1752:<init>|  Chip.java:1751:ExecuteTransaction|  Btree.java:0109:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        15: begin
          main_index2_123 <= freeNext_freeNext_9_result_0[0];
          main_pc <= main_pc + 1;
          case (main_pc)
            15: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0893:<init>|  Chip.java:0892:Copy|  Btree.java:0111:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        16: begin
          if (main_index2_123 == 0) begin
            main_returnCode <= 20;
            main_stop <= 1;
          end
          // AAAA
          main_isLeaf_135 <= 0;
          // AAAA
          main_isFree_136 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            16: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0113:<init>|  Btree.java:0112:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        17: begin
          main_freeNext_9_index_124 <= main_index2_123;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_stuckIsLeaf_8_index_18 <= main_index2_123;
          main_stuckIsLeaf_8_value_19[0] <= main_isLeaf_135;
          stuckIsLeaf_8_requestedAt <= step;
          stuckIsLeaf_8_finishedAt <= -1;
          main_stuckIsFree_11_index_127 <= main_index2_123;
          main_stuckIsFree_11_value_128[0] <= main_isFree_136;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            17: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0128:<init>|  Btree.java:0127:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        19: begin
          main_next_134 <= freeNext_freeNext_9_result_0[0];
          main_pc <= main_pc + 1;
          case (main_pc)
            19: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:0893:<init>|  Chip.java:0892:Copy|  Btree.java:0141:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        20: begin
          main_freeNext_10_index_125 <= main_root_133;
          main_freeNext_10_value_126[0] <= main_next_134;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            20: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0142:allocate|  Btree.java:4388:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        24: begin
          // AAAA
          main_root_138 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            24: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1095:<init>|  Chip.java:1094:Zero|  Btree.java:0158:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        25: begin
          main_freeNext_9_index_124 <= main_root_138;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            25: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1752:<init>|  Chip.java:1751:ExecuteTransaction|  Btree.java:0159:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        27: begin
          main_next_137 <= freeNext_freeNext_9_result_0[0];
          main_freeNext_10_index_125 <= main_root_138;
          main_freeNext_10_value_126[0] <= main_index1_122;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_isFree_139 <= 1;
          main_pc <= main_pc + 1;
          case (main_pc)
            27: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0163:<init>|  Btree.java:0162:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        28: begin
          main_stuckIsFree_11_index_127 <= main_index1_122;
          main_stuckIsFree_11_value_128[0] <= main_isFree_139;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            28: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0175:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        30: begin
          main_freeNext_10_index_125 <= main_index1_122;
          main_freeNext_10_value_126[0] <= main_next_137;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            30: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0177:free|  Btree.java:4425:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        33: begin
          // AAAA
          main_root_141 <= 0;
          main_pc <= main_pc + 1;
          case (main_pc)
            33: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1095:<init>|  Chip.java:1094:Zero|  Btree.java:0158:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        34: begin
          main_freeNext_9_index_124 <= main_root_141;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            34: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1752:<init>|  Chip.java:1751:ExecuteTransaction|  Btree.java:0159:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        36: begin
          main_next_140 <= freeNext_freeNext_9_result_0[0];
          main_freeNext_10_index_125 <= main_root_141;
          main_freeNext_10_value_126[0] <= main_index2_123;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_isFree_142 <= 1;
          main_pc <= main_pc + 1;
          case (main_pc)
            36: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Btree.java:0163:<init>|  Btree.java:0162:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        37: begin
          main_stuckIsFree_11_index_127 <= main_index2_123;
          main_stuckIsFree_11_value_128[0] <= main_isFree_142;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            37: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0175:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        39: begin
          main_freeNext_10_index_125 <= main_index2_123;
          main_freeNext_10_value_126[0] <= main_next_140;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          case (main_pc)
            39: begin
              begin
                integer f;
                f = $fopen("verilog/trace_verilog.txt", "a");
                $fdisplay(f, "Location: Chip.java:0574:<init>|  Chip.java:1822:<init>|  Chip.java:1821:ExecuteTransaction|  Btree.java:0177:free|  Btree.java:4426:test_allocate|  Btree.java:7040:newTests|  Btree.java:7046:main|");
                $fclose(f);
              end
            end
          endcase
        end
        default: main_stop <= 1;
      endcase
    end
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0001
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[4*1];
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
      for(stuckIsLeaf_memory_index = 1; stuckIsLeaf_memory_index < 4; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) begin
        stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= 0;
      end
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
  // Process: stuckIsFree  process_stuckIsFree_0002
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsFree_memory[4*1];
  integer stuckIsFree_11_requestedAt;
  integer stuckIsFree_11_finishedAt;
  integer stuckIsFree_stuckIsFree_11_returnCode;
  integer stuckIsFree_pc;
  integer stuckIsFree_stop;
  integer stuckIsFree_returnCode;
  integer stuckIsFree_memory_index;
  always @ (posedge clock) begin
    if (step < 0) begin
      stuckIsFree_pc <= 0;
      stuckIsFree_stop <= 0;
      stuckIsFree_returnCode <= 0;
      stuckIsFree_11_finishedAt <= -1;
      stuckIsFree_stuckIsFree_11_returnCode <= 0;
      for(stuckIsFree_memory_index = 0; stuckIsFree_memory_index < 1; stuckIsFree_memory_index = stuckIsFree_memory_index + 1) begin
        stuckIsFree_memory[stuckIsFree_memory_index] <= 0;
      end
      stuckIsFree_memory[1] <= 1;
      stuckIsFree_memory[2] <= 1;
      stuckIsFree_memory[3] <= 1;
    end
    else if (processCurrent == 2) begin
      case(stuckIsFree_pc)
        0: begin
          if ((stuckIsFree_11_requestedAt > stuckIsFree_11_finishedAt && stuckIsFree_11_requestedAt != step)) begin
            begin
              for(stuckIsFree_memory_index = 0; stuckIsFree_memory_index < 1; stuckIsFree_memory_index = stuckIsFree_memory_index + 1) begin
                stuckIsFree_memory[main_stuckIsFree_11_index_127*1+stuckIsFree_memory_index] <= main_stuckIsFree_11_value_128[stuckIsFree_memory_index];
              end
            end
            stuckIsFree_11_finishedAt <= step;
          end
          else begin
          end
        end
        default: stuckIsFree_stop <= 1;
      endcase
    end
  end
  // Process: freeNext  process_freeNext_0003
  (* ram_style = "block" *)
  reg [3-1:0] freeNext_memory[4*1];
  (* nomem2reg *)
  reg [3-1:0] freeNext_freeNext_9_result_0[1];
  integer freeNext_9_requestedAt;
  integer freeNext_9_finishedAt;
  integer freeNext_freeNext_9_returnCode;
  integer freeNext_10_requestedAt;
  integer freeNext_10_finishedAt;
  integer freeNext_freeNext_10_returnCode;
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
        for(freeNext_memory_index = 0; freeNext_memory_index < 1; freeNext_memory_index = freeNext_memory_index + 1) begin
          freeNext_freeNext_9_result_0[freeNext_memory_index] <= 0;
        end
      end
      freeNext_9_finishedAt <= -1;
      freeNext_freeNext_9_returnCode <= 0;
      freeNext_10_finishedAt <= -1;
      freeNext_freeNext_10_returnCode <= 0;
      freeNext_memory[0] <= 1;
      freeNext_memory[1] <= 2;
      freeNext_memory[2] <= 3;
      for(freeNext_memory_index = 3; freeNext_memory_index < 4; freeNext_memory_index = freeNext_memory_index + 1) begin
        freeNext_memory[freeNext_memory_index] <= 0;
      end
    end
    else if (processCurrent == 3) begin
      case(freeNext_pc)
        0: begin
          if ((freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step)) begin
            begin
              for(freeNext_memory_index = 0; freeNext_memory_index < 1; freeNext_memory_index = freeNext_memory_index + 1) begin
                freeNext_freeNext_9_result_0[freeNext_memory_index] <= freeNext_memory[main_freeNext_9_index_124*1+freeNext_memory_index];
              end
            end
            freeNext_9_finishedAt <= step;
          end
          else if ((freeNext_10_requestedAt > freeNext_10_finishedAt && freeNext_10_requestedAt != step)) begin
            begin
              for(freeNext_memory_index = 0; freeNext_memory_index < 1; freeNext_memory_index = freeNext_memory_index + 1) begin
                freeNext_memory[main_freeNext_10_index_125*1+freeNext_memory_index] <= main_freeNext_10_value_126[freeNext_memory_index];
              end
            end
            freeNext_10_finishedAt <= step;
          end
          else begin
          end
        end
        default: freeNext_stop <= 1;
      endcase
    end
  end
  // Process: stuckSize  process_stuckSize_0004
  (* ram_style = "block" *)
  reg [3-1:0] stuckSize_memory[4*1];
  (* nomem2reg *)
  reg [3-1:0] stuckSize_stuckSize_5_result_0[1];
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
      for(stuckSize_memory_index = 0; stuckSize_memory_index < 4; stuckSize_memory_index = stuckSize_memory_index + 1) begin
        stuckSize_memory[stuckSize_memory_index] <= 0;
      end
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
  // Process: stuckKeys  process_stuckKeys_0005
  (* ram_style = "block" *)
  reg [8-1:0] stuckKeys_memory[4*4];
  (* nomem2reg *)
  reg [8-1:0] stuckKeys_stuckKeys_1_result_0[4];
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
        for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 4; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
          stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= 0;
        end
      end
      stuckKeys_1_finishedAt <= -1;
      stuckKeys_stuckKeys_1_returnCode <= 0;
      stuckKeys_2_finishedAt <= -1;
      stuckKeys_stuckKeys_2_returnCode <= 0;
      for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
        stuckKeys_memory[stuckKeys_memory_index] <= 0;
      end
    end
    else if (processCurrent == 5) begin
      case(stuckKeys_pc)
        0: begin
          if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
            begin
              for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 4; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                stuckKeys_stuckKeys_1_result_0[stuckKeys_memory_index] <= stuckKeys_memory[main_stuckKeys_1_index_8*4+stuckKeys_memory_index];
              end
            end
            stuckKeys_1_finishedAt <= step;
          end
          else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
            begin
              for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 4; stuckKeys_memory_index = stuckKeys_memory_index + 1) begin
                stuckKeys_memory[main_stuckKeys_2_index_9*4+stuckKeys_memory_index] <= main_stuckKeys_2_value_10[stuckKeys_memory_index];
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
  reg [8-1:0] stuckData_memory[4*4];
  (* nomem2reg *)
  reg [8-1:0] stuckData_stuckData_3_result_0[4];
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
        for(stuckData_memory_index = 0; stuckData_memory_index < 4; stuckData_memory_index = stuckData_memory_index + 1) begin
          stuckData_stuckData_3_result_0[stuckData_memory_index] <= 0;
        end
      end
      stuckData_3_finishedAt <= -1;
      stuckData_stuckData_3_returnCode <= 0;
      stuckData_4_finishedAt <= -1;
      stuckData_stuckData_4_returnCode <= 0;
      for(stuckData_memory_index = 0; stuckData_memory_index < 16; stuckData_memory_index = stuckData_memory_index + 1) begin
        stuckData_memory[stuckData_memory_index] <= 0;
      end
    end
    else if (processCurrent == 6) begin
      case(stuckData_pc)
        0: begin
          if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
            begin
              for(stuckData_memory_index = 0; stuckData_memory_index < 4; stuckData_memory_index = stuckData_memory_index + 1) begin
                stuckData_stuckData_3_result_0[stuckData_memory_index] <= stuckData_memory[main_stuckData_3_index_11*4+stuckData_memory_index];
              end
            end
            stuckData_3_finishedAt <= step;
          end
          else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
            begin
              for(stuckData_memory_index = 0; stuckData_memory_index < 4; stuckData_memory_index = stuckData_memory_index + 1) begin
                stuckData_memory[main_stuckData_4_index_12*4+stuckData_memory_index] <= main_stuckData_4_value_13[stuckData_memory_index];
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
  task chipPrint;
    begin
      integer o;
      o = $fopen("verilog/trace_verilog.txt", "a");
      if (!o) o = $fopen("../verilog/trace_verilog.txt", "a");
      if (!o) $display("Cannot create trace folder: verilog/trace_verilog.txt");
      $fwrite(o, "Chip: %-16s step: %1d, maxSteps: %1d, running: %1d\n", "Btree", step, maxSteps, !stop);
      $fwrite(o, "  Processes:\n");

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 0, "main", 42, main_pc, main_returnCode);
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-50s = %1d\n",  "main_index_0", main_index_0);
      $fwrite(o, "        %-50s = %1d\n",  "main_size_1", main_size_1);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_2", main_isLeaf_2);
      $fwrite(o, "        %-50s = %1d\n",  "main_nextFree_3", main_nextFree_3);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_4", 0, main_Key_4[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_4", 1, main_Key_4[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_4", 2, main_Key_4[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_4", 3, main_Key_4[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 0, main_Compares_5[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 1, main_Compares_5[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 2, main_Compares_5[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_5", 3, main_Compares_5[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 0, main_Collapse_6[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 1, main_Collapse_6[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 2, main_Collapse_6[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_6", 3, main_Collapse_6[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 0, main_Data_7[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 1, main_Data_7[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 2, main_Data_7[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_7", 3, main_Data_7[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_1_index_8", main_stuckKeys_1_index_8);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckKeys_2_index_9", main_stuckKeys_2_index_9);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 0, main_stuckKeys_2_value_10[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 1, main_stuckKeys_2_value_10[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 2, main_stuckKeys_2_value_10[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 3, main_stuckKeys_2_value_10[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_3_index_11", main_stuckData_3_index_11);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckData_4_index_12", main_stuckData_4_index_12);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 0, main_stuckData_4_value_13[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 1, main_stuckData_4_value_13[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 2, main_stuckData_4_value_13[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckData_4_value_13", 3, main_stuckData_4_value_13[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_31", 0, main_Key_31[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_31", 1, main_Key_31[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_31", 2, main_Key_31[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_31", 3, main_Key_31[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 0, main_Compares_32[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 1, main_Compares_32[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 2, main_Compares_32[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_32", 3, main_Compares_32[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 0, main_Collapse_33[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 1, main_Collapse_33[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 2, main_Collapse_33[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_33", 3, main_Collapse_33[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 0, main_Data_34[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 1, main_Data_34[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 2, main_Data_34[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_34", 3, main_Data_34[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_46", 0, main_Key_46[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_46", 1, main_Key_46[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_46", 2, main_Key_46[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_46", 3, main_Key_46[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 0, main_Compares_47[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 1, main_Compares_47[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 2, main_Compares_47[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_47", 3, main_Compares_47[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 0, main_Collapse_48[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 1, main_Collapse_48[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 2, main_Collapse_48[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_48", 3, main_Collapse_48[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 0, main_Data_49[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 1, main_Data_49[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 2, main_Data_49[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_49", 3, main_Data_49[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_61", 0, main_Key_61[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_61", 1, main_Key_61[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_61", 2, main_Key_61[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_61", 3, main_Key_61[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 0, main_Compares_62[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 1, main_Compares_62[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 2, main_Compares_62[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_62", 3, main_Compares_62[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 0, main_Collapse_63[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 1, main_Collapse_63[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 2, main_Collapse_63[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_63", 3, main_Collapse_63[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 0, main_Data_64[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 1, main_Data_64[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 2, main_Data_64[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_64", 3, main_Data_64[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_76", 0, main_Key_76[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_76", 1, main_Key_76[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_76", 2, main_Key_76[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_76", 3, main_Key_76[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 0, main_Compares_77[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 1, main_Compares_77[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 2, main_Compares_77[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_77", 3, main_Compares_77[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 0, main_Collapse_78[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 1, main_Collapse_78[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 2, main_Collapse_78[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_78", 3, main_Collapse_78[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 0, main_Data_79[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 1, main_Data_79[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 2, main_Data_79[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_79", 3, main_Data_79[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_91", 0, main_Key_91[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_91", 1, main_Key_91[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_91", 2, main_Key_91[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_91", 3, main_Key_91[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 0, main_Compares_92[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 1, main_Compares_92[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 2, main_Compares_92[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_92", 3, main_Compares_92[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 0, main_Collapse_93[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 1, main_Collapse_93[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 2, main_Collapse_93[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_93", 3, main_Collapse_93[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 0, main_Data_94[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 1, main_Data_94[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 2, main_Data_94[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_94", 3, main_Data_94[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_106", 0, main_Key_106[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_106", 1, main_Key_106[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_106", 2, main_Key_106[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Key_106", 3, main_Key_106[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 0, main_Compares_107[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 1, main_Compares_107[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 2, main_Compares_107[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Compares_107", 3, main_Compares_107[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 0, main_Collapse_108[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 1, main_Collapse_108[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 2, main_Collapse_108[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Collapse_108", 3, main_Collapse_108[3]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 0, main_Data_109[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 1, main_Data_109[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 2, main_Data_109[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_Data_109", 3, main_Data_109[3]);
      $fwrite(o, "        %-50s = %1d\n",  "main_Found_110", main_Found_110);
      $fwrite(o, "        %-50s = %1d\n",  "main_Key_111", main_Key_111);
      $fwrite(o, "        %-50s = %1d\n",  "main_FoundKey_112", main_FoundKey_112);
      $fwrite(o, "        %-50s = %1d\n",  "main_Data_113", main_Data_113);
      $fwrite(o, "        %-50s = %1d\n",  "main_BtreeIndex_114", main_BtreeIndex_114);
      $fwrite(o, "        %-50s = %1d\n",  "main_StuckIndex_115", main_StuckIndex_115);
      $fwrite(o, "        %-50s = %1d\n",  "main_MergeSuccess_116", main_MergeSuccess_116);
      $fwrite(o, "        %-50s = %1d\n",  "main_sum_117", main_sum_117);
      $fwrite(o, "        %-50s = %1d\n",  "main_can_118", main_can_118);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexLeft_119", main_indexLeft_119);
      $fwrite(o, "        %-50s = %1d\n",  "main_indexRight_120", main_indexRight_120);
      $fwrite(o, "        %-50s = %1d\n",  "main_midKey_121", main_midKey_121);
      $fwrite(o, "        %-50s = %1d\n",  "main_index1_122", main_index1_122);
      $fwrite(o, "        %-50s = %1d\n",  "main_index2_123", main_index2_123);
      $fwrite(o, "        %-50s = %1d\n",  "main_freeNext_9_index_124", main_freeNext_9_index_124);
      $fwrite(o, "        %-50s = %1d\n",  "main_freeNext_10_index_125", main_freeNext_10_index_125);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_freeNext_10_value_126", 0, main_freeNext_10_value_126[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_stuckIsFree_11_index_127", main_stuckIsFree_11_index_127);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "main_stuckIsFree_11_value_128", 0, main_stuckIsFree_11_value_128[0]);
      $fwrite(o, "        %-50s = %1d\n",  "main_root_129", main_root_129);
      $fwrite(o, "        %-50s = %1d\n",  "main_next_130", main_next_130);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_131", main_isLeaf_131);
      $fwrite(o, "        %-50s = %1d\n",  "main_isFree_132", main_isFree_132);
      $fwrite(o, "        %-50s = %1d\n",  "main_root_133", main_root_133);
      $fwrite(o, "        %-50s = %1d\n",  "main_next_134", main_next_134);
      $fwrite(o, "        %-50s = %1d\n",  "main_isLeaf_135", main_isLeaf_135);
      $fwrite(o, "        %-50s = %1d\n",  "main_isFree_136", main_isFree_136);
      $fwrite(o, "        %-50s = %1d\n",  "main_next_137", main_next_137);
      $fwrite(o, "        %-50s = %1d\n",  "main_root_138", main_root_138);
      $fwrite(o, "        %-50s = %1d\n",  "main_isFree_139", main_isFree_139);
      $fwrite(o, "        %-50s = %1d\n",  "main_next_140", main_next_140);
      $fwrite(o, "        %-50s = %1d\n",  "main_root_141", main_root_141);
      $fwrite(o, "        %-50s = %1d\n",  "main_isFree_142", main_isFree_142);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsLeaf", 1, stuckIsLeaf_pc, stuckIsLeaf_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[1]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[2]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[3]);
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
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 1, 1);
      $fwrite(o, "        %2d", stuckIsFree_memory[0]);
      $fwrite(o, ", %2d", stuckIsFree_memory[1]);
      $fwrite(o, ", %2d", stuckIsFree_memory[2]);
      $fwrite(o, ", %2d", stuckIsFree_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsFree_11", stuckIsFree_11_requestedAt, stuckIsFree_11_finishedAt, stuckIsFree_stuckIsFree_11_returnCode, (stuckIsFree_11_requestedAt > stuckIsFree_11_finishedAt && stuckIsFree_11_requestedAt != step), (stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckIsFree_11_index_127", main_stuckIsFree_11_index_127);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckIsFree_11_value_128", 0, main_stuckIsFree_11_value_128[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", freeNext_memory[0]);
      $fwrite(o, ", %2d", freeNext_memory[1]);
      $fwrite(o, ", %2d", freeNext_memory[2]);
      $fwrite(o, ", %2d", freeNext_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "freeNext_freeNext_9_result_0", 0, freeNext_freeNext_9_result_0[0]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "freeNext_9", freeNext_9_requestedAt, freeNext_9_finishedAt, freeNext_freeNext_9_returnCode, (freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step), (freeNext_9_requestedAt < freeNext_9_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_freeNext_9_index_124", main_freeNext_9_index_124);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "freeNext_freeNext_9_result_0", 0, freeNext_freeNext_9_result_0[0]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "freeNext_10", freeNext_10_requestedAt, freeNext_10_finishedAt, freeNext_freeNext_10_returnCode, (freeNext_10_requestedAt > freeNext_10_finishedAt && freeNext_10_requestedAt != step), (freeNext_10_requestedAt < freeNext_10_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_freeNext_10_index_125", main_freeNext_10_index_125);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_freeNext_10_value_126", 0, main_freeNext_10_value_126[0]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0]);
      $fwrite(o, ", %2d", stuckSize_memory[1]);
      $fwrite(o, ", %2d", stuckSize_memory[2]);
      $fwrite(o, ", %2d", stuckSize_memory[3]);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 0, stuckKeys_stuckKeys_1_result_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 1, stuckKeys_stuckKeys_1_result_0[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 2, stuckKeys_stuckKeys_1_result_0[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 3, stuckKeys_stuckKeys_1_result_0[3]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_1_index_8", main_stuckKeys_1_index_8);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 0, stuckKeys_stuckKeys_1_result_0[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 1, stuckKeys_stuckKeys_1_result_0[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 2, stuckKeys_stuckKeys_1_result_0[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckKeys_stuckKeys_1_result_0", 3, stuckKeys_stuckKeys_1_result_0[3]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckKeys_2_index_9", main_stuckKeys_2_index_9);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 0, main_stuckKeys_2_value_10[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 1, main_stuckKeys_2_value_10[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 2, main_stuckKeys_2_value_10[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckKeys_2_value_10", 3, main_stuckKeys_2_value_10[3]);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 6, "stuckData", 1, stuckData_pc, stuckData_returnCode);
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
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 0, stuckData_stuckData_3_result_0[0]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 1, stuckData_stuckData_3_result_0[1]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 2, stuckData_stuckData_3_result_0[2]);
      $fwrite(o, "        %-44s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 3, stuckData_stuckData_3_result_0[3]);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_3_index_11", main_stuckData_3_index_11);
      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 0, stuckData_stuckData_3_result_0[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 1, stuckData_stuckData_3_result_0[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 2, stuckData_stuckData_3_result_0[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "stuckData_stuckData_3_result_0", 3, stuckData_stuckData_3_result_0[3]);
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-46s = %1d\n",  "main_stuckData_4_index_12", main_stuckData_4_index_12);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 0, main_stuckData_4_value_13[0]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 1, main_stuckData_4_value_13[1]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 2, main_stuckData_4_value_13[2]);
      $fwrite(o, "            %-40s[%4d] = %1d\n",  "main_stuckData_4_value_13", 3, main_stuckData_4_value_13[3]);
      $fclose(o);
    end
  endtask
endmodule
