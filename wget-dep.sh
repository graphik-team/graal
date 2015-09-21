#!/bin/sh
mkdir -p lib
cd lib

while read dep
do
    echo $dep
    if [ ! -f $(basename $dep) ]
    then
        wget https://repo1.maven.org/maven2$dep
    fi
done < ../dep-list
