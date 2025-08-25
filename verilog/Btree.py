#!/usr/bin/env python3

from siliconcompiler import Chip                                                # import python package
from siliconcompiler.targets import freepdk45_demo

if __name__ == "__main__":
  chip = Chip('Btree')                                                             # Create chip object.  The name is used to create the summary and mask image file
# chip.set('option', 'loglevel', 'warning')                                     # Warnings and above
# chip.set('option', 'loglevel', 'error')                                       # Warnings and above
  chip.set('option', 'builddir', 'verilog/build')                                    # Build folder
  chip.input('verilog/Btree.v')                                                              # Source code
# chip.input('/home/azureuser/btreeBlock/verilog/$project/$instance/siliconCompiler/memory.v'   ) # Memory black box
  chip.input('verilog/Btree.sdc')                                                              # Synopsys design constraints file
  chip.set('design', 'Btree')                                                      # Show the top most module
  chip.use(freepdk45_demo)                                                      # Load predefined technology and flow target
# chip.set('package', 'description', '$designDescription')                      # Description of design
  chip.clock('clock', period=10)                                                # Define clock speed of design was 100
  chip.set('option', 'remote', False)                                           # Run remote in the cloud
  chip.set('option', 'nodisplay', True)                                         # Do not open displays
# chip.set('constraint', 'density', $density)                                   # Lowering the density gives more area in which to route connections at the cost of wasting surface area and making the chip run slower. For find it seems best to leave this parameter alone
  chip.set('option', 'clean', True)                                             # Clean start else it reuses previous results
  chip.run()                                                                    # Run compilation of design and target
  chip.summary()                                                                # Create a summary - but at the moment it is only printed on stdout so for automation you have to get the same information from the summary pkg.json
  chip.snapshot()                                                               # Create the charming image of the chip along with its size, power, clock frequency
#  chip.show()
#  chip.set('option', 'define', 'CFG_ASIC=1')
#  chip.set('option', 'env', 'PDK_HOME', '/disk/mypdk')
#  chip.set('option', 'idir', './mylib')
#  chip.set('option', 'nodisplay', True)
