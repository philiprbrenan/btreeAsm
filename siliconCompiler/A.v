
module A(
    input  wire       clk,
    input  wire[31:0] a,
    input  wire[31:0] b,
    output reg [31:0] y
);

  always @ (posedge clk) begin
    y = a & b; // simple AND gate
  end
endmodule
