#!/bin/bash
cd `dirname "$0"`

export CP=target/PCA-1.0.0-jar-with-dependencies.jar


java -cp $CP sk.mslavkovsky.nn.PCARunner "${@}"
