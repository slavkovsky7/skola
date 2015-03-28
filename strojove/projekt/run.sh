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

#echo -------LovinsStemmer-------
#java -cp $CP LovinsStemmer $1
#echo -------PorterStemmer-------
#java -cp $CP PorterStemmer $1

java -Xmx4g -cp $CP Learner "${@}"