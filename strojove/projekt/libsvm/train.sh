#!/bin/bash

cd `dirname "$0"`

export OMP_NUM_THREADS=4

./svm-train "${@}"
