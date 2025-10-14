# docker run --rm  -v "/home/phil/btreeAsm/:/workspace" ghcr.io/philiprbrenan/sc-asic:latest /bin/bash
docker run --rm -v "/home/phil/btreeAsm/verilog:/workspace/verilog" ghcr.io/philiprbrenan/sc-asic:latest bash -c "bash /workspace/verilog/Test.exec"
cp "verilog/build/$DESIGN/job0/$DESIGN.pkg.json" "results/Test/Test.json"                                # Copy results out of docker to remote system
cp "verilog/build/$DESIGN/job0/$DESIGN.png"      "results/Test/Test.png"
cp "verilog/build/$DESIGN/job0/job.log"          "results/Test/Test.log"
