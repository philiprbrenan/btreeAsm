REMOTE=s                                                                       # Remote user and machine definition from .ssh/config
FOLDER=btreeAsm                                                                       # Folder containing project under user's home folder
DESIGN=Btree                                                                       # The name of the chip being designed
WORKSP=verilog                                                                       # Verilog workspace folder
rsync -r ~/$FOLDER $REMOTE:~/$FOLDER                                            # Copy local project files to remote system
ssh -S none $REMOTE << EOF                                                      # Execute the following commands on the remote system using a non shared ssh connection
cd $FOLDER                                                                      # Project folder
mkdir -p results/Btree_1024_16_32_32                                                                     # Results folder where the summary of the OpenRoad run will be saved
docker pull appaapps/openroad:latest                                                                  # OpenRoad docker image
# Start silicon compiler environment and run OpenRoad script in a docker container
docker run --rm                                                                 \
  -v ~/$FOLDER/:/root/$FOLDER                                                   \
  -w /root/$FOLDER                                                              \
  appaapps/openroad:latest                                                                            \
  bash -ic "source /root/sc/bin/activate; python3 /root/$FOLDER/$WORKSP/$DESIGN.py"
cp "verilog/build/$DESIGN/job0/$DESIGN.pkg.json" "results/Btree_1024_16_32_32/Btree.json"                                # Copy results out of docker to remote system
cp "verilog/build/$DESIGN/job0/$DESIGN.png"      "results/Btree_1024_16_32_32/Btree.png"
cp "verilog/build/$DESIGN/job0/job.log"          "results/Btree_1024_16_32_32/Btree.log"
EOF
rsync -r $REMOTE:~/$FOLDER/results/* ~/$FOLDER/results                         # Copy results back to local system from remote system
