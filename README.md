#########
# GRAAL #
#########

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


