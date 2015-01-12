#########
# GRAAL #
#########

## How to build graal? ##

mvn package


## How to check code with code analyzer? ##

mvn pmd:check
mvn findbugs:check


## How to generate the executable jar of graal-apps? ##

mvn compile assembly:single


