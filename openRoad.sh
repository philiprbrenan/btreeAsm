#!/bin/sh

set -ex

src_path=$(cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P)/..

mkdir -p deps
cd deps

git clone $(python3 ${src_path}/_tools.py --tool openroad --field git-url) openroad
cd openroad
git checkout $(python3 ${src_path}/_tools.py --tool openroad --field git-commit)
git submodule update --init --recursive

./etc/DependencyInstaller.sh -base
rm -f etc/openroad_deps_prefixes.txt
./etc/DependencyInstaller.sh -common $deps_args
cmake_args="-DENABLE_TESTS=OFF"
./etc/Build.sh -cmake="$cmake_args"

cd build
make install

 1  # 3. Build GTest (system version may not be built)
    2  cd /usr/src/gtest
    3  cmake CMakeLists.txt
    4  make -j$(nproc)
    5  cp *.a /usr/lib
    6  cd -
    7  # 4. Install Python packages
    8  python3 -m pip install --upgrade pip
    9  python3 -m pip install numpy matplotlib pybind11
   10  # 5. Build SWIG >= 4.0 from source
   11  wget https://github.com/swig/swig/archive/refs/tags/v4.1.1.tar.gz
   12  tar xf v4.1.1.tar.gz
   13  cd swig-4.1.1
   14  ./autogen.sh
   15  ./configure
   16  make -j$(nproc)
   17  make install
   18  cd ..
   19  rm -rf swig-4.1.1 v4.1.1.tar.gz
   20  # 6. Build LEMON from GitHub
   21  git clone https://github.com/LEMON/LEMON.git
   22  cd LEMON
   23  mkdir build && cd build
   24  cmake .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local
   25  make -j$(nproc)
   26  make install
   27  cd ../..
   28  rm -rf LEMON
   29  # 7. Finished
   30  echo "All OpenROAD dependencies installed. Remember to pass -DLEMON_DIR=/usr/local/lib/cmake/LEMON to CMake if needed."
   31  git clone https://lemon.cs.elte.hu/pub/svn/lemon/trunk lemon
   32  pwd
   33  git clone https://github.com/LEMON/LEMON.git
   34  GIT_TERMINAL_PROMPT=0 git clone https://github.com/LEMON/LEMON.git
   35  wget https://github.com/LEMON/LEMON/archive/refs/heads/master.zip
   36  unzip master.zip
   37  mv LEMON-master LEMON
   38  cd LEMON
   39  wget https://github.com/LEMON/LEMON/archive/refs/heads/master.zip
   40  apt install wget
   41  wget https://github.com/LEMON/LEMON/archive/refs/heads/master.zip
   42  git clone git@github.com:lemon-lang/lemon.git
   43  wget https://github.com/lemon-lang/lemon/archive/refs/heads/master.zip
   44  ll
   45  mv master.zip LEMON.zip
   46  mkdir lemon
   47  cp LEMON.zip lemon/
   48  cd lemon/
   49  unzip LEMON.zip
   50  apt install zip unzip
   51  unzip LEMON.zip
   52  ll
   53  cd lemon-master/
   54  ll
   55  make
   56  lemon -v
   57  ll
   58  ./lemon -v
   59  ./lemon
   60  male install
   61  make install
   62  which lemon
   63  ll
   64  make install
   65  ll
   66  cmake .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local
   67  apt install cmake
   68  cmake .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr/local
   69  ll
   70  cd src
   71  ll
   72  cd ..
   73  ll
   74  ll lib
   75  cat README.md
   76  ll /usr/bin/local
   77  ls -la  /usr/bin/local
   78  ls -la  /usr/bin
   79  cp ./lemon  /usr/bin
   80  ll /usr/lib
   81  ll /usr/lib/lsb
   82  ll /usr/lib/python3
   83  ll /usr/lib/python3/dist-packages/
   84  mkdir -p /usr/lib/lemon
   85  cp *.so /usr/lib/lemon/
   86  ll /usr/lib/lemon/
   87  ll /usr/bin/l*
   88  ldconfig
   89  lemon
   90  cd
   91  ll
   92  cd ..
   93  ll
   94  em LEMON.zip
   95  rm LEMON.zip
   96  cd root
   97  ll
   98  cd ..
   99  cd yosys/
  100  ll
  101  yosys -v
  102  yosys -V
  103  history
