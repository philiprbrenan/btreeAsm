echo "rsync -r /home/phil/btreeAsm a: # Copy local files to aws"
#git fetch origin && git reset --hard @{u}  # Fetch changes from remote ovewriting any local changes
#gzip -dc verilog/Btree.v.gz > verilog/Btree.v # Unzip as large verilog source cannot be stored on github
mkdir -p results/Btree_1024_40_32_32
docker pull appaapps/openroad:latest
docker run --rm -it -v ~/btreeAsm/:/root/btreeAsm -w /root/btreeAsm appaapps/openroad:latest bash -ic "source /root/sc/bin/activate; python3 /root/btreeAsm/verilog/Btree.py"
cp "verilog/build/Btree/job0/Btree.pkg.json" "results/Btree_1024_40_32_32/Btree.json"
cp "verilog/build/Btree/job0/Btree.png"      "results/Btree_1024_40_32_32/Btree.png"
cp "verilog/build/Btree/job0/job.log"        "results/Btree_1024_40_32_32/job.log"
