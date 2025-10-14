# docker run --rm  -v "/home/phil/btreeAsm/:/workspace" ghcr.io/philiprbrenan/sc-asic:latest /bin/bash
docker run --rm -v "/home/phil/btreeAsm/verilog:/workspace/verilog" ghcr.io/philiprbrenan/sc-asic:latest bash -c "bash /workspace/verilog/Test.exec"
cp "verilog/build/Test/job0/$DESIGN.pkg.json" "results/Test/Test.json"
cp "verilog/build/Test/job0/$DESIGN.png"      "results/Test/Test.png"
cp "verilog/build/Test/job0/job.log"          "results/Test/Test.log"
