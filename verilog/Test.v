//-----------------------------------------------------------------------------
// Database on a chip synthesis
// Philip R Brenan at appaapps dot com, Appa Apps Ltd Inc., 2025
//------------------------------------------------------------------------------
`timescale 10ps/1ps
`default_nettype none
module Test(                                                                      // Test bench for database on a chip
  input                 clock,                                                  // Clock
  input                 reset,                                                  // Reset chip

  output wire            stop                                            // Program has stopped when this goes high
  );
  integer step;
  integer memoryStep;
  integer memoryReset;
  integer returnCode;
  assign stop = (Main_stop != 0 ? 1 : 0);
  always @ (posedge clock) begin
    if (reset) begin
      step             <= 0;
      memoryStep       <= 0;
      memoryReset      <= 1;
    end
    else if (memoryReset > 0) begin
      memoryStep       <= memoryStep + 1;
      memoryReset      <= memoryStep < 16;
    end
    else begin
      step             <= step + 1;
    end
  end
  // process_Main_0000
  // process_Memory_0001
  // Process: Main  process_Main_0000
  reg [16-1:0] Main_a_0;
  reg [16-1:0] Main_b_1;
  reg [16-1:0] Main_c_2;
  reg [4-1:0] Main_i_3;
  reg [4-1:0] Main_Memory_1_index_4;
  reg [16-1:0] Main_Memory_1_value_5;
  integer Main_pc;
  integer Main_stop;
  integer Main_returnCode;
  integer Main_memory_index;
  reg[0-1:0] Main_memory_value;
  always @ (posedge clock) begin
    if (reset) begin
      Main_pc          <= 0;
      Main_stop        <= 0;
      Main_returnCode  <= 0;
      Main_memory_index                <= 0;
      Main_memory_value                <= 0;
      Main_a_0         <= 0;
      Main_b_1         <= 0;
      Main_c_2         <= 0;
      Main_i_3         <= 0;
      Main_Memory_1_index_4            <= 0;
      Main_Memory_1_value_5            <= 0;
      Memory_1_requestedAt             <= -1;
    end
    else if (memoryReset > 0) begin
    end
    else begin
      begin
        case(Main_pc)
          0: begin
            Main_a_0         <= 0;
            Main_b_1         <= 1;
            Main_i_3         <= 0;
            Main_pc          <= Main_pc + 1;
          end
          1, 6, 11, 16, 21, 26, 31, 36, 41, 46, 51, 56, 61, 66, 71, 76: begin
            Main_c_2         <= Main_a_0;
            Main_pc          <= Main_pc + 1;
          end
          2, 7, 12, 17, 22, 27, 32, 37, 42, 47, 52, 57, 62, 67, 72, 77: begin
            Main_c_2         <= Main_c_2 + Main_b_1;
            Main_a_0         <= Main_b_1;
            Main_pc          <= Main_pc + 1;
          end
          3, 8, 13, 18, 23, 28, 33, 38, 43, 48, 53, 58, 63, 68, 73, 78: begin
            Main_b_1         <= Main_c_2;
            Main_Memory_1_index_4            <= Main_i_3;
            Main_Memory_1_value_5            <= Main_c_2;
            Memory_1_requestedAt             <= step;
            Main_pc          <= Main_pc + 1;
          end
          4, 9, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79: begin
            Main_i_3         <= Main_i_3 + 1;
            Main_pc          <= Main_pc + 1;
          end
          5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80: begin
            if ((Memory_1_requestedAt < Memory_1_finishedAt)) begin
              Main_pc          <= Main_pc + 1;
            end
          end
          81: begin
            Main_returnCode  <= 1;
            Main_stop        <= 1;
            Main_pc          <= Main_pc + 1;
          end
          default: Main_stop <= 1;
        endcase
      end
    end
  end
  // Process: Memory  process_Memory_0001
  integer Memory_1_requestedAt;
  integer Memory_1_finishedAt;
  integer Memory_Memory_1_returnCode;
  Memory Memory
  (.clock(clock),
   .Memory_1_index_4(Main_Memory_1_index_4),
   .Memory_1_value_5(Main_Memory_1_value_5),
   .Memory_1_requestedAt(Memory_1_requestedAt),
   .Memory_1_finishedAt(Memory_1_finishedAt),
   .Memory_Memory_1_returnCode(Memory_Memory_1_returnCode),
   .reset(reset));
endmodule
module Memory(
  input clock,
  input wire[4-1:0] Memory_1_index_4,
  input wire[16-1:0] Memory_1_value_5,
  input wire [32-1:0] Memory_1_requestedAt,
  output reg [32-1:0] Memory_1_finishedAt,
  output reg [32-1:0] Memory_Memory_1_returnCode,
  input reset);
endmodule
