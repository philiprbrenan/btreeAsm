###############################################################################
# Created by write_sdc
# Wed Oct 15 22:04:14 2025
###############################################################################
current_design A
###############################################################################
# Timing Constraints
###############################################################################
group_path -name reg2reg\
    -from [list [get_cells {y[0]$_DFF_P_}]\
           [get_cells {y[10]$_DFF_P_}]\
           [get_cells {y[11]$_DFF_P_}]\
           [get_cells {y[12]$_DFF_P_}]\
           [get_cells {y[13]$_DFF_P_}]\
           [get_cells {y[14]$_DFF_P_}]\
           [get_cells {y[15]$_DFF_P_}]\
           [get_cells {y[16]$_DFF_P_}]\
           [get_cells {y[17]$_DFF_P_}]\
           [get_cells {y[18]$_DFF_P_}]\
           [get_cells {y[19]$_DFF_P_}]\
           [get_cells {y[1]$_DFF_P_}]\
           [get_cells {y[20]$_DFF_P_}]\
           [get_cells {y[21]$_DFF_P_}]\
           [get_cells {y[22]$_DFF_P_}]\
           [get_cells {y[23]$_DFF_P_}]\
           [get_cells {y[24]$_DFF_P_}]\
           [get_cells {y[25]$_DFF_P_}]\
           [get_cells {y[26]$_DFF_P_}]\
           [get_cells {y[27]$_DFF_P_}]\
           [get_cells {y[28]$_DFF_P_}]\
           [get_cells {y[29]$_DFF_P_}]\
           [get_cells {y[2]$_DFF_P_}]\
           [get_cells {y[30]$_DFF_P_}]\
           [get_cells {y[31]$_DFF_P_}]\
           [get_cells {y[3]$_DFF_P_}]\
           [get_cells {y[4]$_DFF_P_}]\
           [get_cells {y[5]$_DFF_P_}]\
           [get_cells {y[6]$_DFF_P_}]\
           [get_cells {y[7]$_DFF_P_}]\
           [get_cells {y[8]$_DFF_P_}]\
           [get_cells {y[9]$_DFF_P_}]]\
    -to [list [get_cells {y[0]$_DFF_P_}]\
           [get_cells {y[10]$_DFF_P_}]\
           [get_cells {y[11]$_DFF_P_}]\
           [get_cells {y[12]$_DFF_P_}]\
           [get_cells {y[13]$_DFF_P_}]\
           [get_cells {y[14]$_DFF_P_}]\
           [get_cells {y[15]$_DFF_P_}]\
           [get_cells {y[16]$_DFF_P_}]\
           [get_cells {y[17]$_DFF_P_}]\
           [get_cells {y[18]$_DFF_P_}]\
           [get_cells {y[19]$_DFF_P_}]\
           [get_cells {y[1]$_DFF_P_}]\
           [get_cells {y[20]$_DFF_P_}]\
           [get_cells {y[21]$_DFF_P_}]\
           [get_cells {y[22]$_DFF_P_}]\
           [get_cells {y[23]$_DFF_P_}]\
           [get_cells {y[24]$_DFF_P_}]\
           [get_cells {y[25]$_DFF_P_}]\
           [get_cells {y[26]$_DFF_P_}]\
           [get_cells {y[27]$_DFF_P_}]\
           [get_cells {y[28]$_DFF_P_}]\
           [get_cells {y[29]$_DFF_P_}]\
           [get_cells {y[2]$_DFF_P_}]\
           [get_cells {y[30]$_DFF_P_}]\
           [get_cells {y[31]$_DFF_P_}]\
           [get_cells {y[3]$_DFF_P_}]\
           [get_cells {y[4]$_DFF_P_}]\
           [get_cells {y[5]$_DFF_P_}]\
           [get_cells {y[6]$_DFF_P_}]\
           [get_cells {y[7]$_DFF_P_}]\
           [get_cells {y[8]$_DFF_P_}]\
           [get_cells {y[9]$_DFF_P_}]]
