#!/bin/sh
./launch.sh -q "${1}" | perl -n -e 's/\((.*?),(.*?)\)/$1 --> $2\t/g && print "$_"' | perl -n -e 's/\[//g && print "$_"'| perl -n -e 's/\]//g && print "$_"'
