#!/bin/bash
cd `dirname "$0"`

export CP=target/Uloha4-1.0.0-jar-with-dependencies.jar


java -cp $CP sk.mslavkovsky.zui2.Uloha4 "${@}"