group_path -name reg2out\
    -from [list [get_cells {y[0]$_DFF_P_}]\
           [get_cells {y[10]$_DFF_P_}]\
           [get_cells {y[11]$_DFF_P_}]\
           [get_cells {y[12]$_DFF_P_}]\
           [get_cells {y[13]$_DFF_P_}]\
           [get_cells {y[14]$_DFF_P_}]\
           [get_cells {y[15]$_DFF_P_}]\
           [get_cells {y[16]$_DFF_P_}]\
           [get_cells {y[17]$_DFF_P_}]\
           [get_cells {y[18]$_DFF_P_}]\
           [get_cells {y[19]$_DFF_P_}]\
           [get_cells {y[1]$_DFF_P_}]\
           [get_cells {y[20]$_DFF_P_}]\
           [get_cells {y[21]$_DFF_P_}]\
           [get_cells {y[22]$_DFF_P_}]\
           [get_cells {y[23]$_DFF_P_}]\
           [get_cells {y[24]$_DFF_P_}]\
           [get_cells {y[25]$_DFF_P_}]\
           [get_cells {y[26]$_DFF_P_}]\
           [get_cells {y[27]$_DFF_P_}]\
           [get_cells {y[28]$_DFF_P_}]\
           [get_cells {y[29]$_DFF_P_}]\
           [get_cells {y[2]$_DFF_P_}]\
           [get_cells {y[30]$_DFF_P_}]\
           [get_cells {y[31]$_DFF_P_}]\
           [get_cells {y[3]$_DFF_P_}]\
           [get_cells {y[4]$_DFF_P_}]\
           [get_cells {y[5]$_DFF_P_}]\
           [get_cells {y[6]$_DFF_P_}]\
           [get_cells {y[7]$_DFF_P_}]\
           [get_cells {y[8]$_DFF_P_}]\
           [get_cells {y[9]$_DFF_P_}]]\
    -to [list [get_ports {y[0]}]\
           [get_ports {y[10]}]\
           [get_ports {y[11]}]\
           [get_ports {y[12]}]\
           [get_ports {y[13]}]\
           [get_ports {y[14]}]\
           [get_ports {y[15]}]\
           [get_ports {y[16]}]\
           [get_ports {y[17]}]\
           [get_ports {y[18]}]\
           [get_ports {y[19]}]\
           [get_ports {y[1]}]\
           [get_ports {y[20]}]\
           [get_ports {y[21]}]\
           [get_ports {y[22]}]\
           [get_ports {y[23]}]\
           [get_ports {y[24]}]\
           [get_ports {y[25]}]\
           [get_ports {y[26]}]\
           [get_ports {y[27]}]\
           [get_ports {y[28]}]\
           [get_ports {y[29]}]\
           [get_ports {y[2]}]\
           [get_ports {y[30]}]\
           [get_ports {y[31]}]\
           [get_ports {y[3]}]\
           [get_ports {y[4]}]\
           [get_ports {y[5]}]\
           [get_ports {y[6]}]\
           [get_ports {y[7]}]\
           [get_ports {y[8]}]\
           [get_ports {y[9]}]]
