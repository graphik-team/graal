=========
= GRAAL =
=========

## How to build graal? ##

* clone the repository

  git clone <repo>

* build the project

mvn validate 

mvn package


## How to check code with code analyzer? ##

mvn pmd:check

mvn findbugs:check


## How to generate the executable jar of graal-apps? ##

mvn compile assembly:single



## How to build graal when you don't want to get all stuff maven brings?

* cd inside the graal directory

* ./prepare_ant.sh

* ant

