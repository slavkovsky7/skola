#!/bin/bash
cd `dirname "$0"`

export CP=target/BackPropagation-1.0.0-jar-with-dependencies.jar


java -cp $CP sk.mslavkovsky.nn.MultiLayerNN "${@}"