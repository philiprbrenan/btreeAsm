git pull
docker pull ghcr.io/philiprbrenan/btreeasm:latest
docker run --rm -it -v ~/btreeAsm/:/root/btreeAsm -w /root/btreeAsm ghcr.io/philiprbrenan/btreeasm:latest bash -ic "source /root/sc/bin/activate; python3 /root/btreeAsm/verilog/Btree.py"
mkdir -p results/Btree_4096_4_8_8
cp "verilog/build/Btree/job0/Btree.pkg.json" "results/Btree_4096_4_8_8/Btree.json"
cp "verilog/build/Btree/job0/Btree.png"      "results/Btree_4096_4_8_8/Btree.png"
cp "verilog/build/Btree/job0/job.log"        "results/Btree_4096_4_8_8/job.log"
