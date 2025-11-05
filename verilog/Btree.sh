# docker run --rm  -v "/home/phil/btreeAsm/:/workspace" ghcr.io/philiprbrenan/sc-asic:latest /bin/bash
docker run --rm -v "/home/phil/btreeAsm/verilog:/workspace/verilog" ghcr.io/philiprbrenan/sc-asic:latest bash -c "bash /workspace/verilog/Btree.exec"
cp "/home/phil/btreeAsm/verilog/build/Btree/job0/write.gds/0/outputs/Btree.pkg.json" "results/Btree_1024_16_32_32/Btree.json"
cp "/home/phil/btreeAsm/verilog/build/Btree/job0/write.gds/0/outputs/Btree.gds" "results/Btree_1024_16_32_32/Btree.gds"
cp "/home/phil/btreeAsm/verilog/build/Btree/job0/write.gds/0/outputs/Btree.png" "results/Btree_1024_16_32_32/Btree.png"
cp "/home/phil/btreeAsm/verilog/build/Btree/job0/job.log" "results/Btree_1024_16_32_32/Btree.log"