group_path -name in2reg\
    -from [list [get_ports {a[0]}]\
           [get_ports {a[10]}]\
           [get_ports {a[11]}]\
           [get_ports {a[12]}]\
           [get_ports {a[13]}]\
           [get_ports {a[14]}]\
           [get_ports {a[15]}]\
           [get_ports {a[16]}]\
           [get_ports {a[17]}]\
           [get_ports {a[18]}]\
           [get_ports {a[19]}]\
           [get_ports {a[1]}]\
           [get_ports {a[20]}]\
           [get_ports {a[21]}]\
           [get_ports {a[22]}]\
           [get_ports {a[23]}]\
           [get_ports {a[24]}]\
           [get_ports {a[25]}]\
           [get_ports {a[26]}]\
           [get_ports {a[27]}]\
           [get_ports {a[28]}]\
           [get_ports {a[29]}]\
           [get_ports {a[2]}]\
           [get_ports {a[30]}]\
           [get_ports {a[31]}]\
           [get_ports {a[3]}]\
           [get_ports {a[4]}]\
           [get_ports {a[5]}]\
           [get_ports {a[6]}]\
           [get_ports {a[7]}]\
           [get_ports {a[8]}]\
           [get_ports {a[9]}]\
           [get_ports {b[0]}]\
           [get_ports {b[10]}]\
           [get_ports {b[11]}]\
           [get_ports {b[12]}]\
           [get_ports {b[13]}]\
           [get_ports {b[14]}]\
           [get_ports {b[15]}]\
           [get_ports {b[16]}]\
           [get_ports {b[17]}]\
           [get_ports {b[18]}]\
           [get_ports {b[19]}]\
           [get_ports {b[1]}]\
           [get_ports {b[20]}]\
           [get_ports {b[21]}]\
           [get_ports {b[22]}]\
           [get_ports {b[23]}]\
           [get_ports {b[24]}]\
           [get_ports {b[25]}]\
           [get_ports {b[26]}]\
           [get_ports {b[27]}]\
           [get_ports {b[28]}]\
           [get_ports {b[29]}]\
           [get_ports {b[2]}]\
           [get_ports {b[30]}]\
           [get_ports {b[31]}]\
           [get_ports {b[3]}]\
           [get_ports {b[4]}]\
           [get_ports {b[5]}]\
           [get_ports {b[6]}]\
           [get_ports {b[7]}]\
           [get_ports {b[8]}]\
           [get_ports {b[9]}]\
           [get_ports {clk}]]\
    -to [list [get_cells {y[0]$_DFF_P_}]\
           [get_cells {y[10]$_DFF_P_}]\
           [get_cells {y[11]$_DFF_P_}]\
           [get_cells {y[12]$_DFF_P_}]\
           [get_cells {y[13]$_DFF_P_}]\
           [get_cells {y[14]$_DFF_P_}]\
           [get_cells {y[15]$_DFF_P_}]\
           [get_cells {y[16]$_DFF_P_}]\
           [get_cells {y[17]$_DFF_P_}]\
           [get_cells {y[18]$_DFF_P_}]\
           [get_cells {y[19]$_DFF_P_}]\
           [get_cells {y[1]$_DFF_P_}]\
           [get_cells {y[20]$_DFF_P_}]\
           [get_cells {y[21]$_DFF_P_}]\
           [get_cells {y[22]$_DFF_P_}]\
           [get_cells {y[23]$_DFF_P_}]\
           [get_cells {y[24]$_DFF_P_}]\
           [get_cells {y[25]$_DFF_P_}]\
           [get_cells {y[26]$_DFF_P_}]\
           [get_cells {y[27]$_DFF_P_}]\
           [get_cells {y[28]$_DFF_P_}]\
           [get_cells {y[29]$_DFF_P_}]\
           [get_cells {y[2]$_DFF_P_}]\
           [get_cells {y[30]$_DFF_P_}]\
           [get_cells {y[31]$_DFF_P_}]\
           [get_cells {y[3]$_DFF_P_}]\
           [get_cells {y[4]$_DFF_P_}]\
           [get_cells {y[5]$_DFF_P_}]\
           [get_cells {y[6]$_DFF_P_}]\
           [get_cells {y[7]$_DFF_P_}]\
           [get_cells {y[8]$_DFF_P_}]\
           [get_cells {y[9]$_DFF_P_}]]
group_path -name in2out\
    -from [list [get_ports {a[0]}]\
           [get_ports {a[10]}]\
           [get_ports {a[11]}]\
           [get_ports {a[12]}]\
           [get_ports {a[13]}]\
           [get_ports {a[14]}]\
           [get_ports {a[15]}]\
           [get_ports {a[16]}]\
           [get_ports {a[17]}]\
           [get_ports {a[18]}]\
           [get_ports {a[19]}]\
           [get_ports {a[1]}]\
           [get_ports {a[20]}]\
           [get_ports {a[21]}]\
           [get_ports {a[22]}]\
           [get_ports {a[23]}]\
           [get_ports {a[24]}]\
           [get_ports {a[25]}]\
           [get_ports {a[26]}]\
           [get_ports {a[27]}]\
           [get_ports {a[28]}]\
           [get_ports {a[29]}]\
           [get_ports {a[2]}]\
           [get_ports {a[30]}]\
           [get_ports {a[31]}]\
           [get_ports {a[3]}]\
           [get_ports {a[4]}]\
           [get_ports {a[5]}]\
           [get_ports {a[6]}]\
           [get_ports {a[7]}]\
           [get_ports {a[8]}]\
           [get_ports {a[9]}]\
           [get_ports {b[0]}]\
           [get_ports {b[10]}]\
           [get_ports {b[11]}]\
           [get_ports {b[12]}]\
           [get_ports {b[13]}]\
           [get_ports {b[14]}]\
           [get_ports {b[15]}]\
           [get_ports {b[16]}]\
           [get_ports {b[17]}]\
           [get_ports {b[18]}]\
           [get_ports {b[19]}]\
           [get_ports {b[1]}]\
           [get_ports {b[20]}]\
           [get_ports {b[21]}]\
           [get_ports {b[22]}]\
           [get_ports {b[23]}]\
           [get_ports {b[24]}]\
           [get_ports {b[25]}]\
           [get_ports {b[26]}]\
           [get_ports {b[27]}]\
           [get_ports {b[28]}]\
           [get_ports {b[29]}]\
           [get_ports {b[2]}]\
           [get_ports {b[30]}]\
           [get_ports {b[31]}]\
           [get_ports {b[3]}]\
           [get_ports {b[4]}]\
           [get_ports {b[5]}]\
           [get_ports {b[6]}]\
           [get_ports {b[7]}]\
           [get_ports {b[8]}]\
           [get_ports {b[9]}]\
           [get_ports {clk}]]\
    -to [list [get_ports {y[0]}]\
           [get_ports {y[10]}]\
           [get_ports {y[11]}]\
           [get_ports {y[12]}]\
           [get_ports {y[13]}]\
           [get_ports {y[14]}]\
           [get_ports {y[15]}]\
           [get_ports {y[16]}]\
           [get_ports {y[17]}]\
           [get_ports {y[18]}]\
           [get_ports {y[19]}]\
           [get_ports {y[1]}]\
           [get_ports {y[20]}]\
           [get_ports {y[21]}]\
           [get_ports {y[22]}]\
           [get_ports {y[23]}]\
           [get_ports {y[24]}]\
           [get_ports {y[25]}]\
           [get_ports {y[26]}]\
           [get_ports {y[27]}]\
           [get_ports {y[28]}]\
           [get_ports {y[29]}]\
           [get_ports {y[2]}]\
           [get_ports {y[30]}]\
           [get_ports {y[31]}]\
           [get_ports {y[3]}]\
           [get_ports {y[4]}]\
           [get_ports {y[5]}]\
           [get_ports {y[6]}]\
           [get_ports {y[7]}]\
           [get_ports {y[8]}]\
           [get_ports {y[9]}]]
