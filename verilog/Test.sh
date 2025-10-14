# docker run --rm  -v "/home/phil/btreeAsm/:/workspace" ghcr.io/philiprbrenan/sc-asic:latest /bin/bash
docker run --rm   -v "~/btreeAsm/verilog:/workspace/verilog" -v "~/btreeAsm/verilog/Test.exec:/workspace/verilog/Test.exec" ghcr.io/philiprbrenan/sc-asic:latest
bash -c verilog
cp "results/Test/Test.json/build/$DESIGN/job0/$DESIGN.pkg.json" "verilog"                                # Copy results out of docker to remote system
cp "results/Test/Test.png/build/$DESIGN/job0/$DESIGN.png"      "verilog"
cp "results/Test/Test.log/build/$DESIGN/job0/job.log"          "*"
