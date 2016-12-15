#!/bin/sh
mvn dependency:list | grep ":compile" | grep -v graal | grep -v SNAPSHOT | sort | uniq | sed 's/\[INFO\]\s*\([^ ]*\):compile.*/\1/' | sed 's/:jar//' | perl -n -e 'my $t; if (/(.*?):(.*)/) {$t = $2;} $_ = $1; s/[.:]/\//g; print "$_/$t\n"' | perl -n -e '/(.*)\/(.*)\/(.*)/ && print "/$1/$2/$3/$3.jar\n"' | sed 's-:-/-' | sed 's.:.-.'
