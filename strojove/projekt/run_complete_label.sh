#!/bin/bash
cd `dirname "$0"`

if [ ! -d "data/dist_data" ];then
  mkdir data/dist_data
fi
if [ ! -d "data/imdb_data" ]; then
  mkdir data/imdb_data
fi
if [ ! -d "data/svm_data" ]; then
  mkdir data/svm_data
fi

export OMP_NUM_THREADS=4
export CP=bin:resources
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:lib

java -cp $CP Learner -gen-tdist
echo =============================
java -cp $CP Learner -fil-tdist
echo =============================
java -cp $CP Learner -svmt
echo =============================
java -cp $CP Learner -traint
echo =============================
java -cp $CP Learner -mt
