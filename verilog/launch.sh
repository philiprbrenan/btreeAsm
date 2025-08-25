docker run --rm -it -v ~/btreeAsm/:/root/btreeAsm -w /root/btreeAsm btreeasm:v2 bash -ic "source /root/sc/bin/activate; python3 /root/btreeAsm/verilog/Btree.py"
mkdir -p results/Btree_256_16_8_8
cp "verilog/build/Btree/job0/Btree.pkg.json" "results/Btree_256_16_8_8/Btree.json"
cp "verilog/build/Btree/job0/Btree.png"      "results/Btree_256_16_8_8/Btree.png"
EOF
