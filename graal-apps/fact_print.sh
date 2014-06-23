#!/bin/sh
./launch.sh -p | perl -n -e '/DEBUG/ or /ANS/ or /SELECT/ or print "$_"'
