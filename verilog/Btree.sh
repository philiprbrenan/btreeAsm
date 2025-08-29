git fetch origin && git reset --hard @{u}  # Fetch changes from remote ovewriting any local changes
mkdir -p results/Btree_1024_32_32_32
docker pull ghcr.io/philiprbrenan/btreeasm:latest
docker run --rm -it -v ~/btreeAsm/:/root/btreeAsm -w /root/btreeAsm ghcr.io/philiprbrenan/btreeasm:latest bash -ic "source /root/sc/bin/activate; python3 /root/btreeAsm/verilog/Btree.py"
cp "verilog/build/Btree/job0/Btree.pkg.json" "results/Btree_1024_32_32_32/Btree.json"
cp "verilog/build/Btree/job0/Btree.png"      "results/Btree_1024_32_32_32/Btree.png"
cp "verilog/build/Btree/job0/job.log"        "results/Btree_1024_32_32_32/job.log"
