#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo $DIR

java -cp "${DIR}/build/libs/*" copper.Main
