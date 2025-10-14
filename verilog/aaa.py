#!/usr/bin/env python3
import os, sys

from siliconcompiler import Design, ASIC
from siliconcompiler.targets import skywater130_demo
from siliconcompiler.flows import asicflow

src = "Test.v"
design = Design("Test")

design.set_dataroot("Test", __file__)
design.add_file(src, fileset='verilog')
design.set_topmodule("Test", fileset='verilog')
#design.add_file("heartbeat.sdc", dataroot="heartbeat", fileset="sdc")

project = ASIC(design)
project.add_fileset(['verilog'])
skywater130_demo(project)
project.set('asic', 'core_area', '0 0 100 100')  # xmin ymin xmax ymax
project.clock('clk', period=100)

project.run()
project.summary()
