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
  reg [8-1:0] main_Key_0_4;
  reg [8-1:0] main_Data_0_5;
  reg [8-1:0] main_Key_1_6;
  reg [8-1:0] main_Data_1_7;
  reg [8-1:0] main_Key_2_8;
  reg [8-1:0] main_Data_2_9;
  reg [8-1:0] main_Key_3_10;
  reg [8-1:0] main_Data_3_11;
  reg [2-1:0] main_stuckKeys_1_index_12;
  reg [2-1:0] main_stuckKeys_2_index_13;
  reg [8-1:0] main_stuckKeys_2_value_14;
  reg [8-1:0] main_stuckKeys_2_value_15;
  reg [8-1:0] main_stuckKeys_2_value_16;
  reg [8-1:0] main_stuckKeys_2_value_17;
  reg [2-1:0] main_stuckData_3_index_18;
  reg [2-1:0] main_stuckData_4_index_19;
  reg [8-1:0] main_stuckData_4_value_20;
  reg [8-1:0] main_stuckData_4_value_21;
  reg [8-1:0] main_stuckData_4_value_22;
  reg [8-1:0] main_stuckData_4_value_23;
  reg [2-1:0] main_stuckSize_5_index_24;
  reg [2-1:0] main_stuckSize_6_index_25;
  reg [3-1:0] main_stuckSize_6_value_26;
  reg [2-1:0] main_stuckIsLeaf_7_index_27;
  reg [2-1:0] main_stuckIsLeaf_8_index_28;
  reg [1-1:0] main_stuckIsLeaf_8_value_29;
  reg [1-1:0] main_Found_30;
  reg [8-1:0] main_Key_31;
  reg [8-1:0] main_FoundKey_32;
  reg [8-1:0] main_Data_33;
  reg [3-1:0] main_BtreeIndex_34;
  reg [3-1:0] main_StuckIndex_35;
  reg [1-1:0] main_MergeSuccess_36;
  reg [3-1:0] main_index_37;
  reg [3-1:0] main_size_38;
  reg [1-1:0] main_isLeaf_39;
  reg [3-1:0] main_nextFree_40;
  reg [8-1:0] main_Key_0_41;
  reg [8-1:0] main_Data_0_42;
  reg [8-1:0] main_Key_1_43;
  reg [8-1:0] main_Data_1_44;
  reg [8-1:0] main_Key_2_45;
  reg [8-1:0] main_Data_2_46;
  reg [8-1:0] main_Key_3_47;
  reg [8-1:0] main_Data_3_48;
  reg [1-1:0] main_Found_49;
  reg [8-1:0] main_Key_50;
  reg [8-1:0] main_FoundKey_51;
  reg [8-1:0] main_Data_52;
  reg [3-1:0] main_BtreeIndex_53;
  reg [3-1:0] main_StuckIndex_54;
  reg [1-1:0] main_MergeSuccess_55;
  reg [3-1:0] main_index_56;
  reg [3-1:0] main_size_57;
  reg [1-1:0] main_isLeaf_58;
  reg [3-1:0] main_nextFree_59;
  reg [8-1:0] main_Key_0_60;
  reg [8-1:0] main_Data_0_61;
  reg [8-1:0] main_Key_1_62;
  reg [8-1:0] main_Data_1_63;
  reg [8-1:0] main_Key_2_64;
  reg [8-1:0] main_Data_2_65;
  reg [8-1:0] main_Key_3_66;
  reg [8-1:0] main_Data_3_67;
  reg [1-1:0] main_Found_68;
  reg [8-1:0] main_Key_69;
  reg [8-1:0] main_FoundKey_70;
  reg [8-1:0] main_Data_71;
  reg [3-1:0] main_BtreeIndex_72;
  reg [3-1:0] main_StuckIndex_73;
  reg [1-1:0] main_MergeSuccess_74;
  reg [3-1:0] main_index_75;
  reg [3-1:0] main_size_76;
  reg [1-1:0] main_isLeaf_77;
  reg [3-1:0] main_nextFree_78;
  reg [8-1:0] main_Key_0_79;
  reg [8-1:0] main_Data_0_80;
  reg [8-1:0] main_Key_1_81;
  reg [8-1:0] main_Data_1_82;
  reg [8-1:0] main_Key_2_83;
  reg [8-1:0] main_Data_2_84;
  reg [8-1:0] main_Key_3_85;
  reg [8-1:0] main_Data_3_86;
  reg [1-1:0] main_Found_87;
  reg [8-1:0] main_Key_88;
  reg [8-1:0] main_FoundKey_89;
  reg [8-1:0] main_Data_90;
  reg [3-1:0] main_BtreeIndex_91;
  reg [3-1:0] main_StuckIndex_92;
  reg [1-1:0] main_MergeSuccess_93;
  reg [3-1:0] main_index_94;
  reg [3-1:0] main_size_95;
  reg [1-1:0] main_isLeaf_96;
  reg [3-1:0] main_nextFree_97;
  reg [8-1:0] main_Key_0_98;
  reg [8-1:0] main_Data_0_99;
  reg [8-1:0] main_Key_1_100;
  reg [8-1:0] main_Data_1_101;
  reg [8-1:0] main_Key_2_102;
  reg [8-1:0] main_Data_2_103;
  reg [8-1:0] main_Key_3_104;
  reg [8-1:0] main_Data_3_105;
  reg [1-1:0] main_Found_106;
  reg [8-1:0] main_Key_107;
  reg [8-1:0] main_FoundKey_108;
  reg [8-1:0] main_Data_109;
  reg [3-1:0] main_BtreeIndex_110;
  reg [3-1:0] main_StuckIndex_111;
  reg [1-1:0] main_MergeSuccess_112;
  reg [3-1:0] main_index1_113;
  reg [3-1:0] main_index2_114;
  reg [2-1:0] main_freeNext_9_index_115;
  reg [2-1:0] main_freeNext_10_index_116;
  reg [3-1:0] main_freeNext_10_value_117;
  reg [2-1:0] main_stuckIsFree_11_index_118;
  reg [1-1:0] main_stuckIsFree_11_value_119;
  reg [3-1:0] main_root_120;
  reg [3-1:0] main_next_121;
  reg [1-1:0] main_isLeaf_122;
  reg [1-1:0] main_isFree_123;
  reg [3-1:0] main_root_124;
  reg [3-1:0] main_next_125;
  reg [1-1:0] main_isLeaf_126;
  reg [1-1:0] main_isFree_127;
  reg [3-1:0] main_next_128;
  reg [3-1:0] main_root_129;
  reg [1-1:0] main_isFree_130;
  reg [3-1:0] main_next_131;
  reg [3-1:0] main_root_132;
  reg [1-1:0] main_isFree_133;
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
      main_Key_0_4 <= 0;
      main_Data_0_5 <= 0;
      main_Key_1_6 <= 0;
      main_Data_1_7 <= 0;
      main_Key_2_8 <= 0;
      main_Data_2_9 <= 0;
      main_Key_3_10 <= 0;
      main_Data_3_11 <= 0;
      main_stuckKeys_1_index_12 <= 0;
      main_stuckKeys_2_index_13 <= 0;
      main_stuckKeys_2_value_14 <= 0;
      main_stuckKeys_2_value_15 <= 0;
      main_stuckKeys_2_value_16 <= 0;
      main_stuckKeys_2_value_17 <= 0;
      main_stuckData_3_index_18 <= 0;
      main_stuckData_4_index_19 <= 0;
      main_stuckData_4_value_20 <= 0;
      main_stuckData_4_value_21 <= 0;
      main_stuckData_4_value_22 <= 0;
      main_stuckData_4_value_23 <= 0;
      main_stuckSize_5_index_24 <= 0;
      main_stuckSize_6_index_25 <= 0;
      main_stuckSize_6_value_26 <= 0;
      main_stuckIsLeaf_7_index_27 <= 0;
      main_stuckIsLeaf_8_index_28 <= 0;
      main_stuckIsLeaf_8_value_29 <= 0;
      main_Found_30 <= 0;
      main_Key_31 <= 0;
      main_FoundKey_32 <= 0;
      main_Data_33 <= 0;
      main_BtreeIndex_34 <= 0;
      main_StuckIndex_35 <= 0;
      main_MergeSuccess_36 <= 0;
      main_index_37 <= 0;
      main_size_38 <= 0;
      main_isLeaf_39 <= 0;
      main_nextFree_40 <= 0;
      main_Key_0_41 <= 0;
      main_Data_0_42 <= 0;
      main_Key_1_43 <= 0;
      main_Data_1_44 <= 0;
      main_Key_2_45 <= 0;
      main_Data_2_46 <= 0;
      main_Key_3_47 <= 0;
      main_Data_3_48 <= 0;
      main_Found_49 <= 0;
      main_Key_50 <= 0;
      main_FoundKey_51 <= 0;
      main_Data_52 <= 0;
      main_BtreeIndex_53 <= 0;
      main_StuckIndex_54 <= 0;
      main_MergeSuccess_55 <= 0;
      main_index_56 <= 0;
      main_size_57 <= 0;
      main_isLeaf_58 <= 0;
      main_nextFree_59 <= 0;
      main_Key_0_60 <= 0;
      main_Data_0_61 <= 0;
      main_Key_1_62 <= 0;
      main_Data_1_63 <= 0;
      main_Key_2_64 <= 0;
      main_Data_2_65 <= 0;
      main_Key_3_66 <= 0;
      main_Data_3_67 <= 0;
      main_Found_68 <= 0;
      main_Key_69 <= 0;
      main_FoundKey_70 <= 0;
      main_Data_71 <= 0;
      main_BtreeIndex_72 <= 0;
      main_StuckIndex_73 <= 0;
      main_MergeSuccess_74 <= 0;
      main_index_75 <= 0;
      main_size_76 <= 0;
      main_isLeaf_77 <= 0;
      main_nextFree_78 <= 0;
      main_Key_0_79 <= 0;
      main_Data_0_80 <= 0;
      main_Key_1_81 <= 0;
      main_Data_1_82 <= 0;
      main_Key_2_83 <= 0;
      main_Data_2_84 <= 0;
      main_Key_3_85 <= 0;
      main_Data_3_86 <= 0;
      main_Found_87 <= 0;
      main_Key_88 <= 0;
      main_FoundKey_89 <= 0;
      main_Data_90 <= 0;
      main_BtreeIndex_91 <= 0;
      main_StuckIndex_92 <= 0;
      main_MergeSuccess_93 <= 0;
      main_index_94 <= 0;
      main_size_95 <= 0;
      main_isLeaf_96 <= 0;
      main_nextFree_97 <= 0;
      main_Key_0_98 <= 0;
      main_Data_0_99 <= 0;
      main_Key_1_100 <= 0;
      main_Data_1_101 <= 0;
      main_Key_2_102 <= 0;
      main_Data_2_103 <= 0;
      main_Key_3_104 <= 0;
      main_Data_3_105 <= 0;
      main_Found_106 <= 0;
      main_Key_107 <= 0;
      main_FoundKey_108 <= 0;
      main_Data_109 <= 0;
      main_BtreeIndex_110 <= 0;
      main_StuckIndex_111 <= 0;
      main_MergeSuccess_112 <= 0;
      main_index1_113 <= 0;
      main_index2_114 <= 0;
      main_freeNext_9_index_115 <= 0;
      main_freeNext_10_index_116 <= 0;
      main_freeNext_10_value_117 <= 0;
      main_stuckIsFree_11_index_118 <= 0;
      main_stuckIsFree_11_value_119 <= 0;
      main_root_120 <= 0;
      main_next_121 <= 0;
      main_isLeaf_122 <= 0;
      main_isFree_123 <= 0;
      main_root_124 <= 0;
      main_next_125 <= 0;
      main_isLeaf_126 <= 0;
      main_isFree_127 <= 0;
      main_next_128 <= 0;
      main_root_129 <= 0;
      main_isFree_130 <= 0;
      main_next_131 <= 0;
      main_root_132 <= 0;
      main_isFree_133 <= 0;
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
          main_root_120 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0821:<init>|  Chip.java:0820:Zero|  Btree.java:0091:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        1: begin
          main_freeNext_9_index_115 <= main_root_120;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1352:<init>|  Chip.java:1351:ExecuteTransaction|  Btree.java:0092:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        2: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0093:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        3: begin
          main_index1_113 <= freeNext_freeNext_9_result_0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0699:<init>|  Chip.java:0698:Copy|  Btree.java:0094:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        4: begin
          if (main_index1_113 == 0) begin
            main_returnCode <= 20;
            main_stop <= 1;
          end
          main_isLeaf_122 <= 1;
          main_isFree_123 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0097:<init>|  Btree.java:0096:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        5: begin
          main_freeNext_9_index_115 <= main_index1_113;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_stuckIsLeaf_8_index_28 <= main_index1_113;
          main_stuckIsLeaf_8_value_29 <= main_isLeaf_122;
          stuckIsLeaf_8_requestedAt <= step;
          stuckIsLeaf_8_finishedAt <= -1;
          main_stuckIsFree_11_index_118 <= main_index1_113;
          main_stuckIsFree_11_value_119 <= main_isFree_123;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0112:<init>|  Btree.java:0111:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        6: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0124:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        7: begin
          main_next_121 <= freeNext_freeNext_9_result_0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0699:<init>|  Chip.java:0698:Copy|  Btree.java:0125:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        8: begin
          main_freeNext_10_index_116 <= main_root_120;
          main_freeNext_10_value_117 <= main_next_121;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0126:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        9: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0127:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        10: begin
          if ((stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0128:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        11: begin
          if ((stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0129:allocate|  Btree.java:4639:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        12: begin
          main_root_124 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0821:<init>|  Chip.java:0820:Zero|  Btree.java:0091:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        13: begin
          main_freeNext_9_index_115 <= main_root_124;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1352:<init>|  Chip.java:1351:ExecuteTransaction|  Btree.java:0092:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        14: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0093:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        15: begin
          main_index2_114 <= freeNext_freeNext_9_result_0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0699:<init>|  Chip.java:0698:Copy|  Btree.java:0094:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        16: begin
          if (main_index2_114 == 0) begin
            main_returnCode <= 20;
            main_stop <= 1;
          end
          main_isLeaf_126 <= 0;
          main_isFree_127 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0097:<init>|  Btree.java:0096:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        17: begin
          main_freeNext_9_index_115 <= main_index2_114;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_stuckIsLeaf_8_index_28 <= main_index2_114;
          main_stuckIsLeaf_8_value_29 <= main_isLeaf_126;
          stuckIsLeaf_8_requestedAt <= step;
          stuckIsLeaf_8_finishedAt <= -1;
          main_stuckIsFree_11_index_118 <= main_index2_114;
          main_stuckIsFree_11_value_119 <= main_isFree_127;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0112:<init>|  Btree.java:0111:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        18: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0124:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        19: begin
          main_next_125 <= freeNext_freeNext_9_result_0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0699:<init>|  Chip.java:0698:Copy|  Btree.java:0125:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        20: begin
          main_freeNext_10_index_116 <= main_root_124;
          main_freeNext_10_value_117 <= main_next_125;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0126:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        21: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0127:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        22: begin
          if ((stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0128:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        23: begin
          if ((stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0129:allocate|  Btree.java:4640:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        24: begin
          main_root_129 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0821:<init>|  Chip.java:0820:Zero|  Btree.java:0142:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        25: begin
          main_freeNext_9_index_115 <= main_root_129;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1352:<init>|  Chip.java:1351:ExecuteTransaction|  Btree.java:0143:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        26: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0144:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        27: begin
          main_next_128 <= freeNext_freeNext_9_result_0;
          main_freeNext_10_index_116 <= main_root_129;
          main_freeNext_10_value_117 <= main_index1_113;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_isFree_130 <= 1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0147:<init>|  Btree.java:0146:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        28: begin
          main_stuckIsFree_11_index_118 <= main_index1_113;
          main_stuckIsFree_11_value_119 <= main_isFree_130;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0159:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        29: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0160:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        30: begin
          main_freeNext_10_index_116 <= main_index1_113;
          main_freeNext_10_value_117 <= main_next_128;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0161:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        31: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0162:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        32: begin
          if ((stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0163:free|  Btree.java:4677:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        33: begin
          main_root_132 <= 0;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:0821:<init>|  Chip.java:0820:Zero|  Btree.java:0142:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        34: begin
          main_freeNext_9_index_115 <= main_root_132;
          freeNext_9_requestedAt <= step;
          freeNext_9_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1352:<init>|  Chip.java:1351:ExecuteTransaction|  Btree.java:0143:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        35: begin
          if ((freeNext_9_requestedAt < freeNext_9_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1359:<init>|  Chip.java:1358:waitResultOfTransaction|  Btree.java:0144:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        36: begin
          main_next_131 <= freeNext_freeNext_9_result_0;
          main_freeNext_10_index_116 <= main_root_132;
          main_freeNext_10_value_117 <= main_index2_114;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_isFree_133 <= 1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Btree.java:0147:<init>|  Btree.java:0146:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        37: begin
          main_stuckIsFree_11_index_118 <= main_index2_114;
          main_stuckIsFree_11_value_119 <= main_isFree_133;
          stuckIsFree_11_requestedAt <= step;
          stuckIsFree_11_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0159:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        38: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0160:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        39: begin
          main_freeNext_10_index_116 <= main_index2_114;
          main_freeNext_10_value_117 <= main_next_131;
          freeNext_10_requestedAt <= step;
          freeNext_10_finishedAt <= -1;
          main_pc <= main_pc + 1;
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:0514:<init>|  Chip.java:1415:<init>|  Chip.java:1414:ExecuteTransaction|  Btree.java:0161:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        40: begin
          if ((freeNext_10_requestedAt < freeNext_10_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0162:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        41: begin
          if ((stuckIsFree_11_requestedAt < stuckIsFree_11_finishedAt)) begin
            main_pc <= main_pc + 1;
          end
          begin
            integer f;
            f = $fopen("verilog/trace_verilog.txt", "a");
            $fdisplay(f, "Location: Chip.java:1422:<init>|  Chip.java:1421:waitResultOfTransaction|  Btree.java:0163:free|  Btree.java:4678:test_allocate|  Btree.java:7293:newTests|  Btree.java:7298:main|");
            $fclose(f);
          end
        end
        default: main_stop <= 1;
      endcase
    end
  end
  // Process: stuckIsLeaf  process_stuckIsLeaf_0001
  (* ram_style = "block" *)
  reg [1-1:0] stuckIsLeaf_memory[4*1];
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
      for(stuckIsLeaf_memory_index = 1; stuckIsLeaf_memory_index < 4; stuckIsLeaf_memory_index = stuckIsLeaf_memory_index + 1) stuckIsLeaf_memory[stuckIsLeaf_memory_index] <= 0;
    end
    else if (processCurrent == 1) begin
      case(stuckIsLeaf_pc)
        0: begin
          if ((stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step)) begin
            stuckIsLeaf_stuckIsLeaf_7_result_0 <= stuckIsLeaf_memory[main_stuckIsLeaf_7_index_27*1+0];
            stuckIsLeaf_7_finishedAt <= step;
          end
          else if ((stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step)) begin
            stuckIsLeaf_memory[main_stuckIsLeaf_8_index_28*1+0] <= main_stuckIsLeaf_8_value_29;
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
      for(stuckIsFree_memory_index = 0; stuckIsFree_memory_index < 1; stuckIsFree_memory_index = stuckIsFree_memory_index + 1) stuckIsFree_memory[stuckIsFree_memory_index] <= 0;
      stuckIsFree_memory[1] <= 1;
      stuckIsFree_memory[2] <= 1;
      stuckIsFree_memory[3] <= 1;
    end
    else if (processCurrent == 2) begin
      case(stuckIsFree_pc)
        0: begin
          if ((stuckIsFree_11_requestedAt > stuckIsFree_11_finishedAt && stuckIsFree_11_requestedAt != step)) begin
            stuckIsFree_memory[main_stuckIsFree_11_index_118*1+0] <= main_stuckIsFree_11_value_119;
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
  reg [3-1:0] freeNext_freeNext_9_result_0;
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
      freeNext_freeNext_9_result_0 <= 0;
      freeNext_9_finishedAt <= -1;
      freeNext_freeNext_9_returnCode <= 0;
      freeNext_10_finishedAt <= -1;
      freeNext_freeNext_10_returnCode <= 0;
      freeNext_memory[0] <= 1;
      freeNext_memory[1] <= 2;
      freeNext_memory[2] <= 3;
      for(freeNext_memory_index = 3; freeNext_memory_index < 4; freeNext_memory_index = freeNext_memory_index + 1) freeNext_memory[freeNext_memory_index] <= 0;
    end
    else if (processCurrent == 3) begin
      case(freeNext_pc)
        0: begin
          if ((freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step)) begin
            freeNext_freeNext_9_result_0 <= freeNext_memory[main_freeNext_9_index_115*1+0];
            freeNext_9_finishedAt <= step;
          end
          else if ((freeNext_10_requestedAt > freeNext_10_finishedAt && freeNext_10_requestedAt != step)) begin
            freeNext_memory[main_freeNext_10_index_116*1+0] <= main_freeNext_10_value_117;
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
      for(stuckSize_memory_index = 0; stuckSize_memory_index < 4; stuckSize_memory_index = stuckSize_memory_index + 1) stuckSize_memory[stuckSize_memory_index] <= 0;
    end
    else if (processCurrent == 4) begin
      case(stuckSize_pc)
        0: begin
          if ((stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step)) begin
            stuckSize_stuckSize_5_result_0 <= stuckSize_memory[main_stuckSize_5_index_24*1+0];
            stuckSize_5_finishedAt <= step;
          end
          else if ((stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step)) begin
            stuckSize_memory[main_stuckSize_6_index_25*1+0] <= main_stuckSize_6_value_26;
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
      for(stuckKeys_memory_index = 0; stuckKeys_memory_index < 16; stuckKeys_memory_index = stuckKeys_memory_index + 1) stuckKeys_memory[stuckKeys_memory_index] <= 0;
    end
    else if (processCurrent == 5) begin
      case(stuckKeys_pc)
        0: begin
          if ((stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step)) begin
            stuckKeys_stuckKeys_1_result_0 <= stuckKeys_memory[main_stuckKeys_1_index_12*4+0];
            stuckKeys_stuckKeys_1_result_1 <= stuckKeys_memory[main_stuckKeys_1_index_12*4+1];
            stuckKeys_stuckKeys_1_result_2 <= stuckKeys_memory[main_stuckKeys_1_index_12*4+2];
            stuckKeys_stuckKeys_1_result_3 <= stuckKeys_memory[main_stuckKeys_1_index_12*4+3];
            stuckKeys_1_finishedAt <= step;
          end
          else if ((stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step)) begin
            stuckKeys_memory[main_stuckKeys_2_index_13*4+0] <= main_stuckKeys_2_value_14;
            stuckKeys_memory[main_stuckKeys_2_index_13*4+1] <= main_stuckKeys_2_value_15;
            stuckKeys_memory[main_stuckKeys_2_index_13*4+2] <= main_stuckKeys_2_value_16;
            stuckKeys_memory[main_stuckKeys_2_index_13*4+3] <= main_stuckKeys_2_value_17;
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
      for(stuckData_memory_index = 0; stuckData_memory_index < 16; stuckData_memory_index = stuckData_memory_index + 1) stuckData_memory[stuckData_memory_index] <= 0;
    end
    else if (processCurrent == 6) begin
      case(stuckData_pc)
        0: begin
          if ((stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step)) begin
            stuckData_stuckData_3_result_0 <= stuckData_memory[main_stuckData_3_index_18*4+0];
            stuckData_stuckData_3_result_1 <= stuckData_memory[main_stuckData_3_index_18*4+1];
            stuckData_stuckData_3_result_2 <= stuckData_memory[main_stuckData_3_index_18*4+2];
            stuckData_stuckData_3_result_3 <= stuckData_memory[main_stuckData_3_index_18*4+3];
            stuckData_3_finishedAt <= step;
          end
          else if ((stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step)) begin
            stuckData_memory[main_stuckData_4_index_19*4+0] <= main_stuckData_4_value_20;
            stuckData_memory[main_stuckData_4_index_19*4+1] <= main_stuckData_4_value_21;
            stuckData_memory[main_stuckData_4_index_19*4+2] <= main_stuckData_4_value_22;
            stuckData_memory[main_stuckData_4_index_19*4+3] <= main_stuckData_4_value_23;
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
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index_0", main_index_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_size_1", main_size_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_2", main_isLeaf_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_nextFree_3", main_nextFree_3);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_0_4", main_Key_0_4);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_0_5", main_Data_0_5);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_1_6", main_Key_1_6);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_1_7", main_Data_1_7);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_2_8", main_Key_2_8);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_2_9", main_Data_2_9);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_3_10", main_Key_3_10);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_3_11", main_Data_3_11);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_1_index_12", main_stuckKeys_1_index_12);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_2_index_13", main_stuckKeys_2_index_13);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_2_value_14", main_stuckKeys_2_value_14);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_2_value_15", main_stuckKeys_2_value_15);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_2_value_16", main_stuckKeys_2_value_16);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckKeys_2_value_17", main_stuckKeys_2_value_17);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_3_index_18", main_stuckData_3_index_18);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_4_index_19", main_stuckData_4_index_19);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_4_value_20", main_stuckData_4_value_20);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_4_value_21", main_stuckData_4_value_21);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_4_value_22", main_stuckData_4_value_22);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckData_4_value_23", main_stuckData_4_value_23);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckSize_5_index_24", main_stuckSize_5_index_24);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckSize_6_index_25", main_stuckSize_6_index_25);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckSize_6_value_26", main_stuckSize_6_value_26);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckIsLeaf_7_index_27", main_stuckIsLeaf_7_index_27);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckIsLeaf_8_index_28", main_stuckIsLeaf_8_index_28);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckIsLeaf_8_value_29", main_stuckIsLeaf_8_value_29);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Found_30", main_Found_30);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_31", main_Key_31);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_FoundKey_32", main_FoundKey_32);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_33", main_Data_33);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_BtreeIndex_34", main_BtreeIndex_34);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_StuckIndex_35", main_StuckIndex_35);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_MergeSuccess_36", main_MergeSuccess_36);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index_37", main_index_37);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_size_38", main_size_38);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_39", main_isLeaf_39);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_nextFree_40", main_nextFree_40);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_0_41", main_Key_0_41);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_0_42", main_Data_0_42);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_1_43", main_Key_1_43);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_1_44", main_Data_1_44);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_2_45", main_Key_2_45);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_2_46", main_Data_2_46);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_3_47", main_Key_3_47);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_3_48", main_Data_3_48);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Found_49", main_Found_49);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_50", main_Key_50);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_FoundKey_51", main_FoundKey_51);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_52", main_Data_52);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_BtreeIndex_53", main_BtreeIndex_53);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_StuckIndex_54", main_StuckIndex_54);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_MergeSuccess_55", main_MergeSuccess_55);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index_56", main_index_56);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_size_57", main_size_57);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_58", main_isLeaf_58);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_nextFree_59", main_nextFree_59);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_0_60", main_Key_0_60);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_0_61", main_Data_0_61);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_1_62", main_Key_1_62);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_1_63", main_Data_1_63);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_2_64", main_Key_2_64);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_2_65", main_Data_2_65);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_3_66", main_Key_3_66);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_3_67", main_Data_3_67);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Found_68", main_Found_68);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_69", main_Key_69);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_FoundKey_70", main_FoundKey_70);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_71", main_Data_71);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_BtreeIndex_72", main_BtreeIndex_72);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_StuckIndex_73", main_StuckIndex_73);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_MergeSuccess_74", main_MergeSuccess_74);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index_75", main_index_75);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_size_76", main_size_76);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_77", main_isLeaf_77);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_nextFree_78", main_nextFree_78);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_0_79", main_Key_0_79);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_0_80", main_Data_0_80);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_1_81", main_Key_1_81);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_1_82", main_Data_1_82);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_2_83", main_Key_2_83);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_2_84", main_Data_2_84);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_3_85", main_Key_3_85);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_3_86", main_Data_3_86);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Found_87", main_Found_87);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_88", main_Key_88);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_FoundKey_89", main_FoundKey_89);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_90", main_Data_90);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_BtreeIndex_91", main_BtreeIndex_91);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_StuckIndex_92", main_StuckIndex_92);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_MergeSuccess_93", main_MergeSuccess_93);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index_94", main_index_94);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_size_95", main_size_95);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_96", main_isLeaf_96);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_nextFree_97", main_nextFree_97);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_0_98", main_Key_0_98);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_0_99", main_Data_0_99);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_1_100", main_Key_1_100);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_1_101", main_Data_1_101);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_2_102", main_Key_2_102);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_2_103", main_Data_2_103);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_3_104", main_Key_3_104);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_3_105", main_Data_3_105);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Found_106", main_Found_106);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Key_107", main_Key_107);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_FoundKey_108", main_FoundKey_108);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_Data_109", main_Data_109);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_BtreeIndex_110", main_BtreeIndex_110);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_StuckIndex_111", main_StuckIndex_111);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_MergeSuccess_112", main_MergeSuccess_112);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index1_113", main_index1_113);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_index2_114", main_index2_114);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_freeNext_9_index_115", main_freeNext_9_index_115);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_freeNext_10_index_116", main_freeNext_10_index_116);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_freeNext_10_value_117", main_freeNext_10_value_117);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckIsFree_11_index_118", main_stuckIsFree_11_index_118);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_stuckIsFree_11_value_119", main_stuckIsFree_11_value_119);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_root_120", main_root_120);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_next_121", main_next_121);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_122", main_isLeaf_122);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isFree_123", main_isFree_123);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_root_124", main_root_124);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_next_125", main_next_125);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isLeaf_126", main_isLeaf_126);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isFree_127", main_isFree_127);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_next_128", main_next_128);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_root_129", main_root_129);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isFree_130", main_isFree_130);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_next_131", main_next_131);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_root_132", main_root_132);
      $fwrite(o, "        Register: %-32s = %1d\n",  "main_isFree_133", main_isFree_133);
      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 1, "stuckIsLeaf", 1, stuckIsLeaf_pc, stuckIsLeaf_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 1, 1);
      $fwrite(o, "        %2d", stuckIsLeaf_memory[0]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[1]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[2]);
      $fwrite(o, ", %2d", stuckIsLeaf_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckIsLeaf_7", stuckIsLeaf_7_requestedAt, stuckIsLeaf_7_finishedAt, stuckIsLeaf_stuckIsLeaf_7_returnCode, (stuckIsLeaf_7_requestedAt > stuckIsLeaf_7_finishedAt && stuckIsLeaf_7_requestedAt != step), (stuckIsLeaf_7_requestedAt < stuckIsLeaf_7_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckIsLeaf_7_index_27", main_stuckIsLeaf_7_index_27);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckIsLeaf_stuckIsLeaf_7_result_0", stuckIsLeaf_stuckIsLeaf_7_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckIsLeaf_8", stuckIsLeaf_8_requestedAt, stuckIsLeaf_8_finishedAt, stuckIsLeaf_stuckIsLeaf_8_returnCode, (stuckIsLeaf_8_requestedAt > stuckIsLeaf_8_finishedAt && stuckIsLeaf_8_requestedAt != step), (stuckIsLeaf_8_requestedAt < stuckIsLeaf_8_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckIsLeaf_8_index_28", main_stuckIsLeaf_8_index_28);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckIsLeaf_8_value_29", main_stuckIsLeaf_8_value_29);

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
      $fwrite(o, "            %-38s = %1d\n", "main_stuckIsFree_11_index_118", main_stuckIsFree_11_index_118);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckIsFree_11_value_119", main_stuckIsFree_11_value_119);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 3, "freeNext", 1, freeNext_pc, freeNext_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", freeNext_memory[0]);
      $fwrite(o, ", %2d", freeNext_memory[1]);
      $fwrite(o, ", %2d", freeNext_memory[2]);
      $fwrite(o, ", %2d", freeNext_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "freeNext_freeNext_9_result_0", freeNext_freeNext_9_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "freeNext_9", freeNext_9_requestedAt, freeNext_9_finishedAt, freeNext_freeNext_9_returnCode, (freeNext_9_requestedAt > freeNext_9_finishedAt && freeNext_9_requestedAt != step), (freeNext_9_requestedAt < freeNext_9_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_freeNext_9_index_115", main_freeNext_9_index_115);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "freeNext_freeNext_9_result_0", freeNext_freeNext_9_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "freeNext_10", freeNext_10_requestedAt, freeNext_10_finishedAt, freeNext_freeNext_10_returnCode, (freeNext_10_requestedAt > freeNext_10_finishedAt && freeNext_10_requestedAt != step), (freeNext_10_requestedAt < freeNext_10_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_freeNext_10_index_116", main_freeNext_10_index_116);

      $fwrite(o, "            %-38s = %1d\n", "main_freeNext_10_value_117", main_freeNext_10_value_117);

      $fwrite(o, "    Process: %1d - %-21s instructions: %1d, pc: %1d, rc: %1d\n", 4, "stuckSize", 1, stuckSize_pc, stuckSize_returnCode);
      $fwrite(o, "      Memory: size: %1d, width: %1d, block: %1d\n", 4, 3, 1);
      $fwrite(o, "        %2d", stuckSize_memory[0]);
      $fwrite(o, ", %2d", stuckSize_memory[1]);
      $fwrite(o, ", %2d", stuckSize_memory[2]);
      $fwrite(o, ", %2d", stuckSize_memory[3]);
      $fwrite(o, "\n");
      $fwrite(o, "      Registers :\n");
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckSize_5", stuckSize_5_requestedAt, stuckSize_5_finishedAt, stuckSize_stuckSize_5_returnCode, (stuckSize_5_requestedAt > stuckSize_5_finishedAt && stuckSize_5_requestedAt != step), (stuckSize_5_requestedAt < stuckSize_5_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckSize_5_index_24", main_stuckSize_5_index_24);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckSize_stuckSize_5_result_0", stuckSize_stuckSize_5_result_0);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckSize_6", stuckSize_6_requestedAt, stuckSize_6_finishedAt, stuckSize_stuckSize_6_returnCode, (stuckSize_6_requestedAt > stuckSize_6_finishedAt && stuckSize_6_requestedAt != step), (stuckSize_6_requestedAt < stuckSize_6_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckSize_6_index_25", main_stuckSize_6_index_25);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckSize_6_value_26", main_stuckSize_6_value_26);

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
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckKeys_1", stuckKeys_1_requestedAt, stuckKeys_1_finishedAt, stuckKeys_stuckKeys_1_returnCode, (stuckKeys_1_requestedAt > stuckKeys_1_finishedAt && stuckKeys_1_requestedAt != step), (stuckKeys_1_requestedAt < stuckKeys_1_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_1_index_12", main_stuckKeys_1_index_12);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_0", stuckKeys_stuckKeys_1_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_1", stuckKeys_stuckKeys_1_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_2", stuckKeys_stuckKeys_1_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckKeys_stuckKeys_1_result_3", stuckKeys_stuckKeys_1_result_3);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckKeys_2", stuckKeys_2_requestedAt, stuckKeys_2_finishedAt, stuckKeys_stuckKeys_2_returnCode, (stuckKeys_2_requestedAt > stuckKeys_2_finishedAt && stuckKeys_2_requestedAt != step), (stuckKeys_2_requestedAt < stuckKeys_2_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_2_index_13", main_stuckKeys_2_index_13);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_2_value_14", main_stuckKeys_2_value_14);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_2_value_15", main_stuckKeys_2_value_15);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_2_value_16", main_stuckKeys_2_value_16);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckKeys_2_value_17", main_stuckKeys_2_value_17);

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
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);
      $fwrite(o, "        Register: %-32s = %1d\n",  "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);
      $fwrite(o, "      Transactions:\n");
      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "get", "stuckData_3", stuckData_3_requestedAt, stuckData_3_finishedAt, stuckData_stuckData_3_returnCode, (stuckData_3_requestedAt > stuckData_3_finishedAt && stuckData_3_requestedAt != step), (stuckData_3_requestedAt < stuckData_3_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_3_index_18", main_stuckData_3_index_18);

      $fwrite(o, "          Outputs     :\n");

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_0", stuckData_stuckData_3_result_0);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_1", stuckData_stuckData_3_result_1);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_2", stuckData_stuckData_3_result_2);

      $fwrite(o, "            %-38s = %1d\n", "stuckData_stuckData_3_result_3", stuckData_stuckData_3_result_3);

      $fwrite(o, "        Transaction   : %-8s - %-16s  requested at: %1d, finished at: %1d, returnCode: %1d, executable: %1d, finished: %1d\n", "set", "stuckData_4", stuckData_4_requestedAt, stuckData_4_finishedAt, stuckData_stuckData_4_returnCode, (stuckData_4_requestedAt > stuckData_4_finishedAt && stuckData_4_requestedAt != step), (stuckData_4_requestedAt < stuckData_4_finishedAt));

      $fwrite(o, "          Inputs      :\n");
      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_4_index_19", main_stuckData_4_index_19);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_4_value_20", main_stuckData_4_value_20);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_4_value_21", main_stuckData_4_value_21);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_4_value_22", main_stuckData_4_value_22);

      $fwrite(o, "            %-38s = %1d\n", "main_stuckData_4_value_23", main_stuckData_4_value_23);

      $fclose(o);
    end
  endtask
endmodule
