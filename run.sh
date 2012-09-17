#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

JAVA_HOME=/usr/lib/jvm/jdk1.7.0_07

java -cp $DIR/core/target/core-1.0-jar-with-dependencies.jar:$JAVA_HOME/jre/lib/jfxrt.jar com.irksomeideas.domal.Main
