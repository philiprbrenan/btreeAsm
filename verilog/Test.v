//-----------------------------------------------------------------------------
// Database on a chip synthesis
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
module Test(                                                                      // Test bench for database on a chip
  input                 clock,                                                  // Clock
  input                 reset,                                                  // Reset chip

  output wire            stop                                            // Program has stopped when this goes high
  );
  integer step;
  integer returnCode;
  assign stop = Main_stop||Memory_stop;
  always @ (posedge clock) begin
    if (reset) begin
      step <= -2;
    end
    else begin
      step <= step + 1;
    end
  end
  // process_Main_0000
  // process_Memory_0001
  // Process: Main  process_Main_0000
  reg [16-1:0] Main_a_0;
  reg [16-1:0] Main_b_1;
  reg [16-1:0] Main_c_2;
  reg [16-1:0] Main_i_3;
  reg [4-1:0] Main_Memory_1_index_4;
  reg [16-1:0] Main_Memory_1_value_5;
  integer Main_pc;
  integer Main_stop;
  integer Main_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      Main_pc <= 0;
      Main_stop <= 0;
      Main_returnCode <= 0;
      Main_a_0 <= 0;
      Main_b_1 <= 0;
      Main_c_2 <= 0;
      Main_i_3 <= 0;
      Main_Memory_1_index_4 <= 0;
      Main_Memory_1_value_5 <= 0;
      Memory_1_requestedAt <= -1;
    end
    else begin                                   // Run the process in full parallel
      case(Main_pc)
        0: begin
          Main_a_0 <= 0;
          Main_b_1 <= 1;
          Main_i_3 <= 0;
          Main_pc <= Main_pc + 1;
        end
        1: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        2: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        3: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        4: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        5: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        6: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        7: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        8: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        9: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        10: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        11: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        12: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        13: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        14: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        15: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        16: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        17: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        18: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        19: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        20: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        21: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        22: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        23: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        24: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        25: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        26: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        27: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        28: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        29: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        30: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        31: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        32: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        33: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        34: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        35: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        36: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        37: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        38: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        39: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        40: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        41: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        42: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        43: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        44: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        45: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        46: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        47: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        48: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        49: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        50: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        51: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        52: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        53: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        54: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        55: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        56: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        57: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        58: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        59: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        60: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        61: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        62: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        63: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        64: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        65: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        66: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        67: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        68: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        69: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        70: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        71: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        72: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        73: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        74: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        75: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        76: begin
          Main_c_2 <= Main_a_0;
          Main_pc <= Main_pc + 1;
        end
        77: begin
          Main_c_2 <= Main_c_2 + Main_b_1;
          Main_a_0 <= Main_b_1;
          Main_pc <= Main_pc + 1;
        end
        78: begin
          Main_b_1 <= Main_c_2;
          Main_Memory_1_index_4 <= Main_i_3;
          Main_Memory_1_value_5 <= Main_c_2;
          Memory_1_requestedAt <= step;
          Main_pc <= Main_pc + 1;
        end
        79: begin
          Main_i_3 <= Main_i_3+1;
          Main_pc <= Main_pc + 1;
        end
        80: begin
          if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
            Main_pc <= Main_pc + 1;
          end
        end
        81: begin
          Main_returnCode <= 1;
          Main_stop <= 1;
          Main_pc <= Main_pc + 1;
        end
        default: Main_stop <= 1;
      endcase
    end
  end
  // Process: Memory  process_Memory_0001
  (* ram_style = "block" *)
  reg [16-1:0] Memory_memory[16*1];
  integer Memory_1_requestedAt;
  integer Memory_1_finishedAt;
  integer Memory_Memory_1_returnCode;
  integer Memory_pc;
  integer Memory_stop;
  integer Memory_returnCode;
  always @ (posedge clock) begin
    if (step < 0) begin
      Memory_pc <= 0;
      Memory_stop <= 0;
      Memory_returnCode <= 0;
      Memory_1_finishedAt <= -1;
      Memory_Memory_1_returnCode <= 0;
      Memory_memory[0] <= 0;
      Memory_memory[1] <= 0;
      Memory_memory[2] <= 0;
      Memory_memory[3] <= 0;
      Memory_memory[4] <= 0;
      Memory_memory[5] <= 0;
      Memory_memory[6] <= 0;
      Memory_memory[7] <= 0;
      Memory_memory[8] <= 0;
      Memory_memory[9] <= 0;
      Memory_memory[10] <= 0;
      Memory_memory[11] <= 0;
      Memory_memory[12] <= 0;
      Memory_memory[13] <= 0;
      Memory_memory[14] <= 0;
      Memory_memory[15] <= 0;
    end
    else begin                                   // Run the process in full parallel
      case(Memory_pc)
        0: begin
          if ((Memory_1_requestedAt > Memory_1_finishedAt && Memory_1_requestedAt != step)) begin
            Memory_memory[Main_Memory_1_index_4*1+0] <= Main_Memory_1_value_5;
            Memory_1_finishedAt <= step;
          end
          else begin
          end
        end
        default: Memory_stop <= 1;
      endcase
    end
  end
endmodule