###############################################################################
# Environment
###############################################################################
set_load -pin_load 0.0231 [get_ports {y[31]}]
set_load -pin_load 0.0231 [get_ports {y[30]}]
set_load -pin_load 0.0231 [get_ports {y[29]}]
set_load -pin_load 0.0231 [get_ports {y[28]}]
set_load -pin_load 0.0231 [get_ports {y[27]}]
set_load -pin_load 0.0231 [get_ports {y[26]}]
set_load -pin_load 0.0231 [get_ports {y[25]}]
set_load -pin_load 0.0231 [get_ports {y[24]}]
set_load -pin_load 0.0231 [get_ports {y[23]}]
set_load -pin_load 0.0231 [get_ports {y[22]}]
set_load -pin_load 0.0231 [get_ports {y[21]}]
set_load -pin_load 0.0231 [get_ports {y[20]}]
set_load -pin_load 0.0231 [get_ports {y[19]}]
set_load -pin_load 0.0231 [get_ports {y[18]}]
set_load -pin_load 0.0231 [get_ports {y[17]}]
set_load -pin_load 0.0231 [get_ports {y[16]}]
set_load -pin_load 0.0231 [get_ports {y[15]}]
set_load -pin_load 0.0231 [get_ports {y[14]}]
set_load -pin_load 0.0231 [get_ports {y[13]}]
set_load -pin_load 0.0231 [get_ports {y[12]}]
set_load -pin_load 0.0231 [get_ports {y[11]}]
set_load -pin_load 0.0231 [get_ports {y[10]}]
set_load -pin_load 0.0231 [get_ports {y[9]}]
set_load -pin_load 0.0231 [get_ports {y[8]}]
set_load -pin_load 0.0231 [get_ports {y[7]}]
set_load -pin_load 0.0231 [get_ports {y[6]}]
set_load -pin_load 0.0231 [get_ports {y[5]}]
set_load -pin_load 0.0231 [get_ports {y[4]}]
set_load -pin_load 0.0231 [get_ports {y[3]}]
set_load -pin_load 0.0231 [get_ports {y[2]}]
set_load -pin_load 0.0231 [get_ports {y[1]}]
set_load -pin_load 0.0231 [get_ports {y[0]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {clk}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[31]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[30]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[29]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[28]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[27]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[26]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[25]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[24]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[23]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[22]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[21]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[20]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[19]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[18]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[17]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[16]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[15]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[14]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[13]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[12]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[11]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[10]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[9]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[8]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[7]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[6]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[5]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[4]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[3]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[2]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[1]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {a[0]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[31]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[30]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[29]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[28]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[27]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[26]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[25]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[24]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[23]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[22]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[21]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[20]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[19]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[18]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[17]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[16]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[15]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[14]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[13]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[12]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[11]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[10]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[9]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[8]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[7]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[6]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[5]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[4]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[3]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[2]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[1]}]
set_driving_cell -lib_cell sky130_fd_sc_hd__buf_1 -pin {X} -input_transition_rise 0.0000 -input_transition_fall 0.0000 [get_ports {b[0]}]
###############################################################################
# Design Rules
###############################################################################
