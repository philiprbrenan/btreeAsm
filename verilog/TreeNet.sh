# docker run --rm  -v "/home/phil/btreeAsm/:/workspace" ghcr.io/philiprbrenan/sc-asic:latest /bin/bash
docker run --rm -v "/home/phil/btreeAsm/verilog:/workspace/verilog" ghcr.io/philiprbrenan/sc-asic:latest bash -c "bash /workspace/verilog/TreeNet.exec"
cp "/home/phil/btreeAsm/verilog/build/TreeNet/job0/write.gds/0/outputs/TreeNet.pkg.json" "results/treeNet_15_8_24/TreeNet.json"
cp "/home/phil/btreeAsm/verilog/build/TreeNet/job0/write.gds/0/outputs/TreeNet.gds" "results/treeNet_15_8_24/TreeNet.gds"
cp "/home/phil/btreeAsm/verilog/build/TreeNet/job0/write.gds/0/outputs/TreeNet.png" "results/treeNet_15_8_24/TreeNet.png"
cp "/home/phil/btreeAsm/verilog/build/TreeNet/job0/job.log" "results/treeNet_15_8_24/TreeNet.log"
