#!/usr/bin/env python3

from siliconcompiler import Design, ASIC
from siliconcompiler.targets import skywater130_demo
from siliconcompiler.flows import asicflow

D = "Test"
design = Design(D)

design.set_dataroot(D, __file__)
design.add_file(f"{D}.v", fileset='verilog')
design.set_topmodule(D, fileset='verilog')

project = ASIC(design)
project.add_fileset(['verilog'])
skywater130_demo(project)

project.run()
project.summary()
