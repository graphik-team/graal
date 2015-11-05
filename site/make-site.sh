#!/bin/sh

version=1.0.0
export version

mkdir -p src/site/modules

for module in graal/graal-api graal/graal-util graal/graal-core graal/graal-backward-chaining graal/graal-forward-chaining graal/graal-homomorphism  graal/graal-io/graal-io-dlgp graal/graal-io/graal-io-owl graal/graal-io/graal-io-ruleml graal/graal-io/graal-io-sparql graal/graal-store/graal-store-rdbms
do
    echo $module
    ./make-module-pages.sh ../$module > src/modules/$(basename $module).tt
done
