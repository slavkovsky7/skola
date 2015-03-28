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

java -cp $CP Learner -gen-bdist
echo =============================
java -cp $CP Learner -fil-bdist
echo =============================
java -cp $CP Learner -svmb
echo =============================
java -cp $CP Learner -trainb
echo =============================
java -cp $CP Learner -mb