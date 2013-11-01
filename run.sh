#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java -cp $DIR/build/libs/* copper.Main
