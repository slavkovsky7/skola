#!/bin/bash

cd `dirname "$0"`

source config_build.conf


function build() {
  echo "---- Building ----"
  mkdir build
  cd build
  cmake -DCMAKE_BUILD_TYPE=$BUILD_TYPE -G "CodeLite - Unix Makefiles" ..
  cmake --build . 
}


function clean() {
  echo "---- Cleaning ----"
  rm -rf build 
}

while test $# -gt 0
do
    case "$1" in
        -clean) clean
            ;;
        -build) build
            ;;
        -*) echo "bad option $1"
            ;;
        *) echo "argument $1"
            ;;
    esac
    shift
done