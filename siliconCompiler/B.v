
module B(
    input  wire       clk,
    input  wire[31:0] a1,
    input  wire[31:0] b1,
    input  wire[31:0] a2,
    input  wire[31:0] b2,
    output reg[31:0] y1,
    output reg[31:0] y2
);
    // Instantiate module A twice as hard macros

    A u1 (.clk(clk), .a(a1), .b(b1), .y(y1));
    A u2 (.clk(clk), .a(a2), .b(b2), .y(y2));
endmodule
